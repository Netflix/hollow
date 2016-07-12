package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.MarkedPoisonState;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;

import java.util.Arrays;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;

public class CassandraPoisonStateMarkerJob extends PoisonStateMarkerJob {
    private final PublishWorkflowContext ctx;

    public CassandraPoisonStateMarkerJob(PublishWorkflowContext ctx, PublicationJob validationJob, long cycleVersion) {
        super(ctx, validationJob, cycleVersion);
        this.ctx = ctx;
    }

    @Override
    protected boolean executeJob() {
        try {
            ctx.getStatusIndicator().markFailure(getCycleVersion());
            ctx.getPoisonStateMarker().markStatePoisoned(getCycleVersion(), true);
            ctx.getLogger().error(
                    Arrays.asList(MarkedPoisonState, TransformCycleFailed),
                    "Marked version {} poison.", getCycleVersion());
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
        return true;
    }
}
