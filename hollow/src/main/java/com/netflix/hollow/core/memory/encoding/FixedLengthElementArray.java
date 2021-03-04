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
package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.SegmentedLongArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;
import sun.misc.Unsafe;

/**
 * Note that for performance reasons, this class makes use of {@code sun.misc.Unsafe} to perform
 * unaligned memory reads.  This is designed exclusively for little-endian architectures, and has only been
 * fully battle-tested on x86-64. As a result there two ways to obtain an element value from the bit string at a given
 * bit index.
 * <br><br>
 * {@link #getElementValue(long, int)} or {@link #getElementValue(long, int, long)}: These methods leverage unsafe
 * unaligned (or misaligned) memory reads of {@code long} values from {@code long[]} array segments at byte index
 * offsets within the arrays.
 * <br><br>
 * {@link #getLargeElementValue(long, int)} or {@link #getLargeElementValue(long, int, long)}: These methods leverage
 * safe access to {@code long[]} array segments but require more work to compose an element value from bits that cover
 * two underlying elements in {@code long[]} array segments.
 * <br><br>
 * The methods reading unaligned values need to ensure a segmentation fault (SEGV) does not occur when when reading a
 * {@code long} value at the last byte of the last index in a {@code long[]} array segment.  A {@code long[]} array
 * segment is allocated with a length that is one plus the desired length to ensure such access is safe (see the
 * implementations of {@link ArraySegmentRecycler#getLongArray()}.
 * <br><br>
 * In addition, the value of the last underlying element is the same as the value of the first underlying element in the
 * subsequent array segment (see {@link SegmentedLongArray#set}).  This ensures that an element (n-bit) value can be
 * correctly returned when performing an unaligned read that would otherwise cross an array segment boundary.
 * <br><br>
 * Furthermore, there is an additional constraint that the element values cannot exceed 58 bits. This is because reading
 * values that are unaligned with byte boundaries requires shifting by the number of bits the address is offset by
 * within a byte. For 58 bit values, the offset from a byte boundary can be as high as 6 bits. 58 bits can be shifted 6
 * bits and still fit within the 64 bit space. For 59 bit values the offset from a byte boundary can be as high as
 * 7 bits. Shifting a 59 bit value by 6 or 7 bits will both overflow the 64 bit space, resulting in an invalid value
 * when reading.
 *
 */
@SuppressWarnings("restriction")
public class FixedLengthElementArray extends SegmentedLongArray implements FixedLengthData {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    private final int log2OfSegmentSizeInBytes;
    private final int byteBitmask;

    public FixedLengthElementArray(ArraySegmentRecycler memoryRecycler, long numBits) {
        super(memoryRecycler, ((numBits - 1) >>> 6) + 1);
        this.log2OfSegmentSizeInBytes = log2OfSegmentSize + 3;
        this.byteBitmask = (1 << log2OfSegmentSizeInBytes) - 1;
    }

    @Override
    public void clearElementValue(long index, int bitsPerElement) {
        long whichLong = index >>> 6;
        int whichBit = (int) (index & 0x3F);

        long mask = ((1L << bitsPerElement) - 1);

        set(whichLong, get(whichLong) & ~(mask << whichBit));

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement)
            set(whichLong + 1, get(whichLong + 1) & ~(mask >>> bitsRemaining));
    }

    @Override
    public void setElementValue(long index, int bitsPerElement, long value) {
        long whichLong = index >>> 6;
        int whichBit = (int) (index & 0x3F);

        set(whichLong, get(whichLong) | (value << whichBit));

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement)
            set(whichLong + 1, get(whichLong + 1) | (value >>> bitsRemaining));
    }

    @Override
    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, ((1L << bitsPerElement) - 1));
    }

    @Override
    public long getElementValue(long index, int bitsPerElement, long mask) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        int whichSegment = (int) (whichByte >>> log2OfSegmentSizeInBytes);

        long[] segment = segments[whichSegment];
        long elementByteOffset = (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (whichByte & byteBitmask);
        long longVal = unsafe.getLong(segment, elementByteOffset);
        long l = longVal >>> whichBit;

        return l & mask;
    }

    @Override
    public long getLargeElementValue(long index, int bitsPerElement) {
        long mask = bitsPerElement == 64 ? -1 : ((1L << bitsPerElement) - 1);
        return getLargeElementValue(index, bitsPerElement, mask);
    }

    @Override
    public long getLargeElementValue(long index, int bitsPerElement, long mask) {
        long whichLong = index >>> 6;
        int whichBit = (int) (index & 0x3F);

        long l = get(whichLong) >>> whichBit;

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement) {
            whichLong++;
            l |= get(whichLong) << bitsRemaining;
        }

        return l & mask;
    }

    @Override
    public void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits) {
        if(numBits == 0)
            return;
        
        if ((destStartBit & 63) != 0) {
            int fillBits = (int) Math.min(64 - (destStartBit & 63), numBits);
            long fillValue = copyFrom.getLargeElementValue(sourceStartBit, fillBits);
            setElementValue(destStartBit, fillBits, fillValue);

            destStartBit += fillBits;
            sourceStartBit += fillBits;
            numBits -= fillBits;
        }

        long currentWriteLong = destStartBit >>> 6;

        while (numBits >= 64) {
            long l = copyFrom.getLargeElementValue(sourceStartBit, 64, -1);
            set(currentWriteLong, l);
            numBits -= 64;
            sourceStartBit += 64;
            currentWriteLong++;
        }

        if (numBits != 0) {
            destStartBit = currentWriteLong << 6;

            long fillValue = copyFrom.getLargeElementValue(sourceStartBit, (int) numBits);
            setElementValue(destStartBit, (int) numBits, fillValue);
        }
    }

    @Override
    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements) {
        long endBit = startBit + (bitsBetweenIncrements * numIncrements);
        for(; startBit<endBit; startBit += bitsBetweenIncrements) {
            increment(startBit, increment);
        }
    }

    public void increment(long index, long increment) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        int whichSegment = (int) (whichByte >>> log2OfSegmentSizeInBytes);

        long[] segment = segments[whichSegment];
        long elementByteOffset = (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (whichByte & byteBitmask);
        long l = unsafe.getLong(segment, elementByteOffset);

        unsafe.putLong(segment, elementByteOffset, l + (increment << whichBit));
        unsafe.storeFence();

        /// update the fencepost longs
        if((whichByte & byteBitmask) > bitmask * 8 && (whichSegment + 1) < segments.length) {
            unsafe.putLong(segments[whichSegment + 1], (long) Unsafe.ARRAY_LONG_BASE_OFFSET, segments[whichSegment][bitmask + 1]);
        }
        if((whichByte & byteBitmask) < 8 && whichSegment > 0) {
            unsafe.putLong(segments[whichSegment - 1], (long) Unsafe.ARRAY_LONG_BASE_OFFSET + (8 * (bitmask + 1)), segments[whichSegment][0]);
        }
    }

    public static FixedLengthElementArray newFrom(HollowBlobInput in, ArraySegmentRecycler memoryRecycler)
            throws IOException {

        long numLongs = VarInt.readVLong(in);
        return newFrom(in, memoryRecycler, numLongs);
    }

    public static FixedLengthElementArray newFrom(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, long numLongs)
            throws IOException {

        FixedLengthElementArray arr = new FixedLengthElementArray(memoryRecycler, numLongs * 64);
        arr.readFrom(in, memoryRecycler, numLongs);
        return arr;
    }
}
