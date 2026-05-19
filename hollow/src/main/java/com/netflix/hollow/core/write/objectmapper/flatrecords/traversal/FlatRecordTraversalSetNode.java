package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordOrdinalReader;

import java.util.AbstractSet;
import java.util.Iterator;

public class FlatRecordTraversalSetNode
      extends AbstractSet<FlatRecordTraversalNode>
      implements FlatRecordTraversalNode {

  private final FlatRecordOrdinalReader reader;
  private final HollowSetSchema schema;
  private final int ordinal;
  private final int[] elementOrdinals;

  public FlatRecordTraversalSetNode(FlatRecordOrdinalReader reader, HollowSetSchema schema, int ordinal) {
    this.reader = reader;
    this.ordinal = ordinal;
    this.schema = schema;

    int size = reader.readSize(ordinal);
    elementOrdinals = new int[size];
    reader.readSetElementsInto(ordinal, elementOrdinals);
  }

  @Override
  public HollowSetSchema getSchema() {
    return schema;
  }

  @Override
  public int getOrdinal() {
    return ordinal;
  }

  public Iterator<FlatRecordTraversalObjectNode> objects() {
    return new IteratorImpl<>();
  }

  public Iterator<FlatRecordTraversalListNode> lists() {
    return new IteratorImpl<>();
  }

  public Iterator<FlatRecordTraversalSetNode> sets() {
    return new IteratorImpl<>();
  }

  public Iterator<FlatRecordTraversalMapNode> maps() {
    return new IteratorImpl<>();
  }

  @Override
  public Iterator<FlatRecordTraversalNode> iterator() {
    return new IteratorImpl<>();
  }

  @Override
  public int size() {
    return elementOrdinals.length;
  }

  private class IteratorImpl<T extends FlatRecordTraversalNode> implements Iterator<T> {
    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < elementOrdinals.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T next() {
      int elementOrdinal = elementOrdinals[index++];
      if (elementOrdinal == -1) {
        return null;
      }
      return (T) createNode(reader, elementOrdinal);
    }
  }
}
