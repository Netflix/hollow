package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Create hollow integer set for a given fieldPath in a type based on a predicate.
 */
public class HollowIntSet implements HollowTypeStateListener {

    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final FieldPath fieldPath;
    private final IndexPredicate predicate;

    private SparseBitSet sparseBitSet;
    private Set<Integer> valuesToSet;
    private Set<Integer> valuesToClear;
    private ArraySegmentRecycler memoryRecycler;

    public interface IndexPredicate {
        boolean shouldIndex(int ordinal);
    }

    /**
     * Create a index for integer values based on the given IndexPredicate.
     *
     * @param readStateEngine HollowReadStateEngine to read data set.
     * @param type            type which contains the path to integer values for indexing.
     * @param fieldPath       path to the integer values
     * @param predicate       implementation of IndexPredicate, indicating if the record passes the condition for indexing.
     */
    public HollowIntSet(HollowReadStateEngine readStateEngine, String type, String fieldPath, IndexPredicate predicate) {

        // check arguments
        if (readStateEngine == null) throw new IllegalArgumentException("Read state engine cannot be null");
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (fieldPath == null || fieldPath.isEmpty())
            throw new IllegalArgumentException("fieldPath cannot be null or empty");

        this.readStateEngine = readStateEngine;
        this.type = type;
        this.fieldPath = new FieldPath(readStateEngine, type, fieldPath, HollowObjectSchema.FieldType.INT);
        this.memoryRecycler = WastefulRecycler.DEFAULT_INSTANCE;
        this.predicate = predicate;
        this.valuesToSet = new HashSet<>();
        this.valuesToClear = new HashSet<>();

        build();
    }

    private void build() {

        sparseBitSet = new SparseBitSet(Integer.MAX_VALUE, memoryRecycler);
        BitSet typeBitSet = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = typeBitSet.nextSetBit(0);
        while (ordinal != -1) {
            put(ordinal);
            ordinal = typeBitSet.nextSetBit(ordinal + 1);
        }
        sparseBitSet.compact();
    }

    private void put(int typeOrdinal) {
        if (predicate.shouldIndex(typeOrdinal)) {
            Object[] values = fieldPath.findValuesFollowingPath(typeOrdinal);
            if (values != null && values.length > 0) {
                for (Object value : values)
                    sparseBitSet.set((int) value);
            }
        }
    }

    public boolean get(int i) {
        return sparseBitSet.get(i);
    }

    public int cardinality() {
        return sparseBitSet.cardinality();
    }

    public int size() {
        return sparseBitSet.size();
    }

    @Override
    public void beginUpdate() {
        valuesToSet.clear();
        valuesToClear.clear();
    }

    @Override
    public void addedOrdinal(int ordinal) {
        if (predicate.shouldIndex(ordinal)) {
            Object[] values = fieldPath.findValuesFollowingPath(ordinal);
            for (Object value : values)
                valuesToSet.add((int) value);
        }
    }

    @Override
    public void removedOrdinal(int ordinal) {
        if (predicate.shouldIndex(ordinal)) {
            Object[] values = fieldPath.findValuesFollowingPath(ordinal);
            for (Object value : values)
                valuesToClear.add((int) value);
        }
    }

    @Override
    public void endUpdate() {

    }

    private class SparseBitSet {

        private static final int BUCKET_SHIFT = 6;
        private static final int BUCKET_SIZE = 1 << BUCKET_SHIFT;//currently 1 long -> 64 bits, easy to read from FixedLengthElementArray

        private int totalBuckets;
        private FixedLengthElementArray index;
        private FixedLengthElementArray[] buckets;
        private final ArraySegmentRecycler memoryRecycle;

        private int maxBucketIndexUsed;

        private SparseBitSet(int maxValue, ArraySegmentRecycler memoryRecycle) {
            this.memoryRecycle = memoryRecycle;
            this.maxBucketIndexUsed = -1;

            this.totalBuckets = (maxValue / BUCKET_SIZE) == 0 ? 1 : (maxValue / BUCKET_SIZE);
            this.index = new FixedLengthElementArray(memoryRecycle, totalBuckets);
            this.buckets = new FixedLengthElementArray[totalBuckets];
        }

        private int getBucketIndex(int i) {
            return i >>> BUCKET_SHIFT;
        }

        private int getOffset(int i) {
            return (i & (BUCKET_SIZE - 1));
        }

        private void set(int i) {
            int bucketIndex = getBucketIndex(i);

            if (index.getElementValue(bucketIndex, 1) == 0) {
                buckets[bucketIndex] = new FixedLengthElementArray(memoryRecycle, BUCKET_SIZE);
                index.setElementValue(bucketIndex, 1, 1);
                if (maxBucketIndexUsed < bucketIndex) maxBucketIndexUsed = bucketIndex;
            }

            int offset = getOffset(i);
            int actualIndex = (bucketIndex * BUCKET_SIZE) + offset;
            buckets[bucketIndex].setElementValue(actualIndex, 1, 1);
        }

        private boolean get(int i) {
            int bucketIndex = getBucketIndex(i);
            if (buckets[bucketIndex] == null) return false;

            int offset = getOffset(i);
            int actualIndex = (bucketIndex * BUCKET_SIZE) + offset;
            return buckets[bucketIndex].getElementValue(actualIndex, 1) == 1;
        }

        private void clear(int i) {
            int bucketIndex = getBucketIndex(i);
            if (buckets[bucketIndex] != null) {
                int offset = getOffset(i);
                int actualIndex = (bucketIndex * BUCKET_SIZE) + offset;
                buckets[bucketIndex].setElementValue(actualIndex, 1, 0);
            }
        }

        private int indexCardinality() {
            int cardinality = 0;
            int i = 0;
            while (i <= totalBuckets) {
                cardinality += index.getElementValue(i, 1);
            }
            return cardinality;
        }

        private int cardinality() {
            int cardinality = 0;
            int i = 0;
            while (i <= totalBuckets) {
                if (index.getElementValue(i, 1) == 1) {
                    int offset = 0;
                    while (offset < BUCKET_SIZE)
                        cardinality += buckets[i].getElementValue(((i * BUCKET_SIZE) + offset), 1);
                }
            }
            return cardinality;
        }

        private int size() {
            return (indexCardinality() * BUCKET_SIZE);
        }

        // shrink buckets & index if not needed
        private void compact() {
            if (maxBucketIndexUsed > 0 && maxBucketIndexUsed < totalBuckets) {
                FixedLengthElementArray compactIndex = new FixedLengthElementArray(memoryRecycle, maxBucketIndexUsed);
                compactIndex.copyBits(index, 0, 0, maxBucketIndexUsed * BUCKET_SIZE);
                index = compactIndex;

                FixedLengthElementArray[] compactBuckets = new FixedLengthElementArray[maxBucketIndexUsed + 1];
                for (int i = 0; i < maxBucketIndexUsed + 1; i++) {
                    compactBuckets[i] = buckets[i];
                }
                buckets = compactBuckets;
                totalBuckets = maxBucketIndexUsed;
            }
        }
    }
}