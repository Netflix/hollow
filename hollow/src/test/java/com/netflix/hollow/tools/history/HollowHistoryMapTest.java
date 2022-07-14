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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryMapTest extends AbstractStateEngineTest {

    private HollowMapSchema schema;

    @Before
    public void setUp() {
        schema = new HollowMapSchema("TestMap", "TestKey", "TestValue");

        super.setUp();
    }

    @Test
    public void testHistoricalSet() throws IOException {
        addRecord(10, 11, 20, 21, 30, 31);
        addRecord(20, 21, 30, 31, 40, 41);
        addRecord(30, 31, 40, 41, 50, 51);
        addRecord(40, 41, 50, 51, 60, 61);
        addRecord(50, 51, 60, 61, 70, 71);

        roundTripSnapshot();

        addRecord(10, 11, 20, 21, 30, 31);
        addRecord(20, 21, 30, 31, 40, 41);
        //addRecord(30, 31, 40, 41, 50, 51);
        addRecord(40, 41, 50, 51, 60, 61);
        //addRecord(50, 51, 60, 61, 70, 71);
        addRecord(60, 61, 70, 71, 80, 81);
        addRecord(70, 71, 80, 81, 90, 91);
        addRecord(80, 81, 90, 91, 100, 101);

        roundTripDelta();

        HollowHistoricalStateDataAccess history1 = new HollowHistoricalStateCreator().createBasedOnNewDelta(2, readStateEngine);

        addRecord(10, 11, 20, 21, 30, 31);
        addRecord(20, 21, 30, 31, 40, 41);
        addRecord(100, 101, 200, 201, 300, 301, 400, 401); //addRecord(30, 31, 40, 41, 50, 51);
        addRecord(40, 41, 50, 51, 60, 61);
        // addRecord(50, 51, 60, 61, 70, 71);
        addRecord(60, 61, 70, 71, 80, 81);
        // addRecord(70, 71, 80, 81, 90, 91);
        // addRecord(80, 81, 90, 91, 100, 101);

        roundTripDelta();

        HollowHistoricalStateDataAccess history2 = new HollowHistoricalStateCreator().createBasedOnNewDelta(3, readStateEngine);

        addRecord(10, 11, 20, 21, 30, 31);
        addRecord(20, 21, 30, 31, 40, 41);
        addRecord(100, 101, 200, 201, 300, 301, 400, 401); //addRecord(30, 31, 40, 41, 50, 51);
        addRecord(40, 41, 50, 51, 60, 61);
        addRecord(200, 201); // addRecord(50, 51, 60, 61, 70, 71);
        addRecord(60, 61, 70, 71, 80, 81);
        addRecord(300, 301, 400, 401, 500, 501); // addRecord(70, 71, 80, 81, 90, 91);
        addRecord(400, 401, 500, 501, 600, 601); // addRecord(80, 81, 90, 91, 100, 101);

        roundTripDelta();

        assertRecord(history1, 0, 10, 11, 20, 21, 30, 31);
        assertRecord(history1, 1, 20, 21, 30, 31, 40, 41);
        assertRecord(history1, 2, 30, 31, 40, 41, 50, 51);
        assertRecord(history1, 3, 40, 41, 50, 51, 60, 61);
        assertRecord(history1, 4, 50, 51, 60, 61, 70, 71);

        assertRecord(history2, 0, 10, 11, 20, 21, 30, 31);
        assertRecord(history2, 1, 20, 21, 30, 31, 40, 41);
        assertRecord(history2, 3, 40, 41, 50, 51, 60, 61);
        assertRecord(history2, 5, 60, 61, 70, 71, 80, 81);
        assertRecord(history2, 6, 70, 71, 80, 81, 90, 91);
        assertRecord(history2, 7, 80, 81, 90, 91, 100, 101);


        assertRecord(readStateEngine, 0, 10, 11, 20, 21, 30, 31);
        assertRecord(readStateEngine, 1, 20, 21, 30, 31, 40, 41);
        assertRecord(readStateEngine, 2, 100, 101, 200, 201, 300, 301, 400, 401);
        assertRecord(readStateEngine, 3, 40, 41, 50, 51, 60, 61);
        assertRecord(readStateEngine, 4, 200, 201);
        assertRecord(readStateEngine, 5, 60, 61, 70, 71, 80, 81);
        assertRecord(readStateEngine, 6, 300, 301, 400, 401, 500, 501);
        assertRecord(readStateEngine, 7, 400, 401, 500, 501, 600, 601);
    }

    private void assertRecord(HollowDataAccess dataAccess, int ordinal, int... expectedEntries) {
        HollowMapTypeDataAccess typeDataAccess = (HollowMapTypeDataAccess) dataAccess.getTypeDataAccess("TestMap");

        test:
        for(int i = 0; i < expectedEntries.length; i += 2) {
            HollowMapEntryOrdinalIterator iter = typeDataAccess.potentialMatchOrdinalIterator(ordinal, expectedEntries[i]);

            while(iter.next()) {
                if(iter.getKey() == expectedEntries[i] && iter.getValue() == expectedEntries[i + 1])
                    continue test;
            }

            Assert.fail("Did not find expected entry (" + expectedEntries[i] + "," + expectedEntries[i + 1] + ") for ordinal " + ordinal);
        }
    }

    private void addRecord(int... entries) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i = 0; i < entries.length; i += 2) {
            rec.addEntry(entries[i], entries[i + 1]);
        }

        writeStateEngine.add("TestMap", rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(schema));
    }

}
