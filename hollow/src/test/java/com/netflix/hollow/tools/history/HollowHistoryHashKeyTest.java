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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class HollowHistoryHashKeyTest {

    @Test
    public void testSetHashKeys() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();

        mapper.add(new TestTopLevelObject(1, new Obj(1, "US", 100), new Obj(2, "CA", 200), new Obj(3, "IT", 300), new Obj(4, "GB", 400), new Obj(5, "IT", 500)));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        HollowHistoricalStateDataAccess history0 = new HollowHistoricalStateCreator().createBasedOnNewDelta(0, readEngine);

        mapper.add(new TestTopLevelObject(1, new Obj(1, "US", 101), new Obj(2, "CA", 200), new Obj(3, "IT", 300), new Obj(4, "GB", 401), new Obj(5, "IT", 500)));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);
        HollowHistoricalStateDataAccess history1 = new HollowHistoricalStateCreator().createBasedOnNewDelta(1, readEngine);
        history0.setNextState(history1);

        mapper.add(new TestTopLevelObject(1, new Obj(1, "US", 101), new Obj(2, "CA", 200), new Obj(3, "IT", 302), new Obj(4, "GB", 401), new Obj(5, "IT", 500)));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);
        HollowHistoricalStateDataAccess history2 = new HollowHistoricalStateCreator().createBasedOnNewDelta(2, readEngine);
        history1.setNextState(history2);

        history2.setNextState(readEngine);


        GenericHollowObject obj = new GenericHollowObject(history0, "TestTopLevelObject", 0);

        GenericHollowObject element = (GenericHollowObject) obj.getSet("setById").findElement(1);
        Assert.assertEquals("US", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(2);
        Assert.assertEquals("CA", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(3);
        Assert.assertEquals("IT", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(4);
        Assert.assertEquals("GB", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(5);
        Assert.assertEquals("IT", element.getObject("country").getString("value"));

        element = (GenericHollowObject) obj.getSet("setByIdCountry").findElement(1, "US");
        Assert.assertEquals(1, element.getInt("id"));
        element = (GenericHollowObject) obj.getSet("setByIdCountry").findElement(2, "CA");
        Assert.assertEquals(2, element.getInt("id"));
        element = (GenericHollowObject) obj.getSet("setByIdCountry").findElement(3, "IT");
        Assert.assertEquals(3, element.getInt("id"));
        element = (GenericHollowObject) obj.getSet("setByIdCountry").findElement(4, "GB");
        Assert.assertEquals(4, element.getInt("id"));
        element = (GenericHollowObject) obj.getSet("setByIdCountry").findElement(5, "IT");
        Assert.assertEquals(5, element.getInt("id"));

        element = (GenericHollowObject) obj.getSet("intSet").findElement(100);
        Assert.assertEquals(100, element.getInt("value"));
        element = (GenericHollowObject) obj.getSet("intSet").findElement(200);
        Assert.assertEquals(200, element.getInt("value"));
        element = (GenericHollowObject) obj.getSet("intSet").findElement(300);
        Assert.assertEquals(300, element.getInt("value"));
        element = (GenericHollowObject) obj.getSet("intSet").findElement(400);
        Assert.assertEquals(400, element.getInt("value"));
        element = (GenericHollowObject) obj.getSet("intSet").findElement(500);
        Assert.assertEquals(500, element.getInt("value"));


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

        @HollowTypeName(name = "SetById")
        @HollowHashKey(fields = "id")
        Set<Obj> setById;

        @HollowTypeName(name = "SetByIdCountry")
        @HollowHashKey(fields = {"id", "country.value"})
        Set<Obj> setByIdCountry;

        Set<Integer> intSet;

        @HollowTypeName(name = "MapById")
        @HollowHashKey(fields = "id")
        Map<Obj, Integer> mapById;

        @HollowTypeName(name = "MapByIdCountry")
        @HollowHashKey(fields = {"id", "country.value"})
        Map<Obj, Integer> mapByIdCountry;

        Map<Integer, Integer> intMap;


        public TestTopLevelObject(int id, Obj... elements) {
            this.id = id;
            this.setById = new HashSet<Obj>();
            this.setByIdCountry = new HashSet<Obj>();
            this.intSet = new HashSet<Integer>();
            this.mapById = new HashMap<Obj, Integer>();
            this.mapByIdCountry = new HashMap<Obj, Integer>();
            this.intMap = new HashMap<Integer, Integer>();

            for(int i = 0; i < elements.length; i++) {
                setById.add(elements[i]);
                setByIdCountry.add(elements[i]);
                intSet.add((int) elements[i].extraValue);
                mapById.put(elements[i], (int) elements[i].extraValue);
                mapByIdCountry.put(elements[i], (int) elements[i].extraValue);
                intMap.put(elements[i].id, (int) elements[i].extraValue);
            }
        }
    }

    @SuppressWarnings("unused")
    private static class Obj {
        int id;
        String country;
        long extraValue;

        public Obj(int id, String country, long extraValue) {
            this.id = id;
            this.country = country;
            this.extraValue = extraValue;
        }
    }

}
