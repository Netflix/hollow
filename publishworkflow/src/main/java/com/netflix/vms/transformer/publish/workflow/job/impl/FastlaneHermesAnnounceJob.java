package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;

import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.videometadata.audit.ErrorCodeLogger;
import com.netflix.videometadata.audit.VMSErrorCode.ErrorCode;
import com.netflix.videometadata.audit.VMSLogManager;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import java.util.List;

public class FastlaneHermesAnnounceJob extends PublishWorkflowPublicationJob {

    private static final ErrorCodeLogger LOGGER = VMSLogManager.getErrorCodeLogger(FastlaneHermesAnnounceJob.class);

    private final String vip;
    private final long priorVersion;
    private final RegionEnum region;
    private final HollowBlobPublishJob snapshotPublishJob;
    private final HollowBlobPublishJob deltaPublishJob;
    private final FastlaneHermesAnnounceJob previousAnnounceJob;

    public FastlaneHermesAnnounceJob(PublishWorkflowContext ctx, long priorVersion, long newVersion, RegionEnum region, List<PublicationJob> newPublishJobs, FastlaneHermesAnnounceJob previousAnnounceJob) {
        super(ctx, "announce-" + region, newVersion);
        this.vip = ctx.getVip();
        this.priorVersion = priorVersion;
        this.region = region;
        this.snapshotPublishJob = findJob(newPublishJobs, PublishType.SNAPSHOT);
        this.deltaPublishJob = findJob(newPublishJobs, PublishType.DELTA);
        this.previousAnnounceJob = previousAnnounceJob;
    }

    private HollowBlobPublishJob findJob(List<PublicationJob> jobs, PublishType type) {
        HollowBlobPublishJob job;
        for(final PublicationJob j : jobs) {
            job = (HollowBlobPublishJob) j;
            if(job.getPublishType() == type)
                return job;
        }
        return null;
    }


    @Override
    protected boolean executeJob() {
        boolean success = ctx.getVipAnnouncer().announce(vip, region, false, getCycleVersion(), priorVersion);
        logResult(success);
        return success;
    }

    private void logResult(boolean success) {
        // These log error codes will be used in dashboard to mark that version as failed.
        // TODO: need to figure out a way to expose this message in Errors tab.
        ErrorCode validationResult = ErrorCode.HollowAnnounceFailure;
        String resultStr = "failure";
        if(success){
            validationResult = ErrorCode.HollowAnnounceSuccess;
            resultStr = "success";
        }
        LOGGER.logWithExplicitCycleVersion(validationResult, String.valueOf(getCycleVersion()), "Hollow data announce "+resultStr+": for version "+getCycleVersion()+" for vip "+vip+" region "+region);
    }

    @Override
	public boolean isEligible() {
        if(previousAnnounceJobFinished()) {
            if(jobExistsAndCompletedSuccessfully(snapshotPublishJob))
                return true;
            if(jobExistsAndCompletedSuccessfully(deltaPublishJob) && jobExistsAndCompletedSuccessfully(previousAnnounceJob))
                return true;
        }
        return false;
    }

    private boolean previousAnnounceJobFinished() {
        return previousAnnounceJob == null || previousAnnounceJob.isComplete() || previousAnnounceJob.hasJobFailed();
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if(previousAnnounceJobFinished()) {
            if(deltaPublishJob == null && snapshotPublishJob == null)
                return true;

            if(jobDoesNotExistOrFailed(snapshotPublishJob) &&
                    (jobDoesNotExistOrFailed(deltaPublishJob) || jobDoesNotExistOrFailed(previousAnnounceJob)))
                return true;
        }

        return false;
    }

}
