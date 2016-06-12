package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.util.List;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;

public class HermesCanaryAnnounceJob extends CanaryAnnounceJob {
    public HermesCanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion,
            RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceHook,
            final CanaryValidationJob previousCycleValidationJob,
            final List<PublicationJob> newPublishJobs) {
        super(ctx, vip, newVersion, region, beforeCanaryAnnounceHook, previousCycleValidationJob,
                newPublishJobs);
    }

	  @Override
    protected boolean executeJob() {
        return ctx.getVipAnnouncer().announce(vip, region, true, getCycleVersion());
    }
}
