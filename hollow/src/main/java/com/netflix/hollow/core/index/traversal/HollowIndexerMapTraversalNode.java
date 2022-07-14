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

import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.util.IntList;

/**
 * Not intended for external consumption.
 */
class HollowIndexerMapTraversalNode extends HollowIndexerTraversalNode {

    private HollowIndexerTraversalNode keyNode;
    private HollowIndexerTraversalNode valueNode;

    public HollowIndexerMapTraversalNode(HollowTypeDataAccess dataAccess, IntList[] fieldMatches) {
        super(dataAccess, fieldMatches);
    }

    @Override
    protected void setUpChildren() {
        keyNode = children.get("key");
        valueNode = children.get("value");
    }

    @Override
    public int doTraversal(int ordinal) {
        int numMatches = 0;

        HollowMapEntryOrdinalIterator ordinalIterator = dataAccess().ordinalIterator(ordinal);

        while(ordinalIterator.next()) {
            prepareMultiply();

            if(keyNode != null)
                keyNode.traverse(ordinalIterator.getKey());
            if(valueNode != null)
                valueNode.traverse(ordinalIterator.getValue());

            numMatches += doMultiply();
        }

        return numMatches;
    }

    @Override
    protected HollowMapTypeDataAccess dataAccess() {
        return (HollowMapTypeDataAccess) dataAccess;
    }

    @Override
    protected boolean followingChildrenMultipliesTraversal() {
        return true;
    }

}
