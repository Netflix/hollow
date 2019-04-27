package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import java.time.Duration;

public class PublishCycleListener implements
        CycleListener {
    private final DefaultHollowPublishJobCreator jobCreator;

    public PublishCycleListener(
            DefaultHollowPublishJobCreator jobCreator,
            String vip) {
        this.jobCreator = jobCreator;
    }

    private PublishWorkflowContext ctx;

    // CycleListener

    @Override public void onCycleSkip(CycleSkipReason reason) {
    }

    @Override public void onNewDeltaChain(long version) {
    }

    @Override public void onCycleStart(long version) {
        // Create a context which obtains the current logger from TransformerContext
        // which is bound to to the current cycle
        ctx = jobCreator.beginStagingNewCycle();
    }

    @Override
    public void onCycleComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        // @@@ Move into TransformerCycle.cycle?
        if (status.getType() != Status.StatusType.SUCCESS) {
            // Create Fake job to avoid NPE when constructing CassandraPoisonStateMarkerJob
            // (whose job name is derived from its dependent job name)
            PublicationJob fj = new PublishWorkflowPublicationJob(jobCreator.getContext(), "validation", version) {
                @Override public boolean executeJob() {
                    return false;
                }

                @Override protected boolean isFailedBasedOnDependencies() {
                    return false;
                }

                @Override public boolean isEligible() {
                    return false;
                }
            };
            PoisonStateMarkerJob canaryPoisonStateMarkerJob = jobCreator.createPoisonStateMarkerJob(fj, version);
            canaryPoisonStateMarkerJob.executeJob();
        } else {
            ctx.getStatusIndicator().markSuccess(version);
        }
    }
}
