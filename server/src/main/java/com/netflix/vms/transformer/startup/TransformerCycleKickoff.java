package com.netflix.vms.transformer.startup;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutiveCycleFailures;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForNextCycleDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleSuccess;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WaitForNextCycle;

import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobRetriever;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.google.inject.Inject;
import com.netflix.archaius.api.Config;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobPublisher;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.atlas.AtlasTransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerContext;
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
import com.netflix.vms.transformer.util.OverrideVipNameUtil;
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
            AWSCredentialsProvider credentials,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            TransformerConfig transformerConfig,
            Config config,
            OctoberSkyData octoberSkyData,
            CupLibrary cupLibrary,
            FastlaneIdRetriever fastlaneIdRetriever,
            TransformerServerHealthIndicator healthIndicator) {

        FileStore.useMultipartUploadWhenApplicable(true);

        TransformerContext ctx = ctx(esClient, transformerConfig, config, octoberSkyData, cupLibrary, cassandraHelper, healthIndicator);
        PublishWorkflowStager publishStager = publishStager(ctx, fileStore, credentials, hermesBlobAnnouncer);
        NetflixS3BlobRetriever blobRetriever = new NetflixS3BlobRetriever(credentials, "vms." + transformerConfig.getTransformerVip());

        TransformCycle cycle = new TransformCycle(
                ctx,
                fileStore,
                blobRetriever,
                publishStager,
                transformerConfig.getConverterVip(),
                transformerConfig.getTransformerVip());

        restore(cycle, ctx.getConfig(), fileStore, credentials, hermesBlobAnnouncer);

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
                        if(shouldTryCompaction(ctx.getConfig())) {
                            cycle.tryCompaction();
                            markCycleSucessful();
                        }
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

    private final TransformerContext ctx(ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config, OctoberSkyData octoberSkyData, CupLibrary cupLibrary, TransformerCassandraHelper cassandraHelper, TransformerServerHealthIndicator healthIndicator) {
        return new TransformerServerContext(
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

    private final PublishWorkflowStager publishStager(TransformerContext ctx, FileStore fileStore, AWSCredentialsProvider credentials, HermesBlobAnnouncer hermesBlobAnnouncer) {
        Supplier<ServerUploadStatus> uploadStatus = () -> VMSServerUploadStatus.get();
        
        NetflixS3BlobPublisher blobPublisher = new NetflixS3BlobPublisher(credentials, "vms." + ctx.getConfig().getTransformerVip());
        NetflixS3BlobPublisher nostreamsBlobPublisher = new NetflixS3BlobPublisher(credentials, "vms." + ctx.getConfig().getTransformerVip() + "_nostreams");
        
        if(isFastlane(ctx.getConfig()))
            return new HollowFastlanePublishWorkflowStager(ctx, fileStore, blobPublisher, nostreamsBlobPublisher, hermesBlobAnnouncer, uploadStatus, ctx.getConfig().getTransformerVip());

        return new HollowPublishWorkflowStager(ctx, fileStore, blobPublisher, nostreamsBlobPublisher, hermesBlobAnnouncer, new DataSlicerImpl(), uploadStatus, ctx.getConfig().getTransformerVip());
    }

    private void restore(TransformCycle cycle, TransformerConfig cfg, FileStore fileStore, AWSCredentialsProvider credentials, HermesBlobAnnouncer hermesBlobAnnouncer) {
        if(cfg.isRestoreFromPreviousStateEngine()) {
            long latestVersion = hermesBlobAnnouncer.getLatestAnnouncedVersionFromCassandra(cfg.getTransformerVip());
            long restoreVersion = cfg.getRestoreFromSpecificVersion() != null ? cfg.getRestoreFromSpecificVersion() : latestVersion;
            
            if(restoreVersion != Long.MIN_VALUE) {
                NetflixS3BlobRetriever blobRetriever = new NetflixS3BlobRetriever(credentials, "vms." + cfg.getTransformerVip());
                NetflixS3BlobRetriever nostreamsBlobRetriever = new NetflixS3BlobRetriever(credentials, "vms." + cfg.getTransformerVip() + "_nostreams");
                VMSOutputDataClient outputClient = new VMSOutputDataClient(blobRetriever);
                VMSOutputDataClient nostreamsOutputClient = new VMSOutputDataClient(nostreamsBlobRetriever);
                outputClient.triggerRefreshTo(restoreVersion);
                nostreamsOutputClient.triggerRefreshTo(restoreVersion);

                if(outputClient.getCurrentVersionId() != restoreVersion || nostreamsOutputClient.getCurrentVersionId() != restoreVersion)
                    throw new IllegalStateException("Failed to restore from state: " + restoreVersion);
                cycle.restore(outputClient, nostreamsOutputClient);
            } else {
                if(cfg.isFailIfRestoreNotAvailable())
                    throw new IllegalStateException("Cannot restore from previous state -- previous state does not exist?  If this is expected (e.g. a new VIP), temporarily set vms.failIfRestoreNotAvailable=false");
            }
        }
    }

    private boolean shouldTryCompaction(TransformerConfig cfg) {
        return !isFastlane(cfg) && cfg.isCompactionEnabled();
    }

    private boolean isFastlane(TransformerConfig cfg) {
        return OverrideVipNameUtil.isOverrideVip(cfg);
    }

}
