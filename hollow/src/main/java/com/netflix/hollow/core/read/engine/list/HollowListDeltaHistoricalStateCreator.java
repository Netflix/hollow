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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;

/**
 * This class contains the logic for extracting the removed records from a LIST type state
 * to produce a historical type state.
 * 
 * Not intended for external consumption.
 */
public class HollowListDeltaHistoricalStateCreator {

    private final HollowListTypeReadState typeState;
    private final HollowListTypeDataElements stateEngineDataElements[];
    private final HollowListTypeDataElements historicalDataElements;
    private final RemovedOrdinalIterator iter;

    private final int shardNumberMask;
    private final int shardOrdinalShift;

    private IntMap ordinalMapping;
    private int nextOrdinal = 0;
    private long nextStartElement = 0;

    public HollowListDeltaHistoricalStateCreator(HollowListTypeReadState typeState) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowListTypeDataElements(WastefulRecycler.DEFAULT_INSTANCE);
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class));
        this.shardNumberMask = stateEngineDataElements.length - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(stateEngineDataElements.length);
    }

    public void populateHistory() {
        populateStats();

        historicalDataElements.listPointerData = new FixedLengthElementArray(historicalDataElements.memoryRecycler, ((long)historicalDataElements.maxOrdinal + 1) * historicalDataElements.bitsPerListPointer);
        historicalDataElements.elementData = new FixedLengthElementArray(historicalDataElements.memoryRecycler, historicalDataElements.totalNumberOfElements * historicalDataElements.bitsPerElement);

        iter.reset();

        int ordinal = iter.next();
        while(ordinal != ORDINAL_NONE) {
            ordinalMapping.put(ordinal, nextOrdinal);
            copyRecord(ordinal);

            ordinal = iter.next();
        }
    }

    public IntMap getOrdinalMapping() {
        return ordinalMapping;
    }

    public HollowListTypeReadState createHistoricalTypeReadState() {
        HollowListTypeReadState historicalTypeState = new HollowListTypeReadState(null, typeState.getSchema(), 1);
        historicalTypeState.setCurrentData(historicalDataElements);
        return historicalTypeState;
    }

    private void populateStats() {
        iter.reset();
        int removedEntryCount = 0;
        long totalElementCount = 0;
        int ordinal = iter.next();
        while(ordinal != ORDINAL_NONE) {
            removedEntryCount++;
            totalElementCount += typeState.size(ordinal);
            ordinal = iter.next();
        }

        historicalDataElements.maxOrdinal = removedEntryCount - 1;
        historicalDataElements.totalNumberOfElements = totalElementCount;
        historicalDataElements.bitsPerListPointer = totalElementCount == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalElementCount);
        historicalDataElements.bitsPerElement = stateEngineDataElements[0].bitsPerElement;

        ordinalMapping = new IntMap(removedEntryCount);
    }

    private void copyRecord(int ordinal) {
        int shard = ordinal & shardNumberMask;
        int shardOrdinal = ordinal >> shardOrdinalShift; 
        
        long bitsPerElement = stateEngineDataElements[shard].bitsPerElement;
        long fromStartElement = shardOrdinal == 0 ? 0 : stateEngineDataElements[shard].listPointerData.getElementValue((long)(shardOrdinal - 1) * stateEngineDataElements[shard].bitsPerListPointer, stateEngineDataElements[shard].bitsPerListPointer);
        long fromEndElement = stateEngineDataElements[shard].listPointerData.getElementValue((long)shardOrdinal * stateEngineDataElements[shard].bitsPerListPointer, stateEngineDataElements[shard].bitsPerListPointer);
        long size = fromEndElement - fromStartElement;

        historicalDataElements.elementData.copyBits(stateEngineDataElements[shard].elementData, fromStartElement * bitsPerElement, nextStartElement * bitsPerElement, size * bitsPerElement);
        historicalDataElements.listPointerData.setElementValue((long)nextOrdinal * historicalDataElements.bitsPerListPointer, historicalDataElements.bitsPerListPointer, nextStartElement + size);

        ordinalMapping.put(ordinal, nextOrdinal);
        nextOrdinal++;
        nextStartElement += size;
    }

}
