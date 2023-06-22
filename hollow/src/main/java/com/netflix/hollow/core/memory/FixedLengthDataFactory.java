package com.netflix.hollow.core.memory;

import static com.netflix.hollow.core.memory.encoding.BlobByteBuffer.MAX_SINGLE_BUFFER_CAPACITY;

import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class FixedLengthDataFactory {

    private static final Logger LOG = Logger.getLogger(FixedLengthDataFactory.class.getName());

    public static FixedLengthData get(HollowBlobInput in, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws IOException {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return FixedLengthElementArray.newFrom(in, memoryRecycler);
        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            return EncodedLongBuffer.newFrom(in);
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    // allocate (for write)
    public static FixedLengthData allocate(HollowBlobInput in,
                                           MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler,
                                           String fileName) throws IOException {

        long numLongs = VarInt.readVLong(in);
        long numBits = numLongs << 6;
        return allocate(numBits, memoryMode, memoryRecycler, fileName);
    }

    public static FixedLengthData allocate(long numBits, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler,
                                         String fileName) throws IOException {
        long numLongs = ((numBits - 1) >>> 6) + 1;
        numLongs ++;    // accommodate for reading a long starting at bit index within numLongs-1
        long numBytes = numLongs << 3;
        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new FixedLengthElementArray(memoryRecycler, numBits);
        } else {
            File targetFile = provisionTargetFile(numBytes, fileName);
            try (HollowBlobInput targetBlob = HollowBlobInput.randomAccess(targetFile, MAX_SINGLE_BUFFER_CAPACITY)) {
                return EncodedLongBuffer.newFrom(targetBlob, numLongs, targetFile);   // TODO: test with different single buffer capacities
            }
        }
    }

    static File provisionTargetFile(long numBytes, String fileName) throws IOException {
        File targetFile = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength(numBytes);
        raf.close();
        // System.out.println("SNAP: Provisioned targetFile (one per shard per type) of size " + numBytes + " bytes: " + targetFile.getPath());
        return targetFile;
    }

    public static void destroy(FixedLengthData fld, ArraySegmentRecycler memoryRecycler) throws IOException {
        if (fld instanceof FixedLengthElementArray) {
            ((FixedLengthElementArray) fld).destroy(memoryRecycler);
        } else if (fld instanceof EncodedLongBuffer) {
            LOG.info("SNAP: Destroy operation invoked on EncodedLongBuffer (FixedLengthData)");
            ((EncodedLongBuffer) fld).destroy();
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
