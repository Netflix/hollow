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
    private final boolean ordinalHashKey;
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
     * If of the constant {@link HollowSchema#ORDINAL_HASH_KEY_FIELD_NAMES} then schema has no hash key
     * but the hash function, applied to an element of a set to produce a hash code, is specified to be a
     * function that returns the element's ordinal.
     * Otherwise, the hash function is specified as described by
     * {@link com.netflix.hollow.core.write.objectmapper.HollowHashKey}.
     */
    public HollowSetSchema(String schemaName, String elementType, String... hashKeyFieldPaths) {
        super(schemaName);
        this.elementType = elementType;
        if (hashKeyFieldPaths == ORDINAL_HASH_KEY_FIELD_NAMES) {
            this.ordinalHashKey = true;
            this.hashKey = null;
        } else if (hashKeyFieldPaths == null || hashKeyFieldPaths.length == 0) {
            this.ordinalHashKey = false;
            this.hashKey = null;
        } else {
            this.ordinalHashKey = false;
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

    public HollowSetSchema withoutKeys() {
        if (hashKey == null && !ordinalHashKey) {
            return this;
        }

        return new HollowSetSchema(getName(), getElementType());
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof HollowSetSchema))
            return false;
        HollowSetSchema otherSchema = (HollowSetSchema)other;
        if(!getName().equals(otherSchema.getName()))
            return false;
        if(!getElementType().equals(otherSchema.getElementType()))
            return false;
        if(ordinalHashKey != otherSchema.ordinalHashKey)
            return false;

        return Objects.equals(getHashKey(), otherSchema.getHashKey());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" Set<").append(getElementType()).append(">");

        if(hashKey != null || ordinalHashKey) {
            builder.append(" @HashKey(");
            if(hashKey != null) {
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

        if (hashKey != null || ordinalHashKey)
            dos.write(SchemaType.SET.getTypeIdWithPrimaryKey());
        else
            dos.write(SchemaType.SET.getTypeId());

        dos.writeUTF(getName());
        dos.writeUTF(getElementType());

        if (ordinalHashKey) {
            VarInt.writeVInt(dos, 0);
        } else if (hashKey != null) {
            VarInt.writeVInt(dos, hashKey.numFields());
            for (int i = 0; i < getHashKey().numFields(); i++) {
                dos.writeUTF(getHashKey().getFieldPath(i));
            }
        }
    }

}
