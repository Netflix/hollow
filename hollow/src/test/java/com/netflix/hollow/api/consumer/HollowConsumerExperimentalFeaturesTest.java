package com.netflix.hollow.api.consumer;

import static com.netflix.hollow.core.read.engine.ExperimentalFeature.SHARD_READ_FAST_PATHS;
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
        assertFalse(consumer.getStateEngine().isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
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
