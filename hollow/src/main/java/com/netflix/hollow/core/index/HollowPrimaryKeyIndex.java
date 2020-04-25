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
import static java.util.Objects.requireNonNull;

import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
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

/**
 * A HollowPrimaryKeyIndex is the go-to mechanism for indexing and querying data in a Hollow blob.
 * <p>
 * A primary key index can be used to index and query a type by a {@link PrimaryKey}.  The provided {@link PrimaryKey} does
 * not have to be the same as declared as the default in the data model.
 */
public class HollowPrimaryKeyIndex implements HollowTypeStateListener {
    private static final Logger LOG = Logger.getLogger(HollowPrimaryKeyIndex.class.getName());

    private final HollowObjectTypeReadState typeState;
    private final int[][] fieldPathIndexes;
    private final FieldType[] fieldTypes;
    private final PrimaryKey primaryKey;
    private final HollowPrimaryKeyValueDeriver keyDeriver;

    private final ArraySegmentRecycler memoryRecycler;

    private final BitSet specificOrdinalsToIndex;

    private volatile PrimaryKeyIndexHashTable hashTableVolatile;

    public HollowPrimaryKeyIndex(HollowReadStateEngine stateEngine, String type, String... fieldPaths) {
        this(stateEngine, WastefulRecycler.DEFAULT_INSTANCE, type, fieldPaths);
    }

    public HollowPrimaryKeyIndex(HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
        this(stateEngine, primaryKey, WastefulRecycler.DEFAULT_INSTANCE);
    }

    public HollowPrimaryKeyIndex(HollowReadStateEngine stateEngine, ArraySegmentRecycler memoryRecycler, String type, String... fieldPaths) {
        this(stateEngine, createPrimaryKey(stateEngine, type, fieldPaths), memoryRecycler);
    }

    public HollowPrimaryKeyIndex(HollowReadStateEngine stateEngine, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler) {
        this(stateEngine, primaryKey, memoryRecycler, null);
    }

    /**
     * This initializer can be used to create a HollowPrimaryKeyIndex which will only index a subset of the records in the specified type.
     *
     * @param stateEngine the read state engine
     * @param primaryKey the primary key
     * @param memoryRecycler the memory recycler
     * @param specificOrdinalsToIndex the bit set
     */
    public HollowPrimaryKeyIndex(HollowReadStateEngine stateEngine, PrimaryKey primaryKey, ArraySegmentRecycler memoryRecycler, BitSet specificOrdinalsToIndex) {
        requireNonNull(primaryKey, "Hollow Primary Key Index creation failed because primaryKey was null");
        requireNonNull(stateEngine, "Hollow Primary Key Index creation for type [" + primaryKey.getType()
                + "] failed because read state wasn't initialized");

        this.primaryKey = primaryKey;
        this.typeState = (HollowObjectTypeReadState) stateEngine.getTypeState(primaryKey.getType());
        this.fieldPathIndexes = new int[primaryKey.numFields()][];
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        this.memoryRecycler = memoryRecycler;

        for(int i=0;i<primaryKey.numFields();i++) {
            fieldPathIndexes[i] = primaryKey.getFieldPathIndex(stateEngine, i);
            fieldTypes[i] = primaryKey.getFieldType(stateEngine, i);
        }

        this.keyDeriver = new HollowPrimaryKeyValueDeriver(typeState, fieldPathIndexes, fieldTypes);
        this.specificOrdinalsToIndex = specificOrdinalsToIndex;

        reindex();
    }

    private static PrimaryKey createPrimaryKey(HollowReadStateEngine stateEngine, String type, String... fieldPaths) {
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
        if(specificOrdinalsToIndex != null)
            throw new IllegalStateException("Cannot listen for delta updates when indexing only specified ordinals!");

        typeState.addListener(this);
    }

    /**
     * Once called, this HollowPrimaryKeyIndex will no longer be kept up-to-date when deltas are applied to the indexed state engine.
     * <p>
     * Call this method before discarding indexes which are currently listening for delta updates.
     */
    public void detachFromDeltaUpdates() {
        typeState.removeListener(this);
    }

    public HollowObjectTypeReadState getTypeState() {
        return typeState;
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
        if(fieldPathIndexes.length != 1 || hashTable.bitsPerElement == 0)
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
        if(fieldPathIndexes.length != 2 || hashTable.bitsPerElement == 0)
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
        if(fieldPathIndexes.length != 3 || hashTable.bitsPerElement == 0)
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
        if(fieldPathIndexes.length != keys.length || hashTable.bitsPerElement == 0)
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

        List<Object[]> duplicateKeys = new ArrayList<Object[]>();

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
        BitSet ordinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

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

        BitSet ordinals = specificOrdinalsToIndex;

        if(ordinals == null) {
            PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
            ordinals = listener.getPopulatedOrdinals();
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

        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet prevOrdinals = listener.getPreviousOrdinals();
        BitSet ordinals = listener.getPopulatedOrdinals();

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
        for(int i=0;i<fieldPathIndexes.length;i++) {
            hashCode ^= fieldHash(ordinal, i);
            // hashCode ^= HashCodes.hashInt(hashCode);
        }
        return hashCode;
    }


    private int fieldHash(int ordinal, int fieldIdx) {
        HollowObjectTypeReadState typeState = this.typeState;
        HollowObjectSchema schema = typeState.getSchema();

        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for(int i=0;i<lastFieldPath;i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ordinal = typeState.readOrdinal(ordinal, fieldPosition);
            typeState = (HollowObjectTypeReadState) schema.getReferencedTypeState(fieldPosition);
            schema = typeState.getSchema();
        }

        int hashCode = HollowReadFieldUtils.fieldHashCode(typeState, ordinal, fieldPathIndexes[fieldIdx][lastFieldPath]);

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
        for(int i=0;i<fieldPathIndexes.length;i++) {
            if(!fieldsAreEqual(ordinal1, ordinal2, i))
                return false;
        }
        return true;
    }

    private boolean fieldsAreEqual(int ordinal1, int ordinal2, int fieldIdx) {
        HollowObjectTypeReadState typeState = this.typeState;
        HollowObjectSchema schema = typeState.getSchema();

        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for(int i=0;i<lastFieldPath;i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ordinal1 = typeState.readOrdinal(ordinal1, fieldPosition);
            ordinal2 = typeState.readOrdinal(ordinal2, fieldPosition);
            typeState = (HollowObjectTypeReadState) schema.getReferencedTypeState(fieldPosition);
            schema = typeState.getSchema();
        }

        if(fieldTypes[fieldIdx] == FieldType.REFERENCE)
            return typeState.readOrdinal(ordinal1, fieldPathIndexes[fieldIdx][lastFieldPath]) == typeState.readOrdinal(ordinal2, fieldPathIndexes[fieldIdx][lastFieldPath]);

        return HollowReadFieldUtils.fieldsAreEqual(typeState, ordinal1, fieldPathIndexes[fieldIdx][lastFieldPath], typeState, ordinal2, fieldPathIndexes[fieldIdx][lastFieldPath]);
    }

    private boolean shouldPerformDeltaUpdate() {
        BitSet previousOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPreviousOrdinals();
        BitSet ordinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int prevCardinality = 0;
        int removedRecords = 0;

        int prevOrdinal = previousOrdinals.nextSetBit(0);
        while(prevOrdinal != ORDINAL_NONE) {
            prevCardinality++;
            if(!ordinals.get(prevOrdinal))
                removedRecords++;

            prevOrdinal = previousOrdinals.nextSetBit(prevOrdinal + 1);
        }

        if(removedRecords > prevCardinality * 0.1d)
            return false;
        return true;
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
}
