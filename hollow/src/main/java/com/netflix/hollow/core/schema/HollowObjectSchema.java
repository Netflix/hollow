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

import com.netflix.hollow.api.error.IncompatibleSchemaException;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.TypeFilter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A schema for an Object record type.
 *
 * @see HollowSchema
 *
 * @author dkoszewnik
 *
 */
public class HollowObjectSchema extends HollowSchema {

    private final Map<String, Integer> nameFieldIndexLookup;
    private final String fieldNames[];
    private final FieldType fieldTypes[];
    protected final String referencedTypes[];
    private final HollowTypeReadState referencedFieldTypeStates[];  /// populated during deserialization
    private final PrimaryKey primaryKey;

    private int size;

    public HollowObjectSchema(String schemaName, int numFields, String... keyFieldPaths) {
        this(schemaName, numFields, keyFieldPaths == null || keyFieldPaths.length == 0 ? null : new PrimaryKey(schemaName, keyFieldPaths));
    }

    public HollowObjectSchema(String schemaName, int numFields, PrimaryKey primaryKey) {
        super(schemaName);

        this.nameFieldIndexLookup = new HashMap<>(numFields);
        this.fieldNames = new String[numFields];
        this.fieldTypes = new FieldType[numFields];
        this.referencedTypes = new String[numFields];
        this.referencedFieldTypeStates = new HollowTypeReadState[numFields];
        this.primaryKey = primaryKey;
    }

    public int numFields() {
        return size;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public int addField(String fieldName, FieldType fieldType) {
        return addField(fieldName, fieldType, null);
    }

    public int addField(String fieldName, FieldType fieldType, String referencedType) {
        if (fieldType == FieldType.REFERENCE && referencedType == null) {
            throw new RuntimeException(
                    "When adding a REFERENCE field to a schema, the referenced type must be provided.  Check type: "
                            + getName() + " field: " + fieldName);
        }
        fieldNames[size] = fieldName;
        fieldTypes[size] = fieldType;
        referencedTypes[size] = referencedType;
        nameFieldIndexLookup.put(fieldName, size);

        size++;

        return size - 1;
    }

    /**
     * @deprecated This method ignores the provided {@code HollowTypeReadState} - you should use
     * {@link #addField(String, FieldType, String)} and then call
     * {@link #setReferencedTypeState(int, HollowTypeReadState)} with the returned integer. This
     * method will be removed in a future release.
     */
    @Deprecated
    public int addField(String fieldName, FieldType fieldType, String referencedType, HollowTypeReadState referencedTypeState) {
        return addField(fieldName, fieldType, referencedType);
    }

    /**
     * Returns the position of a field previously added to the map, or -1 if the field has not been added to the map.
     *
     * The positions of the fields are hashed into the <code>hashedPositionArray</code> by the hashCode of the fieldName.
     *
     * @param fieldName the field name
     * @return the position
     */
    public int getPosition(String fieldName) {
        Integer index = nameFieldIndexLookup.get(fieldName);
        if (index == null) {
            return -1;
        }
        return index;
    }

    public String getFieldName(int fieldPosition) {
        return fieldNames[fieldPosition];
    }

    public FieldType getFieldType(String fieldName) {
        int fieldPosition = getPosition(fieldName);

        if(fieldPosition == -1)
            return null;

        return getFieldType(fieldPosition);
    }

    public FieldType getFieldType(int fieldPosition) {
        return fieldTypes[fieldPosition];
    }

    public String getReferencedType(String fieldName) {
        int fieldPosition = getPosition(fieldName);

        if(fieldPosition == -1)
            return null;

        return getReferencedType(fieldPosition);
    }

    public String getReferencedType(int fieldPosition) {
        return referencedTypes[fieldPosition];
    }

    public void setReferencedTypeState(int fieldPosition, HollowTypeReadState state) {
        referencedFieldTypeStates[fieldPosition] = state;
    }

    public HollowTypeReadState getReferencedTypeState(int fieldPosition) {
        return referencedFieldTypeStates[fieldPosition];
    }

    public HollowObjectSchema findCommonSchema(HollowObjectSchema otherSchema) {
        if(!getName().equals(otherSchema.getName())) {
            throw new IllegalArgumentException("Cannot find common schema of two schemas with different names!");
        }

        int commonFields = 0;

        for (String fieldName : fieldNames) {
            if(otherSchema.getPosition(fieldName) != -1) {
                commonFields++;
            }
        }

        PrimaryKey primaryKey = isNullableObjectEquals(this.primaryKey, otherSchema.getPrimaryKey()) ? this.primaryKey : null;
        HollowObjectSchema commonSchema = new HollowObjectSchema(getName(), commonFields, primaryKey);

        for (int i = 0; i < fieldNames.length; i++) {
            int otherFieldIndex = otherSchema.getPosition(fieldNames[i]);
             if (otherFieldIndex != -1) {
                if (fieldTypes[i] != otherSchema.getFieldType(otherFieldIndex)
                        || !referencedTypesEqual(referencedTypes[i], otherSchema.getReferencedType(otherFieldIndex))) {
                    String fieldType = fieldTypes[i] == FieldType.REFERENCE ? referencedTypes[i]
                        : fieldTypes[i].toString().toLowerCase();
                    String otherFieldType = otherSchema.getFieldType(otherFieldIndex) == FieldType.REFERENCE
                        ? otherSchema.getReferencedType(otherFieldIndex)
                        : otherSchema.getFieldType(otherFieldIndex).toString().toLowerCase();
                    throw new IncompatibleSchemaException(getName(), fieldNames[i], fieldType, otherFieldType);
                }

                commonSchema.addField(fieldNames[i], fieldTypes[i], referencedTypes[i]);
            }
        }

        return commonSchema;
    }

    public HollowObjectSchema findUnionSchema(HollowObjectSchema otherSchema) {
        if(!getName().equals(otherSchema.getName())) {
            throw new IllegalArgumentException("Cannot find common schema of two schemas with different names!");
        }

        int totalFields = otherSchema.numFields();

        for (String fieldName : fieldNames) {
            if(otherSchema.getPosition(fieldName) == -1)
                totalFields++;
        }

        PrimaryKey primaryKey = isNullableObjectEquals(this.primaryKey, otherSchema.getPrimaryKey()) ? this.primaryKey : null;
        HollowObjectSchema unionSchema = new HollowObjectSchema(getName(), totalFields, primaryKey);

        for(int i=0;i<fieldNames.length;i++) {
            unionSchema.addField(fieldNames[i], fieldTypes[i], referencedTypes[i]);
        }

        for(int i=0;i<otherSchema.numFields();i++) {
            if(getPosition(otherSchema.getFieldName(i)) == -1) {
                unionSchema.addField(otherSchema.getFieldName(i), otherSchema.getFieldType(i), otherSchema.getReferencedType(i));
            }
        }

        return unionSchema;
    }

    public HollowObjectSchema filterSchema(HollowFilterConfig config) {
        /*
         * This method is preserved for binary compat from before TypeFilter was introduced.
         */

        return filterSchema((TypeFilter)config);
    }

    public HollowObjectSchema filterSchema(TypeFilter filter) {
        String type = getName();

        int includedFields = 0;

        for(int i=0;i<numFields();i++) {
            String field = getFieldName(i);
            if(filter.includes(type, field))
                includedFields++;
        }

        HollowObjectSchema filteredSchema = new HollowObjectSchema(getName(), includedFields, primaryKey);

        for(int i=0;i<numFields();i++) {
            String field = getFieldName(i);
            if(filter.includes(type, field))
                filteredSchema.addField(field, getFieldType(i), getReferencedType(i));
        }

        return filteredSchema;
    }

    private boolean referencedTypesEqual(String type1, String type2) {
        if(type1 == null)
            return type2 == null;
        return type1.equals(type2);
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.OBJECT;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if(!(other instanceof HollowObjectSchema))
            return false;
        HollowObjectSchema otherSchema = (HollowObjectSchema) other;
        if(!getName().equals(otherSchema.getName()))
            return false;
        if(otherSchema.numFields() != numFields())
            return false;

        if (!isNullableObjectEquals(primaryKey, otherSchema.getPrimaryKey()))
            return false;

        for(int i=0;i<numFields();i++) {
            if(getFieldType(i) != otherSchema.getFieldType(i))
                return false;
            if(getFieldType(i) == FieldType.REFERENCE && !getReferencedType(i).equals(otherSchema.getReferencedType(i)))
                return false;
            if(!getFieldName(i).equals(otherSchema.getFieldName(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSchemaType().hashCode();
        result = 31 * result + Objects.hash(primaryKey);
        result = 31 * result + Arrays.hashCode(fieldNames);
        result = 31 * result + Arrays.hashCode(fieldTypes);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getName());
        if (primaryKey != null) {
            builder.append(" @PrimaryKey(");
            if (primaryKey.numFields() > 0) {
                builder.append(primaryKey.getFieldPath(0));
                for (int i = 1; i < primaryKey.numFields(); i++) {
                    builder.append(", ").append(primaryKey.getFieldPath(i));
                }
            }
            builder.append(")");
        }

        builder.append(" {\n");
        for(int i=0;i<numFields();i++) {
            builder.append("\t");
            if(getFieldType(i) == FieldType.REFERENCE) {
                builder.append(getReferencedType(i));
            } else {
                builder.append(getFieldType(i).toString().toLowerCase());
            }
            builder.append(" ").append(getFieldName(i)).append(";\n");
        }
        builder.append("}");

        return builder.toString();
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        if (primaryKey != null)
            dos.write(SchemaType.OBJECT.getTypeIdWithPrimaryKey());
        else
            dos.write(SchemaType.OBJECT.getTypeId());

        dos.writeUTF(getName());
        if (primaryKey != null) {
            VarInt.writeVInt(dos, primaryKey.numFields());
            for (int i = 0; i < primaryKey.numFields(); i++) {
                dos.writeUTF(primaryKey.getFieldPath(i));
            }
        }

        dos.writeShort(size);
        for(int i=0;i<size;i++) {
            dos.writeUTF(fieldNames[i]);
            dos.writeUTF(fieldTypes[i].name());
            if(fieldTypes[i] == FieldType.REFERENCE)
                dos.writeUTF(referencedTypes[i]);
        }

    }

    /**
     * All allowable field types.
     *
     */
    public enum FieldType {
        /**
         * A reference to another field.  References are typed, and are fixed-length fields are encoded as the ordinal of the referenced record.
         */
        REFERENCE(-1, false),
        /**
         * An integer value up to 32 bits.  Integers are fixed-length fields encoded with zig-zag encoding.
         * The value Integer.MIN_VALUE is reserved for a sentinel value indicating null.
         */
        INT(-1, false),
        /**
         * An integer value up to 64 bits.  Longs are fixed-length fields encoded with zig-zag encoding.
         * The value Long.MIN_VALUE is reserved for a sentinel value indicating null.
         */
        LONG(-1, false),
        /**
         * A boolean value.  Booleans are encoded as fields requiring two bits each.  Two bits are required
         * because boolean fields can carry any of the three values: true, false, or null.
         */
        BOOLEAN(1, false),
        /**
         * A floating-point number.  Floats are encoded as fixed-length fields four bytes long.
         */
        FLOAT(4, false),
        /**
         * A double-precision floating point number.  Doubles are encoded as fixed-length fields eight bytes long.
         */
        DOUBLE(8, false),
        /**
         * A String of characters.  All Strings for all records containing a given field are encoded in a packed array
         * of variable-length characters.  The values are ordered by the ordinal of the record to which they belong.
         * Each individual record contains a fixed-length field which holds an integer which points to the end of the
         * array range containing the value for the specific record.  The beginning of the range is determined by
         * reading the pointer from the previous record.
         */
        STRING(-1, true),
        /**
         * A byte array.  All byte arrays for all records containing a given field are encoded in a packed array
         * of bytes.  The values are ordered by the ordinal of the record to which they belong.
         * Each individual record contains a fixed-length field which holds an integer which points to the end of the
         * array range containing the value for the specific record.  The beginning of the range is determined by
         * reading the pointer from the previous record.
         */
        BYTES(-1, true);

        private final int fixedLength;
        private final boolean varIntEncodesLength;

        FieldType(int fixedLength, boolean varIntEncodesLength) {
            this.fixedLength = fixedLength;
            this.varIntEncodesLength = varIntEncodesLength;
        }

        public int getFixedLength() {
            return fixedLength;
        }

        public boolean isVariableLength() {
            return varIntEncodesLength;
        }
    }
}
