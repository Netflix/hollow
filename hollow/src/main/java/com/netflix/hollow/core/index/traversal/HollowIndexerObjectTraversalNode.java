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
package com.netflix.hollow.core.index.traversal;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.util.IntList;
import java.util.Map;

/**
 * Not intended for external consumption.
 */
class HollowIndexerObjectTraversalNode extends HollowIndexerTraversalNode {

    protected HollowIndexerTraversalNode children[];
    protected int childOrdinalFieldPositions[];

    public HollowIndexerObjectTraversalNode(HollowObjectTypeDataAccess dataAccess, IntList fieldMatches[]) {
        super(dataAccess, fieldMatches);
    }

    @Override
    protected void setUpChildren() {
        children = new HollowIndexerTraversalNode[super.children.size()];
        childOrdinalFieldPositions = new int[children.length];

        int idx = 0;

        for(Map.Entry<String, HollowIndexerTraversalNode> entry : super.children.entrySet()) {
            childOrdinalFieldPositions[idx] = dataAccess().getSchema().getPosition(entry.getKey());
            children[idx] = entry.getValue();
            idx++;
        }
    }

    @Override
    public int doTraversal(int ordinal) {
        prepareMultiply();

        for(int i = 0; i < children.length; i++) {
            if(children[i] instanceof HollowIndexerObjectFieldTraversalNode) {
                children[i].traverse(ordinal);
            } else {
                int childOrdinal = dataAccess().readOrdinal(ordinal, childOrdinalFieldPositions[i]);
                if(childOrdinal != -1)
                    children[i].traverse(childOrdinal);
            }
        }

        return doMultiply();
    }

    @Override
    protected HollowObjectTypeDataAccess dataAccess() {
        return (HollowObjectTypeDataAccess) dataAccess;
    }

    @Override
    protected boolean followingChildrenMultipliesTraversal() {
        return false;
    }

}
