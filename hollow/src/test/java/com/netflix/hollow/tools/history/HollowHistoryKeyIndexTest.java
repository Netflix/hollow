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
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryKeyIndexTest extends AbstractStateEngineTest {

    private HollowObjectSchema aSchema;
    private HollowObjectSchema bSchema;

    @Before
    public void setUp() {
        aSchema = new HollowObjectSchema("A", 3);
        aSchema.addField("id", FieldType.FLOAT);
        aSchema.addField("anotherField", FieldType.LONG);
        aSchema.addField("bRef", FieldType.REFERENCE, "B");

        bSchema = new HollowObjectSchema("B", 2, new PrimaryKey("B", "id"));
        bSchema.addField("id", FieldType.STRING);
        bSchema.addField("anotherField", FieldType.DOUBLE);

        super.setUp();
    }

    @Test
    public void extractsAndIndexesKeyRecords() throws IOException {
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
        HollowHistory history = new HollowHistory(readEngine, 1L, 1);
        HollowHistoryKeyIndex keyIdx = new HollowHistoryKeyIndex(history);
        keyIdx.addTypeIndex("A", "id", "bRef.id");
        keyIdx.addTypeIndex("B", "id");

        keyIdx.indexTypeField("A", "bRef");
        keyIdx.indexTypeField("A", "id");
        keyIdx.indexTypeField("B", "id");

        addRecord(1.1F, "one", 1L, 1.1D);
        addRecord(2.2F, "two", 2L, 2.2D);
        addRecord(3.3F, "one", 3L, 1.1D);

        roundTripSnapshot();

        assertResults(keyIdx, "A", "two");

        keyIdx.update(readStateEngine, false);

        addRecord(1.1F, "one", 1L, 1.1D);
        //addRecord(2.2F, "2.2", 2L, 2.2D);
        addRecord(3.3F, "one", 3L, 1.1D);
        addRecord(4.4F, "four", 4L, 4.4D);

        roundTripDelta();
        keyIdx.update(readStateEngine, true);

        addRecord(1.1F, "one", 1L, 1.1D);
        addRecord(5.5F, "five!", 5L, 5.5D);
        addRecord(3.3F, "one", 3L, 1.1D);
        addRecord(4.4F, "four", 4L, 4.4D);

        roundTripDelta();
        keyIdx.update(readStateEngine, true);

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("A");

        Assert.assertEquals(0, keyIdx.getRecordKeyOrdinal(typeState, 0));
        Assert.assertEquals("1.1:one", keyIdx.getKeyDisplayString("A", 0));

        Assert.assertEquals(4, keyIdx.getRecordKeyOrdinal(typeState, 1));
        Assert.assertEquals("5.5:five!", keyIdx.getKeyDisplayString("A", 4));

        Assert.assertEquals(2, keyIdx.getRecordKeyOrdinal(typeState, 2));
        Assert.assertEquals("3.3:one", keyIdx.getKeyDisplayString("A", 2));

        Assert.assertEquals(3, keyIdx.getRecordKeyOrdinal(typeState, 3));
        Assert.assertEquals("4.4:four", keyIdx.getKeyDisplayString("A", 3));

        Assert.assertEquals("2.2:two", keyIdx.getKeyDisplayString("A", 1));


        /// query returns all matching keys
        assertResults(keyIdx, "A", "one", 0, 2);
        assertResults(keyIdx, "A", "two", 1);
        assertResults(keyIdx, "A", "four", 3);
        assertResults(keyIdx, "A", "five!", 4);

        assertResults(keyIdx, "A", "1.1", 0);
        assertResults(keyIdx, "A", "2.2", 1);
        assertResults(keyIdx, "A", "3.3", 2);
        assertResults(keyIdx, "A", "4.4", 3);
        assertResults(keyIdx, "A", "5.5", 4);

        assertResults(keyIdx, "A", "notfound");

        assertResults(keyIdx, "B", "one", 0);
        assertResults(keyIdx, "B", "two", 1);
        assertResults(keyIdx, "B", "four", 2);
        assertResults(keyIdx, "B", "five!", 3);
    }

    private void assertResults(HollowHistoryKeyIndex keyIdx, String type, String query, int... expectedResults) {
        IntList actualResults = keyIdx.getTypeKeyIndexes().get(type).queryIndexedFields(query);

        Assert.assertEquals(expectedResults.length, actualResults.size());

        actualResults.sort();

        for(int i = 0; i < expectedResults.length; i++) {
            Assert.assertEquals(expectedResults[i], actualResults.get(i));
        }
    }

    private void addRecord(float aId, String bId, long anotherAField, double anotherBField) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(bSchema);
        bRec.setString("id", bId);
        bRec.setDouble("anotherField", anotherBField);
        int bOrdinal = writeStateEngine.add("B", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(aSchema);
        aRec.setFloat("id", aId);
        aRec.setReference("bRef", bOrdinal);
        aRec.setLong("anotherField", anotherAField);
        writeStateEngine.add("A", aRec);
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState aWriteState = new HollowObjectTypeWriteState(aSchema);
        HollowObjectTypeWriteState bWriteState = new HollowObjectTypeWriteState(bSchema);
        writeStateEngine.addTypeState(aWriteState);
        writeStateEngine.addTypeState(bWriteState);
    }

}
