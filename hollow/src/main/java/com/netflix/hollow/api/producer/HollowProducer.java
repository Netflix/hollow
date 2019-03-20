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

import static com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE;
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
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.tools.compact.HollowCompactor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A HollowProducer is the top-level class used by producers of Hollow data to populate, publish, and announce data states.
 * The interactions between the "blob" store and announcement mechanism are defined by this class, and the implementations
 * of the data publishing and announcing are abstracted in interfaces which are provided to this class.
 * <p>
 * To obtain a HollowProducer, you should use a builder pattern, for example:
 *
 * <pre>
 * {@code
 *
 * HollowProducer producer = HollowProducer.withPublisher(publisher)
 *                                         .withAnnouncer(announcer)
 *                                         .build();
 * }
 * </pre>
 * <p>
 * The following components are injectable, but only an implementation of the HollowProducer.Publisher is
 * required to be injected, all other components are optional. :
 *
 * <dl>
 * <dt>{@link HollowProducer.Publisher}</dt>
 * <dd>Implementations of this class define how to publish blob data to the blob store.</dd>
 *
 * <dt>{@link HollowProducer.Announcer}</dt>
 * <dd>Implementations of this class define the announcement mechanism, which is used to track the version of the
 * currently announced state.</dd>
 *
 * <dt>One or more event listeners</dt>
 * <dd>Listeners are notified about the progress and status of producer cycles throughout the various cycle stages.
 * Of special note are {@link ValidatorListener} that allow for semantic validation of the data contained in
 * a state prior to announcement.  If a {@code RuntimeException} is thrown during validation, the state will not be
 * announced, and the producer will be automatically rolled back to the prior state.
 * </dd>
 *
 * <dt>A Blob staging directory</dt>
 * <dd>Before blobs are published, they must be written and inspected/validated.  A directory may be specified as a File to which
 * these "staged" blobs will be written prior to publish.  Staged blobs will be cleaned up automatically after publish.</dd>
 *
 * <dt>{@link HollowProducer.BlobCompressor}</dt>
 * <dd>Implementations of this class intercept blob input/output streams to allow for compression in the blob store.</dd>
 *
 * <dt>{@link HollowProducer.BlobStager}</dt>
 * <dd>Implementations will define how to stage blobs, if the default behavior of staging blobs on local disk is not desirable.
 * If a {@link BlobStager} is provided, then neither a blob staging directory or {@link BlobCompressor} should be provided.</dd>
 *
 * <dt>An Executor for publishing snapshots</dt>
 * <dd>When consumers start up, if the latest announced version does not have a snapshot, they can load an earlier snapshot
 * and follow deltas to get up-to-date.  A state can therefore be available and announced prior to the availability of
 * the snapshot.  If an Executor is supplied here, then it will be used to publish snapshots.  This can be useful if
 * snapshot publishing takes a long time -- subsequent cycles may proceed while snapshot uploads are still in progress.</dd>
 *
 * <dt>Number of cycles between snapshots</dt>
 * <dd>Because snapshots are not necessary for a data state to be announced, they need not be published every cycle.
 * If this parameter is specified, then a snapshot will be produced only every (n+1)th cycle.</dd>
 *
 * <dt>{@link HollowProducer.VersionMinter}</dt>
 * <dd>Allows for a custom version identifier minting strategy.</dd>
 *
 * <dt>Target max type shard size</dt>
 * <dd>Specify a target max type shard size.  Defaults to 16MB.  See http://hollow.how/advanced-topics/#type-sharding</dd>
 * </dl>
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
public class HollowProducer {

    private static final long DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE = 16L * 1024L * 1024L;

    private final Logger log = Logger.getLogger(HollowProducer.class.getName());
    private final BlobStager blobStager;
    private final Publisher publisher;
    private final Announcer announcer;
    private final BlobStorageCleaner blobStorageCleaner;
    private HollowObjectMapper objectMapper;
    private final VersionMinter versionMinter;
    private final ListenerSupport listeners;
    private ReadStateHelper readStates;
    private final Executor snapshotPublishExecutor;
    private final int numStatesBetweenSnapshots;
    private int numStatesUntilNextSnapshot;
    private HollowProducerMetrics metrics;
    private HollowMetricsCollector<HollowProducerMetrics> metricsCollector;
    private final SingleProducerEnforcer singleProducerEnforcer;
    private long lastSuccessfulCycle = 0;
    private final HollowObjectHashCodeFinder hashCodeFinder;

    private boolean isInitialized;

    @Deprecated
    public HollowProducer(
            Publisher publisher,
            Announcer announcer) {
        this(new HollowFilesystemBlobStager(), publisher, announcer,
                Collections.emptyList(),
                new VersionMinterWithCounter(), null, 0,
                DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE, null,
                new DummyBlobStorageCleaner(), new BasicSingleProducerEnforcer(),
                null);
    }


    // The only constructor should be that which accepts a builder
    // This ensures that if the builder modified to include new state that
    // extended builders will not require modification to pass on that new state
    protected HollowProducer(Builder<?> b) {
        this(b.stager, b.publisher, b.announcer,
                b.eventListeners,
                b.versionMinter, b.snapshotPublishExecutor,
                b.numStatesBetweenSnapshots, b.targetMaxTypeShardSize,
                b.metricsCollector, b.blobStorageCleaner, b.singleProducerEnforcer,
                b.hashCodeFinder);
    }

    private HollowProducer(
            BlobStager blobStager,
            Publisher publisher,
            Announcer announcer,
            List<? extends HollowProducerEventListener> eventListeners,
            VersionMinter versionMinter,
            Executor snapshotPublishExecutor,
            int numStatesBetweenSnapshots,
            long targetMaxTypeShardSize,
            HollowMetricsCollector<HollowProducerMetrics> metricsCollector,
            BlobStorageCleaner blobStorageCleaner,
            SingleProducerEnforcer singleProducerEnforcer,
            HollowObjectHashCodeFinder hashCodeFinder) {
        this.publisher = publisher;
        this.announcer = announcer;
        this.versionMinter = versionMinter;
        this.blobStager = blobStager;
        this.singleProducerEnforcer = singleProducerEnforcer;
        this.snapshotPublishExecutor = snapshotPublishExecutor;
        this.numStatesBetweenSnapshots = numStatesBetweenSnapshots;
        this.hashCodeFinder = hashCodeFinder;

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

        this.listeners = new ListenerSupport(eventListeners.stream().distinct().collect(toList()));

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
                (restoreFrom, restoreTo) -> HollowWriteStateCreator.populateUsingReadEngine(restoreTo, restoreFrom, false));
    }

    private interface RestoreAction {
        void restore(HollowReadStateEngine restoreFrom, HollowWriteStateEngine restoreTo);
    }

    private HollowProducer.ReadState restore(
            long versionDesired, HollowConsumer.BlobRetriever blobRetriever, RestoreAction restoreAction) {
        Objects.requireNonNull(blobRetriever);
        Objects.requireNonNull(restoreAction);

        if (!isInitialized) {
            throw new IllegalStateException(
                    "You must initialize the data model of a HollowProducer with producer.initializeDataModel(...) prior to restoring");
        }

        ReadState readState = null;
        ListenerSupport.Listeners localListeners = listeners.listeners();
        Status.RestoreStageBuilder status = localListeners.fireProducerRestoreStart(versionDesired);
        try {
            if (versionDesired != HollowConstants.VERSION_NONE) {
                HollowConsumer client = HollowConsumer.withBlobRetriever(blobRetriever).build();
                client.triggerRefreshTo(versionDesired);
                if (client.getCurrentVersionId() == versionDesired) {
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

                    restoreAction.restore(readStates.current().getStateEngine(), writeEngine);

                    status.versions(versionDesired, readState.getVersion())
                            .success();
                    objectMapper = newObjectMapper; // Restore completed successfully so swap
                } else {
                    status.versions(versionDesired, client.getCurrentVersionId());
                    throw new IllegalStateException(
                            "Unable to reach requested version to restore from: " + versionDesired);
                }
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

    /**
     * Runs a cycle to populate, publish, and announce a new single data state.
     *
     * @param task the populating task to add state
     * @return the version identifier of the announced state, otherwise the
     * last successful announced version if 1) there were no data changes compared to that version;
     * or 2) the producer is not the primary producer
     * @throws RuntimeException if the cycle failed
     */    // @@@ Should this be marked as synchronized?
    public long runCycle(Populator task) {
        ListenerSupport.Listeners localListeners = listeners.listeners();

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
            return runCycle(localListeners, task, cycleStatus, toVersion);
        } finally {
            localListeners.fireCycleComplete(cycleStatus);
            metrics.updateCycleMetrics(cycleStatus.build(), cycleStatus.readState, cycleStatus.version);
            if (metricsCollector != null) {
                metricsCollector.collect(metrics);
            }
        }
    }

    /**
     * Run a compaction cycle, will produce a data state with exactly the same data as currently, but
     * reorganized so that ordinal holes are filled.  This may need to be run multiple times to arrive
     * at an optimal state.
     *
     * @param config specifies what criteria to use to determine whether a compaction is necessary
     * @return the version identifier of the produced state, or AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE if compaction was unnecessary.
     */
    public long runCompactionCycle(HollowCompactor.CompactionConfig config) {
        if (config != null && readStates.hasCurrent()) {
            final HollowCompactor compactor = new HollowCompactor(getWriteEngine(),
                    readStates.current().getStateEngine(), config);
            if (compactor.needsCompaction()) {
                return runCycle(newState -> compactor.compact());
            }
        }

        return NO_ANNOUNCEMENT_AVAILABLE;
    }

    long runCycle(ListenerSupport.Listeners listeners, Populator task, Status.StageWithStateBuilder cycleStatus, long toVersion) {
        // 1. Begin a new cycle
        Artifacts artifacts = new Artifacts();
        HollowWriteStateEngine writeEngine = getWriteEngine();
        try {
            // 1a. Prepare the write state
            writeEngine.prepareForNextCycle();

            // 2. Populate the state
            Status.StageBuilder populateStatus = listeners.firePopulateStart(toVersion);
            try (CloseableWriteState writeState = new CloseableWriteState(toVersion, objectMapper,
                    readStates.current())) {
                task.populate(writeState);
                populateStatus.success();
            } catch (Throwable th) {
                populateStatus.fail(th);
                throw th;
            } finally {
                listeners.firePopulateComplete(populateStatus);
            }

            // 3. Produce a new state if there's work to do
            if (writeEngine.hasChangedSinceLastCycle()) {
                // 3a. Publish, run checks & validation, then announce new state consumers
                publish(listeners, toVersion, artifacts);

                ReadStateHelper candidate = readStates.roundtrip(toVersion);
                cycleStatus.readState(candidate.pending());
                candidate = checkIntegrity(listeners, candidate, artifacts);

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

    /*
     * Publish the write state, storing the artifacts in the provided object. Visible for testing.
     */
    void publish(ListenerSupport.Listeners listeners, long toVersion, Artifacts artifacts) throws IOException {
        Status.StageBuilder psb = listeners.firePublishStart(toVersion);
        try {
            artifacts.snapshot = stageBlob(listeners,
                    blobStager.openSnapshot(toVersion));

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

    private Blob stageBlob(ListenerSupport.Listeners listeners, Blob blob) throws IOException {
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

    private void publishBlob(ListenerSupport.Listeners listeners, Blob blob) {
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

    private void publishSnapshotBlobAsync(ListenerSupport.Listeners listeners, Artifacts artifacts) {
        Blob blob = artifacts.snapshot;
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

    private void publishBlob(Blob b) {
        try {
            publisher.publish(b);
        } finally {
            blobStorageCleaner.clean(b.getType());
        }
    }

    /**
     * Given these read states
     * <p>
     * * S(cur) at the currently announced version
     * * S(pnd) at the pending version
     * <p>
     * Ensure that:
     * <p>
     * S(cur).apply(forwardDelta).checksum == S(pnd).checksum
     * S(pnd).apply(reverseDelta).checksum == S(cur).checksum
     *
     * @return updated read states
     */
    private ReadStateHelper checkIntegrity(ListenerSupport.Listeners listeners, ReadStateHelper readStates, Artifacts artifacts) throws Exception {
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
                        throw new ChecksumValidationException(Blob.Type.DELTA);
                    }

                    applyDelta(artifacts.reverseDelta, pending);
                    HollowChecksum reverseChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                    //out.format("  CUR <= PND %s\n", reverseChecksum);
                    if (!reverseChecksum.equals(currentChecksum)) {
                        throw new ChecksumValidationException(Blob.Type.REVERSE_DELTA);
                    }
                    result = readStates.swap();
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

    public static final class ChecksumValidationException extends IllegalStateException {
        private static final long serialVersionUID = -4399719849669674206L;

        ChecksumValidationException(Blob.Type type) {
            super(type.name() + " checksum invalid");
        }
    }

    private void readSnapshot(Blob blob, HollowReadStateEngine stateEngine) throws IOException {
        try (InputStream is = blob.newInputStream()) {
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).readSnapshot(is);
        }
    }

    private void applyDelta(Blob blob, HollowReadStateEngine stateEngine) throws IOException {
        try (InputStream is = blob.newInputStream()) {
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).applyDelta(is);
        }
    }

    private void validate(ListenerSupport.Listeners listeners, HollowProducer.ReadState readState) {
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


    private void announce(ListenerSupport.Listeners listeners, HollowProducer.ReadState readState) {
        if (announcer != null) {
            Status.StageWithStateBuilder status = listeners.fireAnnouncementStart(readState);
            try {
                announcer.announce(readState.getVersion());
                status.success();
            } catch (Throwable th) {
                status.fail(th);
                throw th;
            } finally {
                listeners.fireAnnouncementComplete(status);
            }
        }
    }

    public interface VersionMinter {
        /**
         * Create a new state version.
         * <p>
         * State versions should be ascending -- later states have greater versions.<p>
         *
         * @return a new state version
         */
        long mint();
    }

    /**
     * Represents a procedure that populates a new data state within a {@link HollowProducer} cycle.
     *
     * <p>This is a functional interface whose functional method is
     * {@link #populate(HollowProducer.WriteState)}.
     */
    @FunctionalInterface
    public interface Populator {

        /**
         * Populates the provided {@link WriteState} with new objects. Often written as a lambda passed in to
         * {@link HollowProducer#runCycle(Populator)}:
         *
         * <pre>{@code
         * producer.runCycle(state -> {
         *     sourceOfTruthA = queryA();
         *     for (Record r : sourceOfTruthA) {
         *         Model m = new Model(r);
         *         state.add(m);
         *     }
         *
         *     sourceOfTruthB = queryB();
         *     // ...
         * });
         * }</pre>
         *
         * <p>Notes:
         *
         * <ul>
         * <li>all data for the new state must be added; data from previous cycles is <em>not</em> carried
         * over automatically</li>
         * <li>caught exceptions that are unrecoverable must be rethrown</li>
         * <li>the provided {@code WriteState} will be closed and inoperable when this method returns; method
         * calls against it will throw {@code IllegalStateException}</li>
         * <li>the {@code WriteState} is thread safe</li>
         * </ul>
         *
         * <p>
         * Populating asynchronously has these additional requirements:
         * <ul>
         * <li>MUST NOT return from this method until all workers have completed – either normally
         * or exceptionally – or have been cancelled</li>
         * <li>MUST throw an exception if any worker completed exceptionally. MAY cancel remaining tasks
         * <em>or</em> wait for the remainder to complete.</li>
         * </ul>
         *
         * @param newState the new state to add objects to
         * @throws Exception if population fails
         */
        void populate(HollowProducer.WriteState newState) throws Exception;
    }

    public interface WriteState extends AutoCloseable {
        /**
         * Adds the specified POJO to the state engine. See {@link HollowObjectMapper#add(Object)} for details.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error.
         *
         * @param o the POJO to add
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
         * @return the ordinal associated with the added POJO
         */
        int add(Object o) throws IllegalStateException;

        /**
         * For advanced use-cases, access the underlying {@link HollowObjectMapper}. Prefer using {@link #add(Object)}
         * on this class instead.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error. Exercise caution when
         * saving the returned reference in a local variable that is closed over by an asynchronous task as that
         * circumvents this guard. It is safest to call {@code writeState.getObjectMapper()} within the closure.
         *
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
         * @return the object mapper
         */
        HollowObjectMapper getObjectMapper() throws IllegalStateException;

        /**
         * For advanced use-cases, access the underlying {@link HollowWriteStateEngine}. Prefer using
         * {@link #add(Object)} on this class instead.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error. Exercise caution when
         * saving the returned reference in a local variable that is closed over by an asynchronous task as that
         * circumvents this guard. It is safest to call {@code writeState.getStateEngine()} within the closure.
         *
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
         * @return the write state engine
         */
        HollowWriteStateEngine getStateEngine() throws IllegalStateException;

        /**
         * For advanced use-cases, access the ReadState of the prior successful cycle.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error. Exercise caution when
         * saving the returned reference in a local variable that is closed over by an asynchronous task as that
         * circumvents this guard. It is safest to call {@code writeState.getPriorState()} within the closure.
         *
         * @return the prior read state
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
         */
        ReadState getPriorState() throws IllegalStateException;

        /**
         * Returns the version of the current producer cycle being populated.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error.
         *
         * @return the version of the current producer cycle
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
         */
        long getVersion() throws IllegalStateException;

        /**
         * Closes this write state making it inoperable.
         *
         * <p>Once closed, calling any other method (aside from {@link #close()} will throw
         * {@code IllegalStateException}. The producer closes the current cycle's write state after the populate
         * stage is complete.
         */
        @Override
        void close();
    }

    public interface ReadState {
        long getVersion();

        HollowReadStateEngine getStateEngine();
    }


    public interface BlobStager {
        /**
         * Returns a blob with which a {@code HollowProducer} will write a snapshot for the version specified.
         * <p>
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(HollowProducer.Blob)}.
         *
         * @param version the blob version
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        HollowProducer.Blob openSnapshot(long version);

        /**
         * Returns a blob with which a {@code HollowProducer} will write a forward delta from the version specified to
         * the version specified, i.e. {@code fromVersion => toVersion}.
         * <p>
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(HollowProducer.Blob)}.
         * <p>
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion the data state this delta will transition from
         * @param toVersion the data state this delta will transition to
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        HollowProducer.Blob openDelta(long fromVersion, long toVersion);

        /**
         * Returns a blob with which a {@code HollowProducer} will write a reverse delta from the version specified to
         * the version specified, i.e. {@code fromVersion <= toVersion}.
         * <p>
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(HollowProducer.Blob)}.
         * <p>
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion version in the delta chain immediately after {@code toVersion}
         * @param toVersion version in the delta chain immediately before {@code fromVersion}
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion);
    }

    public interface BlobCompressor {
        BlobCompressor NO_COMPRESSION = new BlobCompressor() {
            @Override
            public OutputStream compress(OutputStream os) {
                return os;
            }

            @Override
            public InputStream decompress(InputStream is) {
                return is;
            }
        };

        /**
         * This method provides an opportunity to wrap the OutputStream used to write the blob (e.g. with a GZIPOutputStream).
         *
         * @param is the uncompressed output stream
         * @return the compressed output stream
         */
        OutputStream compress(OutputStream is);

        /**
         * This method provides an opportunity to wrap the InputStream used to write the blob (e.g. with a GZIPInputStream).
         *
         * @param is the compressed input stream
         * @return the uncompressed input stream
         */
        InputStream decompress(InputStream is);
    }


    public interface Publisher {

        /**
         * Publish the blob specified to this publisher's blobstore.
         * <p>
         * It is guaranteed that {@code blob} was created by calling one of
         * {@link BlobStager#openSnapshot(long)}, {@link BlobStager#openDelta(long, long)}, or
         * {@link BlobStager#openReverseDelta(long, long)} on this publisher.
         *
         * @param blob the blob to publish
         */
        void publish(HollowProducer.Blob blob);
    }

    public static abstract class Blob {

        protected final long fromVersion;
        protected final long toVersion;
        protected final Blob.Type type;


        protected Blob(long fromVersion, long toVersion, Blob.Type type) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.type = type;
        }

        protected abstract void write(HollowBlobWriter writer) throws IOException;

        public abstract InputStream newInputStream() throws IOException;

        public abstract void cleanup();

        @Deprecated
        public File getFile() {
            throw new UnsupportedOperationException("File is not available");
        }

        public Path getPath() {
            throw new UnsupportedOperationException("Path is not available");
        }

        public Type getType() {
            return this.type;
        }

        public long getFromVersion() {
            return this.fromVersion;
        }

        public long getToVersion() {
            return this.toVersion;
        }

        /**
         * Hollow blob types are {@code SNAPSHOT}, {@code DELTA} and {@code REVERSE_DELTA}.
         */
        public enum Type {
            SNAPSHOT("snapshot"),
            DELTA("delta"),
            REVERSE_DELTA("reversedelta");

            public final String prefix;

            Type(String prefix) {
                this.prefix = prefix;
            }
        }
    }

    public interface Announcer {
        void announce(long stateVersion);
    }

    static final class Artifacts {
        Blob snapshot = null;
        Blob delta = null;
        Blob reverseDelta = null;

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

    public static HollowProducer.Builder<?> withPublisher(HollowProducer.Publisher publisher) {
        Builder<?> builder = new Builder<>();
        return builder.withPublisher(publisher);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends HollowProducer.Builder<B>> {
        BlobStager stager;
        BlobCompressor compressor;
        File stagingDir;
        Publisher publisher;
        Announcer announcer;
        List<HollowProducerEventListener> eventListeners = new ArrayList<>();
        VersionMinter versionMinter = new VersionMinterWithCounter();
        Executor snapshotPublishExecutor = null;
        int numStatesBetweenSnapshots = 0;
        long targetMaxTypeShardSize = DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE;
        HollowMetricsCollector<HollowProducerMetrics> metricsCollector;
        BlobStorageCleaner blobStorageCleaner = new DummyBlobStorageCleaner();
        SingleProducerEnforcer singleProducerEnforcer = new BasicSingleProducerEnforcer();
        HollowObjectHashCodeFinder hashCodeFinder = null;

        public B withBlobStager(HollowProducer.BlobStager stager) {
            this.stager = stager;
            return (B) this;
        }

        public B withBlobCompressor(HollowProducer.BlobCompressor compressor) {
            this.compressor = compressor;
            return (B) this;
        }

        public B withBlobStagingDir(File stagingDir) {
            this.stagingDir = stagingDir;
            return (B) this;
        }

        public B withPublisher(HollowProducer.Publisher publisher) {
            this.publisher = publisher;
            return (B) this;
        }

        public B withAnnouncer(HollowProducer.Announcer announcer) {
            this.announcer = announcer;
            return (B) this;
        }

        /**
         * Registers an event listener that will receive events in accordance to the event listener
         * types that are implemented.
         *
         * @param listener the event listener
         * @return this builder
         * @throws IllegalArgumentException if the listener does not implement a recognized event listener type
         */
        public B withListener(HollowProducerEventListener listener) {
            if (!ListenerSupport.isValidListener(listener)) {
                throw new IllegalArgumentException(
                        "Listener does not implement a recognized event listener type: " + listener);
            }
            this.eventListeners.add(listener);
            return (B) this;
        }

        /**
         * Registers one or more event listeners each of which will receive events in accordance to the
         * event listener types that are implemented.
         *
         * @param listeners one or more event listeners
         * @return this builder
         * @throws IllegalArgumentException if the listener does not implement a recognized event listener type
         */
        public B withListeners(HollowProducerEventListener... listeners) {
            for (HollowProducerEventListener listener : listeners) {
                if (!ListenerSupport.isValidListener(listener)) {
                    throw new IllegalArgumentException(
                            "Listener does not implement a recognized event listener type: " + listener);
                }
                this.eventListeners.add(listener);
            }
            return (B) this;
        }

        /**
         * Registers a validator event listener that will receive a validator event
         * during the validator stage.
         *
         * @param validator the validator listener
         * @return this builder
         * @apiNote This method is equivalent to registering the listener with
         * {@link #withListener(HollowProducerEventListener)}.
         * @see #withListener(HollowProducerEventListener)
         */
        public B withValidator(ValidatorListener validator) {
            return withListener(validator);
        }

        /**
         * Register a one or more validator event listeners each of which will receive a validator event
         * during the validator stage.
         *
         * @param validators one or more validator listeners
         * @return this builder
         * @apiNote This method is equivalent to registering the listeners with
         * {@link #withListeners(HollowProducerEventListener...)}.
         * @see #withListeners(HollowProducerEventListener...)
         */
        public B withValidators(ValidatorListener... validators) {
            return withListeners(validators);
        }

        public B withListener(HollowProducerListener listener) {
            return withListener((HollowProducerEventListener) listener);
        }

        public B withListeners(HollowProducerListener... listeners) {
            return withListeners((HollowProducerEventListener[]) listeners);
        }

        public B withVersionMinter(HollowProducer.VersionMinter versionMinter) {
            this.versionMinter = versionMinter;
            return (B) this;
        }

        public B withSnapshotPublishExecutor(Executor executor) {
            this.snapshotPublishExecutor = executor;
            return (B) this;
        }

        public B withNumStatesBetweenSnapshots(int numStatesBetweenSnapshots) {
            this.numStatesBetweenSnapshots = numStatesBetweenSnapshots;
            return (B) this;
        }

        public B withTargetMaxTypeShardSize(long targetMaxTypeShardSize) {
            this.targetMaxTypeShardSize = targetMaxTypeShardSize;
            return (B) this;
        }

        public B withMetricsCollector(HollowMetricsCollector<HollowProducerMetrics> metricsCollector) {
            this.metricsCollector = metricsCollector;
            return (B) this;
        }

        public B withBlobStorageCleaner(BlobStorageCleaner blobStorageCleaner) {
            this.blobStorageCleaner = blobStorageCleaner;
            return (B) this;
        }

        public B withSingleProducerEnforcer(SingleProducerEnforcer singleProducerEnforcer) {
            this.singleProducerEnforcer = singleProducerEnforcer;
            return (B) this;
        }

        public B noSingleProducerEnforcer() {
            this.singleProducerEnforcer = null;
            return (B) this;
        }

        @Deprecated
        public B withHashCodeFinder(HollowObjectHashCodeFinder hashCodeFinder) {
            this.hashCodeFinder = hashCodeFinder;
            return (B) this;
        }

        protected void checkArguments() {
            if (stager != null && compressor != null) {
                throw new IllegalArgumentException(
                        "Both a custom BlobStager and BlobCompressor were specified -- please specify only one of these.");
            }
            if (stager != null && stagingDir != null) {
                throw new IllegalArgumentException(
                        "Both a custom BlobStager and a staging directory were specified -- please specify only one of these.");
            }

            if (this.stager == null) {
                BlobCompressor compressor = this.compressor != null ? this.compressor : BlobCompressor.NO_COMPRESSION;
                File stagingDir = this.stagingDir != null ? this.stagingDir : new File(
                        System.getProperty("java.io.tmpdir"));
                this.stager = new HollowFilesystemBlobStager(stagingDir.toPath(), compressor);
            }
        }

        public HollowProducer build() {
            checkArguments();
            return new HollowProducer(this);
        }
    }

    /**
     * Provides the opportunity to clean the blob storage.
     * It allows users to implement logic base on Blob Type.
     */
    public static abstract class BlobStorageCleaner {
        public void clean(Blob.Type blobType) {
            switch (blobType) {
                case SNAPSHOT:
                    cleanSnapshots();
                    break;
                case DELTA:
                    cleanDeltas();
                    break;
                case REVERSE_DELTA:
                    cleanReverseDeltas();
                    break;
            }
        }

        /**
         * This method provides an opportunity to remove old snapshots.
         */
        public abstract void cleanSnapshots();

        /**
         * This method provides an opportunity to remove old deltas.
         */
        public abstract void cleanDeltas();

        /**
         * This method provides an opportunity to remove old reverse deltas.
         */
        public abstract void cleanReverseDeltas();
    }

    /**
     * This Dummy blob storage cleaner does nothing
     */
    private static class DummyBlobStorageCleaner extends HollowProducer.BlobStorageCleaner {

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
