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
package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataMode;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowSetTypeReadState}.
 * 
 * During a delta, the HollowSetTypeReadState will create a new HollowSetTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */

public class HollowSetTypeDataElements {

    int maxOrdinal;

    FixedLengthData setPointerAndSizeData;
    FixedLengthData elementData;

    GapEncodedVariableLengthIntegerReader encodedRemovals;
    GapEncodedVariableLengthIntegerReader encodedAdditions;

    int bitsPerSetPointer;
    int bitsPerSetSizeValue;
    int bitsPerFixedLengthSetPortion;
    int bitsPerElement;
    int emptyBucketValue;
    long totalNumberOfBuckets;

    final ArraySegmentRecycler memoryRecycler;
    final MemoryMode memoryMode;

    public HollowSetTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this(MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowSetTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        this.memoryMode = memoryMode;
        this.memoryRecycler = memoryRecycler;
    }

    void readSnapshot(HollowBlobInput in) throws IOException {
        readFromInput(in,false);
    }

    void readDelta(HollowBlobInput in) throws IOException {
        readFromInput(in,true);
    }

    private void readFromInput(HollowBlobInput in, boolean isDelta) throws IOException {
        maxOrdinal = VarInt.readVInt(in);

        if(isDelta) {
            encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
            encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
        }

        bitsPerSetPointer = VarInt.readVInt(in);
        bitsPerSetSizeValue = VarInt.readVInt(in);
        bitsPerElement = VarInt.readVInt(in);
        bitsPerFixedLengthSetPortion = bitsPerSetPointer + bitsPerSetSizeValue;
        emptyBucketValue = (1 << bitsPerElement) - 1;
        totalNumberOfBuckets = VarInt.readVLong(in);

        setPointerAndSizeData = FixedLengthDataMode.newFrom(in, memoryMode, memoryRecycler);
        elementData = FixedLengthDataMode.newFrom(in, memoryMode, memoryRecycler);

        // debug.append("HollowSetTypeDataElements setPointerAndSizeData= \n");
        // setPointerAndSizeData.pp(debug);
        // debug.append("HollowSetTypeDataElements elementData= \n");
        // elementData.pp(debug);
        // debug.append("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * end HollowSetTypeDataElements\n");
    }

    static void discardFromStream(HollowBlobInput in, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); // max ordinal

        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(in); // max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }

            /// statistics
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVLong(in);

            /// fixed-length data
            FixedLengthData.discardFrom(in);
            FixedLengthData.discardFrom(in);
        }
    }

    public void applyDelta(HollowSetTypeDataElements fromData, HollowSetTypeDataElements deltaData) {
        new HollowSetDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    public void destroy() {
        FixedLengthDataMode.destroy(setPointerAndSizeData, memoryRecycler);
        FixedLengthDataMode.destroy(elementData, memoryRecycler);
    }
}
