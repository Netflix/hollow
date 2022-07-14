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
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.diffview.effigy.HollowEffigy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;


public class HollowEffigyCollectionPairer extends HollowEffigyFieldPairer {

    static final int MAX_MATRIX_ELEMENT_FIELD_VALUE = 0x1FFFFF;

    private final PrimaryKey matchHint;

    public HollowEffigyCollectionPairer(HollowEffigy fromCollection, HollowEffigy toCollection, PrimaryKey matchHint) {
        super(fromCollection, toCollection);
        this.matchHint = matchHint;
    }

    @Override
    public List<EffigyFieldPair> pair() {
        if(matchHint != null)
            return pairByMatchHint();
        return pairByMinDifference();
    }


    private List<EffigyFieldPair> pairByMatchHint() {
        List<EffigyFieldPair> fieldPairs = new ArrayList<EffigyFieldPair>();

        if(from.getFields().size() == 0) {
            for(int i = 0; i < to.getFields().size(); i++)
                fieldPairs.add(new EffigyFieldPair(null, to.getFields().get(i), -1, i));
            return fieldPairs;
        } else if(to.getFields().size() == 0) {
            for(int i = 0; i < from.getFields().size(); i++)
                fieldPairs.add(new EffigyFieldPair(from.getFields().get(i), null, i, -1));
        }

        int toFieldPathIndexes[][] = new int[matchHint.numFields()][];
        int fromFieldPathIndexes[][] = new int[matchHint.numFields()][];

        for(int i = 0; i < matchHint.numFields(); i++) {
            toFieldPathIndexes[i] = matchHint.getFieldPathIndex(to.getDataAccess().getDataAccess(), i);
            fromFieldPathIndexes[i] = matchHint.getFieldPathIndex(from.getDataAccess().getDataAccess(), i);
        }

        int hashedToFieldIndexes[] = new int[HashCodes.hashTableSize(to.getFields().size())];
        Arrays.fill(hashedToFieldIndexes, -1);

        for(int i = 0; i < to.getFields().size(); i++) {
            HollowEffigy comparisonEffigy = getComparisonEffigy((HollowEffigy) to.getFields().get(i).getValue());
            int hash = hashCode(comparisonEffigy, toFieldPathIndexes);
            hash &= hashedToFieldIndexes.length - 1;
            while(hashedToFieldIndexes[hash] != -1) {
                hash++;
                hash &= hashedToFieldIndexes.length - 1;
            }

            hashedToFieldIndexes[hash] = i;
        }

        BitSet matchedToElements = new BitSet(to.getFields().size());
        BitSet matchedFromElements = new BitSet(from.getFields().size());

        for(int i = 0; i < from.getFields().size(); i++) {
            HollowEffigy fromEffigy = getComparisonEffigy((HollowEffigy) from.getFields().get(i).getValue());
            int hash = hashCode(fromEffigy, fromFieldPathIndexes);
            hash &= hashedToFieldIndexes.length - 1;
            while(hashedToFieldIndexes[hash] != -1) {
                int toIdx = hashedToFieldIndexes[hash];
                if(!matchedToElements.get(toIdx)) {
                    HollowEffigy toEffigy = getComparisonEffigy((HollowEffigy) to.getFields().get(toIdx).getValue());
                    if(recordsMatch(fromEffigy, toEffigy, fromFieldPathIndexes, toFieldPathIndexes)) {
                        fieldPairs.add(new EffigyFieldPair(from.getFields().get(i), to.getFields().get(toIdx), i, toIdx));

                        matchedFromElements.set(i);
                        matchedToElements.set(toIdx);
                    }
                }

                hash++;
                hash &= hashedToFieldIndexes.length - 1;
            }
        }

        addUnmatchedElements(fieldPairs, matchedFromElements, matchedToElements);

        return fieldPairs;
    }

    private boolean recordsMatch(HollowEffigy fromElement, HollowEffigy toElement, int[][] fromFieldPathIndexes, int[][] toFieldPathIndexes) {
        for(int i = 0; i < fromFieldPathIndexes.length; i++) {
            if(!fieldsAreEqual(fromElement, toElement, fromFieldPathIndexes[i], toFieldPathIndexes[i]))
                return false;
        }

        return true;
    }

    private boolean fieldsAreEqual(HollowEffigy fromElement, HollowEffigy toElement, int[] fromFieldPath, int[] toFieldPath) {
        HollowObjectTypeDataAccess fromDataAccess = (HollowObjectTypeDataAccess) fromElement.getDataAccess();
        int fromOrdinal = fromElement.getOrdinal();
        HollowObjectTypeDataAccess toDataAccess = (HollowObjectTypeDataAccess) toElement.getDataAccess();
        int toOrdinal = toElement.getOrdinal();
        HollowObjectSchema fromSchema = fromDataAccess.getSchema();
        HollowObjectSchema toSchema = toDataAccess.getSchema();

        for(int i = 0; i < fromFieldPath.length - 1; i++) {
            int fromFieldPosition = fromFieldPath[i];
            int toFieldPosition = toFieldPath[i];
            fromOrdinal = fromDataAccess.readOrdinal(fromOrdinal, fromFieldPosition);
            toOrdinal = toDataAccess.readOrdinal(toOrdinal, toFieldPosition);
            fromDataAccess = (HollowObjectTypeDataAccess) fromDataAccess.getDataAccess().getTypeDataAccess(fromSchema.getReferencedType(fromFieldPosition));
            toDataAccess = (HollowObjectTypeDataAccess) toDataAccess.getDataAccess().getTypeDataAccess(toSchema.getReferencedType(toFieldPosition));
            fromSchema = fromDataAccess.getSchema();
            toSchema = toDataAccess.getSchema();
        }

        return HollowReadFieldUtils.fieldsAreEqual(fromDataAccess, fromOrdinal, fromFieldPath[fromFieldPath.length - 1], toDataAccess, toOrdinal, toFieldPath[toFieldPath.length - 1]);
    }

    private int hashCode(HollowEffigy element, int[][] fieldPathIndexes) {
        int hash = 0;

        for(int i = 0; i < fieldPathIndexes.length; i++) {
            hash = hash * 31;
            hash ^= fieldHashCode(element, fieldPathIndexes[i]);
        }

        return hash;
    }

    private int fieldHashCode(HollowEffigy element, int[] fieldPath) {
        HollowObjectTypeDataAccess dataAccess = (HollowObjectTypeDataAccess) element.getDataAccess();
        HollowObjectSchema schema = dataAccess.getSchema();
        int ordinal = element.getOrdinal();

        for(int i = 0; i < fieldPath.length - 1; i++) {
            int fieldPosition = fieldPath[i];
            ordinal = dataAccess.readOrdinal(ordinal, fieldPosition);
            dataAccess = (HollowObjectTypeDataAccess) dataAccess.getDataAccess().getTypeDataAccess(schema.getReferencedType(fieldPosition));
            schema = dataAccess.getSchema();
        }

        int fieldHash = HollowReadFieldUtils.fieldHashCode(dataAccess, ordinal, fieldPath[fieldPath.length - 1]);
        return HashCodes.hashInt(fieldHash);
    }

    /**
     * Finds the element pairings which have the minimum number of differences between them.
     */
    private List<EffigyFieldPair> pairByMinDifference() {
        List<EffigyFieldPair> fieldPairs = new ArrayList<EffigyFieldPair>();

        BitSet pairedFromIndices = new BitSet(from.getFields().size());
        BitSet pairedToIndices = new BitSet(to.getFields().size());

        int maxDiffBackoff[] = new int[]{1, 2, 4, 8, Integer.MAX_VALUE};

        int maxPairs = Math.min(from.getFields().size(), to.getFields().size());

        for(int i = 0; i < maxDiffBackoff.length && fieldPairs.size() < maxPairs; i++) {

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

        addUnmatchedElements(fieldPairs, pairedFromIndices, pairedToIndices);

        return fieldPairs;

    }

    private void addUnmatchedElements(List<EffigyFieldPair> fieldPairs, BitSet pairedFromIndices, BitSet pairedToIndices) {
        for(int i = 0; i < from.getFields().size(); i++) {
            if(!pairedFromIndices.get(i))
                fieldPairs.add(new EffigyFieldPair(from.getFields().get(i), null, i, -1));
        }

        for(int i = 0; i < to.getFields().size(); i++) {
            if(!pairedToIndices.get(i))
                fieldPairs.add(new EffigyFieldPair(null, to.getFields().get(i), -1, i));
        }
    }

    public long[] pair(final BitSet pairedFromIndices, final BitSet pairedToIndices, final int maxDiff) {
        final long diffMatrixElements[] = new long[from.getFields().size() * to.getFields().size()];

        int matrixElementIdx = 0;

        for(int i = 0; i < from.getFields().size(); i++) {
            final int fromIdx = i;

            if(pairedFromIndices.get(fromIdx)) {
                for(int j = 0; j < to.getFields().size(); j++) {
                    diffMatrixElements[matrixElementIdx++] = getDiffMatrixElement(fromIdx, j, MAX_MATRIX_ELEMENT_FIELD_VALUE);
                }
            } else {
                HollowEffigy fromElement = getComparisonEffigy((HollowEffigy) from.getFields().get(fromIdx).getValue());
                HollowEffigyDiffRecord diffRecord = new HollowEffigyDiffRecord(fromElement);

                for(int j = 0; j < to.getFields().size(); j++) {
                    if(pairedToIndices.get(j)) {
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
        return ((long) diffScore << 42) | ((long) fromIndex << 21) | ((long) toIndex);
    }

    private int getDiffScore(long diffMatrixElement) {
        return (int) ((diffMatrixElement >> 42) & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

    private int getFromIndex(long diffMatrixElement) {
        return (int) ((diffMatrixElement >> 21) & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

    private int getToIndex(long diffMatrixElement) {
        return (int) (diffMatrixElement & MAX_MATRIX_ELEMENT_FIELD_VALUE);
    }

}
