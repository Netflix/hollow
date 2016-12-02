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
package com.netflix.hollow.tools.diff;

import com.netflix.hollow.core.util.SimultaneousExecutor;

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.tools.diff.count.HollowDiffCountingNode;
import com.netflix.hollow.tools.diff.count.HollowDiffObjectCountingNode;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Obtained via a {@link HollowDiff}, this is a report of the differences in a specific type between two data states. 
 */
public class HollowTypeDiff {

    private final HollowDiff rootDiff;

    private final HollowObjectTypeReadState from;
    private final HollowObjectTypeReadState to;

    private final HollowDiffMatcher matcher;

    private final String type;
    private List<HollowFieldDiff> calculatedFieldDiffs;


    HollowTypeDiff(HollowDiff rootDiff, String type, String... matchPaths) {
        this.rootDiff = rootDiff;
        this.type = type;
        this.from = (HollowObjectTypeReadState) rootDiff.getFromStateEngine().getTypeState(type);
        this.to = (HollowObjectTypeReadState) rootDiff.getToStateEngine().getTypeState(type);
        this.matcher = new HollowDiffMatcher(this.from, this.to);
        
        for(String matchPath : matchPaths) {
            addMatchPath(matchPath);
        }
    }

    /**
     * @return The type name for this type diff
     */
    public String getTypeName() {
        return type;
    }

    /**
     * Add a field path to a component of the primary key
     * @param path
     */
    public void addMatchPath(String path) {
        matcher.addMatchPath(path);
    }

    /**
     * Get the differences broken down by specific field paths
     * 
     * @return
     */
    public List<HollowFieldDiff> getFieldDiffs() {
        return calculatedFieldDiffs;
    }

    /**
     * @return the total number of matched records (based on primary key)
     */
    public int getTotalNumberOfMatches() {
        return matcher.getMatchedOrdinals().size();
    }

    /**
     * @return A list of the record ordinals in the from state which did not have a corresponding match (based on primary key) in the to state. 
     */
    public IntList getUnmatchedOrdinalsInFrom() {
        return matcher.getExtraInFrom();
    }

    /**
     * @return A list of the record ordinals in the to state which did not have a corresponding match (based on primary key) in the from state.
     */
    public IntList getUnmatchedOrdinalsInTo() {
        return matcher.getExtraInTo();
    }

    /**
     * @return The total 'diff score', useful as a very broad measure of the magnitude of the diff.
     */
    public long getTotalDiffScore() {
        long totalDiffScore = 0;
        for(HollowFieldDiff diff : calculatedFieldDiffs) {
            totalDiffScore += diff.getTotalDiffScore();
        }
        return totalDiffScore;
    }

    /**
     * @return The total number of records for this type in the to state.
     */
    public int getTotalItemsInFromState() {
        if (from == null) return 0;
        return from.getPopulatedOrdinals().cardinality();
    }

    /**
     * @return The total number of records for this type in the to state.
     */
    public int getTotalItemsInToState() {
        if (to == null) return 0;
        return to.getPopulatedOrdinals().cardinality();
    }

    public boolean hasAnyData() {
        return from != null || to != null;
    }

    public HollowObjectTypeReadState getFromTypeState() {
        return from;
    }

    public HollowObjectTypeReadState getToTypeState() {
        return to;
    }

    public HollowDiffMatcher getMatcher() {
        return matcher;
    }

    void calculateMatches() {
        matcher.calculateMatches();
    }

    @SuppressWarnings("unchecked")
    void calculateDiffs() {
        final HollowDiffNodeIdentifier rootId = new HollowDiffNodeIdentifier(type);

        SimultaneousExecutor executor = new SimultaneousExecutor();

        final int numThreads = executor.getCorePoolSize();

        final List<HollowFieldDiff>results[] = (List<HollowFieldDiff>[]) new List[numThreads];

        for(int i=0;i<numThreads;i++) {
            final int threadId = i;

            executor.execute(new Runnable() {
                public void run() {
                    try {
                        DiffEqualityMapping equalityMapping = rootDiff.getEqualityMapping();
                        HollowDiffCountingNode rootNode = new HollowDiffObjectCountingNode(equalityMapping, rootId, from, to);

                        DiffEqualOrdinalMap rootNodeOrdinalMap = equalityMapping.getEqualOrdinalMap(type);
                        boolean requiresMissingFieldTraversal = equalityMapping.requiresMissingFieldTraversal(type);

                        LongList matches = matcher.getMatchedOrdinals();
                        for(int i=threadId;i<matches.size();i+=numThreads) {
                            int fromOrdinal = (int)(matches.get(i) >> 32);
                            int toOrdinal = (int)matches.get(i);

                            if(rootNodeOrdinalMap.getIdentityFromOrdinal(fromOrdinal) == -1
                                    || rootNodeOrdinalMap.getIdentityFromOrdinal(fromOrdinal) != rootNodeOrdinalMap.getIdentityToOrdinal(toOrdinal)) {
                                rootNode.prepare(fromOrdinal, toOrdinal);
                                rootNode.traverseDiffs(fromIntList(fromOrdinal), toIntList(toOrdinal));
                            } else if(requiresMissingFieldTraversal) {
                                rootNode.prepare(fromOrdinal, toOrdinal);
                                rootNode.traverseMissingFields(fromIntList(fromOrdinal), toIntList(toOrdinal));
                            }
                        }

                        results[threadId] = rootNode.getFieldDiffs();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                private final IntList fromIntList = new IntList(1);
                private final IntList toIntList = new IntList(1);

                private IntList fromIntList(int ordinal) {
                    fromIntList.clear();
                    fromIntList.add(ordinal);
                    return fromIntList;
                }

                private IntList toIntList(int ordinal) {
                    toIntList.clear();
                    toIntList.add(ordinal);
                    return toIntList;
                }
            });
        }

        executor.awaitUninterruptibly();

        this.calculatedFieldDiffs = combineResults(results);
    }

    private List<HollowFieldDiff> combineResults(List<HollowFieldDiff> shardedResults[]) {
        Map<HollowDiffNodeIdentifier, HollowFieldDiff> combinedResultsMap = new HashMap<HollowDiffNodeIdentifier, HollowFieldDiff>();

        for(List<HollowFieldDiff> shardResult : shardedResults) {
            for(HollowFieldDiff fieldDiff : shardResult) {
                HollowFieldDiff combinedResult = combinedResultsMap.get(fieldDiff.getFieldIdentifier());
                if(combinedResult != null)
                    combinedResult.addResults(fieldDiff);
                else
                    combinedResultsMap.put(fieldDiff.getFieldIdentifier(), fieldDiff);
            }
        }

        List<HollowFieldDiff> combinedResults = new ArrayList<HollowFieldDiff>();
        combinedResults.addAll(combinedResultsMap.values());
        return combinedResults;
    }

}
