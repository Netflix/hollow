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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.memory.ThreadSafeBitSet;

import org.junit.Assert;
import org.junit.Test;

public class ThreadSafeBitSetTest {

    @Test
    public void testEquality() {
        ThreadSafeBitSet set1 = new ThreadSafeBitSet();
        ThreadSafeBitSet set2 = new ThreadSafeBitSet();

        set1.set(100);

        set2.set(100);

        Assert.assertEquals(set1, set2);
        Assert.assertEquals(set2, set1);

        set1.set(100000);

        Assert.assertNotEquals(set1, set2);
        Assert.assertNotEquals(set2, set1);

        set1.clearAll();

        Assert.assertNotEquals(set1, set2);
        Assert.assertNotEquals(set2, set1);

        set1.set(100);

        Assert.assertEquals(set1, set2);
        Assert.assertEquals(set2, set1);

    }

    @Test
    public void testMaxSetBit() {
        ThreadSafeBitSet set1 = new ThreadSafeBitSet();

        set1.set(100);
        Assert.assertEquals(100, set1.maxSetBit());


        set1.set(100000);
        Assert.assertEquals(100000, set1.maxSetBit());

        set1.set(1000000);
        Assert.assertEquals(1000000, set1.maxSetBit());

        set1.clearAll();
        set1.set(555555);
        Assert.assertEquals(555555, set1.maxSetBit());
    }


    @Test
    public void testNextSetBit() {
        ThreadSafeBitSet set1 = new ThreadSafeBitSet();

        set1.set(100);
        set1.set(101);
        set1.set(103);
        set1.set(100000);
        set1.set(1000000);

        Assert.assertEquals(100, set1.nextSetBit(0));
        Assert.assertEquals(101, set1.nextSetBit(101));
        Assert.assertEquals(103, set1.nextSetBit(102));
        Assert.assertEquals(100000, set1.nextSetBit(104));
        Assert.assertEquals(1000000, set1.nextSetBit(100001));
        Assert.assertEquals(-1, set1.nextSetBit(1000001));
        Assert.assertEquals(-1, set1.nextSetBit(1015809));

        set1.clearAll();
        set1.set(555555);
        Assert.assertEquals(555555, set1.nextSetBit(0));
        Assert.assertEquals(-1, set1.nextSetBit(555556));
    }
    @Test
    public void testClear() {
        ThreadSafeBitSet set1 = new ThreadSafeBitSet();

        set1.set(10);
        set1.set(20);
        set1.set(21);
        set1.set(22);

        set1.clear(21);

        Assert.assertEquals(3, set1.cardinality());
    }
}
