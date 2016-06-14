package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class DelayJob extends PublishWorkflowPublicationJob {
    protected final long delayMillis;
    private final PublicationJob dependency;

    public DelayJob(PublishWorkflowContext ctx, PublicationJob dependency, long delayMillis, long cycleVersion) {
        super(ctx, getJobName(dependency, delayMillis), cycleVersion);
        this.dependency = dependency;
        this.delayMillis = delayMillis;
    }

    @Override
    public boolean isEligible() {
        return jobDoesNotExistOrCompletedSuccessfully(dependency);
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return jobExistsAndFailed(dependency);
    }

    private static String getJobName(PublicationJob dependency, long delayMillis) {
        if(dependency == null)
            return "delay-" + delayMillis;
        return "delay-after-" + dependency.getJobName() + "-" + delayMillis;
    }
}
