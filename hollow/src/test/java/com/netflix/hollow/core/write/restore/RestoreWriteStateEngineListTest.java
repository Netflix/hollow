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
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class RestoreWriteStateEngineListTest extends AbstractStateEngineTest {

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

        assertList(0, 1, 2, 3);

        restoreWriteStateEngineFromReadStateEngine();

        addRecord(1, 2, 3);
        addRecord(4, 5, 6);
        addRecord(1000, 1001, 1002);
        addRecord(1000000, 10001, 10002, 10003, 10004, 10005, 10006, 10007);

        roundTripDelta();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestList").maxOrdinal());
        assertList(0, 1, 2, 3);
        assertList(1, 4, 5, 6);
        assertList(3, 1000, 1001, 1002);
        assertList(4, 1000000, 10001, 10002, 10003, 10004, 10005, 10006, 10007);

        addRecord(1000, 1001, 1002);

        roundTripDelta();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestList").maxOrdinal());

        roundTripDelta();

        Assert.assertEquals(3, readStateEngine.getTypeState("TestList").maxOrdinal());
    }

    @Test
    public void restoreFailsIfShardConfigurationChanges() throws IOException {
        roundTripSnapshot();

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowListTypeWriteState misconfiguredTypeState = new HollowListTypeWriteState(new HollowListSchema("TestList", "TestObject"), 16);
        writeStateEngine.addTypeState(misconfiguredTypeState);

        try {
            writeStateEngine.restoreFrom(readStateEngine);
            Assert.fail("Should have thrown IllegalStateException because shard configuration has changed");
        } catch (IllegalStateException expected) {
        }
    }

    private void addRecord(int... ordinals) {
        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int i = 0; i < ordinals.length; i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestList", rec);
    }

    private void assertList(int ordinal, int... elements) {
        HollowListTypeReadState readState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
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
