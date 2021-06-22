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
package com.netflix.hollow.tools.diff;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Intended for use in the context of a HollowDiff.
 *
 * This class will match records of a specific type from two data states based on a user defined primary key.
 */
public class HollowDiffMatcher {

    private final List<String> matchPaths;

    private final HollowObjectTypeReadState fromTypeState;
    private final HollowObjectTypeReadState toTypeState;

    private final LongList matchedOrdinals;
    private final IntList extraInFrom;
    private final IntList extraInTo;

    private HollowPrimaryKeyIndex fromIdx;
    private HollowPrimaryKeyIndex toIdx;

    public HollowDiffMatcher(HollowObjectTypeReadState fromTypeState, HollowObjectTypeReadState toTypeState) {
        this.matchPaths = new ArrayList<>();
        this.fromTypeState = fromTypeState;
        this.toTypeState = toTypeState;
        this.matchedOrdinals = new LongList();
        this.extraInFrom = new IntList();
        this.extraInTo = new IntList();
    }

    public void addMatchPath(String path) {
        matchPaths.add(path);
    }

    public List<String> getMatchPaths() {
        return matchPaths;
    }

    public void calculateMatches() {
        if (fromTypeState==null) {
            toTypeState.getPopulatedOrdinals().stream().forEach(i -> extraInTo.add(i));
            return;
        }

        if (toTypeState==null) {
            fromTypeState.getPopulatedOrdinals().stream().forEach(i -> extraInFrom.add(i));
            return;
        }

        // No Primary Key so no matching will be done
        if (matchPaths==null || matchPaths.isEmpty()) {
            toTypeState.getPopulatedOrdinals().stream().forEach(i -> extraInTo.add(i));
            fromTypeState.getPopulatedOrdinals().stream().forEach(i -> extraInFrom.add(i));
            return;
        }

        fromIdx = new HollowPrimaryKeyIndex(fromTypeState.getStateEngine(), fromTypeState.getSchema().getName(), matchPaths.toArray(new String[matchPaths.size()]));
        toIdx = new HollowPrimaryKeyIndex(toTypeState.getStateEngine(), toTypeState.getSchema().getName(), matchPaths.toArray(new String[matchPaths.size()]));

        BitSet fromPopulatedOrdinals = fromTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        BitSet fromUnmatchedOrdinals = new BitSet(fromPopulatedOrdinals.length());
        fromUnmatchedOrdinals.or(fromPopulatedOrdinals);

        BitSet toPopulatedOrdinals = toTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int candidateToMatchOrdinal = toPopulatedOrdinals.nextSetBit(0);
        while(candidateToMatchOrdinal != -1) {
            Object key[] = toIdx.getRecordKey(candidateToMatchOrdinal);
            int matchedOrdinal = HollowConstants.ORDINAL_NONE;
            try {
                matchedOrdinal = fromIdx.getMatchingOrdinal(key);
            } catch(NullPointerException ex) {
                throw new RuntimeException("Error fetching matching ordinal for null type " + toTypeState.getSchema().getName()
                        + " with key field values " + Arrays.asList(key) + " at ordinal : " + candidateToMatchOrdinal
                        + "with stack trace ", ex);
            }

            if(matchedOrdinal != -1) {
                matchedOrdinals.add(((long)matchedOrdinal << 32) | candidateToMatchOrdinal);
                fromUnmatchedOrdinals.clear(matchedOrdinal);
            } else {
                extraInTo.add(candidateToMatchOrdinal);
            }

            candidateToMatchOrdinal = toPopulatedOrdinals.nextSetBit(candidateToMatchOrdinal + 1);
        }

        int unmatchedFromOrdinal = fromUnmatchedOrdinals.nextSetBit(0);
        while(unmatchedFromOrdinal != -1) {
            extraInFrom.add(unmatchedFromOrdinal);
            unmatchedFromOrdinal = fromUnmatchedOrdinals.nextSetBit(unmatchedFromOrdinal + 1);
        }
    }

    public LongList getMatchedOrdinals() {
        return matchedOrdinals;
    }

    public IntList getExtraInFrom() {
        return extraInFrom;
    }

    public IntList getExtraInTo() {
        return extraInTo;
    }

    public String getKeyDisplayString(HollowObjectTypeReadState state, int ordinal) {
        Object[] key = null;

        if(state == fromTypeState && fromIdx!=null) {
            key = fromIdx.getRecordKey(ordinal);
        } else if(state == toTypeState && toIdx!=null) {
            key = toIdx.getRecordKey(ordinal);
        }

        // Show Display similar to Hollow Explorer when there is no primary key
        if(key == null)
            return "ORDINAL:" + ordinal;

        return keyDisplayString(key);
    }

    private String keyDisplayString(Object[] key) {
        StringBuilder sb = new StringBuilder(key[0].toString());

        for(int i=1;i<key.length;i++) {
            sb.append(" ");
            sb.append(key[i].toString());
        }

        return sb.toString();
    }

}
