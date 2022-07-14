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
package com.netflix.hollow.diff.ui.model;

public class HollowFieldDiffScore implements Comparable<HollowFieldDiffScore> {

    private final String typeName;
    private final int typeFieldIndex;
    private final String displayName;
    private final int numDiffObjects;
    private final int numTotalObjectPairs;
    private final long diffScore;

    public HollowFieldDiffScore(String typeName, int typeFieldIndex, String displayName, int numDiffObjects, int numTotalObjectPairs, long diffScore) {
        this.typeName = typeName;
        this.typeFieldIndex = typeFieldIndex;
        this.displayName = displayName;
        this.numDiffObjects = numDiffObjects;
        this.numTotalObjectPairs = numTotalObjectPairs;
        this.diffScore = diffScore;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeFieldIndex() {
        return typeFieldIndex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getNumDiffObjects() {
        return numDiffObjects;
    }

    public int getNumTotalObjectPairs() {
        return numTotalObjectPairs;
    }

    public long getDiffScore() {
        return diffScore;
    }

    @Override
    public int compareTo(HollowFieldDiffScore o) {
        return o.getNumDiffObjects() - numDiffObjects;
    }
}
