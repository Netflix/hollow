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

    private final HollowObjectTypeReadState typeState;
    private final HollowObjectTypeDataElements stateEngineDataElements[];
    private final HollowObjectTypeDataElements historicalDataElements;
    private  RemovedOrdinalIterator iter;
    
    private final int shardNumberMask;
    private final int shardOrdinalShift;

    private IntMap ordinalMapping;
    private int nextOrdinal;
    private final long currentWriteVarLengthDataPointers[];

    public HollowObjectDeltaHistoricalStateCreator(HollowObjectTypeReadState typeState) {
        this.typeState = typeState;
        this.stateEngineDataElements = typeState.currentDataElements();
        this.historicalDataElements = new HollowObjectTypeDataElements(typeState.getSchema(), WastefulRecycler.DEFAULT_INSTANCE);
        // SNAP: ??? do I or not, need to create ReverseDeltaHistoricalTypeState similarly, with added ordinal iterator instead of removed
        this.iter = new RemovedOrdinalIterator(typeState.getListener(PopulatedOrdinalListener.class));
        this.currentWriteVarLengthDataPointers = new long[typeState.getSchema().numFields()];
        this.shardNumberMask = stateEngineDataElements.length - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(stateEngineDataElements.length);
    }

    public void reverse(){
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);

        iter = new RemovedOrdinalIterator(listener.getPopulatedOrdinals(), listener.getPreviousOrdinals());
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
            copyRecord(ordinal);

            ordinal = iter.next();
        }
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
                    totalVarLengthSizes[i] += varLengthSize(ordinal, i);
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

            historicalDataElements.nullValueForField[i] = (1L << historicalDataElements.bitsPerField[i]) - 1;
            historicalDataElements.bitOffsetPerField[i] = historicalDataElements.bitsPerRecord;
            historicalDataElements.bitsPerRecord += historicalDataElements.bitsPerField[i];
        }

        ordinalMapping = new IntMap(removedEntryCount);
    }

    private long varLengthSize(int ordinal, int fieldIdx) {
        int shard = ordinal & shardNumberMask;
        int shardOrdinal = ordinal >> shardOrdinalShift; 
        
        int numBitsForField = stateEngineDataElements[shard].bitsPerField[fieldIdx];
        long currentBitOffset = ((long)stateEngineDataElements[shard].bitsPerRecord * shardOrdinal) + stateEngineDataElements[shard].bitOffsetPerField[fieldIdx];
        long endByte = stateEngineDataElements[shard].fixedLengthData.getElementValue(currentBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;
        long startByte = shardOrdinal != 0 ? stateEngineDataElements[shard].fixedLengthData.getElementValue(currentBitOffset - stateEngineDataElements[shard].bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1 : 0;

        return endByte - startByte;
    }

    private void copyRecord(int ordinal) {
        int shard = ordinal & shardNumberMask;
        int shardOrdinal = ordinal >> shardOrdinalShift; 

        for(int i=0;i<historicalDataElements.schema.numFields();i++) {
            if(historicalDataElements.varLengthData[i] == null) {
                long value = stateEngineDataElements[shard].fixedLengthData.getLargeElementValue(((long)shardOrdinal * stateEngineDataElements[shard].bitsPerRecord) + stateEngineDataElements[shard].bitOffsetPerField[i], stateEngineDataElements[shard].bitsPerField[i]);
                historicalDataElements.fixedLengthData.setElementValue(((long)nextOrdinal * historicalDataElements.bitsPerRecord) + historicalDataElements.bitOffsetPerField[i], historicalDataElements.bitsPerField[i], value);
            } else {
                long fromStartByte = varLengthStartByte(shard, shardOrdinal, i);
                long fromEndByte = varLengthEndByte(shard, shardOrdinal, i);
                long size = fromEndByte - fromStartByte;

                historicalDataElements.fixedLengthData.setElementValue(((long)nextOrdinal * historicalDataElements.bitsPerRecord) + historicalDataElements.bitOffsetPerField[i], historicalDataElements.bitsPerField[i], currentWriteVarLengthDataPointers[i] + size);
                historicalDataElements.varLengthData[i].copy(stateEngineDataElements[shard].varLengthData[i], fromStartByte, currentWriteVarLengthDataPointers[i], size);

                currentWriteVarLengthDataPointers[i] += size;
            }
        }

        nextOrdinal++;
    }

    private long varLengthStartByte(int shard, int translatedOrdinal, int fieldIdx) {
        if(translatedOrdinal == 0)
            return 0;

        int numBitsForField = stateEngineDataElements[shard].bitsPerField[fieldIdx];
        long currentBitOffset = ((long)stateEngineDataElements[shard].bitsPerRecord * translatedOrdinal) + stateEngineDataElements[shard].bitOffsetPerField[fieldIdx];
        long startByte = stateEngineDataElements[shard].fixedLengthData.getElementValue(currentBitOffset - stateEngineDataElements[shard].bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return startByte;
    }

    private long varLengthEndByte(int shard, int translatedOrdinal, int fieldIdx) {
        int numBitsForField = stateEngineDataElements[shard].bitsPerField[fieldIdx];
        long currentBitOffset = ((long)stateEngineDataElements[shard].bitsPerRecord * translatedOrdinal) + stateEngineDataElements[shard].bitOffsetPerField[fieldIdx];
        long endByte = stateEngineDataElements[shard].fixedLengthData.getElementValue(currentBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return endByte;
    }

}
