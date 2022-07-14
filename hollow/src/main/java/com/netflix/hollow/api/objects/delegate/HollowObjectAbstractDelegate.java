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
package com.netflix.hollow.api.objects.delegate;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.missing.MissingDataHandler;

/**
 * Contains some basic convenience access methods for OBJECT record fields.
 * 
 * @see HollowRecordDelegate
 */
public abstract class HollowObjectAbstractDelegate implements HollowObjectDelegate {

    @Override
    public boolean isNull(int ordinal, String fieldName) {
        try {
            HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
            int fieldIndex = getSchema().getPosition(fieldName);

            if(fieldIndex == -1)
                return missingDataHandler().handleIsNull(getSchema().getName(), ordinal, fieldName);

            return dataAccess.isNull(ordinal, fieldIndex);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Unable to handle ordinal=%s, fieldName=%s", ordinal, fieldName), ex);
        }
    }

    @Override
    public boolean getBoolean(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        Boolean bool = (fieldIndex != -1) ?
                dataAccess.readBoolean(ordinal, fieldIndex)
                : missingDataHandler().handleBoolean(getSchema().getName(), ordinal, fieldName);

        return bool == null ? false : bool.booleanValue();
    }

    @Override
    public int getOrdinal(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleReferencedOrdinal(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readOrdinal(ordinal, fieldIndex);
    }

    @Override
    public int getInt(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleInt(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readInt(ordinal, fieldIndex);
    }

    @Override
    public long getLong(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleLong(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readLong(ordinal, fieldIndex);
    }

    @Override
    public float getFloat(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleFloat(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readFloat(ordinal, fieldIndex);
    }

    @Override
    public double getDouble(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleDouble(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readDouble(ordinal, fieldIndex);
    }

    @Override
    public String getString(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1)
            return missingDataHandler().handleString(getSchema().getName(), ordinal, fieldName);

        return dataAccess.readString(ordinal, fieldIndex);
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, String fieldName, String testValue) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1) {
            return missingDataHandler().handleStringEquals(getSchema().getName(), ordinal, fieldName, testValue);
        }

        return dataAccess.isStringFieldEqual(ordinal, fieldIndex, testValue);
    }

    @Override
    public byte[] getBytes(int ordinal, String fieldName) {
        HollowObjectTypeDataAccess dataAccess = getTypeDataAccess();
        int fieldIndex = getSchema().getPosition(fieldName);

        if(fieldIndex == -1) {
            return missingDataHandler().handleBytes(getSchema().getName(), ordinal, fieldName);
        }

        return dataAccess.readBytes(ordinal, fieldIndex);
    }

    private MissingDataHandler missingDataHandler() {
        return getTypeDataAccess().getDataAccess().getMissingDataHandler();
    }
}
