/*
 *
 *  Copyright 2019 Netflix, Inc.
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
package com.netflix.hollow.core.read.list;

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowList;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class HollowListCollectionTest {

    static class Top {
        List<Integer> ints;
        List<String> strings;

        Top(int... vs) {
            this.ints = IntStream.of(vs).boxed().collect(toList());
            this.strings = IntStream.of(vs).mapToObj(Integer::toString).collect(toList());
        }
    }

    @Test
    public void testIterator() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new Top(1, 2, 3));

        HollowReadStateEngine rse = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, rse);

        GenericHollowList l = new GenericHollowList(rse, "ListOfInteger", 0);

        List<Integer> keys = l.stream()
                .map(r -> (GenericHollowObject) r)
                .map(o -> o.getInt("value"))
                .collect(toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3), keys);

        Iterator<HollowRecord> iterator = l.iterator();
        iterator.forEachRemaining(e -> {
        });
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testEquals() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new Top(1, 2, 3));
        mapper.add(new Top(1, 2, 4));
        mapper.add(new Top(1, 2, 3, 4));

        HollowReadStateEngine rse = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, rse);

        GenericHollowList l1 = new GenericHollowList(rse, "ListOfString", 0);
        GenericHollowList l2 = new GenericHollowList(rse, "ListOfString", 0);
        GenericHollowList l3 = new GenericHollowList(rse, "ListOfString", 1);
        GenericHollowList l4 = new GenericHollowList(rse, "ListOfString", 2);
        GenericHollowList l5 = new GenericHollowList(rse, "ListOfInteger", 0);

        assertListEquals(l1, l1, true);
        assertListEquals(l1, l2, true);
        assertListEquals(l1, new ArrayList<>(l1), true);

        assertListEquals(l1, l3, false);
        assertListEquals(l1, l4, false);
        assertListEquals(l1, l5, false);

        Assert.assertNotEquals(l1, new HashSet<>(l1));
    }

    static void assertListEquals(List<?> a, List<?> b, boolean equal) {
        if(equal) {
            Assert.assertEquals(a.hashCode(), b.hashCode());
            Assert.assertEquals(a, b);
            Assert.assertTrue(equalsUsingIterator(a, b));

            Assert.assertEquals(b, a);
            Assert.assertTrue(equalsUsingIterator(b, a));
        } else {
            Assert.assertNotEquals(a, b);
            Assert.assertFalse(equalsUsingIterator(a, b));

            Assert.assertNotEquals(b, a);
            Assert.assertFalse(equalsUsingIterator(b, a));
        }
    }

    static boolean equalsUsingIterator(List<?> a, List<?> b) {
        ListIterator<?> ia = a.listIterator();
        ListIterator<?> ib = b.listIterator();
        while(ia.hasNext() && ib.hasNext()) {
            if(!Objects.equals(ia.next(), ib.next())) {
                return false;
            }
        }
        return !(ia.hasNext() || ib.hasNext());
    }
}
