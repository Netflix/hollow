package com.netflix.vms.transformer.common;

public interface TransformerHealthIndicator {
    void cycleSucessful();

    void cycleFailed(Throwable th);

    boolean isHealthy();
}
