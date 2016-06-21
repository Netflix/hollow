package com.netflix.vms.transformer.common;

public interface TransformerHealthIndicator {
    public static final String TRANSFORMER_STATUS_STRING = "transformerStatus";

    public static enum Status {
        STARTING, // server is up and the first cycle is running
        STARTED_FAILING_CYCLE, // server has finished one or more cycles, none successfully
        STARTED_SUCCESSFUL // server has had a successful cycle
    }

    void cycleSucessful();

    void cycleFailed(Throwable th);

    Status getStartupStatus();

    void setStartupStatus(Status startupStatus);
}
