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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Reads and applies appended schema data from delta blobs.
 * Populates new field values for preserved ordinals.
 */
public class DeltaSchemaAppendDataApplicator {

    private final HollowReadStateEngine stateEngine;
    private final HollowBlobInput in;

    public DeltaSchemaAppendDataApplicator(HollowReadStateEngine stateEngine, HollowBlobInput in) {
        this.stateEngine = stateEngine;
        this.in = in;
    }

    /**
     * Read and apply appended schema data.
     * Handles cases where consumer schema doesn't match producer schema.
     */
    public void readAndApply() throws IOException {
        long totalByteLength = VarInt.readVLong(in);

        if (totalByteLength == 0) {
            return; // No appended data
        }

        int numTypes = VarInt.readVInt(in);

        for (int i = 0; i < numTypes; i++) {
            readAndApplyTypeData();
        }
    }

    private void readAndApplyTypeData() throws IOException {
        // Read type name
        int typeNameLength = VarInt.readVInt(in);
        byte[] typeNameBytes = new byte[typeNameLength];
        readFully(typeNameBytes);
        String typeName = new String(typeNameBytes, StandardCharsets.UTF_8);

        // Get type state (if exists in consumer schema)
        HollowTypeReadState typeReadState = stateEngine.getTypeState(typeName);
        HollowObjectTypeReadState typeState = null;
        if (typeReadState instanceof HollowObjectTypeReadState) {
            typeState = (HollowObjectTypeReadState) typeReadState;
        }

        int numNewFields = VarInt.readVInt(in);

        for (int i = 0; i < numNewFields; i++) {
            readAndApplyFieldData(typeState);
        }
    }

    private void readAndApplyFieldData(HollowObjectTypeReadState typeState) throws IOException {
        // Read field name
        int fieldNameLength = VarInt.readVInt(in);
        byte[] fieldNameBytes = new byte[fieldNameLength];
        readFully(fieldNameBytes);
        String fieldName = new String(fieldNameBytes, StandardCharsets.UTF_8);

        // Read field type
        int fieldTypeOrdinal = in.read();
        FieldType fieldType = FieldType.values()[fieldTypeOrdinal];

        // Read referenced type if REFERENCE
        if (fieldType == FieldType.REFERENCE) {
            int refTypeLength = VarInt.readVInt(in);
            byte[] refTypeBytes = new byte[refTypeLength];
            readFully(refTypeBytes);
            // Referenced type name read but not stored
        }

        // Check if consumer has this field and type matches
        int fieldIndex = -1;
        boolean canWrite = false;

        if (typeState != null) {
            HollowObjectSchema schema = typeState.getSchema();
            fieldIndex = schema.getPosition(fieldName);

            // Verify field types match
            if (fieldIndex != -1 && schema.getFieldType(fieldIndex) == fieldType) {
                canWrite = true;
            } else {
                fieldIndex = -1; // Type mismatch or field not found
            }
        }

        // Read ordinal-value pairs
        int numOrdinals = VarInt.readVInt(in);

        for (int i = 0; i < numOrdinals; i++) {
            int ordinal = VarInt.readVInt(in);
            Object value = readValue(fieldType);

            // Write directly to data elements if consumer has this field
            if (canWrite && fieldIndex != -1 && typeState != null) {
                // Calculate which shard this ordinal belongs to
                // Shard mask = numShards - 1 (works because numShards is always a power of 2)
                int numShards = typeState.getShardsVolatile().getShards().length;
                int shardMask = numShards - 1;
                int shardIndex = ordinal & shardMask;
                HollowObjectTypeDataElements dataElements =
                    (HollowObjectTypeDataElements) typeState.getShardsVolatile().getShards()[shardIndex].getDataElements();

                if (value == null) {
                    dataElements.writeNull(ordinal, fieldIndex);
                } else {
                    switch (fieldType) {
                        case INT:
                        case REFERENCE:
                            dataElements.writeInt(ordinal, fieldIndex, (Integer) value);
                            break;
                        case LONG:
                            dataElements.writeLong(ordinal, fieldIndex, (Long) value);
                            break;
                        case BOOLEAN:
                            dataElements.writeBoolean(ordinal, fieldIndex, (Boolean) value);
                            break;
                        case FLOAT:
                            dataElements.writeFloat(ordinal, fieldIndex, (Float) value);
                            break;
                        case DOUBLE:
                            dataElements.writeDouble(ordinal, fieldIndex, (Double) value);
                            break;
                        case STRING:
                            dataElements.writeString(ordinal, fieldIndex, (String) value);
                            break;
                        case BYTES:
                            dataElements.writeBytes(ordinal, fieldIndex, (byte[]) value);
                            break;
                    }
                }
            }
            // If field doesn't exist in consumer schema, value is read and discarded (graceful degradation)
        }
    }

    private Object readValue(FieldType fieldType) throws IOException {
        switch (fieldType) {
            case REFERENCE:
            case INT:
                return VarInt.readVInt(in);
            case LONG:
                return VarInt.readVLong(in);
            case BOOLEAN:
                return in.read() == 1;
            case FLOAT:
                return Float.intBitsToFloat(in.readInt());
            case DOUBLE:
                return Double.longBitsToDouble(in.readLong());
            case STRING:
                int length = VarInt.readVInt(in);
                byte[] bytes = new byte[length];
                readFully(bytes);
                return new String(bytes, StandardCharsets.UTF_8);
            case BYTES:
                int byteLength = VarInt.readVInt(in);
                byte[] byteArray = new byte[byteLength];
                readFully(byteArray);
                return byteArray;
            default:
                throw new IllegalStateException("Unknown field type: " + fieldType);
        }
    }

    private void readFully(byte[] buffer) throws IOException {
        int bytesRead = 0;
        while (bytesRead < buffer.length) {
            int count = in.read(buffer, bytesRead, buffer.length - bytesRead);
            if (count < 0) {
                throw new IOException("Unexpected end of stream");
            }
            bytesRead += count;
        }
    }
}
