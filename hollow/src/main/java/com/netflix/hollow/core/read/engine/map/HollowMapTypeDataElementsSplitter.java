package com.netflix.hollow.core.read.engine.map;


import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowMapTypeDataElements} into multiple {@code HollowMapTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowMapTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter<HollowMapTypeDataElements> {

    public HollowMapTypeDataElementsSplitter(HollowMapTypeDataElements from, int numSplits) {
        super(from, numSplits);
    }

    @Override
    public void initToElements() {
        this.to = new HollowMapTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowMapTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
        long[] shardTotalOfMapBuckets  = new long[numSplits];
        long maxShardTotalOfMapBuckets = 0;

        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;

            long startBucket = from.getStartBucket(ordinal);
            long endBucket = from.getEndBucket(ordinal);
            long numBuckets = endBucket - startBucket;

            shardTotalOfMapBuckets[toIndex] += numBuckets;
            if(shardTotalOfMapBuckets[toIndex] > maxShardTotalOfMapBuckets) {
                maxShardTotalOfMapBuckets = shardTotalOfMapBuckets[toIndex];
            }
        }

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            HollowMapTypeDataElements target = to[toIndex];
            // retained
            target.bitsPerKeyElement = from.bitsPerKeyElement;
            target.bitsPerValueElement = from.bitsPerValueElement;
            target.bitsPerMapSizeValue = from.bitsPerMapSizeValue;
            target.emptyBucketKeyValue = from.emptyBucketKeyValue;

            // recomputed
            target.bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(maxShardTotalOfMapBuckets);
            target.totalNumberOfBuckets = shardTotalOfMapBuckets[toIndex];
            target.bitsPerFixedLengthMapPortion = target.bitsPerMapSizeValue + target.bitsPerMapPointer;
            target.bitsPerMapEntry = target.bitsPerKeyElement + target.bitsPerValueElement;
        }
    }

    @Override
    public void copyRecords() {
        int numSplits = to.length;
        long bucketCounter[] = new long[numSplits];

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            HollowMapTypeDataElements target = to[toIndex];
            target.mapPointerAndSizeData = FixedLengthDataFactory.get((long)(target.maxOrdinal + 1) * target.bitsPerFixedLengthMapPortion, target.memoryMode, target.memoryRecycler);
            target.entryData = FixedLengthDataFactory.get(target.totalNumberOfBuckets * target.bitsPerMapEntry, target.memoryMode, target.memoryRecycler);
        }

        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;

            HollowMapTypeDataElements target = to[toIndex];
            long startBucket = from.getStartBucket(ordinal);
            long endBucket = from.getEndBucket(ordinal);

            long numBuckets = endBucket - startBucket;
            target.copyBucketsFrom(bucketCounter[toIndex], from, startBucket, endBucket);
            bucketCounter[toIndex] += numBuckets;

            target.mapPointerAndSizeData.setElementValue((long)toOrdinal * target.bitsPerFixedLengthMapPortion, target.bitsPerMapPointer, bucketCounter[toIndex]);
            long mapSize = from.mapPointerAndSizeData.getElementValue((long)(ordinal * from.bitsPerFixedLengthMapPortion) + from.bitsPerMapPointer, from.bitsPerMapSizeValue);
            target.mapPointerAndSizeData.setElementValue((long)(toOrdinal * target.bitsPerFixedLengthMapPortion) + target.bitsPerMapPointer, target.bitsPerMapSizeValue, mapSize);
        }
    }
}
