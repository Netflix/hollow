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
import java.util.Optional;

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

        this.ordinalMapping = new HollowOrdinalMapper(primaryKey, keyFieldIsIndexed, keyFieldIndices, fieldTypes);
        this.ordinalFieldHashMapping = new HashMap<>();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int findKeyIndexOrdinal(HollowObjectTypeReadState typeState, int ordinal) {
        return ordinalMapping.findAssignedOrdinal(typeState, ordinal);
    }

    public int getMaxIndexedOrdinal() {
        return maxIndexedOrdinal;
    }

    public String[] getKeyFields() {
        return primaryKey.getFieldPaths();
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

    public void initializeKeySchema(HollowObjectTypeReadState initialTypeState) {
        if (isInitialized) return;
        HollowObjectSchema schema = initialTypeState.getSchema();

        for (int i= 0; i < keyFieldNames.length; i ++) {
            String[] keyFieldPart = keyFieldNames[i];
            fieldTypes[i] = addSchemaField(schema, keyFieldPart, 0);
        }
        isInitialized = true;
    }

    private FieldType addSchemaField(HollowObjectSchema schema, String[] keyFieldNames, int keyFieldPartPosition) {
        int schemaPosition = schema.getPosition(keyFieldNames[keyFieldPartPosition]);
        if (keyFieldPartPosition < keyFieldNames.length - 1) {
            HollowObjectSchema nextPartSchema = (HollowObjectSchema) schema.getReferencedTypeState(schemaPosition).getSchema();
            return addSchemaField(nextPartSchema, keyFieldNames, keyFieldPartPosition + 1);
        }
        return schema.getFieldType(schemaPosition);
    }

    public boolean[] getKeyFieldIsIndexed() {
        return keyFieldIsIndexed;
    }

    private void initializeKeyParts(HollowDataset dataModel) {
        for (int i = 0; i < primaryKey.numFields(); i++) {
            keyFieldNames[i] = PrimaryKey.getCompleteFieldPathParts(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
            keyFieldIndices[i] = PrimaryKey.getFieldPathIndex(dataModel, primaryKey.getType(), primaryKey.getFieldPath(i));
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
        ordinalMapping.prepareForRead();
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
        int assignedOrdinal = maxIndexedOrdinal;
        boolean storedUniqueRecord = ordinalMapping.storeNewRecord(typeState, ordinal, assignedOrdinal);

        // Identical record already in memory, no need to store fields
        if(!storedUniqueRecord)
            return;
        maxIndexedOrdinal+=1;
        Object[] fieldObjects = new Object[primaryKey.numFields()];
        for (int i = 0; i < primaryKey.numFields(); i++) {
            fieldObjects[i] = ordinalMapping.readValueInState(typeState, ordinal, i);
        }

        for (int i = 0; i < primaryKey.numFields(); i++)
            writeKeyField(fieldObjects, assignedOrdinal, i);
    }

    private void writeKeyField(Object[] fieldObjects, int assignedOrdinal, int fieldIdx) {
        if (!keyFieldIsIndexed[fieldIdx])
            return;

        Object fieldObject = fieldObjects[fieldIdx];
        int fieldHash = HashCodes.hashInt(HollowReadFieldUtils.hashObject(fieldObject));
        if(!ordinalFieldHashMapping.containsKey(fieldHash))
            ordinalFieldHashMapping.put(fieldHash, new IntList());

        IntList matchingFieldList = ordinalFieldHashMapping.get(fieldHash);
        matchingFieldList.add(assignedOrdinal);
    }

    public String getKeyDisplayString(int keyOrdinal) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < primaryKey.numFields(); i++) {
            Object valueAtField = ordinalMapping.getFieldObject(keyOrdinal, i, fieldTypes[i]);
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
            Object objectToFind = null;
            try {
                switch (fieldTypes[i]) {
                    case INT:
                        final int queryInt = Integer.parseInt(query);
                        hashCode = HollowReadFieldUtils.intHashCode(queryInt);
                        objectToFind = queryInt;
                        break;
                    case LONG:
                        final long queryLong = Long.parseLong(query);
                        hashCode = HollowReadFieldUtils.longHashCode(queryLong);
                        objectToFind = queryLong;
                        break;
                    case STRING:
                        hashCode = HashCodes.hashCode(query);
                        objectToFind = query;
                        break;
                    case DOUBLE:
                        final double queryDouble = Double.parseDouble(query);
                        hashCode = HollowReadFieldUtils.doubleHashCode(queryDouble);
                        objectToFind = queryDouble;
                        break;
                    case FLOAT:
                        final float queryFloat = Float.parseFloat(query);
                        hashCode = HollowReadFieldUtils.floatHashCode(queryFloat);
                        objectToFind = queryFloat;
                        break;
                    default:
                }
                addMatches(HashCodes.hashInt(hashCode), objectToFind, i, matchingKeys);
            } catch(NumberFormatException ignore) {}
        }
        return matchingKeys;
    }

    public void addMatches(int hashCode, Object objectToMatch, int field, IntList results) {
        if (!ordinalFieldHashMapping.containsKey(hashCode))
            return;

        IntList matchingOrdinals = ordinalFieldHashMapping.get(hashCode);
        for(int i=0;i<matchingOrdinals.size();i++) {
            int ordinal = matchingOrdinals.get(i);

            Object matchingObject = ordinalMapping.getFieldObject(ordinal, field, fieldTypes[field]);
            if(objectToMatch.equals(matchingObject)) {
                results.add(ordinal);
            }
        }
    }

    public Object getKeyFieldValue(int keyFieldIdx, int keyOrdinal) {
        return ordinalMapping.getFieldObject(keyOrdinal, keyFieldIdx, fieldTypes[keyFieldIdx]);
    }
}