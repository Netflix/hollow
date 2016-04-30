package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

public abstract class PoisonStateMarkerJob extends PublicationJob {

    private final PublicationJob dependency;

    public PoisonStateMarkerJob(PublishWorkflowContext ctx, PublicationJob dependency, long cycleVersion) {
        super(ctx, "mark-poison-after-" + dependency.getJobName(), cycleVersion);
        this.dependency = dependency;
    }

    @Override
    protected boolean isEligible() {
        if(dependency.hasJobFailed())
            return true;
        return dependency.hasJobFailed();
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return dependency.isComplete();
    }

}
