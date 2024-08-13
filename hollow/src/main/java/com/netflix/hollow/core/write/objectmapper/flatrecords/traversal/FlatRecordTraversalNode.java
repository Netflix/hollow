package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

/**
 * An abstraction that allows for the traversal of a flat record from the root type to a specific sub-path.
 */
public interface FlatRecordTraversalNode {
  HollowSchema getSchema();

  void reposition(FlatRecordReader reader, IntList ordinalPositions, int ordinal);

  default FlatRecordTraversalNode createAndRepositionNode(FlatRecordReader reader, IntList ordinalPositions, int ordinal) {
    reader.pointer = ordinalPositions.get(ordinal);
    HollowSchema schema = reader.readSchema();

    FlatRecordTraversalNode node;
    switch (schema.getSchemaType()) {
      case OBJECT:
        node = new FlatRecordTraversalObjectNode();
        break;
      case LIST:
        node = new FlatRecordTraversalListNode();
        break;
      case SET:
        node = new FlatRecordTraversalSetNode();
        break;
      case MAP:
        node = new FlatRecordTraversalMapNode();
        break;
      default:
        throw new IllegalArgumentException("Unsupported schema type: " + schema.getSchemaType());
    }

    node.reposition(reader, ordinalPositions, ordinal);
    return node;
  }
}