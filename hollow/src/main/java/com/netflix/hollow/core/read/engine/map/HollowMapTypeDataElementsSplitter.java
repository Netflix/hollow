package com.netflix.hollow.core.read.engine.map;


import static com.netflix.hollow.core.read.engine.map.HollowMapTypeReadStateShard.getAbsoluteBucketStart;

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
    public void init() {
        this.to = new HollowMapTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowMapTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
        long[] shardTotalOfMapBuckets  = new long[numSplits];
        long maxShardTotalOfMapBuckets = 0;

        // count buckets per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;

            long startBucket = getAbsoluteBucketStart(from, ordinal);
            long endBucket = from.mapPointerAndSizeData.getElementValue((long)ordinal * from.bitsPerFixedLengthMapPortion, from.bitsPerMapPointer);
            long numBuckets = endBucket - startBucket;

            shardTotalOfMapBuckets[toIndex] += numBuckets;
            if(shardTotalOfMapBuckets[toIndex] > maxShardTotalOfMapBuckets) {
                maxShardTotalOfMapBuckets = shardTotalOfMapBuckets[toIndex];
            }
        }

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            HollowMapTypeDataElements target = to[toIndex];
            // retained because these are computed based on max across all shards, splitting has no effect
            target.bitsPerKeyElement = from.bitsPerKeyElement;
            target.bitsPerValueElement = from.bitsPerValueElement;
            target.bitsPerMapSizeValue = from.bitsPerMapSizeValue;
            target.emptyBucketKeyValue = from.emptyBucketKeyValue;      // SNAP: TODO: test with some empty buckets too

            // recomputed based on split shards
            target.bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(maxShardTotalOfMapBuckets);
            target.totalNumberOfBuckets = shardTotalOfMapBuckets[toIndex];
            target.bitsPerFixedLengthMapPortion = target.bitsPerMapSizeValue + target.bitsPerMapPointer;
            target.bitsPerMapEntry = target.bitsPerKeyElement + target.bitsPerValueElement;

            target.mapPointerAndSizeData = FixedLengthDataFactory.get((long)target.bitsPerFixedLengthMapPortion * (target.maxOrdinal + 1), target.memoryMode, target.memoryRecycler);
            target.entryData = FixedLengthDataFactory.get((long)target.bitsPerMapEntry * shardTotalOfMapBuckets[toIndex], target.memoryMode, target.memoryRecycler);
        }
    }

    @Override
    public void copyRecords() {
        int numSplits = to.length;
        long bucketCounter[] = new long[numSplits];

        // count buckets per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;

            long startBucket = getAbsoluteBucketStart(from, ordinal);
            long endBucket =from.mapPointerAndSizeData.getElementValue((long) ordinal * from.bitsPerFixedLengthMapPortion, from.bitsPerMapPointer);

            HollowMapTypeDataElements target = to[toIndex];
            for (long bucket=startBucket;bucket<endBucket;bucket++) {
                long bucketKey = from.entryData.getElementValue(bucket * from.bitsPerMapEntry, from.bitsPerKeyElement);
                long bucketValue = from.entryData.getElementValue(bucket * from.bitsPerMapEntry + from.bitsPerKeyElement, from.bitsPerValueElement);
                // SNAP: TODO: noop for map type?
                if(bucketKey == from.emptyBucketKeyValue)
                    bucketKey = target.emptyBucketKeyValue;
                // SNAP: TODO: bulk copy cos empty bucket value is the same and key/value bits are the same as from
                long targetBucketOffset = (bucketCounter[toIndex] * target.bitsPerMapEntry);
                target.entryData.setElementValue(targetBucketOffset, target.bitsPerKeyElement, bucketKey);
                target.entryData.setElementValue(targetBucketOffset + target.bitsPerKeyElement, target.bitsPerValueElement, bucketValue);

                bucketCounter[toIndex]++;
            }

            target.mapPointerAndSizeData.setElementValue((long) toOrdinal * target.bitsPerFixedLengthMapPortion, target.bitsPerMapPointer, bucketCounter[toIndex]);
            long mapSize = from.mapPointerAndSizeData.getElementValue((long) (ordinal * from.bitsPerFixedLengthMapPortion) + from.bitsPerMapPointer, from.bitsPerMapSizeValue);
            target.mapPointerAndSizeData.setElementValue((long) (toOrdinal * target.bitsPerFixedLengthMapPortion) + target.bitsPerMapPointer, target.bitsPerMapSizeValue, mapSize);
        }
    }
}
