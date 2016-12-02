/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.util.Arrays;
import java.util.BitSet;

public class HollowHistoryTypeKeyIndex {

    private final String type;
    private final String keyFields[];
    private final String keyFieldParts[][];
    private final boolean keyFieldIsIndexed[];
    private HollowObjectSchema keySchema;

    private int hashedRecordKeys[];
    private int hashedFieldKeys[][];
    private LongList hashedFieldKeyChains;

    private int maxIndexedKeyOrdinal = 0;

    private final HollowWriteStateEngine writeStateEngine;
    private final HollowReadStateEngine readStateEngine;

    public HollowHistoryTypeKeyIndex(String type, HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, String... keyFields) {
        this.type = type;
        this.writeStateEngine = writeEngine;
        this.readStateEngine = readEngine;
        this.keyFields = keyFields;
        this.keyFieldParts = getKeyFieldParts();
        this.keyFieldIsIndexed = new boolean[keyFields.length];
    }

    public void addFieldIndex(String fieldName) {
        for(int i=0;i<keyFields.length;i++) {
            if(fieldName.equals(keyFields[i]))
                keyFieldIsIndexed[i] = true;
        }
    }

    public void initialize(HollowObjectTypeReadState initialTypeState) {
        initKeySchema(initialTypeState.getSchema());
        initializeTypeWriteState();
    }

    public void update(HollowObjectTypeReadState latestTypeState, boolean isDelta) {
        copyExistingKeys();
        if(isDelta)
            populateNewCurrentRecordKeys(latestTypeState);
        else
            populateAllCurrentRecordKeys(latestTypeState);
    }

    public void hashRecordKeys() {
        HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);

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
        int[][] hashedFieldKeys = new int[keyFields.length][];
        LongList hashedFieldKeyChains = new LongList();

        for(int i=0;i<keyFields.length;i++)
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
        while(hashedRecordKeys[bucket] != -1)
            bucket = (bucket + 1) & bucketMask;
        hashedRecordKeys[bucket] = ordinal;

        indexFields:
        for(int j=0;j<keyFields.length;j++) {
            if(keyFieldIsIndexed[j]) {
                int fieldBucket = HashCodes.hashInt(HollowReadFieldUtils.fieldHashCode(keyTypeState, ordinal, j)) & bucketMask;
                int chainStartIndex = hashedFieldKeys[j][fieldBucket];
                while(chainStartIndex != -1) {
                    int representativeOrdinal = (int)hashedFieldKeyChains.get(chainStartIndex);
                    if(HollowReadFieldUtils.fieldsAreEqual(keyTypeState, ordinal, j, keyTypeState, representativeOrdinal, j)) {
                        hashedFieldKeyChains.add(((long)chainStartIndex << 32) | ordinal);
                        hashedFieldKeys[j][fieldBucket] = hashedFieldKeyChains.size() - 1;
                        break indexFields;
                    }
                    fieldBucket = (fieldBucket + 1) & bucketMask;
                    chainStartIndex = hashedFieldKeys[j][fieldBucket];
                }

                hashedFieldKeyChains.add(((long)Integer.MAX_VALUE << 32) | ordinal);
                hashedFieldKeys[j][fieldBucket] = hashedFieldKeyChains.size() - 1;
            }
        }
    }

    private int[] initializeHashedKeyArray(int hashTableSize) {
        int hashedRecordKeys[] = new int[hashTableSize];
        Arrays.fill(hashedRecordKeys, -1);
        return hashedRecordKeys;
    }

    private int hashKeyRecord(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for(int i=0;i<keyFields.length;i++) {
            int fieldHashCode = HollowReadFieldUtils.fieldHashCode(typeState, ordinal, i);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    public int findKeyIndexOrdinal(HollowObjectTypeReadState typeState, int ordinal) {
        HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);

        int bucketMask = hashedRecordKeys.length - 1;

        int bucket = findKeyHashCode(typeState, ordinal) & bucketMask;

        while(hashedRecordKeys[bucket] != -1) {
            if(recordMatchesKey(typeState, ordinal, keyTypeState, hashedRecordKeys[bucket]))
                return hashedRecordKeys[bucket];

            bucket++;
            bucket &= bucketMask;
        }

        return -1;
    }

    private int findKeyHashCode(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for(int i=0;i<keyFieldParts.length;i++) {
            int fieldHashCode = findKeyFieldHashCode(typeState, ordinal, keyFieldParts[i], 0);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    public IntList queryIndexedFields(final String query) {
        final HollowObjectTypeReadState keyTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);
        IntList matchingKeys = new IntList();

        for(int i=0;i<keyFields.length;i++) {
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
        hashCode = HashCodes.hashInt(hashCode);
        int bucket = hashCode & (hashedFieldKeys[fieldIndex].length - 1);

        while(hashedFieldKeys[fieldIndex][bucket] != -1) {
            int chainIndex = hashedFieldKeys[fieldIndex][bucket];
            int representativeOrdinal = (int)hashedFieldKeyChains.get(chainIndex);
            if(matcher.foundMatch(representativeOrdinal)) {
                while(representativeOrdinal != -1) {
                    results.add(representativeOrdinal);
                    chainIndex = (int)(hashedFieldKeyChains.get(chainIndex) >> 32);
                    representativeOrdinal = (chainIndex == Integer.MAX_VALUE) ? -1 : (int)hashedFieldKeyChains.get(chainIndex);
                }
                return;
            }

            bucket++;
            bucket &= hashedFieldKeys[fieldIndex].length - 1;
        }
    }

    private static interface Matcher {
        public boolean foundMatch(int ordinal);
    }

    private int findKeyFieldHashCode(HollowObjectTypeReadState typeState, int ordinal, String keyFieldParts[], int keyFieldPartPosition) {
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

    private boolean recordFieldMatchesKey(HollowObjectTypeReadState typeState, int ordinal, HollowObjectTypeReadState keyTypeState, int keyOrdinal, int keyFieldPosition, String keyFieldParts[], int keyFieldPartPosition) {
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
        HollowTypeWriteState typeState = writeStateEngine.getTypeState(type);
        typeState.addAllObjectsFromPreviousCycle();
    }

    private void populateAllCurrentRecordKeys(HollowObjectTypeReadState typeState) {
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

    private void populateNewCurrentRecordKeys(HollowObjectTypeReadState typeState) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(keySchema);
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();
        BitSet previousOrdinals = listener.getPreviousOrdinals();

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(populatedOrdinals, previousOrdinals);
        int ordinal = iter.next();
        while(ordinal != -1) {
            writeKeyObject(typeState, ordinal, rec);
            ordinal = iter.next();
        }
    }
    
    public String[] getKeyFields() {
        return keyFields;
    }
    
    public Object getKeyFieldValue(int keyFieldIdx, int keyOrdinal) {
        return HollowReadFieldUtils.fieldValueObject((HollowObjectTypeReadState)readStateEngine.getTypeState(type), keyOrdinal, keyFieldIdx);
    }

    public String getKeyDisplayString(int keyOrdinal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<keyFields.length;i++) {
            builder.append(HollowReadFieldUtils.displayString(typeState, keyOrdinal, i));
            if(i < keyFields.length - 1)
                builder.append(':');
        }
        return builder.toString();
    }

    private void writeKeyObject(HollowObjectTypeReadState typeState, int ordinal, HollowObjectWriteRecord rec) {
        rec.reset();
        for(int i=0;i<keyFieldParts.length;i++) {
            writeKeyField(typeState, ordinal, rec, keyFields[i], keyFieldParts[i], 0);
        }
        writeStateEngine.add(type, rec);
    }

    private void writeKeyField(HollowObjectTypeReadState typeState, int ordinal, HollowObjectWriteRecord rec, String keyField, String keyFieldParts[], int keyFieldPartPosition) {
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
            keySchema = new HollowObjectSchema(type, keyFields.length);
            for(int i=0;i<keyFieldParts.length;i++)
                addSchemaField(entireObjectSchema, keySchema, keyFields[i], keyFieldParts[i], 0);
        }
    }

    private void addSchemaField(HollowObjectSchema schema, HollowObjectSchema keySchema, String keyField, String keyFieldParts[], int keyFieldPartPosition) {
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

    private String[][] getKeyFieldParts() {
        String keyFieldParts[][] = new String[keyFields.length][];
        for(int i=0;i<keyFields.length;i++)
            keyFieldParts[i] = keyFields[i].split("\\.");
        return keyFieldParts;
    }

}
