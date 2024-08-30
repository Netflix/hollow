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

import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowListTypeReadStateShard implements HollowTypeReadStateShard {

    final HollowListTypeDataElements dataElements;
    final int shardOrdinalShift;

    @Override
    public HollowListTypeDataElements getDataElements() {
        return dataElements;
    }

    @Override
    public int getShardOrdinalShift() {
        return shardOrdinalShift;
    }

    public HollowListTypeReadStateShard(HollowListTypeDataElements dataElements, int shardOrdinalShift) {
        this.shardOrdinalShift = shardOrdinalShift;
        this.dataElements = dataElements;
    }

    public int size(int ordinal) {
        long startElement = dataElements.getStartElement(ordinal);
        long endElement = dataElements.getEndElement(ordinal);
        int size = (int)(endElement - startElement);

        return size;
    }

    protected void applyShardToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        int ordinal = populatedOrdinals.nextSetBit(shardNumber);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                int size = size(shardOrdinal);
    
                checksum.applyInt(ordinal);
                long startElement = dataElements.getStartElement(shardOrdinal);
                long endElement = dataElements.getEndElement(shardOrdinal);
                for(int i=0;i<size;i++)
                    checksum.applyInt(getElementOrdinal(startElement, endElement, i));

                ordinal = ordinal + numShards;
            } else {
                // Round up ordinal
                int r = (ordinal & -numShards) + shardNumber;
                ordinal = (r <= ordinal) ? r + numShards : r;
            }
            ordinal = populatedOrdinals.nextSetBit(ordinal);
        }
    }

    int getElementOrdinal(long startElement, long endElement, int listIndex) {
        long elementIndex = startElement + listIndex;
        if(elementIndex >= endElement)
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds: " + listIndex + ", list size: " + (endElement - startElement));

        int elementOrdinal = (int)dataElements.elementData.getElementValue(elementIndex * dataElements.bitsPerElement, dataElements.bitsPerElement);
        return elementOrdinal;
    }

    public long getApproximateHeapFootprintInBytes() {
        long requiredListPointerBits = ((long)dataElements.maxOrdinal + 1) * dataElements.bitsPerListPointer;
        long requiredElementBits = dataElements.totalNumberOfElements * dataElements.bitsPerElement;
        long requiredBits = requiredListPointerBits + requiredElementBits;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= dataElements.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += dataElements.bitsPerListPointer;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
}
