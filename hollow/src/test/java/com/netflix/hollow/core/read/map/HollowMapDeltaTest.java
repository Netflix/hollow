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

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
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

        Assert.assertEquals(4, typeState.maxOrdinal());

        roundTripDelta();

        assertMap(typeState, 0, 10, 20, 30, 40);  /// all maps were "removed", but again hang around until the following cycle.
        assertMap(typeState, 1); /// this map should now be disappeared.
        assertMap(typeState, 2, 70, 80, 90, 100);  /// "ghost"
        assertMap(typeState, 3, 100, 200, 300, 400, 500, 600, 700, 800); /// "ghost"
        assertMap(typeState, 4, 1, 2, 3, 4); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(634, 54732);
        addRecord(1, 2, 3, 4);

        roundTripDelta();

        Assert.assertEquals(1, typeState.maxOrdinal());
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

        Assert.assertEquals(0, typeState.maxOrdinal());
        Assert.assertEquals(0, typeState.size(0));
    }

    @Test
    public void testSingleMapWith0KeyValueOrdinals() throws IOException {
        addRecord(0,0);

        roundTripSnapshot();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        Assert.assertEquals(0, typeState.maxOrdinal());
        Assert.assertEquals(1, typeState.size(0));

        HollowMapEntryOrdinalIterator ordinalIterator = typeState.ordinalIterator(0);

        Assert.assertTrue(ordinalIterator.next());
        Assert.assertEquals(0, ordinalIterator.getKey());
        Assert.assertEquals(0, ordinalIterator.getValue());
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

    private void addRecord(int... ordinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i=0;i<ordinals.length;i+=2) {
            rec.addEntry(ordinals[i], ordinals[i+1]);
        }

        writeStateEngine.add("TestMap", rec);
    }

    private void assertMap(HollowMapTypeReadState readState, int ordinal, int... elements) {
        Assert.assertEquals(elements.length / 2, readState.size(ordinal));

        for(int i=0;i<elements.length;i+=2) {
            Assert.assertEquals(elements[i+1], readState.get(ordinal, elements[i]));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }

}
