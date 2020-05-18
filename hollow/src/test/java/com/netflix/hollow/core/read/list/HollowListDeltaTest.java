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
package com.netflix.hollow.core.read.list;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowListDeltaTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(10, 20, 30);
        addRecord(40, 50, 60);
        addRecord(70, 80, 90);

        roundTripSnapshot();

        addRecord(10, 20, 30);
        addRecord(70, 80, 90);
        addRecord(100, 200, 300, 400, 500, 600, 700);
        addRecord(1, 2, 3);

        roundTripDelta();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");

        assertList(typeState, 0, 10, 20, 30);
        assertList(typeState, 1, 40, 50, 60);  /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertList(typeState, 2, 70, 80, 90);
        assertList(typeState, 3, 100, 200, 300, 400, 500, 600, 700);
        assertList(typeState, 4, 1, 2, 3);

        Assert.assertEquals(4, typeState.maxOrdinal());

        roundTripDelta();

        assertList(typeState, 0, 10, 20, 30);  /// all lists were "removed", but again hang around until the following cycle.
        assertList(typeState, 1); /// this list should now be disappeared.
        assertList(typeState, 2, 70, 80, 90);  /// "ghost"
        assertList(typeState, 3, 100, 200, 300, 400, 500, 600, 700); /// "ghost"
        assertList(typeState, 4, 1, 2, 3); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(634, 54732);
        addRecord(1, 2, 3);

        roundTripDelta();

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertList(typeState, 0, 634, 54732); /// now, since all lists were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertList(typeState, 1, 1, 2, 3);  /// even though 1, 2, 3 had an equivalent list in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".

    }

    @Test
    public void testSingleEmptyList() throws IOException {
        addRecord();

        roundTripSnapshot();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");

        assertList(typeState, 0);
    }

    @Test
    public void testSingleListWith0Ordinal() throws IOException {
        addRecord(0);

        roundTripSnapshot();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");

        assertList(typeState, 0, 0);
    }

    @Test
    public void testStaleReferenceException() throws IOException {
        addRecord(0);

        roundTripSnapshot();

        readStateEngine.invalidate();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");

        try {
            assertList(typeState, 0, 0);
            Assert.fail("Should have thrown Exception");
        } catch(NullPointerException expected) { }
    }

    private void addRecord(int... ordinals) {
        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestList", rec);
    }

    private void assertList(HollowListTypeReadState readState, int ordinal, int... elements) {
        HollowOrdinalIterator iter = readState.ordinalIterator(ordinal);

        for(int i=0;i<elements.length;i++) {
            Assert.assertEquals(elements[i], iter.next());
        }

        Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
    }

    @Override
    protected void initializeTypeStates() {
        HollowListTypeWriteState writeState = new HollowListTypeWriteState(new HollowListSchema("TestList", "TestObject"));
        writeStateEngine.addTypeState(writeState);
    }

}
