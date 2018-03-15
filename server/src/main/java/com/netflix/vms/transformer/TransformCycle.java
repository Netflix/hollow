package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric.P1_ReadInputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric.P2_ProcessDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric.P3_WriteOutputDataDuration;
import static com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric.P4_WaitForPublishWorkflowDuration;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobState;
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

import com.google.gson.Gson;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.tools.compact.HollowCompactor;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import com.netflix.servo.monitor.Monitors;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.common.VersionMinter;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.health.TransformerTimeSinceLastPublishGauge;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransformCycle {
    private final String transformerVip;
    private HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final VMSTransformerWriteStateEngine fastlaneOutputStateEngine;
    private final TransformerContext ctx;
    private TransformerOutputBlobHeaderPopulator headerPopulator;
    private final PublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;
    private final FollowVipPinExtractor followVipPinExtractor;
    private final FileStore filestore;
    private final String converterVip;
    private final PinTitleManager pinTitleMgr;
    private final TransformerTimeSinceLastPublishGauge timeSinceLastPublishGauge;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;
    private boolean isFastlane = false;
    private boolean isFirstCycle = true;

    private String previouslyResolvedConverterVip;

    private Map<String, String> currentStateHeader = Collections.emptyMap();
    private Map<String, String> previousStateHeader = Collections.emptyMap();

    public TransformCycle(TransformerContext ctx, FileStore fileStore, PublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.ctx = ctx;
        this.transformerVip = transformerVip;
        this.converterVip = converterVip;
        this.previouslyResolvedConverterVip = resolveConverterVip(ctx, converterVip);
        this.filestore = fileStore;
        this.inputClient = new VMSInputDataClient(fileStore, previouslyResolvedConverterVip);
        this.isFastlane = OverrideVipNameUtil.isOverrideVip(ctx.getConfig());
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.fastlaneOutputStateEngine = new VMSTransformerWriteStateEngine();
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
        this.publishWorkflowStager = publishStager;
        this.versionMinter = new SequenceVersionMinter();
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
        this.pinTitleMgr = new PinTitleManager(fileStore, ctx);
        this.timeSinceLastPublishGauge = new TransformerTimeSinceLastPublishGauge();
        Monitors.registerObject(timeSinceLastPublishGauge);
    }

    public void restore(VMSOutputDataClient restoreFrom, VMSOutputDataClient nostreamsRestoreFrom, boolean isFastlane) {
        outputStateEngine.restoreFrom(restoreFrom.getStateEngine());
        if(!isFastlane)
            publishWorkflowStager.notifyRestoredStateEngine(restoreFrom.getStateEngine(), nostreamsRestoreFrom.getStateEngine());
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
            ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine), "Transformer failed cycle -- rolling back write state engine to previousState=({})", BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader), th);
            outputStateEngine.addHeaderTags(previousStateHeader);
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
                ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine), "Transformer failed cycle -- rolling back write state engine to previousState=({})", BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader), th);
                outputStateEngine.addHeaderTags(previousStateHeader);
                outputStateEngine.resetToLastPrepareForNextCycle();
                fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
                throw th;
            }
        }
    }

    private void beginCycle() {
        previousStateHeader = new HashMap<>(outputStateEngine.getHeaderTags());

        currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);
        ctx.getCycleInterrupter().begin(currentCycleNumber);
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
        ctx.getMetricRecorder().startTimer(P1_ReadInputDataDuration);
        try {
            FollowVipPin followVipPin = followVipPinExtractor.retrieveFollowVipPin(ctx);

            /// load the input data
            Long pinnedInputVersion = ctx.getConfig().getPinInputVersion();
            if(pinnedInputVersion == null && followVipPin != null)
                pinnedInputVersion = followVipPin.getInputVersionId();

            // If the converter vip has changed we need to re-initialize the client
            if(hasConverterVipChanged()) {
                inputClient = new VMSInputDataClient(filestore, resolveConverterVip(ctx, converterVip));
                this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
                previouslyResolvedConverterVip = resolveConverterVip(ctx, converterVip);
            }

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

            ctx.getLogger().info(InputDataConverterVersionId, inputClient.getCurrentVersionId());
            ctx.getLogger().info(ProcessNowMillis, "Using transform timestamp of {} ({})", nowMillis, new Date(nowMillis));

            VMSInputDataVersionLogger.logInputVersions(inputClient.getStateEngine().getHeaderTags(), ctx.getLogger());
        } finally {
            ctx.getMetricRecorder().stopTimer(P1_ReadInputDataDuration);
        }
    }

    private boolean transformTheData() throws Throwable {
        long startTime = System.currentTimeMillis();
        ctx.getMetricRecorder().startTimer(P2_ProcessDataDuration);
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
                combiner.combine();

                String overrideBlobID = PinTitleHelper.getBlobID(outputStateEngine);
                String pinnedTitles = PinTitleHelper.getPinnedTitles(outputStateEngine);
                ctx.getLogger().info(CyclePinnedTitles, "Pinned Titles=[{}]", pinnedTitles);
                ctx.getLogger().info(CyclePinnedTitles, "Processed blobId={}, pinnedTitles={}, hasDataChanged={}, fastlaneChanged={}, isFirstCycle={}, duration={}",
                        overrideBlobID, pinnedTitles, outputStateEngine.hasChangedSinceLastCycle(), fastlaneOutputStateEngine.hasChangedSinceLastCycle(), isFirstCycle, (System.currentTimeMillis() - startTime));
            } else {
                trasformInputData(inputClient.getAPI(), outputStateEngine, ctx);
            }
        } catch(Throwable th) {
            ctx.getLogger().error(TransformCycleFailed, "transform failed", th);
            throw th;
        } finally {
            ctx.getMetricRecorder().stopTimer(P2_ProcessDataDuration);
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
        ctx.getMetricRecorder().startTimer(P3_WriteOutputDataDuration);

        try {
            currentStateHeader = headerPopulator.addHeaders(previousCycleNumber, currentCycleNumber);
            HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(transformerVip);
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            String snapshotFileName = fileNamer.getSnapshotFileName(currentCycleNumber);
            ctx.getLogger().info(BlobState, "writeTheBlobFiles snapshotFileName({}={})", snapshotFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
            try (OutputStream snapshotOutputStream = ctx.files().newBlobOutputStream(new File(snapshotFileName))) {
                writer.writeSnapshot(snapshotOutputStream);
                ctx.getLogger().info(WroteBlob, "Wrote Snapshot to local file {}", snapshotFileName);
            }

            String nostreamsSnapshotFileName = fileNamer.getNostreamsSnapshotFileName(currentCycleNumber);
            createNostreamsFilteredFile(snapshotFileName, nostreamsSnapshotFileName, true);

            if(previousCycleNumber != Long.MIN_VALUE) {
                String deltaFileName = fileNamer.getDeltaFileName(previousCycleNumber, currentCycleNumber);
                ctx.getLogger().info(BlobState, "writeTheBlobFiles deltaFileName({}={})", deltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                try (OutputStream deltaOutputStream = ctx.files().newBlobOutputStream(new File(deltaFileName))) {
                    writer.writeDelta(deltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Delta to local file {}", deltaFileName);
                }

                String nostreamsDeltaFileName = fileNamer.getNostreamsDeltaFileName(previousCycleNumber, currentCycleNumber);
                createNostreamsFilteredFile(deltaFileName, nostreamsDeltaFileName, false);

                String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                outputStateEngine.addHeaderTags(previousStateHeader); // Make sure to have reverse delta's header point to prior state
                ctx.getLogger().info(BlobState, "writeTheBlobFiles reverseDeltaFileName({}={})", reverseDeltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                try (OutputStream reverseDeltaOutputStream = ctx.files().newBlobOutputStream(new File(reverseDeltaFileName))){
                    writer.writeReverseDelta(reverseDeltaOutputStream);
                    ctx.getLogger().info(WroteBlob, "Wrote Reverse Delta to local file {}", reverseDeltaFileName);
                }

                String nostreamsReverseDeltaFileName = fileNamer.getNostreamsReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                createNostreamsFilteredFile(reverseDeltaFileName, nostreamsReverseDeltaFileName, false);
            }
        } catch(IOException e) {
            ctx.getLogger().error(WritingBlobsFailed, "Writing blobs failed", e);
            throw e;
        } finally {
            ctx.getMetricRecorder().stopTimer(P3_WriteOutputDataDuration);
        }
    }

    private void createNostreamsFilteredFile(String unfilteredFilename, String filteredFilename, boolean isSnapshot) throws IOException {
        HollowFilterConfig filterConfig = getStreamsFilter("StreamData", "StreamDownloadLocationFilename", "SetOfStreamData", "FileEncodingData",
                "MapOfDownloadableIdToDrmInfo", "DrmHeader", "DrmKeyString", "ChunkDurationsString", "StreamDataDescriptor",
                "StreamAdditionalData", "DownloadLocationSet", "MapOfIntegerToDrmHeader", "DrmKey", "StreamDrmData",
                "WmDrmKey", "DrmInfo", "StreamMostlyConstantData", "ImageSubtitleIndexByteRange", "DrmInfoData", "QoEInfo",
                "DeploymentIntent", "CodecPrivateDataString");

        FilteredHollowBlobWriter writer = new FilteredHollowBlobWriter(filterConfig);

        try(InputStream is = ctx.files().newBlobInputStream(new File(unfilteredFilename));
                OutputStream os = ctx.files().newBlobOutputStream(new File(filteredFilename))) {
            writer.filter(!isSnapshot, is, os);
            ctx.getLogger().info(WroteBlob, "Wrote NostreamsFilteredFile={}", filteredFilename);
        }
    }

    private HollowFilterConfig getStreamsFilter(String... streamTypes) {
        HollowFilterConfig filterConfig = new HollowFilterConfig(true);

        for(String type : streamTypes) {
            filterConfig.addType(type);
        }

        return filterConfig;
    }

    private boolean isUnchangedFastlaneState() {
        return ctx.getFastlaneIds() != null && previousCycleNumber != Long.MIN_VALUE && !outputStateEngine.isRestored() && !outputStateEngine.hasChangedSinceLastCycle();
    }

    private boolean rollbackFastlaneStateEngine() {
        outputStateEngine.resetToLastPrepareForNextCycle();
        fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
        ctx.getLogger().info(TransformerLogTag.HideCycleFromDashboard, "Fastlane data was unchanged -- rolling back and trying again");
        return true;
    }

    public void submitToPublishWorkflow() {
        // Check to determine whether to abort cycle due to interrupt
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(), "Stopped at submitToPublishWorkflow");

        ctx.getMetricRecorder().startTimer(P4_WaitForPublishWorkflowDuration);
        try {
            CycleStatusFuture future = publishWorkflowStager.triggerPublish(inputClient.getCurrentVersionId(), previousCycleNumber, currentCycleNumber);
            if(!future.awaitStatus())
                throw new RuntimeException("Publish Workflow Failed!");
        } finally {
            ctx.getMetricRecorder().stopTimer(P4_WaitForPublishWorkflowDuration);
        }
    }

    private void endCycleSuccessfully() {
        incrementSuccessCounter();
        timeSinceLastPublishGauge.notifyPublishSuccess();
        previousCycleNumber = currentCycleNumber;

        // On success, make sure outputStateEngine has current state header
        outputStateEngine.addHeaderTags(currentStateHeader);
        ctx.getLogger().info(BlobState, "endCycleSuccessfully write state : before({}), after({}),)", BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader), BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
    }

    private void incrementSuccessCounter() {
        ctx.getMetricRecorder().incrementCounter(Metric.CycleSuccessCounter, 1);
    }

    private boolean hasConverterVipChanged() {
        if(!this.previouslyResolvedConverterVip.equals(resolveConverterVip(this.ctx, this.converterVip))) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private String resolveConverterVip(TransformerContext ctx, String converterVip) {

        // get the map of converterVip vs keybase
        String json = this.ctx.getConfig().getConverterVipToKeybaseMap();

        Map<String, String> map = new HashMap<String, String>();
        Gson gson = new Gson();
        map = gson.fromJson(json, map.getClass());

        // See if we have an entry for converterVip
        if(map.containsKey(converterVip))
            return map.get(converterVip);
        else
            return converterVip;
    }

}
