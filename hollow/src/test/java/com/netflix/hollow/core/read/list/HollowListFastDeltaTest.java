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

public class HollowListFastDeltaTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(10,  20,  30);
        addRecord(20,  30,  40);
        addRecord();
        addRecord(30,  40,  50);
        addRecord(40,  50,  60);
        addRecord(50,  60,  70);
        addRecord(60,  70,  80, 90, 100, 110, 120);
        addRecord(70,  80,  90, 100);
        addRecord(80,  90,  100);
        addRecord(90,  100, 110);
        addRecord(100, 110, 120);
        addRecord(110, 120, 130);
        addRecord(120, 130, 140);

        roundTripSnapshot();

        addRecord(10,  20,  30);
        addRecord(20,  30,  40);
        addRecord();
        addRecord(30,  40,  50);
        addRecord(40,  50,  60);
        addRecord(50,  60,  70);
        addRecord(60,  70,  80, 90, 100, 110, 120);
        addRecord(71,  81,  91, 101);
        addRecord(80,  90,  100);
        addRecord(90,  100, 110);
        addRecord(100, 110, 120);
        addRecord(110, 120, 130);
        addRecord(120, 130, 140);

        roundTripDelta();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");

        assertList(typeState, 0, 10,  20,  30);
        assertList(typeState, 1, 20,  30,  40);
        assertList(typeState, 2);
        assertList(typeState, 3, 30,  40,  50);
        assertList(typeState, 4, 40,  50,  60);
        assertList(typeState, 5, 50,  60,  70);
        assertList(typeState, 6, 60,  70,  80, 90, 100, 110, 120);
        assertList(typeState, 7, 70,  80,  90, 100); // ghost
        assertList(typeState, 8, 80,  90,  100);
        assertList(typeState, 9, 90,  100, 110);
        assertList(typeState, 10, 100, 110, 120);
        assertList(typeState, 11, 110, 120, 130);
        assertList(typeState, 12, 120, 130, 140);
        assertList(typeState, 13, 71, 81, 91, 101);

        Assert.assertEquals(13, typeState.maxOrdinal());


        addRecord(10,  20,  30);
        addRecord(20,  30,  40);
        addRecord();
        addRecord(40,  50,  60);
        addRecord(50,  60,  70);
        addRecord(61,  71,  81, 91, 101, 111, 121);
        addRecord(71,  81,  91, 101);
        addRecord(80,  90,  100);
        addRecord(90,  100, 110);
        addRecord(100, 110, 120);
        addRecord(110, 120, 130);
        addRecord(120, 130, 140);


        roundTripDelta();


        assertList(typeState, 0, 10,  20,  30);
        assertList(typeState, 1, 20,  30,  40);
        assertList(typeState, 2);
        assertList(typeState, 3, 30,  40,  50); // ghost
        assertList(typeState, 4, 40,  50,  60);
        assertList(typeState, 5, 50,  60,  70);
        assertList(typeState, 6, 60,  70,  80, 90, 100, 110, 120);
        assertList(typeState, 7, 61,  71,  81, 91, 101, 111, 121);
        assertList(typeState, 8, 80,  90,  100);
        assertList(typeState, 9, 90,  100, 110);
        assertList(typeState, 10, 100, 110, 120);
        assertList(typeState, 11, 110, 120, 130);
        assertList(typeState, 12, 120, 130, 140);
        assertList(typeState, 13, 71, 81, 91, 101);

        Assert.assertEquals(13, typeState.maxOrdinal());
    }

    private void addRecord(int... ordinals) {
        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int i = 0; i < ordinals.length; i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestList", rec);
    }

    private void assertList(HollowListTypeReadState readState, int ordinal, int... elements) {
        HollowOrdinalIterator iter = readState.ordinalIterator(ordinal);

        for(int i = 0; i < elements.length; i++) {
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
