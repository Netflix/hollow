package com.netflix.vms.transformer.startup;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutiveCycleFailures;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForNextCycleDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleSuccess;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WaitForNextCycle;

import com.google.inject.Inject;
import com.netflix.archaius.api.Config;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.atlas.AtlasTransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
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
import com.netflix.vms.transformer.util.VipUtil;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;

import java.util.function.Supplier;

import netflix.admin.videometadata.uploadstat.ServerUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerUploadStatus;

public class TransformerCycleKickoff {

    @Inject
    public TransformerCycleKickoff(
            ElasticSearchClient esClient,
            TransformerCassandraHelper cassandraHelper,
            FileStore fileStore,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            TransformerConfig transformerConfig,
            Config config,
            OctoberSkyData octoberSkyData,
            FastlaneIdRetriever fastlaneIdRetriever,
            TransformerServerHealthIndicator healthIndicator) {

        FileStore.useMultipartUploadWhenApplicable(true);

        TransformerContext ctx = ctx(esClient, transformerConfig, config, octoberSkyData, cassandraHelper, healthIndicator);
        PublishWorkflowStager publishStager = publishStager(ctx, fileStore, hermesBlobAnnouncer);

        TransformCycle cycle = new TransformCycle(
                ctx,
                fileStore,
                publishStager,
                transformerConfig.getConverterVip(),
                transformerConfig.getTransformerVip());

        if(!isFastlane(ctx.getConfig()))
            restore(cycle, ctx.getConfig(), fileStore, hermesBlobAnnouncer);

        Thread t = new Thread(new Runnable() {
            private long previousCycleStartTime;
            private int consecutiveCycleFailures = 0;

            @Override
            public void run() {
                while(true) {
                    try {
                        waitForMinCycleTimeToPass();
                        if (isFastlane(ctx.getConfig()))
                            setUpFastlaneContext();
                        cycle.cycle();
                        markCycleSucessful();
                    } catch(Throwable th) {
                        markCycleFailed(th);
                    } finally {
                        ctx.getMetricRecorder().recordMetric(ConsecutiveCycleFailures, consecutiveCycleFailures);
                    }
                }
            }

            private void waitForMinCycleTimeToPass() {
                if(isFastlane(ctx.getConfig()))
                    return;

                long minCycleTime = (long)transformerConfig.getMinCycleCadenceMinutes() * 60 * 1000;
                long timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                long msUntilNextCycle = minCycleTime - timeSinceLastCycle;

                if(msUntilNextCycle > 0) {
                    ctx.getLogger().info(WaitForNextCycle, "Waiting " + msUntilNextCycle + "ms until beginning next cycle");
                    ctx.getMetricRecorder().recordMetric(WaitForNextCycleDuration, msUntilNextCycle);
                }

                while(msUntilNextCycle > 0) {
                    try {
                        Thread.sleep(msUntilNextCycle);
                    } catch (InterruptedException ignore) { }

                    timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                    msUntilNextCycle = minCycleTime - timeSinceLastCycle;
                }

                previousCycleStartTime = System.currentTimeMillis();
            }

            private void setUpFastlaneContext() {
                ctx.setFastlaneIds(fastlaneIdRetriever.getFastlaneIds());
                ctx.setTitleOverrideSpecs(fastlaneIdRetriever.getTitleOverrideSpecs());
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

    private final TransformerContext ctx(ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config, OctoberSkyData octoberSkyData, TransformerCassandraHelper cassandraHelper, TransformerServerHealthIndicator healthIndicator) {
        return new TransformerServerContext(
                new TransformerServerLogger(transformerConfig, esClient),
                config,
                octoberSkyData,
                new AtlasTransformerMetricRecorder(),
                cassandraHelper,
                new LZ4VMSTransformerFiles(),
                (history) -> {
                    VMSPublishWorkflowHistoryAdmin.history = history;
                });
    }

    private final PublishWorkflowStager publishStager(TransformerContext ctx, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer) {
        Supplier<ServerUploadStatus> uploadStatus = () -> VMSServerUploadStatus.get();
        if(isFastlane(ctx.getConfig()))
            return new HollowFastlanePublishWorkflowStager(ctx, fileStore, hermesBlobAnnouncer, uploadStatus, ctx.getConfig().getTransformerVip());

        return new HollowPublishWorkflowStager(ctx, fileStore, hermesBlobAnnouncer, new DataSlicerImpl(), uploadStatus, ctx.getConfig().getTransformerVip());
    }

    private void restore(TransformCycle cycle, TransformerConfig cfg, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer) {
        if(cfg.isRestoreFromPreviousStateEngine() && !isFastlane(cfg)) {
            long latestVersion = hermesBlobAnnouncer.getLatestAnnouncedVersionFromCassandra(cfg.getTransformerVip());

            if(latestVersion != Long.MIN_VALUE) {
                VMSOutputDataClient outputClient = new VMSOutputDataClient(fileStore, cfg.getTransformerVip());
                outputClient.triggerRefreshTo(latestVersion);

                cycle.restore(outputClient);
            } else {
                throw new IllegalStateException("Cannot restore from previous state -- previous state does not exist?  If this is expected (e.g. a new VIP), temporarily set vms.restoreFromPreviousStateEngine=false.");
            }
        }
    }

    private boolean isFastlane(TransformerConfig cfg) {
        return VipUtil.isOverrideVip(cfg);
    }

}
