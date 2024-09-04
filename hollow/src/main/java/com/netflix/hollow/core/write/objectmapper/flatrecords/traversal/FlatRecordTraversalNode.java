package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordOrdinalReader;

import java.util.Map;

/**
 * An abstraction that allows for the traversal of a flat record from the root type to a specific sub-path.
 */
public interface FlatRecordTraversalNode {
  HollowSchema getSchema();

  void setCommonSchema(Map<String, HollowObjectSchema> commonSchema);

  default FlatRecordTraversalNode createNode(FlatRecordOrdinalReader reader, int ordinal) {
    HollowSchema schema = reader.readSchema(ordinal);
    switch (schema.getSchemaType()) {
      case OBJECT:
        return new FlatRecordTraversalObjectNode(reader, (HollowObjectSchema) schema, ordinal);
      case LIST:
        return new FlatRecordTraversalListNode(reader, (HollowListSchema) schema, ordinal);
      case SET:
        return new FlatRecordTraversalSetNode(reader, (HollowSetSchema) schema, ordinal);
      case MAP:
        return new FlatRecordTraversalMapNode(reader, (HollowMapSchema) schema, ordinal);
      default:
        throw new IllegalArgumentException("Unsupported schema type: " + schema.getSchemaType());
    }
  }
}