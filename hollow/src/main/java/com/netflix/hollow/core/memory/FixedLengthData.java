package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

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
