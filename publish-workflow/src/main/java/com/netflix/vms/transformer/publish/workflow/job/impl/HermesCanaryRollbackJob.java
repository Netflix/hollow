package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishRegionProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;

public class HermesCanaryRollbackJob extends CanaryRollbackJob {
    public HermesCanaryRollbackJob(PublishWorkflowContext ctx, String vip, long cycleVersion, long priorVersion, CanaryValidationJob validationJob) {
        super(ctx, vip, cycleVersion, priorVersion, ctx.getVipAnnouncer().getPreviouslyAnnouncedCanaryVersion(vip), validationJob);
    }

    @Override public boolean executeJob() {
        long destVersion = rollbackVersion;
        if (rollbackVersion == Long.MIN_VALUE || rollbackVersion == Long.MAX_VALUE)
            destVersion = priorVersion;
        boolean allSucceeded = true;
        for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            if (!ctx.getVipAnnouncer().announce(vip, region, true, destVersion, Long.MIN_VALUE)) {
                allSucceeded = false;
            }
        }
        return allSucceeded;
    }
}
