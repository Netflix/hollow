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

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex.FieldPathElement;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import java.util.Arrays;

/**
 * Used to retrieve and test equality of PrimaryKey values for records.
 * <p>
 * This value deriver should respect object longevity.
 */
class HollowPrimaryKeyValueDeriver2 {

    /**
     * Path for each field that is indexed.
     */
    private final FieldPathElement[][] fieldPaths;

    /**
     * The type of the final element of each field path.
     */
    private final FieldType[] fieldTypes;


    public HollowPrimaryKeyValueDeriver2(FieldPathElement[][] fieldPaths, FieldType[] fieldTypes) {
        this.fieldPaths = fieldPaths;
        this.fieldTypes = fieldTypes;
    }

    /**
     * @param ordinal  ordinal of root object
     * @param fieldIdx field index to traverse
     * @return the ordinal of the second-to-last element. This ordinal can be used with {@link #getLastPathElement(int)}
     * to retrieve the final ordinal
     */
    private int getOrdinalForFieldPath(int ordinal, int fieldIdx) {
        FieldPathElement[] fieldPath = fieldPaths[fieldIdx];
        for (int posIdx = 0; posIdx < fieldPath.length - 1; posIdx++) {
            FieldPathElement fieldPathElement = fieldPath[posIdx];
            ordinal = fieldPathElement.getOrdinalForField(ordinal);
        }
        return ordinal;
    }

    private FieldPathElement getLastPathElement(int fieldIdx) {
        FieldPathElement[] path = fieldPaths[fieldIdx];
        return path[path.length - 1];
    }

    /**
     * Determine whether the specified ordinal contains the provided primary key value.
     *
     * @param ordinal the ordinal
     * @param keys    the primary keys
     * @return true if the ordinal contains the primary keys
     */
    public boolean keyMatches(int ordinal, Object... keys) {
        if (keys.length != fieldPaths.length)
            return false;

        for (int i = 0; i < keys.length; i++) {
            if (!keyMatches(keys[i], ordinal, i))
                return false;
        }

        return true;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public boolean keyMatches(Object key, int recordOrdinal, int fieldIdx) {
        //ordinal of the last element of the path, starting from the recordOrdinal.
        int lastElementOrdinal = getOrdinalForFieldPath(recordOrdinal, fieldIdx);

        FieldPathElement lastPathElement = getLastPathElement(fieldIdx);
        int lastPathPosition = lastPathElement.getFieldPosition();
        HollowObjectTypeDataAccess typeDataAccess = lastPathElement.getObjectTypeDataAccess();

        switch (fieldTypes[fieldIdx]) {
            case BOOLEAN:
                Boolean b = typeDataAccess.readBoolean(lastElementOrdinal, lastPathPosition);
                if (b == key)
                    return true;
                if (b == null)
                    return false;
                return b.booleanValue() == ((Boolean) key).booleanValue();
            case BYTES:
                return Arrays.equals(typeDataAccess.readBytes(lastElementOrdinal, lastPathPosition), (byte[]) key);
            case DOUBLE:
                return typeDataAccess.readDouble(lastElementOrdinal, lastPathPosition) == ((Double) key).doubleValue();
            case FLOAT:
                return typeDataAccess.readFloat(lastElementOrdinal, lastPathPosition) == ((Float) key).floatValue();
            case INT:
                return typeDataAccess.readInt(lastElementOrdinal, lastPathPosition) == ((Integer) key).intValue();
            case LONG:
                return typeDataAccess.readLong(lastElementOrdinal, lastPathPosition) == ((Long) key).longValue();
            case REFERENCE:
                return typeDataAccess.readOrdinal(lastElementOrdinal, lastPathPosition) == ((Integer) key).intValue();
            case STRING:
                return typeDataAccess.isStringFieldEqual(lastElementOrdinal, lastPathPosition, (String) key);
        }

        throw new IllegalArgumentException("I don't know how to compare a " + fieldTypes[fieldIdx]);
    }

    /**
     * Retrieve the primary key value for the specified ordinal.
     *
     * @param ordinal the ordinal
     * @return the primary keys
     */
    public Object[] getRecordKey(int ordinal) {
        Object[] results = new Object[fieldPaths.length];

        for (int i = 0; i < fieldPaths.length; i++) {
            results[i] = readValue(ordinal, i);
        }
        return results;
    }

    private Object readValue(int ordinal, int fieldIdx) {
        ordinal = getOrdinalForFieldPath(ordinal, fieldIdx);
        FieldPathElement lastPathElement = getLastPathElement(fieldIdx);
        return HollowReadFieldUtils.fieldValueObject(lastPathElement.getObjectTypeDataAccess(), ordinal, lastPathElement.getFieldPosition());
    }

}
