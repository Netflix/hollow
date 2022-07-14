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
package com.netflix.hollow.tools.diff.exact.mapper;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.diff.exact.CombinedMatchPairResultsIterator;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Not intended for external consumption.
 */
public abstract class DiffEqualityTypeMapper {

    protected final HollowTypeReadState fromState;
    protected final HollowTypeReadState toState;
    private final boolean oneToOne;

    protected DiffEqualityTypeMapper(HollowTypeReadState fromState, HollowTypeReadState toState, boolean oneToOne) {
        this.fromState = fromState;
        this.toState = toState;
        this.oneToOne = oneToOne;
    }

    public DiffEqualOrdinalMap mapEqualObjects() {
        int toOrdinalsHashed[] = hashToOrdinals();
        return mapMatchingFromOrdinals(toOrdinalsHashed);
    }

    protected int[] hashToOrdinals() {
        PopulatedOrdinalListener listener = toState.getListener(PopulatedOrdinalListener.class);
        final BitSet toPopulatedOrdinals = listener.getPopulatedOrdinals();
        final int ordinalSpaceLength = toPopulatedOrdinals.length();

        int hashedOrdinalsLength = 1 << (32 - Integer.numberOfLeadingZeros((toPopulatedOrdinals.cardinality() * 2) - 1));

        final AtomicIntegerArray hashedToOrdinals = new AtomicIntegerArray(hashedOrdinalsLength);
        for(int i = 0; i < hashedOrdinalsLength; i++)
            hashedToOrdinals.set(i, -1);

        SimultaneousExecutor executor = new SimultaneousExecutor(1.5d, getClass(), "hash-to-ordinals");
        final int numThreads = executor.getCorePoolSize();

        for(int i = 0; i < numThreads; i++) {
            final int threadNumber = i;

            executor.execute(() -> {
                for(int t = threadNumber; t < ordinalSpaceLength; t += numThreads) {
                    if(toPopulatedOrdinals.get(t)) {
                        int hashCode = toRecordHashCode(t);
                        if(hashCode != -1) {
                            int bucket = hashCode & (hashedToOrdinals.length() - 1);
                            while(!hashedToOrdinals.compareAndSet(bucket, -1, t)) {
                                bucket = (bucket + 1) & (hashedToOrdinals.length() - 1);
                            }
                        }
                    }
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        int arr[] = new int[hashedToOrdinals.length()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = hashedToOrdinals.get(i);
        }
        return arr;
    }

    protected DiffEqualOrdinalMap mapMatchingFromOrdinals(final int[] hashedToOrdinals) {
        PopulatedOrdinalListener listener = fromState.getListener(PopulatedOrdinalListener.class);
        final BitSet fromPopulatedOrdinals = listener.getPopulatedOrdinals();
        final int ordinalSpaceLength = fromPopulatedOrdinals.length();

        SimultaneousExecutor executor = new SimultaneousExecutor(1.5d, getClass(), "map-matching-from-ordinals");
        final int numThreads = executor.getCorePoolSize();
        final LongList[] matchPairResults = new LongList[numThreads];

        for(int i = 0; i < numThreads; i++) {
            final int threadNumber = i;
            matchPairResults[threadNumber] = new LongList();
            executor.execute(() -> {
                EqualityDeterminer equalityDeterminer = getEqualityDeterminer();

                for(int t = threadNumber; t < ordinalSpaceLength; t += numThreads) {
                    if(fromPopulatedOrdinals.get(t)) {
                        int hashCode = fromRecordHashCode(t);
                        if(hashCode != -1) {
                            int bucket = hashCode & (hashedToOrdinals.length - 1);
                            while(hashedToOrdinals[bucket] != -1) {
                                if(equalityDeterminer.recordsAreEqual(t, hashedToOrdinals[bucket])) {
                                    matchPairResults[threadNumber].add(((long) t << 32) | hashedToOrdinals[bucket]);
                                }
                                bucket = (bucket + 1) & (hashedToOrdinals.length - 1);
                            }
                        }
                    }
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        int numMatches = 0;
        for(int i = 0; i < matchPairResults.length; i++) {
            numMatches += matchPairResults[i].size();
        }

        CombinedMatchPairResultsIterator resultsIterator = new CombinedMatchPairResultsIterator(matchPairResults);

        DiffEqualOrdinalMap ordinalMap = new DiffEqualOrdinalMap(numMatches);

        if(oneToOne) {
            BitSet alreadyMappedToOrdinals = new BitSet(toState.maxOrdinal() + 1);
            while(resultsIterator.next()) {
                int fromOrdinal = resultsIterator.fromOrdinal();
                IntList toOrdinals = resultsIterator.toOrdinals();
                for(int i = 0; i < toOrdinals.size(); i++) {
                    if(!alreadyMappedToOrdinals.get(toOrdinals.get(i))) {
                        alreadyMappedToOrdinals.set(toOrdinals.get(i));
                        ordinalMap.putEqualOrdinal(fromOrdinal, toOrdinals.get(i));
                        break;
                    }
                }
            }
        } else {
            while(resultsIterator.next()) {
                ordinalMap.putEqualOrdinals(resultsIterator.fromOrdinal(), resultsIterator.toOrdinals());
            }
        }

        return ordinalMap;
    }

    public abstract boolean requiresTraversalForMissingFields();

    protected abstract int fromRecordHashCode(int ordinal);

    protected abstract int toRecordHashCode(int ordinal);

    protected abstract EqualityDeterminer getEqualityDeterminer();

    protected interface EqualityDeterminer {
        public boolean recordsAreEqual(int fromOrdinal, int toOrdinal);
    }

}
