package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;

class Sizing {
  static int sizeOfSchema(HollowSchema schema, FlatRecord record, int offset) {
    int start = offset;
    switch (schema.getSchemaType()) {
      case OBJECT: {
        HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
        for (int i = 0; i < objectSchema.numFields(); i++) {
          offset += sizeOfFieldValue(objectSchema.getFieldType(i), record, offset);
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

  static int sizeOfFieldValue(HollowObjectSchema.FieldType fieldType, FlatRecord record, int offset) {
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
