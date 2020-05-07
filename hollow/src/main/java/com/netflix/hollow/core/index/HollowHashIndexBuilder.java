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
package com.netflix.hollow.core.index;

import static com.netflix.hollow.core.memory.FixedLengthData.bitsRequiredToRepresentValue;

import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.BitSet;

public class HollowHashIndexBuilder {

    private final HollowPreindexer preindexer;

    private final int[] bitsPerTraverserField;
    private final int[] offsetPerTraverserField;
    private final int bitsPerMatchHashKey;
    final int bitsPerSelectHashEntry;

    private final ArraySegmentRecycler memoryRecycler;

    private FixedLengthElementArray finalMatchHashTable;
    FixedLengthElementArray finalSelectHashArray;
    private long finalMatchHashMask;
    private int finalBitsPerMatchHashEntry;
    private int finalBitsPerSelectTableSize;
    private int finalBitsPerSelectTablePointer;

    private GrowingSegmentedLongArray matchIndexHashAndSizeArray;
    private FixedLengthElementArray intermediateMatchHashTable;
    private MultiLinkedElementArray intermediateSelectLists;
    private int intermediateMatchHashTableSize;
    private int bitsPerIntermediateListIdentifier;
    private int bitsPerIntermediateMatchHashEntry;
    private int intermediateMatchHashMask;
    private int intermediateMatchHashTableSizeBeforeGrow;
    private int matchCount;


    ///TODO: Optimization, make the matchFields[].schemaFieldPositionPath as short as possible, to reduce iteration
    /// this means merging the common roots of path from the same base field, and pushing all unique base fields down
    /// to the leaves.
    public HollowHashIndexBuilder(HollowReadStateEngine stateEngine, String type, String selectField, String... matchFields) {
        this.preindexer = new HollowPreindexer(stateEngine, type, selectField, matchFields);
        preindexer.buildFieldSpecifications();

        this.memoryRecycler = WastefulRecycler.DEFAULT_INSTANCE;

        HollowIndexerValueTraverser traverser = preindexer.getTraverser();

        this.bitsPerTraverserField = new int[traverser.getNumFieldPaths()];
        this.offsetPerTraverserField = new int[traverser.getNumFieldPaths()];

        int bitsPerMatchHashKey = 0;
        for(int i=0;i<traverser.getNumFieldPaths();i++) {
            int maxOrdinalForTypeState = ((HollowTypeReadState)traverser.getFieldTypeDataAccess(i)).maxOrdinal();
            bitsPerTraverserField[i] = bitsRequiredToRepresentValue(maxOrdinalForTypeState + 1);
            offsetPerTraverserField[i] = bitsPerMatchHashKey;
            if(i < preindexer.getNumMatchTraverserFields())
                bitsPerMatchHashKey += bitsPerTraverserField[i];
        }

        this.bitsPerMatchHashKey = bitsPerMatchHashKey;
        this.bitsPerSelectHashEntry = bitsPerTraverserField[preindexer.getSelectFieldSpec().getBaseIteratorFieldIdx()];
    }

    public void buildIndex() {
        matchIndexHashAndSizeArray = new GrowingSegmentedLongArray(memoryRecycler);

        BitSet populatedOrdinals = preindexer.getTypeState().getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        /// an initial guess at how big this table might be -- one match per top-level element.
        int guessNumberOfMatches = populatedOrdinals.cardinality();
        intermediateMatchHashTableSize = HashCodes.hashTableSize(guessNumberOfMatches);
        bitsPerIntermediateListIdentifier =  bitsRequiredToRepresentValue(intermediateMatchHashTableSize - 1);
        bitsPerIntermediateMatchHashEntry = bitsPerMatchHashKey + bitsPerIntermediateListIdentifier;

        intermediateMatchHashMask = intermediateMatchHashTableSize - 1;
        intermediateMatchHashTableSizeBeforeGrow = intermediateMatchHashTableSize * 7 / 10;
        matchCount = 0;

        /// a data structure which keeps canonical matches for comparison (the matchHashTable)
        intermediateMatchHashTable = new FixedLengthElementArray(memoryRecycler, (long)intermediateMatchHashTableSize * bitsPerIntermediateMatchHashEntry);

        /// a data structure which tracks lists of matches under canonical matches.
        intermediateSelectLists = new MultiLinkedElementArray(memoryRecycler);

        HollowIndexerValueTraverser traverser = preindexer.getTraverser();


        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            traverser.traverse(ordinal);

            for(int i=0;i<traverser.getNumMatches();i++) {
                int matchHash = getMatchHash(i);

                long bucket = matchHash & intermediateMatchHashMask;
                long hashBucketBit = bucket * bitsPerIntermediateMatchHashEntry;
                boolean bucketIsEmpty = intermediateMatchHashTable.getElementValue(hashBucketBit, bitsPerTraverserField[0]) == 0;
                long bucketMatchListIdx = intermediateMatchHashTable.getElementValue(hashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier);
                int bucketMatchHashCode = (int)matchIndexHashAndSizeArray.get(bucketMatchListIdx);

                while(!bucketIsEmpty && (bucketMatchHashCode != (matchHash & Integer.MAX_VALUE) || !intermediateMatchIsEqual(i, hashBucketBit))) {
                    bucket = (bucket + 1) & intermediateMatchHashMask;
                    hashBucketBit = bucket * bitsPerIntermediateMatchHashEntry;
                    bucketIsEmpty = intermediateMatchHashTable.getElementValue(hashBucketBit, bitsPerTraverserField[0]) == 0;
                    bucketMatchListIdx = intermediateMatchHashTable.getElementValue(hashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier);
                    bucketMatchHashCode = (int)matchIndexHashAndSizeArray.get(bucketMatchListIdx);
                }

                int matchListIdx;

                if(bucketIsEmpty) {
                    matchListIdx = intermediateSelectLists.newList();
                    for(int j=0;j<preindexer.getNumMatchTraverserFields();j++)
                        intermediateMatchHashTable.setElementValue(hashBucketBit + offsetPerTraverserField[j], bitsPerTraverserField[j], traverser.getMatchOrdinal(i, j) + 1);

                    intermediateMatchHashTable.setElementValue(hashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier, matchListIdx);

                    matchIndexHashAndSizeArray.set(matchListIdx, matchHash & Integer.MAX_VALUE);
                    matchCount++;

                    /// GROW IF NECESSARY!
                    if(matchCount > intermediateMatchHashTableSizeBeforeGrow) {
                        growIntermediateHashTable();
                    }

                } else {
                    matchListIdx = (int)intermediateMatchHashTable.getElementValue(hashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier);
                }

                intermediateSelectLists.add(matchListIdx, traverser.getMatchOrdinal(i, preindexer.getSelectFieldSpec().getBaseIteratorFieldIdx()));
            }




            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }


        /// turn those data structures into a compact one optimized for hash lookup
        long totalNumberOfSelectBucketsAndBitsRequiredForSelectTableSize = calculateDedupedSizesAndTotalNumberOfSelectBuckets(intermediateSelectLists, matchIndexHashAndSizeArray);
        long totalNumberOfSelectBuckets = totalNumberOfSelectBucketsAndBitsRequiredForSelectTableSize & 0xFFFFFFFFFFFFFFL;
        long totalNumberOfMatchBuckets = HashCodes.hashTableSize(matchCount);

        int bitsPerFinalSelectBucketPointer = bitsRequiredToRepresentValue(totalNumberOfSelectBuckets);
        int bitsPerSelectTableSize = (int)(totalNumberOfSelectBucketsAndBitsRequiredForSelectTableSize >>> 56);
        int finalBitsPerMatchHashEntry = bitsPerMatchHashKey + bitsPerSelectTableSize + bitsPerFinalSelectBucketPointer;

        FixedLengthElementArray finalMatchArray = new FixedLengthElementArray(memoryRecycler, totalNumberOfMatchBuckets * finalBitsPerMatchHashEntry);
        FixedLengthElementArray finalSelectArray = new FixedLengthElementArray(memoryRecycler, totalNumberOfSelectBuckets * bitsPerSelectHashEntry);

        long finalMatchHashMask = totalNumberOfMatchBuckets - 1;

        long currentSelectArrayBucket = 0;

        for(int i=0;i<matchCount;i++) {
            long matchIndexHashAndSize = matchIndexHashAndSizeArray.get(i);
            int matchIndexSize = (int)(matchIndexHashAndSize >> 32);
            int matchIndexTableSize = HashCodes.hashTableSize(matchIndexSize);
            int matchIndexBucketMask = matchIndexTableSize - 1;

            HollowOrdinalIterator selectOrdinalIter = intermediateSelectLists.iterator(i);
            int selectOrdinal = selectOrdinalIter.next();
            while(selectOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                int selectBucket = HashCodes.hashInt(selectOrdinal) & matchIndexBucketMask;
                int bucketOrdinal = (int)finalSelectArray.getElementValue((currentSelectArrayBucket + selectBucket) * bitsPerSelectHashEntry, bitsPerSelectHashEntry) - 1;
                while(bucketOrdinal != -1 && bucketOrdinal != selectOrdinal) {
                    ///TODO: If select field type is not REFERENCE, then we should dedup -- unless we are reference counting for delta application
                    ///ordinals here with the same value for the specified field.
                    selectBucket = (selectBucket + 1) & matchIndexBucketMask;
                    bucketOrdinal = (int)finalSelectArray.getElementValue((currentSelectArrayBucket + selectBucket) * bitsPerSelectHashEntry, bitsPerSelectHashEntry) - 1;
                }

                if(bucketOrdinal == -1)
                    finalSelectArray.setElementValue((currentSelectArrayBucket + selectBucket) * bitsPerSelectHashEntry, bitsPerSelectHashEntry, selectOrdinal + 1);

                selectOrdinal = selectOrdinalIter.next();
            }

            long finalMatchIndexBucket = matchIndexHashAndSize & finalMatchHashMask;
            long finalMatchIndexBucketBit = finalMatchIndexBucket * finalBitsPerMatchHashEntry;

            while(finalMatchArray.getElementValue(finalMatchIndexBucketBit, bitsPerTraverserField[0]) != 0) {
                finalMatchIndexBucket = (finalMatchIndexBucket + 1) & finalMatchHashMask;
                finalMatchIndexBucketBit = finalMatchIndexBucket * finalBitsPerMatchHashEntry;
            }

            long intermediateMatchHashBucket = matchIndexHashAndSize & intermediateMatchHashMask;
            long intermediateMatchIndexBucketBit = intermediateMatchHashBucket * bitsPerIntermediateMatchHashEntry;
            while(intermediateMatchHashTable.getElementValue(intermediateMatchIndexBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier) != i) {
                intermediateMatchHashBucket = (intermediateMatchHashBucket + 1) & intermediateMatchHashMask;
                intermediateMatchIndexBucketBit = intermediateMatchHashBucket * bitsPerIntermediateMatchHashEntry;
            }

            if(bitsPerMatchHashKey < 56) {
                long matchHashKey = intermediateMatchHashTable.getElementValue(intermediateMatchIndexBucketBit, bitsPerMatchHashKey);
                finalMatchArray.setElementValue(finalMatchIndexBucketBit, bitsPerMatchHashKey, matchHashKey);
            } else {
                finalMatchArray.copyBits(intermediateMatchHashTable, intermediateMatchIndexBucketBit, finalMatchIndexBucketBit, bitsPerMatchHashKey);
            }

            finalMatchArray.setElementValue(finalMatchIndexBucketBit + bitsPerMatchHashKey, bitsPerSelectTableSize, matchIndexSize);
            finalMatchArray.setElementValue(finalMatchIndexBucketBit + bitsPerMatchHashKey + bitsPerSelectTableSize, bitsPerFinalSelectBucketPointer, currentSelectArrayBucket);

            currentSelectArrayBucket += matchIndexTableSize;
        }

        this.finalMatchHashTable = finalMatchArray;
        this.finalSelectHashArray = finalSelectArray;
        this.finalBitsPerMatchHashEntry = finalBitsPerMatchHashEntry;
        this.finalBitsPerSelectTablePointer = bitsPerFinalSelectBucketPointer;
        this.finalBitsPerSelectTableSize = bitsPerSelectTableSize;
        this.finalMatchHashMask = finalMatchHashMask;
    }

    private void growIntermediateHashTable() {
        int newMatchHashTableSize = intermediateMatchHashTableSize * 2;
        int newMatchHashMask = newMatchHashTableSize - 1;
        int newBitsForListIdentifier = bitsRequiredToRepresentValue(newMatchHashTableSize - 1);
        int newBitsPerMatchHashEntry = bitsPerMatchHashKey + newBitsForListIdentifier;
        FixedLengthElementArray newMatchHashTable = new FixedLengthElementArray(memoryRecycler, (long)newMatchHashTableSize * newBitsPerMatchHashEntry);

        for(int j=0;j<matchCount;j++) {
            int rehashCode = (int)matchIndexHashAndSizeArray.get(j);
            long oldHashBucket = rehashCode & intermediateMatchHashMask;
            long oldHashBucketBit = oldHashBucket * bitsPerIntermediateMatchHashEntry;

            while(intermediateMatchHashTable.getElementValue(oldHashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier) != j) {
                oldHashBucket = (oldHashBucket+1) & intermediateMatchHashMask;
                oldHashBucketBit = oldHashBucket * bitsPerIntermediateMatchHashEntry;
            }

            long rehashBucket = rehashCode & newMatchHashMask;
            long rehashBucketBit = rehashBucket * newBitsPerMatchHashEntry;
            boolean rehashBucketIsEmpty = newMatchHashTable.getElementValue(rehashBucketBit, bitsPerTraverserField[0]) == 0;

            while(!rehashBucketIsEmpty) {
                rehashBucket = (rehashBucket + 1) & newMatchHashMask;
                rehashBucketBit = rehashBucket * newBitsPerMatchHashEntry;
                rehashBucketIsEmpty = newMatchHashTable.getElementValue(rehashBucketBit, bitsPerTraverserField[0]) == 0;
            }

            if(bitsPerMatchHashKey < 56) {
                newMatchHashTable.setElementValue(rehashBucketBit, bitsPerMatchHashKey, intermediateMatchHashTable.getElementValue(oldHashBucketBit, bitsPerMatchHashKey));
            } else {
                newMatchHashTable.copyBits(intermediateMatchHashTable, oldHashBucketBit, rehashBucketBit, bitsPerMatchHashKey);
            }

            int listIdx = (int)intermediateMatchHashTable.getElementValue(oldHashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier);
            newMatchHashTable.setElementValue(rehashBucketBit + bitsPerMatchHashKey, bitsPerIntermediateListIdentifier, listIdx);
        }

        intermediateMatchHashTable.destroy(memoryRecycler);
        memoryRecycler.swap();

        intermediateMatchHashTable = newMatchHashTable;
        intermediateMatchHashTableSize = newMatchHashTableSize;
        intermediateMatchHashTableSizeBeforeGrow = intermediateMatchHashTableSize * 7 / 10;
        bitsPerIntermediateListIdentifier = newBitsForListIdentifier;
        bitsPerIntermediateMatchHashEntry = newBitsPerMatchHashEntry;
        intermediateMatchHashMask = newMatchHashMask;
    }

    /**
     * Called after initial pass.
     * Returns the sum total number of select buckets in the low 7 bytes, and the bits required for the max set size in the high 1 byte.
     */
    private long calculateDedupedSizesAndTotalNumberOfSelectBuckets(MultiLinkedElementArray elementArray, GrowingSegmentedLongArray matchIndexHashAndSizeArray) {
        long totalBuckets = 0;
        long maxSize = 0;
        int[] selectArray = new int[8];

        for(int i=0;i<elementArray.numLists();i++) {
            int listSize = elementArray.listSize(i);
            int setSize = 0;
            int predictedBuckets = HashCodes.hashTableSize(listSize);
            int hashMask = predictedBuckets - 1;
            if(predictedBuckets > selectArray.length)
                selectArray = new int[predictedBuckets];
            for(int j=0;j<predictedBuckets;j++)
                selectArray[j] = -1;

            HollowOrdinalIterator iter = elementArray.iterator(i);
            int selectOrdinal = iter.next();
            while(selectOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                int hash = HashCodes.hashInt(selectOrdinal);
                int bucket = hash & hashMask;

                while(true) {
                    if(selectArray[bucket] == selectOrdinal)
                        break;
                    if(selectArray[bucket] == -1) {
                        selectArray[bucket] = selectOrdinal;
                        setSize++;
                        break;
                    }

                    bucket = (bucket+1) & hashMask;
                }

                selectOrdinal = iter.next();
            }

            long matchIndexHashAndSize = matchIndexHashAndSizeArray.get(i);
            matchIndexHashAndSize |= (long)setSize << 32;
            matchIndexHashAndSizeArray.set(i, matchIndexHashAndSize);

            totalBuckets += HashCodes.hashTableSize(setSize);
            if(setSize > maxSize)
                maxSize = setSize;
        }

        return totalBuckets | (long)bitsRequiredToRepresentValue(maxSize) << 56;
    }

    private boolean intermediateMatchIsEqual(int matchIdx, long hashBucketBit) {
        for(int i=0;i<preindexer.getMatchFieldSpecs().length;i++) {
            HollowHashIndexField field = preindexer.getMatchFieldSpecs()[i];
            int matchOrdinal = preindexer.getTraverser().getMatchOrdinal(matchIdx, field.getBaseIteratorFieldIdx());
            int hashOrdinal = (int)intermediateMatchHashTable.getElementValue(hashBucketBit + offsetPerTraverserField[field.getBaseIteratorFieldIdx()], bitsPerTraverserField[field.getBaseIteratorFieldIdx()]) - 1;

            HollowTypeReadState readState = field.getBaseDataAccess();
            int[] fieldPath = field.getSchemaFieldPositionPath();

            if(fieldPath.length == 0) {
                if(matchOrdinal != hashOrdinal)
                    return false;
            } else {
                for(int j=0;j<fieldPath.length - 1;j++) {
                    HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                    readState = objectAccess.getSchema().getReferencedTypeState(fieldPath[j]);
                    matchOrdinal = objectAccess.readOrdinal(matchOrdinal, fieldPath[j]);
                    hashOrdinal = objectAccess.readOrdinal(hashOrdinal, fieldPath[j]);
                }

                if(matchOrdinal != hashOrdinal) {
                    HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                    int fieldIdx = fieldPath[fieldPath.length-1];
                    if(isAnyFieldNull(matchOrdinal, hashOrdinal) || !HollowReadFieldUtils.fieldsAreEqual(objectAccess, matchOrdinal, fieldIdx, objectAccess, hashOrdinal, fieldIdx))
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isAnyFieldNull(int matchOrdinal, int hashOrdinal) {
        return matchOrdinal == -1 || hashOrdinal == -1;
    }

    private int getMatchHash(int matchIdx) {
        int matchHash = 0;

        for(int i=0;i<preindexer.getMatchFieldSpecs().length;i++) {
            HollowHashIndexField field = preindexer.getMatchFieldSpecs()[i];
            int ordinal = preindexer.getTraverser().getMatchOrdinal(matchIdx, field.getBaseIteratorFieldIdx());
            HollowTypeReadState readState = field.getBaseDataAccess();
            int[] fieldPath = field.getSchemaFieldPositionPath();

            if(fieldPath.length == 0) {
                matchHash ^= HashCodes.hashInt(ordinal);
            } else {
                for(int j=0;j<fieldPath.length-1;j++) {
                    HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                    readState = objectAccess.getSchema().getReferencedTypeState(fieldPath[j]);
                    ordinal = objectAccess.readOrdinal(ordinal, fieldPath[j]);
                }

                int fieldHashCode = ordinal == -1 ? -1 : HollowReadFieldUtils.fieldHashCode((HollowObjectTypeDataAccess) readState, ordinal, fieldPath[fieldPath.length-1]);
                matchHash ^= HashCodes.hashInt(fieldHashCode);
            }
        }

        return matchHash;
    }

    public int getBitsPerMatchHashKey() {
        return bitsPerMatchHashKey;
    }

    public FixedLengthElementArray getFinalMatchHashTable() {
        return finalMatchHashTable;
    }

    public long getFinalMatchHashMask() {
        return finalMatchHashMask;
    }

    public int getFinalBitsPerMatchHashEntry() {
        return finalBitsPerMatchHashEntry;
    }

    public int getFinalBitsPerSelectTableSize() {
        return finalBitsPerSelectTableSize;
    }

    public int getFinalBitsPerSelectTablePointer() {
        return finalBitsPerSelectTablePointer;
    }

    public FixedLengthElementArray getFinalSelectHashArray() {
        return finalSelectHashArray;
    }

    public HollowHashIndexField getSelectField() {
        return preindexer.getSelectFieldSpec();
    }

    public HollowHashIndexField[] getMatchFields() {
        return preindexer.getMatchFieldSpecs();
    }

    public int[] getBitsPerTraverserField() {
        return bitsPerTraverserField;
    }

    public int[] getOffsetPerTraverserField() {
        return offsetPerTraverserField;
    }

    public int getBitsPerSelectHashEntry() {
        return bitsPerSelectHashEntry;
    }
}
