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
 * using {@link #getElementValue} for values less than 61 bits in length and the second, using
 * {@link #getLargeElementValue} that is recommended for values of upto 64 bits in length.
 * </p>
 */
public interface FixedLengthData {

    /**
     * Gets an element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}. {@code bitsPerElement} should be less than 61 bits.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, must be less than 61 otherwise
     * the result is undefined
     * @return the element value
     */
    long getElementValue(long index, int bitsPerElement);

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
    long getElementValue(long index, int bitsPerElement, long mask);

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
    long getLargeElementValue(long index, int bitsPerElement);

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
    long getLargeElementValue(long index, int bitsPerElement, long mask);

    void setElementValue(long index, int bitsPerElement, long value);

    void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits);

    void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements);

    void clearElementValue(long index, int bitsPerElement);

    /**
     * Discard fixed length data from input. The input contains the number of longs to discard.
     *
     * @param in Hollow Blob Input to discard data from
     */
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
