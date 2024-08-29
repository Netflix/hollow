package com.netflix.hollow.core.read.engine.map;

import static com.netflix.hollow.core.read.engine.map.HollowMapTypeReadStateShard.getAbsoluteBucketStart;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowMapTypeDataElements}s into 1 {@code HollowMapTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowMapTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner<HollowMapTypeDataElements> {

    public HollowMapTypeDataElementsJoiner(HollowMapTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void init() {
        this.to = new HollowMapTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);
    }

    @Override
    public void populateStats() {
        for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
            int mappedMaxOrdinal = from[fromIndex].maxOrdinal == -1 ? -1 : (from[fromIndex].maxOrdinal * from.length) + fromIndex;
            to.maxOrdinal = Math.max(to.maxOrdinal, mappedMaxOrdinal);

            // uneven stats could be the case for consumers that skip type shards with no additions, so pick max across all shards
            HollowMapTypeDataElements source = from[fromIndex];
            if (source.bitsPerKeyElement > to.bitsPerKeyElement) {
                to.bitsPerKeyElement = source.bitsPerKeyElement;
            }
            if (source.bitsPerValueElement > to.bitsPerValueElement) {
                to.bitsPerValueElement = source.bitsPerValueElement;
            }
            if (source.bitsPerMapSizeValue > to.bitsPerMapSizeValue) {
                to.bitsPerMapSizeValue = source.bitsPerMapSizeValue;
            }
        }
        to.emptyBucketKeyValue = (1 << to.bitsPerKeyElement) - 1;

        long totalOfMapBuckets = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            HollowMapTypeDataElements source = from[fromIndex];

            long startBucket = getAbsoluteBucketStart(source, fromOrdinal);
            long endBucket = source.mapPointerAndSizeData.getElementValue((long)fromOrdinal * source.bitsPerFixedLengthMapPortion, source.bitsPerMapPointer);
            long numBuckets = endBucket - startBucket;

            totalOfMapBuckets += numBuckets;
        }

        to.totalNumberOfBuckets = totalOfMapBuckets;
        to.bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(to.totalNumberOfBuckets);
        to.bitsPerFixedLengthMapPortion = to.bitsPerMapSizeValue + to.bitsPerMapPointer;
        to.bitsPerMapEntry = to.bitsPerKeyElement + to.bitsPerValueElement;

        to.mapPointerAndSizeData = FixedLengthDataFactory.get((long)to.bitsPerFixedLengthMapPortion * (to.maxOrdinal + 1), to.memoryMode, to.memoryRecycler);
        to.entryData = FixedLengthDataFactory.get((long)to.bitsPerMapEntry * to.totalNumberOfBuckets, to.memoryMode, to.memoryRecycler);
    }

    @Override
    public void copyRecords() {
        long bucketCounter = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            HollowMapTypeDataElements source = from[fromIndex];

            long mapSize = 0;
            if (fromOrdinal <= from[fromIndex].maxOrdinal) { // else lopsided shards resulting from skipping type shards with no additions, mapSize remains 0
                long startBucket = getAbsoluteBucketStart(source, fromOrdinal);
                long endBucket = source.mapPointerAndSizeData.getElementValue((long)fromOrdinal * source.bitsPerFixedLengthMapPortion, source.bitsPerMapPointer);
                long numBuckets = endBucket - startBucket;

                // if (false) { // SNAP: TODO: test the slow path
                if (to.bitsPerKeyElement == source.bitsPerKeyElement && to.bitsPerValueElement == source.bitsPerValueElement) {
                    // emptyBucketKeyValue will also be uniform
                    long bitsPerMapEntry = to.bitsPerMapEntry;
                    to.entryData.copyBits(source.entryData, startBucket * bitsPerMapEntry, bucketCounter * bitsPerMapEntry, numBuckets * bitsPerMapEntry);
                    bucketCounter += numBuckets;
                } else {
                    for (long bucket = startBucket; bucket < endBucket; bucket++) {
                        long bucketKey = source.entryData.getElementValue(bucket * source.bitsPerMapEntry, source.bitsPerKeyElement);
                        long bucketValue = source.entryData.getElementValue(bucket * source.bitsPerMapEntry + source.bitsPerKeyElement, source.bitsPerValueElement);
                        if (bucketKey == source.emptyBucketKeyValue)
                            bucketKey = to.emptyBucketKeyValue; // since empty bucket key value can be non-uniform across shards
                        long targetBucketOffset = bucketCounter * to.bitsPerMapEntry;
                        to.entryData.setElementValue(targetBucketOffset, to.bitsPerKeyElement, bucketKey);
                        to.entryData.setElementValue(targetBucketOffset + to.bitsPerKeyElement, to.bitsPerValueElement, bucketValue);
                        bucketCounter ++;
                    }
                }

                mapSize = source.mapPointerAndSizeData.getElementValue((long) (fromOrdinal * source.bitsPerFixedLengthMapPortion) + source.bitsPerMapPointer, source.bitsPerMapSizeValue);
            }

            to.mapPointerAndSizeData.setElementValue( (long) ordinal * to.bitsPerFixedLengthMapPortion, to.bitsPerMapPointer, bucketCounter);
            to.mapPointerAndSizeData.setElementValue((long) (ordinal * to.bitsPerFixedLengthMapPortion) + to.bitsPerMapPointer, to.bitsPerMapSizeValue, mapSize);
        }
    }
}
