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
package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.copyRecord;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;

import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;

/**
 * This class contains the logic for extracting the removed records from an OBJECT type state
 * to produce a historical type state.
 * 
 * Not intended for external consumption.
 */
public class HollowObjectDeltaHistoricalStateCreator {

    private final HollowObjectTypeDataElements historicalDataElements;

    private final int shardNumberMask;
    private final int shardOrdinalShift;

    private HollowObjectTypeReadState typeState;
    private HollowObjectTypeDataElements stateEngineDataElements[];
    private RemovedOrdinalIterator iter;
    private IntMap ordinalMapping;
    private int nextOrdinal;
    private final long currentWriteVarLengthDataPointers[];

    public HollowObjectDeltaHistoricalStateCreator(HollowObjectTypeReadState typeState, boolean reverse) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowObjectTypeDataElements(typeState.getSchema(), WastefulRecycler.DEFAULT_INSTANCE);
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class), reverse);
        this.currentWriteVarLengthDataPointers = new long[typeState.getSchema().numFields()];
        this.shardNumberMask = stateEngineDataElements.length - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(stateEngineDataElements.length);
    }

    public void populateHistory() {
        populateStats();

        historicalDataElements.fixedLengthData = new FixedLengthElementArray(historicalDataElements.memoryRecycler, (long)historicalDataElements.bitsPerRecord * (historicalDataElements.maxOrdinal + 1));

        for(int i=0;i<historicalDataElements.schema.numFields();i++) {
            if(stateEngineDataElements[0].varLengthData[i] != null) {
                historicalDataElements.varLengthData[i] = new SegmentedByteArray(historicalDataElements.memoryRecycler);
            }
        }

        iter.reset();

        int ordinal = iter.next();
        while(ordinal != ORDINAL_NONE) {
            ordinalMapping.put(ordinal, nextOrdinal);

            int shard = ordinal & shardNumberMask;
            int shardOrdinal = ordinal >> shardOrdinalShift;
            copyRecord(historicalDataElements, nextOrdinal, stateEngineDataElements[shard], shardOrdinal, currentWriteVarLengthDataPointers);
            nextOrdinal++;

            ordinal = iter.next();
        }
    }

    /**
     * Once a historical state has been created, the references into the original read state can be released so that
     * the original read state can be GC'ed.
     */
    public void dereferenceTypeState() {
        this.typeState = null;
        this.stateEngineDataElements = null;
        this.iter = null;
    }

    public IntMap getOrdinalMapping() {
        return ordinalMapping;
    }

    public HollowObjectTypeReadState createHistoricalTypeReadState() {
        HollowObjectTypeReadState historicalTypeState = new HollowObjectTypeReadState(null, typeState.getSchema());
        historicalTypeState.setCurrentData(historicalDataElements);
        return historicalTypeState;
    }

    private void populateStats() {
        iter.reset();
        int removedEntryCount = 0;
        long totalVarLengthSizes[] = new long[stateEngineDataElements[0].varLengthData.length];

        int ordinal = iter.next();

        while(ordinal != ORDINAL_NONE) {
            removedEntryCount++;

            for(int i=0;i<totalVarLengthSizes.length;i++) {
                if(stateEngineDataElements[0].varLengthData[i] != null) {
                    int shard = ordinal & shardNumberMask;
                    int shardOrdinal = ordinal >> shardOrdinalShift;
                    totalVarLengthSizes[i] += varLengthSize(stateEngineDataElements[shard], shardOrdinal, i);
                }
            }

            ordinal = iter.next();
        }

        historicalDataElements.maxOrdinal = removedEntryCount - 1;

        for(int i=0;i<stateEngineDataElements[0].bitsPerField.length;i++) {
            if(stateEngineDataElements[0].varLengthData[i] == null) {
                historicalDataElements.bitsPerField[i] = stateEngineDataElements[0].bitsPerField[i];
            } else {
                historicalDataElements.bitsPerField[i] = (64 - Long.numberOfLeadingZeros(totalVarLengthSizes[i] + 1)) + 1;
            }

            historicalDataElements.nullValueForField[i] = historicalDataElements.bitsPerField[i] == 64 ? -1L : (1L << historicalDataElements.bitsPerField[i]) - 1;
            historicalDataElements.bitOffsetPerField[i] = historicalDataElements.bitsPerRecord;
            historicalDataElements.bitsPerRecord += historicalDataElements.bitsPerField[i];
        }

        ordinalMapping = new IntMap(removedEntryCount);
    }
}
