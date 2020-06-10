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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

/**
 * This class contains the logic for applying a delta to a current LIST type state
 * to produce the next LIST type state.
 * 
 * Not intended for external consumption.
 */
class HollowListDeltaApplicator {

    private final HollowListTypeDataElements from;
    private final HollowListTypeDataElements delta;
    private final HollowListTypeDataElements target;

    private long currentFromStateCopyStartBit = 0;
    private long currentDeltaCopyStartBit = 0;
    private long currentWriteStartBit = 0;

    private long currentFromStateStartElement = 0;
    private long currentDeltaStartElement = 0;
    private long currentWriteStartElement = 0;

    private GapEncodedVariableLengthIntegerReader removalsReader;
    private GapEncodedVariableLengthIntegerReader additionsReader;

    HollowListDeltaApplicator(HollowListTypeDataElements from, HollowListTypeDataElements delta, HollowListTypeDataElements target) {
        this.from = from;
        this.delta = delta;
        this.target = target;
    }

    public void applyDelta(boolean isRadial) {
        removalsReader = isRadial ? delta.encodedRemovals : from.encodedRemovals == null ? GapEncodedVariableLengthIntegerReader.EMPTY_READER : from.encodedRemovals;
        additionsReader = delta.encodedAdditions;
        removalsReader.reset();
        additionsReader.reset();

        if(!isRadial)
            target.encodedRemovals = delta.encodedRemovals;

        target.maxOrdinal = delta.maxOrdinal;
        target.totalNumberOfElements = delta.totalNumberOfElements;
        target.bitsPerListPointer = delta.bitsPerListPointer;
        target.bitsPerElement = delta.bitsPerElement;

        target.listPointerArray = new FixedLengthElementArray(target.memoryRecycler, ((long)target.maxOrdinal + 1) * target.bitsPerListPointer);
        target.elementArray = new FixedLengthElementArray(target.memoryRecycler, target.totalNumberOfElements * target.bitsPerElement);

        if(target.bitsPerListPointer == from.bitsPerListPointer
                && target.bitsPerElement == from.bitsPerElement)
                    fastDelta();
        else
            slowDelta();

        from.encodedRemovals = null;
        removalsReader.destroy();
        additionsReader.destroy();
    }

    private void slowDelta() {
        for(int i=0;i<=target.maxOrdinal;i++) {
            mergeOrdinal(i);
        }
    }

    private void fastDelta() {
        int i = 0;
        int bulkCopyEndOrdinal = Math.min(from.maxOrdinal, target.maxOrdinal);

        while(i <= target.maxOrdinal) {
            int nextElementDiff = Math.min(additionsReader.nextElement(), removalsReader.nextElement());

            if(nextElementDiff == i || i > bulkCopyEndOrdinal) {
                mergeOrdinal(i++);
            } else {
                int recordsToCopy = nextElementDiff - i;
                if(nextElementDiff > bulkCopyEndOrdinal)
                    recordsToCopy = bulkCopyEndOrdinal - i + 1;

                fastCopyRecords(recordsToCopy);

                i += recordsToCopy;
            }
        }
    }

    private void fastCopyRecords(int recordsToCopy) {
        long listPointerBitsToCopy = (long)recordsToCopy * target.bitsPerListPointer;
        long eachListPointerDifference = currentWriteStartElement - currentFromStateStartElement;

        target.listPointerArray.copyBits(from.listPointerArray, currentFromStateCopyStartBit, currentWriteStartBit, listPointerBitsToCopy);
        target.listPointerArray.incrementMany(currentWriteStartBit, eachListPointerDifference, target.bitsPerListPointer, recordsToCopy);

        currentFromStateCopyStartBit += listPointerBitsToCopy;
        currentWriteStartBit += listPointerBitsToCopy;

        long fromDataEndElement = from.listPointerArray.getElementValue(currentFromStateCopyStartBit - from.bitsPerListPointer, from.bitsPerListPointer);
        long elementsToCopy = fromDataEndElement - currentFromStateStartElement;
        long bitsToCopy = elementsToCopy * from.bitsPerElement;

        target.elementArray.copyBits(from.elementArray, currentFromStateStartElement * from.bitsPerElement, currentWriteStartElement * from.bitsPerElement, bitsToCopy);

        currentFromStateStartElement += elementsToCopy;
        currentWriteStartElement += elementsToCopy;
    }

    private void mergeOrdinal(int i) {
        boolean addFromDelta = additionsReader.nextElement() == i;
        boolean removeData = removalsReader.nextElement() == i;

        if(addFromDelta) {
            addFromDelta(additionsReader);
        }

        if(i <= from.maxOrdinal) {
            long fromDataEndElement = from.listPointerArray.getElementValue(currentFromStateCopyStartBit, from.bitsPerListPointer);
            if(!removeData) {
                for(long elementIdx=currentFromStateStartElement; elementIdx<fromDataEndElement; elementIdx++) {
                    long elementOrdinal = from.elementArray.getElementValue(elementIdx * from.bitsPerElement, from.bitsPerElement);
                    target.elementArray.setElementValue(currentWriteStartElement * target.bitsPerElement, target.bitsPerElement, elementOrdinal);
                    currentWriteStartElement++;
                }
            } else {
                removalsReader.advance();
            }

            currentFromStateStartElement = fromDataEndElement;
            currentFromStateCopyStartBit += from.bitsPerListPointer;
        }


        target.listPointerArray.setElementValue(currentWriteStartBit, target.bitsPerListPointer, currentWriteStartElement);
        currentWriteStartBit += target.bitsPerListPointer;
    }

    private void addFromDelta(GapEncodedVariableLengthIntegerReader additionsReader) {
        long deltaDataEndElement = delta.listPointerArray.getElementValue(currentDeltaCopyStartBit, delta.bitsPerListPointer);
        for(long elementIdx=currentDeltaStartElement; elementIdx<deltaDataEndElement; elementIdx++) {
            long elementOrdinal = delta.elementArray.getElementValue(elementIdx * delta.bitsPerElement, delta.bitsPerElement);
            target.elementArray.setElementValue(currentWriteStartElement * target.bitsPerElement, target.bitsPerElement, elementOrdinal);
            currentWriteStartElement++;
        }
        currentDeltaStartElement = deltaDataEndElement;
        currentDeltaCopyStartBit += delta.bitsPerListPointer;
        additionsReader.advance();
    }
}
