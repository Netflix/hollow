/*
 *
 *  Copyright 2016 Netflix, Inc.
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

public class HollowObjectPairDiffScore implements Comparable<HollowObjectPairDiffScore> {
    private final String displayKey;
    private final int fromOrdinal;
    private final int toOrdinal;
    private int diffScore;

    public HollowObjectPairDiffScore(String displayKey, int fromOrdinal, int toOrdinal) {
        this(displayKey, fromOrdinal, toOrdinal, 0);
    }

    public HollowObjectPairDiffScore(String displayKey, int fromOrdinal, int toOrdinal, int score) {
        this.displayKey = displayKey;
        this.fromOrdinal = fromOrdinal;
        this.toOrdinal = toOrdinal;
        this.diffScore = score;
    }

    public String getDisplayKey() {
        return displayKey;
    }

    public int getFromOrdinal() {
        return fromOrdinal;
    }

    public int getToOrdinal() {
        return toOrdinal;
    }

    public int getDiffScore() {
        return diffScore;
    }

    public void incrementDiffScore(int incrementBy) {
        diffScore += incrementBy;
    }

    @Override
    public int compareTo(HollowObjectPairDiffScore o) {
        return o.getDiffScore() - diffScore;
    }
}
