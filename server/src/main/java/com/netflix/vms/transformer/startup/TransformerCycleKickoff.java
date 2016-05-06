package com.netflix.vms.transformer.startup;

import com.netflix.vms.transformer.config.VideometadataTransformerConfig;

import com.google.inject.Inject;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.servlet.platform.TransformerServerPlatformLibraries;
import netflix.admin.videometadata.VMSPublishWorkflowHistoryAdmin;

public class TransformerCycleKickoff {

    private static final long MIN_CYCLE_TIME = 10 * 60 * 1000;

    @Inject
    public TransformerCycleKickoff(TransformerServerPlatformLibraries platformLibs, VideometadataTransformerConfig config) {
        FileStore.useMultipartUploadWhenApplicable(true);

        System.out.println("TRANSFORMER VIP: " + config.getTransformerVip());
        System.out.println("CONVERTER VIP: " + config.getConverterVip());

        TransformCycle cycle = new TransformCycle(
                                            platformLibs,
                                            (history) -> { VMSPublishWorkflowHistoryAdmin.history = history; },
                                            config.getConverterVip(),
                                            config.getTransformerVip());

        Thread t = new Thread(new Runnable() {
            private long previousCycleStartTime;

            @Override
            public void run() {
                while(true) {
                    waitForMinCycleTimeToPass();
                    previousCycleStartTime = System.currentTimeMillis();
                    cycle.cycle();
                }
            }

            private void waitForMinCycleTimeToPass() {
                long timeSinceLastCycle = System.currentTimeMillis() - previousCycleStartTime;
                while(timeSinceLastCycle < MIN_CYCLE_TIME) {
                    try {
                        Thread.sleep(MIN_CYCLE_TIME - timeSinceLastCycle);
                    } catch (InterruptedException ignore) { }
                }

                previousCycleStartTime = Long.MIN_VALUE;
            }
        });

        t.setDaemon(true);
        t.setName("vms-transformer-cycler");
        t.start();
    }

}
