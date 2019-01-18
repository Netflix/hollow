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
import com.netflix.hollow.api.consumer.HollowConsumer.TransitionAwareRefreshListener;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.tools.history.HollowHistoricalStateCreator;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * A class comprising much of the internal state of a {@link HollowConsumer}.  Not intended for external consumption.
 */
class HollowDataHolder {

    private final HollowReadStateEngine stateEngine;
    private final HollowAPIFactory apiFactory;
    private final HollowBlobReader reader;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;
    private final HollowConsumer.ObjectLongevityConfig objLongevityConfig;

    private HollowFilterConfig filter;

    private HollowAPI currentAPI;

    private WeakReference<HollowHistoricalStateDataAccess> priorHistoricalDataAccess;

    private long currentVersion = HollowConstants.VERSION_NONE;

    HollowDataHolder(HollowReadStateEngine stateEngine,
                            HollowAPIFactory apiFactory, 
                            FailedTransitionTracker failedTransitionTracker, 
                            StaleHollowReferenceDetector staleReferenceDetector, 
                            HollowConsumer.ObjectLongevityConfig objLongevityConfig) {
        this.stateEngine = stateEngine;
        this.apiFactory = apiFactory;
        this.reader = new HollowBlobReader(stateEngine);
        this.failedTransitionTracker = failedTransitionTracker;
        this.staleReferenceDetector = staleReferenceDetector;
        this.objLongevityConfig = objLongevityConfig;
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
        this.filter = filter;
        return this;
    }

    void update(HollowUpdatePlan updatePlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        if(failedTransitionTracker.anyTransitionWasFailed(updatePlan))
            throw new RuntimeException("Update plan contains known failing transition!");

        if(updatePlan.isSnapshotPlan())
            applySnapshotPlan(updatePlan, refreshListeners);
        else
            applyDeltaOnlyPlan(updatePlan, refreshListeners);
    }

    private void applySnapshotPlan(HollowUpdatePlan updatePlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        applySnapshotTransition(updatePlan.getSnapshotTransition(), refreshListeners);
            
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

    private void applySnapshotTransition(HollowConsumer.Blob snapshotBlob, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        try(InputStream is = snapshotBlob.getInputStream()) {
            applyStateEngineTransition(is, snapshotBlob, refreshListeners);
            initializeAPI();
            
            for(HollowConsumer.RefreshListener refreshListener : refreshListeners) {
                if (refreshListener instanceof TransitionAwareRefreshListener)
                    ((TransitionAwareRefreshListener)refreshListener).snapshotApplied(currentAPI, stateEngine, snapshotBlob.getToVersion());
            }
        } catch(Throwable t) {
            failedTransitionTracker.markFailedTransition(snapshotBlob);
            throw t;
        }
    }

    private void initializeAPI() {
        if(objLongevityConfig != null && objLongevityConfig.enableLongLivedObjectSupport()) {
            HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
            dataAccess.setDataAccess(stateEngine);
            currentAPI = apiFactory.createAPI(dataAccess);
        } else {
            currentAPI = apiFactory.createAPI(stateEngine);
        }
        
        if (staleReferenceDetector != null)
            staleReferenceDetector.newAPIHandle(currentAPI);
    }

    private void applyDeltaOnlyPlan(HollowUpdatePlan updatePlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        for(HollowConsumer.Blob blob : updatePlan) {
            applyDeltaTransition(blob, false, refreshListeners);
        }
    }

    private void applyDeltaTransition(HollowConsumer.Blob blob, boolean isSnapshotPlan, HollowConsumer.RefreshListener[] refreshListeners) throws Throwable {
        try(InputStream is = blob.getInputStream()) {
            applyStateEngineTransition(is, blob, refreshListeners);

            if(objLongevityConfig != null && objLongevityConfig.enableLongLivedObjectSupport()) {
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

            if(staleReferenceDetector != null && !staleReferenceDetector.isKnownAPIHandle(currentAPI))
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

    private void applyStateEngineTransition(InputStream is, HollowConsumer.Blob transition, HollowConsumer.RefreshListener[] refreshListeners) throws IOException {
        if(transition.isSnapshot()) {
            if(filter == null)
                reader.readSnapshot(is);
            else
                reader.readSnapshot(is, filter);
        } else {
            reader.applyDelta(is);
        }

        setVersion(transition.getToVersion());
        
        for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
            refreshListener.blobLoaded(transition);
    }

    private void setVersion(long version) {
        currentVersion = version;
    }

}
