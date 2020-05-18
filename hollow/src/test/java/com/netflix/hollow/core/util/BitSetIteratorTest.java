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
 */
package com.netflix.hollow.core.util;

import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;

public class BitSetIteratorTest {

    @Test
    public void testEmpty() {
        BitSetIterator it = new BitSetIterator(new BitSet());
        Assert.assertFalse(it.hasNext());
        Assert.assertNull(it.next());
    }

    @Test
    public void testNormal() {
        BitSet bs = new BitSet();
        for (int i = 0; i < 5; i++) {
            bs.set(i * 2);
        }

        int count = 0;
        BitSetIterator it = new BitSetIterator(bs);
        while (it.hasNext()) {
            Integer value = it.next();
            Assert.assertTrue(bs.get(value));
            count++;
        }

        Assert.assertEquals(bs.cardinality(), count);
    }

    @Test
    public void testRange() {
        BitSet bs = new BitSet();
        for (int i = 0; i < 20; i++) {
            bs.set(i * 2);
        }
        assertRange(bs, null, null);
        assertRange(bs, null, 10);
        assertRange(bs, 0, null);
        assertRange(bs, bs.cardinality() / 2, null);

        assertRange(bs, 0, 10);
        assertRange(bs, 5, 10);
        assertRange(bs, 15, 100);
        assertRange(bs, 30, 100);
    }

    private void assertRange(BitSet bs, Integer start, Integer limit) {
        int count = 0;
        BitSetIterator it = new BitSetIterator(bs, start, limit);
        while (it.hasNext()) {
            Integer value = it.next();
            Assert.assertTrue(bs.get(value));
            count++;
        }

        if (limit == null) {
            int expected = (start == null || start==0) ? bs.cardinality() : (bs.cardinality() - start)+1;
            Assert.assertEquals(expected, count);
        } else {
            int max = Math.min(limit, bs.cardinality());
            int expected =  (start == null || start==0) ? max : Math.min((bs.cardinality() - start)+1, max);
            if (expected<0) expected=0;
            Assert.assertEquals(expected, count);
        }
    }
}
