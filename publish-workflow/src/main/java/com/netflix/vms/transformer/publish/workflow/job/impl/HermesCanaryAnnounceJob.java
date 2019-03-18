package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;

public class HermesCanaryAnnounceJob extends CanaryAnnounceJob {
    public HermesCanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion,
            RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
        super(ctx, vip, newVersion, region, beforeCanaryAnnounceHook);
    }

	  @Override public boolean executeJob() {
        return ctx.getVipAnnouncer().announce(vip, region, true, getCycleVersion());
    }
}
