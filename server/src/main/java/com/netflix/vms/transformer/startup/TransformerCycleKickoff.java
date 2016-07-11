package com.netflix.vms.transformer.startup;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.*;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutiveCycleFailures;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForNextCycleDuration;

import com.google.inject.Inject;
import com.netflix.archaius.api.Config;
import com.netflix.aws.file.FileStore;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.hermes.publisher.FastPropertyPublisher;
import com.netflix.hermes.subscriber.SubscriptionManager;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.atlas.AtlasTransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.context.TransformerServerContext;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.fastlane.FastlaneIdRetriever;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.fastlane.HollowFastlanePublishWorkflowStager;
import com.netflix.vms.transformer.rest.VMSPublishWorkflowHistoryAdmin;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerUploadStatus;

public class TransformerCycleKickoff {

    @Inject
    public TransformerCycleKickoff(
            ElasticSearchClient esClient,
            NFAstyanaxManager astyanax,
            SubscriptionManager hermesSubscriber,
            FastPropertyPublisher hermesPublisher,
            FileStore fileStore,
            TransformerConfig transformerConfig,
            Config config,
            OctoberSkyData octoberSkyData,
            FastlaneIdRetriever fastlaneIdRetriever, 
            TransformerServerHealthIndicator healthIndicator) {

        FileStore.useMultipartUploadWhenApplicable(true);

        TransformerContext ctx = ctx(astyanax, esClient, transformerConfig, config, octoberSkyData, healthIndicator);
        PublishWorkflowStager publishStager = publishStager(ctx, hermesSubscriber, hermesPublisher, fileStore);

        TransformCycle cycle = new TransformCycle(
                                            ctx,
                                            fileStore,
                                            publishStager,
                                            transformerConfig.getConverterVip(),
                                            transformerConfig.getTransformerVip());

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

    private final TransformerContext ctx(NFAstyanaxManager astyanax, ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config, OctoberSkyData octoberSkyData, TransformerServerHealthIndicator healthIndicator) {
        return new TransformerServerContext(
                new TransformerServerLogger(transformerConfig, esClient),
                config,
                octoberSkyData,
                new AtlasTransformerMetricRecorder(),
                new TransformerServerCassandraHelper(astyanax, "cass_dpt", "vms_poison_states", "poison_states"),
                new TransformerServerCassandraHelper(astyanax, "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats"),
                new TransformerServerCassandraHelper(astyanax, "cass_dpt", "canary_validation", "canary_results"),
                new LZ4VMSTransformerFiles(),
                (history) -> {
                    VMSPublishWorkflowHistoryAdmin.history = history;
                });
    }

    private final PublishWorkflowStager publishStager(TransformerContext ctx, SubscriptionManager hermesSubscriber,
            FastPropertyPublisher hermesPublisher, FileStore fileStore) {
        Supplier<ServerUploadStatus> uploadStatus = () -> VMSServerUploadStatus.get();
        if(isFastlane(ctx.getConfig()))
            return new HollowFastlanePublishWorkflowStager(ctx, hermesSubscriber, hermesPublisher, fileStore, uploadStatus, ctx.getConfig().getTransformerVip());

        return new HollowPublishWorkflowStager(ctx, hermesSubscriber, hermesPublisher, fileStore, uploadStatus, ctx.getConfig().getTransformerVip());
    }
    
    private boolean isFastlane(TransformerConfig cfg) {
    	return cfg.getTransformerVip().endsWith("_override");
    }

}
