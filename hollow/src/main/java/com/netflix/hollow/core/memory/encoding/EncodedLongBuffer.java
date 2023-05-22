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
 * This class allows for storage and retrieval of fixed-length data in ByteBuffers. As a result there two ways to obtain
 * an element value from the bit string at a given bit index.
 * <br><br>
 * {@link #getElementValue(long, int)} or {@link #getElementValue(long, int, long)}: at byte index offsets within
 * the buffers.
 * <br><br>
 * {@link #getLargeElementValue(long, int)} or {@link #getLargeElementValue(long, int, long)}: by reading two long
 * values and then composing an element value from bits that cover the two.
 * <br><br>
 * In the counterpart {@link FixedLengthElementArray} implementation a long read into the last 8 bytes of data was safe
 * because of a padding of 1 long at the end. Instead, this implementation returns a zero byte if the 8 byte range past
 * the buffer capacity is queried.
 * <br><br>
 * {@link #getElementValue} can only support element values of 58 bits or less. This is because reading values that are
 * unaligned with byte boundaries requires shifting by the number of bits the address is offset by within a byte. For
 * 58 bit values, the offset from a byte boundary can be as high as 6 bits. 58 bits can be shifted 6 bits and still fit
 * within the 64 bit space. For 59 bit values the offset from a byte boundary can be as high as 7 bits. Shifting a
 * 59 bit value by 6 or 7 bits will both overflow the 64 bit space, resulting in an invalid value when reading.
 */
@SuppressWarnings("restriction")
public class EncodedLongBuffer implements FixedLengthData {

    private BlobByteBuffer bufferView;
    private long maxByteIndex = -1;

    public EncodedLongBuffer() {}

    /**
     * Returns a new EncodedLongBuffer from deserializing the given input. The value of the first variable length integer
     * in the input indicates how many long values are to then be read from the input.
     *
     * @param in Hollow Blob Input to read data (a var int and then that many longs) from
     * @return new EncodedLongBuffer containing data read from input
     */
    public static EncodedLongBuffer newFrom(HollowBlobInput in) throws IOException {
        long numLongs = VarInt.readVLong(in);
        return newFrom(in, numLongs);
    }

    /**
     * Returns a new EncodedLongBuffer from deserializing numLongs longs from given input.
     *
     * @param in Hollow Blob Input to read numLongs longs from
     * @return new EncodedLongBuffer containing data read from input
     */
    public static EncodedLongBuffer newFrom(HollowBlobInput in, long numLongs) throws IOException {
        EncodedLongBuffer buf = new EncodedLongBuffer();
        buf.loadFrom(in, numLongs);
        return buf;
    }

    private void loadFrom(HollowBlobInput in, long numLongs) throws IOException {
        BlobByteBuffer buffer = in.getBuffer();
        if(numLongs == 0)
            return;

        this.maxByteIndex = (numLongs * Long.BYTES) - 1;
        buffer.position(in.getFilePointer());
        this.bufferView = buffer.duplicate();
        buffer.position(buffer.position() + (numLongs * Long.BYTES));
        in.seek(in.getFilePointer() + (numLongs  * Long.BYTES));    // SNAP: TODO: is this stuff unnecessary when being done in delta application? Called from FixedLengthDataFactory::allocate
    }

    @Override
    public long getElementValue(long index, int bitsPerElement) {
        return getElementValue(index, bitsPerElement, ((1L << bitsPerElement) - 1));
    }

    @Override
    public long getElementValue(long index, int bitsPerElement, long mask) {

        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        if (whichByte + ceil((float) bitsPerElement/8) > this.maxByteIndex + 1) {
            throw new IllegalStateException(String.format("Attempted read past the end of buffer. index=%s, " +
                    "whichByte=%s, this.maxByteIndex=%s, whichBit=%s, bitsPerElement=%s", index, whichByte,
                    this.maxByteIndex, whichBit, bitsPerElement));
        }

        long longVal = this.bufferView.getLong(this.bufferView.position() + whichByte);
        long l =  longVal >>> whichBit;
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

        long l = this.bufferView.getLong(bufferView.position() + whichLong * Long.BYTES) >>> whichBit;

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement) {
            whichLong++;
            l |= this.bufferView.getLong(bufferView.position() + whichLong * Long.BYTES) << bitsRemaining;
        }

        return l & mask;
    }

    @Override
    public void setElementValue(long index, int bitsPerElement, long value) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);
        this.bufferView.putLong(this.bufferView.position() + whichByte,
                this.bufferView.getLong(this.bufferView.position() + whichByte) | (value << whichBit));

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement)
            this.bufferView.putLong(this.bufferView.position() + whichByte + 1,
                    this.bufferView.getLong(this.bufferView.position() + whichByte + 1) | (value >>> bitsRemaining));
    }

    @Override
    public void copyBits(FixedLengthData copyFrom, long sourceStartBit, long destStartBit, long numBits){
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
            this.bufferView.putLong(this.bufferView.position() + (currentWriteLong * 8), l);
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
    public void incrementMany(long startBit, long increment, long bitsBetweenIncrements, int numIncrements){
        long endBit = startBit + (bitsBetweenIncrements * numIncrements);
        for(; startBit<endBit; startBit += bitsBetweenIncrements) {
            increment(startBit, increment);
        }
    }

    public void increment(long index, long increment) {
        long whichByte = index >>> 3;
        int whichBit = (int) (index & 0x07);

        long l = this.bufferView.getLong(this.bufferView.position() + whichByte);

        this.bufferView.putLong(whichByte, l + (increment << whichBit));

        /// SNAP: Didn't update the fencepost longs like we did in FixedLengthElementArray::increment
    }

    @Override
    public void clearElementValue(long index, int bitsPerElement) { // SNAP: can be absorbed into interface, with set and get being the specific implementations
        long whichLong = index >>> 6;
        int whichBit = (int) (index & 0x3F);

        long mask = ((1L << bitsPerElement) - 1);

        set(whichLong, get(whichLong) & ~(mask << whichBit));

        int bitsRemaining = 64 - whichBit;

        if (bitsRemaining < bitsPerElement)
            set(whichLong + 1, get(whichLong + 1) & ~(mask >>> bitsRemaining));
    }

    /**
     * Set and get the long at the given index to the specified value. Index is at Long.BYTES granularity and relative to
     * the start of this buffer. So for e.g. index 0 will represent the long value occupying bytes 0-7 of this buffer, etc.
     */
    public void set(long index, long value) {
        this.bufferView.putLong(this.bufferView.position() + (index * 8), value);
    }
    public long get(long index) {
        return this.bufferView.getLong(this.bufferView.position() + (index * 8));
    }

    public void destroy() throws IOException {
        System.out.println("SNAP: WARNING - shouldn't be getting invoked");
        // since we operate on a bufferView here, we should't mutate the underlying buffer
    }
}
