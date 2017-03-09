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
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.SNAPSHOT;
import static java.lang.System.currentTimeMillis;

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.PublishStatus;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.checksum.HollowChecksum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

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

    public static WriteState newWriteState(long version, HollowObjectMapper objectMapper) {
        return new WriteStateImpl(version, objectMapper);
    }

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
    
    public void initializeDataModel(HollowSchema... schemas) {
        long start = currentTimeMillis();
        HollowWriteStateCreator.populateStateEngineWithTypeWriteStates(writeEngine, Arrays.asList(schemas));
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
        // 1. Begin a new cycle
        long toVersion = versionMinter.mint();
        if(!readStates.hasCurrent()) listeners.fireNewDeltaChain(toVersion);
        ProducerStatus.Builder cycleStatus = listeners.fireCycleStart(toVersion);
        Artifacts artifacts = new Artifacts();
        try {
            // 1a. Prepare the write state
            writeEngine.prepareForNextCycle();
            HollowObjectMapper objectMapper = this.objectMapper;
            WriteState writeState = newWriteState(toVersion, objectMapper);

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
            listeners.fireCycleComplete(cycleStatus);
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
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
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
                System.out.println("CHECKSUMS");
                HollowChecksum currentChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                //out.format("  CUR        %s\n", currentChecksum);

                HollowChecksum pendingChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                //out.format("         PND %s\n", pendingChecksum);

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

    public static abstract class Publisher {

        protected String namespace;
        protected String dir;

        /**
         * Constructor to create a new Publisher with namespace &amp; disk path for Hollow blobs.
         *
         * @param namespace typically project namespace
         * @param dir       directory to use to write hollow blob files.
         */
        public Publisher(String namespace, String dir) {
            this.namespace = namespace;
            this.dir = dir;
        }

        /**
         * Returns a blob with which a {@code HollowProducer} will write a snapshot for the version specified.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob, Map)}.
         *
         * @param version the blob version
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openSnapshot(long version) {
            return Blob.withNamespace(namespace, Long.MIN_VALUE, version, dir, SNAPSHOT);
        }

        /**
         * Returns a blob with which a {@code HollowProducer} will write a forward delta from the version specified to
         * the version specified, i.e. {@code fromVersion => toVersion}.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob, Map)}.
         *
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion the data state this delta will transition from
         * @param toVersion the data state this delta will transition to
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openDelta(long fromVersion, long toVersion) {
            return Blob.withNamespace(namespace, fromVersion, toVersion, dir, DELTA);
        }

        /**
         * Returns a blob with which a {@code HollowProducer} will write a reverse delta from the version specified to
         * the version specified, i.e. {@code fromVersion <= toVersion}.<p>
         *
         * The producer will pass the returned blob back to this publisher when calling {@link Publisher#publish(Blob, Map)}.
         *
         * In the delta chain {@code fromVersion} is the older version such that {@code fromVersion < toVersion}.
         *
         * @param fromVersion version in the delta chain immediately before {@code toVersion}
         * @param toVersion version in the delta chain immediately after {@code fromVersion}
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion) {
            return Blob.withNamespace(namespace, fromVersion, toVersion, dir, REVERSE_DELTA);
        }

        /**
         * Publish the blob specified to this publisher's blobstore.<p>
         *
         * It is guaranteed that {@code blob} was created by calling one of
         * {@link Publisher#openSnapshot(long)}, {@link Publisher#openDelta(long,long)}, or
         * {@link Publisher#openReverseDelta(long,long)} on this publisher.
         *
         * @param blob the blob to publish
         * @param headerTags the header tags, in case these should be added as metadata on published artifacts.
         */
        public abstract void publish(HollowProducer.Blob blob, Map<String, String> headerTags);
    }

    public static class Blob {

        protected final String namespace;
        protected final long fromVersion;
        protected final long toVersion;
        protected final String dir;
        protected final Blob.Type type;
        protected final File file;

        static Blob withNamespace(String namespace, long fromVersion, long toVersion, String dir, Blob.Type type) {
            return new Blob(namespace, fromVersion, toVersion, dir, type);
        }

        private Blob(String namespace, long fromVersion, long toVersion, String dir, Blob.Type type) {
            this.namespace = namespace;
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.type = type;
            this.dir = dir;

            switch (type) {
                case SNAPSHOT:
                    this.file = new File(dir, String.format("%s-%s-%d", namespace, type.prefix, toVersion));
                    break;
                case DELTA:
                    this.file = new File(dir, String.format("%s-%s-%d-%d", namespace, type.prefix, fromVersion, toVersion));
                    break;
                case REVERSE_DELTA:
                    this.file = new File(dir, String.format("%s-%s-%d-%d", namespace, type.prefix, toVersion, fromVersion));
                    break;
                default:
                    throw new IllegalStateException("unknown blob type, type=" + type);
            }
        }

        protected void write(HollowBlobWriter writer) throws IOException {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                switch (type) {
                    case SNAPSHOT:
                        writer.writeSnapshot(os);
                        break;
                    case DELTA:
                        writer.writeDelta(os);
                        break;
                    case REVERSE_DELTA:
                        writer.writeReverseDelta(os);
                        break;
                    default:
                        throw new IllegalStateException("unknown type, type=" + type);
                }
            }
        }

        protected InputStream newInputStream() throws IOException {
            return new BufferedInputStream(new FileInputStream(this.file));
        }

        public File getFile() {
            return this.file;
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

        public void cleanup() {
            if (this.file != null) this.file.delete();
        }

        /**
         * Hollow blob types are {@code SNAPSHOT}, {@code DELTA} and {@code REVERSE_DELTA}.
         */
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

        boolean hasDelta() {
            return delta != null;
        }

        public boolean hasReverseDelta() {
            return reverseDelta != null;
        }
    }
}
