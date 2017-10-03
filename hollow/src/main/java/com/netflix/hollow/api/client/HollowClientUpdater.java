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

import java.util.List;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;

/**
 * A class comprising much of the internal state of a {@link HollowConsumer}.  Not intended for external consumption.
 */
public class HollowClientUpdater {

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

    public synchronized boolean updateTo(long version) throws Throwable {
        if(version == getCurrentVersionId())
            return true;

        long beforeVersion = getCurrentVersionId();

        for(HollowConsumer.RefreshListener listener : refreshListeners)
            listener.refreshStarted(beforeVersion, version);

        try {
            HollowUpdatePlan updatePlan = planUpdate(version);

            if(updatePlan.destinationVersion() == Long.MIN_VALUE && version != Long.MAX_VALUE)
                throw new Exception("Could not create an update plan for version " + version);

            if(updatePlan.destinationVersion(version) == getCurrentVersionId())
                return true;

            if(updatePlan.isSnapshotPlan()) {
                if(hollowDataHolder == null || doubleSnapshotConfig.allowDoubleSnapshot()) {
                    HollowReadStateEngine newStateEngine = newStateEngine();
                    HollowDataHolder newHollowDataHolder = new HollowDataHolder(newStateEngine, apiFactory, failedTransitionTracker, staleReferenceDetector, refreshListeners, objectLongevityConfig);
                    newHollowDataHolder.setFilter(filter);
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
        if(hollowDataHolder != null)
            return hollowDataHolder.getCurrentVersion();
        return Long.MIN_VALUE;
    }

    public void forceDoubleSnapshotNextUpdate() {
        this.forceDoubleSnapshot = true;
    }

    private HollowUpdatePlan planUpdate(long version) throws Exception {
        if(shouldCreateSnapshotPlan())
            return planner.planInitializingUpdate(version);
        return planner.planUpdate(hollowDataHolder.getCurrentVersion(), version, doubleSnapshotConfig.allowDoubleSnapshot());
    }

    private boolean shouldCreateSnapshotPlan() {
        return hollowDataHolder == null || (forceDoubleSnapshot && doubleSnapshotConfig.allowDoubleSnapshot());
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
        return hollowDataHolder.getStateEngine();
    }

    public HollowAPI getAPI() {
        return hollowDataHolder.getAPI();
    }

    public void setFilter(HollowFilterConfig filter) {
        this.filter = filter;
    }
    
    public void clearFailedTransitions() {
        this.failedTransitionTracker.clear();
    }
}
