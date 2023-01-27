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

import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.model.HollowDiffUIBreadcrumbs;
import com.netflix.hollow.diff.ui.model.HollowFieldDiffScore;
import com.netflix.hollow.diff.ui.model.HollowObjectPairDiffScore;
import com.netflix.hollow.diff.ui.model.HollowUnmatchedObject;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import com.netflix.hollow.ui.HollowUISession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

public class DiffTypePage extends DiffPage {

    private final ConcurrentHashMap<String, List<HollowObjectPairDiffScore>> typeObjectPairScores;
    private final ConcurrentHashMap<String, List<HollowUnmatchedObject>> unmatchedFromObjects;
    private final ConcurrentHashMap<String, List<HollowUnmatchedObject>> unmatchedToObjects;

    public DiffTypePage(HollowDiffUI diffUI) {
        super(diffUI, "diff-type.vm");
        this.typeObjectPairScores = new ConcurrentHashMap<String, List<HollowObjectPairDiffScore>>();
        this.unmatchedFromObjects = new ConcurrentHashMap<String, List<HollowUnmatchedObject>>();
        this.unmatchedToObjects = new ConcurrentHashMap<String, List<HollowUnmatchedObject>>();
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String typeName = req.getParameter("type");
        HollowTypeDiff typeDiff = getTypeDiff(typeName);

        int diffPairBeginIdx = intParam(req, session, typeName, "diffPairBeginIdx", 0);
        int diffPairPageSize = intParam(req, session, typeName, "diffPairPageSize", 25);
        int unmatchedFromBeginIdx = intParam(req, session, typeName, "unmatchedFromBeginIdx", 0);
        int unmatchedToBeginIdx = intParam(req, session, typeName, "unmatchedToBeginIdx", 0);
        int unmatchedPageSize = intParam(req, session, typeName, "unmatchedPageSize", 25);
        boolean showFields = boolParam(req, session, typeName, "showFields", true);

        List<HollowObjectPairDiffScore> pairs = lazyGetDiffScorePairs(typeDiff);
        List<HollowUnmatchedObject> unmatchedFrom = lazyGetUnmatchedFromObjects(typeDiff);
        List<HollowUnmatchedObject> unmatchedTo = lazyGetUnmatchedToObjects(typeDiff);

        ctx.put("objectScorePairs", sublist(pairs, diffPairBeginIdx, diffPairPageSize));
        ctx.put("unmatchedFromObjects", sublist(unmatchedFrom, unmatchedFromBeginIdx, unmatchedPageSize));
        ctx.put("unmatchedToObjects", sublist(unmatchedTo, unmatchedToBeginIdx, unmatchedPageSize));

        ctx.put("typeDiff", typeDiff);
        ctx.put("fieldDiffs", getDisplayDiffs(typeDiff));
        ctx.put("numObjectsDiff", pairs.size());

        if(diffPairBeginIdx > 0)
            ctx.put("previousDiffPairPageBeginIdx", diffPairBeginIdx - diffPairPageSize);
        if((diffPairBeginIdx + diffPairPageSize) < pairs.size())
            ctx.put("nextDiffPairPageBeginIdx", diffPairBeginIdx + diffPairPageSize);
        if(unmatchedFromBeginIdx > 0)
            ctx.put("previousUnmatchedFromPageBeginIdx", unmatchedFromBeginIdx - unmatchedPageSize);
        if((unmatchedFromBeginIdx + unmatchedPageSize) < unmatchedFrom.size())
            ctx.put("nextUnmatchedFromPageBeginIdx", unmatchedFromBeginIdx + unmatchedPageSize);
        if(unmatchedToBeginIdx > 0)
            ctx.put("previousUnmatchedToPageBeginIdx", unmatchedToBeginIdx - unmatchedPageSize);
        if((unmatchedToBeginIdx + unmatchedPageSize) < unmatchedTo.size())
            ctx.put("nextUnmatchedToPageBeginIdx", unmatchedToBeginIdx + unmatchedPageSize);
        ctx.put("showFields", showFields);

        ctx.put("breadcrumbs", getBreadcrumbs(typeDiff));
    }

    private <T> List<T> sublist(List<T> list, int fromIndex, int pageSize) {
        if(fromIndex >= list.size())
            fromIndex = 0;

        if(fromIndex + pageSize >= list.size())
            pageSize = list.size() - fromIndex;

        return list.subList(fromIndex, fromIndex + pageSize);
    }

    private List<HollowFieldDiffScore> getDisplayDiffs(HollowTypeDiff typeDiff) {
        List<HollowFieldDiff> fieldDiffs = typeDiff.getFieldDiffs();
        List<HollowFieldDiffScore> displayDiffs = new ArrayList<HollowFieldDiffScore>();

        for(int i=0;i<fieldDiffs.size();i++) {
            HollowFieldDiff fieldDiff = fieldDiffs.get(i);
            displayDiffs.add(
                new HollowFieldDiffScore(
                        typeDiff.getTypeName(),
                        i,
                        fieldDiff.getFieldIdentifier().toString(),
                        fieldDiff.getNumDiffs(),
                        typeDiff.getTotalNumberOfMatches(),
                        fieldDiff.getTotalDiffScore())
            );
        }

        Collections.sort(displayDiffs);

        return displayDiffs;
    }

    private List<HollowObjectPairDiffScore> lazyGetDiffScorePairs(HollowTypeDiff typeDiff) {
        List<HollowObjectPairDiffScore> scores = typeObjectPairScores.get(typeDiff.getTypeName());

        if(scores != null) {
            return scores;
        }

        scores = aggregateFieldDiffScores(typeDiff);

        List<HollowObjectPairDiffScore> existingScores = typeObjectPairScores.putIfAbsent(typeDiff.getTypeName(), scores);
        return existingScores != null ? existingScores : scores;
    }

    private List<HollowObjectPairDiffScore> aggregateFieldDiffScores(HollowTypeDiff typeDiff) {
        // Handle from State missing Type
        if (typeDiff.getFromTypeState()==null) return Collections.emptyList();

        List<HollowObjectPairDiffScore> scores;
        int maxFromOrdinal = typeDiff.getFromTypeState().maxOrdinal();
        HollowObjectPairDiffScore[] allDiffPairsIndexedByFromOrdinal = new HollowObjectPairDiffScore[maxFromOrdinal + 1];
        int diffPairCounts = 0;

        for(HollowFieldDiff fieldDiff : typeDiff.getFieldDiffs()) {
            for(int i=0;i<fieldDiff.getNumDiffs();i++) {
                int fromOrdinal = fieldDiff.getFromOrdinal(i);
                if(allDiffPairsIndexedByFromOrdinal[fromOrdinal] == null) {
                    String displayKey = typeDiff.getMatcher().getKeyDisplayString(typeDiff.getFromTypeState(), fromOrdinal);
                    allDiffPairsIndexedByFromOrdinal[fromOrdinal] = new HollowObjectPairDiffScore(displayKey, fromOrdinal, fieldDiff.getToOrdinal(i));
                    diffPairCounts++;
                }
                allDiffPairsIndexedByFromOrdinal[fromOrdinal].incrementDiffScore(fieldDiff.getPairScore(i));
            }
        }

        scores = new ArrayList<HollowObjectPairDiffScore>(diffPairCounts);

        for(HollowObjectPairDiffScore score : allDiffPairsIndexedByFromOrdinal) {
            if(score != null)
                scores.add(score);
        }

        Collections.sort(scores);
        return scores;
    }

    private List<HollowUnmatchedObject> lazyGetUnmatchedFromObjects(HollowTypeDiff typeDiff) {
        return lazyGetUnmatchedObjects(unmatchedFromObjects, typeDiff, typeDiff.getFromTypeState(), typeDiff.getUnmatchedOrdinalsInFrom());
    }

    private List<HollowUnmatchedObject> lazyGetUnmatchedToObjects(HollowTypeDiff typeDiff) {
        return lazyGetUnmatchedObjects(unmatchedToObjects, typeDiff, typeDiff.getToTypeState(), typeDiff.getUnmatchedOrdinalsInTo());
    }

    private List<HollowUnmatchedObject> lazyGetUnmatchedObjects(
            ConcurrentHashMap<String, List<HollowUnmatchedObject>> cache,
            HollowTypeDiff typeDiff,
            HollowObjectTypeReadState typeState,
            IntList unmatchedOrdinals) {
        // Handle typeState missing from either from or to
        if (typeState==null) return Collections.emptyList();

        List<HollowUnmatchedObject> list = cache.get(typeDiff.getTypeName());
        if(list != null)
            return list;

        list = new ArrayList<HollowUnmatchedObject>();
        for(int i=0;i<unmatchedOrdinals.size();i++) {
            int ordinal = unmatchedOrdinals.get(i);
            String keyDisplay = typeDiff.getMatcher().getKeyDisplayString(typeState, ordinal);
            list.add(new HollowUnmatchedObject(keyDisplay, ordinal));
        }

        List<HollowUnmatchedObject> existingList = cache.putIfAbsent(typeState.getSchema().getName(), list);
        return existingList != null ? existingList : list;
    }

    private List<HollowDiffUIBreadcrumbs> getBreadcrumbs(HollowTypeDiff typeDiff) {
        List<HollowDiffUIBreadcrumbs> breadcrumbs = new ArrayList<HollowDiffUIBreadcrumbs>();

        breadcrumbs.add(new HollowDiffUIBreadcrumbs(StringUtils.isEmpty(diffUI.getDiffUIPath()) ?
                "/" : diffUI.getDiffUIPath(), "Overview"));
        breadcrumbs.add(new HollowDiffUIBreadcrumbs(null, typeDiff.getTypeName()));

        return breadcrumbs;
    }

}
