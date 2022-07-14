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
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowListShardedTest extends AbstractStateEngineTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testShardedData() throws IOException {

        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int i = 0; i < 2000; i++) {
            rec.reset();
            rec.addElement(i);
            rec.addElement(i + 1);
            rec.addElement(i + 2);

            writeStateEngine.add("TestList", rec);
        }

        roundTripSnapshot();

        Assert.assertEquals(4, readStateEngine.getTypeState("TestList").numShards());

        HollowListTypeDataAccess listDataAccess = (HollowListTypeDataAccess) readStateEngine.getTypeDataAccess("TestList");
        for(int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, listDataAccess.getElementOrdinal(i, 0));
            Assert.assertEquals(i + 1, listDataAccess.getElementOrdinal(i, 1));
            Assert.assertEquals(i + 2, listDataAccess.getElementOrdinal(i, 2));
        }

        for(int i = 0; i < 2000; i++) {
            rec.reset();
            rec.addElement(i * 2);
            rec.addElement(i * 2 + 1);
            rec.addElement(i * 2 + 2);

            writeStateEngine.add("TestList", rec);
        }

        roundTripDelta();

        int expectedValue = 0;

        BitSet populatedOrdinals = readStateEngine.getTypeState("TestList").getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            Assert.assertEquals(expectedValue, listDataAccess.getElementOrdinal(ordinal, 0));
            Assert.assertEquals(expectedValue + 1, listDataAccess.getElementOrdinal(ordinal, 1));
            Assert.assertEquals(expectedValue + 2, listDataAccess.getElementOrdinal(ordinal, 2));

            expectedValue += 2;
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowListTypeWriteState(new HollowListSchema("TestList", "TestObject")));
    }

}
