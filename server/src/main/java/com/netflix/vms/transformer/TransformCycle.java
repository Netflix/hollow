package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WroteBlob;

import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.HollowPublishWorkflowStager;
import com.netflix.vms.transformer.util.VersionMinter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class TransformCycle {
    private final String transformerVip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;
    private final HollowPublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;

    public TransformCycle(TransformerContext ctx, HollowPublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.transformerVip = transformerVip;
        this.inputClient = new VMSInputDataClient(ctx.platformLibraries().getFileStore(), converterVip);
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = ctx;
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new VersionMinter();
    }

    public void cycle() {
        currentCycleNumber = versionMinter.get();
        ctx.setCurrentCycleId(currentCycleNumber);

        outputStateEngine.prepareForNextCycle();

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
            ctx.getLogger().error(TransformCycleFailed, "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            return false;
        }

        return true;
    }


    private void writeTheBlobFiles() {
        HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(transformerVip);

        try {
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            String snapshotFileName = fileNamer.getSnapshotFileName(currentCycleNumber);
            try (OutputStream snapshotOutputStream = ctx.files().newBlobOutputStream(new File(snapshotFileName))) {
                writer.writeSnapshot(snapshotOutputStream);
                ctx.getLogger().info(WroteBlob, "Wrote Snapshot to local file " + snapshotFileName);
            }

            if(previousCycleNumber != Long.MIN_VALUE) {
                String deltaFileName = fileNamer.getDeltaFileName(previousCycleNumber, currentCycleNumber);
                try (OutputStream deltaOutputStream = ctx.files().newBlobOutputStream(new File(deltaFileName))) {
                    writer.writeDelta(deltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Delta to local file " + deltaFileName);
                }

                String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                try (OutputStream reverseDeltaOutputStream = ctx.files().newBlobOutputStream(new File(reverseDeltaFileName))){
                    writer.writeReverseDelta(reverseDeltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Reverse Delta to local file " + reverseDeltaFileName);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            /// MUST restore from previous state, (or reset to last prepare for next cycle if possible).
        }
    }


    public void submitToPublishWorkflow() {
        publishWorkflowStager.triggerPublish(previousCycleNumber, currentCycleNumber);
    }

}
