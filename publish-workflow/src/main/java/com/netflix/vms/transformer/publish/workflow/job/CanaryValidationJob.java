package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class CanaryValidationJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    private final AfterCanaryAnnounceJob afterCanaryAnnounceJobs;

    public CanaryValidationJob(PublishWorkflowContext ctx, String vip, long cycleVersion,
            AfterCanaryAnnounceJob afterCanaryAnnounceJobs) {
        super(ctx, "canary-validation", cycleVersion);
        this.vip = vip;
        this.afterCanaryAnnounceJobs = afterCanaryAnnounceJobs;
    }

    @Override
    public boolean isEligible() {
        if(!afterCanaryAnnounceJobs.isComplete())
            return false;
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if(afterCanaryAnnounceJobs.hasJobFailed())
            return true;
        return false;
    }

}
