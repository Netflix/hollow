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

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import sun.misc.Unsafe;

/**
 * A segmented long array can grow without allocating successively larger blocks and copying memory.<p>
 *
 * Segment length is always a power of two so that the location of a given index can be found with mask and shift operations.<p>
 *
 * Conceptually this can be thought of as a single long array of undefined length.  The currently allocated buffer will always be
 * a multiple of the size of the segments.  The buffer will grow automatically when a byte is written to an index greater than the
 * currently allocated buffer.
 *
 * @author dkoszewnik
 *
 */
@SuppressWarnings("restriction")
public class SegmentedLongArray {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    protected final LongBuffer[] segments;
    protected final int log2OfSegmentSize;
    protected final int bitmask;

    public SegmentedLongArray(ArraySegmentRecycler memoryRecycler, long numLongs) {
        this.log2OfSegmentSize = memoryRecycler.getLog2OfLongSegmentSize();
        int numSegments = (int)((numLongs - 1) >>> log2OfSegmentSize) + 1;
        this.bitmask = (1 << log2OfSegmentSize) - 1;
        this.segments = new LongBuffer[numSegments];
    }

    /**
     * Set the byte at the given index to the specified value
     *
     * @param index the index
     * @param value the byte value
     */
    public void set(long index, long value) {
        int segmentIndex = (int)(index >> log2OfSegmentSize);
        int longInSegment = (int)(index & bitmask);
        segments[segmentIndex].put((8 * longInSegment), value);

        /// duplicate the longs here so that we can read faster.
        if(longInSegment == 0 && segmentIndex != 0)
            segments[segmentIndex - 1].put((8 * (1 << log2OfSegmentSize)), value);
    }

    /**
     * Get the value of the byte at the specified index.
     *
     * @param index the index
     * @return the byte value
     */
    public long get(long index) {
        int segmentIndex = (int)(index >>> log2OfSegmentSize);
        return segments[segmentIndex].get((int)(index & bitmask));
    }

    public void fill(long value) {
        throw new UnsupportedOperationException();
    }

    public void writeTo(DataOutputStream dos, long numLongs) throws IOException {
        VarInt.writeVLong(dos, numLongs);

        for(long i=0;i<numLongs;i++) {
            dos.writeLong(get(i));
        }
    }

    public void destroy(ArraySegmentRecycler memoryRecycler) {
        throw new UnsupportedOperationException();
    }


    public static SegmentedLongArray deserializeFrom(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        long numLongs = VarInt.readVLong(dis);

        SegmentedLongArray arr = new SegmentedLongArray(memoryRecycler, numLongs);

        arr.readFrom(dis, memoryRecycler, numLongs);

        return arr;
    }

    // SNAP: TODO: verify correctness
    protected void readFrom(RandomAccessFile raf, MappedByteBuffer buffer, ArraySegmentRecycler memoryRecycler, long numLongs) throws IOException {
        int segmentSize = 1 << memoryRecycler.getLog2OfLongSegmentSize();
        int segment = 0;

        if(numLongs == 0)
            return;

        raf.skipBytes((int) numLongs * 8);   // raf has to be advanced independently of buffer

        while(numLongs > 0) {
            long longsReferenced = 0;
            long longsToReference = Math.min(segmentSize, numLongs);

            // Can't call put, because that copies over: segments[segment].put(fencepostLong);
            segments[segment] = buffer.asLongBuffer();

            buffer.position(buffer.position() + (int) (longsToReference * 8));
            longsReferenced = longsToReference;

            // if(numLongs > longsReferenced) {
            //     buffer.position(buffer.position() - (1*8)); // SNAP: don't do this
            // }

            segment++;
            numLongs -= longsReferenced;

        }

        // SNAP: POTENTIAL BUG: last segment isn't padded with zeros
    }

    protected void readFrom(DataInputStream dis, ArraySegmentRecycler memoryRecycler, long numLongs) throws IOException {
        throw new UnsupportedOperationException();
    }
}
