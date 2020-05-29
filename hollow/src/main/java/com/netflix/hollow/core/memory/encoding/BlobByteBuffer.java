package com.netflix.hollow.core.memory.encoding;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

/**
 * <p>A stitching of {@link MappedByteBuffer}s to operate on large memory mapped blobs. Not threadsafe.</p>
 *
 * <p>The largest blob size supported is ~2 exobytes. Presumably other limits in Hollow or practical limits
 * are reached before encountering this limit.</p>
 */
// FIXME(timt): ByteBuffer isn't thread safe, but it *is* safe to share the underlying byte arrays, e.g
//              to split() from a progenitor buffer
public final class BlobByteBuffer {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();
    public static final int MAX_SINGLE_BUFFER_CAPACITY = 1 << 30; // largest, positive power-of-two int

    private final ByteBuffer[] spine;   // array of MappedByteBuffer
    private final long capacity;
    private final int shift;
    private final int mask;
    private long position;  // within index 0 to capacity-1 in the underlying ByteBuffer

    Lock lock1 = new ReentrantLock();

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine) {
        this(capacity, shift, mask, spine, 0);
    }

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine, long position) {

        if (!spine[0].order().equals(ByteOrder.BIG_ENDIAN)) {
            throw new UnsupportedOperationException("Little endian memory layout is not supported");
        }

        this.capacity = capacity;
        this.shift = shift;
        this.mask = mask;
        this.position = position;

        /// The following assignment is purposefully placed *after* the population of all segments.
        /// The final assignment after the initialization of the array guarantees that no thread
        /// will see any of the array elements before assignment.
        /// We can't risk the segment values being visible as null to any thread, because
        /// FixedLengthData uses Unsafe to access these values, which would cause the
        /// JVM to crash with a segmentation fault.
        this.spine = spine;
    }

    /**
     * Returns a view on the current {@code BlobByteBuffer} as a new {@code BlobByteBuffer}.
     * The returned buffer's capacity, shift, mark, spine, and position will be identical to those of this buffer.
     * @return a new {@code BlobByteBuffer} which is view on the current {@code BlobByteBuffer}
     */
    public BlobByteBuffer duplicate() {
        lock1.lock();
        try {
            return new BlobByteBuffer(this.capacity, this.shift, this.mask, this.spine, this.position);
        } finally {
            lock1.unlock();
        }
    }

    public static BlobByteBuffer mmapBlob(FileChannel channel, int singleBufferCapacity) throws IOException {
        long size = channel.size();
        if (size == 0) {
            throw new IllegalStateException("File to be mmap-ed has no data");
        }

        // divide into N buffers with an int capacity that is a power of 2
        final int bufferCapacity = size > (long) singleBufferCapacity
                ? singleBufferCapacity
                : Integer.highestOneBit((int) size);
        long bufferCount = size % bufferCapacity == 0
                ? size / (long)bufferCapacity
                : (size / (long)bufferCapacity) + 1;
        if (bufferCount > Integer.MAX_VALUE)
            throw new IllegalArgumentException("file too large; size=" + size);

        int shift = 31 - Integer.numberOfLeadingZeros(bufferCapacity); // log2
        int mask = (1 << shift) - 1;
        ByteBuffer[] spine = new MappedByteBuffer[(int)bufferCount];
        for (int i = 0; i < bufferCount; i++) {
            long pos = (long)i * bufferCapacity;
            int cap = i == (bufferCount - 1)
                    ? (int)(size - pos)
                    : bufferCapacity;
            ByteBuffer buffer = channel.map(READ_ONLY, pos, cap);
            /*
             * if (!((MappedByteBuffer) buffer).isLoaded()) // TODO(timt): make pre-fetching configurable
             *    ((MappedByteBuffer) buffer).load();
             */
            spine[i] = buffer;
        }

        return new BlobByteBuffer(size, shift, mask, spine);
    }

    public long position() {
        lock1.lock();
        try {
            return this.position;
        } finally {
            lock1.unlock();
        }

    }

    // @param position in bytes
    public BlobByteBuffer position(long position) {
        if (position > capacity || position < 0)
            throw new IllegalArgumentException("invalid position; position=" + position + " capacity=" + capacity);
        this.position = position;
        return this;
    }

    // @param index position in bytes from offset 0 in the backing BlobByteBuffer
    public byte getByte(long index) throws BufferUnderflowException {
        lock1.lock();
        if (index >= capacity) {  // defensive
            throw new IllegalStateException();
        }
        try {
            int spineIndex = (int)(index >>> (shift));
            int bufferIndex = (int)(index & mask);
            return spine[spineIndex].get(bufferIndex);
        } finally {
            lock1.unlock();
        }
    }

    /**
     * BlobByteBuffer is an array of ByteBuffers. For longs that start at 8-byte boundaries relative to the start of the
     * ByteBuffer (note that this is not necessarily 8-byte aligned with the backing BlobByteBuffer), ByteBuffer::getLong
     * returns a valid result. However for unaligned reads i.e. reading a long starting at a byte that is not at an 8-byte
     * multiple offset relative to the start of the buffer, two aligned longs need to be read using ByteBuffer::getLong,
     * serialized into a byte array, and then an unsafe.getLong needs to be performed into that array. Moreover, the
     * two longs (or single unaligned long) could occur at the boundary between two spine buckets.
     *
     * @param startByteIndex
     * @return
     * @throws BufferUnderflowException
     */
    // Return long starting at given byte index
    // @param startByteIndex long position from offset 0 in the backing BlobByteBuffer
    public long getLongVerbose(long startByteIndex) throws BufferUnderflowException {
        lock1.lock();
        try {
            int spineIndex = (int)(startByteIndex >>> (shift));
            int bufferByteIndex = (int)(startByteIndex & mask);
            int alignmentOffset = (int)(startByteIndex - this.position()) % 8;

            if (bufferByteIndex + Long.BYTES > this.capacity)  // defensive
                throw new IllegalStateException();

            long longVal;

            // SNAP: TEMP:
            // long l = spine[spineIndex].getLong(bufferByteIndex);

            if (alignmentOffset == 0 && bufferByteIndex + 8 <= spine[spineIndex].capacity()) {
                longVal = spine[spineIndex].getLong(bufferByteIndex);   // SNAP: IndexOutOfBoundsException could occur here if bufferByteIndex is within 8 bytes of spine[spineIndex].capacity
            } else if (alignmentOffset == 0) {  // if offset of long to be read is aligned in the underlying ByteBuffer but falls within last 8 bytes of the spine element capacity
                longVal = BlobByteBufferUnalignedUtils.getAlignedLongAcrossSpineBoundary(spine[spineIndex], spine[spineIndex+1], bufferByteIndex);
            }
            else {  // unaligned read
                long[] longs = new long[2];
                int firstBufferOffset = bufferByteIndex - alignmentOffset; // SNAP: if this is negative then have to fetch the previous spine element
                if (firstBufferOffset < 0) {    // if first aligned byte is in the previous spine bucket
                    if (spineIndex == 0) {
                        throw new IllegalStateException();
                    }
                    ByteBuffer prevByteBuffer = spine[spineIndex - 1];
                    ByteBuffer thisByteBuffer = spine[spineIndex];
                    longs[0] = BlobByteBufferUnalignedUtils.getAlignedLongAcrossSpineBoundary(prevByteBuffer, thisByteBuffer, prevByteBuffer.capacity() + firstBufferOffset);
                } else {    // if first aligned byte is in the current spine bucket
                    ByteBuffer thisByteBuffer = spine[spineIndex];
                    ByteBuffer nextByteBuffer = (spineIndex + 1 < spine.length) ? spine[spineIndex + 1] : null;
                    longs[0] = BlobByteBufferUnalignedUtils.getAlignedLongAcrossSpineBoundary(thisByteBuffer, nextByteBuffer, firstBufferOffset);
                }

                int secondBufferOffset = firstBufferOffset + Long.BYTES;
                if ((secondBufferOffset & mask) == secondBufferOffset) {    // if next aligned long is in the same spine bucket
                    if (secondBufferOffset + Long.BYTES <= spine[spineIndex].capacity())
                        longs[1] = spine[spineIndex].getLong(secondBufferOffset);   // optimization, as opposed to using getAlignedLongAcrossSpineBoundary() which copies individual bytes
                    else
                        longs[1] = BlobByteBufferUnalignedUtils.getAlignedLongAcrossSpineBoundary(spine[spineIndex], spine[spineIndex+1], secondBufferOffset);
                } else {
                    if (spineIndex + 1 < spine.length)
                        longs[1] = spine[spineIndex + 1].getLong(spine[spineIndex + 1].position() + (secondBufferOffset & mask)); // read in aligned long from the next spine bucket
                    else
                        throw new IllegalStateException("Attempting to read unaligned long starting in the last 8 bytes of data");
                }
                longVal = unsafe.getLong(longs, Unsafe.ARRAY_LONG_BASE_OFFSET + alignmentOffset);
                // if (l == longVal) {
                //     int a = 5;
                // }
            }
            return longVal;
        } finally {
            lock1.unlock();
        }

    }

    // get 8 bytes corresponding to a long and construct the long
    public long makeLong(long startByteIndex, byte[] bytes) {
        int alignmentOffset = (int)(startByteIndex - this.position()) % 8;
        long nextAlignedByte = startByteIndex - alignmentOffset + 8;
        for (int i = 0; i < 8; i ++ ) {
            bytes[i] = getByte(withEndiannness(startByteIndex + i, nextAlignedByte));
        }
        return BlobByteBufferUnalignedUtils.toLong(bytes);
    }

    // assume big endianness, it is validated in the constructor
    public long withEndiannness(long index, long boundary) {
        long result;
        if (index < boundary) {
            result = (boundary - 8) + (boundary - index) - 1;
        } else {
            result = boundary + (boundary + 8 - index) - 1;
        }
        return result;
    }

    // Return long starting at given byte index
    // @param startByteIndex long position from offset 0 in the backing BlobByteBuffer
    public long getLong(long startByteIndex) throws BufferUnderflowException {
        long expected = getLongVerbose(startByteIndex);

        lock1.lock();
        try {
            int bufferByteIndex = (int)(startByteIndex & mask);
            if (bufferByteIndex + Long.BYTES > this.capacity)  // defensive
                throw new IllegalStateException();

            int alignmentOffset = (int)(startByteIndex - this.position()) % 8;

            byte[] makeLongBytes = new byte[8];
            long actual = makeLong(startByteIndex, makeLongBytes);


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeLong(expected);
            dos.flush();
            byte[] expectedBytes = bos.toByteArray();

            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            DataOutputStream dos2 = new DataOutputStream(bos2);
            dos2.writeLong(actual);
            dos2.flush();
            byte[] actualbytes = bos2.toByteArray();

            if (actual == expected) {
                return actual;
            } else {
                byte[] reversedMakeLongBytes = new byte[8];
                for (int i=0; i<8;i ++) {
                    reversedMakeLongBytes[i] = makeLongBytes[7-i];
                }
                if (!Arrays.equals(reversedMakeLongBytes, expectedBytes)) {
                    System.out.println("Stop Here");
                }
                return BlobByteBufferUnalignedUtils.toLong(reversedMakeLongBytes);
                // return expected;    // SNAP: bytes match but need to deserialize to long correctly
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            lock1.unlock();
        }

    }


    public static class BlobByteBufferUnalignedUtils {

        public static long toLong2(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            // buffer.position(0);
            buffer.put(bytes, 0, bytes.length);
            buffer.flip();
            return buffer.getLong();
        }

        public static long toLong(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            // buffer.position(0);
            buffer.put(bytes, 0, bytes.length);
            buffer.flip();
            return buffer.getLong();
        }

        public static long getAlignedLongAcrossSpineBoundary(ByteBuffer currBuffer, ByteBuffer nextBuffer, int offset) {
            byte[] bytes = new byte[Long.BYTES];
            int pickFromCurrent = currBuffer.capacity() - offset;
            if (pickFromCurrent > 8)
                pickFromCurrent = 8;

            for (int i = 0; i < pickFromCurrent; i++) {
                bytes[i] = currBuffer.get(offset + i);
            }
            int pickFromNext = 8 - pickFromCurrent;
            for (int i = 0; i < pickFromNext; i++)
                bytes[pickFromCurrent + i] = nextBuffer.get(nextBuffer.position() + i);

            return toLong2(bytes);
        }
    }


    private byte[] lbuff = new byte[8];  // buffer when reading int and long
    private byte[] bbuff = new byte[80]; // buffer when reading UTF
    private char[] cbuff = new char[80]; // buffer when reading UTF

    public static String ppBytesInLong(long l) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeLong(l);
            dos.flush();
            byte[] nativeLongBytes = bos.toByteArray();
            String bytesChosenForUnalignedRead = "";
            for (byte b : nativeLongBytes) {
                bytesChosenForUnalignedRead += b + " ";
            }
            return bytesChosenForUnalignedRead;
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

//     public void deserializeFrom(byte[] dst, int offset, int length) {
//         if ((offset | length | (offset + length) | (dst.length - (offset + length))) < 0)
//             throw new IndexOutOfBoundsException();
//         if (position + length >= capacity)
//             throw new BufferUnderflowException();
//
//         int spineIndex = (int)(position >>> shift);
//         int bufferPosition = (int)(position & mask);
//
//         // FIXME(timt): mixing absolute (in other methods) and relative gets
//         spine[spineIndex].position(bufferPosition);
//
//         int remaining = spine[spineIndex].remaining();
//
//         if (length > remaining) { // spans two buffers
//             // FIXME(timt): code review for off-by-one or other errors; even better, unit test it
//             spine[spineIndex].deserializeFrom(dst, 0, remaining);
//             spine[spineIndex + 1].deserializeFrom(dst, remaining, length - remaining);
//         } else {
//             spine[spineIndex].deserializeFrom(dst, offset, length);
//         }
//         position += length;
//     }

//
//     /**
//      * @see VarInt#readVInt(InputStream)
//      */
//     public int readVInt() throws BufferUnderflowException {
//         byte b = deserializeFrom();
//
//         if(b == (byte) 0x80)
//             throw new RuntimeException("Attempting to read null value as int");
//
//         int value = b & 0x7F;
//         while ((b & 0x80) != 0) {
//             b = deserializeFrom();
//             value <<= 7;
//             value |= (b & 0x7F);
//         }
//
//         return value;
//     }
//
//     /**
//      * @see DataInput#readLong()
//      */
//     public long readLong() throws BufferUnderflowException {
//         deserializeFrom(lbuff, 0, 8);
//         return  (long)(lbuff[0]       ) << 56 |
//                 (long)(lbuff[1] & 0xFF) << 48 |
//                 (long)(lbuff[2] & 0xFF) << 40 |
//                 (long)(lbuff[3] & 0xFF) << 32 |
//                 (long)(lbuff[4] & 0xFF) << 24 |
//                 (long)(lbuff[5] & 0xFF) << 16 |
//                 (long)(lbuff[6] & 0xFF) <<  8 |
//                 (long)(lbuff[7] & 0xFF);
//     }
//
//     /**
//      * @see VarInt#readVLong(InputStream)
//      */
//     public long readVLong() throws BufferUnderflowException {
//         byte b = deserializeFrom();
//
//         if(b == (byte) 0x80)
//             throw new RuntimeException("Attempting to read null value as long");
//
//         long value = b & 0x7F;
//         while ((b & 0x80) != 0) {
//             b = deserializeFrom();
//             value <<= 7;
//             value |= (b & 0x7F);
//         }
//         return value;
//     }
//
}
