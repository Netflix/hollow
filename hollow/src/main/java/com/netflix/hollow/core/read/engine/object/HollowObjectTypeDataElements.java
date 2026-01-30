/*
 *  Copyright 2016-2020 Netflix, Inc.
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

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.VariableLengthData;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowObjectTypeReadState}.
 * 
 * During a delta, the HollowObjectTypeReadState will create a new HollowObjectTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowObjectTypeDataElements extends HollowTypeDataElements {

    final HollowObjectSchema schema;

    FixedLengthData fixedLengthData;
    final VariableLengthData varLengthData[];

    final int bitsPerField[];
    final int bitOffsetPerField[];
    final long nullValueForField[];
    int bitsPerRecord;

    private int bitsPerUnfilteredField[];
    private boolean unfilteredFieldIsIncluded[];

    public HollowObjectTypeDataElements(HollowObjectSchema schema, ArraySegmentRecycler memoryRecycler) {
        this(schema, MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowObjectTypeDataElements(HollowObjectSchema schema, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        super(memoryMode, memoryRecycler);
        varLengthData = new VariableLengthData[schema.numFields()];
        bitsPerField = new int[schema.numFields()];
        bitOffsetPerField = new int[schema.numFields()];
        nullValueForField = new long[schema.numFields()];
        this.schema = schema;
    }

    void readSnapshot(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        readFromInput(in, false, unfilteredSchema);
    }

    void readDelta(HollowBlobInput in) throws IOException {
        readFromInput(in, true, schema);
    }

    void readFromInput(HollowBlobInput in, boolean isDelta, HollowObjectSchema unfilteredSchema) throws IOException {
        maxOrdinal = VarInt.readVInt(in);

        if(isDelta) {
            encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
            encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
        }

        readFieldStatistics(in, unfilteredSchema);

        fixedLengthData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
        removeExcludedFieldsFromFixedLengthData();

        readVarLengthData(in, unfilteredSchema);
    }

    private void removeExcludedFieldsFromFixedLengthData() {
        if(bitsPerField.length < bitsPerUnfilteredField.length) {
            long numBitsRequired = (long)bitsPerRecord * (maxOrdinal + 1);
            FixedLengthElementArray filteredData = new FixedLengthElementArray(memoryRecycler, numBitsRequired);

            long currentReadBit = 0;
            long currentWriteBit = 0;

            for(int i=0;i<=maxOrdinal;i++) {
                for(int j=0;j<bitsPerUnfilteredField.length;j++) {
                    if(unfilteredFieldIsIncluded[j]) {
                        long value = bitsPerUnfilteredField[j] < 56 ?
                                fixedLengthData.getElementValue(currentReadBit, bitsPerUnfilteredField[j]) :
                                fixedLengthData.getLargeElementValue(currentReadBit, bitsPerUnfilteredField[j]);
                        filteredData.setElementValue(currentWriteBit, bitsPerUnfilteredField[j], value);
                        currentWriteBit += bitsPerUnfilteredField[j];
                    }

                    currentReadBit += bitsPerUnfilteredField[j];
                }
            }

            FixedLengthDataFactory.destroy(fixedLengthData, memoryRecycler);
            memoryRecycler.swap();
            fixedLengthData = filteredData;
        }
    }

    private void readFieldStatistics(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        bitsPerRecord = 0;

        bitsPerUnfilteredField = new int[unfilteredSchema.numFields()];
        unfilteredFieldIsIncluded = new boolean[unfilteredSchema.numFields()];

        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            int readBitsPerField = VarInt.readVInt(in);
            bitsPerUnfilteredField[i] = readBitsPerField;
            unfilteredFieldIsIncluded[i] = schema.getPosition(unfilteredSchema.getFieldName(i)) != -1;

            if(unfilteredFieldIsIncluded[i]) {
                bitsPerField[filteredFieldIdx] = readBitsPerField;
                nullValueForField[filteredFieldIdx] = bitsPerField[filteredFieldIdx] == 64 ? -1L : (1L << bitsPerField[filteredFieldIdx]) - 1;
                bitOffsetPerField[filteredFieldIdx] = bitsPerRecord;
                bitsPerRecord += bitsPerField[filteredFieldIdx];
                filteredFieldIdx++;
            }
        }
    }


    private void readVarLengthData(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            long numBytesInVarLengthData = VarInt.readVLong(in);

            if(schema.getPosition(unfilteredSchema.getFieldName(i)) != -1) {
                if(numBytesInVarLengthData != 0) {
                    varLengthData[filteredFieldIdx] = VariableLengthDataFactory.get(memoryMode, memoryRecycler);
                    varLengthData[filteredFieldIdx].loadFrom(in, numBytesInVarLengthData);
                }
                filteredFieldIdx++;
            } else {
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= in.skipBytes(numBytesInVarLengthData);
                }
            }
        }
    }

    static void discardFromInput(HollowBlobInput in, HollowObjectSchema schema, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); // max ordinal

        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(in); // max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }

            /// field statistics
            for(int j=0;j<schema.numFields();j++) {
                VarInt.readVInt(in);
            }

            /// fixed length data
            FixedLengthData.discardFrom(in);

            /// variable length data
            for(int j=0;j<schema.numFields();j++) {
                long numBytesInVarLengthData = VarInt.readVLong(in);
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= in.skipBytes(numBytesInVarLengthData);
                }
            }
        }
    }

    void applyDelta(HollowObjectTypeDataElements fromData, HollowObjectTypeDataElements deltaData) {
        new HollowObjectDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    @Override
    public void destroy() {
        FixedLengthDataFactory.destroy(fixedLengthData, memoryRecycler);
        for(int i=0;i<varLengthData.length;i++) {
            if(varLengthData[i] != null)
                VariableLengthDataFactory.destroy(varLengthData[i]);
        }
    }

    static long varLengthStartByte(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        if(ordinal == 0)
            return 0;

        int numBitsForField = from.bitsPerField[fieldIdx];
        long currentBitOffset = ((long)from.bitsPerRecord * ordinal) + from.bitOffsetPerField[fieldIdx];
        long startByte = from.fixedLengthData.getElementValue(currentBitOffset - from.bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return startByte;
    }

    static long varLengthEndByte(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        int numBitsForField = from.bitsPerField[fieldIdx];
        long currentBitOffset = ((long)from.bitsPerRecord * ordinal) + from.bitOffsetPerField[fieldIdx];
        long endByte = from.fixedLengthData.getElementValue(currentBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;

        return endByte;
    }

    static long varLengthSize(HollowObjectTypeDataElements from, int ordinal, int fieldIdx) {
        int numBitsForField = from.bitsPerField[fieldIdx];
        long fromBitOffset = ((long)from.bitsPerRecord*ordinal) + from.bitOffsetPerField[fieldIdx];
        long fromEndByte = from.fixedLengthData.getElementValue(fromBitOffset, numBitsForField) & (1L << (numBitsForField - 1)) - 1;
        long fromStartByte = ordinal != 0 ? from.fixedLengthData.getElementValue(fromBitOffset - from.bitsPerRecord, numBitsForField) & (1L << (numBitsForField - 1)) - 1 : 0;
        return fromEndByte - fromStartByte;
    }

    static void copyRecord(HollowObjectTypeDataElements to, int toOrdinal, HollowObjectTypeDataElements from, int fromOrdinal, long[] currentWriteVarLengthDataPointers) {
        for(int fieldIndex=0;fieldIndex<to.schema.numFields();fieldIndex++) {
            long currentReadFixedLengthStartBit = ((long)fromOrdinal * from.bitsPerRecord) + from.bitOffsetPerField[fieldIndex];
            long readValue = from.bitsPerField[fieldIndex] > 56 ?
                    from.fixedLengthData.getLargeElementValue(currentReadFixedLengthStartBit, from.bitsPerField[fieldIndex])
                    : from.fixedLengthData.getElementValue(currentReadFixedLengthStartBit, from.bitsPerField[fieldIndex]);

            long toWriteFixedLengthStartBit = ((long)toOrdinal * to.bitsPerRecord) + to.bitOffsetPerField[fieldIndex];
            if(to.varLengthData[fieldIndex] == null) {   // fixed len data
                if(readValue == from.nullValueForField[fieldIndex]) {
                    writeNullFixedLengthField(to, fieldIndex, toWriteFixedLengthStartBit);
                }
                else {
                    to.fixedLengthData.setElementValue(toWriteFixedLengthStartBit, to.bitsPerField[fieldIndex], readValue);
                }
            } else {
                if ((readValue & (1L << (from.bitsPerField[fieldIndex] - 1))) != 0) {   // null check is the first bit set (other bits have an offset of the last non-null value)
                    writeNullVarLengthField(to, fieldIndex, toWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
                } else {
                    long fromStartByte = varLengthStartByte(from, fromOrdinal, fieldIndex);
                    long fromEndByte = varLengthEndByte(from, fromOrdinal, fieldIndex);
                    long size = fromEndByte - fromStartByte;

                    to.fixedLengthData.setElementValue(toWriteFixedLengthStartBit, to.bitsPerField[fieldIndex], currentWriteVarLengthDataPointers[fieldIndex] + size);
                    to.varLengthData[fieldIndex].copy(from.varLengthData[fieldIndex], fromStartByte, currentWriteVarLengthDataPointers[fieldIndex], size);
                    currentWriteVarLengthDataPointers[fieldIndex] += size;
                }
            }
        }
    }

    static void writeNullField(HollowObjectTypeDataElements target, int fieldIndex, long currentWriteFixedLengthStartBit, long[] currentWriteVarLengthDataPointers) {
        if(target.varLengthData[fieldIndex] != null) {
            writeNullVarLengthField(target, fieldIndex, currentWriteFixedLengthStartBit, currentWriteVarLengthDataPointers);
        } else {
            writeNullFixedLengthField(target, fieldIndex, currentWriteFixedLengthStartBit);
        }
    }

    static void writeNullVarLengthField(HollowObjectTypeDataElements target, int fieldIndex, long currentWriteFixedLengthStartBit, long[] currentWriteVarLengthDataPointers) {
        long writeValue = (1L << (target.bitsPerField[fieldIndex] - 1)) | currentWriteVarLengthDataPointers[fieldIndex];
        target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], writeValue);
    }

    static void writeNullFixedLengthField(HollowObjectTypeDataElements target, int fieldIndex, long currentWriteFixedLengthStartBit) {
        target.fixedLengthData.setElementValue(currentWriteFixedLengthStartBit, target.bitsPerField[fieldIndex], target.nullValueForField[fieldIndex]);
    }

    /**
     * Prepare data elements for writing field values.
     * Must be called before using write methods.
     * Initializes fixed-length and variable-length data structures.
     */
    public void prepareForWrite() {
        // Calculate bits per record
        bitsPerRecord = 0;
        for (int i = 0; i < schema.numFields(); i++) {
            bitOffsetPerField[i] = bitsPerRecord;

            HollowObjectSchema.FieldType fieldType = schema.getFieldType(i);
            if (fieldType == HollowObjectSchema.FieldType.REFERENCE ||
                fieldType == HollowObjectSchema.FieldType.INT) {
                // INT and REFERENCE use 32 bits
                bitsPerField[i] = 32;
                nullValueForField[i] = (1L << 32) - 1;
            } else if (fieldType == HollowObjectSchema.FieldType.LONG) {
                // LONG uses 64 bits
                bitsPerField[i] = 64;
                nullValueForField[i] = -1L;
            } else if (fieldType == HollowObjectSchema.FieldType.BOOLEAN) {
                // BOOLEAN uses 2 bits (null, false, true)
                bitsPerField[i] = 2;
                nullValueForField[i] = 3;
            } else if (fieldType == HollowObjectSchema.FieldType.FLOAT) {
                // FLOAT uses 32 bits
                bitsPerField[i] = 32;
                nullValueForField[i] = (1L << 32) - 1;
            } else if (fieldType == HollowObjectSchema.FieldType.DOUBLE) {
                // DOUBLE uses 64 bits
                bitsPerField[i] = 64;
                nullValueForField[i] = -1L;
            } else {
                // STRING and BYTES use pointers (40 bits for reasonable data size)
                bitsPerField[i] = 40;
                nullValueForField[i] = (1L << 40) - 1;
            }

            bitsPerRecord += bitsPerField[i];
        }

        // Allocate fixed-length data - note: parameter order is (numBits, memoryMode, recycler)
        long numBitsRequired = (long) bitsPerRecord * (maxOrdinal + 1);
        fixedLengthData = FixedLengthDataFactory.get(numBitsRequired, memoryMode, memoryRecycler);

        // Allocate variable-length data for STRING and BYTES fields
        for (int i = 0; i < schema.numFields(); i++) {
            HollowObjectSchema.FieldType fieldType = schema.getFieldType(i);
            if (fieldType == HollowObjectSchema.FieldType.STRING ||
                fieldType == HollowObjectSchema.FieldType.BYTES) {
                // Initialize with default size - will grow automatically
                varLengthData[i] = VariableLengthDataFactory.get(memoryMode, memoryRecycler);
            }
        }
    }

    /**
     * Write an INT value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the int value
     */
    public void writeInt(int ordinal, int fieldIndex, int value) {
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];
        long elementValue = value & 0xFFFFFFFFL; // Convert to unsigned long
        fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], elementValue);
    }

    /**
     * Write a REFERENCE value (ordinal) to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param referenceOrdinal the ordinal being referenced
     */
    public void writeReference(int ordinal, int fieldIndex, int referenceOrdinal) {
        writeInt(ordinal, fieldIndex, referenceOrdinal);
    }

    /**
     * Write a LONG value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the long value
     */
    public void writeLong(int ordinal, int fieldIndex, long value) {
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];
        // For 64-bit values, we need to handle them specially
        int whichLong = (int)(bitOffset >>> 6);
        int whichBit = (int)(bitOffset & 0x3F);

        FixedLengthElementArray array = (FixedLengthElementArray) fixedLengthData;

        // Clear the bits first to avoid OR corruption
        long clearMask = ~(0xFFFFFFFFFFFFFFFFL << whichBit);
        array.set(whichLong, array.get(whichLong) & clearMask);
        // Now set the new value
        array.set(whichLong, array.get(whichLong) | (value << whichBit));

        int bitsRemaining = 64 - whichBit;
        if (bitsRemaining < 64) {
            // Clear the bits in the next long
            long clearMaskNext = ~(0xFFFFFFFFFFFFFFFFL >>> bitsRemaining);
            array.set(whichLong + 1, array.get(whichLong + 1) & clearMaskNext);
            // Now set the new value
            array.set(whichLong + 1, array.get(whichLong + 1) | (value >>> bitsRemaining));
        }
    }

    /**
     * Write a FLOAT value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the float value
     */
    public void writeFloat(int ordinal, int fieldIndex, float value) {
        int bits = Float.floatToIntBits(value);
        writeInt(ordinal, fieldIndex, bits);
    }

    /**
     * Write a DOUBLE value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the double value
     */
    public void writeDouble(int ordinal, int fieldIndex, double value) {
        long bits = Double.doubleToLongBits(value);
        writeLong(ordinal, fieldIndex, bits);
    }

    /**
     * Write a BOOLEAN value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the boolean value
     */
    public void writeBoolean(int ordinal, int fieldIndex, boolean value) {
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];
        // 0 = false, 1 = true, 3 = null (see nullValueForField)
        long elementValue = value ? 1 : 0;
        fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], elementValue);
    }

    /**
     * Write a STRING value to the specified ordinal and field.
     *
     * Encoding format: Each character is encoded as a VarInt (variable-length integer).
     * No length prefix is written - the string length is implicitly determined by the
     * difference between consecutive byte pointers in the fixed-length data.
     *
     * This format matches the encoding used by HollowObjectWriteRecord.setString(),
     * ensuring compatibility with normal snapshot/delta creation. The format was verified
     * by examining HollowObjectWriteRecord line 193, which writes each character as:
     * VarInt.writeVInt(buf, value.charAt(i))
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the string value
     */
    public void writeString(int ordinal, int fieldIndex, String value) {
        if (varLengthData[fieldIndex] == null) {
            throw new IllegalStateException("Variable length data not initialized for field " + fieldIndex);
        }

        // Get previous end position as start position
        long startByte;
        if (ordinal == 0) {
            startByte = 0;
        } else {
            long prevBitOffset = ((long) (ordinal - 1) * bitsPerRecord) + bitOffsetPerField[fieldIndex];
            startByte = fixedLengthData.getElementValue(prevBitOffset, bitsPerField[fieldIndex]);
        }

        SegmentedByteArray byteArray = (SegmentedByteArray) varLengthData[fieldIndex];

        // Write string as VarInt encoded characters (no length prefix - length is implicit from pointers)
        long currentByte = startByte;
        for (int i = 0; i < value.length(); i++) {
            currentByte = writeVarInt(byteArray, currentByte, value.charAt(i));
        }

        // Write end byte pointer to fixed-length data
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];
        fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], currentByte);
    }

    /**
     * Helper method to write a variable-length integer to a SegmentedByteArray.
     * Returns the next byte position after writing.
     */
    private long writeVarInt(SegmentedByteArray data, long position, int value) {
        if (value > 0x0FFFFFFF || value < 0) data.set(position++, (byte)(0x80 | ((value >>> 28))));
        if (value > 0x1FFFFF || value < 0)   data.set(position++, (byte)(0x80 | ((value >>> 21) & 0x7F)));
        if (value > 0x3FFF || value < 0)     data.set(position++, (byte)(0x80 | ((value >>> 14) & 0x7F)));
        if (value > 0x7F || value < 0)       data.set(position++, (byte)(0x80 | ((value >>>  7) & 0x7F)));
        data.set(position++, (byte)(value & 0x7F));
        return position;
    }

    /**
     * Write a BYTES value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     * @param value the byte array
     */
    public void writeBytes(int ordinal, int fieldIndex, byte[] value) {
        if (varLengthData[fieldIndex] == null) {
            throw new IllegalStateException("Variable length data not initialized for field " + fieldIndex);
        }

        // Get previous end position as start position
        long startByte;
        if (ordinal == 0) {
            startByte = 0;
        } else {
            long prevBitOffset = ((long) (ordinal - 1) * bitsPerRecord) + bitOffsetPerField[fieldIndex];
            startByte = fixedLengthData.getElementValue(prevBitOffset, bitsPerField[fieldIndex]);
        }

        SegmentedByteArray byteArray = (SegmentedByteArray) varLengthData[fieldIndex];

        // Write byte data directly
        for (int i = 0; i < value.length; i++) {
            byteArray.set(startByte + i, value[i]);
        }

        long endByte = startByte + value.length;

        // Write end byte pointer to fixed-length data
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];
        fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], endByte);
    }

    /**
     * Write a NULL value to the specified ordinal and field.
     *
     * @param ordinal the ordinal to write to
     * @param fieldIndex the field index
     */
    public void writeNull(int ordinal, int fieldIndex) {
        long bitOffset = ((long) ordinal * bitsPerRecord) + bitOffsetPerField[fieldIndex];

        HollowObjectSchema.FieldType fieldType = schema.getFieldType(fieldIndex);
        if (fieldType == HollowObjectSchema.FieldType.STRING ||
            fieldType == HollowObjectSchema.FieldType.BYTES) {
            // For variable-length fields, set high bit to indicate null
            long prevEndByte;
            if (ordinal == 0) {
                prevEndByte = 0;
            } else {
                long prevBitOffset = ((long) (ordinal - 1) * bitsPerRecord) + bitOffsetPerField[fieldIndex];
                prevEndByte = fixedLengthData.getElementValue(prevBitOffset, bitsPerField[fieldIndex]);
            }
            // Set high bit to mark as null, preserve pointer
            long nullMarker = prevEndByte | (1L << (bitsPerField[fieldIndex] - 1));
            fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], nullMarker);
        } else {
            // For fixed-length fields, write null sentinel value
            if (bitsPerField[fieldIndex] == 64) {
                // Use the same approach as writeLong for 64-bit values
                int whichLong = (int)(bitOffset >>> 6);
                int whichBit = (int)(bitOffset & 0x3F);
                long value = nullValueForField[fieldIndex];

                FixedLengthElementArray array = (FixedLengthElementArray) fixedLengthData;

                // Clear the bits first to avoid OR corruption
                long clearMask = ~(0xFFFFFFFFFFFFFFFFL << whichBit);
                array.set(whichLong, array.get(whichLong) & clearMask);
                // Now set the new value
                array.set(whichLong, array.get(whichLong) | (value << whichBit));

                int bitsRemaining = 64 - whichBit;
                if (bitsRemaining < 64) {
                    // Clear the bits in the next long
                    long clearMaskNext = ~(0xFFFFFFFFFFFFFFFFL >>> bitsRemaining);
                    array.set(whichLong + 1, array.get(whichLong + 1) & clearMaskNext);
                    // Now set the new value
                    array.set(whichLong + 1, array.get(whichLong + 1) | (value >>> bitsRemaining));
                }
            } else {
                fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], nullValueForField[fieldIndex]);
            }
        }
    }
}
