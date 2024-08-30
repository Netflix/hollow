package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowListTypeDataElements} into multiple {@code HollowListTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowListTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter<HollowListTypeDataElements> {

    public HollowListTypeDataElementsSplitter(HollowListTypeDataElements from, int numSplits) {
        super(from, numSplits);
    }

    @Override
    public void init() {
        this.to = new HollowListTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowListTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
        long[] totalOfListSizes  = new long[numSplits];

        // count elements per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;

            long startElement = from.getStartElement(ordinal);
            long endElement = from.getEndElement(ordinal);
            long numElements = endElement - startElement;
            totalOfListSizes[toIndex] += numElements;
        }

        long maxShardTotalOfListSizes = 0;
        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            if(totalOfListSizes[toIndex] > maxShardTotalOfListSizes)
                maxShardTotalOfListSizes = totalOfListSizes[toIndex];
        }

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            HollowListTypeDataElements target = to[toIndex];
            target.bitsPerElement = from.bitsPerElement;   // retained: it's the max across all shards in type, so splitting has no effect
            target.bitsPerListPointer = maxShardTotalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxShardTotalOfListSizes);

            target.listPointerData = FixedLengthDataFactory.get((long)target.bitsPerListPointer * (target.maxOrdinal + 1), target.memoryMode, target.memoryRecycler);
            target.elementData = FixedLengthDataFactory.get(target.bitsPerElement * totalOfListSizes[toIndex], target.memoryMode, target.memoryRecycler);

            target.totalNumberOfElements = totalOfListSizes[toIndex];  // useful for heap usage stats
        }
    }

    @Override
    public void copyRecords() {
        int numSplits = to.length;
        long elementCounter[] = new long[numSplits];

        // count elements per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;

            long startElement = from.getStartElement(ordinal);
            long endElement = from.getEndElement(ordinal);
            HollowListTypeDataElements target = to[toIndex];

            if (from.bitsPerElement == target.bitsPerElement) {
                // fast path can bulk copy elements. emptyBucketValue is same since bitsPerElement is same
                long numElements = endElement - startElement;
                int bitsPerElement = from.bitsPerElement;
                target.elementData.copyBits(from.elementData, startElement * bitsPerElement, elementCounter[toIndex] * bitsPerElement, numElements * bitsPerElement);
                elementCounter[toIndex] += numElements;
            } else {
                // slow path(but more compact) not exercised until populateSats above supports split shard specific bitsPerElement
                // (which would make sense to add once HollowListTypeWriteState's gatherStatistics supports assigning bitsPerElement at a shard level)
                for (long element=startElement;element<endElement;element++) {
                    int elementOrdinal = (int)from.elementData.getElementValue(element * from.bitsPerElement, from.bitsPerElement);
                    target.elementData.setElementValue(elementCounter[toIndex] * target.bitsPerElement, target.bitsPerElement, elementOrdinal);
                    elementCounter[toIndex]++;
                }
            }

            target.listPointerData.setElementValue((long) target.bitsPerListPointer * toOrdinal, target.bitsPerListPointer, elementCounter[toIndex]);
        }
    }
}
