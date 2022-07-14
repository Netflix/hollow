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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import java.util.HashMap;
import java.util.Map;

public class HollowEffigyDiffRecord {

    private final Map<HollowEffigy.Field, FieldDiffCount> map;
    private final SimilarityDifferenceCounter simDiffCount = new SimilarityDifferenceCounter();
    private int totalOriginalFieldCount;
    private int runId;

    public HollowEffigyDiffRecord(HollowEffigy basedOn) {
        this.map = new HashMap<HollowEffigy.Field, FieldDiffCount>();

        traverseOriginalFields(basedOn);
    }

    private void traverseOriginalFields(HollowEffigy effigy) {
        for(Field field : effigy.getFields()) {
            if(field.isLeafNode()) {
                FieldDiffCount fieldCount = map.get(field);
                if(fieldCount == null) {
                    fieldCount = new FieldDiffCount();
                    map.put(field, fieldCount);
                }
                fieldCount.incrementOriginalCount();
                totalOriginalFieldCount++;
            } else {
                traverseOriginalFields((HollowEffigy) field.getValue());
            }
        }
    }

    public int calculateDiff(HollowEffigy comparison, int maxDiff) {
        runId++;
        simDiffCount.reset();
        traverseComparisonFields(comparison, maxDiff);
        if(simDiffCount.diffCount >= maxDiff)
            return HollowEffigyCollectionPairer.MAX_MATRIX_ELEMENT_FIELD_VALUE;
        return score();
    }

    public void traverseComparisonFields(HollowEffigy comparison, int maxDiff) {
        for(Field field : comparison.getFields()) {
            if(field.isLeafNode()) {
                FieldDiffCount fieldCount = map.get(field);
                if(fieldCount == null) {
                    if(simDiffCount.diffCount + 1 >= maxDiff) {
                        simDiffCount.diffCount++;
                        return;
                    }
                    fieldCount = new FieldDiffCount();
                    map.put(field, fieldCount);
                }

                if(fieldCount.incrementComparisonCount(runId)) {
                    if(++simDiffCount.diffCount >= maxDiff)
                        return;
                } else {
                    simDiffCount.simCount++;
                }
            } else {
                traverseComparisonFields((HollowEffigy) field.getValue(), maxDiff);
                if(simDiffCount.diffCount >= maxDiff)
                    return;
            }
        }
    }

    private int score() {
        int totalDiff = (totalOriginalFieldCount - simDiffCount.simCount) + simDiffCount.diffCount;

        if(simDiffCount.simCount == 0 && totalDiff != 0)
            return HollowEffigyCollectionPairer.MAX_MATRIX_ELEMENT_FIELD_VALUE;

        return totalDiff;
    }

    private class FieldDiffCount {
        private int originalCount;
        private int comparisonCount;
        private int lastComparisonUpdatedRunId;

        private void incrementOriginalCount() {
            originalCount++;
        }

        private boolean incrementComparisonCount(int runId) {
            clearComparisonIfRunChanged(runId);
            return ++comparisonCount > originalCount;
        }

        private void clearComparisonIfRunChanged(int runId) {
            if(runId != lastComparisonUpdatedRunId) {
                comparisonCount = 0;
                lastComparisonUpdatedRunId = runId;
            }
        }
    }

    private static class SimilarityDifferenceCounter {
        private int simCount;
        private int diffCount;

        public void reset() {
            simCount = 0;
            diffCount = 0;
        }
    }

}
