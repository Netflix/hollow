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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;

import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;

/**
 * This class contains the logic for extracting the removed records from a LIST type state
 * to produce a historical type state.
 * 
 * Not intended for external consumption.
 */
public class HollowListDeltaHistoricalStateCreator {

    private final HollowListTypeReadState typeState;
    private final HollowListTypeDataElements stateEngineDataElements;
    private final HollowListTypeDataElements historicalDataElements;
    private final RemovedOrdinalIterator iter;

    private IntMap ordinalMapping;
    private int nextOrdinal = 0;
    private long nextStartElement = 0;

    public HollowListDeltaHistoricalStateCreator(HollowListTypeReadState typeState) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowListTypeDataElements(WastefulRecycler.DEFAULT_INSTANCE);
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class));
    }

    public void populateHistory() {
        populateStats();

        historicalDataElements.listPointerArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, (long)historicalDataElements.bitsPerListPointer * (historicalDataElements.maxOrdinal + 1));
        historicalDataElements.elementArray = new FixedLengthElementArray(historicalDataElements.memoryRecycler, (long)historicalDataElements.bitsPerElement * historicalDataElements.totalNumberOfElements);

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

    public HollowListTypeReadState createHistoricalTypeReadState() {
        HollowListTypeReadState historicalTypeState = new HollowListTypeReadState(null, typeState.getSchema());
        historicalTypeState.setCurrentData(historicalDataElements);
        return historicalTypeState;
    }

    private void populateStats() {
        iter.reset();
        int removedEntryCount = 0;
        long totalElementCount = 0;
        int ordinal = iter.next();
        while(ordinal != -1) {
            removedEntryCount++;
            totalElementCount += typeState.size(ordinal);
            ordinal = iter.next();
        }

        historicalDataElements.maxOrdinal = removedEntryCount - 1;
        historicalDataElements.totalNumberOfElements = totalElementCount;
        historicalDataElements.bitsPerListPointer = totalElementCount == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalElementCount);
        historicalDataElements.bitsPerElement = stateEngineDataElements.bitsPerElement;

        ordinalMapping = new IntMap(removedEntryCount);
    }

    private void copyRecord(int ordinal) {
        long bitsPerElement = stateEngineDataElements.bitsPerElement;
        long fromStartElement = ordinal == 0 ? 0 : stateEngineDataElements.listPointerArray.getElementValue((long)(ordinal - 1) * stateEngineDataElements.bitsPerListPointer, stateEngineDataElements.bitsPerListPointer);
        long fromEndElement = stateEngineDataElements.listPointerArray.getElementValue((long)ordinal * stateEngineDataElements.bitsPerListPointer, stateEngineDataElements.bitsPerListPointer);
        long size = fromEndElement - fromStartElement;

        historicalDataElements.elementArray.copyBits(stateEngineDataElements.elementArray, bitsPerElement * fromStartElement, bitsPerElement * nextStartElement, size * bitsPerElement);
        historicalDataElements.listPointerArray.setElementValue(historicalDataElements.bitsPerListPointer * nextOrdinal, historicalDataElements.bitsPerListPointer, nextStartElement + size);

        ordinalMapping.put(ordinal, nextOrdinal);
        nextOrdinal++;
        nextStartElement += size;
    }

}
