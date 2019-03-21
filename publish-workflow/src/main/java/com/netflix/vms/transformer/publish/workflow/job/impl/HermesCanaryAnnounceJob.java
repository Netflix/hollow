package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishRegionProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;

public class HermesCanaryAnnounceJob extends CanaryAnnounceJob {
    public HermesCanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion,
            BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
        super(ctx, vip, newVersion, beforeCanaryAnnounceHook);
    }

	  @Override public boolean executeJob() {
          // This does a global announcement (we only run canary in us-east, plus even if it ran in all 3 regions.
          // It's okay to do global announcements, it would just mean all canary instances get the version to canary at the same time)
        ctx.getCanaryAnnouncer().announce(getCycleVersion());
        boolean success = true;
        // announce canary version in all 3 regions as separate announcements (does not matter since we only run canary in us-east-1)
        for (RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            success = success & ctx.getVipAnnouncer().announce(vip, region, true, getCycleVersion());
        }
        return success;
    }
}
