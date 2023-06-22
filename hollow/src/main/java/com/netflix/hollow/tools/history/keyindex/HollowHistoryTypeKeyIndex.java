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

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class HollowHistoryTypeKeyIndex {
    private final PrimaryKey primaryKey;
    private final FieldType[] fieldTypes;

    private final String[][] keyFieldNames;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;

    private boolean isInitialized = false;
    private int maxIndexedOrdinal = 0;

    private final HollowOrdinalMapper ordinalMapping;
    private final HashMap<Integer, IntList> ordinalFieldHashMapping;


    public HollowHistoryTypeKeyIndex(PrimaryKey primaryKey, HollowDataset dataModel) {
        this.primaryKey = primaryKey;
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        this.keyFieldNames = new String[primaryKey.numFields()][];
        this.keyFieldIndices = new int[primaryKey.numFields()][];
        this.keyFieldIsIndexed = new boolean[primaryKey.numFields()];
        initializeKeyParts(dataModel);

        this.ordinalMapping = new HollowOrdinalMapper(primaryKey, keyFieldIsIndexed, keyFieldIndices);
        this.ordinalFieldHashMapping = new HashMap<>();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int findKeyIndexOrdinal(HollowObjectTypeReadState typeState, int ordinal) {
        return ordinalMapping.findAssignedOrdinal(typeState, ordinal);
    }

    public void initializeKeySchema(HollowObjectTypeReadState initialTypeState) {
        if (isInitialized) return;
        HollowObjectSchema schema = initialTypeState.getSchema();

        for (String[] keyFieldPart : keyFieldNames) addSchemaField(schema, keyFieldPart, 0);
        isInitialized = true;
    }

    private void addSchemaField(HollowObjectSchema schema, String[] keyFieldNames, int keyFieldPartPosition) {
        int schemaPosition = schema.getPosition(keyFieldNames[keyFieldPartPosition]);
        if (keyFieldPartPosition < keyFieldNames.length - 1) {
            HollowObjectSchema nextPartSchema = (HollowObjectSchema) schema.getReferencedTypeState(schemaPosition).getSchema();
            addSchemaField(nextPartSchema, keyFieldNames, keyFieldPartPosition + 1);
        } else {
            fieldTypes[keyFieldPartPosition] = schema.getFieldType(schemaPosition);
        }
    }

    private void initializeKeyParts(HollowDataset dataModel) {
        for (int i = 0; i < primaryKey.numFields(); i++) {
            keyFieldNames[i] = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
            keyFieldIndices[i] = PrimaryKey.getFieldPathIndex(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
        }
    }

    public void addFieldIndex(String fieldName, HollowDataset dataModel) {
        String[] fieldPathParts = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), fieldName);
        for (int i = 0; i < primaryKey.numFields(); i++) {
            String[] pkFieldPathParts = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
            if (Arrays.equals(pkFieldPathParts, fieldPathParts)) {
                keyFieldIsIndexed[i] = true;
                break;
            }
        }
    }

    public void update(HollowObjectTypeReadState latestTypeState, boolean isDeltaAndIndexInitialized) {
        if (latestTypeState == null) return;

        if (isDeltaAndIndexInitialized) {
            populateNewCurrentRecordKeysIntoIndex(latestTypeState);
        } else {
            maxIndexedOrdinal = 0;
            populateAllCurrentRecordKeysIntoIndex(latestTypeState);
        }
    }

    public int getMaxIndexedOrdinal() {
        return maxIndexedOrdinal;
    }

    public String[] getKeyFields() {
        return primaryKey.getFieldPaths();
    }

    private void populateNewCurrentRecordKeysIntoIndex(HollowObjectTypeReadState typeState) {
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();
        BitSet previousOrdinals = listener.getPreviousOrdinals();

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(populatedOrdinals, previousOrdinals);
        int ordinal = iter.next();
        while (ordinal != ORDINAL_NONE) {
            writeKeyObject(typeState, ordinal, true);
            ordinal = iter.next();
        }
    }

    private void populateAllCurrentRecordKeysIntoIndex(HollowObjectTypeReadState typeState) {
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet previousOrdinals = listener.getPreviousOrdinals();
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();

        final int maxLength = Math.max(previousOrdinals.length(), populatedOrdinals.length());

        for (int i = 0; i < maxLength; i++) {
            if (populatedOrdinals.get(i) || previousOrdinals.get(i))
                writeKeyObject(typeState, i, false);
        }
    }

    private void writeKeyObject(HollowObjectTypeReadState typeState, int ordinal, boolean isDelta) {
        int assignedOrdinal = isDelta ? maxIndexedOrdinal : ordinal;
        maxIndexedOrdinal+=1;
        int assignedIndex = ordinalMapping.storeNewRecord(typeState, ordinal, assignedOrdinal);

        if(assignedIndex==ORDINAL_NONE)
            return;

        for (int i = 0; i < primaryKey.numFields(); i++) {
            if (!keyFieldIsIndexed[i])
                continue;

            Object fieldObject = ordinalMapping.getFieldObject(assignedOrdinal, i);
            int fieldHash = HashCodes.hashInt(hashObject(fieldObject));
            if(!ordinalFieldHashMapping.containsKey(fieldHash))
                ordinalFieldHashMapping.put(fieldHash, new IntList());

            IntList matchingFieldList = ordinalFieldHashMapping.get(fieldHash);
            matchingFieldList.add(assignedOrdinal);
        }
    }

    private int hashObject(Object value) {
        if(value instanceof Integer) {
            return HollowReadFieldUtils.intHashCode((Integer)value);
        } else if(value instanceof String) {
            return HollowReadFieldUtils.stringHashCode((String)value);
        } else if(value instanceof Float) {
            return HollowReadFieldUtils.floatHashCode((Float)value);
        } else if(value instanceof Double) {
            return HollowReadFieldUtils.doubleHashCode((Double)value);
        } else if(value instanceof Boolean) {
            return HollowReadFieldUtils.booleanHashCode((Boolean) value);
        } else if(value instanceof byte[]) {
            return HollowReadFieldUtils.byteArrayHashCode((byte[]) value);
        } else {
            throw new RuntimeException("Unable to hash field of type " + value.getClass().getName());
        }
    }

    public String getKeyDisplayString(int keyOrdinal) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < primaryKey.numFields(); i++) {
            Object valueAtField = ordinalMapping.getFieldObject(keyOrdinal, i);
            builder.append(valueAtField);
            if (i < primaryKey.numFields() - 1)
                builder.append(MULTI_FIELD_KEY_DELIMITER);
        }
        return builder.toString();
    }

    public IntList queryIndexedFields(final String query) {
        IntList matchingKeys = new IntList();

        if (!isInitialized) {
            return matchingKeys;
        }

        for (int i = 0; i < primaryKey.numFields(); i++) {
            int hashCode = 0;
            try {
                switch (fieldTypes[i]) {
                    case INT:
                        final int queryInt = Integer.parseInt(query);
                        hashCode = HollowReadFieldUtils.intHashCode(queryInt);
                        break;
                    case LONG:
                        final long queryLong = Long.parseLong(query);
                        hashCode = HollowReadFieldUtils.longHashCode(queryLong);
                        break;
                    case STRING:
                        hashCode = HashCodes.hashCode(query);
                        break;
                    case DOUBLE:
                        final double queryDouble = Double.parseDouble(query);
                        hashCode = HollowReadFieldUtils.doubleHashCode(queryDouble);
                        break;
                    case FLOAT:
                        final float queryFloat = Float.parseFloat(query);
                        hashCode = HollowReadFieldUtils.floatHashCode(queryFloat);
                        break;
                    default:
                }
                addMatches(HashCodes.hashInt(hashCode), matchingKeys);
            } catch(NumberFormatException ignore) {}
        }
        return matchingKeys;
    }

    public void addMatches(int hashCode, IntList results) {
        if (!ordinalFieldHashMapping.containsKey(hashCode))
            return;
        IntList res2 = ordinalFieldHashMapping.get(hashCode);
        results.addAll(res2);
    }

    //TODO: make a unit test
    public Object getKeyFieldValue(int keyFieldIdx, int keyOrdinal) {
        return ordinalMapping.getFieldObject(keyOrdinal, keyFieldIdx);
    }
}