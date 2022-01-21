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
package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.IntMap.IntMapEntryIterator;
import com.netflix.hollow.tools.combine.OrdinalRemapper;

public class HollowHistoricalStateTypeKeyOrdinalMapping {

    private final String typeName;
    private final HollowHistoryTypeKeyIndex keyIndex;

    private IntMap addedOrdinalMap;
    private IntMap removedOrdinalMap;

    private int numberOfNewRecords;
    private int numberOfRemovedRecords;
    private int numberOfModifiedRecords;

    public HollowHistoricalStateTypeKeyOrdinalMapping(String typeName, HollowHistoryTypeKeyIndex keyIndex) {
        this.typeName = typeName;
        this.keyIndex = keyIndex;
    }

    private HollowHistoricalStateTypeKeyOrdinalMapping(String typeName, HollowHistoryTypeKeyIndex keyIndex, IntMap addedOrdinalMap, IntMap removedOrdinalMap) {
        this.typeName = typeName;
        this.keyIndex = keyIndex;
        this.addedOrdinalMap = addedOrdinalMap;
        this.removedOrdinalMap = removedOrdinalMap;
        finish();
    }

    public void prepare(int numAdditions, int numRemovals) {
        this.addedOrdinalMap = new IntMap(numAdditions);
        this.removedOrdinalMap = new IntMap(numRemovals);
    }
    public void added(HollowTypeReadState typeState, int ordinal) {
        int recordKeyOrdinal = keyIndex.findKeyIndexOrdinal((HollowObjectTypeReadState)typeState, ordinal);
        addedOrdinalMap.put(recordKeyOrdinal, ordinal);
    }

    public void removed(HollowTypeReadState typeState, int ordinal) {
        removed(typeState, ordinal, ordinal);
    }

    public void removed(HollowTypeReadState typeState, int stateEngineOrdinal, int mappedOrdinal) {
        int recordKeyOrdinal = keyIndex.findKeyIndexOrdinal((HollowObjectTypeReadState)typeState, stateEngineOrdinal);
        removedOrdinalMap.put(recordKeyOrdinal, mappedOrdinal);
    }

    public void addedReverse(HollowTypeReadState typeState, int ordinal) {
        int recordKeyOrdinal = keyIndex.findKeyIndexOrdinalReverse((HollowObjectTypeReadState)typeState, ordinal);
        addedOrdinalMap.put(recordKeyOrdinal, ordinal);
    }

    public void removedReverse(HollowTypeReadState typeState, int ordinal) {
        removedReverse(typeState, ordinal, ordinal);
    }

    public void removedReverse(HollowTypeReadState typeState, int stateEngineOrdinal, int mappedOrdinal) {
        int recordKeyOrdinal = keyIndex.findKeyIndexOrdinalReverse((HollowObjectTypeReadState)typeState, stateEngineOrdinal);
        removedOrdinalMap.put(recordKeyOrdinal, mappedOrdinal);
    }

    public HollowHistoricalStateTypeKeyOrdinalMapping remap(OrdinalRemapper remapper) {
        IntMap newAddedOrdinalMap = new IntMap(addedOrdinalMap.size());
        IntMapEntryIterator addedIter = addedOrdinalMap.iterator();
        while(addedIter.next())
            newAddedOrdinalMap.put(addedIter.getKey(), remapper.getMappedOrdinal(typeName, addedIter.getValue()));

        IntMap newRemovedOrdinalMap = new IntMap(removedOrdinalMap.size());
        IntMapEntryIterator removedIter = removedOrdinalMap.iterator();
        while(removedIter.next())
            newRemovedOrdinalMap.put(removedIter.getKey(), remapper.getMappedOrdinal(typeName, removedIter.getValue()));

        return new HollowHistoricalStateTypeKeyOrdinalMapping(typeName, keyIndex, newAddedOrdinalMap, newRemovedOrdinalMap);
    }

    public void finish() {
        IntMapEntryIterator iter = addedOrdinalMap.iterator();

        while(iter.next()) {
            if(removedOrdinalMap.get(iter.getKey()) != -1)
                numberOfModifiedRecords++;
        }

        numberOfNewRecords = addedOrdinalMap.size() - numberOfModifiedRecords;
        numberOfRemovedRecords = removedOrdinalMap.size() - numberOfModifiedRecords;
    }

    public IntMapEntryIterator removedOrdinalMappingIterator() {
        return removedOrdinalMap.iterator();
    }

    public IntMapEntryIterator addedOrdinalMappingIterator() {
        return addedOrdinalMap.iterator();
    }

    public int findRemovedOrdinal(int keyOrdinal) {
        return removedOrdinalMap.get(keyOrdinal);
    }

    public int findAddedOrdinal(int keyOrdinal) {
        return addedOrdinalMap.get(keyOrdinal);
    }

    public HollowHistoryTypeKeyIndex getKeyIndex() {
        return keyIndex;
    }

    public int getNumberOfNewRecords() {
        return numberOfNewRecords;
    }

    public int getNumberOfRemovedRecords() {
        return numberOfRemovedRecords;
    }

    public int getNumberOfModifiedRecords() {
        return numberOfModifiedRecords;
    }

}
