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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import org.junit.Assert;
import org.junit.Test;
import java.util.Set;

public class HollowSchemaComparatorTest {

    @Test
    public void testFindAddedFields() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 3);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);
        newSchema.addField("email", FieldType.STRING);

        Set<String> addedFields = HollowSchemaComparator.findAddedFields(oldSchema, newSchema);

        Assert.assertEquals(1, addedFields.size());
        Assert.assertTrue(addedFields.contains("email"));
    }

    @Test
    public void testFindAddedFieldsWithNoChanges() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);

        Set<String> addedFields = HollowSchemaComparator.findAddedFields(oldSchema, newSchema);

        Assert.assertEquals(0, addedFields.size());
    }

    @Test
    public void testFindAddedFieldsWithMultipleNewFields() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 4);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);
        newSchema.addField("email", FieldType.STRING);
        newSchema.addField("age", FieldType.INT);

        Set<String> addedFields = HollowSchemaComparator.findAddedFields(oldSchema, newSchema);

        Assert.assertEquals(2, addedFields.size());
        Assert.assertTrue(addedFields.contains("email"));
        Assert.assertTrue(addedFields.contains("age"));
    }

    @Test
    public void testFindAddedFieldsWithReorderedFields() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 3);
        newSchema.addField("name", FieldType.STRING);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("email", FieldType.STRING);

        Set<String> addedFields = HollowSchemaComparator.findAddedFields(oldSchema, newSchema);

        Assert.assertEquals(1, addedFields.size());
        Assert.assertTrue(addedFields.contains("email"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAddedFieldsWithNullOldSchema() {
        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 2);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);

        HollowSchemaComparator.findAddedFields(null, newSchema);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAddedFieldsWithNullNewSchema() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowSchemaComparator.findAddedFields(oldSchema, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAddedFieldsWithMismatchedSchemaNames() {
        HollowObjectSchema oldSchema = new HollowObjectSchema("TypeA", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectSchema newSchema = new HollowObjectSchema("TypeB", 2);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);

        HollowSchemaComparator.findAddedFields(oldSchema, newSchema);
    }
}
