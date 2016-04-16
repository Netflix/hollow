package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hermes.Constants;
import com.netflix.hermes.data.DataEntry;
import com.netflix.hermes.data.DirectDataPointer;
import com.netflix.hermes.data.NoContentDataEntry;
import com.netflix.hermes.platformserviceclient.Property;
import com.netflix.hermes.publisher.FastPropertyPublisher;
import com.netflix.hermes.publisher.PurgePolicy;
import com.netflix.util.RetryableAction;
import com.netflix.videometadata.audit.ErrorCodeLogger;
import com.netflix.videometadata.audit.VMSErrorCode.ErrorCode;
import com.netflix.videometadata.audit.VMSLogManager;

@Singleton
public class HermesBlobAnnouncer{
    private static final ErrorCodeLogger LOGGER = VMSLogManager.getErrorCodeLogger(HermesBlobAnnouncer.class);
    private final FastPropertyPublisher publisher;
    private final String topicPrefix;

    @Inject
    public HermesBlobAnnouncer(FastPropertyPublisher publisher, String topicPrefix) {
        this.publisher = publisher;
        this.topicPrefix = topicPrefix;
    }

    private boolean publishTopic(final RegionEnum region, final String topic, final String version,
            FastBlobImagePublishEvent event) throws JsonProcessingException {
        String eventString = new ObjectMapper().writeValueAsString(event);
        final DirectDataPointer pointer = new DirectDataPointer.Builder()
                                            .topic(topic)
                                            .version(version)
                                            .dataString(eventString)
                                            .build();
        final Property prop = new Property();
        prop.setKey(Constants.FAST_PROPERTY_PREFIX + topic);
        prop.setEnv(NetflixConfiguration.getEnvironment());
        prop.setRegion(region.key());

        final DataEntry entry = NoContentDataEntry.newInstance(null, pointer);
        final PurgePolicy purgePolicy = new PurgePolicy.Builder().maxNumOfVersions(1024).build();

        final RetryableAction publishAction = new RetryableAction("topic-publish-thread", 5, 5000) {
            @Override
            public boolean run() throws Exception {
                try {
                    publisher.publish(pointer, entry, purgePolicy, prop);
                    LOGGER.logfWithExplicitCycleVersion(ErrorCode.GeneralInfo, version, "Published topic:%s, region:%s, version:%s", topic, region, version);
                    return true;
                } catch(final Exception e) {
                    throw e;
                }
            }
        };

        try {
            return publishAction.executeWithRetry();
        } catch (Exception e) {
            LOGGER.logf(ErrorCode.GeneralError, version, "Failed to publish topic:%s, version:%s", e, topic, version);
            return false;
        }
    }

    /*
     * Publishes topic for a given region.
     */
    public synchronized boolean publish(RegionEnum region, Map<String, String> attributes) throws Exception{
        String version = attributes.get("dataVersion");
        FastBlobImagePublishEvent event = new FastBlobImagePublishEvent.Builder()
                                                    .withAttributes(attributes)
                                                    .build();

        return publishTopic(region, getTopic(), version, event);
    }

    /*
     * Publishes specified topic for a given region.
     */
    public synchronized boolean publish(RegionEnum region, String topic, Map<String, String> attributes) throws Exception{
        String version = attributes.get("dataVersion");
        FastBlobImagePublishEvent event = new FastBlobImagePublishEvent.Builder()
                                                    .withAttributes(attributes)
                                                    .build();

        return publishTopic(region, topic, version, event);
    }

    public String getTopic() {
        return HermesTopicProvider.getTopic(topicPrefix);
    }
}
