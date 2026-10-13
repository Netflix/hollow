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
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private static final String DELTA_UPDATE_PROPERTY = "com.netflix.hollow.core.index.HollowHashIndex.allowDeltaUpdate";

    private volatile HollowHashIndexState hashStateVolatile;
    private volatile DeltaSnapshot deltaSnapshotVolatile;

    private final HollowDataAccess hollowDataAccess;
    private final HollowObjectTypeDataAccess typeState;
    private final String type;
    private final String selectField;
    private final String[] matchFields;
    private final boolean supportsNoOpDeltaUpdateSkip;

    private boolean deltaHadTypeChanges;
    private DeltaUpdater deltaUpdater;

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
        this.supportsNoOpDeltaUpdateSkip = canSkipNoOpDeltaUpdate();

        if (typeState == null) {
            LOG.log(Level.WARNING, "Index initialization for " + this + " failed because type "
                    + type + " was not found in read state");
            return;
        }
        reindexHashIndex();
    }

    private static boolean allowDeltaUpdate() {
        return Boolean.getBoolean(DELTA_UPDATE_PROPERTY);
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

    private boolean canSkipNoOpDeltaUpdate() {
        if (typeState == null || !"".equals(selectField)) {
            return false;
        }

        for (String matchField : matchFields) {
            if (!isRootLocalField(matchField)) {
                return false;
            }
        }

        return true;
    }

    private boolean isRootLocalField(String fieldPath) {
        try {
            FieldPaths.FieldPath<FieldPaths.FieldSegment> path =
                    FieldPaths.createFieldPathForHashIndex(hollowDataAccess, type, fieldPath);
            if (path.getSegments().size() != 1) {
                return false;
            }

            FieldPaths.FieldSegment segment = path.getSegments().get(0);
            if (!(segment instanceof FieldPaths.ObjectFieldSegment)) {
                return false;
            }

            return ((FieldPaths.ObjectFieldSegment) segment).getType() != FieldType.REFERENCE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    /**
     * Query the index.
     *
     * @param query the query
     * @return the hash index result to gather the matched ordinals. A {@code null} value indicated no matches were
     * found.
     */
    public HollowHashIndexResult findMatches(Object... query) {
        DeltaSnapshot deltaSnapshot = deltaSnapshotVolatile;
        if (deltaSnapshot != null) {
            return deltaSnapshot.findMatches(query);
        }

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

        if (allowDeltaUpdate() && deltaUpdater == null) {
            deltaUpdater = new DeltaUpdater(hollowDataAccess, type, selectField, matchFields);
            deltaSnapshotVolatile = deltaUpdater.rebuildSnapshot();
        }

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
    public void beginUpdate() {
        deltaHadTypeChanges = false;
    }

    @Override
    public void addedOrdinal(int ordinal) {
        deltaHadTypeChanges = true;
    }

    @Override
    public void removedOrdinal(int ordinal) {
        deltaHadTypeChanges = true;
    }

    @Override
    public void endUpdate() {
        if (hashStateVolatile == null) {
            return;
        }

        if ((deltaSnapshotVolatile != null || supportsNoOpDeltaUpdateSkip) && !deltaHadTypeChanges) {
            return;
        }

        if (deltaSnapshotVolatile != null) {
            try {
                HollowObjectTypeReadState objectTypeState = (HollowObjectTypeReadState) typeState;
                deltaSnapshotVolatile = deltaUpdater.applyDelta(objectTypeState.getPreviousOrdinals(), objectTypeState.getPopulatedOrdinals());
                return;
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Delta update of index failed. Falling back to a full rebuild.", e);
                deltaSnapshotVolatile = null;
                deltaUpdater = null;
            }
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
        DeltaSnapshot deltaSnapshot = deltaSnapshotVolatile;
        if (deltaSnapshot != null) {
            return deltaSnapshot.approxHeapFootprintInBytes;
        }

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

    private static final class DeltaUpdater {
        private final HollowPreindexer preindexer;
        private final HollowHashIndexField[] matchFieldSpecs;
        private final HollowHashIndexField selectFieldSpec;
        private final Map<MatchKey, MutableMatchEntry> entries;

        private DeltaUpdater(HollowDataAccess hollowDataAccess, String type, String selectField, String[] matchFields) {
            this.preindexer = new HollowPreindexer(hollowDataAccess, type, selectField, matchFields);
            this.preindexer.buildFieldSpecifications();
            this.matchFieldSpecs = preindexer.getMatchFieldSpecs();
            this.selectFieldSpec = preindexer.getSelectFieldSpec();
            this.entries = new HashMap<>();
        }

        private DeltaSnapshot rebuildSnapshot() {
            entries.clear();
            BitSet ordinals = preindexer.getHollowTypeDataAccess().getTypeState().getPopulatedOrdinals();
            int ordinal = ordinals.nextSetBit(0);
            while (ordinal != HollowConstants.ORDINAL_NONE) {
                applyRootOrdinal(ordinal, true);
                ordinal = ordinals.nextSetBit(ordinal + 1);
            }
            return snapshot();
        }

        private DeltaSnapshot applyDelta(BitSet previousOrdinals, BitSet ordinals) {
            int prevOrdinal = previousOrdinals.nextSetBit(0);
            while (prevOrdinal != HollowConstants.ORDINAL_NONE) {
                if (!ordinals.get(prevOrdinal)) {
                    applyRootOrdinal(prevOrdinal, false);
                }
                prevOrdinal = previousOrdinals.nextSetBit(prevOrdinal + 1);
            }

            int ordinal = ordinals.nextSetBit(0);
            while (ordinal != HollowConstants.ORDINAL_NONE) {
                if (!previousOrdinals.get(ordinal)) {
                    applyRootOrdinal(ordinal, true);
                }
                ordinal = ordinals.nextSetBit(ordinal + 1);
            }

            return snapshot();
        }

        private void applyRootOrdinal(int rootOrdinal, boolean add) {
            preindexer.getTraverser().traverse(rootOrdinal);

            for (int matchIdx = 0; matchIdx < preindexer.getTraverser().getNumMatches(); matchIdx++) {
                MatchKey key = MatchKey.fromTraverser(matchFieldSpecs, preindexer.getTraverser(), matchIdx);
                int selectOrdinal = preindexer.getTraverser().getMatchOrdinal(matchIdx, selectFieldSpec.getBaseIteratorFieldIdx());

                if (add) {
                    entries.computeIfAbsent(key, ignored -> new MutableMatchEntry()).increment(selectOrdinal);
                } else {
                    MutableMatchEntry entry = entries.get(key);
                    if (entry == null) {
                        throw new IllegalStateException("Attempted to remove a match key that was not indexed");
                    }
                    if (entry.decrement(selectOrdinal)) {
                        entries.remove(key);
                    }
                }
            }
        }

        private DeltaSnapshot snapshot() {
            Map<MatchKey, int[]> snapshotMatches = new HashMap<>(entries.size());
            long approxBytes = 0;

            for (Map.Entry<MatchKey, MutableMatchEntry> entry : entries.entrySet()) {
                int[] ordinals = entry.getValue().toOrdinals();
                snapshotMatches.put(entry.getKey(), ordinals);
                approxBytes += 16L + ((long) ordinals.length * Integer.BYTES);
            }

            return new DeltaSnapshot(snapshotMatches, approxBytes);
        }
    }

    private static final class DeltaSnapshot {
        private final Map<MatchKey, int[]> matches;
        private final long approxHeapFootprintInBytes;

        private DeltaSnapshot(Map<MatchKey, int[]> matches, long approxHeapFootprintInBytes) {
            this.matches = matches;
            this.approxHeapFootprintInBytes = approxHeapFootprintInBytes;
        }

        private HollowHashIndexResult findMatches(Object[] query) {
            for (Object value : query) {
                if (value == null) {
                    throw new IllegalArgumentException("querying by null unsupported");
                }
            }

            int[] ordinals = matches.get(new MatchKey(Arrays.copyOf(query, query.length)));
            return ordinals == null ? null : new HollowHashIndexResult(ordinals);
        }
    }

    private static final class MutableMatchEntry {
        private final Map<Integer, Integer> selectOrdinalRefCounts = new LinkedHashMap<>();

        private void increment(int selectOrdinal) {
            selectOrdinalRefCounts.merge(selectOrdinal, 1, Integer::sum);
        }

        private boolean decrement(int selectOrdinal) {
            Integer currentCount = selectOrdinalRefCounts.get(selectOrdinal);
            if (currentCount == null) {
                throw new IllegalStateException("Attempted to remove a select ordinal that was not indexed: " + selectOrdinal);
            }

            if (currentCount == 1) {
                selectOrdinalRefCounts.remove(selectOrdinal);
            } else {
                selectOrdinalRefCounts.put(selectOrdinal, currentCount - 1);
            }

            return selectOrdinalRefCounts.isEmpty();
        }

        private int[] toOrdinals() {
            int[] ordinals = new int[selectOrdinalRefCounts.size()];
            int index = 0;
            for (Integer ordinal : selectOrdinalRefCounts.keySet()) {
                ordinals[index++] = ordinal;
            }
            return ordinals;
        }
    }

    private static final class MatchKey {
        private final Object[] values;
        private final int hashCode;

        private MatchKey(Object[] values) {
            this.values = values;
            this.hashCode = Arrays.deepHashCode(values);
        }

        private static MatchKey fromTraverser(HollowHashIndexField[] fields, com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser traverser, int matchIdx) {
            Object[] values = new Object[fields.length];
            for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
                HollowHashIndexField field = fields[fieldIdx];
                values[fieldIdx] = extractMatchValue(field, traverser.getMatchOrdinal(matchIdx, field.getBaseIteratorFieldIdx()));
            }
            return new MatchKey(values);
        }

        private static Object extractMatchValue(HollowHashIndexField field, int ordinal) {
            FieldPathSegment[] fieldPath = field.getSchemaFieldPositionPath();

            if (fieldPath.length == 0) {
                return ordinal;
            }

            for (int pathIdx = 0; pathIdx < fieldPath.length - 1 && ordinal != HollowConstants.ORDINAL_NONE; pathIdx++) {
                ordinal = fieldPath[pathIdx].getOrdinalForField(ordinal);
            }

            if (ordinal == HollowConstants.ORDINAL_NONE) {
                return null;
            }

            FieldPathSegment lastPathElement = field.getLastFieldPositionPathElement();
            return HollowReadFieldUtils.fieldValueObject(lastPathElement.getObjectTypeDataAccess(), ordinal, lastPathElement.getSegmentFieldPosition());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MatchKey)) {
                return false;
            }
            MatchKey matchKey = (MatchKey) other;
            return Arrays.deepEquals(values, matchKey.values);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
