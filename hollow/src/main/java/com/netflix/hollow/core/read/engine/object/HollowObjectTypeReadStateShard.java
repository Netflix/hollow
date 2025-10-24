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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

class HollowObjectTypeReadStateShard implements HollowTypeReadStateShard {

    private final HollowObjectSchema schema;
    final HollowObjectTypeDataElements dataElements;
    final int shardOrdinalShift;

    @Override
    public HollowObjectTypeDataElements getDataElements() {
        return dataElements;
    }

    @Override
    public int getShardOrdinalShift() {
        return shardOrdinalShift;
    }

    public HollowObjectTypeReadStateShard(HollowObjectSchema schema, HollowObjectTypeDataElements dataElements, int shardOrdinalShift) {
        this.schema = schema;
        this.shardOrdinalShift = shardOrdinalShift;
        this.dataElements = dataElements;
    }

    public long readValue(int ordinal, int fieldIndex) {
        long bitOffset = fieldOffset(ordinal, fieldIndex);
        int numBitsForField = dataElements.bitsPerField[fieldIndex];
        return numBitsForField <= 56 ?
                dataElements.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                : dataElements.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
    }

    public long readOrdinal(int ordinal, int fieldIndex) {
        return readFixedLengthFieldValue(ordinal, fieldIndex);
    }

    public long readInt(int ordinal, int fieldIndex) {
        return readFixedLengthFieldValue(ordinal, fieldIndex);
    }

    public int readFloat(int ordinal, int fieldIndex) {
        return (int)readFixedLengthFieldValue(ordinal, fieldIndex);
    }

    public long readDouble(int ordinal, int fieldIndex) {
        long bitOffset = fieldOffset(ordinal, fieldIndex);
        return dataElements.fixedLengthData.getLargeElementValue(bitOffset, 64, -1L);
    }

    public long readLong(int ordinal, int fieldIndex) {
        long bitOffset = fieldOffset(ordinal, fieldIndex);
        int numBitsForField = dataElements.bitsPerField[fieldIndex];
        return dataElements.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
    }

    public long readBoolean(int ordinal, int fieldIndex) {
        return readFixedLengthFieldValue(ordinal, fieldIndex);
    }

    private long readFixedLengthFieldValue(int ordinal, int fieldIndex) {
        long bitOffset = fieldOffset(ordinal, fieldIndex);
        int numBitsForField = dataElements.bitsPerField[fieldIndex];

        long value = dataElements.fixedLengthData.getElementValue(bitOffset, numBitsForField);

        return value;
    }

    public byte[] readBytes(long startByte, long endByte, int numBitsForField, int fieldIndex) {
        byte[] result;

        if((endByte & (1L << numBitsForField - 1)) != 0)
            return null;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        result = new byte[length];
        for(int i=0;i<length;i++)
            result[i] = dataElements.varLengthData[fieldIndex].get(startByte + i);

        return result;
    }

    public String readString(long startByte, long endByte, int numBitsForField, int fieldIndex) {
        if((endByte & (1L << numBitsForField - 1)) != 0)
            return null;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        return readString(dataElements.varLengthData[fieldIndex], startByte, length);
    }

    public boolean isStringFieldEqual(long startByte, long endByte, int numBitsForField, int fieldIndex, String testValue) {
        if((endByte & (1L << numBitsForField - 1)) != 0)
            return testValue == null;
        if(testValue == null)
            return false;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        return testStringEquality(dataElements.varLengthData[fieldIndex], startByte, length, testValue);
    }

    public int findVarLengthFieldHashCode(long startByte, long endByte, int numBitsForField, int fieldIndex) {
        if((endByte & (1L << numBitsForField - 1)) != 0)
            return -1;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        return HashCodes.hashCode(dataElements.varLengthData[fieldIndex], startByte, length);
    }

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     */
    public int bitsRequiredForField(String fieldName) {
        int fieldIndex = schema.getPosition(fieldName);
        return fieldIndex == -1 ? 0 : dataElements.bitsPerField[fieldIndex];
    }

    long fieldOffset(int ordinal, int fieldIndex) {
        return ((long)dataElements.bitsPerRecord * ordinal) + dataElements.bitOffsetPerField[fieldIndex];
    }

    /**
     * Decode a String as a series of VarInts, one per character.<p>
     */
    private static final ThreadLocal<char[]> chararr = ThreadLocal.withInitial(() -> new char[100]);

    private String readString(ByteData data, long position, int length) {
        char[] chararr = HollowObjectTypeReadStateShard.chararr.get();
        if (length > chararr.length) {
            chararr = new char[length];
        } else {
            Arrays.fill(chararr, 0, length, '\0');
        }

        int count = VarInt.readVIntsInto(data, position, length, chararr);

        // The number of chars may be fewer than the number of bytes in the serialized data
        return new String(chararr, 0, count);
    }

    private boolean testStringEquality(ByteData data, long position, int length, String testValue) {
        if(length < testValue.length()) // can't check exact length here; the length argument is in bytes, which is equal to or greater than the number of characters.
            return false;

        long endPosition = position + length;

        int count = 0;

        while(position < endPosition && count < testValue.length()) {
            int c = VarInt.readVInt(data, position);
            if(testValue.charAt(count++) != (char)c)
                return false;
            position += VarInt.sizeOfVInt(c);
        }

        // The number of chars may be fewer than the number of bytes in the serialized data
        return position == endPosition && count == testValue.length();
    }

    protected void applyShardToChecksum(HollowChecksum checksum, HollowSchema withSchema, BitSet populatedOrdinals, int shardNumber, int shardNumberMask) {
        int numBitsForField;
        long bitOffset;
        long endByte;
        long startByte;

        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + schema.getName());

        HollowObjectSchema commonSchema = schema.findCommonSchema((HollowObjectSchema)withSchema);

        List<String> commonFieldNames = new ArrayList<String>();
        for(int i=0;i<commonSchema.numFields();i++)
            commonFieldNames.add(commonSchema.getFieldName(i));
        Collections.sort(commonFieldNames);
        
        int fieldIndexes[] = new int[commonFieldNames.size()];
        for(int i=0;i<commonFieldNames.size();i++) {
            fieldIndexes[i] = schema.getPosition(commonFieldNames.get(i));
        }

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & shardNumberMask) == shardNumber) {
                int shardOrdinal = ordinal >> shardOrdinalShift;
                checksum.applyInt(ordinal);
                for(int i=0;i<fieldIndexes.length;i++) {
                    int fieldIdx = fieldIndexes[i];
                    bitOffset = fieldOffset(shardOrdinal, fieldIdx);
                    numBitsForField = dataElements.bitsPerField[fieldIdx];
                    if(!schema.getFieldType(fieldIdx).isVariableLength()) {
                        long fixedLengthValue = numBitsForField <= 56 ?
                                dataElements.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                                : dataElements.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
    
                        if(fixedLengthValue == dataElements.nullValueForField[fieldIdx])
                            checksum.applyInt(Integer.MAX_VALUE);
                        else
                            checksum.applyLong(fixedLengthValue);
                    } else {
                        endByte = dataElements.fixedLengthData.getElementValue(bitOffset, numBitsForField);
                        startByte = shardOrdinal != 0 ? dataElements.fixedLengthData.getElementValue(bitOffset - dataElements.bitsPerRecord, numBitsForField) : 0;
                        checksum.applyInt(findVarLengthFieldHashCode(startByte, endByte, numBitsForField, fieldIdx));
                    }
                }
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

    public long getApproximateHeapFootprintInBytes() {
        long bitsPerFixedLengthData = (long)dataElements.bitsPerRecord * (dataElements.maxOrdinal + 1);
        
        long requiredBytes = bitsPerFixedLengthData / 8;
        
        for(int i=0;i<dataElements.varLengthData.length;i++) {
            if(dataElements.varLengthData[i] != null)
                requiredBytes += dataElements.varLengthData[i].size();
        }
        
        return requiredBytes;
    }

    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= dataElements.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += dataElements.bitsPerRecord;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
    
}
