package com.netflix.vms.transformer.common;

import com.netflix.vms.transformer.common.config.OctoberSkyData;

import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.util.Set;
import java.util.function.Consumer;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;

public interface TransformerContext {

    void setCurrentCycleId(long cycleId);

    long getCurrentCycleId();

    void setNowMillis(long now);

    long getNowMillis();
    
    void setFastlaneIds(Set<Integer> fastlaneIds);
    
    Set<Integer> getFastlaneIds();

    TransformerLogger getLogger();
    
    TransformerConfig getConfig();

    TransformerMetricRecorder getMetricRecorder();

    TransformerCassandraHelper getCanaryResultsCassandraHelper();

    TransformerCassandraHelper getValidationStatsCassandraHelper();

    TransformerCassandraHelper getPoisonStatesHelper();

    TransformerFiles files();
    
    OctoberSkyData getOctoberSkyData();

    TransformerPlatformLibraries platformLibraries();

    Consumer<PublicationHistory> getPublicationHistoryConsumer();
    
}
