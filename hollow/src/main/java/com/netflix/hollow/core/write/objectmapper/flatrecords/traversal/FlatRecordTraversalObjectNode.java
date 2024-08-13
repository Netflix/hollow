package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

public class FlatRecordTraversalObjectNode implements FlatRecordTraversalNode {
  private FlatRecordReader reader;
  private IntList ordinalPositions;
  private HollowObjectSchema schema;
  private int position;

  public FlatRecordTraversalObjectNode() {}

  public FlatRecordTraversalObjectNode(FlatRecord rec) {
    FlatRecordReader reader = new FlatRecordReader(rec);

    IntList ordinalPositions = new IntList();
    while (reader.hasMore()) {
      ordinalPositions.add(reader.pointer);
      HollowSchema schema = reader.readSchema();
      reader.skipSchema(schema);
    }

    reposition(reader, ordinalPositions, ordinalPositions.size() - 1);
  }

  @Override
  public void reposition(FlatRecordReader reader, IntList ordinalPositions, int ordinal) {
    this.reader = reader;
    this.ordinalPositions = ordinalPositions;

    reader.resetTo(ordinalPositions.get(ordinal));
    schema = (HollowObjectSchema) reader.readSchema();
    position = reader.pointer;
  }

  @Override
  public HollowObjectSchema getSchema() {
    return schema;
  }

  public FlatRecordTraversalObjectNode getObjectFieldNode(String field) {
    return (FlatRecordTraversalObjectNode) getFieldNode(field);
  }

  public FlatRecordTraversalListNode getListFieldNode(String field) {
    return (FlatRecordTraversalListNode) getFieldNode(field);
  }

  public FlatRecordTraversalSetNode getSetFieldNode(String field) {
    return (FlatRecordTraversalSetNode) getFieldNode(field);
  }

  public FlatRecordTraversalMapNode getMapFieldNode(String field) {
    return (FlatRecordTraversalMapNode) getFieldNode(field);
  }

  public FlatRecordTraversalNode getFieldNode(String field) {
    if (!skipToField(field)) {
      return null;
    }

    if (schema.getFieldType(field) != HollowObjectSchema.FieldType.REFERENCE) {
      throw new IllegalStateException("Cannot get child for non-reference field");
    }

    int refOrdinal = reader.readOrdinal();
    if (refOrdinal == -1) {
      return null;
    }

    return createAndRepositionNode(reader, ordinalPositions, refOrdinal);
  }

  public Object getFieldValue(String field) {
    if (!skipToField(field)) {
      return null;
    }
    switch(schema.getFieldType(field)) {
      case BOOLEAN:
        return reader.readBoolean();
      case INT:
        return reader.readInt();
      case LONG:
        return reader.readLong();
      case FLOAT:
        return reader.readFloat();
      case DOUBLE:
        return reader.readDouble();
      case STRING:
        return reader.readString();
      case BYTES:
        return reader.readBytes();
      case REFERENCE:
        throw new IllegalStateException("Cannot get leaf value for reference field");
    }
    return null;
  }

  public boolean getFieldValueBoolean(String field) {
    if (!skipToField(field)) {
      return false;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.BOOLEAN);
    return reader.readBoolean();
  }

  public Boolean getFieldValueBooleanBoxed(String field) {
    return getFieldValueBoolean(field);
  }

  public int getFieldValueInt(String field) {
    if (!skipToField(field)) {
      return Integer.MIN_VALUE;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.INT);
    return reader.readInt();
  }

  public Integer getFieldValueIntBoxed(String field) {
    int value = getFieldValueInt(field);
    if (value == Integer.MIN_VALUE) {
      return null;
    }
    return value;
  }

  public long getFieldValueLong(String field) {
    if (!skipToField(field)) {
      return Long.MIN_VALUE;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.LONG);
    return reader.readLong();
  }

  public Long getFieldValueLongBoxed(String field) {
    long value = getFieldValueLong(field);
    if (value == Long.MIN_VALUE) {
      return null;
    }
    return value;
  }

  public float getFieldValueFloat(String field) {
    if (!skipToField(field)) {
      return Float.NaN;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.FLOAT);
    return reader.readFloat();
  }

  public Float getFieldValueFloatBoxed(String field) {
    float value = getFieldValueFloat(field);
    if (Float.isNaN(value)) {
      return null;
    }
    return value;
  }

  public double getFieldValueDouble(String field) {
    if (!skipToField(field)) {
      return Double.NaN;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.DOUBLE);
    return reader.readDouble();
  }

  public Double getFieldValueDoubleBoxed(String field) {
    double value = getFieldValueDouble(field);
    if (Double.isNaN(value)) {
      return null;
    }
    return value;
  }

  public String getFieldValueString(String field) {
    if (!skipToField(field)) {
      return null;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.STRING);
    return reader.readString();
  }

  public byte[] getFieldValueBytes(String field) {
    if (!skipToField(field)) {
      return null;
    }
    assertFieldType(field, HollowObjectSchema.FieldType.BYTES);
    return reader.readBytes();
  }

  private void assertFieldType(String fieldName, HollowObjectSchema.FieldType expectedFieldType) {
    int fieldPosition = schema.getPosition(fieldName);
    assertFieldType(fieldPosition, expectedFieldType);
  }

  private void assertFieldType(int fieldPosition, HollowObjectSchema.FieldType expectedFieldType) {
    HollowObjectSchema.FieldType fieldType = schema.getFieldType(fieldPosition);
    if (fieldType != expectedFieldType) {
      throw new IllegalStateException("Field " + schema.getFieldName(fieldPosition) + " is not of type " + expectedFieldType);
    }
  }

  private boolean skipToField(String fieldName) {
    return skipToField(schema.getPosition(fieldName));
  }

  /** Skip to the field at the given position.
   *
   * @param fieldPosition the position of the field to skip to
   * @return true if the field was found and skipped to, false otherwise
   */
  private boolean skipToField(int fieldPosition) {
    if (fieldPosition == -1) {
      return false;
    }
    reader.resetTo(position);
    for (int i = 0; i < fieldPosition; i++) {
      reader.skipField(schema.getFieldType(i));
    }
    return true;
  }
}
