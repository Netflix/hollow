package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;

/**
 * A no-op version of a {@link CanaryValidationJob} used in unit tests.
 */
public class TestCanaryValidationJob extends CanaryValidationJob {
    public TestCanaryValidationJob(PublishWorkflowContext context, String vip, long cycleVersion,
            AfterCanaryAnnounceJob afterCanaryAnnounceJobs) {
        super(context, vip, cycleVersion, afterCanaryAnnounceJobs);
    }

	@Override public boolean executeJob() {
        return true;
    }
}
