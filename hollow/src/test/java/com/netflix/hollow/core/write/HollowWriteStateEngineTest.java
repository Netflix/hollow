package com.netflix.hollow.core.write;

import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_PRODUCER_TO_VERSION;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_TYPE_RESHARDING_INVOKED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Test;

public class HollowWriteStateEngineTest {

    private static final String TEST_TAG = "test";
    @Test
    public void testHeaderTagsOnDeltaAndReverseDelta() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p.initializeDataModel(String.class);

        long version1 = p.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to work around clock skew
            ws.getStateEngine().addHeaderTag(TEST_TAG, "1");
            ws.add("A");
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .build();
        consumer.triggerRefreshTo(version1);    // snapshot load

        long version2 = p.runCycle(ws -> {
            ws.getStateEngine().addHeaderTag(TEST_TAG, "2");
            ws.add("B");
        });

        consumer.triggerRefreshTo(version2);    // delta transition
        assertEquals("2", consumer.getStateEngine().getHeaderTag(TEST_TAG));

        consumer.triggerRefreshTo(version1);    // reverse delta transition
        assertEquals("1", consumer.getStateEngine().getHeaderTag(TEST_TAG));

        // now test the RESTORE case
        HollowProducer p2 = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p2.initializeDataModel(String.class);
        p2.restore(version2, blobStore);

        long version3 = p2.runCycle(ws -> {
            ws.getStateEngine().addHeaderTag(TEST_TAG, "3");
            ws.add("C");
        });

        consumer.triggerRefreshTo(version3);    // delta transition
        assertEquals("3", consumer.getStateEngine().getHeaderTag(TEST_TAG));
        assertEquals(String.valueOf(version3), consumer.getStateEngine().getHeaderTag(HEADER_TAG_PRODUCER_TO_VERSION));

        consumer.triggerRefreshTo(version2);    // reverse delta transition
        assertEquals("2", consumer.getStateEngine().getHeaderTag(TEST_TAG));
        assertEquals(String.valueOf(version2), consumer.getStateEngine().getHeaderTag(HEADER_TAG_PRODUCER_TO_VERSION));

    }

    @Test
    public void testHeaderTagsWhenResharding() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer producer = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32)
                .build();
        long v1 = producer.runCycle(ws -> {
            // causes 2 shards for Integer at shard size 32
            for (int i=0;i<50;i++) {
                ws.add(i);
            }
        });
        assertEquals(2, producer.getWriteEngine().getTypeState("Integer").getNumShards());
        long v2 = producer.runCycle(ws -> {
            // 2x the data, causes 4 shards for Integer at shard size 32
            for (int i=0;i<100;i++) {
                ws.add(i);
            }

        });
        assertEquals(4, producer.getWriteEngine().getTypeState("Integer").getNumShards());
        long v3 = producer.runCycle(ws -> {
            // remain at 4 shards for Integer
            for (int i=0;i<99;i++) {
                ws.add(i);
            }

        });
        assertEquals(4, producer.getWriteEngine().getTypeState("Integer").getNumShards());
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
        assertEquals(2, consumer.getStateEngine().getTypeState("Integer").numShards());
        assertNull(consumer.getStateEngine().getHeaderTag(HEADER_TAG_TYPE_RESHARDING_INVOKED));

        consumer.triggerRefreshTo(v2);
        assertEquals(v2, consumer.getCurrentVersionId());
        assertEquals(4, consumer.getStateEngine().getTypeState("Integer").numShards());
        assertEquals("Integer:(2,4)", consumer.getStateEngine().getHeaderTag(HEADER_TAG_TYPE_RESHARDING_INVOKED));

        consumer.triggerRefreshTo(v3);
        assertEquals(v3, consumer.getCurrentVersionId());
        assertEquals(4, consumer.getStateEngine().getTypeState("Integer").numShards());
        assertEquals(false, consumer.getStateEngine().getHeaderTags().containsKey(HEADER_TAG_TYPE_RESHARDING_INVOKED));
    }
}
