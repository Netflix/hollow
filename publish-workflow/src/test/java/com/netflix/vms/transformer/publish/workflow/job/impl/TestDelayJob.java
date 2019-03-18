package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;

/**
 * A no-op version of a {@link DelayJob} used in unit tests.
 */
public class TestDelayJob extends DelayJob {
    public TestDelayJob(PublishWorkflowContext context, PublicationJob dependency, long delayMillis, long cycleVersion) {
        super(context, dependency, delayMillis, cycleVersion);
    }

    @Override public boolean executeJob() {
        return true;
    }
}
