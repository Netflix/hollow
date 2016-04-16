package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.videometadata.VideoMetaDataUtil.getServerId;
import static com.netflix.videometadata.VideoMetaDataUtil.getVersion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.FastProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hermes.data.DataEntry;
import com.netflix.hermes.data.DirectDataPointer;
import com.netflix.hermes.exception.DataConsumeException;
import com.netflix.hermes.subscriber.Consumer;
import com.netflix.hermes.subscriber.Subscription;
import com.netflix.hermes.subscriber.SubscriptionManager;
import com.netflix.videometadata.VMSInjectionManager;
import com.netflix.videometadata.audit.ErrorCodeLogger;
import com.netflix.videometadata.audit.VMSErrorCode.ErrorCode;
import com.netflix.videometadata.audit.VMSLogManager;
import com.netflix.videometadata.publish.PublishEventRecorder;
import com.netflix.videometadata.publish.hermes.FastBlobImagePublishEvent;
import com.netflix.videometadata.publish.hermes.HermesBlobAnnouncer;
import com.netflix.videometadata.publish.hermes.HermesTopicProvider;
import com.netflix.videometadata.s3.BlobMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class HermesAnnounceUtil {

    public static final ErrorCodeLogger LOGGER = VMSLogManager.getErrorCodeLogger(HermesAnnounceUtil.class);

    private static final FastProperty.BooleanProperty BIG_RED_BUTTON = new FastProperty.BooleanProperty("com.netflix.vms.server.bigredbutton", false);

    private static final HermesBlobAnnouncer HERMES_ANNOUNCER = new HermesBlobAnnouncer(VMSInjectionManager.get().getFastPropertyPublisher(), HermesTopicProvider.HOLLOWBLOB_TOPIC_PREFIX);

    private static long previouslyAnnouncedCanaryVersion = Long.MAX_VALUE;
    private static long currentlyAnnouncedCanaryVersion = Long.MAX_VALUE;

    public static boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion) {
        return announce(vip, region, canary, announceVersion, Long.MIN_VALUE);
    }

    public static boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion, long priorVersion) {
        if(BIG_RED_BUTTON.get())
            return false;

        String hermesTopic = canary ? HermesTopicProvider.getDataCanaryTopic(vip) : HermesTopicProvider.getHollowBlobTopic(vip);

        boolean success = false;
        try {
            success = HERMES_ANNOUNCER.publish(region, hermesTopic, getAttributes(announceVersion, priorVersion));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(success && !canary) {
            PublishEventRecorder eventRecorder = VMSInjectionManager.get().getPinEventRecorder();
            eventRecorder.postAnnounceEvent(String.valueOf(announceVersion), region, hermesTopic);
        }

        if(canary) {
            previouslyAnnouncedCanaryVersion = currentlyAnnouncedCanaryVersion;
            currentlyAnnouncedCanaryVersion = announceVersion;
        }

        return success;
    }

    private static Map<String, String> getAttributes(long cycleVersionId, long priorVersion) {
        Map<String, String> attributes = new HashMap<String, String>();

        attributes.put(BlobMetaData.dataVersion.name(), String.valueOf(cycleVersionId));
        attributes.put(BlobMetaData.ProducedByServer.name(), getServerId());
        attributes.put(BlobMetaData.ProducedByJarVersion.name(), getVersion());
        attributes.put(BlobMetaData.publishedTimestamp.name(), String.valueOf(System.currentTimeMillis()));
        attributes.put(BlobMetaData.priorVersion.name(), (priorVersion != Long.MIN_VALUE || priorVersion != cycleVersionId)? String.valueOf(priorVersion) : "");

        return attributes;
    }

    public static long getPreviouslyAnnouncedCanaryVersion(String vip) {
        if(previouslyAnnouncedCanaryVersion == Long.MIN_VALUE || previouslyAnnouncedCanaryVersion == Long.MAX_VALUE)
            initializePreviouslyAnnouncedCanaryVersion(vip);
        LOGGER.logf(ErrorCode.VMSHermesAnnounceDebug, "Returning %s as previously announced canary version.", currentlyAnnouncedCanaryVersion);
        return previouslyAnnouncedCanaryVersion;
    }

    private static void initializePreviouslyAnnouncedCanaryVersion(String vip) {
        HermesAnnounceListener announceListener = new HermesAnnounceListener();

        SubscriptionManager subscriptionManager = VMSInjectionManager.get().getSubscriptionManager();
        Subscription subscription = subscriptionManager.subscribe(HermesTopicProvider.getDataCanaryTopic(vip), announceListener);

        try {
            subscription.waitInitUpdate(60000L);
        } catch(TimeoutException te) {
            LOGGER.logf(ErrorCode.VMSHermesAnnounceErros, "Did not receive current canary version from Hermes in time, cannot initially roll back canary if it fails", te);
        }
    }

    private static class HermesAnnounceListener implements Consumer {
        public void consume(DataEntry entry) throws DataConsumeException {
            DirectDataPointer directDataPointer = (DirectDataPointer)(entry.getDataPointer());
            String dataString = directDataPointer.getDataString();
            String latestVersion = null;
            try {
                FastBlobImagePublishEvent event = new ObjectMapper().readValue(dataString, FastBlobImagePublishEvent.class);
                latestVersion = event.getVersion();
                LOGGER.logf(ErrorCode.VMSHermesAnnounceDebug, "Received hermes event: " + latestVersion);

                previouslyAnnouncedCanaryVersion = Long.parseLong(latestVersion);
            } catch (Throwable t) {
                LOGGER.logf(ErrorCode.VMSHermesAnnounceErros,"Exception occurred receiving Hermes VMS announcement.  Version: ",latestVersion);
                throw new DataConsumeException("Exception occurred receiving Hermes VMS announcement.  Version: " + latestVersion, true, t);
            }
        }
    }

}
