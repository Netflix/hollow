package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class CanaryAnnounceJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    private final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob;

    public CanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion, BeforeCanaryAnnounceJob beforeCanaryAnnounceJob) {
        super(ctx, "canary-announce", newVersion);
        this.vip = vip;
        this.beforeCanaryAnnounceJob = beforeCanaryAnnounceJob;
    }

    @Override
    public boolean isEligible() {
        return jobExistsAndCompletedSuccessfully(beforeCanaryAnnounceJob);
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if (jobDoesNotExistOrFailed(beforeCanaryAnnounceJob))
            return true;
        return false;
    }
}
