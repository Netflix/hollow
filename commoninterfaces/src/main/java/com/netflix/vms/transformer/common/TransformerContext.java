package com.netflix.vms.transformer.common;

import java.util.function.Consumer;

import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;

public interface TransformerContext {

    void setCurrentCycleId(long cycleId);

    long getCurrentCycleId();

    void setNowMillis(long now);

    long getNowMillis();

    TransformerLogger getLogger();

    TransformerMetricRecorder getMetricRecorder();

    TransformerCassandraHelper getCanaryResultsCassandraHelper();

    TransformerCassandraHelper getValidationStatsCassandraHelper();

    TransformerCassandraHelper getPoisonStatesHelper();

    TransformerFiles files();

    TransformerPlatformLibraries platformLibraries();

    Consumer<PublicationHistory> getPublicationHistoryConsumer();
}
