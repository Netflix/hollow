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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowSchemaIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Reproduces the scenario, originating from RawHollow, where a producer emits a snapshot in which a
 * referenced type ends up with <em>no schema at all</em> -- so a consumer sees a {@code null} schema
 * for that referenced type.
 *
 * <p>The important detail is <b>how the producer builds its data model</b>. A RawHollow producer does
 * not know the client's POJO classes; instead it consumes {@link FlatRecord}s and derives the set of
 * schemas to register purely by scanning the schemas embedded in those flat records (this is exactly
 * what {@code rawhollow-server}'s {@code UnionSchemaCreator.scanFlatRecordSchemas} does). A flat record
 * only embeds the schema of a type when a record of that type is physically serialized into it, and
 * {@link HollowObjectMapper#writeFlat} serializes nothing for a {@code null} reference field. So:
 *
 * <ol>
 *   <li>A client writes a single {@code TypeWithReference} record whose {@code ref} field is
 *       {@code null}.</li>
 *   <li>The resulting flat record embeds the {@code TypeWithReference} schema (which references
 *       {@code ReferencedType}) but <b>not</b> the {@code ReferencedType} schema.</li>
 *   <li>The producer registers only the schemas it scanned -- {@code TypeWithReference} -- and
 *       produces a snapshot that references {@code ReferencedType} without defining it.</li>
 *   <li>The consumer therefore observes {@code getSchema("ReferencedType") == null}.</li>
 * </ol>
 *
 * <p>Note: the equivalent POJO-driven data model path ({@code producer.initializeDataModel(
 * TypeWithReference.class)} or lazy discovery from an added POJO) does <em>not</em> reproduce this,
 * because {@link HollowObjectMapper} recursively registers a type state for every referenced type
 * regardless of whether any record of it exists. The null schema only arises when the data model is
 * assembled by scanning flat records.
 */
public class ReferencedTypeWithNoDataSnapshotTest {

    @HollowPrimaryKey(fields = "id")
    static class TypeWithReference {
        int id;
        ReferencedType ref;

        TypeWithReference(int id, ReferencedType ref) {
            this.id = id;
            this.ref = ref;
        }
    }

    static class ReferencedType {
        int value;

        ReferencedType(int value) {
            this.value = value;
        }
    }

    @HollowPrimaryKey(fields = "id")
    static class TypeWithCollectionReference {
        int id;
        List<CollectionElement> elements;

        TypeWithCollectionReference(int id, List<CollectionElement> elements) {
            this.id = id;
            this.elements = elements;
        }
    }

    static class CollectionElement {
        int value;

        CollectionElement(int value) {
            this.value = value;
        }
    }

    @Test
    public void referencedObjectTypeEndsUpWithNullSchemaViaFlatRecordPath() {
        // 1. CLIENT: a full data model that knows both types is used only to serialize the POJO into a
        //    FlatRecord. Because 'ref' is null, no ReferencedType sub-record (and hence no
        //    ReferencedType schema) is written into the flat record.
        HollowObjectMapper mapper = fullModelMapper(TypeWithReference.class);
        FlatRecord flatRecord = writeFlat(mapper, new TypeWithReference(1, null));

        // The flat record embeds TypeWithReference (which references ReferencedType) but not ReferencedType.
        List<HollowSchema> scannedSchemas = scanEmbeddedSchemas(flatRecord);
        assertTrue("flat record should embed the TypeWithReference schema",
                containsSchema(scannedSchemas, "TypeWithReference"));
        assertNull("flat record must NOT embed a schema for the null-valued reference type",
                findSchema(scannedSchemas, "ReferencedType"));

        // 2. PRODUCER: register ONLY the schemas scanned out of the flat records (mirrors
        //    UnionSchemaCreator), then apply the flat record and produce a snapshot.
        long version = produceFromFlatRecords(scannedSchemas, flatRecord);

        // 3. CONSUMER: the snapshot has TypeWithReference (still referencing ReferencedType) but no
        //    ReferencedType schema -- the null-schema condition.
        HollowReadStateEngine readEngine = readSnapshot(version);

        HollowObjectSchema twrSchema = (HollowObjectSchema) readEngine.getSchema("TypeWithReference");
        assertNotNull("TypeWithReference schema should be present", twrSchema);
        assertEquals("ref field still references ReferencedType",
                "ReferencedType", twrSchema.getReferencedType("ref"));
        assertNull("ReferencedType has no records, so its schema is absent from the snapshot",
                readEngine.getSchema("ReferencedType"));

        // The produced snapshot is still consumable (the referenced-but-absent type is tolerated).
        assertNotNull(readEngine.getTypeState("TypeWithReference"));
    }

    @Test
    public void referencedCollectionElementTypeEndsUpWithNullSchemaViaFlatRecordPath() {
        // Same mechanism, but the missing reference is reached through a collection. A null 'elements'
        // field means neither the backing ListOfCollectionElement nor CollectionElement is serialized.
        HollowObjectMapper mapper = fullModelMapper(TypeWithCollectionReference.class);
        FlatRecord flatRecord = writeFlat(mapper, new TypeWithCollectionReference(1, null));

        List<HollowSchema> scannedSchemas = scanEmbeddedSchemas(flatRecord);
        assertTrue(containsSchema(scannedSchemas, "TypeWithCollectionReference"));
        assertNull(findSchema(scannedSchemas, "ListOfCollectionElement"));
        assertNull(findSchema(scannedSchemas, "CollectionElement"));

        long version = produceFromFlatRecords(scannedSchemas, flatRecord);

        HollowReadStateEngine readEngine = readSnapshot(version);

        HollowObjectSchema twcSchema = (HollowObjectSchema) readEngine.getSchema("TypeWithCollectionReference");
        assertNotNull(twcSchema);
        assertEquals("ListOfCollectionElement", twcSchema.getReferencedType("elements"));
        assertNull("the backing list type has no records, so its schema is absent",
                readEngine.getSchema("ListOfCollectionElement"));
        assertNull("the collection element type has no records, so its schema is absent",
                readEngine.getSchema("CollectionElement"));
    }

    // ---- helpers ----------------------------------------------------------------------------------

    /** A mapper over a state engine that knows the whole POJO graph -- used only to write flat records. */
    private static HollowObjectMapper fullModelMapper(Class<?> topLevel) {
        HollowObjectMapper mapper = new HollowObjectMapper(new HollowWriteStateEngine());
        mapper.doNotUseDefaultHashKeys();
        mapper.initializeTypeState(topLevel);
        return mapper;
    }

    private static FlatRecord writeFlat(HollowObjectMapper mapper, Object pojo) {
        HollowWriteStateEngine model = mapper.getStateEngine();
        FlatRecordWriter writer = new FlatRecordWriter(model, new FakeHollowSchemaIdentifierMapper(model));
        writer.reset();
        mapper.writeFlat(pojo, writer);
        return writer.generateFlatRecord();
    }

    /**
     * Mirrors {@code UnionSchemaCreator.scanFlatRecordSchemas}: the set of schemas a RawHollow producer
     * would register is exactly the set of schemas physically embedded in the incoming flat records.
     */
    private static List<HollowSchema> scanEmbeddedSchemas(FlatRecord flatRecord) {
        Map<String, HollowSchema> schemas = new LinkedHashMap<>();
        FlatRecordReader reader = new FlatRecordReader(flatRecord);
        while (reader.hasMore()) {
            HollowSchema schema = reader.readSchema();
            reader.skipSchema(schema);
            schemas.putIfAbsent(schema.getName(), schema);
        }
        return new ArrayList<>(schemas.values());
    }

    private long produceFromFlatRecords(List<HollowSchema> schemas, FlatRecord... flatRecords) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(schemas.toArray(new HollowSchema[0]));

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);
        for (FlatRecord flatRecord : flatRecords) {
            incrementalProducer.addOrModify(flatRecord);
        }
        return incrementalProducer.runCycle();
    }

    private HollowReadStateEngine readSnapshot(long version) {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);
        return consumer.getStateEngine();
    }

    private static boolean containsSchema(List<HollowSchema> schemas, String name) {
        return findSchema(schemas, name) != null;
    }

    private static HollowSchema findSchema(List<HollowSchema> schemas, String name) {
        for (HollowSchema schema : schemas) {
            if (schema.getName().equals(name)) {
                return schema;
            }
        }
        return null;
    }

    private final InMemoryBlobStore blobStore = new InMemoryBlobStore();
}
