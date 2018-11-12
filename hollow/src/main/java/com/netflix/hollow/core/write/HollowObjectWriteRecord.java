/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class HollowObjectWriteRecord implements HollowWriteRecord {

    private final HollowObjectSchema schema;

    private final ByteDataBuffer fieldData[];
    private final boolean isNonNull[];

    public HollowObjectWriteRecord(HollowObjectSchema schema) {
        this.schema = schema;
        this.fieldData = new ByteDataBuffer[schema.numFields()];
        this.isNonNull = new boolean[schema.numFields()];
        for (int i = 0; i < fieldData.length; i++) {
            fieldData[i] = new ByteDataBuffer(WastefulRecycler.SMALL_ARRAY_RECYCLER);
        }
    }

    public HollowObjectSchema getSchema() {
        return schema;
    }

    /**
     * Concatenates all fields, in order, to the ByteDataBuffer supplied.  This concatenation is the
     * verbatim serialized representation in the FastBlob.
     *
     * @param buf the data buffer to write data to
     */
    public void writeDataTo(ByteDataBuffer buf) {
        for (int i = 0; i < fieldData.length; i++) {
            writeField(buf, i);
        }
    }

    public void writeDataTo(ByteDataBuffer buf, HollowObjectSchema translate) {
        for(int i=0; i < translate.numFields(); i++) {
            int fieldIndex = schema.getPosition(translate.getFieldName(i));

            if(fieldIndex != -1) {
                writeField(buf, fieldIndex);
            } else {
                writeNull(buf, translate.getFieldType(i));
            }
        }
    }

    private void writeField(ByteDataBuffer buf, int fieldIndex) {
        if (isNonNull[fieldIndex]) {
            if (getSchema().getFieldType(fieldIndex).isVariableLength())
                VarInt.writeVInt(buf, (int)fieldData[fieldIndex].length());
            fieldData[fieldIndex].copyTo(buf);
        } else {
            writeNull(buf, schema.getFieldType(fieldIndex));
        }
    }

    /**
     * Reset the ByteDataBuffers for each field.
     */
    public void reset() {
        for (int i = 0; i < fieldData.length; i++) {
            isNonNull[i] = false;
        }
    }

    public void setNull(String fieldName) {
        int fieldIndex = getSchema().getPosition(fieldName);

        ByteDataBuffer fieldBuffer = getFieldBuffer(fieldIndex);
        FieldType fieldType = getSchema().getFieldType(fieldIndex);

        writeNull(fieldBuffer, fieldType);
    }

    public void setInt(String fieldName, int value) {
        if(value == Integer.MIN_VALUE) {
            setNull(fieldName);
        } else {
            int fieldIndex = getSchema().getPosition(fieldName);

            validateFieldType(fieldIndex, fieldName, FieldType.INT);

            ByteDataBuffer buf = getFieldBuffer(fieldIndex);

            // zig zag encoding
            VarInt.writeVInt(buf, ZigZag.encodeInt(value));
        }
    }

    public void setLong(String fieldName, long value) {
        if(value == Long.MIN_VALUE) {
            setNull(fieldName);
        } else {
            int fieldIndex = getSchema().getPosition(fieldName);

            validateFieldType(fieldIndex, fieldName, FieldType.LONG);

            ByteDataBuffer buf = getFieldBuffer(fieldIndex);

            // zig zag encoding
            VarInt.writeVLong(buf, ZigZag.encodeLong(value));
        }
    }

    public void setFloat(String fieldName, float value) {
        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.FLOAT);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        int intBits = Float.floatToIntBits(value);
        writeFixedLengthInt(buf, intBits);
    }

    public void setDouble(String fieldName, double value) {
        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.DOUBLE);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        long longBits = Double.doubleToLongBits(value);
        writeFixedLengthLong(buf, longBits);
    }

    public void setBoolean(String fieldName, boolean value) {
        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.BOOLEAN);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        buf.write(value ? (byte) 1 : (byte) 0);
    }

    public void setBytes(String fieldName, byte[] value) {
        if(value == null)  return;

        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.BYTES);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        for (int i = 0; i < value.length; i++) {
            buf.write(value[i]);
        }
    }

    public void setString(String fieldName, String value) {
        if(value == null)  return;

        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.STRING);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        for(int i=0;i<value.length();i++) {
            VarInt.writeVInt(buf, value.charAt(i));
        }
    }

    public void setReference(String fieldName, int ordinal) {
        int fieldIndex = getSchema().getPosition(fieldName);

        validateFieldType(fieldIndex, fieldName, FieldType.REFERENCE);

        ByteDataBuffer buf = getFieldBuffer(fieldIndex);

        VarInt.writeVInt(buf, ordinal);
    }

    private void writeNull(ByteDataBuffer buf, FieldType fieldType) {
        if(fieldType == FieldType.FLOAT) {
            writeNullFloat(buf);
        } else if(fieldType == FieldType.DOUBLE) {
            writeNullDouble(buf);
        } else {
            VarInt.writeVNull(buf);
        }
    }

    /**
     * Returns the buffer which should be used to serialize the data for the field at the given position in the schema.<p>
     *
     * This is used by the FastBlobFrameworkSerializer when writing the data for a specific field.
     *
     * @param field
     * @return
     */
    private ByteDataBuffer getFieldBuffer(int fieldPosition) {
        isNonNull[fieldPosition] = true;
        fieldData[fieldPosition].reset();
        return fieldData[fieldPosition];
    }

    public static final int NULL_FLOAT_BITS = Float.floatToIntBits(Float.NaN) + 1;
    public static final long NULL_DOUBLE_BITS = Double.doubleToLongBits(Double.NaN) + 1;

    /**
     * Serialize a special 4-byte long sequence indicating a null Float value.
     */
    private static void writeNullFloat(final ByteDataBuffer fieldBuffer) {
        writeFixedLengthInt(fieldBuffer, NULL_FLOAT_BITS);
    }

    /**
     * Write 4 consecutive bytes
     */
    private static void writeFixedLengthInt(ByteDataBuffer fieldBuffer, int intBits) {
        fieldBuffer.write((byte) (intBits >>> 24));
        fieldBuffer.write((byte) (intBits >>> 16));
        fieldBuffer.write((byte) (intBits >>> 8));
        fieldBuffer.write((byte) (intBits));
    }

    /**
     * Serialize a special 8-byte long sequence indicating a null Double value.
     */
    private static void writeNullDouble(ByteDataBuffer fieldBuffer) {
        writeFixedLengthLong(fieldBuffer, NULL_DOUBLE_BITS);
    }

    /**
     * Write 8 consecutive bytes
     */
    private static void writeFixedLengthLong(ByteDataBuffer fieldBuffer, long intBits) {
        fieldBuffer.write((byte) (intBits >>> 56));
        fieldBuffer.write((byte) (intBits >>> 48));
        fieldBuffer.write((byte) (intBits >>> 40));
        fieldBuffer.write((byte) (intBits >>> 32));
        fieldBuffer.write((byte) (intBits >>> 24));
        fieldBuffer.write((byte) (intBits >>> 16));
        fieldBuffer.write((byte) (intBits >>> 8));
        fieldBuffer.write((byte) (intBits));
    }

    private void validateFieldType(int fieldIndex, String fieldName, FieldType attemptedFieldType) {
        if(getSchema().getFieldType(fieldIndex) != attemptedFieldType) {
            throw new IllegalArgumentException("Attempting to serialize " + attemptedFieldType + " in field " + fieldName + ".  Carefully check your schema for type " + getSchema().getName() + ".");
        }
    }
}
