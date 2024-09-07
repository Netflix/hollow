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
package com.netflix.hollow.diff.ui.pages;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.model.HollowDiffOverviewTypeEntry;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class DiffOverviewPage extends DiffPage {

    public DiffOverviewPage(HollowDiffUI diffUI) {
        super(diffUI, "diff-overview.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String sortBy = param(req, session, "overview", "sortBy", "diffs");

        ctx.put("typeOverviewEntries", getTypeEntries(sortBy));
    }

    private List<HollowDiffOverviewTypeEntry> getTypeEntries(String sortBy) {

        List<HollowDiffOverviewTypeEntry> overviewEntries = new ArrayList<>();

        for(HollowTypeDiff diff : getDiff().getTypeDiffs()) {
            long totalDiffScore = diff.getTotalDiffScore();
            int unmatchedInFrom = diff.getUnmatchedOrdinalsInFrom().size();
            int unmatchedInTo = diff.getUnmatchedOrdinalsInTo().size();

            int fromCount = 0;
            try {
                fromCount = diff.getTotalItemsInFromState();
            } catch (Exception ex) {
                System.out.println("DIFF_ERROR: Unable to getTotalItemsInFromState for type=" + diff.getTypeName());
                ex.printStackTrace();
            }
            int toCount = 0;
            try {
                toCount = diff.getTotalItemsInToState();
            } catch (Exception ex) {
                System.out.println("DIFF_ERROR: Unable to getTotalItemsInToState for type=" + diff.getTypeName());
                ex.printStackTrace();
            }
            HollowTypeReadState fromTypeState = diff.getFromTypeState();
            HollowTypeReadState toTypeState = diff.getToTypeState();

            overviewEntries.add(new HollowDiffOverviewTypeEntry(diff.getTypeName(), diff.hasMatchPaths(), totalDiffScore, unmatchedInFrom, unmatchedInTo, fromCount, toCount,
                    fromTypeState==null ? 0:fromTypeState.getApproximateHeapFootprintInBytes(), toTypeState==null ? 0:toTypeState.getApproximateHeapFootprintInBytes(),
                    fromTypeState==null ? 0:fromTypeState.getApproximateHoleCostInBytes(), toTypeState==null ? 0:toTypeState.getApproximateHoleCostInBytes()));
        }

        if(sortBy == null || "diffs".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> {
                int result = Comparator
                        .comparing(HollowDiffOverviewTypeEntry::getTotalDiffScore)
                        .thenComparing(HollowDiffOverviewTypeEntry::getDeltaSize)
                        .thenComparing(HollowDiffOverviewTypeEntry::hasData)
                        .thenComparing(HollowDiffOverviewTypeEntry::hasUnmatched)
                        .thenComparing(HollowDiffOverviewTypeEntry::hasUniqueKey)
                        .compare(o2, o1);

                // Fallback to Type Name Ordering
                if (result==0) {
                    return o1.getTypeName().compareTo(o2.getTypeName());
                }

                return result;
            });
        } else if("unmatchedFrom".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> o2.getUnmatchedInFrom() - o1.getUnmatchedInFrom());
        } else if("unmatchedTo".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> o2.getUnmatchedInTo() - o1.getUnmatchedInTo());
        } else if("fromCount".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> o2.getTotalInFrom() - o1.getTotalInFrom());
        } else if("toCount".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> o2.getTotalInTo() - o1.getTotalInTo());
        } else if("fromHeap".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> (int) (o2.getHeapInFrom() - o1.getHeapInFrom()));
        } else if("toHeap".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> (int) (o2.getHeapInTo() - o1.getHeapInTo()));
        } else if("fromHole".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> (int) (o2.getHoleInFrom() - o1.getHoleInFrom()));
        } else if("toHole".equals(sortBy)) {
            overviewEntries.sort((o1, o2) -> (int) (o2.getHoleInTo() - o1.getHoleInTo()));
        }

        return overviewEntries;
    }
}