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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

/**
 * This class contains the logic for applying a delta to a current SET type state
 * to produce the next SET type state.
 * 
 * Not intended for external consumption.
 */
class HollowSetDeltaApplicator {

    private final HollowSetTypeDataElements from;
    private final HollowSetTypeDataElements delta;
    private final HollowSetTypeDataElements target;

    private long currentFromStateCopyStartBit = 0;
    private long currentDeltaCopyStartBit = 0;
    private long currentWriteStartBit = 0;

    private long currentFromStateStartBucket = 0;
    private long currentDeltaStartBucket = 0;
    private long currentWriteStartBucket = 0;

    private GapEncodedVariableLengthIntegerReader removalsReader;
    private GapEncodedVariableLengthIntegerReader additionsReader;

    HollowSetDeltaApplicator(HollowSetTypeDataElements from, HollowSetTypeDataElements delta, HollowSetTypeDataElements target) {
        this.from = from;
        this.delta = delta;
        this.target = target;
    }


    public void applyDelta() {
        removalsReader = from.encodedRemovals == null ? GapEncodedVariableLengthIntegerReader.EMPTY_READER : from.encodedRemovals;
        additionsReader = delta.encodedAdditions;
        removalsReader.reset();
        additionsReader.reset();

        target.encodedRemovals = delta.encodedRemovals;

        target.maxOrdinal = delta.maxOrdinal;

        target.bitsPerSetPointer = delta.bitsPerSetPointer;
        target.bitsPerSetSizeValue = delta.bitsPerSetSizeValue;
        target.bitsPerFixedLengthSetPortion = delta.bitsPerFixedLengthSetPortion;
        target.bitsPerElement = delta.bitsPerElement;
        target.emptyBucketValue = delta.emptyBucketValue;
        target.totalNumberOfBuckets = delta.totalNumberOfBuckets;

        target.setPointerAndSizeData = new FixedLengthElementArray(target.memoryRecycler, ((long)target.maxOrdinal + 1) * target.bitsPerFixedLengthSetPortion);
        target.elementData = new FixedLengthElementArray(target.memoryRecycler, target.totalNumberOfBuckets * target.bitsPerElement);

        if(target.bitsPerSetPointer == from.bitsPerSetPointer
                && target.bitsPerSetSizeValue == from.bitsPerSetSizeValue
                && target.bitsPerElement == from.bitsPerElement)
                    fastDelta();
        else
            slowDelta();

        from.encodedRemovals = null;
        removalsReader.destroy();
        additionsReader.destroy();

    }

    private void slowDelta() {
        for(int i=0; i<=target.maxOrdinal; i++) {
            mergeOrdinal(i);
        }
    }

    private void fastDelta() {
        int i=0;
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
        long setPointerAndSizeBitsToCopy = (long)recordsToCopy * target.bitsPerFixedLengthSetPortion;
        long eachSetPointerDifference = currentWriteStartBucket - currentFromStateStartBucket;

        target.setPointerAndSizeData.copyBits(from.setPointerAndSizeData, currentFromStateCopyStartBit, currentWriteStartBit, setPointerAndSizeBitsToCopy);
        target.setPointerAndSizeData.incrementMany(currentWriteStartBit, eachSetPointerDifference, target.bitsPerFixedLengthSetPortion, recordsToCopy);

        currentFromStateCopyStartBit += setPointerAndSizeBitsToCopy;
        currentWriteStartBit += setPointerAndSizeBitsToCopy;

        long fromDataEndElement = from.setPointerAndSizeData.getElementValue(currentFromStateCopyStartBit - from.bitsPerFixedLengthSetPortion, from.bitsPerSetPointer);
        long bucketsToCopy = fromDataEndElement - currentFromStateStartBucket;
        long bitsToCopy = bucketsToCopy * from.bitsPerElement;

        target.elementData.copyBits(from.elementData, currentFromStateStartBucket * from.bitsPerElement, currentWriteStartBucket * from.bitsPerElement, bitsToCopy);

        currentFromStateStartBucket += bucketsToCopy;
        currentWriteStartBucket += bucketsToCopy;
    }

    private void mergeOrdinal(int i) {
        boolean addFromDelta = additionsReader.nextElement() == i;
        boolean removeData = removalsReader.nextElement() == i;

        if(addFromDelta) {
            addFromDelta(additionsReader);
        }

        if(i <= from.maxOrdinal) {
            long fromDataEndBucket = from.setPointerAndSizeData.getElementValue(currentFromStateCopyStartBit, from.bitsPerSetPointer);
            if(!removeData) {
                for(long bucketIdx=currentFromStateStartBucket; bucketIdx<fromDataEndBucket; bucketIdx++) {
                    long bucketValue = from.elementData.getElementValue(bucketIdx * from.bitsPerElement, from.bitsPerElement);
                    if(bucketValue == from.emptyBucketValue)
                        bucketValue = target.emptyBucketValue;
                    target.elementData.setElementValue(currentWriteStartBucket * target.bitsPerElement, target.bitsPerElement, bucketValue);
                    currentWriteStartBucket++;
                }
                long fromDataSize = from.setPointerAndSizeData.getElementValue(currentFromStateCopyStartBit + from.bitsPerSetPointer, from.bitsPerSetSizeValue);
                target.setPointerAndSizeData.setElementValue(currentWriteStartBit + target.bitsPerSetPointer, target.bitsPerSetSizeValue, fromDataSize);
            } else {
                removalsReader.advance();
            }

            currentFromStateStartBucket = fromDataEndBucket;
            currentFromStateCopyStartBit += from.bitsPerFixedLengthSetPortion;
        }

        target.setPointerAndSizeData.setElementValue(currentWriteStartBit, target.bitsPerSetPointer, currentWriteStartBucket);
        currentWriteStartBit += target.bitsPerFixedLengthSetPortion;
    }



    private void addFromDelta(GapEncodedVariableLengthIntegerReader additionsReader) {
        long deltaDataEndBucket = delta.setPointerAndSizeData.getElementValue(currentDeltaCopyStartBit, delta.bitsPerSetPointer);
        for(long bucketIdx=currentDeltaStartBucket; bucketIdx<deltaDataEndBucket; bucketIdx++) {
            long bucketValue = delta.elementData.getElementValue(bucketIdx * delta.bitsPerElement, delta.bitsPerElement);
            target.elementData.setElementValue(currentWriteStartBucket * target.bitsPerElement, target.bitsPerElement, bucketValue);
            currentWriteStartBucket++;
        }
        long deltaDataSize = delta.setPointerAndSizeData.getElementValue(currentDeltaCopyStartBit + delta.bitsPerSetPointer, delta.bitsPerSetSizeValue);
        target.setPointerAndSizeData.setElementValue(currentWriteStartBit + target.bitsPerSetPointer, target.bitsPerSetSizeValue, deltaDataSize);

        currentDeltaStartBucket = deltaDataEndBucket;
        currentDeltaCopyStartBit += delta.bitsPerFixedLengthSetPortion;
        additionsReader.advance();
    }

}
