/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Beta API subject to change.
 */
final class IncrementalWriteStateImpl implements HollowIncrementalProducer.IncrementalWriteState {

    protected final HollowProducer.WriteState hollowWriteState;
    protected final Map<RecordPrimaryKey, Integer> insertedOrdinals = new ConcurrentHashMap<>();

    public IncrementalWriteStateImpl(HollowProducer.WriteState hollowWriteState) {
        this.hollowWriteState = hollowWriteState;
    }

    private HollowPrimaryKeyIndex getPrimaryKeyIndex(String typeName) {
        HollowProducer.ReadState priorHollowReadState = getPriorState();
        if (priorHollowReadState == null) {
            return null;
        }

        PrimaryKey primaryKey = getPrimaryKey(typeName);
        HollowReadStateEngine readState = priorHollowReadState.getStateEngine();
        HollowSchema schema = readState.getSchema(typeName);
        if (schema == null) {
            return null;
        }

        return new HollowPrimaryKeyIndex(readState, primaryKey);
    }

    @Override
    public int addOrModify(Object o) {
        RecordPrimaryKey recordPrimaryKey = extractRecordPrimaryKey(o);
        deleteByPrimaryKey(recordPrimaryKey);

        int ordinal = hollowWriteState.add(o);

        insertedOrdinals.put(recordPrimaryKey, ordinal);
        return ordinal;
    }

    public int delete(Object o) {
        RecordPrimaryKey recordPrimaryKey = extractRecordPrimaryKey(o);
        return deleteByPrimaryKey(recordPrimaryKey);
    }

    public int deleteByPrimaryKey(String typeName, Object... id) {
        return deleteByPrimaryKey(new RecordPrimaryKey(typeName, id));
    }

    public int deleteByPrimaryKey(RecordPrimaryKey recordPrimaryKey) {
        String typeName = recordPrimaryKey.getType();

        Integer ordinal = insertedOrdinals.get(recordPrimaryKey);
        if (ordinal != null) {
            getStateEngine().getTypeState(typeName).removeOrdinalFromThisCycle(ordinal);
            return ordinal;
        }

        HollowPrimaryKeyIndex primaryKeyIndex = getPrimaryKeyIndex(typeName);
        if (primaryKeyIndex == null) {
            return -1;
        }

        ordinal = primaryKeyIndex.getMatchingOrdinal(recordPrimaryKey.getKey());

        if (ordinal >= 0) {
            getStateEngine().getTypeState(typeName).removeOrdinalFromThisCycle(ordinal);
        }

        return ordinal;
    }

    @Override
    public HollowObjectMapper getObjectMapper() {
        return hollowWriteState.getObjectMapper();
    }

    @Override
    public HollowWriteStateEngine getStateEngine() {
        return hollowWriteState.getStateEngine();
    }

    @Override
    public HollowProducer.ReadState getPriorState() {
        return hollowWriteState.getPriorState();
    }

    @Override
    public long getVersion() {
        return hollowWriteState.getVersion();
    }

    private PrimaryKey getPrimaryKey(String typeName) {
        HollowSchema schema = getObjectMapper().getStateEngine().getSchema(typeName);
        return ((HollowObjectSchema) schema).getPrimaryKey();
    }

    private RecordPrimaryKey extractRecordPrimaryKey(Object obj) {
        return getObjectMapper().extractPrimaryKey(obj);
    }
}
