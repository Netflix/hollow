package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

public abstract class AutoPinbackJob extends PublicationJob {

    protected final AnnounceJob announcementJob;
    protected final long waitMillis;

    public AutoPinbackJob(PublishWorkflowContext ctx, AnnounceJob announcement, long waitMillis, long cycleVersion) {
        super(ctx, "auto-pinback", cycleVersion);

        this.waitMillis = waitMillis;
        this.announcementJob = announcement;
    }

    @Override
    protected boolean isEligible() {
        return announcementJob.isComplete();
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return announcementJob.hasJobFailed();
    }
}
