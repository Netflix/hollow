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
package com.netflix.hollow.history.ui.model;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.util.IntMap.IntMapEntryIterator;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import java.util.Arrays;

public class HistoryStateTypeChanges {

    private final long stateVersion;
    private final String typeName;
    private final String groupedFieldNames[];
    private final RecordDiffTreeNode modifiedRecords;
    private final RecordDiffTreeNode addedRecords;
    private final RecordDiffTreeNode removedRecords;

    public HistoryStateTypeChanges(HollowHistoricalState historicalState, String typeName, HollowHistoryRecordNamer recordNamer, String... groupedFieldNames) {
        this.stateVersion = historicalState.getVersion();
        this.typeName = typeName;
        this.groupedFieldNames = groupedFieldNames;
        this.modifiedRecords = new RecordDiffTreeNode("", "Modified", "Modified", historicalState, recordNamer);
        this.addedRecords = new RecordDiffTreeNode("", "Added", "Added", historicalState, recordNamer);
        this.removedRecords = new RecordDiffTreeNode("", "Removed", "Removed", historicalState, recordNamer);

        HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping = historicalState.getKeyOrdinalMapping().getTypeMappings().get(typeName);
        HollowObjectTypeDataAccess dataAccess = (HollowObjectTypeDataAccess) historicalState.getDataAccess().getTypeDataAccess(typeName);
        int[] groupedFieldIndexes = getGroupedFieldIndexes(groupedFieldNames, typeKeyMapping.getKeyIndex().getKeyFields());

        IntMapEntryIterator removedIter = typeKeyMapping.removedOrdinalMappingIterator();
        IntMapEntryIterator addedIter = typeKeyMapping.addedOrdinalMappingIterator();

        while(removedIter.next()) {
            int fromOrdinal = removedIter.getValue();
            int toOrdinal = typeKeyMapping.findAddedOrdinal(removedIter.getKey());
            if(toOrdinal != -1) {
                addRecordDiff(modifiedRecords, historicalState, typeKeyMapping, recordNamer, dataAccess, removedIter.getKey(), fromOrdinal, toOrdinal, groupedFieldIndexes);
            } else {
                addRecordDiff(removedRecords, historicalState, typeKeyMapping, recordNamer, dataAccess, removedIter.getKey(), fromOrdinal, toOrdinal, groupedFieldIndexes);
            }
        }

        while(addedIter.next()) {
            if(typeKeyMapping.findRemovedOrdinal(addedIter.getKey()) == -1) {
                int toOrdinal = addedIter.getValue();
                addRecordDiff(addedRecords, historicalState, typeKeyMapping, recordNamer, dataAccess, addedIter.getKey(), -1, toOrdinal, groupedFieldIndexes);
            }
        }
    }

    private void addRecordDiff(RecordDiffTreeNode node, HollowHistoricalState historicalState, HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping, HollowHistoryRecordNamer recordNamer, HollowObjectTypeDataAccess dataAccess, int keyOrdinal, int fromOrdinal, int toOrdinal, int[] fieldGroupIndexes) {
        for(int i = 0; i < fieldGroupIndexes.length; i++) {
            node = node.getChildNode(typeKeyMapping.getKeyIndex().getKeyFieldValue(fieldGroupIndexes[i], keyOrdinal), fieldGroupIndexes[i]);
        }
        node.addRecordDiff(new RecordDiff(historicalState, recordNamer, typeKeyMapping, dataAccess, keyOrdinal, fromOrdinal, toOrdinal));
    }

    public long getStateVersion() {
        return stateVersion;
    }

    public String getTypeName() {
        return typeName;
    }

    public String[] getGroupedFieldNames() {
        return groupedFieldNames;
    }

    public RecordDiffTreeNode getModifiedRecords() {
        return modifiedRecords;
    }

    public RecordDiffTreeNode getAddedRecords() {
        return addedRecords;
    }

    public RecordDiffTreeNode getRemovedRecords() {
        return removedRecords;
    }

    public boolean isEmpty() {
        return modifiedRecords.isEmpty() && addedRecords.isEmpty() && removedRecords.isEmpty();
    }

    private int[] getGroupedFieldIndexes(String groupedFieldNames[], String[] keyFields) {
        int[] groupedFieldIndexes = new int[groupedFieldNames.length];
        Arrays.fill(groupedFieldIndexes, -1);
        for(int i = 0; i < groupedFieldNames.length; i++) {
            for(int j = 0; j < keyFields.length; j++) {
                if(groupedFieldNames[i].equals(keyFields[j])) {
                    groupedFieldIndexes[i] = j;
                }
            }
        }

        return groupedFieldIndexes;
    }

    public RecordDiffTreeNode findTreeNode(String hierarchicalFieldName) {
        RecordDiffTreeNode node = findTreeNode(modifiedRecords, hierarchicalFieldName);
        if(node != null)
            return node;
        node = findTreeNode(addedRecords, hierarchicalFieldName);
        if(node != null)
            return node;
        return findTreeNode(removedRecords, hierarchicalFieldName);
    }

    private RecordDiffTreeNode findTreeNode(RecordDiffTreeNode treeNode, String hierarchicalFieldName) {
        if(treeNode.getHierarchicalFieldName().equals(hierarchicalFieldName))
            return treeNode;
        for(RecordDiffTreeNode child : treeNode.getSubGroups()) {
            RecordDiffTreeNode matchedDescendent = findTreeNode(child, hierarchicalFieldName);
            if(matchedDescendent != null)
                return matchedDescendent;
        }
        return null;
    }

}
