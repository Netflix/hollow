package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordOrdinalReader;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FlatRecordTraversalObjectNode implements FlatRecordTraversalNode {
  private final FlatRecordOrdinalReader reader;
  private final HollowObjectSchema schema;
  private final int ordinal;

  private Map<String, HollowObjectSchema> commonSchemaMap;

  public FlatRecordTraversalObjectNode(FlatRecordOrdinalReader reader, HollowObjectSchema schema, int ordinal) {
    this.reader = reader;
    this.ordinal = ordinal;
    this.schema = schema;
  }

  public FlatRecordTraversalObjectNode(FlatRecord rec) {
    this.reader = new FlatRecordOrdinalReader(rec);
    this.ordinal = reader.getOrdinalCount() - 1;
    this.schema = (HollowObjectSchema) reader.readSchema(ordinal);
  }

  @Override
  public HollowObjectSchema getSchema() {
    return schema;
  }

  @Override
  public void setCommonSchema(Map<String, HollowObjectSchema> commonSchema) {
    this.commonSchemaMap = commonSchema;
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
    HollowObjectSchema.FieldType fieldType = schema.getFieldType(field);
    if (fieldType == null) {
      return null;
    }

    if (fieldType != HollowObjectSchema.FieldType.REFERENCE) {
      throw new IllegalArgumentException("Cannot get child for non-reference field");
    }

    int refOrdinal = reader.readFieldReference(ordinal, field);
    if (refOrdinal == -1) {
      return null;
    }

    return createNode(reader, refOrdinal);
  }

  public Object getFieldValue(String field) {
    HollowObjectSchema.FieldType fieldType = schema.getFieldType(field);
    if (fieldType == null) {
      return null;
    }

    switch (fieldType) {
      case BOOLEAN:
        return reader.readFieldBoolean(ordinal, field);
      case INT:
        return reader.readFieldInt(ordinal, field);
      case LONG:
        return reader.readFieldLong(ordinal, field);
      case FLOAT:
        return reader.readFieldFloat(ordinal, field);
      case DOUBLE:
        return reader.readFieldDouble(ordinal, field);
      case STRING:
        return reader.readFieldString(ordinal, field);
      case BYTES:
        return reader.readFieldBytes(ordinal, field);
      case REFERENCE:
        throw new IllegalArgumentException("Cannot get leaf value for reference field");
    }
    return null;
  }

  public boolean getFieldValueBoolean(String field) {
    Boolean b = reader.readFieldBoolean(ordinal, field);
    return Boolean.TRUE.equals(b);
  }

  public Boolean getFieldValueBooleanBoxed(String field) {
    return getFieldValueBoolean(field);
  }

  public int getFieldValueInt(String field) {
    return reader.readFieldInt(ordinal, field);
  }

  public Integer getFieldValueIntBoxed(String field) {
    int value = getFieldValueInt(field);
    if (value == Integer.MIN_VALUE) {
      return null;
    }
    return value;
  }

  public long getFieldValueLong(String field) {
    return reader.readFieldLong(ordinal, field);
  }

  public Long getFieldValueLongBoxed(String field) {
    long value = getFieldValueLong(field);
    if (value == Long.MIN_VALUE) {
      return null;
    }
    return value;
  }

  public float getFieldValueFloat(String field) {
    return reader.readFieldFloat(ordinal, field);
  }

  public Float getFieldValueFloatBoxed(String field) {
    float value = getFieldValueFloat(field);
    if (Float.isNaN(value)) {
      return null;
    }
    return value;
  }

  public double getFieldValueDouble(String field) {
    return reader.readFieldDouble(ordinal, field);
  }

  public Double getFieldValueDoubleBoxed(String field) {
    double value = getFieldValueDouble(field);
    if (Double.isNaN(value)) {
      return null;
    }
    return value;
  }

  public String getFieldValueString(String field) {
    return reader.readFieldString(ordinal, field);
  }

  public byte[] getFieldValueBytes(String field) {
    return reader.readFieldBytes(ordinal, field);
  }

  @Override
  public int hashCode() {
    HollowObjectSchema commonSchema = commonSchemaMap.get(schema.getName());
    Object[] fields = new Object[commonSchema.numFields()];
    for (int i = 0; i < commonSchema.numFields(); i++) {
      String fieldName = commonSchema.getFieldName(i);
      if (commonSchema.getFieldType(fieldName) != HollowObjectSchema.FieldType.REFERENCE) {
        fields[i] = getFieldValue(fieldName);
      }
    }
    return Objects.hash(schema.getName(), Arrays.deepHashCode(fields));
  }
}
