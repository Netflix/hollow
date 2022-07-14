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
package com.netflix.hollow.tools.diff.count;

import static com.netflix.hollow.core.read.HollowReadFieldUtils.fieldHashCode;
import static com.netflix.hollow.core.read.HollowReadFieldUtils.fieldsAreEqual;

import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Counting nodes are used by the HollowDiff to count and aggregate changes for specific record types in a data model.
 * 
 * This type of counting node is applicable to specific fields in object types.
 * 
 * Not intended for external consumption.
 */
public class HollowDiffFieldCountingNode extends HollowDiffCountingNode {

    private final HollowObjectTypeReadState fromState;
    private final HollowObjectTypeReadState toState;

    private final int fromFieldIndex;
    private final int toFieldIndex;

    private int[] hashedOrdinals;
    private int[] ordinalHashCodes;
    private int[] ordinalHashCounts;
    private int hashSizeBeforeGrow;
    private int hashSize;

    private int currentTopLevelFromOrdinal;
    private int currentTopLevelToOrdinal;

    private int unmatchedToFields;

    private final HollowFieldDiff fieldDiff;

    public HollowDiffFieldCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId, HollowObjectTypeReadState fromState, HollowObjectTypeReadState toState, HollowObjectSchema unionSchema, int unionFieldIndex) {
        super(diff, topLevelTypeDiff, nodeId);
        this.fromState = fromState;
        this.toState = toState;
        String fieldName = unionSchema.getFieldName(unionFieldIndex);
        this.fromFieldIndex = fromState == null ? -1 : fromState.getSchema().getPosition(fieldName);
        this.toFieldIndex = toState == null ? -1 : toState.getSchema().getPosition(fieldName);
        this.fieldDiff = new HollowFieldDiff(nodeId);

        this.hashedOrdinals = new int[16];
        this.ordinalHashCodes = new int[16];
        this.ordinalHashCounts = new int[16];
        this.hashSizeBeforeGrow = 11;
        Arrays.fill(hashedOrdinals, -1);
    }

    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        this.currentTopLevelFromOrdinal = topLevelFromOrdinal;
        this.currentTopLevelToOrdinal = topLevelToOrdinal;
    }

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        if(fromFieldIndex == -1 || toFieldIndex == -1) {
            return traverseMissingFields(fromOrdinals, toOrdinals);
        }

        clearHashTable();

        for(int i = 0; i < fromOrdinals.size(); i++) {
            indexFromOrdinal(fromOrdinals.get(i));
        }

        for(int i = 0; i < toOrdinals.size(); i++) {
            compareToOrdinal(toOrdinals.get(i));
        }

        int score = unmatchedToFields;
        for(int i = 0; i < ordinalHashCounts.length; i++) {
            score += ordinalHashCounts[i];
        }

        if(score != 0) {
            fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, score);
        }

        return score;
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        if(fromFieldIndex == -1) {
            fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, toOrdinals.size());
            return toOrdinals.size();
        }

        if(toFieldIndex == -1) {
            fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, fromOrdinals.size());
            return fromOrdinals.size();
        }

        return 0;
    }

    private void clearHashTable() {
        Arrays.fill(hashedOrdinals, -1);
        Arrays.fill(ordinalHashCounts, 0);
        unmatchedToFields = 0;
        hashSize = 0;
    }


    private void indexFromOrdinal(int ordinal) {
        if(hashSize == hashSizeBeforeGrow) {
            growHashTable();
        }

        int hashCode = fieldHashCode(fromState, ordinal, fromFieldIndex);
        if(hashIntoArray(ordinal, hashCode, 1, hashedOrdinals, ordinalHashCodes, ordinalHashCounts))
            hashSize++;
    }

    private void compareToOrdinal(int ordinal) {
        int hashCode = fieldHashCode(toState, ordinal, toFieldIndex);

        int bucket = hashCode & (hashedOrdinals.length - 1);

        while(hashedOrdinals[bucket] != -1) {
            /// check to see if this is an equal value.
            if(fieldsAreEqual(fromState, hashedOrdinals[bucket], fromFieldIndex, toState, ordinal, toFieldIndex)) {
                if(ordinalHashCounts[bucket] > 0) {
                    ordinalHashCounts[bucket]--;
                } else {
                    unmatchedToFields++;
                }

                return;
            }

            bucket = (bucket + 1) & (hashedOrdinals.length - 1);
        }

        unmatchedToFields++;
    }

    private void growHashTable() {
        int newHashedOrdinals[] = new int[hashedOrdinals.length * 2];
        int newOrdinalHashCodes[] = new int[ordinalHashCodes.length * 2];
        int newOrdinalHashCodeCounts[] = new int[ordinalHashCounts.length * 2];
        Arrays.fill(newHashedOrdinals, -1);

        long ordinalsAndHashCodes[] = ordinalsAndHashCodes();

        for(int i = 0; i < ordinalsAndHashCodes.length; i++) {
            int hashOrdinal = (int) (ordinalsAndHashCodes[i] >> 32);
            int hashCode = (int) ordinalsAndHashCodes[i];
            int hashCount = findOrdinalCount(hashOrdinal, hashCode);
            hashIntoArray(hashOrdinal, hashCode, hashCount, newHashedOrdinals, newOrdinalHashCodes, newOrdinalHashCodeCounts);
        }

        hashedOrdinals = newHashedOrdinals;
        ordinalHashCodes = newOrdinalHashCodes;
        ordinalHashCounts = newOrdinalHashCodeCounts;
        hashSizeBeforeGrow = newHashedOrdinals.length * 7 / 10;
    }

    private long[] ordinalsAndHashCodes() {
        long ordinalsAndHashCodes[] = new long[hashSize];

        int count = 0;

        for(int i = 0; i < hashedOrdinals.length; i++) {
            if(hashedOrdinals[i] != -1)
                ordinalsAndHashCodes[count++] = ((long) hashedOrdinals[i] << 32) | (ordinalHashCodes[i] & 0xFFFFFFFFL);
        }

        Arrays.sort(ordinalsAndHashCodes);

        return ordinalsAndHashCodes;
    }

    private int findOrdinalCount(int ordinal, int hashCode) {
        int bucket = hashCode & (hashedOrdinals.length - 1);

        while(hashedOrdinals[bucket] != ordinal)
            bucket = (bucket + 1) & (hashedOrdinals.length - 1);

        return ordinalHashCounts[bucket];
    }

    private boolean hashIntoArray(int ordinal, int hashCode, int count, int hashedOrdinals[], int ordinalHashCodes[], int ordinalHashCounts[]) {
        int bucket = hashCode & (hashedOrdinals.length - 1);

        while(hashedOrdinals[bucket] != -1) {
            /// check to see if this is an equal value.
            if(fieldsAreEqual(fromState, hashedOrdinals[bucket], fromFieldIndex, fromState, ordinal, fromFieldIndex)) {
                ordinalHashCounts[bucket]++;
                return false;
            }

            bucket = (bucket + 1) & (hashedOrdinals.length - 1);
        }

        hashedOrdinals[bucket] = ordinal;
        ordinalHashCodes[bucket] = hashCode;
        ordinalHashCounts[bucket] = count;
        return true;
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        if(fieldDiff.getTotalDiffScore() > 0)
            return Collections.singletonList(fieldDiff);
        return Collections.emptyList();
    }

}
