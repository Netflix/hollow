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
package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for HollowObjectTypeReadState.readOrdinal() method.
 * This test demonstrates reading reference field ordinals from object records.
 */
public class HollowObjectTypeReadStateOrdinalTest extends AbstractStateEngineTest {

    private HollowObjectSchema parentSchema;
    private HollowObjectSchema childSchema;

    @Before
    public void setUp() {
        // Define a child type
        childSchema = new HollowObjectSchema("Child", 2);
        childSchema.addField("id", FieldType.INT);
        childSchema.addField("name", FieldType.STRING);

        // Define a parent type with reference to child
        parentSchema = new HollowObjectSchema("Parent", 3);
        parentSchema.addField("id", FieldType.INT);
        parentSchema.addField("title", FieldType.STRING);
        parentSchema.addField("child", FieldType.REFERENCE, "Child");

        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(childSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(parentSchema));
    }

    @Test
    public void testReadOrdinal_returnsReferencedChildOrdinal() throws IOException {
        // Setup: Create child records
        initWriteStateEngine();

        HollowObjectWriteRecord childRec = new HollowObjectWriteRecord(childSchema);

        // Add first child (will get ordinal 0)
        childRec.reset();
        childRec.setInt("id", 100);
        childRec.setString("name", "Alice");
        int childOrdinal0 = writeStateEngine.add("Child", childRec);

        // Add second child (will get ordinal 1)
        childRec.reset();
        childRec.setInt("id", 200);
        childRec.setString("name", "Bob");
        int childOrdinal1 = writeStateEngine.add("Child", childRec);

        // Add third child (will get ordinal 2)
        childRec.reset();
        childRec.setInt("id", 300);
        childRec.setString("name", "Charlie");
        int childOrdinal2 = writeStateEngine.add("Child", childRec);

        // Create parent records referencing children
        HollowObjectWriteRecord parentRec = new HollowObjectWriteRecord(parentSchema);

        // Parent 0 -> Child 1
        parentRec.reset();
        parentRec.setInt("id", 0xE0000000);
        parentRec.setString("title", "Parent One");
        parentRec.setReference("child", childOrdinal1);
        writeStateEngine.add("Parent", parentRec);

        // Parent 1 -> Child 2
        parentRec.reset();
        parentRec.setInt("id", 2);
        parentRec.setString("title", "Parent Two");
        parentRec.setReference("child", childOrdinal2);
        writeStateEngine.add("Parent", parentRec);

        // Parent 2 -> Child 0
        parentRec.reset();
        parentRec.setInt("id", 3);
        parentRec.setString("title", "Parent Three");
        parentRec.setReference("child", childOrdinal0);
        writeStateEngine.add("Parent", parentRec);

        // Round trip to read state
        roundTripSnapshot();

        // Get the read state
        HollowObjectTypeReadState parentReadState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Parent");

        // Find the field index for "child" reference field
        int childFieldIndex = parentReadState.getSchema().getPosition("child");

        // Test: Read the ordinal of the referenced child for each parent


        int testValue = parentReadState.readInt(0, 0);

        System.out.println("SNAP: " + testValue);
        // Parent ordinal 0 should reference child ordinal 1
        int referencedChildOrdinal0 = parentReadState.readOrdinal(0, childFieldIndex);
        assertEquals("Parent 0 should reference Child 1", childOrdinal1, referencedChildOrdinal0);
        assertEquals("Parent 0 should reference Child 1 (expected: 1)", 1, referencedChildOrdinal0);

        // Parent ordinal 1 should reference child ordinal 2
        int referencedChildOrdinal1 = parentReadState.readOrdinal(1, childFieldIndex);
        assertEquals("Parent 1 should reference Child 2", childOrdinal2, referencedChildOrdinal1);
        assertEquals("Parent 1 should reference Child 2 (expected: 2)", 2, referencedChildOrdinal1);

        // Parent ordinal 2 should reference child ordinal 0
        int referencedChildOrdinal2 = parentReadState.readOrdinal(2, childFieldIndex);
        assertEquals("Parent 2 should reference Child 0", childOrdinal0, referencedChildOrdinal2);
        assertEquals("Parent 2 should reference Child 0 (expected: 0)", 0, referencedChildOrdinal2);

        // Verify they're all different
        assertNotEquals(referencedChildOrdinal0, referencedChildOrdinal1);
        assertNotEquals(referencedChildOrdinal1, referencedChildOrdinal2);
        assertNotEquals(referencedChildOrdinal0, referencedChildOrdinal2);
    }

    @Test
    public void testReadOrdinal_withLargeOrdinalValues() throws IOException {
        // Setup: Create many child records to test larger ordinal values
        initWriteStateEngine();

        HollowObjectWriteRecord childRec = new HollowObjectWriteRecord(childSchema);

        // Add 1000 child records
        int numChildren = 1000;
        for (int i = 0; i < numChildren; i++) {
            childRec.reset();
            childRec.setInt("id", i);
            childRec.setString("name", "Child_" + i);
            writeStateEngine.add("Child", childRec);
        }

        // Create parent records referencing specific children
        HollowObjectWriteRecord parentRec = new HollowObjectWriteRecord(parentSchema);

        // Parent 0 -> Child 100
        parentRec.reset();
        parentRec.setInt("id", 1);
        parentRec.setString("title", "Parent One");
        parentRec.setReference("child", 100);
        writeStateEngine.add("Parent", parentRec);

        // Parent 1 -> Child 500
        parentRec.reset();
        parentRec.setInt("id", 2);
        parentRec.setString("title", "Parent Two");
        parentRec.setReference("child", 500);
        writeStateEngine.add("Parent", parentRec);

        // Parent 2 -> Child 999
        parentRec.reset();
        parentRec.setInt("id", 3);
        parentRec.setString("title", "Parent Three");
        parentRec.setReference("child", 999);
        writeStateEngine.add("Parent", parentRec);

        // Round trip to read state
        roundTripSnapshot();

        // Get the read state
        HollowObjectTypeReadState parentReadState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Parent");

        // Find the field index for "child" reference field
        int childFieldIndex = parentReadState.getSchema().getPosition("child");

        // Test: Verify large ordinal values are preserved
        assertEquals("Parent 0 should reference Child 100",
                     100, parentReadState.readOrdinal(0, childFieldIndex));
        assertEquals("Parent 1 should reference Child 500",
                     500, parentReadState.readOrdinal(1, childFieldIndex));
        assertEquals("Parent 2 should reference Child 999",
                     999, parentReadState.readOrdinal(2, childFieldIndex));
    }

    @Test
    public void testReadOrdinal_withNullReference() throws IOException {
        // Setup: Create a parent with null child reference
        initWriteStateEngine();

        HollowObjectWriteRecord childRec = new HollowObjectWriteRecord(childSchema);

        // Add one child
        childRec.reset();
        childRec.setInt("id", 100);
        childRec.setString("name", "Alice");
        writeStateEngine.add("Child", childRec);

        // Create parent with null reference
        HollowObjectWriteRecord parentRec = new HollowObjectWriteRecord(parentSchema);

        parentRec.reset();
        parentRec.setInt("id", 1);
        parentRec.setString("title", "Parent With No Child");
        // Don't set child reference - leave it null
        writeStateEngine.add("Parent", parentRec);

        // Round trip to read state
        roundTripSnapshot();

        // Get the read state
        HollowObjectTypeReadState parentReadState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Parent");

        // Find the field index for "child" reference field
        int childFieldIndex = parentReadState.getSchema().getPosition("child");

        // Test: Null reference should return ORDINAL_NONE (-1)
        int referencedChildOrdinal = parentReadState.readOrdinal(0, childFieldIndex);
        assertEquals("Null reference should return ORDINAL_NONE (-1)",
                     -1, referencedChildOrdinal);
    }
}
