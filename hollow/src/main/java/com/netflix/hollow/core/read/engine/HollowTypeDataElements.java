package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;

public abstract class HollowTypeDataElements {

    public int maxOrdinal;

    public GapEncodedVariableLengthIntegerReader encodedAdditions;
    public GapEncodedVariableLengthIntegerReader encodedRemovals;

    public final ArraySegmentRecycler memoryRecycler;
    public final MemoryMode memoryMode;
    public final boolean useShardReadFastPaths;
    public final HollowReadConfiguration readConfiguration;

    public HollowTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        this(new HollowReadConfiguration(memoryMode, memoryRecycler));
    }

    public HollowTypeDataElements(HollowReadConfiguration readConfiguration) {
        this.readConfiguration = readConfiguration;
        this.useShardReadFastPaths = readConfiguration.isExperimentalFeatureEnabled(ExperimentalFeature.SHARD_READ_FAST_PATHS);
        this.memoryMode = readConfiguration.getMemoryMode();
        this.memoryRecycler = readConfiguration.getMemoryRecycler();
    }

    public abstract void destroy();
}
