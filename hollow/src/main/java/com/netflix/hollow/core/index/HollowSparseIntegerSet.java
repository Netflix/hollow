/*
*
*  Copyright 2017 Netflix, Inc.
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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Create hollow integer set for sparse non-negative & unique integer values referenced by fieldPath in a type based on a predicate.
 */
public class HollowSparseIntegerSet implements HollowTypeStateListener {

    private final static Logger log = Logger.getLogger(HollowSparseIntegerSet.class.getName());

    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final FieldPath fieldPath;
    private final IndexPredicate predicate;

    protected SparseBitSet sparseBitSet;
    protected volatile SparseBitSet sparseBitSetVolatile;

    private Set<Integer> valuesToSet;
    private Set<Integer> valuesToClear;
    private Map<Integer, Integer> duplicateValues;
    private int maxValueToSet;

    public interface IndexPredicate {
        boolean shouldIndex(int ordinal);
    }

    private final static IndexPredicate DEFAULT_PREDICATE = new IndexPredicate() {
        @Override
        public boolean shouldIndex(int ordinal) {
            return true;
        }
    };

    /**
     * Create a index for integer values pointed by the given field path.
     *
     * @param readStateEngine
     * @param type
     * @param fieldPath
     */
    public HollowSparseIntegerSet(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        this(readStateEngine, type, fieldPath, DEFAULT_PREDICATE);
    }

    /**
     * Create a index for integer values based on the given IndexPredicate.
     *
     * @param readStateEngine HollowReadStateEngine to read data set.
     * @param type            type which contains the path to integer values for indexing.
     * @param fieldPath       path to the integer values
     * @param predicate       implementation of IndexPredicate, indicating if the record passes the condition for indexing.
     */
    public HollowSparseIntegerSet(HollowReadStateEngine readStateEngine, String type, String fieldPath, IndexPredicate predicate) {

        // check arguments
        if (readStateEngine == null) throw new IllegalArgumentException("Read state engine cannot be null");
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (fieldPath == null || fieldPath.isEmpty())
            throw new IllegalArgumentException("fieldPath cannot be null or empty");

        this.readStateEngine = readStateEngine;
        this.type = type;
        this.fieldPath = new FieldPath(readStateEngine, type, fieldPath);
        this.predicate = predicate;
        this.valuesToSet = new HashSet<>();
        this.valuesToClear = new HashSet<>();
        build();
    }

    protected synchronized void build() {

        SparseBitSet set = new SparseBitSet(Integer.MAX_VALUE);
        BitSet typeBitSet = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = typeBitSet.nextSetBit(0);
        while (ordinal != -1) {
            // check predicate
            if (predicate.shouldIndex(ordinal)) {
                // get integer values
                Object[] values = fieldPath.findValues(ordinal);
                if (values != null && values.length > 0) {
                    for (Object value : values) {
                        set(set, (int) value);
                    }

                }
            }
            ordinal = typeBitSet.nextSetBit(ordinal + 1);
        }
        SparseBitSet compactedSet = compact(set);

        sparseBitSet = compactedSet;
        sparseBitSetVolatile = compactedSet;
    }

    protected void set(SparseBitSet set, int value) {
        if (!set.get(value)) set.set(value);
        else handleDuplicate(value);
    }

    protected SparseBitSet compact(SparseBitSet set) {
        return SparseBitSet.compact(set);
    }

    // although duplicates are not supported, adding a support to log and maintain a small map to handle rare cases.
    protected void handleDuplicate(int value) {
        if (duplicateValues == null) duplicateValues = new HashMap<>(16, 0.75f);
        if (!duplicateValues.containsKey(value)) duplicateValues.put(value, 0);

        int count = duplicateValues.get(value);
        duplicateValues.put(value, ++count);
        log.warning("Found duplicate value :" + value + " with duplicate count : " + count + ". This index is best used with unique values.");
    }

    /**
     * Check if the given value is contained in the set (or if the given value satisfies the predicate condition.)
     *
     * @param i
     * @return boolean value.
     */
    public boolean get(int i) {
        SparseBitSet current = sparseBitSet;
        boolean result;
        do {
            result = current.get(i);
        } while (current != sparseBitSetVolatile);
        return result;
    }

    /**
     * Estimate the total number of bits used to represent the integer set.
     *
     * @return Calculates the total number of bits used by longs in underlying data structure.
     */
    public long size() {
        SparseBitSet current = sparseBitSet;
        long size;
        do {
            size = current.estimateBitsUsed();
        } while (current != sparseBitSetVolatile);
        return size;
    }

    /**
     * Get total number of integers added to the set.
     *
     * @return
     */
    public int cardinality() {
        SparseBitSet current = sparseBitSet;
        int cardinality;
        do {
            cardinality = current.cardinality();
        } while (current != sparseBitSetVolatile);
        return cardinality;
    }

    /**
     * Use this method to keep the index updated with delta changes on the read state engine.
     * Remember to call detachFromDeltaUpdates to stop the delta changes.
     * NOTE: Each delta updates creates a new prefix index and swaps the new with current.
     */
    public void listenForDeltaUpdates() {
        readStateEngine.getTypeState(type).addListener(this);
    }

    /**
     * Stop delta updates for this index.
     */
    public void detachFromDeltaUpdates() {
        readStateEngine.getTypeState(type).removeListener(this);
    }

    @Override
    public void beginUpdate() {
        valuesToSet.clear();
        valuesToClear.clear();
        maxValueToSet = -1;
    }

    @Override
    public void addedOrdinal(int ordinal) {
        if (predicate.shouldIndex(ordinal)) {
            Object[] values = fieldPath.findValues(ordinal);
            for (Object value : values) {
                valuesToSet.add((int) value);
                if (maxValueToSet < (int) value) maxValueToSet = (int) value;
            }
        }
    }

    @Override
    public void removedOrdinal(int ordinal) {
        Object[] values = fieldPath.findValues(ordinal);
        for (Object value : values)
            valuesToClear.add((int) value);
    }

    @Override
    public void endUpdate() {
        boolean swap = false;
        SparseBitSet updated = sparseBitSet;
        // first check if the max value among the new values to be added is more than the max value of the existing sparse bit set.
        if (valuesToSet.size() > 0 && maxValueToSet > sparseBitSet.findMaxValue()) {
            updated = SparseBitSet.resize(sparseBitSet, maxValueToSet);
            swap = true;
        }

        // when applying delta, check for duplicates, increment counts if duplicate values are found else set them
        for (int value : valuesToSet) {
            if (updated.get(value)) handleDuplicate(value);
            else updated.set(value);
        }

        // first clear all the values that are meant to be cleared
        for (int value : valuesToClear) {
            if (duplicateValues != null && duplicateValues.containsKey(value)) {
                int count = duplicateValues.get(value);
                count--;
                if (count <= 1) {
                    duplicateValues.remove(value);
                    updated.clear(value);
                }
            } else {
                updated.clear(value);
            }

        }


        // switch the data structures
        if (swap) {
            sparseBitSet = updated;
            sparseBitSetVolatile = updated;
        }
    }

    /**
     * This implementation is motivated from several ideas to get a compact sparse set.
     * When using a a bucket of BitSet, problems
     * - smaller sizes of BitSet are not useful, since null references are themselves 64/32 bit references.
     * - larger sizes of BitSet for truly sparse integers, has overhead of too many zeroes in one BitSet.
     * <p>
     * The idea is to only store longs in buckets that have non-zero values where bucket sizes are longs. Bucket size of 64 longs are convenient when using mod operations.
     * <p>
     * Each bit in long value in indices array, indicates if a long value is initialized. 64 bits would point to 64 long values ( 1 bucket ).
     * Each bucket could contain 1-64 longs, we only hold non-zero long values in bucket.
     */
    static class SparseBitSet {

        // shift used to determine which bucket and index
        private static final int BUCKET_SHIFT = 12;
        // shift used to determine which Long value to use in bucket.
        private static final int LONG_SHIFT = 6;

        private final int maxValue;
        private final long[] indices;
        private final long[][] buckets;

        SparseBitSet(int maxValue) {
            this.maxValue = maxValue;

            int totalBuckets = maxValue >>> BUCKET_SHIFT;
            indices = new long[totalBuckets + 1];
            buckets = new long[totalBuckets + 1][];
        }

        private SparseBitSet(int maxValue, long[] indices, long[][] buckets) {
            this.maxValue = maxValue;
            this.indices = indices;
            this.buckets = buckets;
        }

        private static int getIndex(int i) {
            // logical right shift
            return i >>> BUCKET_SHIFT;
        }

        // This method returns the number of Longs initialized from LSB to the given bitInIndex.
        // example if longAtIndex (64 bits) 00...1001 and bitInIndex (3) 000...100
        // this method will return 1 since only one bit is set in index bits from left side to bitInIndex.
        private static int getOffset(long longAtIndex, long bitInIndex) {
            // set all bits to one before
            // example : 000... 100 will become 000...011
            long setAllOnesBeforeBitInIndex = bitInIndex - 1;
            long offset = (longAtIndex & setAllOnesBeforeBitInIndex);
            // count of one's set in index to right of bitInIndex -> indicating which long to use in buckets
            return Long.bitCount(offset);
        }

        boolean get(int i) {
            if (i > maxValue)
                throw new IllegalArgumentException("Max value initialized is " + maxValue + " given value is " + i);
            if (i < 0) return false;

            int index = getIndex(i);
            long longAtIndex = indices[index];

            // find which bit in index will point to the long in buckets
            long whichLong = i >>> LONG_SHIFT;
            long bitInIndex = 1L << whichLong;// whichLong % 64

            long isLongInitialized = (longAtIndex & bitInIndex);
            if (isLongInitialized == 0) return false;

            int offset = getOffset(longAtIndex, bitInIndex);
            long value = buckets[index][offset];
            long whichBitInLong = 1L << i;

            return (value & whichBitInLong) != 0;
        }

        void set(int i) {

            if (i > maxValue)
                throw new IllegalArgumentException("Max value initialized is " + maxValue + " given value is " + i);
            if (i < 0) throw new IllegalArgumentException("Cannot index negative numbers");

            int index = getIndex(i);
            long longAtIndex = indices[index];

            // find which bit in index will point to the long in buckets
            long whichLong = i >>> LONG_SHIFT;
            long bitInIndex = 1L << whichLong;// whichLong % 64

            long whichBitInLong = 1L << i;// i % 64

            // if that bit in index is set, means a Long value is initialized to set the (i % 64)
            boolean isLongInitialized = (longAtIndex & bitInIndex) != 0;

            if (isLongInitialized) {
                int offset = getOffset(longAtIndex, bitInIndex);
                buckets[index][offset] |= whichBitInLong;// or preserves previous set operations in this long.
            } else if (longAtIndex == 0) {
                // first set that bit in index long, so that index will be > 0
                indices[index] = bitInIndex;
                buckets[index] = new long[]{whichBitInLong};
            } else {
                // update long value at index
                indices[index] |= bitInIndex;
                longAtIndex = indices[index];

                // find offset
                int offset = getOffset(longAtIndex, bitInIndex);
                int totalLongsInitialized = buckets[index].length;

                final long[] oldLongs = buckets[index];
                long[] newLongs = new long[totalLongsInitialized + 1];

                // if offset is 2 means 3 longs are needed starting from 0
                // if current longs length is 2 (0,1) then append third long at end
                // if current longs length is greater than offset, then insert long 0 -> (offset - 1), new long, offset to (length -1)
                if (offset >= totalLongsInitialized) {
                    // append new long at end
                    int it;
                    for (it = 0; it < totalLongsInitialized; it++)
                        newLongs[it] = oldLongs[it];
                    newLongs[it] = whichBitInLong;
                } else {
                    // insert new long in between
                    int it;
                    for (it = 0; it < offset; it++)
                        newLongs[it] = oldLongs[it];
                    newLongs[offset] = whichBitInLong;
                    for (it = offset; it < totalLongsInitialized; it++)
                        newLongs[it + 1] = oldLongs[it];
                }
                buckets[index] = newLongs;
            }
        }

        void clear(int i) {
            if (i > maxValue)
                throw new IllegalArgumentException("Max value initialized is " + maxValue + " given value is " + i);
            if (i < 0) throw new IllegalArgumentException("Cannot index negative numbers");

            int index = getIndex(i);
            long longAtIndex = indices[index];

            // find which bit in index will point to the long in buckets
            long whichLong = i >>> LONG_SHIFT;
            long bitInIndex = 1L << whichLong;// whichLong % 64

            long whichBitInLong = 1L << i;// i % 64

            long isLongInitialized = (longAtIndex & bitInIndex);
            if (isLongInitialized == 0) return;

            int offset = getOffset(longAtIndex, bitInIndex);
            long value = buckets[index][offset];

            // unset whichBitInIndex in value
            // to clear 3rd bit (00100 whichBitInLong) in 00101(value), & with 11011 to get 00001
            long updatedValue = value & ~whichBitInLong;

            if (updatedValue != 0) {
                buckets[index][offset] = updatedValue;

            } else {

                // if updatedValue is 0, then update the bucket removing that long

                int totalLongsInitialized = buckets[index].length;
                // if only one long was initialized in the bucket, then make the reference null, indexAtLong 0
                if (totalLongsInitialized == 1) {
                    buckets[index] = null;
                    indices[index] = 0;
                } else {
                    // copy everything over, except the long at the given offset,
                    final long[] oldLongs = buckets[index];
                    long[] newLongs = new long[totalLongsInitialized - 1];

                    int it;
                    for (it = 0; it < offset; it++)
                        newLongs[it] = oldLongs[it];
                    it++;
                    while (it < totalLongsInitialized) {
                        newLongs[it - 1] = oldLongs[it];
                        it++;
                    }

                    buckets[index] = newLongs;

                    // and unset bit in indexAtLong to indicate that no long is initialized at that offset.
                    longAtIndex &= ~bitInIndex;
                    indices[index] = longAtIndex;
                }
            }

        }

        int findMaxValue() {
            // find the last index that is initialized
            int index = indices.length - 1;
            while (index >= 0) {
                if (indices[index] != 0) break;
                index--;
            }

            // if no buckets are initialized, then return -1 ( meaning set is empty)
            if (index < 0) return -1;

            // find the highest bit in indexAtLong to see which is last long init in bucket
            int highestBitSetInIndexAtLong = 63 - Long.numberOfLeadingZeros(Long.highestOneBit(indices[index]));

            long[] longs = buckets[index];
            long value = longs[longs.length - 1];
            long highestBitSetInLong = 63 - Long.numberOfLeadingZeros(Long.highestOneBit(value));

            return (int) ((index << BUCKET_SHIFT) + (highestBitSetInIndexAtLong << 6) + highestBitSetInLong);
        }

        int cardinality() {
            int cardinality = 0;
            int index = 0;
            while (index < indices.length) {
                if (indices[index] != 0) {
                    long[] longs = buckets[index];
                    for (long value : longs)
                        cardinality += Long.bitCount(value);
                }
                index++;
            }
            return cardinality;
        }

        long estimateBitsUsed() {
            long bucketsUsed = 0;
            long nullReferences = 0;
            int index = 0;
            while (index < indices.length) {
                if (indices[index] != 0) {
                    bucketsUsed += buckets[index].length;
                } else {
                    nullReferences++;
                }
                index++;
            }

            // total bits used
            // indices array
            long bitsUsedByIndices = indices.length * 64;
            long bitsUsedByBucketIndices = buckets.length * 64;
            long bitsUsedByBuckets = bucketsUsed * 64;
            long bitsUsedByNullReferences = nullReferences * 64;

            return bitsUsedByIndices + bitsUsedByBucketIndices + bitsUsedByBuckets + bitsUsedByNullReferences;
        }

        private long[] getIndices() {
            return indices;
        }

        private long[][] getBuckets() {
            return buckets;
        }

        /**
         * * Use this method to compact an existing SparseBitSet. Note any attempts to add a new value greater than the max value will result in exception.
         *
         * @param sparseBitSet
         * @return new SparseBitSet that is compact, does not hold null references beyond the max int value added in the given input.
         */
        static SparseBitSet compact(SparseBitSet sparseBitSet) {
            int maxValueAdded = sparseBitSet.findMaxValue();
            // if the given set is empty then compact the sparseBitSet to have only 1 bucket i.e. 64 longs
            if (maxValueAdded < 0) {
                maxValueAdded = (1 << BUCKET_SHIFT) - 1;
            }
            int indexForMaxValueAdded = getIndex(maxValueAdded);
            int newLength = indexForMaxValueAdded + 1;
            return cloneSparseBitSetWithNewLength(sparseBitSet, newLength, newLength, maxValueAdded);
        }

        static SparseBitSet resize(SparseBitSet sparseBitSet, int newMaxValue) {
            if (sparseBitSet.findMaxValue() < newMaxValue) {
                int indexForNewMaxValue = getIndex(newMaxValue);
                int newLength = indexForNewMaxValue + 1;
                return cloneSparseBitSetWithNewLength(sparseBitSet, newLength, sparseBitSet.getIndices().length, newMaxValue);
            }
            return sparseBitSet;
        }

        private static SparseBitSet cloneSparseBitSetWithNewLength(SparseBitSet sparseBitSet, int newLength, int lengthToClone, int newMaxValue) {
            long[] compactIndices = new long[newLength];
            System.arraycopy(sparseBitSet.getIndices(), 0, compactIndices, 0, lengthToClone);

            long[][] compactBuckets = new long[newLength][];
            System.arraycopy(sparseBitSet.getBuckets(), 0, compactBuckets, 0, lengthToClone);

            return new SparseBitSet(newMaxValue, compactIndices, compactBuckets);
        }

    }
}