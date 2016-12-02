/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowObjectTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length set data
    private FieldStatistics fieldStats;

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private FixedLengthElementArray fixedLengthLongArray;
    private ByteDataBuffer varLengthByteArrays[];
    private long recordBitOffset;

    /// additional data required for writing delta
    private ByteDataBuffer deltaAddedOrdinals;
    private ByteDataBuffer deltaRemovedOrdinals;

    public HollowObjectTypeWriteState(HollowObjectSchema schema) {
        super(schema);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    /**
     * Called to perform a state transition.<p>
     *
     * Precondition: We are adding objects to this state engine.<br>
     * Postcondition: We are writing the previously added objects to a FastBlob.
     *
     */
    @Override
    public void prepareForWrite() {
        super.prepareForWrite();

        fieldStats = new FieldStatistics(getSchema());

        int maxOrdinal = ordinalMap.maxOrdinal();

        for(int i=0;i<=maxOrdinal;i++) {
            discoverObjectFieldStatisticsForRecord(fieldStats, i);
        }

        fieldStats.completeCalculations();
    }

    private void discoverObjectFieldStatisticsForRecord(FieldStatistics fieldStats, int ordinal) {
        if(currentCyclePopulated.get(ordinal) || previousCyclePopulated.get(ordinal)) {
            long pointer = ordinalMap.getPointerForData(ordinal);

            for(int fieldIndex=0; fieldIndex<((HollowObjectSchema)schema).numFields(); fieldIndex++) {
                pointer = discoverObjectFieldStatisticsForField(fieldStats, pointer, fieldIndex);
            }
        }
    }

    private long discoverObjectFieldStatisticsForField(FieldStatistics fieldStats, long pointer, int fieldIndex) {
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        switch(getSchema().getFieldType(fieldIndex)) {
        case BOOLEAN:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 2);
            pointer += 1;
            break;
        case FLOAT:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 32);
            pointer += 4;
            break;
        case DOUBLE:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 64);
            pointer += 8;
            break;
        case LONG:
        case INT:
        case REFERENCE:
            if(VarInt.readVNull(data, pointer)) {
               addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 1);
               pointer += 1;
            } else {
                long vLong = VarInt.readVLong(data, pointer);
                int requiredBitsForFieldValue = 64 - Long.numberOfLeadingZeros(vLong + 1);
                addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, requiredBitsForFieldValue);
                pointer += VarInt.sizeOfVLong(vLong);
            }
            break;
        case BYTES:
        case STRING:
            if(VarInt.readVNull(data, pointer)) {
                addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 1);
                pointer += 1;
            } else {
                int length = VarInt.readVInt(data, pointer);
                addVarLengthFieldSizeInBytes(fieldStats, fieldIndex, length);
                pointer += length + VarInt.sizeOfVInt(length);
            }
            break;
        }
        return pointer;
    }

    private void addFixedLengthFieldRequiredBits(FieldStatistics fieldStats, int fieldIndex, int numBits) {
        fieldStats.addFixedLengthFieldRequiredBits(fieldIndex, numBits);
    }

    private void addVarLengthFieldSizeInBytes(FieldStatistics fieldStats, int fieldIndex, int numBytes) {
        fieldStats.addVarLengthFieldSize(fieldIndex, numBytes);
    }

    @Override
    public void prepareForNextCycle() {
        super.prepareForNextCycle();

        fieldStats = null;
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int numBitsPerRecord = fieldStats.getNumBitsPerRecord();

        fixedLengthLongArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numBitsPerRecord * (maxOrdinal + 1));
        varLengthByteArrays = new ByteDataBuffer[getSchema().numFields()];

        recordBitOffset = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i)) {
                addRecord(i, recordBitOffset, fixedLengthLongArray, varLengthByteArrays);
            } else {
                addNullRecord(i, recordBitOffset, fixedLengthLongArray, varLengthByteArrays);
            }
            recordBitOffset += numBitsPerRecord;
        }
    }


    @Override
    public void writeSnapshot(DataOutputStream os) throws IOException {
        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) FixedLength field sizes
        for(int i=0;i<getSchema().numFields();i++) {
            VarInt.writeVInt(os, fieldStats.getMaxBitsForField(i));
        }

        /// 3) FixedLength data
        long numBitsRequired = recordBitOffset;
        long numLongsRequired = recordBitOffset == 0 ? 0 : ((numBitsRequired - 1) / 64) + 1;
        fixedLengthLongArray.writeTo(os, numLongsRequired);

        /// 4) VarLength data
        for(int i=0;i<varLengthByteArrays.length;i++) {
            if(varLengthByteArrays[i] == null) {
                VarInt.writeVLong(os, 0);
            } else {
                VarInt.writeVLong(os, varLengthByteArrays[i].length());
                varLengthByteArrays[i].getUnderlyingArray().writeTo(os, 0, varLengthByteArrays[i].length());
            }
        }

        /// 5) Populated bits
        currentCyclePopulated.serializeBitsTo(os);

        fixedLengthLongArray = null;
        varLengthByteArrays = null;
    }

    @Override
    public void calculateDelta() {
        calculateDelta(previousCyclePopulated, currentCyclePopulated);
    }

    @Override
    public void writeDelta(DataOutputStream dos) throws IOException {
        writeCalculatedDelta(dos);
    }

    @Override
    public void calculateReverseDelta() {
        calculateDelta(currentCyclePopulated, previousCyclePopulated);
    }

    @Override
    public void writeReverseDelta(DataOutputStream dos) throws IOException {
        writeCalculatedDelta(dos);
    }

    private void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated) {
        maxOrdinal = ordinalMap.maxOrdinal();
        int numBitsPerRecord = fieldStats.getNumBitsPerRecord();

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        int numRecordsInDelta = deltaAdditions.cardinality();

        fixedLengthLongArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numRecordsInDelta * numBitsPerRecord);
        deltaAddedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        deltaRemovedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);

        varLengthByteArrays = new ByteDataBuffer[getSchema().numFields()];

        recordBitOffset = 0;

        int previousRemovedOrdinal = 0;
        int previousAddedOrdinal = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(toCyclePopulated.get(i) && !fromCyclePopulated.get(i)) {
                addRecord(i, recordBitOffset, fixedLengthLongArray, varLengthByteArrays);
                recordBitOffset += numBitsPerRecord;
                VarInt.writeVInt(deltaAddedOrdinals, i - previousAddedOrdinal);
                previousAddedOrdinal = i;
            } else if(fromCyclePopulated.get(i) && !toCyclePopulated.get(i)) {
                VarInt.writeVInt(deltaRemovedOrdinals, i - previousRemovedOrdinal);
                previousRemovedOrdinal = i;
            }
        }
    }

    private void writeCalculatedDelta(DataOutputStream os) throws IOException {

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals.length());
        deltaRemovedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals.length());
        VarInt.writeVLong(os, deltaAddedOrdinals.length());
        deltaAddedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals.length());

        /// 3) FixedLength field sizes
        for(int i=0;i<getSchema().numFields();i++) {
            VarInt.writeVInt(os, fieldStats.getMaxBitsForField(i));
        }

        /// 4) FixedLength data
        long numBitsRequired = recordBitOffset;
        long numLongsRequired = numBitsRequired == 0 ? 0 : ((numBitsRequired - 1) / 64) + 1;
        fixedLengthLongArray.writeTo(os, numLongsRequired);

        /// 5) VarLength data
        for(int i=0;i<varLengthByteArrays.length;i++) {
            if(varLengthByteArrays[i] == null) {
                VarInt.writeVLong(os, 0);
            } else {
                VarInt.writeVLong(os, varLengthByteArrays[i].length());
                varLengthByteArrays[i].getUnderlyingArray().writeTo(os, 0, varLengthByteArrays[i].length());
            }
        }

        fixedLengthLongArray = null;
        varLengthByteArrays = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
    }

    /// here we need to add the offsets for the variable-length field endings, as they will be read as the start position for the following record.
    private void addNullRecord(int ordinal, long recordBitOffset, FixedLengthElementArray fixedLengthLongArray, ByteDataBuffer varLengthByteArrays[]) {
        for(int fieldIndex=0; fieldIndex < getSchema().numFields(); fieldIndex++) {
            if(getSchema().getFieldType(fieldIndex) == FieldType.STRING || getSchema().getFieldType(fieldIndex) == FieldType.BYTES) {
                long fieldBitOffset = recordBitOffset + fieldStats.getFieldBitOffset(fieldIndex);
                int bitsPerElement = fieldStats.getMaxBitsForField(fieldIndex);
                long currentPointer = varLengthByteArrays[fieldIndex] == null ? 0 : varLengthByteArrays[fieldIndex].length();
                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, currentPointer);
            }
        }
    }

    private void addRecord(int ordinal, long recordBitOffset, FixedLengthElementArray fixedLengthLongArray, ByteDataBuffer varLengthByteArrays[]) {
        long pointer = ordinalMap.getPointerForData(ordinal);

        for(int fieldIndex=0; fieldIndex < getSchema().numFields(); fieldIndex++) {
            pointer = addRecordField(pointer, recordBitOffset, fieldIndex, fixedLengthLongArray, varLengthByteArrays);
        }
    }

    private long addRecordField(long readPointer, long recordBitOffset, int fieldIndex, FixedLengthElementArray fixedLengthLongArray, ByteDataBuffer varLengthByteArrays[]) {
        FieldType fieldType = getSchema().getFieldType(fieldIndex);
        long fieldBitOffset = recordBitOffset + fieldStats.getFieldBitOffset(fieldIndex);
        int bitsPerElement = fieldStats.getMaxBitsForField(fieldIndex);
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        switch(fieldType) {
        case BOOLEAN:
            if(VarInt.readVNull(data, readPointer)) {
                fixedLengthLongArray.setElementValue(fieldBitOffset, 2, 3);
            } else {
                fixedLengthLongArray.setElementValue(fieldBitOffset, 2, data.get(readPointer));
            }
            readPointer += 1;
            break;
        case FLOAT:
            int intValue = readIntBits(data, readPointer);
            fixedLengthLongArray.setElementValue(fieldBitOffset, 32, intValue);
            readPointer += 4;
            break;
        case DOUBLE:
            long longValue = readLongBits(data, readPointer);
            fixedLengthLongArray.setElementValue(fieldBitOffset, 64, longValue);
            readPointer += 8;
            break;
        case LONG:
        case INT:
        case REFERENCE:
            if(VarInt.readVNull(data, readPointer)) {
               fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, (1L << bitsPerElement) - 1);
               readPointer += 1;
            } else {
                long vLong = VarInt.readVLong(data, readPointer);
                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, vLong);
                readPointer += VarInt.sizeOfVLong(vLong);
            }
            break;
        case BYTES:
        case STRING:
            ByteDataBuffer varLengthBuf = getByteArray(varLengthByteArrays, fieldIndex);

            if(VarInt.readVNull(data, readPointer)) {
                long offset = varLengthBuf.length();

                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, offset | (1L << (bitsPerElement - 1))); // write offset with set null bit
                readPointer += 1;
            } else {
                int length = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(length);
                varLengthBuf.copyFrom(data, readPointer, length);

                long offset = varLengthBuf.length();

                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, offset);
                readPointer += length;
            }
            break;
        }
        return readPointer;
    }

    private ByteDataBuffer getByteArray(ByteDataBuffer buffers[], int index) {
        if(buffers[index] == null) {
            buffers[index] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        }
        return buffers[index];
    }

    static int readIntBits(ByteData data, long fieldPosition) {
        int intBits = (data.get(fieldPosition++) & 0xFF) << 24;
        intBits |= (data.get(fieldPosition++) & 0xFF) << 16;
        intBits |= (data.get(fieldPosition++) & 0xFF) << 8;
        intBits |= (data.get(fieldPosition) & 0xFF);
        return intBits;
    }

    static long readLongBits(ByteData data, long fieldPosition) {
        long longBits = (long) (data.get(fieldPosition++) & 0xFF) << 56;
        longBits |= (long) (data.get(fieldPosition++) & 0xFF) << 48;
        longBits |= (long) (data.get(fieldPosition++) & 0xFF) << 40;
        longBits |= (long) (data.get(fieldPosition++) & 0xFF) << 32;
        longBits |= (long) (data.get(fieldPosition++) & 0xFF) << 24;
        longBits |= (data.get(fieldPosition++) & 0xFF) << 16;
        longBits |= (data.get(fieldPosition++) & 0xFF) << 8;
        longBits |= (data.get(fieldPosition) & 0xFF);
        return longBits;
    }

}
