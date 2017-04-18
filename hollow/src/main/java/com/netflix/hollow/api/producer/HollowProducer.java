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
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.DELTA;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.REVERSE_DELTA;
import static java.lang.System.currentTimeMillis;

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.ReadState;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.PublishStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.checksum.HollowChecksum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

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

    private final Logger log = Logger.getLogger(HollowProducer.class.getName());
    private final Publisher publisher;
    private final Validator validator;
    private final Announcer announcer;
    private HollowObjectMapper objectMapper;
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

        objectMapper = new HollowObjectMapper(new HollowWriteStateEngine());
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
    protected HollowWriteStateEngine getWriteEngine() {
        return objectMapper.getStateEngine();
    }

    public void initializeDataModel(HollowSchema... schemas) {
        long start = currentTimeMillis();
        HollowWriteStateCreator.populateStateEngineWithTypeWriteStates(getWriteEngine(), Arrays.asList(schemas));
        listeners.fireProducerInit(currentTimeMillis() - start);
    }


    public HollowProducer restore(long versionDesired, HollowBlobRetriever blobRetriever) {
        restoreAndReturnReadState(versionDesired, blobRetriever);
        return this;
    }

    protected HollowConsumer.ReadState restoreAndReturnReadState(long versionDesired, HollowBlobRetriever blobRetriever) {
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

                    // Need to restore data to new ObjectMapper since can't restore to non empty Write State Engine
                    HollowObjectMapper newObjectMapper = createNewHollowObjectMapperFromExisting(objectMapper);
                    newObjectMapper.getStateEngine().restoreFrom(readStates.current().getStateEngine());
                    status = RestoreStatus.success(versionDesired, readState.getVersion());
                    objectMapper = newObjectMapper; // Restore completed successfully so swap
                } else {
                    status = RestoreStatus.fail(versionDesired, readState.getVersion(), null);
                }
            }
        } catch(Throwable th) {
            status = RestoreStatus.fail(versionDesired, readState != null ? readState.getVersion() : Long.MIN_VALUE, th);
        } finally {
            listeners.fireProducerRestoreComplete(status, currentTimeMillis() - start);
        }
        return readState;
    }

    private static HollowObjectMapper createNewHollowObjectMapperFromExisting(HollowObjectMapper objectMapper) {
        Collection<HollowSchema> schemas = objectMapper.getStateEngine().getSchemas();
        HollowWriteStateEngine writeEngine = HollowWriteStateCreator.createWithSchemas(schemas);
        return new HollowObjectMapper(writeEngine);
    }

    /**
     * Each cycle produces a single data state.
     */
    public void runCycle(Populator task) {
        long toVersion = versionMinter.mint();

        if(!readStates.hasCurrent()) listeners.fireNewDeltaChain(toVersion);
        ProducerStatus.Builder cycleStatus = listeners.fireCycleStart(toVersion);

        try {
            runCycle(task, cycleStatus, toVersion);
        } finally {
            listeners.fireCycleComplete(cycleStatus);
        }
    }

    protected void runCycle(Populator task, ProducerStatus.Builder cycleStatus, long toVersion) {
        // 1. Begin a new cycle
        Artifacts artifacts = new Artifacts();
        HollowWriteStateEngine writeEngine = getWriteEngine();
        try {
            // 1a. Prepare the write state
            writeEngine.prepareForNextCycle();
            WriteState writeState = new WriteStateImpl(toVersion, objectMapper, readStates.current());

            // 2. Populate the state
            ProducerStatus.Builder populateStatus = listeners.firePopulateStart(toVersion);
            try {
                task.populate(writeState);
                populateStatus.success();
            } catch (Throwable th) {
                populateStatus.fail(th);
                throw th;
            } finally {
                listeners.firePopulateComplete(populateStatus);
            }


            // 3. Produce a new state if there's work to do
            if(writeEngine.hasChangedSinceLastCycle()) {
                // 3a. Publish, run checks & validation, then announce new state consumers
                publish(writeState, artifacts);

                ReadStateHelper candidate = readStates.roundtrip(writeState);
                cycleStatus.version(candidate.pending());
                candidate = checkIntegrity(candidate, artifacts);

                validate(candidate.pending());

                announce(candidate.pending());
                readStates = candidate.commit();
                cycleStatus.version(readStates.current()).success();
            } else {
                // 3b. Nothing to do; reset the effects of Step 2
                writeEngine.resetToLastPrepareForNextCycle();
                listeners.fireNoDelta(cycleStatus.success());
            }
        } catch(Throwable th) {
            writeEngine.resetToLastPrepareForNextCycle();
            cycleStatus.fail(th);
        } finally {
            artifacts.cleanup();
        }
    }

    public void addListener(HollowProducerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    private void publish(WriteState writeState, Artifacts artifacts) throws IOException {
        ProducerStatus.Builder psb = listeners.firePublishStart(writeState.getVersion());
        try {
            if (readStates.hasCurrent()) {
                publishBlob(writeState, artifacts, Blob.Type.DELTA);
                publishBlob(writeState, artifacts, Blob.Type.REVERSE_DELTA);
            }
            publishBlob(writeState, artifacts, Blob.Type.SNAPSHOT);
            psb.success();

        } catch (Throwable throwable) {
            psb.fail(throwable);
            throw throwable;
        } finally {
            listeners.firePublishComplete(psb);
        }
    }

    private void publishBlob(WriteState writeState, Artifacts artifacts, Blob.Type blobType) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(getWriteEngine());
        PublishStatus.Builder builder = (new PublishStatus.Builder());
        try {
            switch (blobType) {
                case SNAPSHOT:
                    artifacts.snapshot = publisher.openSnapshot(writeState.getVersion());
                    artifacts.snapshot.write(writer);
                    builder.blob(artifacts.snapshot);
                    publisher.publish(artifacts.snapshot, writeState.getStateEngine().getHeaderTags());
                    break;
                case DELTA:
                    artifacts.delta = publisher.openDelta(readStates.current().getVersion(), writeState.getVersion());
                    artifacts.delta.write(writer);
                    builder.blob(artifacts.delta);
                    publisher.publish(artifacts.delta, writeState.getStateEngine().getHeaderTags());
                    break;
                case REVERSE_DELTA:
                    artifacts.reverseDelta = publisher.openReverseDelta(readStates.current().getVersion(), writeState.getVersion());
                    artifacts.reverseDelta.write(writer);
                    builder.blob(artifacts.reverseDelta);
                    publisher.publish(artifacts.reverseDelta, writeState.getStateEngine().getHeaderTags());
                    break;
                default:
                    throw new IllegalStateException("unknown type, type=" + blobType);
            }
            builder.success();

        } catch (Throwable th) {
            builder.fail(th);
            throw th;
        } finally {
            listeners.fireArtifactPublish(builder);
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
     * @return updated read states
     */
    private ReadStateHelper checkIntegrity(ReadStateHelper readStates, Artifacts artifacts) throws Exception {
        ProducerStatus.Builder status = listeners.fireIntegrityCheckStart(readStates.pending());
        try {
            ReadStateHelper result = readStates;
            HollowReadStateEngine current = readStates.hasCurrent() ? readStates.current().getStateEngine() : null;
            HollowReadStateEngine pending = readStates.pending().getStateEngine();
            readSnapshot(artifacts.snapshot, pending);

            if(readStates.hasCurrent()) {
                log.info("CHECKSUMS");
                HollowChecksum currentChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                log.info("  CUR        " + currentChecksum);

                HollowChecksum pendingChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                log.info("         PND " + pendingChecksum);

                if(artifacts.hasDelta()) {
                    // FIXME: timt: future cycles will fail unless this delta validates *and* we have a reverse
                    // delta *and* it also validates
                    applyDelta(artifacts.delta, current);
                    HollowChecksum forwardChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                    //out.format("  CUR => PND %s\n", forwardChecksum);
                    if(!forwardChecksum.equals(pendingChecksum)) throw new ChecksumValidationException(DELTA);
                }

                if(artifacts.hasReverseDelta()) {
                    applyDelta(artifacts.reverseDelta, pending);
                    HollowChecksum reverseChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                    //out.format("  CUR <= PND %s\n", reverseChecksum);
                    if(!reverseChecksum.equals(currentChecksum)) throw new ChecksumValidationException(REVERSE_DELTA);
                    result = readStates.swap();
                }
            }
            status.success();
            return result;
        } catch(Throwable th) {
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
        InputStream is = blob.newInputStream();
        try {
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).readSnapshot(is);
        } finally {
            is.close();
        }
    }

    private void applyDelta(Blob blob, HollowReadStateEngine stateEngine) throws IOException {
        InputStream is = blob.newInputStream();
        try {
            new HollowBlobReader(stateEngine, new HollowBlobHeaderReader()).applyDelta(is);
        } finally {
            is.close();
        }
    }

    private void validate(HollowConsumer.ReadState readState) {
        ProducerStatus.Builder status = listeners.fireValidationStart(readState);
        try {
            validator.validate(readState);
            status.success();
        } catch (Throwable th) {
            status.fail(th);
            throw th;
        } finally {
            listeners.fireValidationComplete(status);
        }
    }

    private void announce(HollowConsumer.ReadState readState) {
        ProducerStatus.Builder status = listeners.fireAnnouncementStart(readState);
        try {
            announcer.announce(readState.getVersion());
            status.success();
        } catch(Throwable th) {
            status.fail(th);
            throw th;
        } finally {
            listeners.fireAnnouncementComplete(status);
        }
    }

    public interface VersionMinter {
        /**
         * Create a new state version.<p>
         *
         * State versions should be ascending -- later states have greater versions.<p>
         *
         * @return a new state version
         */
        long mint();
    }

    public interface Populator {
        void populate(HollowProducer.WriteState newState) throws Exception;
    }

    public interface WriteState {
        int add(Object o);

        HollowObjectMapper getObjectMapper();

        HollowWriteStateEngine getStateEngine();

        ReadState getPriorState();

        long getVersion();
    }

    public interface Blob {
        /**
         * Hollow blob types are {@code SNAPSHOT}, {@code DELTA} and {@code REVERSE_DELTA}.
         */
        enum Type {
            SNAPSHOT("snapshot"),
            DELTA("delta"),
            REVERSE_DELTA("reversedelta");

            public final String prefix;

            Type(String prefix) {
                this.prefix = prefix;
            }
        }

        void write(HollowBlobWriter writer) throws IOException;

        InputStream newInputStream() throws IOException;

        Type getType();

        long getFromVersion();

        long getToVersion();

        void cleanup();
    }

    public interface Publisher {
        Blob openSnapshot(long version);

        Blob openDelta(long fromVersion, long toVersion);

        Blob openReverseDelta(long fromVersion, long toVersion);

        void publish(Blob blob, Map<String, String> headerTags);
    }

    public interface Validator {
        void validate(HollowConsumer.ReadState readState);
    }

    public interface Announcer {
        void announce(long stateVersion);
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

        boolean hasDelta() {
            return delta != null;
        }

        public boolean hasReverseDelta() {
            return reverseDelta != null;
        }
    }
}
