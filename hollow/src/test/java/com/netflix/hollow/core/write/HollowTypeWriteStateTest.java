package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
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
    public void testReverseDeltaNumShardsWhenNewTypes() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        long v1 = p1.runCycle(ws -> ws.add("s1"));

        // add a new object type and all collection types to data model
        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        p2.initializeDataModel(HasAllTypes.class);
        p2.restore(v1, blobStore);
        long v2 = p2.runCycle(state -> {
            HasAllTypes o1 = new HasAllTypes(
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
        p1.initializeDataModel(HasAllTypes.class);
        long v1 = p1.runCycle(ws -> {
            ws.add("A");
            for (int i=0; i<50; i++) { // results in 2 shards at shard size 32
                final long val = new Long(i);
                ws.add(new HasAllTypes(
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
        // All types dropped all records, no serialization in delta for these types (irrespective of dynamic type sharding)
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(2, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());

        long v4 = p1.runCycle(ws -> {
            ws.add("A");
            for (int i=0; i<50; i++) { // back up to the original shard counts
                final long val = new Long(i);
                ws.add(new HasAllTypes(
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
    public void testNoReshardingIfNumShardsPinnedByAnnotation() {
        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        new HollowObjectMapper(wse).initializeTypeState(TypeWithPinnedNumShards.class);
        HollowObjectTypeWriteState typeWriteState = (HollowObjectTypeWriteState) wse.getTypeState("TypeWithPinnedNumShards");
        assertFalse(typeWriteState.allowTypeResharding());

        wse = new HollowWriteStateEngine();
        new HollowObjectMapper(wse).initializeTypeState(HasAllTypesWithPinnedNumShards.class);
        for (HollowTypeWriteState writeState : wse.getOrderedTypeStates()) {
            assertFalse(writeState.allowTypeResharding());
        }
    }

    @Test
    public void testRestoreNumShardsButDoNotPin() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(HasAllTypes.class);
        long v1 = p1.runCycle(ws -> {
            for (int i=0; i<50; i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypes(
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
        p2.initializeDataModel(HasAllTypes.class);
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
                ws.add(new HasAllTypes(
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
        int newNumShards = consumer2.getStateEngine().getTypeState("Long").numShards();
        assertEquals(v2, consumer2.getCurrentVersionId());
        assertTrue(2 < consumer2.getStateEngine().getTypeState("Long").numShards());
        assertTrue(2 < consumer2.getStateEngine().getTypeState("CustomReferenceType").numShards());

        // producer doesn't support resharding for these types yet
        assertEquals(8, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(4, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(8, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());


    }

    private class HasAllTypes {
        CustomReferenceType customReferenceType;
        Set<String> setOfStrings;
        List<Integer> listOfInt;
        Map<String, Long> mapOfStringToLong;

        private HasAllTypes(CustomReferenceType customReferenceType, Set<String> setOfStrings, List<Integer> listOfInt, Map<String, Long> mapOfStringToLong) {
            this.customReferenceType = customReferenceType;
            this.setOfStrings = setOfStrings;
            this.listOfInt = listOfInt;
            this.mapOfStringToLong = mapOfStringToLong;
        }
    }

    private class CustomReferenceType {
        long id;
        private CustomReferenceType(long id) {
            this.id = id;
        }
    }

    @HollowShardLargeType(numShards=4)
    private static class TypeWithPinnedNumShards {
        private int value;
    }

    private class HasAllTypesWithPinnedNumShards {
        @HollowShardLargeType(numShards = 32)
        CustomReferenceType customReferenceType;
        @HollowShardLargeType(numShards = 32)
        Set<String> setOfStrings;
        @HollowShardLargeType(numShards = 32)
        List<Integer> listOfInt;
        @HollowShardLargeType(numShards = 32)
        Map<String, Long> mapOfStringToLong;

        private HasAllTypesWithPinnedNumShards(CustomReferenceType customReferenceType, Set<String> setOfStrings, List<Integer> listOfInt, Map<String, Long> mapOfStringToLong) {
            this.customReferenceType = customReferenceType;
            this.setOfStrings = setOfStrings;
            this.listOfInt = listOfInt;
            this.mapOfStringToLong = mapOfStringToLong;
        }
    }
}
