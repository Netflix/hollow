package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class AutoPinbackJob extends PublishWorkflowPublicationJob {

    protected final AnnounceJob announcementJob;
    protected final long waitMillis;

    public AutoPinbackJob(PublishWorkflowContext ctx, AnnounceJob announcement, long waitMillis, long cycleVersion) {
        super(ctx, "auto-pinback", cycleVersion);

        this.waitMillis = waitMillis;
        this.announcementJob = announcement;
    }

    @Override
    public boolean isEligible() {
        return announcementJob.isComplete();
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return announcementJob.hasJobFailed();
    }
}
