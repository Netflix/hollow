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
package com.netflix.hollow.core.read.set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowSet;
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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetCollectionTest {

    static class Top {
        Set<Integer> ints;
        Set<String> strings;

        Top(int... vs) {
            this.ints = IntStream.of(vs).boxed().collect(toSet());
            this.strings = IntStream.of(vs).mapToObj(Integer::toString).collect(toSet());
        }
    }

    @Test
    public void testIterator() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new Top(1, 2, 3));

        HollowReadStateEngine rse = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, rse);

        GenericHollowSet s = new GenericHollowSet(rse, "SetOfInteger", 0);

        List<Integer> keys = s.stream()
                .map(r -> (GenericHollowObject) r)
                .map(o -> o.getInt("value"))
                .sorted()
                .collect(toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3), keys);

        Iterator<HollowRecord> iterator = s.iterator();
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

        GenericHollowSet s1 = new GenericHollowSet(rse, "SetOfString", 0);
        GenericHollowSet s2 = new GenericHollowSet(rse, "SetOfString", 0);
        GenericHollowSet s3 = new GenericHollowSet(rse, "SetOfString", 1);
        GenericHollowSet s4 = new GenericHollowSet(rse, "SetOfString", 2);
        GenericHollowSet s5 = new GenericHollowSet(rse, "SetOfInteger", 0);

        assertSetEquals(s1, s1, true);
        assertSetEquals(s1, s2, true);
        assertSetEquals(s1, new HashSet<>(s1), true);

        assertSetEquals(s1, s3, false);
        assertSetEquals(s1, s4, false);
        assertSetEquals(s1, s5, false);

        Assert.assertNotEquals(s1, new ArrayList<>(s1));
    }

    static void assertSetEquals(Set<?> a, Set<?> b, boolean equal) {
        if(equal) {
            Assert.assertEquals(a.hashCode(), b.hashCode());
            Assert.assertEquals(a, b);
            Assert.assertTrue(equalsUsingContains(a, b));

            Assert.assertEquals(b, a);
            Assert.assertTrue(equalsUsingContains(b, a));
        } else {
            Assert.assertNotEquals(a, b);
            Assert.assertFalse(equalsUsingContains(a, b));

            Assert.assertNotEquals(b, a);
            Assert.assertFalse(equalsUsingContains(b, a));
        }
    }

    static boolean equalsUsingContains(Set<?> a, Set<?> b) {
        if(a.size() != b.size()) {
            return false;
        }

        try {
            return a.containsAll(b);
        } catch (Exception e) {
            return false;
        }
    }
}
