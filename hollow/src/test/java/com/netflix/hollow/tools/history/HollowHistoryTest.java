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

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryTypeKeyIndex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryTest extends AbstractStateEngineTest {

    ///TODO: Use key index to test double snapshot behavior.

    private HollowObjectSchema aSchema;
    private HollowObjectSchema bSchema;
    private HollowObjectSchema emptyTypeSchema;

    private static final String B_TYPE = "B";
    private static final String B_FN_PREFIX = "b";


    @Override
    @Before
    public void setUp() {
        emptyTypeSchema = new HollowObjectSchema("Empty", 1);
        emptyTypeSchema.addField("value", FieldType.STRING);

        aSchema = new HollowObjectSchema("A", 3, "a1");
        aSchema.addField("a1", FieldType.INT);
        aSchema.addField("a2", FieldType.INT);
        aSchema.addField("a3", FieldType.INT);

        bSchema = new HollowObjectSchema(B_TYPE, 2,  B_FN_PREFIX + "1");
        bSchema.addField(B_FN_PREFIX + "1", FieldType.INT);
        bSchema.addField(B_FN_PREFIX + "2", FieldType.INT);

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

    @Test
    public void testNewType() throws IOException {
        addRecord(1, 2, 3);
        addRecord(2, 3, 4);
        roundTripSnapshot();

        HollowHistory history = new HollowHistory(readStateEngine, 1L, 5);
        setupKeyIndex(readStateEngine, history);

        // Double Snapshot - With New Type
        {
            initWriteStateEngine();
            addRecord(1, 2, 3);
            // addRecord(2, 3, 4);  removed record
            addRecord(3, 4, 5);
            addRecord(bSchema, B_FN_PREFIX, 1, 2);

            roundTripSnapshot();
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 2L);

            assertRecord(retrieveRemovedRecord(history, 2L, 2), 2, 3, 4);
            assertRecord(retrieveAddedRecord(history, 2L, 3), 3, 4, 5);

            // First cycle with new type, it does not know
            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
        }

        {
            addRecord(1, 2, 3);
            addRecord(2, 3, 4);
            addRecord(3, 4, 5);
            // addRecord(bSchema, B_FN_PREFIX, 1, 2); remove record
            addRecord(bSchema, B_FN_PREFIX, 2, 3);

            roundTripDelta();
            history.deltaOccurred(3L);

            assertRecord(retrieveAddedRecord(history, 3L, 2), 2, 3, 4);
            assertRecord(retrieveRemovedRecord(history, 3L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveAddedRecord(history, 3L, B_TYPE, 2), B_FN_PREFIX, 2, 3);
        }
    }
    
    @Test
    public void testRemoveType() throws IOException {
        addRecord(1, 2, 3);
        addRecord(2, 3, 4);
        addRecord(bSchema, B_FN_PREFIX, 1, 2);
        addRecord(bSchema, B_FN_PREFIX, 2, 3);
        roundTripSnapshot();

        HollowHistory history = new HollowHistory(readStateEngine, 1L, 5);
        setupKeyIndex(readStateEngine, history);

        // Double Snapshot - With New Type
        {
            initWriteStateEngine();
            Assert.assertNull(writeStateEngine.getTypeState(B_TYPE));

            addRecord(1, 2, 3);
            // addRecord(2, 3, 4);
            addRecord(3, 4, 5);
            // addRecord(bSchema, B_FN_PREFIX, 1, 2);
            // addRecord(bSchema, B_FN_PREFIX, 2, 3);

            roundTripSnapshot();
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 2L);

            assertRecord(retrieveRemovedRecord(history, 2L, 2), 2, 3, 4);
            assertRecord(retrieveAddedRecord(history, 2L, 3), 3, 4, 5);

            assertRecord(retrieveRemovedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveRemovedRecord(history, 2L, B_TYPE, 2), B_FN_PREFIX, 2, 3);
        }

        {
            addRecord(1, 2, 3);
            addRecord(2, 3, 4); // Added it back
            addRecord(3, 4, 5);

            roundTripDelta();
            history.deltaOccurred(3L);

            assertRecord(retrieveAddedRecord(history, 3L, 2), 2, 3, 4);
        }
    }

    @Test
    public void testAddRemoveTypeThenDelta() throws IOException {
        addRecord(1, 2, 3);
        addRecord(2, 3, 4);
        roundTripSnapshot();

        long version = 1;
        HollowHistory history = new HollowHistory(readStateEngine, 1, 10);
        setupKeyIndex(readStateEngine, history);

        {
            initWriteStateEngine();

            addRecord(1, 2, 3);
            addRecord(2, 3, 4);
            // Add new type
            addRecord(bSchema, B_FN_PREFIX, 1, 2);
            addRecord(bSchema, B_FN_PREFIX, 2, 3);

            roundTripSnapshot();
            // Populate the B om the history primiary key indexes
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 2);

            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 2), B_FN_PREFIX, 2, 3);

            version++;
        }

        {
            initWriteStateEngine();

            addRecord(1, 2, 3);
            addRecord(2, 3, 4);
            // Remove instances of B

            roundTripSnapshot();
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 3);

            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 2), B_FN_PREFIX, 2, 3);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 2), B_FN_PREFIX, 2, 3);
        }

        {
            addRecord(1, 2, 3);
            addRecord(2, 3, 4);

            roundTripDelta();
            history.deltaOccurred(4);

            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 2), B_FN_PREFIX, 2, 3);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 2), B_FN_PREFIX, 2, 3);
        }

        {
            initWriteStateEngine();

            addRecord(1, 2, 3);
            addRecord(2, 3, 4);

            roundTripSnapshot();
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 5);

            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveAddedRecord(history, 2L, B_TYPE, 2), B_FN_PREFIX, 2, 3);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 1), B_FN_PREFIX, 1, 2);
            assertRecord(retrieveRemovedRecord(history, 3, B_TYPE, 2), B_FN_PREFIX, 2, 3);
        }
    }

    @Test
    public void testHistoricalStates() throws IOException {
        addRecord(1, 2, 3);

        roundTripSnapshot();

        HollowHistory history = new HollowHistory(readStateEngine, 1L, 2);
        Assert.assertEquals(0, history.getNumberOfHistoricalStates());

        {
            addRecord(1, 2, 3);
            addRecord(4, 5, 6);

            roundTripDelta();
            history.deltaOccurred(2L);

            Assert.assertEquals(1, history.getNumberOfHistoricalStates());
        }


        HollowHistoricalState[] historicalStates;
        {
            addRecord(1, 2, 3);

            roundTripDelta();
            history.deltaOccurred(3L);

            Assert.assertEquals(2, history.getNumberOfHistoricalStates());
            historicalStates = history.getHistoricalStates();
        }

        {
            addRecord(1, 2, 3);
            addRecord(4, 5, 6);

            roundTripDelta();
            history.deltaOccurred(4L);

            Assert.assertEquals(2, history.getNumberOfHistoricalStates());
            Assert.assertEquals(historicalStates[0], history.getHistoricalStates()[1]);
        }

        {
            history.removeHistoricalStates(1);
            Assert.assertEquals(1, history.getNumberOfHistoricalStates());
            historicalStates = history.getHistoricalStates();
        }

        {
            addRecord(1, 2, 3);

            roundTripDelta();
            history.deltaOccurred(5L);

            Assert.assertEquals(2, history.getNumberOfHistoricalStates());
            Assert.assertEquals(historicalStates[0], history.getHistoricalStates()[1]);
        }

        {
            history.removeHistoricalStates(history.getNumberOfHistoricalStates());
            Assert.assertEquals(0, history.getNumberOfHistoricalStates());
        }

        {
            addRecord(1, 2, 3);
            addRecord(4, 5, 6);

            roundTripDelta();
            history.deltaOccurred(6L);

            Assert.assertEquals(1, history.getNumberOfHistoricalStates());
        }
    }

    private void setupKeyIndex(HollowReadStateEngine stateEngine, HollowHistory history) {
        HollowHistoryKeyIndex keyIndex = history.getKeyIndex();
        for (String type : stateEngine.getAllTypes()) {

            HollowTypeReadState typeState = stateEngine.getTypeState(type);
            HollowSchema schema = typeState.getSchema();
            if (schema instanceof HollowObjectSchema) {
                HollowObjectSchema oSchema = (HollowObjectSchema) schema;
                PrimaryKey pKey = oSchema.getPrimaryKey();
                if (pKey == null) continue;

                keyIndex.indexTypeField(pKey, stateEngine);
                System.out.println("Setup KeyIndex: type=" + type + "\t" + pKey);
            }
        }
    }

    private HollowObject retrieveRemovedRecord(HollowHistory history, long version, int key) {
        return retrieveRemovedRecord(history, version, "A", key);
    }

    private HollowObject retrieveAddedRecord(HollowHistory history, long version, int key) {
        return retrieveAddedRecord(history, version, "A", key);
    }

    private HollowObject retrieveRemovedRecord(HollowHistory history, long version, String type, int key) {
        HollowHistoricalState historicalState = history.getHistoricalState(version);

        IntList queryResult = history.getKeyIndex().getTypeKeyIndexes().get(type).queryIndexedFields(String.valueOf(key));
        int keyOrdinal = queryResult.get(0);

        int removedOrdinal = historicalState.getKeyOrdinalMapping().getTypeMapping(type).findRemovedOrdinal(keyOrdinal);

        return (HollowObject) GenericHollowRecordHelper.instantiate(historicalState.getDataAccess(), type, removedOrdinal);
    }


    private HollowObject retrieveAddedRecord(HollowHistory history, long version, String type, int key) {
        HollowHistoricalState historicalState = history.getHistoricalState(version);

        Map<String, HollowHistoryTypeKeyIndex> typeKeyIndexes = history.getKeyIndex().getTypeKeyIndexes();
        IntList queryResult = typeKeyIndexes.get(type).queryIndexedFields(String.valueOf(key));
        int keyOrdinal = queryResult.get(0);

        int addedOrdinal = historicalState.getKeyOrdinalMapping().getTypeMapping(type).findAddedOrdinal(keyOrdinal);

        return (HollowObject) GenericHollowRecordHelper.instantiate(historicalState.getDataAccess(), type, addedOrdinal);
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
    
    private void assertRecord(HollowObject obj, String fnPrefix, int... vals) {
        for (int i = 0; i < vals.length; i++) {
            String fn = fnPrefix + (i + 1);
            Assert.assertEquals(vals[i], obj.getInt(fn));
        }
    }

    private void addRecord(HollowObjectSchema schema, String fnPrefix, int ... vals) {
        String bType = schema.getName();
        if (writeStateEngine.getTypeState(bType) == null) {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
        }

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < vals.length; i++) {
            String fn = fnPrefix + (i + 1);
            rec.setInt(fn, vals[i]);
        }

        writeStateEngine.add(bType, rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(aSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(emptyTypeSchema));
    }

}