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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.api.metrics.HollowProducerMetrics;
import com.netflix.hollow.api.producer.enforcer.BasicSingleProducerEnforcer;
import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemBlobStager;
import com.netflix.hollow.api.producer.listener.HollowProducerEventListener;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import com.netflix.hollow.tools.compact.HollowCompactor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

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
public class HollowProducer extends AbstractHollowProducer {

    /*
     * HollowProducer and HollowProducer.Incremental extend from a package protected AbstractHollowProducer
     * for sharing common functionality.
     * To preserve binary compatibility HollowProducer overrides many methods on AbstractHollowProducer
     * and directly defers to them (in effect explicit bridge methods).
     */

    @Deprecated
    public HollowProducer(
            Publisher publisher,
            Announcer announcer) {
        super(publisher, announcer);
    }

    // The only constructor should be that which accepts a builder
    // This ensures that if the builder modified to include new state that
    // extended builders will not require modification to pass on that new state
    protected HollowProducer(Builder<?> b) {
        super(b);
    }


    /**
     * @return the metrics for this producer
     */
    @Override
    public HollowProducerMetrics getMetrics() {
        return super.getMetrics();
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
    @Override
    public void initializeDataModel(Class<?>... classes) {
        super.initializeDataModel(classes);
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
    @Override
    public void initializeDataModel(HollowSchema... schemas) {
        super.initializeDataModel(schemas);
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
    @Override
    public HollowProducer.ReadState restore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
        return super.restore(versionDesired, blobRetriever);
    }

    @Override
    public HollowWriteStateEngine getWriteEngine() {
        return super.getWriteEngine();
    }

    @Override
    public HollowObjectMapper getObjectMapper() {
        return super.getObjectMapper();
    }

    /**
     * Invoke this method to alter runCycle behavior. If this Producer is not primary, runCycle is a no-op. Note that by default,
     * SingleProducerEnforcer is instantiated as BasicSingleProducerEnforcer, which is initialized to return true for isPrimary()
     *
     * @param doEnable true if enable primary producer, if false
     * @return true if the intended action was successful
     */
    @Override
    public boolean enablePrimaryProducer(boolean doEnable) {
        return super.enablePrimaryProducer(doEnable);
    }

    /**
     * Runs a cycle to populate, publish, and announce a new single data state.
     *
     * @param task the populating task to add complete state
     * @return the version identifier of the announced state, otherwise the
     * last successful announced version if 1) there were no data changes compared to that version;
     * or 2) the producer is not the primary producer
     * @throws RuntimeException if the cycle failed
     */
    // @@@ Should this be marked as synchronized?
    public long runCycle(HollowProducer.Populator task) {
        return runCycle(null, task);
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
    @Override
    public void addListener(HollowProducerListener listener) {
        super.addListener(listener);
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
    @Override
    public void addListener(HollowProducerEventListener listener) {
        super.addListener(listener);
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
    @Override
    public void removeListener(HollowProducerListener listener) {
        super.removeListener(listener);
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
    @Override
    public void removeListener(HollowProducerEventListener listener) {
        super.removeListener(listener);
    }

    public static final class ChecksumValidationException extends IllegalStateException {
        private static final long serialVersionUID = -4399719849669674206L;

        ChecksumValidationException(Blob.Type type) {
            super(type.name() + " checksum invalid");
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
     * Represents a task that populates a new data state within a {@link HollowProducer} cycle.
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
         * <li>the provided {@code WriteState} will be inoperable when this method returns; method
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
         * @throws Exception if population fails.  If failure occurs the data from the previous cycle is retained.
         */
        void populate(HollowProducer.WriteState newState) throws Exception;
    }

    /**
     * Representation of new write state.
     */
    public interface WriteState {
        /**
         * Adds the specified POJO to the state engine. See {@link HollowObjectMapper#add(Object)} for details.
         *
         * <p>Calling this method after the producer's populate stage has completed is an error.
         *
         * @param o the POJO to add
         * @return the ordinal associated with the added POJO
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
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
         * @return the object mapper
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
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
         * @return the write state engine
         * @throws IllegalStateException if called after the populate stage has completed (see
         * {@link Populator} for details on the contract)
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
    }

    /**
     * Representation of read state computed from the population of new write state.
     */
    public interface ReadState {
        /**
         * Returns the version of the read state
         *
         * @return the version
         */
        long getVersion();

        /**
         * Returns the read state engine
         *
         * @return the read state engine
         */
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

        default void announce(long stateVersion, Map<String, String> metadata) {
            announce(stateVersion);
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
        boolean doIntegrityCheck = true;

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
            if (!ProducerListenerSupport.isValidListener(listener)) {
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
                if (!ProducerListenerSupport.isValidListener(listener)) {
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
        
        public B noIntegrityCheck() {
            this.doIntegrityCheck = false;
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

        /**
         * Builds a producer where the complete data is populated.
         *
         * @return a producer.
         */
        public HollowProducer build() {
            checkArguments();
            return new HollowProducer(this);
        }

        /**
         * Builds an incremental producer where changes (additions, modifications, removals) are
         * populated.
         *
         * @return an incremental producer
         */
        public HollowProducer.Incremental buildIncremental() {
            checkArguments();
            return new HollowProducer.Incremental(this);
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
     * A producer of Hollow data that populates with given data changes (addition, modification or removal), termed
     * incremental production.
     * <p>
     * A {@link HollowProducer} populates with all the given data after which changes are determined.  Generally in
     * other respects {@code HollowProducer} and {@code HollowProducer.Incremental} have the same behaviour when
     * publishing and announcing data states.
     */
    public static class Incremental extends AbstractHollowProducer {
        protected Incremental(Builder<?> b) {
            super(b);
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
        @Override
        public HollowProducer.ReadState restore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
            return super.hardRestore(versionDesired, blobRetriever);
        }

        /**
         * Runs a cycle to incrementally populate, publish, and announce a new single data state.
         *
         * @param task the incremental populating task to add changes (additions, modifications, or deletions)
         * @return the version identifier of the announced state, otherwise the
         * last successful announced version if 1) there were no data changes compared to that version;
         * or 2) the producer is not the primary producer
         * @throws RuntimeException if the cycle failed
         */
        // @@@ Should this be marked as synchronized?
        public long runIncrementalCycle(Incremental.IncrementalPopulator task) {
            return runCycle(task, null);
        }

        /**
         * Represents a task that incrementally populates a new data state within a {@link HollowProducer} cycle (
         * more specifically within the population stage)
         *
         * <p>This is a functional interface whose functional method is
         * {@link #populate(IncrementalWriteState)}.
         */
        @FunctionalInterface
        public interface IncrementalPopulator {

            /**
             * Incrementally populates the provided {@link IncrementalWriteState} with new additions, modifications or
             * removals of objects. Often written as a lambda passed in to
             * {@link Incremental#runIncrementalCycle(IncrementalPopulator)}:
             *
             * <pre>{@code
             * producer.runIncrementalCycle(incrementalState -> {
             *     Collection<C> changesA = queryA();
             *     for (Change c : changesA) {
             *         if (c.isAddOrModify() {
             *             incrementalState.addOrModify(c.getObject());
             *         } else {
             *             incrementalState.delete(c.getObject());
             *         }
             *     }
             *
             *     changesB = queryB();
             *     // ...
             * });
             * }</pre>
             *
             * <p>Notes:
             *
             * <ul>
             * <li>The data from the previous cycle is carried over with changes (additions, modifications or removals).
             * <li>caught exceptions that are unrecoverable must be rethrown</li>
             * <li>the provided {@code IncrementalWriteState} will be inoperable when this method returns; method
             * calls against it will throw {@code IllegalStateException}</li>
             * <li>the {@code IncrementalWriteState} is thread safe</li>
             * </ul>
             *
             * <p>
             * Incrementally populating asynchronously has these additional requirements:
             * <ul>
             * <li>MUST NOT return from this method until all workers have completed – either normally
             * or exceptionally – or have been cancelled</li>
             * <li>MUST throw an exception if any worker completed exceptionally. MAY cancel remaining tasks
             * <em>or</em> wait for the remainder to complete.</li>
             * </ul>
             *
             * @param state the state to add, modify or delete objects
             * @throws Exception if population fails.  If failure occurs the data from the previous cycle is not changed.
             */
            void populate(IncrementalWriteState state) throws Exception;
        }

        /**
         * Representation of write state that can be incrementally changed.
         */
        public interface IncrementalWriteState {

            /**
             * Adds a new object or modifies an existing object.  The object must define a primary key.
             * <p>
             * Calling this method after the producer's incremental populate stage has completed is an error.
             *
             * @param o the object
             * @throws IllegalArgumentException if the object does not have primary key defined
             */
            void addOrModify(Object o);

            /**
             * Adds a new object.  If the object already exists no action is taken and the object is left unmodified.
             * The object must define a primary key.
             * <p>
             * Calling this method after the producer's incremental populate stage has completed is an error.
             *
             * @param o the object
             * @throws IllegalArgumentException if the object does not have primary key defined
             */
            void addIfAbsent(Object o);

            /**
             * Deletes an object.  The object must define a primary key.
             * <p>
             * This action is ignored if the object does not exist.
             * <p>
             * Calling this method after the producer's incremental populate stage has completed is an error.
             *
             * @param o the object
             * @throws IllegalArgumentException if the object does not have primary key defined
             */
            void delete(Object o);

            /**
             * Deletes an object given its primary key value.
             * <p>
             * If the object does not exist (has already been deleted, say) then this action as no effect on the
             * write state.
             * <p>
             * Calling this method after the producer's incremental populate stage has completed is an error.
             *
             * @param key the primary key value
             * @throws IllegalArgumentException if the field path of the primary key is not resolvable
             */
            void delete(RecordPrimaryKey key);
        }

    }
}
