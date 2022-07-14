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

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.util.IntList;

/**
 * Not intended for external consumption.
 */
class HollowIndexerListTraversalNode extends HollowIndexerCollectionTraversalNode {

    public HollowIndexerListTraversalNode(HollowListTypeDataAccess dataAccess, IntList[] fieldMatches) {
        super(dataAccess, fieldMatches);
    }

    @Override
    public int doTraversal(int ordinal) {
        if(child == null)
            return 1;

        int size = dataAccess().size(ordinal);

        int numMatches = 0;

        for(int i = 0; i < size; i++) {
            prepareMultiply();

            child.traverse(dataAccess().getElementOrdinal(ordinal, i));

            numMatches += doMultiply();
        }

        return numMatches;
    }

    @Override
    protected HollowListTypeDataAccess dataAccess() {
        return (HollowListTypeDataAccess) dataAccess;
    }

}
