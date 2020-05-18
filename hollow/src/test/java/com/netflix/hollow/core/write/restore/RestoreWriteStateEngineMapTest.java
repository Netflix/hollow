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
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class RestoreWriteStateEngineMapTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(10, 20, 30, 40);
        addRecord(40, 50, 60, 70);
        addRecord(70, 80, 90, 100);

        roundTripSnapshot();
        
        restoreWriteStateEngineFromReadStateEngine();
        
        addRecord(10, 20, 30, 40);
        addRecord(70, 80, 90, 100);
        addRecord(100, 200, 300, 400, 500, 600, 700, 800);
        addRecord(1, 2, 3, 4);

        roundTripDelta();
        
        restoreWriteStateEngineFromReadStateEngine();

        assertMap(0, 10, 20, 30, 40);
        assertMap(1, 40, 50, 60, 70);  /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertMap(2, 70, 80, 90, 100);
        assertMap(3, 100, 200, 300, 400, 500, 600, 700, 800);
        assertMap(4, 1, 2, 3, 4);

        Assert.assertEquals(4, maxOrdinal());

        roundTripDelta();

        assertMap(0, 10, 20, 30, 40);  /// all maps were "removed", but again hang around until the following cycle.
        assertMap(1); /// this map should now be disappeared.
        assertMap(2, 70, 80, 90, 100);  /// "ghost"
        assertMap(3, 100, 200, 300, 400, 500, 600, 700, 800); /// "ghost"
        assertMap(4, 1, 2, 3, 4); /// "ghost"

        Assert.assertEquals(4, maxOrdinal());

        addRecord(634, 54732);
        addRecord(1, 2, 3, 4);

        roundTripSnapshot();

        Assert.assertEquals(1, maxOrdinal());
        assertMap(0, 634, 54732); /// now, since all maps were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertMap(1, 1, 2, 3, 4);  /// even though 1, 2, 3 had an equivalent map in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".

    }
    
    @Test
    public void restoreFailsIfShardConfigurationChanges() throws IOException {
        roundTripSnapshot();
        
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMapTypeWriteState misconfiguredTypeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"), 16);
        writeStateEngine.addTypeState(misconfiguredTypeState);

        try {
            writeStateEngine.restoreFrom(readStateEngine);
            Assert.fail("Should have thrown IllegalStateException because shard configuration has changed");
        } catch(IllegalStateException expected) { }
    }

    private void addRecord(int... ordinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i=0;i<ordinals.length;i+=2) {
            rec.addEntry(ordinals[i], ordinals[i+1], ordinals[i] + 100);
        }

        writeStateEngine.add("TestMap", rec);
    }

    private void assertMap(int ordinal, int... elements) {
        HollowMapTypeReadState readState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
        
        Assert.assertEquals(elements.length / 2, readState.size(ordinal));

        for(int i=0;i<elements.length;i+=2) {
            Assert.assertEquals(elements[i+1], readState.get(ordinal, elements[i], elements[i] + 100));
        }
    }
    
    private int maxOrdinal() {
        HollowMapTypeReadState readState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
        return readState.maxOrdinal();
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }

}
