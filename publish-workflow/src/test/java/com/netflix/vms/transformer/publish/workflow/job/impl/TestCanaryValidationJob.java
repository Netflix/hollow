package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import java.util.Map;

/**
 * A no-op version of a {@link CanaryValidationJob} used in unit tests.
 */
public class TestCanaryValidationJob extends CanaryValidationJob {
    public TestCanaryValidationJob(PublishWorkflowContext context, String vip, long cycleVersion,
            Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs,
            Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs) {
        super(context, vip, cycleVersion, beforeCanaryAnnounceJobs, afterCanaryAnnounceJobs);
    }

	@Override
    protected boolean executeJob() {
        return true;
    }
}
