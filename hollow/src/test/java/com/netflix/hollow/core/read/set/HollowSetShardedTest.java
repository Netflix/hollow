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
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowSetShardedTest extends AbstractStateEngineTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testShardedData() throws IOException {

        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i = 0; i < 2000; i++) {
            rec.reset();
            rec.addElement(i);
            rec.addElement(i + 1);
            rec.addElement(i + 2);

            writeStateEngine.add("TestSet", rec);
        }

        roundTripSnapshot();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestSet").numShards());

        HollowSetTypeDataAccess setDataAccess = (HollowSetTypeDataAccess) readStateEngine.getTypeDataAccess("TestSet");
        for(int i = 0; i < 1000; i++) {
            Assert.assertEquals(3, setDataAccess.size(i));
            Assert.assertTrue(setDataAccess.contains(i, i));
            Assert.assertTrue(setDataAccess.contains(i, i + 1));
            Assert.assertTrue(setDataAccess.contains(i, i + 2));
        }

        for(int i = 0; i < 2000; i++) {
            rec.reset();
            rec.addElement(i * 2);
            rec.addElement(i * 2 + 1);
            rec.addElement(i * 2 + 2);

            writeStateEngine.add("TestSet", rec);
        }

        roundTripDelta();

        int expectedValue = 0;

        BitSet populatedOrdinals = readStateEngine.getTypeState("TestSet").getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            Assert.assertEquals(3, setDataAccess.size(ordinal));
            Assert.assertTrue(setDataAccess.contains(ordinal, expectedValue));
            Assert.assertTrue(setDataAccess.contains(ordinal, expectedValue + 1));
            Assert.assertTrue(setDataAccess.contains(ordinal, expectedValue + 2));

            expectedValue += 2;
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject")));
    }

}
