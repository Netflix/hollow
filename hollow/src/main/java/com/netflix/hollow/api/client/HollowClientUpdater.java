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

import com.netflix.hollow.api.custom.HollowAPI;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * A class comprising much of the internal state of a HollowClient.  Not intended for external consumption.
 * 
 * @author dkoszewnik
 *
 */
public class HollowClientUpdater {

    private final HollowUpdatePlanner planner;
    private HollowDataHolder hollowDataHolder;
    private boolean forceDoubleSnapshot = false;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;

    private final HollowUpdateListener updateListener;
    private final HollowAPIFactory apiFactory;
    private final HollowObjectHashCodeFinder hashCodeFinder;
    private final HollowClientMemoryConfig memoryConfig;

    private HollowFilterConfig filter;

    public HollowClientUpdater(HollowBlobRetriever transitionCreator, HollowUpdateListener updateListener, HollowAPIFactory apiFactory, HollowObjectHashCodeFinder hashCodeFinder, HollowClientMemoryConfig memoryConfig) {
        this.planner = new HollowUpdatePlanner(transitionCreator);
        this.failedTransitionTracker = new FailedTransitionTracker();
        this.staleReferenceDetector = new StaleHollowReferenceDetector(memoryConfig, updateListener);

        this.updateListener = updateListener;
        this.apiFactory = apiFactory;
        this.hashCodeFinder = hashCodeFinder;
        this.memoryConfig = memoryConfig;
        this.staleReferenceDetector.startMonitoring();
    }

    public synchronized boolean updateTo(long version) throws Throwable {
        if(version == getCurrentVersionId())
            return true;

        long beforeVersion = getCurrentVersionId();

        updateListener.refreshStarted(beforeVersion, version);

        try {
            HollowUpdatePlan updatePlan = planUpdate(version);

            if(updatePlan.destinationVersion() == Long.MIN_VALUE)
                throw new Exception("Could not create an update plan for version " + version);

            if(updatePlan.destinationVersion() == getCurrentVersionId())
                return true;

            if(updatePlan.isSnapshotPlan()) {
                if(hollowDataHolder == null || memoryConfig.allowDoubleSnapshot()) {
                    HollowReadStateEngine newStateEngine = newStateEngine();
                    HollowDataHolder newHollowDataHolder = new HollowDataHolder(newStateEngine, apiFactory, failedTransitionTracker, staleReferenceDetector, updateListener, memoryConfig);
                    newHollowDataHolder.setFilter(filter);
                    newHollowDataHolder.update(updatePlan);
                    hollowDataHolder = newHollowDataHolder;
                    forceDoubleSnapshot = false;
                }
            } else {
                hollowDataHolder.update(updatePlan);
            }

            updateListener.refreshCompleted(beforeVersion, getCurrentVersionId(), version);
            return getCurrentVersionId() == version;
        } catch(Throwable th) {
            forceDoubleSnapshotNextUpdate();
            updateListener.refreshFailed(beforeVersion, getCurrentVersionId(), version, th);
            throw th;
        }
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
        return planner.planUpdate(hollowDataHolder.getCurrentVersion(), version, memoryConfig.allowDoubleSnapshot());
    }

    private boolean shouldCreateSnapshotPlan() {
        return hollowDataHolder == null || (forceDoubleSnapshot && memoryConfig.allowDoubleSnapshot());
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

    public void setMaxDeltas(int maxDeltas) {
        this.planner.setMaxDeltas(maxDeltas);
    }
    
    public void clearFailedTransitions() {
        this.failedTransitionTracker.clear();
    }
}
