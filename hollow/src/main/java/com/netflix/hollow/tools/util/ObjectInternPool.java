package com.netflix.hollow.tools.util;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.HashSet;
import java.util.Optional;


// This class memoizes types by returning references to existing objects, or storing
// Objects if they are not currently in the pool
public class ObjectInternPool {
    final private ByteArrayOrdinalMap ordinalMap;
    private boolean isReadyToRead = false;
    HashSet<Integer> ordinalsInCycle;

    public ObjectInternPool() {
        this.ordinalMap = new ByteArrayOrdinalMap(1024);
        this.ordinalsInCycle = new HashSet<>();
    }

    public void prepareForRead() {
        if(!isReadyToRead) {
            ordinalMap.prepareForWrite();
        }
        ordinalsInCycle.clear();
        isReadyToRead = true;
    }

    public boolean ordinalInCurrentCycle(int ordinal) {
        return ordinalsInCycle.contains(ordinal);
    }

    public Object getObject(int ordinal, FieldType type) {
        long pointer = ordinalMap.getPointerForData(ordinal);

        switch (type) {
            case BOOLEAN:
                return getBoolean(pointer);
            case FLOAT:
                return getFloat(pointer);
            case DOUBLE:
                return getDouble(pointer);
            case INT:
                return getInt(pointer);
            case LONG:
                return getLong(pointer);
            case STRING:
                return getString(pointer);
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public boolean getBoolean(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return byteData.get(pointer) == 1;
    }

    public float getFloat(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        int intBytes = VarInt.readVInt(byteData, pointer);
        return Float.intBitsToFloat(intBytes);
    }

    public double getDouble(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        long longBytes = VarInt.readVLong(byteData, pointer);
        return Double.longBitsToDouble(longBytes);
    }

    public int getInt(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return VarInt.readVInt(byteData, pointer);
    }

    public long getLong(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return VarInt.readVLong(byteData, pointer);
    }

    public String getString(long pointer) {
        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        int length = byteData.get(pointer);
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++) {
            bytes[i] = byteData.get(pointer+1+i);
        }
        return new String(bytes);
    }

    public int writeAndGetOrdinal(Object objectToIntern) {
        ByteDataArray buf = new ByteDataArray();
        if(objectToIntern==null) {
            throw new IllegalArgumentException("Cannot intern null objects");
        }
        isReadyToRead = false;

        if(objectToIntern instanceof Float) {
            int intBits = Float.floatToIntBits((Float) objectToIntern);
            VarInt.writeVInt(buf, intBits);
        } else if(objectToIntern instanceof Double) {
            long longBits = Double.doubleToLongBits((Double) objectToIntern);
            VarInt.writeVLong(buf, longBits);
        } else if(objectToIntern instanceof Integer) {
            int intBits = (int) objectToIntern;
            VarInt.writeVInt(buf, intBits);
        } else if(objectToIntern instanceof Long) {
            long longBits = (long) objectToIntern;
            VarInt.writeVLong(buf, longBits);
        } else if(objectToIntern instanceof String) {
            VarInt.writeVInt(buf, ((String) objectToIntern).length());
            for (byte b : ((String) objectToIntern).getBytes()) {
                buf.write(b);
            }
        } else if(objectToIntern instanceof Boolean) {
            int valToWrite = (boolean) objectToIntern ? 1 : 0;
            VarInt.writeVInt(buf, valToWrite);
        } else {
            String className = objectToIntern.getClass().getName();
            throw new IllegalArgumentException("Cannot intern object of type " + className);
        }
        int ordinal = ordinalMap.getOrAssignOrdinal(buf);
        ordinalsInCycle.add(ordinal);
        return ordinal;
    }
}