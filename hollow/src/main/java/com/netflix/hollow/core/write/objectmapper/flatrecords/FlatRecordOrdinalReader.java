package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;

public class FlatRecordOrdinalReader {
  private final FlatRecord record;
  private final IntList ordinalOffsets = new IntList();

  public FlatRecordOrdinalReader(FlatRecord record) {
    this.record = record;
    populateOrdinalOffset();
  }

  private void populateOrdinalOffset() {
    int offset = record.dataStartByte;
    while (offset < record.dataEndByte) {
      ordinalOffsets.add(offset);
      offset += sizeOfOrdinal(ordinalOffsets.size() - 1);
    }
  }

  private int getOrdinalOffset(int ordinal) {
    return ordinalOffsets.get(ordinal);
  }

  public int getOrdinalCount() {
    return ordinalOffsets.size();
  }

  public HollowSchema readSchema(int ordinal) {
    int schemaId = VarInt.readVInt(record.data, getOrdinalOffset(ordinal));
    return record.schemaIdMapper.getSchema(schemaId);
  }

  public int readSize(int ordinal) {
    int offset = getOrdinalOffset(ordinal);

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    if (schema.getSchemaType() != HollowSchema.SchemaType.LIST &&
        schema.getSchemaType() != HollowSchema.SchemaType.SET &&
        schema.getSchemaType() != HollowSchema.SchemaType.MAP) {
      throw new IllegalArgumentException(String.format("Ordinal %d is not a LIST, SET, or MAP type (found %s)", ordinal, schema.getSchemaType()));
    }

    return VarInt.readVInt(record.data, offset);
  }

  public void readListElementsInto(int ordinal, int[] elements) {
    int offset = getOrdinalOffset(ordinal);

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    if (schema.getSchemaType() != HollowSchema.SchemaType.LIST) {
      throw new IllegalArgumentException(String.format("Ordinal %d is not a LIST type (found %s)", ordinal, schema.getSchemaType()));
    }

    int size = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(size);

    for (int i = 0; i < size; i++) {
      elements[i] = VarInt.readVInt(record.data, offset);
      offset += VarInt.sizeOfVInt(elements[i]);
    }
  }

  public void readSetElementsInto(int ordinal, int[] elements) {
    int offset = getOrdinalOffset(ordinal);

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    if (schema.getSchemaType() != HollowSchema.SchemaType.SET) {
      throw new IllegalArgumentException(String.format("Ordinal %d is not a SET type (found %s)", ordinal, schema.getSchemaType()));
    }

    int size = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(size);

    int elementOrdinal = 0;
    for (int i = 0; i < size; i++) {
      int elementOrdinalDelta = VarInt.readVInt(record.data, offset);
      offset += VarInt.sizeOfVInt(elementOrdinalDelta);
      elementOrdinal += elementOrdinalDelta;
      elements[i] = elementOrdinal;
    }
  }

  public void readMapElementsInto(int ordinal, int[] keys, int[] values) {
    int offset = getOrdinalOffset(ordinal);

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    if (schema.getSchemaType() != HollowSchema.SchemaType.MAP) {
      throw new IllegalArgumentException(String.format("Ordinal %d is not a MAP type (found %s)", ordinal, schema.getSchemaType()));
    }

    int size = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(size);

    int keyOrdinal = 0;
    for (int i = 0; i < size; i++) {
      int keyOrdinalDelta = VarInt.readVInt(record.data, offset);
      offset += VarInt.sizeOfVInt(keyOrdinalDelta);
      keyOrdinal += keyOrdinalDelta;
      keys[i] = keyOrdinal;
      values[i] = VarInt.readVInt(record.data, offset);
      offset += VarInt.sizeOfVInt(values[i]);
    }
  }

  public int readFieldReference(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.REFERENCE, field);
    if (offset == -1) {
      return -1;
    }

    if (VarInt.readVNull(record.data, offset)) {
      return -1;
    }

    return VarInt.readVInt(record.data, offset);
  }

  public Boolean readFieldBoolean(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.BOOLEAN, field);
    if (offset == -1) {
      return null;
    }

    if (VarInt.readVNull(record.data, offset)) {
      return null;
    }

    int value = record.data.get(offset);
    return value == 1 ? Boolean.TRUE : Boolean.FALSE;
  }

  public int readFieldInt(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.INT, field);
    if (offset == -1) {
      return Integer.MIN_VALUE;
    }

    if (VarInt.readVNull(record.data, offset)) {
      return Integer.MIN_VALUE;
    }

    int value = VarInt.readVInt(record.data, offset);
    return ZigZag.decodeInt(value);
  }

  public long readFieldLong(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.LONG, field);
    if (offset == -1) {
      return Long.MIN_VALUE;
    }

    if (VarInt.readVNull(record.data, offset)) {
      return Long.MIN_VALUE;
    }

    long value = VarInt.readVLong(record.data, offset);
    return ZigZag.decodeLong(value);
  }

  public float readFieldFloat(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.FLOAT, field);
    if (offset == -1) {
      return Float.NaN;
    }

    int value = record.data.readIntBits(offset);
    if (value == HollowObjectWriteRecord.NULL_FLOAT_BITS) {
      return Float.NaN;
    }

    return Float.intBitsToFloat(value);
  }

  public double readFieldDouble(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.DOUBLE, field);
    if (offset == -1) {
      return Double.NaN;
    }

    long value = record.data.readLongBits(offset);
    if (value == HollowObjectWriteRecord.NULL_DOUBLE_BITS) {
      return Double.NaN;
    }

    return Double.longBitsToDouble(value);
  }

  public String readFieldString(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.STRING, field);
    if (offset == -1) {
      return null;
    }

    if (VarInt.readVNull(record.data, offset)) {
        return null;
    }

    int length = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(length);

    int cLength = VarInt.countVarIntsInRange(record.data, offset, length);
    char[] s = new char[cLength];
    for (int i = 0; i < cLength; i++) {
      int charValue = VarInt.readVInt(record.data, offset);
      s[i] = (char) charValue;
      offset += VarInt.sizeOfVInt(charValue);
    }

    return new String(s);
  }

  public byte[] readFieldBytes(int ordinal, String field) {
    int offset = skipToField(ordinal, HollowObjectSchema.FieldType.BYTES, field);
    if (offset == -1) {
      return null;
    }

    if (VarInt.readVNull(record.data, offset)) {
        return null;
    }

    int length = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(length);

    byte[] b = new byte[length];
    for (int i = 0; i < length; i++) {
      b[i] = record.data.get(offset++);
    }

    return b;
  }

  private int skipToField(int ordinal, HollowObjectSchema.FieldType fieldType, String field) {
    int offset = getOrdinalOffset(ordinal);

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    if (schema.getSchemaType() != HollowSchema.SchemaType.OBJECT) {
      throw new IllegalArgumentException(String.format("Ordinal %d is not an OBJECT type (found %s)", ordinal, schema.getSchemaType()));
    }
    HollowObjectSchema objectSchema = (HollowObjectSchema) schema;

    int fieldIndex = objectSchema.getPosition(field);
    if (fieldIndex == -1) {
      return -1;
    }

    if (fieldType != objectSchema.getFieldType(fieldIndex)) {
      throw new IllegalArgumentException(String.format("Field %s is not of type %s", field, fieldType));
    }

    for (int i = 0; i < fieldIndex; i++) {
      offset += sizeOfFieldValue(objectSchema.getFieldType(i), offset);
    }

    return offset;
  }

  private int sizeOfOrdinal(int ordinal) {
    int offset = getOrdinalOffset(ordinal);
    int start = offset;

    int schemaId = VarInt.readVInt(record.data, offset);
    offset += VarInt.sizeOfVInt(schemaId);

    HollowSchema schema = record.schemaIdMapper.getSchema(schemaId);
    switch (schema.getSchemaType()) {
      case OBJECT: {
        HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
        for (int i = 0; i < objectSchema.numFields(); i++) {
          offset += sizeOfFieldValue(objectSchema.getFieldType(i), offset);
        }
        break;
      }
      case LIST:
      case SET: {
        int size = VarInt.readVInt(record.data, offset);
        offset += VarInt.sizeOfVInt(size);
        for (int i = 0; i < size; i++) {
          offset += VarInt.nextVLongSize(record.data, offset);
        }
        break;
      }
      case MAP: {
        int size = VarInt.readVInt(record.data, offset);
        offset += VarInt.sizeOfVInt(size);
        for (int i = 0; i < size; i++) {
          offset += VarInt.nextVLongSize(record.data, offset); // key
          offset += VarInt.nextVLongSize(record.data, offset); // value
        }
        break;
      }
    }

    return offset - start;
  }

  private int sizeOfFieldValue(HollowObjectSchema.FieldType fieldType, int offset) {
    switch (fieldType) {
      case INT:
      case LONG:
      case REFERENCE:
        return VarInt.nextVLongSize(record.data, offset);
      case BYTES:
      case STRING:
        if (VarInt.readVNull(record.data, offset)) {
          return 1;
        }
        int fieldLength = VarInt.readVInt(record.data, offset);
        return VarInt.sizeOfVInt(fieldLength) + fieldLength;
      case BOOLEAN:
        return 1;
      case DOUBLE:
        return 8;
      case FLOAT:
        return 4;
      default:
        throw new IllegalArgumentException("Unsupported field type: " + fieldType);
    }
  }
}
