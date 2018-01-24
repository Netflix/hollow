package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executor;

// this test doesn't do much beyond making sure that a custom builder will
// compile and ensure that HollowConsumer.Builder is parameterized correctly
// to allow custom builder methods to be interleaved with base class builder
// methods
public class CustomConsumerBuilderTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void defaultBehavior() {
        HollowConsumer consumer = new AugmentedBuilder()
                .withBlobRetriever(blobStore)
                .build();
        Assert.assertTrue(consumer instanceof HollowConsumer);
        Assert.assertFalse(consumer instanceof AugmentedConsumer);
    }

    @Test
    public void augmentedBehavior() {
        HollowConsumer consumer = new AugmentedBuilder()
                .withBlobRetriever(blobStore) // should be called before custom builder methods
                .withAugmentation()
                .build();
        Assert.assertTrue(consumer instanceof AugmentedConsumer);
    }

    private static class AugmentedBuilder extends HollowConsumer.Builder<AugmentedBuilder> {
        private boolean shouldAugment = false;
        AugmentedBuilder withAugmentation() {
            shouldAugment = true;
            return this;
        }

        @Override
        public HollowConsumer build() {
            checkArguments();
            HollowConsumer consumer;
            if(shouldAugment)
                consumer = new AugmentedConsumer(
                        blobRetriever,
                        announcementWatcher,
                        refreshListeners,
                        apiFactory,
                        filterConfig,
                        objectLongevityConfig,
                        objectLongevityDetector,
                        doubleSnapshotConfig,
                        hashCodeFinder,
                        refreshExecutor,
                        metricsCollector
                );
            else
                consumer = super.build();
            return consumer;
        }
    }

    private static class AugmentedConsumer extends HollowConsumer {
        AugmentedConsumer(
                HollowConsumer.BlobRetriever blobRetriever,
                HollowConsumer.AnnouncementWatcher announcementWatcher,
                List<RefreshListener> refreshListeners,
                HollowAPIFactory apiFactory,
                HollowFilterConfig filterConfig,
                ObjectLongevityConfig objectLongevityConfig,
                ObjectLongevityDetector objectLongevityDetector,
                DoubleSnapshotConfig doubleSnapshotConfig,
                HollowObjectHashCodeFinder hashCodeFinder,
                Executor refreshExecutor,
                HollowMetricsCollector<HollowConsumerMetrics> metricsCollector
        ) {
            super(blobRetriever,
                    announcementWatcher,
                    refreshListeners,
                    apiFactory,
                    filterConfig,
                    objectLongevityConfig,
                    objectLongevityDetector,
                    doubleSnapshotConfig,
                    hashCodeFinder,
                    refreshExecutor,
                    metricsCollector);        }

        @Override
        public String toString() {
            return "I am augmented";
        }
    }
}
