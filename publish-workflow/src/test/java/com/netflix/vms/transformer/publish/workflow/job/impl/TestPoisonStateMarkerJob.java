package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;

/**
 * A version of an {@link PoisonStateMarkerJob} used in unit tests. It does nothing except
 * for mark the cycle as failed.
 */
public class TestPoisonStateMarkerJob extends PoisonStateMarkerJob {
    public TestPoisonStateMarkerJob(PublishWorkflowContext context, PublicationJob validationJob, long cycleVersion) {
        super(context, validationJob, cycleVersion);
    }

    @Override public boolean executeJob() {
        ctx.getStatusIndicator().markFailure(getCycleVersion());
        return true;
    }
}
