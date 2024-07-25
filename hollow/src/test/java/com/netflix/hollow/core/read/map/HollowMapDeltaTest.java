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

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapDeltaTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(10, 20, 30, 40);
        addRecord(40, 50, 60, 70);
        addRecord(70, 80, 90, 100);

        roundTripSnapshot();

        addRecord(10, 20, 30, 40);
        addRecord(70, 80, 90, 100);
        addRecord(100, 200, 300, 400, 500, 600, 700, 800);
        addRecord(1, 2, 3, 4);

        roundTripDelta();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        assertMap(typeState, 0, 10, 20, 30, 40);
        assertMap(typeState, 1, 40, 50, 60, 70);  /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertMap(typeState, 2, 70, 80, 90, 100);
        assertMap(typeState, 3, 100, 200, 300, 400, 500, 600, 700, 800);
        assertMap(typeState, 4, 1, 2, 3, 4);

        assertEquals(4, typeState.maxOrdinal());

        roundTripDelta();

        assertMap(typeState, 0, 10, 20, 30, 40);  /// all maps were "removed", but again hang around until the following cycle.
        assertMap(typeState, 1); /// this map should now be disappeared.
        assertMap(typeState, 2, 70, 80, 90, 100);  /// "ghost"
        assertMap(typeState, 3, 100, 200, 300, 400, 500, 600, 700, 800); /// "ghost"
        assertMap(typeState, 4, 1, 2, 3, 4); /// "ghost"

        assertEquals(4, typeState.maxOrdinal());

        addRecord(634, 54732);
        addRecord(1, 2, 3, 4);

        roundTripDelta();

        assertEquals(1, typeState.maxOrdinal());
        assertMap(typeState, 0, 634, 54732); /// now, since all maps were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertMap(typeState, 1, 1, 2, 3, 4);  /// even though 1, 2, 3 had an equivalent map in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".
    }

    @Test
    public void testShrinkingMapEntrySize() throws IOException {
        addRecord(1, 1000, 2, 2000);

        roundTripSnapshot();

        addRecord(1, 1000, 2, 200);

        roundTripDelta();

        addRecord(1, 1000, 2, 200);
        addRecord(1, 1001, 2, 201);

        roundTripDelta();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        assertMap(typeState, 1, 1, 1000, 2, 200);
    }

    @Test
    public void testSingleEmptyMap() throws IOException {
        addRecord();

        roundTripSnapshot();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        assertEquals(0, typeState.maxOrdinal());
        assertEquals(0, typeState.size(0));
    }

    @Test
    public void testSingleMapWith0KeyValueOrdinals() throws IOException {
        addRecord(0,0);

        roundTripSnapshot();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        assertEquals(0, typeState.maxOrdinal());
        assertEquals(1, typeState.size(0));

        HollowMapEntryOrdinalIterator ordinalIterator = typeState.ordinalIterator(0);

        Assert.assertTrue(ordinalIterator.next());
        assertEquals(0, ordinalIterator.getKey());
        assertEquals(0, ordinalIterator.getValue());
        Assert.assertFalse(ordinalIterator.next());
    }

    @Test
    public void testStaleReferenceException() throws IOException {
        addRecord(0, 0);

        roundTripSnapshot();

        readStateEngine.invalidate();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        try {
            assertMap(typeState, 0, 0, 0);
            Assert.fail("Should have thrown Exception");
        } catch(NullPointerException expected) { }
    }

    @Test
    public void testMapSerializationSortOnKeyAndValueOrdinals() {
        // for cases where duplicate keys exist in the map (for e.g. equals/hashcode not overridden in data model)
        // sorting on both key and value ordinals ensures consistent serialization
        HollowMapWriteRecord mapWriteRecord1 = new HollowMapWriteRecord();
        mapWriteRecord1.addEntry(0, 0);
        mapWriteRecord1.addEntry(0, 1);

        ByteDataArray byteDataArray1 = new ByteDataArray();
        mapWriteRecord1.writeDataTo(byteDataArray1);

        HollowMapWriteRecord mapWriteRecord2 = new HollowMapWriteRecord();
        mapWriteRecord2.addEntry(0, 1);
        mapWriteRecord2.addEntry(0, 0);

        ByteDataArray byteDataArray2 = new ByteDataArray();
        mapWriteRecord2.writeDataTo(byteDataArray2);

        assertEquals(byteDataArray1.length(), byteDataArray2.length());
        for (int i=0; i<byteDataArray1.length(); i++) {
            assertEquals(byteDataArray1.get(i), byteDataArray2.get(i));
        }
    }

    @Test
    public void testMapChecksumSortOnKeyAndValueOrdinals() throws Exception {
        // for cases where duplicate keys exist in the map (for e.g. equals/hashcode not overridden in data model)
        // sorting on both key and value ordinals ensures consistent checksum (and avoids integrity check failures)
        final int KEY = 1;
        final String VALUE1 = "value1";
        final String VALUE2 = "value2";

        Pojo pojo1 = new Pojo(KEY);
        Pojo pojo2 = new Pojo(KEY); // same data, but no equals or hashCode defined

        Map<Pojo, String> orderedMap1 = new LinkedHashMap<>();
        orderedMap1.put(pojo1, VALUE1);
        orderedMap1.put(pojo2, VALUE2);
        PojoMap pojoMap1 = new PojoMap(orderedMap1); // {1->value1, 1->value2}

        Map<Pojo, String> orderedMap2 = new LinkedHashMap<>();
        orderedMap2.put(pojo2, VALUE2); // flip the insertion order
        orderedMap2.put(pojo1, VALUE1);
        PojoMap pojoMap2 = new PojoMap(orderedMap2); // {1->value2, 1->value1}

        HollowWriteStateEngine writeStateEngine1 = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper1 = new HollowObjectMapper(writeStateEngine1);
        HollowWriteStateEngine writeStateEngine2 = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper2 = new HollowObjectMapper(writeStateEngine2);
        objectMapper1.initializeTypeState(PojoMap.class);
        objectMapper2.initializeTypeState(PojoMap.class);

        objectMapper1.add(VALUE1); // pin VALUE1 and VALUE2 to ordinals 0 and 1 consistently across both states
        objectMapper1.add(VALUE2); // so that we can test for ordering in map type
        objectMapper2.add(VALUE1);
        objectMapper2.add(VALUE2);

        // HollowMapTypeMapper iterates on pojo map entries to assign ordinals and add HollowMapWriteRecord entries
        objectMapper1.add(pojoMap1); // ordinals {0,0}, {0,1}
        objectMapper2.add(pojoMap2); // ordinals {0,1}, {0,0}

        HollowReadStateEngine readStateEngine1 = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine1, readStateEngine1);

        HollowReadStateEngine readStateEngine2 = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine2, readStateEngine2);

        // serialization should sort those identically, i.e. on both key and value ordinal values
        HollowTypeReadState mapTypeState1 = readStateEngine1.getTypeState("MapOfPojoToString");
        HollowTypeReadState mapTypeState2 = readStateEngine2.getTypeState("MapOfPojoToString");
        assertEquals(mapTypeState1.getChecksum(mapTypeState1.getSchema()), mapTypeState2.getChecksum(mapTypeState2.getSchema()));
    }

    private void addRecord(int... ordinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i=0;i<ordinals.length;i+=2) {
            rec.addEntry(ordinals[i], ordinals[i+1]);
        }

        writeStateEngine.add("TestMap", rec);
    }

    private void assertMap(HollowMapTypeReadState readState, int ordinal, int... elements) {
        assertEquals(elements.length / 2, readState.size(ordinal));

        for(int i=0;i<elements.length;i+=2) {
            assertEquals(elements[i+1], readState.get(ordinal, elements[i]));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }

}

class Pojo {
    public int val;

    public Pojo(int val) {
        this.val = val;
    }

    // no hashCode or equals defined
}

class PojoMap {
    public Map<Pojo, String> map;

    public PojoMap(Map<Pojo, String> map) {
        this.map = map;
    }
}