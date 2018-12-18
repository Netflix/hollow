package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashIndexUpdatesTest {
    InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void deltaUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withGeneratedAPIClass(DataModel.Consumer.Api.class)
                .build();
        consumer.triggerRefreshTo(v1);

        HashIndex<DataModel.Consumer.TypeA, Integer> hi = HashIndex.from(consumer, DataModel.Consumer.TypeA.class)
                .usingPath("i", int.class);

        Assert.assertEquals(1L, hi.findMatches(1).count());


        long v2 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
        });
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(2L, hi.findMatches(1).count());


        hi.detachFromDataRefresh();
        long v3 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
            ws.add(new DataModel.Producer.TypeA(1, "3"));
        });
        consumer.triggerRefreshTo(v3);

        Assert.assertEquals(2L, hi.findMatches(1).count());
    }

    @Test
    public void snapshotUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withGeneratedAPIClass(DataModel.Consumer.Api.class)
                .build();
        consumer.triggerRefreshTo(v1);

        HashIndex<DataModel.Consumer.TypeA, Integer> hi = HashIndex.from(consumer, DataModel.Consumer.TypeA.class)
                .usingPath("i", int.class);

        Assert.assertEquals(1L, hi.findMatches(1).count());


        long v2 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
        });
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(2L, hi.findMatches(1).count());


        hi.detachFromDataRefresh();
        long v3 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
            ws.add(new DataModel.Producer.TypeA(1, "3"));
        });
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v3);

        Assert.assertEquals(2L, hi.findMatches(1).count());
    }
}
