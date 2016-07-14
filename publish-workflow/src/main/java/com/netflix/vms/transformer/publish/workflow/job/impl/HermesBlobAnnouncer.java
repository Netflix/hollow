package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hermes.Constants;
import com.netflix.hermes.data.DataEntry;
import com.netflix.hermes.data.DirectDataPointer;
import com.netflix.hermes.data.NoContentDataEntry;
import com.netflix.hermes.platformserviceclient.Property;
import com.netflix.hermes.publisher.FastPropertyPublisher;
import com.netflix.hermes.publisher.PurgePolicy;
import com.netflix.vms.transformer.common.VersionMinter;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class HermesBlobAnnouncer{
    
    private final FastPropertyPublisher publisher;
    private final TransformerCassandraColumnFamilyHelper announcedVersionCassandraHelper;
    private final VersionMinter hermesVersionMinter;
    
    private final ConcurrentHashMap<String, AtomicLong> latestAnnouncedVersionsPerTopic;

    @Inject
    public HermesBlobAnnouncer(FastPropertyPublisher publisher, 
                               TransformerCassandraHelper cassandraHelper, 
                               @Named("vipAnnounceID") VersionMinter hermesVersionMinter) {
        this.publisher = publisher;
        this.announcedVersionCassandraHelper = cassandraHelper.getColumnFamilyHelper("vms_announced_versions", "vms_announced_versions");
        this.latestAnnouncedVersionsPerTopic = new ConcurrentHashMap<>();
        this.hermesVersionMinter = hermesVersionMinter;
    }
    
    /*
     * Publishes specified topic for a given region.
     */
    public synchronized boolean publish(RegionEnum region, String vip, boolean isCanary, Map<String, String> attributes) throws Exception{
        String topic = isCanary ? HermesTopicProvider.getDataCanaryTopic(vip) : HermesTopicProvider.getHollowBlobTopic(vip);
        
        long version = Long.parseLong(attributes.get("dataVersion"));
        HermesVipAnnouncer.HermesAnnounceEvent event = new HermesVipAnnouncer.HermesAnnounceEvent.Builder()
                                                    .withAttributes(attributes)
                                                    .build();

        return publishTopic(region, topic, version, event);
    }

    private boolean publishTopic(RegionEnum region, String topic, long version, HermesVipAnnouncer.HermesAnnounceEvent event) throws JsonProcessingException {
        String eventString = new ObjectMapper().writeValueAsString(event);
        final DirectDataPointer pointer = new DirectDataPointer.Builder()
                                            .topic(topic)
                                            .version(String.valueOf(hermesVersionMinter.mintANewVersion()))
                                            .dataString(eventString)
                                            .build();
        final Property prop = new Property();
        prop.setKey(Constants.FAST_PROPERTY_PREFIX + topic);
        prop.setEnv(NetflixConfiguration.getEnvironment());
        prop.setRegion(region.key());

        final DataEntry entry = NoContentDataEntry.newInstance(null, pointer);
        final PurgePolicy purgePolicy = new PurgePolicy.Builder().maxNumOfVersions(1024).build();

        int retryCount = 0;

        while(retryCount < 10) {
            try {
                writePublishedVersionToCassandra(topic, version);
                publisher.publish(pointer, entry, purgePolicy, prop);
                return true;
            } catch(Throwable th) {
                th.printStackTrace();
            }
            
            retryCount++;

            try {
                Thread.sleep(retryCount * 1000L);
            } catch(InterruptedException ignore) { }

        }

        return false;
    }
    
    private void writePublishedVersionToCassandra(String topic, long version) throws ConnectionException {
        AtomicLong latest = latestAnnouncedVersionsPerTopic.get(topic);
        if(latest == null) {
            latest = new AtomicLong(Long.MIN_VALUE);
            AtomicLong existingLatest = latestAnnouncedVersionsPerTopic.putIfAbsent(topic, latest);
            if(existingLatest != null)
                latest = existingLatest;
        }
        
        long latestVal = latest.get();
        while(latestVal < version) {
            if(latest.compareAndSet(latestVal, version)) {
                announcedVersionCassandraHelper.addKeyValuePair(topic, String.valueOf(version));
                return;
            }
            latestVal = latest.get();
        }
    }
    
    public long getLatestAnnouncedVersionFromCassandra(String vip) {
        String topic = HermesTopicProvider.getHollowBlobTopic(vip);
        
        int retryCount = 0;
        
        while(retryCount < 5) {
            try {
                String value = announcedVersionCassandraHelper.getKeyValuePair(topic);
                return Long.parseLong(value);
            } catch(Throwable th) {
                th.printStackTrace();
            }
            
            retryCount++;
        }
        
        return Long.MIN_VALUE;
    }
}
