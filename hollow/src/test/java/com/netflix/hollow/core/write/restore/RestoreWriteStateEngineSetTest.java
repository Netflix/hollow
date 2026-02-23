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
package com.netflix.hollow.core.write.restore;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class RestoreWriteStateEngineSetTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(1, 2, 3);
        addRecord(2, 3, 4);
        addRecord(3, 4, 5);

        roundTripSnapshot();

        addRecord(1, 2, 3);
        addRecord(3, 4, 5);
        addRecord(1000, 1001, 1002);

        roundTripDelta();

        assertSet(1, 2, 3, 4);

        restoreWriteStateEngineFromReadStateEngine();

        addRecord(1, 2, 3);
        addRecord(4, 5, 6);
        addRecord(1000, 1001, 1002);
        addRecord(1000000, 10001, 10002, 10003, 10004, 10005, 10006, 10007);

        roundTripDelta();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestSet").maxOrdinal());
        assertSet(0, 1, 2, 3);
        assertSet(1, 4, 5, 6);
        assertSet(3, 1000, 1001, 1002);
        assertSet(4, 1000000, 10001, 10002, 10003, 10004, 10005, 10006, 10007);

        addRecord(1000, 1001, 1002);

        roundTripDelta();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestSet").maxOrdinal());

        roundTripDelta();

        Assert.assertEquals(3, readStateEngine.getTypeState("TestSet").maxOrdinal());
    }

    @Test
    public void restoreFailsIfShardConfigurationChanges() throws IOException {
        roundTripSnapshot();
        
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowSetTypeWriteState misconfiguredTypeState = new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject"), 16, false, null);
        writeStateEngine.addTypeState(misconfiguredTypeState);

        try {
            writeStateEngine.restoreFrom(readStateEngine);
            Assert.fail("Should have thrown IllegalStateException because shard configuration has changed");
        } catch(IllegalStateException expected) { }
    }

    private void addRecord(int... ordinals) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i], ordinals[i] + 10); // the hash code is deliberately specified here as different from the ordinal.
        }

        writeStateEngine.add("TestSet", rec);
    }

    private void assertSet(int ordinal, int... elements) {
        HollowSetTypeReadState readState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        Assert.assertEquals(elements.length, readState.size(ordinal));

        for(int element : elements) {
            if(!readState.contains(ordinal, element, element + 10)) { // the hash code is deliberately specified here as different from the ordinal.
                Assert.fail("Set did not contain element: " + element);
            }
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowSetTypeWriteState writeState = new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject"));
        writeStateEngine.addTypeState(writeState);
    }

}
