package com.netflix.hollow.api.consumer.metrics;

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_METRIC_CYCLE_START;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
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
        Assert.assertEquals(false, refreshMetrics.getIsInitialLoad());
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
                Assert.assertEquals(false, refreshMetrics.getIsRefreshSuccess());
                Assert.assertNotEquals(Optional.empty(), refreshMetrics.getRefreshSuccessAgeMillisOptional());
                Assert.assertNotEquals(0l, refreshMetrics.getRefreshEndTimeNano());
                Assert.assertEquals(false, refreshMetrics.getCycleStartTimestamp().isPresent());
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
}
