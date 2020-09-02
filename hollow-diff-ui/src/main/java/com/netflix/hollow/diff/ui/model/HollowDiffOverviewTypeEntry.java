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

import static com.netflix.hollow.HollowDiffUtil.formatBytes;

public class HollowDiffOverviewTypeEntry {

    private final String typeName;
    private final long totalDiffScore;
    private final int unmatchedInFrom;
    private final int unmatchedInTo;
    private final int totalInFrom;
    private final int totalInTo;
    private final long heapInFrom;
    private final long heapInTo;
    private final long holeInFrom;
    private final long holeInTo;

    public HollowDiffOverviewTypeEntry(String typeName, long totalDiffScore, int unmatchedInFrom, int unmatchedInTo, int totalInFrom, int totalInTo) {
        this(typeName, totalDiffScore, unmatchedInFrom, unmatchedInTo, totalInFrom, totalInTo, 0, 0, 0, 0);
    }

    public HollowDiffOverviewTypeEntry(String typeName, long totalDiffScore, int unmatchedInFrom, int unmatchedInTo, int totalInFrom, int totalInTo,
                                       long heapInFrom, long heapInTo, long holeInFrom, long holeInTo) {
        this.typeName = typeName;
        this.totalDiffScore = totalDiffScore;
        this.unmatchedInFrom = unmatchedInFrom;
        this.unmatchedInTo = unmatchedInTo;
        this.totalInFrom = totalInFrom;
        this.totalInTo = totalInTo;
        this.heapInFrom = heapInFrom;
        this.heapInTo = heapInTo;
        this.holeInFrom = holeInFrom;
        this.holeInTo = holeInTo;
    }

    public String getTypeName() {
        return typeName;
    }
    public long getTotalDiffScore() {
        return totalDiffScore;
    }
    public int getUnmatchedInFrom() {
        return unmatchedInFrom;
    }
    public int getUnmatchedInTo() {
        return unmatchedInTo;
    }
    public int getTotalInFrom() {
        return totalInFrom;
    }
    public int getTotalInTo() {
        return totalInTo;
    }
    public long getHeapInFrom() { return heapInFrom; }
    public long getHeapInTo() { return heapInTo; }
    public long getHoleInFrom() { return holeInFrom; }
    public long getHoleInTo() { return holeInTo; }

    public String getHeapInFromFormatted() { return formatBytes(heapInFrom); }
    public String getHeapInToFormatted() { return formatBytes(heapInTo); }
    public String getHoleInFromFormatted() { return formatBytes(holeInFrom); }
    public String getHoleInToFormatted() { return formatBytes(holeInTo); }

    public String getBgColor() {
        if  (totalDiffScore > 0 || unmatchedInFrom > 0 || unmatchedInTo > 0)
            return "#FFCC99";
        else if (totalInFrom == 0 && totalInTo == 0)
            return "#D0D0D0";
        return "";
    }
}
