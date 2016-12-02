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
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowObjectSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowObjectTypeReadState extends HollowTypeReadState implements HollowObjectTypeDataAccess {

    private HollowObjectTypeDataElements currentData;
    private volatile HollowObjectTypeDataElements currentDataVolatile;

    private final HollowObjectSchema unfilteredSchema;
    private final HollowObjectSampler sampler;

    public HollowObjectTypeReadState(HollowReadStateEngine stateEngine, HollowObjectSchema schema) {
        this(stateEngine, schema, schema);
    }

    public HollowObjectTypeReadState(HollowReadStateEngine stateEngine, HollowObjectSchema schema, HollowObjectSchema unfilteredSchema) {
        super(stateEngine, schema);
        this.sampler = new HollowObjectSampler(schema, DisabledSamplingDirector.INSTANCE);
        this.unfilteredSchema = unfilteredSchema;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    @Override
    public int maxOrdinal() {
        return currentData.maxOrdinal;
    }

    @Override
    public void readSnapshot(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowObjectTypeDataElements currentData = new HollowObjectTypeDataElements(getSchema(), memoryRecycler);
        currentData.readSnapshot(dis, unfilteredSchema);
        setCurrentData(currentData);
        SnapshotPopulatedOrdinalsReader.readOrdinals(dis, stateListeners);
    }

    @Override
    public void applyDelta(DataInputStream dis, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowObjectTypeDataElements deltaData = new HollowObjectTypeDataElements((HollowObjectSchema)deltaSchema, memoryRecycler);
        HollowObjectTypeDataElements nextData = new HollowObjectTypeDataElements(getSchema(), memoryRecycler);
        deltaData.readDelta(dis);
        nextData.applyDelta(currentData, deltaData);
        HollowObjectTypeDataElements oldData = currentData;
        setCurrentData(nextData);
        notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions);
        deltaData.destroy();
        oldData.destroy();
    }

    public static void discardSnapshot(DataInputStream dis, HollowObjectSchema schema) throws IOException {
        discardType(dis, schema, false);
    }

    public static void discardDelta(DataInputStream dis, HollowObjectSchema schema) throws IOException {
        discardType(dis, schema, true);
    }

    public static void discardType(DataInputStream dis, HollowObjectSchema schema, boolean delta) throws IOException {
        HollowObjectTypeDataElements.discardFromStream(dis, schema, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(dis);
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long fixedLengthValue;

        do {
            currentData = this.currentData;

            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            int numBitsForField = currentData.bitsPerField[fieldIndex];

            fixedLengthValue = numBitsForField <= 56 ?
                    currentData.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                    : currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
        } while(readWasUnsafe(currentData));

        switch(getSchema().getFieldType(fieldIndex)) {
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

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long refOrdinal;

        do {
            currentData = this.currentData;
            refOrdinal = readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(refOrdinal == currentData.nullValueForField[fieldIndex])
            return -1;
        return (int)refOrdinal;
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentData;
            value = readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(value == currentData.nullValueForField[fieldIndex])
            return Integer.MIN_VALUE;
        return ZigZag.decodeInt((int)value);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        int value;

        do {
            currentData = this.currentData;
            value = (int)readFixedLengthFieldValue(currentData, ordinal, fieldIndex);
        } while(readWasUnsafe(currentData));

        if(value == HollowObjectWriteRecord.NULL_FLOAT_BITS)
            return Float.NaN;
        return Float.intBitsToFloat(value);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentData;
            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            value = currentData.fixedLengthData.getLargeElementValue(bitOffset, 64, -1L);
        } while(readWasUnsafe(currentData));

        if(value == HollowObjectWriteRecord.NULL_DOUBLE_BITS)
            return Double.NaN;
        return Double.longBitsToDouble(value);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentData;
            long bitOffset = fieldOffset(currentData, ordinal, fieldIndex);
            int numBitsForField = currentData.bitsPerField[fieldIndex];
            value = currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);
        } while(readWasUnsafe(currentData));

        if(value == currentData.nullValueForField[fieldIndex])
            return Long.MIN_VALUE;
        return ZigZag.decodeLong(value);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        long value;

        do {
            currentData = this.currentData;
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

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        byte[] result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentData;

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

    @Override
    public String readString(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        String result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentData;

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

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        boolean result;

        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentData;

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

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeDataElements currentData;
        int hashCode;
        do {
            int numBitsForField;
            long endByte;
            long startByte;

            do {
                currentData = this.currentData;

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
        int fieldIndex = getSchema().getPosition(fieldName);
        return fieldIndex == -1 ? 0 : currentData.bitsPerField[fieldIndex];
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

    @Override
    public HollowSampler getSampler() {
        return sampler;
    }

    @Override
    protected void invalidate() {
        stateListeners = EMPTY_LISTENERS;
        setCurrentData(null);
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        sampler.setSamplingDirector(director);
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        sampler.setFieldSpecificSamplingDirector(fieldSpec, director);
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        sampler.setUpdateThread(t);
    }

    HollowObjectTypeDataElements currentDataElements() {
        return currentData;
    }

    private boolean readWasUnsafe(HollowObjectTypeDataElements data) {
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowObjectTypeDataElements data) {
        this.currentData = data;
        this.currentDataVolatile = data;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + getSchema().getName());

        HollowObjectSchema commonSchema = getSchema().findCommonSchema((HollowObjectSchema)withSchema);

        List<String> commonFieldNames = new ArrayList<String>();
        for(int i=0;i<commonSchema.numFields();i++)
            commonFieldNames.add(commonSchema.getFieldName(i));
        Collections.sort(commonFieldNames);
        
        int fieldIndexes[] = new int[commonFieldNames.size()];
        for(int i=0;i<commonFieldNames.size();i++) {
            fieldIndexes[i] = getSchema().getPosition(commonFieldNames.get(i));
        }
        
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            checksum.applyInt(ordinal);
            for(int i=0;i<fieldIndexes.length;i++) {
                int fieldIdx = fieldIndexes[i];
                if(!getSchema().getFieldType(fieldIdx).isVariableLength()) {
                    long bitOffset = fieldOffset(currentData, ordinal, fieldIdx);
                    int numBitsForField = currentData.bitsPerField[fieldIdx];
                    long fixedLengthValue = numBitsForField <= 56 ?
                            currentData.fixedLengthData.getElementValue(bitOffset, numBitsForField)
                            : currentData.fixedLengthData.getLargeElementValue(bitOffset, numBitsForField);

                    if(fixedLengthValue == currentData.nullValueForField[fieldIdx])
                        checksum.applyInt(Integer.MAX_VALUE);
                    else
                        checksum.applyLong(fixedLengthValue);
                } else {
                    checksum.applyInt(findVarLengthFieldHashCode(ordinal, fieldIdx));
                }
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
		long bitsPerFixedLengthData = (long)currentData.bitsPerRecord * (currentData.maxOrdinal + 1);
		
		long requiredBytes = bitsPerFixedLengthData / 8;
		
		for(int i=0;i<currentData.varLengthData.length;i++) {
			if(currentData.varLengthData[i] != null)
				requiredBytes += currentData.varLengthData[i].size();
		}
		
		return requiredBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        
        return ((long)(populatedOrdinals.length() - populatedOrdinals.cardinality()) * (long)currentData.bitsPerRecord) / 8; 
	}

}
