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
import java.util.logging.Logger;

/**
 * A class comprising much of the internal state of a {@link HollowConsumer}.  Not intended for external consumption.
 */
public class HollowClientUpdater {
    private static final Logger LOG = Logger.getLogger(HollowClientUpdater.class.getName());

    private final HollowUpdatePlanner planner;
    private HollowDataHolder hollowDataHolder;
    private boolean forceDoubleSnapshot = false;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;

    private final List<HollowConsumer.RefreshListener> refreshListeners;
    private final HollowAPIFactory apiFactory;
    private final HollowObjectHashCodeFinder hashCodeFinder;
    private final HollowConsumer.ObjectLongevityConfig objectLongevityConfig;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final HollowConsumerMetrics metrics;
    private final HollowMetricsCollector metricsCollector;

    private HollowFilterConfig filter;

    public HollowClientUpdater(HollowConsumer.BlobRetriever transitionCreator,
                               List<HollowConsumer.RefreshListener> updateListeners,
                               HollowAPIFactory apiFactory,
                               HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig,
                               HollowObjectHashCodeFinder hashCodeFinder,
                               HollowConsumer.ObjectLongevityConfig objectLongevityConfig,
                               HollowConsumer.ObjectLongevityDetector objectLongevityDetector,
                               HollowConsumerMetrics metrics,
                               HollowMetricsCollector metricsCollector) {
        this.planner = new HollowUpdatePlanner(transitionCreator, doubleSnapshotConfig);
        this.failedTransitionTracker = new FailedTransitionTracker();
        this.staleReferenceDetector = new StaleHollowReferenceDetector(objectLongevityConfig, objectLongevityDetector);

        this.refreshListeners = updateListeners;
        this.apiFactory = apiFactory;
        this.hashCodeFinder = hashCodeFinder;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
        this.objectLongevityConfig = objectLongevityConfig;
        this.staleReferenceDetector.startMonitoring();
        this.metrics = metrics;
        this.metricsCollector = metricsCollector;
    }

    /**
     * Updates the state to the provided version. Returns true if the update was successful.
     */
    public synchronized boolean updateTo(long version) throws Throwable {
        if (version == getCurrentVersionId()) {
            if (version == HollowConstants.VERSION_NONE && hollowDataHolder == null) {
                LOG.warning("No versions to update to, initializing to empty state");
                // attempting to refresh, but no available versions - initialize to empty state
                hollowDataHolder = newHollowDataHolder();
                forceDoubleSnapshotNextUpdate(); // intentionally ignore doubleSnapshotConfig
                return true;
            } else { // already up to date
                return true;
            }
        }

        long beforeVersion = getCurrentVersionId();

        for (HollowConsumer.RefreshListener listener : refreshListeners)
            listener.refreshStarted(beforeVersion, version);

        try {
            HollowUpdatePlan updatePlan = planUpdate(version);

            if (updatePlan.destinationVersion() == HollowConstants.VERSION_NONE
                    && version != HollowConstants.VERSION_LATEST)
                throw new Exception("Could not create an update plan for version " + version);

            if (updatePlan.destinationVersion(version) == getCurrentVersionId())
                return true;

            if (updatePlan.isSnapshotPlan()) {
                if (hollowDataHolder == null || doubleSnapshotConfig.allowDoubleSnapshot()) {
                    HollowDataHolder newHollowDataHolder = newHollowDataHolder();
                    newHollowDataHolder.update(updatePlan);
                    hollowDataHolder = newHollowDataHolder;
                    forceDoubleSnapshot = false;
                }
            } else {
                hollowDataHolder.update(updatePlan);
            }

            for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
                refreshListener.refreshSuccessful(beforeVersion, getCurrentVersionId(), version);

            metrics.updateTypeStateMetrics(getStateEngine(), version);
            if(metricsCollector != null)
                metricsCollector.collect(metrics);
            return getCurrentVersionId() == version;
        } catch(Throwable th) {
            forceDoubleSnapshotNextUpdate();
            for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
                refreshListener.refreshFailed(beforeVersion, getCurrentVersionId(), version, th);
            metrics.updateRefreshFailed();
            if(metricsCollector != null)
                metricsCollector.collect(metrics);
            throw th;
        }
    }
    
    public void addRefreshListener(HollowConsumer.RefreshListener refreshListener) {
        refreshListeners.add(refreshListener);
    }

    public void removeRefreshListener(HollowConsumer.RefreshListener refreshListener) {
        refreshListeners.remove(refreshListener);
    }

    public long getCurrentVersionId() {
        return hollowDataHolder != null ? hollowDataHolder.getCurrentVersion()
            : HollowConstants.VERSION_NONE;
    }

    public void forceDoubleSnapshotNextUpdate() {
        this.forceDoubleSnapshot = true;
    }

    private HollowUpdatePlan planUpdate(long version) throws Exception {
        if(shouldCreateSnapshotPlan())
            return planner.planInitializingUpdate(version);
        return planner.planUpdate(hollowDataHolder.getCurrentVersion(), version, doubleSnapshotConfig.allowDoubleSnapshot());
    }

    /**
     * Whether or not a snapshot plan should be created. Visible for testing.
     */
    boolean shouldCreateSnapshotPlan() {
        return hollowDataHolder == null || getCurrentVersionId() == HollowConstants.VERSION_NONE
            ||  (forceDoubleSnapshot && doubleSnapshotConfig.allowDoubleSnapshot());
    }

    private HollowDataHolder newHollowDataHolder() {
        return new HollowDataHolder(newStateEngine(), apiFactory,
                failedTransitionTracker, staleReferenceDetector, refreshListeners,
                objectLongevityConfig).setFilter(filter);
    }

    private HollowReadStateEngine newStateEngine() {
        if(hollowDataHolder != null) {
            ArraySegmentRecycler existingRecycler = hollowDataHolder.getStateEngine().getMemoryRecycler();
            return new HollowReadStateEngine(hashCodeFinder, true, existingRecycler);
        }

        return new HollowReadStateEngine(hashCodeFinder);
    }

    public StackTraceRecorder getStaleReferenceUsageStackTraceRecorder() {
        return staleReferenceDetector.getStaleReferenceStackTraceRecorder();
    }

    public HollowReadStateEngine getStateEngine() {
        return hollowDataHolder == null ? null : hollowDataHolder.getStateEngine();
    }

    public HollowAPI getAPI() {
        return hollowDataHolder.getAPI();
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
}
