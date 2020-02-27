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

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.SegmentedLongArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import sun.misc.Unsafe;

/**
 * Each record in Hollow begins with a fixed-length number of bits.  At the lowest level, these bits
 * are held in long arrays using the class FixedLengthElementArray.  This class allows for storage
 * and retrieval of fixed-length data in a range of bits.  For example, if a FixedLengthElementArray
 * was queried for the 6-bit value starting at bit 7 in the following example range of bits:
 * <pre>
 *     0001000100100001101000010100101001111010101010010010101
 * </pre>
 * <p>
 * The value 100100 in binary, or 36 in base 10, would be returned.
 * <p>
 * // SNAP: TODO: Update doc
 * As a result there two ways to obtain an element value from the bit string at a given bit index.  The first,
 * using {@link #getElementValue(long, int)} or {@link #getElementValue(long, int, long)}, leverages unsafe unaligned
 * (or misaligned) memory reads of {@code long} values from {@code long[]} array segments at byte index offsets within
 * the arrays.
 * The second, using {@link #getLargeElementValue(long, int)} or
 * {@link #getLargeElementValue(long, int, long)}, leverages safe access to {@code long[]} array segments but
 * requires more work to compose an element value from bits that cover two underlying elements in {@code long[]} array
 * segments.
 * The first approach needs to ensure a segmentation fault (SEGV) does not occur when when reading a {@code long} value
 * at the last byte of the last index in a {@code long[]} array segment.  A {@code long[]} array segment is allocated
 * with a length that is one plus the desired length to ensure such access is safe (see the implementations of
 * {@link ArraySegmentRecycler#getLongArray()}.
 * In addition, the value of the last underlying element is the same as the value of the first underlying element in the
 * subsequent array segment (see {@link SegmentedLongArray#set}).  This ensures that an element (n-bit) value can be
 * correctly returned when performing an unaligned read that would otherwise cross an array segment boundary.
 * Furthermore, there is an additional constraint that first method can only support element values of 60-bits or less.
 * Two 60-bit values in sequence can be represented exactly in 15 bytes.  Two 61-bit values in sequence require 16
 * bytes.  For such a bit string performing an unaligned read at byte index 7 to obtain the second 61-bit value will
 * result in missing the 2 most significant bits located at byte index 15.
 */
@SuppressWarnings("restriction")
public class FixedLengthElementArray extends SegmentedLongArray {

    private static int debug_count = 0;

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    private final int log2OfSegmentSizeInBytes;
    private final int byteBitmask;

    public FixedLengthElementArray(ArraySegmentRecycler memoryRecycler, long numBits) {
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

    /**
     * Gets an element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, must be less than 61 otherwise
     * the result is undefined
     * @return the element value
     */
    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, ((1L << bitsPerElement) - 1));
    }

    /**
     * Gets a masked element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, must be less than 61 otherwise
     * the result is undefined
     * @param mask the mask to apply to an element value before it is returned.
     * The mask should be less than or equal to {@code (1L << bitsPerElement) - 1} to
     * guarantee that one or more (possibly) partial element values occurring
     * before and after the desired element value are not included in the returned value.
     * @return the masked element value
     */
    public long getElementValue(long index, int bitsPerElement, long mask) {

        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        int whichSegment = (int) (whichByte >>> log2OfSegmentSizeInBytes);
        if (whichSegment >= segments.length) {
            throw new IllegalStateException();
        }
        debug_count ++;

        BlobByteBuffer segment = segments[whichSegment];
        long elementOffset = whichByte & byteBitmask;   // which byte in the current segment

        long longVal = segment.getLong(segment.position() + elementOffset);
        long l =  longVal >>> whichBit;
        return l & mask;
    }

    /**
     * Gets a large element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}.
     * <p>
     * This method should be utilized if the {@code bitsPerElement} may exceed {@code 60} bits,
     * otherwise the method {@link #getLargeElementValue(long, int)} can be utilized instead.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, may be greater than 60
     * @return the large element value
     */
    public long getLargeElementValue(long index, int bitsPerElement) {
        long mask = bitsPerElement == 64 ? -1 : ((1L << bitsPerElement) - 1);
        return getLargeElementValue(index, bitsPerElement, mask);
    }

    /**
     * Gets a masked large element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}.
     * <p>
     * This method should be utilized if the {@code bitsPerElement} may exceed {@code 60} bits,
     * otherwise the method {@link #getLargeElementValue(long, int, long)} can be utilized instead.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, may be greater than 60
     * @param mask the mask to apply to an element value before it is returned.
     * The mask should be less than or equal to {@code (1L << bitsPerElement) - 1} to
     * guarantee that one or more (possibly) partial element values occurring
     * before and after the desired element value are not included in the returned value.
     * @return the masked large element value
     */
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

    public void copyBits(FixedLengthElementArray copyFrom, long sourceStartBit, long destStartBit, long numBits) {
        if(numBits == 0)
            return;

        if ((destStartBit & 63) != 0) {
            int fillBits = (int)Math.min(64 - (destStartBit & 63), numBits);
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

    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements) {
        long endBit = startBit + (bitsBetweenIncrements * numIncrements);
        for(; startBit<endBit; startBit += bitsBetweenIncrements) {
            increment(startBit, increment);
        }
    }

    public void increment(long index, long increment) {
        throw new UnsupportedOperationException();
    }


    public static FixedLengthElementArray deserializeFrom(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        long numLongs = VarInt.readVLong(dis);

        FixedLengthElementArray arr = new FixedLengthElementArray(memoryRecycler, numLongs * 64);

        arr.readFrom(dis, memoryRecycler, numLongs);

        return arr;
    }

    // returns a FixedLengthElementArray that contains deserialized data from given file
    public static FixedLengthElementArray deserializeFrom(RandomAccessFile raf, BlobByteBuffer buffer, ArraySegmentRecycler memoryRecycler) throws IOException {
        long numLongs = VarInt.readVLong(raf);
        FixedLengthElementArray arr = new FixedLengthElementArray(memoryRecycler, numLongs * 64);
        arr.readFrom(raf, buffer, memoryRecycler, numLongs);
        return arr;
    }

    public static void discardFrom(DataInputStream dis) throws IOException {
        long numLongs = VarInt.readVLong(dis);
        long bytesToSkip = numLongs * 8;

        while(bytesToSkip > 0) {
            bytesToSkip -= dis.skip(bytesToSkip);
        }
    }

    public static int bitsRequiredToRepresentValue(long value) {
        if(value == 0)
            return 1;
        return 64 - Long.numberOfLeadingZeros(value);
    }


    // debug utility: pretty print
    public void pp(BufferedWriter debug) throws IOException {
        StringBuffer pp = new StringBuffer();

        int segmentSize = 1 << log2OfSegmentSize;
        long maxIndex = segments.length * segmentSize;


        pp.append("\n\n FixedLengthElementArray get()s =>");
        for (int g = 0; g < maxIndex; g ++) {
            long v = get(g);
            pp.append(v + " ");
        }

        pp.append("\n");
//        pp.append("\n FixedLengthElementArray raw bytes underneath:\n");
//        for (int i = 0; i < segments.length; i ++) {
//            if (segments[i] == null) {
//                pp.append("- - - - - NULL - - - - ");
//                pp.append("\n");
//                continue;
//            }
//
//            pp.append(String.format("FixedLengthElementArray i= %d/%d => ", i, segments.length-1));
//
//            for (int j = 0; j < segmentSize; j ++ ) {
//                long v = segments[i].get(segments[i].position() + j);
//                pp.append(v + " ");
//            }
//            pp.append("\n");
//        }
        debug.append(pp.toString());
    }
}
