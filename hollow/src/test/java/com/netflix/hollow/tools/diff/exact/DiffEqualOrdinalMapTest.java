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
package com.netflix.hollow.tools.diff.exact;

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.MatchIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiffEqualOrdinalMapTest {

    private DiffEqualOrdinalMap map;

    @Before
    public void setUp() {
        map = new DiffEqualOrdinalMap(10);

        map.putEqualOrdinals(1, list(1, 2, 3));
        map.putEqualOrdinals(2, list(4, 5, 6));
        map.putEqualOrdinals(3, list(7, 8, 9));
        map.putEqualOrdinals(100, list(5025));
    }

    @Test
    public void testFromOrdinals() {
        assertMatchIterator(map.getEqualOrdinals(1), 1, 2, 3);
        assertMatchIterator(map.getEqualOrdinals(2), 4, 5, 6);
        assertMatchIterator(map.getEqualOrdinals(3), 7, 8, 9);
        assertMatchIterator(map.getEqualOrdinals(100), 5025);
        assertMatchIterator(map.getEqualOrdinals(200));
    }

    @Test
    public void testFromIdentityOrdinals() {
        Assert.assertEquals(1, map.getIdentityFromOrdinal(1));
        Assert.assertEquals(4, map.getIdentityFromOrdinal(2));
        Assert.assertEquals(7, map.getIdentityFromOrdinal(3));
        Assert.assertEquals(5025, map.getIdentityFromOrdinal(100));
        Assert.assertEquals(-1, map.getIdentityFromOrdinal(200));
    }

    @Test
    public void testToIdentityOrdinals() {
        map.buildToOrdinalIdentityMapping();

        Assert.assertEquals(1, map.getIdentityToOrdinal(1));
        Assert.assertEquals(1, map.getIdentityToOrdinal(2));
        Assert.assertEquals(1, map.getIdentityToOrdinal(3));
        Assert.assertEquals(4, map.getIdentityToOrdinal(4));
        Assert.assertEquals(4, map.getIdentityToOrdinal(5));
        Assert.assertEquals(4, map.getIdentityToOrdinal(6));
        Assert.assertEquals(7, map.getIdentityToOrdinal(7));
        Assert.assertEquals(7, map.getIdentityToOrdinal(8));
        Assert.assertEquals(7, map.getIdentityToOrdinal(9));
        Assert.assertEquals(5025, map.getIdentityToOrdinal(5025));
        Assert.assertEquals(-1, map.getIdentityToOrdinal(200));
    }

    private void assertMatchIterator(MatchIterator iter, int... values) {
        for(int i = 0; i < values.length; i++) {
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(values[i], iter.next());
        }

        Assert.assertFalse(iter.hasNext());
    }


    private IntList list(int... values) {
        IntList list = new IntList(values.length);

        for(int i = 0; i < values.length; i++) {
            list.add(values[i]);
        }

        return list;
    }

}
