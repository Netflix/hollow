package com.netflix.vms.transformer.common;

public interface TransformerContext {

    void setNowMillis(long now);

    long getNowMillis();

    TransformerLogger getLogger();

    TransformerCassandraHelper getCanaryResultsCassandraHelper();

    TransformerCassandraHelper getValidationStatsCassandraHelper();

    TransformerCassandraHelper getPoisonStatesHelper();

    TransformerFiles files();

    TransformerPlatformLibraries platformLibraries();
}
