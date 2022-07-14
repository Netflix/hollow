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

/**
 * Obtained via a {@link HollowTypeDiff}, this is a report of the differences in a specific field between two data states. 
 * 
 */
public class HollowFieldDiff implements Comparable<HollowFieldDiff> {
    private final HollowDiffNodeIdentifier fieldIdentifier;
    private final IntList diffFromOrdinals;
    private final IntList diffToOrdinals;
    private final IntList diffPairScores;

    private long totalDiffScore;

    public HollowFieldDiff(HollowDiffNodeIdentifier fieldIdentifier) {
        this.diffFromOrdinals = new IntList();
        this.diffToOrdinals = new IntList();
        this.diffPairScores = new IntList();
        this.fieldIdentifier = fieldIdentifier;
    }

    /**
     * Should be called exclusively from the {@link HollowDiff} -- not intended for external consumption
     *
     * @param fromOrdinal the from ordinal
     * @param toOrdinal the to ordinal
     * @param score the score
     */
    public void addDiff(int fromOrdinal, int toOrdinal, int score) {
        if(isSameDiffAsLastAdd(fromOrdinal, toOrdinal)) {
            int scoreIdx = diffPairScores.size() - 1;
            diffPairScores.set(scoreIdx, diffPairScores.get(scoreIdx) + score);
        } else {
            diffFromOrdinals.add(fromOrdinal);
            diffToOrdinals.add(toOrdinal);
            diffPairScores.add(score);
        }

        totalDiffScore += score;
    }

    private boolean isSameDiffAsLastAdd(int fromOrdinal, int toOrdinal) {
        return diffFromOrdinals.size() > 0
                && diffFromOrdinals.get(diffFromOrdinals.size() - 1) == fromOrdinal
                && diffToOrdinals.get(diffToOrdinals.size() - 1) == toOrdinal;
    }

    /**
     * @return The identifier for the field on which this diff reports.
     */
    public HollowDiffNodeIdentifier getFieldIdentifier() {
        return fieldIdentifier;
    }

    /**
     * @return the total score, used to judge relative magnitude of the diff. 
     */
    public long getTotalDiffScore() {
        return totalDiffScore;
    }

    /**
     * @return the number of records which had at least one diff for this field.
     */
    public int getNumDiffs() {
        return diffToOrdinals.size();
    }

    /**
     * @param diffPairIdx a number from 0-n, where n is the value returned from numDiffs
     * @return the from ordinal for the (diffPairIdx)th record pair in which there were differences for this field.
     */
    public int getFromOrdinal(int diffPairIdx) {
        return diffFromOrdinals.get(diffPairIdx);
    }

    /**
     * @param diffPairIdx a number from 0-n, where n is the value returned from numDiffs
     * @return the to ordinal for the (diffPairIdx)th record pair in which there were differences for this field.
     */
    public int getToOrdinal(int diffPairIdx) {
        return diffToOrdinals.get(diffPairIdx);
    }

    /**
     * @param diffPairIdx a number from 0-n, where n is the value returned from numDiffs
     * @return the score of the diff for this field in the (diffPairIdx)th record pair in which there were differences for this field.
     */
    public int getPairScore(int diffPairIdx) {
        return diffPairScores.get(diffPairIdx);
    }

    /**
     * This should be called exclusively from the {@link HollowDiff}.  Not for external consumption.
     * @param otherFieldDiff the field diff to add
     */
    public void addResults(HollowFieldDiff otherFieldDiff) {
        for(int i = 0; i < otherFieldDiff.getNumDiffs(); i++) {
            addDiff(otherFieldDiff.getFromOrdinal(i), otherFieldDiff.getToOrdinal(i), otherFieldDiff.getPairScore(i));
        }
    }

    /**
     * Comparison is based on the totalDiffScore().
     */
    @Override
    public int compareTo(HollowFieldDiff o) {
        if(o.getTotalDiffScore() > totalDiffScore)
            return 1;
        else if(o.getTotalDiffScore() < totalDiffScore)
            return -1;
        return 0;
    }

}
