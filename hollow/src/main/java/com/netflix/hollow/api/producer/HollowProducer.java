/*
 *
 *  Copyright 2017 Netflix, Inc.
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

import static java.lang.System.currentTimeMillis;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.ReadStateImpl;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<timt@netflix.com>}
 */
public class HollowProducer {
    public static final Validator NO_VALIDATIONS = new Validator(){
        @Override
        public void validate(HollowConsumer.ReadState readState) {}
    };

    private final VersionMinter versionMinter;
    private final Publisher publisher;
    private final Validator validator;
    private final Announcer announcer;
    // TODO: timt: use HollowConsumer API
    private final HollowBlobRetriever blobRetriever;
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;
    private final ListenerSupport listeners;

    private Transition announced;

    public HollowProducer(HollowProducer.Publisher publisher,
            HollowProducer.Announcer announcer,
            HollowBlobRetriever blobRetriever) {
        this(new VersionMinterWithCounter(), publisher, NO_VALIDATIONS, announcer, blobRetriever);
    }

    public HollowProducer(HollowProducer.Publisher publisher,
            HollowProducer.Validator validator,
            HollowProducer.Announcer announcer,
            HollowBlobRetriever blobRetriever) {
        this(new VersionMinterWithCounter(), publisher, validator, announcer, blobRetriever);
    }

    private HollowProducer(VersionMinter versionMinter,
            Publisher publisher,
            Validator validator,
            Announcer announcer,
            HollowBlobRetriever blobRetriever) {
        this.versionMinter = versionMinter;
        this.publisher = publisher;
        this.validator = validator;
        this.announcer = announcer;
        this.blobRetriever = blobRetriever;

        announced = new Transition();
        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        listeners = new ListenerSupport();
    }

    public void initializeDataModel(Class<?>...classes) {
        long start = currentTimeMillis();
        for(Class<?> c : classes)
            objectMapper.initializeTypeState(c);
        listeners.fireProducerInit(currentTimeMillis() - start);
    }

    public HollowProducer restore(HollowConsumer.AnnouncementRetriever announcementRetriever) {
        long start = currentTimeMillis();
        try {
            long version = announcementRetriever.get();
            if(version != Long.MIN_VALUE) {
                HollowConsumer.ReadState readState = toReadState(version);
                writeEngine.restoreFrom(readState.getStateEngine());
                announced = new Transition(readState.getVersion());
                listeners.fireProducerRestore(announced.getToVersion(), currentTimeMillis() - start);
            } else {
                // TODO: timt: notify listeners
                System.out.println("RESTORE UNAVAILABLE; PRODUCING NEW DELTA CHAIN");
            }
        } catch(Exception ex) {
            // TODO: timt: notify listeners
            ex.printStackTrace();
            System.out.println("RESTORE UNAVAILABLE; PRODUCING NEW DELTA CHAIN");
        }
        return this;
    }

    /**
     * Each cycle produces a single data state.
     */
    public void runCycle(Populator task) {
        WriteState writeState = null;
        long start = currentTimeMillis();
        ProducerStatus cycleStatus = ProducerStatus.unknownFailure();
        long mintedVersion = Long.MIN_VALUE;
        try {
            mintedVersion = versionMinter.mint();
            listeners.fireCycleStart(mintedVersion);
            Transition transition = announced.advance(mintedVersion);
            writeState = beginCycle(transition);
            task.populate(writeState);
            if(writeEngine.hasChangedSinceLastCycle()) {
                publish(transition, writeState);
                HollowConsumer.ReadState readState = checkIntegrity(transition, writeState);
                validate(readState);
                announce(readState);
                announced = transition;
                cycleStatus = ProducerStatus.success(readState);
            } else {
                writeEngine.resetToLastPrepareForNextCycle();
                listeners.fireNoDelta(writeState.getVersion());
                cycleStatus = ProducerStatus.success(writeState.getVersion());
            }
        } catch(Throwable th) {
            th.printStackTrace();
            writeEngine.resetToLastPrepareForNextCycle();
            cycleStatus = ProducerStatus.fail(mintedVersion, th);
        } finally {
            listeners.fireCycleComplete(cycleStatus, start);
        }
    }

    public void addListener(HollowProducerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    private WriteState beginCycle(Transition transition) {
        writeEngine.prepareForNextCycle();
        WriteState writeState = new WriteStateImpl(objectMapper, transition);
        return writeState;
    }

    private void publish(Transition transition, WriteState writeState) throws IOException {
        long start = listeners.firePublishStart(writeState.getVersion());
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);

        Blob snapshot = publisher.openSnapshot(transition);
        ProducerStatus status = ProducerStatus.unknownFailure();
        try {
            writer.writeSnapshot(snapshot.getOutputStream());

            if(transition.isDelta()) {
                Blob delta = publisher.openDelta(transition);
                try {
                    writer.writeDelta(delta.getOutputStream());
                    publisher.publish(delta);
                } finally {
                    delta.close();
                }
                Blob reverseDelta = publisher.openReverseDelta(transition);
                try {
                    writer.writeReverseDelta(reverseDelta.getOutputStream());
                    publisher.publish(reverseDelta);
                } finally {
                    reverseDelta.close();
                }
            }

            /// it's ok to fail to publish a snapshot, as long as you don't miss too many in a row.
            /// you can add a timeout or even do this in a separate thread.
            try {
                publisher.publish(snapshot);
            } catch(Throwable ignored) {
                ignored.printStackTrace(); // TODO: timt: log and notify listerners
            }
            status = ProducerStatus.success(writeState.getVersion());
        } catch(Throwable th) {
            status = ProducerStatus.fail(writeState.getVersion(), th);
            throw th;
        } finally {
            listeners.firePublishComplete(status, start);
            snapshot.close();
        }
    }

    /**
     *  Given
     *
     *  1. read state (S1) at the previous announced version
     *  2. read state (S2) from the currently produced snapshot
     *
     *  Ensure:
     *
     *  S1.apply(forward delta).checksum == S2.checksum
     *  S2.apply(reverse delta).checksum == S1.checksum
     *
     * @param transition
     * @param writeState
     * @return
     */
    private HollowConsumer.ReadState checkIntegrity(Transition transition, WriteState writeState) {
        long start = listeners.fireIntegrityCheckStart(writeState);
        ProducerStatus status = ProducerStatus.unknownFailure();
        try {
            final HollowConsumer.ReadState result;

            // TODO: timt: use HollowConsumer
            if(transition.isDelta()) {
                long desiredVersion = transition.getFromVersion();
                @SuppressWarnings("unused")
                HollowConsumer.ReadState fromReadState = toReadState(desiredVersion);
                // FIXME: timt: do the integrity checks, leaving (S2) assigned to `toReadState`

                result = readSnapshot(transition);
            } else if(transition.isSnapshot()) {
                result = readSnapshot(transition);
            } else {
                throw new IllegalStateException("no blobs to check");
            }

            status = ProducerStatus.success(result);
            return result;
        } catch(Throwable th) {
            status = ProducerStatus.fail(writeState.getVersion(), th);
            throw th;
        } finally {
            listeners.fireIntegrityCheckComplete(status, start);
        }
    }

    /// TODO: timt: validator API TBD
    private void validate(HollowConsumer.ReadState readState) {
        long start = listeners.fireValidationStart(readState);
        ProducerStatus status = ProducerStatus.unknownFailure();
        try {
            validator.validate(readState);
            status = ProducerStatus.success(readState);
        } catch (Throwable th) {
            status = ProducerStatus.fail(readState, th);
            throw th;
        } finally {
            listeners.fireValidationComplete(status, start);
        }
    }

    private void announce(HollowConsumer.ReadState readState) {
        long start = listeners.fireAnnouncementStart(readState);
        ProducerStatus status = ProducerStatus.unknownFailure();
        try {
            announcer.announce(readState.getVersion());
            status = ProducerStatus.success(readState);
        } catch(Throwable th) {
            status = ProducerStatus.fail(readState, th);
            throw th;
        } finally {
            listeners.fireAnnouncementComplete(status, start);
        }
    }

    public static interface VersionMinter {
        /**
         * Create a new state version.<p>
         *
         * State versions should be ascending -- later states have greater versions.<p>
         *
         * @return a new state version
         */
        long mint();
    }

    public static interface Populator {
        void populate(HollowProducer.WriteState newState);
    }

    public static interface WriteState {
        int add(Object o);

        HollowObjectMapper getObjectMapper();

        HollowWriteStateEngine getStateEngine();

        long getVersion();
    }

    public static interface Publisher {
        public HollowProducer.Blob openSnapshot(Transition transition);
        public HollowProducer.Blob openDelta(Transition transition);
        public HollowProducer.Blob openReverseDelta(Transition transition);

        public void publish(HollowProducer.Blob blob);
    }

    public static interface Blob extends Closeable {
        OutputStream getOutputStream();

        // TODO: timt: belongs in HollowConsumer.Blob
        InputStream getInputStream();

        @Override
        void close();
    }

    public static interface Validator {
        void validate(HollowConsumer.ReadState readState);
    }

    public static interface Announcer {
        public void announce(long stateVersion);
    }

    /**
     * Immutable class representing a single point along the delta chain.
     *
     * @author Tim Taylor {@literal<timt@netflix.com>}
     */
    public static final class Transition {

        private final long fromVersion;
        private final long toVersion;

        /**
         * Creates a null transition.
         *
         * A producer would use this to avoid null checks when beginning a new delta chain; calling
         * {@link #advance(long)} will return a transition representing the first snapshot in
         * a chain.<p>
         *
         * Consumers cannot initialize their read state from this transition.<p>
         *
         * @see <a href="http://hollow.how/advanced-topics/#double-snapshots">Double Snapshot</a>
         */
        public Transition() {
            this(Long.MIN_VALUE, Long.MIN_VALUE);
        }

        /**
         * Creates a transition capable of being used to restore from a delta chain at
         * the specified version, a.k.a. a snapshot.<p>
         *
         * Consumers can initialize their read state from a snapshot corresponding to
         * this transition; an already initialized consumer can only utilize
         * this by performing a double snapshot.<p>
         *
         * A producer would use this transition to restore from a previous announced state in order
         * to resume producing on that delta chain by calling {@link #advance(long)} when ready to
         * produce the next state.
         *
         * @see <a href="http://hollow.how/advanced-topics/#double-snapshots">Double Snapshot</a>
         */
        public Transition(long toVersion) {
            this(Long.MIN_VALUE, toVersion);
        }

        /**
         * Creates a transition fully representing a transition within the delta chain, a.k.a. a delta, between
         * {@code fromVersion} and {@code toVersion}.
         */
        public Transition(long fromVersion, long toVersion) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
        }

        /**
         * Returns a new transition representing the transition from this state's {@code toVersion} to the specified version;
         * equivalent to calling {@code new StateTransition(this.toVersion, nextVersion)}.
         *
         * <pre>
         * <code>
         * [13,45].advance(72) == [45,72]
         * </code>
         * </pre>
         *
         * @param nextVersion the next version to transition to
         *
         * @return a new state transition with its {@code fromVersion} and {@code toVersion} assigned our {@code toVersion} and
         *     the specified {@code nextVersion} respectively
         */
        public Transition advance(long nextVersion) {
            return new Transition(toVersion, nextVersion);
        }

        /**
         * Returns a new transition with versions swapped. Only valid on deltas.

         * <pre>
         * <code>
         * [13,45].reverse() == [45,13]
         * </code>
         * </pre>

         * @return
         *
         * @throws IllegalStateException if this transition isn't a delta
         */
        public Transition reverse() {
            if(isDiscontinous() || isSnapshot()) throw new IllegalStateException("must be a delta");
            return new Transition(this.toVersion, this.fromVersion);
        }

        public long getFromVersion() {
            return fromVersion;
        }

        public long getToVersion() {
            return toVersion;
        }

        /**
         * Determines whether this transition represents a new or broken delta chain.
         *
         * @return true if this has neither a {@code fromVersion} nor a {@code toVersion}; false otherwise.
         */
        public boolean isDiscontinous() {
            return fromVersion == Long.MIN_VALUE && toVersion == Long.MIN_VALUE;
        }

        /**
         * Determines whether this state represents a delta, e.g. a transition between two state versions.
         *
         * @return true if this has a {@code fromVersion} and {@code toVersion}; false otherwise
         */
        public boolean isDelta() {
            return fromVersion != Long.MIN_VALUE && toVersion != Long.MIN_VALUE;
        }

        public boolean isForwardDelta() {
            return isDelta() && fromVersion < toVersion;
        }

        public boolean isReverseDelta() {
            return isDelta() && fromVersion > toVersion;
        }

        public boolean isSnapshot() {
            return !isDiscontinous() && !isDelta();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if(isDiscontinous()) {
                sb.append("new/broken delta chain");
            } else if(isDelta()) {
                if(isReverseDelta()) sb.append("reverse");
                sb.append("delta [");
                sb.append(fromVersion);
                if(isForwardDelta()) sb.append(" -> ");
                else sb.append(" <- ");
                sb.append(toVersion);
                sb.append("]");
            } else {
                sb.append("snapshot [");
                sb.append(toVersion);
                sb.append("]");
            }
            return sb.toString();
        }

    }

    ///// TODO: timt: move to HollowConsumer API ////
    private HollowConsumer.ReadState toReadState(long desiredVersion) {
        HollowClient client = new HollowClient(blobRetriever);
        client.triggerRefreshTo(desiredVersion);
        long actualVersion = client.getCurrentVersionId();
        if(desiredVersion != actualVersion) throw new IllegalStateException(String.format("desiredVersion=%d actualVersion=%d", desiredVersion, actualVersion));
        HollowConsumer.ReadState readState = new ReadStateImpl(client);
        return readState;
    }

    private HollowConsumer.ReadState readSnapshot(Transition transition) {
        final HollowConsumer.ReadState readState;
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine, new HollowBlobHeaderReader());
        Blob snapshot = publisher.openSnapshot(transition);
        try {
            reader.readSnapshot(snapshot.getInputStream());
            readState = new ReadStateImpl(transition.getToVersion(), readEngine);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            snapshot.close();
        }
        return readState;
    }
    ///// END TODO ////

}
