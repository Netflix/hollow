package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.ContiguousFixedLengthData;
import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadConfiguration;
import java.io.IOException;
import java.util.logging.Logger;

public class FixedLengthDataFactory {

    private static final Logger LOG = Logger.getLogger(FixedLengthDataFactory.class.getName());

    public static FixedLengthData get(HollowBlobInput in, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws IOException {
        return get(in, new HollowReadConfiguration(memoryMode, memoryRecycler));
    }

    public static FixedLengthData get(HollowBlobInput in, HollowReadConfiguration configuration) throws IOException {
        MemoryMode mode = configuration.getMemoryMode();
        if(mode == MemoryMode.ON_HEAP) {
            long numLongs = VarInt.readVLong(in);
            if(useContiguousStorage(configuration, numLongs))
                return ContiguousFixedLengthData.newFrom(in, numLongs);
            return FixedLengthElementArray.newFrom(in, configuration, numLongs);
        } else if(mode == MemoryMode.SHARED_MEMORY_LAZY) {
            return EncodedLongBuffer.newFrom(in);
        }
        throw new UnsupportedOperationException("Memory mode " + mode.name() + " not supported");
    }

    public static FixedLengthData get(long numBits, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        return get(numBits, new HollowReadConfiguration(memoryMode, memoryRecycler));
    }

    public static FixedLengthData get(long numBits, HollowReadConfiguration configuration) {
        if(configuration.getMemoryMode() == MemoryMode.ON_HEAP) {
            long numLongs = numBits == 0 ? 0 : ((numBits - 1) >>> 6) + 1;
            if(useContiguousStorage(configuration, numLongs))
                return new ContiguousFixedLengthData(numBits);
            return new FixedLengthElementArray(configuration, numBits);
        }
        throw new UnsupportedOperationException("Memory mode " + configuration.getMemoryMode().name() + " not supported");
    }

    private static boolean useContiguousStorage(HollowReadConfiguration configuration, long numLongs) {
        return configuration.isExperimentalFeatureEnabled(ExperimentalFeature.SHARD_READ_FAST_PATHS)
                && !configuration.getMemoryRecycler().recyclesArrays()
                && ContiguousFixedLengthData.canStore(numLongs);
    }

    public static void destroy(FixedLengthData fld, ArraySegmentRecycler memoryRecycler) {
        if(fld instanceof FixedLengthElementArray) {
            ((FixedLengthElementArray)fld).destroy(memoryRecycler);
        } else if(fld instanceof ContiguousFixedLengthData) {
            // Contiguous storage is only used when arrays are not recycled.
        } else if(fld instanceof EncodedLongBuffer) {
            LOG.warning("Destroy operation is a no-op in shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
