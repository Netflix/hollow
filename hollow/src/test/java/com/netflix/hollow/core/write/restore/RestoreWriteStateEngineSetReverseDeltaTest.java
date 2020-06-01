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
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class RestoreWriteStateEngineSetReverseDeltaTest extends AbstractStateEngineTest {
    @Test
    public void test() throws IOException {
        addRecord(1, 3,
                  2, 2);
        
        roundTripSnapshot();
        
        assertSetContains(0, 1, 3);
        assertSetContains(0, 2, 2);
        
        restoreWriteStateEngineFromReadStateEngine();
        
        addRecord(3, 3,
                  4, 4);
        
        writeStateEngine.prepareForWrite();
        ByteArrayOutputStream reverseDeltaStream = new ByteArrayOutputStream();
        ByteArrayOutputStream deltaStream = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        writer.writeReverseDelta(reverseDeltaStream);
        writer.writeDelta(deltaStream);
        
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.applyDelta(HollowBlobInput.serial(deltaStream.toByteArray()));
        reader.applyDelta(HollowBlobInput.serial(reverseDeltaStream.toByteArray()));
        
        assertSetContains(0, 1, 3);
        assertSetContains(0, 2, 2);
    }
    
    private void assertSetContains(int setOrdinal, int valueOrdinal, int hashCode) {
        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");
        
        Assert.assertTrue(typeState.contains(setOrdinal, valueOrdinal, hashCode));
    }

    
    private void addRecord(int... ordinalsAndHashCodes) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i=0;i<ordinalsAndHashCodes.length;i+=2) {
            rec.addElement(ordinalsAndHashCodes[i], ordinalsAndHashCodes[i+1]);
        }

        writeStateEngine.add("TestSet", rec);
    }


    
    @Override
    protected void initializeTypeStates() {
        HollowSetTypeWriteState writeState = new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject"));
        writeStateEngine.addTypeState(writeState);
    }
}
