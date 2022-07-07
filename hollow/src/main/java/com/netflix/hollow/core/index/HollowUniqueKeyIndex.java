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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowHashIndexField.FieldPathSegment;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex.PrimaryKeyIndexHashTable;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class HollowUniqueKeyIndex implements HollowTypeStateListener, TestableUniqueKeyIndex {
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
     * @param memoryRecycler   memory recycler implementation
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
     * @param memoryRecycler   memory recycler implementation
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

        return getMatchingOrdinalImpl(key, null, null);
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

        return getMatchingOrdinalImpl(key0, key1, null);
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

        return getMatchingOrdinalImpl(key0, key1, key2);
    }

    /**
     * Single implementation for up to 3 fields. There is a very tiny null array length check to determine whether to
     * use fields 1 and 2.
     *
     * @param key0 key for field 0
     * @param key1 key for field 1
     * @param key2 key for field 2
     * @return ordinal or {@link com.netflix.hollow.core.HollowConstants#ORDINAL_NONE}
     */
    private int getMatchingOrdinalImpl(
            Object key0,
            Object key1,
            Object key2) {
        int fieldCount = fields.length;

        int hashCode = generateKeyHashCode(key0, fields[0].getFieldType());
        if (fieldCount >= 2) {
            hashCode ^= generateKeyHashCode(key1, fields[1].getFieldType());
            if (fieldCount == 3) {
                hashCode ^= generateKeyHashCode(key2, fields[2].getFieldType());
            }
        }

        PrimaryKeyIndexHashTable hashTable;
        int ordinal;
        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while (ordinal != ORDINAL_NONE) {
                if (keyMatches(key0, ordinal, 0)
                        && (fieldCount < 2 || keyMatches(key1, ordinal, 1))
                        && (fieldCount < 3 || keyMatches(key2, ordinal, 2))) {
                    //This is a match. Break and return the ordinal.
                    break;
                }

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while (this.hashTableVolatile != hashTable);

        return ordinal;
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

        int hashCode = 0;
        for (int fieldIdx = 0; fieldIdx < keys.length; fieldIdx++)
            hashCode ^= generateKeyHashCode(keys[fieldIdx], fields[fieldIdx].getFieldType());

        PrimaryKeyIndexHashTable hashTable;
        int ordinal;

        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while (ordinal != -1) {
                if (keysAllMatch(ordinal, keys))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while (hashTableVolatile != hashTable);

        return ordinal;
    }

    private boolean isProvidedKeyCountNotEqualToIndexedFieldsCount(int keyCount) {
        // mismatched number of fields or the table is empty
        return this.fields.length != keyCount || this.hashTableVolatile.bitsPerElement == 0;
    }

    private int readOrdinal(PrimaryKeyIndexHashTable hashTable, int bucket) {
        return (int) hashTable.hashTable.getElementValue((long) hashTable.bitsPerElement * (long) bucket, hashTable.bitsPerElement) - 1;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    private static int generateKeyHashCode(Object key, FieldType fieldType) {
        switch (fieldType) {
            case BOOLEAN:
                return HashCodes.hashInt(HollowReadFieldUtils.booleanHashCode((Boolean) key));
            case DOUBLE:
                return HashCodes.hashInt(HollowReadFieldUtils.doubleHashCode(((Double) key).doubleValue()));
            case FLOAT:
                return HashCodes.hashInt(HollowReadFieldUtils.floatHashCode(((Float) key).floatValue()));
            case INT:
                return HashCodes.hashInt(HollowReadFieldUtils.intHashCode(((Integer) key).intValue()));
            case LONG:
                return HashCodes.hashInt(HollowReadFieldUtils.longHashCode(((Long) key).longValue()));
            case REFERENCE:
                return HashCodes.hashInt(((Integer) key).intValue());
            case BYTES:
                return HashCodes.hashCode((byte[]) key);
            case STRING:
                return HashCodes.hashCode((String) key);
        }

        throw new IllegalArgumentException("I don't know how to hash a " + fieldType);
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

    public synchronized Collection<Object[]> getDuplicateKeys() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (hashTable.bitsPerElement == 0)
            return Collections.emptyList();

        List<Object[]> duplicateKeys = new ArrayList<>();

        for (int i = 0; i < hashTable.hashTableSize; i++) {
            int ordinal = (int) hashTable.hashTable.getElementValue((long) i * (long) hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;

            if (ordinal != -1) {
                int compareBucket = (i + 1) & hashTable.hashMask;
                int compareOrdinal = (int) hashTable.hashTable.getElementValue((long) compareBucket * (long) hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;
                while (compareOrdinal != -1) {
                    if (recordsHaveEqualKeys(ordinal, compareOrdinal))
                        duplicateKeys.add(getRecordKey(ordinal));

                    compareBucket = (compareBucket + 1) & hashTable.hashMask;
                    compareOrdinal = (int) hashTable.hashTable.getElementValue((long) compareBucket * (long) hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;
                }
            }
        }

        return duplicateKeys;
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

    private static final boolean ALLOW_DELTA_UPDATE =
            Boolean.getBoolean("com.netflix.hollow.core.index.HollowUniqueKeyIndex.allowDeltaUpdate");

    @Override
    public synchronized void endUpdate() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        //This doesn't affect compatibility with object longevity since this only gets invoked
        //when the index is being updated.
        BitSet ordinals = typeState.getPopulatedOrdinals();

        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(typeState.maxOrdinal() + 1));

        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (ALLOW_DELTA_UPDATE
                && hashTableSize == hashTable.hashTableSize
                && bitsPerElement == hashTable.bitsPerElement
                && shouldPerformDeltaUpdate()) {
            try {
                deltaUpdate(hashTableSize, bitsPerElement);
            } catch (OrdinalNotFoundException e) {
                /*
                It has been observed that delta updates can result in CPU spinning attempting to find
                a previous ordinal to remove.  It's not clear what the cause of the issue is but it does
                not appear to be data related (since the failure is not consistent when multiple instances
                update to the same version) nor concurrency related (since an update occurs in a synchronized
                block).  A rare possibility is it might be a C2 compiler issue.  Changing the code shape may
                well fix that.  Attempts to reproduce this locally has so far failed.
                Given the importance of indexing a full reindex is performed on such a failure.  This, however,
                will make it more difficult to detect such issues.

                This approach does not protect against the case where the index is corrupt and not yet
                detected, until a further update.  In such cases it may be possible for clients, in the interim
                of a forced reindex, to operate on a corrupt index: queries may incorrectly return no match.  As such
                delta update of the index have been disabled by default.
                 */
                LOG.log(Level.SEVERE, "Delta update of index failed.  Performing a full reindex", e);
                reindex();
            }
        } else {
            reindex();
        }
    }

    public void destroy() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (hashTable != null)
            hashTable.hashTable.destroy(memoryRecycler);
    }

    private synchronized void reindex() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        // Could be null on first reindex
        if (hashTable != null) {
            hashTable.hashTable.destroy(memoryRecycler);
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();

        BitSet ordinals = specificOrdinalsToIndex;
        if (ordinals == null) {
            //Note: this call is what makes it impossible to create an index against a non-current client.
            //This works even when object longevity it turned on *ONLY* if it is created against the
            //current version. Otherwise, this will return a bit set that does not match the HollowAPI's
            //historic version. (This method gets called upon construction)
            ordinals = typeState.getPopulatedOrdinals();
        }

        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(typeState.maxOrdinal() + 1));

        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, (long) hashTableSize * (long) bitsPerElement);

        int hashMask = hashTableSize - 1;

        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != ORDINAL_NONE) {
            int hashCode = generateRecordHash(ordinal);
            int bucket = hashCode & hashMask;

            while (hashedArray.getElementValue((long) bucket * (long) bitsPerElement, bitsPerElement) != 0)
                bucket = (bucket + 1) & hashMask;

            hashedArray.setElementValue((long) bucket * (long) bitsPerElement, bitsPerElement, ordinal + 1);

            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        setHashTable(new PrimaryKeyIndexHashTable(hashedArray, hashTableSize, hashMask, bitsPerElement));

        memoryRecycler.swap();
    }

    private void deltaUpdate(int hashTableSize, int bitsPerElement) {
        // For a delta update hashTableVolatile cannot be null
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        hashTable.hashTable.destroy(memoryRecycler);

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        //This doesn't affect compatibility with object longevity since this only gets invoked
        //when the index is being updated.
        BitSet prevOrdinals = typeState.getPreviousOrdinals();
        BitSet ordinals = typeState.getPopulatedOrdinals();

        long totalBitsInHashTable = (long) hashTableSize * (long) bitsPerElement;
        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, totalBitsInHashTable);
        hashedArray.copyBits(hashTable.hashTable, 0, 0, totalBitsInHashTable);

        int hashMask = hashTableSize - 1;

        int prevOrdinal = prevOrdinals.nextSetBit(0);
        while (prevOrdinal != ORDINAL_NONE) {
            if (!ordinals.get(prevOrdinal)) {
                /// find and remove this ordinal
                int hashCode = generateRecordHash(prevOrdinal);
                int bucket = findOrdinalBucket(bitsPerElement, hashedArray, hashCode, hashMask, prevOrdinal);

                hashedArray.clearElementValue((long) bucket * (long) bitsPerElement, bitsPerElement);
                int emptyBucket = bucket;
                bucket = (bucket + 1) & hashMask;
                int moveOrdinal = (int) hashedArray.getElementValue((long) bucket * (long) bitsPerElement, bitsPerElement) - 1;

                while (moveOrdinal != ORDINAL_NONE) {
                    int naturalHash = generateRecordHash(moveOrdinal);
                    int naturalBucket = naturalHash & hashMask;

                    if (!bucketInRange(emptyBucket, bucket, naturalBucket)) {
                        hashedArray.setElementValue((long) emptyBucket * (long) bitsPerElement, bitsPerElement, moveOrdinal + 1);
                        hashedArray.clearElementValue((long) bucket * (long) bitsPerElement, bitsPerElement);
                        emptyBucket = bucket;
                    }

                    bucket = (bucket + 1) & hashMask;
                    moveOrdinal = (int) hashedArray.getElementValue((long) bucket * (long) bitsPerElement, bitsPerElement) - 1;
                }

            }

            prevOrdinal = prevOrdinals.nextSetBit(prevOrdinal + 1);
        }


        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != ORDINAL_NONE) {
            if (!prevOrdinals.get(ordinal)) {
                int hashCode = generateRecordHash(ordinal);
                int bucket = hashCode & hashMask;

                while (hashedArray.getElementValue((long) bucket * (long) bitsPerElement, bitsPerElement) != 0) {
                    bucket = (bucket + 1) & hashMask;
                }

                hashedArray.setElementValue((long) bucket * (long) bitsPerElement, bitsPerElement, ordinal + 1);
            }

            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        setHashTable(new PrimaryKeyIndexHashTable(hashedArray, hashTableSize, hashMask, bitsPerElement));

        memoryRecycler.swap();
    }

    private int findOrdinalBucket(int bitsPerElement, FixedLengthElementArray hashedArray, int hashCode, int hashMask, int prevOrdinal) {
        int startBucket = hashCode & hashMask;
        int bucket = startBucket;
        long value;
        do {
            value = hashedArray.getElementValue((long) bucket * (long) bitsPerElement, bitsPerElement);
            if (prevOrdinal + 1 == value) {
                return bucket;
            }
            bucket = (bucket + 1) & hashMask;
        } while (value != 0 && bucket != startBucket);

        if (value == 0) {
            throw new OrdinalNotFoundException(String.format("Ordinal not found (found empty entry): "
                    + "ordinal=%d startBucket=%d", prevOrdinal, startBucket));
        } else {
            throw new OrdinalNotFoundException(String.format("Ordinal not found (wrapped around table): "
                    + "ordinal=%d startBucket=%d", prevOrdinal, startBucket));
        }
    }

    private boolean bucketInRange(int fromBucket, int toBucket, int testBucket) {
        if (toBucket > fromBucket) {
            return testBucket > fromBucket && testBucket <= toBucket;
        } else {
            return testBucket > fromBucket || testBucket <= toBucket;
        }
    }

    private int generateRecordHash(int ordinal) {
        int hashCode = 0;
        for (int i = 0; i < fields.length; i++) {
            hashCode ^= generateFieldHash(ordinal, i);
        }
        return hashCode;
    }

    private int generateFieldHash(int ordinal, int fieldIdx) {
        //It is super important that all references to data accessors originated
        //from a HollowAPI to maintain support for object longevity. Do not get an accessor
        //from a Schema.

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
        //It is super important that all references to data accessors originated
        //from a HollowAPI to maintain support for object longevity. Do not get an accessor
        //from a Schema.
        FieldPathSegment[] pathElements = field.getSchemaFieldPositionPath();
        for (int posIdx = 0; posIdx < pathElements.length - 1; posIdx++) {
            FieldPathSegment fieldPathElement = pathElements[posIdx];
            ordinal = fieldPathElement.getOrdinalForField(ordinal);
        }
        return ordinal;
    }

    /**
     * This method is similar to {@link HollowPrimaryKeyValueDeriver#keyMatches(int, Object...)}.
     *
     * @param ordinal ordinal of record to match
     * @param keys    keys to match against
     * @return true if object's keys matches the specified keys
     */
    private boolean keysAllMatch(int ordinal, Object... keys) {
        for (int i = 0; i < keys.length; i++) {
            if (!keyMatches(keys[i], ordinal, i))
                return false;
        }

        return true;
    }

    /**
     * This method is similar to {@link HollowPrimaryKeyValueDeriver#keyMatches(Object, int, int)}
     *
     * @param key           key to match field against
     * @param recordOrdinal ordinal of record to match
     * @param fieldIdx      index of field to match against
     * @return true if the object's field matches the specified key
     */
    private boolean keyMatches(Object key, int recordOrdinal, int fieldIdx) {
        //It is super important that all references to data accessors originated
        //from a HollowAPI to maintain support for object longevity. Do not get an accessor
        //from a Schema.

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
        //It is super important that all references to data accessors originated
        //from a HollowAPI to maintain support for object longevity. Do not get an accessor
        //from a Schema.

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

    private boolean shouldPerformDeltaUpdate() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        //This doesn't affect compatibility with object longevity since this only gets invoked
        //when the index is being updated.
        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        BitSet ordinals = typeState.getPopulatedOrdinals();

        int prevCardinality = 0;
        int removedRecords = 0;

        int prevOrdinal = previousOrdinals.nextSetBit(0);
        while (prevOrdinal != ORDINAL_NONE) {
            prevCardinality++;
            if (!ordinals.get(prevOrdinal))
                removedRecords++;

            prevOrdinal = previousOrdinals.nextSetBit(prevOrdinal + 1);
        }

        return !(removedRecords > prevCardinality * 0.1d);
    }

    private static class OrdinalNotFoundException extends IllegalStateException {
        public OrdinalNotFoundException(String message) {
            super(message);
        }
    }

}
