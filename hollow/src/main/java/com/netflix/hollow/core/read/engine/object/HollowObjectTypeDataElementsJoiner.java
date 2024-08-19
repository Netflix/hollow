package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.copyRecord;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.writeNullField;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;
import com.netflix.hollow.core.schema.HollowObjectSchema;


/**
 * Join multiple {@code HollowObjectTypeDataElements}s into 1 {@code HollowObjectTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowObjectTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner {

    private HollowObjectSchema schema;
    private HollowObjectTypeDataElements to;

    public HollowObjectTypeDataElementsJoiner(HollowObjectTypeDataElements[] from) {
        super(from);
        this.schema = from[0].schema;
    }

    @Override
    public void init() {
        this.to = new HollowObjectTypeDataElements(schema, from[0].memoryMode, from[0].memoryRecycler);
    }

    @Override
    public void populateStats() {
        HollowObjectTypeDataElements[] from = (HollowObjectTypeDataElements[]) this.from;

        long[] varLengthSizes = new long[to.schema.numFields()];

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

    @Override
    public void copyRecords() {
        HollowObjectTypeDataElements[] from = (HollowObjectTypeDataElements[]) this.from;

        long[] currentWriteVarLengthDataPointers = new long[from[0].schema.numFields()];

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

    }

    private void writeNullRecord(HollowObjectTypeDataElements to, int toOrdinal, long[] currentWriteVarLengthDataPointers) {
        for(int fieldIndex=0;fieldIndex<to.schema.numFields();fieldIndex++) {
            long currentWriteFixedLengthStartBit = ((long)toOrdinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIndex];
            writeNullField(to, fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
        }
    }
}
