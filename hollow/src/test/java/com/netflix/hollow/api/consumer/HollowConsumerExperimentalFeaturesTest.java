package com.netflix.hollow.api.consumer;

import static com.netflix.hollow.core.read.engine.ExperimentalFeature.SHARD_READ_FAST_PATHS;
import static com.netflix.hollow.core.read.engine.ExperimentalFeature.DIRECT_SEGMENT_STRING_READS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.GarbageCollectorAwareRecycler;
import com.netflix.hollow.core.memory.pool.MemoryRecyclingMode;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.test.model.Movie;
import java.io.IOException;
import org.junit.Test;

public class HollowConsumerExperimentalFeaturesTest {
    @Test
    public void featuresDefaultToOff() throws IOException {
        TestHollowConsumer consumer = builder().build();
        loadSnapshot(consumer, 1L);
        for(ExperimentalFeature feature : ExperimentalFeature.values())
            assertFalse(consumer.getStateEngine().isExperimentalFeatureEnabled(feature));
    }

    @Test
    public void optInsAreAdditiveAndCopiedAtConstruction() throws IOException {
        ExperimentalFeature[] features = { SHARD_READ_FAST_PATHS };
        TestHollowConsumer.Builder builder = builder().withExperimentalFeatures(features);
        features[0] = null;
        builder.withExperimentalFeatures().withExperimentalFeatures(SHARD_READ_FAST_PATHS);
        TestHollowConsumer consumer = builder.build();
        builder.experimentalFeatures.clear();
        loadSnapshot(consumer, 1L);
        assertTrue(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
    }

    @Test
    public void optInsSurviveDeltasAndReplacementSnapshots() throws IOException {
        TestHollowConsumer consumer = builder().withExperimentalFeatures(SHARD_READ_FAST_PATHS).build();
        loadSnapshot(consumer, 1L);
        HollowReadStateEngine initial = consumer.getStateEngine();
        consumer.addDelta(1L, 2L, new HollowWriteStateEngineBuilder().add(new Movie(2, "delta", 2026)).build());
        consumer.triggerRefreshTo(2L);
        assertTrue(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
        loadSnapshot(consumer, 3L);
        assertNotSame(initial, consumer.getStateEngine());
        assertTrue(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
    }

    @Test
    public void recyclerModesAreExplicitAndIndependentOfFeatures() throws IOException {
        for(MemoryRecyclingMode mode : MemoryRecyclingMode.values()) {
            TestHollowConsumer consumer = builder().withMemoryRecyclingMode(mode).build();
            loadSnapshot(consumer, 1L);
            ArraySegmentRecycler recycler = consumer.getStateEngine().getMemoryRecycler();
            boolean expected = mode == MemoryRecyclingMode.AUTO
                    ? new GarbageCollectorAwareRecycler().recyclesArrays() : mode == MemoryRecyclingMode.ENABLED;
            assertEquals(expected, recycler.recyclesArrays());
            assertFalse(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
            loadSnapshot(consumer, 2L);
            assertSame(recycler, consumer.getStateEngine().getMemoryRecycler());
        }
    }

    @Test
    public void consumersDoNotShareRecyclingPools() throws IOException {
        TestHollowConsumer.Builder builder = builder().withMemoryRecyclingMode(MemoryRecyclingMode.ENABLED);
        TestHollowConsumer first = builder.build();
        TestHollowConsumer second = builder.build();
        loadSnapshot(first, 1L);
        loadSnapshot(second, 1L);
        assertNotSame(first.getStateEngine().getMemoryRecycler(), second.getStateEngine().getMemoryRecycler());
    }

    @Test
    public void defaultRecyclerSelectionRemainsGcAware() throws IOException {
        TestHollowConsumer consumer = builder().build();
        loadSnapshot(consumer, 1L);
        assertTrue(consumer.getStateEngine().getMemoryRecycler() instanceof GarbageCollectorAwareRecycler);
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullRecyclerMode() {
        builder().withMemoryRecyclingMode(null);
    }

    @Test
    public void stringAndShardOptInsAreIndependent() throws IOException {
        TestHollowConsumer strings = builder().withExperimentalFeatures(DIRECT_SEGMENT_STRING_READS).build();
        loadSnapshot(strings, 1L);
        assertTrue(strings.getStateEngine().isExperimentalFeatureEnabled(DIRECT_SEGMENT_STRING_READS));
        assertFalse(strings.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
        TestHollowConsumer both = builder().withExperimentalFeatures(DIRECT_SEGMENT_STRING_READS)
                .withExperimentalFeatures(SHARD_READ_FAST_PATHS).build();
        loadSnapshot(both, 1L);
        assertTrue(both.getStateEngine().isExperimentalFeatureEnabled(DIRECT_SEGMENT_STRING_READS));
        assertTrue(both.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
    }

    @Test
    public void allFeatureCombinationsSurviveReplacementSnapshots() throws IOException {
        ExperimentalFeature[] features = ExperimentalFeature.values();
        for(int mask = 0; mask < (1 << features.length); mask++) {
            TestHollowConsumer.Builder builder = builder();
            for(int i = 0; i < features.length; i++) {
                if((mask & (1 << i)) != 0)
                    builder.withExperimentalFeatures(features[i]);
            }
            TestHollowConsumer consumer = builder.build();
            loadSnapshot(consumer, 1L);
            loadSnapshot(consumer, 2L);
            for(int i = 0; i < features.length; i++)
                assertEquals((mask & (1 << i)) != 0, consumer.getStateEngine().isExperimentalFeatureEnabled(features[i]));
            assertEquals((mask & (1 << ExperimentalFeature.SHARD_CURSOR_ITERATORS.ordinal())) != 0,
                    consumer.getStateEngine().isShardCursorIteratorsEnabled());
        }
    }

    @Test
    public void iteratorConvenienceMethodOnlyOptsInToIterators() throws IOException {
        TestHollowConsumer consumer = builder().withShardCursorIterators().build();
        loadSnapshot(consumer, 1L);
        assertTrue(consumer.getStateEngine().isExperimentalFeatureEnabled(ExperimentalFeature.SHARD_CURSOR_ITERATORS));
        assertFalse(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
        assertFalse(consumer.getStateEngine().isExperimentalFeatureEnabled(DIRECT_SEGMENT_STRING_READS));
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullFeature() {
        builder().withExperimentalFeatures((ExperimentalFeature)null);
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullFeatureArray() {
        builder().withExperimentalFeatures((ExperimentalFeature[])null);
    }

    private static TestHollowConsumer.Builder builder() {
        return new TestHollowConsumer.Builder().withBlobRetriever(new TestBlobRetriever());
    }

    private static void loadSnapshot(TestHollowConsumer consumer, long version) throws IOException {
        consumer.addSnapshot(version, new HollowWriteStateEngineBuilder().add(new Movie(version, "snapshot", 2026)).build());
        consumer.triggerRefreshTo(version);
    }
}
