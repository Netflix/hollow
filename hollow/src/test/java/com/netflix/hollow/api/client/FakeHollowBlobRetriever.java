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

import com.netflix.hollow.api.client.HollowBlobRetriever;
import com.netflix.hollow.api.client.HollowBlob;

import java.util.HashMap;
import java.util.Map;

public class FakeHollowBlobRetriever implements HollowBlobRetriever {

    private final Map<Long, HollowBlob> snapshots;
    private final Map<Long, HollowBlob> deltas;
    private final Map<Long, HollowBlob> reverseDeltas;

    public FakeHollowBlobRetriever() {
        this.snapshots = new HashMap<Long, HollowBlob>();
        this.deltas = new HashMap<Long, HollowBlob>();
        this.reverseDeltas = new HashMap<Long, HollowBlob>();
    }

    @Override
    public HollowBlob retrieveSnapshotBlob(long desiredVersion) {
        return snapshots.get(desiredVersion);
    }

    @Override
    public HollowBlob retrieveDeltaBlob(long currentVersion) {
        return deltas.get(currentVersion);
    }

    @Override
    public HollowBlob retrieveReverseDeltaBlob(long currentVersion) {
        return reverseDeltas.get(currentVersion);
    }

    public void addSnapshot(long desiredVersion, HollowBlob transition) {
        snapshots.put(desiredVersion, transition);
    }

    public void addDelta(long currentVersion, HollowBlob transition) {
        deltas.put(currentVersion, transition);
    }

    public void addReverseDelta(long currentVersion, HollowBlob transition) {
        reverseDeltas.put(currentVersion, transition);
    }

}
