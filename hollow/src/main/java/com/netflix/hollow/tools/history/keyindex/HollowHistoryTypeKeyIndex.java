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
import static com.netflix.hollow.tools.util.SearchUtils.ESCAPED_MULTI_FIELD_KEY_DELIMITER;
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
import java.util.Objects;
import java.util.Set;

public class HollowHistoryTypeKeyIndex {
    private final PrimaryKey primaryKey;
    private final FieldType[] fieldTypes;

    private final String[][] keyFieldNames;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;

    private boolean isInitialized = false;
    private int maxIndexedOrdinal = 0;

    private final HollowOrdinalMapper ordinalMapping;


    public HollowHistoryTypeKeyIndex(PrimaryKey primaryKey, HollowDataset dataModel) {
        this.primaryKey = primaryKey;
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        this.keyFieldNames = new String[primaryKey.numFields()][];
        this.keyFieldIndices = new int[primaryKey.numFields()][];
        this.keyFieldIsIndexed = new boolean[primaryKey.numFields()];
        initializeKeyParts(dataModel);

        this.ordinalMapping = new HollowOrdinalMapper(primaryKey, keyFieldIsIndexed, keyFieldIndices, fieldTypes);
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
            writeKeyObject(typeState, ordinal);
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
                writeKeyObject(typeState, i);
        }
    }

    private void writeKeyObject(HollowObjectTypeReadState typeState, int ordinal) {
        int assignedOrdinal = maxIndexedOrdinal;
        boolean storedUniqueRecord = ordinalMapping.storeNewRecord(typeState, ordinal, assignedOrdinal);

        // Identical record already in memory, no need to store fields
        if(!storedUniqueRecord)
            return;
        maxIndexedOrdinal+=1;
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

    private void getMatchesForField(int fieldIdx, String strVal, IntList matchingKeys) throws NumberFormatException {
        int hashCode = 0;
        Object objectToFind = null;
        FieldType fieldType = fieldTypes[fieldIdx];
        switch (fieldType) {
            case INT:
                final int queryInt = Integer.parseInt(strVal);
                hashCode = HollowReadFieldUtils.intHashCode(queryInt);
                objectToFind = queryInt;
                break;
            case LONG:
                final long queryLong = Long.parseLong(strVal);
                hashCode = HollowReadFieldUtils.longHashCode(queryLong);
                objectToFind = queryLong;
                break;
            case STRING:
                hashCode = HashCodes.hashCode(strVal);
                objectToFind = strVal.replaceAll(ESCAPED_MULTI_FIELD_KEY_DELIMITER, MULTI_FIELD_KEY_DELIMITER);
                break;
            case DOUBLE:
                final double queryDouble = Double.parseDouble(strVal);
                hashCode = HollowReadFieldUtils.doubleHashCode(queryDouble);
                objectToFind = queryDouble;
                break;
            case FLOAT:
                final float queryFloat = Float.parseFloat(strVal);
                hashCode = HollowReadFieldUtils.floatHashCode(queryFloat);
                objectToFind = queryFloat;
                break;
            default:
        }
        ordinalMapping.addMatches(HashCodes.hashInt(hashCode), objectToFind, fieldIdx, fieldType, matchingKeys);
    }

    // find the exact matches for the given composite key.
    private IntList queryIndexedFieldsForCompositeKey(final String[] compositeKeyComponents) {
        IntList matchingKeys = new IntList();
        Set<Integer> resultSet = null;
        for (int i = 0; i < compositeKeyComponents.length; ++i) {
            String currComponent = compositeKeyComponents[i];
            try {
                getMatchesForField(i, currComponent, matchingKeys);
                Set<Integer> keySet = IntList.createSetFromIntList(matchingKeys);
                matchingKeys.clear();
                if (keySet.isEmpty()) {
                    // directly return as we'll not be able to find any exact matches for the given
                    // composite key.
                    return new IntList();
                }
                if (Objects.isNull(resultSet)) {
                    resultSet = keySet;
                }
                else {
                    resultSet.retainAll(keySet);
                }
            } catch (NumberFormatException ignore) {
                return new IntList();
            }
        }

        return IntList.createIntListFromSet(resultSet);
    }

    private IntList queryIndexedFieldsForNonCompositeKey(final String query) {
        IntList matchingKeys = new IntList();
        for (int i = 0; i < primaryKey.numFields(); i++) {
            try {
                getMatchesForField(i, query, matchingKeys);
            } catch(NumberFormatException ignore) {}
        }
        return matchingKeys;
    }

    public IntList queryIndexedFields(final String query) {
        if (!isInitialized) {
            return new IntList();
        }

        String[] keyComponents = query.split("(?<!\\\\)" + MULTI_FIELD_KEY_DELIMITER, primaryKey.numFields());
        if (keyComponents.length > 1 && keyComponents.length == primaryKey.numFields()) {
            return queryIndexedFieldsForCompositeKey(keyComponents);
        } else {
            return queryIndexedFieldsForNonCompositeKey(query);
        }
    }

    public Object getKeyFieldValue(int keyFieldIdx, int keyOrdinal) {
        return ordinalMapping.getFieldObject(keyOrdinal, keyFieldIdx, fieldTypes[keyFieldIdx]);
    }
}