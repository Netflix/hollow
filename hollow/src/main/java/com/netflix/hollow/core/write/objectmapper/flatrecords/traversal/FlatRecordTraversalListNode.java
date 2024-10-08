package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordOrdinalReader;

import java.util.AbstractList;
import java.util.Map;

public class FlatRecordTraversalListNode extends AbstractList<FlatRecordTraversalNode> implements FlatRecordTraversalNode {
  private final FlatRecordOrdinalReader reader;
  private final HollowListSchema schema;
  private final int ordinal;
  private final int[] elementOrdinals;

  private Map<String, HollowObjectSchema> commonSchemaMap;

  public FlatRecordTraversalListNode(FlatRecordOrdinalReader reader, HollowListSchema schema, int ordinal) {
    this.reader = reader;
    this.ordinal = ordinal;
    this.schema = schema;

    int size = reader.readSize(ordinal);
    elementOrdinals = new int[size];
    reader.readListElementsInto(ordinal, elementOrdinals);
  }

  @Override
  public HollowListSchema getSchema() {
    return schema;
  }

  @Override
  public int getOrdinal() {
    return ordinal;
  }

  @Override
  public FlatRecordOrdinalReader getReader() {
    return reader;
  }

  @Override
  public void setCommonSchema(Map<String, HollowObjectSchema> commonSchema) {
    this.commonSchemaMap = commonSchema;
  }

  public FlatRecordTraversalObjectNode getObject(int index) {
    return (FlatRecordTraversalObjectNode) get(index);
  }

  public FlatRecordTraversalListNode getList(int index) {
    return (FlatRecordTraversalListNode) get(index);
  }

  public FlatRecordTraversalSetNode getSet(int index) {
    return (FlatRecordTraversalSetNode) get(index);
  }

  public FlatRecordTraversalMapNode getMap(int index) {
    return (FlatRecordTraversalMapNode) get(index);
  }

  @Override
  public FlatRecordTraversalNode get(int index) {
    if (index >= elementOrdinals.length) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elementOrdinals.length);
    }
    int elementOrdinal = elementOrdinals[index];
    if (elementOrdinal == -1) {
      return null;
    }
    return createNode(reader, elementOrdinal);
  }

  @Override
  public int size() {
    return elementOrdinals.length;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (FlatRecordTraversalNode e : this) {
      FlatRecordTraversalObjectNode objectNode = (FlatRecordTraversalObjectNode) e;
      if (objectNode != null && commonSchemaMap.containsKey(objectNode.getSchema().getName())) {
        objectNode.setCommonSchema(commonSchemaMap);
        hashCode = 31 * hashCode + objectNode.hashCode();
      }
      else if (objectNode == null) {
        hashCode = 31 * hashCode;
      }
    }
    return hashCode;
  }
}
