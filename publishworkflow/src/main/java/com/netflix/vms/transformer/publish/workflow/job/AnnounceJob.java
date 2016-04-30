package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

import com.netflix.config.NetflixConfiguration.RegionEnum;

public abstract class AnnounceJob extends PublishWorkflowPublicationJob {
    public final static String ANNOUNCE_DELTA_JOB_NAME_PREFIX = "announce_delta_";
    public final static String ANNOUNCE_SNAPSHOT_JOB_NAME_PREFIX = "announce_snapshot_";

    protected final String vip;
    protected final RegionEnum region;
    protected final long priorVersion;
    private final AnnounceJob previousAnnounceJob;
    private final CanaryValidationJob canaryValidationJob;
    private final DelayJob delayJob;

    public AnnounceJob(PublishWorkflowContext ctx, String vip, long priorVersion, long newVersion, RegionEnum region, CanaryValidationJob canaryValidationJob, DelayJob delayJob, AnnounceJob previousAnnounceJob) {
        super(ctx, "announce-"+region, newVersion);
        this.vip = vip;
        this.canaryValidationJob = canaryValidationJob;
        this.previousAnnounceJob = previousAnnounceJob;
        this.priorVersion = priorVersion;
        this.delayJob = delayJob;
        this.region = region;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if(previousAnnounceJobFinished()) {
            if(jobExistsAndFailed(delayJob) || jobDoesNotExistOrFailed(canaryValidationJob))
                return true;
        }

        return false;
    }

    @Override
    public boolean isEligible() {
        if(previousAnnounceJobFinished()) {
            return jobDoesNotExistOrCompletedSuccessfully(delayJob) && jobExistsAndCompletedSuccessfully(canaryValidationJob);
        }
        return false;
    }

    private boolean previousAnnounceJobFinished() {
        return previousAnnounceJob == null || previousAnnounceJob.isComplete() || previousAnnounceJob.hasJobFailed();
    }

    public String getVip() {
        return vip;
    }

    public RegionEnum getRegion() {
        return region;
    }

}
