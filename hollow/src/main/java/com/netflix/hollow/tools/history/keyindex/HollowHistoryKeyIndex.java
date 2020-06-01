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
package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HollowHistoryKeyIndex {

    private final HollowHistory history;
    private final Map<String, HollowHistoryTypeKeyIndex> typeKeyIndexes;
    private final HollowWriteStateEngine writeStateEngine;
    private HollowReadStateEngine readStateEngine;

    public HollowHistoryKeyIndex(HollowHistory history) {
        this.history = history;
        this.typeKeyIndexes = new HashMap<String, HollowHistoryTypeKeyIndex>();
        this.writeStateEngine = new HollowWriteStateEngine();
        this.readStateEngine = new HollowReadStateEngine();
    }

    public int numUniqueKeys(String type) {
        return readStateEngine.getTypeState(type).maxOrdinal() + 1;
    }

    public String getKeyDisplayString(String type, int keyOrdinal) {
        return typeKeyIndexes.get(type).getKeyDisplayString(keyOrdinal);
    }

    public int getRecordKeyOrdinal(HollowObjectTypeReadState typeState, int ordinal) {
        return typeKeyIndexes.get(typeState.getSchema().getName()).findKeyIndexOrdinal(typeState, ordinal);
    }

    public void addTypeIndex(String type, String... keyFieldPaths) {
        addTypeIndex(new PrimaryKey(type, keyFieldPaths));
    }

    public void addTypeIndex(PrimaryKey primaryKey) {
        addTypeIndex(primaryKey, history.getLatestState());
    }

    public HollowHistoryTypeKeyIndex addTypeIndex(PrimaryKey primaryKey, HollowDataset dataModel) {
        HollowHistoryTypeKeyIndex keyIdx = new HollowHistoryTypeKeyIndex(primaryKey, dataModel, writeStateEngine, readStateEngine);
        typeKeyIndexes.put(primaryKey.getType(), keyIdx);
        return keyIdx;
    }

    public void indexTypeField(String type, String keyFieldPath) {
        typeKeyIndexes.get(type).addFieldIndex(keyFieldPath, history.getLatestState());
    }

    public void indexTypeField(PrimaryKey primaryKey) {
        indexTypeField(primaryKey, history.getLatestState());
    }

    public void indexTypeField(PrimaryKey primaryKey, HollowDataset dataModel) {
        String type = primaryKey.getType();
        HollowHistoryTypeKeyIndex typeIndex = typeKeyIndexes.get(type);
        if (typeIndex==null) {
            typeIndex = addTypeIndex(primaryKey, dataModel);
        }

        for (String fieldPath : primaryKey.getFieldPaths()) {
            typeIndex.addFieldIndex(fieldPath, dataModel);
        }
    }

    public Map<String, HollowHistoryTypeKeyIndex> getTypeKeyIndexes() {
        return typeKeyIndexes;
    }

    public void update(HollowReadStateEngine latestStateEngine, boolean isDelta) {
        boolean isInitialUpdate = !isInitialized();

        initializeTypeIndexes(latestStateEngine);
        updateTypeIndexes(latestStateEngine, isDelta && !isInitialUpdate);

        HollowReadStateEngine newReadState = roundTripStateEngine(isInitialUpdate, !isDelta);
        if (newReadState != readStateEngine) {
            // New ReadState was created so let's update references to old one
            readStateEngine = newReadState;
            for(final Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
                entry.getValue().updateReadStateEngine(readStateEngine);
            }
        }

        rehashKeys();
    }

    public boolean isInitialized() {
        return !readStateEngine.getTypeStates().isEmpty();
    }

    private void initializeTypeIndexes(HollowReadStateEngine latestStateEngine) {
        for(Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            String type = entry.getKey();
            HollowHistoryTypeKeyIndex index = entry.getValue();
            if (index.isInitialized()) continue;

            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestStateEngine.getTypeState(type);
            if (typeState == null) continue;

            index.initialize(typeState);
        }
    }

    private void updateTypeIndexes(final HollowReadStateEngine latestStateEngine, final boolean isDelta) {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "update-type-indexes");

        for(final Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            executor.execute(() -> {
                HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestStateEngine.getTypeState(entry.getKey());
                entry.getValue().update(typeState, isDelta);
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private HollowReadStateEngine roundTripStateEngine(boolean isInitialUpdate, boolean isSnapshot) {
        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        // Use existing readStateEngine on initial update or delta;
        // otherwise, create new one to properly handle double snapshot
        HollowReadStateEngine newReadStateEngine = (isInitialUpdate || !isSnapshot)
                ? readStateEngine : new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(newReadStateEngine);

        // Use a pipe to write and read concurrently to avoid writing
        // to temporary files or allocating memory
        // @@@ for small states it's more efficient to sequentially write to
        // and read from a byte array but it is tricky to estimate the size
        SimultaneousExecutor executor = new SimultaneousExecutor(1, HollowHistoryKeyIndex.class, "round-trip");
        Exception pipeException = null;
        // Ensure read-side is closed after completion of read
        try (PipedInputStream is = new PipedInputStream(1 << 15)) {
            BufferedOutputStream out = new BufferedOutputStream(new PipedOutputStream(is));
            executor.execute(() -> {
                // Ensure write-side is closed after completion of write
                try (Closeable ac = out) {
                    if (isInitialUpdate || isSnapshot) {
                        writer.writeSnapshot(out);
                    } else {
                        writer.writeDelta(out);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            try (HollowBlobInput in = HollowBlobInput.serial(new BufferedInputStream(is))) {   // SNAP: fixed memory leak here, "bin" wasn't being closed
                if (isInitialUpdate || isSnapshot) {
                    reader.readSnapshot(in);
                } else {
                    reader.applyDelta(in);
                }
            }
        } catch (Exception e) {
            pipeException = e;
        }

        // Ensure no underlying writer exception is lost due to broken pipe
        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            if (pipeException == null) {
                throw new RuntimeException(e);
            }

            pipeException.addSuppressed(e);
        }
        if (pipeException != null)
            throw new RuntimeException(pipeException);

        writeStateEngine.prepareForNextCycle();
        return newReadStateEngine;
    }

    private void rehashKeys() {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "rehash-keys");

        for(final Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            executor.execute(() -> entry.getValue().hashRecordKeys());
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
