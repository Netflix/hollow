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
package com.netflix.hollow.tools.diff.count;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Counting nodes are used by the HollowDiff to count and aggregate changes for specific record types in a data model.
 * 
 * This type of counting node is applicable to map types.
 * 
 * Not intended for external consumption.
 */
public class HollowDiffMapCountingNode extends HollowDiffCountingNode {

    private final HollowMapTypeReadState fromState;
    private final HollowMapTypeReadState toState;
    private final HollowDiffCountingNode keyNode;
    private final HollowDiffCountingNode valueNode;

    private final DiffEqualOrdinalFilter keyFilter;
    private final DiffEqualOrdinalFilter valueFilter;
    private final boolean keyRequiresTraversalForMissingFields;
    private final boolean valueRequiresTraversalForMissingFields;

    public HollowDiffMapCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId, HollowMapTypeReadState fromState, HollowMapTypeReadState toState) {
        super(diff, topLevelTypeDiff, nodeId);
        this.fromState = fromState;
        this.toState = toState;

        HollowTypeReadState keyFromState = fromState == null ? null : fromState.getSchema().getKeyTypeState();
        HollowTypeReadState keyToState = toState == null ? null : toState.getSchema().getKeyTypeState();
        this.keyNode = getHollowDiffCountingNode(keyFromState, keyToState, "key");

        HollowTypeReadState valueFromState = fromState == null ? null : fromState.getSchema().getValueTypeState();
        HollowTypeReadState valueToState = toState == null ? null : toState.getSchema().getValueTypeState();
        this.valueNode = getHollowDiffCountingNode(valueFromState, valueToState, "value");

        String keyType = fromState != null ? fromState.getSchema().getKeyType() : toState.getSchema().getKeyType();
        String valueType = fromState != null ? fromState.getSchema().getValueType() : toState.getSchema().getValueType();

        this.keyFilter = new DiffEqualOrdinalFilter(equalityMapping.getEqualOrdinalMap(keyType));
        this.valueFilter = new DiffEqualOrdinalFilter(equalityMapping.getEqualOrdinalMap(valueType));
        this.keyRequiresTraversalForMissingFields = equalityMapping.requiresMissingFieldTraversal(keyType);
        this.valueRequiresTraversalForMissingFields = equalityMapping.requiresMissingFieldTraversal(valueType);
    }

    @Override
    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        keyNode.prepare(topLevelFromOrdinal, topLevelToOrdinal);
        valueNode.prepare(topLevelFromOrdinal, topLevelToOrdinal);
    }

    private final IntList traversalFromKeyOrdinals = new IntList();
    private final IntList traversalToKeyOrdinals = new IntList();
    private final IntList traversalFromValueOrdinals = new IntList();
    private final IntList traversalToValueOrdinals = new IntList();

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        fillTraversalLists(fromOrdinals, toOrdinals);

        keyFilter.filter(traversalFromKeyOrdinals, traversalToKeyOrdinals);
        valueFilter.filter(traversalFromValueOrdinals, traversalToValueOrdinals);

        int score = 0;

        if(keyFilter.getUnmatchedFromOrdinals().size() != 0 || keyFilter.getUnmatchedToOrdinals().size() != 0)
            score += keyNode.traverseDiffs(keyFilter.getUnmatchedFromOrdinals(), keyFilter.getUnmatchedToOrdinals());
        if(keyRequiresTraversalForMissingFields)
            if(keyFilter.getMatchedFromOrdinals().size() != 0 || keyFilter.getMatchedToOrdinals().size() != 0)
                score += keyNode.traverseMissingFields(keyFilter.getMatchedFromOrdinals(), keyFilter.getMatchedToOrdinals());

        if(valueFilter.getUnmatchedFromOrdinals().size() != 0 || valueFilter.getUnmatchedToOrdinals().size() != 0)
            score += valueNode.traverseDiffs(valueFilter.getUnmatchedFromOrdinals(), valueFilter.getUnmatchedToOrdinals());
        if(valueRequiresTraversalForMissingFields)
            if(valueFilter.getMatchedFromOrdinals().size() != 0 || valueFilter.getMatchedToOrdinals().size() != 0)
                score += valueNode.traverseMissingFields(valueFilter.getMatchedFromOrdinals(), valueFilter.getMatchedToOrdinals());

        return score;
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        fillTraversalLists(fromOrdinals, toOrdinals);

        int score = 0;

        score += keyNode.traverseMissingFields(traversalFromKeyOrdinals, traversalToKeyOrdinals);
        score += valueNode.traverseMissingFields(traversalFromValueOrdinals, traversalToValueOrdinals);

        return score;
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        List<HollowFieldDiff> list = new ArrayList<HollowFieldDiff>();

        list.addAll(keyNode.getFieldDiffs());
        list.addAll(valueNode.getFieldDiffs());

        return list;
    }


    private void fillTraversalLists(IntList fromOrdinals, IntList toOrdinals) {
        traversalFromKeyOrdinals.clear();
        traversalToKeyOrdinals.clear();
        traversalFromValueOrdinals.clear();
        traversalToValueOrdinals.clear();

        if(fromState != null) {
            for(int i = 0; i < fromOrdinals.size(); i++) {
                fillListsWithReferencedOrdinals(fromState, fromOrdinals.get(i), traversalFromKeyOrdinals, traversalFromValueOrdinals);
            }
        }

        if(toState != null) {
            for(int i = 0; i < toOrdinals.size(); i++) {
                fillListsWithReferencedOrdinals(toState, toOrdinals.get(i), traversalToKeyOrdinals, traversalToValueOrdinals);
            }
        }
    }

    private void fillListsWithReferencedOrdinals(HollowMapTypeReadState typeState, int ordinal, IntList fillKeyList, IntList fillValueList) {
        HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(ordinal);
        while(iter.next()) {
            fillKeyList.add(iter.getKey());
            fillValueList.add(iter.getValue());
        }
    }

}
