package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;

public abstract class AbstractHollowTypeDataElements {

    public int maxOrdinal;
    public GapEncodedVariableLengthIntegerReader encodedAdditions;
    public GapEncodedVariableLengthIntegerReader encodedRemovals;
    public final ArraySegmentRecycler memoryRecycler;
    public final MemoryMode memoryMode;

    public AbstractHollowTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        this.memoryMode = memoryMode;
        this.memoryRecycler = memoryRecycler;
    }
}
