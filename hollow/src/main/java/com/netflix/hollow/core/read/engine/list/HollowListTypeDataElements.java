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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowListTypeReadState}.
 * 
 * During a delta, the HollowListTypeReadState will create a new HollowListTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowListTypeDataElements {

    int maxOrdinal;

    FixedLengthData listPointerData;
    FixedLengthData elementData;

    GapEncodedVariableLengthIntegerReader encodedAdditions;
    GapEncodedVariableLengthIntegerReader encodedRemovals;

    int bitsPerListPointer;
    int bitsPerElement;
    long totalNumberOfElements = 0;

    final ArraySegmentRecycler memoryRecycler;
    final MemoryMode memoryMode;
    final HollowSchema schemaForDiag;

    public HollowListTypeDataElements(HollowSchema schemaForDiag, ArraySegmentRecycler memoryRecycler) {
        this(schemaForDiag, MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowListTypeDataElements(HollowSchema schemaForDiag, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        this.schemaForDiag = schemaForDiag;
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

        bitsPerListPointer = VarInt.readVInt(in);
        bitsPerElement = VarInt.readVInt(in);
        totalNumberOfElements = VarInt.readVLong(in);

        listPointerData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
        elementData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
    }

    static void discardFromStream(HollowBlobInput in, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); /// max ordinal
        
        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(in); /// max ordinal
    
            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }
    
            /// statistics
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVLong(in);
    
            /// fixed-length data
            FixedLengthData.discardFrom(in);
            FixedLengthData.discardFrom(in);
        }
    }

    public void applyDelta(HollowListTypeDataElements fromData, HollowListTypeDataElements deltaData, int whichShardForDiag) throws IOException {
        new HollowListDeltaApplicator(fromData, deltaData, this, whichShardForDiag).applyDelta();
    }

    public void destroy() throws IOException {
        FixedLengthDataFactory.destroy(listPointerData, memoryRecycler);
        FixedLengthDataFactory.destroy(elementData, memoryRecycler);
    }

}
