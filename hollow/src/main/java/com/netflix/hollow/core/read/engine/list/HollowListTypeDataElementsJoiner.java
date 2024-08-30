package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowListTypeDataElements}s into 1 {@code HollowListTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowListTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner<HollowListTypeDataElements> {

    public HollowListTypeDataElementsJoiner(HollowListTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void init() {
        this.to = new HollowListTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);
    }

    @Override
    public void populateStats() {
        for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
            int mappedMaxOrdinal = from[fromIndex].maxOrdinal == -1 ? -1 : (from[fromIndex].maxOrdinal * from.length) + fromIndex;
            to.maxOrdinal = Math.max(to.maxOrdinal, mappedMaxOrdinal);
            if (from[fromIndex].bitsPerElement > to.bitsPerElement) {
                // uneven bitsPerElement could be the case for consumers that skip type shards with no additions, so pick max across all shards
                to.bitsPerElement = from[fromIndex].bitsPerElement;
            }
        }

        long totalOfListSizes = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            long startElement = from[fromIndex].getStartElement(fromOrdinal);
            long endElement = from[fromIndex].getEndElement(fromOrdinal);
            long numElements = endElement - startElement;
            totalOfListSizes += numElements;

        }
        to.bitsPerListPointer = totalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalOfListSizes);
        to.listPointerData = FixedLengthDataFactory.get((long)to.bitsPerListPointer * (to.maxOrdinal + 1), to.memoryMode, to.memoryRecycler);
        to.elementData = FixedLengthDataFactory.get((long)to.bitsPerElement * totalOfListSizes, to.memoryMode, to.memoryRecycler);
        to.totalNumberOfElements = totalOfListSizes;
    }

    @Override
    public void copyRecords() {
        long elementCounter = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            if (fromOrdinal <= from[fromIndex].maxOrdinal) {
                HollowListTypeDataElements source = from[fromIndex];
                long startElement = source.getStartElement(fromOrdinal);
                long endElement = source.getEndElement(fromOrdinal);

                if (source.bitsPerElement == to.bitsPerElement) {
                    // fastpath can bulk copy elements. emptyBucketValue is same since bitsPerElement is same
                    long numElements = endElement - startElement;
                    int bitsPerElement = source.bitsPerElement;
                    to.elementData.copyBits(source.elementData, startElement * bitsPerElement, elementCounter * bitsPerElement, numElements * bitsPerElement);
                    elementCounter += numElements;
                } else {
                    for (long element = startElement; element < endElement; element++) {
                        int elementOrdinal = (int) source.elementData.getElementValue(element * source.bitsPerElement, source.bitsPerElement);
                        to.elementData.setElementValue(elementCounter * to.bitsPerElement, to.bitsPerElement, elementOrdinal);
                        elementCounter++;
                    }
                }
            } // else: lopsided shard for e.g. when consumers skip type shards with no additions.
              //       nothing is written to elementData and the cached value of elementCounter is written to listPointerData.

            to.listPointerData.setElementValue((long) to.bitsPerListPointer * ordinal, to.bitsPerListPointer, elementCounter);
        }
    }
}
