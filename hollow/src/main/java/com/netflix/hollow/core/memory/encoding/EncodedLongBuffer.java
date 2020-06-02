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

import static java.lang.Math.ceil;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

/**
 * This class allows for storage and retrieval of fixed-length data in ByteBuffers.
 *
 * As a result there two ways to obtain an element value from the bit string at a given bit index.  The first,
 * using {@link #getElementValue(long, int)} or {@link #getElementValue(long, int, long)}, at byte index offsets within
 * the buffers. The second, using {@link #getLargeElementValue(long, int)} or
 * {@link #getLargeElementValue(long, int, long)}, by reading two long values and then composing an element value
 * from bits that cover the two.
 *
 * In the counterpart {@link FixedLengthElementArray} implementation a long read into the last 8 bytes of data was safe
 * because of a padding of 1 long at the end. Instead, this implementation returns a zero byte if the 8 byte range past
 * the buffer capacity is queried.
 *
 * {@link #getElementValue} can only support element values of 60-bits or less since two 60-bit values in sequence can
 * be represented exactly in 15 bytes.  Two 61-bit values in sequence require 16 bytes.  For such a bit string
 * performing an unaligned read at byte index 7 to obtain the second 61-bit value will result in missing the 2 most
 * significant bits located at byte index 15.
 */
@SuppressWarnings("restriction")
public class EncodedLongBuffer implements FixedLengthData {

    private BlobByteBuffer bufferView;
    private long maxLongs = -1;
    private long maxByteIndex = -1;

    public EncodedLongBuffer() {}

    /**
     * Gets an element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}. {@code bitsPerElement} should be less than 61 bits.
     *
     * @param index the bit index
     * @param bitsPerElement bits per element, must be less than 61 otherwise
     * the result is undefined
     * @return the element value
     */
    @Override
    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, ((1L << bitsPerElement) - 1));
    }

    /**
     * Gets a masked element value, comprising of {@code bitsPerElement} bits, at the given
     * bit {@code index}. {@code bitsPerElement} should be less than 61 bits.
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
    @Override
    public long getElementValue(long index, int bitsPerElement, long mask) {

        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        if (whichByte + ceil(bitsPerElement/8) > this.maxByteIndex) {
            throw new IllegalStateException();
        }

        long longVal = this.bufferView.getLong(this.bufferView.position() + whichByte);
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
    @Override
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
    @Override
    public long getLargeElementValue(long index, int bitsPerElement, long mask) {

        long whichLong = index >>> 6;
        int whichBit = (int) (index & 0x3F);

        long l = this.bufferView.getLong(bufferView.position() + whichLong * Long.BYTES) >>> whichBit;

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement) {
            whichLong++;
            l |= this.bufferView.getLong(bufferView.position() + whichLong * Long.BYTES) << bitsRemaining;
        }

        return l & mask;
    }

    private void loadFrom(HollowBlobInput in, long numLongs) throws IOException {
        BlobByteBuffer buffer = in.getBuffer();
        this.maxLongs = numLongs;
        this.maxByteIndex = (this.maxLongs * Long.BYTES) - 1;

        if(numLongs == 0)
            return;

        buffer.position(in.getFilePointer());
        this.bufferView = buffer.duplicate();
        buffer.position(buffer.position() + (numLongs * Long.BYTES));
        in.seek(in.getFilePointer() + (numLongs  * Long.BYTES));
    }

    @Override
    public void setElementValue(long index, int bitsPerElement, long value) {
        throw new UnsupportedOperationException("Not supported in shared-memory mode");
    }

    @Override
    public void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits){
        throw new UnsupportedOperationException("Not supported in shared-memory mode");
    }

    @Override
    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements){
        throw new UnsupportedOperationException("Not supported in shared-memory mode");
    }

    @Override
    public void clearElementValue(long index, int bitsPerElement) {
        throw new UnsupportedOperationException("Not supported in shared-memory mode");
    }

    /**
     * Returns a new EncodedLongBuffer from deserializing the given input. The value of the first variable length integer
     * in the input indicates how many long values are to then be read from the input.
     *
     * @input in Hollow Blob Input to read data (a var int and then that many longs) from
     * @return new EncodedLongBuffer containing data read from input
     */
    public static EncodedLongBuffer newFrom(HollowBlobInput in) throws IOException {
        long numLongs = VarInt.readVLong(in);
        return newFrom(in, numLongs);
    }

    /**
     * Returns a new EncodedLongBuffer from deserializing numLongs longs from given input.
     *
     * @input in Hollow Blob Input to read numLongs longs from
     * @return new EncodedLongBuffer containing data read from input
     */
    public static EncodedLongBuffer newFrom(HollowBlobInput in, long numLongs) throws IOException {
        EncodedLongBuffer buf = new EncodedLongBuffer();
        buf.loadFrom(in, numLongs);
        return buf;
    }
}
