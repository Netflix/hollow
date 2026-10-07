package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Immutable configuration for read implementations and their storage. The recycler instance is
 * retained across updates; the configuration never changes its array-reuse behavior.
 */
public final class HollowReadConfiguration {
    private final MemoryMode memoryMode;
    private final ArraySegmentRecycler memoryRecycler;
    private final EnumSet<ExperimentalFeature> experimentalFeatures;

    public HollowReadConfiguration(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler,
                                   ExperimentalFeature... experimentalFeatures) {
        this.memoryMode = Objects.requireNonNull(memoryMode);
        this.memoryRecycler = Objects.requireNonNull(memoryRecycler);
        this.experimentalFeatures = EnumSet.noneOf(ExperimentalFeature.class);
        Collections.addAll(this.experimentalFeatures, experimentalFeatures);
    }

    public MemoryMode getMemoryMode() {
        return memoryMode;
    }

    public ArraySegmentRecycler getMemoryRecycler() {
        return memoryRecycler;
    }

    public boolean isExperimentalFeatureEnabled(ExperimentalFeature feature) {
        return experimentalFeatures.contains(Objects.requireNonNull(feature));
    }

    public HollowReadConfiguration withMemoryMode(MemoryMode mode) {
        return mode == memoryMode ? this : new HollowReadConfiguration(mode, memoryRecycler,
                experimentalFeatures.toArray(new ExperimentalFeature[0]));
    }

    public HollowReadConfiguration withMemoryRecycler(ArraySegmentRecycler recycler) {
        return recycler == memoryRecycler ? this : new HollowReadConfiguration(memoryMode, recycler,
                experimentalFeatures.toArray(new ExperimentalFeature[0]));
    }
}
