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
package com.netflix.hollow.history.ui;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.history.HollowHistory;

/**
 * This can listener can be attached to either (a) a single consumer that always transitions in the forward direction of
 * increasing versions (v1, v2, v3, etc.), or (b) to two consumers that start at the same version but one always transitions
 * in the forward direction and the other always transitions in the reverse direction.
 *
 * This class synchronizes modifications to HollowHistory to prevent the two consumers (if applicable) from concurrently
 * invoking modifications on the underlying HollowHistory object.
 */
public class HollowHistoryRefreshListener extends HollowConsumer.AbstractRefreshListener {

    private final HollowHistory history;

    public HollowHistoryRefreshListener(HollowHistory history) {
        this.history = history;
    }

    @Override
    public synchronized void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        history.doubleSnapshotOccurred(stateEngine, version);
    }

    @Override
    public synchronized void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        if(stateEngine == history.getOldestState()) {
            history.reverseDeltaOccurred(version);
        } else {
            history.deltaOccurred(version);
        }
    }

    @Override
    public void refreshStarted(long currentVersion, long requestedVersion) {
    }

    @Override
    public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
    }

    @Override
    public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
    }

    @Override
    public void blobLoaded(Blob transition) {
    }

}