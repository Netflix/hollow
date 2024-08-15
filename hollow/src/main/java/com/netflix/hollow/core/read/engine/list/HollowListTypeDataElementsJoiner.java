package com.netflix.hollow.core.read.engine.list;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.copyRecord;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.writeNullField;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements;


/**
 * Join multiple {@code HollowObjectTypeDataElements}s into 1 {@code HollowObjectTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowListTypeDataElementsJoiner {

    HollowObjectTypeDataElements join(HollowObjectTypeDataElements[] from) {
        final int fromMask = from.length - 1;
        final int fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);
        long[] currentWriteVarLengthDataPointers;

        if (from.length<=0 || !((from.length&(from.length-1))==0)) {
            throw new IllegalStateException("No. of DataElements to be joined must be a power of 2");
        }

        HollowObjectTypeDataElements to = new HollowObjectTypeDataElements(from[0].schema, from[0].memoryMode, from[0].memoryRecycler);
        currentWriteVarLengthDataPointers = new long[from[0].schema.numFields()];

        populateStats(to, from);

        GapEncodedVariableLengthIntegerReader[] fromRemovals = new GapEncodedVariableLengthIntegerReader[from.length];
        for (int i=0;i<from.length;i++) {
            fromRemovals[i] = from[i].encodedRemovals;
        }
        to.encodedRemovals = GapEncodedVariableLengthIntegerReader.join(fromRemovals);

        for (HollowObjectTypeDataElements elements : from) {
            if (elements.encodedAdditions != null) {
                throw new IllegalStateException("Encountered encodedAdditions in data elements joiner- this is not expected " +
                        "since encodedAdditions only exist on delta data elements and they dont carry over to target data elements, " +
                        "delta data elements are never split/joined");
            }
        }

        for(int ordinal=0;ordinal<=to.maxOrdinal;ordinal++) {
            int fromIndex = ordinal & fromMask;
            int fromOrdinal = ordinal >> fromOrdinalShift;

            if (fromOrdinal <= from[fromIndex].maxOrdinal) {
                copyRecord(to, ordinal, from[fromIndex], fromOrdinal, currentWriteVarLengthDataPointers);
            } else {
                // lopsided shards could result for consumers that skip type shards with no additions
                writeNullRecord(to, ordinal, currentWriteVarLengthDataPointers);
            }
        }

        return to;
    }

    private void writeNullRecord(HollowObjectTypeDataElements to, int toOrdinal, long[] currentWriteVarLengthDataPointers) {
        for(int fieldIndex=0;fieldIndex<to.schema.numFields();fieldIndex++) {
            long currentWriteFixedLengthStartBit = ((long)toOrdinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIndex];
            writeNullField(to, fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
        }
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
            int mappedMaxOrdinal = from[fromIndex].maxOrdinal == -1 ? -1 : (from[fromIndex].maxOrdinal * from.length) + fromIndex;
            to.maxOrdinal = Math.max(to.maxOrdinal, mappedMaxOrdinal);
        }

        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            for(int i=0;i<from.length;i++) {
                if(from[i].varLengthData[fieldIdx] != null) { // if any of the join candidates have var len data set for this field
                    to.varLengthData[fieldIdx] = VariableLengthDataFactory.get(to.memoryMode, to.memoryRecycler);
                    break;
                }
            }
        }

        for(int fieldIdx=0;fieldIdx<to.schema.numFields();fieldIdx++) {
            if(to.varLengthData[fieldIdx] == null) {
                // do not assume bitsPerField will be uniform
                for(int fromIndex=0;fromIndex<from.length;fromIndex++) {
                    to.bitsPerField[fieldIdx] = Math.max(to.bitsPerField[fieldIdx], from[fromIndex].bitsPerField[fieldIdx]);
                }
            } else {
                to.bitsPerField[fieldIdx] = (64 - Long.numberOfLeadingZeros(varLengthSizes[fieldIdx] + 1)) + 1;
            }
            to.nullValueForField[fieldIdx] = to.bitsPerField[fieldIdx] == 64 ? -1L : (1L << to.bitsPerField[fieldIdx]) - 1;
            to.bitOffsetPerField[fieldIdx] = to.bitsPerRecord;
            to.bitsPerRecord += to.bitsPerField[fieldIdx];
        }
        to.fixedLengthData = FixedLengthDataFactory.get((long)to.bitsPerRecord * (to.maxOrdinal + 1), to.memoryMode, to.memoryRecycler);

        // unused
        //  to.bitsPerUnfilteredField
        //  to.unfilteredFieldIsIncluded
    }
}
