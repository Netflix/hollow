package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ProcessDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ReadInputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WaitForPublishWorkflowDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.WriteOutputDataDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CycleFastlaneIds;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataConverterVersionId;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.ProcessNowMillis;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.RollbackStateEngine;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TitleOverride;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleBegin;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WritingBlobsFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WroteBlob;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowClient;
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
import com.netflix.vms.transformer.override.TitleOverrideHelper;
import com.netflix.vms.transformer.override.TitleOverrideHollowCombiner;
import com.netflix.vms.transformer.override.TitleOverrideManager;
import com.netflix.vms.transformer.publish.status.CycleStatusFuture;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.job.impl.BlobMetaDataUtil;
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
    private final TransformerContext ctx;
    private final TransformerOutputBlobHeaderPopulator headerPopulator;
    private final PublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;
    private final FollowVipPinExtractor followVipPinExtractor;
    private final FileStore fileStore;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;

    public TransformCycle(TransformerContext ctx, FileStore fileStore, PublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.transformerVip = transformerVip;
        this.inputClient = new VMSInputDataClient(fileStore, converterVip);
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = ctx;
        this.fileStore = fileStore;
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new SequenceVersionMinter();
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
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
            throw th;
        }
    }

    private void beginCycle() {
        currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);
        ctx.getOctoberSkyData().refresh();

        if(ctx.getFastlaneIds() != null)
            ctx.getLogger().info(CycleFastlaneIds, ctx.getFastlaneIds());

        if (ctx.getTitleOverrideSpecs() != null) {
            ctx.getLogger().info(TitleOverride, ctx.getTitleOverrideSpecs());
        }

        ctx.getLogger().info(TransformCycleBegin, "Beginning cycle={} jarVersion={}", currentCycleNumber, BlobMetaDataUtil.getJarVersion());

        outputStateEngine.prepareForNextCycle();
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
            Set<String> overrideTitleSpecs = ctx.getTitleOverrideSpecs();
            if (overrideTitleSpecs == null || overrideTitleSpecs.isEmpty()) {
                SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputClient.getAPI(), outputStateEngine, ctx);
                transformer.transform();
            } else {
                TitleOverrideManager mgr = new TitleOverrideManager(fileStore, ctx);
                mgr.processASync(overrideTitleSpecs);

                VMSTransformerWriteStateEngine fastlaneOutput = new VMSTransformerWriteStateEngine();
                SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputClient.getAPI(), fastlaneOutput, ctx);
                transformer.transform();
                TitleOverrideHelper.addBlobID(fastlaneOutput, "FastlaneIds_" + ctx.getFastlaneIds());

                List<HollowReadStateEngine> overrideTitleOutputs = mgr.waitForResults();
                TitleOverrideHollowCombiner combiner = new TitleOverrideHollowCombiner(ctx, outputStateEngine, fastlaneOutput, overrideTitleOutputs);
                combiner.combine();
                ctx.getLogger().info(TitleOverride, "Processed override titles={}", overrideTitleSpecs);
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
        return ctx.getFastlaneIds() != null && previousCycleNumber != Long.MIN_VALUE && !outputStateEngine.hasChangedSinceLastCycle();
    }

    private boolean rollbackFastlaneStateEngine() {
        outputStateEngine.resetToLastPrepareForNextCycle();
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