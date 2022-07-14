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
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapFastDeltaTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(10, 20, 30, 40);
        addRecord(20, 30, 40, 50);
        addRecord(30, 40, 50, 60);
        addRecord(40, 50, 60, 70);
        addRecord(50, 60, 70, 80);
        addRecord(60, 70, 80, 90);
        addRecord(70, 80, 90, 100);
        addRecord(80, 90, 100, 110);
        addRecord(90, 100, 110, 120);
        addRecord(100, 110, 120, 130, 140, 150, 160, 170);
        addRecord(110, 120, 130, 140, 150, 160);
        addRecord(120, 130, 140, 150);
        addRecord(130, 140, 150, 160);
        addRecord(140, 150, 160, 170);
        addRecord(150, 160, 170, 180);
        addRecord(160, 170, 180, 190);

        roundTripSnapshot();

        addRecord(10, 20, 30, 40);
        addRecord(20, 30, 40, 50);
        addRecord(30, 40, 50, 60);
        addRecord(40, 50, 60, 70);
        addRecord(50, 60, 70, 80);
        addRecord(60, 70, 80, 90);
        addRecord(70, 80, 90, 100);
        addRecord(80, 90, 100, 110);
        addRecord(90, 100, 110, 120);
        addRecord(101, 111, 121, 131, 141, 151, 161, 171);
        addRecord(110, 120, 130, 140, 150, 160);
        addRecord(120, 130, 140, 150);
        addRecord(130, 140, 150, 160);
        addRecord(140, 150, 160, 170);
        addRecord(150, 160, 170, 180);
        addRecord(160, 170, 180, 190);

        roundTripDelta();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");

        assertMap(typeState, 0,  10,  20,  30,  40);
        assertMap(typeState, 1,  20,  30,  40,  50);
        assertMap(typeState, 2,  30,  40,  50,  60);
        assertMap(typeState, 3,  40,  50,  60,  70);
        assertMap(typeState, 4,  50,  60,  70,  80);
        assertMap(typeState, 5,  60,  70,  80,  90);
        assertMap(typeState, 6,  70,  80,  90,  100);
        assertMap(typeState, 7,  80,  90,  100, 110);
        assertMap(typeState, 8,  90,  100, 110, 120);
        assertMap(typeState, 9,  100, 110, 120, 130, 140, 150, 160, 170); // ghost
        assertMap(typeState, 10, 110, 120, 130, 140, 150, 160);
        assertMap(typeState, 11, 120, 130, 140, 150);
        assertMap(typeState, 12, 130, 140, 150, 160);
        assertMap(typeState, 13, 140, 150, 160, 170);
        assertMap(typeState, 14, 150, 160, 170, 180);
        assertMap(typeState, 15, 160, 170, 180, 190);
        assertMap(typeState, 16, 101, 111, 121, 131, 141, 151, 161, 171);


        addRecord(10, 20, 30, 40);
        addRecord(20, 30, 40, 50);
        addRecord(30, 40, 50, 60);
        addRecord(40, 50, 60, 70);
        addRecord(50, 60, 70, 80);
        addRecord(70, 80, 90, 100);
        addRecord(80, 90, 100, 110);
        addRecord(90, 100, 110, 120);
        addRecord(101, 111, 121, 131, 141, 151, 161, 171);
        addRecord(111, 121, 131, 141, 151, 161);
        addRecord(120, 130, 140, 150);
        addRecord(130, 140, 150, 160);
        addRecord(140, 150, 160, 170);
        addRecord(150, 160, 170, 180);
        addRecord(160, 170, 180, 190);

        roundTripDelta();

        assertMap(typeState, 0,  10,  20,  30,  40);
        assertMap(typeState, 1,  20,  30,  40,  50);
        assertMap(typeState, 2,  30,  40,  50,  60);
        assertMap(typeState, 3,  40,  50,  60,  70);
        assertMap(typeState, 4,  50,  60,  70,  80);
        assertMap(typeState, 5,  60,  70,  80,  90); // ghost
        assertMap(typeState, 6,  70,  80,  90,  100);
        assertMap(typeState, 7,  80,  90,  100, 110);
        assertMap(typeState, 8,  90,  100, 110, 120);
        assertMap(typeState, 9,  111, 121, 131, 141, 151, 161);
        assertMap(typeState, 10, 110, 120, 130, 140, 150, 160); // ghost
        assertMap(typeState, 11, 120, 130, 140, 150);
        assertMap(typeState, 12, 130, 140, 150, 160);
        assertMap(typeState, 13, 140, 150, 160, 170);
        assertMap(typeState, 14, 150, 160, 170, 180);
        assertMap(typeState, 15, 160, 170, 180, 190);
        assertMap(typeState, 16, 101, 111, 121, 131, 141, 151, 161, 171);


        addRecord(10, 20, 30, 40);
        addRecord(20, 30, 40, 50);
        addRecord(31, 41, 51, 61);
        addRecord(40, 50, 60, 70);
        addRecord(50, 60, 70, 80);
        addRecord(70, 80, 90, 100);
        addRecord(80, 90, 100, 110);
        addRecord(90, 100, 110, 120);
        addRecord(101, 111, 121, 131, 141, 151, 161, 171);
        addRecord(111, 121, 131, 141, 151, 161);
        addRecord(120, 130, 140, 150);
        addRecord(130, 140, 150, 160);
        addRecord(140, 150, 160, 170);
        addRecord(150, 160, 170, 180);
        addRecord(160, 170, 180, 190);

        roundTripDelta();

        assertMap(typeState, 0,  10,  20,  30,  40);
        assertMap(typeState, 1,  20,  30,  40,  50);
        assertMap(typeState, 2,  30,  40,  50,  60); // ghost
        assertMap(typeState, 3,  40,  50,  60,  70);
        assertMap(typeState, 4,  50,  60,  70,  80);
        assertMap(typeState, 5,  31,  41,  51,  61);
        assertMap(typeState, 6,  70,  80,  90,  100);
        assertMap(typeState, 7,  80,  90,  100, 110);
        assertMap(typeState, 8,  90,  100, 110, 120);
        assertMap(typeState, 9,  111, 121, 131, 141, 151, 161);
        assertMap(typeState, 10);
        assertMap(typeState, 11, 120, 130, 140, 150);
        assertMap(typeState, 12, 130, 140, 150, 160);
        assertMap(typeState, 13, 140, 150, 160, 170);
        assertMap(typeState, 14, 150, 160, 170, 180);
        assertMap(typeState, 15, 160, 170, 180, 190);
        assertMap(typeState, 16, 101, 111, 121, 131, 141, 151, 161, 171);
    }

    private void addRecord(int... ordinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i = 0; i < ordinals.length; i += 2) {
            rec.addEntry(ordinals[i], ordinals[i + 1]);
        }

        writeStateEngine.add("TestMap", rec);
    }

    private void assertMap(HollowMapTypeReadState readState, int ordinal, int... elements) {
        Assert.assertEquals(elements.length / 2, readState.size(ordinal));

        for(int i = 0; i < elements.length; i += 2) {
            Assert.assertEquals(elements[i + 1], readState.get(ordinal, elements[i]));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }

}
