/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer;

import static com.netflix.hollow.api.producer.ProducerListenerSupport.ProducerListeners;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.api.metrics.HollowProducerMetrics;
import com.netflix.hollow.api.producer.enforcer.BasicSingleProducerEnforcer;
import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemBlobStager;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.HollowProducerEventListener;
import com.netflix.hollow.api.producer.validation.ValidationResult;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusException;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractHollowProducer {

    static final long DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE = 16L * 1024L * 1024L;

    final Logger log = Logger.getLogger(AbstractHollowProducer.class.getName());
    final HollowProducer.BlobStager blobStager;
    final HollowProducer.Publisher publisher;
    final HollowProducer.Announcer announcer;
    final HollowProducer.BlobStorageCleaner blobStorageCleaner;
    HollowObjectMapper objectMapper;
    final HollowProducer.VersionMinter versionMinter;
    final ProducerListenerSupport listeners;
    ReadStateHelper readStates;
    final Executor snapshotPublishExecutor;
    final int numStatesBetweenSnapshots;
    int numStatesUntilNextSnapshot;
    HollowProducerMetrics metrics;
    HollowMetricsCollector<HollowProducerMetrics> metricsCollector;
    final SingleProducerEnforcer singleProducerEnforcer;
    long lastSuccessfulCycle = 0;
    final HollowObjectHashCodeFinder hashCodeFinder;
    final boolean doIntegrityCheck;

    boolean isInitialized;

    @Deprecated
    public AbstractHollowProducer(
            HollowProducer.Publisher publisher,
            HollowProducer.Announcer announcer) {
        this(new HollowFilesystemBlobStager(), publisher, announcer,
                Collections.emptyList(),
                new VersionMinterWithCounter(), null, 0,
                DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE, null,
                new DummyBlobStorageCleaner(), new BasicSingleProducerEnforcer(),
                null, true);
    }

    // The only constructor should be that which accepts a builder
    // This ensures that if the builder modified to include new state that
    // extended builders will not require modification to pass on that new state
    AbstractHollowProducer(HollowProducer.Builder<?> b) {
        this(b.stager, b.publisher, b.announcer,
                b.eventListeners,
                b.versionMinter, b.snapshotPublishExecutor,
                b.numStatesBetweenSnapshots, b.targetMaxTypeShardSize,
                b.metricsCollector, b.blobStorageCleaner, b.singleProducerEnforcer,
                b.hashCodeFinder, b.doIntegrityCheck);
    }

    private AbstractHollowProducer(
            HollowProducer.BlobStager blobStager,
            HollowProducer.Publisher publisher,
            HollowProducer.Announcer announcer,
            List<? extends HollowProducerEventListener> eventListeners,
            HollowProducer.VersionMinter versionMinter,
            Executor snapshotPublishExecutor,
            int numStatesBetweenSnapshots,
            long targetMaxTypeShardSize,
            HollowMetricsCollector<HollowProducerMetrics> metricsCollector,
            HollowProducer.BlobStorageCleaner blobStorageCleaner,
            SingleProducerEnforcer singleProducerEnforcer,
            HollowObjectHashCodeFinder hashCodeFinder,
            boolean doIntegrityCheck) {
        this.publisher = publisher;
        this.announcer = announcer;
        this.versionMinter = versionMinter;
        this.blobStager = blobStager;
        this.singleProducerEnforcer = singleProducerEnforcer;
        this.snapshotPublishExecutor = snapshotPublishExecutor;
        this.numStatesBetweenSnapshots = numStatesBetweenSnapshots;
        this.hashCodeFinder = hashCodeFinder;
        this.doIntegrityCheck = doIntegrityCheck;

        HollowWriteStateEngine writeEngine = hashCodeFinder == null
                ? new HollowWriteStateEngine()
                : new HollowWriteStateEngine(hashCodeFinder);
        writeEngine.setTargetMaxTypeShardSize(targetMaxTypeShardSize);

        this.objectMapper = new HollowObjectMapper(writeEngine);
        if (hashCodeFinder != null) {
            objectMapper.doNotUseDefaultHashKeys();
        }
        this.readStates = ReadStateHelper.newDeltaChain();
        this.blobStorageCleaner = blobStorageCleaner;

        this.listeners = new ProducerListenerSupport(eventListeners.stream().distinct().collect(toList()));

        this.metrics = new HollowProducerMetrics();
        this.metricsCollector = metricsCollector;
    }

    /**
     * @return the metrics for this producer
     */
    public HollowProducerMetrics getMetrics() {
        return this.metrics;
    }

    /**
     * Initializes the data model for the given classes.
     * <p>
     * Data model initialization is required prior to {@link #restore(long, HollowConsumer.BlobRetriever) restoring}
     * the producer.
     * This ensures that restoration can correctly compare the producer's current data model
     * with the data model of the restored data state and manage any differences in those models
     * (such as not restoring state for any types in the restoring data model not present in the
     * producer's current data model).
     * <p>
     * After initialization a data model initialization event will be emitted
     * to all registered data model initialization listeners
     * {@link com.netflix.hollow.api.producer.listener.DataModelInitializationListener listeners}.
     *
     * @param classes the data model classes
     * @throws IllegalArgumentException if {@code classes} is empty
     * @see #restore(long, HollowConsumer.BlobRetriever)
     */
    public void initializeDataModel(Class<?>... classes) {
        Objects.requireNonNull(classes);
        if (classes.length == 0) {
            throw new IllegalArgumentException("classes is empty");
        }

        long start = currentTimeMillis();
        for (Class<?> c : classes) {
            objectMapper.initializeTypeState(c);
        }
        listeners.listeners().fireProducerInit(currentTimeMillis() - start);

        isInitialized = true;
    }

    /**
     * Initializes the producer data model for the given schemas.
     * <p>
     * Data model initialization is required prior to {@link #restore(long, HollowConsumer.BlobRetriever) restoring}
     * the producer.
     * This ensures that restoration can correctly compare the producer's current data model
     * with the data model of the restored data state and manage any differences in those models
     * (such as not restoring state for any types in the restoring data model not present in the
     * producer's current data model).
     * <p>
     * After initialization a data model initialization event will be emitted
     * to all registered data model initialization listeners
     * {@link com.netflix.hollow.api.producer.listener.DataModelInitializationListener listeners}.
     *
     * @param schemas the data model classes
     * @throws IllegalArgumentException if {@code schemas} is empty
     * @see #restore(long, HollowConsumer.BlobRetriever)
     */
    public void initializeDataModel(HollowSchema... schemas) {
        Objects.requireNonNull(schemas);
        if (schemas.length == 0) {
            throw new IllegalArgumentException("classes is empty");
        }

        long start = currentTimeMillis();
        HollowWriteStateCreator.populateStateEngineWithTypeWriteStates(getWriteEngine(), Arrays.asList(schemas));
        listeners.listeners().fireProducerInit(currentTimeMillis() - start);

        isInitialized = true;
    }

    /**
     * Restores the data state to a desired version.
     * <p>
     * Data model {@link #initializeDataModel(Class[]) initialization} is required prior to
     * restoring the producer.  This ensures that restoration can correctly compare the producer's
     * current data model with the data model of the restored data state and manage any differences
     * in those models (such as not restoring state for any types in the restoring data model not
     * present in the producer's current data model)
     *
     * @param versionDesired the desired version
     * @param blobRetriever the blob retriever
     * @return the read state of the restored state
     * @throws IllegalStateException if the producer's data model has not been initialized
     * @see #initializeDataModel(Class[])
     */
    public HollowProducer.ReadState restore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
        return restore(versionDesired, blobRetriever,
                (restoreFrom, restoreTo) -> restoreTo.restoreFrom(restoreFrom));
    }

    HollowProducer.ReadState hardRestore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
        return restore(versionDesired, blobRetriever,
                (restoreFrom, restoreTo) -> HollowWriteStateCreator.
                        populateUsingReadEngine(restoreTo, restoreFrom, false));
    }

    private HollowProducer.ReadState restore(
            long versionDesired, HollowConsumer.BlobRetriever blobRetriever,
            BiConsumer<HollowReadStateEngine, HollowWriteStateEngine> restoreAction) {
        Objects.requireNonNull(blobRetriever);
        Objects.requireNonNull(restoreAction);

        if (!isInitialized) {
            throw new IllegalStateException(
                    "You must initialize the data model of a HollowProducer with producer.initializeDataModel(...) prior to restoring");
        }

        HollowProducer.ReadState readState = null;
        ProducerListeners localListeners = listeners.listeners();
        Status.RestoreStageBuilder status = localListeners.fireProducerRestoreStart(versionDesired);
        try {
            if (versionDesired != HollowConstants.VERSION_NONE) {
                HollowConsumer client = HollowConsumer.withBlobRetriever(blobRetriever).build();
                client.triggerRefreshTo(versionDesired);
                readState = ReadStateHelper.newReadState(client.getCurrentVersionId(), client.getStateEngine());
                readStates = ReadStateHelper.restored(readState);

                // Need to restore data to new ObjectMapper since can't restore to non empty Write State Engine
                Collection<HollowSchema> schemas = objectMapper.getStateEngine().getSchemas();
                HollowWriteStateEngine writeEngine = hashCodeFinder == null
                        ? new HollowWriteStateEngine()
                        : new HollowWriteStateEngine(hashCodeFinder);
                HollowWriteStateCreator.populateStateEngineWithTypeWriteStates(writeEngine, schemas);
                HollowObjectMapper newObjectMapper = new HollowObjectMapper(writeEngine);
                if (hashCodeFinder != null) {
                    newObjectMapper.doNotUseDefaultHashKeys();
                }

                restoreAction.accept(readStates.current().getStateEngine(), writeEngine);

                status.versions(versionDesired, readState.getVersion())
                        .success();
                objectMapper = newObjectMapper; // Restore completed successfully so swap
            }
        } catch (Throwable th) {
            status.fail(th);
            throw th;
        } finally {
            localListeners.fireProducerRestoreComplete(status);
        }
        return readState;
    }

    public HollowWriteStateEngine getWriteEngine() {
        return objectMapper.getStateEngine();
    }

    public HollowObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Invoke this method to alter runCycle behavior. If this Producer is not primary, runCycle is a no-op. Note that by default,
     * SingleProducerEnforcer is instantiated as BasicSingleProducerEnforcer, which is initialized to return true for isPrimary()
     *
     * @param doEnable true if enable primary producer, if false
     * @return true if the intended action was successful
     */
    public boolean enablePrimaryProducer(boolean doEnable) {
        if (doEnable) {
            singleProducerEnforcer.enable();
        } else {
            singleProducerEnforcer.disable();
        }
        return (singleProducerEnforcer.isPrimary() == doEnable);
    }

    long runCycle(HollowProducer.Incremental.IncrementalPopulator incrementalPopulator, HollowProducer.Populator populator) {
        ProducerListeners localListeners = listeners.listeners();

        if (!singleProducerEnforcer.isPrimary()) {
            // TODO: minimum time spacing between cycles
            log.log(Level.INFO, "cycle not executed -- not primary");
            localListeners.fireCycleSkipped(CycleListener.CycleSkipReason.NOT_PRIMARY_PRODUCER);
            return lastSuccessfulCycle;
        }

        long toVersion = versionMinter.mint();

        if (!readStates.hasCurrent()) {
            localListeners.fireNewDeltaChain(toVersion);
        }

        Status.StageWithStateBuilder cycleStatus = localListeners.fireCycleStart(toVersion);
        try {
            return runCycle(localListeners, incrementalPopulator, populator, cycleStatus, toVersion);
        } finally {
            localListeners.fireCycleComplete(cycleStatus);
            metrics.updateCycleMetrics(cycleStatus.build(), cycleStatus.readState, cycleStatus.version);
            if (metricsCollector != null) {
                metricsCollector.collect(metrics);
            }
        }
    }

    long runCycle(
            ProducerListeners listeners,
            HollowProducer.Incremental.IncrementalPopulator incrementalPopulator, HollowProducer.Populator populator,
            Status.StageWithStateBuilder cycleStatus, long toVersion) {
        // 1. Begin a new cycle
        Artifacts artifacts = new Artifacts();
        HollowWriteStateEngine writeEngine = getWriteEngine();
        try {
            // 1a. Prepare the write state
            writeEngine.prepareForNextCycle();

            // 2. Populate the state
            populate(listeners, incrementalPopulator, populator, toVersion);

            // 3. Produce a new state if there's work to do
            if (writeEngine.hasChangedSinceLastCycle()) {
                boolean schemaChangedFromPriorVersion = readStates.hasCurrent() &&
                        !writeEngine.hasIdenticalSchemas(readStates.current().getStateEngine());
                if (schemaChangedFromPriorVersion) {
                    writeEngine.addHeaderTag(HollowStateEngine.HEADER_TAG_SCHEMA_CHANGE, Boolean.TRUE.toString());
                } else {
                    writeEngine.getHeaderTags().remove(HollowStateEngine.HEADER_TAG_SCHEMA_CHANGE);
                }

                // 3a. Publish, run checks & validation, then announce new state consumers
                publish(listeners, toVersion, artifacts);

                ReadStateHelper candidate = readStates.roundtrip(toVersion);
                cycleStatus.readState(candidate.pending());
                candidate = doIntegrityCheck ? 
                        checkIntegrity(listeners, candidate, artifacts, schemaChangedFromPriorVersion) :
                            noIntegrityCheck(candidate, artifacts);

                try {
                    validate(listeners, candidate.pending());

                    announce(listeners, candidate.pending());

                    readStates = candidate.commit();
                    cycleStatus.readState(readStates.current()).success();
                } catch (Throwable th) {
                    if (artifacts.hasReverseDelta()) {
                        applyDelta(artifacts.reverseDelta, candidate.pending().getStateEngine());
                        readStates = candidate.rollback();
                    }
                    throw th;
                }
                lastSuccessfulCycle = toVersion;
            } else {
                // 3b. Nothing to do; reset the effects of Step 2
                // Return the lastSucessfulCycle to the caller thereby
                // the callee can track that version against consumers
                // without having to listen to events.
                // Consistently report the version that would be used if
                // data had been published for the events.  This
                // is for consistency in tracking
                writeEngine.resetToLastPrepareForNextCycle();
                cycleStatus.success();
                listeners.fireNoDelta(toVersion);
            }
        } catch (Throwable th) {
            writeEngine.resetToLastPrepareForNextCycle();
            cycleStatus.fail(th);

            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            throw new RuntimeException(th);
        } finally {
            artifacts.cleanup();
        }
        return lastSuccessfulCycle;
    }

    /**
     * Adds a listener to this producer.
     * <p>
     * If the listener was previously added to this consumer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is added, concurrently, during the occurrence of a cycle or restore then the listener will not
     * receive events until the next cycle or restore.  The listener may also be removed concurrently.
     *
     * @param listener the listener to add
     */
    public void addListener(HollowProducerListener listener) {
        listeners.addListener(listener);
    }

    /**
     * Adds a listener to this producer.
     * <p>
     * If the listener was previously added to this consumer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is added, concurrently, during the occurrence of a cycle or restore then the listener will not
     * receive events until the next cycle or restore.  The listener may also be removed concurrently.
     *
     * @param listener the listener to add
     */
    public void addListener(HollowProducerEventListener listener) {
        listeners.addListener(listener);
    }

    /**
     * Removes a listener to this producer.
     * <p>
     * If the listener was not previously added to this producer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is removed, concurrently, during the occurrence of a cycle or restore then the listener will
     * receive all events for that cycle or restore but not receive events for a subsequent cycle or restore.
     *
     * @param listener the listener to remove
     */
    public void removeListener(HollowProducerListener listener) {
        listeners.removeListener(listener);
    }

    /**
     * Removes a listener to this producer.
     * <p>
     * If the listener was not previously added to this producer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is removed, concurrently, during the occurrence of a cycle or restore then the listener will
     * receive all events for that cycle or restore but not receive events for a subsequent cycle or restore.
     *
     * @param listener the listener to remove
     */
    public void removeListener(HollowProducerEventListener listener) {
        listeners.removeListener(listener);
    }

    void populate(
            ProducerListeners listeners,
            HollowProducer.Incremental.IncrementalPopulator incrementalPopulator, HollowProducer.Populator populator,
            long toVersion) throws Exception {
        assert incrementalPopulator != null ^ populator != null;

        Status.StageBuilder populateStatus = listeners.firePopulateStart(toVersion);
        try {
            if (incrementalPopulator != null) {
                // Incremental population is a sub-stage of the population stage
                // This ensures good integration with existing population listeners if this sub-stage fails
                // then the population stage will fail
                populator = incrementalPopulate(listeners, incrementalPopulator, toVersion);
            }

            try (CloseableWriteState writeState = new CloseableWriteState(toVersion, objectMapper,
                    readStates.current())) {
                populator.populate(writeState);
                populateStatus.success();
            }
        } catch (Throwable th) {
            populateStatus.fail(th);
            throw th;
        } finally {
            listeners.firePopulateComplete(populateStatus);
        }
    }

    HollowProducer.Populator incrementalPopulate(
            ProducerListeners listeners,
            HollowProducer.Incremental.IncrementalPopulator incrementalPopulator,
            long toVersion) throws Exception {
        ConcurrentHashMap<RecordPrimaryKey, Object> events = new ConcurrentHashMap<>();
        Status.IncrementalPopulateBuilder incrementalPopulateStatus = listeners.fireIncrementalPopulateStart(toVersion);
        try (CloseableIncrementalWriteState iws = new CloseableIncrementalWriteState(events, getObjectMapper())) {
            incrementalPopulator.populate(iws);
            incrementalPopulateStatus.success();

            long removed = events.values().stream()
                    .filter(o -> o == HollowIncrementalCyclePopulator.DELETE_RECORD).count();
            long addedOrModified = events.size() - removed;
            incrementalPopulateStatus.changes(removed, addedOrModified);
        } catch (Throwable th) {
            incrementalPopulateStatus.fail(th);
            throw th;
        } finally {
            listeners.fireIncrementalPopulateComplete(incrementalPopulateStatus);
        }

        return new HollowIncrementalCyclePopulator(events, 1.0);
    }

    /*
     * Publish the write state, storing the artifacts in the provided object. Visible for testing.
     */
    void publish(ProducerListeners listeners, long toVersion, Artifacts artifacts) throws IOException {
        Status.StageBuilder psb = listeners.firePublishStart(toVersion);
        try {
            if(!readStates.hasCurrent() || doIntegrityCheck || numStatesUntilNextSnapshot <= 0)
                artifacts.snapshot = stageBlob(listeners, blobStager.openSnapshot(toVersion));

            if (readStates.hasCurrent()) {
                artifacts.delta = stageBlob(listeners,
                        blobStager.openDelta(readStates.current().getVersion(), toVersion));
                artifacts.reverseDelta = stageBlob(listeners,
                        blobStager.openReverseDelta(toVersion, readStates.current().getVersion()));

                publishBlob(listeners, artifacts.delta);
                publishBlob(listeners, artifacts.reverseDelta);

                if (--numStatesUntilNextSnapshot < 0) {
                    if (snapshotPublishExecutor == null) {
                        publishBlob(listeners, artifacts.snapshot);
                        artifacts.markSnapshotPublishComplete();
                    } else {
                        // Submit the publish blob task to the executor
                        publishSnapshotBlobAsync(listeners, artifacts);
                    }
                    numStatesUntilNextSnapshot = numStatesBetweenSnapshots;
                } else {
                    artifacts.markSnapshotPublishComplete();
                }
            } else {
                publishBlob(listeners, artifacts.snapshot);
                artifacts.markSnapshotPublishComplete();
                numStatesUntilNextSnapshot = numStatesBetweenSnapshots;
            }

            psb.success();
        } catch (Throwable throwable) {
            psb.fail(throwable);
            throw throwable;
        } finally {
            listeners.firePublishComplete(psb);
        }
    }

    private HollowProducer.Blob stageBlob(ProducerListeners listeners, HollowProducer.Blob blob)
            throws IOException {
        Status.PublishBuilder builder = new Status.PublishBuilder();
        HollowBlobWriter writer = new HollowBlobWriter(getWriteEngine());
        try {
            builder.blob(blob);
            blob.write(writer);
            builder.success();
            return blob;
        } catch (Throwable t) {
            builder.fail(t);
            throw t;
        } finally {
            listeners.fireBlobStage(builder);
        }
    }

    private void publishBlob(ProducerListeners listeners, HollowProducer.Blob blob) {
        Status.PublishBuilder builder = new Status.PublishBuilder();
        try {
            builder.blob(blob);
            publishBlob(blob);
            builder.success();
        } catch (Throwable t) {
            builder.fail(t);
            throw t;
        } finally {
            listeners.fireBlobPublish(builder);
            metrics.updateBlobTypeMetrics(builder.build(), blob);
            if (metricsCollector != null) {
                metricsCollector.collect(metrics);
            }
        }
    }

    private void publishSnapshotBlobAsync(ProducerListeners listeners, Artifacts artifacts) {
        HollowProducer.Blob blob = artifacts.snapshot;
        CompletableFuture<HollowProducer.Blob> cf = new CompletableFuture<>();
        try {
            snapshotPublishExecutor.execute(() -> {
                Status.StageBuilder builder = new Status.StageBuilder();
                try {
                    publishBlob(blob);
                    builder.success();
                    // Any dependent task that needs access to the blob contents should
                    // not execute asynchronously otherwise the blob will be cleaned up
                    cf.complete(blob);
                } catch (Throwable t) {
                    builder.fail(t);
                    cf.completeExceptionally(t);
                    throw t;
                } finally {
                    metrics.updateBlobTypeMetrics(builder.build(), blob);
                    if (metricsCollector != null) {
                        metricsCollector.collect(metrics);
                    }
                }
                artifacts.markSnapshotPublishComplete();
            });
        } catch (Throwable t) {
            cf.completeExceptionally(t);
            metrics.updateBlobTypeMetrics(new Status.StageBuilder().fail(t).build(), blob);
            if (metricsCollector != null) {
                metricsCollector.collect(metrics);
            }
            throw t;
        } finally {
            listeners.fireBlobPublishAsync(cf);
        }
    }

    private void publishBlob(HollowProducer.Blob b) {
        try {
            publisher.publish(b);
        } finally {
            blobStorageCleaner.clean(b.getType());
        }
    }

    /**
     * Given these read states
     *
     * * S(cur) at the currently announced version
     * * S(pnd) = empty read state
     *
     * 1. Read in the snapshot artifact to initialize S(pnd)
     * 2. Ensure that:
     *   - S(cur).apply(forwardDelta).checksum == S(pnd).checksum
     *   - S(pnd).apply(reverseDelta).checksum == S(cur).checksum
     *
     * @return S(cur) and S(pnd)
     */
    private ReadStateHelper checkIntegrity(
            ProducerListeners listeners, ReadStateHelper readStates, Artifacts artifacts,
            boolean schemaChangedFromPriorVersion) throws Exception {
        Status.StageWithStateBuilder status = listeners.fireIntegrityCheckStart(readStates.pending());
        try {
            ReadStateHelper result = readStates;
            HollowReadStateEngine pending = readStates.pending().getStateEngine();
            readSnapshot(artifacts.snapshot, pending);

            if (readStates.hasCurrent()) {
                HollowReadStateEngine current = readStates.current().getStateEngine();

                log.info("CHECKSUMS");
                HollowChecksum currentChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                log.info("  CUR        " + currentChecksum);

                HollowChecksum pendingChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                log.info("         PND " + pendingChecksum);

                if (artifacts.hasDelta()) {
                    if (!artifacts.hasReverseDelta()) {
                        throw new IllegalStateException("Both a delta and reverse delta are required");
                    }

                    // FIXME: timt: future cycles will fail unless both deltas validate
                    applyDelta(artifacts.delta, current);
                    HollowChecksum forwardChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                    //out.format("  CUR => PND %s\n", forwardChecksum);
                    if (!forwardChecksum.equals(pendingChecksum)) {
                        throw new HollowProducer.ChecksumValidationException(HollowProducer.Blob.Type.DELTA);
                    }

                    applyDelta(artifacts.reverseDelta, pending);
                    HollowChecksum reverseChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                    //out.format("  CUR <= PND %s\n", reverseChecksum);
                    if (!reverseChecksum.equals(currentChecksum)) {
                        throw new HollowProducer.ChecksumValidationException(HollowProducer.Blob.Type.REVERSE_DELTA);
                    }
                    if (!schemaChangedFromPriorVersion) {
                        // optimization - they have identical schemas, so just swap them
                        log.log(Level.FINE, "current and pending have identical schemas, swapping");
                        result = readStates.swap();
                    } else {
                        // undo the changes we made to the read states
                        log.log(Level.FINE, "current and pending have non-identical schemas, reverting");
                        applyDelta(artifacts.reverseDelta, current);
                        applyDelta(artifacts.delta, pending);
                    }
                }
            }
            status.success();
            return result;
        } catch (Throwable th) {
            status.fail(th);
            throw th;
        } finally {
            listeners.fireIntegrityCheckComplete(status);
        }
    }
    
    private ReadStateHelper noIntegrityCheck(ReadStateHelper readStates, Artifacts artifacts) throws IOException {
        ReadStateHelper result = readStates;

        if(!readStates.hasCurrent() || 
                (!readStates.current().getStateEngine().hasIdenticalSchemas(getWriteEngine()) && artifacts.snapshot != null)) {
            HollowReadStateEngine pending = readStates.pending().getStateEngine();
            readSnapshot(artifacts.snapshot, pending);
        } else {
            HollowReadStateEngine current = readStates.current().getStateEngine();

            if (artifacts.hasDelta()) {
                if (!artifacts.hasReverseDelta()) {
                    throw new IllegalStateException("Both a delta and reverse delta are required");
                }

                applyDelta(artifacts.delta, current);
                
                result = readStates.swap();
            }
        }

        return result;
    }

    private void readSnapshot(HollowProducer.Blob blob, HollowReadStateEngine stateEngine) throws IOException {
        try (HollowBlobInput in = HollowBlobInput.serial(blob.newInputStream())) {   // shared memory mode is not supported for producer
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).readSnapshot(in);
        }
    }

    private void applyDelta(HollowProducer.Blob blob, HollowReadStateEngine stateEngine) throws IOException {
        try (HollowBlobInput in = HollowBlobInput.serial(blob.newInputStream())) {   // shared memory mode is not supported for producer
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).applyDelta(in);
        }
    }

    private void validate(ProducerListeners listeners, HollowProducer.ReadState readState) {
        Status.StageWithStateBuilder psb = listeners.fireValidationStart(readState);

        ValidationStatus status = null;
        try {
            // Stream over the concatenation of the old and new validators
            List<ValidationResult> results =
                    listeners.getListeners(ValidatorListener.class)
                            .map(v -> {
                                try {
                                    return v.onValidate(readState);
                                } catch (RuntimeException e) {
                                    return ValidationResult.from(v).error(e);
                                }
                            })
                            .collect(toList());

            status = new ValidationStatus(results);

            if (!status.passed()) {
                ValidationStatusException e = new ValidationStatusException(
                        status, "One or more validations failed. Please check individual failures.");
                psb.fail(e);
                throw e;
            }
            psb.success();
        } finally {
            listeners.fireValidationComplete(psb, status);
        }
    }


    private void announce(ProducerListeners listeners, HollowProducer.ReadState readState) {
        if (announcer != null) {
            Status.StageWithStateBuilder status = listeners.fireAnnouncementStart(readState);
            try {
                announcer.announce(readState.getVersion(), readState.getStateEngine().getHeaderTags());
                status.success();
            } catch (Throwable th) {
                status.fail(th);
                throw th;
            } finally {
                listeners.fireAnnouncementComplete(status);
            }
        }
    }

    static final class Artifacts {
        HollowProducer.Blob snapshot = null;
        HollowProducer.Blob delta = null;
        HollowProducer.Blob reverseDelta = null;

        boolean cleanupCalled;
        boolean snapshotPublishComplete;

        synchronized void cleanup() {
            cleanupCalled = true;

            cleanupSnapshot();

            if (delta != null) {
                delta.cleanup();
                delta = null;
            }
            if (reverseDelta != null) {
                reverseDelta.cleanup();
                reverseDelta = null;
            }
        }

        synchronized void markSnapshotPublishComplete() {
            snapshotPublishComplete = true;

            cleanupSnapshot();
        }

        private void cleanupSnapshot() {
            if (cleanupCalled && snapshotPublishComplete && snapshot != null) {
                snapshot.cleanup();
                snapshot = null;
            }
        }

        boolean hasDelta() {
            return delta != null;
        }

        boolean hasReverseDelta() {
            return reverseDelta != null;
        }
    }

    /**
     * This Dummy blob storage cleaner does nothing
     */
    static class DummyBlobStorageCleaner extends HollowProducer.BlobStorageCleaner {

        @Override
        public void cleanSnapshots() {
        }

        @Override
        public void cleanDeltas() {
        }

        @Override
        public void cleanReverseDeltas() {
        }
    }

}
