/*
 *
 *  Copyright 2016 Netflix, Inc.
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
import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;

/**
 * This class contains the logic for extracting the removed records from an SET type state
 * to produce a historical type state.
 * 
 * Not intended for external consumption.
 */
public class HollowSetDeltaHistoricalStateCreator {

    private final HollowSetTypeReadState typeState;
    private final HollowSetTypeDataElements stateEngineDataElements;
    private final HollowSetTypeDataElements historicalDataElements;
    private final RemovedOrdinalIterator iter;

    private IntMap ordinalMapping;
    private int nextOrdinal;
    private long nextStartBucket;

    public HollowSetDeltaHistoricalStateCreator(HollowSetTypeReadState typeState) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowSetTypeDataElements(WastefulRecycler.DEFAULT_INSTANCE);
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class));
    }

    public void populateHistory() {
        populateStats();

        historicalDataElements.setPointerAndSizeArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, historicalDataElements.bitsPerFixedLengthSetPortion * (historicalDataElements.maxOrdinal + 1));
        historicalDataElements.elementArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, historicalDataElements.bitsPerElement * historicalDataElements.totalNumberOfBuckets);

        iter.reset();

        int ordinal = iter.next();
        while(ordinal != -1) {
            ordinalMapping.put(ordinal, nextOrdinal);
            copyRecord(ordinal);

            ordinal = iter.next();
        }
    }

    public IntMap getOrdinalMapping() {
        return ordinalMapping;
    }

    public HollowSetTypeReadState createHistoricalTypeReadState() {
        HollowSetTypeReadState historicalTypeState = new HollowSetTypeReadState(null, typeState.getSchema());
        historicalTypeState.setCurrentData(historicalDataElements);
        return historicalTypeState;
    }

    private void populateStats() {
        iter.reset();
        int removedEntryCount = 0;
        int maxSize = 0;
        long totalBucketCount = 0;
        int ordinal = iter.next();

        while(ordinal != -1) {
            removedEntryCount++;
            int size = typeState.size(ordinal);
            if(size > maxSize)
                maxSize = size;
            totalBucketCount += HashCodes.hashTableSize(size);
            ordinal = iter.next();
        }

        historicalDataElements.maxOrdinal = removedEntryCount - 1;
        historicalDataElements.bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(totalBucketCount);
        historicalDataElements.bitsPerSetSizeValue = 64 - Long.numberOfLeadingZeros(maxSize);
        historicalDataElements.bitsPerFixedLengthSetPortion = historicalDataElements.bitsPerSetPointer + historicalDataElements.bitsPerSetSizeValue;
        historicalDataElements.bitsPerElement = stateEngineDataElements.bitsPerElement;
        historicalDataElements.emptyBucketValue = stateEngineDataElements.emptyBucketValue;
        historicalDataElements.totalNumberOfBuckets = totalBucketCount;

        ordinalMapping = new IntMap(removedEntryCount);
    }

    private void copyRecord(int ordinal) {
        long bitsPerBucket = historicalDataElements.bitsPerElement;
        long size = typeState.size(ordinal);

        long fromStartBucket = ordinal == 0 ? 0 : stateEngineDataElements.setPointerAndSizeArray.getElementValue((long)(ordinal - 1) * stateEngineDataElements.bitsPerFixedLengthSetPortion, stateEngineDataElements.bitsPerSetPointer);
        long fromEndBucket = stateEngineDataElements.setPointerAndSizeArray.getElementValue((long)ordinal * stateEngineDataElements.bitsPerFixedLengthSetPortion, stateEngineDataElements.bitsPerSetPointer);
        long numBuckets = fromEndBucket - fromStartBucket;

        historicalDataElements.setPointerAndSizeArray.setElementValue((long)nextOrdinal * historicalDataElements.bitsPerFixedLengthSetPortion, historicalDataElements.bitsPerSetPointer, nextStartBucket + numBuckets);
        historicalDataElements.setPointerAndSizeArray.setElementValue((long)(nextOrdinal * historicalDataElements.bitsPerFixedLengthSetPortion) + historicalDataElements.bitsPerSetPointer, historicalDataElements.bitsPerSetSizeValue, size);

        historicalDataElements.elementArray.copyBits(stateEngineDataElements.elementArray, fromStartBucket * bitsPerBucket, nextStartBucket * bitsPerBucket, numBuckets * bitsPerBucket);

        nextOrdinal++;
        nextStartBucket += numBuckets;
    }

}
