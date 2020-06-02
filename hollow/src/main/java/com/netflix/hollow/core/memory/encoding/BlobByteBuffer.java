package com.netflix.hollow.core.memory.encoding;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * <p>A stitching of {@link MappedByteBuffer}s to operate on large memory mapped blobs. {@code MappedByteBuffer} is
 * limited to mapping memory of integral size. Note that that JDK 14 will introduce improved API for accessing foreign
 * memory and replace {@code MappedByteBuffer}.
 *
 * This class is not thread safe, but it *is* safe to share the underlying Byte Buffers for parallel reads</p>
 *
 * <p>The largest blob size supported is ~2 exobytes. Presumably other limits in Hollow or practical limits
 * are reached before encountering this limit.</p>
 *
 * @author Sunjeet Singh
 *         Tim Taylor
 *
 */
public final class BlobByteBuffer {

    public static final int MAX_SINGLE_BUFFER_CAPACITY = 1 << 30;   // largest, positive power-of-two int

    private final ByteBuffer[] spine;   // array of MappedByteBuffers
    private final long capacity;        // in bytes
    private final int shift;
    private final int mask;

    private long position;              // within index 0 to capacity-1 in the underlying ByteBuffer

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

        // The following assignment is purposefully placed *after* the population of all segments (this method is called
        // after mmap). The final assignment after the initialization of the array of MappedByteBuffers guarantees that
        // no thread will see any of the array elements before assignment.
        this.spine = spine;
    }

    /**
     * Returns a view on the current {@code BlobByteBuffer} as a new {@code BlobByteBuffer}.
     * The returned buffer's capacity, shift, mark, spine, and position will be identical to those of this buffer.
     * @return a new {@code BlobByteBuffer} which is view on the current {@code BlobByteBuffer}
     */
    public BlobByteBuffer duplicate() {
        return new BlobByteBuffer(this.capacity, this.shift, this.mask, this.spine, this.position);
    }

    /**
     * mmap the entire contents of FileChannel into an array of {@code MappedByteBuffer}s, each of size singleBufferCapacity.
     * @param channel FileChannel for file to be mmap-ed
     * @param singleBufferCapacity Size of individual MappedByteBuffers in array of {@code MappedByteBuffer}s required
     *                to map the entire file channel. It must be a power of 2, and due to {@code MappedByteBuffer}
     *                constraints it is limited to the max integer that is a power of 2.
     * @return BlobByteBuffer containing an array of {@code MappedByteBuffer}s that mmap-ed the entire file channel
     * @throws IOException
     */
    public static BlobByteBuffer mmapBlob(FileChannel channel, int singleBufferCapacity) throws IOException {
        long size = channel.size();
        if (size == 0) {
            throw new IllegalStateException("File to be mmap-ed has no data");
        }
        if ((singleBufferCapacity & (singleBufferCapacity - 1)) != 0) { // should be a power of 2
            throw new IllegalArgumentException("singleBufferCapacity must be a power of 2");
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

    /**
     * Return position in bytes.
     * @return position in bytes
     */
    public long position() {
        return this.position;
    }

    /**
     * Set position, in bytes.
     * @param position the byte index to set position to
     * @return new position in bytes
     */
    public BlobByteBuffer position(long position) {
        if (position > capacity || position < 0)
            throw new IllegalArgumentException("invalid position; position=" + position + " capacity=" + capacity);
        this.position = position;
        return this;
    }

    /**
     * Reads the byte at the given index.
     * @param index byte index (from offset 0 in the backing BlobByteBuffer) at which to read byte value
     * @return byte at the given index
     * @throws IndexOutOfBoundsException if index out of bounds of the backing buffer
     */
    public byte getByte(long index) throws BufferUnderflowException {
        if (index < capacity) {
            int spineIndex = (int)(index >>> (shift));
            int bufferIndex = (int)(index & mask);
            return spine[spineIndex].get(bufferIndex);
        }
        else {
            assert(index < capacity + Long.BYTES);
            // this situation occurs when read for bits near the end of the buffer requires reading a long value that
            // extends past the buffer capacity by upto Long.BYTES bytes. To handle this case,
            // return 0 for (index >= capacity - Long.BYTES && index < capacity )
            // these zero bytes will be discarded anyway when the returned long value is shifted to get the queried bits
            return (byte) 0;
        }
    }

    /**
     * Return the long value starting from given byte index. This method is thread safe.
     * @param startByteIndex byte index (from offset 0 in the backing BlobByteBuffer) at which to start reading long value
     * @returns long value
     */
    public long getLong(long startByteIndex) throws BufferUnderflowException {

        int alignmentOffset = (int)(startByteIndex - this.position()) % Long.BYTES;
        long nextAlignedPos = startByteIndex - alignmentOffset + Long.BYTES;

        byte[] bytes = new byte[Long.BYTES];
        for (int i = 0; i < Long.BYTES; i ++ ) {
            bytes[i] = getByte(bigEndian(startByteIndex + i, nextAlignedPos));
        }

        return ((((long) (bytes[7]       )) << 56) |
                (((long) (bytes[6] & 0xff)) << 48) |
                (((long) (bytes[5] & 0xff)) << 40) |
                (((long) (bytes[4] & 0xff)) << 32) |
                (((long) (bytes[3] & 0xff)) << 24) |
                (((long) (bytes[2] & 0xff)) << 16) |
                (((long) (bytes[1] & 0xff)) <<  8) |
                (((long) (bytes[0] & 0xff))      ));
    }

    /**
     * Given big-endian byte order, returns the position into the buffer for a given byte index. Java nio DirectByteBuffers
     * are by default big-endian. Big-endianness is validated in the constructor.
     * @param index byte index
     * @param boundary index of the next 8-byte aligned byte
     * @return position in buffer
     */
    private long bigEndian(long index, long boundary) {
        long result;
        if (index < boundary) {
            result = (boundary - Long.BYTES) + (boundary - index) - 1;
        } else {
            result = boundary + (boundary + Long.BYTES - index) - 1;
        }
        return result;
    }
}
