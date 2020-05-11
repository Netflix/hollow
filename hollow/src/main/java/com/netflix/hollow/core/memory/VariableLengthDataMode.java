package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class VariableLengthDataMode {

    public static VariableLengthData get(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            /// list pointer array
            return new EncodedByteBuffer();
        } else {
            throw new NotImplementedException();
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
