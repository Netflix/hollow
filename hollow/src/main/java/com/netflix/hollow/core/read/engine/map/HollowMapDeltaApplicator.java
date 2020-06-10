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
package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

/**
 * This class contains the logic for applying a delta to a current MAP type state
 * to produce the next MAP type state.
 * 
 * Not intended for external consumption.
 */
class HollowMapDeltaApplicator {

    private final HollowMapTypeDataElements from;
    private final HollowMapTypeDataElements delta;
    private final HollowMapTypeDataElements target;

    private long currentFromStateCopyStartBit = 0;
    private long currentDeltaCopyStartBit = 0;
    private long currentWriteStartBit = 0;

    private long currentFromStateStartBucket = 0;
    private long currentDeltaStartBucket = 0;
    private long currentWriteStartBucket = 0;

    private GapEncodedVariableLengthIntegerReader removalsReader;
    private GapEncodedVariableLengthIntegerReader additionsReader;

    HollowMapDeltaApplicator(HollowMapTypeDataElements from, HollowMapTypeDataElements delta, HollowMapTypeDataElements target) {
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

        target.bitsPerMapPointer = delta.bitsPerMapPointer;
        target.bitsPerMapSizeValue = delta.bitsPerMapSizeValue;
        target.bitsPerKeyElement = delta.bitsPerKeyElement;
        target.bitsPerValueElement = delta.bitsPerValueElement;
        target.bitsPerFixedLengthMapPortion = delta.bitsPerFixedLengthMapPortion;
        target.bitsPerMapEntry = delta.bitsPerMapEntry;
        target.emptyBucketKeyValue = delta.emptyBucketKeyValue;
        target.totalNumberOfBuckets = delta.totalNumberOfBuckets;

        target.mapPointerAndSizeArray = new FixedLengthElementArray(target.memoryRecycler, ((long)target.maxOrdinal + 1) * target.bitsPerFixedLengthMapPortion);
        target.entryArray = new FixedLengthElementArray(target.memoryRecycler, target.totalNumberOfBuckets * target.bitsPerMapEntry);

        if(target.bitsPerMapPointer == from.bitsPerMapPointer
                && target.bitsPerMapSizeValue == from.bitsPerMapSizeValue
                && target.bitsPerKeyElement == from.bitsPerKeyElement
                && target.bitsPerValueElement == from.bitsPerValueElement)
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
        long mapPointerAndSizeBitsToCopy = (long)recordsToCopy * target.bitsPerFixedLengthMapPortion;
        long eachMapPointerDifference = currentWriteStartBucket - currentFromStateStartBucket;

        target.mapPointerAndSizeArray.copyBits(from.mapPointerAndSizeArray, currentFromStateCopyStartBit, currentWriteStartBit, mapPointerAndSizeBitsToCopy);
        target.mapPointerAndSizeArray.incrementMany(currentWriteStartBit, eachMapPointerDifference, target.bitsPerFixedLengthMapPortion, recordsToCopy);

        currentFromStateCopyStartBit += mapPointerAndSizeBitsToCopy;
        currentWriteStartBit += mapPointerAndSizeBitsToCopy;

        long fromDataEndElement = from.mapPointerAndSizeArray.getElementValue(currentFromStateCopyStartBit - from.bitsPerFixedLengthMapPortion, from.bitsPerMapPointer);
        long bucketsToCopy = fromDataEndElement - currentFromStateStartBucket;
        long bitsToCopy = bucketsToCopy * from.bitsPerMapEntry;

        target.entryArray.copyBits(from.entryArray, currentFromStateStartBucket * from.bitsPerMapEntry, currentWriteStartBucket * from.bitsPerMapEntry, bitsToCopy);

        currentFromStateStartBucket += bucketsToCopy;
        currentWriteStartBucket += bucketsToCopy;
    }

    private void mergeOrdinal(int ordinal) {
        boolean addFromDelta = additionsReader.nextElement() == ordinal;
        boolean removeData = removalsReader.nextElement() == ordinal;

        if(addFromDelta) {
            addFromDelta(additionsReader);
        }

        if(ordinal <= from.maxOrdinal) {
            long fromDataEndBucket = from.mapPointerAndSizeArray.getElementValue(currentFromStateCopyStartBit, from.bitsPerMapPointer);
            if(!removeData) {
                for(long bucketIdx=currentFromStateStartBucket; bucketIdx<fromDataEndBucket; bucketIdx++) {
                    long bucketKey = from.entryArray.getElementValue(bucketIdx * from.bitsPerMapEntry, from.bitsPerKeyElement);
                    long bucketValue = from.entryArray.getElementValue(bucketIdx * from.bitsPerMapEntry + from.bitsPerKeyElement, from.bitsPerValueElement);
                    if(bucketKey == from.emptyBucketKeyValue)
                        bucketKey = target.emptyBucketKeyValue;
                    long currentWriteStartBucketBit = currentWriteStartBucket * target.bitsPerMapEntry;
                    target.entryArray.setElementValue(currentWriteStartBucketBit, target.bitsPerKeyElement, bucketKey);
                    target.entryArray.setElementValue(currentWriteStartBucketBit + target.bitsPerKeyElement, target.bitsPerValueElement, bucketValue);
                    currentWriteStartBucket++;
                }
                long fromDataSize = from.mapPointerAndSizeArray.getElementValue(currentFromStateCopyStartBit + from.bitsPerMapPointer, from.bitsPerMapSizeValue);
                target.mapPointerAndSizeArray.setElementValue(currentWriteStartBit + target.bitsPerMapPointer, target.bitsPerMapSizeValue, fromDataSize);
            } else {
                removalsReader.advance();
            }

            currentFromStateStartBucket = fromDataEndBucket;
            currentFromStateCopyStartBit += from.bitsPerFixedLengthMapPortion;
        }

        target.mapPointerAndSizeArray.setElementValue(currentWriteStartBit, target.bitsPerMapPointer, currentWriteStartBucket);
        currentWriteStartBit += target.bitsPerFixedLengthMapPortion;
    }

    private void addFromDelta(GapEncodedVariableLengthIntegerReader additionsReader) {
        long deltaDataEndBucket = delta.mapPointerAndSizeArray.getElementValue(currentDeltaCopyStartBit, delta.bitsPerMapPointer);
        for(long bucketIdx=currentDeltaStartBucket; bucketIdx<deltaDataEndBucket; bucketIdx++) {
            long bucketEntry = delta.entryArray.getElementValue(bucketIdx * delta.bitsPerMapEntry, delta.bitsPerMapEntry);
            target.entryArray.setElementValue(currentWriteStartBucket * target.bitsPerMapEntry, target.bitsPerMapEntry, bucketEntry);
            currentWriteStartBucket++;
        }
        long deltaDataSize = delta.mapPointerAndSizeArray.getElementValue(currentDeltaCopyStartBit + delta.bitsPerMapPointer, delta.bitsPerMapSizeValue);
        target.mapPointerAndSizeArray.setElementValue(currentWriteStartBit + target.bitsPerMapPointer, target.bitsPerMapSizeValue, deltaDataSize);

        currentDeltaStartBucket = deltaDataEndBucket;
        currentDeltaCopyStartBit += delta.bitsPerFixedLengthMapPortion;
        additionsReader.advance();
    }

}
