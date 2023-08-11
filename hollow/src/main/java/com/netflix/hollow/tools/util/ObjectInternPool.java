package com.netflix.hollow.tools.util;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.encoding.VarInt;


// This class memoizes types by returning references to existing objects, or storing
// Objects if they are not currently in the pool
public class ObjectInternPool {
    final private ByteArrayOrdinalMap ordinalMap;
    private boolean isReadyToRead = false;
    public ObjectInternPool() {
        this.ordinalMap = new ByteArrayOrdinalMap(1024);
    }

    private void ensureReadyToRead() {
        if(!isReadyToRead) {
            ordinalMap.prepareForWrite();
        }
        isReadyToRead = true;
    }

    public boolean getBoolean(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return false;
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return byteData.get(pointer) == 1;
    }

    public float getFloat(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return Float.NaN; //TODO: could have actual NaN's in memory, bad way of representing "not found"
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        int intBytes = VarInt.readVInt(byteData, pointer);
        return Float.intBitsToFloat(intBytes);
    }

    public double getDouble(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return Double.NaN; //TODO: could have actual NaN's in memory, bad way of representing "not found"
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        long longBytes = VarInt.readVLong(byteData, pointer);
        return Double.longBitsToDouble(longBytes);
    }

    public int getInt(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return -1; //TODO: could have actual -1's in memory, bad way of representing "not found"
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return VarInt.readVInt(byteData, pointer);
    }

    public long getLong(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return -1L; //TODO: could have actual -1's in memory, bad way of representing "not found"
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        return VarInt.readVLong(byteData, pointer);
    }

    public String getString(int ordinal) {
        ensureReadyToRead();

        long pointer = ordinalMap.getPointerForData(ordinal);
        if (pointer==-1L) {
            return null;
        }

        ByteData byteData = ordinalMap.getByteData().getUnderlyingArray();
        int length = byteData.get(pointer);
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++) {
            bytes[i] = byteData.get(pointer+1+i);
        }
        return new String(bytes);
    }

    //TODO: consider splitting into individual functions?
    //NOTE: this function is inefficient if repeatedly read/write
    //designed to read, then write, etc
    //TODO: see if this can be improved
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
            //add length to beginning of string
            VarInt.writeVInt(buf, ((String) objectToIntern).length());
            for (byte b : ((String) objectToIntern).getBytes()) {
                buf.write(b);
            }
        } else if(objectToIntern instanceof Boolean) {
            //for consistency
            boolean bool = (boolean) objectToIntern;
            if(bool) {
                VarInt.writeVInt(buf, 1);
            } else {
                VarInt.writeVInt(buf, 0);
            }
        } else {
            String className = objectToIntern.getClass().getName();
            throw new IllegalArgumentException("Cannot intern object of type " + className);
        }

        return ordinalMap.getOrAssignOrdinal(buf);
    }
}