package com.netflix.vms.transformer.common;

import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;

import java.util.Set;
import java.util.function.Consumer;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;

public interface TransformerContext {

    void setCurrentCycleId(long cycleId);

    long getCurrentCycleId();

    void setNowMillis(long now);

    long getNowMillis();
    
    void setFastlaneIds(Set<Integer> fastlaneIds);
    
    Set<Integer> getFastlaneIds();

    TaggingLogger getLogger();
    
    TransformerConfig getConfig();

    TransformerMetricRecorder getMetricRecorder();

    TransformerCassandraHelper getCassandraHelper();

    TransformerFiles files();
    
    OctoberSkyData getOctoberSkyData();

    Consumer<PublicationHistory> getPublicationHistoryConsumer();
}
