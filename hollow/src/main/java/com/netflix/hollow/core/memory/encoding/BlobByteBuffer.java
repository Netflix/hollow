package com.netflix.hollow.core.memory.encoding;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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
 */
public final class BlobByteBuffer {
    private static final Logger LOG = Logger.getLogger(BlobByteBuffer.class.getName());
    public static final int MAX_SINGLE_BUFFER_CAPACITY = 1 << 30;   // largest, positive power-of-two int

    private final ByteBuffer[] spine;   // array of MappedByteBuffers
    private final long capacity;        // in bytes
    private final int shift;
    private final int mask;

    // SNAP: TODO: is this needed for destruction?
    private final FileChannel channel;

    private long position;              // within index 0 to capacity-1 in the underlying ByteBuffer

    private AtomicInteger referenceCount;

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine, FileChannel channel, AtomicInteger referenceCount) {
        this(capacity, shift, mask, spine, 0, channel, referenceCount);
    }

    private BlobByteBuffer(long capacity, int shift, int mask, ByteBuffer[] spine, long position, FileChannel channel, AtomicInteger referenceCount) {

        if (!spine[0].order().equals(ByteOrder.BIG_ENDIAN)) {
            throw new UnsupportedOperationException("Little endian memory layout is not supported");
        }

        this.capacity = capacity;
        this.shift = shift;
        this.mask = mask;
        this.channel = channel;
        this.position = position;
        this.referenceCount = referenceCount;
        this.referenceCount.getAndIncrement();

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
        return new BlobByteBuffer(this.capacity, this.shift, this.mask, this.spine, this.position, this.channel, this.referenceCount);
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
            ByteBuffer buffer = channel.map(READ_WRITE, pos, cap);
            /*
             * if (!((MappedByteBuffer) buffer).isLoaded()) // TODO(timt): make pre-fetching configurable
             *    ((MappedByteBuffer) buffer).load();
             */
            spine[i] = buffer;
        }

        return new BlobByteBuffer(size, shift, mask, spine, channel, new AtomicInteger(0));
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

    // advances pos in backing buf
    public int getBytes(long index, long len, byte[] bytes, boolean restorePos) {
        if (index >= capacity) {
            // this situation occurs when read for bits near the end of the buffer requires reading a long value that
            // extends past the buffer capacity by upto Long.BYTES bytes. To handle this case,
            // return 0 for (index >= capacity - Long.BYTES && index < capacity )
            // these zero bytes will be discarded anyway when the returned long value is shifted to get the queried bits
            LOG.warning(String.format("Unexpected read past the end, index=%s, capacity=%s", index, capacity));
        }
        int spineIndex = (int)(index >>> (shift));
        ByteBuffer buf = spine[spineIndex];
        int indexIntoBuf = (int)(index & mask);
        int toCopy = (int) Math.min(len, buf.capacity() - indexIntoBuf);
        int savePos = buf.position();
        try {
            buf.position(indexIntoBuf);
            buf.get(bytes, 0, toCopy);
            if (restorePos) {
                buf.position(savePos);
            }
        } catch (BufferUnderflowException e) {
            throw e;
        }
        return toCopy;
    }

    public int putBytes(long index, long len, byte[] bytes, boolean restorePos) {
        if (index < capacity) {
            int spineIndex = (int)(index >>> (shift));
            ByteBuffer buf = spine[spineIndex];
            int indexIntoBuf = (int)(index & mask);
            int toCopy = (int) Math.min(len, buf.capacity() - indexIntoBuf);
            int savePos = buf.position();
            buf.position(indexIntoBuf);
            buf.put(bytes, 0, toCopy);
            if (restorePos) {
                buf.position(savePos);
            }
            return toCopy;
        } else {
            assert(index < capacity + Long.BYTES);
            // this situation occurs when read for bits near the end of the buffer requires reading a long value that
            // extends past the buffer capacity by upto Long.BYTES bytes. To handle this case,
            // return 0 for (index >= capacity - Long.BYTES && index < capacity )
            // these zero bytes will be discarded anyway when the returned long value is shifted to get the queried bits
            throw new UnsupportedOperationException(String.format("Unexpected write past the end, index=%s, capacity=%s", index, capacity));
        }
    }

    public void putByte(long index, byte value) {
        if (index < 0 || index >= (this.capacity+1) << 6) {
            throw new IllegalStateException("Attempting to write a byte out of bounds");
        }

        if (index < capacity) {
            int spineIndex = (int)(index >>> (shift));
            int bufferIndex = (int)(index & mask);
            spine[spineIndex].put(bufferIndex, value);
        }
        else {
            assert(index < capacity + Long.BYTES);
            // this situation occurs when write for bits near the end of the buffer requires writing a long value that
            // extends past the buffer capacity by upto Long.BYTES bytes. To handle this case, ignore writes to
            // (index >= capacity - Long.BYTES && index < capacity )
            // these zero bytes will be discarded anyway when the returned long value is shifted to get the queried bits
            // these bytes should not hold a value
            if (value != 0) {
                if (index > capacity + Long.BYTES) {    // SNAP: can make check more strict
                    throw new IllegalStateException("Attempting to write a byte beyond the max buffer capacity");
                    // SNAP: TODO: move the inner check, and validate that value should be 0 or else those writes will be lost
                    // Just that that'll fail the testCopyBitRange unit test, but probably the right thing to do.
                }
            }
        }
    }

    /**
     * Return the long value starting from given byte index. This method is thread safe.
     * @param startByteIndex byte index (from offset 0 in the backing BlobByteBuffer) at which to start reading long value
     * @return long value
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

    public void putLong(long startByteIndex, long value) {
        int alignmentOffset = (int) (startByteIndex - this.position()) % Long.BYTES;
        long nextAlignedPos = startByteIndex - alignmentOffset + Long.BYTES;

        byte[] bytes = new byte[Long.BYTES];
        bytes[0] = (byte) (value & 0x000000ff);
        bytes[1] = (byte) ((value >>> 8)  & 0x000000ff);
        bytes[2] = (byte) ((value >>> 16) & 0x000000ff);
        bytes[3] = (byte) ((value >>> 24) & 0x000000ff);
        bytes[4] = (byte) ((value >>> 32) & 0x000000ff);
        bytes[5] = (byte) ((value >>> 40) & 0x000000ff);
        bytes[6] = (byte) ((value >>> 48) & 0x000000ff);
        bytes[7] = (byte) ((value >>> 56) & 0x000000ff);

        for (int i = 0; i < Long.BYTES; i++) {
            putByte(bigEndian(startByteIndex + i, nextAlignedPos), bytes[i]);
        }
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

    public void unmapBlob() {
        // The BlobByteBuffer backed by the initial snapshot load file will likely be referenced for a while so its ref
        // count will sustain it from getting cleaned up, but cleanup will be promptly invoked on delta blob files after
        // consumption and on per-shard per-type delta target files when it is superseded by another file in a future delta.
        if (this.referenceCount.decrementAndGet() == 0) {
            LOG.info("SNAP: Unmapping BlobByteBuffer because ref count has reached 0");
            for (int i = 0; i < spine.length; i++) {
                ByteBuffer buf = spine[i];
                if (buf != null) {
                    // SNAP: TODO: This isn't available in java 17. For now relying on System.gc(), although it seems to add
                    //             a cost on delta refresh
                    //  DirectBuffer directBuffer = (DirectBuffer) buf;
                    //  jdk.internal.ref.Cleaner cleaner = directBuffer.cleaner();
                    //  cleaner.clean();
                } else {
                    LOG.warning("SNAP: unmapBlob called on BlobByteBuffer after its already been unmapped previously. " +
                            "spine.length= " + spine.length + ", i= " + i);
                }
                spine[i] = null;
                System.gc();    // just a hint, but does seem to keep the size of mapped file region lower- both virtual and physical sizes as reported by vmmap on mac
                                // note that this also adds 2s to delta refresh thats 10s without it
            }
        }
    }

    public FileChannel getChannel() {
        return channel;
    }

    public AtomicInteger getReferenceCount() {
        return referenceCount;
    }
}
