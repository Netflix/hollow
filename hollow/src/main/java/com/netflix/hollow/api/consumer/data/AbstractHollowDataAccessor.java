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
 */
package com.netflix.hollow.api.consumer.data;

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.api.consumer.HollowConsumer;
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

public abstract class AbstractHollowDataAccessor<T> {
    protected final String type;
    protected final PrimaryKey primaryKey;
    protected final HollowReadStateEngine rStateEngine;

    private List<T> removedRecords = Collections.emptyList();
    private List<T> addedRecords = Collections.emptyList();
    private List<UpdatedRecord<T>> updatedRecords = Collections.emptyList();

    private boolean isDataChangeComputed = false;

    public AbstractHollowDataAccessor(HollowConsumer consumer, String type) {
        this(consumer.getStateEngine(), type);
    }

    public AbstractHollowDataAccessor(HollowReadStateEngine rStateEngine, String type) {
        this(rStateEngine, type, (PrimaryKey) null);
    }

    public AbstractHollowDataAccessor(HollowReadStateEngine rStateEngine, String type, String... fieldPaths) {
        this(rStateEngine, type, new PrimaryKey(type, fieldPaths));
    }

    public AbstractHollowDataAccessor(HollowReadStateEngine rStateEngine, String type, PrimaryKey primaryKey) {
        this.rStateEngine = requireNonNull(rStateEngine, "read state required");
        HollowTypeReadState typeState = requireNonNull(rStateEngine.getTypeState(type),
                "type not loaded or does not exist in dataset; type=" + type);
        HollowSchema schema = typeState.getSchema();
        if (schema instanceof HollowObjectSchema) {
            this.type = type;

            if (primaryKey == null) {
                HollowObjectSchema oSchema = ((HollowObjectSchema) schema);
                this.primaryKey = oSchema.getPrimaryKey();
            } else {
                this.primaryKey = primaryKey;
            }
            if (this.primaryKey == null)
                throw new RuntimeException(String.format("Unsupported DataType=%s with SchemaType=%s : %s", type, schema.getSchemaType(), "PrimaryKey is missing"));

        } else {
            throw new RuntimeException(String.format("Unsupported DataType=%s with SchemaType=%s : %s", type, schema.getSchemaType(), "Only supported type=" + SchemaType.OBJECT));
        }
    }

    /**
     * Compute Data Change
     */
    public synchronized void computeDataChange() {
        if (isDataChangeComputed) return;

        computeDataChange(type, rStateEngine, primaryKey);
        isDataChangeComputed = true;
    }

    /**
     * @return true if data change has been computed
     */
    public boolean isDataChangeComputed() {
        return isDataChangeComputed;
    }

    protected void computeDataChange(String type, HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
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

        { // Determine added / updated records (removed records and added back with different value)
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
        }

        { // determine removed records
            int removedOrdinal = removedOrdinals.nextSetBit(0);
            while (removedOrdinal != -1) {
                // exclude records that was removed but re-added with different ordinal since that is considered as updated record
                if (!updatedRecordOrdinals.get(removedOrdinal)) {
                    T removedRecord = getRecord(removedOrdinal);
                    removedRecords.add(removedRecord);
                }

                removedOrdinal = removedOrdinals.nextSetBit(removedOrdinal + 1);
            }
        }

        this.removedRecords = Collections.unmodifiableList(removedRecords);
        this.addedRecords = Collections.unmodifiableList(addedRecords);
        this.updatedRecords = Collections.unmodifiableList(updatedRecords);
    }

    /**
     * @return the associated Type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the PrimaryKey that can uniquely identify a single record
     */
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param ordinal the ordinal
     * @return the Record at specified Ordinal
     */
    public abstract T getRecord(int ordinal);

    /**
     * @return all the available Record
     */
    public Collection<T> getAllRecords() {
        return new AllHollowRecordCollection<T>(rStateEngine.getTypeState(type)) {
            @Override
            protected T getForOrdinal(int ordinal) {
                return getRecord(ordinal);
            }
        };
    }

    /**
     * @return only the Records that are Added
     * @see #getUpdatedRecords()
     */
    public Collection<T> getAddedRecords() {
        if (!isDataChangeComputed) computeDataChange();
        return addedRecords;
    }

    /**
     * @return only the Records that are Removed
     * @see #getUpdatedRecords()
     */
    public Collection<T> getRemovedRecords() {
        if (!isDataChangeComputed) computeDataChange();
        return removedRecords;
    }

    /**
     * @return the Records that are Updated with both Before and After
     * @see UpdatedRecord
     */
    public Collection<UpdatedRecord<T>> getUpdatedRecords() {
        if (!isDataChangeComputed) computeDataChange();
        return updatedRecords;
    }

    /**
     * Keeps track of record before and after Update
     */
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

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("UpdatedRecord [before=");
            builder.append(before);
            builder.append(", after=");
            builder.append(after);
            builder.append("]");
            return builder.toString();
        }
    }
}