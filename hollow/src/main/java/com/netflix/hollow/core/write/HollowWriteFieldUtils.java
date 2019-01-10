package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteData;

public class HollowWriteFieldUtils {

    public static int readIntBits(ByteData data, long fieldPosition) {
        int intBits = (data.get(fieldPosition++) & 0xFF) << 24;
        intBits |= (data.get(fieldPosition++) & 0xFF) << 16;
        intBits |= (data.get(fieldPosition++) & 0xFF) << 8;
        intBits |= (data.get(fieldPosition) & 0xFF);
        return intBits;
    }

    public static long readLongBits(ByteData data, long fieldPosition) {
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
