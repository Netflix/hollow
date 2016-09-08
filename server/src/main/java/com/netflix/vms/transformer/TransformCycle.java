package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ProcessDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ReadInputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForPublishWorkflowDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WriteOutputDataDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CycleFastlaneIds;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CyclePinnedTitles;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataConverterVersionId;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.ProcessNowMillis;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.RollbackStateEngine;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.StateEngineCompaction;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleBegin;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WritingBlobsFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WroteBlob;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.compact.HollowCompactor;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.common.VersionMinter;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.FollowVipPin;
import com.netflix.vms.transformer.input.FollowVipPinExtractor;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.input.VMSInputDataVersionLogger;
import com.netflix.vms.transformer.input.VMSOutputDataClient;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.override.PinTitleHollowCombiner;
import com.netflix.vms.transformer.override.PinTitleManager;
import com.netflix.vms.transformer.publish.status.CycleStatusFuture;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.job.impl.BlobMetaDataUtil;
import com.netflix.vms.transformer.util.OverrideVipNameUtil;
import com.netflix.vms.transformer.util.SequenceVersionMinter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TransformCycle {
    private final String transformerVip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final VMSTransformerWriteStateEngine fastlaneOutputStateEngine;
    private final TransformerContext ctx;
    private final TransformerOutputBlobHeaderPopulator headerPopulator;
    private final PublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;
    private final FollowVipPinExtractor followVipPinExtractor;
    private final PinTitleManager pinTitleMgr;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;
    private boolean isFastlane = false;
    private boolean isFirstCycle = true;

    public TransformCycle(TransformerContext ctx, FileStore fileStore, PublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.ctx = ctx;
        this.transformerVip = transformerVip;
        this.inputClient = new VMSInputDataClient(fileStore, converterVip);
        this.isFastlane = OverrideVipNameUtil.isOverrideVip(ctx.getConfig());
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.fastlaneOutputStateEngine = new VMSTransformerWriteStateEngine();
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new SequenceVersionMinter();
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
        this.pinTitleMgr = new PinTitleManager(fileStore, ctx);
    }

    public void restore(VMSOutputDataClient restoreFrom) {
        outputStateEngine.restoreFrom(restoreFrom.getStateEngine());
        publishWorkflowStager.notifyRestoredStateEngine(restoreFrom.getStateEngine());
        previousCycleNumber = restoreFrom.getCurrentVersionId();
    }

    public void cycle() throws Throwable {
        try {
            beginCycle();
            updateTheInput();
            transformTheData();
            if(isUnchangedFastlaneState()) {
                rollbackFastlaneStateEngine();
                incrementSuccessCounter();
            } else {
                writeTheBlobFiles();
                submitToPublishWorkflow();
                endCycleSuccessfully();
            }
        } catch (Throwable th) {
            ctx.getLogger().error(RollbackStateEngine, "Transformer failed cycle -- rolling back write state engine", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
            throw th;
        } finally {
            isFirstCycle = false;
        }
    }

    public void tryCompaction() throws Throwable {
        long compactionBytesThreshold = ctx.getConfig().getCompactionHoleByteThreshold();
        int compactionPercentThreshold = ctx.getConfig().getCompactionHolePercentThreshold();
        HollowCompactor compactor = new HollowCompactor(outputStateEngine, publishWorkflowStager.getCurrentReadStateEngine(), compactionBytesThreshold, compactionPercentThreshold);

        if(compactor.needsCompaction()) {
            try {
                beginCycle();
                ctx.getLogger().info(StateEngineCompaction, "Compacting State Engine");
                compactor.compact();
                writeTheBlobFiles();
                submitToPublishWorkflow();
                endCycleSuccessfully();
            } catch(Throwable th) {
                ctx.getLogger().error(RollbackStateEngine, "Transformer failed compaction cycle -- rolling back write state engine", th);
                outputStateEngine.resetToLastPrepareForNextCycle();
                fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
                throw th;
            }
        }
    }

    private void beginCycle() {
        currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);
        ctx.getOctoberSkyData().refresh();

        if(ctx.getFastlaneIds() != null)
            ctx.getLogger().info(CycleFastlaneIds, ctx.getFastlaneIds());

        if (ctx.getPinTitleSpecs() != null) {
            ctx.getLogger().info(CyclePinnedTitles, "Config Spec={}", ctx.getPinTitleSpecs());
        }

        ctx.getLogger().info(TransformCycleBegin, "Beginning cycle={} jarVersion={}", currentCycleNumber, BlobMetaDataUtil.getJarVersion());

        outputStateEngine.prepareForNextCycle();
        fastlaneOutputStateEngine.prepareForNextCycle();
        pinTitleMgr.prepareForNextCycle();
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
        ctx.getLogger().info(InputDataConverterVersionId, inputClient.getCurrentVersionId());
        ctx.getLogger().info(ProcessNowMillis, "Using transform timestamp of {} ({})", nowMillis, new Date(nowMillis));

        VMSInputDataVersionLogger.logInputVersions(inputClient.getStateEngine().getHeaderTags(), ctx.getLogger());
    }

    private boolean transformTheData() throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            if (isFastlane) {
                // Kick off processing of Title Override/Pinning and use blobs that is ready
                Set<String> pinnedTitleSpecs = ctx.getPinTitleSpecs();
                pinTitleMgr.submitJobsToProcessASync(pinnedTitleSpecs);

                // Process fastlane
                trasformInputData(inputClient.getAPI(), fastlaneOutputStateEngine, ctx);

                // Combine data
                List<HollowReadStateEngine> overrideTitleOutputs = pinTitleMgr.getResults(isFirstCycle);
                PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx, outputStateEngine, fastlaneOutputStateEngine, overrideTitleOutputs);
                String overrideBlobID = combiner.combine();

                ctx.getLogger().info(CyclePinnedTitles, "Processed cycleNumber={}, blobId={}, hasDataChanged={}, fastlaneChanged={}, isFirstCycle={}, duration={}",
                        currentCycleNumber, overrideBlobID, outputStateEngine.hasChangedSinceLastCycle(), fastlaneOutputStateEngine.hasChangedSinceLastCycle(), isFirstCycle, (System.currentTimeMillis() - startTime));
            } else {
                trasformInputData(inputClient.getAPI(), outputStateEngine, ctx);
            }
        } catch(Throwable th) {
            ctx.getLogger().error(TransformCycleFailed, "transform failed", th);
            throw th;
        } finally {
            long endTime = System.currentTimeMillis();
            ctx.getMetricRecorder().recordMetric(ProcessDataDuration, endTime - startTime);
        }

        return true;
    }

    private static void trasformInputData(HollowAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine, TransformerContext ctx) throws Throwable {
        SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputAPI, outputStateEngine, ctx);
        transformer.transform();

        String BLOB_ID = OverrideVipNameUtil.isOverrideVip(ctx.getConfig()) ? "FASTLANE" : "BASEBLOB";
        PinTitleHelper.addBlobID(outputStateEngine, BLOB_ID);
    }

    private void writeTheBlobFiles() throws IOException {
        headerPopulator.addHeaders(previousCycleNumber, currentCycleNumber);

        long startTime = System.currentTimeMillis();

        HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(transformerVip);

        try {
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            String snapshotFileName = fileNamer.getSnapshotFileName(currentCycleNumber);
            try (OutputStream snapshotOutputStream = ctx.files().newBlobOutputStream(new File(snapshotFileName))) {
                writer.writeSnapshot(snapshotOutputStream);
                ctx.getLogger().info(WroteBlob, "Wrote Snapshot to local file {}", snapshotFileName);
            }

            if(previousCycleNumber != Long.MIN_VALUE) {
                String deltaFileName = fileNamer.getDeltaFileName(previousCycleNumber, currentCycleNumber);
                try (OutputStream deltaOutputStream = ctx.files().newBlobOutputStream(new File(deltaFileName))) {
                    writer.writeDelta(deltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Delta to local file {}", deltaFileName);
                }

                String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                try (OutputStream reverseDeltaOutputStream = ctx.files().newBlobOutputStream(new File(reverseDeltaFileName))){
                    writer.writeReverseDelta(reverseDeltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Reverse Delta to local file {}", reverseDeltaFileName);
                }
            }
        } catch(IOException e) {
            ctx.getLogger().error(WritingBlobsFailed, "Writing blobs failed", e);
            throw e;
        }

        long endTime = System.currentTimeMillis();

        ctx.getMetricRecorder().recordMetric(WriteOutputDataDuration, endTime - startTime);
    }

    private boolean isUnchangedFastlaneState() {
        return ctx.getFastlaneIds() != null && previousCycleNumber != Long.MIN_VALUE && !outputStateEngine.isRestored() && !outputStateEngine.hasChangedSinceLastCycle();
    }

    private boolean rollbackFastlaneStateEngine() {
        outputStateEngine.resetToLastPrepareForNextCycle();
        fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
        ctx.getMetricRecorder().recordMetric(WriteOutputDataDuration, 0);
        return true;
    }

    public void submitToPublishWorkflow() {
        long startTime = System.currentTimeMillis();

        try {
            CycleStatusFuture future = publishWorkflowStager.triggerPublish(inputClient.getCurrentVersionId(), previousCycleNumber, currentCycleNumber);
            if(!future.awaitStatus())
                throw new RuntimeException("Publish Workflow Failed!");
        } finally {
            long endTime = System.currentTimeMillis();
            ctx.getMetricRecorder().recordMetric(WaitForPublishWorkflowDuration, endTime - startTime);
        }

    }

    private void endCycleSuccessfully() {
        incrementSuccessCounter();
        previousCycleNumber = currentCycleNumber;
    }

    private void incrementSuccessCounter() {
        ctx.getMetricRecorder().incrementCounter(Metric.CycleSuccessCounter, 1);
    }

}