package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;

public abstract class CreateDevSliceJob extends PublishWorkflowPublicationJob {

    private final AnnounceJob dependency;
    
    public CreateDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency, long cycleVersion) {
        super(ctx, "create-dev-slice", cycleVersion);
        this.dependency = dependency;
    }

    @Override
    public boolean isEligible() {
        return jobExistsAndCompletedSuccessfully(dependency);
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return jobExistsAndFailed(dependency);
    }
    
}
