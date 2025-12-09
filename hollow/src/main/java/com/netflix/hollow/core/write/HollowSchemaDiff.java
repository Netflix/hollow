/*
 *  Copyright 2016-2025 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the difference between two schemas.
 * Used to transmit schema changes via delta blobs.
 *
 * Currently supports only field additions.
 * Future versions may support field removals, renames, etc.
 */
public class HollowSchemaDiff {

    private final String typeName;
    private final List<FieldAddition> addedFields;

    /**
     * Represents a field that was added to a schema.
     */
    public static class FieldAddition {
        private final String fieldName;
        private final FieldType fieldType;
        private final int position;
        private final String referencedType; // null for non-REFERENCE types

        public FieldAddition(String fieldName, FieldType fieldType, int position, String referencedType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.position = position;
            this.referencedType = referencedType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public FieldType getFieldType() {
            return fieldType;
        }

        public int getPosition() {
            return position;
        }

        public String getReferencedType() {
            return referencedType;
        }
    }

    public HollowSchemaDiff(String typeName, List<FieldAddition> addedFields) {
        this.typeName = typeName;
        this.addedFields = addedFields;
    }

    /**
     * Compute the difference between two schemas.
     * Currently supports only field additions.
     */
    public static HollowSchemaDiff compute(HollowSchema oldSchema, HollowSchema newSchema) {
        if (!(oldSchema instanceof HollowObjectSchema) || !(newSchema instanceof HollowObjectSchema)) {
            throw new IllegalArgumentException("Schema diff only supports HollowObjectSchema");
        }

        if (!oldSchema.getName().equals(newSchema.getName())) {
            throw new IllegalArgumentException("Cannot compute diff for schemas with different names");
        }

        HollowObjectSchema oldObj = (HollowObjectSchema) oldSchema;
        HollowObjectSchema newObj = (HollowObjectSchema) newSchema;

        List<FieldAddition> addedFields = new ArrayList<>();

        // Check all fields in new schema
        for (int i = 0; i < newObj.numFields(); i++) {
            String fieldName = newObj.getFieldName(i);
            FieldType newType = newObj.getFieldType(i);
            int oldPosition = oldObj.getPosition(fieldName);

            if (oldPosition == -1) {
                // Field was added
                String referencedType = newType == FieldType.REFERENCE ? newObj.getReferencedType(i) : null;
                addedFields.add(new FieldAddition(fieldName, newType, i, referencedType));
            } else {
                // Field exists in old schema - verify type didn't change
                FieldType oldType = oldObj.getFieldType(oldPosition);
                if (oldType != newType) {
                    throw new IllegalArgumentException(
                        "Field type change not supported: " + fieldName + " changed from " + oldType + " to " + newType);
                }

                // For REFERENCE fields, verify referenced type didn't change
                if (newType == FieldType.REFERENCE) {
                    String oldRefType = oldObj.getReferencedType(oldPosition);
                    String newRefType = newObj.getReferencedType(i);
                    if (!oldRefType.equals(newRefType)) {
                        throw new IllegalArgumentException(
                            "Reference type change not supported: " + fieldName + " changed from " + oldRefType + " to " + newRefType);
                    }
                }
            }
        }

        // Check for removed fields (not supported)
        for (int i = 0; i < oldObj.numFields(); i++) {
            String fieldName = oldObj.getFieldName(i);
            int newPosition = newObj.getPosition(fieldName);
            if (newPosition == -1) {
                throw new IllegalArgumentException("Field removal not supported: " + fieldName + " was removed");
            }
        }

        return new HollowSchemaDiff(oldSchema.getName(), addedFields);
    }

    /**
     * Apply this diff to a schema to produce the new schema.
     */
    public HollowObjectSchema apply(HollowObjectSchema oldSchema) {
        if (!oldSchema.getName().equals(typeName)) {
            throw new IllegalArgumentException("Schema name mismatch: diff is for " + typeName + ", schema is " + oldSchema.getName());
        }

        int newNumFields = oldSchema.numFields() + addedFields.size();
        HollowObjectSchema newSchema = new HollowObjectSchema(typeName, newNumFields);

        // Copy existing fields
        for (int i = 0; i < oldSchema.numFields(); i++) {
            String fieldName = oldSchema.getFieldName(i);
            FieldType fieldType = oldSchema.getFieldType(i);
            if (fieldType == FieldType.REFERENCE) {
                newSchema.addField(fieldName, fieldType, oldSchema.getReferencedType(i));
            } else {
                newSchema.addField(fieldName, fieldType);
            }
        }

        // Add new fields
        for (FieldAddition addition : addedFields) {
            if (addition.fieldType == FieldType.REFERENCE) {
                newSchema.addField(addition.fieldName, addition.fieldType, addition.referencedType);
            } else {
                newSchema.addField(addition.fieldName, addition.fieldType);
            }
        }

        return newSchema;
    }

    /**
     * Write this diff to an output stream in binary format.
     */
    public void writeTo(OutputStream os) throws IOException {
        VarInt.writeVInt(os, addedFields.size());

        for (FieldAddition field : addedFields) {
            // Write field name
            byte[] fieldNameBytes = field.fieldName.getBytes(StandardCharsets.UTF_8);
            VarInt.writeVInt(os, fieldNameBytes.length);
            os.write(fieldNameBytes);

            // Write field type
            os.write(field.fieldType.ordinal());

            // Write referenced type if REFERENCE
            if (field.fieldType == FieldType.REFERENCE && field.referencedType != null) {
                byte[] refTypeBytes = field.referencedType.getBytes(StandardCharsets.UTF_8);
                VarInt.writeVInt(os, refTypeBytes.length);
                os.write(refTypeBytes);
            }
        }
    }

    /**
     * Read a diff from an input stream.
     */
    public static HollowSchemaDiff readFrom(InputStream is, String typeName) throws IOException {
        List<FieldAddition> addedFields = new ArrayList<>();

        int numFields = VarInt.readVInt(is);

        for (int i = 0; i < numFields; i++) {
            // Read field name
            int fieldNameLength = VarInt.readVInt(is);
            byte[] fieldNameBytes = new byte[fieldNameLength];
            readFully(is, fieldNameBytes);
            String fieldName = new String(fieldNameBytes, StandardCharsets.UTF_8);

            // Read field type
            int fieldTypeOrdinal = is.read();
            if (fieldTypeOrdinal == -1) {
                throw new IOException("Unexpected end of stream reading field type");
            }
            FieldType fieldType = FieldType.values()[fieldTypeOrdinal];

            // Read referenced type if REFERENCE
            String referencedType = null;
            if (fieldType == FieldType.REFERENCE) {
                int refTypeLength = VarInt.readVInt(is);
                byte[] refTypeBytes = new byte[refTypeLength];
                readFully(is, refTypeBytes);
                referencedType = new String(refTypeBytes, StandardCharsets.UTF_8);
            }

            // Position will be determined when applied
            addedFields.add(new FieldAddition(fieldName, fieldType, -1, referencedType));
        }

        return new HollowSchemaDiff(typeName, addedFields);
    }

    /**
     * Read a diff from a HollowBlobInput.
     */
    public static HollowSchemaDiff readFrom(HollowBlobInput in, String typeName) throws IOException {
        List<FieldAddition> addedFields = new ArrayList<>();

        int numFields = VarInt.readVInt(in);

        for (int i = 0; i < numFields; i++) {
            // Read field name
            int fieldNameLength = VarInt.readVInt(in);
            byte[] fieldNameBytes = new byte[fieldNameLength];
            readFully(in, fieldNameBytes);
            String fieldName = new String(fieldNameBytes, StandardCharsets.UTF_8);

            // Read field type
            int fieldTypeOrdinal = in.read();
            if (fieldTypeOrdinal == -1) {
                throw new IOException("Unexpected end of stream reading field type");
            }
            FieldType fieldType = FieldType.values()[fieldTypeOrdinal];

            // Read referenced type if REFERENCE
            String referencedType = null;
            if (fieldType == FieldType.REFERENCE) {
                int refTypeLength = VarInt.readVInt(in);
                byte[] refTypeBytes = new byte[refTypeLength];
                readFully(in, refTypeBytes);
                referencedType = new String(refTypeBytes, StandardCharsets.UTF_8);
            }

            // Position will be determined when applied
            addedFields.add(new FieldAddition(fieldName, fieldType, -1, referencedType));
        }

        return new HollowSchemaDiff(typeName, addedFields);
    }

    private static void readFully(InputStream is, byte[] buffer) throws IOException {
        int bytesRead = 0;
        while (bytesRead < buffer.length) {
            int count = is.read(buffer, bytesRead, buffer.length - bytesRead);
            if (count < 0) {
                throw new IOException("Unexpected end of stream");
            }
            bytesRead += count;
        }
    }

    private static void readFully(HollowBlobInput in, byte[] buffer) throws IOException {
        int bytesRead = 0;
        while (bytesRead < buffer.length) {
            int count = in.read(buffer, bytesRead, buffer.length - bytesRead);
            if (count < 0) {
                throw new IOException("Unexpected end of stream");
            }
            bytesRead += count;
        }
    }

    public String getTypeName() {
        return typeName;
    }

    public List<FieldAddition> getAddedFields() {
        return addedFields;
    }

    public boolean isEmpty() {
        return addedFields.isEmpty();
    }
}
