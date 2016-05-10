package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.MarkedPoisonState;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleFailed;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import java.util.Arrays;


public class CassandraPoisonStateMarkerJob extends PoisonStateMarkerJob {

    private final PublishWorkflowContext ctx;
    private final long cycleVersion;

    public CassandraPoisonStateMarkerJob(PublishWorkflowContext ctx, PublicationJob validationJob, long cycleVersion) {
        super(ctx, validationJob, cycleVersion);
        this.cycleVersion = cycleVersion;
        this.ctx = ctx;
    }

    @Override
    protected boolean executeJob() {
        try {
            ctx.getPoisonStateMarker().markStatePoisoned(getCycleVersion(), true);

            ctx.getLogger().error(
                    Arrays.asList(MarkedPoisonState, TransformCycleFailed),
                    "Marked version "+cycleVersion+" poison.");

        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
        return true;
    }

}
