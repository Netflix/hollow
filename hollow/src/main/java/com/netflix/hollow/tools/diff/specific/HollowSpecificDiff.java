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
package com.netflix.hollow.tools.diff.specific;

import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.diff.HollowDiffMatcher;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The HollowSpecificDiff allows for the investigation of diffs among specific fields for matched records in two states.
 */
public class HollowSpecificDiff {

    private final HollowReadStateEngine from;
    private final HollowReadStateEngine to;
    private final HollowDiffMatcher matcher;
    private final String type;

    private BitSet elementKeyPaths;
    private BitSet elementNonKeyPaths;
    private String elementPaths[];

    private final AtomicLong totalUnmatchedFromElements;
    private final AtomicLong totalUnmatchedToElements;
    private final AtomicLong totalModifiedElements;
    private final AtomicLong totalMatchedEqualElements;

    /**
     * @param from the from state
     * @param to the to state
     * @param type the type to diff
     */
    public HollowSpecificDiff(HollowReadStateEngine from, HollowReadStateEngine to, String type) {
        this.from = from;
        this.to = to;
        this.matcher = new HollowDiffMatcher((HollowObjectTypeReadState) from.getTypeState(type), (HollowObjectTypeReadState) to.getTypeState(type));
        this.type = type;
        this.totalUnmatchedFromElements = new AtomicLong();
        this.totalUnmatchedToElements = new AtomicLong();
        this.totalMatchedEqualElements = new AtomicLong();
        this.totalModifiedElements = new AtomicLong();
    }

    /**
     * Set the primary key paths which will be used to find matching records across the two states
     *
     * @param paths the key paths
     */
    public void setRecordMatchPaths(String... paths) {
        for(String path : paths)
            matcher.addMatchPath(path);
    }

    /**
     * Set the paths for which we will inspect differences across the two states
     *
     * @param paths the paths for inspection
     */
    public void setElementMatchPaths(String... paths) {
        resetResults();
        this.elementPaths = paths;
        this.elementKeyPaths = null;
        this.elementNonKeyPaths = null;
    }

    /**
     * Optionally specify paths for which we will match records within an individual type's hierarchy
     *
     * @param paths the paths for matching
     */
    public void setElementKeyPaths(String... paths) {
        resetResults();
        elementKeyPaths = new BitSet(elementPaths.length);
        for(int i = 0; i < paths.length; i++) {
            int elementPathIdx = getElementPathIdx(paths[i]);
            if(elementPathIdx == -1)
                throw new IllegalArgumentException("Key path must have been specified as an element match path.  Offending path: " + paths[i]);
            elementKeyPaths.set(elementPathIdx);
        }

        elementNonKeyPaths = new BitSet(elementPaths.length);
        elementNonKeyPaths.set(0, elementPaths.length);
        elementNonKeyPaths.andNot(elementKeyPaths);
    }

    public int getElementPathIdx(String path) {
        for(int i = 0; i < elementPaths.length; i++) {
            if(elementPaths[i].equals(path))
                return i;
        }
        return -1;
    }

    /**
     * Find the matching records (based on primary keys) across states
     */
    public void prepareMatches() {
        matcher.calculateMatches();
    }

    /**
     * Calculate the differences
     */
    public void calculate() {
        resetResults();
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "calculate");
        final int numThreads = executor.getCorePoolSize();

        for(int i = 0; i < numThreads; i++) {
            final int threadNumber = i;
            executor.execute(new Runnable() {
                public void run() {
                    HollowIndexerValueTraverser fromTraverser = new HollowIndexerValueTraverser(from, type, elementPaths);
                    HollowIndexerValueTraverser toTraverser = new HollowIndexerValueTraverser(to, type, elementPaths);

                    int hashedResults[] = new int[16];

                    for(int i = threadNumber; i < matcher.getMatchedOrdinals().size(); i += numThreads) {
                        long ordinalPair = matcher.getMatchedOrdinals().get(i);
                        int fromOrdinal = (int) (ordinalPair >>> 32);
                        int toOrdinal = (int) ordinalPair;

                        fromTraverser.traverse(fromOrdinal);
                        toTraverser.traverse(toOrdinal);

                        if(fromTraverser.getNumMatches() * 2 > hashedResults.length)
                            hashedResults = new int[hashTableSize(fromTraverser.getNumMatches())];

                        populateHashTable(fromTraverser, hashedResults);

                        countMatches(fromTraverser, toTraverser, hashedResults);
                    }

                    for(int i = threadNumber; i < matcher.getExtraInFrom().size(); i += numThreads) {
                        fromTraverser.traverse(matcher.getExtraInFrom().get(i));
                        totalUnmatchedFromElements.addAndGet(fromTraverser.getNumMatches());
                    }

                    for(int i = threadNumber; i < matcher.getExtraInTo().size(); i += numThreads) {
                        toTraverser.traverse(matcher.getExtraInTo().get(i));
                        totalUnmatchedToElements.addAndGet(toTraverser.getNumMatches());
                    }
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void countMatches(HollowIndexerValueTraverser fromTraverser, HollowIndexerValueTraverser toTraverser, int[] hashedResults) {
        int numMatchedEqualElements = 0;
        int numModifiedElements = 0;

        int hashMask = hashedResults.length - 1;

        for(int j = 0; j < toTraverser.getNumMatches(); j++) {
            int hash = elementKeyPaths == null ? toTraverser.getMatchHash(j) : toTraverser.getMatchHash(j, elementKeyPaths);
            int bucket = hash & hashMask;

            while(hashedResults[bucket] != -1) {
                if(elementKeyPaths == null) {
                    if(fromTraverser.isMatchEqual(hashedResults[bucket], toTraverser, j)) {
                        numMatchedEqualElements++;
                        break;
                    }
                } else {
                    if(fromTraverser.isMatchEqual(hashedResults[bucket], toTraverser, j, elementKeyPaths)) {
                        if(fromTraverser.isMatchEqual(hashedResults[bucket], toTraverser, j, elementNonKeyPaths))
                            numMatchedEqualElements++;
                        else
                            numModifiedElements++;
                        break;
                    }
                }

                bucket++;
                bucket &= hashMask;
            }
        }

        int numCommonMatches = numMatchedEqualElements + numModifiedElements;

        totalMatchedEqualElements.addAndGet(numMatchedEqualElements);
        totalModifiedElements.addAndGet(numModifiedElements);
        totalUnmatchedFromElements.addAndGet(fromTraverser.getNumMatches() - numCommonMatches);
        totalUnmatchedToElements.addAndGet(toTraverser.getNumMatches() - numCommonMatches);
    }

    private void populateHashTable(HollowIndexerValueTraverser fromTraverser, int[] hashedResults) {
        Arrays.fill(hashedResults, -1);
        int hashMask = hashedResults.length - 1;

        for(int j = 0; j < fromTraverser.getNumMatches(); j++) {
            int hash = elementKeyPaths == null ? fromTraverser.getMatchHash(j) : fromTraverser.getMatchHash(j, elementKeyPaths);
            int bucket = hash & hashMask;

            while(hashedResults[bucket] != -1) {
                bucket++;
                bucket &= hashMask;
            }

            hashedResults[bucket] = j;
        }
    }

    private int hashTableSize(int numMatches) {
        return 1 << (32 - Integer.numberOfLeadingZeros((numMatches * 2) - 1));
    }

    private void resetResults() {
        totalUnmatchedFromElements.set(0);
        totalUnmatchedToElements.set(0);
        totalMatchedEqualElements.set(0);
        totalModifiedElements.set(0);
    }

    public long getTotalUnmatchedFromElements() {
        return totalUnmatchedFromElements.get();
    }

    public long getTotalUnmatchedToElements() {
        return totalUnmatchedToElements.get();
    }

    public long getTotalMatchedEqualElements() {
        return totalMatchedEqualElements.get();
    }

    public long getTotalModifiedElements() {
        return totalModifiedElements.get();
    }

}
