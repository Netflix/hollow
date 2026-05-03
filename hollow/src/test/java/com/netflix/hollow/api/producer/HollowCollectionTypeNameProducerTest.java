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
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.HollowCollectionTypeName;
import com.netflix.hollow.core.write.objectmapper.HollowMapTypeName;
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
    // withCollectionTypeNaming() on HollowProducer.Builder — populate path
    // -------------------------------------------------------------------------

    @Test
    public void builderFlag_listElementTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withCollectionTypeNaming()
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
    public void builderFlag_setElementTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withCollectionTypeNaming()
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
    public void builderFlag_mapKeyAndValueTypeNamed() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withCollectionTypeNaming()
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

    @Test
    public void builderFlag_off_annotationsIgnored() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                // withCollectionTypeNaming() NOT called
                .build();

        long version = producer.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("key", "value");
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(version);
        HollowMapSchema schema = (HollowMapSchema) readState.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("String", schema.getValueType());
        Assert.assertNull("No custom 'MapKey' type state should exist when flag is off",
                readState.getTypeState("MapKey"));
    }

    // -------------------------------------------------------------------------
    // Restore path: flag must be re-applied to the new objectMapper after restore
    // -------------------------------------------------------------------------

    @Test
    public void restorePath_flagPropagatedToNewMapper() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        // Cycle 1: produce a snapshot
        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .withCollectionTypeNaming()
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
                .withCollectionTypeNaming()
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
    public void restorePath_flagOff_annotationsStillIgnoredAfterRestore() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer producer1 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        long snapshotVersion = producer1.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            ws.add(obj);
        });

        HollowProducer producer2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(blobStager)
                // no withCollectionTypeNaming()
                .build();
        producer2.initializeDataModel(TypeWithAnnotatedMap.class);
        producer2.restore(snapshotVersion, blobStore);

        long deltaVersion = producer2.runCycle(ws -> {
            TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
            obj.data = new HashMap<>();
            obj.data.put("k", "v");
            ws.add(obj);
        });

        HollowReadStateEngine readState = loadAtVersion(deltaVersion);
        HollowMapSchema schema = (HollowMapSchema) readState.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertNull("MapKey should not exist when flag is off",
                readState.getTypeState("MapKey"));
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
}
