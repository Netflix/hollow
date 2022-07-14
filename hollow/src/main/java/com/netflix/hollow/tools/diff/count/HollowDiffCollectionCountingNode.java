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

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalFilter;
import java.util.List;

/**
 * Counting nodes are used by the HollowDiff to count and aggregate changes for specific record types in a data model.
 * 
 * This type of counting node is applicable to collection types.
 * 
 * Not intended for external consumption.
 */
public class HollowDiffCollectionCountingNode extends HollowDiffCountingNode {

    private final HollowCollectionTypeReadState fromState;
    private final HollowCollectionTypeReadState toState;
    private final HollowDiffCountingNode elementNode;

    private final DiffEqualOrdinalFilter referenceFilter;
    private final boolean requiresTraversalForMissingFields;

    public HollowDiffCollectionCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId, HollowCollectionTypeReadState fromState, HollowCollectionTypeReadState toState) {
        super(diff, topLevelTypeDiff, nodeId);
        this.fromState = fromState;
        this.toState = toState;
        HollowTypeReadState refFromState = fromState == null ? null : fromState.getSchema().getElementTypeState();
        HollowTypeReadState refToState = toState == null ? null : toState.getSchema().getElementTypeState();

        String referencedType = fromState == null ? toState.getSchema().getElementType() : fromState.getSchema().getElementType();

        this.elementNode = getHollowDiffCountingNode(refFromState, refToState, "element");

        this.referenceFilter = new DiffEqualOrdinalFilter(equalityMapping.getEqualOrdinalMap(referencedType));
        this.requiresTraversalForMissingFields = equalityMapping.requiresMissingFieldTraversal(referencedType);
    }

    @Override
    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        elementNode.prepare(topLevelFromOrdinal, topLevelToOrdinal);
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        return elementNode.getFieldDiffs();
    }

    private final IntList traversalFromOrdinals = new IntList();
    private final IntList traversalToOrdinals = new IntList();

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        fillTraversalLists(fromOrdinals, toOrdinals);

        referenceFilter.filter(traversalFromOrdinals, traversalToOrdinals);

        int score = 0;

        if(referenceFilter.getUnmatchedFromOrdinals().size() != 0 || referenceFilter.getUnmatchedToOrdinals().size() != 0)
            score += elementNode.traverseDiffs(referenceFilter.getUnmatchedFromOrdinals(), referenceFilter.getUnmatchedToOrdinals());
        if(requiresTraversalForMissingFields)
            if(referenceFilter.getMatchedFromOrdinals().size() != 0 || referenceFilter.getMatchedToOrdinals().size() != 0)
                score += elementNode.traverseMissingFields(referenceFilter.getMatchedFromOrdinals(), referenceFilter.getMatchedToOrdinals());

        return score;
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        fillTraversalLists(fromOrdinals, toOrdinals);
        return elementNode.traverseMissingFields(traversalFromOrdinals, traversalToOrdinals);
    }

    private void fillTraversalLists(IntList fromOrdinals, IntList toOrdinals) {
        traversalFromOrdinals.clear();
        traversalToOrdinals.clear();

        if(fromState != null) {
            for(int i = 0; i < fromOrdinals.size(); i++) {
                fillListWithReferencedOrdinals(fromState, fromOrdinals.get(i), traversalFromOrdinals);
            }
        }

        if(toState != null) {
            for(int i = 0; i < toOrdinals.size(); i++) {
                fillListWithReferencedOrdinals(toState, toOrdinals.get(i), traversalToOrdinals);
            }
        }

    }

    private void fillListWithReferencedOrdinals(HollowCollectionTypeReadState typeState, int ordinal, IntList fillList) {
        HollowOrdinalIterator iter = typeState.ordinalIterator(ordinal);
        int refOrdinal = iter.next();
        while(refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            fillList.add(refOrdinal);
            refOrdinal = iter.next();
        }
    }

}
