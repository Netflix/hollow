/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryListTest extends AbstractStateEngineTest {

    private HollowListSchema schema;

    @Before
    public void setUp() {
        schema = new HollowListSchema("TestList", "TestObject");

        super.setUp();
    }

    @Test
    public void testHistoricalList() throws IOException {
        addRecord(10, 20, 30);
        addRecord(20, 30, 40);
        addRecord(30, 40, 50);
        addRecord(40, 50, 60);
        addRecord(50, 60, 70);

        roundTripSnapshot();

        addRecord(10, 20, 30);
        addRecord(20, 30, 40);
        //addRecord(30, 40, 50);
        addRecord(40, 50, 60);
        //addRecord(50, 60, 70);
        addRecord(60, 70, 80);
        addRecord(70, 80, 90);
        addRecord(80, 90, 100);

        roundTripDelta();

        HollowHistoricalStateDataAccess history1 = new HollowHistoricalStateCreator().createBasedOnNewDelta(2, readStateEngine);

        addRecord(10, 20, 30);
        addRecord(20, 30, 40);
        addRecord(100, 200, 300); //addRecord(30, 40, 50);
        addRecord(40, 50, 60);
        // addRecord(50, 60, 70);
        addRecord(60, 70, 80);
        // addRecord(70, 80, 90);
        // addRecord(80, 90, 100);

        roundTripDelta();

        HollowHistoricalStateDataAccess history2 = new HollowHistoricalStateCreator().createBasedOnNewDelta(3, readStateEngine);

        addRecord(10, 20, 30);
        addRecord(20, 30, 40);
        addRecord(100, 200, 300); //addRecord(30, 40, 50);
        addRecord(40, 50, 60);
        addRecord(200, 300, 400); // addRecord(50, 60, 70);
        addRecord(60, 70, 80);
        addRecord(300, 400, 500); // addRecord(70, 80, 90);
        addRecord(400, 500, 600); // addRecord(80, 90, 100);

        roundTripDelta();

        assertRecord(history1, 0, 10, 20, 30);
        assertRecord(history1, 1, 20, 30, 40);
        assertRecord(history1, 2, 30, 40, 50);
        assertRecord(history1, 3, 40, 50, 60);
        assertRecord(history1, 4, 50, 60, 70);

        assertRecord(history2, 0, 10, 20, 30);
        assertRecord(history2, 1, 20, 30, 40);
        assertRecord(history2, 3, 40, 50, 60);
        assertRecord(history2, 5, 60, 70, 80);
        assertRecord(history2, 6, 70, 80, 90);
        assertRecord(history2, 7, 80, 90, 100);


        assertRecord(readStateEngine, 0, 10, 20, 30);
        assertRecord(readStateEngine, 1, 20, 30, 40);
        assertRecord(readStateEngine, 2, 100, 200, 300);
        assertRecord(readStateEngine, 3, 40, 50, 60);
        assertRecord(readStateEngine, 4, 200, 300, 400);
        assertRecord(readStateEngine, 5, 60, 70, 80);
        assertRecord(readStateEngine, 6, 300, 400, 500);
        assertRecord(readStateEngine, 7, 400, 500, 600);
    }

    private void assertRecord(HollowDataAccess dataAccess, int ordinal, int... expectedElements) {
        HollowListTypeDataAccess typeDataAccess = (HollowListTypeDataAccess)dataAccess.getTypeDataAccess("TestList");

        for(int i=0;i<expectedElements.length;i++) {
            Assert.assertEquals(expectedElements[i], typeDataAccess.getElementOrdinal(ordinal, i));
        }
    }

    private void addRecord(int... elements) {
        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int element : elements) {
            rec.addElement(element);
        }

        writeStateEngine.add("TestList", rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowListTypeWriteState(schema));
    }

}
