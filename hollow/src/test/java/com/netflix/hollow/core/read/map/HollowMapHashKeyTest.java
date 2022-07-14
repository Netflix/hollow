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
package com.netflix.hollow.core.read.map;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowMap;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapHashKeyTest {

    @Test
    public void testMapHashKeys() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();

        mapper.add(new TestTopLevelObject(1, new Obj(1, "New York", "US", 100), new Obj(2, "Ottawa", "CA", 200),
                new Obj(3, "Rome", "IT", 300), new Obj(4, "London", "GB", 400), new Obj(5, "Turin", "IT", 500)));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        GenericHollowObject obj = new GenericHollowObject(readEngine, "TestTopLevelObject", 0);

        GenericHollowObject key = (GenericHollowObject) obj.getMap("mapById").findKey(1);
        Assert.assertEquals("US", key.getObject("country").getString("value"));
        key = (GenericHollowObject) obj.getMap("mapById").findKey(2);
        Assert.assertEquals("CA", key.getObject("country").getString("value"));
        key = (GenericHollowObject) obj.getMap("mapById").findKey(3);
        Assert.assertEquals("IT", key.getObject("country").getString("value"));
        key = (GenericHollowObject) obj.getMap("mapById").findKey(4);
        Assert.assertEquals("GB", key.getObject("country").getString("value"));
        key = (GenericHollowObject) obj.getMap("mapById").findKey(5);
        Assert.assertEquals("IT", key.getObject("country").getString("value"));

        key = (GenericHollowObject) obj.getMap("mapByIdCountry").findKey(1, "US");
        Assert.assertEquals(1, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCountry").findKey(2, "CA");
        Assert.assertEquals(2, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCountry").findKey(3, "IT");
        Assert.assertEquals(3, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCountry").findKey(4, "GB");
        Assert.assertEquals(4, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCountry").findKey(5, "IT");
        Assert.assertEquals(5, key.getInt("id"));

        key = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findKey(1, "New York", "US");
        Assert.assertEquals(1, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findKey(2, "Ottawa", "CA");
        Assert.assertEquals(2, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findKey(3, "Rome", "IT");
        Assert.assertEquals(3, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findKey(4, "London", "GB");
        Assert.assertEquals(4, key.getInt("id"));
        key = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findKey(5, "Turin", "IT");
        Assert.assertEquals(5, key.getInt("id"));

        GenericHollowObject value = (GenericHollowObject) obj.getMap("mapById").findValue(1);
        Assert.assertEquals(100, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapById").findValue(2);
        Assert.assertEquals(200, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapById").findValue(3);
        Assert.assertEquals(300, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapById").findValue(4);
        Assert.assertEquals(400, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapById").findValue(5);
        Assert.assertEquals(500, value.getInt("value"));

        value = (GenericHollowObject) obj.getMap("mapByIdCountry").findValue(1, "US");
        Assert.assertEquals(100, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCountry").findValue(2, "CA");
        Assert.assertEquals(200, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCountry").findValue(3, "IT");
        Assert.assertEquals(300, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCountry").findValue(4, "GB");
        Assert.assertEquals(400, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCountry").findValue(5, "IT");
        Assert.assertEquals(500, value.getInt("value"));

        value = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findValue(1, "New York", "US");
        Assert.assertEquals(100, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findValue(2, "Ottawa", "CA");
        Assert.assertEquals(200, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findValue(3, "Rome", "IT");
        Assert.assertEquals(300, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findValue(4, "London", "GB");
        Assert.assertEquals(400, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("mapByIdCityCountry").findValue(5, "Turin", "IT");
        Assert.assertEquals(500, value.getInt("value"));

        Map.Entry<HollowRecord, HollowRecord> entry = obj.getMap("mapById").findEntry(1);
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(1, key.getInt("id"));
        Assert.assertEquals(100, value.getInt("value"));
        entry = obj.getMap("mapById").findEntry(2);
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(2, key.getInt("id"));
        Assert.assertEquals(200, value.getInt("value"));
        entry = obj.getMap("mapById").findEntry(3);
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(3, key.getInt("id"));
        Assert.assertEquals(300, value.getInt("value"));
        entry = obj.getMap("mapById").findEntry(4);
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(4, key.getInt("id"));
        Assert.assertEquals(400, value.getInt("value"));
        entry = obj.getMap("mapById").findEntry(5);
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(5, key.getInt("id"));
        Assert.assertEquals(500, value.getInt("value"));

        entry = obj.getMap("mapByIdCountry").findEntry(1, "US");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(1, key.getInt("id"));
        Assert.assertEquals(100, value.getInt("value"));
        entry = obj.getMap("mapByIdCountry").findEntry(2, "CA");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(2, key.getInt("id"));
        Assert.assertEquals(200, value.getInt("value"));
        entry = obj.getMap("mapByIdCountry").findEntry(3, "IT");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(3, key.getInt("id"));
        Assert.assertEquals(300, value.getInt("value"));
        entry = obj.getMap("mapByIdCountry").findEntry(4, "GB");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(4, key.getInt("id"));
        Assert.assertEquals(400, value.getInt("value"));
        entry = obj.getMap("mapByIdCountry").findEntry(5, "IT");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(5, key.getInt("id"));
        Assert.assertEquals(500, value.getInt("value"));

        entry = obj.getMap("mapByIdCityCountry").findEntry(1, "New York", "US");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(1, key.getInt("id"));
        Assert.assertEquals(100, value.getInt("value"));
        entry = obj.getMap("mapByIdCityCountry").findEntry(2, "Ottawa", "CA");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(2, key.getInt("id"));
        Assert.assertEquals(200, value.getInt("value"));
        entry = obj.getMap("mapByIdCityCountry").findEntry(3, "Rome", "IT");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(3, key.getInt("id"));
        Assert.assertEquals(300, value.getInt("value"));
        entry = obj.getMap("mapByIdCityCountry").findEntry(4, "London", "GB");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(4, key.getInt("id"));
        Assert.assertEquals(400, value.getInt("value"));
        entry = obj.getMap("mapByIdCityCountry").findEntry(5, "Turin", "IT");
        key = (GenericHollowObject) entry.getKey();
        value = (GenericHollowObject) entry.getValue();
        Assert.assertEquals(5, key.getInt("id"));
        Assert.assertEquals(500, value.getInt("value"));

        value = (GenericHollowObject) obj.getMap("intMap").findValue(1);
        Assert.assertEquals(100, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("intMap").findValue(2);
        Assert.assertEquals(200, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("intMap").findValue(3);
        Assert.assertEquals(300, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("intMap").findValue(4);
        Assert.assertEquals(400, value.getInt("value"));
        value = (GenericHollowObject) obj.getMap("intMap").findValue(5);
        Assert.assertEquals(500, value.getInt("value"));
    }

    @SuppressWarnings("unused")
    private static class TestTopLevelObject {
        int id;

        @HollowTypeName(name = "MapById")
        @HollowHashKey(fields = "id")
        Map<Obj, Integer> mapById;

        @HollowTypeName(name = "MapByIdCountry")
        @HollowHashKey(fields = {"id", "country.value"})
        Map<Obj, Integer> mapByIdCountry;

        @HollowTypeName(name = "MapByIdCityCountry")
        @HollowHashKey(fields = {"id", "city", "country.value"})
        Map<Obj, Integer> mapByIdCityCountry;

        Map<Integer, Integer> intMap;

        public TestTopLevelObject(int id, Obj... elements) {
            this.id = id;
            this.mapById = new HashMap<Obj, Integer>();
            this.mapByIdCountry = new HashMap<Obj, Integer>();
            this.mapByIdCityCountry = new HashMap<>();
            this.intMap = new HashMap<Integer, Integer>();

            for(int i = 0; i < elements.length; i++) {
                mapById.put(elements[i], (int) elements[i].extraValue);
                mapByIdCountry.put(elements[i], (int) elements[i].extraValue);
                mapByIdCityCountry.put(elements[i],  (int) elements[i].extraValue);
                intMap.put(elements[i].id, (int) elements[i].extraValue);
            }
        }
    }

    @SuppressWarnings("unused")
    private static class Obj {
        int id;
        @HollowInline
        String city;
        String country;
        long extraValue;

        public Obj(int id, String city, String country, long extraValue) {
            this.id = id;
            this.city = city;
            this.country = country;
            this.extraValue = extraValue;
        }
    }

    @Test
    public void testLookupOfLongKey() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeWithLongMap.class);

        TypeWithLongMap top = new TypeWithLongMap();
        long longValue = (long) Integer.MAX_VALUE + 1;
        top.longMap.put(longValue, 100L);

        mapper.add(top);

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        GenericHollowMap map = new GenericHollowMap(readEngine, "MapOfLongToLong", 0);

        GenericHollowObject value = new GenericHollowObject(readEngine, "Long", map.findValue(longValue).getOrdinal());

        Assert.assertEquals(100L, value.getLong("value"));
    }

    private static class TypeWithLongMap {
        Map<Long, Long> longMap = new HashMap<>();
    }

}
