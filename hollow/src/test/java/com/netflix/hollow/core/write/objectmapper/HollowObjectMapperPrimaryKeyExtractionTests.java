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
package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectMapperPrimaryKeyExtractionTests {

    @Test
    public void extractsPrimaryKey() {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        Object[] key1 = mapper.extractPrimaryKey(new TypeA(1, "one")).getKey();
        Object[] key2 = mapper.extractPrimaryKey(new TypeA(2, "two")).getKey();
        Object[] key3 = mapper.extractPrimaryKey(new TypeA(3, "three")).getKey();

        Assert.assertArrayEquals(new Object[]{1, "one"}, key1);
        Assert.assertArrayEquals(new Object[]{2, "two"}, key2);
        Assert.assertArrayEquals(new Object[]{3, "three"}, key3);
    }

    @Test
    public void willReturnNullFields() {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        Object[] key1 = mapper.extractPrimaryKey(new TypeA(1)).getKey();
        Object[] key2 = mapper.extractPrimaryKey(new TypeA(null)).getKey();
        Object[] key3 = mapper.extractPrimaryKey(new TypeA(2, null)).getKey();
        Object[] key4 = mapper.extractPrimaryKey(new TypeA(null, null)).getKey();

        Assert.assertArrayEquals(new Object[]{1, null}, key1);
        Assert.assertArrayEquals(new Object[]{null, null}, key2);
        Assert.assertArrayEquals(new Object[]{2, null}, key3);
        Assert.assertArrayEquals(new Object[]{null, null}, key4);
    }

    @Test
    public void failsIfPrimaryKeyIncludesReferenceField() {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        try {
            mapper.extractPrimaryKey(new TypeAWithReferenceTypeForPrimaryKeyField(1, "asdf"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Cannot extract POJO primary key from a REFERENCE mapped field type", expected.getMessage());
        }
    }


    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = {"id", "name"})
    private static class TypeA {
        Str name;
        @HollowInline
        Integer id;

        public TypeA(Integer id) {
            this.id = id;
            this.name = null;
        }

        public TypeA(Integer id, String name) {
            this.id = id;
            this.name = new Str(name);
        }
    }

    @SuppressWarnings("unused")
    private static class Str {
        String value;

        public Str(String value) {
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = {"id", "name!"})
    private static class TypeAWithReferenceTypeForPrimaryKeyField {
        int id;
        String name;

        public TypeAWithReferenceTypeForPrimaryKeyField(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


}
