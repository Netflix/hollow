package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementFailure;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementSuccess;

import com.netflix.config.NetflixConfiguration;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import java.time.Duration;
import java.util.function.LongSupplier;

public class HermesAnnounceListener implements
        RestoreListener,
        CycleListener {
    private final LongSupplier inputVersion;
    private final DefaultHollowPublishJobCreator jobCreator;
    private final String vip;
    private final PublishWorkflowContext ctx;

    public HermesAnnounceListener(
            LongSupplier inputVersion,
            DefaultHollowPublishJobCreator jobCreator,
            String vip) {
        this.inputVersion = inputVersion;
        this.jobCreator = jobCreator;
        this.vip = vip;
        this.ctx = jobCreator.getContext();
    }

    private long previousVersion;

    private long currentVersion;


    // RestoreListener

    @Override
    public void onProducerRestoreStart(long restoreVersion) {
        previousVersion = Long.MIN_VALUE;
    }

    @Override
    public void onProducerRestoreComplete(Status status, long versionDesired, long versionReached, Duration elapsed) {
        previousVersion = versionReached;
    }


    // CycleListener

    @Override public void onCycleSkip(CycleSkipReason reason) {
    }

    @Override public void onNewDeltaChain(long version) {
    }

    @Override public void onCycleStart(long version) {
    }

    @Override
    public void onCycleComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        if (status.getType() != Status.StatusType.SUCCESS) {
            return;
        }

        // @@@ Stagger announcements for the two non-main regions
        // Add to queue
        for (NetflixConfiguration.RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            boolean success = jobCreator.getContext().getVipAnnouncer()
                    .announce(vip, region, false, version, previousVersion);

            logResult(success, version, region);
        }
        ctx.getStatusIndicator().markSuccess(version);
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
}
