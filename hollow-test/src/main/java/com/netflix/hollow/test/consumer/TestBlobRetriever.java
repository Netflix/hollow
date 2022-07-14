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
package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.api.consumer.HollowConsumer.HeaderBlob;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of a BlobRetriever which allows adding blobs and holds them all in
 * memory.
 */
public class TestBlobRetriever implements BlobRetriever {
    private final Map<Long, Blob> snapshots = new HashMap<>();
    private final Map<Long, Blob> deltas = new HashMap<>();
    private final Map<Long, Blob> reverseDeltas = new HashMap<>();
    private final Map<Long, HeaderBlob> headers = new HashMap<>();

    @Override
    public HeaderBlob retrieveHeaderBlob(long desiredVersion) {
        return headers.get(desiredVersion);
    }

    @Override
    public Blob retrieveSnapshotBlob(long desiredVersion) {
        Blob b = snapshots.get(desiredVersion);
        resetStream(b);
        return b;
    }

    @Override
    public Blob retrieveDeltaBlob(long currentVersion) {
        Blob b = deltas.get(currentVersion);
        resetStream(b);
        return b;
    }

    @Override
    public Blob retrieveReverseDeltaBlob(long currentVersion) {
        Blob b = reverseDeltas.get(currentVersion);
        resetStream(b);
        return b;
    }

    // so blob can be reused
    private void resetStream(Blob b) {
        try {
            if(b != null && b.getInputStream() != null) {
                b.getInputStream().reset();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to get and reset stream", e);
        }
    }

    public void addSnapshot(long desiredVersion, Blob transition) {
        snapshots.put(desiredVersion, transition);
    }

    public void addDelta(long currentVersion, Blob transition) {
        deltas.put(currentVersion, transition);
    }

    public void addReverseDelta(long currentVersion, Blob transition) {
        reverseDeltas.put(currentVersion, transition);
    }

    public void addHeader(long desiredVersion, HeaderBlob headerBlob) {
        headers.put(desiredVersion, headerBlob);
    }
}
