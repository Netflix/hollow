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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * A schema for a Set record type.
 *
 * @see HollowSchema
 *
 * @author dkoszewnik
 *
 */
public class HollowSetSchema extends HollowCollectionSchema {

    private final String elementType;
    private final PrimaryKey hashKey;

    private HollowTypeReadState elementTypeState;

    public HollowSetSchema(String schemaName, String elementType, String... hashKeyFieldPaths) {
        super(schemaName);
        this.elementType = elementType;
        this.hashKey = hashKeyFieldPaths == null || hashKeyFieldPaths.length == 0 ? null : new PrimaryKey(elementType, hashKeyFieldPaths);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    public PrimaryKey getHashKey() {
        return hashKey;
    }

    public void setElementTypeState(HollowTypeReadState elementTypeState) {
        this.elementTypeState = elementTypeState;
    }

    @Override
    public HollowTypeReadState getElementTypeState() {
        return elementTypeState;
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.SET;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if(!(other instanceof HollowSetSchema))
            return false;
        HollowSetSchema otherSchema = (HollowSetSchema)other;
        if(!getName().equals(otherSchema.getName()))
            return false;
        if(!getElementType().equals(otherSchema.getElementType()))
            return false;

        return isNullableObjectEquals(hashKey, otherSchema.getHashKey());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSchemaType().hashCode();
        result = 31 * result + elementType.hashCode();
        result = 31 * result + Objects.hashCode(hashKey);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" Set<").append(getElementType()).append(">");

        if(hashKey != null) {
            builder.append(" @HashKey(");
            if(hashKey.numFields() > 0) {
                builder.append(hashKey.getFieldPath(0));
                for(int i=1;i<hashKey.numFields();i++) {
                    builder.append(", ").append(hashKey.getFieldPath(i));
                }
            }
            builder.append(")");
        }

        builder.append(";");
        return builder.toString();
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        if(getHashKey() != null)
            dos.write(SchemaType.SET.getTypeIdWithPrimaryKey());
        else
            dos.write(SchemaType.SET.getTypeId());

        dos.writeUTF(getName());
        dos.writeUTF(getElementType());

        if(getHashKey() != null) {
            VarInt.writeVInt(dos, getHashKey().numFields());
            for(int i=0;i<getHashKey().numFields();i++) {
                dos.writeUTF(getHashKey().getFieldPath(i));
            }
        }
    }

}
