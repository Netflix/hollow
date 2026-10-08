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
package com.netflix.hollow.tools.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import org.junit.Before;
import org.junit.Test;

public class ObjectInternPoolTest {

    ObjectInternPool internPool = new ObjectInternPool();

    @Before
    public void setup() {
        internPool = new ObjectInternPool();
    }

    @Test
    public void testInt() {
        Integer intObj1 = 130;
        Integer intObj2 = 130;

        int int1Ordinal = internPool.writeAndGetOrdinal(intObj1);
        int int2Ordinal = internPool.writeAndGetOrdinal(intObj2);
        internPool.prepareForRead();

        int retrievedInt1 = (int) internPool.getObject(int1Ordinal, FieldType.INT);

        assertEquals(int1Ordinal, int2Ordinal);
        assertEquals(retrievedInt1, 130);

        Integer intObj3 = 1900;
        Integer intObj4 = 1900;

        int int3Ordinal = internPool.writeAndGetOrdinal(intObj3);
        int int4Ordinal = internPool.writeAndGetOrdinal(intObj4);
        internPool.prepareForRead();

        int retrievedInt3 = (int) internPool.getObject(int3Ordinal, FieldType.INT);

        assertEquals(int3Ordinal, int4Ordinal);
        assertEquals(retrievedInt3, 1900);
    }

    @Test
    public void testFloat() {
        Float floatObj1 = 130.0f;
        Float floatObj2 = 130.0f;
        Float floatObj3 = 1900.0f;
        Float floatObj4 = 1900.0f;

        int float1Ordinal = internPool.writeAndGetOrdinal(floatObj1);
        int float2Ordinal = internPool.writeAndGetOrdinal(floatObj2);
        internPool.prepareForRead();

        float retrievedFloat1 = (float) internPool.getObject(float1Ordinal, FieldType.FLOAT);

        assertEquals(float1Ordinal, float2Ordinal);
        assertEquals(retrievedFloat1, 130.0f, 0.0f);

        int float3Ordinal = internPool.writeAndGetOrdinal(floatObj3);
        int float4Ordinal = internPool.writeAndGetOrdinal(floatObj4);
        internPool.prepareForRead();

        float retrievedFloat2 = (float) internPool.getObject(float3Ordinal, FieldType.FLOAT);

        assertEquals(float3Ordinal, float4Ordinal);
        assertEquals(retrievedFloat2, 1900.0f, 0.0f);
    }

    @Test
    public void testLong() {
        Long longObj1 = 130L;
        Long longObj2 = 130L;
        Long longObj3 = 1900L;
        Long longObj4 = 1900L;

        int long1Ordinal = internPool.writeAndGetOrdinal(longObj1);
        int long2Ordinal = internPool.writeAndGetOrdinal(longObj2);
        internPool.prepareForRead();

        long retrievedLong1 = (Long) internPool.getObject(long1Ordinal, FieldType.LONG);

        assertEquals(long1Ordinal, long2Ordinal);
        assertEquals(retrievedLong1, 130L);

        int long3Ordinal = internPool.writeAndGetOrdinal(longObj3);
        int long4Ordinal = internPool.writeAndGetOrdinal(longObj4);
        internPool.prepareForRead();

        long retrievedLong2 = (Long) internPool.getObject(long3Ordinal, FieldType.LONG);

        assertEquals(long3Ordinal, long4Ordinal);
        assertEquals(retrievedLong2, 1900L);
    }

    @Test
    public void testDouble() {
        Double doubleObj1 = 130.0;
        Double doubleObj2 = 130.0;
        Double doubleObj3 = 1900.0;
        Double doubleObj4 = 1900.0;

        int double1Ordinal = internPool.writeAndGetOrdinal(doubleObj1);
        int double2Ordinal = internPool.writeAndGetOrdinal(doubleObj2);
        internPool.prepareForRead();

        double retrievedDouble1 = (double) internPool.getObject(double1Ordinal, FieldType.DOUBLE);

        assertEquals(double1Ordinal, double2Ordinal);
        assertEquals(retrievedDouble1, 130.0, 0.0);

        int double3Ordinal = internPool.writeAndGetOrdinal(doubleObj3);
        int double4Ordinal = internPool.writeAndGetOrdinal(doubleObj4);
        internPool.prepareForRead();

        double retrievedDouble2 = (double) internPool.getObject(double3Ordinal, FieldType.DOUBLE);

        assertEquals(double3Ordinal, double4Ordinal);
        assertEquals(retrievedDouble2, 1900.0, 0.0);
    }

    @Test
    public void testString() {
        String stringObj1 = "I am Groot";
        String stringObj2 = "I am Groot";
        String stringObj3 = "I can do this all day";
        String stringObj4 = "I can do this all day";

        int string1Ordinal = internPool.writeAndGetOrdinal(stringObj1);
        int string2Ordinal = internPool.writeAndGetOrdinal(stringObj2);
        internPool.prepareForRead();

        String retrievedString1 = (String) internPool.getObject(string1Ordinal, FieldType.STRING);

        assertEquals(string1Ordinal, string2Ordinal);
        assertEquals(retrievedString1, "I am Groot");

        int string3Ordinal = internPool.writeAndGetOrdinal(stringObj3);
        int string4Ordinal = internPool.writeAndGetOrdinal(stringObj4);
        internPool.prepareForRead();

        String retrievedString2 = (String) internPool.getObject(string3Ordinal, FieldType.STRING);

        assertEquals(string3Ordinal, string4Ordinal);
        assertEquals(retrievedString2, "I can do this all day");
    }

    @Test
    public void testBytes() {
        byte[] bytesObj1 = "I am Groot".getBytes();
        byte[] bytesObj2 = "I am Groot".getBytes();
        byte[] bytesObj3 = "I can do this all day".getBytes();
        byte[] bytesObj4 = "I can do this all day".getBytes();

        int bytes1Ordinal = internPool.writeAndGetOrdinal(bytesObj1);
        int bytes2Ordinal = internPool.writeAndGetOrdinal(bytesObj2);
        internPool.prepareForRead();

        byte[] retrievedString1 = (byte[]) internPool.getObject(bytes1Ordinal, FieldType.BYTES);

        assertEquals(bytes1Ordinal, bytes2Ordinal);
        assertArrayEquals(retrievedString1, "I am Groot".getBytes());

        int bytes3Ordinal = internPool.writeAndGetOrdinal(bytesObj3);
        int bytes4Ordinal = internPool.writeAndGetOrdinal(bytesObj4);
        internPool.prepareForRead();

        byte[] retrievedBytes2 = (byte[]) internPool.getObject(bytes3Ordinal, FieldType.BYTES);

        assertEquals(bytes3Ordinal, bytes4Ordinal);
        assertArrayEquals(retrievedBytes2, "I can do this all day".getBytes());
    }

    @Test
    public void testBool() {
        Boolean boolObj1 = true;
        Boolean boolObj2 = true;
        Boolean boolObj3 = false;
        Boolean boolObj4 = false;

        int bool1Ordinal = internPool.writeAndGetOrdinal(boolObj1);
        int bool2Ordinal = internPool.writeAndGetOrdinal(boolObj2);
        internPool.prepareForRead();

        boolean retrievedBool1 = (boolean) internPool.getObject(bool1Ordinal, FieldType.BOOLEAN);

        assertEquals(bool1Ordinal, bool2Ordinal);
        assertEquals(retrievedBool1, true);

        int bool3Ordinal = internPool.writeAndGetOrdinal(boolObj3);
        int bool4Ordinal = internPool.writeAndGetOrdinal(boolObj4);
        internPool.prepareForRead();

        boolean retrievedBool2 = (boolean) internPool.getObject(bool3Ordinal, FieldType.BOOLEAN);

        assertEquals(bool3Ordinal, bool4Ordinal);
        assertEquals(retrievedBool2, false);
    }
}
