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

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.util.IntList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Not intended for external consumption.
 */
abstract class HollowIndexerTraversalNode {

    protected final HollowTypeDataAccess dataAccess;
    protected final IntList[] fieldMatches;
    protected int indexedFieldPosition = -1;

    protected final Map<String, HollowIndexerTraversalNode> children;

    private boolean shouldMultiplyBranchResults;
    private int childrenRepeatCounts[];
    private int childrenMatchCounts[];
    private int fieldChildMap[];
    private int childFirstFieldMap[];

    private int currentMultiplyFieldMatchListPosition;

    public HollowIndexerTraversalNode(HollowTypeDataAccess dataAccess, IntList[] fieldMatches) {
        this.dataAccess = dataAccess;
        this.fieldMatches = fieldMatches;
        this.children = new HashMap<String, HollowIndexerTraversalNode>();
    }

    public void setIndexedFieldPosition(int indexedFieldPosition) {
        this.indexedFieldPosition = indexedFieldPosition;
    }

    public int getIndexedFieldPosition() {
        return indexedFieldPosition;
    }

    /**
     * @return the transitive child branch field positions
     */
    public IntList setUpMultiplication() {
        this.shouldMultiplyBranchResults = shouldMultiplyBranchResults();

        this.childrenRepeatCounts = new int[children.size()];
        this.childrenMatchCounts = new int[children.size()];
        this.fieldChildMap = new int[fieldMatches.length];
        this.childFirstFieldMap = new int[children.size()];

        Arrays.fill(fieldChildMap, -1);

        IntList branchFieldPositions = new IntList();

        if(indexedFieldPosition != -1)
            branchFieldPositions.add(indexedFieldPosition);

        int childCounter = 0;

        for(Map.Entry<String, HollowIndexerTraversalNode> entry : children.entrySet()) {
            IntList childBranchFieldPositions = entry.getValue().setUpMultiplication();

            this.childFirstFieldMap[childCounter] = childBranchFieldPositions.get(0);

            for(int i = 0; i < childBranchFieldPositions.size(); i++) {
                this.fieldChildMap[childBranchFieldPositions.get(i)] = childCounter;
                branchFieldPositions.add(childBranchFieldPositions.get(i));
            }

            childCounter++;
        }

        return branchFieldPositions;
    }

    public void traverse(int ordinal) {
        if(childFirstFieldMap.length == 0) {
            doTraversal(ordinal);
            if(indexedFieldPosition != -1)
                fieldMatches[indexedFieldPosition].add(ordinal);
        } else {
            int childMatchSize = doTraversal(ordinal);

            if(indexedFieldPosition != -1) {
                for(int i = 0; i < childMatchSize; i++)
                    fieldMatches[indexedFieldPosition].add(ordinal);
            }
        }
    }

    public void prepareMultiply() {
        if(childFirstFieldMap.length > 0)
            this.currentMultiplyFieldMatchListPosition = fieldMatches[childFirstFieldMap[0]].size();
    }

    public int doMultiply() {
        if(shouldMultiplyBranchResults) {

            int nextRepeatCount = 1;
            for(int i = 0; i < childrenMatchCounts.length; i++) {
                childrenMatchCounts[i] = fieldMatches[childFirstFieldMap[i]].size() - currentMultiplyFieldMatchListPosition;
                childrenRepeatCounts[i] = nextRepeatCount;
                nextRepeatCount *= childrenMatchCounts[i];
            }

            if(nextRepeatCount == 0) {
                for(int i = 0; i < childrenMatchCounts.length; i++) {
                    fieldMatches[childFirstFieldMap[i]].expandTo(currentMultiplyFieldMatchListPosition);
                }

                return 0;
            }

            int newFieldMatchListPosition = currentMultiplyFieldMatchListPosition + nextRepeatCount;

            for(int i = 0; i < fieldMatches.length; i++) {
                if(fieldChildMap[i] != -1) {
                    fieldMatches[i].expandTo(newFieldMatchListPosition);

                    int currentCopyToIdx = newFieldMatchListPosition - 1;
                    int startCopyFromIdx = currentMultiplyFieldMatchListPosition + childrenMatchCounts[fieldChildMap[i]] - 1;
                    int currentCopyFromIdx = startCopyFromIdx;
                    while(currentCopyToIdx > currentMultiplyFieldMatchListPosition) {
                        for(int j = 0; j < childrenRepeatCounts[fieldChildMap[i]]; j++) {
                            fieldMatches[i].set(currentCopyToIdx, fieldMatches[i].get(currentCopyFromIdx));
                            currentCopyToIdx--;
                        }

                        currentCopyFromIdx--;
                        if(currentCopyFromIdx < currentMultiplyFieldMatchListPosition)
                            currentCopyFromIdx = startCopyFromIdx;
                    }
                }
            }

            return nextRepeatCount;
        }

        if(childFirstFieldMap.length != 0)
            return fieldMatches[childFirstFieldMap[0]].size() - currentMultiplyFieldMatchListPosition;

        return 1;
    }


    public abstract int doTraversal(int ordinal);

    protected abstract HollowTypeDataAccess dataAccess();

    /**
     * Called at the end of creation of the indexer tree.
     *
     * Implementations of this method should set up data structures necessary for fast traversal of children.
     */
    protected abstract void setUpChildren();

    protected abstract boolean followingChildrenMultipliesTraversal();

    HollowIndexerTraversalNode getChild(String name) {
        return children.get(name);
    }

    void addChild(String name, HollowIndexerTraversalNode child) {
        children.put(name, child);
    }

    private boolean shouldMultiplyBranchResults() {
        if(children.size() > 1) {
            for(Map.Entry<String, HollowIndexerTraversalNode> entry : children.entrySet()) {
                if(entry.getValue().branchMayProduceMoreThanOneMatch())
                    return true;
            }
        }

        return false;
    }

    private boolean branchMayProduceMoreThanOneMatch() {
        if(!children.isEmpty() && followingChildrenMultipliesTraversal())
            return true;

        for(Map.Entry<String, HollowIndexerTraversalNode> entry : children.entrySet()) {
            if(entry.getValue().branchMayProduceMoreThanOneMatch())
                return true;
        }

        return false;
    }
}
