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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowSchemaDiffTest {

    @Test
    public void testComputeDiffWithFieldAdditions() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 4);
        newSchema.addField("field1", FieldType.INT);
        newSchema.addField("field2", FieldType.STRING);
        newSchema.addField("field3", FieldType.LONG);
        newSchema.addField("field4", FieldType.BOOLEAN);

        HollowSchemaDiff diff = HollowSchemaDiff.compute(oldSchema, newSchema);

        Assert.assertEquals("TestType", diff.getTypeName());
        Assert.assertEquals(2, diff.getAddedFields().size());

        Assert.assertEquals("field3", diff.getAddedFields().get(0).getFieldName());
        Assert.assertEquals(FieldType.LONG, diff.getAddedFields().get(0).getFieldType());

        Assert.assertEquals("field4", diff.getAddedFields().get(1).getFieldName());
        Assert.assertEquals(FieldType.BOOLEAN, diff.getAddedFields().get(1).getFieldType());
    }

    @Test
    public void testComputeDiffWithNoChanges() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("field1", FieldType.INT);
        newSchema.addField("field2", FieldType.STRING);

        HollowSchemaDiff diff = HollowSchemaDiff.compute(oldSchema, newSchema);

        Assert.assertTrue(diff.isEmpty());
        Assert.assertEquals(0, diff.getAddedFields().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeDiffRejectsFieldRemoval() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 3);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);
        oldSchema.addField("field3", FieldType.LONG);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("field1", FieldType.INT);
        newSchema.addField("field2", FieldType.STRING);

        HollowSchemaDiff.compute(oldSchema, newSchema);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeDiffRejectsFieldTypeChange() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("field1", FieldType.LONG); // Changed from INT
        newSchema.addField("field2", FieldType.STRING);

        HollowSchemaDiff.compute(oldSchema, newSchema);
    }

    @Test
    public void testComputeDiffWithReferenceField() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("Movie", 1);
        oldSchema.addField("title", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("Movie", 2);
        newSchema.addField("title", FieldType.STRING);
        newSchema.addField("director", FieldType.REFERENCE, "Person");

        HollowSchemaDiff diff = HollowSchemaDiff.compute(oldSchema, newSchema);

        Assert.assertEquals(1, diff.getAddedFields().size());
        Assert.assertEquals("director", diff.getAddedFields().get(0).getFieldName());
        Assert.assertEquals(FieldType.REFERENCE, diff.getAddedFields().get(0).getFieldType());
        Assert.assertEquals("Person", diff.getAddedFields().get(0).getReferencedType());
    }

    @Test
    public void testRoundTripEncoding() throws IOException {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 5);
        newSchema.addField("field1", FieldType.INT);
        newSchema.addField("field2", FieldType.STRING);
        newSchema.addField("field3", FieldType.LONG);
        newSchema.addField("field4", FieldType.BOOLEAN);
        newSchema.addField("field5", FieldType.REFERENCE, "OtherType");

        HollowSchemaDiff original = HollowSchemaDiff.compute(oldSchema, newSchema);

        // Write to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        original.writeTo(baos);
        byte[] bytes = baos.toByteArray();

        // Read back
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        HollowSchemaDiff decoded = HollowSchemaDiff.readFrom(bais, "TestType");

        // Verify
        Assert.assertEquals(original.getTypeName(), decoded.getTypeName());
        Assert.assertEquals(original.getAddedFields().size(), decoded.getAddedFields().size());

        for (int i = 0; i < original.getAddedFields().size(); i++) {
            HollowSchemaDiff.FieldAddition origField = original.getAddedFields().get(i);
            HollowSchemaDiff.FieldAddition decodedField = decoded.getAddedFields().get(i);

            Assert.assertEquals(origField.getFieldName(), decodedField.getFieldName());
            Assert.assertEquals(origField.getFieldType(), decodedField.getFieldType());
            Assert.assertEquals(origField.getReferencedType(), decodedField.getReferencedType());
        }
    }

    @Test
    public void testApplyDiff() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema expectedSchema = new HollowObjectSchema("TestType", 4);
        expectedSchema.addField("field1", FieldType.INT);
        expectedSchema.addField("field2", FieldType.STRING);
        expectedSchema.addField("field3", FieldType.LONG);
        expectedSchema.addField("field4", FieldType.REFERENCE, "OtherType");

        HollowSchemaDiff diff = HollowSchemaDiff.compute(oldSchema, expectedSchema);
        HollowObjectSchema appliedSchema = diff.apply(oldSchema);

        // Verify applied schema matches expected
        Assert.assertEquals(expectedSchema.numFields(), appliedSchema.numFields());
        for (int i = 0; i < expectedSchema.numFields(); i++) {
            Assert.assertEquals(expectedSchema.getFieldName(i), appliedSchema.getFieldName(i));
            Assert.assertEquals(expectedSchema.getFieldType(i), appliedSchema.getFieldType(i));
            if (expectedSchema.getFieldType(i) == FieldType.REFERENCE) {
                Assert.assertEquals(expectedSchema.getReferencedType(i), appliedSchema.getReferencedType(i));
            }
        }
    }

    @Test
    public void testApplyEmptyDiff() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("field1", FieldType.INT);
        oldSchema.addField("field2", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("field1", FieldType.INT);
        newSchema.addField("field2", FieldType.STRING);

        HollowSchemaDiff diff = HollowSchemaDiff.compute(oldSchema, newSchema);
        HollowObjectSchema appliedSchema = diff.apply(oldSchema);

        // Schema should be unchanged
        Assert.assertEquals(oldSchema.numFields(), appliedSchema.numFields());
        for (int i = 0; i < oldSchema.numFields(); i++) {
            Assert.assertEquals(oldSchema.getFieldName(i), appliedSchema.getFieldName(i));
            Assert.assertEquals(oldSchema.getFieldType(i), appliedSchema.getFieldType(i));
        }
    }

    @Test
    public void testRoundTripWithEmptyDiff() throws IOException {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("field1", FieldType.INT);
        schema.addField("field2", FieldType.STRING);

        HollowSchemaDiff emptyDiff = HollowSchemaDiff.compute(schema, schema);

        // Write to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        emptyDiff.writeTo(baos);
        byte[] bytes = baos.toByteArray();

        // Read back
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        HollowSchemaDiff decoded = HollowSchemaDiff.readFrom(bais, "TestType");

        Assert.assertTrue(decoded.isEmpty());
        Assert.assertEquals(0, decoded.getAddedFields().size());
    }
}
