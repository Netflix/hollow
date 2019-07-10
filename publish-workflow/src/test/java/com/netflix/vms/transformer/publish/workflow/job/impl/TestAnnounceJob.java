package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;
import java.util.Map;

/**
 * A version of an {@link AnnounceJob} used in unit tests. It does nothing except for mark
 * the cycle as successful.
 */
public class TestAnnounceJob extends AnnounceJob {
    public TestAnnounceJob(PublishWorkflowContext context, String vip, long priorVersion, long newVersion,
            RegionEnum region, Map<String, String> metadata, CanaryValidationJob validationJob, DelayJob delayJob,
            AnnounceJob previousAnnounceJob) {
        super(context, vip, priorVersion, newVersion, region, metadata, validationJob, delayJob, previousAnnounceJob);
    }

    @Override public boolean executeJob() {
        ctx.getStatusIndicator().markSuccess(getCycleVersion());
        return true;
    }
}
