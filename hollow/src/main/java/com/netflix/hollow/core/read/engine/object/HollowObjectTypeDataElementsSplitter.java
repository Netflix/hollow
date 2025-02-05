package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.copyRecord;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitter;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * Split a {@code HollowObjectTypeDataElements} into multiple {@code HollowObjectTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowObjectTypeDataElementsSplitter extends HollowTypeDataElementsSplitter<HollowObjectTypeDataElements> {
    private HollowObjectSchema schema;

    public HollowObjectTypeDataElementsSplitter(HollowObjectTypeDataElements from, int numSplits) {
        super(from, numSplits);
        this.schema = from.schema;
    }

    @Override
    public void initToElements() {
        this.to = new HollowObjectTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowObjectTypeDataElements(schema, from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
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
                to[toIndex].nullValueForField[fieldIdx] = (to[toIndex].bitsPerField[fieldIdx] == 64) ? -1L : (1L << to[toIndex].bitsPerField[fieldIdx]) - 1;
                to[toIndex].bitOffsetPerField[fieldIdx] = to[toIndex].bitsPerRecord;
                to[toIndex].bitsPerRecord += to[toIndex].bitsPerField[fieldIdx];

                // unused
                // to[toIndex].bitsPerUnfilteredField = from.bitsPerUnfilteredField;
                // to[toIndex].unfilteredFieldIsIncluded = from.unfilteredFieldIsIncluded;
            }
        }

        System.out.println("Hi split");
    }

    @Override
    public void copyRecords() {
        final long[][] currentWriteVarLengthDataPointers = new long[to.length][from.schema.numFields()];

        for(int i=0;i<to.length;i++) {
            to[i].fixedLengthData = FixedLengthDataFactory.get((long)to[i].bitsPerRecord * (to[i].maxOrdinal + 1), to[i].memoryMode, to[i].memoryRecycler);
            for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
                if(from.varLengthData[fieldIdx] != null) {
                    to[i].varLengthData[fieldIdx] = VariableLengthDataFactory.get(from.memoryMode, from.memoryRecycler);
                }
            }
        }

        for(int i=0;i<=from.maxOrdinal;i++) {
            int toIndex = i & toMask;
            int toOrdinal = i >> toOrdinalShift;
            copyRecord(to[toIndex], toOrdinal, from, i, currentWriteVarLengthDataPointers[toIndex]);
        }
    }
}
