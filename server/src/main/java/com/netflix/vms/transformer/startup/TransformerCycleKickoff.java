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
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.cinder.producer.NFHollowAnnouncer;
import com.netflix.cinder.producer.NFHollowPublisher;
import com.netflix.gutenberg.GutenbergIdentifiers;
import com.netflix.gutenberg.api.publisher.GutenbergFilePublisher;
import com.netflix.gutenberg.api.publisher.GutenbergValuePublisher;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.hollow.core.util.SimultaneousExecutor;
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
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.fastlane.HollowFastlanePublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesBlobAnnouncer;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesTopicProvider;
import com.netflix.vms.transformer.publish.workflow.util.VipNameUtil;
import com.netflix.vms.transformer.rest.VMSPublishWorkflowHistoryAdmin;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerUploadStatus;

@Singleton
public class TransformerCycleKickoff {

    @Inject
    public TransformerCycleKickoff(
            TransformerCycleInterrupter cycleInterrupter,
            ElasticSearchClient esClient,
            TransformerCassandraHelper cassandraHelper,
            FileStore fileStore,
            Supplier<CinderProducerBuilder> cinderBuilder,
            GutenbergFilePublisher gutenbergFilePublisher,
            GutenbergFileConsumer gutenbergFileConsumer,
            GutenbergValuePublisher gutenbergValuePublisher,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            TransformerConfig transformerConfig,
            Config config,
            OctoberSkyData octoberSkyData,
            CupLibrary cupLibrary,
            FastlaneIdRetriever fastlaneIdRetriever,
            TransformerServerHealthIndicator healthIndicator) {
        FileStore.useMultipartUploadWhenApplicable(true);

        String outputNamespace = "vms-" + transformerConfig.getTransformerVip();
        String nostreamsOutputNamespace = "vms-" + VipNameUtil.getNoStreamsVip(transformerConfig);
        // ultimately canary namespace for announcement = "hollow." + "vms.canary.hollow.blob." + vip + ".announcement"
        String canaryNamespace = HermesTopicProvider.getDataCanaryTopic(transformerConfig.getTransformerVip());
        String devSliceNamespace = "vms-" + HermesTopicProvider.getDevSliceTopic(transformerConfig.getTransformerVip());

        Publisher publisher = new NFHollowPublisher(gutenbergFilePublisher, outputNamespace,
                GutenbergIdentifiers.DEFAULT_REGIONS);
        Publisher nostreamsPublisher = new NFHollowPublisher(gutenbergFilePublisher, nostreamsOutputNamespace,
                GutenbergIdentifiers.DEFAULT_REGIONS);
        Announcer announcer = new NFHollowAnnouncer(gutenbergValuePublisher,
                new NFHollowBlobRetriever(gutenbergFileConsumer, outputNamespace), outputNamespace);
        Announcer nostreamsAnnouncer = new NFHollowAnnouncer(gutenbergValuePublisher,
                new NFHollowBlobRetriever(gutenbergFileConsumer, nostreamsOutputNamespace), nostreamsOutputNamespace);

        /**
         * Trick : Canary announcement is done on a different topic. However, for the NFHollowAnnouncer to verify the publish we use outputNamespace.
         *
         * In the publish workflow, we first upload the blobs for the outputNamespace. The blobs are published (uploaded) to the output namespace
         * but they are not announced to the consumers listening to the output namespace until the integrity checks, validation and canary pass.
         *
         * Once the blobs are uploaded, we then announce the new version on the canary topic.
         * When announcing on the canary topic, NFHollowAnnouncer verifies if the blobs are published/uploaded.
         * Hence using outerNamespace in the NFHollowBlobRetriever when constructing the announcer for canary topic.
         *
         * Note : The client needs to do a similar operation for getting updates for running canary on the outputNamespace.
         * That is, use announcement watcher for the canary topic, and create blob retriever for the outputNamespace.
         */
        Announcer canaryAnnouncer = new NFHollowAnnouncer(gutenbergValuePublisher, new NFHollowBlobRetriever(gutenbergFileConsumer, outputNamespace), canaryNamespace);
        Publisher devSlicePublisher = new NFHollowPublisher(gutenbergFilePublisher, devSliceNamespace, GutenbergIdentifiers.DEFAULT_REGIONS);
        Announcer devSliceAnnouncer = new NFHollowAnnouncer(gutenbergValuePublisher, new NFHollowBlobRetriever(gutenbergFileConsumer, devSliceNamespace), devSliceNamespace);

        TransformerContext ctx = ctx(cycleInterrupter, esClient, transformerConfig, config, octoberSkyData, cupLibrary, cassandraHelper, healthIndicator);
        boolean isFastlane = VipNameUtil.isOverrideVip(ctx.getConfig());
        PublishWorkflowStager publishStager = publishStager(ctx, isFastlane, fileStore, publisher, nostreamsPublisher, announcer, nostreamsAnnouncer, canaryAnnouncer, devSlicePublisher, devSliceAnnouncer, hermesBlobAnnouncer);

        TransformCycle cycle = new TransformCycle(
                ctx,
                fileStore,
                hermesBlobAnnouncer,
                publishStager,
                transformerConfig.getConverterVip(),
                transformerConfig.getTransformerVip(),
                cinderBuilder);

        if (!ctx.getConfig().isProcessRestoreAndInputInParallel()) {
            TransformCycle.restore(new SimultaneousExecutor(2, "vms-restore"), ctx, cycle, fileStore, hermesBlobAnnouncer, isFastlane, false);
        }

        Thread t = new Thread(new Runnable() {
            private long cycleStartTime = 0;
            private int consecutiveCycleFailures = 0;

            @Override
            public void run() {
                while(true) {
                    cycleStartTime = System.currentTimeMillis();
                    try {
                        if (isFastlane) setUpFastlaneContext();

                        cycle.cycle();
                        markCycleSucessful();

                        if (shouldTryCompaction(isFastlane, ctx.getConfig())) {
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
                if (isFastlane)
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

    private TransformerContext ctx(TransformerCycleInterrupter cycleInterrupter, ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config, OctoberSkyData octoberSkyData, CupLibrary cupLibrary, TransformerCassandraHelper cassandraHelper, TransformerServerHealthIndicator healthIndicator) {
        return new TransformerServerContext(
                cycleInterrupter,
                new TransformerServerLogger(transformerConfig, esClient),
                config,
                octoberSkyData,
                cupLibrary,
                new AtlasTransformerMetricRecorder(),
                cassandraHelper,
                new LZ4VMSTransformerFiles(),
                history -> VMSPublishWorkflowHistoryAdmin.history = history);
    }

    private static PublishWorkflowStager publishStager(TransformerContext ctx, boolean isFastlane, FileStore fileStore, Publisher publisher, Publisher nostreamsPublisher, Announcer announcer, Announcer nostreamsAnnouncer, Announcer canaryAnnouncer, Publisher devSlicePublisher, Announcer devSliceAnnouncer, HermesBlobAnnouncer hermesBlobAnnouncer) {
        Supplier<ServerUploadStatus> uploadStatus = VMSServerUploadStatus::get;
        if (isFastlane)
            return new HollowFastlanePublishWorkflowStager(ctx, fileStore, publisher, announcer, hermesBlobAnnouncer, uploadStatus, ctx.getConfig().getTransformerVip());

        return new HollowPublishWorkflowStager(ctx, fileStore, publisher, nostreamsPublisher, announcer, nostreamsAnnouncer, canaryAnnouncer, devSlicePublisher, devSliceAnnouncer, hermesBlobAnnouncer, new DataSlicerImpl(), uploadStatus, ctx.getConfig().getTransformerVip());
    }

    private static boolean shouldTryCompaction(boolean isFastlane, TransformerConfig cfg) {
        return !isFastlane && cfg.isCompactionEnabled();
    }
}
