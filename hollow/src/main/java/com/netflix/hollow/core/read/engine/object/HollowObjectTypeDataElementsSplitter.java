package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthEndByte;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthStartByte;

import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

public class HollowObjectTypeDataElementsSplitter {

    HollowObjectTypeDataElements[] split(HollowObjectTypeDataElements from, int numSplits) {
        final int toMask = numSplits - 1;
        final int toOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);
        final long[][] currentWriteVarLengthDataPointers;

        if (!(numSplits>0) || !((numSplits&(numSplits-1))==0)) {
            throw new UnsupportedOperationException("Must split by power of 2");
        }

        HollowObjectTypeDataElements[] to = new HollowObjectTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowObjectTypeDataElements(from.schema, from.memoryMode, from.memoryRecycler);
            to[i].maxOrdinal = -1;
        }
        currentWriteVarLengthDataPointers = new long[numSplits][from.schema.numFields()];

        populateStats(to, from, toMask, toOrdinalShift);

        // split gap encoded var length removals
        if (from.encodedRemovals != null) {
            // splits from.encodedRemovals to to[i].encodedRemovals, creating ByteDataArrays for to[i], and does not clean up from.encodedRemovals.getUnderyingArray()
            System.out.println("SNAP: pre-split gap ended removals");   // TODO: remove
            from.encodedRemovals.prettyPrint();
            System.out.println("SNAP: from.maxOrdinal was " + from.maxOrdinal);

            GapEncodedVariableLengthIntegerReader[] splitRemovals = from.encodedRemovals.split(numSplits);
            for(int i=0;i<to.length;i++) {
                to[i].encodedRemovals = splitRemovals[i];
            }

            for(int i=0;i<numSplits;i++) {  // TODO: remove
                System.out.println("SNAP: post-split gap ended removals for split " + i);
                to[i].encodedRemovals.prettyPrint();
            }
        }
        if (from.encodedAdditions != null) {
            throw new UnsupportedOperationException("// SNAP: TODO: We never expect to split/join encodedAdditions- they are accepted from delta as-is");
        }

        for(int i=0;i<to.length;i++) {
            to[i].fixedLengthData = new FixedLengthElementArray(to[i].memoryRecycler, (long)to[i].bitsPerRecord * (to[i].maxOrdinal + 1));  // TODO: add to FxiedLengthDataFactory to support non-heap modes
            for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
                if(from.varLengthData[fieldIdx] != null) {
                    to[i].varLengthData[fieldIdx] = VariableLengthDataFactory.get(from.memoryMode, from.memoryRecycler);
                }
            }
        }

        for(int i=0;i<=from.maxOrdinal;i++) {
            copyRecord(i, to, from, toMask, toOrdinalShift, currentWriteVarLengthDataPointers);
        }
        return to;
    }

    private void populateStats(HollowObjectTypeDataElements[] to, HollowObjectTypeDataElements from, int toMask, int toOrdinalShift) {
        long[][] varLengthSizes = new long[to.length][from.schema.numFields()];

        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal;
            for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
                if(from.varLengthData[fieldIdx] != null) {
                    varLengthSizes[toIndex][fieldIdx] += varLengthSize(from, ordinal, fieldIdx);
                }
            }
        }

        for(int toIndex=0;toIndex<to.length;toIndex++) {
            for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
                if(from.varLengthData[fieldIdx] == null) {
                    to[toIndex].bitsPerField[fieldIdx] = from.bitsPerField[fieldIdx];
                } else {
                    to[toIndex].bitsPerField[fieldIdx] = (64 - Long.numberOfLeadingZeros(varLengthSizes[toIndex][fieldIdx] + 1)) + 1;
                }
                to[toIndex].nullValueForField[fieldIdx] = (to[toIndex].bitsPerField[fieldIdx] == 64) ? -1L : (1L << to[toIndex].bitsPerField[fieldIdx]) - 1;    // SNAP: here: can just copy over?
                to[toIndex].bitOffsetPerField[fieldIdx] = to[toIndex].bitsPerRecord;
                to[toIndex].bitsPerRecord += to[toIndex].bitsPerField[fieldIdx];

                to[toIndex].bitsPerUnfilteredField = from.bitsPerUnfilteredField;
                to[toIndex].unfilteredFieldIsIncluded = from.unfilteredFieldIsIncluded;
            }
        }
    }

    private void copyRecord(int ordinal, HollowObjectTypeDataElements[] to, HollowObjectTypeDataElements from, int toMask, int toOrdinalShift, long[][] currentWriteVarLengthDataPointers) {
        int toIndex = ordinal & toMask;
        int toOrdinal = ordinal >> toOrdinalShift;

        for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
            if(to[toIndex].varLengthData[fieldIdx] == null) {
                long value = from.fixedLengthData.getLargeElementValue(((long)ordinal * from.bitsPerRecord) + from.bitOffsetPerField[fieldIdx], from.bitsPerField[fieldIdx]);
                to[toIndex].fixedLengthData.setElementValue(((long)toOrdinal * to[toIndex].bitsPerRecord) + to[toIndex].bitOffsetPerField[fieldIdx], to[toIndex].bitsPerField[fieldIdx], value);
            } else {
                long fromStartByte = varLengthStartByte(from, ordinal, fieldIdx);
                long fromEndByte = varLengthEndByte(from, ordinal, fieldIdx);
                long size = fromEndByte - fromStartByte;

                to[toIndex].fixedLengthData.setElementValue(((long)toOrdinal * to[toIndex].bitsPerRecord) + to[toIndex].bitOffsetPerField[fieldIdx], to[toIndex].bitsPerField[fieldIdx], currentWriteVarLengthDataPointers[toIndex][fieldIdx] + size);
                to[toIndex].varLengthData[fieldIdx].copy(from.varLengthData[fieldIdx], fromStartByte, currentWriteVarLengthDataPointers[toIndex][fieldIdx], size);

                currentWriteVarLengthDataPointers[toIndex][fieldIdx] += size;
            }
        }
    }
}
