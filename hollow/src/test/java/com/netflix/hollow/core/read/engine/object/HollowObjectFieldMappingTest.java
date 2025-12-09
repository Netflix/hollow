/*
 *  Copyright 2016-2025 Netflix, Inc.
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

import com.netflix.hollow.core.schema.HollowObjectSchema;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for HollowObjectFieldMapping.
 */
public class HollowObjectFieldMappingTest {

    @Test
    public void testMappingWithNoSchemaChanges() {
        // Create identical schemas
        HollowObjectSchema schema = createSchema("TestType", "field1", "field2", "field3");
        HollowObjectSchema identicalSchema = createSchema("TestType", "field1", "field2", "field3");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            schema,           // target
            identicalSchema,  // from
            schema            // delta
        );

        // Should have identity mapping (no changes)
        Assert.assertFalse("Should not have schema changes", mapping.hasSchemaChanges());

        // All fields should map to themselves
        for (int i = 0; i < schema.numFields(); i++) {
            Assert.assertEquals("Field " + i + " should map to itself in from schema",
                i, mapping.getFromFieldIndex(i));
            Assert.assertEquals("Field " + i + " should map to itself in delta schema",
                i, mapping.getDeltaFieldIndex(i));
            Assert.assertFalse("Field " + i + " should not be new",
                mapping.isNewField(i));
        }
    }

    @Test
    public void testMappingWithAddedFields() {
        // From schema has 2 fields
        HollowObjectSchema fromSchema = createSchema("TestType", "field1", "field2");

        // Target schema has 3 fields (field3 is new)
        HollowObjectSchema targetSchema = createSchema("TestType", "field1", "field2", "field3");

        // Delta schema has all 3 fields
        HollowObjectSchema deltaSchema = createSchema("TestType", "field1", "field2", "field3");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            targetSchema,
            fromSchema,
            deltaSchema
        );

        // Should detect schema changes
        Assert.assertTrue("Should have schema changes", mapping.hasSchemaChanges());

        // Existing fields should map correctly
        Assert.assertEquals("field1 should map to index 0 in from schema",
            0, mapping.getFromFieldIndex(0));
        Assert.assertEquals("field2 should map to index 1 in from schema",
            1, mapping.getFromFieldIndex(1));
        Assert.assertFalse("field1 should not be new", mapping.isNewField(0));
        Assert.assertFalse("field2 should not be new", mapping.isNewField(1));

        // New field should return -1 for from schema
        Assert.assertEquals("field3 should not exist in from schema",
            -1, mapping.getFromFieldIndex(2));
        Assert.assertTrue("field3 should be new", mapping.isNewField(2));

        // All fields should exist in delta schema
        Assert.assertEquals("field1 should map to index 0 in delta schema",
            0, mapping.getDeltaFieldIndex(0));
        Assert.assertEquals("field2 should map to index 1 in delta schema",
            1, mapping.getDeltaFieldIndex(1));
        Assert.assertEquals("field3 should map to index 2 in delta schema",
            2, mapping.getDeltaFieldIndex(2));
    }

    @Test
    public void testMappingWithFieldReordering() {
        // From schema: field1, field2, field3
        HollowObjectSchema fromSchema = createSchema("TestType", "field1", "field2", "field3");

        // Target schema: field3, field1, field2 (reordered)
        HollowObjectSchema targetSchema = createSchema("TestType", "field3", "field1", "field2");

        // Delta schema: matches target
        HollowObjectSchema deltaSchema = createSchema("TestType", "field3", "field1", "field2");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            targetSchema,
            fromSchema,
            deltaSchema
        );

        // Should NOT have schema changes (just reordering, no new fields)
        Assert.assertFalse("Should not have schema changes for reordering alone",
            mapping.hasSchemaChanges());

        // Check mapping from target to from schema (by name, not position)
        Assert.assertEquals("field3 (target index 0) should map to from index 2",
            2, mapping.getFromFieldIndex(0));
        Assert.assertEquals("field1 (target index 1) should map to from index 0",
            0, mapping.getFromFieldIndex(1));
        Assert.assertEquals("field2 (target index 2) should map to from index 1",
            1, mapping.getFromFieldIndex(2));

        // All fields should map to themselves in delta (same order)
        Assert.assertEquals("field3 should map to delta index 0",
            0, mapping.getDeltaFieldIndex(0));
        Assert.assertEquals("field1 should map to delta index 1",
            1, mapping.getDeltaFieldIndex(1));
        Assert.assertEquals("field2 should map to delta index 2",
            2, mapping.getDeltaFieldIndex(2));

        // No fields should be new
        Assert.assertFalse("field3 should not be new", mapping.isNewField(0));
        Assert.assertFalse("field1 should not be new", mapping.isNewField(1));
        Assert.assertFalse("field2 should not be new", mapping.isNewField(2));
    }

    @Test
    public void testIsNewFieldDetection() {
        // From schema has 2 fields
        HollowObjectSchema fromSchema = createSchema("TestType", "oldField1", "oldField2");

        // Target schema has 4 fields (2 old, 2 new)
        HollowObjectSchema targetSchema = createSchema("TestType",
            "oldField1", "newField1", "oldField2", "newField2");

        // Delta schema has all 4 fields
        HollowObjectSchema deltaSchema = createSchema("TestType",
            "oldField1", "newField1", "oldField2", "newField2");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            targetSchema,
            fromSchema,
            deltaSchema
        );

        // Should detect schema changes
        Assert.assertTrue("Should have schema changes", mapping.hasSchemaChanges());

        // Check which fields are new
        Assert.assertFalse("oldField1 should not be new", mapping.isNewField(0));
        Assert.assertTrue("newField1 should be new", mapping.isNewField(1));
        Assert.assertFalse("oldField2 should not be new", mapping.isNewField(2));
        Assert.assertTrue("newField2 should be new", mapping.isNewField(3));

        // Verify mappings
        Assert.assertEquals(0, mapping.getFromFieldIndex(0)); // oldField1 -> oldField1
        Assert.assertEquals(-1, mapping.getFromFieldIndex(1)); // newField1 -> not in from
        Assert.assertEquals(1, mapping.getFromFieldIndex(2)); // oldField2 -> oldField2
        Assert.assertEquals(-1, mapping.getFromFieldIndex(3)); // newField2 -> not in from
    }

    @Test
    public void testMappingWithMissingFieldInDelta() {
        // From schema has 3 fields
        HollowObjectSchema fromSchema = createSchema("TestType", "field1", "field2", "field3");

        // Target schema has same 3 fields
        HollowObjectSchema targetSchema = createSchema("TestType", "field1", "field2", "field3");

        // Delta schema only has 2 fields (field2 is missing in delta)
        HollowObjectSchema deltaSchema = createSchema("TestType", "field1", "field3");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            targetSchema,
            fromSchema,
            deltaSchema
        );

        // No new fields, so no schema changes
        Assert.assertFalse("Should not have schema changes", mapping.hasSchemaChanges());

        // Check delta mappings
        Assert.assertEquals("field1 should map to delta index 0",
            0, mapping.getDeltaFieldIndex(0));
        Assert.assertEquals("field2 should not be in delta",
            -1, mapping.getDeltaFieldIndex(1));
        Assert.assertEquals("field3 should map to delta index 1",
            1, mapping.getDeltaFieldIndex(2));

        // All fields exist in from schema
        Assert.assertEquals(0, mapping.getFromFieldIndex(0));
        Assert.assertEquals(1, mapping.getFromFieldIndex(1));
        Assert.assertEquals(2, mapping.getFromFieldIndex(2));
    }

    @Test
    public void testComplexMappingScenario() {
        // From schema: A, B, C, D
        HollowObjectSchema fromSchema = createSchema("TestType", "A", "B", "C", "D");

        // Target schema: C, A, E, B, F (removed D, added E and F, reordered)
        HollowObjectSchema targetSchema = createSchema("TestType", "C", "A", "E", "B", "F");

        // Delta schema: matches target
        HollowObjectSchema deltaSchema = createSchema("TestType", "C", "A", "E", "B", "F");

        HollowObjectFieldMapping mapping = new HollowObjectFieldMapping(
            targetSchema,
            fromSchema,
            deltaSchema
        );

        // Should detect schema changes (new fields added)
        Assert.assertTrue("Should have schema changes", mapping.hasSchemaChanges());

        // Verify target -> from mappings
        Assert.assertEquals("C (target 0) -> from 2", 2, mapping.getFromFieldIndex(0));
        Assert.assertEquals("A (target 1) -> from 0", 0, mapping.getFromFieldIndex(1));
        Assert.assertEquals("E (target 2) -> not in from", -1, mapping.getFromFieldIndex(2));
        Assert.assertEquals("B (target 3) -> from 1", 1, mapping.getFromFieldIndex(3));
        Assert.assertEquals("F (target 4) -> not in from", -1, mapping.getFromFieldIndex(4));

        // Verify new field detection
        Assert.assertFalse(mapping.isNewField(0)); // C
        Assert.assertFalse(mapping.isNewField(1)); // A
        Assert.assertTrue(mapping.isNewField(2));  // E
        Assert.assertFalse(mapping.isNewField(3)); // B
        Assert.assertTrue(mapping.isNewField(4));  // F
    }

    /**
     * Helper method to create a schema with the given field names.
     * All fields are INT type for simplicity.
     */
    private HollowObjectSchema createSchema(String typeName, String... fieldNames) {
        HollowObjectSchema schema = new HollowObjectSchema(typeName, fieldNames.length);
        for (String fieldName : fieldNames) {
            schema.addField(fieldName, HollowObjectSchema.FieldType.INT);
        }
        return schema;
    }
}
