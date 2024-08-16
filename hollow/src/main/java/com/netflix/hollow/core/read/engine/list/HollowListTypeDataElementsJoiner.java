package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;


/**
 * Join multiple {@code HollowListTypeDataElements}s into 1 {@code HollowListTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowListTypeDataElementsJoiner {

    HollowListTypeDataElements join(HollowListTypeDataElements[] from) {
        final int fromMask = from.length - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);

        if (from.length<=0 || !((from.length&(from.length-1))==0)) {
            throw new IllegalStateException("No. of DataElements to be joined must be a power of 2");
        }

        HollowListTypeDataElements to = new HollowListTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);

        GapEncodedVariableLengthIntegerReader[] fromRemovals = new GapEncodedVariableLengthIntegerReader[from.length];
        for (int i=0;i<from.length;i++) {
            fromRemovals[i] = from[i].encodedRemovals;
        }
        to.encodedRemovals = GapEncodedVariableLengthIntegerReader.join(fromRemovals);

        for (HollowListTypeDataElements elements : from) {
            if (elements.encodedAdditions != null) {
                throw new IllegalStateException("Encountered encodedAdditions in data elements joiner- this is not expected " +
                        "since encodedAdditions only exist on delta data elements and they dont carry over to target data elements, " +
                        "delta data elements are never split/joined");
            }
        }

        populateStats(to, from);

        copyRecords(to, from);

        return to;
    }

    void populateStats(HollowListTypeDataElements to, HollowListTypeDataElements[] from) {
        to.maxOrdinal = -1;
        for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
            int mappedMaxOrdinal = from[fromIndex].maxOrdinal == -1 ? -1 : (from[fromIndex].maxOrdinal * from.length) + fromIndex;
            to.maxOrdinal = Math.max(to.maxOrdinal, mappedMaxOrdinal);
            if (from[fromIndex].bitsPerElement > to.bitsPerElement) {
                // uneven bitsPerElement could be the case for consumers that skip type shards with no additions, so pick max across all shards
                to.bitsPerElement = from[fromIndex].bitsPerElement;
            }
        }

        final int fromMask = from.length - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);
        long totalOfListSizes = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            long startElement;
            long endElement;
            if (fromOrdinal == 0) {
                startElement = 0;
                endElement = from[fromIndex].listPointerData.getElementValue(0, from[fromIndex].bitsPerListPointer);
            } else {
                long endFixedLengthOffset = (long)fromOrdinal * from[fromIndex].bitsPerListPointer;
                long startFixedLengthOffset = endFixedLengthOffset - from[fromIndex].bitsPerListPointer;
                startElement = from[fromIndex].listPointerData.getElementValue(startFixedLengthOffset, from[fromIndex].bitsPerListPointer);
                endElement = from[fromIndex].listPointerData.getElementValue(endFixedLengthOffset, from[fromIndex].bitsPerListPointer);
            }
            long numElements = endElement - startElement;
            totalOfListSizes += numElements;

        }
        to.bitsPerListPointer = totalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalOfListSizes);
        to.listPointerData = FixedLengthDataFactory.get((long)to.bitsPerListPointer * (to.maxOrdinal + 1), to.memoryMode, to.memoryRecycler);
        to.elementData = FixedLengthDataFactory.get((long)to.bitsPerElement * totalOfListSizes, to.memoryMode, to.memoryRecycler);
        to.totalNumberOfElements = totalOfListSizes;
    }

    private void copyRecords(HollowListTypeDataElements to, HollowListTypeDataElements[] from) {
        final int fromMask = from.length - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);
        long elementCounter = 0;
        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            long startElement;
            long endElement;
            if (fromOrdinal <= from[fromIndex].maxOrdinal) {
                if (fromOrdinal == 0) {
                    startElement = 0;
                    endElement = from[fromIndex].listPointerData.getElementValue(0, from[fromIndex].bitsPerListPointer);
                } else {
                    long endFixedLengthOffset = (long)fromOrdinal * from[fromIndex].bitsPerListPointer;
                    long startFixedLengthOffset = endFixedLengthOffset - from[fromIndex].bitsPerListPointer;
                    startElement = from[fromIndex].listPointerData.getElementValue(startFixedLengthOffset, from[fromIndex].bitsPerListPointer);
                    endElement = from[fromIndex].listPointerData.getElementValue(endFixedLengthOffset, from[fromIndex].bitsPerListPointer);
                }
                for (long element=startElement;element<endElement;element++) {
                    int elementOrdinal = (int)from[fromIndex].elementData.getElementValue(element * from[fromIndex].bitsPerElement, from[fromIndex].bitsPerElement);
                    to.elementData.setElementValue(elementCounter * to.bitsPerElement, to.bitsPerElement, elementOrdinal);
                    elementCounter++;
                }
            } // else: lopsided shards could result for consumers that skip type shards with no additions, that gets handled
              // by not writing anything to elementData, and writing the cached value of elementCounter to listPointerData
                // SNAP: TODO: write a test for lopsided list shards

            to.listPointerData.setElementValue(to.bitsPerListPointer * ordinal, to.bitsPerListPointer, elementCounter);
        }
    }
}
