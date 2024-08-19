package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

import java.util.AbstractList;
import java.util.Map;

public class FlatRecordTraversalListNode extends AbstractList<FlatRecordTraversalNode> implements FlatRecordTraversalNode {
  private FlatRecordReader reader;
  private IntList ordinalPositions;
  private HollowListSchema schema;
  private int[] elementOrdinals;
  private Map<String, HollowObjectSchema> commonSchemaMap;

  @Override
  public void reposition(FlatRecordReader reader, IntList ordinalPositions, int ordinal) {
    this.reader = reader;
    this.ordinalPositions = ordinalPositions;

    reader.resetTo(ordinalPositions.get(ordinal));
    schema = (HollowListSchema) reader.readSchema();

    int size = reader.readCollectionSize();
    elementOrdinals = new int[size];
    for (int i = 0; i < size; i++) {
      elementOrdinals[i] = reader.readOrdinal();
    }
  }

  @Override
  public void setCommonSchema(Map<String, HollowObjectSchema> commonSchema) {
    this.commonSchemaMap = commonSchema;
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


  @Override
  public HollowListSchema getSchema() {
    return schema;
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
    return createAndRepositionNode(reader, ordinalPositions, elementOrdinal);
  }

  @Override
  public int size() {
    return elementOrdinals.length;
  }
}
