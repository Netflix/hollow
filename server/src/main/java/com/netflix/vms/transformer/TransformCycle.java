package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.CycleFastlaneIds;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleBegin;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WritingBlobsFailed;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.WroteBlob;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ProcessDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ReadInputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WriteOutputDataDuration;

import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.FollowVipPin;
import com.netflix.vms.transformer.input.FollowVipPinExtractor;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.input.VMSInputDataVersionLogger;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.util.VersionMinter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class TransformCycle {
    private final String transformerVip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;
    private final TransformerOutputBlobHeaderPopulator headerPopulator;
    private final PublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;
    private final FollowVipPinExtractor followVipPinExtractor;
    
    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;

    public TransformCycle(TransformerContext ctx, FileStore fileStore, PublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.transformerVip = transformerVip;
        this.inputClient = new VMSInputDataClient(fileStore, converterVip);
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = ctx;
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new VersionMinter();
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
    }

    public void cycle() throws Throwable {
        try {
            beginCycle();
            outputStateEngine.prepareForNextCycle();
            updateTheInput();
            transformTheData();
            writeTheBlobFiles();
            submitToPublishWorkflow();
        } catch (Throwable th) {
            ctx.getLogger().error(TransformCycleFailed, "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            throw th;
        }
        endCycleSuccessfully();
    }

    private void beginCycle() {
        currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);

        if(ctx.getFastlaneIds() != null)
            ctx.getLogger().info(CycleFastlaneIds, ctx.getFastlaneIds());
        ctx.getLogger().info(TransformCycleBegin, "Beginning cycle " + currentCycleNumber);
    }
    
    private void updateTheInput() {
        long startTime = System.currentTimeMillis();
        
        FollowVipPin followVipPin = followVipPinExtractor.retrieveFollowVipPin(ctx);
        
        /// load the input data
        Long pinnedInputVersion = ctx.getConfig().getPinInputVersion();
        if(pinnedInputVersion == null && followVipPin != null)
            pinnedInputVersion = followVipPin.getInputVersionId();
        
        if(pinnedInputVersion == null)
            inputClient.triggerRefresh();
        else
            inputClient.triggerRefreshTo(pinnedInputVersion);

        //// set the now millis
        Long nowMillis = ctx.getConfig().getNowMillis();
        if(nowMillis == null && followVipPin != null)
            nowMillis = followVipPin.getNowMillis();
        if(nowMillis == null)
            nowMillis = System.currentTimeMillis();
        ctx.setNowMillis(nowMillis.longValue());

        long endTime = System.currentTimeMillis();

        ctx.getMetricRecorder().recordMetric(ReadInputDataDuration, endTime - startTime);
        ctx.getLogger().info(LogTag.InputDataConverterVersionId, inputClient.getCurrentVersionId());
        ctx.getLogger().info(LogTag.ProcessNowMillis, "Using transform timestamp of " + nowMillis + " (" + new Date(nowMillis).toString() + ")");

        VMSInputDataVersionLogger.logInputVersions(inputClient.getStateEngine().getHeaderTags(), ctx.getLogger());
    }

    private boolean transformTheData() throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI)inputClient.getAPI(), outputStateEngine, ctx);
            transformer.transform();
        } catch(Throwable th) {
            ctx.getLogger().error(TransformCycleFailed, "transform failed", th);
            throw th;
        } finally {
            long endTime = System.currentTimeMillis();
            ctx.getMetricRecorder().recordMetric(ProcessDataDuration, endTime - startTime);
        }

        return true;
    }

    private void writeTheBlobFiles() throws IOException {
    	if(rollbackFastlaneStateEngineIfUnchanged())
    		return;

    	headerPopulator.addHeaders(previousCycleNumber, currentCycleNumber);
    	
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
            throw e;
        }

        long endTime = System.currentTimeMillis();

        ctx.getMetricRecorder().recordMetric(WriteOutputDataDuration, endTime - startTime);
    }

    private boolean rollbackFastlaneStateEngineIfUnchanged() {
		if(ctx.getFastlaneIds() != null && previousCycleNumber != Long.MIN_VALUE && !outputStateEngine.hasChangedSinceLastCycle()) {
    		outputStateEngine.resetToLastPrepareForNextCycle();
    		ctx.getMetricRecorder().recordMetric(WriteOutputDataDuration, 0);
    		return true;
    	}
		return false;
	}


    public void submitToPublishWorkflow() {
        publishWorkflowStager.triggerPublish(inputClient.getCurrentVersionId(), previousCycleNumber, currentCycleNumber);
    }

    private void endCycleSuccessfully() {
        ctx.getMetricRecorder().incrementCounter(Metric.CycleSuccessCounter, 1);
        previousCycleNumber = currentCycleNumber;
    }

}
