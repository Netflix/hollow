package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import java.util.Map;

public abstract class CanaryValidationJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    private final Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs;

    public CanaryValidationJob(PublishWorkflowContext ctx, String vip, long cycleVersion, Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs, Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs) {
        super(ctx, "canary-validation", cycleVersion);
        this.vip = vip;
        this.afterCanaryAnnounceJobs = afterCanaryAnnounceJobs;
    }

    @Override
    public boolean isEligible() {
        for(final AfterCanaryAnnounceJob dependency : afterCanaryAnnounceJobs.values()) {
            if(!dependency.isComplete())
                return false;
        }
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        for(final AfterCanaryAnnounceJob dependency : afterCanaryAnnounceJobs.values()) {
            if(dependency.hasJobFailed())
                return true;
        }
        return false;
    }

}
