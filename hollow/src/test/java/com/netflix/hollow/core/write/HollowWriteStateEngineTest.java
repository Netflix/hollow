package com.netflix.hollow.core.write;

import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_DELTA_CHAIN_VERSION_COUNTER;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_PRODUCER_TO_VERSION;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_TYPE_RESHARDING_INVOKED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.model.CustomReferenceType;
import com.netflix.hollow.api.producer.model.HasAllTypeStates;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
        assertEquals("3", consumer.getStateEngine().getHeaderTag(HEADER_TAG_DELTA_CHAIN_VERSION_COUNTER));

        consumer.triggerRefreshTo(version2);    // reverse delta transition
        assertEquals("2", consumer.getStateEngine().getHeaderTag(TEST_TAG));
        assertEquals(String.valueOf(version2), consumer.getStateEngine().getHeaderTag(HEADER_TAG_PRODUCER_TO_VERSION));
        assertEquals("2", consumer.getStateEngine().getHeaderTag(HEADER_TAG_DELTA_CHAIN_VERSION_COUNTER));

    }

    @Test
    public void testHeaderTagsWhenResharding() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer producer = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32)
                .build();
        long v1 = producer.runCycle(ws -> {
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

        int oldNumShardsObj = producer.getWriteEngine().getTypeState("Long").getNumShards();
        assertTrue(0 < oldNumShardsObj);
        int oldNumShardsRef = producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards();
        assertTrue(0 < oldNumShardsRef);
        int oldNumShardsSet = producer.getWriteEngine().getTypeState("SetOfString").getNumShards();
        assertTrue(0 < oldNumShardsSet);
        int oldNumShardsList = producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards();
        assertTrue(0 < oldNumShardsList);
        int oldNumShardsMap = producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards();
        assertTrue(0 < oldNumShardsMap);

        long v2 = producer.runCycle(ws -> {
            // 2x the data
            for (int i=0;i<100;i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });
        int newNumShardsObj = producer.getWriteEngine().getTypeState("Long").getNumShards();
        assertTrue(oldNumShardsObj < newNumShardsObj);
        int newNumShardsRef = producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards();
        assertTrue(oldNumShardsRef < newNumShardsRef);
        int newNumShardsSet = producer.getWriteEngine().getTypeState("SetOfString").getNumShards();
        assertTrue(oldNumShardsSet < newNumShardsSet);
        int newNumShardsList = producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards();
        assertTrue(oldNumShardsList < newNumShardsList);
        int newNumShardsMap = producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards();
        assertTrue(oldNumShardsMap < newNumShardsMap);

        long v3 = producer.runCycle(ws -> {
            // remain at same num shards as 100 records
            for (int i=0;i<99;i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }

        });
        assertEquals(newNumShardsObj, producer.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(newNumShardsRef, producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(newNumShardsSet, producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(newNumShardsList, producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(newNumShardsMap, producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

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
        assertEquals(oldNumShardsObj, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(oldNumShardsRef, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(oldNumShardsSet, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(oldNumShardsList, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(oldNumShardsMap, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
        assertNull(consumer.getStateEngine().getHeaderTag(HEADER_TAG_TYPE_RESHARDING_INVOKED));

        consumer.triggerRefreshTo(v2);
        assertEquals(v2, consumer.getCurrentVersionId());
        assertEquals(newNumShardsObj, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(newNumShardsRef, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(newNumShardsSet, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(newNumShardsList, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(newNumShardsMap, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
        String reshardingInvokedHeader = consumer.getStateEngine().getHeaderTag(HEADER_TAG_TYPE_RESHARDING_INVOKED);
        assertTrue(reshardingInvokedHeader.contains(String.format("Long:(%s,%s)", oldNumShardsObj, newNumShardsObj)));
        assertTrue(reshardingInvokedHeader.contains(String.format("CustomReferenceType:(%s,%s)", oldNumShardsRef, newNumShardsRef)));
        assertTrue(reshardingInvokedHeader.contains(String.format("SetOfString:(%s,%s)", oldNumShardsSet, newNumShardsSet)));
        assertTrue(reshardingInvokedHeader.contains(String.format("ListOfInteger:(%s,%s)", oldNumShardsList, newNumShardsList)));
        assertTrue(reshardingInvokedHeader.contains(String.format("MapOfStringToLong:(%s,%s)", oldNumShardsMap, newNumShardsMap)));

        consumer.triggerRefreshTo(v3);
        assertEquals(v3, consumer.getCurrentVersionId());
        assertEquals(newNumShardsObj, consumer.getStateEngine().getTypeState("Long").numShards());
        assertEquals(newNumShardsRef, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(newNumShardsSet, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(newNumShardsList, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(newNumShardsMap, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
        assertEquals(false, consumer.getStateEngine().getHeaderTags().containsKey(HEADER_TAG_TYPE_RESHARDING_INVOKED));
    }
}
