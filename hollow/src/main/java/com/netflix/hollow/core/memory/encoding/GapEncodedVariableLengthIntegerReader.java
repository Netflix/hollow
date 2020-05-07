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

import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.util.IOUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GapEncodedVariableLengthIntegerReader {

    public static GapEncodedVariableLengthIntegerReader EMPTY_READER = new GapEncodedVariableLengthIntegerReader(null, 0) {
        @Override
        public int nextElement() {
            return Integer.MAX_VALUE;
        }
    };

    private final SegmentedByteArray data;
    private final int numBytes;
    private int currentPosition;

    private int nextElement;
    private int elementIndex;

    public GapEncodedVariableLengthIntegerReader(SegmentedByteArray data, int numBytes) {
        this.data = data;
        this.numBytes = numBytes;
        reset();
    }

    public void advance() {
        if(currentPosition == numBytes) {
            nextElement = Integer.MAX_VALUE;
        } else {
            int nextElementDelta = VarInt.readVInt(data, currentPosition);
            currentPosition += VarInt.sizeOfVInt(nextElementDelta);
            nextElement += nextElementDelta;
            elementIndex++;
        }
    }

    public int nextElement() {
        return nextElement;
    }

    public int elementIndex() {
        return elementIndex;
    }

    public void reset() {
        currentPosition = 0;
        elementIndex = -1;
        nextElement = 0;
        advance();
    }
    
    public int remainingElements() {
        int remainingElementCount = 0;
        while(nextElement != Integer.MAX_VALUE) {
            remainingElementCount++;
            advance();
        }
        return remainingElementCount;
    }

    public void destroy() {
        if(data != null)
            data.destroy();
    }
    
    public void writeTo(OutputStream os) throws IOException {
        VarInt.writeVInt(os, numBytes);
        data.writeTo(os, 0, numBytes);
    }

    public static GapEncodedVariableLengthIntegerReader readEncodedDeltaOrdinals(HollowBlobInput in, ArraySegmentRecycler memoryRecycler) throws IOException {
        SegmentedByteArray arr = new SegmentedByteArray(memoryRecycler);
        long numBytesEncodedOrdinals = VarInt.readVLong(in);
        arr.loadFrom(in, numBytesEncodedOrdinals);
        return new GapEncodedVariableLengthIntegerReader(arr, (int)numBytesEncodedOrdinals);
    }

    public static void copyEncodedDeltaOrdinals(DataInputStream is, DataOutputStream... os) throws IOException {
        long numBytesEncodedOrdinals = IOUtils.copyVLong(is, os);
        IOUtils.copyBytes(is, os, numBytesEncodedOrdinals);
    }

    public static void discardEncodedDeltaOrdinals(HollowBlobInput in) throws IOException {
        long numBytesToSkip = VarInt.readVLong(in);
        while(numBytesToSkip > 0) {
            numBytesToSkip -= in.skipBytes(numBytesToSkip);
        }
    }

}
