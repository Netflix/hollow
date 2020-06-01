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
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class RestoreWriteStateEngineMapReverseDeltaTest extends AbstractStateEngineTest {

    @Test
    public void test() throws IOException {
        addRecord(1, 1, 3,
                  2, 2, 2);
        
        roundTripSnapshot();
        
        assertMapContains(0, 1, 1, 3);
        assertMapContains(0, 2, 2, 2);
        
        restoreWriteStateEngineFromReadStateEngine();
        
        addRecord(3, 3, 3,
                  4, 4, 2);
        
        writeStateEngine.prepareForWrite();
        ByteArrayOutputStream reverseDeltaStream = new ByteArrayOutputStream();
        ByteArrayOutputStream deltaStream = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        writer.writeReverseDelta(reverseDeltaStream);
        writer.writeDelta(deltaStream);
        
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.applyDelta(HollowBlobInput.serial(deltaStream.toByteArray()));
        reader.applyDelta(HollowBlobInput.serial(reverseDeltaStream.toByteArray()));
        
        assertMapContains(0, 1, 1, 3);
        assertMapContains(0, 2, 2, 2);
    }
    
    private void assertMapContains(int mapOrdinal, int keyOrdinal, int valueOrdinal, int hashCode) {
        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
        
        int actualValueOrdinal = typeState.get(mapOrdinal, keyOrdinal, hashCode);
        
        Assert.assertEquals(valueOrdinal, actualValueOrdinal);
    }

    
    private void addRecord(int... ordinalsAndHashCodes) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i=0;i<ordinalsAndHashCodes.length;i+=3) {
            rec.addEntry(ordinalsAndHashCodes[i], ordinalsAndHashCodes[i+1], ordinalsAndHashCodes[i+2]);
        }

        writeStateEngine.add("TestMap", rec);
    }


    
    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }


}
