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
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    protected final long[][] segments;
    protected final int log2OfSegmentSize;
    protected final int bitmask;

    public SegmentedLongArray(ArraySegmentRecycler memoryRecycler, long numLongs) {
        this.log2OfSegmentSize = memoryRecycler.getLog2OfLongSegmentSize();
        int numSegments = (int)((numLongs - 1) >>> log2OfSegmentSize) + 1;
        long[][] segments = new long[numSegments][];
        this.bitmask = (1 << log2OfSegmentSize) - 1;

        for(int i=0;i<segments.length;i++) {
            segments[i] = memoryRecycler.getLongArray();
        }

        /// The following assignment is purposefully placed *after* the population of all segments.
        /// The final assignment after the initialization of the array guarantees that no thread
        /// will see any of the array elements before assignment.
        /// We can't risk the segment values being visible as null to any thread, because
        /// FixedLengthElementArray uses Unsafe to access these values, which would cause the
        /// JVM to crash with a segmentation fault.
        this.segments = segments;
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
        unsafe.putOrderedLong(segments[segmentIndex], (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (8 * longInSegment), value);

        /// duplicate the longs here so that we can read faster.
        if(longInSegment == 0 && segmentIndex != 0)
            unsafe.putOrderedLong(segments[segmentIndex - 1], (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (8 * (1 << log2OfSegmentSize)), value);
    }

    /**
     * Get the value of the byte at the specified index.
     *
     * @param index the index
     * @return the byte value
     */
    public long get(long index) {
        int segmentIndex = (int)(index >>> log2OfSegmentSize);
        long ret = segments[segmentIndex][(int)(index & bitmask)];

        return ret;
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

    public void fill(long value) {
        for(int i=0;i<segments.length;i++) {
            long offset = Unsafe.ARRAY_LONG_BASE_OFFSET;
            for(int j=0;j<segments[i].length;j++) {
                unsafe.putOrderedLong(segments[i], offset, value);
                offset += 8;
            }
        }
    }

    public void writeTo(DataOutputStream dos, long numLongs) throws IOException {
        VarInt.writeVLong(dos, numLongs);

        for(long i=0;i<numLongs;i++) {
            dos.writeLong(get(i));
        }
    }

    public void destroy(ArraySegmentRecycler memoryRecycler) {
        for(int i=0;i<segments.length;i++) {
            if(segments[i] != null)
                memoryRecycler.recycleLongArray(segments[i]);
        }
    }

    protected void readFrom(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, long numLongs) throws
            IOException {
        int segmentSize = 1 << memoryRecycler.getLog2OfLongSegmentSize();
        int segment = 0;

        if(numLongs == 0)
            return;

        long fencepostLong = in.readLong();

        while(numLongs > 0) {
            long longsToCopy = Math.min(segmentSize, numLongs);

            unsafe.putOrderedLong(segments[segment], (long) Unsafe.ARRAY_LONG_BASE_OFFSET, fencepostLong);

            int longsCopied = 1;

            while(longsCopied < longsToCopy) {
                long l = in.readLong();
                unsafe.putOrderedLong(segments[segment], (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (8 * longsCopied++), l);
            }

            if(numLongs > longsCopied) {
                unsafe.putOrderedLong(segments[segment], (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (8 * longsCopied), in.readLong());
                fencepostLong = segments[segment][longsCopied];
            }

            segment++;
            numLongs -= longsCopied;
        }
    }
}
