package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.copyRecord;
import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements.varLengthSize;

import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElements;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowObjectTypeDataElements} into multiple {@code HollowObjectTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * // SNAP: TODO: single-shot, split() not expected to be called repeatedly
 * {@code numSplits} must be a power of 2.
 */
public class HollowObjectTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter {

    HollowObjectTypeDataElementsSplitter(HollowObjectTypeDataElements from, int numSplits) {
        super(from, numSplits);
        this.to = new HollowObjectTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowObjectTypeDataElements(from.schema, from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {
        HollowObjectTypeDataElements[] to = (HollowObjectTypeDataElements[])this.to;
        HollowObjectTypeDataElements from = (HollowObjectTypeDataElements) this.from;

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
    }

    @Override
    public void copyRecords() {
        HollowObjectTypeDataElements[] to = (HollowObjectTypeDataElements[])this.to;
        HollowObjectTypeDataElements from = (HollowObjectTypeDataElements) this.from;

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
