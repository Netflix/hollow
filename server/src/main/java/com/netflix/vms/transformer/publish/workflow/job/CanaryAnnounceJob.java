package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import java.util.List;

public abstract class CanaryAnnounceJob extends PublicationJob {

    protected final String vip;
    protected final RegionEnum region;
	private final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob;
	private final CanaryValidationJob previousCycleValidationJob;
	private HollowBlobPublishJob snapshotJob;


    public CanaryAnnounceJob(final String vip, final long newVersion, final RegionEnum region, final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob,
			final CanaryValidationJob previousCycleValidationJob,
			final List<PublicationJob> newPublishJobs) {
        super("canary-announce-"+region, newVersion);
        this.vip = vip;
        this.region = region;
        this.beforeCanaryAnnounceJob = beforeCanaryAnnounceJob;
        this.previousCycleValidationJob = previousCycleValidationJob;
		this.snapshotJob = findJob(newPublishJobs, PublishType.SNAPSHOT);
    }

    private HollowBlobPublishJob findJob(final List<PublicationJob> jobs, final PublishType type) {
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
		if (jobDoesNotExistOrCompletedSuccessfully(beforeCanaryAnnounceJob)) {
			if(jobExistsAndFailed(previousCycleValidationJob)){
				if(jobExistsAndCompletedSuccessfully(snapshotJob)){
					return true;
				}
			}else 
				return true;
		}
		return false;
	}

    @Override
    protected boolean isFailedBasedOnDependencies() {
		if (jobDoesNotExistOrFailed(beforeCanaryAnnounceJob))
			return true;

        return false;
    }
}
