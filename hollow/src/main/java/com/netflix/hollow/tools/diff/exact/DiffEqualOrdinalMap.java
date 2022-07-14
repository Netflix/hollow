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
package com.netflix.hollow.tools.diff.exact;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.util.IntList;
import java.util.Arrays;

/**
 * Not intended for external consumption.
 */
public class DiffEqualOrdinalMap {

    public static final DiffEqualOrdinalMap EMPTY_MAP = new DiffEqualOrdinalMap(0);

    private final long fromOrdinalsMap[];
    private final IntList pivotedToOrdinalClusters;

    private final long toOrdinalsIdentityMap[];

    public DiffEqualOrdinalMap(int numMatches) {
        int hashTableSize = 1 << (32 - Integer.numberOfLeadingZeros(numMatches * 2 - 1));

        fromOrdinalsMap = new long[hashTableSize];
        toOrdinalsIdentityMap = new long[hashTableSize];
        pivotedToOrdinalClusters = new IntList();
        Arrays.fill(fromOrdinalsMap, -1L);
        Arrays.fill(toOrdinalsIdentityMap, -1L);
    }

    public void putEqualOrdinal(int fromOrdinal, int toOrdinal) {
        long fromOrdinalMapEntry = (long) toOrdinal << 32 | fromOrdinal;

        int hashCode = HashCodes.hashInt(fromOrdinal);

        int bucket = hashCode & (fromOrdinalsMap.length - 1);

        while(fromOrdinalsMap[bucket] != -1)
            bucket = (bucket + 1) & (fromOrdinalsMap.length - 1);

        fromOrdinalsMap[bucket] = fromOrdinalMapEntry;
    }

    public void putEqualOrdinals(int fromOrdinal, IntList toOrdinals) {
        long fromOrdinalMapEntry = (long) toOrdinals.get(0) << 32 | fromOrdinal;

        if(toOrdinals.size() > 1) {
            fromOrdinalMapEntry = Long.MIN_VALUE | (long) pivotedToOrdinalClusters.size() << 32 | fromOrdinal;

            for(int i = 0; i < toOrdinals.size(); i++) {
                int valueToAdd = toOrdinals.get(i);
                if(i == toOrdinals.size() - 1)
                    valueToAdd |= Integer.MIN_VALUE;
                pivotedToOrdinalClusters.add(valueToAdd);
            }
        }

        int hashCode = HashCodes.hashInt(fromOrdinal);

        int bucket = hashCode & (fromOrdinalsMap.length - 1);

        while(fromOrdinalsMap[bucket] != -1)
            bucket = (bucket + 1) & (fromOrdinalsMap.length - 1);

        fromOrdinalsMap[bucket] = fromOrdinalMapEntry;
    }

    public void buildToOrdinalIdentityMapping() {
        for(int i = 0; i < fromOrdinalsMap.length; i++) {
            if(fromOrdinalsMap[i] >= 0) {
                int toOrdinal = (int) (fromOrdinalsMap[i] >> 32);
                addToOrdinalIdentity(toOrdinal, toOrdinal);
            }
        }

        boolean newCluster = true;
        int currentIdentity = 0;

        for(int i = 0; i < pivotedToOrdinalClusters.size(); i++) {
            if(newCluster)
                currentIdentity = pivotedToOrdinalClusters.get(i);
            addToOrdinalIdentity(pivotedToOrdinalClusters.get(i) & Integer.MAX_VALUE, currentIdentity);
            newCluster = (pivotedToOrdinalClusters.get(i) & Integer.MIN_VALUE) != 0;
        }
    }

    private void addToOrdinalIdentity(int toOrdinal, int identity) {
        int hashCode = HashCodes.hashInt(toOrdinal);
        int bucket = hashCode & (toOrdinalsIdentityMap.length - 1);

        while(toOrdinalsIdentityMap[bucket] != -1) {
            bucket = (bucket + 1) & (toOrdinalsIdentityMap.length - 1);
        }

        toOrdinalsIdentityMap[bucket] = ((long) identity << 32) | toOrdinal;
    }

    public MatchIterator getEqualOrdinals(int fromOrdinal) {
        int hashCode = HashCodes.hashInt(fromOrdinal);

        int bucket = hashCode & (fromOrdinalsMap.length - 1);

        while(fromOrdinalsMap[bucket] != -1L) {
            if((int) fromOrdinalsMap[bucket] == fromOrdinal) {
                if((fromOrdinalsMap[bucket] & Long.MIN_VALUE) != 0L)
                    return new PivotedMatchIterator((int) ((fromOrdinalsMap[bucket] & Long.MAX_VALUE) >> 32));
                return new SingleMatchIterator((int) (fromOrdinalsMap[bucket] >> 32));
            }
            bucket = (bucket + 1) & (fromOrdinalsMap.length - 1);
        }

        return EmptyMatchIterator.INSTANCE;
    }

    public int getIdentityFromOrdinal(int fromOrdinal) {
        int hashCode = HashCodes.hashInt(fromOrdinal);

        int bucket = hashCode & (fromOrdinalsMap.length - 1);

        while(fromOrdinalsMap[bucket] != -1L) {
            if((int) fromOrdinalsMap[bucket] == fromOrdinal) {
                if((fromOrdinalsMap[bucket] & Long.MIN_VALUE) != 0L)
                    return pivotedToOrdinalClusters.get((int) ((fromOrdinalsMap[bucket] & Long.MAX_VALUE) >> 32));
                return (int) (fromOrdinalsMap[bucket] >> 32);
            }
            bucket = (bucket + 1) & (fromOrdinalsMap.length - 1);
        }

        return -1;
    }

    public int getIdentityToOrdinal(int toOrdinal) {
        int hashCode = HashCodes.hashInt(toOrdinal);

        int bucket = hashCode & (toOrdinalsIdentityMap.length - 1);

        while(toOrdinalsIdentityMap[bucket] != -1L) {
            if((int) toOrdinalsIdentityMap[bucket] == toOrdinal)
                return (int) (toOrdinalsIdentityMap[bucket] >> 32);
            bucket = (bucket + 1) & (toOrdinalsIdentityMap.length - 1);
        }

        return -1;
    }

    public static interface OrdinalIdentityTranslator {
        public int getIdentityOrdinal(int ordinal);
    }

    private final OrdinalIdentityTranslator fromIdentityTranslator = new OrdinalIdentityTranslator() {
        public int getIdentityOrdinal(int ordinal) {
            return getIdentityFromOrdinal(ordinal);
        }
    };

    private final OrdinalIdentityTranslator toIdentityTranslator = new OrdinalIdentityTranslator() {
        public int getIdentityOrdinal(int ordinal) {
            return getIdentityToOrdinal(ordinal);
        }
    };

    public OrdinalIdentityTranslator getFromOrdinalIdentityTranslator() {
        return fromIdentityTranslator;
    }

    public OrdinalIdentityTranslator getToOrdinalIdentityTranslator() {
        return toIdentityTranslator;
    }

    public static interface MatchIterator {
        public boolean hasNext();

        public int next();
    }

    public static class EmptyMatchIterator implements MatchIterator {
        static EmptyMatchIterator INSTANCE = new EmptyMatchIterator();

        public boolean hasNext() {
            return false;
        }

        public int next() {
            return -1;
        }
    }

    public static class SingleMatchIterator implements MatchIterator {
        private final int singleMatch;
        private boolean exhausted;

        public SingleMatchIterator(int singleMatch) {
            this.singleMatch = singleMatch;
        }

        public boolean hasNext() {
            return !exhausted;
        }

        public int next() {
            exhausted = true;
            return singleMatch;
        }
    }

    public class PivotedMatchIterator implements MatchIterator {
        private int currentMatchListPosition;
        private boolean exhausted;

        public PivotedMatchIterator(int matchListPosition) {
            this.currentMatchListPosition = matchListPosition;
        }

        public boolean hasNext() {
            return !exhausted;
        }

        public int next() {
            int nextVal = pivotedToOrdinalClusters.get(currentMatchListPosition++);

            exhausted = (nextVal & Integer.MIN_VALUE) != 0;
            return nextVal & Integer.MAX_VALUE;
        }

    }

}
