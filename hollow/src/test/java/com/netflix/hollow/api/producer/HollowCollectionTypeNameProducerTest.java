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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.HollowCollectionTypeName;
import com.netflix.hollow.core.write.objectmapper.HollowMapTypeName;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HollowCollectionTypeNameProducerTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    // -------------------------------------------------------------------------
    // HollowProducer.Builder — populate path
    // -------------------------------------------------------------------------

    @Test
    public void listElementTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(ws -> {
            TypeWithAnnotatedList obj = new TypeWithAnnotatedList();
            obj.ids = Arrays.asList(1, 2, 3);
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(version);
        HollowListSchema schema = (HollowListSchema) readState.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("MovieId", schema.getElementType());
        Assert.assertNotNull("Dedicated 'MovieId' type state should exist",
                readState.getTypeState("MovieId"));
        Assert.assertNull("Global 'Integer' type state should NOT exist",
                readState.getTypeState("Integer"));
    }

    @Test
    public void setElementTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(ws -> {
            TypeWithAnnotatedSet obj = new TypeWithAnnotatedSet();
            obj.tags = new HashSet<>(Arrays.asList("action", "drama"));
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(version);
        HollowSetSchema schema = (HollowSetSchema) readState.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("TagString", schema.getElementType());
        Assert.assertNotNull(readState.getTypeState("TagString"));
    }

    @Test
    public void mapKeyAndValueTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("hello", "world");
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(version);
        HollowMapSchema schema = (HollowMapSchema) readState.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("MapKey", schema.getKeyType());
        Assert.assertEquals("MapValue", schema.getValueType());
        Assert.assertNotNull(readState.getTypeState("MapKey"));
        Assert.assertNotNull(readState.getTypeState("MapValue"));
    }

    // -------------------------------------------------------------------------
    // Restore path
    // -------------------------------------------------------------------------

    @Test
    public void restorePath_namedTypesActiveAfterRestore() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        // Cycle 1: produce a snapshot
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long snapshotVersion = producer1.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("hello", "world");
            ws.add(obj);
        });

        // New producer restores from that snapshot, then runs a delta cycle
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        producer2.initializeDataModel(TypeWithAnnotatedMap.class);
        producer2.restore(snapshotVersion, blobStore);

        long deltaVersion = producer2.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("after-restore", "value");
            ws.add(obj);
        });

        // Named types must still be active after restore + delta cycle
        HollowReadStateEngine readState = loadAtVersion(deltaVersion);
        HollowMapSchema schema = (HollowMapSchema) readState.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("MapKey", schema.getKeyType());
        Assert.assertEquals("MapValue", schema.getValueType());
        Assert.assertNotNull("MapKey type state must survive the restore path",
                readState.getTypeState("MapKey"));
    }

    @Test
    public void restorePath_listNamedTypeActiveAfterRestore() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long snapshotVersion = producer1.runCycle(ws -> {
            TypeWithAnnotatedList obj = new TypeWithAnnotatedList();
            obj.ids = Arrays.asList(1, 2, 3);
            ws.add(obj);
        });

        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        producer2.initializeDataModel(TypeWithAnnotatedList.class);
        producer2.restore(snapshotVersion, blobStore);
        long deltaVersion = producer2.runCycle(ws -> {
            TypeWithAnnotatedList obj = new TypeWithAnnotatedList();
            obj.ids = Arrays.asList(4, 5);
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(deltaVersion);
        HollowListSchema schema = (HollowListSchema) readState.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("MovieId", schema.getElementType());
        Assert.assertNotNull("MovieId type state must survive the restore path",
                readState.getTypeState("MovieId"));
    }

    @Test
    public void restorePath_setNamedTypeActiveAfterRestore() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long snapshotVersion = producer1.runCycle(ws -> {
            TypeWithAnnotatedSet obj = new TypeWithAnnotatedSet();
            obj.tags = new HashSet<>(Arrays.asList("action", "drama"));
            ws.add(obj);
        });

        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        producer2.initializeDataModel(TypeWithAnnotatedSet.class);
        producer2.restore(snapshotVersion, blobStore);
        long deltaVersion = producer2.runCycle(ws -> {
            TypeWithAnnotatedSet obj = new TypeWithAnnotatedSet();
            obj.tags = new HashSet<>(Arrays.asList("comedy"));
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(deltaVersion);
        HollowSetSchema schema = (HollowSetSchema) readState.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("TagString", schema.getElementType());
        Assert.assertNotNull("TagString type state must survive the restore path",
                readState.getTypeState("TagString"));
    }

    // -------------------------------------------------------------------------
    // Reverse delta
    // -------------------------------------------------------------------------

    @Test
    public void reverseDelta_namedMapTypeThroughReverseDelta() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();

        long v1 = producer.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("k1", "v1");
            ws.add(obj);
        });
        long v2 = producer.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("k2", "v2");
            ws.add(obj);
        });

        // Delta-only consumer: with double snapshots disabled, the backward refresh must apply the
        // reverse delta. If reverse delta through the named key/value types were broken, this would throw.
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        consumer.triggerRefreshTo(v1); // reverse delta
        Assert.assertEquals(v1, consumer.getCurrentVersionId());

        HollowMapSchema schema = (HollowMapSchema) consumer.getStateEngine().getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("MapKey", schema.getKeyType());
        Assert.assertEquals("MapValue", schema.getValueType());
        Assert.assertNotNull(consumer.getStateEngine().getTypeState("MapKey"));
    }

    // -------------------------------------------------------------------------
    // Schema change: annotation added to an existing field (auto double snapshot)
    // -------------------------------------------------------------------------

    @Test
    public void schemaChange_consumerRecoversWhenKeyTypeNamedOnExistingField() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        // v1: deployed WITHOUT the annotation — the map key uses the global shared "String" type.
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long v1 = producer1.runCycle(ws -> {
            UnannotatedAttributes obj = new UnannotatedAttributes();
            obj.attributes = new HashMap<>();
            obj.attributes.put("hello", "world");
            ws.add(obj);
        });

        // A consumer reads the pre-annotation schema.
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);
        HollowMapSchema before = (HollowMapSchema) consumer.getStateEngine().getTypeState("AttributesMap").getSchema();
        Assert.assertEquals("String", before.getKeyType());

        // v2: redeployed WITH the annotation. Naming the key changes an existing type's key schema
        // ("String" -> "AttrKey") for the pinned outer type "AttributesMap". This is a breaking change:
        // the new producer cannot restore across it and continue the delta chain (Hollow requires a
        // type's schema to be stable within a chain), so it deploys a fresh snapshot.
        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long v2 = producer2.runCycle(ws -> {
            AnnotatedAttributes obj = new AnnotatedAttributes();
            obj.attributes = new HashMap<>();
            obj.attributes.put("hello", "world");
            ws.add(obj);
        });

        // The consumer has no delta path across the schema change, so it auto-double-snapshots to v2
        // and recovers cleanly on the renamed, dedicated key type.
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        HollowMapSchema after = (HollowMapSchema) consumer.getStateEngine().getTypeState("AttributesMap").getSchema();
        Assert.assertEquals("AttrKey", after.getKeyType());
        Assert.assertNotNull("Dedicated 'AttrKey' type state must exist after recovery",
                consumer.getStateEngine().getTypeState("AttrKey"));
    }

    // -------------------------------------------------------------------------
    // Consumer type filter
    // -------------------------------------------------------------------------

    @Test
    public void withTypeFilter_excludesNamedElementType() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        long version = producer.runCycle(ws -> {
            TypeWithAnnotatedList obj = new TypeWithAnnotatedList();
            obj.ids = Arrays.asList(1, 2, 3);
            ws.add(obj);
        });

        // A consumer that filters out the dedicated element type must still load cleanly.
        HollowFilterConfig filter = new HollowFilterConfig(true);
        filter.addType("MovieId");

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withFilterConfig(filter)
                .build();
        consumer.triggerRefreshTo(version);

        Assert.assertEquals(version, consumer.getCurrentVersionId());
        Assert.assertNotNull("Parent list type should still load",
                consumer.getStateEngine().getTypeState("ListOfInteger"));
        Assert.assertNull("Filtered-out 'MovieId' type state should be absent",
                consumer.getStateEngine().getTypeState("MovieId"));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private HollowReadStateEngine loadAtVersion(long version) {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);
        return consumer.getStateEngine();
    }

    // -------------------------------------------------------------------------
    // Model classes
    // -------------------------------------------------------------------------

    static class TypeWithAnnotatedList {
        @HollowCollectionTypeName(elementTypeName = "MovieId")
        List<Integer> ids;
    }

    static class TypeWithAnnotatedSet {
        @HollowCollectionTypeName(elementTypeName = "TagString")
        Set<String> tags;
    }

    static class TypeWithAnnotatedMap {
        @HollowMapTypeName(keyTypeName = "MapKey", valueTypeName = "MapValue")
        Map<String, String> data;
    }

    // Pre-annotation deployment: the map key uses the global shared "String" type.
    static class UnannotatedAttributes {
        @HollowTypeName(name = "AttributesMap")
        Map<String, String> attributes;
    }

    // Post-annotation deployment: same outer type, but the key gets a dedicated "AttrKey" type.
    static class AnnotatedAttributes {
        @HollowTypeName(name = "AttributesMap")
        @HollowMapTypeName(keyTypeName = "AttrKey")
        Map<String, String> attributes;
    }
}
