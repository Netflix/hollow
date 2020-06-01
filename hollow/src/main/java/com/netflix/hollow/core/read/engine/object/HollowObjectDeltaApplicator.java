/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

/**
 * This class contains the logic for applying a delta to a current OBJECT type state
 * to produce the next OBJECT type state.
 * 
 * Not intended for external consumption.
 */
class HollowObjectDeltaApplicator {

    private final HollowObjectTypeDataElements from;
    private final HollowObjectTypeDataElements delta;
    private final HollowObjectTypeDataElements target;

    long currentDeltaStateReadFixedLengthStartBit = 0;
    long currentFromStateReadFixedLengthStartBit = 0;
    long currentWriteFixedLengthStartBit = 0;

    long currentDeltaReadVarLengthDataPointers[];
    long currentFromStateReadVarLengthDataPointers[];
    long currentWriteVarLengthDataPointers[];

    int deltaFieldIndexMapping[];

    GapEncodedVariableLengthIntegerReader removalsReader;
    GapEncodedVariableLengthIntegerReader additionsReader;

    int numMergeFields = 0;

    public HollowObjectDeltaApplicator(HollowObjectTypeDataElements from, HollowObjectTypeDataElements delta, HollowObjectTypeDataElements target) {
        this.from = from;
        this.delta = delta;
        this.target = target;
    }

    void applyDelta() {
        removalsReader = from.encodedRemovals == null ? GapEncodedVariableLengthIntegerReader.EMPTY_READER : from.encodedRemovals;
        additionsReader = delta.encodedAdditions;
        removalsReader.reset();
        additionsReader.reset();

        target.encodedRemovals = delta.encodedRemovals;

        target.maxOrdinal = delta.maxOrdinal;

        deltaFieldIndexMapping = new int[target.bitsPerField.length];

        for(int i=0;i<target.bitsPerField.length;i++) {
            deltaFieldIndexMapping[i] = delta.schema.getPosition(target.schema.getFieldName(i));
        }

        for(int i=0;i<target.bitsPerField.length;i++) {
            target.bitsPerField[i] = deltaFieldIndexMapping[i] == -1 ? from.bitsPerField[i] : delta.bitsPerField[deltaFieldIndexMapping[i]];
            target.nullValueForField[i] = (1L << target.bitsPerField[i]) - 1;
            target.bitOffsetPerField[i] = target.bitsPerRecord;
            target.bitsPerRecord += target.bitsPerField[i];
            if(target.bitsPerField[i] != 0)
                numMergeFields = i+1;
        }

        target.fixedLengthData = new FixedLengthElementArray(target.memoryRecycler, (long)target.bitsPerRecord * (target.maxOrdinal + 1));

        for(int i=0;i<target.schema.numFields();i++) {
            if(target.schema.getFieldType(i) == FieldType.STRING || target.schema.getFieldType(i) == FieldType.BYTES) {
                target.varLengthData[i] = new SegmentedByteArray(target.memoryRecycler);
            }
        }

        currentDeltaReadVarLengthDataPointers = new long[target.varLengthData.length];
        currentFromStateReadVarLengthDataPointers = new long[target.varLengthData.length];
        currentWriteVarLengthDataPointers = new long[target.varLengthData.length];

        if(canDoFastDelta())
            fastDelta();
        else
            slowDelta();

        from.encodedRemovals = null;
        removalsReader.destroy();
        additionsReader.destroy();
    }

    private boolean canDoFastDelta() {
        for(int i=0;i<target.bitsPerField.length;i++) {
            if(target.bitsPerField[i] != from.bitsPerField[i])
                return false;
        }
        return true;
    }

    private void fastDelta() {
        int i = 0;
        int bulkCopyEndOrdinal = Math.min(from.maxOrdinal, target.maxOrdinal);

        while(i <= target.maxOrdinal) {
            int nextElementDiff = Math.min(additionsReader.nextElement(), removalsReader.nextElement());

            if(nextElementDiff == i || i > bulkCopyEndOrdinal) {
                mergeOrdinal(i++);
            } else {
                int recordsToCopy = nextElementDiff - i;
                if(nextElementDiff > bulkCopyEndOrdinal)
                    recordsToCopy = bulkCopyEndOrdinal - i + 1;

                fastCopyRecords(recordsToCopy);

                i += recordsToCopy;
            }
        }
    }

    private void fastCopyRecords(int recordsToCopy) {
        long fixedLengthBitsToCopy = (long)from.bitsPerRecord * recordsToCopy;

        target.fixedLengthData.copyBits(from.fixedLengthData, currentFromStateReadFixedLengthStartBit, currentWriteFixedLengthStartBit, fixedLengthBitsToCopy);

        currentFromStateReadFixedLengthStartBit += fixedLengthBitsToCopy;

        for(int i=0;i<from.schema.numFields();i++) {
            if(target.varLengthData[i] != null) {
                long fromEndByte = from.fixedLengthData.getElementValue(currentFromStateReadFixedLengthStartBit - from.bitsPerRecord + from.bitOffsetPerField[i], from.bitsPerField[i]);
                fromEndByte &= (from.nullValueForField[i] >>> 1);
                long varLengthToCopy = fromEndByte - currentFromStateReadVarLengthDataPointers[i];
                long varLengthDiff = currentWriteVarLengthDataPointers[i] - currentFromStateReadVarLengthDataPointers[i];

                target.varLengthData[i].orderedCopy(from.varLengthData[i], currentFromStateReadVarLengthDataPointers[i], currentWriteVarLengthDataPointers[i], varLengthToCopy);
                target.fixedLengthData.incrementMany(currentWriteFixedLengthStartBit + from.bitOffsetPerField[i], varLengthDiff, from.bitsPerRecord, recordsToCopy);

                currentFromStateReadVarLengthDataPointers[i] += varLengthToCopy;
                currentWriteVarLengthDataPointers[i] += varLengthToCopy;
            }
        }

        currentWriteFixedLengthStartBit += fixedLengthBitsToCopy;
    }

    private void slowDelta() {
        for(int i=0;i<=target.maxOrdinal;i++) {
            mergeOrdinal(i);
        }
    }

    private void mergeOrdinal(int i) {
        boolean addFromDelta = additionsReader.nextElement() == i;
        boolean removeData = removalsReader.nextElement() == i;

        for(int fieldIndex=0;fieldIndex<numMergeFields;fieldIndex++) {
            int deltaFieldIndex = deltaFieldIndexMapping[fieldIndex];

            if(addFromDelta) {
                addFromDelta(removeData, fieldIndex, deltaFieldIndex);

            } else {
                if(i <= from.maxOrdinal) {
                    long readStartBit = currentFromStateReadFixedLengthStartBit + from.bitOffsetPerField[fieldIndex];
                    copyRecordField(fieldIndex, fieldIndex, from, readStartBit, currentWriteFixedLengthStartBit, currentFromStateReadVarLengthDataPointers, currentWriteVarLengthDataPointers, removeData);
                } else if(target.varLengthData[fieldIndex] != null) {
                	writeNullVarLengthField(fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
                }
            }
            currentWriteFixedLengthStartBit += target.bitsPerField[fieldIndex];
        }

        if(addFromDelta) {
            currentDeltaStateReadFixedLengthStartBit += delta.bitsPerRecord;
            additionsReader.advance();
        }
        currentFromStateReadFixedLengthStartBit += from.bitsPerRecord;

        if(removeData)
            removalsReader.advance();
    }

    private void addFromDelta(boolean removeData, int fieldIndex, int deltaFieldIndex) {
        if(deltaFieldIndex == -1) {
            writeNullField(fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
        } else {
            long readStartBit = currentDeltaStateReadFixedLengthStartBit + delta.bitOffsetPerField[deltaFieldIndex];
            copyRecordField(fieldIndex, deltaFieldIndex, delta, readStartBit, currentWriteFixedLengthStartBit, currentDeltaReadVarLengthDataPointers, currentWriteVarLengthDataPointers, false);
        }

        /// skip over var length data in from state, if removed.
        if(removeData && target.varLengthData[fieldIndex] != null) {
            long readValue = from.fixedLengthData.getElementValue(currentFromStateReadFixedLengthStartBit + from.bitOffsetPerField[fieldIndex], from.bitsPerField[fieldIndex]);
            if((readValue & (1L << (from.bitsPerField[fieldIndex] - 1))) == 0)
                currentFromStateReadVarLengthDataPointers[fieldIndex] = readValue;
        }
    }

    private void copyRecordField(int fieldIndex, int fromFieldIndex, HollowObjectTypeDataElements copyFromData, long currentReadFixedLengthStartBit, long currentWriteFixedLengthStartBit, long[] currentReadVarLengthDataPointers, long[] currentWriteVarLengthDataPointers, boolean removeData) {
        long readValue = copyFromData.bitsPerField[fromFieldIndex] > 56 ?
                copyFromData.fixedLengthData.getLargeElementValue(currentReadFixedLengthStartBit, copyFromData.bitsPerField[fromFieldIndex])
                : copyFromData.fixedLengthData.getElementValue(currentReadFixedLengthStartBit, copyFromData.bitsPerField[fromFieldIndex]);

        if(target.varLengthData[fieldIndex] != null) {
            if((readValue & (1L << (copyFromData.bitsPerField[fromFieldIndex] - 1))) != 0) {
                writeNullVarLengthField(fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
            } else {
                long readStart = currentReadVarLengthDataPointers[fieldIndex];
                long length = readValue - readStart;
                if(!removeData) {
                    long writeStart = currentWriteVarLengthDataPointers[fieldIndex];
                    target.varLengthData[fieldIndex].orderedCopy(copyFromData.varLengthData[fromFieldIndex], readStart, writeStart, length);
                    currentWriteVarLengthDataPointers[fieldIndex] += length;
                }
                target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], currentWriteVarLengthDataPointers[fieldIndex]);
                currentReadVarLengthDataPointers[fieldIndex] = readValue;
            }
        } else if(!removeData) {
            if(readValue == copyFromData.nullValueForField[fromFieldIndex])
                writeNullFixedLengthField(fieldIndex, currentWriteFixedLengthStartBit);
            else
                target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], readValue);
        }
    }

    private void writeNullField(int fieldIndex, long currentWriteFixedLengthStartBit, long[] currentWriteVarLengthDataPointers) {
        if(target.varLengthData[fieldIndex] != null) {
            writeNullVarLengthField(fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
        } else {
            writeNullFixedLengthField(fieldIndex, currentWriteFixedLengthStartBit);
        }
    }

    private void writeNullVarLengthField(int fieldIndex, long currentWriteFixedLengthStartBit, long[] currentWriteVarLengthDataPointers) {
        long writeValue = (1L << (target.bitsPerField[fieldIndex] - 1)) | currentWriteVarLengthDataPointers[fieldIndex];
        target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], writeValue);
    }

    private void writeNullFixedLengthField(int fieldIndex, long currentWriteFixedLengthStartBit) {
        target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], target.nullValueForField[fieldIndex]);
    }


}
