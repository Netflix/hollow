package com.netflix.vms.transformer;

import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistoryConsumer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.io.LZ4VMSOutputStream;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowConfig;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;
import com.netflix.vms.transformer.util.VersionMinter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

public class TransformCycle {
    private final String vip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;
    private final Supplier<Long> versionMinter;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;

    public TransformCycle(TransformerPlatformLibraries platformLibraries, PublicationHistoryConsumer historyConsumer, String vip) {
        this.vip = vip;
        this.inputClient = new VMSInputDataClient(platformLibraries.getFileStore());
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = new TransformerServerContext(new TransformerServerLogger(),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "vms_poison_states", "poison_states"),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats"),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "canary_validation", "canary_results"),
                new LZ4VMSTransformerFiles(),
                platformLibraries,
                historyConsumer);
        this.versionMinter = new VersionMinter();
    }

    public void cycle() {
        currentCycleNumber = versionMinter.get();

        updateTheInput();
        if(transformTheData()) {
            writeTheBlobFiles();
            submitToPublishWorkflow();
            previousCycleNumber = currentCycleNumber;
        }
    }

    private void updateTheInput() {
        inputClient.triggerRefresh();
    }

    private boolean transformTheData() {
        try {
            ctx.setNowMillis(System.currentTimeMillis());
            SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI)inputClient.getAPI(), outputStateEngine, ctx);
            transformer.transform();
        } catch(Throwable th) {
            ctx.getLogger().error("TransformCycleFailed", "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            return false;
        }

        return true;
    }


    private void writeTheBlobFiles() {
        HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(vip);

        try {
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            try (OutputStream snapshotOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), fileNamer.getSnapshotFileName(currentCycleNumber))))) {
                writer.writeSnapshot(snapshotOutputStream);
            }

            if(previousCycleNumber != Long.MIN_VALUE) {
                try (OutputStream deltaOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), fileNamer.getDeltaFileName(previousCycleNumber, currentCycleNumber))))) {
                    writer.writeDelta(deltaOutputStream);
                }

                try (OutputStream reverseDeltaOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber))))){
                    writer.writeDelta(reverseDeltaOutputStream);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            /// MUST restore from previous state, (or reset to last prepare for next cycle if possible).
        }
    }


    public void submitToPublishWorkflow() {
        HollowPublishWorkflowStager stager = new HollowPublishWorkflowStager(ctx, new PublishWorkflowConfig(), vip);
        stager.triggerPublish(previousCycleNumber, currentCycleNumber);
    }

}
