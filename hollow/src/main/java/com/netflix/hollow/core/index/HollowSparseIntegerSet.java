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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Create hollow integer set for sparse non-negative {@literal &} unique integer values referenced by fieldPath in a type based on a predicate.
 */
public class HollowSparseIntegerSet implements HollowTypeStateListener {

    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final FieldPath fieldPath;
    private final IndexPredicate predicate;

    protected volatile SparseBitSet sparseBitSetVolatile;

    private Set<Integer> valuesToSet;
    private Set<Integer> valuesToClear;
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
     * @param readStateEngine the read state
     * @param type the type name
     * @param fieldPath the field path
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

    protected void build() {

        // initialize an instance of SparseBitSet
        initSet(Integer.MAX_VALUE);

        // iterate through all populated ordinals for the type to set the values based on predicate
        BitSet typeBitSet = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = typeBitSet.nextSetBit(0);
        while (ordinal != -1) {
            set(ordinal);
            ordinal = typeBitSet.nextSetBit(ordinal + 1);
        }

        // run compaction
        compact();
    }

    protected void initSet(int maxValue) {
        sparseBitSetVolatile = new SparseBitSet(maxValue);
    }

    protected void set(int ordinal) {
        if (predicate.shouldIndex(ordinal)) {
            Object[] values = fieldPath.findValues(ordinal);
            if (values != null && values.length > 0) {
                SparseBitSet bitSet = sparseBitSetVolatile;
                for (Object value : values) {
                    bitSet.set((int) value);
                }
            }
        }
    }

    protected void compact() {
        SparseBitSet current = sparseBitSetVolatile;
        SparseBitSet compactedSet = SparseBitSet.compact(current);
        sparseBitSetVolatile = compactedSet;
    }


    /**
     * Check if the given value is contained in the set (or if the given value satisfies the predicate condition.)
     *
     * @param i the integer value
     * @return {@code true} if the value is present
     */
    public boolean get(int i) {
        SparseBitSet current;
        boolean result;
        do {
            current = sparseBitSetVolatile;
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
        SparseBitSet current;
        long size;
        do {
            current = sparseBitSetVolatile;
            size = current.estimateBitsUsed();
        } while (current != sparseBitSetVolatile);
        return size;
    }

    /**
     * @return the total number of integers added to the set.
     */
    public int cardinality() {
        SparseBitSet current;
        int cardinality;
        do {
            current = sparseBitSetVolatile;
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
        boolean didSomeWork = false;
        SparseBitSet updated = sparseBitSetVolatile;
        // first check if the max value among the new values to be added is more than the max value of the existing sparse bit set.
        if (valuesToSet.size() > 0 && maxValueToSet > updated.findMaxValue()) {
            updated = SparseBitSet.resize(updated, maxValueToSet);
            didSomeWork = true;
        }

        // when applying delta, check for duplicates, increment counts if duplicate values are found else set them
        for (int value : valuesToSet) {
            updated.set(value);
        }

        // first clear all the values that are meant to be cleared
        for (int value : valuesToClear) {
            updated.clear(value);
        }

        if (didSomeWork) {
            sparseBitSetVolatile = updated;
        }
    }

    /**
     * This implementation is motivated from several ideas to get a compact sparse set.
     * When using a a bucket of BitSet, problems
     * - smaller sizes of BitSet are not useful, since null references are themselves 64/32 bit references.
     * - larger sizes of BitSet for truly sparse integers, has overhead of too many zeroes in one BitSet.
     * <p>
     * The idea is to only store longs in bb that have non-zero values where bucket sizes are longs. Bucket size of 64 longs are convenient when using mod operations.
     * <p>
     * Each bit in long value in indices array, indicates if a long value is initialized. 64 bits would point to 64 long values ( 1 bucket ).
     * Each bucket could contain 1-64 longs, we only hold non-zero long values in bucket.
     */
    static class SparseBitSet {

        // shift used to determine which bucket
        private static final int BUCKET_SHIFT = 12;
        // shift used to determine which Long value to use in bucket.
        private static final int LONG_SHIFT = 6;

        private final int maxValue;
        private final AtomicReferenceArray<Bucket> buckets;

        private static class Bucket {
            private long idx;
            private long[] longs;

            private Bucket(long idx, long[] longs) {
                this.idx = idx;
                this.longs = longs;
            }
        }

        SparseBitSet(int maxValue) {
            int totalBuckets = maxValue >>> BUCKET_SHIFT;
            this.maxValue = maxValue;
            this.buckets = new AtomicReferenceArray<>(totalBuckets + 1);
        }

        private SparseBitSet(int maxValue, AtomicReferenceArray<Bucket> buckets) {
            this.maxValue = maxValue;
            this.buckets = buckets;
        }

        private static int getIndex(int i) {
            // logical right shift
            return i >>> BUCKET_SHIFT;
        }


        /**
         * This method returns the number of Longs initialized from LSB to the given bitInIndex.
         * For example longAtIndex (64 bits) = 00...1001 and bitInIndex (3rd bit is set) 000...100
         * then this method will return 1 since only one bit is set in longAtIndex to the right of bitInIndex
         *
         * @param longAtIndex
         * @param bitInIndex
         * @return
         */
        private static int getOffset(long longAtIndex, long bitInIndex) {
            // set all bits to one before the bit that is set in bitInIndex
            // example : 000...0100 will become 000...011
            long setAllOnesBeforeBitInIndex = bitInIndex - 1;
            long offset = (longAtIndex & setAllOnesBeforeBitInIndex);
            return Long.bitCount(offset);
        }

        boolean get(int i) {
            if (i > maxValue || i < 0)
                return false;

            int index = getIndex(i);
            Bucket currentBucket = buckets.get(index);
            if (currentBucket == null) return false;

            long currentLongAtIndex = currentBucket.idx;
            long[] longs = currentBucket.longs;

            // find which bit in index will point to the long in bb
            long whichLong = i >>> LONG_SHIFT;
            long bitInIndex = 1L << whichLong;// whichLong % 64

            long isLongInitialized = (currentLongAtIndex & bitInIndex);
            if (isLongInitialized == 0) return false;

            int offset = getOffset(currentLongAtIndex, bitInIndex);
            long value = longs[offset];
            long whichBitInLong = 1L << i;

            return (value & whichBitInLong) != 0;
        }

        // thread-safe
        void set(int i) {

            if (i > maxValue)
                throw new IllegalArgumentException("Max value initialized is " + maxValue + " given value is " + i);
            if (i < 0) throw new IllegalArgumentException("Cannot index negative numbers");

            // find which bucket
            int index = getIndex(i);

            // find which bit in index will point to the long in bb
            long whichLong = i >>> LONG_SHIFT;
            long bitInIndex = 1L << whichLong;// whichLong % 64
            long whichBitInLong = 1L << i;// i % 64

            while (true) {

                long longAtIndex = 0;
                long[] longs = null;

                Bucket currentBucket = buckets.get(index);
                if (currentBucket != null) {
                    longAtIndex = currentBucket.idx;
                    longs = currentBucket.longs.clone();
                }

                boolean isLongInitialized = (longAtIndex & bitInIndex) != 0;
                if (isLongInitialized) {

                    // if a long value is set, the find the correct offset to determine which long in longs to use.
                    int offset = getOffset(longAtIndex, bitInIndex);
                    longs[offset] |= whichBitInLong;// or preserves previous set operations in this long.

                } else if (longAtIndex == 0) {

                    // first set that bit in idx for that bucket, and assign a new long[]
                    longAtIndex = bitInIndex;
                    longs = new long[]{whichBitInLong};

                } else {

                    // update long value at index
                    longAtIndex |= bitInIndex;

                    // find offset
                    int offset = getOffset(longAtIndex, bitInIndex);

                    int oldLongsLen = longs.length;
                    long[] newLongs = new long[oldLongsLen + 1];

                    // if offset is 2 means 3 longs are needed starting from 0
                    // if current longs length is 2 (0,1) then append third long at end
                    // if current longs length is greater than offset, then insert long 0 -> (offset - 1), new long, offset to (length -1)
                    if (offset >= oldLongsLen) {
                        // append new long at end
                        int it;
                        for (it = 0; it < oldLongsLen; it++)
                            newLongs[it] = longs[it];
                        newLongs[it] = whichBitInLong;
                    } else {
                        // insert new long in between
                        int it;
                        for (it = 0; it < offset; it++)
                            newLongs[it] = longs[it];
                        newLongs[offset] = whichBitInLong;
                        for (it = offset; it < oldLongsLen; it++)
                            newLongs[it + 1] = longs[it];
                    }
                    longs = newLongs;
                }

                Bucket newBucket = new Bucket(longAtIndex, longs);
                if (buckets.compareAndSet(index, currentBucket, newBucket))
                    break;
            }
        }

        // thread-safe
        void clear(int i) {
            if (i > maxValue || i < 0) return;

            int index = getIndex(i);

            while (true) {
                Bucket currentBucket = buckets.get(index);
                if (currentBucket == null) return;
                long longAtIndex = currentBucket.idx;
                long[] longs = currentBucket.longs.clone();

                // find which bit in index will point to the long in bb
                long whichLong = i >>> LONG_SHIFT;
                long bitInIndex = 1L << whichLong;// whichLong % 64
                long whichBitInLong = 1L << i;// i % 64

                long isLongInitialized = (longAtIndex & bitInIndex);
                if (isLongInitialized == 0) return;

                int offset = getOffset(longAtIndex, bitInIndex);
                long value = longs[offset];

                // unset whichBitInIndex in value
                // to clear 3rd bit (00100 whichBitInLong) in 00101(value), & with 11011 to get 00001
                long updatedValue = value & ~whichBitInLong;
                boolean isBucketEmpty = false;
                if (updatedValue != 0) {
                    longs[offset] = updatedValue;
                } else {

                    // if updatedValue is 0, then update the bucket removing that long
                    int oldLongsLen = longs.length;
                    // if only one long was initialized in the bucket, then make the reference null, indexAtLong 0
                    if (oldLongsLen == 1) {
                        longs = null;
                        longAtIndex = 0;
                        isBucketEmpty = true;
                    } else {
                        // copy everything over, except the long at the given offset,

                        long[] newLongs = new long[oldLongsLen - 1];

                        int it;
                        for (it = 0; it < offset; it++)
                            newLongs[it] = longs[it];
                        it++;
                        while (it < oldLongsLen) {
                            newLongs[it - 1] = longs[it];
                            it++;
                        }

                        longs = newLongs;
                        longAtIndex &= ~bitInIndex;
                    }
                }
                Bucket updatedBucket = null;
                if (!isBucketEmpty) updatedBucket = new Bucket(longAtIndex, longs);
                if (buckets.compareAndSet(index, currentBucket, updatedBucket))
                    break;

            }
        }

        int findMaxValue() {
            // find the last index that is initialized
            int index = buckets.length() - 1;
            while (index >= 0) {
                if (buckets.get(index) != null) break;
                index--;
            }

            // if no buckets are initialized, then return -1 ( meaning set is empty)
            if (index < 0) return -1;

            // find the highest bit in indexAtLong to see which is last long init in bucket
            int highestBitSetInIndexAtLong = 63 - Long.numberOfLeadingZeros(Long.highestOneBit(buckets.get(index).idx));

            long[] longs = buckets.get(index).longs;
            long value = longs[longs.length - 1];
            long highestBitSetInLong = 63 - Long.numberOfLeadingZeros(Long.highestOneBit(value));

            return (int) ((index << BUCKET_SHIFT) + (highestBitSetInIndexAtLong << 6) + highestBitSetInLong);
        }

        int cardinality() {
            int cardinality = 0;
            int index = 0;
            while (index < buckets.length()) {

                if (buckets.get(index) != null) {
                    long[] longs = buckets.get(index).longs;
                    for (long value : longs)
                        cardinality += Long.bitCount(value);
                }
                index++;
            }
            return cardinality;
        }

        long estimateBitsUsed() {
            long longsUsed = 0;
            long idxCounts = 0;

            int index = 0;
            while (index < buckets.length()) {
                if (buckets.get(index) != null) {
                    idxCounts++;
                    longsUsed += buckets.get(index).longs.length;
                }
                index++;
            }

            // total bits used
            long bitsUsedByArrayPointers = buckets.length() * 64;
            long bitsUsedByIdx = idxCounts * 64;
            long bitsUsedByLongs = longsUsed * 64;

            return bitsUsedByArrayPointers + bitsUsedByIdx + bitsUsedByLongs;
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
            return copyWithNewLength(sparseBitSet, newLength, newLength, maxValueAdded);
        }

        static SparseBitSet resize(SparseBitSet sparseBitSet, int newMaxValue) {
            if (sparseBitSet.findMaxValue() < newMaxValue) {
                int indexForNewMaxValue = getIndex(newMaxValue);
                int newLength = indexForNewMaxValue + 1;
                return copyWithNewLength(sparseBitSet, newLength, sparseBitSet.buckets.length(), newMaxValue);
            }
            return sparseBitSet;
        }

        private static SparseBitSet copyWithNewLength(SparseBitSet sparseBitSet, int newLength, int lengthToClone, int newMaxValue) {
            AtomicReferenceArray<Bucket> compactBuckets = new AtomicReferenceArray<Bucket>(newLength);
            for (int i = 0; i < lengthToClone; i++) {
                if (sparseBitSet.buckets.get(i) != null) compactBuckets.set(i, sparseBitSet.buckets.get(i));
            }
            return new SparseBitSet(newMaxValue, compactBuckets);
        }

    }
}
