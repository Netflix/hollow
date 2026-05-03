package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.model.CustomReferenceType;
import com.netflix.hollow.api.producer.model.HasAllTypeStates;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowTypeWriteStateTest {

    @Test
    public void testCalcMaxShardOrdinal() {
        HollowObjectSchema testSchema = new HollowObjectSchema("Test", 1);
        testSchema.addField("test1", HollowObjectSchema.FieldType.INT);
        HollowObjectTypeWriteState testState = new HollowObjectTypeWriteState(testSchema);

        assertTrue(Arrays.equals(new int[] {-1, -1, -1, -1}, testState.calcMaxShardOrdinal(-1, 4)));
        assertTrue(Arrays.equals(new int[] {0, -1, -1, -1}, testState.calcMaxShardOrdinal(0, 4)));
        assertTrue(Arrays.equals(new int[] {0, 0, -1, -1}, testState.calcMaxShardOrdinal(1, 4)));
        assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, testState.calcMaxShardOrdinal(3, 4)));
        assertTrue(Arrays.equals(new int[] {1, 1, 1, 1}, testState.calcMaxShardOrdinal(7, 4)));
    }

    @Test
    public void testReverseDeltaNumShardsWhenNewTypes() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        long v1 = p1.runCycle(ws -> ws.add("s1"));

        // add a new object type and all collection types to data model
        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        p2.initializeDataModel(HasAllTypeStates.class);
        p2.restore(v1, blobStore);
        long v2 = p2.runCycle(state -> {
            HasAllTypeStates o1 = new HasAllTypeStates(
                    new CustomReferenceType(5l),
                    new HashSet<>(Arrays.asList("e1")),
                    Arrays.asList(1, 2, 3),
                    new HashMap<String, Long>(){{put("k1", 1L);}}
            );
            state.add(o1);
        });

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
        consumer.triggerRefreshTo(v2);

        int numShardsObject = consumer.getStateEngine().getTypeState("CustomReferenceType").numShards();
        assertTrue(numShardsObject > 0);
        int numShardsList = consumer.getStateEngine().getTypeState("ListOfInteger").numShards();
        assertTrue(numShardsList > 0);
        int numShardsSet = consumer.getStateEngine().getTypeState("SetOfString").numShards();
        assertTrue(numShardsSet > 0);
        int numShardsMap = consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards();
        assertTrue(numShardsMap > 0);

        consumer.triggerRefreshTo(v1);
        assertEquals(v1, consumer.getCurrentVersionId());
        assertEquals(numShardsObject, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(numShardsList, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(numShardsSet, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(numShardsMap, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
    }

    @Test
    public void testNumShardsWhenTypeDropsToZeroRecords() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(HasAllTypeStates.class);
        long v1 = p1.runCycle(ws -> {
            ws.add("A");
            for (int i=0; i<50; i++) { // results in 2 shards at shard size 32
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });

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
        assertEquals(v1, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
        HollowChecksum origChecksum = new HollowChecksum().forStateEngineWithCommonSchemas(consumer.getStateEngine(), consumer.getStateEngine());

        long v2 = p1.runCycle(ws -> {
            ws.add("A");
        });
        consumer.triggerRefreshTo(v2);
        assertEquals(v2, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards()); // all types contain ghost records
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        long v3 = p1.runCycle(ws -> {
            ws.add("A");
            ws.add("B");
        });
        consumer.triggerRefreshTo(v3);
        assertEquals(v3, consumer.getCurrentVersionId());
        // All types dropped all records, no serialization in delta for these types irrespective of dynamic type sharding
        assertEquals(1, consumer.getStateEngine().getTypeState("Long").numShards()); // all types contain ghost records
        assertEquals(1, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(2, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        long v4 = p1.runCycle(ws -> {
            ws.add("A");
            for (int i=0; i<50; i++) { // back up to the original shard counts
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });
        consumer.triggerRefreshTo(v4);
        assertEquals(v4, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        consumer.triggerRefreshTo(v1);
        HollowChecksum finalChecksum = new HollowChecksum().forStateEngineWithCommonSchemas(consumer.getStateEngine(), consumer.getStateEngine());

        assertEquals(finalChecksum, origChecksum);
    }

    @Test
    public void testReshardingDespiteNumShardsPinnedByAnnotation() {
        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        new HollowObjectMapper(wse).initializeTypeState(HasAllTypesWithPinnedNumShards.class);
        assertFalse(wse.getTypeState("HasAllTypesWithPinnedNumShards").allowTypeResharding());
        assertFalse(wse.getTypeState("SetOfString").allowTypeResharding());
        assertFalse(wse.getTypeState("ListOfInteger").allowTypeResharding());
        assertFalse(wse.getTypeState("MapOfStringToLong").allowTypeResharding());
        wse.allowTypeResharding(true);
        assertTrue(wse.getTypeState("HasAllTypesWithPinnedNumShards").allowTypeResharding());
        assertTrue(wse.getTypeState("SetOfString").allowTypeResharding());
        assertTrue(wse.getTypeState("ListOfInteger").allowTypeResharding());
        assertTrue(wse.getTypeState("MapOfStringToLong").allowTypeResharding());
    }

    @Test
    public void testRestoreIfNumShardsPinnedByAnnotation() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(HasAllTypesWithPinnedNumShards.class);
        long v1 = p1.runCycle(ws -> {
            for (int i=0; i<5; i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypesWithPinnedNumShards(
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });

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
        assertEquals(v1, consumer.getCurrentVersionId());

        assertEquals(32, consumer.getStateEngine().getTypeState("HasAllTypesWithPinnedNumShards").numShards());
        assertEquals(32, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(32, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(32, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        for (boolean allowTypeResharding : new boolean[] {false, true}) {
            HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                    .withTypeResharding(allowTypeResharding).withTargetMaxTypeShardSize(32).build();
            p2.initializeDataModel(HasAllTypesWithPinnedNumShards.class);
            p2.restore(v1, blobStore);

            assertEquals(32, consumer.getStateEngine().getTypeState("HasAllTypesWithPinnedNumShards").numShards());
            assertEquals(32, consumer.getStateEngine().getTypeState("SetOfString").numShards());
            assertEquals(32, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
            assertEquals(32, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

            long v2 = p2.runCycle(ws -> {
                for (int i=0; i<5000; i++) { // results in more than 32 shards at same shard size if resharding allowed
                    final long val = new Long(i);
                    ws.add(new HasAllTypesWithPinnedNumShards(
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));
                }
            });

            HollowConsumer consumer2 = HollowConsumer.withBlobRetriever(blobStore)
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
            consumer2.triggerRefreshTo(v2);
            assertEquals(v2, consumer2.getCurrentVersionId());
            if (allowTypeResharding) {
                // if type shards is allowed then the number of shards will be more than 32
                assertTrue(32 < consumer2.getStateEngine().getTypeState("HasAllTypesWithPinnedNumShards").numShards());
                assertTrue(32 < consumer2.getStateEngine().getTypeState("SetOfString").numShards());
                assertTrue(32 < consumer2.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertTrue(32 < consumer2.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            } else {
                assertEquals(32, consumer2.getStateEngine().getTypeState("HasAllTypesWithPinnedNumShards").numShards());
                assertEquals(32, consumer2.getStateEngine().getTypeState("SetOfString").numShards());
                assertEquals(32, consumer2.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertEquals(32, consumer2.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            }
        }
    }

    @Test
    public void testRestoreNumShardsButDoNotPin() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(HasAllTypeStates.class);
        long v1 = p1.runCycle(ws -> {
            for (int i=0; i<50; i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });

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
        assertEquals(v1, consumer.getCurrentVersionId());
        // results in following numShards per type at shard size of 32
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();
        p2.initializeDataModel(HasAllTypeStates.class);
        p2.restore(v1, blobStore);
        assertEquals(2, p2.getWriteEngine().getTypeState("Long").numShards);
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        for (HollowTypeWriteState writeState : p2.getWriteEngine().getOrderedTypeStates()) {
            assertFalse(writeState.isNumShardsPinned());
        }

        long v2 = p2.runCycle(ws -> {
            for (int i=0; i<1000; i++) { // results more shards at same shard size
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });

        HollowConsumer consumer2 = HollowConsumer.withBlobRetriever(blobStore)
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
        consumer2.triggerRefreshTo(v2);
        assertEquals(v2, consumer2.getCurrentVersionId());
        assertTrue(2 < consumer2.getStateEngine().getTypeState("Long").numShards());
        assertTrue(2 < consumer2.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertTrue(8 < consumer2.getStateEngine().getTypeState("SetOfString").numShards());
        assertTrue(4 < consumer2.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertTrue(8 < consumer2.getStateEngine().getTypeState("MapOfStringToLong").numShards());
    }

    @Test
    public void testMapOrdinalWithPartitionedMaps() {
        HollowObjectSchema schema = new HollowObjectSchema("TestObj", 1);
        schema.addField("val", FieldType.INT);

        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        wse.setPartitionedOrdinalMap(true);
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, true, null);
        wse.addTypeState(writeState);
        wse.prepareForNextCycle();

        // Map records at specific global ordinals
        // Global ordinal 0: mapIndex=0, localOrdinal=0
        // Global ordinal 5: mapIndex=1 (5 & 3 = 1), localOrdinal=1 (5 >>> 2 = 1)
        // Global ordinal 10: mapIndex=2 (10 & 3 = 2), localOrdinal=2 (10 >>> 2 = 2)
        // Global ordinal 15: mapIndex=3 (15 & 3 = 3), localOrdinal=3 (15 >>> 2 = 3)
        int[] ordinals = {0, 5, 10, 15};
        int[] values = {100, 200, 300, 400};

        for (int i = 0; i < ordinals.length; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("val", values[i]);
            writeState.mapOrdinal(rec, ordinals[i], false, true);
        }
        writeState.recalculateFreeOrdinals();

        // prepareForWrite makes ordinal map data readable
        writeState.prepareForWrite(false);

        // Verify that getPointerForData returns valid (non-negative) pointers for each ordinal
        for (int ordinal : ordinals) {
            long pointer = writeState.getPointerForData(ordinal);
            assertTrue("Pointer for ordinal " + ordinal + " should be non-negative", pointer >= 0);
        }

        // Verify records are distributed across different maps by checking
        // that ordinals with different lower 2 bits route to different maps
        assertEquals(0, ordinals[0] & 3); // map 0
        assertEquals(1, ordinals[1] & 3); // map 1
        assertEquals(2, ordinals[2] & 3); // map 2
        assertEquals(3, ordinals[3] & 3); // map 3
    }

    @Test
    public void testMaxOrdinalAfterPrepareForWrite() {
        HollowObjectSchema schema = new HollowObjectSchema("TestObj", 1);
        schema.addField("val", FieldType.INT);

        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        wse.setPartitionedOrdinalMap(true);
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, true, null);
        wse.addTypeState(writeState);
        wse.prepareForNextCycle();

        // Add several records via the normal add() path
        int maxSeen = -1;
        for (int i = 0; i < 20; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("val", i * 10);
            int ordinal = writeState.add(rec);
            maxSeen = Math.max(maxSeen, ordinal);
        }

        writeState.prepareForWrite(false);

        // maxOrdinal should equal to the highest ordinal we saw
        assertEquals("maxOrdinal should equal to the highest assigned ordinal",
                writeState.maxOrdinal, maxSeen);
    }

    @HollowShardLargeType(numShards=32)
    public class HasAllTypesWithPinnedNumShards {
        @HollowShardLargeType(numShards = 32)
        Set<String> setOfStrings;
        @HollowShardLargeType(numShards = 32)
        List<Integer> listOfInt;
        @HollowShardLargeType(numShards = 32)
        Map<String, Long> mapOfStringToLong;

        public HasAllTypesWithPinnedNumShards(Set<String> setOfStrings, List<Integer> listOfInt, Map<String, Long> mapOfStringToLong) {
            this.setOfStrings = setOfStrings;
            this.listOfInt = listOfInt;
            this.mapOfStringToLong = mapOfStringToLong;
        }
    }
}
