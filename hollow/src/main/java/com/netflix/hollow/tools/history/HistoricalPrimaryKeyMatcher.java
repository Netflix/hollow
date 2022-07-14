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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Arrays;

public class HistoricalPrimaryKeyMatcher {

    private final HollowObjectTypeDataAccess keyTypeAccess;
    private final int fieldPathIndexes[][];
    private final FieldType[] fieldTypes;

    public HistoricalPrimaryKeyMatcher(HollowDataAccess dataAccess, PrimaryKey primaryKey) {
        this.fieldPathIndexes = new int[primaryKey.numFields()][];
        this.fieldTypes = new FieldType[primaryKey.numFields()];

        for(int i = 0; i < primaryKey.numFields(); i++) {
            fieldPathIndexes[i] = primaryKey.getFieldPathIndex(dataAccess, i);
            fieldTypes[i] = primaryKey.getFieldType(dataAccess, i);
        }

        this.keyTypeAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(primaryKey.getType());
    }

    public boolean keyMatches(int ordinal, Object... keys) {
        if(keys.length != fieldPathIndexes.length)
            return false;

        for(int i = 0; i < keys.length; i++) {
            if(!keyMatches(keys[i], ordinal, i))
                return false;
        }

        return true;
    }

    public boolean keyMatches(Object key, int ordinal, int fieldIdx) {
        HollowObjectTypeDataAccess dataAccess = keyTypeAccess;
        HollowObjectSchema schema = dataAccess.getSchema();

        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for(int i = 0; i < lastFieldPath; i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ordinal = dataAccess.readOrdinal(ordinal, fieldPosition);
            dataAccess = (HollowObjectTypeDataAccess) dataAccess.getDataAccess().getTypeDataAccess(schema.getReferencedType(fieldPosition), ordinal);
            schema = dataAccess.getSchema();
        }

        int lastFieldIdx = fieldPathIndexes[fieldIdx][lastFieldPath];

        switch(fieldTypes[fieldIdx]) {
            case BOOLEAN:
                Boolean b = dataAccess.readBoolean(ordinal, lastFieldIdx);
                if(b == key)
                    return true;
                if(b == null || key == null)
                    return false;
                return b.booleanValue() == ((Boolean) key).booleanValue();
            case BYTES:
                return Arrays.equals(dataAccess.readBytes(ordinal, lastFieldIdx), (byte[]) key);
            case DOUBLE:
                return dataAccess.readDouble(ordinal, lastFieldIdx) == ((Double) key).doubleValue();
            case FLOAT:
                return dataAccess.readFloat(ordinal, lastFieldIdx) == ((Float) key).floatValue();
            case INT:
                return dataAccess.readInt(ordinal, lastFieldIdx) == ((Integer) key).intValue();
            case LONG:
                return dataAccess.readLong(ordinal, lastFieldIdx) == ((Long) key).longValue();
            case REFERENCE:
                return dataAccess.readOrdinal(ordinal, lastFieldIdx) == ((Integer) key).intValue();
            case STRING:
                return dataAccess.isStringFieldEqual(ordinal, lastFieldIdx, (String) key);
        }

        throw new IllegalArgumentException("I don't know how to compare a " + fieldTypes[fieldIdx]);

    }

    public FieldType[] getFieldTypes() {
        return fieldTypes;
    }

}
