package com.netflix.hollow.core.memory;

import static com.netflix.hollow.core.memory.encoding.BlobByteBuffer.MAX_SINGLE_BUFFER_CAPACITY;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowSchema;
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

    public static VariableLengthData get(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            return new EncodedByteBuffer();
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    // stage (for writing to)
    public static StagedVariableLengthData stage(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler,
                                                 HollowSchema schemaForDiag, int whichShardForDiag) throws FileNotFoundException {
        return new StagedVariableLengthData(memoryMode, memoryRecycler, schemaForDiag, whichShardForDiag);
    }

    public static void destroy(VariableLengthData vld) {
        if (vld instanceof SegmentedByteArray) {
            ((SegmentedByteArray) vld).destroy();
        } else if (vld instanceof EncodedByteBuffer) {
            LOG.info("SNAP: Destroy operation invoked on EncodedByteBuffer (VariableLengthData)");
            ((EncodedByteBuffer) vld).destroy();
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }


    public static class StagedVariableLengthData {
        private final MemoryMode memoryMode;
        private final SegmentedByteArray byteArray;
        private final File file;
        private final RandomAccessFile raf;

        public RandomAccessFile getRaf() {
            return raf;
        }

        public StagedVariableLengthData(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler,
                                        HollowSchema schemaForDiag, int whichShardForDiag) throws FileNotFoundException {
            this.memoryMode = memoryMode;
            if (memoryMode.equals(MemoryMode.ON_HEAP)) {
                byteArray = new SegmentedByteArray(memoryRecycler);
                file = null;
                raf = null;
            } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
                byteArray = null;
                file = new File("/tmp/delta-staging-varLengthData_"
                        + schemaForDiag.getName() + "_" + whichShardForDiag + "_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        + "_" + UUID.randomUUID());
                raf = new RandomAccessFile(file, "rws");
            } else {
                throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
            }
        }

        public void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length) throws IOException {
            if (this.byteArray != null) {
                this.byteArray.orderedCopy(src, srcPos, destPos, length);
            } else {
                EncodedByteBuffer encodedByteBuffer = (EncodedByteBuffer) src;
                // FileChannel readableChannel = encodedByteBuffer.getBufferView().getChannel();
                // if (readableChannel.isOpen()) { // SNAP: TODO: test. This is coming from delta raf
                //     long savePos = readableChannel.position();
                //     readableChannel.position(srcPos);
                //     this.raf.getChannel().transferFrom(readableChannel, destPos, length);
                //     readableChannel.position(savePos);
                // } else {
                    byte[] chunk = new byte[16384];    // SNAP: vm_stat returns 16384 as page size on my mac

                    while (length > 0) {
                        int toReadBytes = (int) Math.min(length, (long) chunk.length);
                        int readBytes = encodedByteBuffer.getBytes(srcPos, toReadBytes, chunk);
                        length = length - readBytes;
                        srcPos = srcPos + readBytes;

                        this.raf.write(chunk, 0, readBytes);
                    }
            }
        }

        private void writeRaf(int b) throws IOException {
            raf.write(b);
        }

        public void resize(long sizeInBytes) throws IOException {
            if (memoryMode.equals(MemoryMode.ON_HEAP)) {
                // TODO: NOP because array is resized dynamically
            } else {
                this.raf.setLength(sizeInBytes);
            }
        }

        public VariableLengthData commit() throws IOException {
            if (memoryMode.equals(MemoryMode.ON_HEAP)) {
                return this.byteArray;

            } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
                EncodedByteBuffer byteBuffer = new EncodedByteBuffer();
                if (this.raf.length() == 0) {
                    return byteBuffer;
                }
                this.raf.seek(0);
                try (HollowBlobInput hbi = HollowBlobInput.mmap(this.file, this.raf, MAX_SINGLE_BUFFER_CAPACITY, false)) {
                    byteBuffer.loadFrom(hbi, this.raf.length());
                    LOG.info("SNAP:  Closing randomaccessfile because HollowBlobInput does not manage the lifecycle (will not close) for " + file);
                    this.raf.close();
                    return byteBuffer;
                }
            } else {
                throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
            }
        }
    }
}
