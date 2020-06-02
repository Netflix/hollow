package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;
import java.util.logging.Logger;

public class FixedLengthDataMode {

    private static final Logger LOG = Logger.getLogger(FixedLengthDataMode.class.getName());

    public static FixedLengthData newFrom(HollowBlobInput in, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws IOException {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return FixedLengthElementArray.newFrom(in, memoryRecycler);
        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            return EncodedLongBuffer.newFrom(in);
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static void destroy(FixedLengthData fld, ArraySegmentRecycler memoryRecycler) {
        if (fld instanceof FixedLengthElementArray) {
            ((FixedLengthElementArray) fld).destroy(memoryRecycler);
        } else if (fld instanceof EncodedLongBuffer) {
            LOG.warning("Destroy operation is a no-op in shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
