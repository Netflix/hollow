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

public class HollowObjectLargeFieldTest extends AbstractStateEngineTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 3);
        schema.addField("longField", FieldType.LONG);
        schema.addField("intField", FieldType.INT);
        schema.addField("doubleField", FieldType.DOUBLE);

        super.setUp();
    }

    @Test
    public void test() throws IOException {
        addRecord(1, 1, 2.53D);
        addRecord(100, 100, 3523456.3252352456346D);

        roundTripSnapshot();

        addRecord(100, 100, 3523456.3252352456346D);
        addRecord(Long.MIN_VALUE, Integer.MIN_VALUE, Double.MIN_VALUE);
        addRecord(200, 200, 1.00003D);

        roundTripDelta();

        assertObject(1, 100, 100, 3523456.3252352456346D);
        assertObject(2, Long.MIN_VALUE, Integer.MIN_VALUE, Double.MIN_VALUE);
        assertObject(3, 200, 200, 1.00003D);

        addRecord(Long.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE);
        addRecord(100, 100, 3523456.3252352456346D);
        addRecord(Long.MIN_VALUE, Integer.MIN_VALUE, Double.MIN_VALUE);
        addRecord(200, 200, 1.00003D);

        roundTripDelta();

        assertObject(0, Long.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE);
        assertObject(1, 100, 100, 3523456.3252352456346D);
        assertObject(2, Long.MIN_VALUE, Integer.MIN_VALUE, Double.MIN_VALUE);
        assertObject(3, 200, 200, 1.00003D);

    }

    private void addRecord(long l, int i, double d) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setLong("longField", l);
        rec.setInt("intField", i);
        rec.setDouble("doubleField", d);

        writeStateEngine.add("TestObject", rec);
    }

    private void assertObject(int ordinal, long l, int i, double d) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);

        Assert.assertEquals(l, obj.getLong("longField"));
        Assert.assertEquals(i, obj.getInt("intField"));
        Assert.assertTrue(d == obj.getDouble("doubleField"));
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

}
