/*
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
 */
package com.netflix.hollow.api.consumer.data;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractHollowRecordManager<API extends HollowAPI, T> {
    protected final String type;
    protected final HollowConsumer consumer;
    protected final PrimaryKey primaryKey;

    protected API api;
    protected HollowReadStateEngine rStateEngine;
    protected DataChangeHolder<T> dataChangeHolder;

    public AbstractHollowRecordManager(HollowConsumer consumer, String type) {
        this(consumer, type, null);
    }

    public AbstractHollowRecordManager(HollowConsumer consumer, String type, PrimaryKey primaryKey) {
        consumer.getRefreshLock().lock();
        try {
            this.rStateEngine = consumer.getStateEngine();
            HollowSchema schema = rStateEngine.getTypeState(type).getSchema();
            if (schema instanceof HollowObjectSchema) {
                this.api = getAPI(consumer.getAPI());
                this.consumer = consumer;
                this.type = type;

                if (primaryKey == null) {
                    HollowObjectSchema oSchema = ((HollowObjectSchema) schema);
                    this.primaryKey = oSchema.getPrimaryKey();
                } else {
                    this.primaryKey = primaryKey;
                }
                if (this.primaryKey == null)
                    throw new RuntimeException(String.format("Unsupported DataType=%s with SchemaType=%s : %s", type, schema.getSchemaType(), "PrimaryKey is missing"));

                consumer.addRefreshListener(new DataRefreshListener());
            } else {
                throw new RuntimeException(String.format("Unsupported DataType=%s with SchemaType=%s : %s", type, schema.getSchemaType(), "Only supported type=" + SchemaType.OBJECT));
            }

            dataChangeHolder = computeDataChange(consumer.getCurrentVersionId(), type, rStateEngine, this.primaryKey);
        } finally {
            consumer.getRefreshLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private API getAPI(HollowAPI api) {
        return (API) api;
    }

    protected DataChangeHolder<T> computeDataChange(long version, String type, HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
        HollowTypeReadState typeState = stateEngine.getTypeDataAccess(type).getTypeState();

        // track removed ordinals
        BitSet removedOrdinals = new BitSet();
        removedOrdinals.or(typeState.getPreviousOrdinals());
        removedOrdinals.andNot(typeState.getPopulatedOrdinals());

        // track added ordinals
        BitSet addedOrdinals = new BitSet();
        addedOrdinals.or(typeState.getPopulatedOrdinals());
        addedOrdinals.andNot(typeState.getPreviousOrdinals());

        HollowPrimaryKeyValueDeriver keyDeriver = new HollowPrimaryKeyValueDeriver(primaryKey, stateEngine);
        HollowPrimaryKeyIndex removalsIndex = new HollowPrimaryKeyIndex(stateEngine, primaryKey, stateEngine.getMemoryRecycler(), removedOrdinals);

        List<T> addedRecords = new ArrayList<>();
        List<T> removedRecords = new ArrayList<>();
        List<UpdatedRecord<T>> updatedRecords = new ArrayList<>();

        BitSet updatedRecordOrdinals = new BitSet();

        // Determine added / updated records (removed records and added back with different value)
        int addedOrdinal = addedOrdinals.nextSetBit(0);
        while (addedOrdinal != -1) {
            Object[] key = keyDeriver.getRecordKey(addedOrdinal);
            int removedOrdinal = removalsIndex.getMatchingOrdinal(key);

            T addedRecord = getRecord(addedOrdinal);
            if (removedOrdinal != -1) { // record was re-added after being removed = update
                updatedRecordOrdinals.set(removedOrdinal);
                T removedRecord = getRecord(removedOrdinal);
                updatedRecords.add(new UpdatedRecord<T>(removedRecord, addedRecord));
            } else {
                addedRecords.add(addedRecord);
            }

            addedOrdinal = addedOrdinals.nextSetBit(addedOrdinal + 1);
        }

        // determine removed records
        int removedOrdinal = removedOrdinals.nextSetBit(0);
        while (removedOrdinal != -1) {
            if (updatedRecordOrdinals.get(removedOrdinal))
                continue; // ordinal was removed but record with same key was re-added so skip since it is being tracked already as updated record

            T removedRecord = getRecord(removedOrdinal);
            removedRecords.add(removedRecord);

            addedOrdinal = removedOrdinals.nextSetBit(addedOrdinal + 1);
        }

        return new DataChangeHolder<T>(version, removedRecords, addedRecords, updatedRecords);
    }

    public abstract T getRecord(int ordinal);

    public Collection<T> getAllRecords() {
        return new AllHollowRecordCollection<T>(rStateEngine.getTypeState(type)) {
            @Override
            protected T getForOrdinal(int ordinal) {
                return getRecord(ordinal);
            }
        };
    }

    public Collection<T> getAddedRecords() {
        return dataChangeHolder.getAddedRecords();
    }

    public Collection<T> getRemovedRecords() {
        return dataChangeHolder.getRemovedRecords();
    }

    public Collection<UpdatedRecord<T>> getUpdatedRecords() {
        return dataChangeHolder.getUpdatedRecords();
    }

    private class DataRefreshListener implements HollowConsumer.RefreshListener {
        @Override
        public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            api = getAPI(api);
            rStateEngine = stateEngine;
            dataChangeHolder = computeDataChange(version, type, stateEngine, primaryKey);
        }

        @Override
        public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            api = getAPI(api);
            rStateEngine = stateEngine;
            dataChangeHolder = computeDataChange(version, type, stateEngine, primaryKey);
        }

        @Override public void refreshStarted(long currentVersion, long requestedVersion) {}
        @Override public void blobLoaded(HollowConsumer.Blob transition) {}
        @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {}
        @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {}
    }

    private static class DataChangeHolder<T> {
        private final long version;
        private final List<T> removedRecords;
        private final List<T> addedRecords;
        private final List<UpdatedRecord<T>> updatedRecords;

        public DataChangeHolder(long version, List<T> removedRecords, List<T> addedRecords, List<UpdatedRecord<T>> updatedRecords) {
            this.version = version;
            this.removedRecords = Collections.unmodifiableList(removedRecords);
            this.addedRecords = Collections.unmodifiableList(addedRecords);
            this.updatedRecords = Collections.unmodifiableList(updatedRecords);
        }

        @SuppressWarnings("unused")
        public long getVersion() {
            return version;
        }

        public List<T> getRemovedRecords() {
            return removedRecords;
        }

        public List<T> getAddedRecords() {
            return addedRecords;
        }

        public List<UpdatedRecord<T>> getUpdatedRecords() {
            return updatedRecords;
        }
    }

    public static class UpdatedRecord<T> {
        private final T before;
        private final T after;

        public UpdatedRecord(T before, T after) {
            this.before = before;
            this.after = after;
        }

        public T getBefore() {
            return before;
        }

        public T getAfter() {
            return after;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((after == null) ? 0 : after.hashCode());
            result = prime * result + ((before == null) ? 0 : before.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            UpdatedRecord<?> other = (UpdatedRecord<?>) obj;
            if (after == null) {
                if (other.after != null)
                    return false;
            } else if (!after.equals(other.after))
                return false;
            if (before == null) {
                if (other.before != null)
                    return false;
            } else if (!before.equals(other.before))
                return false;
            return true;
        }
    }
}