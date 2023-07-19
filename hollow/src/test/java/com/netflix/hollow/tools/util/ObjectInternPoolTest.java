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

    GenericInternPool<Integer> integerPool;
    GenericInternPool<Float> floatPool;
    GenericInternPool<Double> doublePool;
    GenericInternPool<Long> longPool;
    ObjectInternPool internPool;

    @Before
    public void setup() {
        integerPool = new GenericInternPool<>();
        floatPool = new GenericInternPool<>();
        doublePool = new GenericInternPool<>();
        longPool = new GenericInternPool<>();

        internPool = new ObjectInternPool();
    }

    @Test
    public void testInteger() {
        // Java caches boxed integers between -127 and 128
        // Must be outside that range to test interning
        Integer intObj = 130;
        Integer dupIntObj = 130;

        Integer internedInt = integerPool.intern(intObj);
        Integer dupInternedInt = integerPool.intern(dupIntObj);

        assertNotSame(intObj, dupIntObj);
        assertSame(internedInt, dupInternedInt);
        assertEquals(intObj, internedInt);
    }

    @Test
    public void testFloat() {
        Float floatObj = 130f;
        Float dupFloatObj = 130f;

        Float internedFloat = floatPool.intern(floatObj);
        Float dupInternedFloat = floatPool.intern(dupFloatObj);

        assertNotSame(floatObj, dupFloatObj);
        assertSame(internedFloat, dupInternedFloat);
        assertEquals(floatObj, internedFloat);
    }

    @Test
    public void testDouble() {
        Double doubleObj = 130d;
        Double dupDoubleObj = 130d;

        Double internedDouble = doublePool.intern(doubleObj);
        Double dupInternedDouble = doublePool.intern(dupDoubleObj);

        assertNotSame(doubleObj, dupDoubleObj);
        assertSame(internedDouble, dupInternedDouble);
        assertEquals(doubleObj, internedDouble);
    }

    @Test
    public void testLong() {
        Long longObj = 130L;
        Long dupLongObj = 130L;

        Long internedLong = longPool.intern(longObj);
        Long dupInternedLong = longPool.intern(dupLongObj);

        assertNotSame(longObj, dupLongObj);
        assertSame(internedLong, dupInternedLong);
        assertEquals(longObj, internedLong);
    }

    @Test
    public void testAutoInternInteger() {
        //Java should automatically cache these
        Integer lowInt = -128;
        Integer dupLowInt = -128;

        Integer highInt = 127;
        Integer dupHighInt = 127;

        Integer internedLow1 = integerPool.intern(lowInt);
        Integer internedLow2 = integerPool.intern(dupLowInt);

        Integer internedHigh1 = integerPool.intern(highInt);
        Integer internedHigh2 = integerPool.intern(dupHighInt);

        assertSame(lowInt, dupLowInt);
        assertSame(highInt, dupHighInt);

        assertSame(internedLow1, internedLow2);
        assertSame(internedHigh1, internedHigh2);

        assertEquals(lowInt, internedLow1);
        assertEquals(highInt, internedHigh1);
    }

    @Test
    public void testAll() {
        Integer intObj = 130;
        Integer dupIntObj = 130;

        Float floatObj = 140f;
        Float dupFloatObj = 140f;

        Double doubleObj = 150d;
        Double dupDoubleObj = 150d;

        Long longObj = 160L;
        Long dupLongObj = 160L;

        String stringObj = new String("I am groot");
        String dupStringObj = new String("I am groot");

        Boolean booleanObj = true;
        Boolean dupBooleanObj = true;

        assertNotSame(intObj, dupIntObj);
        assertNotSame(floatObj, dupFloatObj);
        assertNotSame(doubleObj, dupDoubleObj);
        assertNotSame(stringObj, dupStringObj);
        assertNotSame(longObj, dupLongObj);
        //booleans always cached

        Integer internedInt1 = (Integer)internPool.intern(intObj);
        Integer internedInt2 = (Integer)internPool.intern(dupIntObj);

        Float internedFloat1 = (Float)internPool.intern(floatObj);
        Float internedFloat2 = (Float)internPool.intern(dupFloatObj);

        Double internedDouble1 = (Double)internPool.intern(doubleObj);
        Double internedDouble2 = (Double)internPool.intern(dupDoubleObj);

        Long internedLong1 = (Long)internPool.intern(longObj);
        Long internedLong2 = (Long)internPool.intern(dupLongObj);

        String internedString1 = (String)internPool.intern(stringObj);
        String internedString2 = (String)internPool.intern(dupStringObj);

        Boolean internedBoolean1 = (Boolean)internPool.intern(booleanObj);
        Boolean internedBoolean2 = (Boolean)internPool.intern(dupBooleanObj);

        assertSame(internedInt1, internedInt2);
        assertSame(internedFloat1, internedFloat2);
        assertSame(internedDouble1, internedDouble2);
        assertSame(internedLong1, internedLong2);
        assertSame(internedString1, internedString2);
        assertSame(internedBoolean1, internedBoolean2);

        assertEquals(intObj, internedInt1);
        assertEquals(floatObj, internedFloat1);
        assertEquals(doubleObj, internedDouble1);
        assertEquals(longObj, internedLong1);
        assertEquals(stringObj, internedString1);
        assertEquals(booleanObj, internedBoolean1);
    }
}
