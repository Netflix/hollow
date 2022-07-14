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
package com.netflix.hollow.core.read.map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowMap;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapCollectionTest {

    @Test
    public void testEntryIterator() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new Top(1, 2, 3));

        HollowReadStateEngine rse = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, rse);

        GenericHollowMap m = new GenericHollowMap(rse, "MapOfIntegerToInteger", 0);

        List<Integer> keys = m.entrySet().stream().map(Map.Entry::getKey)
                .map(r -> (GenericHollowObject) r)
                .map(o -> o.getInt("value"))
                .sorted()
                .collect(toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3), keys);

        Iterator<Map.Entry<HollowRecord, HollowRecord>> iterator = m.entrySet().iterator();
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

        GenericHollowMap m1 = new GenericHollowMap(rse, "MapOfStringToString", 0);
        GenericHollowMap m2 = new GenericHollowMap(rse, "MapOfStringToString", 0);
        GenericHollowMap m3 = new GenericHollowMap(rse, "MapOfStringToString", 1);
        GenericHollowMap m4 = new GenericHollowMap(rse, "MapOfStringToString", 2);
        GenericHollowMap m5 = new GenericHollowMap(rse, "MapOfIntegerToInteger", 0);

        assertMapEquals(m1, m1, true);
        assertMapEquals(m1, m2, true);
        assertMapEquals(m1, new HashMap<>(m1), true);

        assertMapEquals(m1, m3, false);
        assertMapEquals(m1, m4, false);
        assertMapEquals(m1, m5, false);

        Assert.assertNotEquals(m1, new ArrayList<>(m1.keySet()));
    }

    static void assertMapEquals(Map<?, ?> a, Map<?, ?> b, boolean equal) {
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

    static class Top {
        Map<Integer, Integer> ints;
        Map<String, String> strings;

        Top(int... vs) {
            this.ints = IntStream.of(vs).boxed().collect(toMap(e -> e, e -> e));
            this.strings = IntStream.of(vs).mapToObj(Integer::toString).collect(toMap(e -> e, e -> e));
        }
    }

    static boolean equalsUsingContains(Map<?, ?> a, Map<?, ?> b) {
        if(a.size() != b.size()) {
            return false;
        }
        return a.entrySet().containsAll(b.entrySet());
    }
}
