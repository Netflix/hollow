package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A no-op version of a {@link BeforeCanaryAnnounceJob} used in unit tests.
 */
public class TestBeforeCanaryAnnounceJob extends BeforeCanaryAnnounceJob {
	public TestBeforeCanaryAnnounceJob(PublishWorkflowContext context, String vip, long newVersion,
        NetflixConfiguration.RegionEnum region, CircuitBreakerJob circuitBreakerJob,
        List<PublicationJob> newPublishJobs) {
		super(context, vip, newVersion, region, circuitBreakerJob, newPublishJobs);
	}

	@Override public boolean executeJob() {
        return true;
	}

	@Override
	public Map<VideoCountryKey, Boolean> getTestResults() {
		return Collections.emptyMap();
	}

	@Override
	public void clearResults() {
	}
}
