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
package com.netflix.hollow.tools.query;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowFieldMatchQueryTest {

    private HollowReadStateEngine stateEngine;

    @Before
    public void setUp() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA(1, 100));
        mapper.add(new TypeA(2, 200));
        mapper.add(new TypeA(3, 100));
        mapper.add(new TypeA(4, 200));

        mapper.add(new TypeB("1", 1.1f));
        mapper.add(new TypeB("2", 2.2f));
        mapper.add(new TypeB("3", 3.3f));
        mapper.add(new TypeB("4", 4.4f));

        stateEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    @Test
    public void matchesRecordsOfAnyType() {
        HollowFieldMatchQuery query = new HollowFieldMatchQuery(stateEngine);

        Map<String, BitSet> matches = query.findMatchingRecords("id", "2");

        Assert.assertEquals(2, matches.size());

        Assert.assertEquals(1, matches.get("TypeA").cardinality());
        Assert.assertTrue(matches.get("TypeA").get(1));

        Assert.assertEquals(1, matches.get("TypeB").cardinality());
        Assert.assertTrue(matches.get("TypeB").get(1));
    }

    @Test
    public void matchesOnlyRecordsOfSpecifiedType() {
        HollowFieldMatchQuery query = new HollowFieldMatchQuery(stateEngine);

        Map<String, BitSet> matches = query.findMatchingRecords("TypeA", "id", "2");

        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(1, matches.get("TypeA").cardinality());
        Assert.assertTrue(matches.get("TypeA").get(1));
    }

    @Test
    public void matchesOnlyRecordsWithSpecifiedField() {
        HollowFieldMatchQuery query = new HollowFieldMatchQuery(stateEngine);

        Map<String, BitSet> matches = query.findMatchingRecords("bValue", "4.4");

        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(1, matches.get("TypeB").cardinality());
        Assert.assertTrue(matches.get("TypeB").get(3));
    }

    @SuppressWarnings("unused")
    private static class TypeA {
        int id;
        int aValue;

        public TypeA(int id, int aValue) {
            this.id = id;
            this.aValue = aValue;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeB {
        String id;
        float bValue;

        public TypeB(String id, float bValue) {
            this.id = id;
            this.bValue = bValue;
        }
    }
}
