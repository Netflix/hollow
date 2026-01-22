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
import java.io.ByteArrayOutputStream;
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
     * Includes schema diffs and field values subsections.
     */
    public void writeAppendedData(DeltaSchemaAppendDataCollector collector) throws IOException {
        Map<String, TypeAppendData> typeDataMap = collector.getTypeDataMap();
        Map<String, HollowSchemaDiff> schemaDiffs = collector.collectSchemaDiffs();

        if (typeDataMap.isEmpty() && schemaDiffs.isEmpty()) {
            VarInt.writeVLong(dos, 0L);
            return;
        }

        ByteArrayOutputStream schemaDiffsBuffer = new ByteArrayOutputStream();
        DataOutputStream schemaDiffsOut = new DataOutputStream(schemaDiffsBuffer);

        VarInt.writeVInt(schemaDiffsOut, schemaDiffs.size());
        for (Map.Entry<String, HollowSchemaDiff> entry : schemaDiffs.entrySet()) {
            String typeName = entry.getKey();
            byte[] typeNameBytes = typeName.getBytes(StandardCharsets.UTF_8);
            VarInt.writeVInt(schemaDiffsOut, typeNameBytes.length);
            schemaDiffsOut.write(typeNameBytes);
            entry.getValue().writeTo(schemaDiffsOut);
        }

        long schemaDiffsSectionLength = schemaDiffsBuffer.size();

        ByteArrayOutputStream fieldValuesBuffer = new ByteArrayOutputStream();
        DataOutputStream fieldValuesOut = new DataOutputStream(fieldValuesBuffer);

        VarInt.writeVInt(fieldValuesOut, typeDataMap.size());
        for (TypeAppendData typeData : typeDataMap.values()) {
            writeTypeAppendDataToStream(fieldValuesOut, typeData);
        }

        long fieldValuesSectionLength = fieldValuesBuffer.size();

        long totalSize = VarInt.sizeOfVLong(schemaDiffsSectionLength) + schemaDiffsSectionLength + fieldValuesSectionLength;

        VarInt.writeVLong(dos, totalSize);
        VarInt.writeVLong(dos, schemaDiffsSectionLength);
        schemaDiffsBuffer.writeTo(dos);
        fieldValuesBuffer.writeTo(dos);
    }

    private void writeTypeAppendDataToStream(DataOutputStream out, TypeAppendData typeData) throws IOException {
        byte[] typeNameBytes = typeData.typeName.getBytes(StandardCharsets.UTF_8);
        VarInt.writeVInt(out, typeNameBytes.length);
        out.write(typeNameBytes);

        VarInt.writeVInt(out, typeData.fields.size());

        for (FieldAppendData fieldData : typeData.fields) {
            writeFieldAppendDataToStream(out, fieldData);
        }
    }

    private void writeFieldAppendDataToStream(DataOutputStream out, FieldAppendData fieldData) throws IOException {
        byte[] fieldNameBytes = fieldData.fieldName.getBytes(StandardCharsets.UTF_8);
        VarInt.writeVInt(out, fieldNameBytes.length);
        out.write(fieldNameBytes);

        out.writeByte(fieldData.fieldType.ordinal());

        if (fieldData.fieldType == FieldType.REFERENCE) {
            byte[] refTypeBytes = fieldData.referencedType.getBytes(StandardCharsets.UTF_8);
            VarInt.writeVInt(out, refTypeBytes.length);
            out.write(refTypeBytes);
        }

        VarInt.writeVInt(out, fieldData.ordinalToValue.size());

        List<Integer> sortedOrdinals = new ArrayList<>(fieldData.ordinalToValue.keySet());
        Collections.sort(sortedOrdinals);

        for (Integer ordinal : sortedOrdinals) {
            VarInt.writeVInt(out, ordinal);
            Object value = fieldData.ordinalToValue.get(ordinal);
            writeValueToStream(out, fieldData.fieldType, value);
        }
    }

    private void writeValueToStream(DataOutputStream out, FieldType fieldType, Object value) throws IOException {
        switch (fieldType) {
            case REFERENCE:
            case INT:
                VarInt.writeVInt(out, (Integer) value);
                break;
            case LONG:
                VarInt.writeVLong(out, (Long) value);
                break;
            case BOOLEAN:
                Boolean b = (Boolean) value;
                out.writeByte(b ? 1 : 0);
                break;
            case FLOAT:
                // Write as int bits to match reader's Float.intBitsToFloat(in.readInt())
                out.writeInt(Float.floatToIntBits((Float) value));
                break;
            case DOUBLE:
                // Write as long bits to match reader's Double.longBitsToDouble(in.readLong())
                out.writeLong(Double.doubleToLongBits((Double) value));
                break;
            case STRING:
                String s = (String) value;
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                VarInt.writeVInt(out, bytes.length);
                out.write(bytes);
                break;
            case BYTES:
                byte[] byteArray = (byte[]) value;
                VarInt.writeVInt(out, byteArray.length);
                out.write(byteArray);
                break;
        }
    }

}
