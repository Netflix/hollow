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
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.DeltaSchemaAppendDataCollector.FieldAppendData;
import com.netflix.hollow.core.write.DeltaSchemaAppendDataCollector.TypeAppendData;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Writes appended schema data to a data output stream.
 * Handles encoding of values according to field type per the wire format specification.
 */
public class DeltaSchemaAppendDataWriter {

    private final DataOutputStream dos;

    public DeltaSchemaAppendDataWriter(DataOutputStream dos) {
        this.dos = dos;
    }

    /**
     * Write appended data for all types.
     * Format:
     *   totalByteLength (VarLong)
     *   numTypes (VarInt)
     *   for each type: writeTypeAppendData()
     */
    public void writeAppendedData(DeltaSchemaAppendDataCollector collector) throws IOException {
        Map<String, TypeAppendData> typeDataMap = collector.getTypeDataMap();

        if (typeDataMap.isEmpty()) {
            VarInt.writeVLong(dos, 0L);
            return;
        }

        // Calculate total size for length prefix
        long totalSize = collector.calculateTotalSize();

        // Write total size
        VarInt.writeVLong(dos, totalSize);

        // Write number of types
        VarInt.writeVInt(dos, typeDataMap.size());

        // Write each type's appended data
        for (TypeAppendData typeData : typeDataMap.values()) {
            writeTypeAppendData(typeData);
        }
    }

    private void writeTypeAppendData(TypeAppendData typeData) throws IOException {
        // Write type name
        byte[] typeNameBytes = typeData.typeName.getBytes(StandardCharsets.UTF_8);
        VarInt.writeVInt(dos, typeNameBytes.length);
        dos.write(typeNameBytes);

        // Write number of new fields
        VarInt.writeVInt(dos, typeData.fields.size());

        // Write each field's data
        for (FieldAppendData fieldData : typeData.fields) {
            writeFieldAppendData(fieldData);
        }
    }

    private void writeFieldAppendData(FieldAppendData fieldData) throws IOException {
        // Write field name
        byte[] fieldNameBytes = fieldData.fieldName.getBytes(StandardCharsets.UTF_8);
        VarInt.writeVInt(dos, fieldNameBytes.length);
        dos.write(fieldNameBytes);

        // Write field type ordinal
        dos.writeByte(fieldData.fieldType.ordinal());

        // Write referenced type name if REFERENCE
        if (fieldData.fieldType == FieldType.REFERENCE) {
            byte[] refTypeBytes = fieldData.referencedType.getBytes(StandardCharsets.UTF_8);
            VarInt.writeVInt(dos, refTypeBytes.length);
            dos.write(refTypeBytes);
        }

        // Write number of ordinals with data
        VarInt.writeVInt(dos, fieldData.ordinalToValue.size());

        // Write ordinal-value pairs (sorted by ordinal for efficiency)
        List<Integer> sortedOrdinals = new ArrayList<>(fieldData.ordinalToValue.keySet());
        Collections.sort(sortedOrdinals);

        for (Integer ordinal : sortedOrdinals) {
            VarInt.writeVInt(dos, ordinal);
            Object value = fieldData.ordinalToValue.get(ordinal);
            writeValue(fieldData.fieldType, value);
        }
    }

    private void writeValue(FieldType fieldType, Object value) throws IOException {
        switch (fieldType) {
            case REFERENCE:
            case INT:
                VarInt.writeVInt(dos, (Integer) value);
                break;
            case LONG:
                VarInt.writeVLong(dos, (Long) value);
                break;
            case BOOLEAN:
                Boolean b = (Boolean) value;
                dos.writeByte(b ? 1 : 0);
                break;
            case FLOAT:
                // Write as int bits to match reader's Float.intBitsToFloat(in.readInt())
                dos.writeInt(Float.floatToIntBits((Float) value));
                break;
            case DOUBLE:
                // Write as long bits to match reader's Double.longBitsToDouble(in.readLong())
                dos.writeLong(Double.doubleToLongBits((Double) value));
                break;
            case STRING:
                String s = (String) value;
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                VarInt.writeVInt(dos, bytes.length);
                dos.write(bytes);
                break;
            case BYTES:
                byte[] byteArray = (byte[]) value;
                VarInt.writeVInt(dos, byteArray.length);
                dos.write(byteArray);
                break;
        }
    }
}
