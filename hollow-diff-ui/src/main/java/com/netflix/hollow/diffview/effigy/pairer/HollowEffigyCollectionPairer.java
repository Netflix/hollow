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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;


public class HollowEffigyCollectionPairer extends HollowEffigyFieldPairer {

    private static final long[] EMPTY_DIFF_MATRIX = new long[0];
    static final int MAX_MATRIX_ELEMENT_FIELD_VALUE = 0x1FFFFF;

    private final long deadlineBeforePairingTimeout;

    public HollowEffigyCollectionPairer(HollowEffigy fromCollection, HollowEffigy toCollection, long deadlineBeforePairingTimeout) {
        super(fromCollection, toCollection);
        this.deadlineBeforePairingTimeout = deadlineBeforePairingTimeout;
    }

    /**
     * Finds the element pairings which have the minimum number of differences between them.
     */
    @Override
    public List<EffigyFieldPair> pair() {
        List<EffigyFieldPair> fieldPairs = new ArrayList<EffigyFieldPair>();

        BitSet pairedFromIndices = new BitSet(from.getFields().size());
        BitSet pairedToIndices = new BitSet(to.getFields().size());

        int maxDiffBackoff[] = new int[] {1, 2, 4, 8, Integer.MAX_VALUE};

        int maxPairs = Math.min(from.getFields().size(), to.getFields().size());

        for(int i=0;i<maxDiffBackoff.length && fieldPairs.size() < maxPairs && System.currentTimeMillis() < deadlineBeforePairingTimeout;i++) {

            long diffMatrixElements[] = pair(pairedFromIndices, pairedToIndices, maxDiffBackoff[i]);

            Arrays.sort(diffMatrixElements);

            for(long matrixElement : diffMatrixElements) {
                if(fieldPairs.size() == maxPairs)
                    break;

                int diffScore = getDiffScore(matrixElement);

                if(diffScore == MAX_MATRIX_ELEMENT_FIELD_VALUE)
                    break;

                int fromIndex = getFromIndex(matrixElement);
                int toIndex = getToIndex(matrixElement);

                if(pairedFromIndices.get(fromIndex))
                    continue;
                if(pairedToIndices.get(toIndex))
                    continue;

                fieldPairs.add(new EffigyFieldPair(from.getFields().get(fromIndex), to.getFields().get(toIndex), fromIndex, toIndex));
                pairedFromIndices.set(fromIndex);
                pairedToIndices.set(toIndex);
            }
        }

        if(System.currentTimeMillis() > deadlineBeforePairingTimeout) {
            HollowEffigy.Field fromField = new HollowEffigy.Field(new HollowDiffNodeIdentifier("TRUNCATED"), "PAIRING TIMEOUT");
            HollowEffigy.Field toField = new HollowEffigy.Field(new HollowDiffNodeIdentifier("TRUNCATED"), "ROWS OMITTED");
            fieldPairs.add(new EffigyFieldPair(fromField, toField, -1, -1));
        } else {
            for(int i=0;i<from.getFields().size();i++) {
                if(!pairedFromIndices.get(i))
                    fieldPairs.add(new EffigyFieldPair(from.getFields().get(i), null, i, -1));
            }

            for(int i=0;i<to.getFields().size();i++) {
                if(!pairedToIndices.get(i))
                    fieldPairs.add(new EffigyFieldPair(null, to.getFields().get(i), -1, i));
            }
        }

        return fieldPairs;

    }

    public long[] pair(final BitSet pairedFromIndices, final BitSet pairedToIndices, final int maxDiff) {
        if(System.currentTimeMillis() > deadlineBeforePairingTimeout)
            return EMPTY_DIFF_MATRIX;

        final long diffMatrixElements[] = new long[from.getFields().size() * to.getFields().size()];

        int matrixElementIdx = 0;

        for(int i=0;i<from.getFields().size();i++) {
            final int fromIdx = i;

            if(pairedFromIndices.get(fromIdx)) {
                for(int j=0;j<to.getFields().size();j++) {
                    diffMatrixElements[matrixElementIdx++] = getDiffMatrixElement(fromIdx, j, MAX_MATRIX_ELEMENT_FIELD_VALUE);
                }
            } else {
                HollowEffigy fromElement = getComparisonEffigy((HollowEffigy) from.getFields().get(fromIdx).getValue());
                HollowEffigyDiffRecord diffRecord = new HollowEffigyDiffRecord(fromElement);

                for(int j=0;j<to.getFields().size();j++) {
                    if(pairedToIndices.get(j) || System.currentTimeMillis() > deadlineBeforePairingTimeout) {
                        diffMatrixElements[matrixElementIdx++] = getDiffMatrixElement(fromIdx, j, MAX_MATRIX_ELEMENT_FIELD_VALUE);
                    } else {
                        HollowEffigy toElement = getComparisonEffigy((HollowEffigy) to.getFields().get(j).getValue());
                        int diffScore = diffRecord.calculateDiff(toElement, maxDiff);

                        diffMatrixElements[matrixElementIdx++] = getDiffMatrixElement(fromIdx, j, diffScore);
                    }
                }
            }
        }

        return diffMatrixElements;
    }

    protected HollowEffigy getComparisonEffigy(HollowEffigy effigy) {
        return effigy;
    }

    private long getDiffMatrixElement(int fromIndex, int toIndex, int diffScore) {
        return ((long)diffScore << 42) | ((long)fromIndex << 21) | ((long)toIndex);
    }

    private int getDiffScore(long diffMatrixElement) {
        return (int)((diffMatrixElement >> 42) & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

    private int getFromIndex(long diffMatrixElement) {
        return (int)((diffMatrixElement >> 21) & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

    private int getToIndex(long diffMatrixElement) {
        return (int)(diffMatrixElement & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

}
