package com.netflix.hollow.api.consumer.metrics;

import static com.netflix.hollow.core.util.Versions.maybeVersion;

import com.netflix.hollow.api.consumer.HollowConsumer.AbstractRefreshListener;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * A class for computing Hollow Consumer refresh metrics, requires plugging in metrics reporting implementation.
 * <p>
 * This class computes Hollow Consumer refresh metrics by listening to refresh events. At the end of every refresh, whether
 * the refresh succeeded or failed, the refresh metrics in {@code ConsumerRefreshMetrics} and refresh details in {@code UpdatePlanDetails}
 * are reporte using the {@code RefreshMetricsReporting} interface. This interface makes it mandatory for concrete subclasses
 * to implement custom metrics reporting behavior.
 */
public abstract class AbstractRefreshMetricsListener extends AbstractRefreshListener implements RefreshMetricsReporting {

    private OptionalLong lastRefreshTimeNanoOptional;
    private long refreshStart;
    private long consecutiveFailures;
    private ConsumerRefreshMetrics.Builder refreshMetricsBuilder;
    private BlobType overallRefreshType;    // Indicates whether the overall refresh (that could comprise of multiple transitions)
                                            // is classified as snapshot, delta, or reverse delta. Note that if a snapshot
                                            // transition is present then the overall refresh type is snapshot.

    private UpdatePlanDetails updatePlanDetails;  // Some details about the transitions comprising a refresh


    public AbstractRefreshMetricsListener() {
        lastRefreshTimeNanoOptional = OptionalLong.empty();
        consecutiveFailures = 0l;
    }

    @Override
    public final void refreshStarted(long currentVersion, long requestedVersion) {

        refreshStart = System.nanoTime();
        updatePlanDetails = new UpdatePlanDetails();

        refreshMetricsBuilder = new ConsumerRefreshMetrics.Builder();
        refreshMetricsBuilder.setIsInitialLoad(!maybeVersion(currentVersion).isPresent());
    }

    /**
     * This method acquires details of individual transitions that comprise a larger refresh.
     * <p>
     * Details of transitions in a refresh such as count and type can be useful to understand consumer performance and
     * to troubleshoot issues relating to refresh failure.
     * </p>
     * @param beforeVersion The version when refresh started
     * @param desiredVersion The version that the consumer refresh tries update to, even though it might not be attainable eg. HollowConstants.VERSION_LATEST
     * @param isSnapshotPlan Indicates whether the refresh involves a snapshot transition
     * @param transitionSequence List of transitions comprising the refresh
     */
    @Override
    public final void transitionsPlanned(long beforeVersion, long desiredVersion, boolean isSnapshotPlan, List<HollowConsumer.Blob.BlobType> transitionSequence) {

        updatePlanDetails.beforeVersion = beforeVersion;
        updatePlanDetails.desiredVersion = desiredVersion;
        updatePlanDetails.numTransitions = transitionSequence.size();
        updatePlanDetails.transitionSequence = transitionSequence;
        if (isSnapshotPlan) {
            overallRefreshType = BlobType.SNAPSHOT;
        } else {
            overallRefreshType = desiredVersion > beforeVersion ? BlobType.DELTA : BlobType.REVERSE_DELTA;
        }
        refreshMetricsBuilder.setOverallRefreshType(overallRefreshType);
    }

    @Override
    public final void blobLoaded(HollowConsumer.Blob transition) {
        updatePlanDetails.numSuccessfulTransitions ++;
    }

    @Override
    public final void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {

        long refreshEnd = System.nanoTime();

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(refreshEnd - refreshStart);
        consecutiveFailures = 0l;
        lastRefreshTimeNanoOptional = OptionalLong.of(refreshEnd);

        refreshMetricsBuilder.setDurationMillis(durationMillis)
                .setIsRefreshSuccess(true)
                .setConsecutiveFailures(consecutiveFailures)
                .setRefreshSuccessAgeMillisOptional(0l);
        ConsumerRefreshMetrics refreshMetrics = refreshMetricsBuilder.build();

        refreshEndMetricsReporting(refreshMetrics);
    }

    @Override
    public final void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {

        long refreshEnd = System.nanoTime();
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(refreshEnd - refreshStart);
        consecutiveFailures ++;

        refreshMetricsBuilder.setDurationMillis(durationMillis)
                .setIsRefreshSuccess(false)
                .setConsecutiveFailures(consecutiveFailures)
                .setUpdatePlanDetails(updatePlanDetails);
        if (lastRefreshTimeNanoOptional.isPresent()) {
            refreshMetricsBuilder.setRefreshSuccessAgeMillisOptional(TimeUnit.NANOSECONDS.toMillis(refreshEnd - lastRefreshTimeNanoOptional.getAsLong()));
        }
        ConsumerRefreshMetrics refreshMetrics = refreshMetricsBuilder.build();
        refreshEndMetricsReporting(refreshMetrics);
    }

    @Override
    public final void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        // no-op
    }

    @Override
    public final void deltaUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        // no-op
    }

    @Override
    public final void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

    @Override
    public final void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

}
