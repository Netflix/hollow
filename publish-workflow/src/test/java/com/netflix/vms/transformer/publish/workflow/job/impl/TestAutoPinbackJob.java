package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AutoPinbackJob;

/**
 * A no-op version of an {@link AutoPinbackJob} used in unit tests.
 */
public class TestAutoPinbackJob extends AutoPinbackJob {
    public TestAutoPinbackJob(PublishWorkflowContext context, AnnounceJob announcement, long waitMillis, long cycleVersion) {
        super(context, announcement, waitMillis, cycleVersion);
    }

    @Override public boolean executeJob() {
        return false;
    }
}
