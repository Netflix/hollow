package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.Arrays;
import org.junit.Test;

public class HollowObjectTypeWriteStateTest {

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
    public void testReverseDeltaNumShardsWhenNewType() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(String.class);
        long v1 = p1.runCycle(ws -> {
            ws.add("A");
        });

        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).withTargetMaxTypeShardSize(32).build();
        p2.initializeDataModel(String.class, Long.class);
        p2.restore(v1, blobStore);
        long v2 = p2.runCycle(ws -> {
            ws.add("A");
            ws.add("B");
            for (int i=0; i<50; i++) {
                ws.add(new Long(i));
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
        consumer.triggerRefreshTo(v2);
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());

        consumer.triggerRefreshTo(v1);    // reverse delta transition for new type with customNumShards
        assertEquals(v1, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards());
    }

    @Test
    public void testReverseDeltaNumShardsWhenTypeDropsToZeroRecords() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(String.class, Long.class);
        long v1 = p1.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to work around clock skew
            ws.add("A");
            for (int i=0; i<50; i++) { // results in 2 shards at shard size 32
                ws.add(new Long(i));
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

        long v2 = p1.runCycle(ws -> {
            ws.add("A");
        });
        consumer.triggerRefreshTo(v2);
        assertEquals(v2, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards()); // Long type has a ghost record

        long v3 = p1.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to work around clock skew
            ws.add("A");
            ws.add("B");
        });
        consumer.triggerRefreshTo(v3);
        assertEquals(v3, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards()); // Long type dropped all records

        long v4 = p1.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to work around clock skew
            ws.add("A");
            for (int i=0; i<50; i++) { // results in 2 shards at shard size 32
                ws.add(new Long(i));
            }
        });
        consumer.triggerRefreshTo(v4);
        assertEquals(v4, consumer.getCurrentVersionId());
        assertEquals(2, consumer.getStateEngine().getTypeState("Long").numShards()); // Long type has 1 record again
    }

    @Test
    public void testNoReshardingIfNumShardsPinnedByAnnotation() {
        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        new HollowObjectMapper(wse).initializeTypeState(TypeWithPinnedNumShards.class);
        HollowObjectTypeWriteState typeWriteState = (HollowObjectTypeWriteState) wse.getTypeState("TypeWithPinnedNumShards");
        assertFalse(typeWriteState.allowTypeResharding());
    }

    @Test
    public void testRestoreNumShardsButDoNotPin() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTargetMaxTypeShardSize(32).build();
        p1.initializeDataModel(Long.class);
        long v1 = p1.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to work around clock skew
            for (int i=0; i<50; i++) { // results in 2 shards at shard size 32
                ws.add(new Long(i));
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

        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();
        p2.initializeDataModel(Long.class);
        p2.restore(v1, blobStore);
        assertEquals(2, p2.getWriteEngine().getTypeState("Long").numShards);
        assertFalse(p2.getWriteEngine().getTypeState("Long").isNumShardsPinned());

        long v2 = p2.runCycle(ws -> {
            for (int i=0; i<100; i++) { // results in 2 shards at shard size 32
                ws.add(new Long(i));
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
        assertEquals(4, newNumShards);
    }

    @HollowShardLargeType(numShards=4)
    private static class TypeWithPinnedNumShards {
        private int value;
    }
}
