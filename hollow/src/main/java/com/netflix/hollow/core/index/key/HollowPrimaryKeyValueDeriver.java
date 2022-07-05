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
package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Arrays;

/**
 * Used to retrieve and test equality of PrimaryKey values for records.
 */
public class HollowPrimaryKeyValueDeriver {

    private final HollowObjectTypeDataAccess typeDataAccess;
    private final int[][] fieldPathIndexes;
    private final FieldType[] fieldTypes;

    /**
     * Create a new deriver.
     *
     * @param primaryKey    The primary key spec
     * @param hollowDataset The state engine to retrieve data from
     */
    public HollowPrimaryKeyValueDeriver(PrimaryKey primaryKey, HollowDataAccess hollowDataset) {
        this.fieldPathIndexes = new int[primaryKey.numFields()][];
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        for (int i = 0; i < primaryKey.numFields(); i++) {
            fieldPathIndexes[i] = primaryKey.getFieldPathIndex(hollowDataset, i);
            fieldTypes[i] = primaryKey.getFieldType(hollowDataset, i);
        }

        this.typeDataAccess = (HollowObjectTypeReadState) hollowDataset.getTypeDataAccess(primaryKey.getType());
    }

    public HollowPrimaryKeyValueDeriver(HollowObjectTypeDataAccess typeDataAccess, int[][] fieldPathIndexes,  FieldType[] fieldTypes) {
        this.typeDataAccess = typeDataAccess;
        this.fieldPathIndexes = fieldPathIndexes;
        this.fieldTypes = fieldTypes;
    }

    /**
     * Determine whether or not the specified ordinal contains the provided primary key value.
     *
     * @param ordinal the oridinal
     * @param keys    the primary keys
     * @return true if the ordinal contains the primary keys
     */
    public boolean keyMatches(int ordinal, Object... keys) {
        if (keys.length != fieldPathIndexes.length)
            return false;

        for (int i = 0; i < keys.length; i++) {
            if (!keyMatches(keys[i], ordinal, i))
                return false;
        }

        return true;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public boolean keyMatches(Object key, int ordinal, int fieldIdx) {
        HollowObjectTypeDataAccess typeDataAccess = this.typeDataAccess;
        HollowObjectSchema schema = typeDataAccess.getSchema();

        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for (int i = 0; i < lastFieldPath; i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ordinal = typeDataAccess.readOrdinal(ordinal, fieldPosition);
            typeDataAccess = (HollowObjectTypeReadState) schema.getReferencedTypeState(fieldPosition);
            schema = typeDataAccess.getSchema();
        }

        int lastFieldIdx = fieldPathIndexes[fieldIdx][lastFieldPath];

        switch (fieldTypes[fieldIdx]) {
            case BOOLEAN:
                Boolean b = typeDataAccess.readBoolean(ordinal, lastFieldIdx);
                if (b == key)
                    return true;
                if (b == null)
                    return false;
                return b.booleanValue() == ((Boolean) key).booleanValue();
            case BYTES:
                return Arrays.equals(typeDataAccess.readBytes(ordinal, lastFieldIdx), (byte[]) key);
            case DOUBLE:
                return typeDataAccess.readDouble(ordinal, lastFieldIdx) == ((Double) key).doubleValue();
            case FLOAT:
                return typeDataAccess.readFloat(ordinal, lastFieldIdx) == ((Float) key).floatValue();
            case INT:
                return typeDataAccess.readInt(ordinal, lastFieldIdx) == ((Integer) key).intValue();
            case LONG:
                return typeDataAccess.readLong(ordinal, lastFieldIdx) == ((Long) key).longValue();
            case REFERENCE:
                return typeDataAccess.readOrdinal(ordinal, lastFieldIdx) == ((Integer) key).intValue();
            case STRING:
                return typeDataAccess.isStringFieldEqual(ordinal, lastFieldIdx, (String) key);
        }

        throw new IllegalArgumentException("I don't know how to compare a " + fieldTypes[fieldIdx]);
    }

    /**
     * Retrieve the primary key value for the specified ordinal.
     *
     * @param ordinal the oridinal
     * @return the primary keys
     */
    public Object[] getRecordKey(int ordinal) {
        Object[] results = new Object[fieldPathIndexes.length];

        for (int i = 0; i < fieldPathIndexes.length; i++) {
            results[i] = readValue(ordinal, i);
        }
        return results;
    }

    private Object readValue(int ordinal, int fieldIdx) {
        HollowObjectTypeDataAccess typeState = this.typeDataAccess;
        HollowObjectSchema schema = typeState.getSchema();

        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for (int i = 0; i < lastFieldPath; i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ordinal = typeState.readOrdinal(ordinal, fieldPosition);
            String refTypeName = schema.getReferencedType(fieldPosition);
            typeState = (HollowObjectTypeDataAccess) typeState.getDataAccess().getTypeDataAccess(refTypeName);
            schema = typeState.getSchema();
        }

        return HollowReadFieldUtils.fieldValueObject(typeState, ordinal, fieldPathIndexes[fieldIdx][lastFieldPath]);
    }

    public int[][] getFieldPathIndexes() {
        return fieldPathIndexes;
    }

    public FieldType[] getFieldTypes() {
        return fieldTypes;
    }
}
