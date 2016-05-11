package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.InputDataVersionIds;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleBegin;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleSuccess;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WritingBlobsFailed;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WroteBlob;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutiveCycleFailures;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ProcessDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ReadInputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WriteOutputDataDuration;

import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.input.VMSInputDataLogMessage;
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
    private int consecutiveCycleFailures = 0;

    public TransformCycle(TransformerContext ctx, HollowPublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.transformerVip = transformerVip;
        this.inputClient = new VMSInputDataClient(ctx.platformLibraries().getFileStore(), converterVip);
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = ctx;
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new VersionMinter();
    }

    public void cycle() {
        beginCycle();

        outputStateEngine.prepareForNextCycle();

        updateTheInput();
        if(transformTheData()) {
            writeTheBlobFiles();
            submitToPublishWorkflow();
            endCycleSuccessfully();
        }

        ctx.getMetricRecorder().recordMetric(ConsecutiveCycleFailures, consecutiveCycleFailures);
    }

    private void beginCycle() {
        currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);

        ctx.getLogger().info(TransformCycleBegin, "Beginning cycle " + currentCycleNumber);
    }

    private void updateTheInput() {
        long startTime = System.currentTimeMillis();
        inputClient.triggerRefresh();
        long endTime = System.currentTimeMillis();

        ctx.getMetricRecorder().recordMetric(ReadInputDataDuration, endTime - startTime);

        VMSInputDataLogMessage message = new VMSInputDataLogMessage(inputClient.getStateEngine().getHeaderTags());
        ctx.getLogger().info(InputDataVersionIds, message);
    }

    private boolean transformTheData() {
        long startTime = System.currentTimeMillis();

        try {
            ctx.setNowMillis(System.currentTimeMillis());
            SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI)inputClient.getAPI(), outputStateEngine, ctx);
            transformer.transform();
        } catch(Throwable th) {
            ctx.getLogger().error(TransformCycleFailed, "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            consecutiveCycleFailures++;
            return false;
        } finally {
            long endTime = System.currentTimeMillis();
            ctx.getMetricRecorder().recordMetric(ProcessDataDuration, endTime - startTime);
        }

        return true;
    }


    private void writeTheBlobFiles() {
        long startTime = System.currentTimeMillis();

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
            ctx.getLogger().error(WritingBlobsFailed, "Writing blobs failed", e);
            consecutiveCycleFailures++;
            /// TODO: MUST reset to last prepare for next cycle.  We're already writing so that functionality needs to be added to netflix-hollow.
        }

        long endTime = System.currentTimeMillis();

        ctx.getMetricRecorder().recordMetric(WriteOutputDataDuration, endTime - startTime);
    }


    public void submitToPublishWorkflow() {
        publishWorkflowStager.triggerPublish(previousCycleNumber, currentCycleNumber);
    }

    private void endCycleSuccessfully() {
        ctx.getLogger().info(TransformCycleSuccess, "Cycle " + currentCycleNumber + " succeeded");
        previousCycleNumber = currentCycleNumber;
        consecutiveCycleFailures = 0;
    }

}
