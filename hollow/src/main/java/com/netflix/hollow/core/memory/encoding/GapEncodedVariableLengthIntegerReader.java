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

import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.util.IOUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public boolean isEmpty() {
        return numBytes == 0;
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

    public static void copyEncodedDeltaOrdinals(HollowBlobInput in, DataOutputStream... os) throws IOException {
        long numBytesEncodedOrdinals = IOUtils.copyVLong(in, os);
        IOUtils.copyBytes(in, os, numBytesEncodedOrdinals);
    }

    public static void discardEncodedDeltaOrdinals(HollowBlobInput in) throws IOException {
        long numBytesToSkip = VarInt.readVLong(in);
        while(numBytesToSkip > 0) {
            numBytesToSkip -= in.skipBytes(numBytesToSkip);
        }
    }

    public static GapEncodedVariableLengthIntegerReader combine(GapEncodedVariableLengthIntegerReader reader1, GapEncodedVariableLengthIntegerReader reader2, ArraySegmentRecycler memoryRecycler) {
        reader1.reset();
        reader2.reset();
        ByteDataArray arr = new ByteDataArray(memoryRecycler);
        int cur = 0;

        while(reader1.nextElement() != Integer.MAX_VALUE || reader2.nextElement() != Integer.MAX_VALUE) {
            if(reader1.nextElement() < reader2.nextElement()) {
                VarInt.writeVInt(arr, reader1.nextElement() - cur);
                cur = reader1.nextElement();
                reader1.advance();
            } else if(reader2.nextElement() < reader1.nextElement()) {
                VarInt.writeVInt(arr, reader2.nextElement() - cur);
                cur = reader2.nextElement();
                reader2.advance();
            } else {
                VarInt.writeVInt(arr, reader1.nextElement() - cur);
                cur = reader1.nextElement();
                reader1.advance();
                reader2.advance();
            }
        }

        return new GapEncodedVariableLengthIntegerReader(arr.getUnderlyingArray(), (int)arr.length());
    }

    /**
     * Splits the current {@code GapEncodedVariableLengthIntegerReader} instance into {@code numSplits} instances of
     * {@code GapEncodedVariableLengthIntegerReader}. Values in the original are distributed over the split result and
     * translated accordingly. The original data is not cleaned up.
     *
     * @param numSplits the number of instances to split into, should be a power of 2.
     * @return an array of {@code GapEncodedVariableLengthIntegerReader} instances populated with the results of the split.
     */
    public GapEncodedVariableLengthIntegerReader[] split(int numSplits) {
        if (numSplits<=0 || !((numSplits&(numSplits-1))==0)) {
            throw new IllegalStateException("Split should only be called with powers of 2, it was called with " + numSplits);
        }
        final int toMask = numSplits - 1;
        final int toOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);
        GapEncodedVariableLengthIntegerReader[] to = new GapEncodedVariableLengthIntegerReader[numSplits];

        List<Integer> ordinals = new ArrayList<>();
        reset();
        while(nextElement() != Integer.MAX_VALUE) {
            ordinals.add(nextElement());
            advance();
        }

        ByteDataArray[] splitOrdinals = new ByteDataArray[numSplits];
        int previousSplitOrdinal[] = new int[numSplits];
        for(int i=0;i<numSplits;i++) {
            splitOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        }
        for (int ordinal : ordinals) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            VarInt.writeVInt(splitOrdinals[toIndex], toOrdinal - previousSplitOrdinal[toIndex]);
            previousSplitOrdinal[toIndex] = toOrdinal;
        }
        for(int i=0;i<numSplits;i++) {
            if (splitOrdinals[i].length() > 0) {
                to[i] = new GapEncodedVariableLengthIntegerReader(splitOrdinals[i].getUnderlyingArray(), (int) splitOrdinals[i].length());
            } else {
                // SNAP: TODO:
                splitOrdinals[i].getUnderlyingArray().destroy();
                to[i] = EMPTY_READER;
            }
        }

        return to;
    }

    /**
     * Takes an array of {@code GapEncodedVariableLengthIntegerReader} instances to return one resulting
     * {@code GapEncodedVariableLengthIntegerReader} instance. Values in the original data are translated as they are
     * populated into the joined result. The original data is not cleaned up.
     *
     * @param from the array of {@code GapEncodedVariableLengthIntegerReader} to join, should have a power of 2 number of elements.
     * @return an instance of {@code GapEncodedVariableLengthIntegerReader} with the joined result.
     */
    public static GapEncodedVariableLengthIntegerReader join(GapEncodedVariableLengthIntegerReader[] from) {
        if (from==null) {
            throw new IllegalStateException("Join invoked on a null input array");
        }
        if (from.length<=0 || !((from.length&(from.length-1))==0)) {
            throw new IllegalStateException("Join should only be called with powers of 2, it was called with " + from.length);
        }

        int numSplits = from.length;
        final int fromMask = numSplits - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);
        int joinedMaxOrdinal = -1;

        HashSet<Integer>[] fromOrdinals = new HashSet[from.length];
        for (int i=0;i<from.length;i++) {
            fromOrdinals[i] = new HashSet<>();
            if (from[i] == null) {
                continue;
            }
            from[i].reset();

            while(from[i].nextElement() != Integer.MAX_VALUE) {
                int splitOrdinal = from[i].nextElement();
                fromOrdinals[i].add(splitOrdinal);
                joinedMaxOrdinal = Math.max(joinedMaxOrdinal, splitOrdinal*numSplits + i);
                from[i].advance();
            }
        }

        ByteDataArray toRemovals = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        int previousOrdinal = 0;
        for (int ordinal=0;ordinal<=joinedMaxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;
            if (fromOrdinals[fromIndex].contains(fromOrdinal)) {
                VarInt.writeVInt(toRemovals, ordinal - previousOrdinal);
                previousOrdinal = ordinal;
            }
        }

        if (toRemovals.length() == 0) {
            toRemovals.getUnderlyingArray().destroy();   // SNAP: here
            return EMPTY_READER;
        } else {
            return new GapEncodedVariableLengthIntegerReader(toRemovals.getUnderlyingArray(), (int) toRemovals.length());
        }
    }
}
