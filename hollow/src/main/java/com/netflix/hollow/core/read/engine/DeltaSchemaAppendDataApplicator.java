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
import com.netflix.hollow.core.write.HollowDeltaSchemaAppendConfig;
import com.netflix.hollow.core.write.HollowSchemaDiff;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Reads and applies appended schema data from delta blobs.
 * Populates new field values for preserved ordinals.
 */
public class DeltaSchemaAppendDataApplicator {

    private final HollowReadStateEngine stateEngine;
    private final HollowBlobInput in;
    private boolean hasFieldValuesToRead = false;

    public DeltaSchemaAppendDataApplicator(HollowReadStateEngine stateEngine, HollowBlobInput in) {
        this.stateEngine = stateEngine;
        this.in = in;
    }

    /**
     * Read and apply schema diffs before type state deltas.
     * Schema migrations must happen before data changes.
     */
    public void readAndApplySchemaDiffs() throws IOException {
        long totalByteLength = VarInt.readVLong(in);

        if (totalByteLength == 0) {
            hasFieldValuesToRead = false;
            return;
        }

        long schemaDiffsSectionLength = VarInt.readVLong(in);

        HollowDeltaSchemaAppendConfig config = stateEngine.getDeltaSchemaAppendConfig();
        boolean applySchemaUpdates = config != null && config.shouldIncludeSchemaDefinitions();

        if (applySchemaUpdates && schemaDiffsSectionLength > 0) {
            readAndApplySchemaDiffsInternal();
        } else if (schemaDiffsSectionLength > 0) {
            skipBytes(schemaDiffsSectionLength);
        }

        hasFieldValuesToRead = true;
    }

    /**
     * Read and apply field values after type state deltas.
     */
    public void readAndApplyFieldValues() throws IOException {
        if (!hasFieldValuesToRead) {
            return;
        }

        int numTypes = VarInt.readVInt(in);

        for (int i = 0; i < numTypes; i++) {
            readAndApplyTypeData();
        }
    }

    private void readAndApplySchemaDiffsInternal() throws IOException {
        int numDiffs = VarInt.readVInt(in);

        for (int i = 0; i < numDiffs; i++) {
            int typeNameLength = VarInt.readVInt(in);
            byte[] typeNameBytes = new byte[typeNameLength];
            readFully(typeNameBytes);
            String typeName = new String(typeNameBytes, StandardCharsets.UTF_8);

            HollowSchemaDiff diff = HollowSchemaDiff.readFrom(in, typeName);

            HollowTypeReadState typeState = stateEngine.getTypeState(typeName);
            if (typeState instanceof HollowObjectTypeReadState) {
                HollowObjectTypeReadState objectState = (HollowObjectTypeReadState) typeState;
                HollowObjectSchema currentSchema = objectState.getSchema();
                HollowObjectSchema newSchema = diff.apply(currentSchema);
                objectState.updateSchema(newSchema);
            }
        }
    }

    private void skipBytes(long numBytes) throws IOException {
        long remaining = numBytes;
        while (remaining > 0) {
            long skipped = in.skipBytes(remaining);
            if (skipped <= 0) {
                throw new IOException("Unable to skip bytes in stream");
            }
            remaining -= skipped;
        }
    }

    private void readAndApplyTypeData() throws IOException {
        int typeNameLength = VarInt.readVInt(in);
        byte[] typeNameBytes = new byte[typeNameLength];
        readFully(typeNameBytes);
        String typeName = new String(typeNameBytes, StandardCharsets.UTF_8);

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
        int fieldNameLength = VarInt.readVInt(in);
        byte[] fieldNameBytes = new byte[fieldNameLength];
        readFully(fieldNameBytes);
        String fieldName = new String(fieldNameBytes, StandardCharsets.UTF_8);

        int fieldTypeOrdinal = in.read();
        FieldType fieldType = FieldType.values()[fieldTypeOrdinal];

        if (fieldType == FieldType.REFERENCE) {
            int refTypeLength = VarInt.readVInt(in);
            byte[] refTypeBytes = new byte[refTypeLength];
            readFully(refTypeBytes);
        }

        int fieldIndex = -1;
        boolean canWrite = false;

        if (typeState != null) {
            HollowObjectSchema schema = typeState.getSchema();
            fieldIndex = schema.getPosition(fieldName);

            if (fieldIndex != -1 && schema.getFieldType(fieldIndex) == fieldType) {
                canWrite = true;
            } else {
                fieldIndex = -1;
            }
        }

        int numOrdinals = VarInt.readVInt(in);

        for (int i = 0; i < numOrdinals; i++) {
            int ordinal = VarInt.readVInt(in);
            Object value = readValue(fieldType);

            if (canWrite && fieldIndex != -1 && typeState != null) {
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
