package com.netflix.hollow.core.memory.encoding;

import static java.lang.Integer.highestOneBit;
import static java.lang.Long.numberOfLeadingZeros;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

    private static final int MAX_BUFFER_CAPACITY = 1 << 30; // largest, positive power-of-two int

    public static BlobByteBuffer mmapBlob(FileChannel channel) throws IOException {
        long size = channel.size();

        // divide into N buffers with an int capacity that is a power of 2
        final int bufferCapacity = size > (long) MAX_BUFFER_CAPACITY
                ? MAX_BUFFER_CAPACITY
                : highestOneBit((int) size);
        long bufferCount = size % bufferCapacity == 0
                ? size / (long)bufferCapacity
                : (size / (long)bufferCapacity) + 1;
        if (bufferCount > Integer.MAX_VALUE)
            throw new IllegalArgumentException("file too large; size=" + size);

        int shift = 31 - numberOfLeadingZeros(bufferCapacity); // log2
        int mask = (1 << shift) - 1;
        ByteBuffer[] spine = new MappedByteBuffer[(int)bufferCount];
        for (int i = 0; i < bufferCount; i++) {
            long pos = (long)i * bufferCapacity;
            int cap = i == (bufferCount - 1)
                    ? (int)(size - pos)
                    : bufferCapacity;
            ByteBuffer buffer = channel.map(READ_ONLY, pos, cap);
            /*
            if (!buffer.isLoaded()) // TODO(timt): make pre-fetching configurable
                buffer.load();
             */
            spine[i] = buffer;
        }

        return new BlobByteBuffer(size, shift, mask, spine);
    }

    private final ByteBuffer[] spine;
    private final long capacity;
    private final int shift;
    private final int mask;

    private byte[] lbuff = new byte[8];  // buffer when reading int and long
    private byte[] bbuff = new byte[80]; // buffer when reading UTF
    private char[] cbuff = new char[80]; // buffer when reading UTF

    private long position;  // 0 to capacity-1

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine) {
        this(capacity, shift, mask, spine, 0);
    }

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine, long position) {
        this.capacity = capacity;
        this.shift = shift;
        this.mask = mask;
        this.position = position;

        /// The following assignment is purposefully placed *after* the population of all segments.
        /// The final assignment after the initialization of the array guarantees that no thread
        /// will see any of the array elements before assignment.
        /// We can't risk the segment values being visible as null to any thread, because
        /// FixedLengthElementArray uses Unsafe to access these values, which would cause the
        /// JVM to crash with a segmentation fault.
        this.spine = spine;
    }

    public long position() {
        return this.position;
    }

    // @param position in bytes
    public BlobByteBuffer position(long position) {
        if (position >= capacity || position < 0)
            throw new IllegalArgumentException("invalid position; position=" + position + " capacity=" + capacity);
        this.position = position;
        return this;
    }

    // @param position in bytes from offset 0 in the backing BlobByteBuffer
    public byte getByte(long position) throws BufferUnderflowException {
        int spineIndex = (int)(position >>> shift);
        int bufferPosition = (int)(position & mask);
        return spine[spineIndex].get(bufferPosition);
    }

    // @param position byte position from offset 0 in the backing BlobByteBuffer
    public long getLong(long position) throws BufferUnderflowException {
        int spineIndex = (int)(position >>> shift);
        int bufferPosition = (int)(position & mask);

        if (!(bufferPosition + 8 < this.capacity))
            throw new IllegalStateException();

        boolean aligned = position % 8 == 0;
        if (aligned) {
            return spine[spineIndex].getLong(bufferPosition);
        }
        else {
            long snapRead = 0l;
            int alignedLongByte = bufferPosition & 0xFFFFFFF8;
            int nextAlignedLongByte = alignedLongByte + 8;
            long[] alignedLongs = new long[2];
            alignedLongs[0] = spine[spineIndex].getLong(alignedLongByte);   // this aligned long is definitely in current spine bucket
            alignedLongs[1] = getLong(nextAlignedLongByte); // this aligned long may be in next spine bucket

            System.out.println("SNAP: alignedLongs[0] bytes= " + ppBytesInLong(alignedLongs[0]));
            System.out.println("SNAP: alignedLongs[1] bytes= " + ppBytesInLong(alignedLongs[1]));


            long offsetIntoLongArray = bufferPosition & 0x07;
            long unalignedLongVal = unsafe.getLong(alignedLongs, (long) Unsafe.ARRAY_LONG_BASE_OFFSET + offsetIntoLongArray);

            // if (bufferPosition < MAX_BUFFER_CAPACITY - 8) {      // SNAP: Test this too
            //     snapRead = spine[spineIndex].getLong(bufferPosition);
            //     if (snapRead == unalignedLongVal) {
            //         System.out.println("Simplify !!!");
            //     }
            // }

            return unalignedLongVal;
        }
    }

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

    public BlobByteBuffer duplicate() {
        return new BlobByteBuffer(this.capacity, this.shift, this.mask, this.spine, this.position);
    }


//     public void get(byte[] dst, int offset, int length) {
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
//             spine[spineIndex].get(dst, 0, remaining);
//             spine[spineIndex + 1].get(dst, remaining, length - remaining);
//         } else {
//             spine[spineIndex].get(dst, offset, length);
//         }
//         position += length;
//     }

//
//     /**
//      * @see VarInt#readVInt(InputStream)
//      */
//     public int readVInt() throws BufferUnderflowException {
//         byte b = get();
//
//         if(b == (byte) 0x80)
//             throw new RuntimeException("Attempting to read null value as int");
//
//         int value = b & 0x7F;
//         while ((b & 0x80) != 0) {
//             b = get();
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
//         get(lbuff, 0, 8);
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
//         byte b = get();
//
//         if(b == (byte) 0x80)
//             throw new RuntimeException("Attempting to read null value as long");
//
//         long value = b & 0x7F;
//         while ((b & 0x80) != 0) {
//             b = get();
//             value <<= 7;
//             value |= (b & 0x7F);
//         }
//         return value;
//     }
//
}
