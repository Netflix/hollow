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
 */
package com.netflix.hollow.api.consumer.data;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor.UpdatedRecord;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowDataAccessorTest extends AbstractStateEngineTest {
    private static final String TEST_TYPE = "TestObject";
    HollowObjectSchema schema;

    @Override
    @Before
    public void setUp() {
        schema = new HollowObjectSchema(TEST_TYPE, 2, new PrimaryKey(TEST_TYPE, "f1"));
        schema.addField("f1", FieldType.INT);
        schema.addField("f2", FieldType.STRING);

        super.setUp();
    }

    @Test
    public void test() throws IOException {
        addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");

        roundTripSnapshot();
        {
            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
            dAccessor.computeDataChange();
            Assert.assertTrue(dAccessor.isDataChangeComputed());

            Assert.assertEquals(3, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(1, 2, 3));
            Assert.assertTrue(dAccessor.getRemovedRecords().isEmpty());
            Assert.assertTrue(dAccessor.getUpdatedRecords().isEmpty());
        }

        writeStateEngine.prepareForNextCycle(); /// not necessary to call, but needs to be a no-op.

        addRecord(1, "one");
        // addRecord(2, "two"); // removed
        addRecord(3, "three_updated"); // updated
        addRecord(1000, "one thousand"); // added
        addRecord(0, "zero"); // added

        roundTripDelta();
        {
            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
            Assert.assertFalse(dAccessor.isDataChangeComputed()); // Make sure it does not pre compute

            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(1000, 0));
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(2));
            Assert.assertEquals(1, dAccessor.getUpdatedRecords().size());
            assertUpdatedList(dAccessor.getUpdatedRecords(), Arrays.asList("three"), Arrays.asList("three_updated"));

            Assert.assertTrue(dAccessor.isDataChangeComputed()); // Make sure data change is computed once data change API are invoked
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(TEST_TYPE);
        Assert.assertEquals(5, typeState.maxOrdinal());

        assertObject(typeState, 0, 1, "one");
        assertObject(typeState, 1, 2, "two"); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, 3, "three"); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 3, 3, "three_updated");
        assertObject(typeState, 4, 1000, "one thousand");
        assertObject(typeState, 5, 0, "zero");

        roundTripDelta(); // remove everything
        {
            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(4, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(1, 3, 1000, 0));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        assertObject(typeState, 0, 1, "one"); /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, 2, ""); /// this record should now be disappeared.
        // assertObject(typeState, 2, 3, "three"); /// this record should now be disappeared.
        assertObject(typeState, 3, 3, "three_updated"); /// "ghost"
        assertObject(typeState, 4, 1000, "one thousand"); /// "ghost"
        assertObject(typeState, 5, 0, "zero"); /// "ghost"

        Assert.assertEquals(5, typeState.maxOrdinal());

        addRecord(634, "six hundred thirty four");
        addRecord(0, "zero");

        roundTripDelta();
        {
            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(634, 0));
            Assert.assertEquals(0, dAccessor.getRemovedRecords().size());
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, 634, "six hundred thirty four"); /// now, since all records were removed, we can recycle the ordinal "0", even
                                                                    /// though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, 0, "zero"); /// even though "zero" had an equivalent record in the previous cycle at ordinal "4", it is now
                                               /// assigned to recycled ordinal "1".
    }

    @Test
    public void typeMissing() throws IOException {
        roundTripSnapshot();

        String typeName = "ThisTypeDoesNotExist";
        assertThatThrownBy(() -> {
            new AbstractHollowDataAccessor<Object>(readStateEngine, typeName) {
                @Override public Object getRecord(int ordinal) { return null; }
            };
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining(typeName)
          .hasMessageContaining("not loaded");
    }

    private void addRecord(int intVal, String strVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);

        writeStateEngine.add(TEST_TYPE, rec);
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);

        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
    }

    private void assertList(Collection<GenericHollowObject> listOfObj, List<Integer> listOfIds) {
        int i = 0;
        for (GenericHollowObject obj : listOfObj) {
            int id = listOfIds.get(i++);
            Assert.assertEquals(id, obj.getInt("f1"));
        }
    }

    private void assertUpdatedList(Collection<UpdatedRecord<GenericHollowObject>> listOfObj, List<String> beforeValues, List<String> afterValues) {
        int i = 0;
        for (UpdatedRecord<GenericHollowObject> obj : listOfObj) {
            int beforeId = obj.getBefore().getInt("f1");
            int afterId = obj.getAfter().getInt("f1");
            Assert.assertEquals(beforeId, afterId);

            String beforeVal = beforeValues.get(i);
            String afterVal = afterValues.get(i++);
            Assert.assertNotEquals(beforeVal, afterVal);
            Assert.assertEquals(beforeVal, obj.getBefore().getString("f2"));
            Assert.assertEquals(afterVal, obj.getAfter().getString("f2"));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }
}
