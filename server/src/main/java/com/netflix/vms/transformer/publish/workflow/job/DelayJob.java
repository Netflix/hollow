package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

public abstract class DelayJob extends PublicationJob {

    protected final long delayMillis;
    private final PublicationJob dependency;

    public DelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
        super(getJobName(dependency, delayMillis), cycleVersion);
        this.dependency = dependency;
        this.delayMillis = delayMillis;
    }

    @Override
    protected boolean isEligible() {
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
