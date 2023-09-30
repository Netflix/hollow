package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader.EMPTY_READER;

import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.ArrayList;
import java.util.List;

public class HollowObjectTypeDataElementsSplitter {
    private final HollowObjectTypeDataElements from;
    private final int numSplits;
    private final int toMask;
    private final int toOrdinalShift;

    long[][] currentWriteVarLengthDataPointers;

    public HollowObjectTypeDataElementsSplitter(HollowObjectTypeDataElements originalDataElements, int numSplits) {
        this.from = originalDataElements;
        this.numSplits = numSplits;
        this.toMask = numSplits - 1;
        this.toOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);

        if (!(numSplits>0) || !((numSplits&(numSplits-1))==0)) {
            throw new UnsupportedOperationException("Must split by power of 2");
        }
    }

    public HollowObjectTypeDataElements[] split() { // TODO: package private
        HollowObjectTypeDataElements[] to = new HollowObjectTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowObjectTypeDataElements(from.schema, from.memoryMode, from.memoryRecycler);
        }
        currentWriteVarLengthDataPointers = new long[numSplits][from.schema.numFields()];

        populateStats(to, from);

        // split gap encoded var length removals
        if (from.encodedRemovals != null) {
            // splits from.encodedRemovals to to[i].encodedRemovals, creating ByteDataArrays for to[i], and does not clean up from.encodedRemovals.getUnderyingArray()
            copyEncodedRemovals(to, from);
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
            copyRecord(i, to);
        }
        return to;
    }

    void populateStats(HollowObjectTypeDataElements[] to, HollowObjectTypeDataElements from) {
        long[][] varLengthSizes = new long[to.length][from.schema.numFields()];

        for(int ordinal=0;ordinal<=from.maxOrdinal;ordinal++) {    // TODO: verify we're always inclusive of maxOrdinal everywhere else
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            to[toIndex].maxOrdinal = toOrdinal; // note not thread-safe but delta thread is probably the only one that needs to see maxOrdinal per split or shard
            for(int fieldIdx=0;fieldIdx<from.schema.numFields();fieldIdx++) {
                if(from.varLengthData[fieldIdx] != null) {
                    varLengthSizes[toIndex][fieldIdx] += varLengthSize(ordinal, fieldIdx);
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
                to[toIndex].nullValueForField[fieldIdx] = (1L << to[toIndex].bitsPerField[fieldIdx]) - 1;
                to[toIndex].bitOffsetPerField[fieldIdx] = to[toIndex].bitsPerRecord;
                to[toIndex].bitsPerRecord += to[toIndex].bitsPerField[fieldIdx];
            }
        }
        // TODO: what about unfilteredFieldIsIncluded and bitsPerUnfilteredField, do we need to worry about those here?
    }

    private long varLengthSize(int ordinal, int fieldIdx) {
        int numBitsForField = from.bitsPerField[fieldIdx];
        long fromBitOffset = ((long)from.bitsPerRecord*ordinal) + from.bitOffsetPerField[fieldIdx];
        long fromEndByte = from.fixedLengthData.getElementValue(fromBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;
        long fromStartByte = ordinal != 0 ? from.fixedLengthData.getElementValue(fromBitOffset - from.bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1 : 0;
        return fromEndByte - fromStartByte;
    }

    private void copyRecord(int ordinal, HollowObjectTypeDataElements[] to) {
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

    private void copyEncodedRemovals(HollowObjectTypeDataElements[] to, HollowObjectTypeDataElements from) {
        GapEncodedVariableLengthIntegerReader preSplitRemovals = from.encodedRemovals;
        System.out.println("SNAP: pre-split gap ended removals");
        preSplitRemovals.diagResetAndPrint();
        System.out.println("SNAP: from.maxOrdinal was " + from.maxOrdinal);

        List<Integer> ordinals = new ArrayList<>();
        // if (preSplitRemovals.equals(EMPTY_READER)) // TODO: Test this case, and what about null?
        preSplitRemovals.reset();
        while(preSplitRemovals.nextElement() != Integer.MAX_VALUE) {
            ordinals.add(preSplitRemovals.nextElement());
            preSplitRemovals.advance();
        }

        ByteDataArray[] splitOrdinals = new ByteDataArray[numSplits];
        int previousSplitOrdinal[] = new int[numSplits];
        for(int i=0;i<numSplits;i++) {
            to[i].encodedRemovals = EMPTY_READER;
            splitOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        }
        for (int ordinal : ordinals) {
            int toIndex = ordinal & toMask;
            int toOrdinal = ordinal >> toOrdinalShift;
            VarInt.writeVInt(splitOrdinals[toIndex], toOrdinal - previousSplitOrdinal[toIndex]);
            previousSplitOrdinal[toIndex] = toOrdinal;
        }
        for(int i=0;i<numSplits;i++) {
            to[i].encodedRemovals = new GapEncodedVariableLengthIntegerReader(splitOrdinals[i].getUnderlyingArray(), (int)splitOrdinals[i].length());
            System.out.println("SNAP: post-split gap ended removals for split " + i);
            to[i].encodedRemovals.diagResetAndPrint();
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
