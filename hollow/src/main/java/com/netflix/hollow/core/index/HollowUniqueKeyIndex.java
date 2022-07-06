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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowHashIndexField.FieldPathSegment;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex.PrimaryKeyIndexHashTable;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.core.index.HollowPrimaryKeyIndex.reindexImpl;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * A HollowUniqueKeyIndex is a helper class for indexing and querying data in a Hollow Blob.
 * <p>
 * A primary key index can be used to index and query a type by a {@link PrimaryKey}.  The provided {@link PrimaryKey} does
 * not have to be the same as declared as the default in the data model.
 * <p>
 * This class differs from {@link HollowPrimaryKeyIndex} in that it supports object longevity. HollowPrimaryKeyIndex can
 * be created when object longevity is enabled, but the index will become unusable after 2 or more deltas.
 * <p>
 * HollowUniqueKeyIndex serves the same purpose as HollowPrimaryKeyIndex, but will remain valid beyond 2 or more deltas
 * as long as object longevity is enabled. The primary difference between these two classes is the use of data accessors
 * vs. state engines.
 *
 * <b>WARNING: The HollowUniqueKeyIndex must be created from the <i>current</i> version. It cannot be created from an
 * a data accessor of an outdated version. This limitation is caused by the getPopulatedOrdinals method that does not
 * work against old versions.</b>
 */
public class HollowUniqueKeyIndex implements HollowTypeStateListener, UniqueKeyIndex {
    private static final Logger LOG = Logger.getLogger(HollowUniqueKeyIndex.class.getName());

    /**
     * Data access object for the type being indexed. This must be a {@link HollowDataAccess} rather than
     * a {@link com.netflix.hollow.core.HollowStateEngine} to participate in object longevity.
     */
    private final HollowObjectTypeDataAccess objectTypeDataAccess;

    /**
     * Each field that is part of this index.
     */
    private final HollowHashIndexField[] fields;

    private final PrimaryKey primaryKey;

    private final ArraySegmentRecycler memoryRecycler;

    private final BitSet specificOrdinalsToIndex;

    private volatile PrimaryKeyIndexHashTable hashTableVolatile;

    /**
     * <b>To support object longevity, the {@code HollowDataAccess} object must come from {@link HollowAPI#getDataAccess()}.</b>
     * <p>
     * If the index is being built from a {@link HollowConsumer.RefreshListener} method, be sure to use passed in HollowAPI's accessor
     * rather than the state engine. The state engine will change over time rendering this index invalid unless
     * {@link #listenForDeltaUpdates()} is enabled.
     *
     * @param hollowDataAccess hollow data access. <b>For object longevity, this must be from {@link HollowAPI#getDataAccess()}</b>
     * @param type             type of object being indexed
     * @param fieldPaths       paths to fields being indexed
     */
    public HollowUniqueKeyIndex(HollowDataAccess hollowDataAccess, String type, String... fieldPaths) {
        this(hollowDataAccess, WastefulRecycler.DEFAULT_INSTANCE, type, fieldPaths);
    }

    /**
     * <b>To support object longevity, the {@code HollowDataAccess} object must come from {@link HollowAPI#getDataAccess()}.</b>
     * <p>
     * If the index is being built from a {@link HollowConsumer.RefreshListener} method, be sure to use passed in HollowAPI's accessor
     * rather than the state engine. The state engine will change over time rendering this index invalid unless
     * {@link #listenForDeltaUpdates()} is enabled.
     *
     * @param hollowDataAccess hollow data access. <b>For object longevity, this must be from {@link HollowAPI#getDataAccess()}</b>
     * @param primaryKey       primary key definition. This does not have to match the primary key defined in the type.
     */
    public HollowUniqueKeyIndex(HollowDataAccess hollowDataAccess, PrimaryKey primaryKey) {
        this(hollowDataAccess, primaryKey, WastefulRecycler.DEFAULT_INSTANCE);
    }

    /**
     * <b>To support object longevity, the {@code HollowDataAccess} object must come from {@link HollowAPI#getDataAccess()}.</b>
     * <p>
     * If the index is being built from a {@link HollowConsumer.RefreshListener} method, be sure to use passed in HollowAPI's accessor
     * rather than the state engine. The state engine will change over time rendering this index invalid unless
     * {@link #listenForDeltaUpdates()} is enabled.
     *
     * @param hollowDataAccess hollow data access. <b>For object longevity, this must be from {@link HollowAPI#getDataAccess()}</b>
     * @param type             type of object being indexed
     * @param fieldPaths       paths to fields being indexed
     */
    public HollowUniqueKeyIndex(HollowDataAccess hollowDataAccess, ArraySegmentRecycler memoryRecycler, String type, String... fieldPaths) {
        this(hollowDataAccess, PrimaryKey.create(hollowDataAccess, type, fieldPaths), memoryRecycler);
    }

    /**
     * <b>To support object longevity, the {@code HollowDataAccess} object must come from {@link HollowAPI#getDataAccess()}.</b>
     * <p>
     * If the index is being built from a {@link HollowConsumer.RefreshListener} method, be sure to use passed in HollowAPI's accessor
     * rather than the state engine. The state engine will change over time rendering this index invalid unless
     * {@link #listenForDeltaUpdates()} is enabled.
     *
     * @param hollowDataAccess hollow data access. <b>For object longevity, this must be from {@link HollowAPI#getDataAccess()}</b>
     * @param primaryKey       primary key definition. This does not have to match the primary key defined in the type.
     */
    public HollowUniqueKeyIndex(HollowDataAccess hollowDataAccess, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler) {
        this(hollowDataAccess, primaryKey, memoryRecycler, null);
    }

    /**
     * This initializer can be used to create a HollowUniqueKeyIndex which will only index a subset of the records in the specified type.
     *
     * @param hollowDataAccess        the read state engine
     * @param primaryKey              the primary key
     * @param memoryRecycler          the memory recycler
     * @param specificOrdinalsToIndex the bit set
     */
    public HollowUniqueKeyIndex(HollowDataAccess hollowDataAccess, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler, BitSet specificOrdinalsToIndex) {
        requireNonNull(primaryKey, "Hollow Primary Key Index creation failed because primaryKey was null");
        requireNonNull(hollowDataAccess, "Hollow Primary Key Index creation for type [" + primaryKey.getType()
                + "] failed because read state wasn't initialized");

        this.primaryKey = primaryKey;
        //Obviously, the type we're indexing must be an object... no point in indexing primitives, etc.
        this.objectTypeDataAccess = (HollowObjectTypeDataAccess) hollowDataAccess.getTypeDataAccess(primaryKey.getType());
        this.fields = new HollowHashIndexField[primaryKey.numFields()];

        this.memoryRecycler = memoryRecycler;

        for (int fieldIdx = 0; fieldIdx < primaryKey.numFields(); fieldIdx++) {
            //This is the field type of the final item on the path.
            FieldType fieldType = primaryKey.getFieldType(hollowDataAccess, fieldIdx);

            //This always starts at the "root" object that's being indexed
            HollowObjectTypeDataAccess currentDataAccess = this.objectTypeDataAccess;

            //For each segment of the path, this returns the field position relative within the containing object.
            //The 0th position is relative to primaryKey.getType().
            int[] fieldPathPositions = primaryKey.getFieldPathIndex(hollowDataAccess, fieldIdx);
            FieldPathSegment[] fieldPathElements = new FieldPathSegment[fieldPathPositions.length];

            for (int posIdx = 0; posIdx < fieldPathPositions.length; posIdx++) {
                if (currentDataAccess == null) {
                    //This gets set to null if the previous segment was a non-reference type (i.e. we can't traverse a primitive, etc).
                    throw new IllegalArgumentException("Path " + primaryKey.getFieldPath(fieldIdx) + " traverses a non-reference type. Non-reference types must be the last element of the path.");
                }

                int fieldPosition = fieldPathPositions[posIdx];
                fieldPathElements[posIdx] = new FieldPathSegment(fieldPosition, currentDataAccess);

                //Using schema.getReferencedTypeState(...) will always use the *current* version and not necessarily
                //the version associated with the hollowDataAccess used to create this. This will break object longevity.
                //As such, we have to do this indirect lookup that reaches data access objects through
                //hollowDataAccess. There is a non-zero cost to performing these lookups so we do them here rather than
                //at the place where we access the data.
                String referencedType = currentDataAccess.getSchema().getReferencedType(fieldPosition);
                if (referencedType != null) {
                    //This is instead of currentDataAccess.getSchema().getReferencedTypeState()
                    currentDataAccess = (HollowObjectTypeDataAccess) hollowDataAccess.getTypeDataAccess(referencedType);
                } else {
                    currentDataAccess = null;
                }
            }
            fields[fieldIdx] = new HollowHashIndexField(fieldIdx, fieldPathElements, currentDataAccess, fieldType);
        }

        this.specificOrdinalsToIndex = specificOrdinalsToIndex;

        reindex();
    }

    /**
     * Once called, this HollowUniqueKeyIndex will be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * This method should be called <b>before</b> any subsequent deltas occur after the index is created.
     * <p>
     * In order to prevent memory leaks, if this method is called and the index is no longer needed, call detachFromDeltaUpdates() before
     * discarding the index.
     */
    public void listenForDeltaUpdates() {
        if (specificOrdinalsToIndex != null)
            throw new IllegalStateException("Cannot listen for delta updates when indexing only specified ordinals!");
        if (!(objectTypeDataAccess instanceof HollowObjectTypeReadState))
            throw new IllegalStateException("Cannot listen for delta updates when objectTypeDataAccess is a "
                    + objectTypeDataAccess.getClass().getSimpleName() + ". You may have created this index from a Data Access instance that has object longevity enabled.");

        ((HollowObjectTypeReadState) objectTypeDataAccess).addListener(this);
    }

    /**
     * Once called, this HollowUniqueKeyIndex will no longer be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * Call this method before discarding indexes which are currently listening for delta updates.
     */
    public void detachFromDeltaUpdates() {
        //We won't throw here. Just silently fail since it's unlikely this class was ever successfully added as a listener.
        if (objectTypeDataAccess instanceof HollowObjectTypeReadState)
            ((HollowObjectTypeReadState) objectTypeDataAccess).removeListener(this);
    }

    public HollowObjectTypeDataAccess getObjectTypeDataAccess() {
        return objectTypeDataAccess;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public List<FieldType> getFieldTypes() {
        return stream(fields).map(HollowHashIndexField::getFieldType).collect(toList());
    }

    /**
     * Query an index with a single specified field.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param key the field key
     * @return the matching ordinal for the key, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object key) {
        if (isProvidedKeyCountNotEqualToIndexedFieldsCount(1))
            return ORDINAL_NONE;

        return HollowPrimaryKeyIndex.getMatchingOrdinal(
                () -> this.hashTableVolatile,
                this::keyMatches,
                this.fields[0].getFieldType(), key,
                null, null,
                null, null);
    }

    /**
     * Query an index with two specified fields.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param key0 the first field key
     * @param key1 the second field key
     * @return the matching ordinal for the two keys, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object key0, Object key1) {
        if (isProvidedKeyCountNotEqualToIndexedFieldsCount(2))
            return ORDINAL_NONE;

        return HollowPrimaryKeyIndex.getMatchingOrdinal(
                () -> this.hashTableVolatile,
                this::keyMatches,
                this.fields[0].getFieldType(), key0,
                this.fields[1].getFieldType(), key1,
                null, null);
    }

    /**
     * Query an index with three specified fields.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param key0 the first field key
     * @param key1 the second field key
     * @param key2 the third field key
     * @return the matching ordinal for the three keys, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object key0, Object key1, Object key2) {
        if (isProvidedKeyCountNotEqualToIndexedFieldsCount(3))
            return ORDINAL_NONE;

        return HollowPrimaryKeyIndex.getMatchingOrdinal(
                () -> this.hashTableVolatile,
                this::keyMatches,
                this.fields[0].getFieldType(), key0,
                this.fields[1].getFieldType(), key1,
                this.fields[2].getFieldType(), key2);
    }

    /**
     * Query an index with four or more specified fields.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param keys the field keys
     * @return the matching ordinal for the keys, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object... keys) {
        if (isProvidedKeyCountNotEqualToIndexedFieldsCount(keys.length))
            return ORDINAL_NONE;

        return HollowPrimaryKeyIndex.getMatchingOrdinal(
                () -> this.hashTableVolatile,
                this::keyMatches,
                fieldIdx -> this.fields[fieldIdx].getFieldType(),
                keys);
    }

    private boolean isProvidedKeyCountNotEqualToIndexedFieldsCount(int keyCount) {
        return this.fields.length != keyCount || this.hashTableVolatile.bitsPerElement == 0;
    }

    private void setHashTable(PrimaryKeyIndexHashTable hashTable) {
        this.hashTableVolatile = hashTable;
    }

    /**
     * @return whether this index contains duplicate records (two or more records mapping to a single primary key).
     */
    public boolean containsDuplicates() {
        return !getDuplicateKeys().isEmpty();
    }

    /**
     * @return any keys which are mapped to two or more records.
     */
    public synchronized Collection<Object[]> getDuplicateKeys() {
        //Synchronized to prevent index changes while this calculation is occuring
        return HollowPrimaryKeyIndex.getDuplicateKeys(hashTableVolatile, this::recordsHaveEqualKeys, this::getRecordKey);
    }

    @Override
    public void beginUpdate() {
    }

    @Override
    public void addedOrdinal(int ordinal) {
    }

    @Override
    public void removedOrdinal(int ordinal) {
    }

    @Override
    public synchronized void endUpdate() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        HollowPrimaryKeyIndex.endUpdateImpl(
                this.memoryRecycler,
                this::setHashTable,
                this.hashTableVolatile,
                this.specificOrdinalsToIndex,
                typeState.getPopulatedOrdinals(),
                typeState.getPreviousOrdinals(),
                typeState.maxOrdinal(),
                this::generateRecordHash);
    }

    public void destroy() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (hashTable != null)
            hashTable.hashTable.destroy(memoryRecycler);
    }

    private synchronized void reindex() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        reindexImpl(this.memoryRecycler,
                this::setHashTable,
                this.hashTableVolatile,
                this.specificOrdinalsToIndex,
                typeState.getPopulatedOrdinals(),
                typeState.maxOrdinal(),
                this::generateRecordHash);
    }

    private int generateRecordHash(int ordinal) {
        int hashCode = 0;
        for (int i = 0; i < fields.length; i++) {
            hashCode ^= generateFieldHash(ordinal, i);
        }
        return hashCode;
    }

    private int generateFieldHash(int ordinal, int fieldIdx) {
        HollowHashIndexField field = fields[fieldIdx];
        int lastPathIdx = field.getSchemaFieldPositionPath().length - 1;
        for (int pathIdx = 0; pathIdx < lastPathIdx; pathIdx++) {
            FieldPathSegment pathElement = field.getSchemaFieldPositionPath()[pathIdx];
            ordinal = pathElement.getOrdinalForField(ordinal);
        }
        //When the loop finishes, we should have the ordinal of the object containing the last field.
        FieldPathSegment lastPathElement = field.getLastFieldPositionPathElement();

        int hashCode = HollowReadFieldUtils.fieldHashCode(lastPathElement.getObjectTypeDataAccess(), ordinal, lastPathElement.getSegmentFieldPosition());

        switch (field.getFieldType()) {
            case STRING:
            case BYTES:
                return hashCode;
            default:
                return HashCodes.hashInt(hashCode);
        }
    }

    public Object[] getRecordKey(int ordinal) {
        Object[] results = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            HollowHashIndexField field = fields[i];
            int lastPathOrdinal = getOrdinalForFieldPath(field, ordinal);
            FieldPathSegment lastElement = field.getLastFieldPositionPathElement();
            results[i] = HollowReadFieldUtils.fieldValueObject(lastElement.getObjectTypeDataAccess(), lastPathOrdinal, lastElement.getSegmentFieldPosition());
        }
        return results;
    }

    /**
     * @param ordinal ordinal of root object
     * @param field   field to traverse
     * @return the ordinal of the second-to-last element. This ordinal can be used with the last path element
     * to retrieve the final ordinal
     */
    private int getOrdinalForFieldPath(HollowHashIndexField field, int ordinal) {
        FieldPathSegment[] pathElements = field.getSchemaFieldPositionPath();
        for (int posIdx = 0; posIdx < pathElements.length - 1; posIdx++) {
            FieldPathSegment fieldPathElement = pathElements[posIdx];
            ordinal = fieldPathElement.getOrdinalForField(ordinal);
        }
        return ordinal;
    }

    private boolean keyMatches(Object key, int recordOrdinal, int fieldIdx) {
        HollowHashIndexField field = fields[fieldIdx];

        //ordinal of the last element of the path, starting from the recordOrdinal.
        int lastElementOrdinal = getOrdinalForFieldPath(field, recordOrdinal);

        FieldPathSegment lastPathElement = field.getLastFieldPositionPathElement();
        int lastPathPosition = lastPathElement.getSegmentFieldPosition();
        HollowObjectTypeDataAccess typeDataAccess = lastPathElement.getObjectTypeDataAccess();

        return HollowPrimaryKeyValueDeriver.keyMatches(key, field.getFieldType(), lastPathPosition, lastElementOrdinal, typeDataAccess);
    }

    private boolean recordsHaveEqualKeys(int ordinal1, int ordinal2) {
        for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
            if (!fieldsAreEqual(ordinal1, ordinal2, fieldIdx))
                return false;
        }
        return true;
    }

    private boolean fieldsAreEqual(int ordinal1, int ordinal2, int fieldIdx) {
        HollowHashIndexField field = fields[fieldIdx];
        FieldPathSegment[] fieldPathElements = field.getSchemaFieldPositionPath();
        for (int posIdx = 0; posIdx < fieldPathElements.length - 1; posIdx++) {
            FieldPathSegment pathElement = fieldPathElements[posIdx];
            ordinal1 = pathElement.getOrdinalForField(ordinal1);
            ordinal2 = pathElement.getOrdinalForField(ordinal2);
        }
        //Ordinals now reference the record that contains the last field value.
        //For a path with only one element, ordinal is unchanged. For a path with two elements,
        //ordinal will refer to the record for the first element. Using that ordinal, you can
        //then invoke lastPathElement.getOrdinal(ordinal) to get the final element.

        if (field.getFieldType() == FieldType.REFERENCE)
            return ordinal1 == ordinal2;

        FieldPathSegment lastPathElement = field.getLastFieldPositionPathElement();
        return HollowReadFieldUtils.fieldsAreEqual(
                lastPathElement.getObjectTypeDataAccess(), ordinal1, lastPathElement.getSegmentFieldPosition(),
                lastPathElement.getObjectTypeDataAccess(), ordinal2, lastPathElement.getSegmentFieldPosition());
    }

}
