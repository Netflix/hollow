/*
 *  Copyright 2026 New Relic
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

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Test;

public class SegmentedByteArrayTest {

    // SMALL_ARRAY_RECYCLER uses 2^5 == 32 byte segments, so copies longer than 32 bytes and
    // non-segment-aligned offsets exercise the segment-boundary handling in orderedCopy.
    private static final WastefulRecycler RECYCLER = WastefulRecycler.SMALL_ARRAY_RECYCLER;

    @Test
    public void orderedCopyReproducesSourceAcrossSegmentBoundaries() {
        int length = 100; // spans multiple 32-byte segments in both source and destination
        SegmentedByteArray src = new SegmentedByteArray(RECYCLER);
        for (int i = 0; i < length; i++) {
            src.set(i, (byte) (i & 0xFF));
        }

        // Start writing at a non-zero, non-aligned destination position so the destination write
        // offsets straddle segment boundaries differently than the source read offsets.
        long destPos = 20;
        SegmentedByteArray dest = new SegmentedByteArray(RECYCLER);
        dest.orderedCopy(src, 0, destPos, length);

        for (int i = 0; i < length; i++) {
            assertEquals("mismatch at offset " + i, (byte) (i & 0xFF), dest.get(destPos + i));
        }
    }

    @Test
    public void orderedCopyHandlesNonAlignedSourceAndDestination() {
        int total = 80;
        SegmentedByteArray src = new SegmentedByteArray(RECYCLER);
        for (int i = 0; i < total; i++) {
            src.set(i, (byte) (i + 1));
        }

        long srcPos = 13;  // non-aligned source start (mid first segment)
        long destPos = 7;  // non-aligned destination start
        int length = 50;   // crosses multiple segment boundaries on both sides
        SegmentedByteArray dest = new SegmentedByteArray(RECYCLER);
        dest.orderedCopy(src, srcPos, destPos, length);

        for (int i = 0; i < length; i++) {
            assertEquals("mismatch at offset " + i, src.get(srcPos + i), dest.get(destPos + i));
        }
    }

    @Test
    public void orderedCopyExactSegmentMultipleFromSegmentStart() {
        int length = 64; // exactly two full 32-byte segments, aligned to segment start
        SegmentedByteArray src = new SegmentedByteArray(RECYCLER);
        for (int i = 0; i < length; i++) {
            src.set(i, (byte) (255 - i));
        }

        SegmentedByteArray dest = new SegmentedByteArray(RECYCLER);
        dest.orderedCopy(src, 0, 0, length);

        for (int i = 0; i < length; i++) {
            assertEquals("mismatch at offset " + i, (byte) (255 - i), dest.get(i));
        }
    }
}
