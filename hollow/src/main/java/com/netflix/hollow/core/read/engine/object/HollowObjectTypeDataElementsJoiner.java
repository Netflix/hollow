package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthEndByte;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthStartByte;

import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

public class HollowObjectTypeDataElementsJoiner {

    HollowObjectTypeDataElements join(HollowObjectTypeDataElements[] from) {
        final int fromMask = from.length - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);
        long[] currentWriteVarLengthDataPointers;

        if (from.length<=0 || !((from.length&(from.length-1))==0)) { // TODO: from.length==0
            throw new IllegalStateException("No. of DataElements to be joined must be a power of 2");
        }

        HollowObjectTypeDataElements to = new HollowObjectTypeDataElements(from[0].schema, from[0].memoryMode, from[0].memoryRecycler);
        currentWriteVarLengthDataPointers = new long[from[0].schema.numFields()];

        populateStats(to, from);

        GapEncodedVariableLengthIntegerReader[] fromRemovals = new GapEncodedVariableLengthIntegerReader[from.length];
        for (int i=0;i<from.length;i++) {
            fromRemovals[i] = from[i].encodedRemovals;

            // TODO: remove
            if (fromRemovals[i] == null) {
                continue;   // todo: test
            }
            // System.out.println("SNAP: pre-join gap ended removals for split " + i);
            // fromRemovals[i].prettyPrint();
        }
        to.encodedRemovals = GapEncodedVariableLengthIntegerReader.join(fromRemovals, to.maxOrdinal);

        // TODO: remove
        // System.out.println("SNAP: joined gap ended removals:");
        // System.out.println("SNAP: to.maxOrdinal was " + to.maxOrdinal);
        // to.encodedRemovals.prettyPrint();

        for (HollowObjectTypeDataElements elements : from) {
            if (elements.encodedAdditions != null) {
                throw new UnsupportedOperationException("// SNAP: TODO: We never expect to split/join encodedAdditions- they are accepted from delta as-is");
            }
        }

        to.fixedLengthData = new FixedLengthElementArray(to.memoryRecycler, (long)to.bitsPerRecord * (to.maxOrdinal + 1));  // TODO: add to FxiedLengthDataFactory to support non-heap modes
        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(from[0].varLengthData[fieldIdx] != null) {
                to.varLengthData[fieldIdx] = VariableLengthDataFactory.get(to.memoryMode, to.memoryRecycler);
            }
        }

        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            copyRecord(ordinal, to, from, fromMask, fromOrdinalShift, currentWriteVarLengthDataPointers);
        }

        return to;
    }

    void populateStats(HollowObjectTypeDataElements to, HollowObjectTypeDataElements[] from) {
        long[] varLengthSizes = new long[to.schema.numFields()];

        to.maxOrdinal = -1;
        for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
            for(int ordinal=0;ordinal<=from[fromIndex].maxOrdinal;ordinal++) {
                for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
                    if(from[fromIndex].varLengthData[fieldIdx] != null) {
                        varLengthSizes[fieldIdx] += varLengthSize(from[fromIndex], ordinal, fieldIdx);
                    }
                }
            }
            to.maxOrdinal+= from[fromIndex].maxOrdinal + 1;
        }

        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(from[0].varLengthData[fieldIdx] == null) {
                to.bitsPerField[fieldIdx] = from[0].bitsPerField[fieldIdx];
            } else {
                to.bitsPerField[fieldIdx] = (64 - Long.numberOfLeadingZeros(varLengthSizes[fieldIdx] + 1)) + 1;
            }
            to.nullValueForField[fieldIdx] = to.bitsPerField[fieldIdx] == 64 ? -1L : (1L << to.bitsPerField[fieldIdx]) - 1; // SNAP: here too, can just copy over?
            to.bitOffsetPerField[fieldIdx] = to.bitsPerRecord;
            to.bitsPerRecord += to.bitsPerField[fieldIdx];
        }

        to.bitsPerUnfilteredField = from[0].bitsPerUnfilteredField;
        to.unfilteredFieldIsIncluded = from[0].unfilteredFieldIsIncluded;
    }

    private void copyRecord(int ordinal, HollowObjectTypeDataElements to, HollowObjectTypeDataElements[] from, int fromMask, int fromOrdinalShift, long[] currentWriteVarLengthDataPointers) {
        int fromIndex = ordinal & fromMask;
        int fromOrdinal = ordinal >> fromOrdinalShift;

        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(to.varLengthData[fieldIdx] == null) {
                long value = from[fromIndex].fixedLengthData.getLargeElementValue(((long)fromOrdinal * from[fromIndex].bitsPerRecord) + from[fromIndex].bitOffsetPerField[fieldIdx], from[fromIndex].bitsPerField[fieldIdx]);
                to.fixedLengthData.setElementValue(((long)ordinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIdx], to.bitsPerField[fieldIdx], value);
            } else {
                long fromStartByte = varLengthStartByte(from[fromIndex], fromOrdinal, fieldIdx);
                long fromEndByte = varLengthEndByte(from[fromIndex], fromOrdinal, fieldIdx);
                long size = fromEndByte - fromStartByte;

                to.fixedLengthData.setElementValue(((long)ordinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIdx], to.bitsPerField[fieldIdx], currentWriteVarLengthDataPointers[fieldIdx] + size);
                to.varLengthData[fieldIdx].copy(from[fromIndex].varLengthData[fieldIdx], fromStartByte, currentWriteVarLengthDataPointers[fieldIdx], size);

                currentWriteVarLengthDataPointers[fieldIdx] += size;
            }
        }
    }
}
