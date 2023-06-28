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
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.history.HollowHistory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A {@code HollowHistoryKeyIndex} index is used to track all records seen in all known states.
 * It achieves this by maintaining a growing readStateEngine. A delta transition applies incoming keys to this
 * readStateEngine, and a snapshot transition applies all the existing keys in readStateEngine and new keys
 * in the incoming snapshot into a new readStateEngine that is used moving forward.
 */
public class HollowHistoryKeyIndex {

    private final HollowHistory history;
    private final Map<String, HollowHistoryTypeKeyIndex> typeKeyIndexes;
    private boolean isInitialized;

    public HollowHistoryKeyIndex(HollowHistory history) {
        this.history = history;
        this.typeKeyIndexes = new HashMap<>();
    }

    public int numUniqueKeys(String type) {
        return typeKeyIndexes.get(type).getMaxIndexedOrdinal();
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
        HollowHistoryTypeKeyIndex prevKeyIdx = typeKeyIndexes.get(primaryKey.getType());
        HollowHistoryTypeKeyIndex keyIdx = new HollowHistoryTypeKeyIndex(primaryKey, dataModel);
        // retain any previous indexed fields
        if (prevKeyIdx != null) {
            for (int i = 0; i < prevKeyIdx.getKeyFields().length; i++) {
                if (prevKeyIdx.getKeyFieldIsIndexed()[i]) {
                    keyIdx.addFieldIndex(prevKeyIdx.getKeyFields()[i], dataModel);
                }
            }
        }
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

        // For all the types in the key index make sure a {@code HollowHistoryTypeKeyIndex} index is initialized (and
        // has a writeable write state engine)
        // The type index stores ordinals (in a sequence independent of how they existed in the read state) and the
        // value of the primary keys.
        initializeTypeIndexes(latestStateEngine);

        // This call updates the type key indexes of all types in this history key index.
        updateTypeIndexes(latestStateEngine, isDelta && !isInitialUpdate);
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void initializeTypeIndexes(HollowReadStateEngine latestStateEngine) {
        for(Map.Entry<String, HollowHistoryTypeKeyIndex> entry : typeKeyIndexes.entrySet()) {
            String type = entry.getKey();
            HollowHistoryTypeKeyIndex index = entry.getValue();
            if (index.isInitialized()) continue;

            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestStateEngine.getTypeState(type);
            if (typeState == null) continue;

            index.initializeKeySchema(typeState);
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
}
