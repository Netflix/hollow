/*
 *  Copyright 2016-2021 Netflix, Inc.
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
import com.netflix.hollow.api.consumer.HollowConsumer.TransitionAwareRefreshListener;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.OptionalBlobPartInput;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.TypeFilter;
import com.netflix.hollow.tools.history.HollowHistoricalStateCreator;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class comprising much of the internal state of a {@link HollowConsumer}.  Not intended for external consumption.
 */
class HollowDataHolder {
    private static final Logger LOG = Logger.getLogger(HollowDataHolder.class.getName());

    private final HollowReadStateEngine stateEngine;
    private final HollowAPIFactory apiFactory;
    private final MemoryMode memoryMode;
    private final HollowBlobReader reader;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;
    private final HollowConsumer.ObjectLongevityConfig objLongevityConfig;
    private final HollowRepairApplier repairApplier;
    private final HollowConsumerMetrics metrics;
    private final com.netflix.hollow.core.read.engine.metrics.TypeMemoryProfiler memoryProfiler;

    private TypeFilter filter;

    private HollowAPI currentAPI;

    private WeakReference<HollowHistoricalStateDataAccess> priorHistoricalDataAccess;

    private long currentVersion = HollowConstants.VERSION_NONE;

    HollowDataHolder(HollowReadStateEngine stateEngine,
                            HollowAPIFactory apiFactory,
                            MemoryMode memoryMode,
                            HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig,
                            FailedTransitionTracker failedTransitionTracker,
                            StaleHollowReferenceDetector staleReferenceDetector,
                            HollowConsumer.ObjectLongevityConfig objLongevityConfig,
                            HollowRepairApplier repairApplier,
                            HollowConsumerMetrics metrics) {
        this.stateEngine = stateEngine;
        this.apiFactory = apiFactory;
        this.memoryMode = memoryMode;
        this.reader = new HollowBlobReader(stateEngine, memoryMode);
        this.doubleSnapshotConfig = doubleSnapshotConfig;
        this.failedTransitionTracker = failedTransitionTracker;
        this.staleReferenceDetector = staleReferenceDetector;
        this.objLongevityConfig = objLongevityConfig;
        this.repairApplier = repairApplier;
        this.metrics = metrics;
        this.memoryProfiler = new com.netflix.hollow.core.read.engine.metrics.TypeMemoryProfiler();
    }

    HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    HollowAPI getAPI() {
        return currentAPI;
    }

    long getCurrentVersion() {
        return currentVersion;
    }

    HollowDataHolder setFilter(HollowFilterConfig filter) {
        /*
         * This method is preserved for binary compat from before TypeFilter was introduced.
         */

        return setFilter((TypeFilter)filter);
    }

    HollowDataHolder setFilter(TypeFilter filter) {
        this.filter = filter;
        return this;
    }

    HollowDataHolder setSkipTypeShardUpdateWithNoAdditions(boolean skipTypeShardUpdateWithNoAdditions) {
        this.stateEngine.setSkipTypeShardUpdateWithNoAdditions(skipTypeShardUpdateWithNoAdditions);
        return this;
    }

    void update(HollowUpdatePlan updatePlan, HollowConsumer.RefreshListener[] refreshListeners,
            Runnable apiInitCallback) throws Throwable {
        // Only fail if double snapshot is configured.
        // This is a short term solution until it is decided to either remove this feature
        // or refine it.
        // If the consumer is configured to only follow deltas (no double snapshot) then
        // any failure to transition will cause the consumer to become "stuck" on stale data
        // unless the failed transitions are explicitly cleared or a new consumer is created.
        // A transition failure is very broad encompassing many forms of transitory failure,
        // such as network failures when accessing a blob, where the consumer might recover,
        // such as when a new delta is published.
        // Note that a refresh listener may also induce a failed transition, likely unknowingly,
        // by throwing an exception.
        if (doubleSnapshotConfig.allowDoubleSnapshot() && failedTransitionTracker.anyTransitionWasFailed(updatePlan)) {
            throw new RuntimeException("Update plan contains known failing transition!");
        }

        if (updatePlan.isSnapshotPlan()) {
            applySnapshotPlan(updatePlan, refreshListeners, apiInitCallback);
        } else {
            applyDeltaOnlyPlan(updatePlan, refreshListeners);
        }
    }

    private void applySnapshotPlan(HollowUpdatePlan updatePlan,
            HollowConsumer.RefreshListener[] refreshListeners,
            Runnable apiInitCallback) throws Throwable {
        applySnapshotTransition(updatePlan.getSnapshotTransition(), refreshListeners, apiInitCallback);
            
        for(HollowConsumer.Blob blob : updatePlan.getDeltaTransitions()) {
            applyDeltaTransition(blob, true, refreshListeners);
        }

        try {
            for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
                refreshListener.snapshotUpdateOccurred(currentAPI, stateEngine, updatePlan.destinationVersion());
        } catch(Throwable t) {
            failedTransitionTracker.markAllTransitionsAsFailed(updatePlan);
            throw t;
        }
    }

    private void applySnapshotTransition(HollowConsumer.Blob snapshotBlob,
            HollowConsumer.RefreshListener[] refreshListeners,
            Runnable apiInitCallback) throws Throwable {
        try (HollowBlobInput in = HollowBlobInput.modeBasedSelector(memoryMode, snapshotBlob);
             OptionalBlobPartInput optionalPartIn = snapshotBlob.getOptionalBlobPartInputs()) {
            applyStateEngineTransition(in, optionalPartIn, snapshotBlob, refreshListeners);
            initializeAPI(apiInitCallback);

            for (HollowConsumer.RefreshListener refreshListener : refreshListeners) {
                if (refreshListener instanceof TransitionAwareRefreshListener)
                    ((TransitionAwareRefreshListener)refreshListener).snapshotApplied(currentAPI, stateEngine, snapshotBlob.getToVersion());
            }
        } catch (Throwable t) {
            failedTransitionTracker.markFailedTransition(snapshotBlob);
            throw t;
        }
    }

    private void applyStateEngineTransition(HollowBlobInput in, OptionalBlobPartInput optionalPartIn, HollowConsumer.Blob transition, HollowConsumer.RefreshListener[] refreshListeners) throws IOException {
        // Before reading snapshot
        if (LOG.isLoggable(Level.FINE)) {
            memoryProfiler.startProfiling(stateEngine);
        }

        if(transition.isSnapshot()) {
            if(filter == null) {
                reader.readSnapshot(in, optionalPartIn);
            }
            else {
                reader.readSnapshot(in, optionalPartIn, filter);
            }
        } else {
            reader.applyDelta(in, optionalPartIn);
        }

        // After reading snapshot
        if (LOG.isLoggable(Level.FINE)) {
            com.netflix.hollow.core.read.engine.metrics.TypeMemoryProfiler.ProfileResult result =
                memoryProfiler.endProfiling();
            result.printSummary();
        }

        setVersion(transition.getToVersion());

        for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
            refreshListener.blobLoaded(transition);
    }

    private void initializeAPI(Runnable r) {
        if (objLongevityConfig.enableLongLivedObjectSupport()) {
            HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
            dataAccess.setDataAccess(stateEngine);
            currentAPI = apiFactory.createAPI(dataAccess);
        } else {
            currentAPI = apiFactory.createAPI(stateEngine);
        }
        staleReferenceDetector.newAPIHandle(currentAPI);
        try {
            r.run();
        } catch (Throwable t) {
            LOG.warning("Failed to execute API init callback: " + t);
        }
    }

    private void applyDeltaOnlyPlan(HollowUpdatePlan updatePlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        for(HollowConsumer.Blob blob : updatePlan) {
            applyDeltaTransition(blob, false, refreshListeners);
        }
    }

    private void applyDeltaTransition(HollowConsumer.Blob blob, boolean isSnapshotPlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        if (!memoryMode.equals(MemoryMode.ON_HEAP)) {
            LOG.warning("Skipping delta transition in shared-memory mode");
            return;
        }

        try (HollowBlobInput in = HollowBlobInput.modeBasedSelector(memoryMode, blob);
             OptionalBlobPartInput optionalPartIn = blob.getOptionalBlobPartInputs()) {
            applyStateEngineTransition(in, optionalPartIn, blob, refreshListeners);

            if(objLongevityConfig.enableLongLivedObjectSupport()) {
                HollowDataAccess previousDataAccess = currentAPI.getDataAccess();
                HollowHistoricalStateDataAccess priorState = new HollowHistoricalStateCreator(null).createBasedOnNewDelta(currentVersion, stateEngine);
                HollowProxyDataAccess newDataAccess = new HollowProxyDataAccess();
                newDataAccess.setDataAccess(stateEngine);
                currentAPI = apiFactory.createAPI(newDataAccess, currentAPI);

                if(previousDataAccess instanceof HollowProxyDataAccess)
                    ((HollowProxyDataAccess)previousDataAccess).setDataAccess(priorState);

                wireHistoricalStateChain(priorState);
            } else {
                if(currentAPI.getDataAccess() != stateEngine)
                    currentAPI = apiFactory.createAPI(stateEngine);

                priorHistoricalDataAccess = null;
            }

            if(!staleReferenceDetector.isKnownAPIHandle(currentAPI))
                staleReferenceDetector.newAPIHandle(currentAPI);

            for(HollowConsumer.RefreshListener refreshListener : refreshListeners) {
                if(!isSnapshotPlan)
                    refreshListener.deltaUpdateOccurred(currentAPI, stateEngine, blob.getToVersion());
                if (refreshListener instanceof TransitionAwareRefreshListener)
                    ((TransitionAwareRefreshListener)refreshListener).deltaApplied(currentAPI, stateEngine, blob.getToVersion());
            }

        } catch(Throwable t) {
            failedTransitionTracker.markFailedTransition(blob);
            throw t;
        }
    }

    private void wireHistoricalStateChain(HollowHistoricalStateDataAccess nextPriorState) {
        if(priorHistoricalDataAccess != null) {
            HollowHistoricalStateDataAccess dataAccess = priorHistoricalDataAccess.get();
            if(dataAccess != null) {
                dataAccess.setNextState(nextPriorState);
            }
        }

        priorHistoricalDataAccess = new WeakReference<HollowHistoricalStateDataAccess>(nextPriorState);
    }

    private void setVersion(long version) {
        currentVersion = version;
    }

    /**
     * Applies a repair transition using snapshot as source of truth.
     * Analyzes checksums to identify corruption, then reinitializes consumer state from snapshot.
     *
     * @param repairBlob the repair blob containing snapshot data
     * @param refreshListeners listeners to notify of repair completion
     * @throws Exception if repair fails
     */
    void applyRepairTransition(HollowConsumer.Blob repairBlob,
                               HollowConsumer.RefreshListener[] refreshListeners) throws Exception {
        long startTime = System.currentTimeMillis();
        long version = repairBlob.getToVersion();

        LOG.log(Level.INFO, "Applying REPAIR transition to version " + version);

        if (metrics != null) {
            metrics.recordRepairTriggered(version);
        }

        if (repairApplier == null) {
            throw new IllegalStateException("RepairApplier not configured");
        }

        byte[] snapshotBytes = cacheSnapshotBlob(repairBlob);
        HollowRepairApplier.RepairResult result = analyzeRepair(snapshotBytes);

        if (!result.isSuccess()) {
            throw new IllegalStateException("Repair analysis failed for version " + version);
        }

        replaceStateFromSnapshot(snapshotBytes, version);

        long duration = System.currentTimeMillis() - startTime;
        emitRepairMetrics(version, duration, result);

        int ordinalsRepaired = result.getOrdinalsRepairedByType().values().stream()
            .mapToInt(Integer::intValue).sum();
        LOG.log(Level.INFO, String.format(
            "Repair complete: %d types, %d ordinals repaired in %dms",
            result.getTypesNeedingRepair(), ordinalsRepaired, duration));

        notifyRepairListeners(refreshListeners, version);
    }

    private byte[] cacheSnapshotBlob(HollowConsumer.Blob repairBlob) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = repairBlob.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        return baos.toByteArray();
    }

    private HollowRepairApplier.RepairResult analyzeRepair(byte[] snapshotBytes) throws IOException {
        HollowReadStateEngine snapshotState = new HollowReadStateEngine();
        HollowBlobReader snapshotReader = new HollowBlobReader(snapshotState, memoryMode);

        try (InputStream is = new ByteArrayInputStream(snapshotBytes)) {
            if (filter == null) {
                snapshotReader.readSnapshot(is);
            } else {
                snapshotReader.readSnapshot(is, filter);
            }
        }

        return repairApplier.repair(stateEngine, snapshotState);
    }

    private void replaceStateFromSnapshot(byte[] snapshotBytes, long version) throws IOException {
        if (filter == null) {
            reader.readSnapshot(new ByteArrayInputStream(snapshotBytes));
        } else {
            reader.readSnapshot(new ByteArrayInputStream(snapshotBytes), filter);
        }

        try {
            updateAPIAfterRepair();
            setVersion(version);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to update API after repair - consumer in inconsistent state", e);
            throw new IllegalStateException("Repair replaced state but API update failed - consumer unusable", e);
        }
    }

    private void updateAPIAfterRepair() {
        if (currentAPI == null) {
            initializeAPIForRepair();
        } else if (objLongevityConfig.enableLongLivedObjectSupport()) {
            updateProxyDataAccess();
        } else {
            recreateAPIIfNeeded();
        }

        if (!staleReferenceDetector.isKnownAPIHandle(currentAPI)) {
            staleReferenceDetector.newAPIHandle(currentAPI);
        }
    }

    private void initializeAPIForRepair() {
        if (objLongevityConfig.enableLongLivedObjectSupport()) {
            HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
            dataAccess.setDataAccess(stateEngine);
            currentAPI = apiFactory.createAPI(dataAccess);
        } else {
            currentAPI = apiFactory.createAPI(stateEngine);
        }
    }

    private void updateProxyDataAccess() {
        HollowDataAccess dataAccess = currentAPI.getDataAccess();
        if (dataAccess instanceof HollowProxyDataAccess) {
            ((HollowProxyDataAccess) dataAccess).setDataAccess(stateEngine);
        } else {
            HollowProxyDataAccess newDataAccess = new HollowProxyDataAccess();
            newDataAccess.setDataAccess(stateEngine);
            currentAPI = apiFactory.createAPI(newDataAccess, currentAPI);
        }
    }

    private void recreateAPIIfNeeded() {
        if (currentAPI.getDataAccess() != stateEngine) {
            currentAPI = apiFactory.createAPI(stateEngine);
        }
    }

    private void emitRepairMetrics(long version, long duration, HollowRepairApplier.RepairResult result) {
        if (metrics == null) return;

        metrics.recordRepairDuration(version, duration);
        for (Map.Entry<String, Integer> entry : result.getOrdinalsRepairedByType().entrySet()) {
            metrics.recordRepairOrdinals(entry.getKey(), entry.getValue());
        }
    }

    private void notifyRepairListeners(HollowConsumer.RefreshListener[] refreshListeners, long version) {
        for (HollowConsumer.RefreshListener listener : refreshListeners) {
            if (listener instanceof TransitionAwareRefreshListener) {
                try {
                    ((TransitionAwareRefreshListener) listener)
                        .repairApplied(currentAPI, stateEngine, version);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Listener " + listener.getClass().getName() +
                        " failed after successful repair", e);
                }
            }
        }
    }

}
