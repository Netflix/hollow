package com.netflix.vms.transformer.startup;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WaitForNextCycle;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForNextCycleDuration;

import com.google.inject.Inject;
import com.netflix.archaius.api.Config;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.atlas.AtlasTransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.context.TransformerServerContext;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.fastlane.FastlaneIdRetriever;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.fastlane.HollowFastlanePublishWorkflowStager;
import com.netflix.vms.transformer.rest.VMSPublishWorkflowHistoryAdmin;
import com.netflix.vms.transformer.servlet.platform.TransformerServerPlatformLibraries;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;

public class TransformerCycleKickoff {

    private static final long MIN_CYCLE_TIME = 10 * 60 * 1000;

    @Inject
    public TransformerCycleKickoff(
    		TransformerServerPlatformLibraries platformLibs, 
    		ElasticSearchClient esClient, 
    		TransformerConfig transformerConfig,
    		Config config,
    		FastlaneIdRetriever fastlaneIdRetriever) {
    	
        FileStore.useMultipartUploadWhenApplicable(true);

        TransformerContext ctx = ctx(platformLibs, esClient, transformerConfig, config);
        PublishWorkflowStager publishStager = publishStager(ctx);

        TransformCycle cycle = new TransformCycle(
                                            ctx,
                                            publishStager,
                                            transformerConfig.getConverterVip(),
                                            transformerConfig.getTransformerVip());

        Thread t = new Thread(new Runnable() {
            private long previousCycleStartTime;

            @Override
            public void run() {
                while(true) {
                    try {
                    	waitForMinCycleTimeToPass();
                    	if(isFastlane(ctx.getConfig()))
                    		setUpFastlaneContext();
	                    cycle.cycle();
                    } catch(Throwable th) {
                    	ctx.getLogger().error(LogTag.UnexpectedError, "Unexpected error occurred", th);
                    }
                }
            }

            private void waitForMinCycleTimeToPass() {
            	if(isFastlane(ctx.getConfig()))
            		return;
            	
                long timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                long msUntilNextCycle = MIN_CYCLE_TIME - timeSinceLastCycle;

                if(msUntilNextCycle > 0) {
                    ctx.getLogger().info(WaitForNextCycle, "Waiting " + msUntilNextCycle + "ms until beginning next cycle");
                    ctx.getMetricRecorder().recordMetric(WaitForNextCycleDuration, msUntilNextCycle);
                }

                while(msUntilNextCycle > 0) {
                    try {
                        Thread.sleep(msUntilNextCycle);
                    } catch (InterruptedException ignore) { }

                    timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                    msUntilNextCycle = MIN_CYCLE_TIME - timeSinceLastCycle;
                }

                previousCycleStartTime = System.currentTimeMillis();
            }
            
            private void setUpFastlaneContext() {
    			ctx.setFastlaneIds(fastlaneIdRetriever.getFastlaneIds());
            }
        });

        t.setDaemon(true);
        t.setName("vms-transformer-cycler");
        t.start();
    }

    private final TransformerContext ctx(TransformerServerPlatformLibraries platformLibs, ElasticSearchClient esClient, TransformerConfig transformerConfig, Config config) {
        return new TransformerServerContext(
                new TransformerServerLogger(transformerConfig, esClient),
                config,
                new AtlasTransformerMetricRecorder(),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "vms_poison_states", "poison_states"),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats"),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "canary_validation", "canary_results"),
                new LZ4VMSTransformerFiles(),
                platformLibs,
                (history) -> { VMSPublishWorkflowHistoryAdmin.history = history; });
    }

    private final PublishWorkflowStager publishStager(TransformerContext ctx) {
    	if(isFastlane(ctx.getConfig()))
    		return new HollowFastlanePublishWorkflowStager(ctx, ctx.getConfig().getTransformerVip());
    	
        return new HollowPublishWorkflowStager(ctx, ctx.getConfig().getTransformerVip());
    }
    
    private boolean isFastlane(TransformerConfig cfg) {
    	return cfg.getTransformerVip().endsWith("_override");
    }

}
