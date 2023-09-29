package com.netflix.hollow.api.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowConsumerMetricsTests {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void metricsWhenLoadingSnapshot() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(new HollowProducer.Populator() {
            public void populate(HollowProducer.WriteState state) throws Exception {
                state.add(Integer.valueOf(1));
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowConsumerMetrics hollowConsumerMetrics = consumer.getMetrics();
        Assert.assertEquals(version, consumer.getCurrentVersionId());
        Assert.assertEquals(hollowConsumerMetrics.getCurrentVersion(), consumer.getCurrentVersionId());
        Assert.assertEquals(hollowConsumerMetrics.getRefreshSucceded(), 1);
        Assert.assertEquals(hollowConsumerMetrics.getTotalPopulatedOrdinals(), 1);
    }

    @Test
    public void metricsWhenRefreshFails() {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        try {
            consumer.triggerRefreshTo(0);
        } catch (Exception ignored) { }

        HollowConsumerMetrics hollowConsumerMetrics = consumer.getMetrics();
        Assert.assertEquals(hollowConsumerMetrics.getRefreshFailed(), 1);
        Assert.assertEquals(hollowConsumerMetrics.getTotalPopulatedOrdinals(), 0);
    }

    @Test
    public void metricsWhenRefreshFailsDoNotRestartPreviousOnes() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        /// Showing verbose version of `runCycle(producer, 1);`
        long version = producer.runCycle(new HollowProducer.Populator() {
            public void populate(HollowProducer.WriteState state) throws Exception {
                state.add(Integer.valueOf(1));
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        try {
            consumer.triggerRefreshTo(0);
        } catch (Exception ignored) { }

        HollowConsumerMetrics hollowConsumerMetrics = consumer.getMetrics();
        Assert.assertEquals(hollowConsumerMetrics.getRefreshFailed(), 1);
        Assert.assertEquals(hollowConsumerMetrics.getRefreshSucceded(), 1);
        Assert.assertEquals(hollowConsumerMetrics.getTotalPopulatedOrdinals(), 1);
    }
}
