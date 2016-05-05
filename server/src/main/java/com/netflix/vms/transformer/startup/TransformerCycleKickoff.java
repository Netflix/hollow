package com.netflix.vms.transformer.startup;

import com.google.inject.Inject;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformCycle;
import com.netflix.vms.transformer.servlet.platform.TransformerServerPlatformLibraries;
import netflix.admin.videometadata.VMSPublishWorkflowHistoryAdmin;

public class TransformerCycleKickoff {


    @Inject
    public TransformerCycleKickoff(TransformerServerPlatformLibraries platformLibs) {
        FileStore.useMultipartUploadWhenApplicable(true);

        TransformCycle cycle = new TransformCycle(
                                            platformLibs,
                                            (history) -> { VMSPublishWorkflowHistoryAdmin.history = history; },
                                            System.getProperty("vms.transformer.vip"));

        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    cycle.cycle();
                }
            }
        });

        t.setDaemon(true);
        t.setName("vms-transformer-cycler");
        t.start();

    }



}
