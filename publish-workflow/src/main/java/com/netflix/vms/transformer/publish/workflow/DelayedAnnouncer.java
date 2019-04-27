package com.netflix.vms.transformer.publish.workflow;

import com.netflix.config.NetflixConfiguration;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public abstract class DelayedAnnouncer implements HollowProducer.Announcer {
    private final DefaultHollowPublishJobCreator jobCreator;
    private final String vip;
    private final LongSupplier previousVersion;
    private final ScheduledExecutorService ses;

    public DelayedAnnouncer(
            DefaultHollowPublishJobCreator jobCreator, String vip, LongSupplier previousVersion) {
        this.jobCreator = jobCreator;
        this.vip = vip;
        this.previousVersion = previousVersion;
        this.ses = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "vmstransformer-producer-announcer");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public void announce(long version) {
        PublishWorkflowContext ctx = jobCreator.getContext();
        PublishRegionProvider regionProvider = new PublishRegionProvider(
                ctx.getLogger());

        long pv = previousVersion.getAsLong();
        // Announce to primary region
        announce(ctx, vip, pv, version, regionProvider.getPrimaryRegion());

        for (NetflixConfiguration.RegionEnum region : regionProvider.getNonPrimaryRegions()) {
            long delay = regionProvider.getPublishDelayInSeconds(region);
            if (delay == 0) {
                announce(ctx, vip, pv, version, region);
            } else {
                ses.schedule(
                        () -> announce(ctx, vip, pv, version, region),
                        delay, TimeUnit.SECONDS);
            }
        }
    }

    abstract void announce(
            PublishWorkflowContext ctx,
            String vip, long previousVersion, long currentVersion,
            NetflixConfiguration.RegionEnum region);
}
