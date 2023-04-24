package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;


public class FlatRecordReader {
  private final FlatRecord record;

  private int pointer;

  public FlatRecordReader(FlatRecord record) {
    this.record = record;
    this.pointer = record.dataStartByte;
  }

  public boolean hasMore() {
    return pointer < record.dataEndByte;
  }

  public HollowSchema readSchema() {
    int schemaId = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(schemaId);
    return record.schemaIdMapper.getSchema(schemaId);
  }

  public int readCollectionSize() {
    int size = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(size);
    return size;
  }

  public int readOrdinal() {
    if (VarInt.readVNull(record.data, this.pointer)) {
      this.pointer += 1;
      return -1;
    }

    int value = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(value);

    return value;
  }

  public Boolean readBoolean() {
    if(VarInt.readVNull(record.data, this.pointer)) {
      this.pointer += 1;
      return null;
    }

    int value = record.data.get(this.pointer);
    this.pointer += 1;

    return value == 1 ? Boolean.TRUE : Boolean.FALSE;
  }

  public int readInt() {
    if (VarInt.readVNull(record.data, this.pointer)) {
      this.pointer += 1;
      return Integer.MIN_VALUE;
    }

    int value = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(value);

    return ZigZag.decodeInt(value);
  }

  public long readLong() {
    if (VarInt.readVNull(record.data, this.pointer)) {
      this.pointer += 1;
      return Long.MIN_VALUE;
    }

    long value = VarInt.readVLong(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVLong(value);
    return ZigZag.decodeLong(value);
  }

  public float readFloat() {
    int value = record.data.readIntBits(this.pointer);
    this.pointer += 4;
    if (value == HollowObjectWriteRecord.NULL_FLOAT_BITS) {
      return Float.NaN;
    }
    return Float.intBitsToFloat(value);
  }

  public double readDouble() {
    long value = record.data.readLongBits(this.pointer);
    this.pointer += 8;
    if (value == HollowObjectWriteRecord.NULL_DOUBLE_BITS) {
      return Double.NaN;
    }
    return Double.longBitsToDouble(value);
  }

  public String readString() {
    if (VarInt.readVNull(record.data, this.pointer)) {
        this.pointer += 1;
        return null;
    }

    int length = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(length);

    int cLength = VarInt.countVarIntsInRange(record.data, this.pointer, length);
    char[] s = new char[cLength];

    for(int i=0;i<cLength;i++) {
      int charValue = VarInt.readVInt(record.data, this.pointer);
      s[i] = (char)charValue;
      this.pointer += VarInt.sizeOfVInt(charValue);
    }

    return new String(s);
  }

  public byte[] readBytes() {
    if (VarInt.readVNull(record.data, this.pointer)) {
        this.pointer += 1;
        return null;
    }

    int length = VarInt.readVInt(record.data, this.pointer);
    this.pointer += VarInt.sizeOfVInt(length);
    byte[] b = new byte[length];

    for(int i=0;i<length;i++) {
      b[i] = record.data.get(this.pointer++);
    }

    return b;
  }

  public void skipSchema(HollowSchema schema) {
    switch (schema.getSchemaType()) {
      case OBJECT: {
        HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
        int numFields = objectSchema.numFields();
        for (int i = 0; i < numFields; i++) {
          skipField(objectSchema.getFieldType(i));
        }
        break;
      }
      case LIST:
      case SET: {
        int numElements = readCollectionSize();
        for (int i = 0; i < numElements; i++) {
          readOrdinal();
        }
        break;
      }
      case MAP: {
        int numElements = readCollectionSize();
        for (int i = 0; i < numElements; i++) {
          readOrdinal(); // key
          readOrdinal(); // value
        }
        break;
      }
    }
  }

  public void skipField(HollowObjectSchema.FieldType fieldType) {
    switch(fieldType) {
      case BOOLEAN:
        readBoolean();
        break;
      case BYTES:
        readBytes();
        break;
      case DOUBLE:
        readDouble();
        break;
      case FLOAT:
        readFloat();
        break;
      case INT:
        readInt();
        break;
      case LONG:
        readLong();
        break;
      case REFERENCE:
        readOrdinal();
        break;
      case STRING:
        readString();
        break;
    }
  }
}
