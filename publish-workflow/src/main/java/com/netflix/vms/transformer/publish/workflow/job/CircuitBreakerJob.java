package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import java.io.File;

public abstract class CircuitBreakerJob extends PublishWorkflowPublicationJob {
    protected final long cycleVersion;
    protected final File snapshotFile;
    protected final File deltaFile;
    protected final File reverseDeltaFile;
    protected final File nostreamsSnapshotFile;
    protected final File nostreamsDeltaFile;
    protected final File nostreamsReverseDeltaFile;

    public CircuitBreakerJob(PublishWorkflowContext ctx, String vip, long cycleVersion,
            File snapshotFile, File deltaFile, File reverseDeltaFile,
            File nostreamsSnapshotFile, File nostreamsDeltaFile, File nostreamsReverseDeltaFile) {
        super(ctx, "circuit-breaker", cycleVersion);
        this.cycleVersion = cycleVersion;
        this.snapshotFile = snapshotFile;
        this.deltaFile = deltaFile;
        this.reverseDeltaFile = reverseDeltaFile;
        this.nostreamsSnapshotFile = nostreamsSnapshotFile;
        this.nostreamsDeltaFile = nostreamsDeltaFile;
        this.nostreamsReverseDeltaFile = nostreamsReverseDeltaFile;
    }

    @Override
    public boolean isEligible() {
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return false;
    }

}
