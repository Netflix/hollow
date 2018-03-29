package com.netflix.vms.transformer.startup;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric.P5_WaitForNextCycleDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutiveCycleFailures;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CycleInterrupted;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleSuccess;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WaitForNextCycle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.archaius.api.Config;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.internal.hollow.factory.HollowAnnouncerFactory;
import com.netflix.internal.hollow.factory.HollowPublisherFactory;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.atlas.AtlasTransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerCycleInterrupter;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.context.TransformerServerContext;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.fastlane.FastlaneIdRetriever;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator;
import com.netflix.vms.transformer.input.VMSOutputDataClient;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.fastlane.HollowFastlanePublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesBlobAnnouncer;
import com.netflix.vms.transformer.rest.VMSPublishWorkflowHistoryAdmin;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.OverrideVipNameUtil;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerUploadStatus;

@Singleton
public class TransformerCycleKickoff {
    private final TransformerContext ctx;

    @Inject
    public TransformerCycleKickoff(
            TransformerCycleInterrupter cycleInterrupter,
            ElasticSearchClient esClient,
            TransformerCassandraHelper cassandraHelper,
            FileStore fileStore,
            HollowPublisherFactory publisherFactory,
            HollowAnnouncerFactory announcerFactory,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            TransformerConfig transformerConfig,
            Config config,
            OctoberSkyData octoberSkyData,
            CupLibrary cupLibrary,
            FastlaneIdRetriever fastlaneIdRetriever,
            TransformerServerHealthIndicator healthIndicator) {

        FileStore.useMultipartUploadWhenApplicable(true);

        Publisher publisher = publisherFactory.getForNamespace("vms-" + transformerConfig.getTransformerVip());
        Publisher nostreamsPublisher = publisherFactory.getForNamespace("vms-" + transformerConfig.getTransformerVip() + "_nostreams");
        Announcer announcer = announcerFactory.getForNamespace("vms-" + transformerConfig.getTransformerVip());
        Announcer nostreamsAnnouncer = announcerFactory.getForNamespace("vms-" + transformerConfig.getTransformerVip() + "_nostreams");

        ctx = ctx(cycleInterrupter, esClient, transformerConfig, config, octoberSkyData, cupLibrary, cassandraHelper, healthIndicator);
        PublishWorkflowStager publishStager = publishStager(ctx, fileStore, publisher, nostreamsPublisher, announcer, nostreamsAnnouncer, hermesBlobAnnouncer);

        TransformCycle cycle = new TransformCycle(
                ctx,
                fileStore,
                publishStager,
                transformerConfig.getConverterVip(),
                transformerConfig.getTransformerVip());

        restore(cycle, ctx.getConfig(), fileStore, hermesBlobAnnouncer);

        Thread t = new Thread(new Runnable() {
            private long cycleStartTime = 0;
            private int consecutiveCycleFailures = 0;

            @Override
            public void run() {
                boolean isFastlane = isFastlane(ctx.getConfig());
                while(true) {
                    cycleStartTime = System.currentTimeMillis();
                    try {
                        if (isFastlane) setUpFastlaneContext();

                        cycle.cycle();
                        markCycleSucessful();

                        if(shouldTryCompaction(ctx.getConfig())) {
                            cycle.tryCompaction();
                            markCycleSucessful();
                        }

                        waitForMinCycleTimeToPass();
                    } catch(Throwable th) {
                        markCycleFailed(th);
                    } finally {
                        ctx.getMetricRecorder().recordMetric(ConsecutiveCycleFailures, consecutiveCycleFailures);

                        // Reset for next cycle
                        ctx.getCycleInterrupter().reset(ctx.getCurrentCycleId());
                        for (DurationMetric metric : DurationMetric.values()) {
                            ctx.getMetricRecorder().resetTimer(metric);
                        }
                    }
                }
            }

            private void waitForMinCycleTimeToPass() {
                if(isFastlane(ctx.getConfig()))
                    return;

                long minCycleTime = (long)transformerConfig.getMinCycleCadenceMinutes() * 60 * 1000;
                long timeSinceLastCycle = System.currentTimeMillis() - cycleStartTime;
                long msUntilNextCycle = minCycleTime - timeSinceLastCycle;
                ctx.getLogger().info(WaitForNextCycle, "Waiting {}ms until beginning next cycle", Math.max(msUntilNextCycle, 0));

                long sleepStart = System.currentTimeMillis();
                ctx.getMetricRecorder().startTimer(P5_WaitForNextCycleDuration);
                while(msUntilNextCycle > 0) {
                    if (ctx.getCycleInterrupter().isCycleInterrupted()) {
                        ctx.getLogger().info(CycleInterrupted, "Interrupted while waiting for next Cycle ");
                        break;
                    }

                    try { // Sleep in small intervals to give it a chance to react to cycle interrupt
                        long sleepInMS = Math.min(10000, msUntilNextCycle);
                        Thread.sleep(sleepInMS);
                    } catch (InterruptedException ignore) { }

                    timeSinceLastCycle = System.currentTimeMillis() - cycleStartTime;
                    msUntilNextCycle = minCycleTime - timeSinceLastCycle;
                }
                long sleepDuration = System.currentTimeMillis() - sleepStart;
                ctx.stopTimerAndLogDuration(P5_WaitForNextCycleDuration);
                ctx.getLogger().info(WaitForNextCycle, "Waited {}", OutputUtil.formatDuration(sleepDuration, true));
            }

            private void setUpFastlaneContext() {
                ctx.setFastlaneIds(fastlaneIdRetriever.getFastlaneIds());
                ctx.setPinTitleSpecs(fastlaneIdRetriever.getPinnedTitleSpecs());
            }

            private void markCycleFailed(Throwable th) {
                consecutiveCycleFailures++;
                healthIndicator.cycleFailed(th);
                ctx.getLogger().error(TransformCycleFailed, "TransformerCycleKickoff failed cycle", th);
            }

            private void markCycleSucessful() {
                consecutiveCycleFailures = 0;
                ctx.getLogger().info(TransformCycleSuccess, "Cycle succeeded");
                healthIndicator.cycleSucessful();
            }

        });

        t.setDaemon(true);
        t.setName("vmstransformer-cycler");
        t.start();
    }

    private final TransformerContext ctx(TransformerCycleInterrupter cycleInterrupter, ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config, OctoberSkyData octoberSkyData, CupLibrary cupLibrary, TransformerCassandraHelper cassandraHelper, TransformerServerHealthIndicator healthIndicator) {
        return new TransformerServerContext(
                cycleInterrupter,
                new TransformerServerLogger(transformerConfig, esClient),
                config,
                octoberSkyData,
                cupLibrary,
                new AtlasTransformerMetricRecorder(),
                cassandraHelper,
                new LZ4VMSTransformerFiles(),
                (history) -> {
                    VMSPublishWorkflowHistoryAdmin.history = history;
                });
    }

    private final PublishWorkflowStager publishStager(TransformerContext ctx, FileStore fileStore, Publisher publisher, Publisher nostreamsPublisher, Announcer announcer, Announcer nostreamsAnnouncer, HermesBlobAnnouncer hermesBlobAnnouncer) {
        Supplier<ServerUploadStatus> uploadStatus = () -> VMSServerUploadStatus.get();
        if(isFastlane(ctx.getConfig()))
            return new HollowFastlanePublishWorkflowStager(ctx, fileStore, publisher, announcer, hermesBlobAnnouncer, uploadStatus, ctx.getConfig().getTransformerVip());

        return new HollowPublishWorkflowStager(ctx, fileStore, publisher, nostreamsPublisher, announcer, nostreamsAnnouncer, hermesBlobAnnouncer, new DataSlicerImpl(), uploadStatus, ctx.getConfig().getTransformerVip());
    }

    private void restore(TransformCycle cycle, TransformerConfig cfg, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer) {
        ctx.getMetricRecorder().startTimer(DurationMetric.P0_RestoreDataDuration);
        try {
            if(cfg.isRestoreFromPreviousStateEngine()) {
                long latestVersion = hermesBlobAnnouncer.getLatestAnnouncedVersionFromCassandra(cfg.getTransformerVip());
                long restoreVersion = cfg.getRestoreFromSpecificVersion() != null ? cfg.getRestoreFromSpecificVersion() : latestVersion;

                if(restoreVersion != Long.MIN_VALUE) {
                    VMSOutputDataClient outputClient = new VMSOutputDataClient(fileStore, cfg.getTransformerVip());
                    outputClient.triggerRefreshTo(restoreVersion);
                    if(outputClient.getCurrentVersionId() != restoreVersion)
                        throw new IllegalStateException("Failed to restore (with streams) from state: " + restoreVersion);

                    if(isFastlane(cfg)) {
                        cycle.restore(outputClient, null, true);
                    } else {
                        VMSOutputDataClient nostreamsOutputClient = new VMSOutputDataClient(fileStore, cfg.getTransformerVip() + "_nostreams");
                        nostreamsOutputClient.triggerRefreshTo(restoreVersion);
                        if(nostreamsOutputClient.getCurrentVersionId() != restoreVersion)
                            throw new IllegalStateException("Failed to restore (nostreams) from state: " + restoreVersion);

                        cycle.restore(outputClient, nostreamsOutputClient, false);
                    }

                } else {
                    if(cfg.isFailIfRestoreNotAvailable())
                        throw new IllegalStateException("Cannot restore from previous state -- previous state does not exist?  If this is expected (e.g. a new VIP), temporarily set vms.failIfRestoreNotAvailable=false");
                }
            }
        } finally {
            ctx.stopTimerAndLogDuration(DurationMetric.P0_RestoreDataDuration);
        }
    }

    private boolean shouldTryCompaction(TransformerConfig cfg) {
        return !isFastlane(cfg) && cfg.isCompactionEnabled();
    }

    private boolean isFastlane(TransformerConfig cfg) {
        return OverrideVipNameUtil.isOverrideVip(cfg);
    }

}
