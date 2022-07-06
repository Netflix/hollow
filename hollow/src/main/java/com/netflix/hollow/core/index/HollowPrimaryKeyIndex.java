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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A HollowPrimaryKeyIndex is the go-to mechanism for indexing and querying data in a Hollow blob.
 * <p>
 * A primary key index can be used to index and query a type by a {@link PrimaryKey}.  The provided {@link PrimaryKey} does
 * not have to be the same as declared as the default in the data model.
 */
public class HollowPrimaryKeyIndex implements HollowTypeStateListener, UniqueKeyIndex {
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
        this(stateEngine, PrimaryKey.create(stateEngine, type, fieldPaths), memoryRecycler);
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
            return ORDINAL_NONE;

        return getMatchingOrdinal(
                () -> this.hashTableVolatile,
                keyDeriver::keyMatches,
                this.fieldTypes[0], key,
                null, null,
                null, null);
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
            return ORDINAL_NONE;

        return getMatchingOrdinal(
                () -> this.hashTableVolatile,
                keyDeriver::keyMatches,
                this.fieldTypes[0], key1,
                this.fieldTypes[1], key2,
                null, null);
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
            return ORDINAL_NONE;

        return getMatchingOrdinal(
                () -> this.hashTableVolatile,
                keyDeriver::keyMatches,
                this.fieldTypes[0], key1,
                this.fieldTypes[1], key2,
                this.fieldTypes[2], key3);
    }

    /**
     * @param hashTableVolatile getter for the volatile hash table reference
     * @param keyMatcher        key matcher
     * @param fieldType0        type for field 0
     * @param key0              key for field 0
     * @param fieldType1        type for field 1. <b>use null if there is no field 1</b>
     * @param key1              key for field 1
     * @param fieldType2        type for field 2. <b>use null if there is no field 2</b>
     * @param key2              key for field 2
     * @return ordinal or {@link com.netflix.hollow.core.HollowConstants#ORDINAL_NONE}
     */
    static int getMatchingOrdinal(
            Supplier<PrimaryKeyIndexHashTable> hashTableVolatile,
            KeyMatcher keyMatcher,
            FieldType fieldType0,
            Object key0,
            FieldType fieldType1,
            Object key1,
            FieldType fieldType2,
            Object key2) {
        int hashCode = keyHashCode(key0, fieldType0);
        if(fieldType1 != null) {
            hashCode ^= keyHashCode(key1, fieldType1);
            if (fieldType2 != null) {
                hashCode ^= keyHashCode(key2, fieldType2);
            }
        }

        PrimaryKeyIndexHashTable hashTable;
        int ordinal;
        do {
            hashTable = hashTableVolatile.get();
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != ORDINAL_NONE) {
                if(keyMatcher.test(key0, ordinal, 0)
                        && (fieldType1 == null || keyMatcher.test(key1, ordinal, 1))
                        && (fieldType2 == null || keyMatcher.test(key2, ordinal, 2))) {
                    //This is a match. Break and return the ordinal.
                    break;
                }

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile.get() != hashTable);

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
            return ORDINAL_NONE;

        return getMatchingOrdinal(
                () -> HollowPrimaryKeyIndex.this.hashTableVolatile,
                HollowPrimaryKeyIndex.this.keyDeriver::keyMatches,
                fieldIdx -> HollowPrimaryKeyIndex.this.fieldTypes[fieldIdx],
                keys);
    }

    /**
     * @param hashTableVolatile getter for the volatile hash table reference
     * @param keyMatcher        key matcher
     * @param fieldTypes         type for fields by index
     * @param keys              keys
     * @return ordinal or {@link com.netflix.hollow.core.HollowConstants#ORDINAL_NONE}
     */
    static int getMatchingOrdinal(
            Supplier<PrimaryKeyIndexHashTable> hashTableVolatile,
            KeyMatcher keyMatcher,
            IntFunction<FieldType> fieldTypes,
            Object ... keys) {
        int hashCode = 0;
        for(int i=0; i<keys.length; i++)
            hashCode ^= keyHashCode(keys[i], fieldTypes.apply(i));

        PrimaryKeyIndexHashTable hashTable;
        int ordinal;

        do {
            hashTable = hashTableVolatile.get();
            int bucket = hashCode & hashTable.hashMask;
            ordinal = readOrdinal(hashTable, bucket);
            while(ordinal != ORDINAL_NONE) {
                if(keysAllMatch(keyMatcher, ordinal, keys))
                    break;

                bucket++;
                bucket &= hashTable.hashMask;
                ordinal = readOrdinal(hashTable, bucket);
            }
        } while(hashTableVolatile.get() != hashTable);

        return ordinal;
    }

    private static boolean keysAllMatch(KeyMatcher keyMatcher, int ordinal, Object ... keys) {
        for(int i=0;i<keys.length;i++) {
            if(!keyMatcher.test(keys[i], ordinal, i))
                return false;
        }

        return true;
    }


    static int readOrdinal(PrimaryKeyIndexHashTable hashTable, int bucket) {
        return (int)hashTable.hashTable.getElementValue((long)hashTable.bitsPerElement * (long)bucket, hashTable.bitsPerElement) - 1;
    }

    /**
     * Calculate the hash for the given key. The hash method varies depending on the expected field type.
     *
     * This enforces the type of the key vs the field tyle and does not allow for coercion
     * (key = int, fieldType = Double, etc).
     * @param key       key to hash
     * @param fieldType field type
     * @return hash code
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    static int keyHashCode(Object key, FieldType fieldType) {
        switch(fieldType) {
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

        throw new IllegalArgumentException("I don't know how to hash a " + fieldType);
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
        //Synchronized to prevent index changes while this calculation is occurring
        return getDuplicateKeys(hashTableVolatile,this::recordsHaveEqualKeys, keyDeriver::getRecordKey);
    }

    /**
     * This method should be synchronized by the caller.
     *
     * @return any keys which are mapped to two or more records.
     */
    static Collection<Object[]> getDuplicateKeys(
            PrimaryKeyIndexHashTable hashTable,
            CompareOrdinalPredicate recordsHaveEqualKeys,
            IntFunction<Object[]> getRecordKey) {
        if(hashTable.bitsPerElement == 0)
            return Collections.emptyList();

        List<Object[]> duplicateKeys = new ArrayList<>();

        for(int i=0;i<hashTable.hashTableSize;i++) {
            int ordinal = (int)hashTable.hashTable.getElementValue((long)i * (long)hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;

            if(ordinal != -1) {
                int compareBucket = (i+1) & hashTable.hashMask;
                int compareOrdinal = (int)hashTable.hashTable.getElementValue((long)compareBucket * (long)hashTable.bitsPerElement, hashTable.bitsPerElement) - 1;
                while(compareOrdinal != -1) {
                    if(recordsHaveEqualKeys.test(ordinal, compareOrdinal)) {
                        duplicateKeys.add(getRecordKey.apply(ordinal));
                    }

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

    static void endUpdateImpl(ArraySegmentRecycler memoryRecycler,
                              Consumer<PrimaryKeyIndexHashTable> hashTableSetter,
                              PrimaryKeyIndexHashTable hashTable,
                              BitSet specificOrdinalsToIndex,
                              BitSet ordinals,
                              BitSet prevOrdinals,
                              int maxOrdinal,
                              RecordHasher recordHasher) {
        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(maxOrdinal + 1));

        //In this block, hashTableSetter should only be set once.

        if(ALLOW_DELTA_UPDATE
                && hashTableSize == hashTable.hashTableSize
                && bitsPerElement == hashTable.bitsPerElement
                && shouldPerformDeltaUpdate(ordinals, prevOrdinals)) {
            try {
                deltaUpdate(memoryRecycler, hashTableSetter, hashTable, ordinals, prevOrdinals, recordHasher, hashTableSize, bitsPerElement);
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
                reindexImpl(memoryRecycler, hashTableSetter, hashTable, specificOrdinalsToIndex, ordinals, maxOrdinal, recordHasher);
            }
        } else {
            reindexImpl(memoryRecycler, hashTableSetter, hashTable, specificOrdinalsToIndex, ordinals, maxOrdinal, recordHasher);
        }
    }

    @Override
    public synchronized void endUpdate() {
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet ordinals = listener.getPopulatedOrdinals();
        BitSet prevOrdinals = listener.getPreviousOrdinals();

        endUpdateImpl(memoryRecycler,
                this::setHashTable,
                this.hashTableVolatile,
                this.specificOrdinalsToIndex,
                ordinals,
                prevOrdinals,
                typeState.maxOrdinal(),
                this::recordHash);
    }

    static class OrdinalNotFoundException extends IllegalStateException {
        OrdinalNotFoundException(String s) {
            super(s);
        }
    }

    public void destroy() {
        PrimaryKeyIndexHashTable hashTable = hashTableVolatile;
        if(hashTable != null)
            hashTable.hashTable.destroy(memoryRecycler);
    }

    static synchronized void reindexImpl(
            ArraySegmentRecycler memoryRecycler,
            Consumer<PrimaryKeyIndexHashTable> hashTableSetter,
            PrimaryKeyIndexHashTable hashTable,
            BitSet specificOrdinalsToIndex,
            BitSet ordinals,
            int maxOrdinal,
            RecordHasher recordHasher) {
        // Could be null on first reindex
        if(hashTable != null) {
            hashTable.hashTable.destroy(memoryRecycler);
        }

        ordinals = specificOrdinalsToIndex != null ? specificOrdinalsToIndex : ordinals;

        int hashTableSize = HashCodes.hashTableSize(ordinals.cardinality());
        int bitsPerElement = (32 - Integer.numberOfLeadingZeros(maxOrdinal + 1));

        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, (long)hashTableSize * (long)bitsPerElement);

        int hashMask = hashTableSize - 1;

        int ordinal = ordinals.nextSetBit(0);
        while(ordinal != ORDINAL_NONE) {
            int hashCode = recordHasher.hash(ordinal);
            int bucket = hashCode & hashMask;

            while(hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) != 0)
                bucket = (bucket + 1) & hashMask;

            hashedArray.setElementValue((long)bucket * (long)bitsPerElement, bitsPerElement, ordinal + 1);

            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        hashTableSetter.accept(new PrimaryKeyIndexHashTable(hashedArray, hashTableSize, hashMask, bitsPerElement));

        memoryRecycler.swap();
    }

    /**
     * This method must remain private (or final) because it is called from the constructor
     */
    private synchronized void reindex() {
        reindexImpl(this.memoryRecycler,
                this::setHashTable,
                this.hashTableVolatile,
                this.specificOrdinalsToIndex,
                typeState.getPopulatedOrdinals(),
                this.typeState.maxOrdinal(),
                this::recordHash);
    }

    private static void deltaUpdate(
            ArraySegmentRecycler memoryRecycler,
            Consumer<PrimaryKeyIndexHashTable> hashTableSetter,
            PrimaryKeyIndexHashTable hashTable,
            BitSet ordinals,
            BitSet prevOrdinals,
            RecordHasher recordHasher,
            int hashTableSize, int bitsPerElement) {

        // For a delta update hashTableVolatile cannot be null
        hashTable.hashTable.destroy(memoryRecycler);

        long totalBitsInHashTable = (long)hashTableSize * (long)bitsPerElement;
        FixedLengthElementArray hashedArray = new FixedLengthElementArray(memoryRecycler, totalBitsInHashTable);
        hashedArray.copyBits(hashTable.hashTable, 0, 0, totalBitsInHashTable);

        int hashMask = hashTableSize - 1;

        int prevOrdinal = prevOrdinals.nextSetBit(0);
        while(prevOrdinal != ORDINAL_NONE) {
            if(!ordinals.get(prevOrdinal)) {
                /// find and remove this ordinal
                int hashCode = recordHasher.hash(prevOrdinal);
                int bucket = findOrdinalBucket(bitsPerElement, hashedArray, hashCode, hashMask, prevOrdinal);

                hashedArray.clearElementValue((long)bucket * (long)bitsPerElement, bitsPerElement);
                int emptyBucket = bucket;
                bucket = (bucket + 1) & hashMask;
                int moveOrdinal = (int)hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) - 1;

                while(moveOrdinal != ORDINAL_NONE) {
                    int naturalHash = recordHasher.hash(moveOrdinal);
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
                int hashCode = recordHasher.hash(ordinal);
                int bucket = hashCode & hashMask;

                while(hashedArray.getElementValue((long)bucket * (long)bitsPerElement, bitsPerElement) != 0) {
                    bucket = (bucket + 1) & hashMask;
                }

                hashedArray.setElementValue((long)bucket * (long)bitsPerElement, bitsPerElement, ordinal + 1);
            }

            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        hashTableSetter.accept(new PrimaryKeyIndexHashTable(hashedArray, hashTableSize, hashMask, bitsPerElement));

        memoryRecycler.swap();
    }

    private static int findOrdinalBucket(int bitsPerElement, FixedLengthElementArray hashedArray, int hashCode, int hashMask, int prevOrdinal) {
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

    private static boolean bucketInRange(int fromBucket, int toBucket, int testBucket) {
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

    private static boolean shouldPerformDeltaUpdate(BitSet ordinals, BitSet previousOrdinals) {
        int prevCardinality = 0;
        int removedRecords = 0;

        int prevOrdinal = previousOrdinals.nextSetBit(0);
        while(prevOrdinal != ORDINAL_NONE) {
            prevCardinality++;
            if(!ordinals.get(prevOrdinal))
                removedRecords++;

            prevOrdinal = previousOrdinals.nextSetBit(prevOrdinal + 1);
        }

        return !(removedRecords > prevCardinality * 0.1d);
    }

    static class PrimaryKeyIndexHashTable {
        final FixedLengthElementArray hashTable;
        final int hashTableSize;
        final int hashMask;
        final int bitsPerElement;

        public PrimaryKeyIndexHashTable(FixedLengthElementArray hashTable, int hashTableSize, int hashMask, int bitsPerElement) {
            this.hashTable = hashTable;
            this.hashTableSize = hashTableSize;
            this.hashMask = hashMask;
            this.bitsPerElement = bitsPerElement;
        }
    }

    /**
     * Functional interface for an ordinal comparison method
     */
    @FunctionalInterface
    interface CompareOrdinalPredicate {
        /**
         * Perform a comparison of the two ordinals and returns true if they are equivalent in some way.
         * @param lhsOrdinal first ordinal
         * @param rhsOrdinal second ordinal
         * @return true if they are equivalent
         */
        boolean test(int lhsOrdinal, int rhsOrdinal);
    }

    /**
     * Functional interface for hashing a record using an ordinal
     */
    @FunctionalInterface
    interface RecordHasher {
        /**
         * Hash the keys of this record.
         *
         * @param ordinal record to hash
         * @return hash code
         */
        int hash(int ordinal);
    }

    /**
     * Functional interface for determining if a provided key matches the keys for the specified ordinal.
     */
    @FunctionalInterface
    interface KeyMatcher {
        /**
         * @param key      key to compare
         * @param ordinal  ordinal of record to compare
         * @param fieldIdx field index of record
         * @return true if match, false if not
         */
        boolean test(Object key, int ordinal, int fieldIdx);
    }
}
