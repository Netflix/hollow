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
package com.netflix.hollow.core.memory;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class HollowUnsafeHandle {
    private static final Logger log = Logger.getLogger(HollowUnsafeHandle.class.getName());
    private static final Unsafe unsafe;
    private static final HollowUnsafeHandle singleton;

    // TODO: figure this out somehow
    private static final boolean UNALIGNED_ALLOWED = false;

    private HollowUnsafeHandle() {}

    static {
        Field theUnsafe;
        Unsafe u = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            u = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unsafe access failed", e);
        }
        unsafe = u;
        singleton = new HollowUnsafeHandle();
    }

    public static HollowUnsafeHandle getUnsafe() {
        return singleton;
    }

    public void putOrderedLong(Object o, long l, long l1) {
        if (UNALIGNED_ALLOWED) {
            unsafe.putOrderedLong(o, l, l1);
        } else {
            // Assume little endian
            unsafe.putByteVolatile(o, l, (byte) (l1));
            unsafe.putByteVolatile(o, l + 1, (byte) (l1 >>> 8));
            unsafe.putByteVolatile(o, l + 2, (byte) (l1 >>> 16));
            unsafe.putByteVolatile(o, l + 3, (byte) (l1 >>> 24));
            unsafe.putByteVolatile(o, l + 4, (byte) (l1 >>> 32));
            unsafe.putByteVolatile(o, l + 5, (byte) (l1 >>> 40));
            unsafe.putByteVolatile(o, l + 6, (byte) (l1 >>> 48));
            unsafe.putByteVolatile(o, l + 7, (byte) (l1 >>> 56));
        }
    }

    public long getLong(Object o, long l) {
        if (UNALIGNED_ALLOWED) {
            return unsafe.getLong(o, l);
        } else {
            // Assume little endian
            return ((long) unsafe.getByte(o, l) & 0xFF)
                    | (((long) unsafe.getByte(o, l + 1) & 0xFF) << 8)
                    | (((long) unsafe.getByte(o, l + 2) & 0xFF) << 16)
                    | (((long) unsafe.getByte(o, l + 3) & 0xFF) << 24)
                    | (((long) unsafe.getByte(o, l + 4) & 0xFF) << 32)
                    | (((long) unsafe.getByte(o, l + 5) & 0xFF) << 40)
                    | (((long) unsafe.getByte(o, l + 6) & 0xFF) << 48)
                    | (((long) unsafe.getByte(o, l + 7) & 0xFF) << 56);
        }
    }

    public void putByteVolatile(Object o, long l, byte b) {
        unsafe.putByteVolatile(o, l, b);
    }

    public void loadFence() {
        unsafe.loadFence();
    }

    public long objectFieldOffset(Field field) {
        return unsafe.objectFieldOffset(field);
    }

    public Object getObject(Object o, long l) {
        return unsafe.getObject(o, l);
    }

    public float getFloat(Object o, long l) {
        return unsafe.getFloat(o, l);
    }

    public double getDouble(Object o, long l) {
        return unsafe.getDouble(o, l);
    }

    public void putLong(Object o, long l, long l1) {
        if (UNALIGNED_ALLOWED) {
            unsafe.putLong(o, l, l1);
        } else {
            // Assume little endian
            unsafe.putByte(o, l, (byte) (l1));
            unsafe.putByte(o, l + 1, (byte) (l1 >>> 8));
            unsafe.putByte(o, l + 2, (byte) (l1 >>> 16));
            unsafe.putByte(o, l + 3, (byte) (l1 >>> 24));
            unsafe.putByte(o, l + 4, (byte) (l1 >>> 32));
            unsafe.putByte(o, l + 5, (byte) (l1 >>> 40));
            unsafe.putByte(o, l + 6, (byte) (l1 >>> 48));
            unsafe.putByte(o, l + 7, (byte) (l1 >>> 56));
        }
    }

    public boolean getBoolean(Object o, long l) {
        return unsafe.getBoolean(o, l);
    }

    public int getInt(Object o, long l) {
        return unsafe.getInt(o, l);
    }

    public short getShort(Object o, long l) {
        return unsafe.getShort(o, l);
    }

    public byte getByte(Object o, long l) {
        return unsafe.getByte(o, l);
    }

    public char getChar(Object o, long l) {
        return unsafe.getChar(o, l);
    }
}
