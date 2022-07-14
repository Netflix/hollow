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

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectExactBitBoundaryEdgeCaseTest extends AbstractStateEngineTest {

    private HollowObjectSchema schema;
    private HollowObjectSchema schema2;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 3);
        schema.addField("float1", FieldType.FLOAT);
        schema.addField("float2", FieldType.FLOAT);
        schema.addField("unpopulatedField", FieldType.INT);

        schema2 = new HollowObjectSchema("TestObject2", 3);
        schema2.addField("int1", FieldType.INT);
        schema2.addField("int2", FieldType.INT);
        schema2.addField("unpopulatedField", FieldType.INT);

        super.setUp();
    }

    @Test
    public void unpopulatedFieldOnSegmentBoundary() throws IOException {
        for(int i = 0; i < 4094; i++) {
            addRecord((float) i);
        }

        for(int i = 0; i < 992; i++) {
            addRecord(i);
        }

        roundTripSnapshot();

        for(int i = 0; i < 4096; i++) {
            addRecord((float) i);
        }

        for(int i = 0; i < 993; i++) {
            addRecord(i);
        }

        roundTripDelta();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess("TestObject");
        typeState.readInt(4096, 2);

        typeState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess("TestObject2");
        typeState.readInt(992, 2);

    }

    private void addRecord(float value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setFloat("float1", value);
        rec.setFloat("float2", value);
        writeStateEngine.add("TestObject", rec);
    }

    private void addRecord(int value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema2);
        rec.setInt("int1", value);
        rec.setInt("int2", 1047552);
        writeStateEngine.add("TestObject2", rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema2));
    }

}
