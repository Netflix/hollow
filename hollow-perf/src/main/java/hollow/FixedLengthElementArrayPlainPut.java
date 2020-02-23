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
package hollow;

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import sun.misc.Unsafe;

// This is a copy of the class in hollow that replaces release stores with plain stores.

/**
 * Each record in Hollow begins with a fixed-length number of bits.  At the lowest level, these bits
 * are held in long arrays using the class FixedLengthElementArray.  This class allows for storage
 * and retrieval of fixed-length data in a range of bits.  For example, if a FixedLengthElementArray
 * was queried for the 6-bit value starting at bit 7 in the following example range of bits:
 * <p>
 * <pre>
 *     0001000100100001101000010100101001111010101010010010101
 * </pre>
 * <p>
 * The value 100100 in binary, or 36 in base 10, would be returned.
 * <p>
 * Note that for performance reasons, this class makes use of sun.misc.Unsafe to perform unaligned
 * memory reads.  This is designed exclusively for little-endian architectures, and has only been
 * fully battle-tested on x86-64.
 */
@SuppressWarnings("restriction")
public class FixedLengthElementArrayPlainPut extends SegmentedLongArrayPlainPut {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    private final int log2OfSegmentSizeInBytes;
    private final int byteBitmask;

    public FixedLengthElementArrayPlainPut(ArraySegmentRecycler memoryRecycler, long numBits) {
        super(memoryRecycler, ((numBits - 1) >>> 6) + 1);
        this.log2OfSegmentSizeInBytes = log2OfSegmentSize + 3;
        this.byteBitmask = (1 << log2OfSegmentSizeInBytes) - 1;
    }

    public void clearElementValue(long index, int bitsPerElement) {
        throw new UnsupportedOperationException();
    }

    public void setElementValue(long index, int bitsPerElement, long value) {
        throw new UnsupportedOperationException();
    }

    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, ((1L << bitsPerElement) - 1));
    }

    public long getElementValue(long index, int bitsPerElement, long mask) {
        throw new UnsupportedOperationException();
    }

    public long getLargeElementValue(long index, int bitsPerElement) {
        long mask = bitsPerElement == 64 ? -1 : ((1L << bitsPerElement) - 1);
        return getLargeElementValue(index, bitsPerElement, mask);
    }

    public long getLargeElementValue(long index, int bitsPerElement, long mask) {
        throw new UnsupportedOperationException();
    }

    public void copyBits(
            com.netflix.hollow.core.memory.encoding.FixedLengthElementArray copyFrom, long sourceStartBit,
            long destStartBit, long numBits) {
        throw new UnsupportedOperationException();
    }

    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements) {
        throw new UnsupportedOperationException();
    }

    public void increment(long index, long increment) {
        throw new UnsupportedOperationException();
    }

    public static int bitsRequiredToRepresentValue(long value) {
        throw new UnsupportedOperationException();
    }

}
