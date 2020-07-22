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

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A HollowSchema defines the structure of a hollow data model.
 * <p>
 * Each HollowSchema corresponds to a single named record type, and defines the structure of those records.  A schema
 * will be one of:
 *
 * <dl>
 *      <dt>HollowObjectSchema</dt>
 *      <dd>Defines a fixed set of strongly typed fields. See {@link FieldType} for a complete list of allowable types.</dd>
 *      <dt>HollowListSchema</dt>
 *      <dd>Defines an ordered collection of records of a specific element record type.</dd>
 *      <dt>HollowSetSchema</dt>
 *      <dd>Defines an unordered collection of records of a specific element record type, without duplicates.</dd>
 *      <dt>HollowMapSchema</dt>
 *      <dd>Defines a key/value pair mapping between a specific key record type and specific value record type.</dd>
 *
 * </dl>
 *
 * @author dkoszewnik
 *
 */
public abstract class HollowSchema {

    private final String name;

    public HollowSchema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract SchemaType getSchemaType();

    public abstract void writeTo(OutputStream os) throws IOException;
    
    public static HollowSchema withoutKeys(HollowSchema schema) {
        switch(schema.getSchemaType()) {
        case SET:
            HollowSetSchema setSchema = (HollowSetSchema)schema;
            if(setSchema.getHashKey() != null)
                setSchema = new HollowSetSchema(setSchema.getName(), setSchema.getElementType());
            return setSchema;
        case MAP:
            HollowMapSchema mapSchema = (HollowMapSchema)schema;
            if(mapSchema.getHashKey() != null)
                mapSchema = new HollowMapSchema(mapSchema.getName(), mapSchema.getKeyType(), mapSchema.getValueType());
            return mapSchema;
        default:
            return schema;
        }
    }

    public static HollowSchema readFrom(InputStream is) throws IOException {
        try (HollowBlobInput hbi = HollowBlobInput.serial(is)) {
            return readFrom(hbi);
        }
    }
    public static HollowSchema readFrom(HollowBlobInput in) throws IOException {
        int schemaTypeId = in.read();

        String schemaName = in.readUTF();

        switch(SchemaType.fromTypeId(schemaTypeId)) {
            case OBJECT:
                return readObjectSchemaFrom(in, schemaName, SchemaType.hasKey(schemaTypeId));
            case LIST:
                return readListSchemaFrom(in, schemaName);
            case SET:
                return readSetSchemaFrom(in, schemaName, SchemaType.hasKey(schemaTypeId));
            case MAP:
                return readMapSchemaFrom(in, schemaName, SchemaType.hasKey(schemaTypeId));
        }

        throw new IOException();
    }

    private static HollowObjectSchema readObjectSchemaFrom(HollowBlobInput in, String schemaName, boolean hasPrimaryKey) throws IOException {
        String[] keyFieldPaths = null;
        if (hasPrimaryKey) {
            int numFields = VarInt.readVInt(in);
            keyFieldPaths = new String[numFields];
            for(int i=0;i<numFields;i++) {
                keyFieldPaths[i] = in.readUTF();
            }
        }

        int numFields = in.readShort();
        HollowObjectSchema schema = new HollowObjectSchema(schemaName, numFields, keyFieldPaths);

        for(int i=0;i<numFields;i++) {
            String fieldName = in.readUTF();
            FieldType fieldType = FieldType.valueOf(in.readUTF());
            String referencedType = fieldType == FieldType.REFERENCE ? in.readUTF() : null;
            schema.addField(fieldName, fieldType, referencedType);
        }

        return schema;
    }

    private static HollowSetSchema readSetSchemaFrom(HollowBlobInput in, String schemaName, boolean hasHashKey) throws IOException {
        String elementType = in.readUTF();

        String hashKeyFields[] = null;

        if(hasHashKey) {
            int numFields = VarInt.readVInt(in);
            hashKeyFields = new String[numFields];
            for(int i=0;i<numFields;i++) {
                hashKeyFields[i] = in.readUTF();
            }
        }

        return new HollowSetSchema(schemaName, elementType, hashKeyFields);
    }

    private static HollowListSchema readListSchemaFrom(HollowBlobInput in, String schemaName) throws IOException {
        String elementType = in.readUTF();

        return new HollowListSchema(schemaName, elementType);
    }

    private static HollowMapSchema readMapSchemaFrom(HollowBlobInput in, String schemaName, boolean hasHashKey) throws IOException {
        String keyType = in.readUTF();
        String valueType = in.readUTF();

        String hashKeyFields[] = null;

        if(hasHashKey) {
            int numFields = VarInt.readVInt(in);
            hashKeyFields = new String[numFields];
            for(int i=0;i<numFields;i++) {
                hashKeyFields[i] = in.readUTF();
            }
        }

        return new HollowMapSchema(schemaName, keyType, valueType, hashKeyFields);
    }

    protected static <T> boolean isNullableObjectEquals(T o1, T o2) {
        if (o1==o2) return true;
        if (o1==null && o2==null) return true;
        if (o1!=null && o1.equals(o2)) return true;
        return false;
    }

    public static enum SchemaType {
        OBJECT(0, 6),
        SET(1, 4),
        LIST(2, -1),
        MAP(3, 5);

        private final int typeId;
        private final int typeIdWithPrimaryKey;

        private SchemaType(int typeId, int typeIdWithPrimaryKey) {
            this.typeId = typeId;
            this.typeIdWithPrimaryKey = typeIdWithPrimaryKey;
        }

        public int getTypeId() {
            return typeId;
        }

        public int getTypeIdWithPrimaryKey() {
            return typeIdWithPrimaryKey;
        }

        public static SchemaType fromTypeId(int id) {
            switch(id) {
                case 0:
                case 6:
                    return OBJECT;
                case 1:
                case 4:
                    return SET;
                case 2:
                    return LIST;
                case 3:
                case 5:
                    return MAP;
            }
            throw new IllegalArgumentException("Cannot recognize HollowSchema type id " + id);
        }

        public static boolean hasKey(int typeId) {
            return typeId == 4 || typeId == 5 || typeId == 6;
        }

        public static class UnrecognizedSchemaTypeException extends IllegalStateException {
            public UnrecognizedSchemaTypeException(String name, SchemaType type) {
                super("unrecognized schema type; name=" + name + " type=" + type);
            }
        }
    }
}
