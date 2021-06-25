package com.netflix.hollow.api.consumer.metrics;

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_METRIC_CYCLE_START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractRefreshMetricsListenerTest {

    private final long TEST_VERSION_LOW = 123l;
    private final long TEST_VERSION_HIGH = 456l;
    private final long TEST_CYCLE_START_TIMESTAMP = System.currentTimeMillis();

    private Map<String, String> testHeaderTags = new HashMap<>();
    protected TestRefreshMetricsListener concreteRefreshMetricsListener;

    @Mock HollowReadStateEngine mockStateEngine;

    class TestRefreshMetricsListener extends AbstractRefreshMetricsListener {
        @Override
        public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
            Assert.assertNotNull(refreshMetrics);
        }
    }

    @Before
    public void setup() {
        concreteRefreshMetricsListener = new TestRefreshMetricsListener();

        MockitoAnnotations.initMocks(this);
        when(mockStateEngine.getHeaderTags()).thenReturn(testHeaderTags);
    }

    @Test
    public void testRefreshStartedWithInitialLoad() {
        concreteRefreshMetricsListener.refreshStarted(VERSION_NONE, TEST_VERSION_HIGH);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();
        Assert.assertEquals(true, refreshMetrics.getIsInitialLoad());
        Assert.assertNotNull(refreshMetrics.getUpdatePlanDetails());
    }

    @Test
    public void testRefreshStartedWithSubsequentLoad() {
        concreteRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();
        Assert.assertFalse(refreshMetrics.getIsInitialLoad());
        Assert.assertNotNull(refreshMetrics.getUpdatePlanDetails());
    }

    @Test
    public void testTransitionsPlannedWithSnapshotUpdatePlan() {
        List<HollowConsumer.Blob.BlobType> testTransitionSequence = new ArrayList<HollowConsumer.Blob.BlobType>() {{
            add(HollowConsumer.Blob.BlobType.SNAPSHOT);
            add(HollowConsumer.Blob.BlobType.DELTA);
            add(HollowConsumer.Blob.BlobType.DELTA);
        }};
        concreteRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        concreteRefreshMetricsListener.transitionsPlanned(TEST_VERSION_LOW, TEST_VERSION_HIGH, true, testTransitionSequence);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();

        Assert.assertEquals(HollowConsumer.Blob.BlobType.SNAPSHOT, refreshMetrics.getOverallRefreshType());
        Assert.assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        Assert.assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        Assert.assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
    }

    @Test
    public void testTransitionsPlannedWithDeltaUpdatePlan() {
        List<HollowConsumer.Blob.BlobType> testTransitionSequence = new ArrayList<HollowConsumer.Blob.BlobType>() {{
            add(HollowConsumer.Blob.BlobType.DELTA);
            add(HollowConsumer.Blob.BlobType.DELTA);
            add(HollowConsumer.Blob.BlobType.DELTA);
        }};
        concreteRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        concreteRefreshMetricsListener.transitionsPlanned(TEST_VERSION_LOW, TEST_VERSION_HIGH, false, testTransitionSequence);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();

        Assert.assertEquals(HollowConsumer.Blob.BlobType.DELTA, refreshMetrics.getOverallRefreshType());
        Assert.assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        Assert.assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        Assert.assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
    }

    @Test
    public void testTransitionsPlannedWithReverseDeltaUpdatePlan() {
        List<HollowConsumer.Blob.BlobType> testTransitionSequence = new ArrayList<HollowConsumer.Blob.BlobType>() {{
            add(HollowConsumer.Blob.BlobType.REVERSE_DELTA);
            add(HollowConsumer.Blob.BlobType.REVERSE_DELTA);
            add(HollowConsumer.Blob.BlobType.REVERSE_DELTA);
        }};
        concreteRefreshMetricsListener.refreshStarted(TEST_VERSION_HIGH, TEST_VERSION_LOW);
        concreteRefreshMetricsListener.transitionsPlanned(TEST_VERSION_HIGH, TEST_VERSION_LOW, false, testTransitionSequence);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();

        Assert.assertEquals(HollowConsumer.Blob.BlobType.REVERSE_DELTA, refreshMetrics.getOverallRefreshType());
        Assert.assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        Assert.assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        Assert.assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
    }

    @Test
    public void testRefreshSuccess() {
        class SuccessTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                Assert.assertEquals(0l, refreshMetrics.getConsecutiveFailures());
                Assert.assertEquals(true, refreshMetrics.getIsRefreshSuccess());
                Assert.assertEquals(0l, refreshMetrics.getRefreshSuccessAgeMillisOptional().getAsLong());
                Assert.assertNotEquals(0l, refreshMetrics.getRefreshEndTimeNano());
                Assert.assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
            }
        }
        SuccessTestRefreshMetricsListener successTestRefreshMetricsListener = new SuccessTestRefreshMetricsListener();
        successTestRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);

        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP));
        successTestRefreshMetricsListener.snapshotUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH);

        successTestRefreshMetricsListener.refreshSuccessful(TEST_VERSION_LOW, TEST_VERSION_HIGH, TEST_VERSION_HIGH);
    }

    @Test
    public void testRefreshFailure() {
        class FailureTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                Assert.assertNotEquals(0l, refreshMetrics.getConsecutiveFailures());
                Assert.assertFalse(refreshMetrics.getIsRefreshSuccess());
                Assert.assertNotEquals(Optional.empty(), refreshMetrics.getRefreshSuccessAgeMillisOptional());
                Assert.assertNotEquals(0l, refreshMetrics.getRefreshEndTimeNano());
                Assert.assertFalse(refreshMetrics.getCycleStartTimestamp().isPresent());
            }
        }
        FailureTestRefreshMetricsListener failTestRefreshMetricsListener = new FailureTestRefreshMetricsListener();
        failTestRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        failTestRefreshMetricsListener.refreshFailed(TEST_VERSION_LOW, TEST_VERSION_HIGH, TEST_VERSION_HIGH, null);

    }

    @Test
    public void testMetricsWhenMultiTransitionRefreshSucceeds() {
        class SuccessTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                Assert.assertEquals(3, refreshMetrics.getUpdatePlanDetails().getNumSuccessfulTransitions());
                Assert.assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
            }
        }
        List<HollowConsumer.Blob.BlobType> testTransitionSequence = new ArrayList<HollowConsumer.Blob.BlobType>() {{
            add(HollowConsumer.Blob.BlobType.SNAPSHOT);
            add(HollowConsumer.Blob.BlobType.DELTA);
            add(HollowConsumer.Blob.BlobType.DELTA);
        }};

        SuccessTestRefreshMetricsListener successTestRefreshMetricsListener = new SuccessTestRefreshMetricsListener();
        successTestRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        successTestRefreshMetricsListener.transitionsPlanned(TEST_VERSION_LOW, TEST_VERSION_HIGH, true, testTransitionSequence);

        successTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP-2));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH-2);

        successTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP-1));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH-1);

        successTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH);

        successTestRefreshMetricsListener.refreshSuccessful(TEST_VERSION_LOW, TEST_VERSION_HIGH, TEST_VERSION_HIGH);
    }

    @Test
    public void testMetricsWhenMultiTransitionRefreshFails() {
        class FailureTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                Assert.assertEquals(1, refreshMetrics.getUpdatePlanDetails().getNumSuccessfulTransitions());
                Assert.assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
            }
        }
        List<HollowConsumer.Blob.BlobType> testTransitionSequence = new ArrayList<HollowConsumer.Blob.BlobType>() {{
            add(HollowConsumer.Blob.BlobType.SNAPSHOT);
            add(HollowConsumer.Blob.BlobType.DELTA);
            add(HollowConsumer.Blob.BlobType.DELTA);
        }};

        FailureTestRefreshMetricsListener failureTestRefreshMetricsListener = new FailureTestRefreshMetricsListener();
        failureTestRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        failureTestRefreshMetricsListener.transitionsPlanned(TEST_VERSION_LOW, TEST_VERSION_HIGH, true, testTransitionSequence);

        failureTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP));
        failureTestRefreshMetricsListener.snapshotUpdateOccurred(null, mockStateEngine, TEST_VERSION_LOW);

        failureTestRefreshMetricsListener.refreshFailed(TEST_VERSION_LOW-1, TEST_VERSION_LOW, TEST_VERSION_HIGH, null);
    }

    @Test
    public void testCycleStart() throws InterruptedException {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p.initializeDataModel(X.class);

        long version1 = p.runCycle(ws -> {
            ws.add(new X(1));
        });

        class TestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            int run = 0;
            long reportedCycleStartV1 = 0;

            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                run ++;
                assertNotNull(refreshMetrics.getCycleStartTimestamp());

                if (run == 1) {
                    reportedCycleStartV1 = refreshMetrics.getCycleStartTimestamp().getAsLong();
                }

                if (run == 2) {
                    long reportedCycleStartV2 = refreshMetrics.getCycleStartTimestamp().getAsLong();
                    assertTrue(reportedCycleStartV2 > reportedCycleStartV1);
                }

                if (run == 3) {
                    assertEquals(reportedCycleStartV1, refreshMetrics.getCycleStartTimestamp().getAsLong());
                }
            }
        }
        TestRefreshMetricsListener testMetricsListener = new TestRefreshMetricsListener();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withRefreshListener(testMetricsListener)
                .build();
        consumer.triggerRefreshTo(version1);    // snapshot load
        final String cycleStartV1 = consumer.getStateEngine().getHeaderTag(HEADER_TAG_METRIC_CYCLE_START);

        Thread.sleep(1);

        long version2 = p.runCycle(ws -> {
            ws.add(new X(2));
        });

        consumer.triggerRefreshTo(version2);    // delta transition
        final String cycleStartV2 = consumer.getStateEngine().getHeaderTag(HEADER_TAG_METRIC_CYCLE_START);
        assertTrue(Long.valueOf(cycleStartV2) > Long.valueOf(cycleStartV1));

        consumer.triggerRefreshTo(version1);    // reverse delta transition
        assertEquals(cycleStartV1, consumer.getStateEngine().getHeaderTag(HEADER_TAG_METRIC_CYCLE_START));


        HollowProducer p2 = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p2.initializeDataModel(X.class);

        Thread.sleep(1);

        p2.restore(version2, blobStore);
        long version3 = p2.runCycle(ws -> {
            ws.add(new X(3));
        });

        consumer.triggerRefreshTo(version3);    // delta transition
        final String cycleStartV3 = consumer.getStateEngine().getHeaderTag(HEADER_TAG_METRIC_CYCLE_START);
        assertTrue(Long.valueOf(cycleStartV3) > Long.valueOf(cycleStartV2));

        consumer.triggerRefreshTo(version2);    // reverse delta transition
        assertEquals(cycleStartV2, consumer.getStateEngine().getHeaderTag(HEADER_TAG_METRIC_CYCLE_START));
    }

    static class X {
        final int id;

        X(int id) {
            this.id = id;
        }
    }
}
