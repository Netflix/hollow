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

import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.model.HollowDiffUIBreadcrumbs;
import com.netflix.hollow.diff.ui.model.HollowObjectPairDiffScore;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class DiffFieldPage extends DiffPage {

    public DiffFieldPage(HollowDiffUI diffUI) {
        super(diffUI, "diff-field.vm");
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String typeName = req.getParameter("type");
        HollowTypeDiff typeDiff = getTypeDiff(typeName);
        int fieldIdx = Integer.parseInt(req.getParameter("fieldIdx"));
        HollowFieldDiff fieldDiff = typeDiff.getFieldDiffs().get(fieldIdx);

        String sessionPageCtx = typeName + ":" + fieldIdx;

        int diffPairBeginIdx = intParam(req, session, sessionPageCtx, "diffPairBeginIdx", 0);
        int diffPairPageSize = intParam(req, session, sessionPageCtx, "diffPairPageSize", 25);

        List<HollowObjectPairDiffScore> pairs = getObjectDiffScores(typeDiff, fieldDiff, diffPairBeginIdx, diffPairPageSize);

        ctx.put("objectScorePairs", pairs);
        ctx.put("typeDiff", typeDiff);
        ctx.put("fieldDiff", fieldDiff);
        ctx.put("fieldIdx", fieldIdx);

        if(diffPairBeginIdx > 0)
            ctx.put("previousDiffPairPageBeginIdx", diffPairBeginIdx - diffPairPageSize);
        if((diffPairBeginIdx + diffPairPageSize) < fieldDiff.getNumDiffs())
            ctx.put("nextDiffPairPageBeginIdx", diffPairBeginIdx + diffPairPageSize);

        ctx.put("breadcrumbs", getBreadcrumbs(typeDiff, fieldDiff));
    }

    private List<HollowObjectPairDiffScore> getObjectDiffScores(HollowTypeDiff typeDiff, HollowFieldDiff fieldDiff, int beginRecord, int pageSize) {
        List<HollowObjectPairDiffScore> list = new ArrayList<HollowObjectPairDiffScore>();

        for(int i=0;i<fieldDiff.getNumDiffs();i++) {
            int fromOrdinal = fieldDiff.getFromOrdinal(i);
            int toOrdinal = fieldDiff.getToOrdinal(i);
            String displayKey = typeDiff.getMatcher().getKeyDisplayString(typeDiff.getFromTypeState(), fieldDiff.getFromOrdinal(i));
            list.add(new HollowObjectPairDiffScore(displayKey, fromOrdinal, toOrdinal, fieldDiff.getPairScore(i)));
        }

        Collections.sort(list);

        return list.subList(beginRecord, beginRecord + pageSize > list.size() ? list.size() : beginRecord + pageSize);
    }

    private List<HollowDiffUIBreadcrumbs> getBreadcrumbs(HollowTypeDiff typeDiff, HollowFieldDiff fieldDiff) {
        List<HollowDiffUIBreadcrumbs> breadcrumbs = new ArrayList<HollowDiffUIBreadcrumbs>();

        breadcrumbs.add(new HollowDiffUIBreadcrumbs((diffUI.getDiffUIPath() == null || diffUI.getDiffUIPath().length() == 0) ?
                "/" : diffUI.getDiffUIPath(), "Overview"));
        breadcrumbs.add(new HollowDiffUIBreadcrumbs((diffUI.getDiffUIPath() == null || diffUI.getDiffUIPath().length() == 0) ?
                "typediff?type=" + typeDiff.getTypeName() : diffUI.getDiffUIPath() + "/typediff?type=" + typeDiff.getTypeName(), typeDiff.getTypeName()));
        breadcrumbs.add(new HollowDiffUIBreadcrumbs(null, fieldDiff.getFieldIdentifier().toString()));

        return breadcrumbs;
    }
}
