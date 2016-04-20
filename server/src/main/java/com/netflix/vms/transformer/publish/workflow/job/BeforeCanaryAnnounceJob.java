package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;
import java.util.List;
import java.util.Map;

public abstract class BeforeCanaryAnnounceJob extends PublicationJob {

    protected final String vip;
    protected final RegionEnum region;
    private final HollowBlobPublishJob snapshotPublishJob;
    private final HollowBlobPublishJob deltaPublishJob;
    private final CanaryValidationJob previousCanaryValidationJob;
    private final CircuitBreakerJob circuitBreakerJob;
    private final CanaryRollbackJob previousCanaryRollbackJob;


	public BeforeCanaryAnnounceJob(String vip, long newVersion, RegionEnum region, CircuitBreakerJob circuitBreakerJob,
			CanaryValidationJob previousCycleValidationJob, List<PublicationJob> newPublishJobs, CanaryRollbackJob previousCanaryRollBackJob) {
		super("Before-canary-annouce-"+region, newVersion);
        this.vip = vip;
        this.region = region;
        this.previousCanaryValidationJob = previousCycleValidationJob;
        this.circuitBreakerJob = circuitBreakerJob;
        this.snapshotPublishJob = findJob(newPublishJobs, PublishType.SNAPSHOT);
        this.deltaPublishJob = findJob(newPublishJobs, PublishType.DELTA);
        this.previousCanaryRollbackJob = previousCanaryRollBackJob;
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
    protected boolean isEligible() {
        if(previousCanaryValidationJobFinished()) {
            if(jobExistsAndCompletedSuccessfully(circuitBreakerJob)) {
                if(jobExistsAndCompletedSuccessfully(snapshotPublishJob))
                    return true;
                if(jobExistsAndCompletedSuccessfully(deltaPublishJob))
                	if(jobExistsAndCompletedSuccessfully(previousCanaryValidationJob))
                		return true;
                	else if(previousCanaryValidationJob != null && previousCanaryValidationJob.hasJobFailed()
                			&& jobExistsAndCompletedSuccessfully(previousCanaryRollbackJob))
                		return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if(previousCanaryValidationJobFinished()) {
            if(jobDoesNotExistOrFailed(circuitBreakerJob))
                return true;

            if(deltaPublishJob == null && snapshotPublishJob == null)
                return true;

            if(jobDoesNotExistOrFailed(snapshotPublishJob) &&
                    (jobDoesNotExistOrFailed(deltaPublishJob) || jobDoesNotExistOrFailed(previousCanaryValidationJob)))
                return true;
        }

        return false;
    }

    private boolean previousCanaryValidationJobFinished() {
        return previousCanaryValidationJob == null || previousCanaryValidationJob.isComplete() || previousCanaryValidationJob.hasJobFailed();
    }

    /**
     *
     * @return: before canary results
     */
	public abstract Map<VideoCountryKey, Boolean> getTestResults();

	/**
	 * This method is used to clear results even as job is held on for debugging purpose.
	 */
	public abstract void clearResults();

}
