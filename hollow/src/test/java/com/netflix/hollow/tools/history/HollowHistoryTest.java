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

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryTest extends AbstractStateEngineTest {

    ///TODO: Use key index to test double snapshot behavior.

    private HollowObjectSchema aSchema;

    @Override
    @Before
    public void setUp() {
        aSchema = new HollowObjectSchema("A", 3, "a1");
        aSchema.addField("a1", FieldType.INT);
        aSchema.addField("a2", FieldType.INT);
        aSchema.addField("a3", FieldType.INT);

        super.setUp();
    }

    @Test
    public void test() throws IOException {
        addRecord(1, 2, 3);
        addRecord(2, 3, 4);
        addRecord(3, 4, 5);
        addRecord(4, 5, 6);

        roundTripSnapshot();

        HollowHistory history = new HollowHistory(readStateEngine, 1L, 5);
        // No needed since it can auto discover from schema's primary key
        // history.getKeyIndex().addTypeIndex("A", "a1");
        history.getKeyIndex().indexTypeField("A", "a1");

        addRecord(1, 2, 3);
        /// addRecord(2, 3, 4);  removed record
        addRecord(3, 4, 5);
        addRecord(4, 5, 6);
        addRecord(2, 3, 7);

        roundTripDelta();

        history.deltaOccurred(2L);

        addRecord(1, 2, 3);
        addRecord(3, 4, 7);
        /// addRecord(3, 4, 5);  removed record
        addRecord(4, 5, 6);
        addRecord(2, 3, 7);

        roundTripSnapshot();
        history.doubleSnapshotOccurred(readStateEngine, 3L);

        super.setUp();

        //addRecord(3, 4, 7);
        addRecord(4, 5, 7);
        addRecord(1, 2, 3);
        // addRecord(4, 5, 6);  /// removed record
        addRecord(2, 3, 7);
        //addRecord(5, 6, 7);

        roundTripSnapshot();
        history.doubleSnapshotOccurred(readStateEngine, 4L);

        addRecord(1, 2, 3);
        addRecord(3, 4, 7);
        //addRecord(4, 5, 7);
        addRecord(5, 6, 7);
        //addRecord(2, 3, 7);

        roundTripDelta();
        history.deltaOccurred(5L);

        assertRecord(retrieveRemovedRecord(history, 2L, 2), 2, 3, 4);
        assertRecord(retrieveAddedRecord  (history, 2L, 2), 2, 3, 7);

        assertRecord(retrieveRemovedRecord(history, 3L, 3), 3, 4, 5);
        assertRecord(retrieveAddedRecord  (history, 3L, 3), 3, 4, 7);

        assertRecord(retrieveRemovedRecord(history, 4L, 4), 4, 5, 6);
        assertRecord(retrieveAddedRecord  (history, 4L, 4), 4, 5, 7);
        assertRecord(retrieveRemovedRecord(history, 4L, 3), 3, 4, 7);

        assertRecord(retrieveRemovedRecord(history, 5L, 4), 4, 5, 7);
        assertRecord(retrieveRemovedRecord(history, 5L, 2), 2, 3, 7);
        assertRecord(retrieveAddedRecord  (history, 5L, 5), 5, 6, 7);
        assertRecord(retrieveAddedRecord  (history, 5L, 3), 3, 4, 7);
    }

    private HollowObject retrieveRemovedRecord(HollowHistory history, long version, int key) {
        HollowHistoricalState historicalState = history.getHistoricalState(version);

        IntList queryResult = history.getKeyIndex().getTypeKeyIndexes().get("A").queryIndexedFields(String.valueOf(key));
        int keyOrdinal = queryResult.get(0);

        int removedOrdinal = historicalState.getKeyOrdinalMapping().getTypeMapping("A").findRemovedOrdinal(keyOrdinal);

        return (HollowObject) GenericHollowRecordHelper.instantiate(historicalState.getDataAccess(), "A", removedOrdinal);
    }

    private HollowObject retrieveAddedRecord(HollowHistory history, long version, int key) {
        HollowHistoricalState historicalState = history.getHistoricalState(version);

        IntList queryResult = history.getKeyIndex().getTypeKeyIndexes().get("A").queryIndexedFields(String.valueOf(key));
        int keyOrdinal = queryResult.get(0);

        int addedOrdinal = historicalState.getKeyOrdinalMapping().getTypeMapping("A").findAddedOrdinal(keyOrdinal);

        return (HollowObject) GenericHollowRecordHelper.instantiate(historicalState.getDataAccess(), "A", addedOrdinal);
    }



    private void assertRecord(HollowObject obj, int a1, int a2, int a3) {
        Assert.assertEquals(a1, obj.getInt("a1"));
        Assert.assertEquals(a2, obj.getInt("a2"));
        Assert.assertEquals(a3, obj.getInt("a3"));
    }

    @SuppressWarnings("unused")
    private void printRecord(HollowObject obj) {
        System.out.println(obj.getInt("a1") + "," + obj.getInt("a2") + "," + obj.getInt("a3"));
    }


    private void addRecord(int a1, int a2, int a3) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(aSchema);

        rec.setInt("a1", a1);
        rec.setInt("a2", a2);
        rec.setInt("a3", a3);

        writeStateEngine.add("A", rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(aSchema));
    }

}
