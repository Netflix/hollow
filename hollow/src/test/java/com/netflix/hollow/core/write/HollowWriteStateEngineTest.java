package com.netflix.hollow.core.write;

import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_PRODUCER_TO_VERSION;
import static org.junit.Assert.assertEquals;

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
}
