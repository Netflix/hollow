package com.netflix.hollow.core.read.engine.list;

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

        populateStatsAndShards(to, from, toMask, toOrdinalShift);

        return to;
    }

    private void populateStatsAndShards(HollowListTypeDataElements[] to, HollowListTypeDataElements from, int toMask, int toOrdinalShift) {
        int numShards = to.length;
        int shardMask = numShards - 1;
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;
        }

        long[] totalOfListSizes  = new long[numShards];
        long elementCounter[] = new long[numShards];
        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            int shardOrdinal = ordinal / numShards;
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

            for (long element=startElement;element<endElement;element++) {
                int elementOrdinal = (int)from.elementData.getElementValue(element * from.bitsPerElement, from.bitsPerElement);
                to[shardNumber].elementData.setElementValue(elementCounter[shardNumber] * to[shardNumber].bitsPerElement, to[shardNumber].bitsPerElement, elementOrdinal);
                elementCounter[shardNumber]++;
            }
            to[shardNumber].listPointerData.setElementValue(to[shardNumber].bitsPerListPointer * shardOrdinal, to[shardNumber].bitsPerListPointer, elementCounter[shardNumber]);
            totalOfListSizes[shardNumber] += numElements;
        }

        long maxShardTotalOfListSizes = 0;
        for(int shardNumber=0;shardNumber<numShards;shardNumber++) {
            if(totalOfListSizes[shardNumber] > maxShardTotalOfListSizes)
                maxShardTotalOfListSizes = totalOfListSizes[shardNumber];
        }

        for(int shardNumber=0;shardNumber<numShards;shardNumber++) {
            to[shardNumber].bitsPerElement = from.bitsPerElement;   // retained: it's the max across all shards in type, so splitting has no effect
            to[shardNumber].bitsPerListPointer = maxShardTotalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxShardTotalOfListSizes);
            to[shardNumber].totalNumberOfElements = totalOfListSizes[shardNumber];
        }
    }
}
