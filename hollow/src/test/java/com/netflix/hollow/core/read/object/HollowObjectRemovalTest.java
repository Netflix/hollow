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

public class HollowObjectRemovalTest extends AbstractStateEngineTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", FieldType.INT);
        schema.addField("f2", FieldType.INT);

        super.setUp();
    }

    @Test
    public void removeRecordWithLargeFieldValueAtEndOfRecord() throws IOException {
        addRecord(10, 251);
        addRecord(15, 15);
        addRecord(20, 1021);
        addRecord(30, 251);

        roundTripSnapshot();

        addRecord(10, 251);
        addRecord(30, 251);

        roundTripDelta();

        addRecord(10, 251);
        addRecord(30, 251);
        addRecord(1, 1);

        roundTripDelta();

        assertObject(0, 10, 251);
        assertObject(3, 30, 251);
    }

    private void assertObject(int ordinal, int int1, int int2) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);

        Assert.assertEquals(int1, obj.getInt("f1"));
        Assert.assertEquals(int2, obj.getInt("f2"));
    }

    private void addRecord(int int1, int int2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setInt("f1", int1);
        rec.setInt("f2", int2);

        writeStateEngine.add("TestObject", rec);
    }


    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

}
