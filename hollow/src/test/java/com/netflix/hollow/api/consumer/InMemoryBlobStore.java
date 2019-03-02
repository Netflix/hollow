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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/// This InMemoryBlobStore is both a HollowProducer.Publisher and HollowConsumer.BlobRetriever!
public class InMemoryBlobStore implements BlobRetriever, Publisher {
    
    private Map<Long, Blob> snapshots;
    private Map<Long, Blob> deltas;
    private Map<Long, Blob> reverseDeltas;
    
    public InMemoryBlobStore() {
        this.snapshots = new HashMap<Long, Blob>();
        this.deltas = new HashMap<Long, Blob>();
        this.reverseDeltas = new HashMap<Long, Blob>();
    }

    @Override
    public Blob retrieveSnapshotBlob(long desiredVersion) {
        Blob snapshot = snapshots.get(desiredVersion);
        if(snapshot != null)
            return snapshot;
        
        long greatestPriorSnapshotVersion = Long.MIN_VALUE;
        
        for(Map.Entry<Long, Blob> entry : snapshots.entrySet()) {
            if(entry.getKey() > greatestPriorSnapshotVersion && entry.getKey() < desiredVersion)
                greatestPriorSnapshotVersion = entry.getKey();
        }
        
        return snapshots.get(greatestPriorSnapshotVersion);
    }

    @Override
    public Blob retrieveDeltaBlob(long currentVersion) {
        return deltas.get(currentVersion);
    }

    @Override
    public Blob retrieveReverseDeltaBlob(long currentVersion) {
        return reverseDeltas.get(currentVersion);
    }

    
    
    @Override
    public void publish(final HollowProducer.Blob blob) {
        Blob consumerBlob = new Blob(blob.getFromVersion(), blob.getToVersion()) {
            @Override
            public InputStream getInputStream() throws IOException {
                return blob.newInputStream();
            }
        };
        
        switch(blob.getType()) {
        case SNAPSHOT:
            snapshots.put(blob.getToVersion(), consumerBlob);
            break;
        case DELTA:
            deltas.put(blob.getFromVersion(), consumerBlob);
            break;
        case REVERSE_DELTA:
            reverseDeltas.put(blob.getFromVersion(), consumerBlob);
            break;
        }
    }
    
    public void removeSnapshot(long version) {
        snapshots.remove(version);
    }
    
}
