package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import java.io.File;
import java.util.function.Supplier;

/**
 * A version of a {@link CircuitBreakerJob} used in unit tests. It does nothing by itself,
 * but allows for customizing its executeJob block.
 */
public class TestCircuitBreakerJob extends CircuitBreakerJob {
    private final Supplier<Boolean> execute;

    public TestCircuitBreakerJob(PublishWorkflowContext context, Supplier<Boolean> execute,
            String vip, long cycleVersion, File snapshotFile, File deltaFile, File reverseDeltaFile,
            File nostreamsSnapshot, File nostreamsDelta, File nostreamsReverseDelta) {
        super(context, vip, cycleVersion, snapshotFile, deltaFile, reverseDeltaFile, nostreamsSnapshot,
                nostreamsDelta, nostreamsReverseDelta);
        this.execute = execute;
    }

    @Override public boolean executeJob() {
        return this.execute.get();
    }
}
