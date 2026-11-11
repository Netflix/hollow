/*
 *  Copyright 2026 Netflix, Inc.
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
 */
package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;
import sun.misc.Unsafe;

/**
 * On-heap fixed-length data held in one contiguous {@code long[]}.
 *
 * <p>Like {@link FixedLengthElementArray}, this uses an unaligned long load for values up to 58
 * bits. The array has one fencepost long so that a load beginning in its last data long remains
 * safe. Unlike the segmented representation, reads do not need to resolve a nested array.
 */
@SuppressWarnings("restriction")
public final class ContiguousFixedLengthData implements FixedLengthData {

    // Mirrors jdk.internal.util.ArraysSupport.SOFT_MAX_ARRAY_LENGTH, a conservative bound
    // below VM-specific array length limits. It is internal and unavailable on Java 8.
    private static final int JDK_SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;
    private static final int FENCEPOST_LONGS = 1;
    private static final int MAX_DATA_LONGS = JDK_SOFT_MAX_ARRAY_LENGTH - FENCEPOST_LONGS;
    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    private final long[] data;
    private final long sizeBits;

    public ContiguousFixedLengthData(long numBits) {
        this(numBits, numberOfLongs(numBits));
    }

    private ContiguousFixedLengthData(long numBits, long numLongs) {
        if (!canStore(numLongs)) {
            throw new IllegalArgumentException("Fixed-length data is too large for one array: " + numLongs);
        }
        this.data = new long[(int) numLongs + FENCEPOST_LONGS];
        this.sizeBits = numBits;
    }

    public static boolean canStore(long numLongs) {
        return numLongs >= 0 && numLongs <= MAX_DATA_LONGS;
    }

    public static ContiguousFixedLengthData newFrom(HollowBlobInput in, long numLongs)
            throws IOException {
        ContiguousFixedLengthData data = new ContiguousFixedLengthData(numLongs * 64, numLongs);
        int dataLongs = (int) numLongs;
        for (int i = 0; i < dataLongs; i++) {
            data.data[i] = in.readLong();
        }
        return data;
    }

    public long approxHeapFootprintInBytes() {
        return sizeBits / 8;
    }

    @Override
    public void clearElementValue(long index, int bitsPerElement) {
        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3F);
        long mask = (1L << bitsPerElement) - 1;

        data[whichLong] &= ~(mask << whichBit);

        int bitsRemaining = 64 - whichBit;
        if (bitsRemaining < bitsPerElement) {
            data[whichLong + 1] &= ~(mask >>> bitsRemaining);
        }
    }

    @Override
    public void setElementValue(long index, int bitsPerElement, long value) {
        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3F);

        data[whichLong] |= value << whichBit;

        int bitsRemaining = 64 - whichBit;
        if (bitsRemaining < bitsPerElement) {
            data[whichLong + 1] |= value >>> bitsRemaining;
        }
    }

    @Override
    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, (1L << bitsPerElement) - 1);
    }

    @Override
    public long getElementValue(long index, int bitsPerElement, long mask) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);
        long byteOffset = (long) Unsafe.ARRAY_LONG_BASE_OFFSET + whichByte;
        return (unsafe.getLong(data, byteOffset) >>> whichBit) & mask;
    }

    @Override
    public long getLargeElementValue(long index, int bitsPerElement) {
        long mask = bitsPerElement == 64 ? -1 : (1L << bitsPerElement) - 1;
        return getLargeElementValue(index, bitsPerElement, mask);
    }

    @Override
    public long getLargeElementValue(long index, int bitsPerElement, long mask) {
        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3F);
        long value = data[whichLong] >>> whichBit;

        int bitsRemaining = 64 - whichBit;
        if (bitsRemaining < bitsPerElement) {
            value |= data[whichLong + 1] << bitsRemaining;
        }
        return value & mask;
    }

    @Override
    public void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits) {
        if (numBits == 0) {
            return;
        }

        if ((destStartBit & 63) != 0) {
            int fillBits = (int) Math.min(64 - (destStartBit & 63), numBits);
            long fillValue = copyFrom.getLargeElementValue(sourceStartBit, fillBits);
            setElementValue(destStartBit, fillBits, fillValue);

            destStartBit += fillBits;
            sourceStartBit += fillBits;
            numBits -= fillBits;
        }

        int currentWriteLong = (int) (destStartBit >>> 6);
        while (numBits >= 64) {
            data[currentWriteLong++] = copyFrom.getLargeElementValue(sourceStartBit, 64, -1);
            numBits -= 64;
            sourceStartBit += 64;
        }

        if (numBits != 0) {
            destStartBit = (long) currentWriteLong << 6;
            long fillValue = copyFrom.getLargeElementValue(sourceStartBit, (int) numBits);
            setElementValue(destStartBit, (int) numBits, fillValue);
        }
    }

    @Override
    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements) {
        long endBit = startBit + bitsBetweenIncrements * numIncrements;
        for (; startBit < endBit; startBit += bitsBetweenIncrements) {
            increment(startBit, increment);
        }
    }

    private void increment(long index, long increment) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);
        long byteOffset = (long) Unsafe.ARRAY_LONG_BASE_OFFSET + whichByte;
        long value = unsafe.getLong(data, byteOffset);
        unsafe.putLong(data, byteOffset, value + (increment << whichBit));
    }

    private static long numberOfLongs(long numBits) {
        return numBits == 0 ? 0 : ((numBits - 1) >>> 6) + 1;
    }
}
