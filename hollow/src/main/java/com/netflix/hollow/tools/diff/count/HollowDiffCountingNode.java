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
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;
import java.util.List;

/**
 * Counting nodes are used by the HollowDiff to count and aggregate changes for specific record types in a data model.
 * 
 * Not intended for external consumption.
 * 
 */
public abstract class HollowDiffCountingNode {

    protected static final IntList EMPTY_ORDINAL_LIST = new IntList(0);

    private final HollowDiff diff;
    private final HollowTypeDiff topLevelTypeDiff;
    protected final DiffEqualityMapping equalityMapping;
    protected final HollowDiffNodeIdentifier nodeId;


    public HollowDiffCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId) {
        this.diff = diff;
        this.topLevelTypeDiff = topLevelTypeDiff;
        this.equalityMapping = diff.getEqualityMapping();
        this.nodeId = nodeId;
    }

    public abstract void prepare(int topLevelFromOrdinal, int topLevelToOrdinal);

    public abstract int traverseDiffs(IntList fromOrdinals, IntList toOrdinals);

    public abstract int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals);

    public abstract List<HollowFieldDiff> getFieldDiffs();

    protected HollowDiffCountingNode getHollowDiffCountingNode(HollowTypeReadState refFromState, HollowTypeReadState refToState, String viaFieldName) {
        if(refFromState == null && refToState == null)
            return HollowDiffMissingCountingNode.INSTANCE;

        HollowSchema elementSchema = refFromState == null ? refToState.getSchema() : refFromState.getSchema();

        HollowDiffNodeIdentifier childNodeId = new HollowDiffNodeIdentifier(this.nodeId, viaFieldName, elementSchema.getName());

        if(topLevelTypeDiff.isShortcutType(elementSchema.getName()))
            return new HollowDiffShortcutTypeCountingNode(diff, topLevelTypeDiff, childNodeId);

        switch(elementSchema.getSchemaType()) {
            case OBJECT:
                return new HollowDiffObjectCountingNode(diff, topLevelTypeDiff, childNodeId, (HollowObjectTypeReadState) refFromState, (HollowObjectTypeReadState) refToState);
            case LIST:
            case SET:
                return new HollowDiffCollectionCountingNode(diff, topLevelTypeDiff, childNodeId, (HollowCollectionTypeReadState) refFromState, (HollowCollectionTypeReadState) refToState);
            case MAP:
                return new HollowDiffMapCountingNode(diff, topLevelTypeDiff, childNodeId, (HollowMapTypeReadState) refFromState, (HollowMapTypeReadState) refToState);
        }

        throw new IllegalArgumentException("I don't know how to create a HollowDiffCountingNode for a " + elementSchema.getSchemaType());
    }

}
