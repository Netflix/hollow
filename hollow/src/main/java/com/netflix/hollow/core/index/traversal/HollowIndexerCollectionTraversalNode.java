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

import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.IntList;

/**
 * Not intended for external consumption.
 */
class HollowIndexerCollectionTraversalNode extends HollowIndexerTraversalNode {

    protected HollowIndexerTraversalNode child;

    public HollowIndexerCollectionTraversalNode(HollowTypeDataAccess dataAccess, IntList[] fieldMatches) {
        super(dataAccess, fieldMatches);
    }

    @Override
    protected void setUpChildren() {
        child = children.get("element");
    }

    @Override
    public int doTraversal(int ordinal) {
        if(child == null)
            return 1;

        HollowOrdinalIterator iter = dataAccess().ordinalIterator(ordinal);

        int numMatches = 0;

        int elementOrdinal = iter.next();
        while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            prepareMultiply();

            child.traverse(elementOrdinal);

            numMatches += doMultiply();

            elementOrdinal = iter.next();
        }

        return numMatches;
    }

    @Override
    protected HollowCollectionTypeDataAccess dataAccess() {
        return (HollowCollectionTypeDataAccess) dataAccess;
    }

    @Override
    protected boolean followingChildrenMultipliesTraversal() {
        return true;
    }

}
