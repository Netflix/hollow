package com.netflix.hollow.api.consumer.metrics;

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_METRIC_ANNOUNCEMENT;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_METRIC_CYCLE_START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private final long TEST_ANNOUNCEMENT_TIMESTAMP = System.currentTimeMillis() + 5000l;

    private Map<String, String> testHeaderTags = new HashMap<>();
    protected TestRefreshMetricsListener concreteRefreshMetricsListener;

    @Mock HollowReadStateEngine mockStateEngine;

    class TestRefreshMetricsListener extends AbstractRefreshMetricsListener {
        @Override
        public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
            assertNotNull(refreshMetrics);
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
        assertEquals(true, refreshMetrics.getIsInitialLoad());
        assertNotNull(refreshMetrics.getUpdatePlanDetails());
    }

    @Test
    public void testRefreshStartedWithSubsequentLoad() {
        concreteRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);
        ConsumerRefreshMetrics refreshMetrics = concreteRefreshMetricsListener.refreshMetricsBuilder.build();
        Assert.assertFalse(refreshMetrics.getIsInitialLoad());
        assertNotNull(refreshMetrics.getUpdatePlanDetails());
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

        assertEquals(HollowConsumer.Blob.BlobType.SNAPSHOT, refreshMetrics.getOverallRefreshType());
        assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
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

        assertEquals(HollowConsumer.Blob.BlobType.DELTA, refreshMetrics.getOverallRefreshType());
        assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
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

        assertEquals(HollowConsumer.Blob.BlobType.REVERSE_DELTA, refreshMetrics.getOverallRefreshType());
        assertEquals(TEST_VERSION_LOW, refreshMetrics.getUpdatePlanDetails().getDesiredVersion());
        assertEquals(TEST_VERSION_HIGH, refreshMetrics.getUpdatePlanDetails().getBeforeVersion());
        assertEquals(testTransitionSequence, refreshMetrics.getUpdatePlanDetails().getTransitionSequence());
    }

    @Test
    public void testRefreshSuccess() {
        class SuccessTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                assertEquals(0l, refreshMetrics.getConsecutiveFailures());
                assertEquals(true, refreshMetrics.getIsRefreshSuccess());
                assertEquals(0l, refreshMetrics.getRefreshSuccessAgeMillisOptional().getAsLong());
                Assert.assertNotEquals(0l, refreshMetrics.getRefreshEndTimeNano());
                assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
                assertEquals(TEST_ANNOUNCEMENT_TIMESTAMP, refreshMetrics.getAnnouncementTimestamp().getAsLong());
            }
        }
        SuccessTestRefreshMetricsListener successTestRefreshMetricsListener = new SuccessTestRefreshMetricsListener();
        successTestRefreshMetricsListener.refreshStarted(TEST_VERSION_LOW, TEST_VERSION_HIGH);

        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP));
        testHeaderTags.put(HEADER_TAG_METRIC_ANNOUNCEMENT, String.valueOf(TEST_ANNOUNCEMENT_TIMESTAMP));
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
                Assert.assertFalse(refreshMetrics.getAnnouncementTimestamp().isPresent());
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
                assertEquals(3, refreshMetrics.getUpdatePlanDetails().getNumSuccessfulTransitions());
                assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
                assertEquals(TEST_ANNOUNCEMENT_TIMESTAMP, refreshMetrics.getAnnouncementTimestamp().getAsLong());
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
        testHeaderTags.put(HEADER_TAG_METRIC_ANNOUNCEMENT, String.valueOf(TEST_ANNOUNCEMENT_TIMESTAMP-2));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH-2);

        successTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP-1));
        testHeaderTags.put(HEADER_TAG_METRIC_ANNOUNCEMENT, String.valueOf(TEST_ANNOUNCEMENT_TIMESTAMP-1));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH-1);

        successTestRefreshMetricsListener.blobLoaded(null);
        testHeaderTags.put(HEADER_TAG_METRIC_CYCLE_START, String.valueOf(TEST_CYCLE_START_TIMESTAMP));
        testHeaderTags.put(HEADER_TAG_METRIC_ANNOUNCEMENT, String.valueOf(TEST_ANNOUNCEMENT_TIMESTAMP));
        successTestRefreshMetricsListener.deltaUpdateOccurred(null, mockStateEngine, TEST_VERSION_HIGH);

        successTestRefreshMetricsListener.refreshSuccessful(TEST_VERSION_LOW, TEST_VERSION_HIGH, TEST_VERSION_HIGH);
    }

    @Test
    public void testMetricsWhenMultiTransitionRefreshFails() {
        class FailureTestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                assertEquals(1, refreshMetrics.getUpdatePlanDetails().getNumSuccessfulTransitions());
                assertEquals(TEST_CYCLE_START_TIMESTAMP, refreshMetrics.getCycleStartTimestamp().getAsLong());
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
    public void testCycleStart() {  // also exercises reverse delta transition
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p.initializeDataModel(String.class);

        long version1 = p.runCycle(ws -> {
            // override cycle start time with a strictly incrementing count to prevent potential
            // clock skew issue from use of System.currentTimeMillis() that would make test brittle
            ws.getStateEngine().addHeaderTag(HEADER_TAG_METRIC_CYCLE_START, "1");
            ws.add("A");
        });

        class TestRefreshMetricsListener extends AbstractRefreshMetricsListener {
            int run = 0;

            @Override
            public void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
                run ++;
                assertNotNull(refreshMetrics.getCycleStartTimestamp());
                switch (run) {
                    case 1:
                        assertEquals(1L, refreshMetrics.getCycleStartTimestamp().getAsLong());
                        break;
                    case 2:
                    case 5:
                        assertEquals(2L, refreshMetrics.getCycleStartTimestamp().getAsLong());
                        break;
                    case 3:
                        assertEquals(1L, refreshMetrics.getCycleStartTimestamp().getAsLong());
                        break;
                    case 4:
                        assertEquals(3L, refreshMetrics.getCycleStartTimestamp().getAsLong());
                        break;
                }
            }
        }
        TestRefreshMetricsListener testMetricsListener = new TestRefreshMetricsListener();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withRefreshListener(testMetricsListener)
                .build();
        consumer.triggerRefreshTo(version1);    // snapshot load

        long version2 = p.runCycle(ws -> {
            ws.getStateEngine().addHeaderTag(HEADER_TAG_METRIC_CYCLE_START, "2");
            ws.add("B");
        });

        consumer.triggerRefreshTo(version2);    // delta transition
        consumer.triggerRefreshTo(version1);    // reverse delta transition

        // now restore from v2 and continue delta chain
        HollowProducer p2 = HollowProducer
                .withPublisher(blobStore)
                .withBlobStager(blobStager)
                .build();
        p2.initializeDataModel(String.class);
        p2.restore(version2, blobStore);

        long version3 = p2.runCycle(ws -> {
            ws.getStateEngine().addHeaderTag(HEADER_TAG_METRIC_CYCLE_START, "3");
            ws.add("C");
        });

        consumer.triggerRefreshTo(version3);    // delta transition
        consumer.triggerRefreshTo(version2);    // reverse delta transition
    }
}