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
 * A schema for a Map record type
 *
 * @see HollowSchema
 *
 * @author dkoszewnik
 *
 */
public class HollowMapSchema extends HollowSchema {

    private final String keyType;
    private final String valueType;
    private final PrimaryKey hashKey;

    private HollowTypeReadState keyTypeState;
    private HollowTypeReadState valueTypeState;

    /**
     * Constructs a schema for a hollow set.
     *
     * @param schemaName the schema name
     * @param keyType the key type name of the map
     * @param valueType the value type name of the map
     * @param hashKeyFieldPaths the field paths of the hash key applied to a map of this schema.
     * If {@code null} or empty then the schema has no hash key and the hash function, applied to
     * a key of a map to produce a hash code, is unspecified by this schema.
     * Otherwise, the hash function is specified as described by
     * {@link com.netflix.hollow.core.write.objectmapper.HollowHashKey}.
     */
    public HollowMapSchema(String schemaName, String keyType, String valueType, String... hashKeyFieldPaths) {
        super(schemaName);
        this.keyType = keyType;
        this.valueType = valueType;
        if (hashKeyFieldPaths == null || hashKeyFieldPaths.length == 0) {
            this.hashKey = null;
        } else {
            this.hashKey = new PrimaryKey(keyType, hashKeyFieldPaths);
        }
    }

    public String getKeyType() {
        return keyType;
    }

    public String getValueType() {
        return valueType;
    }

    public PrimaryKey getHashKey() {
        return hashKey;
    }

    public HollowTypeReadState getKeyTypeState() {
        return keyTypeState;
    }

    public void setKeyTypeState(HollowTypeReadState keyTypeState) {
        this.keyTypeState = keyTypeState;
    }

    public HollowTypeReadState getValueTypeState() {
        return valueTypeState;
    }

    public void setValueTypeState(HollowTypeReadState valueTypeState) {
        this.valueTypeState = valueTypeState;
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.MAP;
    }

    public HollowMapSchema withoutHashKey() {
        if (hashKey == null) {
            return this;
        }

        return new HollowMapSchema(getName(), getKeyType(), getValueType());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if(!(other instanceof HollowMapSchema))
            return false;
        HollowMapSchema otherSchema = (HollowMapSchema)other;
        if(!getName().equals(otherSchema.getName()))
            return false;
        if(!getKeyType().equals(otherSchema.getKeyType()))
            return false;
        if(!getValueType().equals(otherSchema.getValueType()))
            return false;

        return Objects.equals(getHashKey(), otherSchema.getHashKey());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSchemaType().hashCode();
        result = 31 * result + keyType.hashCode();
        result = 31 * result + valueType.hashCode();
        result = 31 * result + Objects.hashCode(hashKey);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" Map<").append(getKeyType()).append(",").append(getValueType()).append(">");

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
            dos.write(SchemaType.MAP.getTypeIdWithPrimaryKey());
        else
            dos.write(SchemaType.MAP.getTypeId());

        dos.writeUTF(getName());
        dos.writeUTF(getKeyType());
        dos.writeUTF(getValueType());

        if (hashKey != null) {
            VarInt.writeVInt(dos, getHashKey().numFields());
            for(int i=0;i<getHashKey().numFields();i++) {
                dos.writeUTF(getHashKey().getFieldPath(i));
            }
        }
    }
}
