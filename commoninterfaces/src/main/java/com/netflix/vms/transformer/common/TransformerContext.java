package com.netflix.vms.transformer.common;

import java.util.function.Consumer;

public interface TransformerContext {

    void setNowMillis(long now);

    long getNowMillis();

    TransformerLogger getLogger();

    TransformerCassandraHelper getCanaryResultsCassandraHelper();

    TransformerCassandraHelper getValidationStatsCassandraHelper();

    TransformerCassandraHelper getPoisonStatesHelper();

    TransformerFiles files();

    TransformerPlatformLibraries platformLibraries();

    Consumer<PublicationHistory> getPublicationHistoryConsumer();
}
