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

    /**
     * Constructs a schema for a hollow set.
     *
     * @param schemaName the schema name
     * @param elementType the element type name of the set
     * @param hashKeyFieldPaths the field paths of the hash key applied to a set of this schema.
     * If {@code null} or empty then the schema has no hash key and the hash function, applied to
     * an element of a set to produce a hash code, is unspecified by this schema.
     * Otherwise, the hash function is specified as described by
     * {@link com.netflix.hollow.core.write.objectmapper.HollowHashKey}.
     */
    public HollowSetSchema(String schemaName, String elementType, String... hashKeyFieldPaths) {
        super(schemaName);
        this.elementType = elementType;
        if (hashKeyFieldPaths == null || hashKeyFieldPaths.length == 0) {
            this.hashKey = null;
        } else {
            this.hashKey = new PrimaryKey(elementType, hashKeyFieldPaths);
        }
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

    public HollowSetSchema withoutHashKey() {
        if (hashKey == null) {
            return this;
        }

        return new HollowSetSchema(getName(), getElementType());
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

        return Objects.equals(getHashKey(), otherSchema.getHashKey());
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
            builder.append(hashKey.getFieldPath(0));
            for(int i=1;i<hashKey.numFields();i++) {
                builder.append(", ").append(hashKey.getFieldPath(i));
            }
            builder.append(")");
        }

        builder.append(";");
        return builder.toString();
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        if (hashKey != null)
            dos.write(SchemaType.SET.getTypeIdWithPrimaryKey());
        else
            dos.write(SchemaType.SET.getTypeId());

        dos.writeUTF(getName());
        dos.writeUTF(getElementType());

        if (hashKey != null) {
            VarInt.writeVInt(dos, hashKey.numFields());
            for (int i = 0; i < getHashKey().numFields(); i++) {
                dos.writeUTF(getHashKey().getFieldPath(i));
            }
        }
    }

}
