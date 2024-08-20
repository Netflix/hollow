package com.netflix.hollow.core.read.engine.set;

import static com.netflix.hollow.core.read.engine.set.HollowSetTypeReadStateShard.getAbsoluteBucketStart;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowSetTypeDataElements}s into 1 {@code HollowSetTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowSetTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner<HollowSetTypeDataElements> {

    public HollowSetTypeDataElementsJoiner(HollowSetTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void init() {
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

            HollowSetTypeDataElements source = from[fromIndex];

            long startBucket = getAbsoluteBucketStart(source, fromOrdinal);
            long endBucket = source.setPointerAndSizeData.getElementValue((long)fromOrdinal * source.bitsPerFixedLengthSetPortion, source.bitsPerSetPointer);
            long numBuckets = endBucket - startBucket;

            totalOfSetBuckets += numBuckets;
        }

        // retained because these are computed based on max across all shards, splitting has no effect
        to.totalNumberOfBuckets = totalOfSetBuckets;
        to.bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(to.totalNumberOfBuckets);
        to.bitsPerFixedLengthSetPortion = to.bitsPerSetPointer + to.bitsPerSetSizeValue;

        to.setPointerAndSizeData = FixedLengthDataFactory.get(((long)to.maxOrdinal + 1) * to.bitsPerFixedLengthSetPortion, to.memoryMode, to.memoryRecycler);
        to.elementData = FixedLengthDataFactory.get(to.totalNumberOfBuckets * to.bitsPerElement, to.memoryMode, to.memoryRecycler);
    }

    @Override
    public void copyRecords() {
        long bucketCounter = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            HollowSetTypeDataElements source = from[fromIndex];

            if (fromOrdinal <= from[fromIndex].maxOrdinal) {
                long startBucket = getAbsoluteBucketStart(source, fromOrdinal);
                long endBucket = source.setPointerAndSizeData.getElementValue((long)fromOrdinal * source.bitsPerFixedLengthSetPortion, source.bitsPerSetPointer);

                for (long bucket=startBucket;bucket<endBucket;bucket++) {
                    int bucketOrdinal = (int)source.elementData.getElementValue(bucket * source.bitsPerElement, source.bitsPerElement);
                    to.elementData.setElementValue(bucketCounter * to.bitsPerElement, to.bitsPerElement, bucketOrdinal);
                    bucketCounter++;
                }
            } // else: lopsided shards could result for consumers that skip type shards with no additions, that gets handled
            // by not writing anything to elementData, and writing the cached value of bucketCounter to listPointerData
            // SNAP: TODO: write a test for lopsided list shards (similar to for list types). Theres one in object joiner tests.

            to.setPointerAndSizeData.setElementValue((ordinal * to.bitsPerFixedLengthSetPortion), to.bitsPerSetPointer, bucketCounter);
            long setSize = source.setPointerAndSizeData.getElementValue((fromOrdinal * source.bitsPerFixedLengthSetPortion) + source.bitsPerSetPointer, source.bitsPerSetSizeValue);
            to.setPointerAndSizeData.setElementValue((ordinal * to.bitsPerFixedLengthSetPortion) + to.bitsPerSetPointer, to.bitsPerSetSizeValue, setSize);
        }
    }
}
