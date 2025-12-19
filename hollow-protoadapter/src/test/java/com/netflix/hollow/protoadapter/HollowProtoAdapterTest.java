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
package com.netflix.hollow.protoadapter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Functional test for HollowProtoAdapter using dynamically loaded Protocol Buffer classes.
 */
public class HollowProtoAdapterTest {

    private Message personMessage;
    private URLClassLoader protoClassLoader;

    @Before
    public void setUp() throws Exception {
        // Load the compiled proto classes dynamically
        File protoClassesDir = new File("build/test-proto-bin");
        assertTrue("Proto classes directory should exist: " + protoClassesDir.getAbsolutePath(),
                   protoClassesDir.exists());

        // Also need main classes for HollowOptions
        File mainClassesDir = new File("build/classes/java/main");
        assertTrue("Main classes directory should exist: " + mainClassesDir.getAbsolutePath(),
                   mainClassesDir.exists());

        protoClassLoader = new URLClassLoader(
            new URL[]{protoClassesDir.toURI().toURL(), mainClassesDir.toURI().toURL()},
            this.getClass().getClassLoader()
        );

        // Load the generated proto classes using reflection
        Class<?> personClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Person");
        // Get builder methods
        Method newBuilderPerson = personClass.getMethod("newBuilder");
        // Create Person
        Message.Builder personBuilder = (Message.Builder) newBuilderPerson.invoke(null);

        // load the Json from the test_person.json resource
        InputStream personJsonStream = getClass().getClassLoader().getResourceAsStream("test_person.json");
        assertNotNull("test_person.json resource should exist", personJsonStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(personJsonStream))) {
            String personJson = reader.lines().collect(Collectors.joining("\n"));

            // Parse the JSON array and extract the first person
            // The JSON file now contains an array of people, so we need to extract the first one
            String trimmed = personJson.trim();
            if (trimmed.startsWith("[")) {
                // Find the first complete JSON object in the array
                int firstBrace = trimmed.indexOf('{');
                int depth = 0;
                int endBrace = -1;
                for (int i = firstBrace; i < trimmed.length(); i++) {
                    char c = trimmed.charAt(i);
                    if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0) {
                            endBrace = i;
                            break;
                        }
                    }
                }
                if (endBrace > firstBrace) {
                    personJson = trimmed.substring(firstBrace, endBrace + 1);
                }
            }

            // load the JSON data into the proto builder
            JsonFormat.parser().merge(personJson, personBuilder);
        }
        personMessage = personBuilder.build();
    }

    @Test
    public void testProcessPersonMessage() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();

        // Use HollowMessageMapper like HollowObjectMapper - automatically infers schemas and type name
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);
        int ordinal = mapper.add(personMessage);
        assertTrue("Ordinal should be non-negative", ordinal >= 0);

        // Round-trip the data
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // Verify Person data
        HollowObjectTypeReadState personState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Person");
        assertNotNull("Person state should exist", personState);

        assertEquals(123, personState.readInt(ordinal, 0));
        assertEquals("John Doe", personState.readString(ordinal, 1));
        assertEquals("john@example.com", personState.readString(ordinal, 2));
        assertEquals("ACTIVE", personState.readString(ordinal, 4)); // status enum

        // Verify Address reference
        int addressOrdinal = personState.readOrdinal(ordinal, 5);
        assertTrue("Address ordinal should be valid", addressOrdinal >= 0);

        HollowObjectTypeReadState addressState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Address");
        assertEquals("123 Main St", addressState.readString(addressOrdinal, 0));
        assertEquals("San Francisco", addressState.readString(addressOrdinal, 1));
        assertEquals("CA", addressState.readString(addressOrdinal, 2));
        assertEquals(94105, addressState.readInt(addressOrdinal, 3));
    }

    @Test
    public void testExtractPrimaryKeyWithOption() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);

        // Extract primary key from Person message which has hollow_primary_key = "id"
        com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey primaryKey =
            mapper.extractPrimaryKey(personMessage);

        assertNotNull("Primary key should not be null", primaryKey);
        assertEquals("Person", primaryKey.getType());

        // Person proto has: option (com.netflix.hollow.hollow_primary_key) = "id";
        // So primary key should only contain the id field (123), not all scalar fields
        Object[] keyFields = primaryKey.getKey();
        assertEquals("Primary key should contain exactly 1 field", 1, keyFields.length);
        assertEquals("Primary key field should be id=123", 123, keyFields[0]);
    }

    @Test
    public void testHollowTypeNameOption() throws Exception {
        // Load Product proto class
        File protoClassesDir = new File("build/test-proto-bin");
        File mainClassesDir = new File("build/classes/java/main");
        URLClassLoader protoClassLoader = new URLClassLoader(
            new URL[]{protoClassesDir.toURI().toURL(), mainClassesDir.toURI().toURL()},
            this.getClass().getClassLoader()
        );
        Class<?> productClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Product");
        Method newBuilder = productClass.getMethod("newBuilder");
        Message.Builder productBuilder = (Message.Builder) newBuilder.invoke(null);

        // Set product fields
        productBuilder.setField(
            productBuilder.getDescriptorForType().findFieldByName("id"), 1);
        productBuilder.setField(
            productBuilder.getDescriptorForType().findFieldByName("name"), "Test Product");
        Message product = productBuilder.build();

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);
        int ordinal = mapper.add(product);

        // Verify schema structure
        HollowObjectSchema productSchema = (HollowObjectSchema) writeStateEngine.getSchema("Product");
        assertNotNull("Product schema should exist", productSchema);

        // Field should still be named "name" but reference "ProductTitle" instead of "String"
        int nameFieldPos = productSchema.getPosition("name");
        assertTrue("Field 'name' should exist", nameFieldPos >= 0);
        assertEquals("name field should be REFERENCE",
            HollowObjectSchema.FieldType.REFERENCE,
            productSchema.getFieldType(nameFieldPos));
        assertEquals("name field should reference ProductTitle (namespaced type)",
            "ProductTitle",
            productSchema.getReferencedType(nameFieldPos));

        // Verify ProductTitle wrapper type exists
        HollowObjectSchema productTitleSchema = (HollowObjectSchema) writeStateEngine.getSchema("ProductTitle");
        assertNotNull("ProductTitle wrapper schema should exist", productTitleSchema);
        int valueFieldPos = productTitleSchema.getPosition("value");
        assertTrue("ProductTitle should have 'value' field", valueFieldPos >= 0);
        assertEquals("ProductTitle.value should be STRING",
            HollowObjectSchema.FieldType.STRING,
            productTitleSchema.getFieldType(valueFieldPos));

        // Round-trip and verify data
        writeStateEngine.prepareForWrite();
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // Verify data - read through the wrapper
        HollowObjectTypeReadState productState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Product");
        int productTitleOrdinal = productState.readOrdinal(ordinal, nameFieldPos);
        assertTrue("ProductTitle ordinal should be valid", productTitleOrdinal >= 0);

        HollowObjectTypeReadState productTitleState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("ProductTitle");
        String productName = productTitleState.readString(productTitleOrdinal, valueFieldPos);
        assertEquals("Test Product", productName);
    }

    @Test
    public void testHollowInlineOption() throws Exception {
        // Load Account proto class
        File protoClassesDir = new File("build/test-proto-bin");
        File mainClassesDir = new File("build/classes/java/main");
        URLClassLoader protoClassLoader = new URLClassLoader(
            new URL[]{protoClassesDir.toURI().toURL(), mainClassesDir.toURI().toURL()},
            this.getClass().getClassLoader()
        );
        Class<?> accountClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Account");
        Class<?> int32ValueClass = protoClassLoader.loadClass("com.google.protobuf.Int32Value");
        Class<?> stringValueClass = protoClassLoader.loadClass("com.google.protobuf.StringValue");

        Method newBuilder = accountClass.getMethod("newBuilder");
        Message.Builder accountBuilder = (Message.Builder) newBuilder.invoke(null);

        // Create Int32Value for balance (should be reference)
        Method int32ValueOf = int32ValueClass.getMethod("of", int.class);
        Object balanceValue = int32ValueOf.invoke(null, 1000);

        // Create StringValue for account_type (should be inline due to hollow_inline option)
        Method stringValueOf = stringValueClass.getMethod("of", String.class);
        Object accountTypeValue = stringValueOf.invoke(null, "CHECKING");

        accountBuilder.setField(
            accountBuilder.getDescriptorForType().findFieldByName("account_id"), "ACC123");
        accountBuilder.setField(
            accountBuilder.getDescriptorForType().findFieldByName("region"), "us-west-2");
        accountBuilder.setField(
            accountBuilder.getDescriptorForType().findFieldByName("balance"), balanceValue);
        accountBuilder.setField(
            accountBuilder.getDescriptorForType().findFieldByName("account_type"), accountTypeValue);
        Message account = accountBuilder.build();

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);
        mapper.add(account);

        // Verify schema structure
        HollowObjectSchema accountSchema = (HollowObjectSchema) writeStateEngine.getSchema("Account");
        assertNotNull("Account schema should exist", accountSchema);

        // balance field should be REFERENCE to Integer (boxed int, no inline option)
        // Int32Value unwraps to reference to the underlying int type
        int balanceFieldPos = accountSchema.getPosition("balance");
        assertEquals("balance field should be REFERENCE",
            HollowObjectSchema.FieldType.REFERENCE,
            accountSchema.getFieldType(balanceFieldPos));
        assertEquals("balance field should reference Integer (unwrapped from Int32Value)",
            "Integer",
            accountSchema.getReferencedType(balanceFieldPos));

        // account_type field should be STRING (inlined due to hollow_inline option)
        // StringValue with hollow_inline unwraps to inline String
        int accountTypeFieldPos = accountSchema.getPosition("account_type");
        assertEquals("account_type field should be STRING (inlined)",
            HollowObjectSchema.FieldType.STRING,
            accountSchema.getFieldType(accountTypeFieldPos));

        // Verify no Int32Value or StringValue object schemas exist (they should be unwrapped)
        assertNull("Int32Value schema should not exist (unwrapped)",
            writeStateEngine.getSchema("Int32Value"));
        assertNull("StringValue schema should not exist (unwrapped)",
            writeStateEngine.getSchema("StringValue"));

        // Round-trip and verify data
        writeStateEngine.prepareForWrite();
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowObjectTypeReadState accountState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Account");
        // balance should be a reference ordinal
        int balanceOrdinal = accountState.readOrdinal(0, balanceFieldPos);
        assertTrue("balance should be a reference (ordinal >= 0)", balanceOrdinal >= 0);

        // account_type should be an inline string
        String accountType = accountState.readString(0, accountTypeFieldPos);
        assertEquals("CHECKING", accountType);
    }

    /**
     * Comprehensive test for google.protobuf.Struct, Value, and ListValue handling via lazy schema creation.
     * Tests multiple Person records with diverse metadata structures to validate:
     * <ul>
     *   <li>Lazy schema creation for Struct/Value/ListValue (only created when data appears)</li>
     *   <li>Multiple instances with different field sets in their Struct metadata</li>
     *   <li>Nested Struct values (Struct containing Struct)</li>
     *   <li>Null value handling (google.protobuf.NullValue enum)</li>
     *   <li>ListValue for arrays</li>
     *   <li>Empty metadata and missing metadata fields</li>
     *   <li>hollow_type_name option (employee_code creates EmployeeCode wrapper)</li>
     *   <li>hollow_inline option (priority field inlined as INT)</li>
     *   <li>google.protobuf.*Value as reference vs inline</li>
     * </ul>
     * The Value union schema includes all possible oneof fields (nullValue, numberValue, stringValue,
     * boolValue, structValue, listValue) to accommodate any field having any type at runtime.
     */
    @Test
    public void testGoogleStructAndValueAsReferences() throws Exception {
        // Load Person proto class and google.protobuf types
        Class<?> personClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Person");
        Class<?> structClass = protoClassLoader.loadClass("com.google.protobuf.Struct");
        Class<?> valueClass = protoClassLoader.loadClass("com.google.protobuf.Value");
        Class<?> listValueClass = protoClassLoader.loadClass("com.google.protobuf.ListValue");
        Class<?> int32ValueClass = protoClassLoader.loadClass("com.google.protobuf.Int32Value");

        // Get the Struct.Builder.putFields method for adding fields to the map
        Method structNewBuilder = structClass.getMethod("newBuilder");
        Method structBuilderPutFields = structClass.getDeclaredClasses()[0].getMethod("putFields", String.class, valueClass);
        Method valueNewBuilder = valueClass.getMethod("newBuilder");
        Method personNewBuilder = personClass.getMethod("newBuilder");
        Method listValueNewBuilder = listValueClass.getMethod("newBuilder");
        Method int32ValueNewBuilder = int32ValueClass.getMethod("newBuilder");

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);

        // Person 1: John Doe - mixed metadata types (number, string, boolean)
        Message.Builder person1Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct1Builder = (Message.Builder) structNewBuilder.invoke(null);

        structBuilderPutFields.invoke(struct1Builder, "age",
            createNumberValue(valueNewBuilder, 35.0));
        structBuilderPutFields.invoke(struct1Builder, "department",
            createStringValue(valueNewBuilder, "Engineering"));
        structBuilderPutFields.invoke(struct1Builder, "employeeId",
            createStringValue(valueNewBuilder, "EMP001"));
        structBuilderPutFields.invoke(struct1Builder, "active",
            createBoolValue(valueNewBuilder, true));

        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("id"), 1);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("name"), "John Doe");
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("email"), "john@example.com");
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("metadata"), struct1Builder.build());

        // Test hollow_type_name: employee_code uses EmployeeCode wrapper type
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("employee_code"), "EMP-001");

        // Test google.protobuf.Int32Value as reference (default)
        Message.Builder ageValueBuilder = (Message.Builder) int32ValueNewBuilder.invoke(null);
        ageValueBuilder.setField(ageValueBuilder.getDescriptorForType().findFieldByName("value"), 35);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("age_years"), ageValueBuilder.build());

        // Test hollow_inline: priority should be inlined
        Message.Builder priorityBuilder = (Message.Builder) int32ValueNewBuilder.invoke(null);
        priorityBuilder.setField(priorityBuilder.getDescriptorForType().findFieldByName("value"), 5);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("priority"), priorityBuilder.build());

        mapper.add(person1Builder.build());

        // Person 2: Jane Smith - different metadata fields (no overlap with Person 1)
        Message.Builder person2Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct2Builder = (Message.Builder) structNewBuilder.invoke(null);

        structBuilderPutFields.invoke(struct2Builder, "fullTime",
            createStringValue(valueNewBuilder, "yes"));
        structBuilderPutFields.invoke(struct2Builder, "department",
            createStringValue(valueNewBuilder, "Product"));
        structBuilderPutFields.invoke(struct2Builder, "remote",
            createBoolValue(valueNewBuilder, true));
        structBuilderPutFields.invoke(struct2Builder, "level",
            createStringValue(valueNewBuilder, "Senior"));

        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("id"), 2);
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("name"), "Jane Smith");
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("email"), "jane@example.com");
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("metadata"), struct2Builder.build());
        mapper.add(person2Builder.build());

        // Person 3: Bob Wilson - metadata with nested Struct and null value
        Message.Builder person3Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct3Builder = (Message.Builder) structNewBuilder.invoke(null);

        structBuilderPutFields.invoke(struct3Builder, "retired",
            createBoolValue(valueNewBuilder, true));
        structBuilderPutFields.invoke(struct3Builder, "yearsOfService",
            createNumberValue(valueNewBuilder, 25.0));
        structBuilderPutFields.invoke(struct3Builder, "exitDate",
            createStringValue(valueNewBuilder, "2024-12-31"));

        // Test nested Struct: add "contact" field with nested Struct value
        Message.Builder nestedStructBuilder = (Message.Builder) structNewBuilder.invoke(null);
        structBuilderPutFields.invoke(nestedStructBuilder, "phone",
            createStringValue(valueNewBuilder, "555-0000"));
        structBuilderPutFields.invoke(nestedStructBuilder, "preferred",
            createBoolValue(valueNewBuilder, false));

        Message.Builder nestedStructValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        nestedStructValueBuilder.setField(
            nestedStructValueBuilder.getDescriptorForType().findFieldByName("struct_value"),
            nestedStructBuilder.build());
        structBuilderPutFields.invoke(struct3Builder, "contact", nestedStructValueBuilder.build());

        // Test null value (NullValue enum)
        Message.Builder nullValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        Descriptors.FieldDescriptor nullValueField = nullValueBuilder.getDescriptorForType().findFieldByName("null_value");
        Descriptors.EnumValueDescriptor nullEnumValue = nullValueField.getEnumType().findValueByNumber(0);
        nullValueBuilder.setField(nullValueField, nullEnumValue);
        structBuilderPutFields.invoke(struct3Builder, "middleName", nullValueBuilder.build());

        person3Builder.setField(person3Builder.getDescriptorForType().findFieldByName("id"), 3);
        person3Builder.setField(person3Builder.getDescriptorForType().findFieldByName("name"), "Bob Wilson");
        person3Builder.setField(person3Builder.getDescriptorForType().findFieldByName("email"), "bob@example.com");
        person3Builder.setField(person3Builder.getDescriptorForType().findFieldByName("metadata"), struct3Builder.build());
        mapper.add(person3Builder.build());

        // Person 4: Alice Chen - metadata with ListValue (array)
        Message.Builder person4Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct4Builder = (Message.Builder) structNewBuilder.invoke(null);

        // Create a ListValue for languages array
        Message.Builder listBuilder = (Message.Builder) listValueNewBuilder.invoke(null);
        Method addValues = listBuilder.getClass().getMethod("addValues", valueClass);
        addValues.invoke(listBuilder, createStringValue(valueNewBuilder, "English"));
        addValues.invoke(listBuilder, createStringValue(valueNewBuilder, "Mandarin"));
        addValues.invoke(listBuilder, createStringValue(valueNewBuilder, "Spanish"));

        Message.Builder languagesValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        languagesValueBuilder.setField(
            languagesValueBuilder.getDescriptorForType().findFieldByName("list_value"),
            listBuilder.build());

        structBuilderPutFields.invoke(struct4Builder, "languages", languagesValueBuilder.build());
        structBuilderPutFields.invoke(struct4Builder, "certifications",
            createNumberValue(valueNewBuilder, 5.0));
        structBuilderPutFields.invoke(struct4Builder, "startDate",
            createStringValue(valueNewBuilder, "2020-03-15"));
        structBuilderPutFields.invoke(struct4Builder, "permanent",
            createBoolValue(valueNewBuilder, true));

        person4Builder.setField(person4Builder.getDescriptorForType().findFieldByName("id"), 4);
        person4Builder.setField(person4Builder.getDescriptorForType().findFieldByName("name"), "Alice Chen");
        person4Builder.setField(person4Builder.getDescriptorForType().findFieldByName("email"), "alice@example.com");
        person4Builder.setField(person4Builder.getDescriptorForType().findFieldByName("metadata"), struct4Builder.build());
        mapper.add(person4Builder.build());

        // Person 5: Carlos Rodriguez - empty metadata
        Message.Builder person5Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct5Builder = (Message.Builder) structNewBuilder.invoke(null);

        person5Builder.setField(person5Builder.getDescriptorForType().findFieldByName("id"), 5);
        person5Builder.setField(person5Builder.getDescriptorForType().findFieldByName("name"), "Carlos Rodriguez");
        person5Builder.setField(person5Builder.getDescriptorForType().findFieldByName("email"), "carlos@example.com");
        person5Builder.setField(person5Builder.getDescriptorForType().findFieldByName("metadata"), struct5Builder.build());
        mapper.add(person5Builder.build());

        // Person 6: Diana Park - no metadata field (omitted entirely)
        Message.Builder person6Builder = (Message.Builder) personNewBuilder.invoke(null);

        person6Builder.setField(person6Builder.getDescriptorForType().findFieldByName("id"), 6);
        person6Builder.setField(person6Builder.getDescriptorForType().findFieldByName("name"), "Diana Park");
        person6Builder.setField(person6Builder.getDescriptorForType().findFieldByName("email"), "diana@example.com");
        // Note: metadata field intentionally omitted
        mapper.add(person6Builder.build());

        // Verify schema structure
        HollowObjectSchema personSchema = (HollowObjectSchema) writeStateEngine.getSchema("Person");
        assertNotNull("Person schema should exist", personSchema);

        // metadata field (Struct) should be REFERENCE (like boxed types)
        int metadataFieldPos = personSchema.getPosition("metadata");
        assertEquals("metadata field (Struct) should be REFERENCE",
            HollowObjectSchema.FieldType.REFERENCE,
            personSchema.getFieldType(metadataFieldPos));
        assertEquals("metadata should reference Struct type",
            "Struct",
            personSchema.getReferencedType(metadataFieldPos));

        // Verify hollow_type_name: employee_code should use EmployeeCode wrapper
        int employeeCodePos = personSchema.getPosition("employee_code");
        assertEquals("employee_code should be REFERENCE (namespaced wrapper)",
            HollowObjectSchema.FieldType.REFERENCE,
            personSchema.getFieldType(employeeCodePos));
        assertEquals("employee_code should reference EmployeeCode type",
            "EmployeeCode",
            personSchema.getReferencedType(employeeCodePos));
        assertNotNull("EmployeeCode schema should exist", writeStateEngine.getSchema("EmployeeCode"));

        // Verify google.protobuf.Int32Value as reference (default)
        int ageYearsPos = personSchema.getPosition("age_years");
        assertEquals("age_years should be REFERENCE",
            HollowObjectSchema.FieldType.REFERENCE,
            personSchema.getFieldType(ageYearsPos));
        assertEquals("age_years should reference Integer wrapper",
            "Integer",
            personSchema.getReferencedType(ageYearsPos));

        // Verify hollow_inline: priority should be inlined INT
        int priorityPos = personSchema.getPosition("priority");
        assertEquals("priority with hollow_inline should be INT (inlined)",
            HollowObjectSchema.FieldType.INT,
            personSchema.getFieldType(priorityPos));

        // Verify that Struct and Value schemas exist (they are separate types)
        assertNotNull("Struct schema should exist", writeStateEngine.getSchema("Struct"));
        assertNotNull("Value schema should exist", writeStateEngine.getSchema("Value"));
        assertNotNull("ListValue schema should exist", writeStateEngine.getSchema("ListValue"));

        // Verify Value schema has all expected fields for handling different types
        HollowObjectSchema valueSchema = (HollowObjectSchema) writeStateEngine.getSchema("Value");
        assertTrue("Value should have nullValue field", valueSchema.getPosition("nullValue") >= 0);
        assertTrue("Value should have numberValue field", valueSchema.getPosition("numberValue") >= 0);
        assertTrue("Value should have stringValue field", valueSchema.getPosition("stringValue") >= 0);
        assertTrue("Value should have boolValue field", valueSchema.getPosition("boolValue") >= 0);
        assertTrue("Value should have structValue field", valueSchema.getPosition("structValue") >= 0);
        assertTrue("Value should have listValue field", valueSchema.getPosition("listValue") >= 0);

        // Verify we can have nested Structs (tested in Person 3's metadata.contact field)
        // This demonstrates that the lazy schema creation handles recursive Struct references correctly
    }

    // Helper methods to create Value messages
    private Message createNumberValue(Method valueNewBuilder, double value) throws Exception {
        Message.Builder builder = (Message.Builder) valueNewBuilder.invoke(null);
        builder.setField(builder.getDescriptorForType().findFieldByName("number_value"), value);
        return builder.build();
    }

    private Message createStringValue(Method valueNewBuilder, String value) throws Exception {
        Message.Builder builder = (Message.Builder) valueNewBuilder.invoke(null);
        builder.setField(builder.getDescriptorForType().findFieldByName("string_value"), value);
        return builder.build();
    }

    private Message createBoolValue(Method valueNewBuilder, boolean value) throws Exception {
        Message.Builder builder = (Message.Builder) valueNewBuilder.invoke(null);
        builder.setField(builder.getDescriptorForType().findFieldByName("bool_value"), value);
        return builder.build();
    }

    @Test
    public void testIgnoreListOrdering() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);
        mapper.ignoreListOrdering(); // Enable unordered list mode

        // Create two Person messages with phone_numbers in different orders
        Message.Builder person1Builder = personMessage.toBuilder();
        person1Builder.clearField(person1Builder.getDescriptorForType().findFieldByName("phone_numbers"));
        person1Builder.addRepeatedField(
            person1Builder.getDescriptorForType().findFieldByName("phone_numbers"), "555-1234");
        person1Builder.addRepeatedField(
            person1Builder.getDescriptorForType().findFieldByName("phone_numbers"), "555-5678");
        Message person1 = person1Builder.build();

        Message.Builder person2Builder = personMessage.toBuilder();
        person2Builder.clearField(person2Builder.getDescriptorForType().findFieldByName("phone_numbers"));
        person2Builder.addRepeatedField(
            person2Builder.getDescriptorForType().findFieldByName("phone_numbers"), "555-5678"); // Reversed!
        person2Builder.addRepeatedField(
            person2Builder.getDescriptorForType().findFieldByName("phone_numbers"), "555-1234");
        Message person2 = person2Builder.build();

        // Add both messages
        mapper.add(person1);
        mapper.add(person2);

        // Prepare state engine
        writeStateEngine.prepareForWrite();

        // Round-trip the data
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // Verify Person data - get both person records
        HollowObjectTypeReadState personState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("Person");

        // Both persons should reference the SAME list ordinal
        // because ignoreListOrdering is true and they have the same elements
        int person1PhoneListOrdinal = personState.readOrdinal(0, 3); // phone_numbers field
        int person2PhoneListOrdinal = personState.readOrdinal(1, 3);

        assertEquals("Both persons should reference the same list (deduplicated)",
                     person1PhoneListOrdinal, person2PhoneListOrdinal);
    }

    @Test
    public void testCircularReferenceDetection() throws Exception {
        // Load Node proto class which has a circular reference
        Class<?> nodeClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Node");
        Method newBuilder = nodeClass.getMethod("newBuilder");
        Message.Builder nodeBuilder = (Message.Builder) newBuilder.invoke(null);

        nodeBuilder.setField(
            nodeBuilder.getDescriptorForType().findFieldByName("value"), "root");
        Message node = nodeBuilder.build();

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);

        // Attempting to add a Node message should throw IllegalStateException
        // due to circular reference (Node references itself)
        try {
            mapper.add(node);
            fail("Expected IllegalStateException for circular reference");
        } catch (IllegalStateException e) {
            assertTrue("Error message should mention circular reference",
                e.getMessage().contains("Circular reference detected"));
            assertTrue("Error message should mention Node type",
                e.getMessage().contains("Node"));
        }
    }

    /**
     * Test that lazy schema creation works correctly across multiple snapshot cycles.
     * This tests the concern that schemas created lazily (like Struct/Value) work
     * properly when publishing snapshots and deltas.
     *
     * Cycle 1: Add people WITHOUT metadata (no Struct/Value schemas created)
     * Cycle 2: Add people WITH metadata (triggers lazy schema creation)
     *
     * Verifies that:
     * - Schemas appear correctly in the state engine
     * - Data from both cycles can be read correctly
     * - Delta transitions work properly
     */
    @Test
    public void testLazySchemaCreationAcrossCycles() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);

        // Get Person class and builder using the proto class loader
        Class<?> personClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Person");
        Method newBuilder = personClass.getMethod("newBuilder");
        Class<?> builderClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Person$Builder");
        Method build = builderClass.getMethod("build");

        // CYCLE 1: Add person WITHOUT metadata - no Struct/Value schemas yet
        Message.Builder person1Builder = (Message.Builder) newBuilder.invoke(null);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("id"), 100);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("name"), "Test Person 1");
        Message person1 = (Message) build.invoke(person1Builder);

        mapper.add(person1);

        // Verify Struct/Value schemas don't exist yet (lazy creation)
        assertNull("Struct schema should not exist yet", writeStateEngine.getSchema("Struct"));
        assertNull("Value schema should not exist yet", writeStateEngine.getSchema("Value"));
        assertNull("MapOfStringToValue schema should not exist yet", writeStateEngine.getSchema("MapOfStringToValue"));

        // Prepare first snapshot
        writeStateEngine.prepareForWrite();
        HollowReadStateEngine readEngine1 = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readEngine1);

        // Verify person 1 data
        HollowObjectTypeReadState personState1 =
            (HollowObjectTypeReadState) readEngine1.getTypeState("Person");
        assertNotNull("Person state should exist", personState1);
        assertEquals("Should have 1 person", 1, personState1.maxOrdinal() + 1);

        // CYCLE 2: Add person WITH metadata - triggers lazy schema creation
        writeStateEngine.prepareForNextCycle();
        writeStateEngine.addAllObjectsFromPreviousCycle();

        // Create person with metadata (Struct)
        Message.Builder person2Builder = (Message.Builder) newBuilder.invoke(null);
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("id"), 200);
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("name"), "Test Person 2");

        // Add metadata using google.protobuf.Struct
        Class<?> structClass = protoClassLoader.loadClass("com.google.protobuf.Struct");
        Method structNewBuilder = structClass.getMethod("newBuilder");
        Class<?> structBuilderClass = protoClassLoader.loadClass("com.google.protobuf.Struct$Builder");
        Class<?> valueClass = protoClassLoader.loadClass("com.google.protobuf.Value");
        Method structBuilderPutFields = structBuilderClass.getMethod("putFields", String.class, valueClass);

        Method valueNewBuilder = valueClass.getMethod("newBuilder");

        Message.Builder struct2Builder = (Message.Builder) structNewBuilder.invoke(null);

        // Add string value to metadata
        Message.Builder stringValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        stringValueBuilder.setField(
            stringValueBuilder.getDescriptorForType().findFieldByName("string_value"),
            "test value");
        structBuilderPutFields.invoke(struct2Builder, "department", stringValueBuilder.build());

        // Add number value to metadata
        Message.Builder numberValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        numberValueBuilder.setField(
            numberValueBuilder.getDescriptorForType().findFieldByName("number_value"),
            42.0);
        structBuilderPutFields.invoke(struct2Builder, "score", numberValueBuilder.build());

        person2Builder.setField(
            person2Builder.getDescriptorForType().findFieldByName("metadata"),
            struct2Builder.build());
        Message person2 = (Message) build.invoke(person2Builder);

        mapper.add(person2);

        // Now Struct/Value schemas SHOULD exist (lazy creation triggered)
        assertNotNull("Struct schema should now exist", writeStateEngine.getSchema("Struct"));
        assertNotNull("Value schema should now exist", writeStateEngine.getSchema("Value"));
        assertNotNull("MapOfStringToValue schema should now exist", writeStateEngine.getSchema("MapOfStringToValue"));

        // Prepare second snapshot
        writeStateEngine.prepareForWrite();
        HollowReadStateEngine readEngine2 = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readEngine2);

        // Verify both people exist in second snapshot
        HollowObjectTypeReadState personState2 =
            (HollowObjectTypeReadState) readEngine2.getTypeState("Person");
        assertNotNull("Person state should exist in second snapshot", personState2);
        assertEquals("Should have 2 people in second snapshot", 2, personState2.maxOrdinal() + 1);

        // Verify Struct/Value schemas exist in read engine
        assertNotNull("Struct schema should exist in read engine",
            readEngine2.getTypeState("Struct"));
        assertNotNull("Value schema should exist in read engine",
            readEngine2.getTypeState("Value"));
        assertNotNull("MapOfStringToValue schema should exist in read engine",
            readEngine2.getTypeState("MapOfStringToValue"));

        // Verify person 1 (no metadata) can still be read
        int person1Id = personState2.readInt(0, personState2.getSchema().getPosition("id"));
        assertEquals("Person 1 ID should be 100", 100, person1Id);

        // Verify person 2 (with metadata) can be read
        int person2Id = personState2.readInt(1, personState2.getSchema().getPosition("id"));
        assertEquals("Person 2 ID should be 200", 200, person2Id);

        // Verify metadata reference exists for person 2
        int metadataPos = personState2.getSchema().getPosition("metadata");
        int metadataOrdinal = personState2.readOrdinal(1, metadataPos);
        assertTrue("Person 2 should have metadata reference", metadataOrdinal >= 0);
    }

    /**
     * Test that conflicting types for the same Struct field are detected and rejected.
     * If two instances have the same field name with different types, an error should be thrown.
     */
    @Test
    public void testStructFieldTypeConflictDetection() throws Exception {
        // Load Person and Struct classes
        Class<?> personClass = protoClassLoader.loadClass("com.netflix.hollow.test.proto.PersonProtos$Person");
        Class<?> structClass = protoClassLoader.loadClass("com.google.protobuf.Struct");
        Class<?> valueClass = protoClassLoader.loadClass("com.google.protobuf.Value");

        Method personNewBuilder = personClass.getMethod("newBuilder");
        Method structNewBuilder = structClass.getMethod("newBuilder");
        Method structBuilderPutFields = structClass.getDeclaredClasses()[0].getMethod("putFields", String.class, valueClass);
        Method valueNewBuilder = valueClass.getMethod("newBuilder");

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowMessageMapper mapper = new HollowMessageMapper(writeStateEngine);

        // Person 1: age as NUMBER
        Message.Builder person1Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct1Builder = (Message.Builder) structNewBuilder.invoke(null);

        Message.Builder numberValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        numberValueBuilder.setField(
            numberValueBuilder.getDescriptorForType().findFieldByName("number_value"),
            35.0);
        structBuilderPutFields.invoke(struct1Builder, "age", numberValueBuilder.build());

        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("id"), 1);
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("name"), "Person 1");
        person1Builder.setField(person1Builder.getDescriptorForType().findFieldByName("metadata"), struct1Builder.build());

        mapper.add(person1Builder.build());

        // Person 2: age as STRING (conflict!)
        Message.Builder person2Builder = (Message.Builder) personNewBuilder.invoke(null);
        Message.Builder struct2Builder = (Message.Builder) structNewBuilder.invoke(null);

        Message.Builder stringValueBuilder = (Message.Builder) valueNewBuilder.invoke(null);
        stringValueBuilder.setField(
            stringValueBuilder.getDescriptorForType().findFieldByName("string_value"),
            "twenty-eight");
        structBuilderPutFields.invoke(struct2Builder, "age", stringValueBuilder.build());

        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("id"), 2);
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("name"), "Person 2");
        person2Builder.setField(person2Builder.getDescriptorForType().findFieldByName("metadata"), struct2Builder.build());

        // Should throw RuntimeException wrapping IllegalStateException due to conflicting types for "age"
        try {
            mapper.add(person2Builder.build());
            fail("Expected RuntimeException for conflicting Struct field types");
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            assertNotNull("Should have a cause", cause);
            assertTrue("Cause should be IllegalStateException",
                cause instanceof IllegalStateException);
            String message = cause.getMessage();
            assertTrue("Error message should mention conflicting types",
                message.contains("Conflicting types"));
            assertTrue("Error message should mention the field name 'age'",
                message.contains("age"));
            assertTrue("Error message should mention number_value",
                message.contains("number_value"));
            assertTrue("Error message should mention string_value",
                message.contains("string_value"));
        }
    }
}

