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

import com.netflix.hollow.core.index.HollowHashIndexField.FieldPathElement;
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
    private final HollowHashIndexField[] fields;



    public HollowPrimaryKeyValueDeriver2(HollowHashIndexField[] fields) {
        this.fields = fields;
    }

    /**
     * @param ordinal  ordinal of root object
     * @param field    field to traverse
     * @return the ordinal of the second-to-last element. This ordinal can be used with the last path element
     * to retrieve the final ordinal
     */
    private int getOrdinalForFieldPath(HollowHashIndexField field, int ordinal) {
        FieldPathElement[] pathElements = field.getSchemaFieldPositionPath();
        for (int posIdx = 0; posIdx < pathElements.length - 1; posIdx++) {
            FieldPathElement fieldPathElement = pathElements[posIdx];
            ordinal = fieldPathElement.getOrdinalForField(ordinal);
        }
        return ordinal;
    }

    /**
     * Determine whether the specified ordinal contains the provided primary key value.
     *
     * @param ordinal the ordinal
     * @param keys    the primary keys
     * @return true if the ordinal contains the primary keys
     */
    public boolean keyMatches(int ordinal, Object... keys) {
        if (keys.length != fields.length)
            return false;

        for (int i = 0; i < keys.length; i++) {
            if (!keyMatches(keys[i], ordinal, i))
                return false;
        }

        return true;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public boolean keyMatches(Object key, int recordOrdinal, int fieldIdx) {
        HollowHashIndexField field = fields[fieldIdx];

        //ordinal of the last element of the path, starting from the recordOrdinal.
        int lastElementOrdinal = getOrdinalForFieldPath(field, recordOrdinal);

        FieldPathElement lastPathElement = field.getLastFieldPositionPathElement();
        int lastPathPosition = lastPathElement.getFieldPosition();
        HollowObjectTypeDataAccess typeDataAccess = lastPathElement.getObjectTypeDataAccess();

        switch (field.getFieldType()) {
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

        throw new IllegalArgumentException("I don't know how to compare a " + field.getFieldType());
    }

    /**
     * Retrieve the primary key value for the specified ordinal.
     *
     * @param ordinal the ordinal
     * @return the primary keys
     */
    public Object[] getRecordKey(int ordinal) {
        Object[] results = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            HollowHashIndexField field = fields[i];
            int lastPathOrdinal = getOrdinalForFieldPath(field, ordinal);
            FieldPathElement lastElement = field.getLastFieldPositionPathElement();
            results[i] = HollowReadFieldUtils.fieldValueObject(lastElement.getObjectTypeDataAccess(), lastPathOrdinal, lastElement.getFieldPosition());
        }
        return results;
    }

}
