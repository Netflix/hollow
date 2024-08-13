package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

import java.util.AbstractSet;
import java.util.Iterator;

public class FlatRecordTraversalSetNode extends AbstractSet<FlatRecordTraversalNode> implements FlatRecordTraversalNode {
  private FlatRecordReader reader;
  private IntList ordinalPositions;
  private HollowSetSchema schema;
  private int[] elementOrdinals;

  @Override
  public void reposition(FlatRecordReader reader, IntList ordinalPositions, int ordinal) {
    this.reader = reader;
    this.ordinalPositions = ordinalPositions;

    reader.resetTo(ordinalPositions.get(ordinal));
    schema = (HollowSetSchema) reader.readSchema();

    int size = reader.readCollectionSize();
    elementOrdinals = new int[size];
    int elementOrdinal = 0;
    for (int i = 0; i < size; i++) {
      elementOrdinal += reader.readOrdinal();
      elementOrdinals[i] = elementOrdinal;
    }
  }

  @Override
  public HollowSetSchema getSchema() {
    return schema;
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
    public T next() {
      int elementOrdinal = elementOrdinals[index++];
      if (elementOrdinal == -1) {
        return null;
      }
      return (T) createAndRepositionNode(reader, ordinalPositions, elementOrdinal);
    }
  }
}
