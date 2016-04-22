package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;


public class CassandraPoisonStateMarkerJob extends PoisonStateMarkerJob {

    private final PublishWorkflowContext ctx;
    private final long cycleVersion;

    public CassandraPoisonStateMarkerJob(PublicationJob validationJob, PublishWorkflowContext ctx, long cycleVersion) {
        super(validationJob, cycleVersion);
        this.cycleVersion = cycleVersion;
        this.ctx = ctx;
    }

    @Override
    protected boolean executeJob() {
        try {
            ctx.getPoisonStateMarker().markStatePoisoned(getCycleVersion(), true);
            ctx.getLogger().error("HollowMarkStatePoison", "Marked version "+cycleVersion+" poison.");
            ctx.getLogger().error("RefreshAttemptOnVMSCachesFailed", "Marked version "+cycleVersion+" poison.");
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
        return true;
    }

}
