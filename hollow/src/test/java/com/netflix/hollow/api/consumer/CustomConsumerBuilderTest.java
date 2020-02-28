package com.netflix.hollow.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.TypeFilter;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Assert.assertNotNull(consumer);
        assertThat(consumer).isNotInstanceOf(AugmentedConsumer.class);
    }

    @Test
    public void augmentedBehavior_deprecatedConstructor() {
        HollowConsumer consumer = new AugmentedBuilder()
                .withBlobRetriever(blobStore) // should be called before custom builder methods
                .withDeprecatedAugmentation()
                .build();
        assertThat(consumer).isInstanceOf(AugmentedConsumer.class);
    }

    @Test
    public void augmentedBehavior_builderConstructor() {
        HollowConsumer consumer = new AugmentedBuilder()
                .withBlobRetriever(blobStore) // should be called before custom builder methods
                .withAugmentation()
                .build();
        assertThat(consumer).isInstanceOf(AugmentedConsumer.class);
    }

    @Test
    public void augmentedBehavior_breakingTypeFilter() {
        try {
            new AugmentedBuilder()
                    .withBlobRetriever(blobStore)
                    .withAugmentation()
                    .withFilterConfig(new HollowFilterConfig(true))
                    .withTypeFilter(TypeFilter.newTypeFilter().build())
            .build();
            fail();
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage()).startsWith("Only one of typeFilter");
        }
    }

    private static class AugmentedBuilder extends HollowConsumer.Builder<AugmentedBuilder> {
        enum Augmentation {
            none, deprecatedConstructor, builderConstructor;
        }
        private Augmentation augmentation = Augmentation.none;

        AugmentedBuilder withDeprecatedAugmentation() {
            augmentation = Augmentation.deprecatedConstructor;
            return this;
        }

        AugmentedBuilder withAugmentation() {
            augmentation = Augmentation.builderConstructor;
            return this;
        }

        @Override
        public AugmentedBuilder withFilterConfig(HollowFilterConfig filterConfig) {
            /*
             * this intentionally breaks the migration to TypeFilter so we can test
             * our guard against the scenario where both filterConfig and typeFilter are set
             */
            this.filterConfig = filterConfig;
            return this;
        }

        @Override
        public HollowConsumer build() {
            checkArguments();
            HollowConsumer consumer;
            switch (augmentation) {
                case none:
                    consumer = super.build();
                    break;
                case deprecatedConstructor:
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
                    break;
                case builderConstructor:
                    consumer = new AugmentedConsumer(this);
                    break;

                default:
                    throw new IllegalStateException();
            }
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
                    metricsCollector);
        }

        AugmentedConsumer(AugmentedBuilder builder) {
            super(builder);
        }

        @Override
        public String toString() {
            return "I am augmented";
        }
    }
}
