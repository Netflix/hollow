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

import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordDiffTreeNode {

    private final String hierarchicalFieldName;
    private final HollowHistoricalState historicalState;
    private final HollowHistoryRecordNamer recordNamer;
    private final String groupName;
    private final Map<Object, RecordDiffTreeNode> childNodes;
    private final List<RecordDiff> recordDiffs;

    public RecordDiffTreeNode(String parentHierarchicalFieldName, Object groupIdentifier, String groupName, HollowHistoricalState historicalState, HollowHistoryRecordNamer recordNamer) {
        this.hierarchicalFieldName = parentHierarchicalFieldName + "." + String.valueOf(groupIdentifier);
        this.historicalState = historicalState;
        this.recordNamer = recordNamer;
        this.groupName = groupName;
        this.childNodes = new HashMap<Object, RecordDiffTreeNode>();
        this.recordDiffs = new ArrayList<RecordDiff>();
    }

    public String getHierarchicalFieldName() {
        return hierarchicalFieldName;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean hasSubGroups() {
        return !childNodes.isEmpty();
    }

    public RecordDiffTreeNode getChildNode(Object value, int keyFieldIdx) {
        RecordDiffTreeNode child = childNodes.get(value);
        if(child == null) {
            child = new RecordDiffTreeNode(hierarchicalFieldName, value, recordNamer.getKeyFieldName(historicalState, value, keyFieldIdx), historicalState, recordNamer);
            childNodes.put(value, child);
        }
        return child;
    }

    public void addRecordDiff(RecordDiff diff) {
        recordDiffs.add(diff);
    }

    public List<RecordDiff> getRecordDiffs() {
        return recordDiffs;
    }

    public boolean isEmpty() {
        return recordDiffs.isEmpty() && childNodes.isEmpty();
    }

    public int getDiffCount() {
        int totalCount = 0;

        for(Map.Entry<Object, RecordDiffTreeNode> entry : childNodes.entrySet()) {
            totalCount += entry.getValue().getDiffCount();
        }

        totalCount += recordDiffs.size();

        return totalCount;
    }

    public Collection<RecordDiffTreeNode> getSubGroups() {
        return childNodes.values();
    }
}
