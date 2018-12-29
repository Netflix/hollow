package com.netflix.hollow.api.consumer.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class AbstractRefreshMetricsListener extends HollowConsumer.AbstractRefreshListener  {

    private long consecutiveSuccesses;
    private long consecutiveFailures;

    private Instant lastRefreshInstant;
    private Instant refreshStart;

    private ConsumerRefreshStatus refreshStatus;                    // success or failure
    private ConsumerRefreshTransitionType refreshTransitionType;    // snapshot or delta transition
    private ConsumerRefreshLoadType refreshLoadType;                // "init" indicates first successful load on consumer, "subsequent" indicates subsequent load during refresh

    private long refreshLoadCount;

    // Encapsulate the refresh metrics so that more metrics/properties can be added in future wihtout breaking existing clients.
    public static class ConsumerRefreshMetrics {
        private long refreshDurationMillis;
        private ConsumerRefreshStatus refreshStatus;
        private ConsumerRefreshTransitionType refreshTransitionType;
        private ConsumerRefreshLoadType refreshLoadType;

        public ConsumerRefreshMetrics(long refreshDurationMillis, ConsumerRefreshStatus refreshStatus, ConsumerRefreshTransitionType refreshTransitionType, ConsumerRefreshLoadType refreshLoadType) {
            this.refreshDurationMillis = refreshDurationMillis;
            this.refreshStatus = Objects.requireNonNull(refreshStatus);
            this.refreshTransitionType = refreshTransitionType;   // Transition type can be unknown for some failed snapshot or delta transition
            this.refreshLoadType = Objects.requireNonNull(refreshLoadType);
        }

        public long getRefreshDurationMillis() {
            return refreshDurationMillis;
        }
        public ConsumerRefreshStatus getRefreshStatus() {
            return refreshStatus;
        }
        public ConsumerRefreshTransitionType getRefreshTransitionType() {
            return refreshTransitionType;
        }
        public ConsumerRefreshLoadType getRefreshLoadType() {
            return refreshLoadType;
        }
    }

    public enum ConsumerRefreshTransitionType {
        Snapshot("snapshot"),       // refresh involved snapshot transition
        Delta("delta");             // refresh applied a delta transition

        final private String type;
        ConsumerRefreshTransitionType(String type) {
            this.type = type;
        }
    }

    public enum ConsumerRefreshLoadType {
        Init("init"),               // consumer's first-time initialized with a snapshot
        Subsequent("subsequent");   // consumer subsequent updated with either delta or snapshot

        final private String type;
        ConsumerRefreshLoadType(String type) {
            this.type = type;
        }
    }

    public enum ConsumerRefreshStatus {
        Success("success"),
        Failure("failure");

        final private String status;
        ConsumerRefreshStatus(String status) {
            this.status = status;
        }
    }


    public AbstractRefreshMetricsListener() {
        lastRefreshInstant = Instant.now(); // TODO: Is this an acceptable value for the first transition?
        refreshLoadCount = 0l;
        consecutiveSuccesses = 0l;
        consecutiveFailures = 0l;
        refreshStatus = ConsumerRefreshStatus.Success;  // TODO: Ok to initialize as success for first refresh?
        refreshLoadType = ConsumerRefreshLoadType.Init;
        refreshTransitionType = null;
    }

    @Override
    public final void refreshStarted(long currentVersion, long requestedVersion) {
        refreshStart = Instant.now();
        refreshTransitionType = null;   // Don't assume snapshot or delta transition, since a failed transition might
                                        // leave the value of this variable in an incorrect state. Hence the value is
                                        // reset at the start of every refresh.

        // TODO: If this is an init load then we can get visibility into bootstrap time
        // if (loadType.equals(ConsumerLoadType.Init)) {
        //
        // }
    }

    @Override
    public final void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
        Instant refreshEnd = Instant.now();

        long refreshDurationMillis = Duration.between(refreshStart, refreshEnd).toMillis();
        long timeSinceLastSuccessfulRefreshMillis = 0l;

        consecutiveSuccesses ++;
        consecutiveFailures = 0l;
        // reset the timer to zero to indicate a refresh happened.
        lastRefreshInstant = refreshEnd;
        refreshStatus = ConsumerRefreshStatus.Success;

        reportRefreshStats(new ConsumerRefreshMetrics(refreshDurationMillis, refreshStatus, refreshTransitionType, refreshLoadType));
        reportTimeSinceLastSuccessfulRefresh(timeSinceLastSuccessfulRefreshMillis);
        reportConsecutiveSuccesses(consecutiveSuccesses);
        reportConsecutiveFailures(consecutiveFailures);
    }

    @Override
    public final void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
        Instant refreshEnd = Instant.now();

        long refreshDurationMillis = Duration.between(refreshStart, refreshEnd).toMillis();
        long timeSinceLastSuccessfulRefreshMillis = Duration.between(lastRefreshInstant, refreshEnd).toMillis();

        consecutiveSuccesses = 0l;
        consecutiveFailures ++;
        refreshStatus = ConsumerRefreshStatus.Failure;

        reportRefreshStats(new ConsumerRefreshMetrics(refreshDurationMillis, refreshStatus, refreshTransitionType, refreshLoadType));
        reportTimeSinceLastSuccessfulRefresh(timeSinceLastSuccessfulRefreshMillis);
        reportConsecutiveSuccesses(consecutiveSuccesses);
        reportConsecutiveFailures(consecutiveFailures);
    }

    @Override
    public final void blobLoaded(HollowConsumer.Blob transition) {
        refreshLoadCount ++;

        if (refreshLoadCount > 1) {
            refreshLoadType = ConsumerRefreshLoadType.Subsequent;
        }
    }

    @Override
    public final void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        refreshTransitionType = ConsumerRefreshTransitionType.Snapshot;
    }

    @Override
    public final void deltaUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
        refreshTransitionType = ConsumerRefreshTransitionType.Delta;
    }

    @Override
    public final void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

    @Override
    public final void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        // no-op
    }

    // Below reporting methods can be overridden by classes for custom metric reporting behavior. This way more metrics
    // can be added without breaking existing clients.
    public void reportRefreshStats(ConsumerRefreshMetrics refreshStats) { }
    public void reportTimeSinceLastSuccessfulRefresh(long timeSinceLastSuccessfulRefreshMillis) { }
    public void reportConsecutiveSuccesses(long consecutiveSuccesses) { }
    public void reportConsecutiveFailures(long consecutiveFailures) { }

}
