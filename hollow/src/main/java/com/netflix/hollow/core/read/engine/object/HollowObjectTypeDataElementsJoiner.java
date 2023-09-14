package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;

public class HollowObjectTypeDataElementsJoiner {
    private final HollowObjectTypeDataElements[] from;
    private final int fromMask;
    private final int fromOrdinalShift;

    public HollowObjectTypeDataElementsJoiner(HollowObjectTypeDataElements[] originalDataElements) {
        this.from = originalDataElements;
        this.fromMask = originalDataElements.length - 1;
        this.fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(originalDataElements.length);

        if (originalDataElements.length != 2) {
            throw new UnsupportedOperationException("Only joining 2 at a time is supported");  // FUTURE: remove this limitation
        }
    }

    HollowObjectTypeDataElements join() {
        HollowObjectTypeDataElements to = new HollowObjectTypeDataElements(from[0].schema, from[0].memoryMode, from[0].memoryRecycler);

        populateStats(to, from);

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
                for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {    // TODO: validate that all schemas in from[] match
                    if(from[fromIndex].varLengthData[fieldIdx] != null) {
                        varLengthSizes[fieldIdx] += varLengthSize(from[fromIndex], ordinal, fieldIdx);
                    }
                }
            }
            to.maxOrdinal+= from[fromIndex].maxOrdinal + 1; // note not thread-safe but delta thread is probably the only one that needs to see maxOrdinal per split or shard
        }


        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(from[0].varLengthData[fieldIdx] == null) {
                to.bitsPerField[fieldIdx] = from[0].bitsPerField[fieldIdx]; // TODO: validate this should be same across all elements in from[]
            } else {
                to.bitsPerField[fieldIdx] = (64 - Long.numberOfLeadingZeros(varLengthSizes[fieldIdx] + 1)) + 1;
            }
            to.nullValueForField[fieldIdx] = (1L << to.bitsPerField[fieldIdx]) - 1;
            to.bitOffsetPerField[fieldIdx] = to.bitsPerRecord;
            to.bitsPerRecord += to.bitsPerField[fieldIdx];
        }
        // TODO: what about unfilteredFieldIsIncluded and bitsPerUnfilteredField, do we need to worry about those here?
    }

    private long varLengthSize(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        int numBitsForField = from.bitsPerField[fieldIdx];
        long fromBitOffset = ((long)from.bitsPerRecord*ordinal) + from.bitOffsetPerField[fieldIdx];
        long fromEndByte = from.fixedLengthData.getElementValue(fromBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;
        long fromStartByte = ordinal != 0 ? from.fixedLengthData.getElementValue(fromBitOffset - from.bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1 : 0;
        return fromEndByte - fromStartByte;
    }

    private void copyRecord(int ordinal, HollowObjectTypeDataElements to) {
        int fromIndex = ordinal & fromMask;
        int fromOrdinal = ordinal >> fromOrdinalShift;

        long[] currentWriteVarLengthDataPointers = new long[to.schema.numFields()];

        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(to.varLengthData[fieldIdx] == null) {
                long value = from[fromIndex].fixedLengthData.getLargeElementValue(((long)ordinal * from[fromIndex].bitsPerRecord) + from[fromIndex].bitOffsetPerField[fieldIdx], from[fromIndex].bitsPerField[fieldIdx]);
                to.fixedLengthData.setElementValue(((long)ordinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIdx], to.bitsPerField[fieldIdx], value);
            } else {
                long fromStartByte = varLengthStartByte(from[fromIndex], ordinal, fieldIdx);
                long fromEndByte = varLengthEndByte(from[fromIndex], ordinal, fieldIdx);
                long size = fromEndByte - fromStartByte;

                to.fixedLengthData.setElementValue(((long)ordinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIdx], to.bitsPerField[fieldIdx], currentWriteVarLengthDataPointers[fieldIdx] + size);
                to.varLengthData[fieldIdx].copy(from[fromIndex].varLengthData[fieldIdx], fromStartByte, currentWriteVarLengthDataPointers[fieldIdx], size);

                currentWriteVarLengthDataPointers[fieldIdx] += size;
            }
        }
    }

    private long varLengthStartByte(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        if(ordinal == 0)
            return 0;

        int numBitsForField = from.bitsPerField[fieldIdx];
        long currentBitOffset = ((long)from.bitsPerRecord * ordinal) + from.bitOffsetPerField[fieldIdx];
        long startByte = from.fixedLengthData.getElementValue(currentBitOffset - from.bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return startByte;
    }

    private long varLengthEndByte(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        int numBitsForField = from.bitsPerField[fieldIdx];
        long currentBitOffset = ((long)from.bitsPerRecord * ordinal) + from.bitOffsetPerField[fieldIdx];
        long endByte = from.fixedLengthData.getElementValue(currentBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return endByte;
    }
}
