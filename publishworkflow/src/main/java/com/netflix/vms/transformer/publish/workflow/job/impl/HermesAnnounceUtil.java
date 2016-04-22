package com.netflix.vms.transformer.publish.workflow.job.impl;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.netflix.config.FastProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hermes.data.DataEntry;
import com.netflix.hermes.data.DirectDataPointer;
import com.netflix.hermes.exception.DataConsumeException;
import com.netflix.hermes.subscriber.Consumer;
import com.netflix.hermes.subscriber.Subscription;
import com.netflix.hermes.subscriber.SubscriptionManager;
import com.netflix.vms.transformer.servlet.platform.PlatformLibraries;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class HermesAnnounceUtil {

    private static final FastProperty.BooleanProperty BIG_RED_BUTTON = new FastProperty.BooleanProperty("com.netflix.vms.server.bigredbutton", false);

    private static final HermesBlobAnnouncer HERMES_ANNOUNCER = new HermesBlobAnnouncer(PlatformLibraries.HERMES_PUBLISHER, HermesTopicProvider.HOLLOWBLOB_TOPIC_PREFIX);

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
            //PublishEventRecorder eventRecorder = VMSInjectionManager.get().getPinEventRecorder();
            //eventRecorder.postAnnounceEvent(String.valueOf(announceVersion), region, hermesTopic);
        }

        if(canary) {
            previouslyAnnouncedCanaryVersion = currentlyAnnouncedCanaryVersion;
            currentlyAnnouncedCanaryVersion = announceVersion;
        }

        return success;
    }

    private static Map<String, String> getAttributes(long cycleVersionId, long priorVersion) {
        String currentVersionStr = String.valueOf(cycleVersionId);
        String priorVersionStr = (priorVersion != Long.MIN_VALUE || priorVersion != cycleVersionId)? String.valueOf(priorVersion) : "";
        return BlobMetaDataUtil.getPublisherProps(System.currentTimeMillis(), currentVersionStr, priorVersionStr);
    }

    public static long getPreviouslyAnnouncedCanaryVersion(String vip) {
        if(previouslyAnnouncedCanaryVersion == Long.MIN_VALUE || previouslyAnnouncedCanaryVersion == Long.MAX_VALUE)
            initializePreviouslyAnnouncedCanaryVersion(vip);
        //LOGGER.logf(ErrorCode.VMSHermesAnnounceDebug, "Returning %s as previously announced canary version.", currentlyAnnouncedCanaryVersion);
        return previouslyAnnouncedCanaryVersion;
    }

    private static boolean initializePreviouslyAnnouncedCanaryVersion(String vip) {
        HermesAnnounceListener announceListener = new HermesAnnounceListener();

        SubscriptionManager subscriptionManager = PlatformLibraries.HERMES_SUBSCRIBER;
        Subscription subscription = subscriptionManager.subscribe(HermesTopicProvider.getDataCanaryTopic(vip), announceListener);

        try {
            subscription.waitInitUpdate(60000L);
            return true;
        } catch(TimeoutException te) {
            throw new RuntimeException(te);
        }
    }

    private static class HermesAnnounceListener implements Consumer {
        public void consume(DataEntry entry) throws DataConsumeException {
            DirectDataPointer directDataPointer = (DirectDataPointer)(entry.getDataPointer());
            String dataString = directDataPointer.getDataString();
            String latestVersion = null;
            try {
                HermesAnnounceEvent event = new ObjectMapper().readValue(dataString, HermesAnnounceEvent.class);
                latestVersion = event.getVersion();

                previouslyAnnouncedCanaryVersion = Long.parseLong(latestVersion);
            } catch (Throwable t) {
                throw new DataConsumeException("Exception occurred receiving Hermes VMS announcement.  Version: " + latestVersion, true, t);
            }
        }
    }

    @JsonDeserialize(builder=HermesAnnounceEvent.Builder.class)
    public static class HermesAnnounceEvent {
        private final Map<String, String> attributes;
        private final FastBlobType fastBlobImageType;

        private HermesAnnounceEvent(Map<String, String> attributes, FastBlobType type) {
            this.attributes = attributes;
            this.fastBlobImageType = type;
        }

        public  Map<String, String> getAttributes() {
            return attributes;
        }

        public FastBlobType getFastBlobImageType() {
            return fastBlobImageType;
        }

        @JsonIgnore
        public String getVersion() {
            return attributes.get("dataVersion");
        }

        public static class Builder {
            private Map<String, String> attributes;
            private FastBlobType fastBlobImageType;

            public Builder withAttributes(Map<String, String> attributes) {
                this.attributes = attributes;
                return this;
            }

            public Builder withFastBlobImageType(FastBlobType type) {
                this.fastBlobImageType = type;
                return this;
            }

            public HermesAnnounceEvent build() {
                return new HermesAnnounceEvent(attributes, fastBlobImageType);
            }
        }

        private static enum FastBlobType {
            SNAPSHOT("snapshot"), DELTA("delta"), REVERSEDELTA("reversedelta"), STATE("state");

            private final String key;
            private final String succLoadMetricName;
            private final String failLoadMetricName;
            FastBlobType(final String key) {
                this.key = key;
                this.succLoadMetricName = "vms_blob_" + key + "_load_success";
                this.failLoadMetricName = "vms_blob_" + key + "_load_failed";
            }

            public String getKey() {
                return key;
            }

            public String getSuccLoadMetricName() {
                return succLoadMetricName;
            }

            public String getFailLoadMetricName() {
                return failLoadMetricName;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((attributes == null) ? 0 : attributes.hashCode());
            result = prime * result + ((fastBlobImageType == null) ? 0 : fastBlobImageType.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            HermesAnnounceEvent other = (HermesAnnounceEvent) obj;
            if (attributes == null) {
                if (other.attributes != null)
                    return false;
            } else if (!attributes.equals(other.attributes))
                return false;
            if (fastBlobImageType != other.fastBlobImageType)
                return false;
            return true;
        }
    }


}
