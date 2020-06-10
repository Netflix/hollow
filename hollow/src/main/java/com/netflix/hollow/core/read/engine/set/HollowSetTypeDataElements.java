/*
 *  Copyright 2016-2020 Netflix, Inc.
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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowSetTypeReadState}.
 * 
 * During a delta, the HollowSetTypeReadState will create a new HollowSetTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */

public class HollowSetTypeDataElements {

    int maxOrdinal;

    FixedLengthElementArray setPointerAndSizeArray;
    FixedLengthElementArray elementArray;

    GapEncodedVariableLengthIntegerReader encodedRemovals;
    GapEncodedVariableLengthIntegerReader encodedAdditions;

    int bitsPerSetPointer;
    int bitsPerSetSizeValue;
    int bitsPerFixedLengthSetPortion;
    int bitsPerElement;
    int emptyBucketValue;
    long totalNumberOfBuckets;

    final ArraySegmentRecycler memoryRecycler;

    public HollowSetTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this.memoryRecycler = memoryRecycler;
    }

    void readSnapshot(DataInputStream dis) throws IOException {
        readFromStream(dis, false);
    }

    void readDelta(DataInputStream dis) throws IOException {
        readFromStream(dis, true);
    }

    private void readFromStream(DataInputStream dis, boolean isDelta) throws IOException {
        maxOrdinal = VarInt.readVInt(dis);

        if(isDelta) {
            encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
            encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
        }

        bitsPerSetPointer = VarInt.readVInt(dis);
        bitsPerSetSizeValue = VarInt.readVInt(dis);
        bitsPerElement = VarInt.readVInt(dis);
        bitsPerFixedLengthSetPortion = bitsPerSetPointer + bitsPerSetSizeValue;
        emptyBucketValue = (1 << bitsPerElement) - 1;
        totalNumberOfBuckets = VarInt.readVLong(dis);

        setPointerAndSizeArray = FixedLengthElementArray.deserializeFrom(dis, memoryRecycler);

        elementArray = FixedLengthElementArray.deserializeFrom(dis, memoryRecycler);
    }

    static void discardFromStream(DataInputStream dis, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(dis); // max ordinal
        
        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(dis); // max ordinal
    
            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
            }
    
            /// statistics
            VarInt.readVInt(dis);
            VarInt.readVInt(dis);
            VarInt.readVInt(dis);
            VarInt.readVLong(dis);
    
            /// fixed-length data
            FixedLengthElementArray.discardFrom(dis);
            FixedLengthElementArray.discardFrom(dis);
        }
    }

    public void applyDelta(HollowSetTypeDataElements fromData, HollowSetTypeDataElements deltaData, boolean isRadial) {
        new HollowSetDeltaApplicator(fromData, deltaData, this).applyDelta(isRadial);
    }

    public void destroy() {
        setPointerAndSizeArray.destroy(memoryRecycler);
        elementArray.destroy(memoryRecycler);
    }
}
