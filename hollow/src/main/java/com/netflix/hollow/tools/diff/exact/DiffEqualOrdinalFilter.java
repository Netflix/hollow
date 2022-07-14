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
public class DiffEqualOrdinalFilter {

    private final DiffEqualOrdinalMap equalOrdinalMap;
    private final IntList matchedFromOrdinals;
    private final IntList matchedToOrdinals;
    private final IntList unmatchedFromOrdinals;
    private final IntList unmatchedToOrdinals;

    private int hashedIdentityOrdinals[];
    private int hashedIdentityOrdinalsCounts[];
    private int matchedOrdinalsCounts[];

    public DiffEqualOrdinalFilter(DiffEqualOrdinalMap equalityMapping) {
        this.equalOrdinalMap = equalityMapping;
        this.matchedFromOrdinals = new IntList();
        this.matchedToOrdinals = new IntList();
        this.unmatchedFromOrdinals = new IntList();
        this.unmatchedToOrdinals = new IntList();
        this.hashedIdentityOrdinals = new int[0];
        this.hashedIdentityOrdinalsCounts = new int[0];
        this.matchedOrdinalsCounts = new int[0];
    }

    public void filter(IntList fromOrdinals, IntList toOrdinals) {
        matchedFromOrdinals.clear();
        matchedToOrdinals.clear();
        unmatchedFromOrdinals.clear();
        unmatchedToOrdinals.clear();

        int hashSize = 1 << (32 - Integer.numberOfLeadingZeros((fromOrdinals.size() * 2) - 1));
        if(hashedIdentityOrdinals.length < hashSize) {
            hashedIdentityOrdinals = new int[hashSize];
            hashedIdentityOrdinalsCounts = new int[hashSize];
            matchedOrdinalsCounts = new int[hashSize];
        }

        Arrays.fill(hashedIdentityOrdinals, -1);
        Arrays.fill(hashedIdentityOrdinalsCounts, 0);
        Arrays.fill(matchedOrdinalsCounts, 0);

        for(int i = 0; i < fromOrdinals.size(); i++) {
            int identity = equalOrdinalMap.getIdentityFromOrdinal(fromOrdinals.get(i));
            if(identity != -1) {
                int hashCode = HashCodes.hashInt(identity);
                int bucket = hashCode & (hashedIdentityOrdinals.length - 1);

                while(hashedIdentityOrdinals[bucket] != -1 && hashedIdentityOrdinals[bucket] != identity) {
                    bucket = (bucket + 1) & (hashedIdentityOrdinals.length - 1);
                }

                hashedIdentityOrdinals[bucket] = identity;
                hashedIdentityOrdinalsCounts[bucket]++;
            }
        }

        for(int i = 0; i < toOrdinals.size(); i++) {
            int identity = equalOrdinalMap.getIdentityToOrdinal(toOrdinals.get(i));
            if(identity != -1) {
                int hashCode = HashCodes.hashInt(identity);
                int bucket = hashCode & (hashedIdentityOrdinals.length - 1);

                while(hashedIdentityOrdinals[bucket] != -1 && hashedIdentityOrdinals[bucket] != identity) {
                    bucket = (bucket + 1) & (hashedIdentityOrdinals.length - 1);
                }

                if(hashedIdentityOrdinals[bucket] == identity && matchedOrdinalsCounts[bucket] < hashedIdentityOrdinalsCounts[bucket]) {
                    matchedOrdinalsCounts[bucket]++;
                    matchedToOrdinals.add(toOrdinals.get(i));
                } else {
                    unmatchedToOrdinals.add(toOrdinals.get(i));
                }
            } else {
                unmatchedToOrdinals.add(toOrdinals.get(i));
            }
        }

        for(int i = 0; i < fromOrdinals.size(); i++) {
            int identity = equalOrdinalMap.getIdentityFromOrdinal(fromOrdinals.get(i));
            if(identity != -1) {
                int hashCode = HashCodes.hashInt(identity);
                int bucket = hashCode & (hashedIdentityOrdinals.length - 1);

                while(hashedIdentityOrdinals[bucket] != identity) {
                    bucket = (bucket + 1) & (hashedIdentityOrdinals.length - 1);
                }

                if(matchedOrdinalsCounts[bucket] > 0) {
                    matchedOrdinalsCounts[bucket]--;
                    matchedFromOrdinals.add(fromOrdinals.get(i));
                } else {
                    unmatchedFromOrdinals.add(fromOrdinals.get(i));
                }
            } else {
                unmatchedFromOrdinals.add(fromOrdinals.get(i));
            }
        }
    }

    public IntList getMatchedFromOrdinals() {
        return matchedFromOrdinals;
    }

    public IntList getMatchedToOrdinals() {
        return matchedToOrdinals;
    }

    public IntList getUnmatchedFromOrdinals() {
        return unmatchedFromOrdinals;
    }

    public IntList getUnmatchedToOrdinals() {
        return unmatchedToOrdinals;
    }

}
