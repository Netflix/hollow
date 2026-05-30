package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordOrdinalReader;

import java.util.AbstractList;

public class FlatRecordTraversalListNode
      extends AbstractList<FlatRecordTraversalNode>
      implements FlatRecordTraversalNode {

  private final FlatRecordOrdinalReader reader;
  private final HollowListSchema schema;
  private final int ordinal;
  private final int[] elementOrdinals;

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
}
