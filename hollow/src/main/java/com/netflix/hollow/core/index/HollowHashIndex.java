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
package com.netflix.hollow.core.index;

import static com.netflix.hollow.core.index.FieldPaths.FieldPathException.ErrorKind.NOT_BINDABLE;
import static java.util.Objects.requireNonNull;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowHashIndexField.FieldPathSegment;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A HollowHashIndex is used for indexing non-primary-key data.  This type of index can map multiple keys to a single matching record, and/or
 * multiple records to a single key.
 * <p>
 * The field definitions in a hash key may be hierarchical (traverse multiple record types) via dot-notation.  For example,
 * the field definition <i>actors.element.actorId</i> may be used to traverse a child <b>LIST</b> or <b>SET</b> type record referenced by the field
 * <i>actors</i>, each elements contained therein, and finally each actors <i>actorId</i> field.
 */
public class HollowHashIndex implements HollowTypeStateListener {
    private static final Logger LOG = Logger.getLogger(HollowHashIndex.class.getName());

    private volatile HollowHashIndexState hashStateVolatile;

    private final HollowDataAccess hollowDataAccess;
    private final HollowObjectTypeDataAccess typeState;
    private final String type;
    private final String selectField;
    private final String[] matchFields;

    /**
     * This constructor is for binary-compatibility for code compiled against
     * older builds. 
     *
     * @param stateEngine The state engine to index
     * @param type The query starts with the specified type
     * @param selectField The query will select records at this field (specify "" to select the specified type).
     * The selectField may span collection elements and/or map keys or values, which can result in multiple matches per record of the specified start type.
     * @param matchFields The query will match on the specified match fields.  The match fields may span collection elements and/or map keys or values.
     */
    public HollowHashIndex(HollowReadStateEngine stateEngine, String type, String selectField, String... matchFields) {
        this((HollowDataAccess) stateEngine, type, selectField, matchFields);
    }

    /**
     * Define a {@link HollowHashIndex}.
     *
     * @param hollowDataAccess The state engine to index
     * @param type The query starts with the specified type
     * @param selectField The query will select records at this field (specify "" to select the specified type).
     * The selectField may span collection elements and/or map keys or values, which can result in multiple matches per record of the specified start type.
     * @param matchFields The query will match on the specified match fields.  The match fields may span collection elements and/or map keys or values.
     */
    public HollowHashIndex(HollowDataAccess hollowDataAccess, String type, String selectField, String... matchFields) {
        requireNonNull(type, "Hollow Hash Index creation failed because type was null");
        requireNonNull(hollowDataAccess, "Hollow Hash Index creation on type [" + type
                + "] failed because read state wasn't initialized");

        this.hollowDataAccess = hollowDataAccess;
        this.type = type;
        this.typeState = (HollowObjectTypeDataAccess) hollowDataAccess.getTypeDataAccess(type);
        this.selectField = selectField;
        this.matchFields = matchFields;

        if (typeState == null) {
            LOG.log(Level.WARNING, "Index initialization for " + this + " failed because type "
                    + type + " was not found in read state");
            return;
        }
        reindexHashIndex();
    }

    /**
     * Recreate the hash index entirely
     */
    private void reindexHashIndex() {
        HollowHashIndexBuilder builder;
        try {
            builder = new HollowHashIndexBuilder(hollowDataAccess, type, selectField, matchFields);
        } catch (FieldPaths.FieldPathException e) {
            if (e.error == NOT_BINDABLE) {
                LOG.log(Level.WARNING, "Index initialization for " + this
                        + " failed because one of the match fields could not be bound to a type in"
                        + " the read state");
                this.hashStateVolatile = null;
                return;
            } else {
                throw e;
            }
        }

        builder.buildIndex();
        this.hashStateVolatile = new HollowHashIndexState(builder);
    }


    /**
     * Query the index.
     *
     * @param query the query
     * @return the hash index result to gather the matched ordinals. A {@code null} value indicated no matches were
     * found.
     */
    public HollowHashIndexResult findMatches(Object... query) {
        if (hashStateVolatile == null) {
            throw new IllegalStateException(this + " wasn't initialized");
        }
        int hashCode = 0;

        for(int i=0;i<query.length;i++) {
            if(query[i] == null)
                throw new IllegalArgumentException("querying by null unsupported; i=" + i);
            hashCode ^= HashCodes.hashInt(keyHashCode(query[i], i));
        }

        HollowHashIndexResult result;
        HollowHashIndexState hashState;
        do {
            result = null;
            hashState = hashStateVolatile;
            long bucket = hashCode & hashState.getMatchHashMask();
            long hashBucketBit = bucket * hashState.getBitsPerMatchHashEntry();
            boolean bucketIsEmpty = hashState.getMatchHashTable().getElementValue(hashBucketBit, hashState.getBitsPerTraverserField()[0]) == 0;

            while (!bucketIsEmpty) {
                if (matchIsEqual(hashState.getMatchHashTable(), hashBucketBit, query)) {
                    int selectSize = (int) hashState.getMatchHashTable().getElementValue(hashBucketBit + hashState.getBitsPerMatchHashKey(), hashState.getBitsPerSelectTableSize());
                    long selectBucketPointer = hashState.getMatchHashTable().getElementValue(hashBucketBit + hashState.getBitsPerMatchHashKey() + hashState.getBitsPerSelectTableSize(), hashState.getBitsPerSelectTablePointer());

                    result = new HollowHashIndexResult(hashState, selectBucketPointer, selectSize);
                    break;
                }

                bucket = (bucket + 1) & hashState.getMatchHashMask();
                hashBucketBit = bucket * hashState.getBitsPerMatchHashEntry();
                bucketIsEmpty = hashState.getMatchHashTable().getElementValue(hashBucketBit, hashState.getBitsPerTraverserField()[0]) == 0;
            }
        } while (hashState != hashStateVolatile);

        return result;
    }

    private int keyHashCode(Object key, int fieldIdx) {
        HollowHashIndexState hashState = hashStateVolatile;
        switch(hashState.getMatchFields()[fieldIdx].getFieldType()) {
        case BOOLEAN:
            return HollowReadFieldUtils.booleanHashCode((Boolean)key);
        case DOUBLE:
            return HollowReadFieldUtils.doubleHashCode((Double) key);
        case FLOAT:
            return HollowReadFieldUtils.floatHashCode((Float) key);
        case INT:
            return HollowReadFieldUtils.intHashCode((Integer) key);
        case LONG:
            return HollowReadFieldUtils.longHashCode((Long) key);
        case REFERENCE:
            return (Integer) key;
        case BYTES:
            return HashCodes.hashCode((byte[])key);
        case STRING:
            return HashCodes.hashCode((String)key);
        }

        throw new IllegalArgumentException("I don't know how to hash a " + hashState.getMatchFields()[fieldIdx].getFieldType());
    }

    private boolean matchIsEqual(FixedLengthElementArray matchHashTable, long hashBucketBit, Object[] query) {
        HollowHashIndexState hashState = hashStateVolatile;
        for(int i = 0; i< hashState.getMatchFields().length; i++) {
            HollowHashIndexField field = hashState.getMatchFields()[i];
            int hashOrdinal = (int)matchHashTable.getElementValue(hashBucketBit + hashState.getOffsetPerTraverserField()[field.getBaseIteratorFieldIdx()], hashState.getBitsPerTraverserField()[field.getBaseIteratorFieldIdx()]) - 1;

            FieldPathSegment[] fieldPath = field.getSchemaFieldPositionPath();

            if(fieldPath.length == 0) {
                if (!query[i].equals(hashOrdinal))
                    return false;
            } else {
                for(int j=0;j<fieldPath.length - 1;j++) {
                    hashOrdinal = fieldPath[j].getOrdinalForField(hashOrdinal);
                    // Cannot find nested ordinal for null parent
                    if(hashOrdinal == HollowConstants.ORDINAL_NONE) {
                        break;
                    }
                }

                FieldPathSegment lastPathElement = fieldPath[fieldPath.length - 1];
                if(hashOrdinal == HollowConstants.ORDINAL_NONE || !HollowReadFieldUtils.fieldValueEquals(lastPathElement.getObjectTypeDataAccess(), hashOrdinal, lastPathElement.getSegmentFieldPosition(), query[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Once called, this HollowHashIndex will be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * This method should be called <b>before</b> any subsequent deltas occur after the index is created.
     * <p>
     * In order to prevent memory leaks, if this method is called and the index is no longer needed, call detachFromDeltaUpdates() before
     * discarding the index.
     * <p>
     * Note that this index does not listen on snapshot update. If a snapshot update occurs this index will
     * NOT return the latest data in the consumer and after 2 updates it could start returning corrupt results.
     * If double-snapshot updates are expected the caller must detach this index instance and initialize a new one. See
     * implementation of {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} for a reference implementation.
     */
    public void listenForDeltaUpdates() {
        if (typeState == null) {
            return;
        }
        if (!(typeState instanceof HollowObjectTypeReadState))
            throw new IllegalStateException("Cannot listen for delta updates when objectTypeDataAccess is a " + typeState.getClass().getSimpleName() + ". Is this index participating in object longevity?");

        ((HollowObjectTypeReadState) typeState).addListener(this);
    }

    /**
     * Once called, this HollowHashIndex will no longer be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * Call this method before discarding indexes which are currently listening for delta updates.
     */
    public void detachFromDeltaUpdates() {
        if (typeState == null) {
            return;
        }
        if ((typeState instanceof HollowObjectTypeReadState))
            ((HollowObjectTypeReadState) typeState).removeListener(this);
    }

    @Override
    public void beginUpdate() { }

    @Override
    public void addedOrdinal(int ordinal) { }

    @Override
    public void removedOrdinal(int ordinal) { }

    @Override
    public void endUpdate() {
        if (hashStateVolatile == null) {
            return;
        }
        reindexHashIndex();
    }

    /**
     * @return state engine.
     * @throws ClassCastException thrown if the underlying hollowDataAccess is not a state engine. This occurs if the
     * index was created from a consumer with hollow object longevity enabled.
     */
    public HollowReadStateEngine getStateEngine() {
        return (HollowReadStateEngine) hollowDataAccess;
    }

    public HollowDataAccess getHollowDataAccess() {
        return hollowDataAccess;
    }

    public String getType() {
        return type;
    }

    public String getSelectField() {
        return selectField;
    }

    public String[] getMatchFields() {
        return matchFields;
    }

    public long approxHeapFootprintInBytes() {
        HollowHashIndexState hashState = hashStateVolatile;
        if (hashState == null) {
            return 0;
        }
        return hashState.getMatchHashTable().approxHeapFootprintInBytes() +
          hashState.getSelectHashArray().approxHeapFootprintInBytes();
    }

    protected static class HollowHashIndexState {

        final FixedLengthElementArray selectHashArray;
        final int bitsPerSelectHashEntry;
        private final FixedLengthElementArray matchHashTable;
        private final HollowHashIndexField[] matchFields;
        private final int matchHashMask;
        private final int bitsPerMatchHashKey;
        private final int bitsPerMatchHashEntry;
        private final int[] bitsPerTraverserField;
        private final int[] offsetPerTraverserField;
        private final int bitsPerSelectTableSize;
        private final int bitsPerSelectTablePointer;

        public HollowHashIndexState(HollowHashIndexBuilder builder) {
            matchHashTable = builder.getFinalMatchHashTable();
            selectHashArray = builder.getFinalSelectHashArray();
            matchFields = builder.getMatchFields();
            matchHashMask = (int) builder.getFinalMatchHashMask();
            bitsPerMatchHashKey = builder.getBitsPerMatchHashKey();
            bitsPerMatchHashEntry = builder.getFinalBitsPerMatchHashEntry();
            bitsPerTraverserField = builder.getBitsPerTraverserField();
            offsetPerTraverserField = builder.getOffsetPerTraverserField();
            bitsPerSelectTableSize = builder.getFinalBitsPerSelectTableSize();
            bitsPerSelectTablePointer = builder.getFinalBitsPerSelectTablePointer();
            bitsPerSelectHashEntry = builder.getBitsPerSelectHashEntry();
        }

        public FixedLengthElementArray getSelectHashArray() {
            return selectHashArray;
        }

        public int getBitsPerSelectHashEntry() {
            return bitsPerSelectHashEntry;
        }

        public FixedLengthElementArray getMatchHashTable() {
            return matchHashTable;
        }

        public HollowHashIndexField[] getMatchFields() {
            return matchFields;
        }

        public int getMatchHashMask() {
            return matchHashMask;
        }

        public int getBitsPerMatchHashKey() {
            return bitsPerMatchHashKey;
        }

        public int getBitsPerMatchHashEntry() {
            return bitsPerMatchHashEntry;
        }

        public int[] getBitsPerTraverserField() {
            return bitsPerTraverserField;
        }

        public int[] getOffsetPerTraverserField() {
            return offsetPerTraverserField;
        }

        public int getBitsPerSelectTableSize() {
            return bitsPerSelectTableSize;
        }

        public int getBitsPerSelectTablePointer() {
            return bitsPerSelectTablePointer;
        }
    }

    @Override
    public String toString() {
        return "HollowHashIndex [type=" + type + ", selectField=" + selectField + ", matchFields=" + Arrays.toString(matchFields) + "]";
    }
}
