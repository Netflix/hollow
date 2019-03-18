package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;

/**
 * A no-op version of a {@link TestCanaryAnnounceJob} used in unit tests.
 */
public class TestCanaryAnnounceJob extends CanaryAnnounceJob {
    public TestCanaryAnnounceJob(PublishWorkflowContext context, String vip, long newVersion,
        RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
		super(context, vip, newVersion, region, beforeCanaryAnnounceHook);
	}

	@Override public boolean executeJob() {
        return true;
    }

}
