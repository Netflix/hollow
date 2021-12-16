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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.AbstractVersionedBlob;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.api.consumer.HollowConsumer.HeaderBlob;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.hollow.core.read.OptionalBlobPartInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/// This InMemoryBlobStore is both a HollowProducer.Publisher and HollowConsumer.BlobRetriever!
public class InMemoryBlobStore implements BlobRetriever, Publisher {
    
    private final Set<String> optionalPartsToRetrieve;

    private Map<Long, Blob> snapshots;
    private Map<Long, Blob> deltas;
    private Map<Long, Blob> reverseDeltas;
    private Map<Long, HeaderBlob> headers;
    
    public InMemoryBlobStore() {
        this(null);
    }

    public InMemoryBlobStore(Set<String> optionalPartsToRetrieve) {
        this.snapshots = new HashMap<>();
        this.deltas = new HashMap<>();
        this.reverseDeltas = new HashMap<>();
        this.headers = new HashMap<>();
        this.optionalPartsToRetrieve = optionalPartsToRetrieve;
    }

    private HollowConsumer.AbstractVersionedBlob getDesiredVersion(long desiredVersion, Map<Long, ? extends AbstractVersionedBlob> map) {
        HollowConsumer.AbstractVersionedBlob snapshot = map.get(desiredVersion);
        if(snapshot != null)
            return snapshot;

        long greatestPriorSnapshotVersion = Long.MIN_VALUE;

        for(Map.Entry<Long, ? extends AbstractVersionedBlob> entry : map.entrySet()) {
            if(entry.getKey() > greatestPriorSnapshotVersion && entry.getKey() < desiredVersion)
                greatestPriorSnapshotVersion = entry.getKey();
        }

        return map.get(greatestPriorSnapshotVersion);
    }

    @Override
    public Blob retrieveSnapshotBlob(long desiredVersion) {
        return (Blob) getDesiredVersion(desiredVersion, snapshots);
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
    public HeaderBlob retrieveHeaderBlob(long desiredVersion) {
        return (HeaderBlob) getDesiredVersion(desiredVersion, headers);
    }

    @Override
    public void publish(HollowProducer.HeaderBlob headerBlob) {
        HeaderBlob consumerBlob = new HeaderBlob(headerBlob.getVersion()) {
            @Override
            public InputStream getInputStream() throws IOException {
                return headerBlob.newInputStream();
            }
        };
        headers.put(headerBlob.getVersion(), consumerBlob);
    }

    @Override
    public void publish(final HollowProducer.Blob blob) {
        Blob consumerBlob = new Blob(blob.getFromVersion(), blob.getToVersion()) {
            @Override
            public InputStream getInputStream() throws IOException {
                return blob.newInputStream();
            }

            @Override
            public OptionalBlobPartInput getOptionalBlobPartInputs() throws IOException {
                if(blob.getOptionalPartConfig() == null || optionalPartsToRetrieve == null)
                    return null;

                OptionalBlobPartInput parts = new OptionalBlobPartInput();
                for(String part : blob.getOptionalPartConfig().getParts()) {
                    if(optionalPartsToRetrieve.contains(part))
                        parts.addInput(part, blob.newOptionalPartInputStream(part));
                }

                if(parts.getPartNames().isEmpty())
                    return null;

                return parts;
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
