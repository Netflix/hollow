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
package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;

/**
 * This class contains the logic for extracting the removed records from a MAP type state
 * to produce a historical type state.
 * 
 * Not intended for external consumption.
 */
public class HollowMapDeltaHistoricalStateCreator {

    private final HollowMapTypeReadState typeState;
    private final HollowMapTypeDataElements stateEngineDataElements[];
    private final HollowMapTypeDataElements historicalDataElements;
    private final RemovedOrdinalIterator iter;

    private final int shardNumberMask;
    private final int shardOrdinalShift;

    private IntMap ordinalMapping;
    private int nextOrdinal;
    private long nextStartBucket;

    public HollowMapDeltaHistoricalStateCreator(HollowMapTypeReadState typeState) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowMapTypeDataElements(WastefulRecycler.DEFAULT_INSTANCE);
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class));
        this.shardNumberMask = stateEngineDataElements.length - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(stateEngineDataElements.length);
    }

    public void populateHistory() {
        populateStats();

        historicalDataElements.mapPointerAndSizeArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, historicalDataElements.bitsPerFixedLengthMapPortion * (historicalDataElements.maxOrdinal + 1));
        historicalDataElements.entryArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, historicalDataElements.bitsPerMapEntry * historicalDataElements.totalNumberOfBuckets);

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

    public HollowMapTypeReadState createHistoricalTypeReadState() {
        HollowMapTypeReadState historicalTypeState = new HollowMapTypeReadState(null, typeState.getSchema(), 1);
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
        historicalDataElements.bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(totalBucketCount);
        historicalDataElements.bitsPerMapSizeValue = 64 - Long.numberOfLeadingZeros(maxSize);
        historicalDataElements.bitsPerFixedLengthMapPortion = historicalDataElements.bitsPerMapPointer + historicalDataElements.bitsPerMapSizeValue;
        historicalDataElements.bitsPerKeyElement = stateEngineDataElements[0].bitsPerKeyElement;
        historicalDataElements.bitsPerValueElement = stateEngineDataElements[0].bitsPerValueElement;
        historicalDataElements.bitsPerMapEntry = stateEngineDataElements[0].bitsPerMapEntry;
        historicalDataElements.emptyBucketKeyValue = stateEngineDataElements[0].emptyBucketKeyValue;
        historicalDataElements.totalNumberOfBuckets = totalBucketCount;

        ordinalMapping = new IntMap(removedEntryCount);
    }

    private void copyRecord(int ordinal) {
        int shard = ordinal & shardNumberMask;
        int shardOrdinal = ordinal >> shardOrdinalShift; 

        long bitsPerBucket = historicalDataElements.bitsPerMapEntry;
        long size = typeState.size(ordinal);

        long fromStartBucket = shardOrdinal == 0 ? 0 : stateEngineDataElements[shard].mapPointerAndSizeArray.getElementValue((long)(shardOrdinal - 1) * stateEngineDataElements[shard].bitsPerFixedLengthMapPortion, stateEngineDataElements[shard].bitsPerMapPointer);
        long fromEndBucket = stateEngineDataElements[shard].mapPointerAndSizeArray.getElementValue((long)shardOrdinal * stateEngineDataElements[shard].bitsPerFixedLengthMapPortion, stateEngineDataElements[shard].bitsPerMapPointer);
        long numBuckets = fromEndBucket - fromStartBucket;

        historicalDataElements.mapPointerAndSizeArray.setElementValue(nextOrdinal * historicalDataElements.bitsPerFixedLengthMapPortion, historicalDataElements.bitsPerMapPointer, nextStartBucket + numBuckets);
        historicalDataElements.mapPointerAndSizeArray.setElementValue((nextOrdinal * historicalDataElements.bitsPerFixedLengthMapPortion) + historicalDataElements.bitsPerMapPointer, historicalDataElements.bitsPerMapSizeValue, size);

        historicalDataElements.entryArray.copyBits(stateEngineDataElements[shard].entryArray, fromStartBucket * bitsPerBucket, nextStartBucket * bitsPerBucket, numBuckets * bitsPerBucket);

        nextOrdinal++;
        nextStartBucket += numBuckets;
    }

}
