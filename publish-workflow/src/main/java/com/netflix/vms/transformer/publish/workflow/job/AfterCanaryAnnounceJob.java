package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import java.util.Map;

public abstract class AfterCanaryAnnounceJob extends PublishWorkflowPublicationJob {
	private final CanaryAnnounceJob canaryAnnounceJob;
	protected final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob;
	protected final String vip;
	protected final RegionEnum region;

	public AfterCanaryAnnounceJob(PublishWorkflowContext ctx,
	        final String vip,
			final long newVersion,
			final RegionEnum region,
			final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob,
			final CanaryAnnounceJob canaryAnnounceJob) {
		super(ctx, "AfterCanaryAnnounce-"+region, newVersion);
		this.region = region;
		this.vip = vip;
		this.beforeCanaryAnnounceJob = beforeCanaryAnnounceJob;
		this.canaryAnnounceJob = canaryAnnounceJob;
	}

	@Override
	public boolean isEligible() {
		if(jobExistsAndCompletedSuccessfully(canaryAnnounceJob))
			return true;
		return false;
	}

	@Override
	protected boolean isFailedBasedOnDependencies() {
		if(canaryAnnounceJob != null && canaryAnnounceJob.hasJobFailed())
			return true;
		return false;
	}

	/**
	 *
	 * @return: after canary results
	 */
	public abstract Map<VideoCountryKey, Boolean> getTestResults();

	/**
	 * This method is used to clear results even as job is held on for debugging purpose.
	 */
	public abstract void clearResults();

}
