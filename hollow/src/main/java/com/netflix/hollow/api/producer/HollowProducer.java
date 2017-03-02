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

import static com.netflix.hollow.api.consumer.HollowConsumer.newReadState;
import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
public class HollowProducer {
    public static final Validator NO_VALIDATIONS = new Validator(){
        @Override
        public void validate(HollowConsumer.ReadState readState) {}
    };

    private final Publisher publisher;
    private final Validator validator;
    private final Announcer announcer;
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;
    private final VersionMinter versionMinter;
    private final ListenerSupport listeners;
    private ReadStateHelper readStates;

    public HollowProducer(HollowProducer.Publisher publisher,
            HollowProducer.Announcer announcer) {
        this(publisher, NO_VALIDATIONS, announcer);
    }

    public HollowProducer(
            Publisher publisher,
            Validator validator,
            Announcer announcer) {
        this.publisher = publisher;
        this.validator = validator;
        this.announcer = announcer;

        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        versionMinter = new VersionMinterWithCounter();
        listeners = new ListenerSupport();
        readStates = ReadStateHelper.newDeltaChain();
    }

    public void initializeDataModel(Class<?>...classes) {
        long start = currentTimeMillis();
        for(Class<?> c : classes)
            objectMapper.initializeTypeState(c);
        listeners.fireProducerInit(currentTimeMillis() - start);
    }

    public HollowProducer restore(long versionDesired, HollowBlobRetriever blobRetriever) {
        long start = currentTimeMillis();
        RestoreStatus status = RestoreStatus.unknownFailure();
        HollowConsumer.ReadState readState = null;

        try {
            listeners.fireProducerRestoreStart(versionDesired);
            if(versionDesired != Long.MIN_VALUE) {

                HollowClient client = new HollowClient(blobRetriever);
                client.triggerRefreshTo(versionDesired);
                readState = newReadState(client.getCurrentVersionId(), client.getStateEngine());
                if(readState.getVersion() == versionDesired) {
                    readStates = ReadStateHelper.restored(readState);
                    writeEngine.restoreFrom(readStates.current().getStateEngine());
                    status = RestoreStatus.success(versionDesired, readState.getVersion());
                } else {
                    status = RestoreStatus.fail(versionDesired, readState.getVersion(), null);
                }
            }
        } catch(Throwable th) {
            status = RestoreStatus.fail(versionDesired, readState != null ? readState.getVersion() : Long.MIN_VALUE, th);
        } finally {
            listeners.fireProducerRestoreComplete(status, currentTimeMillis() - start);
        }
        return this;
    }

    /**
     * Each cycle produces a single data state.
     */
    public void runCycle(Populator task) {
        long start = currentTimeMillis();
        ProducerStatus cycleStatus = ProducerStatus.unknownFailure();
        WriteState writeState = null;
        Artifacts artifacts = new Artifacts();
        try {
            writeState = beginCycle();
            task.populate(writeState);
            if(writeEngine.hasChangedSinceLastCycle()) {
                publish(writeState, artifacts);
                ReadStateHelper candidate = readStates.roundtrip(writeState);
                checkIntegrity(candidate);
                validate(candidate.pending());
                announce(candidate.pending());
                readStates = candidate.commit();
                cycleStatus = ProducerStatus.success(readStates.current());
            } else {
                writeEngine.resetToLastPrepareForNextCycle();
                listeners.fireNoDelta(writeState.getVersion());
                cycleStatus = ProducerStatus.success(writeState.getVersion());
            }
        } catch(Throwable th) {
            writeEngine.resetToLastPrepareForNextCycle();
            cycleStatus = ProducerStatus.fail(writeState != null ? writeState.getVersion() : Long.MIN_VALUE, th);
        } finally {
            artifacts.cleanup();
            listeners.fireCycleComplete(cycleStatus, start);
        }
    }

    public void addListener(HollowProducerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    private WriteState beginCycle() {
        long toVersion = versionMinter.mint();
        if(!readStates.hasCurrent()) listeners.fireNewDeltaChain(toVersion);
        listeners.fireCycleStart(toVersion);
        writeEngine.prepareForNextCycle();
        return new WriteStateImpl(objectMapper, toVersion);
    }

    private void publish(WriteState writeState, Artifacts artifacts) throws IOException {
        long start = listeners.firePublishStart(writeState.getVersion());
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);

        ProducerStatus status = ProducerStatus.unknownFailure();
        try {
            // 1. Write the snapshot
            artifacts.snapshot = publisher.openSnapshot(writeState.getVersion());
            OutputStream ssos = artifacts.snapshot.newOutputStream();
            try { writer.writeSnapshot(ssos); } finally { ssos.close(); }

            // 2. Write & publish the deltas; active canaries and tooling receive it sooner
            if(readStates.hasCurrent()) {
                artifacts.delta = publisher.openDelta(readStates.current().getVersion(), writeState.getVersion());
                OutputStream fdos = artifacts.delta.newOutputStream();
                try { writer.writeDelta(fdos); } finally { fdos.close(); }
                publisher.publish(artifacts.delta);

                artifacts.reverseDelta = publisher.openReverseDelta(readStates.current().getVersion(), writeState.getVersion());
                OutputStream rdos = artifacts.reverseDelta.newOutputStream();
                try { writer.writeReverseDelta(rdos); } finally { rdos.close(); }
                publisher.publish(artifacts.reverseDelta);
            }

            // 3. Publish the snapshot
            publisher.publish(artifacts.snapshot);
            status = ProducerStatus.success(writeState.getVersion());
        } catch(Throwable th) {
            status = ProducerStatus.fail(writeState.getVersion(), th);
            throw th;
        } finally {
            listeners.firePublishComplete(status, start);
        }
    }

    /**
     *  Given these read states
     *
     *  * S(cur) at the currently announced version
     *  * S(pnd) at the pending version
     *
     *  Ensure that:
     *
     *  S(cur).apply(forwardDelta).checksum == S(pnd).checksum
     *  S(pnd).apply(reverseDelta).checksum == S(cur).checksum
     *
     * @param readStates
     */
    private void checkIntegrity(ReadStateHelper readStates) throws Exception {
        long start = listeners.fireIntegrityCheckStart(readStates.pendingVersion());
        ProducerStatus status = ProducerStatus.unknownFailure();
        Blob snapshot = null;
        try {
            snapshot = publisher.openSnapshot(readStates.pendingVersion());
            HollowBlobReader reader = new HollowBlobReader(readStates.pending().getStateEngine(), new HollowBlobHeaderReader());
            reader.readSnapshot(snapshot.getInputStream());

            if(readStates.hasCurrent()) {
                // FIXME: timt: do delta integrity checks; we only compare checksums for
                // schemas that are common and unchanged between S(curr) and S(prev)
            }

            status = ProducerStatus.success(readStates.pending());
        } catch(Throwable th) {
            status = ProducerStatus.fail(readStates.pendingVersion(), th);
            throw th;
        } finally {
            listeners.fireIntegrityCheckComplete(status, start);
            if(snapshot != null) snapshot.close();
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

    static interface VersionMinter {
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
        void populate(HollowProducer.WriteState newState) throws Exception;
    }

    public static interface WriteState {
        int add(Object o);

        HollowObjectMapper getObjectMapper();

        HollowWriteStateEngine getStateEngine();

        long getVersion();
    }

    public static interface Publisher {
        /**
         * Returns a blob with which a {@code HollowProducer} will write a snapshot for the version specified.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob)}.
         *
         * @param version the blob version
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openSnapshot(long version);

        /**
         * Returns a blob with which a {@code HollowProducer} will write a forward delta from the version specified to
         * the version specified, i.e. {@code fromVersion => toVersion}.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob)}.
         *
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion the data state this delta will transition from
         * @param toVersion the data state this delta will transition to
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openDelta(long fromVersion, long toVersion);

        /**
         * Returns a blob with which a {@code HollowProducer} will write a reverse delta from the version specified to
         * the version specified, i.e. {@code fromVersion <= toVersion}.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob)}.
         *
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion version in the delta chain immediately before {@code toVersion}
         * @param toVersion version in the delta chain immediately after {@code fromVersion}
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion);

        /**
         * Publish the blob specified to this publisher's blobstore.<p>
         *
         * It is guaranteed that {@code blob} was created by calling one of
         * {@link Publisher#openSnapshot(long)}, {@link Publisher#openDelta(long,long)}, or
         * {@link Publisher#openReverseDelta(long,long)} on this publisher.
         *
         * @param blob the blob to publish
         */
        public void publish(HollowProducer.Blob blob);
    }

    public static interface Blob {
        OutputStream newOutputStream();
        InputStream newInputStream();
        long getFromVersion();
        long getToVersion();
        Type getType();
        void cleanup();

        public static enum Type {
            SNAPSHOT("snapshot"),
            DELTA("delta"),
            REVERSE_DELTA("reversedelta");

            public final String prefix;

            Type(String prefix) {
                this.prefix = prefix;
            }
        }
    }

    public static interface Validator {
        void validate(HollowConsumer.ReadState readState);
    }

    public static interface Announcer {
        public void announce(long stateVersion);
    }

    private static final class Artifacts {
        Blob snapshot = null;
        Blob delta = null;
        Blob reverseDelta = null;

        void cleanup() {
            if(snapshot != null) {
                snapshot.cleanup();
                snapshot = null;
            }
            if(delta != null) {
                delta.cleanup();
                delta = null;
            }
            if(reverseDelta != null) {
                reverseDelta.cleanup();
                reverseDelta = null;
            }
        }

        public boolean hasSnapshot() {
            return snapshot != null;
        }

        boolean hasDelta() {
            return delta != null;
        }

        public boolean hasReverseDelta() {
            return reverseDelta != null;
        }
    }
}
