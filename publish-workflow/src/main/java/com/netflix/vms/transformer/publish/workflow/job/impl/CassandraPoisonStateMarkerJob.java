package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.MarkedPoisonState;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import java.util.Arrays;

public class CassandraPoisonStateMarkerJob extends PoisonStateMarkerJob {

    private final PublishWorkflowContext ctx;
    private final HollowBlobDataProvider hollowBlobDataProvider;

    public CassandraPoisonStateMarkerJob(PublishWorkflowContext ctx, PublicationJob validationJob, HollowBlobDataProvider dataProvider, long cycleVersion) {
        super(ctx, validationJob, cycleVersion);
        this.ctx = ctx;
        this.hollowBlobDataProvider = dataProvider;
    }

    @Override public boolean executeJob() {
        try {
            ctx.getStatusIndicator().markFailure(getCycleVersion());
            hollowBlobDataProvider.revertToPriorVersion();
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
