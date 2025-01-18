package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowSetTypeDataElements}s into 1 {@code HollowSetTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowSetTypeDataElementsJoiner extends HollowTypeDataElementsJoiner<HollowSetTypeDataElements> {

    public HollowSetTypeDataElementsJoiner(HollowSetTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void initToElements() {
        this.to = new HollowSetTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);
        to.bitsPerElement = 0;
    }

    @Override
    public void populateStats() {
        for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
            int mappedMaxOrdinal = from[fromIndex].maxOrdinal == -1 ? -1 : (from[fromIndex].maxOrdinal * from.length) + fromIndex;
            to.maxOrdinal = Math.max(to.maxOrdinal, mappedMaxOrdinal);

            // uneven bitsPerElement could be the case for consumers that skip type shards with no additions, so pick max across all shards
            HollowSetTypeDataElements source = from[fromIndex];
            if (source.bitsPerElement > to.bitsPerElement) {
                to.bitsPerElement = source.bitsPerElement;
            }
            if (source.bitsPerSetSizeValue > to.bitsPerSetSizeValue) {
                to.bitsPerSetSizeValue = source.bitsPerSetSizeValue;
            }
        }
        to.emptyBucketValue = (1 << to.bitsPerElement) - 1;

        long totalOfSetBuckets = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;
            if (fromOrdinal > from[fromIndex].maxOrdinal) {
                continue; // could be lopsided shards resulting from skipping type shards with no additions
            }

            HollowSetTypeDataElements source = from[fromIndex];
            long startBucket = source.getStartBucket(fromOrdinal);
            long endBucket = source.getEndBucket(fromOrdinal);
            long numBuckets = endBucket - startBucket;

            totalOfSetBuckets += numBuckets;
        }

        to.totalNumberOfBuckets = totalOfSetBuckets;
        to.bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(to.totalNumberOfBuckets);
        to.bitsPerFixedLengthSetPortion = to.bitsPerSetPointer + to.bitsPerSetSizeValue;
    }

    @Override
    public void copyRecords() {
        long bucketCounter = 0;

        to.setPointerAndSizeData = FixedLengthDataFactory.get(((long)to.maxOrdinal + 1) * to.bitsPerFixedLengthSetPortion, to.memoryMode, to.memoryRecycler);
        to.elementData = FixedLengthDataFactory.get(to.totalNumberOfBuckets * to.bitsPerElement, to.memoryMode, to.memoryRecycler);

        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            HollowSetTypeDataElements source = from[fromIndex];

            long setSize = 0;
            if (fromOrdinal <= from[fromIndex].maxOrdinal) { // else lopsided shards resulting from skipping type shards with no additions, setSize remains 0
                long startBucket = source.getStartBucket(fromOrdinal);
                long endBucket = source.getEndBucket(fromOrdinal);

                long numBuckets = endBucket - startBucket;
                to.copyBucketsFrom(bucketCounter, source, startBucket, endBucket);
                bucketCounter += numBuckets;

                setSize = source.setPointerAndSizeData.getElementValue(((long)fromOrdinal * source.bitsPerFixedLengthSetPortion) + source.bitsPerSetPointer, source.bitsPerSetSizeValue);
            }
            to.setPointerAndSizeData.setElementValue((long)ordinal * to.bitsPerFixedLengthSetPortion, to.bitsPerSetPointer, bucketCounter);
            to.setPointerAndSizeData.setElementValue(((long)ordinal * to.bitsPerFixedLengthSetPortion) + to.bitsPerSetPointer, to.bitsPerSetSizeValue, setSize);
        }
    }
}
