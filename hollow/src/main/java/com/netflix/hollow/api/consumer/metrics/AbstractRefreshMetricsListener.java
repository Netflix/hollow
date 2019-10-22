/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.consumer.metrics;

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.AbstractRefreshListener;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for computing Hollow Consumer refresh metrics, requires plugging in metrics reporting implementation.
 * <p>
 * This class computes Hollow Consumer refresh metrics by listening to refresh events. At the end of every refresh, whether
 * the refresh succeeded or failed, the refresh metrics in {@code ConsumerRefreshMetrics} and refresh details in {@code UpdatePlanDetails}
 * are reporte using the {@code RefreshMetricsReporting} interface. This interface makes it mandatory for concrete subclasses
 * to implement custom metrics reporting behavior.
 */
public abstract class AbstractRefreshMetricsListener extends AbstractRefreshListener implements RefreshMetricsReporting {

    private static final Logger log = Logger.getLogger(AbstractRefreshMetricsListener.class.getName());

    private OptionalLong lastRefreshTimeNanoOptional;
    private long refreshStartTimeNano;
    private long consecutiveFailures;
    private BlobType overallRefreshType;    // Indicates whether the overall refresh (that could comprise of multiple transitions)
                                            // is classified as snapshot, delta, or reverse delta. Note that if a snapshot
                                            // transition is present then the overall refresh type is snapshot.
    private ConsumerRefreshMetrics.UpdatePlanDetails updatePlanDetails;  // Some details about the transitions comprising a refresh
    // visible for testing
    ConsumerRefreshMetrics.Builder refreshMetricsBuilder;

    public AbstractRefreshMetricsListener() {
        lastRefreshTimeNanoOptional = OptionalLong.empty();
        consecutiveFailures = 0l;
    }

    @Override
    public void refreshStarted(long currentVersion, long requestedVersion) {
        updatePlanDetails = new ConsumerRefreshMetrics.UpdatePlanDetails();
        refreshStartTimeNano = System.nanoTime();
        refreshMetricsBuilder = new ConsumerRefreshMetrics.Builder();
        refreshMetricsBuilder.setIsInitialLoad(currentVersion == VERSION_NONE);
        refreshMetricsBuilder.setUpdatePlanDetails(updatePlanDetails);
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
    public void transitionsPlanned(long beforeVersion, long desiredVersion, boolean isSnapshotPlan, List<HollowConsumer.Blob.BlobType> transitionSequence) {
        updatePlanDetails.beforeVersion = beforeVersion;
        updatePlanDetails.desiredVersion = desiredVersion;
        updatePlanDetails.transitionSequence = transitionSequence;
        if (isSnapshotPlan) {
            overallRefreshType = BlobType.SNAPSHOT;
        } else {
            overallRefreshType = desiredVersion > beforeVersion ? BlobType.DELTA : BlobType.REVERSE_DELTA;
        }
        refreshMetricsBuilder.setOverallRefreshType(overallRefreshType);
    }

    @Override
    public void blobLoaded(HollowConsumer.Blob transition) {
        updatePlanDetails.numSuccessfulTransitions ++;
    }

    /**
     * Metrics reporting implementation is provided by the extending subclass. If exceptions are not gracefully handled
     * in the extending subclass then an exception there can fail the consumer refresh, even though metrics reporting
     * might not be mission critical. This method protects against that scenario by catching all exceptions, logging
     * that there was an exception, and continuing with the consumer refresh.
     * @param refreshMetrics Consumer refresh metrics being reported
     */
    private final void noFailRefreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics) {
        try {
            refreshEndMetricsReporting(refreshMetrics);
        } catch (Exception e) {
            // Metric reporting is not considered critical to consumer refresh. Log exceptions and continue.
            log.log(Level.SEVERE, "Encountered an exception in reporting consumer refresh metrics, ignoring exception and continuing with consumer refresh", e);
        }
    }

    @Override
    public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
        long refreshEndTimeNano = System.nanoTime();

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(refreshEndTimeNano - refreshStartTimeNano);
        consecutiveFailures = 0l;
        lastRefreshTimeNanoOptional = OptionalLong.of(refreshEndTimeNano);

        refreshMetricsBuilder.setDurationMillis(durationMillis)
                .setIsRefreshSuccess(true)
                .setConsecutiveFailures(consecutiveFailures)
                .setRefreshSuccessAgeMillisOptional(0l)
                .setRefreshEndTimeNano(refreshEndTimeNano);

        noFailRefreshEndMetricsReporting(refreshMetricsBuilder.build());
    }

    @Override
    public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
        long  refreshEndTimeNano = System.nanoTime();
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(refreshEndTimeNano - refreshStartTimeNano);
        consecutiveFailures ++;

        refreshMetricsBuilder.setDurationMillis(durationMillis)
                .setIsRefreshSuccess(false)
                .setConsecutiveFailures(consecutiveFailures)
                .setRefreshEndTimeNano(refreshEndTimeNano);
        if (lastRefreshTimeNanoOptional.isPresent()) {
            refreshMetricsBuilder.setRefreshSuccessAgeMillisOptional(TimeUnit.NANOSECONDS.toMillis(refreshEndTimeNano - lastRefreshTimeNanoOptional.getAsLong()));
        }

        noFailRefreshEndMetricsReporting(refreshMetricsBuilder.build());
    }

    @Override
    public void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        // no-op
    }

    @Override
    public void deltaUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        // no-op
    }

    @Override
    public void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

    @Override
    public void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

}
