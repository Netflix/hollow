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
 */
package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * Intended for internal use only - used by API code generator
 * @deprecated see {@link HashIndex}
 */
// TODO(timt): how to move to `API extends HollowAPI` without binary incompatiblity of access to the `api`
//             field in generated subclasses, e.g. `find*Matches(...)`
@Deprecated
public abstract class AbstractHollowHashIndex<API> {
    protected final HollowConsumer consumer;
    protected final String queryType;
    protected final String selectFieldPath;
    protected final String matchFieldPaths[];

    protected HollowHashIndex idx;
    protected API api;
    protected boolean isListenToDataRefresh;
    protected RefreshListener refreshListener;

    public AbstractHollowHashIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String queryType, String selectFieldPath, String... matchFieldPaths) {
        this.consumer = consumer;
        this.queryType = queryType;
        this.selectFieldPath = selectFieldPath;
        this.matchFieldPaths = matchFieldPaths;
        consumer.getRefreshLock().lock();
        try {
            this.api = castAPI(consumer.getAPI());
            this.idx = new HollowHashIndex(consumer.getStateEngine(), queryType, selectFieldPath, matchFieldPaths);
            this.refreshListener = new RefreshListener();

            if(isListenToDataRefreah) {
                listenToDataRefresh();
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("The HollowConsumer provided was not created with the PackageErgoTestAPI generated API class.");
        } finally {
            consumer.getRefreshLock().unlock();
        }

    }

    @SuppressWarnings("unchecked")
    private API castAPI(HollowAPI api) {
        return (API) api;
    }

    @Deprecated
    public boolean isListenToDataRefreah() {
        return isListenToDataRefresh;
    }

    @Deprecated
    public void listenToDataRefreah() {
        listenToDataRefresh();
    }

    public boolean isListenToDataRefresh() {
        return isListenToDataRefresh;
    }

    public void listenToDataRefresh() {
        if(isListenToDataRefresh) return;

        isListenToDataRefresh = true;
        idx.listenForDeltaUpdates();
        consumer.addRefreshListener(refreshListener);
    }

    public void detachFromDataRefresh() {
        isListenToDataRefresh = false;
        idx.detachFromDeltaUpdates();
        consumer.removeRefreshListener(refreshListener);
    }

    private class RefreshListener implements HollowConsumer.RefreshListener {
        @Override
        public void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
            idx.detachFromDeltaUpdates();
            idx = new HollowHashIndex(stateEngine, queryType, selectFieldPath, matchFieldPaths);
            idx.listenForDeltaUpdates();

            api = castAPI(refreshAPI);
        }

        @Override
        public void deltaUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
            api = castAPI(refreshAPI);
        }

        @Override
        public void refreshStarted(long currentVersion, long requestedVersion) {
        }

        @Override
        public void blobLoaded(HollowConsumer.Blob transition) {
        }

        @Override
        public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
        }

        @Override
        public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
        }
    }
}