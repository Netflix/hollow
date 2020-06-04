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
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

class HollowObjectTypeReadStateShard {

    private volatile HollowObjectTypeDataElements currentDataVolatile;

    private final HollowObjectSchema schema;
    
    HollowObjectTypeReadStateShard(HollowObjectSchema schema) {
        this.schema = schema;
    }

    public boolean isNull(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long fixedLengthValue;

        do {
            currentData = this.currentDataVolatile;

            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            int numBitsForField = currentData.bitsPerField[fieldIndex];

            fixedLengthValue = numBitsForField <= 56 ?
                    currentData.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                    : currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
        } while(readWasUnsafe(currentData));

        switch(schema.getFieldType(fieldIndex)) {
        case BYTES:
        case STRING:
            int numBits = currentData.bitsPerField[fieldIndex];
            return (fixedLengthValue & (1 << (numBits - 1))) != 0;
        case FLOAT:
            return (int)fixedLengthValue == HollowObjectWriteRecord.NULL_FLOAT_BITS;
        case DOUBLE:
            return fixedLengthValue == HollowObjectWriteRecord.NULL_DOUBLE_BITS;
        default:
            return fixedLengthValue == currentData.nullValueForField[fieldIndex];
        }
    }

    public int readOrdinal(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long refOrdinal;

        do {
            currentData = this.currentDataVolatile;
            refOrdinal = readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(refOrdinal == currentData.nullValueForField[fieldIndex])
            return ORDINAL_NONE;
        return (int)refOrdinal;
    }

    public int readInt(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentDataVolatile;
            value = readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(value == currentData.nullValueForField[fieldIndex])
            return Integer.MIN_VALUE;
        return ZigZag.decodeInt((int)value);
    }

    public float readFloat(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        int value;

        do {
            currentData = this.currentDataVolatile;
            value = (int)readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(value == HollowObjectWriteRecord.NULL_FLOAT_BITS)
            return Float.NaN;
        return Float.intBitsToFloat(value);
    }

    public double readDouble(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentDataVolatile;
            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            value = currentData.fixedLengthData.getLargeElementValue(bitOffset, 64, -1L);
        } while(readWasUnsafe(currentData));

        if(value == HollowObjectWriteRecord.NULL_DOUBLE_BITS)
            return Double.NaN;
        return Double.longBitsToDouble(value);
    }

    public long readLong(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentDataVolatile;
            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            int numBitsForField = currentData.bitsPerField[fieldIndex];
            value = currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
        } while(readWasUnsafe(currentData));

        if(value == currentData.nullValueForField[fieldIndex])
            return Long.MIN_VALUE;
        return ZigZag.decodeLong(value);
    }

    public Boolean readBoolean(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentDataVolatile;
            value = readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(value == currentData.nullValueForField[fieldIndex])
            return null;
        return value == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private long readFixedLengthFieldValue(HollowObjectTypeDataElements currentData, int ordinal, int fieldIndex) {
        long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
        int numBitsForField = currentData.bitsPerField[fieldIndex];

        long value = currentData.fixedLengthData.getElementValue(bitOffset, numBitsForField);

        return value;
    }

    public byte[] readBytes(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        byte[] result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentDataVolatile;

                numBitsForField = currentData.bitsPerField[fieldIndex];
                long currentBitOffset = fieldOffset(currentData, ordinal, fieldIndex);
                endByte = currentData.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = ordinal != 0 ? currentData.fixedLengthData.getElementValue(currentBitOffset - currentData.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(currentData));

            if((endByte & (1L << numBitsForField - 1)) != 0)
                return null;

            startByte &= (1L << numBitsForField - 1) - 1;

            int length = (int)(endByte - startByte);
            result = new byte[length];
            for(int i=0;i<length;i++)
                result[i] = currentData.varLengthData[fieldIndex].get(startByte + i);

        } while(readWasUnsafe(currentData));

        return result;
    }

    public String readString(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        String result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentDataVolatile;

                numBitsForField = currentData.bitsPerField[fieldIndex];
                long currentBitOffset = fieldOffset(currentData, ordinal, fieldIndex);
                endByte = currentData.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = ordinal != 0 ? currentData.fixedLengthData.getElementValue(currentBitOffset - currentData.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(currentData));

            if((endByte & (1L << numBitsForField - 1)) != 0)
                return null;

            startByte &= (1L << numBitsForField - 1) - 1;

            int length = (int)(endByte - startByte);

            result = readString(currentData.varLengthData[fieldIndex], startByte, length);
        } while(readWasUnsafe(currentData));

        return result;
    }

    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        HollowObjectTypeDataElements currentData;
        boolean result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentDataVolatile;

                numBitsForField = currentData.bitsPerField[fieldIndex];

                long currentBitOffset = fieldOffset(currentData, ordinal, fieldIndex);
                endByte = currentData.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = ordinal != 0 ? currentData.fixedLengthData.getElementValue(currentBitOffset - currentData.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(currentData));

            if((endByte & (1L << numBitsForField - 1)) != 0)
                return testValue == null;
            if(testValue == null)
                return false;

            startByte &= (1L << numBitsForField - 1) - 1;

            int length = (int)(endByte - startByte);

            result = testStringEquality(currentData.varLengthData[fieldIndex], startByte, length, testValue);
        } while(readWasUnsafe(currentData));

        return result;
    }

    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        HollowObjectTypeDataElements currentData;
        int hashCode;
        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentDataVolatile;

                numBitsForField = currentData.bitsPerField[fieldIndex];
                long currentBitOffset = fieldOffset(currentData, ordinal, fieldIndex);
                endByte = currentData.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = ordinal != 0 ? currentData.fixedLengthData.getElementValue(currentBitOffset - currentData.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(currentData));

            if((endByte & (1L << numBitsForField - 1)) != 0)
                return -1;

            startByte &= (1L << numBitsForField - 1) - 1;

            int length = (int)(endByte - startByte);

            hashCode = HashCodes.hashCode(currentData.varLengthData[fieldIndex], startByte, length);
        } while(readWasUnsafe(currentData));

        return hashCode;
    }

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     */
    public int bitsRequiredForField(String fieldName) {
        int fieldIndex = schema.getPosition(fieldName);
        return fieldIndex == -1 ? 0 : currentDataVolatile.bitsPerField[fieldIndex];
    }

    private long fieldOffset(HollowObjectTypeDataElements currentData, int ordinal, int fieldIndex) {
        return ((long)currentData.bitsPerRecord * ordinal) + currentData.bitOffsetPerField[fieldIndex];
    }

    /**
     * Decode a String as a series of VarInts, one per character.<p>
     *
     * @param str
     * @param out
     * @return
     */
    private static final ThreadLocal<char[]> chararr = new ThreadLocal<char[]>();

    private String readString(ByteData data, long position, int length) {
        long endPosition = position + length;

        char chararr[] = getCharArray();

        if(length > chararr.length)
            chararr = new char[length];

        int count = 0;

        while(position < endPosition) {
            int c = VarInt.readVInt(data, position);
            chararr[count++] = (char)c;
            position += VarInt.sizeOfVInt(c);
        }

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

    private char[] getCharArray() {
        char ch[] = chararr.get();
        if(ch == null) {
            ch = new char[100];
            chararr.set(ch);
        }
        return ch;
    }

    void invalidate() {
        setCurrentData(null);
    }

    HollowObjectTypeDataElements currentDataElements() {
        return currentDataVolatile;
    }

    private boolean readWasUnsafe(HollowObjectTypeDataElements data) {
        // Use a load (acquire) fence to constrain the compiler reordering prior plain loads so
        // that they cannot "float down" below the volatile load of currentDataVolatile.
        // This ensures data is checked against currentData *after* optimistic calculations
        // have been performed on data.
        //
        // Note: the Java Memory Model allows for the reordering of plain loads and stores
        // before a volatile load (those plain loads and stores can "float down" below the
        // volatile load), but forbids the reordering of plain loads after a volatile load
        // (those plain loads are not allowed to "float above" the volatile load).
        // Similar reordering also applies to plain loads and stores and volatile stores.
        // In effect the ordering of volatile loads and stores is retained and plain loads
        // and stores can be shuffled around and grouped together, which increases
        // optimization opportunities.
        // This is why locks can be coarsened; plain loads and stores may enter the lock region
        // from above (float down the acquire) or below (float above the release) but existing
        // loads and stores may not exit (a "lock roach motel" and why there is almost universal
        // misunderstanding of, and many misguided attempts to optimize, the infamous double
        // checked locking idiom).
        //
        // Note: the fence provides stronger ordering guarantees than a corresponding non-plain
        // load or store since the former affects all prior or subsequent loads and stores,
        // whereas the latter is scoped to the particular load or store.
        //
        // For more details see http://gee.cs.oswego.edu/dl/html/j9mm.html
        HollowUnsafeHandle.getUnsafe().loadFence();
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowObjectTypeDataElements data) {
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema, BitSet populatedOrdinals, int shardNumber, int numShards) {
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

        HollowObjectTypeDataElements currentData = currentDataVolatile;
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                checksum.applyInt(ordinal);
                for(int i=0;i<fieldIndexes.length;i++) {
                    int fieldIdx = fieldIndexes[i];
                    if(!schema.getFieldType(fieldIdx).isVariableLength()) {
                        long bitOffset = fieldOffset(currentData, shardOrdinal, fieldIdx);
                        int numBitsForField = currentData.bitsPerField[fieldIdx];
                        long fixedLengthValue = numBitsForField <= 56 ?
                                currentData.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                                : currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
    
                        if(fixedLengthValue == currentData.nullValueForField[fieldIdx])
                            checksum.applyInt(Integer.MAX_VALUE);
                        else
                            checksum.applyLong(fixedLengthValue);
                    } else {
                        checksum.applyInt(findVarLengthFieldHashCode(shardOrdinal, fieldIdx));
                    }
                }
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

    public long getApproximateHeapFootprintInBytes() {
        HollowObjectTypeDataElements currentData = currentDataVolatile;
        long bitsPerFixedLengthData = (long)currentData.bitsPerRecord * (currentData.maxOrdinal + 1);
        
        long requiredBytes = bitsPerFixedLengthData / 8;
        
        for(int i=0;i<currentData.varLengthData.length;i++) {
            if(currentData.varLengthData[i] != null)
                requiredBytes += currentData.varLengthData[i].size();
        }
        
        return requiredBytes;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowObjectTypeDataElements currentData = currentDataVolatile;
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= currentData.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += currentData.bitsPerRecord;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
    
}
