package com.netflix.vms.transformer.publish.workflow.job;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class BeforeCanaryAnnounceJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    protected final RegionEnum region;
    private final List<HollowBlobPublishJob> snapshotPublishJobs;
    private final List<HollowBlobPublishJob> deltaPublishJobs;
    private final CanaryValidationJob previousCanaryValidationJob;
    private final CircuitBreakerJob circuitBreakerJob;


	public BeforeCanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion, RegionEnum region, CircuitBreakerJob circuitBreakerJob, CanaryValidationJob previousCycleValidationJob, List<PublicationJob> newPublishJobs) {
		super(ctx, "Before-canary-annouce-"+region, newVersion);
        this.vip = vip;
        this.region = region;
        this.previousCanaryValidationJob = previousCycleValidationJob;
        this.circuitBreakerJob = circuitBreakerJob;
        this.snapshotPublishJobs = findJob(newPublishJobs, PublishType.SNAPSHOT);
        this.deltaPublishJobs = findJob(newPublishJobs, PublishType.DELTA);
	}

    private List<HollowBlobPublishJob> findJob(List<PublicationJob> jobs, PublishType type) {
        List<HollowBlobPublishJob> foundJobs = new ArrayList<>();
        
        HollowBlobPublishJob job;
        for(final PublicationJob j : jobs) {
            job = (HollowBlobPublishJob) j;
            if(job.getPublishType() == type)
                foundJobs.add(job);
        }
        
        return foundJobs;
    }

    @Override
    public boolean isEligible() {
        if(previousCanaryValidationJobFinished()) {
            if(jobExistsAndCompletedSuccessfully(circuitBreakerJob)) {
                if(!deltaPublishJobs.isEmpty()) {
                    for(HollowBlobPublishJob deltaPublishJob : deltaPublishJobs) {
                        if(!jobExistsAndCompletedSuccessfully(deltaPublishJob))
                            return false;
                    }
                    return true;
                } else if(!snapshotPublishJobs.isEmpty()){
                    for(HollowBlobPublishJob snapshotPublishJob : snapshotPublishJobs) {
                        if(!jobExistsAndCompletedSuccessfully(snapshotPublishJob))
                            return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        if(previousCanaryValidationJobFinished()) {
            if(jobDoesNotExistOrFailed(circuitBreakerJob))
                return true;

            if(deltaPublishJobs.isEmpty() && snapshotPublishJobs.isEmpty())
                return true;

            if(!deltaPublishJobs.isEmpty()) {
                if(jobDoesNotExistOrFailed(previousCanaryValidationJob))
                    return true;
                
                for(HollowBlobPublishJob deltaPublishJob : deltaPublishJobs)
                    if(deltaPublishJob.hasJobFailed()) return true;
            } else {
                for(HollowBlobPublishJob snapshotPublishJob : snapshotPublishJobs)
                    if(snapshotPublishJob.hasJobFailed()) return true;
            }
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
