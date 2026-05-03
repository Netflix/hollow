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
package com.netflix.hollow.api.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for producer restore when the snapshot contains schemas that weren't
 * registered at init time. This simulates dynamic schema creation (e.g.,
 * HollowMessageMapper's lazy schema creation for Struct/Value/ListValue).
 */
public class DynamicSchemaRestoreTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    /**
     * Test that restore succeeds when the snapshot contains additional types
     * not registered via initializeDataModel(). This simulates the case where
     * a producer dynamically creates schemas (e.g., for google.protobuf.Struct)
     * during runCycle, and a new producer instance tries to restore from that
     * snapshot with only the base types registered.
     */
    @Test
    public void testRestoreWithAdditionalTypesInSnapshot() {
        // --- Producer 1: publish with base types + dynamic types ---
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        // Register base schema: Person with name and metadata reference
        HollowObjectSchema personSchema = new HollowObjectSchema("Person", 2);
        personSchema.addField("name", FieldType.REFERENCE, "String");
        personSchema.addField("metadata", FieldType.REFERENCE, "Metadata");

        HollowObjectSchema stringSchema = new HollowObjectSchema("String", 1);
        stringSchema.addField("value", FieldType.STRING);

        // Metadata is a "dynamically created" type (like Struct)
        HollowObjectSchema metadataSchema = new HollowObjectSchema("Metadata", 1);
        metadataSchema.addField("data", FieldType.STRING);

        producer1.initializeDataModel(personSchema, stringSchema, metadataSchema);

        long v1 = producer1.runCycle(state -> {
            HollowWriteStateEngine engine = state.getStateEngine();

            HollowObjectWriteRecord stringRec = new HollowObjectWriteRecord(stringSchema);
            stringRec.setString("value", "Alice");
            int nameOrd = engine.add("String", stringRec);

            HollowObjectWriteRecord metaRec = new HollowObjectWriteRecord(metadataSchema);
            metaRec.setString("data", "some-metadata");
            int metaOrd = engine.add("Metadata", metaRec);

            HollowObjectWriteRecord personRec = new HollowObjectWriteRecord(personSchema);
            personRec.setReference("name", nameOrd);
            personRec.setReference("metadata", metaOrd);
            engine.add("Person", personRec);
        });

        // --- Producer 2: restore with only base types (no Metadata) ---
        // This simulates a new producer instance that only knows about Person and
        // String but not the dynamically-created Metadata type.
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        // Only register Person and String — not Metadata
        producer2.initializeDataModel(personSchema, stringSchema);

        // Restore should succeed, adopting Metadata from the snapshot
        HollowProducer.ReadState readState = producer2.restore(v1, blobStore);
        assertNotNull("Restore should succeed", readState);
        assertEquals(v1, readState.getVersion());

        // Verify the restored data includes Metadata
        HollowReadStateEngine readEngine = readState.getStateEngine();
        assertNotNull("Metadata type should exist after restore",
                readEngine.getTypeState("Metadata"));

        // Verify we can continue the delta chain
        long v2 = producer2.runCycle(state -> {
            HollowWriteStateEngine engine = state.getStateEngine();

            HollowObjectWriteRecord stringRec = new HollowObjectWriteRecord(stringSchema);
            stringRec.setString("value", "Bob");
            int nameOrd = engine.add("String", stringRec);

            HollowObjectWriteRecord metaRec = new HollowObjectWriteRecord(metadataSchema);
            metaRec.setString("data", "bob-metadata");
            int metaOrd = engine.add("Metadata", metaRec);

            HollowObjectWriteRecord personRec = new HollowObjectWriteRecord(personSchema);
            personRec.setReference("name", nameOrd);
            personRec.setReference("metadata", metaOrd);
            engine.add("Person", personRec);
        });

        assertTrue("Should produce a new version", v2 > v1);

        // Verify delta was produced (not just a snapshot)
        assertNotNull("Delta blob should exist", blobStore.retrieveDeltaBlob(v1));
    }

    /**
     * Test that restore works when the snapshot has a type with additional fields
     * not in the producer's schema (non-breaking: field added in snapshot).
     * The producer's schema should take precedence — extra fields from the
     * snapshot are ignored during restore.
     */
    @Test
    public void testRestoreWithExtraFieldsInSnapshotSchema() {
        // --- Producer 1: publish with extended Person (has age field) ---
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        HollowObjectSchema personSchemaV2 = new HollowObjectSchema("Person", 2);
        personSchemaV2.addField("name", FieldType.STRING);
        personSchemaV2.addField("age", FieldType.INT);

        producer1.initializeDataModel(personSchemaV2);

        long v1 = producer1.runCycle(state -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(personSchemaV2);
            rec.setString("name", "Alice");
            rec.setInt("age", 30);
            state.getStateEngine().add("Person", rec);
        });

        // --- Producer 2: restore with Person that only has name (no age) ---
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        HollowObjectSchema personSchemaV1 = new HollowObjectSchema("Person", 1);
        personSchemaV1.addField("name", FieldType.STRING);

        producer2.initializeDataModel(personSchemaV1);

        // Restore should succeed — producer's schema wins, extra fields ignored
        HollowProducer.ReadState readState = producer2.restore(v1, blobStore);
        assertNotNull("Restore should succeed with schema evolution", readState);

        // The restored data should be readable with the producer's schema
        long v2 = producer2.runCycle(state -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(personSchemaV1);
            rec.setString("name", "Bob");
            state.getStateEngine().add("Person", rec);
        });

        assertTrue("Should produce a new version", v2 > v1);
    }

    /**
     * Test that restore works when the producer adds a new field not in the
     * snapshot (non-breaking: field added by producer).
     */
    @Test
    public void testRestoreWithNewFieldInProducerSchema() {
        // --- Producer 1: publish with basic Person ---
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        HollowObjectSchema personSchemaV1 = new HollowObjectSchema("Person", 1);
        personSchemaV1.addField("name", FieldType.STRING);

        producer1.initializeDataModel(personSchemaV1);

        long v1 = producer1.runCycle(state -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(personSchemaV1);
            rec.setString("name", "Alice");
            state.getStateEngine().add("Person", rec);
        });

        // --- Producer 2: restore with extended Person (has age) ---
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        HollowObjectSchema personSchemaV2 = new HollowObjectSchema("Person", 2);
        personSchemaV2.addField("name", FieldType.STRING);
        personSchemaV2.addField("age", FieldType.INT);

        producer2.initializeDataModel(personSchemaV2);

        // Restore should succeed — producer's schema wins, missing fields are empty
        HollowProducer.ReadState readState = producer2.restore(v1, blobStore);
        assertNotNull("Restore should succeed with new field", readState);

        long v2 = producer2.runCycle(state -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(personSchemaV2);
            rec.setString("name", "Bob");
            rec.setInt("age", 25);
            state.getStateEngine().add("Person", rec);
        });

        assertTrue("Should produce a new version", v2 > v1);
    }

    /**
     * Test restore when snapshot has a complex type hierarchy not registered
     * by the producer. Simulates HollowMessageMapper's lazy creation of
     * List types and nested reference chains.
     */
    @Test
    public void testRestoreWithDynamicListAndNestedTypes() {
        // --- Producer 1: publish with Person + Tags list (dynamic types) ---
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        HollowObjectSchema personSchema = new HollowObjectSchema("Person", 2);
        personSchema.addField("name", FieldType.STRING);
        personSchema.addField("tags", FieldType.REFERENCE, "ListOfTag");

        HollowObjectSchema tagSchema = new HollowObjectSchema("Tag", 1);
        tagSchema.addField("value", FieldType.STRING);

        HollowListSchema listOfTagSchema = new HollowListSchema("ListOfTag", "Tag");

        producer1.initializeDataModel(personSchema, tagSchema, listOfTagSchema);

        long v1 = producer1.runCycle(state -> {
            HollowWriteStateEngine engine = state.getStateEngine();

            HollowObjectWriteRecord tag1Rec = new HollowObjectWriteRecord(tagSchema);
            tag1Rec.setString("value", "important");
            int tag1Ord = engine.add("Tag", tag1Rec);

            HollowObjectWriteRecord tag2Rec = new HollowObjectWriteRecord(tagSchema);
            tag2Rec.setString("value", "urgent");
            int tag2Ord = engine.add("Tag", tag2Rec);

            HollowListWriteRecord listRec = new HollowListWriteRecord();
            listRec.addElement(tag1Ord);
            listRec.addElement(tag2Ord);
            int listOrd = engine.add("ListOfTag", listRec);

            HollowObjectWriteRecord personRec = new HollowObjectWriteRecord(personSchema);
            personRec.setString("name", "Alice");
            personRec.setReference("tags", listOrd);
            engine.add("Person", personRec);
        });

        // --- Producer 2: only registers Person (no Tag, no ListOfTag) ---
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        // Only register Person schema — Tag and ListOfTag are "dynamic"
        producer2.initializeDataModel(personSchema);

        // Restore should succeed, adopting Tag and ListOfTag from snapshot
        HollowProducer.ReadState readState = producer2.restore(v1, blobStore);
        assertNotNull("Restore should succeed", readState);

        HollowReadStateEngine readEngine = readState.getStateEngine();
        assertNotNull("Tag type should exist after restore",
                readEngine.getTypeState("Tag"));
        assertNotNull("ListOfTag type should exist after restore",
                readEngine.getTypeState("ListOfTag"));

        // Verify we can continue producing with all types
        long v2 = producer2.runCycle(state -> {
            HollowWriteStateEngine engine = state.getStateEngine();

            HollowObjectWriteRecord tagRec = new HollowObjectWriteRecord(tagSchema);
            tagRec.setString("value", "new-tag");
            int tagOrd = engine.add("Tag", tagRec);

            HollowListWriteRecord listRec = new HollowListWriteRecord();
            listRec.addElement(tagOrd);
            int listOrd = engine.add("ListOfTag", listRec);

            HollowObjectWriteRecord personRec = new HollowObjectWriteRecord(personSchema);
            personRec.setString("name", "Bob");
            personRec.setReference("tags", listOrd);
            engine.add("Person", personRec);
        });

        assertTrue("Should produce a new version", v2 > v1);
        assertNotNull("Delta blob should exist", blobStore.retrieveDeltaBlob(v1));
    }
}
