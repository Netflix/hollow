package com.netflix.vms.transformer.publish.workflow.job.impl;

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
import java.util.Map;

@Singleton
public class HermesBlobAnnouncer{
    private final FastPropertyPublisher publisher;

    @Inject
    public HermesBlobAnnouncer(FastPropertyPublisher publisher) {
        this.publisher = publisher;
    }

    private boolean publishTopic(RegionEnum region, String topic, String version, HermesVipAnnouncer.HermesAnnounceEvent event) throws JsonProcessingException {
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

        int retryCount = 0;

        while(retryCount < 3) {
            try {
                publisher.publish(pointer, entry, purgePolicy, prop);
                return true;
            } catch(Throwable th) {
                th.printStackTrace();
            }

            retryCount++;
        }

        return false;
    }

    /*
     * Publishes specified topic for a given region.
     */
    public synchronized boolean publish(RegionEnum region, String topic, Map<String, String> attributes) throws Exception{
        String version = attributes.get("dataVersion");
        HermesVipAnnouncer.HermesAnnounceEvent event = new HermesVipAnnouncer.HermesAnnounceEvent.Builder()
                                                    .withAttributes(attributes)
                                                    .build();

        return publishTopic(region, topic, version, event);
    }
}
