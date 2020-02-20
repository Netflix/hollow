/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import sun.misc.Unsafe;

/**
 * A segmented byte array backs the {@link ByteData} interface with array segments, which potentially come from a pool of reusable memory.<p>
 * 
 * This ByteData can grow without allocating successively larger blocks and copying memory.<p>
 *
 * Segment length is always a power of two so that the location of a given index can be found with mask and shift operations.<p>
 *
 * Conceptually this can be thought of as a single byte array of undefined length.  The currently allocated buffer will always be
 * a multiple of the size of the segments.  The buffer will grow automatically when a byte is written to an index greater than the
 * currently allocated buffer.
 *
 * @see ArraySegmentRecycler
 *
 * @author dkoszewnik
 *
 */
@SuppressWarnings("restriction")
public class SegmentedByteArray implements ByteData {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    private MappedByteBuffer bufferRef;

    private byte[][] segments;
    private final int log2OfSegmentSize;
    private final int bitmask;
    private final ArraySegmentRecycler memoryRecycler;

    public SegmentedByteArray(ArraySegmentRecycler memoryRecycler) {
        this.segments = new byte[2][];
        this.log2OfSegmentSize = memoryRecycler.getLog2OfByteSegmentSize();
        this.bitmask = (1 << log2OfSegmentSize) - 1;
        this.memoryRecycler = memoryRecycler;
    }

    /**
     * Set the byte at the given index to the specified value
     * @param index the index
     * @param value the byte value
     */
    public void set(long index, byte value) {
        int segmentIndex = (int)(index >> log2OfSegmentSize);
        ensureCapacity(segmentIndex);
        segments[segmentIndex][(int)(index & bitmask)] = value;
    }

    /**
     * Get the value of the byte at the specified index.
     * @param index the index
     * @return the byte value
     */
    public byte get(long index) {
        return segments[(int)(index >>> log2OfSegmentSize)][(int)(index & bitmask)];
    }

    /**
     * Copy bytes from another ByteData to this array.
     *
     * @param src the source data
     * @param srcPos the position to begin copying from the source data
     * @param destPos the position to begin writing in this array
     * @param length the length of the data to copy
     */
    public void copy(ByteData src, long srcPos, long destPos, long length) {
        for(long i=0;i<length;i++) {
            set(destPos++, src.get(srcPos++));
        }
    }

    /**
     * For a SegmentedByteArray, this is a faster copy implementation.
     *
     * @param src the source data
     * @param srcPos the position to begin copying from the source data
     * @param destPos the position to begin writing in this array
     * @param length the length of the data to copy
     */
    public void copy(SegmentedByteArray src, long srcPos, long destPos, long length) {
        int segmentLength = 1 << log2OfSegmentSize;
        int currentSegment = (int)(destPos >>> log2OfSegmentSize);
        int segmentStartPos = (int)(destPos & bitmask);
        int remainingBytesInSegment = segmentLength - segmentStartPos;

        while(length > 0) {
            int bytesToCopyFromSegment = (int)Math.min(remainingBytesInSegment, length);
            ensureCapacity(currentSegment);
            int copiedBytes = src.copy(srcPos, segments[currentSegment], segmentStartPos, bytesToCopyFromSegment);

            srcPos += copiedBytes;
            length -= copiedBytes;
            segmentStartPos = 0;
            remainingBytesInSegment = segmentLength;
            currentSegment++;
        }
    }

    /**
     * copies exactly data.length bytes from this SegmentedByteArray into the provided byte array
     *
     * @param srcPos the position to begin copying from the source data
     * @param data the source data
     * @param destPos the position to begin writing in this array
     * @param length the length of the data to copy
     * @return the number of bytes copied
     */
    public int copy(long srcPos, byte[] data, int destPos, int length) {
        int segmentSize = 1 << log2OfSegmentSize;
        int remainingBytesInSegment = (int)(segmentSize - (srcPos & bitmask));
        int dataPosition = destPos;

        while(length > 0) {
            byte[] segment = segments[(int)(srcPos >>> log2OfSegmentSize)];

            int bytesToCopyFromSegment = Math.min(remainingBytesInSegment, length);

            System.arraycopy(segment, (int)(srcPos & bitmask), data, dataPosition, bytesToCopyFromSegment);

            dataPosition += bytesToCopyFromSegment;
            srcPos += bytesToCopyFromSegment;
            remainingBytesInSegment = segmentSize - (int)(srcPos & bitmask);
            length -= bytesToCopyFromSegment;
        }

        return dataPosition - destPos;
    }
    
    /**
     * checks equality for a specified range of bytes in two arrays
     * 
     * @param rangeStart the start position of the comparison range in this array
     * @param compareTo the other array to compare
     * @param cmpStart the start position of the comparison range in the other array
     * @param length the length of the comparison range
     * @return
     */
    public boolean rangeEquals(long rangeStart, SegmentedByteArray compareTo, long cmpStart, int length) {
    	for(int i=0;i<length;i++)
    		if(get(rangeStart + i) != compareTo.get(cmpStart + i))
    			return false;
    	return true;
    }

    /**
     * Copies the data from the provided source array into this array, guaranteeing that
     * if the update is seen by another thread, then all other writes prior to this call
     * are also visible to that thread.
     *
     * @param src the source data
     * @param srcPos the position to begin copying from the source data
     * @param destPos the position to begin writing in this array
     * @param length the length of the data to copy
     */
    public void orderedCopy(SegmentedByteArray src, long srcPos, long destPos, long length) {
        int segmentLength = 1 << log2OfSegmentSize;
        int currentSegment = (int)(destPos >>> log2OfSegmentSize);
        int segmentStartPos = (int)(destPos & bitmask);
        int remainingBytesInSegment = segmentLength - segmentStartPos;

        while(length > 0) {
            int bytesToCopyFromSegment = (int)Math.min(remainingBytesInSegment, length);
            ensureCapacity(currentSegment);
            int copiedBytes = src.orderedCopy(srcPos, segments[currentSegment], segmentStartPos, bytesToCopyFromSegment);

            srcPos += copiedBytes;
            length -= copiedBytes;
            segmentStartPos = 0;
            remainingBytesInSegment = segmentLength;
            currentSegment++;
        }
    }

    /**
     * copies exactly data.length bytes from this SegmentedByteArray into the provided byte array,
     * guaranteeing that if the update is seen by another thread, then all other writes prior to
     * this call are also visible to that thread.
     *
     * @param srcPos the position to begin copying from the source data
     * @param data the source data
     * @param destPos the position to begin writing in this array
     * @param length the length of the data to copy
     * @return the number of bytes copied
     */
    public int orderedCopy(long srcPos, byte[] data, int destPos, int length) {
        int segmentSize = 1 << log2OfSegmentSize;
        int remainingBytesInSegment = (int)(segmentSize - (srcPos & bitmask));
        int dataPosition = destPos;

        while(length > 0) {
            byte[] segment = segments[(int)(srcPos >>> log2OfSegmentSize)];

            int bytesToCopyFromSegment = Math.min(remainingBytesInSegment, length);

            orderedCopy(segment, (int)(srcPos & bitmask), data, dataPosition, bytesToCopyFromSegment);

            dataPosition += bytesToCopyFromSegment;
            srcPos += bytesToCopyFromSegment;
            remainingBytesInSegment = segmentSize - (int)(srcPos & bitmask);
            length -= bytesToCopyFromSegment;
        }

        return dataPosition - destPos;
    }

    /**
     * Copy bytes from the supplied InputStream into this array.
     *
     * @param is the source data
     * @param length the length of the data to copy
     * @throws IOException if the copy could not be performed
     */
    public void readFrom(InputStream is, long length) throws IOException {
        int segmentSize = 1 << log2OfSegmentSize;
        int segment = 0;

        byte scratch[] = new byte[segmentSize];

        while(length > 0) {
            ensureCapacity(segment);
            long bytesToCopy = Math.min(segmentSize, length);
            long bytesCopied = 0;
            while(bytesCopied < bytesToCopy) {
                bytesCopied += is.read(scratch, (int)bytesCopied, (int)(bytesToCopy - bytesCopied));
            }
            orderedCopy(scratch, 0, segments[segment++], 0, (int)bytesCopied);
            length -= bytesCopied;
        }
    }

    public void readFrom(RandomAccessFile raf, long length) throws IOException {
        int segmentSize = 1 << log2OfSegmentSize;
        int segment = 0;

        FileChannel channel = raf.getChannel(); // SNAP: Map MappedByteBuffer once altogether
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, raf.getFilePointer(), raf.length() - raf.getFilePointer());
        bufferRef = buffer; // hold the ref so that buffer doesn't get GC'ed

        // SNAP: simplification: assume one segment
        segments[0] = buffer.array();
        raf.skipBytes((int) length);

        throw new UnsupportedOperationException();  // temporarily throwing this until we can support multiple segments


    }

    /**
     * Write a portion of this data to an OutputStream.
     *
     * @param os the output stream to write to
     * @param startPosition the position to begin copying from this array
     * @param len the length of the data to copy
     * @throws IOException if the write to the output stream could not be performed
     */
    public void writeTo(OutputStream os, long startPosition, long len) throws IOException {
        int segmentSize = 1 << log2OfSegmentSize;
        int remainingBytesInSegment = segmentSize - (int)(startPosition & bitmask);
        long remainingBytesInCopy = len;

        while(remainingBytesInCopy > 0) {
            long bytesToCopyFromSegment = Math.min(remainingBytesInSegment, remainingBytesInCopy);

            os.write(segments[(int)(startPosition >>> log2OfSegmentSize)], (int)(startPosition & bitmask), (int)bytesToCopyFromSegment);

            startPosition += bytesToCopyFromSegment;
            remainingBytesInSegment = segmentSize - (int)(startPosition & bitmask);
            remainingBytesInCopy -= bytesToCopyFromSegment;
        }
    }

    private void orderedCopy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        int endSrcPos = srcPos + length;
        destPos += Unsafe.ARRAY_BYTE_BASE_OFFSET;

        while(srcPos < endSrcPos) {
            unsafe.putByteVolatile(dest, destPos++, src[srcPos++]);
        }
    }
    private void orderedReference(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        int endSrcPos = srcPos + length;
        destPos += Unsafe.ARRAY_BYTE_BASE_OFFSET;

        while(srcPos < endSrcPos) {
            unsafe.putByteVolatile(dest, destPos++, src[srcPos++]);
        }
    }

    /**
     * Ensures that the segment at segmentIndex exists
     *
     * @param segmentIndex the segment index
     */
    private void ensureCapacity(int segmentIndex) {
        while(segmentIndex >= segments.length) {
            segments = Arrays.copyOf(segments, segments.length * 3 / 2);
        }

        if(segments[segmentIndex] == null) {
            segments[segmentIndex] = memoryRecycler.getByteArray();
        }
    }

    public void destroy() {
        for(int i=0;i<segments.length;i++) {
            if(segments[i] != null)
                memoryRecycler.recycleByteArray(segments[i]);
        }
    }

    public long size() {
        long size = 0;
        for(int i=0;i<segments.length;i++) {
            if(segments[i] != null)
                size += segments[i].length;
        }

        return size;
    }

}
