package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

public class VariableLengthDataMode {

    public static final boolean SHARED_MEMORY_MODE = true;

    public static VariableLengthData get(ArraySegmentRecycler memoryRecycler) throws IOException {

        if (SHARED_MEMORY_MODE) {
            return new EncodedByteBuffer();
        } else {
            return new SegmentedByteArray(memoryRecycler);
        }
    }

    public static void destroy(VariableLengthData vld) {
        if (vld instanceof SegmentedByteArray) {
            ((SegmentedByteArray) vld).destroy();
        } else if (vld instanceof EncodedByteBuffer) {
            throw new UnsupportedOperationException("Destroy operation not supported in shared memory mode");   // SNAP: Should this be NOP instead?
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
