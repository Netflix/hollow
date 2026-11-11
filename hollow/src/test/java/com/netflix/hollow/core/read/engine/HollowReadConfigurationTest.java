package com.netflix.hollow.core.read.engine;

import static com.netflix.hollow.core.read.engine.ExperimentalFeature.SHARD_READ_FAST_PATHS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Test;

public class HollowReadConfigurationTest {
    @Test
    public void copiesOptInsAndPreservesThemWhenDerivingStorageConfigurations() {
        ExperimentalFeature[] features = { SHARD_READ_FAST_PATHS };
        RecyclingRecycler recycler = new RecyclingRecycler();
        HollowReadConfiguration original = new HollowReadConfiguration(MemoryMode.ON_HEAP, recycler, features);
        features[0] = null;
        assertTrue(original.isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
        assertSame(original, original.withMemoryMode(MemoryMode.ON_HEAP));
        assertSame(original, original.withMemoryRecycler(recycler));
        WastefulRecycler wasteful = new WastefulRecycler(5, 2);
        HollowReadConfiguration derived = original.withMemoryRecycler(wasteful).withMemoryMode(MemoryMode.SHARED_MEMORY_LAZY);
        assertSame(wasteful, derived.getMemoryRecycler());
        assertSame(MemoryMode.SHARED_MEMORY_LAZY, derived.getMemoryMode());
        assertTrue(derived.isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
        assertSame(recycler, original.getMemoryRecycler());
        assertSame(MemoryMode.ON_HEAP, original.getMemoryMode());
    }

    @Test
    public void defaultConfigurationDoesNotEnableExperiments() {
        HollowReadConfiguration configuration = new HollowReadConfiguration(MemoryMode.ON_HEAP, new RecyclingRecycler());
        assertFalse(configuration.isExperimentalFeatureEnabled(SHARD_READ_FAST_PATHS));
    }
}
