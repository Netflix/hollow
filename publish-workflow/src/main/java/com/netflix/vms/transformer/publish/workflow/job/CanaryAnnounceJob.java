package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class CanaryAnnounceJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    protected final RegionEnum region;
    private final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob;

    public CanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion, RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceJob) {
        super(ctx, "canary-announce-"+region, newVersion);
        this.vip = vip;
        this.region = region;
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
