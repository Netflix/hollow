package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

/**
 * Split a {@code HollowListTypeDataElements} into multiple {@code HollowListTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowListTypeDataElementsSplitter {

    HollowListTypeDataElements[] split(HollowListTypeDataElements from, int numSplits) {
        final int toMask = numSplits - 1;
        final int toOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);

        if (numSplits<=0 || !((numSplits&(numSplits-1))==0)) {
            throw new IllegalStateException("Must split by power of 2");
        }

        HollowListTypeDataElements[] to = new HollowListTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowListTypeDataElements(from.memoryMode, from.memoryRecycler);
            to[i].maxOrdinal = -1;
        }

        if (from.encodedRemovals != null) {
            GapEncodedVariableLengthIntegerReader[] splitRemovals = from.encodedRemovals.split(numSplits);
            for(int i=0;i<to.length;i++) {
                to[i].encodedRemovals = splitRemovals[i];
            }
        }
        if (from.encodedAdditions != null) {
            throw new IllegalStateException("Encountered encodedAdditions in data elements splitter- this is not expected " +
                    "since encodedAdditions only exist on delta data elements and they dont carry over to target data elements, " +
                    "delta data elements are never split/joined");
        }

        populateStats(to, from, toMask, toOrdinalShift);

        copyRecords(to, from, toMask);

        return to;
    }

    private void populateStats(HollowListTypeDataElements[] to, HollowListTypeDataElements from, int toMask, int toOrdinalShift) {
        int numSplits = to.length;
        long[] totalOfListSizes  = new long[numSplits];

        // count elements per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;

            long startElement;
            long endElement;
            if (ordinal == 0) {
                startElement = 0;
                endElement = from.listPointerData.getElementValue(0, from.bitsPerListPointer);
            } else {
                long endFixedLengthOffset = (long)ordinal * from.bitsPerListPointer;
                long startFixedLengthOffset = endFixedLengthOffset - from.bitsPerListPointer;
                startElement = from.listPointerData.getElementValue(startFixedLengthOffset, from.bitsPerListPointer);
                endElement = from.listPointerData.getElementValue(endFixedLengthOffset, from.bitsPerListPointer);
            }

            long numElements = endElement - startElement;
            totalOfListSizes[toIndex] += numElements;
        }

        long maxShardTotalOfListSizes = 0;
        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            if(totalOfListSizes[toIndex] > maxShardTotalOfListSizes)
                maxShardTotalOfListSizes = totalOfListSizes[toIndex];
        }

        for(int toIndex=0;toIndex<numSplits;toIndex++) {
            to[toIndex].bitsPerElement = from.bitsPerElement;   // retained: it's the max across all shards in type, so splitting has no effect
            to[toIndex].bitsPerListPointer = maxShardTotalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxShardTotalOfListSizes);

            to[toIndex].listPointerData = FixedLengthDataFactory.get((long)to[toIndex].bitsPerListPointer * (to[toIndex].maxOrdinal + 1), to[toIndex].memoryMode, to[toIndex].memoryRecycler);
            to[toIndex].elementData = FixedLengthDataFactory.get((long)to[toIndex].bitsPerElement * totalOfListSizes[toIndex], to[toIndex].memoryMode, to[toIndex].memoryRecycler);
            // SNAP: TODO: who does clean up?

            to[toIndex].totalNumberOfElements = totalOfListSizes[toIndex];  // useful for heap usage stats
        }
    }

    private void copyRecords(HollowListTypeDataElements[] to, HollowListTypeDataElements from, int toMask) {
        int numSplits = to.length;
        long elementCounter[] = new long[numSplits];

        // count elements per split
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;

            long startElement;
            long endElement;
            if (ordinal == 0) {
                startElement = 0;
                endElement = from.listPointerData.getElementValue(0, from.bitsPerListPointer);
            } else {
                long endFixedLengthOffset = (long)ordinal * from.bitsPerListPointer;
                long startFixedLengthOffset = endFixedLengthOffset - from.bitsPerListPointer;
                startElement = from.listPointerData.getElementValue(startFixedLengthOffset, from.bitsPerListPointer);
                endElement = from.listPointerData.getElementValue(endFixedLengthOffset, from.bitsPerListPointer);
            }

            for (long element=startElement;element<endElement;element++) {
                int elementOrdinal = (int)from.elementData.getElementValue(element * from.bitsPerElement, from.bitsPerElement);
                to[toIndex].elementData.setElementValue(elementCounter[toIndex] * to[toIndex].bitsPerElement, to[toIndex].bitsPerElement, elementOrdinal);
                elementCounter[toIndex]++;
            }
            to[toIndex].listPointerData.setElementValue(to[toIndex].bitsPerListPointer * toIndex, to[toIndex].bitsPerListPointer, elementCounter[toIndex]);
        }
    }
}
