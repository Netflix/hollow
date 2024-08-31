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
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElements;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowListTypeReadState}.
 * 
 * During a delta, the HollowListTypeReadState will create a new HollowListTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowListTypeDataElements extends AbstractHollowTypeDataElements {

    FixedLengthData listPointerData;
    FixedLengthData elementData;

    int bitsPerListPointer;
    int bitsPerElement = 0;
    long totalNumberOfElements = 0;

    public HollowListTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this(MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowListTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        super(memoryMode, memoryRecycler);
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

    public void applyDelta(HollowListTypeDataElements fromData, HollowListTypeDataElements deltaData) {
        new HollowListDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    @Override
    public void destroy() {
        FixedLengthDataFactory.destroy(listPointerData, memoryRecycler);
        FixedLengthDataFactory.destroy(elementData, memoryRecycler);
    }

    long getStartElement(int ordinal) {
        return ordinal == 0 ? 0 : listPointerData.getElementValue(((long)(ordinal-1) * bitsPerListPointer), bitsPerListPointer);
    }

    long getEndElement(int ordinal) {
        return listPointerData.getElementValue((long)ordinal * bitsPerListPointer, bitsPerListPointer);
    }

    void copyElementsFrom(long startElement, HollowListTypeDataElements src, long srcStartElement, long srcEndElement) {
        if (bitsPerElement == src.bitsPerElement) {
            // fast path can bulk copy elements
            long numElements = srcEndElement - srcStartElement;
            elementData.copyBits(src.elementData, srcStartElement * bitsPerElement, startElement * bitsPerElement, numElements * bitsPerElement);
        } else {
            for (long element=srcStartElement;element<srcEndElement;element++) {
                long elementVal = src.elementData.getElementValue(element * src.bitsPerElement, src.bitsPerElement);
                elementData.setElementValue(startElement * bitsPerElement, bitsPerElement, elementVal);
                startElement++;
            }
        }
    }
}
