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
import static java.util.concurrent.TimeUnit.SECONDS;

import com.netflix.aws.file.FileStore;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.IntegrityCheckListener;
import com.netflix.hollow.api.producer.listener.PopulateListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.hollow.api.producer.listener.VetoableListener;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.combine.HollowCombiner;
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
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class TransformCycle {
    private final String transformerVip;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final VMSTransformerWriteStateEngine fastlaneOutputStateEngine;
    private final TransformerContext ctx;
    private final TransformerOutputBlobHeaderPopulator headerPopulator;
    private final PublishWorkflowStager publishWorkflowStager;
    private final VersionMinter versionMinter;
    private final FollowVipPinExtractor followVipPinExtractor;
    private final FileStore filestore;
    private final HermesBlobAnnouncer hermesBlobAnnouncer;
    private final PinTitleManager pinTitleMgr;
    private final TransformerTimeSinceLastPublishGauge timeSinceLastPublishGauge;
    private final CycleMonkey cycleMonkey;
    private HollowConsumer inputConsumer;

    private long previousCycleNumber = Long.MIN_VALUE;
    private long currentCycleNumber = Long.MIN_VALUE;
    private final boolean isFastlane;
    private final boolean isNoStreamsBlobEnabled;
    private boolean isFirstCycle = true;
    private boolean isRestoreNeeded = true;

    private Map<String, String> currentStateHeader = Collections.emptyMap();
    private Map<String, String> previousStateHeader = Collections.emptyMap();

    private final HollowProducer producer;
    private final ExecutorService produceExecutor;

    public TransformCycle(TransformerContext ctx, HollowConsumer inputConsumer,
            FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer, PublishWorkflowStager publishStager,
            String transformerVip,
            Supplier<CinderProducerBuilder> cinderBuilder) {
        this.ctx = ctx;
        this.versionMinter = new SequenceVersionMinter();
        currentCycleNumber = initCycleNumber(ctx, versionMinter); // Init first cycle here so logs can be grouped properly

        this.transformerVip = transformerVip;
        this.filestore = fileStore;
        this.hermesBlobAnnouncer = hermesBlobAnnouncer;
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.fastlaneOutputStateEngine = new VMSTransformerWriteStateEngine();
        this.headerPopulator = new TransformerOutputBlobHeaderPopulator(ctx);
        this.publishWorkflowStager = publishStager;
        this.followVipPinExtractor = new FollowVipPinExtractor(fileStore);
        this.pinTitleMgr = new PinTitleManager(fileStore, ctx);
        this.timeSinceLastPublishGauge = new TransformerTimeSinceLastPublishGauge();
        this.cycleMonkey = ctx.getCycleMonkey();

        this.isFastlane = VipNameUtil.isOverrideVip(ctx.getConfig());
        this.isNoStreamsBlobEnabled = isNoStreamsBlobEnabled(isFastlane);

        this.inputConsumer = inputConsumer;

        Monitors.registerObject(timeSinceLastPublishGauge);

        /*
         * Use HollowProducer for "feather" and "feather_nostreams" if enabled and not configured
         * for the fast lane.
         * TODO:
         * - Announcements are not staggered, see HermesAnnounceListener
         *   Possible use CompletableFuture.supplyAsync(supplier, delayedExecutor(timeout, timeUnit))
         *   rather than an explicit queue + thread pool.
         *
         * - Canary/Playback monkey not yet tested
         *
         * - The VMS dashboard mostly works but is not reporting published blobs and announcements
         *
         * - Likely not possible to interrupt a cycle
         *   See usages of the method TransformerCycleInterrupter.triggerInterruptIfNeeded
         *
         * - No integration with the cycle monkey (see also listener veto support)
         *   See usages of the method CycleMonkey.doMonkeyBusiness
         *
         * - If the time between an announce and a new cycle is very small there is a chance
         *   the new cycle will obtain the prior announce version from Gutenberg and therefore
         *   restore backwards (as if pinned).  There are some consistency issues with
         *   Gutenberg, where it takes a few seconds to reach consistency.
         *
         * - There may be rare cases where the main producer cycle results in changes but the nostreams
         *   producer does not.  In that case the nostreams cycle will not publish and announce
         *   anything.  Presumably if such cases have previously occurred then delta and reverse delta
         *   blobs would be published containing no changes.
         *
         * - Remove the use of VMSTransformerHashCodeFinder when initializing the producers
         *   This may require a new VIP as there are issues processing the nostreams data,
         *   specifically a map with a hash key whose key type is not present.  This likely
         *   makes it difficult to transition an existing VIP to use HollowProducer while
         *   preserving the delta chain
         */
        if (ctx.getConfig().isCinderEnabled() && !isFastlane) {
            LongSupplier cinderPreviousVersion = () -> previousCycleNumber;
            LongSupplier cinderVersion = () -> currentCycleNumber;
            AtomicLong noStreamsVersion = new AtomicLong(Long.MIN_VALUE);

            this.produceExecutor = Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "vmstransformer-producer");
                // Not necessary if constructing thread is a daemon thread
                t.setDaemon(true);
                return t;
            });
            String cinderVip = ctx.getConfig().getTransformerVip();
            String cinderOutputNamespace = "vms-" + cinderVip;
            CinderProducerBuilder producerBuilder = cinderBuilder.get()
                    .withSnapshotPublishExecutor(produceExecutor)
                    .forNamespace(cinderOutputNamespace)
                    .withVersionMinter(cinderVersion::getAsLong)
                    .withRestore();
            publishStager.initProducer(
                    inputConsumer::getCurrentVersionId,
                    producerBuilder,
                    cinderVip,
                    cinderPreviousVersion, noStreamsVersion::get);
            this.producer = VMSTransformerWriteStateEngine.initAndBuildProducer(producerBuilder);

            String nostreamsCinderVip = cinderVip + "_nostreams";
            String cinderNostreamsOutputNamespace = "vms-" + nostreamsCinderVip;

            CinderProducerBuilder noStreamsProducerBuilder = cinderBuilder.get()
                    .withSnapshotPublishExecutor(produceExecutor)
                    .forNamespace(cinderNostreamsOutputNamespace)
                    .withVersionMinter(cinderVersion::getAsLong)
                    .withRestore();
            publishStager.initNoStreamsProducer(
                    inputConsumer::getCurrentVersionId,
                    noStreamsProducerBuilder,
                    nostreamsCinderVip,
                    cinderPreviousVersion);
            HollowProducer noStreamsProducer = VMSTransformerWriteStateEngine.initAndBuildNoStreamsProducer(
                    noStreamsProducerBuilder);

            class NoStreamsPipeline implements CycleListener, IntegrityCheckListener, AnnouncementListener {

                CompletableFuture<Long> noStreams;

                // IntegrityCheckListener

                @Override
                public void onIntegrityCheckStart(long version) {
                }

                @Override
                public void onIntegrityCheckComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                    // Execute nostreams pipeline in parallel to the main pipeline
                    // and join on announcement or cycle completion
                    noStreams = CompletableFuture.supplyAsync(() -> {
                        return noStreamsProducer.runCycle(ws -> {
                            HollowReadStateEngine input = readState.getStateEngine();
                            HollowWriteStateEngine output = ws.getStateEngine();
                            HollowCombiner combiner = VMSTransformerWriteStateEngine.getNoStreamsCombiner(input, output);
                            combiner.combine();
                        });
                    }, produceExecutor);
                }


                // AnnouncementListener

                @Override
                public void onAnnouncementStart(long version) {
                    try {
                        // Wait for nostreams to complete, any exception will veto the main pipeline
                        noStreamsVersion.set(noStreams.join());
                    } catch (Exception e) {
                        throw new VetoableListener.ListenerVetoException(e);
                    } finally {
                        noStreams = null;
                    }
                }

                @Override
                public void onAnnouncementComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                }


                // CycleListener

                @Override public void onCycleSkip(CycleSkipReason reason) {
                }

                @Override public void onNewDeltaChain(long version) {
                }

                @Override public void onCycleStart(long version) {
                }

                @Override public void onCycleComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                    if (status.getType() == Status.StatusType.FAIL && noStreams != null) {
                        // Wait for nostreams to complete if main pipeline failed after integrity check
                        try {
                            noStreams.join();
                        } catch (Exception e) {
                            Throwable cause = status.getCause();
                            if (cause != null) {
                                status.getCause().addSuppressed(e);
                            }
                        } finally {
                            noStreams = null;
                        }
                    }
                }
            }
            producer.addListener(new NoStreamsPipeline());

            class LoggingListener implements CycleListener, PublishListener, RestoreListener, PopulateListener,
                    IntegrityCheckListener, AnnouncementListener {
                // Restore listener

                @Override public void onProducerRestoreStart(long restoreVersion) {
                    // @@@ Cycle start
                }

                @Override public void onProducerRestoreComplete(
                        Status status, long versionDesired, long versionReached, Duration elapsed) {
                    previousCycleNumber = versionReached;
                }


                // Cycle listener

                @Override public void onCycleSkip(CycleSkipReason reason) {
                }

                @Override public void onNewDeltaChain(long version) {
                }

                @Override public void onCycleStart(long version) {
                }

                @Override public void onCycleComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                    if (workflowStarted) {
                        workflowStarted = false;
                        ctx.stopTimerAndLogDuration(P4_WaitForPublishWorkflowDuration);
                    }
                }

                @Override public void onNoDeltaAvailable(long version) {
                }


                // Populate listener

                @Override public void onPopulateStart(long version) {
                    ctx.getMetricRecorder().startTimer(P2_ProcessDataDuration);
                }

                @Override public void onPopulateComplete(Status status, long version, Duration elapsed) {
                    if (status.getType() == Status.StatusType.FAIL) {
                        ctx.getLogger().error(Arrays.asList(BlobState, TransformCycleFailed), "transform failed",
                                status.getCause());
                    }
                    ctx.stopTimerAndLogDuration(P2_ProcessDataDuration);
                }


                // Publish listener

                // @@@ Timing of P3_WriteOutputDataDuration writing and publish blobs

                @Override public void onPublishStart(long version) {
                    ctx.getMetricRecorder().startTimer(P3_WriteOutputDataDuration);
                    currentStateHeader = new HashMap<>(producer.getWriteEngine().getHeaderTags());
                }

                @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                }

                @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
                    if (status.getType() == Status.StatusType.FAIL) {
                        ctx.getLogger().error(Arrays.asList(BlobState, WritingBlobsFailed), "Writing blobs failed",
                                status.getCause());
                    }
                    ctx.stopTimerAndLogDuration(P3_WriteOutputDataDuration);
                }


                // Integrity check listener

                boolean workflowStarted;

                @Override public void onIntegrityCheckStart(long version) {
                    workflowStarted = true;
                    ctx.getMetricRecorder().startTimer(P4_WaitForPublishWorkflowDuration);
                }

                @Override public void onIntegrityCheckComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                }


                // Announcement listener

                @Override public void onAnnouncementStart(long version) {
                }

                @Override public void onAnnouncementComplete(
                        Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                    if (status.getType() == Status.StatusType.FAIL) {
                        return;
                    }

                    previousCycleNumber = version;
                    timeSinceLastPublishGauge.notifyPublishSuccess();

                    ctx.getLogger().info(BlobState,
                            "endCycleSuccessfully write state : before({}), after({}),)",
                            BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader),
                            BlobMetaDataUtil.fetchCoreHeaders(producer.getWriteEngine()));
                }
            }
            producer.addListener(new LoggingListener());
        } else {
            this.producer = null;
            this.produceExecutor = null;
        }
    }

    private static boolean isNoStreamsBlobEnabled(boolean isFastlane) {
        return !isFastlane; // Fastlane does not deal with NoStreams blob
    }

    private void restoreOutputs(VMSOutputDataClient restoreFrom, VMSOutputDataClient nostreamsRestoreFrom, boolean isNoStreamsBlobEnabled) {
        HollowReadStateEngine restoreStateEngine = restoreFrom.getStateEngine();
        HollowReadStateEngine restoreNoStreamStateEngine = isNoStreamsBlobEnabled ? nostreamsRestoreFrom.getStateEngine() : null;

        { // @TODO FIX: should restore headers as well
            outputStateEngine.addHeaderTags(restoreStateEngine.getHeaderTags());
            outputStateEngine.restoreFrom(restoreStateEngine);
        }
        ctx.getLogger().info(BlobState, "restore : input({}), output({}),)",
                BlobMetaDataUtil.fetchCoreHeaders(restoreStateEngine), BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
        previousCycleNumber = restoreFrom.getCurrentVersionId();

        publishWorkflowStager.notifyRestoredStateEngine(restoreStateEngine, restoreNoStreamStateEngine);
        setRestoreNeeded(false);
    }

    private void setRestoreNeeded(boolean isRestoreNeeded) {
        this.isRestoreNeeded = isRestoreNeeded;
    }

    public void cycle() throws Throwable {
        if (producer == null) {
            try {
                beginCycle();
                loadInputAndRestoreOutputs();

                transformTheData();
                if (isUnchangedFastlaneState()) {
                    rollbackFastlaneStateEngine();
                    incrementSuccessCounter();
                } else {
                    writeTheBlobFiles();
                    submitToPublishWorkflow();
                    endCycleSuccessfully();
                }
            } catch (Throwable th) {
                outputStateEngine.addHeaderTags(previousStateHeader);
                ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine),
                        "Transformer failed cycle -- rolling back write state engine to previousState=({})",
                        BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine), th);

                outputStateEngine.resetToLastPrepareForNextCycle();
                fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
                throw th;
            } finally {
                isFirstCycle = false;
            }
        } else {
            try {
                beginCycleCinder();

                // Only restores the input
                // @@@ Does not report the time take to restore the output
                setRestoreNeeded(false);
                loadInputAndRestoreOutputs();

                cinderCycle();

                endCycleSuccessfullyCinder();

                // @@@ Sleep to ensure Gutenberg's announcement is available to query when restoring
                //     If the time between the end of one cycle and a new cycle is too quick a restore
                //     may still observe the previously announced version and restore to that version
                //     resulting in a possible break of the delta chain (luckily playback monkey checks
                //     will cause the cycle to fail).
                //     It can take a little time until Gutenberg's announcement is eventually consistent.
                //     This *needs* to be fixed in Cinder's NFHollowAnnouncer.
                try {
                    SECONDS.sleep(15);
                } catch (InterruptedException e) {}
            } catch (Throwable th) {
                ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine),
                        "Transformer failed cycle -- rolling back write state engine to previousState=({})",
                        BlobMetaDataUtil.fetchCoreHeaders(producer.getWriteEngine()), th);
                throw th;
            } finally {
                isFirstCycle = false;
            }
        }
    }

    public void tryCompaction() throws Throwable {
        long compactionBytesThreshold = ctx.getConfig().getCompactionHoleByteThreshold();
        int compactionPercentThreshold = ctx.getConfig().getCompactionHolePercentThreshold();
        HollowCompactor compactor = new HollowCompactor(outputStateEngine, publishWorkflowStager.getCurrentReadStateEngine(),
                compactionBytesThreshold, compactionPercentThreshold);

        if(compactor.needsCompaction()) {
            try {
                beginCycle();
                ctx.getLogger().info(Arrays.asList(BlobState, StateEngineCompaction),
                        "Compacting State Engine");
                compactor.compact();
                writeTheBlobFiles();
                submitToPublishWorkflow();
                endCycleSuccessfully();
            } catch(Throwable th) {
                outputStateEngine.addHeaderTags(previousStateHeader);
                ctx.getLogger().error(Arrays.asList(BlobState, RollbackStateEngine),
                        "Transformer failed cycle -- rolling back write state engine to previousState=({})",
                        BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine), th);

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
        if (wasCyclePaused) {
            ctx.getLogger().warn(Arrays.asList(TransformCyclePaused, TransformCycleResumed),
                    "Resumed cycle={}",
                    currentCycleNumber);
        }
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
        ctx.getLogger().info(BlobState,
                "beginCycle : cycle={}, isFastlane={}, isNoStreamsBlobEnabled={}, previousStateHeader({})",
                currentCycleNumber, isFastlane, isNoStreamsBlobEnabled, BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader));
        ctx.getLogger().info(TransformCycleBegin,
                "Beginning cycle={} jarVersion={}",
                currentCycleNumber, BlobMetaDataUtil.getJarVersion());

        // Check whether to Pause Cycle (before any logic)
        checkPauseCycle();

        // Init Context to begin cycle processing (e.g. Freeze Config)
        ctx.beginCycle();
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(),
                "Stopped at beginCycle");

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

    private void beginCycleCinder() {
        // First cycle already initialized in Constructor
        if (!isFirstCycle) currentCycleNumber = initCycleNumber(ctx, versionMinter);

        // Track cycle begin
        previousStateHeader = new HashMap<>(producer.getWriteEngine().getHeaderTags());
        ctx.getLogger().info(BlobState,
                "Using Cinder/HollowProducer");
        ctx.getLogger().info(BlobState,
                "beginCycle : cycle={}, isFastlane={}, isNoStreamsBlobEnabled={}, previousStateHeader({})",
                currentCycleNumber, isFastlane, isNoStreamsBlobEnabled, BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader));
        ctx.getLogger().info(TransformCycleBegin,
                "Beginning cycle={} jarVersion={}",
                currentCycleNumber, BlobMetaDataUtil.getJarVersion());

        // Check whether to Pause Cycle (before any logic)
        checkPauseCycle();

        // Init Context to begin cycle processing (e.g. Freeze Config)
        ctx.beginCycle();
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(),
                "Stopped at beginCycle");

        // track ids
        trackIds(CycleFastlaneIds, "Fastlane Ids={}", ctx.getFastlaneIds());
        trackIds(CyclePinnedTitles, "Config Spec={}", ctx.getPinTitleSpecs());

        // Spot to trigger Cycle Monkey if enabled
        cycleMonkey.doMonkeyBusiness("beginCycle");
    }

    private static CompletableFuture<Void> restoreOutputAsync(Executor executor,
            String name, TransformerContext ctx, VMSOutputDataClient outputClient, long restoreVersion) {
        return CompletableFuture.runAsync(() -> restoreOutput(name, ctx, outputClient, restoreVersion), executor);
    }

    private static void restoreOutput(String name, TransformerContext ctx,
            VMSOutputDataClient outputClient, long restoreVersion) {
        try {
            // Spot to trigger Cycle Monkey if enabled
            ctx.getCycleMonkey().doMonkeyBusiness(name);

            long start = System.currentTimeMillis();
            outputClient.triggerRefreshTo(restoreVersion);

            ctx.getLogger().info(Arrays.asList(TransformRestore, BlobState),
                    "Restored {} version={}, duration={}, header={}",
                    name, restoreVersion, (System.currentTimeMillis() - start),
                    BlobMetaDataUtil.fetchCoreHeaders(outputClient.getStateEngine()));
        } catch (RuntimeException e) {
            ctx.getLogger().error(TransformRestore,
                    "Failed to restore {} version={}",
                    name, restoreVersion, e);
            throw e;
        }
    }

    private static void restoreOutputsInParallel(SimultaneousExecutor executor,
            TransformerContext ctx, long restoreVersion,
            VMSOutputDataClient outputClient, VMSOutputDataClient nostreamsOutputClient,
            boolean isNoStreamsBlobEnabled) throws Exception {
        // Execute Restore in background
        CompletableFuture<Void> restoreNormalBlobResult =
                restoreOutputAsync(executor, "restoreNormalBlob", ctx, outputClient, restoreVersion);
        CompletableFuture<Void> restoreNoStreamsBlobResult = isNoStreamsBlobEnabled
                ? restoreOutputAsync(executor, "restoreNoStreamsBlob", ctx, nostreamsOutputClient, restoreVersion)
                : null;

        // Wait for all executions to complete, including loading the input
        executor.awaitUninterruptibly();

        restoreNormalBlobResult.get();
        if (isNoStreamsBlobEnabled) {
            restoreNoStreamsBlobResult.get();
        }

        // Validate that both have restored to the specified version
        if (outputClient.getCurrentVersionId() != restoreVersion) {
            throw new IllegalStateException(
                    "Failed to restore (with streams) to specified restoreVersion: " + restoreVersion
                            + ",  currentVersion=" + outputClient.getCurrentVersionId());
        } else if (isNoStreamsBlobEnabled && nostreamsOutputClient.getCurrentVersionId() != restoreVersion) {
            throw new IllegalStateException(
                    "Failed to restore (nostreams) to specified restoreVersion: " + restoreVersion
                            + ",  currentVersion=" + nostreamsOutputClient.getCurrentVersionId());
        }
    }

    public static void restoreOutputs(SimultaneousExecutor executor,
            TransformerContext ctx, TransformCycle cycle, FileStore fileStore, HermesBlobAnnouncer hermesBlobAnnouncer,
            boolean isFastlane, boolean isRunWithinUpdateInput) {
        // No need to track duration if RunWithinUpdateInput since it will be part of that duration
        if (!isRunWithinUpdateInput) {
            ctx.getMetricRecorder().startTimer(DurationMetric.P0_RestoreDataDuration);
        }

        try {
            TransformerConfig cfg = ctx.getConfig();
            if (cfg.isRestoreFromPreviousStateEngine()) {
                long latestVersion = hermesBlobAnnouncer.getLatestAnnouncedVersionFromCassandra(cfg.getTransformerVip());
                long restoreVersion = cfg.getRestoreFromSpecificVersion() != null
                        ? cfg.getRestoreFromSpecificVersion()
                        : latestVersion;

                if (restoreVersion != Long.MIN_VALUE) {
                    // Restore in parallel
                    VMSOutputDataClient outputClient = new VMSOutputDataClient(fileStore, cfg.getTransformerVip());
                    VMSOutputDataClient nostreamsOutputClient = new VMSOutputDataClient(fileStore,
                            VipNameUtil.getNoStreamsVip(cfg.getTransformerVip()));

                    boolean isNoStreamsBlobEnabled = isNoStreamsBlobEnabled(isFastlane);
                    restoreOutputsInParallel(executor, ctx, restoreVersion, outputClient, nostreamsOutputClient,
                            isNoStreamsBlobEnabled);

                    // Let TransformCycle complete the restore process
                    cycle.restoreOutputs(outputClient, nostreamsOutputClient, isNoStreamsBlobEnabled);
                } else if (cfg.isFailIfRestoreNotAvailable()) {
                    // @TODO: Wonder if restoreVersion==Long.MIN_VALUE is sufficient to detect new namespace,
                    //  if so this exception is not needed
                    throw new IllegalStateException("Cannot restore from previous state -- previous state does not exist?"
                            + "  If this is expected (e.g. a new VIP), temporarily set vms.failIfRestoreNotAvailable=false");
                } else {
                    // Likely new Namespace
                    cycle.setRestoreNeeded(false);
                }
            }
        } catch (Exception ex) {
            ctx.getLogger().error(Arrays.asList(TransformRestore, BlobState),
                    "Failed to restore data", ex);
            throw new IllegalStateException("Failed to restore data", ex);
        } finally {
            if (!isRunWithinUpdateInput) {
                ctx.stopTimerAndLogDuration(DurationMetric.P0_RestoreDataDuration);
            }
        }
    }

    private static CompletableFuture<Void> loadInputAsync(Executor executor,
            TransformerContext ctx, HollowConsumer inputConsumer, final Long pinnedInputVersion) {
        return CompletableFuture.runAsync(() -> loadInput(ctx, inputConsumer, pinnedInputVersion), executor);
    }

    private static void loadInput(TransformerContext ctx, HollowConsumer inputConsumer, final Long pinnedInputVersion) {
        try {
            // Spot to trigger Cycle Monkey if enabled
            ctx.getCycleMonkey().doMonkeyBusiness("loadInput");

            if (pinnedInputVersion == null) {
                inputConsumer.triggerRefresh();
            } else {
                inputConsumer.triggerRefreshTo(pinnedInputVersion);
                if (inputConsumer.getCurrentVersionId() != pinnedInputVersion) {
                    throw new IllegalStateException("Failed to pin input to :" + pinnedInputVersion);
                }
            }

            ctx.getLogger().info(BlobState,
                    "Loaded input to version={}, header={}",
                    inputConsumer.getCurrentVersionId(), inputConsumer.getStateEngine().getHeaderTags());
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState,
                    "Failed to Load Input", ex);
            throw ex;
        }
    }

    private void loadInputAndRestoreOutputs() {
        ctx.getMetricRecorder().startTimer(P1_ReadInputDataDuration);
        try {
            FollowVipPin followVipPin = followVipPinExtractor.retrieveFollowVipPin(ctx);

            /// load the input data
            Long pinnedInputVersion = ctx.getConfig().getPinInputVersion();
            if(pinnedInputVersion == null && followVipPin != null) {
                pinnedInputVersion = followVipPin.getInputVersionId();
            }

            // Determine whether to process restore here; only restore once
            if (isRestoreNeeded && ctx.getConfig().isProcessRestoreAndInputInParallel()) {
                SimultaneousExecutor executor = new SimultaneousExecutor(3,
                        TransformCycle.class,
                        "vms-restore-and-input-processing");

                // Load input concurrently with restoring the outputs
                CompletableFuture<Void> inputProcessingResult = loadInputAsync(executor, ctx, inputConsumer, pinnedInputVersion);
                // Restore outputs concurrently
                restoreOutputs(executor, ctx, this, filestore, hermesBlobAnnouncer, isFastlane, true);

                executor.awaitUninterruptibly();
                inputProcessingResult.get();
            } else {
                // Load input sequentially
                loadInput(ctx, inputConsumer, pinnedInputVersion);
            }

            //// set the now millis
            Long nowMillis = ctx.getConfig().getNowMillis();
            if(nowMillis == null && followVipPin != null)
                nowMillis = followVipPin.getNowMillis();
            if(nowMillis == null)
                nowMillis = System.currentTimeMillis();
            ctx.setNowMillis(nowMillis);

            ctx.getLogger().info(InputDataConverterVersionId, inputConsumer.getCurrentVersionId());
            ctx.getLogger().info(ProcessNowMillis,
                    "Using transform timestamp of {} ({})",
                    nowMillis, new Date(nowMillis));

            VMSInputDataVersionLogger.logInputVersions(inputConsumer.getStateEngine().getHeaderTags(), ctx.getLogger());
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState,
                    "Failed to process Input", ex);
            throw new RuntimeException("Failed to process input", ex);
        } finally {
            ctx.stopTimerAndLogDuration(P1_ReadInputDataDuration);
        }
    }


    private void cinderCycle() {
        producer.runCycle(newState -> {
            HollowWriteStateEngine output = newState.getStateEngine();
            output.getHeaderTags().clear();
            long previousVersion = newState.getPriorState() == null ? -1 : newState.getPriorState().getVersion();

            output.getHeaderTags().clear();
            headerPopulator.addHeaders(inputConsumer, output, previousVersion, newState.getVersion());

            SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputConsumer.getAPI(), output, ctx);
            try {
                transformer.transform();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
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
                trasformInputData(inputConsumer.getAPI(), fastlaneOutputStateEngine, ctx);

                /*
                 * Combine input states
                 *
                 * When combining pinned and fastlane inputs, there are some interesting behaviors (good and bad) of
                 * note.
                 *
                 * For video-related types as listed by {@code OutputTypeConfig.VIDEO_RELATED_TYPES}, the pinned inputs
                 * take precedence over the fastlane input.
                 * For non-video-related types as defined by {@code OutputTypeConfig.NON_VIDEO_RELATED_TYPES}, the
                 * fastlane input takes precedence over pinned inputs.
                 *
                 * The good news is that this allows video-related data (for eg. PackageData) to be pinned to a certain
                 * version while allowing updates to non-video-related data (for eg. PersonImages that are shared by
                 * multiple videos) to be propagated to the output write state.
                 * The bad news is that this leads to confusion around whether data fixes should be propagated using
                 * pinning or the fast lane, and whether pinning a title to a previous version will fix a data error or
                 * not. To clear this confusion one would need to be cognizant of this combining behavior and
                 * classification of types as being video-related or non-video-related as defined in
                 * {@code OutputTypeConfig}.
                 *
                 * This behavior is a result the pursuit of a simple design for pinning. Non-video-related data such as
                 * CharacterImages is shared across multiple videos, so pinning non-video-related data for one title
                 * would affect other titles, and in order to pin multiple titles at different versions a lot of
                 * referenced data will also need to be pinned. Meanwhile, how to apply fastlane updates to pinned
                 * versions would be another issue.
                 *
                 * As a result, with the current pinning design, if a top-level title is added to both pinned and
                 * fastlane inputs, then the output write state will obtain video-related data from the pinned version,
                 * and non-video-related-data from the fastlane. Thus, when using pinning or fastlane to fix data
                 * errors, one needs to know if the erroneous attribute belongs to video-related or non-video-related
                 * data.
                 */

                //////////////////////
                /*
                 * Address issue related to removing a subset of pinned titles.
                 *
                 * Title pinning was designed to try to maintain the fastlane SLA since title pinning could take from a
                 * few minutes to ++ depending on how many titles and number of different versions they belong (# of
                 * title pinning jobs, job = per pin version).
                 * This means that fastlane cycle will not wait for title pinning job to complete (except for startup so
                 * that there is not gap during deployment).
                 * It was done that way because new title pinning requests could be done in the background and be
                 * combined with fastlane when ready on subsequent cycles.
                 * However, this introduces a bug where a subset of pinning titles are being removed form a pinned job
                 * because such change is considered a new title pin job.
                 * Fastlane cycle won't wait for that new job to complete; hence, the remaining pinned titles disappears
                 * from fastlane until the background job is completed in subsequent.
                 *
                 * Proposal:
                 * - Short term: Fastlane always wait for all title pinning job to complete to address the bug but it
                 * will impact fastlane SLA whenever a new title pinning requests appears at the beginning of fastlane
                 * cycle.
                 *
                 * - Long term: Better handle this use case with tracking prior cycle job and correlate with new job to
                 * detect this use case. This will be a bit more involved and will take more time to properly verify.
                 *
                 * NOTE: Waiting for all jobs to complete on every cycle instead of just firstCycle to address bug on
                 * moving subset of titles being pinned
                 * List<HollowReadStateEngine> overrideTitleOutputs = pinTitleMgr.getResults(isFirstCycle);
                 */
                List<HollowReadStateEngine> overrideTitleOutputs = pinTitleMgr.getResults(true);
                PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx,
                        outputStateEngine, fastlaneOutputStateEngine, overrideTitleOutputs);
                combiner.combine();

                String overrideBlobID = PinTitleHelper.getBlobID(outputStateEngine);
                String pinnedTitles = PinTitleHelper.getPinnedTitles(outputStateEngine);
                ctx.getLogger().info(CyclePinnedTitles,
                        "Pinned Titles=[{}]",
                        pinnedTitles);
                ctx.getLogger().info(CyclePinnedTitles,
                        "Processed blobId={}, pinnedTitles={}, hasDataChanged={}, fastlaneChanged={}, isFirstCycle={}, duration={}",
                        overrideBlobID, pinnedTitles,
                        outputStateEngine.hasChangedSinceLastCycle(), fastlaneOutputStateEngine.hasChangedSinceLastCycle(),
                        isFirstCycle, (System.currentTimeMillis() - startTime));
            } else {
                trasformInputData(inputConsumer.getAPI(), outputStateEngine, ctx);

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

    private static void trasformInputData(HollowAPI inputAPI, VMSTransformerWriteStateEngine outputStateEngine,
            TransformerContext ctx) throws Throwable {
        SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI) inputAPI, outputStateEngine, ctx);
        transformer.transform();

        String BLOB_ID = VipNameUtil.isOverrideVip(ctx.getConfig()) ? "FASTLANE" : "BASEBLOB";
        PinTitleHelper.addBlobID(outputStateEngine, BLOB_ID);
    }

    private void writeTheBlobFiles() throws Exception {
        ctx.getMetricRecorder().startTimer(P3_WriteOutputDataDuration);

        Collection<LogTag> blobStateTags = Arrays.asList(WroteBlob, BlobState);
        try {
            currentStateHeader = new HashMap<>(headerPopulator.addHeaders(
                    inputConsumer, outputStateEngine,
                    previousCycleNumber, currentCycleNumber));
            HollowBlobFileNamer fileNamer = new HollowBlobFileNamer(transformerVip);
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);

            String snapshotFileName = fileNamer.getSnapshotFileName(currentCycleNumber);
            try (OutputStream snapshotOutputStream = ctx.files().newBlobOutputStream(new File(snapshotFileName))) {
                writer.writeSnapshot(snapshotOutputStream);
                ctx.getLogger().info(blobStateTags,
                        "Wrote Snapshot to local file( {} ) - header( {} )",
                        snapshotFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
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
                    ctx.getLogger().info(blobStateTags,
                            "Wrote Delta to local file( {}) - header( {} )",
                            deltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                }

                if (isNoStreamsBlobEnabled) {
                    String nostreamsDeltaFileName = fileNamer.getNostreamsDeltaFileName(previousCycleNumber, currentCycleNumber);
                    createNostreamsFilteredFile(deltaFileName, nostreamsDeltaFileName, false);
                }

                String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(currentCycleNumber, previousCycleNumber);
                outputStateEngine.addHeaderTags(previousStateHeader); // Make sure to have reverse delta's header point to prior state
                try (OutputStream reverseDeltaOutputStream = ctx.files().newBlobOutputStream(new File(reverseDeltaFileName))){
                    writer.writeReverseDelta(reverseDeltaOutputStream);
                    ctx.getLogger().info(blobStateTags,
                            "Wrote Reverse Delta to local file( {} ) - header( {} )",
                            reverseDeltaFileName, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
                }

                if (isNoStreamsBlobEnabled) {
                    String nostreamsReverseDeltaFileName = fileNamer.getNostreamsReverseDeltaFileName(
                            currentCycleNumber, previousCycleNumber);
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

    private void createNostreamsFilteredFile(String unfilteredFilename, String filteredFilename, boolean isSnapshot)
            throws IOException {
        HollowFilterConfig filterConfig = VMSTransformerWriteStateEngine.getNoStreamsFilterConfig();
        FilteredHollowBlobWriter writer = new FilteredHollowBlobWriter(filterConfig);

        Collection<LogTag> blobStateTags = Arrays.asList(WroteBlob, BlobState);
        try(InputStream is = ctx.files().newBlobInputStream(new File(unfilteredFilename));
                OutputStream os = ctx.files().newBlobOutputStream(new File(filteredFilename))) {
            writer.filter(!isSnapshot, is, os);
            ctx.getLogger().info(blobStateTags,
                    "Wrote NostreamsFilteredFile({}) - header( {} )",
                    filteredFilename, BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
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
        return ctx.getFastlaneIds() != null &&
                previousCycleNumber != Long.MIN_VALUE &&
                !outputStateEngine.isRestored() &&
                !outputStateEngine.hasChangedSinceLastCycle();
    }

    private boolean rollbackFastlaneStateEngine() {
        outputStateEngine.addHeaderTags(previousStateHeader);
        outputStateEngine.resetToLastPrepareForNextCycle();
        fastlaneOutputStateEngine.resetToLastPrepareForNextCycle();
        ctx.getLogger().info(TransformerLogTag.HideCycleFromDashboard,
                "Fastlane data was unchanged -- rolling back and trying again");
        return true;
    }

    public void submitToPublishWorkflow() {
        // Check to determine whether to abort cycle due to interrupt
        ctx.getCycleInterrupter().triggerInterruptIfNeeded(ctx.getCurrentCycleId(), ctx.getLogger(),
                "Stopped at submitToPublishWorkflow");

        ctx.getMetricRecorder().startTimer(P4_WaitForPublishWorkflowDuration);
        try {
            CycleStatusFuture future = publishWorkflowStager.triggerPublish(
                    inputConsumer.getCurrentVersionId(), previousCycleNumber, currentCycleNumber);
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
        ctx.getLogger().info(BlobState,
                "endCycleSuccessfully write state : before({}), after({}),)",
                BlobMetaDataUtil.fetchCoreHeaders(previousStateHeader),
                BlobMetaDataUtil.fetchCoreHeaders(outputStateEngine));
    }

    private void endCycleSuccessfullyCinder() {
        incrementSuccessCounter();
    }

    private void incrementSuccessCounter() {
        ctx.getMetricRecorder().incrementCounter(Metric.CycleSuccessCounter, 1);
    }
}
