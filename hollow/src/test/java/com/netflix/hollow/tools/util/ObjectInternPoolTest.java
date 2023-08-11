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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

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

        assertEquals(int1Ordinal, int2Ordinal);
        assertEquals(internPool.getInt(int1Ordinal), 130);

        Integer intObj3 = 1900;
        Integer intObj4 = 1900;

        int int3Ordinal = internPool.writeAndGetOrdinal(intObj3);
        int int4Ordinal = internPool.writeAndGetOrdinal(intObj4);

        assertEquals(int3Ordinal, int4Ordinal);
        assertEquals(internPool.getInt(int3Ordinal), 1900);
    }

    @Test
    public void testFloat() {
        Float floatObj1 = 130.0f;
        Float floatObj2 = 130.0f;
        Float floatObj3 = 1900.0f;
        Float floatObj4 = 1900.0f;

        int float1Ordinal = internPool.writeAndGetOrdinal(floatObj1);
        int float2Ordinal = internPool.writeAndGetOrdinal(floatObj2);

        assertEquals(float1Ordinal, float2Ordinal);
        assertEquals(internPool.getFloat(float1Ordinal), 130.0f, 0.0f);

        int float3Ordinal = internPool.writeAndGetOrdinal(floatObj3);
        int float4Ordinal = internPool.writeAndGetOrdinal(floatObj4);

        assertEquals(float3Ordinal, float4Ordinal);
        assertEquals(internPool.getFloat(float3Ordinal), 1900.0f, 0.0f);
    }

    @Test
    public void testLong() {
        Long longObj1 = 130L;
        Long longObj2 = 130L;
        Long longObj3 = 1900L;
        Long longObj4 = 1900L;

        int long1Ordinal = internPool.writeAndGetOrdinal(longObj1);
        int long2Ordinal = internPool.writeAndGetOrdinal(longObj2);

        assertEquals(long1Ordinal, long2Ordinal);
        assertEquals(internPool.getLong(long1Ordinal), 130L);

        int long3Ordinal = internPool.writeAndGetOrdinal(longObj3);
        int long4Ordinal = internPool.writeAndGetOrdinal(longObj4);

        assertEquals(long3Ordinal, long4Ordinal);
        assertEquals(internPool.getLong(long3Ordinal), 1900L);
    }

    @Test
    public void testDouble() {
        Double doubleObj1 = 130.0;
        Double doubleObj2 = 130.0;
        Double doubleObj3 = 1900.0;
        Double doubleObj4 = 1900.0;

        int double1Ordinal = internPool.writeAndGetOrdinal(doubleObj1);
        int double2Ordinal = internPool.writeAndGetOrdinal(doubleObj2);

        assertEquals(double1Ordinal, double2Ordinal);
        assertEquals(internPool.getDouble(double1Ordinal), 130.0, 0.0);

        int double3Ordinal = internPool.writeAndGetOrdinal(doubleObj3);
        int double4Ordinal = internPool.writeAndGetOrdinal(doubleObj4);

        assertEquals(double3Ordinal, double4Ordinal);
        assertEquals(internPool.getDouble(double3Ordinal), 1900.0, 0.0);
    }

    @Test
    public void testString() {
        String stringObj1 = "I am Groot";
        String stringObj2 = "I am Groot";
        String stringObj3 = "I can do this all day";
        String stringObj4 = "I can do this all day";

        int string1Ordinal = internPool.writeAndGetOrdinal(stringObj1);
        int string2Ordinal = internPool.writeAndGetOrdinal(stringObj2);

        assertEquals(string1Ordinal, string2Ordinal);
        assertEquals(internPool.getString(string1Ordinal), "I am Groot");

        int string3Ordinal = internPool.writeAndGetOrdinal(stringObj3);
        int string4Ordinal = internPool.writeAndGetOrdinal(stringObj4);

        assertEquals(string3Ordinal, string4Ordinal);
        assertEquals(internPool.getString(string3Ordinal), "I can do this all day");
    }

    @Test
    public void testBool() {
        Boolean boolObj1 = true;
        Boolean boolObj2 = true;
        Boolean boolObj3 = false;
        Boolean boolObj4 = false;

        int bool1Ordinal = internPool.writeAndGetOrdinal(boolObj1);
        int bool2Ordinal = internPool.writeAndGetOrdinal(boolObj2);

        assertEquals(bool1Ordinal, bool2Ordinal);
        assertEquals(internPool.getBoolean(bool1Ordinal), true);

        int bool3Ordinal = internPool.writeAndGetOrdinal(boolObj3);
        int bool4Ordinal = internPool.writeAndGetOrdinal(boolObj4);

        assertEquals(bool3Ordinal, bool4Ordinal);
        assertEquals(internPool.getBoolean(bool3Ordinal), false);
    }
}
