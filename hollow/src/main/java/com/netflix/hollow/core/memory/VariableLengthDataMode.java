package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.util.logging.Logger;

public class VariableLengthDataMode {

    private static final Logger LOG = Logger.getLogger(VariableLengthDataMode.class.getName());

    public static VariableLengthData get(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            /// list pointer array
            return new EncodedByteBuffer();
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static void destroy(VariableLengthData vld) {
        if (vld instanceof SegmentedByteArray) {
            ((SegmentedByteArray) vld).destroy();
        } else if (vld instanceof EncodedByteBuffer) {
            LOG.warning("Destroy operation is a no-op in shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
