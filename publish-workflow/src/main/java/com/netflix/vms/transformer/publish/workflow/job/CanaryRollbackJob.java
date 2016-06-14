package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class CanaryRollbackJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    protected final long priorVersion;
    protected final long rollbackVersion;
    private final CanaryValidationJob validationJob;

    public CanaryRollbackJob(PublishWorkflowContext ctx, String vip, long cycleVersion, long priorVersion, long rollbackVersion, CanaryValidationJob validationJob) {
        super(ctx, "canary-rollback", cycleVersion);
        this.validationJob = validationJob;
        this.priorVersion = priorVersion;
        this.rollbackVersion = rollbackVersion;
        this.vip = vip;
    }

    @Override
    public boolean isEligible() {
        return validationJob.hasJobFailed();
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return validationJob.isComplete();
    }

}
