package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.AnnouncementFailure;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.AnnouncementSuccess;

import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;

public class HermesAnnounceJob extends AnnounceJob {
    public HermesAnnounceJob(PublishWorkflowContext ctx,
                             long priorVersion,
                             long newVersion,
                             RegionEnum region,
                             CanaryValidationJob validationJob,
                             DelayJob delayJob,
                             AnnounceJob previousAnnounceJob) {

        super(ctx, ctx.getVip(), priorVersion, newVersion, region, validationJob, delayJob, previousAnnounceJob);
    }

    @Override
    protected boolean executeJob() {
        boolean success = ctx.getVipAnnouncer().announce(vip, region, false, getCycleVersion(), priorVersion);
        ctx.getStatusIndicator().markSuccess(getCycleVersion());
        logResult(success);
        return success;
    }

    private void logResult(boolean success) {
        if(success) {
            ctx.getLogger().info(AnnouncementSuccess, "Hollow data announce success: for version " + getCycleVersion() + " for vip "+vip+" region " + region);
            ctx.getMetricRecorder().incrementCounter(Metric.AnnounceSuccess, 1, "destination.region", region.toString());
        } else {
            ctx.getLogger().error(AnnouncementFailure, "Hollow data announce failure: for version " + getCycleVersion() + " for vip "+vip+" region "+region);
        }
    }
}
