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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectAddedBackAfterRemoveTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("Test", 3);

        schema.addField("FieldA", FieldType.INT);
        schema.addField("FieldB", FieldType.LONG);
        schema.addField("FieldC", FieldType.BOOLEAN);
    }

    @Test
    public void runtTest() {
        HollowWriteStateEngine eng = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(eng);
        mapper.add(new TestClass(1, "one"));
        System.out.println( eng.getTypeState("String").getPopulatedBitSet().get(0));

        eng.getTypeState("TestClass").removeOrdinalFromThisCycle(0);
        eng.getTypeState("String").removeOrdinalFromThisCycle(0);
        System.out.println(" removed :" + eng.getTypeState("TestClass").getPopulatedBitSet().get(0));
        System.out.println(" removed :" + eng.getTypeState("String").getPopulatedBitSet().get(0));

        mapper.add(new TestClass(1, "one"));
        System.out.println(" added back :" + eng.getTypeState("TestClass").getPopulatedBitSet().get(0));
        System.out.println(" added back :" + eng.getTypeState("String").getPopulatedBitSet().get(0));
    }

    static class TestClass {
        int id;
        String value;

        public TestClass(int id, String value) { this.id = id; this.value = value;}
    }

}
