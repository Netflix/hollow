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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.util.Arrays;

public class GrowingSegmentedLongArray {

    private long[][] segments;
    private final int log2OfSegmentSize;
    private final int bitmask;
    
    private final ArraySegmentRecycler memoryRecycler;

    public GrowingSegmentedLongArray(ArraySegmentRecycler memoryRecycler) {
        this.memoryRecycler = memoryRecycler;
        this.log2OfSegmentSize = memoryRecycler.getLog2OfLongSegmentSize();
        this.bitmask = (1 << log2OfSegmentSize) - 1;
        this.segments = new long[64][];
    }

    /**
     * Set the byte at the given index to the specified value
     * @param index the index
     * @param value the byte
     */
    public void set(long index, long value) {
        throw new UnsupportedOperationException();
        // int segmentIndex = (int)(index >> log2OfSegmentSize);
        //
        // if(segmentIndex >= segments.length) {
        //     int nextPowerOfTwo = 1 << (32 - Integer.numberOfLeadingZeros(segmentIndex));
        //     segments = Arrays.copyOf(segments, nextPowerOfTwo);
        // }
        //
        // if(segments[segmentIndex] == null) {
        //     segments[segmentIndex] = memoryRecycler.getLongArray();
        // }
        //
        // int longInSegment = (int)(index & bitmask);
        // segments[segmentIndex][longInSegment] = value;
    }

    /**
     * Get the value of the byte at the specified index.
     * @param index the index
     * @return the byte value
     */
    public long get(long index) {
        throw new UnsupportedOperationException();
//        int segmentIndex = (int)(index >> log2OfSegmentSize);
//
//        if(segmentIndex >= segments.length || segments[segmentIndex] == null)
//            return 0;
//
//        int longInSegment = (int)(index & bitmask);
//        return segments[segmentIndex][longInSegment];
    }
    
    
    public void destroy() {
        throw new UnsupportedOperationException();
//        for(int i=0;i<segments.length;i++) {
//            if(segments[i] != null)
//                memoryRecycler.recycleLongArray(segments[i]);
//        }
    }

}
