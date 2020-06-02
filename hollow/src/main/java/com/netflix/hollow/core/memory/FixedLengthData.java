package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

/**
 * <p>
 *     Each record in Hollow begins with a fixed-length number of bits.  At the lowest level, these bits are held in
 * {@code FixedLengthData} data structures which can be either backed by long arrays or ByteBuffers.
 * For example, if an EncodedLongBuffer was queried for the 6-bit value starting at bit 7 in the following example
 * range of bits:
 * <pre>
 *     0001000100100001101000010100101001111010101010010010101
 * </pre>
 * the value 100100 in binary, or 36 in base 10, would be returned. </p>
 * <p>
 *     As a result there two ways to obtain an element value from the bit string at a given bit index.  The first,
 * using {@link #getElementValue(long, int)} or {@link #getElementValue(long, int, long)}, leverages unsafe unaligned
 * (or misaligned) memory reads of {@code long} values from {@code long[]} array segments at byte index offsets within
 * the backing arrays or ByteBuffers. The second, using {@link #getLargeElementValue(long, int)} or
 * {@link #getLargeElementValue(long, int, long)}, leverages safe access to array segments but requires more work to
 * compose an element value from bits that cover two underlying elements in the backing long[] array segments or ByteBuffer.
 * </p>
 */
public interface FixedLengthData {

    long getElementValue(long index, int bitsPerElement);

    long getElementValue(long index, int bitsPerElement, long mask);

    long getLargeElementValue(long index, int bitsPerElement);

    long getLargeElementValue(long index, int bitsPerElement, long mask);

    void setElementValue(long index, int bitsPerElement, long value);

    void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits);

    void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements);

    void clearElementValue(long index, int bitsPerElement);

    // discard fixed length data from input
    static void discardFrom(HollowBlobInput in) throws IOException {
        long numLongs = VarInt.readVLong(in);
        long bytesToSkip = numLongs * 8;

        while(bytesToSkip > 0) {
            bytesToSkip -= in.skipBytes(bytesToSkip);
        }
    }

    static int bitsRequiredToRepresentValue(long value) {
        if(value == 0)
            return 1;
        return 64 - Long.numberOfLeadingZeros(value);
    }

}
