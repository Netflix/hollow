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
package com.netflix.hollow.core.read.dataaccess.missing;

import com.netflix.hollow.api.sampling.HollowObjectSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

/**
 * Used when an entire OBJECT type, which is expected by a Generated Hollow API, is missing from the actual data.  
 */
public class HollowObjectMissingDataAccess implements HollowObjectTypeDataAccess {

    private final HollowDataAccess dataAccess;
    private final String typeName;

    public HollowObjectMissingDataAccess(HollowDataAccess dataAccess, String typeName) {
        this.dataAccess = dataAccess;
        this.typeName = typeName;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema) missingDataHandler().handleSchema(typeName);
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        return missingDataHandler().handleIsNull(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        return missingDataHandler().handleReferencedOrdinal(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        return missingDataHandler().handleInt(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        return missingDataHandler().handleFloat(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        return missingDataHandler().handleDouble(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        return missingDataHandler().handleLong(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        return missingDataHandler().handleBoolean(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        return missingDataHandler().handleBytes(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        return missingDataHandler().handleString(typeName, ordinal, fieldName(fieldIndex));
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        return missingDataHandler().handleStringEquals(typeName, ordinal, fieldName(fieldIndex), testValue);
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        HollowObjectSchema schema = getSchema();
        if(schema.getFieldType(fieldIndex) == FieldType.STRING) {
            return HashCodes.hashCode(missingDataHandler().handleString(typeName, ordinal, schema.getFieldName(fieldIndex)));
        } else {
            return HashCodes.hashCode(missingDataHandler().handleBytes(typeName, ordinal, schema.getFieldName(fieldIndex)));
        }
    }

    private String fieldName(int fieldIndex) {
        return getSchema().getFieldName(fieldIndex);
    }

    private MissingDataHandler missingDataHandler() {
        return dataAccess.getMissingDataHandler();
    }

    @Override
    public HollowTypeReadState getTypeState() {
        throw new UnsupportedOperationException("No HollowTypeReadState exists for " + typeName);
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
    }

    @Override
    public HollowSampler getSampler() {
        return HollowObjectSampler.NULL_SAMPLER;
    }

}
