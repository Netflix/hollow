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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.RandomAccess;

@SuppressWarnings("deprecation")
class HollowClientConsumerBridge {
    
    static HollowConsumer.BlobRetriever consumerBlobRetrieverFor(final HollowBlobRetriever blobRetriever) {
        return new HollowConsumer.BlobRetriever() {
            
            @Override
            public Blob retrieveSnapshotBlob(long desiredVersion) {
                final HollowBlob blob = blobRetriever.retrieveSnapshotBlob(desiredVersion);
                if(blob == null)
                    return null;
                
                return new HollowConsumer.Blob(blob.getFromVersion(), blob.getToVersion()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return blob.getInputStream();
                    }

                    @Override
                    public File getFile() throws IOException {
                        return blob.getFile();
                    }
                };
            }
            
            @Override
            public Blob retrieveDeltaBlob(long currentVersion) {
                final HollowBlob blob = blobRetriever.retrieveDeltaBlob(currentVersion);
                if(blob == null)
                    return null;
                
                return new HollowConsumer.Blob(blob.getFromVersion(), blob.getToVersion()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return blob.getInputStream();
                    }
                };
            }
            
            @Override
            public Blob retrieveReverseDeltaBlob(long currentVersion) {
                final HollowBlob blob = blobRetriever.retrieveReverseDeltaBlob(currentVersion);
                if(blob == null)
                    return null;
                
                return new HollowConsumer.Blob(blob.getFromVersion(), blob.getToVersion()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return blob.getInputStream();
                    }
                };
            }
        };
    }
    
    static HollowConsumer.RefreshListener consumerRefreshListenerFor(final HollowUpdateListener listener) {
            
        return new HollowConsumer.AbstractRefreshListener() {

            @Override
            public void refreshStarted(long currentVersion, long requestedVersion) {
                listener.refreshStarted(currentVersion, requestedVersion);
            }

            @Override
            public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
                listener.dataInitialized(api, stateEngine, version);
            }
            
            @Override
            public void blobLoaded(final HollowConsumer.Blob transition) {
                listener.transitionApplied(new HollowBlob(transition.getFromVersion(), transition.getToVersion()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return transition.getInputStream();
                    }
                    @Override
                    public File getFile() throws IOException {
                        return transition.getFile();
                    }
                });
            }

            @Override
            public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
                listener.dataUpdated(api, stateEngine, version);
            }

            @Override
            public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
                listener.refreshCompleted(beforeVersion, afterVersion, requestedVersion);
            }
            
            @Override
            public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
                listener.refreshFailed(beforeVersion, afterVersion, requestedVersion, failureCause);
            }
        };
    }
    
    static HollowClientDoubleSnapshotConfig doubleSnapshotConfigFor(HollowClientMemoryConfig memoryConfig) {
        return new HollowClientDoubleSnapshotConfig(memoryConfig);
    }

    static class HollowClientDoubleSnapshotConfig implements HollowConsumer.DoubleSnapshotConfig {
        
        private final HollowClientMemoryConfig clientMemCfg;
        private int maxDeltasBeforeDoubleSnapshot = 32;
        
        private HollowClientDoubleSnapshotConfig(HollowClientMemoryConfig clientMemCfg) {
            this.clientMemCfg = clientMemCfg;
        }
        
        @Override
        public boolean allowDoubleSnapshot() {
            return clientMemCfg.allowDoubleSnapshot();
        }

        @Override
        public int maxDeltasBeforeDoubleSnapshot() {
            return maxDeltasBeforeDoubleSnapshot;
        }
        
        public void setMaxDeltasBeforeDoubleSnapshot(int maxDeltas) {
            this.maxDeltasBeforeDoubleSnapshot = maxDeltas;
        }
        
    }

}
