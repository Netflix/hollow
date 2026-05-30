package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalListNode;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalMapNode;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalSetNode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlatRecordStringifier {
  private static final String NEWLINE = "\n";
  private static final String INDENT = "  ";

  private final Set<String> excludeObjectTypes = new HashSet<>();

  public FlatRecordStringifier addExcludeObjectTypes(String... types) {
    this.excludeObjectTypes.addAll(Arrays.asList(types));
    return this;
  }

  public String stringify(FlatRecord record) {
    FlatRecordTraversalObjectNode root = new FlatRecordTraversalObjectNode(record);
    return stringify(root);
  }

  public String stringify(FlatRecordTraversalNode node) {
    try {
      StringWriter writer = new StringWriter();
      stringify(writer, node);
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void stringify(Writer writer, FlatRecordTraversalNode node) throws IOException {
    appendStringify(writer, 0, node);
  }

  private void appendStringify(Writer writer, int indentation, FlatRecordTraversalNode node) throws IOException {
    if (node == null) {
      writer.append("null");
      return;
    }

    HollowSchema schema = node.getSchema();
    if (excludeObjectTypes.contains(schema.getName())) {
      writer.append("null");
      return;
    }

    switch (schema.getSchemaType()) {
      case OBJECT:
        appendObjectStringify(writer, indentation, (FlatRecordTraversalObjectNode) node);
        break;
      case LIST:
        appendListStringify(writer, indentation, (FlatRecordTraversalListNode) node);
        break;
      case SET:
        appendSetStringify(writer, indentation, (FlatRecordTraversalSetNode) node);
        break;
      case MAP:
        appendMapStringify(writer, indentation, (FlatRecordTraversalMapNode) node);
        break;
      default:
        throw new IllegalArgumentException("Unsupported schema type: " + schema.getSchemaType());
    }
  }

  private void appendMapStringify(Writer writer, int indentation, FlatRecordTraversalMapNode node) throws IOException {
    indentation++;
    for (Map.Entry<FlatRecordTraversalNode, FlatRecordTraversalNode> entry : node.entrySet()) {
      writer.append(NEWLINE);
      appendIndentation(writer, indentation);
      writer.append("k: ");
      appendStringify(writer, indentation, entry.getKey());
      writer.append(NEWLINE);
      appendIndentation(writer, indentation);
      writer.append("v: ");
      appendStringify(writer, indentation, entry.getValue());
    }
  }

  private void appendSetStringify(Writer writer, int indentation, FlatRecordTraversalSetNode node) throws IOException {
    indentation++;
    for (FlatRecordTraversalNode elementNode : node) {
      writer.append(NEWLINE);
      appendIndentation(writer, indentation);
      writer.append("e: ");
      appendStringify(writer, indentation, elementNode);
    }
  }

  private void appendListStringify(Writer writer, int indentation, FlatRecordTraversalListNode node) throws IOException {
    indentation++;

    int i = 0;
    for (FlatRecordTraversalNode elementNode : node) {
      writer.append(NEWLINE);
      appendIndentation(writer, indentation);
      writer.append("e").append(String.valueOf(i)).append(": ");
      appendStringify(writer, indentation, elementNode);
      i++;
    }
  }

  private void appendObjectStringify(Writer writer, int indentation, FlatRecordTraversalObjectNode node) throws IOException {
    HollowObjectSchema schema = node.getSchema();

    if(schema.numFields() == 1) {
      appendFieldStringify(writer, indentation, node, schema.getFieldName(0));
    } else {
      indentation++;

      for (int i = 0; i < schema.numFields(); i++) {
        writer.append(NEWLINE);

        String fieldName = schema.getFieldName(i);
        appendIndentation(writer, indentation);
        writer.append(fieldName).append(": ");

        appendFieldStringify(writer, indentation, node, fieldName);
      }
    }
  }

  private void appendFieldStringify(Writer writer, int indentation, FlatRecordTraversalObjectNode node, String fieldName) throws IOException {
    if (node.isFieldNull(fieldName)) {
      writer.append("null");
      return;
    }

    HollowObjectSchema schema = node.getSchema();
    switch (schema.getFieldType(fieldName)) {
      case BOOLEAN:
        boolean boolValue = node.getFieldValueBoolean(fieldName);
        writer.append(Boolean.toString(boolValue));
        break;
      case BYTES:
        writer.append(Arrays.toString(node.getFieldValueBytes(fieldName)));
        break;
      case DOUBLE:
        double doubleValue = node.getFieldValueDouble(fieldName);
        writer.append(Double.toString(doubleValue));
        break;
      case FLOAT:
        float floatValue = node.getFieldValueFloat(fieldName);
        writer.append(Float.toString(floatValue));
        break;
      case INT:
        int intValue = node.getFieldValueInt(fieldName);
        writer.append(Integer.toString(intValue));
        break;
      case LONG:
        long longValue = node.getFieldValueLong(fieldName);
        writer.append(Long.toString(longValue));
        break;
      case STRING:
        String stringValue = node.getFieldValueString(fieldName);
        writer.append(stringValue);
        break;
      case REFERENCE:
        FlatRecordTraversalNode fieldNode = node.getFieldNode(fieldName);
        appendStringify(writer, indentation, fieldNode);
        break;
    }
  }

  private void appendIndentation(Writer writer, int indentation) throws IOException {
    for (int i = 0; i < indentation; i++) {
      writer.append(INDENT);
    }
  }
}
