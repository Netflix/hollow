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
    public static StagedVariableLengthData stage(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws FileNotFoundException {
        return new StagedVariableLengthData(memoryMode, memoryRecycler);
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


    public static class StagedVariableLengthData {
        private final MemoryMode memoryMode;
        private final SegmentedByteArray byteArray;
        private final RandomAccessFile raf;

        public RandomAccessFile getRaf() {
            return raf;
        }

        public StagedVariableLengthData(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws FileNotFoundException {
            this.memoryMode = memoryMode;
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
                EncodedByteBuffer encodedByteBuffer = (EncodedByteBuffer) src;
                // TODO: zero-copy from delta to target raf
                try {
                    // if (length > 0
                    //         && encodedByteBuffer.getBufferView() != null
                    //         && encodedByteBuffer.getBufferView().getChannel() != null
                    //         && encodedByteBuffer.getBufferView().getChannel().isOpen()) {
                    //     FileChannel readableChannel = encodedByteBuffer.getBufferView().getChannel();
                    //     long savePos = readableChannel.position();
                    //     readableChannel.position(srcPos);
                    //     this.raf.getChannel().transferFrom(readableChannel, destPos, length);
                    //     readableChannel.position(savePos);
                    // } else {
                        byte[] chunk = new byte[16384];    // SNAP: page size returned by vm_stat on mac
                        while (length > 0) {
                            int toReadBytes = (int) Math.min(length, (long) chunk.length);
                            int readBytes = encodedByteBuffer.getBytes(srcPos, toReadBytes, chunk);
                            length -= readBytes;
                            srcPos += readBytes;
                            this.raf.write(chunk, 0, readBytes);
                        }
                    // }
                } catch (NullPointerException e) {
                    LOG.warning("E");
                    throw e;
                }
                //  FileChannel readableChannel = encodedByteBuffer.getBufferView().getChannel();
                //  if (readableChannel.isOpen()) {
                //   long savePos = readableChannel.position();
                //   readableChannel.position(srcPos);
                //   this.raf.getChannel().transferFrom(readableChannel, destPos, length);
                //   readableChannel.position(savePos);
                //  }
                // TODO: this is much faster than a byte at a time, but still slow (10x of in-memory delta on my mac, for e.g. for 1G blob size <1s vs. 9s for delta update)

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
                HollowBlobInput hbi = HollowBlobInput.mmap(this.raf, MAX_SINGLE_BUFFER_CAPACITY);
                byteBuffer.loadFrom(hbi, this.raf.length());
                return byteBuffer;
            } else {
                throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
            }
        }
    }
}
