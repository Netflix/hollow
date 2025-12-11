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

        URLClassLoader protoClassLoader = new URLClassLoader(
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
}

