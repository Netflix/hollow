package com.netflix.hollow.core.memory;

import static com.netflix.hollow.core.memory.encoding.BlobByteBuffer.MAX_SINGLE_BUFFER_CAPACITY;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

public class VariableLengthDataFactory {

    private static final Logger LOG = Logger.getLogger(VariableLengthDataFactory.class.getName());

    public static class StagedVariableLengthData {
        SegmentedByteArray byteArray;
        RandomAccessFile raf;

        public StagedVariableLengthData(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws FileNotFoundException {
            if (memoryMode.equals(MemoryMode.ON_HEAP)) {
                byteArray = new SegmentedByteArray(memoryRecycler);
                raf = null;
            } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
                byteArray = null;
                raf = new RandomAccessFile(new File("/tmp/delta-staging-varLengthData_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        + "_" + UUID.randomUUID()), "rws");
            } else {
                throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
            }
        }

        public void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length) throws IOException {
            if (this.byteArray != null) {
                this.byteArray.orderedCopy(src, srcPos, destPos, length);
            } else {
                raf.setLength(destPos + length);
                long endSrcPos = srcPos + length;
                while(srcPos < endSrcPos) {
                    raf.write(src.get(srcPos++));   // TODO: write faster than one byte at a time
                }
            }
        }
    }

    public static VariableLengthData get(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            return new EncodedByteBuffer();
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static StagedVariableLengthData stage(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws FileNotFoundException {
        return new StagedVariableLengthData(memoryMode, memoryRecycler);
    }

    public static VariableLengthData commit(StagedVariableLengthData staged, MemoryMode memoryMode) throws IOException {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return staged.byteArray;

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            EncodedByteBuffer byteBuffer = new EncodedByteBuffer();
            HollowBlobInput hbi = HollowBlobInput.mmap(staged.raf, MAX_SINGLE_BUFFER_CAPACITY);
            staged.raf.seek(0);
            byteBuffer.loadFrom(hbi, staged.raf.length());
            return byteBuffer;
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static void destroy(VariableLengthData vld) {
        if (vld instanceof SegmentedByteArray) {
            ((SegmentedByteArray) vld).destroy();
        } else if (vld instanceof EncodedByteBuffer) {
            LOG.warning("Destroy operation is a not implemented for shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
