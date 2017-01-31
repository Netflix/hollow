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
package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HollowHistoryKeyIndex {

    private final HollowHistory history;
    private final Map<String, HollowHistoryTypeKeyIndex> typeKeyIndexes;
    private final HollowWriteStateEngine writeStateEngine;
    private final HollowReadStateEngine readStateEngine;

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
        HollowHistoryTypeKeyIndex keyIdx = new HollowHistoryTypeKeyIndex(primaryKey, history.getLatestState(), writeStateEngine, readStateEngine);
        typeKeyIndexes.put(primaryKey.getType(), keyIdx);
    }

    public void indexTypeField(String type, String keyFieldPath) {
        typeKeyIndexes.get(type).addFieldIndex(keyFieldPath, history.getLatestState());
    }

    public Map<String, HollowHistoryTypeKeyIndex> getTypeKeyIndexes() {
        return typeKeyIndexes;
    }

    public void update(HollowReadStateEngine latestStateEngine, boolean isDelta) {
        boolean isInitialUpdate = !isInitialized();

        if(isInitialUpdate)
            initializeTypeIndexes(latestStateEngine);

        updateTypeIndexes(latestStateEngine, isDelta && !isInitialUpdate);

        roundTripStateEngine(isInitialUpdate);

        rehashKeys();
    }

    public boolean isInitialized() {
        return !readStateEngine.getTypeStates().isEmpty();
    }

    private void initializeTypeIndexes(HollowReadStateEngine latestStateEngine) {
        for(Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestStateEngine.getTypeState(entry.getKey());
            if(typeState == null)
                throw new RuntimeException("HollowHistory: Configured index type " + entry.getKey() + " does not exist!");
            entry.getValue().initialize(typeState);
        }
    }

    private void updateTypeIndexes(final HollowReadStateEngine latestStateEngine, final boolean isDelta) {
        SimultaneousExecutor executor = new SimultaneousExecutor();

        for(final Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            executor.execute(new Runnable() {
                public void run() {
                    HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestStateEngine.getTypeState(entry.getKey());
                    entry.getValue().update(typeState, isDelta);
                }
            });
        }

        executor.awaitUninterruptibly();
    }

    private void roundTripStateEngine(boolean isInitialUpdate) {
        try {
            if(isInitialUpdate) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
                writer.writeSnapshot(baos);
                HollowBlobReader reader = new HollowBlobReader(readStateEngine);
                reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
                writer.writeDelta(baos);
                HollowBlobReader reader = new HollowBlobReader(readStateEngine);
                reader.applyDelta(new ByteArrayInputStream(baos.toByteArray()));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            writeStateEngine.prepareForNextCycle();
        }
    }

    private void rehashKeys() {
        SimultaneousExecutor executor = new SimultaneousExecutor();

        for(final Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            executor.execute(new Runnable() {
                public void run() {
                    entry.getValue().hashRecordKeys();
                }
            });
        }

        executor.awaitUninterruptibly();
    }

}
