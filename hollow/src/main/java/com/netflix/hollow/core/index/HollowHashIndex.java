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

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;

/**
 * A HollowHashIndex is used for indexing non-primary-key data.  This type of index can map multiple keys to a single matching record, and/or
 * multiple records to a single key.
 * <p>
 * The field definitions in a hash key may be hierarchical (traverse multiple record types) via dot-notation.  For example,
 * the field definition <i>actors.element.actorId</i> may be used to traverse a child <b>LIST</b> or <b>SET</b> type record referenced by the field
 * <i>actors</i>, each elements contained therein, and finally each actors <i>actorId</i> field.
 */
public class HollowHashIndex implements HollowTypeStateListener {

    private volatile HollowHashIndexState hashStateVolatile;

    private final HollowReadStateEngine stateEngine;
    private final HollowObjectTypeReadState typeState;
    private final String type;
    private final String selectField;
    private final String[] matchFields;

    /**
     * Define a {@link HollowHashIndex}.
     *
     * @param stateEngine The state engine to index
     * @param type The query starts with the specified type
     * @param selectField The query will select records at this field (specify "" to select the specified type).
     * The selectField may span collection elements and/or map keys or values, which can result in multiple matches per record of the specified start type.
     * @param matchFields The query will match on the specified match fields.  The match fields may span collection elements and/or map keys or values.
     */
    public HollowHashIndex(HollowReadStateEngine stateEngine, String type, String selectField, String... matchFields) {
        requireNonNull(type, "Hollow Hash Index creation failed because type was null");
        requireNonNull(stateEngine, "Hollow Hash Index creation on type [" + type
                + "] failed because read state wasn't initialized");

        this.stateEngine = stateEngine;
        this.type = type;
        this.typeState = (HollowObjectTypeReadState) stateEngine.getTypeState(type);
        this.selectField = selectField;
        this.matchFields = matchFields;
        
        reindexHashIndex();
    }

    /**
     * Recreate the hash index entirely
     */
    private void reindexHashIndex() {
        HollowHashIndexBuilder builder = new HollowHashIndexBuilder(stateEngine, type, selectField, matchFields);

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

            HollowTypeReadState readState = field.getBaseDataAccess();
            int[] fieldPath = field.getSchemaFieldPositionPath();

            if(fieldPath.length == 0) {
                if (!query[i].equals(hashOrdinal))
                    return false;
            } else {
                for(int j=0;j<fieldPath.length - 1;j++) {
                    HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                    readState = objectAccess.getSchema().getReferencedTypeState(fieldPath[j]);
                    hashOrdinal = objectAccess.readOrdinal(hashOrdinal, fieldPath[j]);
                }

                HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                int fieldIdx = fieldPath[fieldPath.length-1];
                if(hashOrdinal == -1 || !HollowReadFieldUtils.fieldValueEquals(objectAccess, hashOrdinal, fieldIdx, query[i])) {
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
     */
    public void listenForDeltaUpdates() {
        typeState.addListener(this);
    }

    /**
     * Once called, this HollowHashIndex will no longer be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * Call this method before discarding indexes which are currently listening for delta updates.
     */
    public void detachFromDeltaUpdates() {
        typeState.removeListener(this);
    }

    @Override
    public void beginUpdate() { }

    @Override
    public void addedOrdinal(int ordinal) { }

    @Override
    public void removedOrdinal(int ordinal) { }

    @Override
    public void endUpdate() {
       reindexHashIndex();
    }

    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
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
}
