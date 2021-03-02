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
import com.netflix.hollow.core.util.HollowRecordCollection;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractHollowDataAccessor<T> {
    protected final String type;
    protected final PrimaryKey primaryKey;
    protected final HollowReadStateEngine rStateEngine;
    protected final HollowTypeReadState typeState;

    private BitSet removedOrdinals = new BitSet();
    private BitSet addedOrdinals = new BitSet();
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
        this.typeState = requireNonNull(rStateEngine.getTypeState(type),
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
     * Indicate whether Data Accessor contains prior state
     *
     * NOTE: This is critical since loading a Snapshot will not contain any information about changes from prior state
     *
     * @return true indicate it contains prior state
     */
    public boolean hasPriorState() {
        return !typeState.getPreviousOrdinals().isEmpty();
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

        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        BitSet currentOrdinals = typeState.getPopulatedOrdinals();

        // track removed ordinals
        removedOrdinals = new BitSet();
        removedOrdinals.or(previousOrdinals);
        removedOrdinals.andNot(currentOrdinals);

        // track added ordinals
        addedOrdinals = new BitSet();
        addedOrdinals.or(currentOrdinals);
        addedOrdinals.andNot(previousOrdinals);

        // track updated ordinals
        updatedRecords = new ArrayList<>();
        HollowPrimaryKeyValueDeriver keyDeriver = new HollowPrimaryKeyValueDeriver(primaryKey, stateEngine);
        HollowPrimaryKeyIndex removalsIndex = new HollowPrimaryKeyIndex(stateEngine, primaryKey, stateEngine.getMemoryRecycler(), removedOrdinals);

        { // Determine updated records (removed records and added back with different value)
            int addedOrdinal = addedOrdinals.nextSetBit(0);
            while (addedOrdinal != -1) {
                Object[] key = keyDeriver.getRecordKey(addedOrdinal);
                int removedOrdinal = removalsIndex.getMatchingOrdinal(key);

                if (removedOrdinal != -1) { // record was re-added after being removed = update
                    updatedRecords.add(new UpdatedRecordOrdinal(removedOrdinal, addedOrdinal));

                    // removedOrdinal && addedOrdinal is from an UPDATE so clear it from explicit tracking
                    addedOrdinals.clear(addedOrdinal);
                    removedOrdinals.clear(removedOrdinal);
                }

                addedOrdinal = addedOrdinals.nextSetBit(addedOrdinal + 1);
            }
        }
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

        return new HollowRecordCollection<T>(addedOrdinals) { @Override protected T getForOrdinal(int ordinal) {
                return getRecord(ordinal);
            }};
    }

    /**
     * @return only the Records that are Removed
     * @see #getUpdatedRecords()
     */
    public Collection<T> getRemovedRecords() {
        if (!isDataChangeComputed) computeDataChange();
        return new HollowRecordCollection<T>(removedOrdinals) { @Override protected T getForOrdinal(int ordinal) {
            return getRecord(ordinal);
        }};
    }

    /**
     * @return the Records that are Updated with both Before and After
     * @see UpdatedRecord
     */
    public Collection<UpdatedRecord<T>> getUpdatedRecords() {
        if (!isDataChangeComputed) computeDataChange();
        return updatedRecords;
    }

    private class UpdatedRecordOrdinal extends UpdatedRecord<T>{
        private final int before;
        private final int after;

        private UpdatedRecordOrdinal(int before, int after) {
            super(null, null);
            this.before = before;
            this.after = after;
        }

        public T getBefore() {
            return getRecord(before);
        }

        public T getAfter() { return getRecord(after);}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UpdatedRecordOrdinal that = (UpdatedRecordOrdinal) o;
            return before == that.before &&
                    after == that.after;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), before, after);
        }
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
            builder.append(getBefore());
            builder.append(", after=");
            builder.append(getAfter());
            builder.append("]");
            return builder.toString();
        }
    }
}
