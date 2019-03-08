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
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCyclePaused;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleResumed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformRestore;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WritingBlobsFailed;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.WroteBlob;

import com.google.gson.Gson;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.tools.compact.HollowCompactor;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import com.netflix.servo.monitor.Monitors;
import com.netflix.vms.logging.TaggingLogger.LogTag;
import com.netflix.vms.transformer.common.CycleMonkey;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.DurationMetric;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.common.VersionMinter;
import com.netflix.vms.transformer.common.config.TransformerConfig;
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
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesBlobAnnouncer;
import com.netflix.vms.transformer.publish.workflow.util.VipNameUtil;
import com.netflix.vms.transformer.util.SequenceVersionMinter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
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
    private final HermesBlobAnnouncer hermesBlobAnnouncer;
    private final String converterVip;
    private final PinTitleManager pinTitleMgr;
    private final TransformerTimeSinceLastPublishGauge timeSinceLastPublishGauge;
    private final CycleMonkey cycleMonkey;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;
    private boolean isFastlane = false;
    private boolean isNoStreamsBlobEnabled = true;
    private boolean isFirstCycle = true;
    private boolean isRestoreNeeded = true;

    private String previouslyResolvedConverterVip;

    private Map<String, String> currentStateHeader = Collections.emptyMap();
    private Map<String, String> previousStateHeader = Collections.emptyMap();

    public TransformCycle(TransformerContext ctx, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer, PublishWorkflowStager publishStager, String converterVip, String transformerVip) {
        this.ctx = ctx;
        this.versionMinter = new SequenceVersionMinter();
        currentCycleNumber = initCycleNumber(ctx, versionMinter); // Init first cycle here so logs can be grouped properly

        this.transformerVip = transformerVip;
        this.converterVip = converterVip;
        this.previouslyResolvedConverterVip = resolveConverterVip(ctx, converterVip);
        this.filestore = fileStore;
        this.hermesBlobAnnouncer = hermesBlobAnnouncer;
        this.inputClient = new VMSInputDataClient(fileStore, previouslyResolvedConverterVip);
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.fastlaneOutputStateEngine = new VMSTransformerWriteStateEngine();
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(inputClient, outputStateEngine, ctx);
        this.publishWorkflowStager = publishStager;
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
        this.pinTitleMgr = new PinTitleManager(fileStore, ctx);
        this.timeSinceLastPublishGauge = new TransformerTimeSinceLastPublishGauge();
        this.cycleMonkey = ctx.getCycleMonkey();

        this.isFastlane = VipNameUtil.isOverrideVip(ctx.getConfig());
        this.isNoStreamsBlobEnabled = isNoStreamsBlobEnabled(isFastlane);

        Monitors.registerObject(timeSinceLastPublishGauge);
    }

    private static boolean isNoStreamsBlobEnabled(boolean isFastlane) {
        return !isFastlane; // Fastlane does not deal with NoStreams blob
    }

    private void restore(VMSOutputDataClient restoreFrom, VMSOutputDataClient nostreamsRestoreFrom, boolean isNoStreamsBlobEnabled) {
        HollowReadStateEngine restoreStateEngine = restoreFrom.getStateEngine();
        HollowReadStateEngine restoreNoStreamStateEngine = isNoStreamsBlobEnabled ? nostreamsRestoreFrom.getStateEngine() : null;

        { // @TODO FIX: should restore headers as well
            outputStateEngine.addHeaderTags(restoreStateEngine.getHeaderTags());
            outputStateEngine.restoreFrom(restoreStateEngine);
        }
        ctx.getLogger().info(BlobState, "restore : input({}), output({}),)", BlobMetaDataUtil.fetchCoreHeaders(restoreStateEngine), BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
        previousCycleNumber = restoreFrom.getCurrentVersionId();

        publishWorkflowStager.notifyRestoredStateEngine(restoreStateEngine, restoreNoStreamStateEngine);
        setRestoreNeeded(false);
    }

    private void setRestoreNeeded(boolean isRestoreNeeded) {
        this.isRestoreNeeded = isRestoreNeeded;
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
            outputStateEngine.addHeaderTags(previousStateHeader);
            ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine), "Transformer failed cycle -- rolling back write state engine to previousState=({})", BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine), th);

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
                ctx.getLogger().info(Arrays.asList(BlobState, StateEngineCompaction), "Compacting State Engine");
                compactor.compact();
                writeTheBlobFiles();
                submitToPublishWorkflow();
                endCycleSuccessfully();
            } catch(Throwable th) {
                outputStateEngine.addHeaderTags(previousStateHeader);
                ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine), "Transformer failed cycle -- rolling back write state engine to previousState=({})", BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine), th);

                outputStateEngine.resetToLastPrepareForNextCycle();
                fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
                throw th;
            }
        }
    }

    private static long initCycleNumber(TransformerContext ctx, VersionMinter versionMinter) {
        long currentCycleNumber = versionMinter.mintANewVersion();
        ctx.setCurrentCycleId(currentCycleNumber);
        return currentCycleNumber;
    }

    private void checkPauseCycle() {
        boolean wasCyclePaused = false;
        while (ctx.getCycleInterrupter().isCyclePaused()) {
            if (!wasCyclePaused) ctx.getLogger().warn(TransformCyclePaused, "Paused cycle={}", currentCycleNumber);
            wasCyclePaused = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
        if (wasCyclePaused) ctx.getLogger().warn(Arrays.asList(TransformCyclePaused, TransformCycleResumed), "Resumed cycle={}", currentCycleNumber);
    }

    private void trackIds(TransformerLogTag tag, String format, Set<?> ids) {
        if (ids == null || ids.isEmpty()) return;

        ctx.getLogger().info(tag, format, ids);
    }

    private void beginCycle() {
        // First cycle already initialized in Constructor
        if (!isFirstCycle) currentCycleNumber = initCycleNumber(ctx, versionMinter);

        // Track cycle begin
        previousStateHeader = new HashMap<>(outputStateEngine.getHeaderTags());
        ctx.getLogger().info(BlobState, "beginCycle : cycle={}, isFastlane={}, isNoStreamsBlobEnabled={}, previousStateHeader({})", currentCycleNumber, isFastlane, isNoStreamsBlobEnabled, BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader));
        ctx.getLogger().info(TransformCycleBegin, "Beginning cycle={} jarVersion={}", currentCycleNumber, BlobMetaDataUtil.getJarVersion());

        // Check whether to Pause Cycle (before any logic)
        checkPauseCycle();

        // Init Context to begin cycle processing (e.g. Freeze Config)
        ctx.beginCycle();
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(), "Stopped at beginCycle");

        // track ids
        trackIds(CycleFastlaneIds, "Fastlane Ids={}", ctx.getFastlaneIds());
        trackIds(CyclePinnedTitles, "Config Spec={}", ctx.getPinTitleSpecs());

        // Spot to trigger Cycle Monkey if enabled
        cycleMonkey.doMonkeyBusiness("beginCycle");

        // Prepare state(s) for new cycle
        outputStateEngine.prepareForNextCycle();
        fastlaneOutputStateEngine.prepareForNextCycle();
        pinTitleMgr.prepareForNextCycle();
    }

    private static ExecuteFutureResult executeRestore(String name, TransformerContext ctx,
            SimultaneousExecutor executor, VMSOutputDataClient outputClient, long restoreVersion) {
        ExecuteFutureResult restoreResult = new ExecuteFutureResult(ctx, name);
        executor.execute(() -> {
            try {
                restoreResult.started();

                // Spot to trigger Cycle Monkey if enabled
                ctx.getCycleMonkey().doMonkeyBusiness(restoreResult.getName());

                long start = System.currentTimeMillis();
                outputClient.triggerRefreshTo(restoreVersion);

                ctx.getLogger().info(Arrays.asList(TransformRestore, BlobState), "Restored {} version={}, duration={}, header={}", name, restoreVersion, (System.currentTimeMillis() - start), BlobMetaDataUtil.fetchCoreHeaders(outputClient.getStateEngine()));
                restoreResult.completed();
            } catch (RuntimeException e) {
                ctx.getLogger().error(TransformRestore, "Failed to restore {} version={}", name, restoreVersion, e);
                restoreResult.failed(e);
            }
        });

        return restoreResult;
    }

    private static void restoreInParallel(SimultaneousExecutor executor, TransformerContext ctx, long restoreVersion, VMSOutputDataClient outputClient, VMSOutputDataClient nostreamsOutputClient, boolean isNoStreamsBlobEnabled) throws Exception {
        // Execute Restore in background
        ExecuteFutureResult restoreNormalBlobResult = executeRestore("restoreNormalBlob", ctx, executor, outputClient, restoreVersion);
        ExecuteFutureResult restoreNoStreamsBlobResult = isNoStreamsBlobEnabled ? executeRestore("restoreNoStreamsBlob", ctx, executor, nostreamsOutputClient, restoreVersion) : null;

        // Wait for them to complete and validate success
        executor.awaitSuccessfulCompletion();
        restoreNormalBlobResult.throwExceptionIfNotCompleteSuccessfully();
        if (isNoStreamsBlobEnabled) restoreNoStreamsBlobResult.throwExceptionIfNotCompleteSuccessfully();

        // Validate that both have restored to the specified version
        if (outputClient.getCurrentVersionId() != restoreVersion) throw new IllegalStateException("Failed to restore (with streams) to specified restoreVersion: " + restoreVersion + ",  currentVersion=" + outputClient.getCurrentVersionId());
        if (isNoStreamsBlobEnabled && nostreamsOutputClient.getCurrentVersionId() != restoreVersion) throw new IllegalStateException("Failed to restore (nostreams) to specified restoreVersion: " + restoreVersion + ",  currentVersion=" + nostreamsOutputClient.getCurrentVersionId());
    }

    public static void restore(SimultaneousExecutor executor, TransformerContext ctx, TransformCycle cycle, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer, boolean isFastlane, boolean isRunWithinUpdateInput) {
        // No need to track duration if RunWithinUpdateInput since it will be part of that duration
        if (!isRunWithinUpdateInput) ctx.getMetricRecorder().startTimer(DurationMetric.P0_RestoreDataDuration);
        try {
            TransformerConfig cfg = ctx.getConfig();
            if (cfg.isRestoreFromPreviousStateEngine()) {
                long latestVersion = hermesBlobAnnouncer.getLatestAnnouncedVersionFromCassandra(cfg.getTransformerVip());
                long restoreVersion = cfg.getRestoreFromSpecificVersion() != null ? cfg.getRestoreFromSpecificVersion() : latestVersion;

                if (restoreVersion != Long.MIN_VALUE) {
                    // Restore in parallel
                    VMSOutputDataClient outputClient = new VMSOutputDataClient(fileStore, cfg.getTransformerVip());
                    VMSOutputDataClient nostreamsOutputClient = new VMSOutputDataClient(fileStore, VipNameUtil.getNoStreamsVip(cfg.getTransformerVip()));

                    boolean isNoStreamsBlobEnabled = isNoStreamsBlobEnabled(isFastlane);
                    restoreInParallel(executor, ctx, restoreVersion, outputClient, nostreamsOutputClient, isNoStreamsBlobEnabled);

                    // Let TransformCycle complete the restore process
                    cycle.restore(outputClient, nostreamsOutputClient, isNoStreamsBlobEnabled);
                } else if (cfg.isFailIfRestoreNotAvailable()) {
                    // @TODO: Wonder if restoreVersion==Long.MIN_VALUE is sufficient to detect new namespace and if so, this exception is not needed
                    throw new IllegalStateException("Cannot restore from previous state -- previous state does not exist?  If this is expected (e.g. a new VIP), temporarily set vms.failIfRestoreNotAvailable=false");
                } else {
                    // Likely new Namespace
                    cycle.setRestoreNeeded(false);
                }
            }
        } catch (Exception ex) {
            ctx.getLogger().error(Arrays.asList(TransformRestore, BlobState), "Failed to restore data", ex);
            throw new IllegalStateException("Failed to restore data", ex);
        } finally {
            if (!isRunWithinUpdateInput) ctx.stopTimerAndLogDuration(DurationMetric.P0_RestoreDataDuration);
        }
    }

    private static ExecuteFutureResult executeLoadInput(SimultaneousExecutor executor, TransformerContext ctx, HollowClient inputClient, final Long pinnedInputVersion) {
        ExecuteFutureResult inputProcessingResult = new ExecuteFutureResult(ctx, "updateTheInput");
        executor.execute(() -> {
            try {
                inputProcessingResult.started();

                // Spot to trigger Cycle Monkey if enabled
                ctx.getCycleMonkey().doMonkeyBusiness(inputProcessingResult.getName());

                if (pinnedInputVersion == null)
                    inputClient.triggerRefresh();
                else {
                    inputClient.triggerRefreshTo(pinnedInputVersion);
                    if (inputClient.getCurrentVersionId() != pinnedInputVersion) throw new IllegalStateException("Failed to pin input to :" + pinnedInputVersion);
                }

                ctx.getLogger().info(BlobState, "Loaded input to version={}, header={}", inputClient.getCurrentVersionId(), inputClient.getStateEngine().getHeaderTags());
                inputProcessingResult.completed();
            } catch (Exception ex) {
                ctx.getLogger().error(BlobState, "Failed to Load Input", ex);
                inputProcessingResult.failed(ex);
            }
        });
        return inputProcessingResult;
    }

    private void updateTheInput() {
        ctx.getMetricRecorder().startTimer(P1_ReadInputDataDuration);
        try {
            SimultaneousExecutor executor = new SimultaneousExecutor(3, "vms-restore-and-input-processing");

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
            // Load Input on thread so it can process restore on first cycle if needed
            ExecuteFutureResult inputProcessingResult = executeLoadInput(executor, ctx, inputClient, pinnedInputVersion);

            // Determine whether to process restore here; only restore once
            if (isRestoreNeeded && ctx.getConfig().isProcessRestoreAndInputInParallel()) {
                restore(executor, ctx, this, filestore, hermesBlobAnnouncer, isFastlane, true);
            }

            // Wait to complete and validate success
            executor.awaitSuccessfulCompletion();
            inputProcessingResult.throwExceptionIfNotCompleteSuccessfully();

            //// set the now millis
            Long nowMillis = ctx.getConfig().getNowMillis();
            if(nowMillis == null && followVipPin != null)
                nowMillis = followVipPin.getNowMillis();
            if(nowMillis == null)
                nowMillis = System.currentTimeMillis();
            ctx.setNowMillis(nowMillis);

            ctx.getLogger().info(InputDataConverterVersionId, inputClient.getCurrentVersionId());
            ctx.getLogger().info(ProcessNowMillis, "Using transform timestamp of {} ({})", nowMillis, new Date(nowMillis));

            VMSInputDataVersionLogger.logInputVersions(inputClient.getStateEngine().getHeaderTags(), ctx.getLogger());
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState, "Failed to process Input", ex);
            throw new RuntimeException("Failed to process input", ex);
        } finally {
            ctx.stopTimerAndLogDuration(P1_ReadInputDataDuration);
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

                /**
                 * Combine input states
                 *
                 * When combining pinned and fastlane inputs, there are some interesting behaviors (good and bad) of note.
                 *
                 * For video-related types as listed by {@code OutputTypeConfig.VIDEO_RELATED_TYPES}, the pinned inputs take precedence over the fastlane input.
                 * For non-video-related types as defined by {@code OutputTypeConfig.NON_VIDEO_RELATED_TYPES}, the fastlane input takes precedence over pinned inputs.
                 *
                 * The good news is that this allows video-related data (for eg. PackageData) to be pinned to a certain version while allowing updates to
                 * non-video-related data (for eg. PersonImages that are shared by multiple videos) to be propagated to the output write state.
                 * The bad news is that this leads to confusion around whether data fixes should be propagated using pinning or the fast lane, and whether pinning
                 * a title to a previous version will fix a data error or not. To clear this confusion one would need to be cognizant of this combining behavior and
                 * classification of types as being video-related or non-video-related as defined in {@code OutputTypeConfig}.
                 *
                 * This behavior is a result the pursuit of a simple design for pinning. Non-video-related data such as CharacterImages is shared across multiple videos,
                 * so pinning non-video-related data for one title would affect other titles, and in order to pin multiple titles at different versions a lot of referenced
                 * data will also need to be pinned. Meanwhile, how to apply fastlane updates to pinned versions would be another issue.
                 *
                 * As a result, with the current pinning design, if a top-level title is added to both pinned and fastlane inputs, then the output write state will obtain
                 * video-related data from the pinned version, and non-video-related-data from the fastlane. Thus, when using pinning or fastlane to fix data errors, one
                 * needs to know if the erroneous attribute belongs to video-related or non-video-related data.
                 */

                //////////////////////
                /*
                 * Address issue related to removing a subset of pinned titles.
                 *
                 * Title pinning was designed to try to maintain the fastlane SLA since title pinning could take from a few minutes to ++ depending on how many titles and number of different versions
                 * they belong (# of title pinning jobs, job = per pin version).
                 * This means that fastlane cycle will not wait for title pinning job to complete (except for startup so that there is not gap during deployment).
                 * It was done that way because new title pinning requests could be done in the background and be combined with fastlane when ready on subsequent cycles.
                 * However, this introduces a bug where a subset of pinning titles are being removed form a pinned job because such change is considered a new title pin job.
                 * Fastlane cycle won't wait for that new job to complete; hence, the remaining pinned titles disappears from fastlane until the background job is completed in subsequent.
                 *
                 * Proposal:
                 * - Short term: Fastlane always wait for all title pinning job to complete to address the bug but it will impact fastlane SLA whenever a new title pinning requests appears at the
                 * beginning of fastlane cycle.
                 *
                 * - Long term: Better handle this use case with tracking prior cycle job and correlate with new job to detect this use case. This will be a bit more involved and will take more time
                 * to properly verify.
                 *
                 * NOTE: Waiting for all jobs to complete on every cycle instead of just firstCycle to address bug on moving subset of titles being pinned
                 * List<HollowReadStateEngine> overrideTitleOutputs = pinTitleMgr.getResults(isFirstCycle);
                 */
                List<HollowReadStateEngine> overrideTitleOutputs = pinTitleMgr.getResults(true);
                PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx, outputStateEngine, fastlaneOutputStateEngine, overrideTitleOutputs);
                combiner.combine();

                String overrideBlobID = PinTitleHelper.getBlobID(outputStateEngine);
                String pinnedTitles = PinTitleHelper.getPinnedTitles(outputStateEngine);
                ctx.getLogger().info(CyclePinnedTitles, "Pinned Titles=[{}]", pinnedTitles);
                ctx.getLogger().info(CyclePinnedTitles, "Processed blobId={}, pinnedTitles={}, hasDataChanged={}, fastlaneChanged={}, isFirstCycle={}, duration={}",
                        overrideBlobID, pinnedTitles, outputStateEngine.hasChangedSinceLastCycle(), fastlaneOutputStateEngine.hasChangedSinceLastCycle(), isFirstCycle, (System.currentTimeMillis() - startTime));
            } else {
                trasformInputData(inputClient.getAPI(), outputStateEngine, ctx);

                // Spot to trigger Cycle Monkey if enabled
                cycleMonkey.doMonkeyBusiness("transformTheData");
            }
        } catch(Throwable th) {
            ctx.getLogger().error(Arrays.asList(BlobState, TransformCycleFailed), "transform failed", th);
            throw th;
        } finally {
            ctx.stopTimerAndLogDuration(P2_ProcessDataDuration);
        }

        return true;
    }

    private static void trasformInputData(HollowAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine, TransformerContext ctx) throws Throwable {
        SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputAPI, outputStateEngine, ctx);
        transformer.transform();

        String BLOB_ID = VipNameUtil.isOverrideVip(ctx.getConfig()) ? "FASTLANE" : "BASEBLOB";
        PinTitleHelper.addBlobID(outputStateEngine, BLOB_ID);
    }

    private void writeTheBlobFiles() throws Exception {
        ctx.getMetricRecorder().startTimer(P3_WriteOutputDataDuration);

        Collection<LogTag> blobStateTags = Arrays.asList(WroteBlob, BlobState);
        try {
            currentStateHeader = new HashMap<>(headerPopulator.addHeaders(previousCycleNumber, currentCycleNumber));
            HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(transformerVip);
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            String snapshotFileName = fileNamer.getSnapshotFileName(currentCycleNumber);
            try (OutputStream snapshotOutputStream = ctx.files().newBlobOutputStream(new File(snapshotFileName))) {
                writer.writeSnapshot(snapshotOutputStream);
                ctx.getLogger().info(blobStateTags, "Wrote Snapshot to local file( {} ) - header( {} )", snapshotFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
            }

            // Spot to trigger Cycle Monkey if enabled
            cycleMonkey.doMonkeyBusiness("writeTheBlobFiles");

            if (isNoStreamsBlobEnabled) {
                String nostreamsSnapshotFileName = fileNamer.getNostreamsSnapshotFileName(currentCycleNumber);
                createNostreamsFilteredFile(snapshotFileName, nostreamsSnapshotFileName, true);
            }

            if(previousCycleNumber != Long.MIN_VALUE) {
                String deltaFileName = fileNamer.getDeltaFileName(previousCycleNumber, currentCycleNumber);
                try (OutputStream deltaOutputStream = ctx.files().newBlobOutputStream(new File(deltaFileName))) {
                    writer.writeDelta(deltaOutputStream);
                    ctx.getLogger().info(blobStateTags, "Wrote Delta to local file( {}) - header( {} )", deltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                }

                if (isNoStreamsBlobEnabled) {
                    String nostreamsDeltaFileName = fileNamer.getNostreamsDeltaFileName(previousCycleNumber, currentCycleNumber);
                    createNostreamsFilteredFile(deltaFileName, nostreamsDeltaFileName, false);
                }

                String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                outputStateEngine.addHeaderTags(previousStateHeader); // Make sure to have reverse delta's header point to prior state
                try (OutputStream reverseDeltaOutputStream = ctx.files().newBlobOutputStream(new File(reverseDeltaFileName))){
                    writer.writeReverseDelta(reverseDeltaOutputStream);
                    ctx.getLogger().info(blobStateTags, "Wrote Reverse Delta to local file( {} ) - header( {} )", reverseDeltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                }

                if (isNoStreamsBlobEnabled) {
                    String nostreamsReverseDeltaFileName = fileNamer.getNostreamsReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                    createNostreamsFilteredFile(reverseDeltaFileName, nostreamsReverseDeltaFileName, false);
                }
            }
        } catch (Exception e) {
            ctx.getLogger().error(Arrays.asList(BlobState, WritingBlobsFailed), "Writing blobs failed", e);
            throw e;
        } finally {
            ctx.stopTimerAndLogDuration(P3_WriteOutputDataDuration);
        }
    }

    private void createNostreamsFilteredFile(String unfilteredFilename, String filteredFilename, boolean isSnapshot) throws IOException {
        HollowFilterConfig filterConfig = getStreamsFilter("StreamData", "StreamDownloadLocationFilename", "SetOfStreamData", "FileEncodingData",
                "MapOfDownloadableIdToDrmInfo", "DrmHeader", "DrmKeyString", "ChunkDurationsString", "StreamDataDescriptor",
                "StreamAdditionalData", "DownloadLocationSet", "MapOfIntegerToDrmHeader", "DrmKey", "StreamDrmData",
                "WmDrmKey", "DrmInfo", "StreamMostlyConstantData", "ImageSubtitleIndexByteRange", "DrmInfoData", "QoEInfo",
                "DeploymentIntent", "CodecPrivateDataString");

        FilteredHollowBlobWriter writer = new FilteredHollowBlobWriter(filterConfig);

        Collection<LogTag> blobStateTags = Arrays.asList(WroteBlob, BlobState);
        try(InputStream is = ctx.files().newBlobInputStream(new File(unfilteredFilename));
                OutputStream os = ctx.files().newBlobOutputStream(new File(filteredFilename))) {
            writer.filter(!isSnapshot, is, os);
            ctx.getLogger().info(blobStateTags, "Wrote NostreamsFilteredFile({}) - header( {} )", filteredFilename, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
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
        outputStateEngine.addHeaderTags(previousStateHeader);
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

            // Spot to trigger Cycle Monkey if enabled
            cycleMonkey.doMonkeyBusiness("submitToPublishWorkflow");
        } finally {
            ctx.stopTimerAndLogDuration(P4_WaitForPublishWorkflowDuration);
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
        return !this.previouslyResolvedConverterVip.equals(resolveConverterVip(this.ctx, this.converterVip));
    }

    @SuppressWarnings("unchecked")
    private String resolveConverterVip(TransformerContext ctx, String converterVip) {

        // get the map of converterVip vs keybase
        String json = this.ctx.getConfig().getConverterVipToKeybaseMap();

        Map<String, String> map = new HashMap<String, String>();
        Gson gson = new Gson();
        map = gson.fromJson(json, map.getClass());

        // See if we have an entry for converterVip
        return map.containsKey(converterVip) ? map.get(converterVip) : converterVip;
    }

    public static class ExecuteFutureResult {
        public enum Status { IDLE, STARTED, COMPLETED, FAILED; }

        private final TransformerContext ctx;
        private final String name;
        private Status status;
        private Exception exception;

        ExecuteFutureResult(TransformerContext ctx, String name) {
            this.ctx = ctx;
            this.name = name;
            this.status = Status.IDLE;
        }

        public String getName() { return name; }
        public Status getStatus() { return status; }
        public Exception getException() { return exception; }

        public void started() {
            this.status = Status.STARTED;
        }
        public void completed() { completed(false, null); }
        public void failed(Exception ex) { completed(false, ex); }

        private void completed(boolean isFailed, Exception ex) {
            this.exception = ex;
            if (isFailed) {
                this.status = Status.FAILED;
                ctx.getLogger().error(TransformerLogTag.BlobState, "Execute {} failed", name, ex);
            } else {
                this.status = Status.COMPLETED;
                ctx.getLogger().info(TransformerLogTag.BlobState, "Execute {} completed successfully", name);
            }
        }

        public void throwExceptionIfNotCompleteSuccessfully() throws Exception {
            if (status == Status.COMPLETED) return;

            if (status == Status.FAILED) {
                throw new Exception(name + " failed", exception);
            }
            throw new Exception(name + " not completed. Current status=" + status);
        }
    }
}
