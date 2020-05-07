package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

public class FixedLengthDataMode {

    public static final boolean SHARED_MEMORY_MODE = true;

    public static FixedLengthData deserializeFrom(HollowBlobInput in, ArraySegmentRecycler memoryRecycler) throws IOException {

        // SNAP: TODO: Toggle memory mode: Should I model it as an enum thats passed everywhere?
        if (SHARED_MEMORY_MODE) {
            /// list pointer array
            EncodedLongBuffer data = EncodedLongBuffer.deserializeFrom(in);
            return data;
        } else {
            FixedLengthElementArray data = FixedLengthElementArray.deserializeFrom(in, memoryRecycler);
            return data;
        }
    }

    public static void destroy(FixedLengthData fld, ArraySegmentRecycler memoryRecycler) {
        if (fld instanceof FixedLengthElementArray) {
            ((FixedLengthElementArray) fld).destroy(memoryRecycler);
        } else if (fld instanceof EncodedLongBuffer) {
            throw new UnsupportedOperationException("Destroy operation not supported in shared memory mode");   // SNAP: Should this be NOP instead?
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
