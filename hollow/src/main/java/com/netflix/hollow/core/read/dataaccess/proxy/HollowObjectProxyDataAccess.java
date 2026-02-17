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
package com.netflix.hollow.core.read.dataaccess.proxy;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * A {@link HollowTypeProxyDataAccess} for an OBJECT type.
 * 
 * @see HollowProxyDataAccess
 */
public class HollowObjectProxyDataAccess extends HollowTypeProxyDataAccess<HollowObjectTypeDataAccess> implements HollowObjectTypeDataAccess {

    HollowObjectProxyDataAccess(HollowProxyDataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return currentDataAccess.getSchema();
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        return currentDataAccess.isNull(ordinal, fieldIndex);
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        return currentDataAccess.readOrdinal(ordinal, fieldIndex);
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        return currentDataAccess.readInt(ordinal, fieldIndex);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        return currentDataAccess.readFloat(ordinal, fieldIndex);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        return currentDataAccess.readDouble(ordinal, fieldIndex);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        return currentDataAccess.readLong(ordinal, fieldIndex);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        return currentDataAccess.readBoolean(ordinal, fieldIndex);
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        return currentDataAccess.readBytes(ordinal, fieldIndex);
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        return currentDataAccess.readString(ordinal, fieldIndex);
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        return currentDataAccess.isStringFieldEqual(ordinal, fieldIndex, testValue);
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        return currentDataAccess.findVarLengthFieldHashCode(ordinal, fieldIndex);
    }
}
