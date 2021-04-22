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
package com.netflix.hollow.test.consumer;

import com.netflix.hollow.Internal;
import com.netflix.hollow.PublicSpi;
import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerCopyDirector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * This class allows constructing HollowConsumer objects for use in unit and integration tests.
 * This uses an in-memory blob store, which means that all state is kept in memory, and so this is
 * not suited for use in normal application usage, just in tests.
 *
 * Example usage:
 *
 * // your data state will be represented by a state engine
 * HollowWriteStateEngine stateEngine = new HollowWriteStateEngineBuilder()
 *     .add("somedata")
 *     .add(new MyDataModelType("somestuff", 2))
 *     .build();
 * // we will add the snapshot with a version, and make the announcementWatcher see this version
 * long latestVersion = 1L;
 * TestHollowConsumer consumer = new TestHollowConsumer.Builder()
 *        .withAnnouncementWatcher(new TestAnnouncementWatcher().setLatestVersion(latestVersion))
 *        .withBlobRetriever(new TestBlobRetriever())
 *        .withGeneratedAPIClass(MyApiClass.class)
 *        .build();
 * consumer.addSnapshot(latestVersion, stateEngine);
 * consumer.triggerRefresh();
 *
 * If you wish to use triggerRefreshTo instead of triggerRefresh, do not provide an
 * AnnouncementWatcher.
 */
@PublicSpi
public class TestHollowConsumer extends HollowConsumer {
    private final BlobRetriever blobRetriever;

    /**
     * @deprecated use {@link TestHollowConsumer.Builder}
     */
    @Internal
    @Deprecated
    protected TestHollowConsumer(BlobRetriever blobRetriever,
            AnnouncementWatcher announcementWatcher,
            List<RefreshListener> refreshListeners,
            HollowAPIFactory apiFactory,
            HollowFilterConfig dataFilter,
            ObjectLongevityConfig objectLongevityConfig,
            ObjectLongevityDetector objectLongevityDetector,
            DoubleSnapshotConfig doubleSnapshotConfig,
            HollowObjectHashCodeFinder hashCodeFinder,
            Executor refreshExecutor,
            HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {
        super(blobRetriever, announcementWatcher, refreshListeners, apiFactory, dataFilter, objectLongevityConfig,
                objectLongevityDetector, doubleSnapshotConfig, hashCodeFinder, refreshExecutor, MemoryMode.ON_HEAP,
                metricsCollector);
        this.blobRetriever = blobRetriever;
    }

    protected TestHollowConsumer(Builder builder) {
        super(builder);
        this.blobRetriever = builder.blobRetriever();
    }

    /**
     * Apply a snapshot transition to {@code version} by applying {@code state}. This can be called on a {@code TestHollowConsumer}
     * that hasn't been initialized with a read state, or as a double-snapshot on a {@code TestHollowConsumer} with existing state.
     */
    public void applySnapshot(long toVersion, HollowWriteStateEngine state) throws IOException {
        addSnapshot(toVersion, state);
        triggerRefreshTo(toVersion);
    }

    /**
     * Apply a delta transition to {@code toVersion} by applying {@code state}.
     */
    public void applyDelta(long toVersion, HollowWriteStateEngine state) throws IOException {
        addDelta(getCurrentVersionId(), toVersion, state);
        triggerRefreshTo(toVersion);
    }

    public TestHollowConsumer addSnapshot(long version, HollowWriteStateEngine state) throws IOException {
        // if consumer has state then restore it
        if (getStateEngine() != null) {
            HollowWriteStateEngine snapshotState = HollowWriteStateCreator.createWithSchemas(getStateEngine().getSchemas());
            snapshotState.restoreFrom(getStateEngine());

            HollowCombiner combiner = new HollowCombiner(HollowCombinerCopyDirector.DEFAULT_DIRECTOR,
                    snapshotState,      // output
                    roundTrip(state));  // input
            combiner.combine();
        }

        if (blobRetriever instanceof TestBlobRetriever) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new HollowBlobWriter(state).writeSnapshot(outputStream);
            ((TestBlobRetriever) blobRetriever).addSnapshot(version, new TestBlob(version,
                    new ByteArrayInputStream(outputStream.toByteArray())));
        } else {
            throw new IllegalStateException("Cannot add snapshot if not using TestBlobRetriever");
        }
        return this;
    }

    public TestHollowConsumer addDelta(long fromVersion, long toVersion, HollowWriteStateEngine state)
            throws IOException {

        if (getStateEngine() == null) {
            throw new UnsupportedOperationException("Delta can not be applied without first applying a snapshot");
        }

        // create a new write state for delta application, restore from current state
        HollowWriteStateEngine deltaState = HollowWriteStateCreator.createWithSchemas(getStateEngine().getSchemas());
        deltaState.restoreFrom(getStateEngine());
        deltaState.overridePreviousStateRandomizedTag(this.getStateEngine().getCurrentRandomizedTag());

        // add all records from passed in {@code state} to delta write state
        HollowCombiner combiner = new HollowCombiner(HollowCombinerCopyDirector.DEFAULT_DIRECTOR,
                deltaState,          // output
                roundTrip(state));   // input
        combiner.combine();

        // apply delta write state to consumer
        if (blobRetriever instanceof TestBlobRetriever) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new HollowBlobWriter(deltaState).writeDelta(outputStream);
            ((TestBlobRetriever) blobRetriever).addDelta(fromVersion, new TestBlob(fromVersion, toVersion,
                    new ByteArrayInputStream(outputStream.toByteArray())));
        } else {
            throw new IllegalStateException("Cannot add delta if not using TestBlobRetriever");
        }
        return this;
    }

    private HollowReadStateEngine roundTrip(HollowWriteStateEngine writeEngine) throws IOException {
        writeEngine.prepareForWrite();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine(writeEngine.getHashCodeFinder());
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));

        return readEngine;
    }

    @PublicSpi
    public static class Builder extends HollowConsumer.Builder<Builder> {
        protected HollowConsumer.BlobRetriever blobRetriever() {
            return blobRetriever;
        }

        @Override
        public TestHollowConsumer build() {
            checkArguments();
            TestHollowConsumer consumer = new TestHollowConsumer(this);
            return consumer;
        }
    }
}
