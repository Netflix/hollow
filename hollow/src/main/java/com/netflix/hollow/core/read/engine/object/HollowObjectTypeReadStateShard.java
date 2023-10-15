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
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

class HollowObjectTypeReadStateShard {

    private volatile HollowObjectTypeDataElements currentDataVolatile;
    final HollowObjectTypeDataElements dataElements;
    // private volatile HollowObjectTypeReadState.ShardsHolder shardsHolderVolatile;
    final int shardOrdinalShift;

    private final HollowObjectSchema schema;
    
    HollowObjectTypeReadStateShard(HollowObjectSchema schema, int shardOrdinalShift) {
        this.schema = schema;
        this.shardOrdinalShift = shardOrdinalShift;
        this.dataElements = null;
        // this.shardsHolderVolatile = null;
    }

    HollowObjectTypeReadStateShard(HollowObjectSchema schema, HollowObjectTypeDataElements dataElements, int shardOrdinalShift) {
        this.schema = schema;
        this.shardOrdinalShift = shardOrdinalShift;
        this.dataElements = dataElements;
        // this.shardsHolderVolatile = null;
    }

    // HollowObjectTypeReadStateShard(HollowObjectSchema schema, HollowObjectTypeDataElements dataElements, int shardOrdinalShift, HollowObjectTypeReadState.ShardsHolder shardsHolder) {
    //     this.schema = schema;
    //     this.shardOrdinalShift = shardOrdinalShift;
    //     this.dataElements = dataElements;
    //     // this.shardsHolderVolatile = shardsHolder;
    // SNAP: TODO: No point referencing shardsHOlderVolatile here because stale value would mean a different shard needs to be looked up,
    //     so we have to go back up to read state.
    // }

    // HollowObjectTypeReadStateShard(HollowObjectTypeReadStateShard oldShard, HollowObjectTypeReadState.ShardsHolder shardsHolder) {
    //     this.schema = oldShard.schema;
    //     this.shardOrdinalShift = oldShard.shardOrdinalShift;
    //     this.dataElements = oldShard.dataElements;
    //     this.shardsHolderVolatile = shardsHolder;
    //     so we have to go back up to read state.
    // }

    public long isNull(int ordinal, int fieldIndex) {
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

    static class VarLenStats {
        int numBitsForField;
        long startByte;
        long endByte;
    }

    public byte[] readBytes(VarLenStats stats, int fieldIndex) {
        byte[] result;

        int numBitsForField = stats.numBitsForField;
        long startByte = stats.startByte;
        long endByte = stats.endByte;

        if((endByte & (1L << numBitsForField - 1)) != 0)
            return null;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);
        result = new byte[length];
        for(int i=0;i<length;i++)
            result[i] = dataElements.varLengthData[fieldIndex].get(startByte + i);

        return result;
    }

    public VarLenStats readVarLenStats(int ordinal, int fieldIndex) {
        VarLenStats result = new VarLenStats();
        int numBitsForField = dataElements.bitsPerField[fieldIndex];
        long currentBitOffset = fieldOffset(ordinal, fieldIndex);

        result.numBitsForField = numBitsForField;
        result.endByte = dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
        result.startByte = ordinal != 0 ? dataElements.fixedLengthData.getElementValue(currentBitOffset - dataElements.bitsPerRecord, numBitsForField) : 0;
        // TODO: constructor
        return result;
    }

    public String readString(VarLenStats stats, int fieldIndex) {
        int numBitsForField = stats.numBitsForField;
        long startByte = stats.startByte;
        long endByte = stats.endByte;

        if((endByte & (1L << numBitsForField - 1)) != 0)
            return null;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        return readString(dataElements.varLengthData[fieldIndex], startByte, length);
    }

    public boolean isStringFieldEqual(VarLenStats stats, int fieldIndex, String testValue) {
        int numBitsForField = stats.numBitsForField;
        long startByte = stats.startByte;
        long endByte = stats.endByte;

        if((endByte & (1L << numBitsForField - 1)) != 0)
            return testValue == null;
        if(testValue == null)
            return false;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        return testStringEquality(dataElements.varLengthData[fieldIndex], startByte, length, testValue);
    }

    public int findVarLengthFieldHashCode(VarLenStats stats, int fieldIndex) {
        int numBitsForField = stats.numBitsForField;
        long startByte = stats.startByte;
        long endByte = stats.endByte;

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
        return fieldIndex == -1 ? 0 : currentDataVolatile.bitsPerField[fieldIndex];
    }

    private long fieldOffset(int ordinal, int fieldIndex) {
        return ((long)dataElements.bitsPerRecord * ordinal) + dataElements.bitOffsetPerField[fieldIndex];
    }

    /**
     * Decode a String as a series of VarInts, one per character.<p>
     *
     * @param str
     * @param out
     * @return
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

    void invalidate() {
        setCurrentData(null);
    }

    HollowObjectTypeDataElements currentDataElements() {
        return currentDataVolatile;
    }

    private boolean readWasUnsafe(HollowObjectTypeReadState.ShardsHolder shardsHolder) {
        // Use a load (acquire) fence to constrain the compiler reordering prior plain loads so
        // that they cannot "float down" below the volatile load of shardsHolder.
        // This ensures data is checked against current shard holder *after* optimistic calculations
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
        return shardsHolder != shardsHolder;
    }

    void setCurrentData(HollowObjectTypeReadState.ShardsHolder shardsHolder) {
        this.shardsHolderVolatile = shardsHolder;
    }
    void setCurrentData(HollowObjectTypeDataElements data) {
        setCurrentData(data, true);
    }

    void setCurrentData(HollowObjectTypeDataElements data, boolean unsupported) {
        if (unsupported) {
            throw new UnsupportedOperationException("// SNAP: TODO: This volatile is now a final field");
        }
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema, BitSet populatedOrdinals, int shardNumber, int shardNumberMask) {
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
                    if(!schema.getFieldType(fieldIdx).isVariableLength()) {
                        long bitOffset = fieldOffset(shardOrdinal, fieldIdx);
                        int numBitsForField = dataElements.bitsPerField[fieldIdx];
                        long fixedLengthValue = numBitsForField <= 56 ?
                                dataElements.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                                : dataElements.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
    
                        if(fixedLengthValue == dataElements.nullValueForField[fieldIdx])
                            checksum.applyInt(Integer.MAX_VALUE);
                        else
                            checksum.applyLong(fixedLengthValue);
                    } else {
                        checksum.applyInt(findVarLengthFieldHashCode(shardOrdinal, fieldIdx));  // SNAP: TODO:
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
