package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlatRecordTraversalSetNode extends AbstractSet<FlatRecordTraversalNode> implements FlatRecordTraversalNode {
  private FlatRecordReader reader;
  private IntList ordinalPositions;
  private HollowSetSchema schema;
  private int[] elementOrdinals;

  private Map<String, HollowObjectSchema> commonSchemaMap;
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
  public void setCommonSchema(Map<String, HollowObjectSchema> commonSchema) {
    this.commonSchemaMap = commonSchema;
  }
  @Override
  public HollowSetSchema getSchema() {
    return schema;
  }

  @Override
  public int hashCode() {
    int h = 0;
    Iterator<FlatRecordTraversalNode> i = iterator();
    while (i.hasNext()) {
      FlatRecordTraversalNode obj = i.next();
      if (obj != null && commonSchemaMap.containsKey(obj.getSchema().getName()))
        obj.setCommonSchema(commonSchemaMap);
        h += obj.hashCode();
    }
    return h;
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
