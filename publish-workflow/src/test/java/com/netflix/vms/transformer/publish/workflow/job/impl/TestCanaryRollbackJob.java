package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;

/**
 * A no-op version of a {@link CanaryRollbackJob} used in unit tests.
 */
public class TestCanaryRollbackJob extends CanaryRollbackJob {
    public TestCanaryRollbackJob(PublishWorkflowContext context, String vip, long cycleVersion,
            long priorVersion, CanaryValidationJob validationJob) {
        super(context, vip, cycleVersion, priorVersion, priorVersion, validationJob);
    }

    @Override public boolean executeJob() {
        return true;
    }
}
