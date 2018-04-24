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
package com.netflix.hollow.core.read.object;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectDeltaTest extends AbstractStateEngineTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 2);
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

        addRecord(1, "one");
        addRecord(3, "three");
        addRecord(1000, "ten thousand");
        addRecord(0, "zero");

        writeStateEngine.resetToLastPrepareForNextCycle();
        writeStateEngine.prepareForNextCycle();    /// not necessary to call, but needs to be a no-op.

        addRecord(1, "one");
        addRecord(3, "three");
        addRecord(1000, "one thousand");
        addRecord(0, "zero");

        roundTripDelta();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        Assert.assertEquals(4, typeState.maxOrdinal());

        assertObject(typeState, 0, 1, "one");
        assertObject(typeState, 1, 2, "two");  /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, 3, "three");
        assertObject(typeState, 3, 1000, "one thousand");
        assertObject(typeState, 4, 0, "zero");


        roundTripDelta();

        assertObject(typeState, 0, 1, "one");  /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, 2, ""); /// this record should now be disappeared.
        assertObject(typeState, 2, 3, "three");  /// "ghost"
        assertObject(typeState, 3, 1000, "one thousand"); /// "ghost"
        assertObject(typeState, 4, 0, "zero"); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(634, "six hundred thirty four");
        addRecord(0, "zero");

        roundTripDelta();

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, 634, "six hundred thirty four"); /// now, since all records were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, 0, "zero");  /// even though "zero" had an equivalent record in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".

    }

    @Test
    public void allStringFieldsNullTest() throws IOException {
        addRecord(1, null);
        addRecord(2, null);

        roundTripSnapshot();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        assertObject(typeState, 0, 1, null);
        assertObject(typeState, 1, 2, null);
    }

    @Test
    public void testStaleReferenceException() throws IOException {
        addRecord(0, null);

        roundTripSnapshot();

        readStateEngine.invalidate();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        try {
            assertObject(typeState, 0, 0, null);
            Assert.fail("Should have thrown Exception");
        } catch(NullPointerException expected) { }
    }

    private void addRecord(int intVal, String strVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);

        writeStateEngine.add("TestObject", rec);
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);

        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }
}
