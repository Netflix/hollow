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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private ByteBuffer[] segments;
    private final int log2OfSegmentSize;
    private final int bitmask;
    private final ArraySegmentRecycler memoryRecycler;
    private long maxIndex;

    public SegmentedByteArray(ArraySegmentRecycler memoryRecycler) {
        this.segments = new ByteBuffer[2];
        this.log2OfSegmentSize = memoryRecycler.getLog2OfByteSegmentSize();
        this.bitmask = (1 << log2OfSegmentSize) - 1;
        this.memoryRecycler = memoryRecycler;
        this.maxIndex = -1;
    }

    /**
     * Set the byte at the given index to the specified value
     * @param index the index
     * @param value the byte value
     */
    public void set(long index, byte value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of the byte at the specified index.
     * @param index the index
     * @return the byte value
     */
    public byte get(long index) {
        if (index >= this.maxIndex) {
            return 0;   // SNAP: make up for missing padding at the end of the last segment
        }
        int segmentNo = (int)(index >>> log2OfSegmentSize);
        if (segments[segmentNo] == null) {
            return 0;   // SNAP: deviation from original behavior
        }

        byte retVal = segments[segmentNo].get(segments[segmentNo].position() + (int)(index & bitmask));
        return retVal;
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * Copy bytes from the supplied InputStream into this array.
     *
     * @param raf the random access file
     * @param length the length of the data to copy
     * @throws IOException if the copy could not be performed
     */
    public void readFrom(RandomAccessFile raf, MappedByteBuffer buffer, long length) throws IOException {
        int segmentSize = 1 << log2OfSegmentSize;
        int segment = 0;

        this.maxIndex = length;
        long initLength = length;

        buffer.position((int) raf.getFilePointer());

        while(length > 0) {
            ensureCapacity(segment);
            long thisSegmentSize = Math.min(segmentSize, length);

            segments[segment] = buffer.duplicate(); // returns a new direct buffer sharing
                                                    // the same content but with different
                                                    // trackers for position etc.
            buffer.position(buffer.position() + (int) thisSegmentSize); // SNAP: long to int cast due to MappedByteBuffer constraints

            segment++;
            length -= thisSegmentSize;
        }

        raf.skipBytes((int) initLength); // SNAP: long to int cast; RandomAccessFile skipBytes takes int, although seek can take long
        // SNAP: TODO: Do I need to zero-out remaining bytes in the last segment?
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
        throw new UnsupportedOperationException();
        // SNAP: If we do get to writing, we'll have to make sure that the written data is at parity
    }

    private void orderedCopy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        throw new UnsupportedOperationException();
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
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public long size() {
        throw new UnsupportedOperationException();
    }

    public void pp(BufferedWriter debug) throws IOException {
        StringBuffer pp = new StringBuffer();

        int segmentSize = 1 << log2OfSegmentSize;
        long maxIndex = segments.length * segmentSize;


        pp.append("\n\n SegmentedByteArray get()s => ");
        for (int g = 0; g < maxIndex; g ++) {
            byte v = get(g);
            pp.append(v+ " ");
        }

        pp.append("\n");
//        pp.append("\n SegmentedByteArray raw bytes underneath:\n");
//        for (int i = 0; i < segments.length; i ++) {
//            if (segments[i] == null) {
//                pp.append("- - - - - NULL - - - - ");
//                pp.append("\n");
//                continue;
//            }
//
//            pp.append(String.format("SegmentedByteArray i= %d/%d => ", i, segments.length-1));
//
//            for (int j = 0; j < segmentSize; j ++ ) {
//                byte v = segments[i].get(segments[i].position() + j);
//                pp.append(v + " ");
//            }
//            pp.append("\n");
//        }
        debug.append(pp.toString());
    }

}
