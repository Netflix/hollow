package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementFailure;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementSuccess;

import com.netflix.config.NetflixConfiguration;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import java.time.Duration;

public class HermesAnnounceListener implements
        RestoreListener,
        AnnouncementListener,
        CycleListener {
    private final DefaultHollowPublishJobCreator jobCreator;
    private final String vip;

    public HermesAnnounceListener(
            DefaultHollowPublishJobCreator jobCreator,
            String vip) {
        this.jobCreator = jobCreator;
        this.vip = vip;
    }

    private long previousVersion;

    private PublishWorkflowContext ctx;

    // RestoreListener

    @Override
    public void onProducerRestoreStart(long restoreVersion) {
        previousVersion = Long.MIN_VALUE;
    }

    @Override
    public void onProducerRestoreComplete(Status status, long versionDesired, long versionReached, Duration elapsed) {
        previousVersion = versionReached;
    }


    // AnnouncementListener

    @Override
    public void onAnnouncementStart(long version) {
    }

    @Override
    public void onAnnouncementComplete(
            Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        if (status.getType() != Status.StatusType.SUCCESS) {
            return;
        }

        // @@@ Stagger announcements for the two non-main regions
        // Add to queue
        for (NetflixConfiguration.RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            boolean success = ctx.getVipAnnouncer()
                    .announce(vip, region, false, version, previousVersion);

            logResult(success, version, region);
        }
    }

    private void logResult(boolean success, long version, NetflixConfiguration.RegionEnum region) {
        if (success) {
            ctx.getLogger().info(AnnouncementSuccess, "Hollow data announce success: for version "
                    + version + " for vip " + vip + " region " + region);
            ctx.getMetricRecorder().incrementCounter(TransformerMetricRecorder.Metric.AnnounceSuccess, 1,
                    "destination.region", region.toString());
        } else {
            ctx.getLogger().error(AnnouncementFailure, "Hollow data announce failure: for version "
                    + version + " for vip " + vip + " region " + region);
        }
    }


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
