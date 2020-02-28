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
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
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
                objectLongevityDetector, doubleSnapshotConfig, hashCodeFinder, refreshExecutor, metricsCollector);
        this.blobRetriever = blobRetriever;
    }

    protected TestHollowConsumer(Builder builder) {
        super(builder);
        this.blobRetriever = builder.blobRetriever();
    }

    public TestHollowConsumer addSnapshot(long version, HollowWriteStateEngine stateEngine) throws IOException {
        if (blobRetriever instanceof TestBlobRetriever) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new HollowBlobWriter(stateEngine).writeSnapshot(outputStream);
            ((TestBlobRetriever) blobRetriever).addSnapshot(version, new TestBlob(version,
                    new ByteArrayInputStream(outputStream.toByteArray())));
        } else {
            throw new IllegalStateException("Cannot add snapshot if not using TestBlobRetriever");
        }
        return this;
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
