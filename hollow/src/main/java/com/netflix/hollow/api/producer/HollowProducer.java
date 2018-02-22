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

import static com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE;
import static java.lang.System.currentTimeMillis;

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
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.api.metrics.HollowProducerMetrics;
import com.netflix.hollow.api.producer.HollowProducer.Validator.ValidationException;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.PublishStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.enforcer.BasicSingleProducerEnforcer;
import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemBlobStager;
import com.netflix.hollow.api.producer.validation.AllValidationStatus;
import com.netflix.hollow.api.producer.validation.AllValidationStatus.AllValidationStatusBuilder;
import com.netflix.hollow.api.producer.validation.HollowValidationListener;
import com.netflix.hollow.api.producer.validation.SingleValidationStatus;
import com.netflix.hollow.api.producer.validation.SingleValidationStatus.SingleValidationStatusBuilder;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.tools.compact.HollowCompactor;

/**
 * 
 * A HollowProducer is the top-level class used by producers of Hollow data to populate, publish, and announce data states. 
 * The interactions between the "blob" store and announcement mechanism are defined by this class, and the implementations 
 * of the data publishing and announcing are abstracted in interfaces which are provided to this class.
 * 
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
 * 
 * The following components are injectable, but only an implementation of the HollowProducer.Publisher is 
 * required to be injected, all other components are optional. :     
 * 
 * <dl>
 *      <dt>{@link HollowProducer.Publisher}</dt>
 *      <dd>Implementations of this class define how to publish blob data to the blob store.</dd>
 *      
 *      <dt>{@link HollowProducer.Announcer}</dt>
 *      <dd>Implementations of this class define the announcement mechanism, which is used to track the version of the 
 *          currently announced state.</dd>
 *
 *      <dt>One or more {@link HollowProducer.Validator}</dt>
 *      <dd>Implementations of this class allow for semantic validation of the data contained in a state prior to announcement.
 *          If an Exception is thrown during validation, the state will not be announced, and the producer will be automatically 
 *          rolled back to the prior state.</dd>
 *      
 *      <dt>One or more {@link HollowProducerListener}</dt>
 *      <dd>Listeners are notified about the progress and status of producer cycles throughout the various cycle stages.</dd>
 * 
 *      <dt>A Blob staging directory</dt>
 *      <dd>Before blobs are published, they must be written and inspected/validated.  A directory may be specified as a File to which
 *          these "staged" blobs will be written prior to publish.  Staged blobs will be cleaned up automatically after publish.</dd>
 *          
 *      <dt>{@link HollowProducer.BlobCompressor}</dt>
 *      <dd>Implementations of this class intercept blob input/output streams to allow for compression in the blob store.</dd>
 *      
 *      <dt>{@link HollowProducer.BlobStager}</dt>
 *      <dd>Implementations will define how to stage blobs, if the default behavior of staging blobs on local disk is not desirable.
 *          If a {@link BlobStager} is provided, then neither a blob staging directory or {@link BlobCompressor} should be provided.</dd> 
 *      
 *      <dt>An Executor for publishing snapshots</dt>
 *      <dd>When consumers start up, if the latest announced version does not have a snapshot, they can load an earlier snapshot 
 *          and follow deltas to get up-to-date.  A state can therefore be available and announced prior to the availability of 
 *          the snapshot.  If an Executor is supplied here, then it will be used to publish snapshots.  This can be useful if 
 *          snapshot publishing takes a long time -- subsequent cycles may proceed while snapshot uploads are still in progress.</dd>
 * 
 *      <dt>Number of cycles between snapshots</dt>
 *      <dd>Because snapshots are not necessary for a data state to be announced, they need not be published every cycle.
 *          If this parameter is specified, then a snapshot will be produced only every (n+1)th cycle.</dd>
 *          
 *      <dt>{@link HollowProducer.VersionMinter}</dt>
 *      <dd>Allows for a custom version identifier minting strategy.</dd>
 *      
 *      <dt>Target max type shard size</dt>
 *      <dd>Specify a target max type shard size.  Defaults to 16MB.  See http://hollow.how/advanced-topics/#type-sharding</dd>
 *</dl>
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
public class HollowProducer {

    private static final long DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE = 16L * 1024L * 1024L;

    private final Logger log = Logger.getLogger(HollowProducer.class.getName());
    private final BlobStager blobStager;
    private final Publisher publisher;
    private final List<Validator> validators;
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
    private long lastSucessfulCycle=0;

    private boolean isInitialized;

    public HollowProducer(Publisher publisher,
                          Announcer announcer) {
        this(new HollowFilesystemBlobStager(), publisher, announcer, Collections.<Validator>emptyList(), Collections.<HollowProducerListener>emptyList(), Collections.<HollowValidationListener>emptyList(), new VersionMinterWithCounter(), null, 0, DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE, null, new DummyBlobStorageCleaner(), new BasicSingleProducerEnforcer());
    }

    public HollowProducer(Publisher publisher,
                          Validator validator,
                          Announcer announcer) {
        this(new HollowFilesystemBlobStager(), publisher, announcer, Collections.singletonList(validator), Collections.<HollowProducerListener>emptyList(), Collections.<HollowValidationListener>emptyList(),new VersionMinterWithCounter(), null, 0, DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE, null, new DummyBlobStorageCleaner(), new BasicSingleProducerEnforcer());
    }

    @Deprecated // TOBE cleaned up on Hollow 3
    protected HollowProducer(BlobStager blobStager,
            Publisher publisher,
            Announcer announcer,
            List<Validator> validators,
            List<HollowProducerListener> listeners,
            VersionMinter versionMinter,
            Executor snapshotPublishExecutor,
            int numStatesBetweenSnapshots,
            long targetMaxTypeShardSize) {
        this(blobStager, publisher, announcer, validators, listeners, versionMinter, snapshotPublishExecutor, numStatesBetweenSnapshots, targetMaxTypeShardSize, null);
    }

    @Deprecated // TOBE cleaned up on Hollow 3
    protected HollowProducer(BlobStager blobStager,
            Publisher publisher,
            Announcer announcer,
            List<Validator> validators,
            List<HollowProducerListener> listeners,
            VersionMinter versionMinter,
            Executor snapshotPublishExecutor,
            int numStatesBetweenSnapshots,
            long targetMaxTypeShardSize,
            HollowMetricsCollector<HollowProducerMetrics> metricsCollector) {
        this(blobStager, publisher, announcer, validators, listeners, Collections.<HollowValidationListener>emptyList(), versionMinter, snapshotPublishExecutor, numStatesBetweenSnapshots, targetMaxTypeShardSize, metricsCollector, new DummyBlobStorageCleaner(), new BasicSingleProducerEnforcer());
    }

    @Deprecated // TOBE cleaned up on Hollow 3
    protected HollowProducer(BlobStager blobStager,
            Publisher publisher,
            Announcer announcer,
            List<Validator> validators,
            List<HollowProducerListener> listeners,
            VersionMinter versionMinter,
            Executor snapshotPublishExecutor,
            int numStatesBetweenSnapshots,
            long targetMaxTypeShardSize,
            HollowMetricsCollector<HollowProducerMetrics> metricsCollector, 
            BlobStorageCleaner blobStorageCleaner, 
            SingleProducerEnforcer singleProducerEnforcer) {
    	this(blobStager, publisher, announcer, validators, listeners, Collections.<HollowValidationListener>emptyList(), versionMinter, snapshotPublishExecutor, 
    			numStatesBetweenSnapshots, targetMaxTypeShardSize, metricsCollector, blobStorageCleaner, singleProducerEnforcer);
    }
    
    protected HollowProducer(BlobStager blobStager,
                             Publisher publisher,
                             Announcer announcer,
                             List<Validator> validators,
                             List<HollowProducerListener> listeners,
                             List<HollowValidationListener> validationListeners,
                             VersionMinter versionMinter,
                             Executor snapshotPublishExecutor,
                             int numStatesBetweenSnapshots,
                             long targetMaxTypeShardSize,
                             HollowMetricsCollector<HollowProducerMetrics> metricsCollector, 
                             BlobStorageCleaner blobStorageCleaner, 
                             SingleProducerEnforcer singleProducerEnforcer) {
        this.publisher = publisher;
        this.validators = validators;
        this.announcer = announcer;
        this.versionMinter = versionMinter;
        this.blobStager = blobStager;
        this.singleProducerEnforcer = singleProducerEnforcer;
        this.snapshotPublishExecutor = snapshotPublishExecutor == null ? new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        } : snapshotPublishExecutor;
        this.numStatesBetweenSnapshots = numStatesBetweenSnapshots;

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(targetMaxTypeShardSize);

        this.objectMapper = new HollowObjectMapper(writeEngine);
        this.listeners = new ListenerSupport();
        this.readStates = ReadStateHelper.newDeltaChain();
        this.blobStorageCleaner = blobStorageCleaner;

        for(HollowProducerListener listener : listeners)
            this.listeners.add(listener);
        
        for(HollowValidationListener vallistener: validationListeners){
        	this.listeners.add(vallistener);
        }

        this.metrics = new HollowProducerMetrics();
        this.metricsCollector = metricsCollector;
    }

    /**
     * Returns the metrics for this producer
     */
    public HollowProducerMetrics getMetrics() {
        return this.metrics;
    }

    public void initializeDataModel(Class<?>...classes) {
        long start = currentTimeMillis();
        for(Class<?> c : classes)
            objectMapper.initializeTypeState(c);
        listeners.fireProducerInit(currentTimeMillis() - start);
        
        isInitialized = true;
    }

    public void initializeDataModel(HollowSchema... schemas) {
        long start = currentTimeMillis();
        HollowWriteStateCreator.populateStateEngineWithTypeWriteStates(getWriteEngine(), Arrays.asList(schemas));
        listeners.fireProducerInit(currentTimeMillis() - start);
        
        isInitialized = true;
    }

    public HollowProducer.ReadState restore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
        return restore(versionDesired, blobRetriever, new RestoreAction() {
            @Override
            public void restore(HollowReadStateEngine restoreFrom, HollowWriteStateEngine restoreTo) {
                restoreTo.restoreFrom(restoreFrom);
            }
        });
    }
    
    HollowProducer.ReadState hardRestore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever) {
        return restore(versionDesired, blobRetriever, new RestoreAction() {
            @Override
            public void restore(HollowReadStateEngine restoreFrom, HollowWriteStateEngine restoreTo) {
                HollowWriteStateCreator.populateUsingReadEngine(restoreTo, restoreFrom);
            }
        });
    }
    
    private static interface RestoreAction {
        void restore(HollowReadStateEngine restoreFrom, HollowWriteStateEngine restoreTo);
    }
    
    private HollowProducer.ReadState restore(long versionDesired, HollowConsumer.BlobRetriever blobRetriever, RestoreAction restoreAction) {
        if(!isInitialized)
            throw new IllegalStateException("You must initialize the data model of a HollowProducer with producer.initializeDataModel(...) prior to restoring");
        
        long start = currentTimeMillis();
        RestoreStatus status = RestoreStatus.unknownFailure();
        ReadState readState = null;

        try {
            listeners.fireProducerRestoreStart(versionDesired);
            if(versionDesired != Long.MIN_VALUE) {

                HollowConsumer client = HollowConsumer.withBlobRetriever(blobRetriever).build();
                client.triggerRefreshTo(versionDesired);
                if(client.getCurrentVersionId() == versionDesired) {
                    readState = ReadStateHelper.newReadState(client.getCurrentVersionId(), client.getStateEngine());
                    readStates = ReadStateHelper.restored(readState);

                    // Need to restore data to new ObjectMapper since can't restore to non empty Write State Engine
                    HollowObjectMapper newObjectMapper = createNewHollowObjectMapperFromExisting(objectMapper);
                    
                    restoreAction.restore(readStates.current().getStateEngine(), newObjectMapper.getStateEngine());
                    
                    status = RestoreStatus.success(versionDesired, readState.getVersion());
                    objectMapper = newObjectMapper; // Restore completed successfully so swap
                } else {
                    status = RestoreStatus.fail(versionDesired, client.getCurrentVersionId(), null);
                    throw new IllegalStateException("Unable to reach requested version to restore from: " + versionDesired);
                }
            }
        } catch(Throwable th) {
            status = RestoreStatus.fail(versionDesired, readState != null ? readState.getVersion() : Long.MIN_VALUE, th);
            throw th;
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

    protected HollowWriteStateEngine getWriteEngine() {
        return objectMapper.getStateEngine();
    }
    
    protected HollowObjectMapper getObjectMapper() {
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

        if(doEnable) {
            singleProducerEnforcer.enable();
        } else {
            singleProducerEnforcer.disable();
        }
        return (singleProducerEnforcer.isPrimary() == doEnable);
    }

    /**
     * Each cycle produces a single data state.
     * 
     * @return the version identifier of the produced state.
     */
    public long runCycle(Populator task) {
        if(!singleProducerEnforcer.isPrimary()) {
            // TODO: minimum time spacing between cycles
            log.log(Level.INFO, "cycle not executed -- not primary");
            return lastSucessfulCycle;
        }

        long toVersion = versionMinter.mint();

        if(!readStates.hasCurrent()) listeners.fireNewDeltaChain(toVersion);
        ProducerStatus.Builder cycleStatus = listeners.fireCycleStart(toVersion);

        try {
            runCycle(task, cycleStatus, toVersion);
        } finally {
            listeners.fireCycleComplete(cycleStatus);
            metrics.updateCycleMetrics(cycleStatus.build());
            if(metricsCollector !=null)
                metricsCollector.collect(metrics);
        }

        lastSucessfulCycle = toVersion;
        return toVersion;
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
        if(config != null && readStates.hasCurrent()) {
            final HollowCompactor compactor = new HollowCompactor(getWriteEngine(), readStates.current().getStateEngine(), config);
            if(compactor.needsCompaction()) {
                return runCycle(new Populator() {
                    @Override
                    public void populate(WriteState newState) throws Exception {
                        compactor.compact();
                    }
                });
            }
        }
        
        return NO_ANNOUNCEMENT_AVAILABLE;
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

                try {
                    validate(candidate.pending());
    
                    announce(candidate.pending());
    
                    readStates = candidate.commit();
                    cycleStatus.version(readStates.current()).success();
                } catch(Throwable th) {
                    if(artifacts.hasReverseDelta()) {
                        applyDelta(artifacts.reverseDelta, candidate.pending().getStateEngine());
                        readStates = candidate.rollback();
                    }
                    throw th;
                }
            } else {
                // 3b. Nothing to do; reset the effects of Step 2
                writeEngine.resetToLastPrepareForNextCycle();
                listeners.fireNoDelta(cycleStatus.success());
            }
        } catch(Throwable th) {
            writeEngine.resetToLastPrepareForNextCycle();
            cycleStatus.fail(th);
            
            if(th instanceof RuntimeException)
                throw (RuntimeException)th;
            throw new RuntimeException(th);
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

    private void publish(final WriteState writeState, final Artifacts artifacts) throws IOException {
        ProducerStatus.Builder psb = listeners.firePublishStart(writeState.getVersion());
        try {
            stageBlob(writeState, artifacts, Blob.Type.SNAPSHOT);
            
            if (readStates.hasCurrent()) {
                stageBlob(writeState, artifacts, Blob.Type.DELTA);
                stageBlob(writeState, artifacts, Blob.Type.REVERSE_DELTA);
                publishBlob(writeState, artifacts, Blob.Type.DELTA);
                publishBlob(writeState, artifacts, Blob.Type.REVERSE_DELTA);
                
                if(--numStatesUntilNextSnapshot < 0) {
                    snapshotPublishExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                publishBlob(writeState, artifacts, Blob.Type.SNAPSHOT);
                                artifacts.markSnapshotPublishComplete();
                            } catch(IOException e) {
                                log.log(Level.WARNING, "Snapshot publish failed", e);
                            }
                        }
                    });
                    numStatesUntilNextSnapshot = numStatesBetweenSnapshots;
                } else {
                    artifacts.markSnapshotPublishComplete();
                }
            } else {
                publishBlob(writeState, artifacts, Blob.Type.SNAPSHOT);
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
    
    private void stageBlob(WriteState writeState, Artifacts artifacts, Blob.Type blobType) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(getWriteEngine());
        try {
            switch (blobType) {
                case SNAPSHOT:
                    artifacts.snapshot = blobStager.openSnapshot(writeState.getVersion());
                    artifacts.snapshot.write(writer);
                    break;
                case DELTA:
                    artifacts.delta = blobStager.openDelta(readStates.current().getVersion(), writeState.getVersion());
                    artifacts.delta.write(writer);
                    break;
                case REVERSE_DELTA:
                    artifacts.reverseDelta = blobStager.openReverseDelta(writeState.getVersion(), readStates.current().getVersion());
                    artifacts.reverseDelta.write(writer);
                    break;
                default:
                    throw new IllegalStateException("unknown type, type=" + blobType);
            }

        } catch (Throwable th) {
            throw th;
        }
    }

    private void publishBlob(WriteState writeState, Artifacts artifacts, Blob.Type blobType) throws IOException {
        PublishStatus.Builder builder = (new PublishStatus.Builder());
        try {
            switch (blobType) {
                case SNAPSHOT:
                    builder.blob(artifacts.snapshot);
                    publisher.publish(artifacts.snapshot);
                    break;
                case DELTA:
                    builder.blob(artifacts.delta);
                    publisher.publish(artifacts.delta);
                    break;
                case REVERSE_DELTA:
                    builder.blob(artifacts.reverseDelta);
                    publisher.publish(artifacts.reverseDelta);
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
            metrics.updateBlobTypeMetrics(builder.build());
            if(metricsCollector !=null)
                metricsCollector.collect(metrics);
            blobStorageCleaner.clean(blobType);
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
                    if(!artifacts.hasReverseDelta())
                        throw new IllegalStateException("Both a delta and reverse delta are required");
                    
                    // FIXME: timt: future cycles will fail unless both deltas validate
                    applyDelta(artifacts.delta, current);
                    HollowChecksum forwardChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending);
                    //out.format("  CUR => PND %s\n", forwardChecksum);
                    if(!forwardChecksum.equals(pendingChecksum)) throw new ChecksumValidationException(Blob.Type.DELTA);

                    applyDelta(artifacts.reverseDelta, pending);
                    HollowChecksum reverseChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current);
                    //out.format("  CUR <= PND %s\n", reverseChecksum);
                    if(!reverseChecksum.equals(currentChecksum)) throw new ChecksumValidationException(Blob.Type.REVERSE_DELTA);
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

    private void validate(HollowProducer.ReadState readState) {
    	com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus.Builder psb = listeners.fireValidationStart(readState);
    	List<Throwable> exceptions = new ArrayList<>();
    	AllValidationStatusBuilder valStatus = AllValidationStatus.builder();
    	
    	try {
    		for(Validator validator: validators) {
    			Throwable throwable = null;
    			try {
    				validator.validate(readState);
	    		} catch (Throwable th) {
	    			throwable = th;
	    			exceptions.add(th);
	    		}
    			valStatus.addSingelValidationStatus(getValidationStatus(readState, validator, throwable));
    		}
	    	
	    	if(!exceptions.isEmpty()) {
	    		ValidationException valEx = new ValidationException("One or more validations failed. Please check individual failures.", exceptions);
	    		psb.fail(valEx);
	    		valStatus.fail();
	    		throw valEx;
	    	}
	    	psb.success();
	    	valStatus.success();
    	} finally {
    		listeners.fireValidationComplete(psb, valStatus);
    	}
    }

	private SingleValidationStatus getValidationStatus(HollowProducer.ReadState readState, Validator validator, Throwable throwable) {
		String name = (validator instanceof Nameable)? ((Nameable)validator).getName():"";
		SingleValidationStatusBuilder status = SingleValidationStatus.builder(name).withMessage(validator.toString());
		if(throwable != null) {
			status.fail(throwable);
		} else
			status.success();
		return status.build();
	}
    

    private void announce(HollowProducer.ReadState readState) {
        if(announcer != null) {
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
        void populate(HollowProducer.WriteState newState) throws Exception;
    }

    public static interface WriteState {
        int add(Object o);

        HollowObjectMapper getObjectMapper();

        HollowWriteStateEngine getStateEngine();

        ReadState getPriorState();

        long getVersion();
    }
    
    public static interface ReadState {
        public long getVersion();

        public HollowReadStateEngine getStateEngine();
    }
    
    
    public static interface BlobStager {
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
         * @param fromVersion version in the delta chain immediately after {@code toVersion}
         * @param toVersion version in the delta chain immediately before {@code fromVersion}
         *
         * @return a {@link HollowProducer.Blob} representing a snapshot for the {@code version}
         */
        public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion);
    }
    
    public static interface BlobCompressor {
        public static final BlobCompressor NO_COMPRESSION = new BlobCompressor() {
            @Override
            public OutputStream compress(OutputStream os) { return os; }

            @Override
            public InputStream decompress(InputStream is) { return is; }
        };

        /**
         * This method provides an opportunity to wrap the OutputStream used to write the blob (e.g. with a GZIPOutputStream).
         */
        public OutputStream compress(OutputStream is);
        
        /**
         * This method provides an opportunity to wrap the InputStream used to write the blob (e.g. with a GZIPInputStream).
         */
        public InputStream decompress(InputStream is);
    }

    

    public static interface Publisher {

        /**
         * Publish the blob specified to this publisher's blobstore.<p>
         *
         * It is guaranteed that {@code blob} was created by calling one of
         * {@link BlobStager#openSnapshot(long)}, {@link BlobStager#openDelta(long,long)}, or
         * {@link BlobStager#openReverseDelta(long,long)} on this publisher.
         *
         * @param blob the blob to publish
         */
        public abstract void publish(HollowProducer.Blob blob);

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
    
    /**
     * Can be used for implementations that have name.
     * Beta: Could change any time
     * Near future, might be removed.
     * @author lkanchanapalli
     *
     */
    public static interface Nameable {
    	public String getName();
    }
    
    public static interface Validator {
        void validate(HollowProducer.ReadState readState);
    
        @SuppressWarnings("serial")
        public static class ValidationException extends RuntimeException {
            private List<Throwable> individualFailures;
            
            public ValidationException() {
                super();
            }
            
            public ValidationException(String msg) {
                super(msg);
            }
            
            public ValidationException(String msg, Throwable cause) {
                super(msg, cause);
            }
            
            public ValidationException(String msg, List<Throwable> individualFailures) {
            	super(msg);
            	this.individualFailures = individualFailures;
			}

			public void setIndividualFailures(List<Throwable> individualFailures) {
                this.individualFailures = individualFailures;
            }
            public List<Throwable> getIndividualFailures() {
                return individualFailures;
            }
        }
    }

    public static interface Announcer {
        public void announce(long stateVersion);
    }

    private static final class Artifacts {
        Blob snapshot = null;
        Blob delta = null;
        Blob reverseDelta = null;
        
        boolean cleanupCalled;
        boolean snapshotPublishComplete;

        synchronized void cleanup() {
            cleanupCalled = true;
            
            cleanupSnapshot();
            
            if(delta != null) {
                delta.cleanup();
                delta = null;
            }
            if(reverseDelta != null) {
                reverseDelta.cleanup();
                reverseDelta = null;
            }
        }
        
        synchronized void markSnapshotPublishComplete() {
            snapshotPublishComplete = true;
            
            cleanupSnapshot();
        }
        
        private void cleanupSnapshot() {
            if(cleanupCalled && snapshotPublishComplete && snapshot != null) {
                snapshot.cleanup();
                snapshot = null;
            }
        }

        boolean hasDelta() {
            return delta != null;
        }

        public boolean hasReverseDelta() {
            return reverseDelta != null;
        }
    }
    
    public static HollowProducer.Builder withPublisher(HollowProducer.Publisher publisher) {
        Builder builder = new Builder();
        return builder.withPublisher(publisher);
    }
    
    public static class Builder<B extends HollowProducer.Builder<B>> {
        protected BlobStager stager;
        protected BlobCompressor compressor;
        protected File stagingDir;
        protected Publisher publisher;
        protected Announcer announcer;
        protected List<Validator> validators = new ArrayList<Validator>();
        protected List<HollowProducerListener> listeners = new ArrayList<HollowProducerListener>();
        protected List<HollowValidationListener> validationListeners = new ArrayList<HollowValidationListener>();
        protected VersionMinter versionMinter = new VersionMinterWithCounter();
        protected Executor snapshotPublishExecutor = null;
        protected int numStatesBetweenSnapshots = 0;
        protected long targetMaxTypeShardSize = DEFAULT_TARGET_MAX_TYPE_SHARD_SIZE;
        protected HollowMetricsCollector<HollowProducerMetrics> metricsCollector;
        protected BlobStorageCleaner blobStorageCleaner = new DummyBlobStorageCleaner();
        protected SingleProducerEnforcer singleProducerEnforcer = new BasicSingleProducerEnforcer();

        public B withBlobStager(HollowProducer.BlobStager stager) {
            this.stager = stager;
            return (B)this;
        }

        public B withBlobCompressor(HollowProducer.BlobCompressor compressor) {
            this.compressor = compressor;
            return (B)this;
        }
        
        public B withBlobStagingDir(File stagingDir) {
            this.stagingDir = stagingDir;
            return (B)this;
        }
        
        public B withPublisher(HollowProducer.Publisher publisher) {
            this.publisher = publisher; 
            return (B)this;
        }
        
        public B withAnnouncer(HollowProducer.Announcer announcer) {
            this.announcer = announcer;
            return (B)this;
        }
        
        public B withValidator(HollowProducer.Validator validator) {
            this.validators.add(validator);
            return (B)this;
        }
        
        public B withValidators(HollowProducer.Validator... validators) {
            for(Validator validator : validators)
                this.validators.add(validator);
            return (B)this;
        }
        
        public B withListener(HollowProducerListener listener) {
            this.listeners.add(listener);
            return (B)this;
        }
        
        public B withListeners(HollowProducerListener... listeners) {
            for(HollowProducerListener listener : listeners)
                this.listeners.add(listener);
            return (B)this;
        }
        
        public B withValidationListeners(HollowValidationListener... listeners) {
            for(HollowValidationListener listener : listeners)
                this.validationListeners.add(listener);
            return (B)this;
        }
        
        public B withVersionMinter(HollowProducer.VersionMinter versionMinter) {
            this.versionMinter = versionMinter;
            return (B)this;
        }
        
        public B withSnapshotPublishExecutor(Executor executor) {
            this.snapshotPublishExecutor = executor;
            return (B)this;
        }
        
        public B withNumStatesBetweenSnapshots(int numStatesBetweenSnapshots) {
            this.numStatesBetweenSnapshots = numStatesBetweenSnapshots;
            return (B)this;
        }
        
        public B withTargetMaxTypeShardSize(long targetMaxTypeShardSize) {
            this.targetMaxTypeShardSize = targetMaxTypeShardSize;
            return (B)this;
        }

        public B withMetricsCollector(HollowMetricsCollector<HollowProducerMetrics> metricsCollector) {
            this.metricsCollector = metricsCollector;
            return (B)this;
        }

        public B withBlobStorageCleaner(BlobStorageCleaner blobStorageCleaner) {
            this.blobStorageCleaner = blobStorageCleaner;
            return (B)this;
        }

        public B withSingleProducerEnforcer(SingleProducerEnforcer singleProducerEnforcer) {
            this.singleProducerEnforcer = singleProducerEnforcer;
            return (B)this;
        }

        public B noSingleProducerEnforcer() {
            this.singleProducerEnforcer = null;
            return (B)this;
        }

        protected void checkArguments() {
            if(stager != null && compressor != null)
                throw new IllegalArgumentException("Both a custom BlobStager and BlobCompressor were specified -- please specify only one of these.");
            if(stager != null && stagingDir != null)
                throw new IllegalArgumentException("Both a custom BlobStager and a staging directory were specified -- please specify only one of these.");

            if(this.stager == null) {
                BlobCompressor compressor = this.compressor != null ? this.compressor : BlobCompressor.NO_COMPRESSION;
                File stagingDir = this.stagingDir != null ? this.stagingDir : new File(System.getProperty("java.io.tmpdir"));
                this.stager = new HollowFilesystemBlobStager(stagingDir, compressor);
            }
        }
        
        public HollowProducer build() {
            checkArguments();
            return new HollowProducer(stager, publisher, announcer, validators, listeners, validationListeners, versionMinter, snapshotPublishExecutor, numStatesBetweenSnapshots, targetMaxTypeShardSize, metricsCollector, blobStorageCleaner, singleProducerEnforcer);
        }
    }

    /**
     * Provides the opportunity to clean the blob storage.
     * It allows users to implement logic base on Blob Type.
     */
    public static abstract class BlobStorageCleaner {
        public void clean(Blob.Type blobType) {
            switch(blobType) {
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
        public void cleanSnapshots() { }

        @Override
        public void cleanDeltas() { }

        @Override
        public void cleanReverseDeltas() { }
    }

}
