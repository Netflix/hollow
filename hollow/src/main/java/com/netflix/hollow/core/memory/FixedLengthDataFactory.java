package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.ContiguousFixedLengthData;
import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;
import java.util.logging.Logger;

public class FixedLengthDataFactory {

    private static final Logger LOG = Logger.getLogger(FixedLengthDataFactory.class.getName());

    public static FixedLengthData get(HollowBlobInput in, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws IOException {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            long numLongs = VarInt.readVLong(in);
            if (useContiguousStorage(memoryRecycler, numLongs)) {
                return ContiguousFixedLengthData.newFrom(in, numLongs);
            }
            return FixedLengthElementArray.newFrom(in, memoryRecycler, numLongs);
        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            return EncodedLongBuffer.newFrom(in);
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static FixedLengthData get(long numBits, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            long numLongs = numBits == 0 ? 0 : ((numBits - 1) >>> 6) + 1;
            if (useContiguousStorage(memoryRecycler, numLongs)) {
                return new ContiguousFixedLengthData(numBits);
            }
            return new FixedLengthElementArray(memoryRecycler, numBits);
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    private static boolean useContiguousStorage(ArraySegmentRecycler memoryRecycler, long numLongs) {
        // Non-recycling data need not retain fixed-size segments for later reuse.
        return !memoryRecycler.recyclesArrays() && ContiguousFixedLengthData.canStore(numLongs);
    }

    public static void destroy(FixedLengthData fld, ArraySegmentRecycler memoryRecycler) {
        if (fld instanceof FixedLengthElementArray) {
            ((FixedLengthElementArray) fld).destroy(memoryRecycler);
        } else if (fld instanceof ContiguousFixedLengthData) {
            // Contiguous storage is only used when arrays are not recycled.
        } else if (fld instanceof EncodedLongBuffer) {
            LOG.warning("Destroy operation is a no-op in shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
