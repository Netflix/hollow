package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

public abstract class AutoPinbackJob extends PublicationJob {

    protected final AnnounceJob announcementJob;
    protected final long waitMillis;

    public AutoPinbackJob(AnnounceJob announcement, long waitMillis, long cycleVersion) {
        super("auto-pinback", cycleVersion);

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
