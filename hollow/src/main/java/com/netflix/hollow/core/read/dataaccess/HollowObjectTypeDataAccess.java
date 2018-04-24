/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.core.read.dataaccess;

import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

/**
 * A handle for all of the records of a specific OBJECT type in a Hollow dataset.  The most common type of {@link HollowObjectTypeDataAccess}
 * is a {@link HollowObjectTypeReadState}.
 * 
 * @see HollowObjectSchema
 */
public interface HollowObjectTypeDataAccess extends HollowTypeDataAccess {

    public HollowObjectSchema getSchema();

    /**
     * @return whether or not the record with the specified ordinal's field at the specified field index is null. 
     */
    public boolean isNull(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#REFERENCE} field's value at the specified fieldIndex for the specified ordinal. 
     */
    public int readOrdinal(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#INT} field's value at the specified fieldIndex for the specified ordinal.
     */
    public int readInt(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#FLOAT} field's value at the specified fieldIndex for the specified ordinal.
     */
    public float readFloat(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#DOUBLE} field's value at the specified fieldIndex for the specified ordinal.
     */
    public double readDouble(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#LONG} field's value at the specified fieldIndex for the specified ordinal.
     */
    public long readLong(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#BOOLEAN} field's value at the specified fieldIndex for the specified ordinal.
     */
    public Boolean readBoolean(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#BYTES} field's value at the specified fieldIndex for the specified ordinal.
     */
    public byte[] readBytes(int ordinal, int fieldIndex);

    /**
     * @return the {@link FieldType#STRING} field's value at the specified fieldIndex for the specified ordinal.
     */
    public String readString(int ordinal, int fieldIndex);

    /**
     * @return whether or not the {@link FieldType#STRING} field's value at the specified fieldIndex for the specified ordinal is exactly equal to the given value.
     */
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue);

    /**
     * @return a hashCode for the {@link FieldType#BYTES} or {@link FieldType#STRING} field's value at the specified fieldIndex for the specified ordinal.
     */
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex);

}
