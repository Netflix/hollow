package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthEndByte;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthStartByte;

import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

public class HollowObjectTypeDataElementsJoiner {
    private final HollowObjectTypeDataElements[] from;
    private final int fromMask;
    private final int fromOrdinalShift;

    private long[] currentWriteVarLengthDataPointers;

    public HollowObjectTypeDataElementsJoiner(HollowObjectTypeDataElements[] originalDataElements) {
        this.from = originalDataElements;
        this.fromMask = originalDataElements.length - 1;
        this.fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(originalDataElements.length);

        int numElements = originalDataElements.length;
        if (!(numElements>0) || !((numElements&(numElements-1))==0)) {
            throw new UnsupportedOperationException("No. of DataElements to be joined must be a power of 2");
        }
    }

    public HollowObjectTypeDataElements join() {    // TODO: package private
        HollowObjectTypeDataElements to = new HollowObjectTypeDataElements(from[0].schema, from[0].memoryMode, from[0].memoryRecycler);

        populateStats(to, from);

        GapEncodedVariableLengthIntegerReader[] fromRemovals = new GapEncodedVariableLengthIntegerReader[from.length];
        for (int i=0;i<from.length;i++) {
            fromRemovals[i] = from[i].encodedRemovals;

            // TODO: remove
            if (fromRemovals[i] == null) {
                continue;   // todo: test
            }
            System.out.println("SNAP: pre-join gap ended removals for split " + i);
            fromRemovals[i].prettyPrint();
        }
        to.encodedRemovals = GapEncodedVariableLengthIntegerReader.join(fromRemovals, to.maxOrdinal);

        // TODO: remove
        System.out.println("SNAP: joined gap ended removals:");
        System.out.println("SNAP: to.maxOrdinal was " + to.maxOrdinal);
        to.encodedRemovals.prettyPrint();

        for (HollowObjectTypeDataElements elements : from) {
            if (elements.encodedAdditions != null) {
                throw new UnsupportedOperationException("// SNAP: TODO: We never expect to split/join encodedAdditions- they are accepted from delta as-is");
            }
        }

        currentWriteVarLengthDataPointers = new long[to.schema.numFields()];
        to.fixedLengthData = new FixedLengthElementArray(to.memoryRecycler, (long)to.bitsPerRecord * (to.maxOrdinal + 1));  // TODO: add to FxiedLengthDataFactory to support non-heap modes
        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(from[0].varLengthData[fieldIdx] != null) {
                to.varLengthData[fieldIdx] = VariableLengthDataFactory.get(to.memoryMode, to.memoryRecycler);
            }
        }

        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            copyRecord(ordinal, to);
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

    private void copyRecord(int ordinal, HollowObjectTypeDataElements to) {
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
