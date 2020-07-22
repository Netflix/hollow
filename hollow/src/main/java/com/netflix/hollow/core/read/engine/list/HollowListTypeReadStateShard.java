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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowListTypeReadStateShard {

    private volatile HollowListTypeDataElements currentDataVolatile;

    public int getElementOrdinal(int ordinal, int listIndex) {
        HollowListTypeDataElements currentData;
        int elementOrdinal;

        do {
            long startAndEndElement;

            do {
                currentData = this.currentDataVolatile;

                long fixedLengthOffset = (long)ordinal * currentData.bitsPerListPointer;

                startAndEndElement = ordinal == 0 ?
                        currentData.listPointerData.getElementValue(fixedLengthOffset, currentData.bitsPerListPointer) << currentData.bitsPerListPointer :
                            currentData.listPointerData.getElementValue(fixedLengthOffset - currentData.bitsPerListPointer, currentData.bitsPerListPointer * 2);

            } while(readWasUnsafe(currentData));

            long endElement = startAndEndElement >> currentData.bitsPerListPointer;
            long startElement = startAndEndElement &  ((1 << currentData.bitsPerListPointer) - 1);

            long elementIndex = startElement + listIndex;

            if(elementIndex >= endElement)
                throw new ArrayIndexOutOfBoundsException("Array index out of bounds: " + listIndex + ", list size: " + (endElement - startElement));

            elementOrdinal = (int)currentData.elementData.getElementValue(elementIndex * currentData.bitsPerElement, currentData.bitsPerElement);
        } while(readWasUnsafe(currentData));

        return elementOrdinal;
    }

    public int size(int ordinal) {
        HollowListTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentDataVolatile;

            long fixedLengthOffset = (long)ordinal * currentData.bitsPerListPointer;

            long startAndEndElement = ordinal == 0 ?
                    currentData.listPointerData.getElementValue(fixedLengthOffset, currentData.bitsPerListPointer) << currentData.bitsPerListPointer :
                        currentData.listPointerData.getElementValue(fixedLengthOffset - currentData.bitsPerListPointer, currentData.bitsPerListPointer * 2);

            long endElement = startAndEndElement >> currentData.bitsPerListPointer;
            long startElement = startAndEndElement &  ((1 << currentData.bitsPerListPointer) - 1);

            size = (int)(endElement - startElement);
        } while(readWasUnsafe(currentData));

        return size;
    }

    void invalidate() {
        setCurrentData(null);
    }

    HollowListTypeDataElements currentDataElements() {
        return currentDataVolatile;
    }

    private boolean readWasUnsafe(HollowListTypeDataElements data) {
        HollowUnsafeHandle.getUnsafe().loadFence();
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowListTypeDataElements data) {
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        int ordinal = populatedOrdinals.nextSetBit(shardNumber);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                int size = size(shardOrdinal);
    
                checksum.applyInt(ordinal);
                for(int i=0;i<size;i++)
                    checksum.applyInt(getElementOrdinal(shardOrdinal, i));

                ordinal = ordinal + numShards;
            } else {
                // Round up ordinal
                int r = (ordinal & -numShards) + shardNumber;
                ordinal = (r <= ordinal) ? r + numShards : r;
            }
            ordinal = populatedOrdinals.nextSetBit(ordinal);
        }
    }

    public long getApproximateHeapFootprintInBytes() {
        HollowListTypeDataElements currentData = currentDataVolatile;
        long requiredListPointerBits = ((long)currentData.maxOrdinal + 1) * currentData.bitsPerListPointer;
        long requiredElementBits = currentData.totalNumberOfElements * currentData.bitsPerElement;
        long requiredBits = requiredListPointerBits + requiredElementBits;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowListTypeDataElements currentData = currentDataVolatile;
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= currentData.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += currentData.bitsPerListPointer;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
}
