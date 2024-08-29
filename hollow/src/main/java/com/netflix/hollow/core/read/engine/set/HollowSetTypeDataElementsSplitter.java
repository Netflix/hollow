package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowSetTypeDataElements} into multiple {@code HollowSetTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowSetTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter<HollowSetTypeDataElements> {

    public HollowSetTypeDataElementsSplitter(HollowSetTypeDataElements from, int numSplits) {
        super(from, numSplits);
    }

    @Override
    public void init() {
        this.to = new HollowSetTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowSetTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
        long[] shardTotalOfSetBuckets  = new long[numSplits];
        long maxShardTotalOfSetBuckets = 0;

        // count buckets per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;

            long startBucket = from.getStartBucket(ordinal);
            long endBucket = from.getEndBucket(ordinal);
            long numBuckets = endBucket - startBucket;

            shardTotalOfSetBuckets[toIndex] += numBuckets;
            if(shardTotalOfSetBuckets[toIndex] > maxShardTotalOfSetBuckets) {
                maxShardTotalOfSetBuckets = shardTotalOfSetBuckets[toIndex];
            }
        }

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            HollowSetTypeDataElements target = to[toIndex];
            // retained because these are computed based on max across all shards, splitting has no effect
            target.bitsPerElement = from.bitsPerElement;
            target.emptyBucketValue = from.emptyBucketValue;
            target.bitsPerSetSizeValue = from.bitsPerSetSizeValue;

            // recomputed based on split shards
            target.bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(maxShardTotalOfSetBuckets);
            target.totalNumberOfBuckets = shardTotalOfSetBuckets[toIndex];
            target.bitsPerFixedLengthSetPortion = target.bitsPerSetPointer + target.bitsPerSetSizeValue;

            target.setPointerAndSizeData = FixedLengthDataFactory.get(((long)target.maxOrdinal + 1) * target.bitsPerFixedLengthSetPortion, target.memoryMode, target.memoryRecycler);
            target.elementData = FixedLengthDataFactory.get(target.totalNumberOfBuckets * target.bitsPerElement, target.memoryMode, target.memoryRecycler);
        }
    }

    @Override
    public void copyRecords() {
        int numSplits = to.length;
        long bucketCounter[] = new long[numSplits];

        // count elements per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;

            long startBucket = from.getStartBucket(ordinal);
            long endBucket = from.getEndBucket(ordinal);
            HollowSetTypeDataElements target = to[toIndex];

            if (target.bitsPerElement == from.bitsPerElement && target.emptyBucketValue == from.emptyBucketValue) {
                // fastpath
                int bitsPerElement = from.bitsPerElement;
                long numBuckets = endBucket - startBucket;
                target.elementData.copyBits(from.elementData, startBucket * bitsPerElement, bucketCounter[toIndex] * bitsPerElement, numBuckets * bitsPerElement);
                bucketCounter[toIndex] += numBuckets;
            } else {
                // for (long bucket=startBucket;bucket<endBucket;bucket++) {
                //     long bucketVal = from.elementData.getElementValue(bucket * from.bitsPerElement, from.bitsPerElement);
                //     if(bucketVal == from.emptyBucketValue)
                //         bucketVal = target.emptyBucketValue;
                //     target.elementData.setElementValue(bucketCounter[toIndex] * target.bitsPerElement, target.bitsPerElement, bucketVal);
                //     bucketCounter[toIndex]++;
                // }
                throw new RuntimeException("Unexpected for Set type during split, expected during join");  // SNAP: TODO: remove, or keep for now so that we can test the slow path and in future when we make size optimizations then consumers will be backwards compatible

            }
            target.setPointerAndSizeData.setElementValue((long) toOrdinal * target.bitsPerFixedLengthSetPortion, target.bitsPerSetPointer, bucketCounter[toIndex]);
            long setSize = from.setPointerAndSizeData.getElementValue((long) (ordinal * from.bitsPerFixedLengthSetPortion) + from.bitsPerSetPointer, from.bitsPerSetSizeValue);
            target.setPointerAndSizeData.setElementValue((long) (toOrdinal * target.bitsPerFixedLengthSetPortion) + target.bitsPerSetPointer, target.bitsPerSetSizeValue, setSize);
        }
    }
}
