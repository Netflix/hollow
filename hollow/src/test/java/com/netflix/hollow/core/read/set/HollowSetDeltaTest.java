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
package com.netflix.hollow.core.read.set;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetDeltaTest extends AbstractStateEngineTest {

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

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        assertSet(typeState, 0, 10, 20, 30);
        assertSet(typeState, 1, 40, 50, 60);  /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertSet(typeState, 2, 70, 80, 90);
        assertSet(typeState, 3, 100, 200, 300, 400, 500, 600, 700);
        assertSet(typeState, 4, 1, 2, 3);

        Assert.assertEquals(4, typeState.maxOrdinal());

        roundTripDelta();

        assertSet(typeState, 0, 10, 20, 30);  /// all sets were "removed", but again hang around until the following cycle.
        assertSet(typeState, 1); /// this set should now be disappeared.
        assertSet(typeState, 2, 70, 80, 90);  /// "ghost"
        assertSet(typeState, 3, 100, 200, 300, 400, 500, 600, 700); /// "ghost"
        assertSet(typeState, 4, 1, 2, 3); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(634, 54732);
        addRecord(1, 2, 3);

        roundTripDelta();

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertSet(typeState, 0, 634, 54732); /// now, since all sets were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertSet(typeState, 1, 1, 2, 3);  /// even though 1, 2, 3 had an equivalent set in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".

    }

    private void addRecord(int... ordinals) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestSet", rec);
    }

    private void assertSet(HollowSetTypeReadState readState, int ordinal, int... elements) {
        Assert.assertEquals(elements.length, readState.size(ordinal));

        for(int i=0;i<elements.length;i++) {
            Assert.assertTrue(readState.contains(ordinal, elements[i]));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowSetTypeWriteState writeState = new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject"));
        writeStateEngine.addTypeState(writeState);
    }

}
