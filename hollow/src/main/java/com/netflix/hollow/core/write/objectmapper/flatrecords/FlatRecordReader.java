package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;

/**
 * A sequential, single-pass reader for accessing data within a {@link FlatRecord}.
 *
 * <p>This reader maintains an internal pointer that advances as data is read,
 * allowing efficient streaming through the record in a single pass. The caller
 * is responsible for reading fields in the correct order as defined by the schema.
 *
 * <p><b>Trade-offs:</b>
 * <ul>
 *   <li>Not thread-safe—the internal pointer is mutable shared state</li>
 *   <li>Must read data sequentially; random access requires manual repositioning
 *       via {@link #resetTo(int)}</li>
 * </ul>
 *
 * <p>Use this reader when processing records sequentially in a single thread
 * and optimal single-pass performance is desired.
 *
 * @see FlatRecordOrdinalReader for a thread-safe random-access reader
 */
public class FlatRecordReader {
  private final FlatRecord record;

  public int pointer;

  public FlatRecordReader(FlatRecord record) {
    this.record = record;
    this.pointer = record.dataStartByte;
  }

  public void reset() {
    this.pointer = record.dataStartByte;
  }

  public void resetTo(int position) {
    this.pointer = position;
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
    int size = Sizing.sizeOfSchema(schema, record, pointer);
    this.pointer += size;
  }

  public void skipField(HollowObjectSchema.FieldType fieldType) {
    int size = Sizing.sizeOfFieldValue(fieldType, record, pointer);
    this.pointer += size;
  }
}
