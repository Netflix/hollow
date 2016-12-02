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

import com.netflix.hollow.tools.history.HollowHistoricalStateCreator;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * A class comprising much of the internal state of a HollowClient.  Not intended for external consumption.
 * 
 * @author dkoszewnik
 *
 */
public class HollowDataHolder {

    private final HollowReadStateEngine stateEngine;
    private final HollowAPIFactory apiFactory;
    private final HollowBlobReader reader;
    private final FailedTransitionTracker failedTransitionTracker;
    private final StaleHollowReferenceDetector staleReferenceDetector;
    private final HollowClientMemoryConfig clientConfig;

    private HollowFilterConfig filter;

    private HollowAPI currentAPI;

    private final HollowUpdateListener updateListener;

    private WeakReference<HollowHistoricalStateDataAccess> priorHistoricalDataAccess;

    private long currentVersion = Long.MIN_VALUE;

    public HollowDataHolder(HollowReadStateEngine stateEngine, HollowAPIFactory apiFactory, FailedTransitionTracker failedTransitionTracker, StaleHollowReferenceDetector staleReferenceDetector, HollowUpdateListener updateListener, HollowClientMemoryConfig clientConfig) {
        this.stateEngine = stateEngine;
        this.apiFactory = apiFactory;
        this.reader = new HollowBlobReader(stateEngine);
        this.failedTransitionTracker = failedTransitionTracker;
        this.staleReferenceDetector = staleReferenceDetector;
        this.updateListener = updateListener;
        this.clientConfig = clientConfig;
    }

    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    public HollowAPI getAPI() {
        return currentAPI;
    }

    public long getCurrentVersion() {
        return currentVersion;
    }

    public void setFilter(HollowFilterConfig filter) {
        this.filter = filter;
    }

    public void update(HollowUpdatePlan updatePlan) throws Throwable {
        if(failedTransitionTracker.anyTransitionWasFailed(updatePlan))
            throw new RuntimeException("Update plan contains known failing transition!");

        if(updatePlan.isSnapshotPlan())
            applyInitialTransitions(updatePlan);
        else
            applySubsequentTransitions(updatePlan);
    }

    private void applyInitialTransitions(HollowUpdatePlan updatePlan) throws Throwable {
        for(HollowBlob transition : updatePlan) {
            InputStream is = transition.getInputStream();

            try {
                applyTransition(is, transition);
            } catch(Throwable t) {
                failedTransitionTracker.markFailedTransition(transition);
                throw t;
            } finally {
                is.close();
            }
        }

        try {
            if(clientConfig.enableLongLivedObjectSupport()) {
                HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
                dataAccess.setDataAccess(stateEngine);
                currentAPI = apiFactory.createAPI(dataAccess);
            } else {
                currentAPI = apiFactory.createAPI(stateEngine);
            }

            updateListener.dataInitialized(currentAPI, stateEngine, updatePlan.destinationVersion());

            staleReferenceDetector.newAPIHandle(currentAPI);
        } catch(Throwable t) {
            failedTransitionTracker.markAllTransitionsAsFailed(updatePlan);
            throw t;
        }
    }

    private void applySubsequentTransitions(HollowUpdatePlan updatePlan) throws Throwable {
        for(HollowBlob transition : updatePlan) {
            InputStream is = transition.getInputStream();

            try {
                applyTransition(is, transition);

                if(clientConfig.enableLongLivedObjectSupport()) {
                    HollowDataAccess previousDataAccess = currentAPI.getDataAccess();
                    HollowHistoricalStateDataAccess priorState = new HollowHistoricalStateCreator(null).createBasedOnNewDelta(currentVersion, stateEngine);
                    HollowProxyDataAccess newDataAccess = new HollowProxyDataAccess();
                    newDataAccess.setDataAccess(stateEngine);
                    currentAPI = apiFactory.createAPI(newDataAccess, currentAPI);

                    updateListener.dataUpdated(currentAPI, stateEngine, transition.getToVersion());

                    if(previousDataAccess instanceof HollowProxyDataAccess)
                        ((HollowProxyDataAccess)previousDataAccess).setDataAccess(priorState);

                    wireHistoricalStateChain(priorState);
                } else {
                    if(currentAPI.getDataAccess() != stateEngine)
                        currentAPI = apiFactory.createAPI(stateEngine);
                    updateListener.dataUpdated(currentAPI, stateEngine, transition.getToVersion());

                    priorHistoricalDataAccess = null;
                }

                if(!staleReferenceDetector.isKnownAPIHandle(currentAPI))
                    staleReferenceDetector.newAPIHandle(currentAPI);

            } catch(Throwable t) {
                failedTransitionTracker.markFailedTransition(transition);
                throw t;
            } finally {
                is.close();
            }
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

    private void applyTransition(InputStream is, HollowBlob transition) throws IOException {
        if(transition.isSnapshot()) {
            if(filter == null)
                reader.readSnapshot(is);
            else
                reader.readSnapshot(is, filter);
        } else {
            reader.applyDelta(is);
        }

        setVersion(transition.getToVersion());
        updateListener.transitionApplied(transition);
    }

    private void setVersion(long version) {
        currentVersion = version;
    }

}
