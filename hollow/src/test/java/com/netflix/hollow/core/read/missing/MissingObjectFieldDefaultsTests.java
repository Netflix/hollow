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
package com.netflix.hollow.core.read.missing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class MissingObjectFieldDefaultsTests extends AbstractStateEngineTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", FieldType.INT);
        schema.addField("f2", FieldType.STRING);

        super.setUp();
    }

    @Test
    public void testDefaultMissingObjectValue() throws IOException {
        addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");

        roundTripSnapshot();

        GenericHollowObject obj = (GenericHollowObject) GenericHollowRecordHelper.instantiate(readStateEngine, "TestObject", 1);

        assertEquals(2, obj.getInt("f1"));
        assertEquals("two", obj.getString("f2"));
        assertEquals(Long.MIN_VALUE, obj.getLong("f3"));
        assertEquals(Integer.MIN_VALUE, obj.getInt("f4"));
        assertTrue(Float.isNaN(obj.getFloat("f5")));
        assertTrue(Double.isNaN(obj.getDouble("f6")));
        assertEquals(null, obj.getString("f7"));
        assertFalse(obj.isStringFieldEqual("f7", "not-null"));
        assertTrue(obj.isStringFieldEqual("f7", null));
        assertEquals(null, obj.getBytes("f8"));
        assertTrue(obj.isNull("f9"));
        assertFalse(obj.getBoolean("f10"));
    }


    private void addRecord(int intVal, String strVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);

        writeStateEngine.add("TestObject", rec);
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }


}
