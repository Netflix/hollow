package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class AnnounceJob extends PublishWorkflowPublicationJob {
    protected final String vip;
    protected final RegionEnum region;
    protected final long priorVersion;
    private final AnnounceJob previousAnnounceJob;
    private final CanaryValidationJob canaryValidationJob;
    private final DelayJob delayJob;

    public AnnounceJob(PublishWorkflowContext ctx, String vip, long priorVersion, long newVersion,
            RegionEnum region, CanaryValidationJob canaryValidationJob, DelayJob delayJob,
            AnnounceJob previousAnnounceJob) {
        super(ctx, "announce-" + region, newVersion);
        this.vip = vip;
        this.canaryValidationJob = canaryValidationJob;
        this.previousAnnounceJob = previousAnnounceJob;
        this.priorVersion = priorVersion;
        this.delayJob = delayJob;
        this.region = region;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return previousAnnounceJobFinished()
                && (jobExistsAndFailed(delayJob) || jobDoesNotExistOrFailed(canaryValidationJob));
    }

    @Override
    public boolean isEligible() {
        return previousAnnounceJobFinished() && jobDoesNotExistOrCompletedSuccessfully(delayJob)
                && jobExistsAndCompletedSuccessfully(canaryValidationJob);
    }

    private boolean previousAnnounceJobFinished() {
        return previousAnnounceJob == null || previousAnnounceJob.isComplete() || previousAnnounceJob.hasJobFailed();
    }
}
