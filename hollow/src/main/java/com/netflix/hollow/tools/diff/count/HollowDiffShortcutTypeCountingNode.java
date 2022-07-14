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

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import java.util.Collections;
import java.util.List;

public class HollowDiffShortcutTypeCountingNode extends HollowDiffCountingNode {

    private final HollowFieldDiff fieldDiff;

    private int currentTopLevelFromOrdinal;
    private int currentTopLevelToOrdinal;

    public HollowDiffShortcutTypeCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId) {
        super(diff, topLevelTypeDiff, nodeId);

        this.fieldDiff = new HollowFieldDiff(nodeId);
    }

    @Override
    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        this.currentTopLevelFromOrdinal = topLevelFromOrdinal;
        this.currentTopLevelToOrdinal = topLevelToOrdinal;
    }

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        return addResultToFieldDiff(fromOrdinals, toOrdinals);
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        return addResultToFieldDiff(fromOrdinals, toOrdinals);
    }

    private int addResultToFieldDiff(IntList fromOrdinals, IntList toOrdinals) {
        int score = fromOrdinals.size() + toOrdinals.size();

        if(score != 0)
            fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, score);

        return score;
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        if(fieldDiff.getTotalDiffScore() > 0)
            return Collections.singletonList(fieldDiff);
        return Collections.emptyList();
    }

}
