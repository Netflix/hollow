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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.ReadState;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
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
        public void validate(ReadState readState) {}
    };

    private final VersionMinter versionMinter;
    private final Publisher publisher;
    private final Validator validator;
    private final Announcer announcer;
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;
    private final ListenerSupport listeners;

    private Transition announced = null;

    public HollowProducer(HollowProducer.Publisher publisher,
            HollowProducer.Announcer announcer ) {
        this(new VersionMinterWithCounter(), publisher, NO_VALIDATIONS, announcer);
    }

    public HollowProducer(HollowProducer.Publisher publisher,
            HollowProducer.Validator validator,
            HollowProducer.Announcer announcer ) {
        this(new VersionMinterWithCounter(), publisher, validator, announcer);
    }

    private HollowProducer(HollowProducer.VersionMinter versionMinter,
            HollowProducer.Publisher publisher,
            HollowProducer.Validator validator,
            HollowProducer.Announcer announcer) {
        this.versionMinter = versionMinter;
        this.publisher = publisher;
        this.validator = validator;
        this.announcer = announcer;

        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        listeners = new ListenerSupport();
    }

    public void initializeDataModel(Class<?>...classes) {
        for(Class<?> c : classes)
            objectMapper.initializeTypeState(c);
        listeners.fireProducerInit();
    }

    public HollowProducer restore(HollowConsumer.AnnouncementRetriever announcementRetriever,
            HollowBlobRetriever blobRetriever) {
        try {
            long stateVersion = announcementRetriever.get();
            if(stateVersion != Long.MIN_VALUE) {
                // TODO: timt: use HollowConsumer
                HollowClient client = new HollowClient(blobRetriever);
                client.triggerRefreshTo(stateVersion);
                // FIXME: timt: should fail if we didn't make it to the announced version
                restoreFrom(client.getStateEngine(), client.getCurrentVersionId());
                listeners.fireProducerRestore(announced.getToVersion());
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

    private HollowProducer restoreFrom(HollowReadStateEngine priorAnnouncedState, long priorAnnouncedVersion) {
        writeEngine.restoreFrom(priorAnnouncedState);
        announced = new Transition(priorAnnouncedVersion);
        return this;
    }

    /**
     * Each cycle produces a single state.
     */
    public void runCycle(Populator task) {
        WriteState writeState = null;
        ProducerStatus cycleStatus = ProducerStatus.unknownFailure();
        try {
            HollowConsumer.ReadState readState = null;
            Transition transition = announced.advance(versionMinter.mint());
            writeState = beginCycle(transition);
            task.populate(writeState);
            if(writeEngine.hasChangedSinceLastCycle()) {
                publish(transition, writeState);
                readState = integrityCheck(writeState);
                validate(writeState.getVersion(), readState);
                announce(writeState, readState);
                announced = transition;
            } else {
                writeEngine.resetToLastPrepareForNextCycle();
                listeners.fireNoDelta(writeState.getVersion());
            }
            cycleStatus = ProducerStatus.success(writeState.getVersion(), readState);
        } catch(Throwable th) {
            th.printStackTrace();
            rollback();
            if(writeState != null) cycleStatus = ProducerStatus.fail(writeState.getVersion(), th);
        } finally {
            listeners.fireCycleComplete(cycleStatus);
        }
    }

    public void addListener(HollowProducerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    private WriteState beginCycle(Transition transition) {
        listeners.fireCycleStart(transition.getToVersion());
        writeEngine.prepareForNextCycle();
        WriteState writeState = new WriteStateImpl(objectMapper, transition);
        return writeState;
    }

    private void publish(Transition transition, WriteState writeState) throws IOException {
        long version = writeState.getVersion();
        listeners.firePublishStart(version);
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);

        Blob snapshot = publisher.openSnapshot(transition);
        ProducerStatus publishStatus = ProducerStatus.unknownFailure();
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
            publishStatus = ProducerStatus.success(version);
        } catch(Throwable th) {
            publishStatus = ProducerStatus.fail(version, th);
            throw th;
        } finally {
            listeners.firePublishComplete(publishStatus);
            snapshot.close();
        }
    }

    private ReadState integrityCheck(WriteState writeState) {
        /// Given
        ///
        /// 1. read state (S1) at the previous announced version
        /// 2. read state (S2) from the currently produced snapshot
        ///
        /// Ensure:
        ///
        /// S1.apply(forward delta).checksum == S2.checksum
        /// S2.apply(reverse delta).checksum == S1.checksum
        long version = writeState.getVersion();
        listeners.fireIntegrityCheckStart(version);
        ProducerStatus integrityCheckStatus = ProducerStatus.unknownFailure();
        try {
            ReadState fromReadState = null;
            ReadState toReadState = null;

            // FIXME: timt: do the integrity checks, leaving (S2) assigned to `toReadState`

            integrityCheckStatus = ProducerStatus.success(version, toReadState);
            return null;
        } catch(Throwable th) {
            integrityCheckStatus = ProducerStatus.fail(version, th);
            throw th;
        } finally {
            listeners.fireIntegrityCheckComplete(integrityCheckStatus);
        }

    }

    private void validate(long version, ReadState readState) {
        listeners.fireValidationStart(version);
        ProducerStatus validationStatus = ProducerStatus.unknownFailure();
        try {
            validator.validate(null);
            validationStatus = ProducerStatus.success(version, readState);
        } catch (Throwable th) {
            validationStatus = ProducerStatus.fail(version, th);
            throw th;
        } finally {
            listeners.fireValidationComplete(validationStatus);
        }
    }

    private void announce(WriteState writeState, ReadState readState) {
        long version = writeState.getVersion();
        ProducerStatus announcementStatus = ProducerStatus.unknownFailure();
        try {
            listeners.fireAnnouncementStart(version);
            announcer.announce(version);
            announcementStatus = ProducerStatus.success(version, readState);
        } catch(Throwable th) {
            announcementStatus = ProducerStatus.fail(writeState.getVersion(), th);
            throw th;
        } finally {
            listeners.fireAnnouncementComplete(announcementStatus);
        }
    }

    private void rollback() {
        writeEngine.resetToLastPrepareForNextCycle();
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

}
