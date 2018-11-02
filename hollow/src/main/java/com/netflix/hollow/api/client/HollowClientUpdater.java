/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * A class comprising much of the internal state of a {@link HollowConsumer}.  Not intended for external consumption.
 */
public class HollowClientUpdater {
    private static final Logger LOG = Logger.getLogger(HollowClientUpdater.class.getName());

    private volatile HollowDataHolder hollowDataHolderVolatile;

    private final HollowUpdatePlanner planner;
    private final CompletableFuture<Long> initialLoad;
    private boolean forceDoubleSnapshot = false;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;

    private final CopyOnWriteArrayList<HollowConsumer.RefreshListener> refreshListeners;
    private final HollowAPIFactory apiFactory;
    private final HollowObjectHashCodeFinder hashCodeFinder;
    private final HollowConsumer.ObjectLongevityConfig objectLongevityConfig;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final HollowConsumerMetrics metrics;
    private final HollowMetricsCollector<HollowConsumerMetrics> metricsCollector;

    private HollowFilterConfig filter;

    public HollowClientUpdater(HollowConsumer.BlobRetriever transitionCreator,
                               List<HollowConsumer.RefreshListener> refreshListeners,
                               HollowAPIFactory apiFactory,
                               HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig,
                               HollowObjectHashCodeFinder hashCodeFinder,
                               HollowConsumer.ObjectLongevityConfig objectLongevityConfig,
                               HollowConsumer.ObjectLongevityDetector objectLongevityDetector,
                               HollowConsumerMetrics metrics,
                               HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {
        this.planner = new HollowUpdatePlanner(transitionCreator, doubleSnapshotConfig);
        this.failedTransitionTracker = new FailedTransitionTracker();
        this.staleReferenceDetector = new StaleHollowReferenceDetector(objectLongevityConfig, objectLongevityDetector);
        // Create a copy of the listeners, removing any duplicates
        this.refreshListeners = new CopyOnWriteArrayList<>(
                refreshListeners.stream().distinct().toArray(HollowConsumer.RefreshListener[]::new));
        this.apiFactory = apiFactory;
        this.hashCodeFinder = hashCodeFinder;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
        this.objectLongevityConfig = objectLongevityConfig;
        this.staleReferenceDetector.startMonitoring();
        this.metrics = metrics;
        this.metricsCollector = metricsCollector;
        this.initialLoad = new CompletableFuture<>();
    }

    /**
     * Updates the state to the provided version. Returns true if the update was successful.
     * Note that this method is synchronized and it is the only method that modifies the
     * {@code hollowDataHolderVolatile}, so we don't need to worry about it changing out from
     * under us.
     */
    public synchronized boolean updateTo(long version) throws Throwable {
        if (version == getCurrentVersionId()) {
            if (version == HollowConstants.VERSION_NONE && hollowDataHolderVolatile == null) {
                LOG.warning("No versions to update to, initializing to empty state");
                // attempting to refresh, but no available versions - initialize to empty state
                hollowDataHolderVolatile = newHollowDataHolder();
                forceDoubleSnapshotNextUpdate(); // intentionally ignore doubleSnapshotConfig
            }
            return true;
        }

        // Take a snapshot of the listeners to ensure additions or removals may occur concurrently
        // but will not take effect until a subsequent refresh
        final HollowConsumer.RefreshListener[] localListeners =
                refreshListeners.toArray(new HollowConsumer.RefreshListener[0]);

        long beforeVersion = getCurrentVersionId();

        for (HollowConsumer.RefreshListener listener : localListeners)
            listener.refreshStarted(beforeVersion, version);

        try {
            HollowUpdatePlan updatePlan = shouldCreateSnapshotPlan()
                ? planner.planInitializingUpdate(version)
                : planner.planUpdate(hollowDataHolderVolatile.getCurrentVersion(), version,
                        doubleSnapshotConfig.allowDoubleSnapshot());

            if (updatePlan.destinationVersion() == HollowConstants.VERSION_NONE
                    && version != HollowConstants.VERSION_LATEST)
                throw new Exception("Could not create an update plan for version " + version);

            if (updatePlan.destinationVersion(version) == getCurrentVersionId())
                return true;

            if (updatePlan.isSnapshotPlan()) {
                if (hollowDataHolderVolatile == null || doubleSnapshotConfig.allowDoubleSnapshot()) {
                    hollowDataHolderVolatile = newHollowDataHolder();
                    hollowDataHolderVolatile.update(updatePlan, localListeners);
                    forceDoubleSnapshot = false;
                }
            } else {
                hollowDataHolderVolatile.update(updatePlan, localListeners);
            }

            for(HollowConsumer.RefreshListener refreshListener : localListeners)
                refreshListener.refreshSuccessful(beforeVersion, getCurrentVersionId(), version);

            metrics.updateTypeStateMetrics(getStateEngine(), version);
            if(metricsCollector != null)
                metricsCollector.collect(metrics);

            initialLoad.complete(getCurrentVersionId()); // only set the first time
            return getCurrentVersionId() == version;
        } catch(Throwable th) {
            forceDoubleSnapshotNextUpdate();
            metrics.updateRefreshFailed();
            if(metricsCollector != null)
                metricsCollector.collect(metrics);
            for(HollowConsumer.RefreshListener refreshListener : localListeners)
                refreshListener.refreshFailed(beforeVersion, getCurrentVersionId(), version, th);

            // intentionally omitting a call to initialLoad.completeExceptionally(th), for producers
            // that write often a consumer has a chance to try another snapshot that might succeed

            throw th;
        }
    }

    public void addRefreshListener(HollowConsumer.RefreshListener refreshListener) {
        refreshListeners.addIfAbsent(refreshListener);
    }

    public void removeRefreshListener(HollowConsumer.RefreshListener refreshListener) {
        refreshListeners.remove(refreshListener);
    }

    public long getCurrentVersionId() {
        HollowDataHolder hollowDataHolderLocal = hollowDataHolderVolatile;
        return hollowDataHolderLocal != null ? hollowDataHolderLocal.getCurrentVersion()
            : HollowConstants.VERSION_NONE;
    }

    public void forceDoubleSnapshotNextUpdate() {
        this.forceDoubleSnapshot = true;
    }

    /**
     * Whether or not a snapshot plan should be created. Visible for testing.
     */
    boolean shouldCreateSnapshotPlan() {
        return getCurrentVersionId() == HollowConstants.VERSION_NONE
            ||  (forceDoubleSnapshot && doubleSnapshotConfig.allowDoubleSnapshot());
    }

    private HollowDataHolder newHollowDataHolder() {
        return new HollowDataHolder(newStateEngine(), apiFactory,
                failedTransitionTracker, staleReferenceDetector,
                objectLongevityConfig).setFilter(filter);
    }

    private HollowReadStateEngine newStateEngine() {
        HollowDataHolder hollowDataHolderLocal = hollowDataHolderVolatile;
        if (hollowDataHolderLocal != null) {
            ArraySegmentRecycler existingRecycler =
                    hollowDataHolderLocal.getStateEngine().getMemoryRecycler();
            return new HollowReadStateEngine(hashCodeFinder, true, existingRecycler);
        }
        return new HollowReadStateEngine(hashCodeFinder);
    }

    public StackTraceRecorder getStaleReferenceUsageStackTraceRecorder() {
        return staleReferenceDetector.getStaleReferenceStackTraceRecorder();
    }

    public HollowReadStateEngine getStateEngine() {
        HollowDataHolder hollowDataHolderLocal = hollowDataHolderVolatile;
        return hollowDataHolderLocal == null ? null : hollowDataHolderLocal.getStateEngine();
    }

    public HollowAPI getAPI() {
        HollowDataHolder hollowDataHolderLocal = hollowDataHolderVolatile;
        return hollowDataHolderLocal == null ? null : hollowDataHolderLocal.getAPI();
    }

    public void setFilter(HollowFilterConfig filter) {
        this.filter = filter;
    }

    /**
     * Returns the number of failed snapshot transitions stored in the {@link FailedTransitionTracker}.
     */
    public int getNumFailedSnapshotTransitions() {
        return failedTransitionTracker.getNumFailedSnapshotTransitions();
    }

    /**
     * Returns the number of failed delta transitions stored in the {@link FailedTransitionTracker}.
     */
    public int getNumFailedDeltaTransitions() {
        return failedTransitionTracker.getNumFailedDeltaTransitions();
    }

    /**
     * Clear any failed transitions from the {@link FailedTransitionTracker}, so that they may be reattempted when an update is triggered.
     */
    public void clearFailedTransitions() {
        this.failedTransitionTracker.clear();
    }

    /**
     * Returns a future that will be completed with the version of data loaded when the initial load of data
     * has completed.
     */
    public CompletableFuture<Long> getInitialLoad() {
        return this.initialLoad;
    }
}
