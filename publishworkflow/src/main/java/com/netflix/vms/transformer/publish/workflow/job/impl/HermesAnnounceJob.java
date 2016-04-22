package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;

public class HermesAnnounceJob extends AnnounceJob {

    PublishWorkflowContext ctx;

    public HermesAnnounceJob(PublishWorkflowContext ctx,
                             long priorVersion,
                             long newVersion,
                             RegionEnum region,
                             CanaryValidationJob validationJob,
                             DelayJob delayJob,
                             AnnounceJob previousAnnounceJob) {

        super(ctx.getVip(), priorVersion, newVersion, region, validationJob, delayJob, previousAnnounceJob);
        this.ctx = ctx;
    }

    @Override
    protected boolean executeJob() {
        boolean success = HermesAnnounceUtil.announce(vip, region, false, getCycleVersion(), priorVersion);
        logResult(success);
        return success;
    }

    private void logResult(boolean success) {
        if(success)
            ctx.getLogger().info("HollowAnnounceSuccess", "Hollow data announce success: for version " + getCycleVersion() + " for vip "+vip+" region " + region);
        else
            ctx.getLogger().error("HollowAnnounceFailure", "Hollow data announce failure: for version " + getCycleVersion() + " for vip "+vip+" region "+region);
    }

}
