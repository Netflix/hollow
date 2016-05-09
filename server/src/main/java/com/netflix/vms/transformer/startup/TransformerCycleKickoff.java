package com.netflix.vms.transformer.startup;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowConfig;

import com.netflix.vms.transformer.common.publish.workflow.PublicationHistoryConsumer;
import com.netflix.vms.transformer.TransformerServerContext;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.google.inject.Inject;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.servlet.platform.TransformerServerPlatformLibraries;
import netflix.admin.videometadata.VMSPublishWorkflowHistoryAdmin;

public class TransformerCycleKickoff {

    private static final long MIN_CYCLE_TIME = 10 * 60 * 1000;

    @Inject
    public TransformerCycleKickoff(TransformerServerPlatformLibraries platformLibs, TransformerConfig config) {
        FileStore.useMultipartUploadWhenApplicable(true);

        System.out.println("TRANSFORMER VIP: " + config.getTransformerVip());
        System.out.println("CONVERTER VIP: " + config.getConverterVip());

        TransformerContext ctx = ctx(platformLibs, (history) -> { VMSPublishWorkflowHistoryAdmin.history = history; });
        HollowPublishWorkflowStager publishStager = publishStager(ctx, config);

        TransformCycle cycle = new TransformCycle(
                                            ctx,
                                            publishStager,
                                            config.getConverterVip(),
                                            config.getTransformerVip());

        Thread t = new Thread(new Runnable() {
            private long previousCycleStartTime;

            @Override
            public void run() {
                while(true) {
                    waitForMinCycleTimeToPass();
                    cycle.cycle();
                }
            }

            private void waitForMinCycleTimeToPass() {
                long timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                long msUntilNextCycle = MIN_CYCLE_TIME - timeSinceLastCycle;

                ctx.getLogger().info("WaitForNextCycle", "Waiting " + msUntilNextCycle + "ms until beginning next cycle");

                while(msUntilNextCycle > 0) {
                    try {
                        Thread.sleep(msUntilNextCycle);
                    } catch (InterruptedException ignore) { }

                    timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                    msUntilNextCycle = MIN_CYCLE_TIME - timeSinceLastCycle;
                }

                previousCycleStartTime = System.currentTimeMillis();
            }
        });

        t.setDaemon(true);
        t.setName("vms-transformer-cycler");
        t.start();
    }

    private final TransformerContext ctx(TransformerServerPlatformLibraries platformLibs, PublicationHistoryConsumer historyConsumer) {
        return new TransformerServerContext(new TransformerServerLogger(),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "vms_poison_states", "poison_states"),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats"),
                new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "canary_validation", "canary_results"),
                new LZ4VMSTransformerFiles(),
                platformLibs,
                historyConsumer);
    }

    private final HollowPublishWorkflowStager publishStager(TransformerContext ctx, TransformerConfig cfg) {
        return new HollowPublishWorkflowStager(ctx, new PublishWorkflowConfig(), cfg.getTransformerVip());
    }

}
