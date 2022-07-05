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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static java.util.Objects.requireNonNull;

/**
 * A HollowPrimaryKeyIndex is the go-to mechanism for indexing and querying data in a Hollow blob.
 * <p>
 * A primary key index can be used to index and query a type by a {@link PrimaryKey}.  The provided {@link PrimaryKey} does
 * not have to be the same as declared as the default in the data model.
 */
public class HollowPrimaryKeyIndex implements HollowTypeStateListener {
    private static final Logger LOG = Logger.getLogger(HollowPrimaryKeyIndex.class.getName());

    private final HollowObjectTypeDataAccess objectTypeDataAccess;

    /**
     * Path of each field.
     */
    private final FieldPathElement[][] fieldPaths;

    /**
     * FieldType of the last element in each {@link #fieldPaths}.
     */
    private final FieldType[] fieldTypes;

    private final PrimaryKey primaryKey;
    private final HollowPrimaryKeyValueDeriver2 keyDeriver;

    private final ArraySegmentRecycler memoryRecycler;

    private final BitSet specificOrdinalsToIndex;

    private volatile PrimaryKeyIndexHashTable hashTableVolatile;

    public HollowPrimaryKeyIndex(HollowDataAccess stateEngine, String type, String... fieldPaths) {
        this(stateEngine, WastefulRecycler.DEFAULT_INSTANCE, type, fieldPaths);
    }

    public HollowPrimaryKeyIndex(HollowDataAccess stateEngine, PrimaryKey primaryKey) {
        this(stateEngine, primaryKey, WastefulRecycler.DEFAULT_INSTANCE);
    }

    public HollowPrimaryKeyIndex(HollowDataAccess stateEngine, ArraySegmentRecycler memoryRecycler, String type, String... fieldPaths) {
        this(stateEngine, createPrimaryKey(stateEngine, type, fieldPaths), memoryRecycler);
    }

    public HollowPrimaryKeyIndex(HollowDataAccess stateEngine, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler) {
        this(stateEngine, primaryKey, memoryRecycler, null);
    }

    /**
     * This initializer can be used to create a HollowPrimaryKeyIndex which will only index a subset of the records in the specified type.
     *
     * @param hollowDataAccess        the read state engine
     * @param primaryKey              the primary key
     * @param memoryRecycler          the memory recycler
     * @param specificOrdinalsToIndex the bit set
     */
    public HollowPrimaryKeyIndex(HollowDataAccess hollowDataAccess, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler, BitSet specificOrdinalsToIndex) {
        requireNonNull(primaryKey, "Hollow Primary Key Index creation failed because primaryKey was null");
        requireNonNull(hollowDataAccess, "Hollow Primary Key Index creation for type [" + primaryKey.getType()
                + "] failed because read state wasn't initialized");

        this.primaryKey = primaryKey;
        this.objectTypeDataAccess = (HollowObjectTypeDataAccess) hollowDataAccess.getTypeDataAccess(primaryKey.getType());
        this.fieldPaths = new FieldPathElement[primaryKey.numFields()][];
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        this.memoryRecycler = memoryRecycler;

        for (int fieldIdx = 0; fieldIdx < primaryKey.numFields(); fieldIdx++) {
            fieldTypes[fieldIdx] = primaryKey.getFieldType(hollowDataAccess, fieldIdx);

            //This always starts at the "root" object that's being indexed
            HollowObjectTypeDataAccess currentDataAccess = this.objectTypeDataAccess;

            int[] fieldPathPositions = primaryKey.getFieldPathIndex(hollowDataAccess, fieldIdx);
            FieldPathElement[] fieldPathElements = fieldPaths[fieldIdx] = new FieldPathElement[fieldPathPositions.length];

            for (int posIdx = 0; posIdx < fieldPathPositions.length; posIdx++) {
                if (currentDataAccess == null) {
                    throw new IllegalArgumentException("Path " + primaryKey.getFieldPath(fieldIdx) + " traverses a non-reference type. Non-reference types must be the last element of the path.");
                }

                int fieldPosition = fieldPathPositions[posIdx];
                fieldPathElements[posIdx] = new FieldPathElement(fieldPosition, currentDataAccess);

                //Using schema.getReferencedTypeState(...) will always use the *current* version and not necessarily
                //the version passed in through hollowDataAccess. This will break object longevity. As such, we have
                // to do this indirect lookup that reaches data access objects through hollowDataAccess.
                String referencedType = currentDataAccess.getSchema().getReferencedType(fieldPosition);
                if (referencedType != null) {
                    currentDataAccess = (HollowObjectTypeDataAccess) hollowDataAccess.getTypeDataAccess(referencedType);
                } else {
                    currentDataAccess = null;
                }
            }
        }

        this.keyDeriver = new HollowPrimaryKeyValueDeriver2(fieldPaths, fieldTypes);
        this.specificOrdinalsToIndex = specificOrdinalsToIndex;

        reindex();
    }

    private static PrimaryKey createPrimaryKey(HollowDataAccess stateEngine, String type, String... fieldPaths) {
        if (fieldPaths != null && fieldPaths.length != 0) {
            return new PrimaryKey(type, fieldPaths);
        }

        HollowSchema schema = stateEngine.getSchema(type);
        if (schema instanceof HollowObjectSchema) {
            return ((HollowObjectSchema) schema).getPrimaryKey();
        }
        return null;
    }

    /**
     * Once called, this HollowPrimaryKeyIndex will be kept up-to-date when deltas are applied to the indexed state engine.
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
            throw new IllegalStateException("Cannot listen for delta updates when objectTypeDataAccess is a " + objectTypeDataAccess.getClass().getSimpleName() + ". Is this index participating in object longevity?");

        ((HollowObjectTypeReadState) objectTypeDataAccess).addListener(this);

    }

    /**
     * Once called, this HollowPrimaryKeyIndex will no longer be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * Call this method before discarding indexes which are currently listening for delta updates.
     */
    public void detachFromDeltaUpdates() {
        if (objectTypeDataAccess instanceof HollowObjectTypeReadState)
            ((HollowObjectTypeReadState) objectTypeDataAccess).removeListener(this);
    }

    public HollowObjectTypeDataAccess getObjectTypeDataAccess() {
        return objectTypeDataAccess;
    }

    public HollowTypeReadState getTypeState() {
        return objectTypeDataAccess.getTypeState();
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public List<FieldType> getFieldTypes() {
        return Arrays.asList(fieldTypes);
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
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (fieldPaths.length != 1 || hashTable.bitsPerElement == 0)
            return -1;

        int hashCode = keyHashCode(key, 0);

        int ordinal = -1;

        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != -1) {
                if(keyDeriver.keyMatches(key, ordinal, 0))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile != hashTable);

        return ordinal;
    }

    /**
     * Query an index with two specified fields.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param key1 the first field key
     * @param key2 the second field key
     * @return the matching ordinal for the two keys, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object key1, Object key2) {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (fieldPaths.length != 2 || hashTable.bitsPerElement == 0)
            return -1;

        int hashCode = keyHashCode(key1, 0);
        hashCode ^= keyHashCode(key2, 1);

        int ordinal = -1;

        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != -1) {
                if(keyDeriver.keyMatches(key1, ordinal, 0) && keyDeriver.keyMatches(key2, ordinal, 1))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile != hashTable);

        return ordinal;
    }

    /**
     * Query an index with three specified fields.  The returned value with be the ordinal of the matching record.
     * <p>
     * Use a generated API or the Generic Object API to use the returned ordinal.
     *
     * @param key1 the first field key
     * @param key2 the second field key
     * @param key3 the third field key
     * @return the matching ordinal for the three keys, otherwise -1 if the key is not present
     */
    public int getMatchingOrdinal(Object key1, Object key2, Object key3) {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (fieldPaths.length != 3 || hashTable.bitsPerElement == 0)
            return -1;

        int hashCode = keyHashCode(key1, 0);
        hashCode ^= keyHashCode(key2, 1);
        hashCode ^= keyHashCode(key3, 2);

        int ordinal = -1;

        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != -1) {
                if(keyDeriver.keyMatches(key1, ordinal, 0) && keyDeriver.keyMatches(key2, ordinal, 1) && keyDeriver.keyMatches(key3, ordinal, 2))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile != hashTable);

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
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if (fieldPaths.length != keys.length || hashTable.bitsPerElement == 0)
            return -1;

        int hashCode = 0;
        for(int i=0;i<keys.length;i++)
            hashCode ^= keyHashCode(keys[i], i);

        int ordinal = -1;

        do {
            hashTable = this.hashTableVolatile;
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != -1) {
                if(keyDeriver.keyMatches(ordinal, keys))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile != hashTable);

        return ordinal;
    }

    private int readOrdinal(PrimaryKeyIndexHashTable hashTable, int bucket) {
        return (int)hashTable.hashTable.getElementValue((long)hashTable.bitsPerElement * (long)bucket, hashTable.bitsPerElement) - 1;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    private int keyHashCode(Object key, int fieldIdx) {
        switch(fieldTypes[fieldIdx]) {
            case BOOLEAN:
                return HashCodes.hashInt(HollowReadFieldUtils.booleanHashCode((Boolean)key));
            case DOUBLE:
                return HashCodes.hashInt(HollowReadFieldUtils.doubleHashCode(((Double)key).doubleValue()));
            case FLOAT:
                return HashCodes.hashInt(HollowReadFieldUtils.floatHashCode(((Float)key).floatValue()));
            case INT:
                return HashCodes.hashInt(HollowReadFieldUtils.intHashCode(((Integer)key).intValue()));
            case LONG:
                return HashCodes.hashInt(HollowReadFieldUtils.longHashCode(((Long)key).longValue()));
            case REFERENCE:
                return HashCodes.hashInt(((Integer)key).intValue());
            case BYTES:
                return HashCodes.hashCode((byte[])key);
            case STRING:
                return HashCodes.hashCode((String)key);
        }

        throw new IllegalArgumentException("I don't know how to hash a " + fieldTypes[fieldIdx]);
    }

    private void setHashTable(PrimaryKeyIndexHashTable hashTable) {
        this.hashTableVolatile = hashTable;
    }

    /**
     * @return whether or not this index contains duplicate records (two or more records mapping to a single primary key).
     */
    public boolean containsDuplicates() {
        return !getDuplicateKeys().isEmpty();
    }

    /**
     * @return any keys which are mapped to two or more records.
     */
    public synchronized Collection<Object[]> getDuplicateKeys() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if(hashTable.bitsPerElement == 0)
            return Collections.emptyList();

        List<Object[]> duplicateKeys = new ArrayList<>();

        for(int i=0;i<hashTable.hashTableSize;i++) {
            int ordinal = (int)hashTable.hashTable.getElementValue((long)i * (long)hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;

            if(ordinal != -1) {
                int compareBucket = (i+1) & hashTable.hashMask;
                int compareOrdinal = (int)hashTable.hashTable.getElementValue((long)compareBucket * (long)hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;
                while(compareOrdinal != -1) {
                    if(recordsHaveEqualKeys(ordinal, compareOrdinal))
                        duplicateKeys.add(keyDeriver.getRecordKey(ordinal));

                    compareBucket = (compareBucket + 1) & hashTable.hashMask;
                    compareOrdinal = (int)hashTable.hashTable.getElementValue((long)compareBucket * (long)hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;
                }
            }
        }

        return duplicateKeys;
    }

    @Override
    public void beginUpdate() { }

    @Override
    public void addedOrdinal(int ordinal) { }

    @Override
    public void removedOrdinal(int ordinal) { }

    private static final boolean ALLOW_DELTA_UPDATE =
            Boolean.getBoolean("com.netflix.hollow.core.index.HollowPrimaryKeyIndex.allowDeltaUpdate");

    @Override
    public synchronized void endUpdate() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        BitSet ordinals = typeState.getPopulatedOrdinals();

        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(typeState.maxOrdinal() + 1));

        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if(ALLOW_DELTA_UPDATE
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

    private static class OrdinalNotFoundException extends IllegalStateException {
        OrdinalNotFoundException(String s) {
            super(s);
        }
    }

    public void destroy() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if(hashTable != null)
            hashTable.hashTable.destroy(memoryRecycler);
    }

    private synchronized void reindex() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        // Could be null on first reindex
        if(hashTable != null) {
            hashTable.hashTable.destroy(memoryRecycler);
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();

        BitSet ordinals = specificOrdinalsToIndex;
        if (ordinals == null) {
            ordinals = typeState.getPopulatedOrdinals();
        }

        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(typeState.maxOrdinal() + 1));

        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, (long)hashTableSize * (long)bitsPerElement);

        int hashMask = hashTableSize - 1;

        int ordinal = ordinals.nextSetBit(0);
        while(ordinal != ORDINAL_NONE) {
            int hashCode = recordHash(ordinal);
            int bucket = hashCode & hashMask;

            while(hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) != 0)
                bucket = (bucket + 1) & hashMask;

            hashedArray.setElementValue((long)bucket * (long)bitsPerElement, bitsPerElement, ordinal + 1);

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
        BitSet prevOrdinals = typeState.getPreviousOrdinals();
        BitSet ordinals = typeState.getPopulatedOrdinals();

        long totalBitsInHashTable = (long)hashTableSize * (long)bitsPerElement;
        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, totalBitsInHashTable);
        hashedArray.copyBits(hashTable.hashTable, 0, 0, totalBitsInHashTable);

        int hashMask = hashTableSize - 1;

        int prevOrdinal = prevOrdinals.nextSetBit(0);
        while(prevOrdinal != ORDINAL_NONE) {
            if(!ordinals.get(prevOrdinal)) {
                /// find and remove this ordinal
                int hashCode = recordHash(prevOrdinal);
                int bucket = findOrdinalBucket(bitsPerElement, hashedArray, hashCode, hashMask, prevOrdinal);

                hashedArray.clearElementValue((long)bucket * (long)bitsPerElement, bitsPerElement);
                int emptyBucket = bucket;
                bucket = (bucket + 1) & hashMask;
                int moveOrdinal = (int)hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) - 1;

                while(moveOrdinal != ORDINAL_NONE) {
                    int naturalHash = recordHash(moveOrdinal);
                    int naturalBucket = naturalHash & hashMask;

                    if(!bucketInRange(emptyBucket, bucket, naturalBucket)) {
                        hashedArray.setElementValue((long)emptyBucket * (long)bitsPerElement, bitsPerElement, moveOrdinal + 1);
                        hashedArray.clearElementValue((long)bucket * (long)bitsPerElement, bitsPerElement);
                        emptyBucket = bucket;
                    }


                    bucket = (bucket + 1) & hashMask;
                    moveOrdinal = (int)hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) - 1;
                }

            }

            prevOrdinal = prevOrdinals.nextSetBit(prevOrdinal + 1);
        }


        int ordinal = ordinals.nextSetBit(0);
        while(ordinal != ORDINAL_NONE) {
            if(!prevOrdinals.get(ordinal)) {
                int hashCode = recordHash(ordinal);
                int bucket = hashCode & hashMask;

                while(hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) != 0) {
                    bucket = (bucket + 1) & hashMask;
                }

                hashedArray.setElementValue((long)bucket * (long)bitsPerElement, bitsPerElement, ordinal + 1);
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
            value = hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement);
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
        if(toBucket > fromBucket) {
            return testBucket > fromBucket && testBucket <= toBucket;
        } else {
            return testBucket > fromBucket || testBucket <= toBucket;
        }
    }

    private int recordHash(int ordinal) {
        int hashCode = 0;
        for (int i = 0; i < fieldPaths.length; i++) {
            hashCode ^= fieldHash(ordinal, i);
            // hashCode ^= HashCodes.hashInt(hashCode);
        }
        return hashCode;
    }


    private int fieldHash(int ordinal, int fieldIdx) {
        int lastPathIdx = fieldPaths[fieldIdx].length - 1;
        for (int pathIdx = 0; pathIdx < lastPathIdx; pathIdx++) {
            FieldPathElement pathElement = fieldPaths[fieldIdx][pathIdx];
            pathElement.objectTypeDataAccess.readOrdinal(ordinal, pathElement.fieldPosition);
            ordinal = pathElement.getOrdinalForField(ordinal);
        }
        //When the loop finishes, we should have the ordinal of the object containing the last field.
        FieldPathElement lastPathElement = fieldPaths[fieldIdx][lastPathIdx];

        int hashCode = HollowReadFieldUtils.fieldHashCode(lastPathElement.objectTypeDataAccess, ordinal, lastPathElement.fieldPosition);

        switch(fieldTypes[fieldIdx]) {
            case STRING:
            case BYTES:
                return hashCode;
            default:
                return HashCodes.hashInt(hashCode);
        }
    }

    public Object[] getRecordKey(int ordinal) {
        return keyDeriver.getRecordKey(ordinal);
    }

    private boolean recordsHaveEqualKeys(int ordinal1, int ordinal2) {
        for (int i = 0; i < fieldPaths.length; i++) {
            if(!fieldsAreEqual(ordinal1, ordinal2, i))
                return false;
        }
        return true;
    }

    private boolean fieldsAreEqual(int ordinal1, int ordinal2, int fieldIdx) {
        for (int posIdx = 0; posIdx < fieldPaths[fieldIdx].length - 1; posIdx++) {
            FieldPathElement pathElement = fieldPaths[fieldIdx][posIdx];
            ordinal1 = pathElement.getOrdinalForField(ordinal1);
            ordinal2 = pathElement.getOrdinalForField(ordinal2);
        }
        //Ordinals now reference the record that contains the last field value.
        //For a path with only one element, ordinal is unchanged. For a path with two elements,
        //ordinal will refer to the record for the first element. Using that ordinal, you can
        //then invoke lastPathElement.getOrdinal(ordinal) to get the final element.

        if (fieldTypes[fieldIdx] == FieldType.REFERENCE)
            return ordinal1 == ordinal2;

        FieldPathElement lastPathElement = fieldPaths[fieldIdx][fieldPaths[fieldIdx].length - 1];
        return HollowReadFieldUtils.fieldsAreEqual(
                lastPathElement.objectTypeDataAccess, ordinal1, lastPathElement.fieldPosition,
                lastPathElement.objectTypeDataAccess, ordinal2, lastPathElement.fieldPosition);
    }

    private boolean shouldPerformDeltaUpdate() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) this.objectTypeDataAccess.getTypeState();
        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        BitSet ordinals = typeState.getPopulatedOrdinals();

        int prevCardinality = 0;
        int removedRecords = 0;

        int prevOrdinal = previousOrdinals.nextSetBit(0);
        while(prevOrdinal != ORDINAL_NONE) {
            prevCardinality++;
            if (!ordinals.get(prevOrdinal))
                removedRecords++;

            prevOrdinal = previousOrdinals.nextSetBit(prevOrdinal + 1);
        }

        return !(removedRecords > prevCardinality * 0.1d);
    }

    private static class PrimaryKeyIndexHashTable {
        private final FixedLengthElementArray hashTable;
        private final int hashTableSize;
        private final int hashMask;
        private final int bitsPerElement;

        public PrimaryKeyIndexHashTable(FixedLengthElementArray hashTable, int hashTableSize, int hashMask, int bitsPerElement) {
            this.hashTable = hashTable;
            this.hashTableSize = hashTableSize;
            this.hashMask = hashMask;
            this.bitsPerElement = bitsPerElement;
        }
    }

    static class FieldPathElement {
        /**
         * Field position for this element of the path. For path {@code actor.name},
         * {@code actor} is 0 and {@code name} is 1.
         */
        private final int fieldPosition;

        /**
         * For the path {@code actor.name}, position 0 is {@code actor} and the data access is
         * {@code ThingThatReferencesActorDataAccess}. For {@code name}, position is 1 and data access
         * is {@code ActorTypeDataAccess}.
         */
        private final HollowObjectTypeDataAccess objectTypeDataAccess;

        private FieldPathElement(int fieldPosition, HollowObjectTypeDataAccess objectTypeDataAccess) {
            this.fieldPosition = fieldPosition;
            this.objectTypeDataAccess = objectTypeDataAccess;
        }

        /**
         * @param ordinal ordinal of record containing the desired field.
         * @return ordinal of the record referenced by the field
         */
        int getOrdinalForField(int ordinal) {
            return this.objectTypeDataAccess.readOrdinal(ordinal, fieldPosition);
        }

        int getFieldPosition() {
            return fieldPosition;
        }

        HollowObjectTypeDataAccess getObjectTypeDataAccess() {
            return objectTypeDataAccess;
        }
    }
}
