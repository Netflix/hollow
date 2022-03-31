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
package com.netflix.hollow.tools.history.keyindex;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.tools.util.SearchUtils.MULTI_FIELD_KEY_DELIMITER;
import static com.netflix.hollow.tools.util.SearchUtils.getFieldPathIndexes;
import static com.netflix.hollow.tools.util.SearchUtils.getOrdinalToDisplay;
import static com.netflix.hollow.tools.util.SearchUtils.parseKey;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Arrays;
import java.util.BitSet;

public class HollowHistoryTypeKeyIndex {

    private final PrimaryKey primaryKey;
    private final String[][] keyFieldParts;
    private final boolean[] keyFieldIsIndexed;
    private HollowObjectSchema keySchema;

    private int[] hashedRecordKeys;
    private int[][] hashedFieldKeys;
    private LongList hashedFieldKeyChains;

    private int maxIndexedKeyOrdinal = 0;

    private final HollowWriteStateEngine writeStateEngine;
    private HollowReadStateEngine readStateEngine;
    private boolean isInitialized = false;

    public HollowHistoryTypeKeyIndex(PrimaryKey primaryKey, HollowDataset dataModel, HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) {
        this.primaryKey = primaryKey;
        this.writeStateEngine = writeEngine;
        this.readStateEngine = readEngine;
        this.keyFieldParts = getKeyFieldParts(dataModel);
        this.keyFieldIsIndexed = new boolean[primaryKey.numFields()];
    }

    public void addFieldIndex(String fieldName, HollowDataset dataModel) {
        String[] fieldPathParts = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), fieldName);
        
        for(int i=0;i<primaryKey.numFields();i++) {
            String[] pkFieldPathParts = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
            if(Arrays.equals(pkFieldPathParts, fieldPathParts)) {
                keyFieldIsIndexed[i] = true;
                break;
            }
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize(HollowObjectTypeReadState initialTypeState) {
        if (isInitialized) return;

        initKeySchema(initialTypeState.getSchema());
        initializeTypeWriteState();
        isInitialized = true;
    }

    public void updateReadStateEngine(HollowReadStateEngine readEngine) {
        readStateEngine=readEngine;
    }

    public void update(HollowObjectTypeReadState latestTypeState, boolean isDeltaAndIndexInitialized) {
        // copies over keys corresponding to previously populated ordinals AND currently populated ordinals
        copyExistingKeys(); // only populates the ordianl map (to be the OR of previous and current ordinals)
        if (latestTypeState == null) return;

        if (isDeltaAndIndexInitialized) {
            // For fwd delta transition: write all key objects that were added when going from v1 -> v2 to the history type key index
            populateNewCurrentRecordKeysIntoIndex(latestTypeState);
        }
        else {
            // when index is not yet initialized with data (usually on first fwd delta transition) or on  double snapshot
            populateAllCurrentRecordKeysIntoIndex(latestTypeState);
        }
    }

    public void hashRecordKeys() {
        HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(primaryKey.getType());
        if (keyTypeState == null) return;

        int hashTableSize = HashCodes.hashTableSize(keyTypeState.maxOrdinal() + 1);

        if(hashedRecordKeys == null || hashedRecordKeys.length < hashTableSize) {
            rehashAllRecordKeys(keyTypeState, hashTableSize);
        } else {
            hashNewRecordKeys(keyTypeState);
        }
    }

    private void hashNewRecordKeys(HollowObjectTypeReadState keyTypeState) {
        for(int i=maxIndexedKeyOrdinal+1;i<=keyTypeState.maxOrdinal();i++)
            indexOrdinal(keyTypeState, i, hashedRecordKeys, hashedFieldKeys, hashedFieldKeyChains);
        maxIndexedKeyOrdinal = keyTypeState.maxOrdinal();
    }

    private void rehashAllRecordKeys(HollowObjectTypeReadState keyTypeState, int hashTableSize) {
        int[] hashedRecordKeys = initializeHashedKeyArray(hashTableSize);
        int[][] hashedFieldKeys = new int[primaryKey.numFields()][];
        LongList hashedFieldKeyChains = new LongList();

        for(int i=0;i<primaryKey.numFields();i++)
            if(keyFieldIsIndexed[i])
                hashedFieldKeys[i] = initializeHashedKeyArray(hashTableSize);

        for(int i=0;i<=keyTypeState.maxOrdinal();i++)
            indexOrdinal(keyTypeState, i, hashedRecordKeys, hashedFieldKeys, hashedFieldKeyChains);

        this.hashedRecordKeys = hashedRecordKeys;
        this.hashedFieldKeys = hashedFieldKeys;
        this.hashedFieldKeyChains = hashedFieldKeyChains;
        this.maxIndexedKeyOrdinal = keyTypeState.maxOrdinal();
    }

    private void indexOrdinal(HollowObjectTypeReadState keyTypeState, int ordinal, int[] hashedRecordKeys, int[][] hashedFieldKeys, LongList hashedFieldKeyChains) {
        int bucketMask = hashedRecordKeys.length - 1;

        int bucket = hashKeyRecord(keyTypeState, ordinal) & bucketMask;
        while(hashedRecordKeys[bucket] != ORDINAL_NONE)
            bucket = (bucket + 1) & bucketMask;
        hashedRecordKeys[bucket] = ordinal;

        for(int j=0;j<primaryKey.numFields();j++) {
            if(keyFieldIsIndexed[j]) {
                int fieldBucket = HashCodes.hashInt(HollowReadFieldUtils.fieldHashCode(keyTypeState, ordinal, j)) & bucketMask;
                int chainStartIndex = hashedFieldKeys[j][fieldBucket];
                while(chainStartIndex != ORDINAL_NONE) {
                    int representativeOrdinal = (int)hashedFieldKeyChains.get(chainStartIndex);
                    if(HollowReadFieldUtils.fieldsAreEqual(keyTypeState, ordinal, j, keyTypeState, representativeOrdinal, j)) {
                        hashedFieldKeyChains.add(((long)chainStartIndex << 32) | ordinal);
                        hashedFieldKeys[j][fieldBucket] = hashedFieldKeyChains.size() - 1;
                        break;
                    }
                    fieldBucket = (fieldBucket + 1) & bucketMask;
                    chainStartIndex = hashedFieldKeys[j][fieldBucket];
                }
                if (chainStartIndex == ORDINAL_NONE) {
                    hashedFieldKeyChains.add(((long) Integer.MAX_VALUE << 32) | ordinal);
                    hashedFieldKeys[j][fieldBucket] = hashedFieldKeyChains.size() - 1;
                }
            }
        }
    }

    private int[] initializeHashedKeyArray(int hashTableSize) {
        int[] hashedRecordKeys = new int[hashTableSize];
        Arrays.fill(hashedRecordKeys, ORDINAL_NONE);
        return hashedRecordKeys;
    }

    private int hashKeyRecord(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for(int i=0;i<primaryKey.numFields();i++) {
            int fieldHashCode = HollowReadFieldUtils.fieldHashCode(typeState, ordinal, i);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    public int findKeyIndexOrdinal(HollowObjectTypeReadState typeState, int ordinal) {
        HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(primaryKey.getType());

        int bucketMask = hashedRecordKeys.length - 1;

        int bucket = findKeyHashCode(typeState, ordinal) & bucketMask;

        while(hashedRecordKeys[bucket] != ORDINAL_NONE) {
            if(recordMatchesKey(typeState, ordinal, keyTypeState, hashedRecordKeys[bucket]))
                return hashedRecordKeys[bucket];

            bucket++;
            bucket &= bucketMask;
        }

        return ORDINAL_NONE;
    }

    private int findKeyHashCode(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for (String[] keyFieldPart : keyFieldParts) {
            int fieldHashCode = findKeyFieldHashCode(typeState, ordinal, keyFieldPart, 0);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    public IntList queryIndexedFields(final String query) {
        final HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(primaryKey.getType());
        IntList matchingKeys = new IntList();
        if (keyTypeState == null){
            return matchingKeys;
        }

        String[] parts;
        if (query.contains(MULTI_FIELD_KEY_DELIMITER)) {  // composite field query, uses ':' as separator
            parts = query.split(MULTI_FIELD_KEY_DELIMITER);
            if (parts.length != primaryKey.numFields()) {
                return matchingKeys;
            }

            Object[] parsedKey;
            try {
                parsedKey = parseKey(readStateEngine, primaryKey, query);
            } catch(Exception e) {
                return matchingKeys;
            }

            BitSet selectedOrdinals = keyTypeState.getPopulatedOrdinals();
            int fieldPathIndexes[][] = getFieldPathIndexes(readStateEngine, primaryKey);
            int ordinal = getOrdinalToDisplay(readStateEngine, query, parsedKey, ORDINAL_NONE, selectedOrdinals, fieldPathIndexes, keyTypeState);
            matchingKeys.add(ordinal);
            return matchingKeys;
        }

        // match query against each indexed field
        for(int i=0;i<primaryKey.numFields();i++) {
            final int fieldIndex = i;
            try {
                int hashCode;
                if(keyFieldIsIndexed[i]) {
                    switch(keySchema.getFieldType(i)) {
                    case INT:
                        final int queryInt = Integer.parseInt(query);
                        hashCode = HollowReadFieldUtils.intHashCode(queryInt);
                        addMatches(new Matcher() {
                            public boolean foundMatch(int ordinal) {
                                return keyTypeState.readInt(ordinal, fieldIndex) == queryInt;
                            }
                        }, i, hashCode, matchingKeys);
                        break;
                    case LONG:
                        final long queryLong = Long.parseLong(query);
                        hashCode = HollowReadFieldUtils.longHashCode(queryLong);
                        addMatches(new Matcher() {
                            public boolean foundMatch(int ordinal) {
                                return keyTypeState.readLong(ordinal, fieldIndex) == queryLong;
                            }
                        }, i, hashCode, matchingKeys);
                        break;
                    case STRING:
                        hashCode = HashCodes.hashCode(query);
                        addMatches(new Matcher() {
                            public boolean foundMatch(int ordinal) {
                                return keyTypeState.isStringFieldEqual(ordinal, fieldIndex, query);
                            }
                        }, i, hashCode, matchingKeys);
                        break;
                    case DOUBLE:
                        final double queryDouble = Double.parseDouble(query);
                        hashCode = HollowReadFieldUtils.doubleHashCode(queryDouble);
                        addMatches(new Matcher() {
                            public boolean foundMatch(int ordinal) {
                                return keyTypeState.readDouble(ordinal, fieldIndex) == queryDouble;
                            }
                        }, i, hashCode, matchingKeys);
                        break;
                    case FLOAT:
                        final float queryFloat = Float.parseFloat(query);
                        hashCode = HollowReadFieldUtils.floatHashCode(queryFloat);
                        addMatches(new Matcher() {
                            public boolean foundMatch(int ordinal) {
                                return keyTypeState.readFloat(ordinal, fieldIndex) == queryFloat;
                            }
                        }, i, hashCode, matchingKeys);
                        break;
                    default:
                    }
                }
            } catch(NumberFormatException ignore) { }
        }

        return matchingKeys;
    }

    private void addMatches(Matcher matcher, int fieldIndex, int hashCode, IntList results) {
        int hashIntCode = HashCodes.hashInt(hashCode);
        int bucket = hashIntCode & (hashedFieldKeys[fieldIndex].length - 1);

        while(hashedFieldKeys[fieldIndex][bucket] != ORDINAL_NONE) {
            int chainIndex = hashedFieldKeys[fieldIndex][bucket];
            int representativeOrdinal = (int)hashedFieldKeyChains.get(chainIndex);
            if(matcher.foundMatch(representativeOrdinal)) {
                while(representativeOrdinal != ORDINAL_NONE) {
                    results.add(representativeOrdinal);
                    chainIndex = (int)(hashedFieldKeyChains.get(chainIndex) >> 32);
                    representativeOrdinal = (chainIndex == Integer.MAX_VALUE) ? ORDINAL_NONE : (int)hashedFieldKeyChains.get(chainIndex);
                }
                return;
            }

            bucket++;
            bucket &= hashedFieldKeys[fieldIndex].length - 1;
        }
    }

    private interface Matcher {
        boolean foundMatch(int ordinal);
    }

    private int findKeyFieldHashCode(HollowObjectTypeReadState typeState, int ordinal, String[] keyFieldParts, int keyFieldPartPosition) {
        int schemaPosition = typeState.getSchema().getPosition(keyFieldParts[keyFieldPartPosition]);
        if(keyFieldPartPosition < keyFieldParts.length - 1) {
            HollowObjectTypeReadState nextPartTypeState = (HollowObjectTypeReadState) typeState.getSchema().getReferencedTypeState(schemaPosition);
            int nextOrdinal = typeState.readOrdinal(ordinal, schemaPosition);
            return findKeyFieldHashCode(nextPartTypeState, nextOrdinal, keyFieldParts, keyFieldPartPosition + 1);
        } else {
            return HollowReadFieldUtils.fieldHashCode(typeState, ordinal, schemaPosition);
        }
    }

    private boolean recordMatchesKey(HollowObjectTypeReadState typeState, int ordinal, HollowObjectTypeReadState keyTypeState, int keyOrdinal) {
        for(int i=0;i<keyFieldParts.length;i++) {
            if(!recordFieldMatchesKey(typeState, ordinal, keyTypeState, keyOrdinal, i, keyFieldParts[i], 0))
                return false;
        }
        return true;
    }

    private boolean recordFieldMatchesKey(HollowObjectTypeReadState typeState, int ordinal, HollowObjectTypeReadState keyTypeState, int keyOrdinal, int keyFieldPosition, String[] keyFieldParts, int keyFieldPartPosition) {
        int schemaPosition = typeState.getSchema().getPosition(keyFieldParts[keyFieldPartPosition]);
        if(keyFieldPartPosition < keyFieldParts.length - 1) {
            HollowObjectTypeReadState nextPartTypeState = (HollowObjectTypeReadState) typeState.getSchema().getReferencedTypeState(schemaPosition);
            int nextOrdinal = typeState.readOrdinal(ordinal, schemaPosition);
            return recordFieldMatchesKey(nextPartTypeState, nextOrdinal, keyTypeState, keyOrdinal, keyFieldPosition, keyFieldParts, keyFieldPartPosition + 1);
        } else {
            return HollowReadFieldUtils.fieldsAreEqual(typeState, ordinal, schemaPosition, keyTypeState, keyOrdinal, keyFieldPosition);
        }
    }

    private void copyExistingKeys() {
        HollowTypeWriteState typeState = writeStateEngine.getTypeState(primaryKey.getType());
        if (typeState == null) return;

        typeState.addAllObjectsFromPreviousCycle();
    }

    private void populateAllCurrentRecordKeysIntoIndex(HollowObjectTypeReadState typeState) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(keySchema);
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet previousOrdinals = listener.getPreviousOrdinals();
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();

        int maxLength = Math.max(previousOrdinals.length(), populatedOrdinals.length());

        for(int i=0;i<maxLength;i++) {
            if(populatedOrdinals.get(i) || previousOrdinals.get(i))
                writeKeyObject(typeState, i, rec);
        }
    }

    private void populateNewCurrentRecordKeysIntoIndex(HollowObjectTypeReadState typeState) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(keySchema);
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();
        BitSet previousOrdinals = listener.getPreviousOrdinals();

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(populatedOrdinals, previousOrdinals);
        int ordinal = iter.next();
        while(ordinal != ORDINAL_NONE) {
            writeKeyObject(typeState, ordinal, rec);
            ordinal = iter.next();
        }
    }

    public String[] getKeyFields() {
        return primaryKey.getFieldPaths();
    }

    public Object getKeyFieldValue(int keyFieldIdx, int keyOrdinal) {
        return HollowReadFieldUtils.fieldValueObject((HollowObjectTypeReadState)readStateEngine.getTypeState(primaryKey.getType()), keyOrdinal, keyFieldIdx);
    }

    public String getKeyDisplayString(int keyOrdinal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(primaryKey.getType());

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<primaryKey.numFields();i++) {
            builder.append(HollowReadFieldUtils.displayString(typeState, keyOrdinal, i));
            if(i < primaryKey.numFields() - 1)
                builder.append(MULTI_FIELD_KEY_DELIMITER);
        }
        return builder.toString();
    }

    private void writeKeyObject(HollowObjectTypeReadState typeState, int ordinal, HollowObjectWriteRecord rec) {
        rec.reset();
        for(int i=0;i<keyFieldParts.length;i++) {
            writeKeyField(typeState, ordinal, rec, primaryKey.getFieldPath(i), keyFieldParts[i], 0);
        }
        writeStateEngine.add(primaryKey.getType(), rec);
    }

    private void writeKeyField(HollowObjectTypeReadState typeState, int ordinal, HollowObjectWriteRecord rec, String keyField, String[] keyFieldParts, int keyFieldPartPosition) {
        int schemaPosition = typeState.getSchema().getPosition(keyFieldParts[keyFieldPartPosition]);
        if(keyFieldPartPosition < keyFieldParts.length - 1) {
            HollowObjectTypeReadState nextPartTypeState = (HollowObjectTypeReadState) typeState.getSchema().getReferencedTypeState(schemaPosition);
            int nextOrdinal = typeState.readOrdinal(ordinal, schemaPosition);
            writeKeyField(nextPartTypeState, nextOrdinal, rec, keyField, keyFieldParts, keyFieldPartPosition + 1);
        } else {
            switch(typeState.getSchema().getFieldType(schemaPosition)) {
            case BOOLEAN:
                Boolean bool = typeState.readBoolean(ordinal, schemaPosition);
                if(bool != null)
                    rec.setBoolean(keyField, bool);
                break;
            case BYTES:
                byte[] b = typeState.readBytes(ordinal, schemaPosition);
                if(b != null)
                    rec.setBytes(keyField, b);
                break;
            case DOUBLE:
                double d = typeState.readDouble(ordinal, schemaPosition);
                if(!Double.isNaN(d))
                    rec.setDouble(keyField, d);
                break;
            case FLOAT:
                float f = typeState.readFloat(ordinal, schemaPosition);
                if(!Float.isNaN(f))
                    rec.setFloat(keyField, f);
                break;
            case INT:
                int i = typeState.readInt(ordinal, schemaPosition);
                rec.setInt(keyField, i);
                break;
            case LONG:
                long l = typeState.readLong(ordinal, schemaPosition);
                rec.setLong(keyField, l);
                break;
            case STRING:
                String s = typeState.readString(ordinal, schemaPosition);
                if(s != null)
                    rec.setString(keyField, s);
                break;
            default:
                throw new IllegalArgumentException("Primary key components must be a value leaf node");
            }
        }
    }

    private void initKeySchema(HollowObjectSchema entireObjectSchema) {
        if(keySchema == null) {
            keySchema = new HollowObjectSchema(primaryKey.getType(), primaryKey.numFields());
            for(int i=0;i<keyFieldParts.length;i++)
                addSchemaField(entireObjectSchema, keySchema, primaryKey.getFieldPath(i), keyFieldParts[i], 0);
        }
    }

    private void addSchemaField(HollowObjectSchema schema, HollowObjectSchema keySchema, String keyField, String[] keyFieldParts, int keyFieldPartPosition) {
        int schemaPosition = schema.getPosition(keyFieldParts[keyFieldPartPosition]);
        if(keyFieldPartPosition < keyFieldParts.length - 1) {
            HollowObjectSchema nextPartSchema = (HollowObjectSchema) schema.getReferencedTypeState(schemaPosition).getSchema();
            addSchemaField(nextPartSchema, keySchema, keyField, keyFieldParts, keyFieldPartPosition + 1);
        } else {
            keySchema.addField(keyField, schema.getFieldType(schemaPosition), schema.getReferencedType(schemaPosition));
        }
    }

    private void initializeTypeWriteState() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(keySchema);
        writeStateEngine.addTypeState(writeState);
    }

    private String[][] getKeyFieldParts(HollowDataset dataModel) {
        String[][] keyFieldParts = new String[primaryKey.numFields()][];
        for(int i=0;i<primaryKey.numFields();i++)
            keyFieldParts[i] = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
        return keyFieldParts;
    }

}
