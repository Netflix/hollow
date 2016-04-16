package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

import java.io.File;

public abstract class CircuitBreakerJob extends PublicationJob {
    protected final long cycleVersion;
    protected final File snapshotFile;
    protected final File deltaFile;
    protected final File reverseDeltaFile;

    public CircuitBreakerJob(String vip, long cycleVersion, File snapshotFile, File deltaFile, File reverseDeltaFile) {
        super("circuit-breaker", cycleVersion);
        this.cycleVersion = cycleVersion;
        this.snapshotFile = snapshotFile;
        this.deltaFile = deltaFile;
        this.reverseDeltaFile = reverseDeltaFile;
    }

    @Override
    protected boolean isEligible() {
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return false;
    }

}
