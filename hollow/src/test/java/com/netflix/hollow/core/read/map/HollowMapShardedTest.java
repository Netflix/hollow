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
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowMapShardedTest extends AbstractStateEngineTest {

    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testShardedData() throws IOException {
    
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        
        for(int i=0;i<2000;i++) {
            rec.reset();
            rec.addEntry(i, i+1);
            rec.addEntry(i+2, i+3);
            rec.addEntry(i+4, i+5);
            
            writeStateEngine.add("TestMap", rec);
        }
        
        roundTripSnapshot();
        
        Assert.assertEquals(8, readStateEngine.getTypeState("TestMap").numShards());
        
        HollowMapTypeDataAccess mapDataAccess = (HollowMapTypeDataAccess) readStateEngine.getTypeDataAccess("TestMap");
        for(int i=0;i<1000;i++) {
            Assert.assertEquals(3, mapDataAccess.size(i));
            Assert.assertEquals(i+1, mapDataAccess.get(i, i));
            Assert.assertEquals(i+3, mapDataAccess.get(i, i+2));
            Assert.assertEquals(i+5, mapDataAccess.get(i, i+4));
        }

        for(int i=0;i<2000;i++) {
            rec.reset();
            rec.addEntry(i*2, i*2+1);
            rec.addEntry(i*2+2, i*2+3);
            rec.addEntry(i*2+4, i*2+5);
            
            writeStateEngine.add("TestMap", rec);
        }
        
        roundTripDelta();
        
        int expectedValue = 0;
        
        BitSet populatedOrdinals = readStateEngine.getTypeState("TestMap").getPopulatedOrdinals();
        
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            Assert.assertEquals(3, mapDataAccess.size(ordinal));
            Assert.assertEquals(expectedValue+1, mapDataAccess.get(ordinal, expectedValue));
            Assert.assertEquals(expectedValue+3, mapDataAccess.get(ordinal, expectedValue+2));
            Assert.assertEquals(expectedValue+5, mapDataAccess.get(ordinal, expectedValue+4));
            
            expectedValue += 2;
            ordinal = populatedOrdinals.nextSetBit(ordinal+1);
        }
    }
    
    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue")));
    }

}
