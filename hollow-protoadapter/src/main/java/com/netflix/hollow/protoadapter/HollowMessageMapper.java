/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.protoadapter;

import com.google.protobuf.Descriptors;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * Maps Protocol Buffer message descriptors to Hollow schemas.
 * Similar to HollowObjectMapper for POJOs, but works with protobuf Descriptors.
 */
public class HollowMessageMapper {

    private final HollowWriteStateEngine stateEngine;
    private final Set<String> processedTypes = new HashSet<String>();
    private boolean ignoreListOrdering = false;

    public HollowMessageMapper(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
    }

    /**
     * Configures this mapper to ignore list ordering for all repeated fields.
     * <p>
     * When enabled, lists with the same elements in different orders are considered
     * identical for deduplication purposes. For example, ["a", "b", "c"] and
     * ["c", "a", "b"] would be treated as the same list.
     * <p>
     * This is useful for repeated fields that represent unordered collections (tags, IDs)
     * and can significantly reduce memory usage.
     * <p>
     * Similar to {@code HollowObjectMapper.ignoreListOrdering()}.
     */
    public void ignoreListOrdering() {
        this.ignoreListOrdering = true;
    }

    /**
     * Initialize Hollow type state from a Protocol Buffer Descriptor.
     * Automatically creates schemas for the message and all nested types.
     *
     * @param descriptor the protobuf message descriptor
     */
    public void initializeTypeState(Descriptors.Descriptor descriptor) {
        createSchemas(descriptor);
    }

    /**
     * Add a Protocol Buffer message to the state engine.
     * Similar to HollowObjectMapper.add() for POJOs.
     * <p>
     * Automatically infers schemas on first use and writes the message.
     *
     * @param message the protobuf message to add
     * @return the ordinal assigned to the message
     */
    public int add(com.google.protobuf.Message message) {
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        String typeName = descriptor.getName();

        // Initialize schema if not already done
        if (stateEngine.getSchema(typeName) == null) {
            initializeTypeState(descriptor);
        }

        // Create adapter and process message
        HollowProtoAdapter adapter = new HollowProtoAdapter(stateEngine, typeName, ignoreListOrdering);
        try {
            return adapter.processMessage(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process message of type " + typeName, e);
        }
    }

    /**
     * Recursively creates Hollow schemas from a protobuf Descriptor.
     */
    private void createSchemas(Descriptors.Descriptor descriptor) {
        String typeName = descriptor.getName();

        // Skip if already processed
        if (processedTypes.contains(typeName)) {
            return;
        }

        // Skip google.protobuf.*Value types - they are unwrapped, not stored as separate types
        if (isProtoValueType(descriptor)) {
            processedTypes.add(typeName);
            return;
        }

        processedTypes.add(typeName);

        // First, process all nested message types (except *Value types)
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                Descriptors.Descriptor msgType = field.getMessageType();
                // Skip *Value types - they are unwrapped
                if (!isProtoValueType(msgType)) {
                    createSchemas(msgType);
                }
            }
        }

        // Count fields to size the schema
        int fieldCount = descriptor.getFields().size();

        // Create the object schema for this message
        HollowObjectSchema schema = new HollowObjectSchema(typeName, fieldCount);

        // Add fields to the schema
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            String fieldName = field.getName();

            if (field.isRepeated()) {
                // Repeated fields map to Hollow lists
                String listTypeName = "ListOf" + getElementTypeName(field);
                String elementType = getElementTypeName(field);

                // Ensure the element type schema exists
                if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                    // Already created above
                } else {
                    // For primitive repeated fields, create wrapper if needed
                    ensurePrimitiveWrapperSchema(elementType);
                }

                // Create list schema if not exists
                if (stateEngine.getSchema(listTypeName) == null) {
                    HollowListSchema listSchema = new HollowListSchema(listTypeName, elementType);
                    stateEngine.addTypeState(new HollowListTypeWriteState(listSchema));
                }

                schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, listTypeName);

            } else {
                // Check if field has hollow_type_name option (for namespaced wrapper types)
                String namespacedTypeName = getHollowFieldName(field);
                boolean hasNamespacedType = !fieldName.equals(namespacedTypeName);

                // Non-repeated fields
                switch (field.getType()) {
                    case INT32:
                    case SINT32:
                    case SFIXED32:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.INT);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.INT);
                        }
                        break;

                    case INT64:
                    case SINT64:
                    case SFIXED64:
                    case UINT64:
                    case FIXED64:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.LONG);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.LONG);
                        }
                        break;

                    case UINT32:
                    case FIXED32:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.INT);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.INT);
                        }
                        break;

                    case FLOAT:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.FLOAT);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.FLOAT);
                        }
                        break;

                    case DOUBLE:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.DOUBLE);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.DOUBLE);
                        }
                        break;

                    case BOOL:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.BOOLEAN);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.BOOLEAN);
                        }
                        break;

                    case STRING:
                    case BYTES:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.STRING);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.STRING);
                        }
                        break;

                    case ENUM:
                        // Enums stored as strings
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.STRING);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.STRING);
                        }
                        break;

                    case MESSAGE:
                        // Check if this is a google.protobuf.*Value type
                        Descriptors.Descriptor messageType = field.getMessageType();
                        if (isProtoValueType(messageType)) {
                            // Unwrap *Value types
                            if (shouldInlineField(field)) {
                                // hollow_inline: unwrap to inline primitive
                                HollowObjectSchema.FieldType inlineType = getValueTypeInline(messageType);
                                if (inlineType == null) {
                                    throw new IllegalArgumentException("Unsupported *Value type for inlining: " + messageType.getName());
                                }
                                schema.addField(fieldName, inlineType);
                            } else {
                                // Default: unwrap to reference to wrapper type (e.g., Integer, String)
                                String wrapperType = getValueTypeWrapper(messageType);
                                if (wrapperType == null) {
                                    throw new IllegalArgumentException("Unsupported *Value type: " + messageType.getName());
                                }
                                ensurePrimitiveWrapperSchema(wrapperType);
                                schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, wrapperType);
                            }
                        } else {
                            // Regular nested messages are references
                            String referencedType = messageType.getName();
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, referencedType);
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                }
            }
        }

        // Add the schema to the state engine
        stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    /**
     * Get the element type name for a field (handles both primitives and messages).
     */
    private String getElementTypeName(Descriptors.FieldDescriptor field) {
        if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            return field.getMessageType().getName();
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            return "String"; // Enums stored as strings
        } else {
            // Primitive types need wrapper types for collections
            return getPrimitiveWrapperTypeName(field.getType());
        }
    }

    /**
     * Get wrapper type name for primitives in collections.
     */
    private String getPrimitiveWrapperTypeName(Descriptors.FieldDescriptor.Type type) {
        switch (type) {
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                return "Integer";

            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                return "Long";

            case FLOAT:
                return "Float";

            case DOUBLE:
                return "Double";

            case BOOL:
                return "Boolean";

            case STRING:
            case BYTES:
                return "String";

            default:
                return "String";
        }
    }

    /**
     * Ensure a primitive wrapper schema exists (e.g., Integer, String).
     */
    private void ensurePrimitiveWrapperSchema(String wrapperType) {
        if (stateEngine.getSchema(wrapperType) != null) {
            return;
        }

        HollowObjectSchema wrapperSchema = new HollowObjectSchema(wrapperType, 1);

        if (wrapperType.equals("Integer")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.INT);
        } else if (wrapperType.equals("Long")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.LONG);
        } else if (wrapperType.equals("Float")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.FLOAT);
        } else if (wrapperType.equals("Double")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.DOUBLE);
        } else if (wrapperType.equals("Boolean")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.BOOLEAN);
        } else if (wrapperType.equals("String")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.STRING);
        }

        stateEngine.addTypeState(new HollowObjectTypeWriteState(wrapperSchema));
    }

    /**
     * Ensure a primitive wrapper schema exists with a custom name (for namespaced types).
     * Used when hollow_type_name option is specified on a primitive field.
     */
    private void ensurePrimitiveWrapperSchema(String wrapperTypeName, HollowObjectSchema.FieldType primitiveType) {
        if (stateEngine.getSchema(wrapperTypeName) != null) {
            return;
        }

        HollowObjectSchema wrapperSchema = new HollowObjectSchema(wrapperTypeName, 1);
        wrapperSchema.addField("value", primitiveType);
        stateEngine.addTypeState(new HollowObjectTypeWriteState(wrapperSchema));
    }

    /**
     * Check if a message type is a google.protobuf.*Value wrapper type.
     * Note: google.protobuf.Value (without a prefix) is NOT a wrapper type - it's a union type.
     */
    private boolean isProtoValueType(Descriptors.Descriptor descriptor) {
        if (descriptor == null) return false;
        String fullName = descriptor.getFullName();
        // Only match specific wrapper types, not google.protobuf.Value or google.protobuf.Struct
        return fullName.equals("google.protobuf.Int32Value")
            || fullName.equals("google.protobuf.Int64Value")
            || fullName.equals("google.protobuf.UInt32Value")
            || fullName.equals("google.protobuf.UInt64Value")
            || fullName.equals("google.protobuf.FloatValue")
            || fullName.equals("google.protobuf.DoubleValue")
            || fullName.equals("google.protobuf.BoolValue")
            || fullName.equals("google.protobuf.StringValue")
            || fullName.equals("google.protobuf.BytesValue");
    }

    /**
     * Get the Hollow wrapper type name for a google.protobuf.*Value type.
     * E.g., Int32Value -> Integer, StringValue -> String
     */
    private String getValueTypeWrapper(Descriptors.Descriptor descriptor) {
        String name = descriptor.getName();
        if (name.equals("Int32Value") || name.equals("UInt32Value")) {
            return "Integer";
        } else if (name.equals("Int64Value") || name.equals("UInt64Value")) {
            return "Long";
        } else if (name.equals("FloatValue")) {
            return "Float";
        } else if (name.equals("DoubleValue")) {
            return "Double";
        } else if (name.equals("BoolValue")) {
            return "Boolean";
        } else if (name.equals("StringValue")) {
            return "String";
        } else if (name.equals("BytesValue")) {
            return "String"; // Bytes stored as String
        }
        return null;
    }

    /**
     * Get the Hollow inline field type for a google.protobuf.*Value type.
     */
    private HollowObjectSchema.FieldType getValueTypeInline(Descriptors.Descriptor descriptor) {
        String name = descriptor.getName();
        if (name.equals("Int32Value") || name.equals("UInt32Value")) {
            return HollowObjectSchema.FieldType.INT;
        } else if (name.equals("Int64Value") || name.equals("UInt64Value")) {
            return HollowObjectSchema.FieldType.LONG;
        } else if (name.equals("FloatValue")) {
            return HollowObjectSchema.FieldType.FLOAT;
        } else if (name.equals("DoubleValue")) {
            return HollowObjectSchema.FieldType.DOUBLE;
        } else if (name.equals("BoolValue")) {
            return HollowObjectSchema.FieldType.BOOLEAN;
        } else if (name.equals("StringValue") || name.equals("BytesValue")) {
            return HollowObjectSchema.FieldType.STRING;
        }
        return null;
    }

    /**
     * Get the hollow field name, checking for hollow_type_name custom option.
     */
    private String getHollowFieldName(Descriptors.FieldDescriptor field) {
        // Check for hollow_type_name custom option
        if (field.getOptions().hasExtension(HollowOptions.hollowTypeName)) {
            String customName = field.getOptions().getExtension(HollowOptions.hollowTypeName);
            if (customName != null && !customName.isEmpty()) {
                return customName;
            }
        }
        return field.getName();
    }

    /**
     * Check if a field should be inlined (has hollow_inline option set to true).
     */
    private boolean shouldInlineField(Descriptors.FieldDescriptor field) {
        if (field.getOptions().hasExtension(HollowOptions.hollowInline)) {
            return field.getOptions().getExtension(HollowOptions.hollowInline);
        }
        return false;
    }

    /**
     * Write a Protocol Buffer message to flat records.
     * Similar to HollowObjectMapper.writeFlat() for POJOs.
     *
     * @param message the protobuf message to write
     * @param flatRecordWriter the flat record writer
     */
    public void writeFlat(com.google.protobuf.Message message,
                         com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter flatRecordWriter) {
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        String typeName = descriptor.getName();

        // Initialize schema if not already done
        if (stateEngine.getSchema(typeName) == null) {
            initializeTypeState(descriptor);
        }

        // Create adapter and write to flat records
        HollowProtoAdapter adapter = new HollowProtoAdapter(stateEngine, typeName, ignoreListOrdering);
        try {
            adapter.processMessage(message, flatRecordWriter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write message to flat records: " + typeName, e);
        }
    }

    /**
     * Read a Protocol Buffer message from a HollowRecord.
     * Similar to HollowObjectMapper.readHollowRecord() for POJOs.
     *
     * @param record the hollow record to read
     * @param descriptor the protobuf descriptor for the message type
     * @return the reconstructed protobuf message
     */
    public com.google.protobuf.Message readHollowRecord(
            com.netflix.hollow.api.objects.HollowRecord record,
            Descriptors.Descriptor descriptor) {

        com.google.protobuf.DynamicMessage.Builder builder =
            com.google.protobuf.DynamicMessage.newBuilder(descriptor);

        // Read each field from the Hollow record
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            readFieldFromHollow(record, field, builder);
        }

        return builder.build();
    }

    /**
     * Read a Protocol Buffer message from flat records.
     * Similar to HollowObjectMapper.readFlat() for POJOs.
     *
     * @param node the flat record traversal node
     * @param descriptor the protobuf descriptor for the message type
     * @return the reconstructed protobuf message
     */
    public com.google.protobuf.Message readFlat(
            com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode node,
            Descriptors.Descriptor descriptor) {

        if (!(node instanceof com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode)) {
            throw new IllegalArgumentException("Expected FlatRecordTraversalObjectNode for message type");
        }

        com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode objectNode =
            (com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode) node;

        com.google.protobuf.DynamicMessage.Builder builder =
            com.google.protobuf.DynamicMessage.newBuilder(descriptor);

        // Read each field from flat records
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            readFieldFromFlatRecord(objectNode, field, builder);
        }

        return builder.build();
    }

    /**
     * Extract primary key from a Protocol Buffer message.
     * <p>
     * Reads the primary key fields from the message using the hollow_primary_key option
     * defined in hollow_options.proto. If no option is specified, falls back to using
     * all scalar fields.
     * <p>
     * Example proto definition:
     * <pre>
     * import "hollow_options.proto";
     *
     * message Person {
     *   option (com.netflix.hollow.hollow_primary_key) = "id";
     *
     *   int32 id = 1;
     *   string name = 2;
     * }
     * </pre>
     *
     * @param message the protobuf message
     * @return primary key containing the type name and field values
     */
    public com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey extractPrimaryKey(
            com.google.protobuf.Message message) {

        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        String typeName = descriptor.getName();
        java.util.List<Object> keyValues = new java.util.ArrayList<Object>();

        // Try to read the hollow_primary_key option
        // Note: hollow_primary_key is a repeated field, so we can't use hasExtension()
        // Just call getExtension() which returns empty list if not set
        com.google.protobuf.DescriptorProtos.MessageOptions options = descriptor.getOptions();
        java.util.List<String> keyFieldNames = null;

        try {
            keyFieldNames = options.getExtension(HollowOptions.hollowPrimaryKey);
        } catch (Exception e) {
            // Extension not available
        }

        if (keyFieldNames != null && !keyFieldNames.isEmpty()) {
            // Use specified primary key fields from option
            for (String fieldName : keyFieldNames) {
                Descriptors.FieldDescriptor field = descriptor.findFieldByName(fieldName);
                if (field == null) {
                    throw new IllegalArgumentException(
                        "Primary key field '" + fieldName + "' not found in message type " + typeName);
                }
                Object value = message.getField(field);
                keyValues.add(value);
            }
        } else {
            // Fallback: use all scalar fields
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                if (!field.isRepeated() && field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
                    Object value = message.getField(field);
                    keyValues.add(value);
                }
            }
        }

        return new com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey(
            typeName,
            keyValues.toArray()
        );
    }

    /**
     * Read a field from a HollowRecord and set it on the message builder.
     */
    private void readFieldFromHollow(
            com.netflix.hollow.api.objects.HollowRecord record,
            Descriptors.FieldDescriptor field,
            com.google.protobuf.Message.Builder builder) {

        String fieldName = field.getName();
        int fieldIndex = getFieldIndex(record.getSchema(), fieldName);

        if (fieldIndex < 0) {
            return; // Field not found in schema
        }

        if (field.isRepeated()) {
            // Handle repeated fields - they're stored as references to Lists
            if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                com.netflix.hollow.api.objects.generic.GenericHollowObject genericRecord =
                    (com.netflix.hollow.api.objects.generic.GenericHollowObject) record;

                com.netflix.hollow.api.objects.HollowRecord listRecord = genericRecord.getReferencedGenericRecord(fieldName);
                if (listRecord == null) {
                    return;
                }

                // List records expose size() and iterator
                if (listRecord instanceof com.netflix.hollow.api.objects.generic.GenericHollowList) {
                    com.netflix.hollow.api.objects.generic.GenericHollowList list =
                        (com.netflix.hollow.api.objects.generic.GenericHollowList) listRecord;

                    for (int i = 0; i < list.size(); i++) {
                        Object element = readListElement(list, i, field);
                        if (element != null) {
                            builder.addRepeatedField(field, element);
                        }
                    }
                }
            }
            return;
        }

        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    int intValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getInt(fieldName);
                    builder.setField(field, intValue);
                }
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    long longValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getLong(fieldName);
                    builder.setField(field, longValue);
                }
                break;

            case FLOAT:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    float floatValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getFloat(fieldName);
                    builder.setField(field, floatValue);
                }
                break;

            case DOUBLE:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    double doubleValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getDouble(fieldName);
                    builder.setField(field, doubleValue);
                }
                break;

            case BOOL:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    boolean boolValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getBoolean(fieldName);
                    builder.setField(field, boolValue);
                }
                break;

            case STRING:
            case BYTES:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String stringValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getString(fieldName);
                    if (stringValue != null) {
                        if (field.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                            builder.setField(field, com.google.protobuf.ByteString.copyFromUtf8(stringValue));
                        } else {
                            builder.setField(field, stringValue);
                        }
                    }
                }
                break;

            case ENUM:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String enumValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getString(fieldName);
                    if (enumValue != null) {
                        Descriptors.EnumValueDescriptor enumDesc = field.getEnumType().findValueByName(enumValue);
                        if (enumDesc != null) {
                            builder.setField(field, enumDesc);
                        }
                    }
                }
                break;

            case MESSAGE:
                // Handle nested messages recursively
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    com.netflix.hollow.api.objects.HollowRecord nestedRecord =
                        ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getReferencedGenericRecord(fieldName);
                    if (nestedRecord != null) {
                        com.google.protobuf.Message nestedMessage = readHollowRecord(nestedRecord, field.getMessageType());
                        builder.setField(field, nestedMessage);
                    }
                }
                break;
        }
    }

    /**
     * Read an element from a Hollow list and convert to the appropriate protobuf type.
     */
    private Object readListElement(
            com.netflix.hollow.api.objects.generic.GenericHollowList list,
            int index,
            Descriptors.FieldDescriptor field) {

        com.netflix.hollow.api.objects.HollowRecord element = list.get(index);
        if (element == null) {
            return null;
        }

        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                // List contains wrapper objects with "value" field
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    return ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getInt("value");
                }
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    return ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getLong("value");
                }
                break;

            case FLOAT:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    return ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getFloat("value");
                }
                break;

            case DOUBLE:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    return ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getDouble("value");
                }
                break;

            case BOOL:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    return ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getBoolean("value");
                }
                break;

            case STRING:
            case BYTES:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String stringValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getString("value");
                    if (stringValue != null) {
                        if (field.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                            return com.google.protobuf.ByteString.copyFromUtf8(stringValue);
                        } else {
                            return stringValue;
                        }
                    }
                }
                break;

            case ENUM:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String enumValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getString("value");
                    if (enumValue != null) {
                        return field.getEnumType().findValueByName(enumValue);
                    }
                }
                break;

            case MESSAGE:
                // Nested messages in lists - element is already the nested record
                return readHollowRecord(element, field.getMessageType());
        }

        return null;
    }

    /**
     * Read a field from flat records and set it on the message builder.
     */
    private void readFieldFromFlatRecord(
            com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode node,
            Descriptors.FieldDescriptor field,
            com.google.protobuf.Message.Builder builder) {

        String fieldName = field.getName();

        if (field.isRepeated()) {
            // Handle repeated fields from flat records
            com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode listNode =
                node.getFieldNode(fieldName);
            if (listNode == null) {
                return;
            }

            if (listNode instanceof com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalListNode) {
                com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalListNode list =
                    (com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalListNode) listNode;

                for (int i = 0; i < list.size(); i++) {
                    Object element = readFlatListElement(list.get(i), field);
                    if (element != null) {
                        builder.addRepeatedField(field, element);
                    }
                }
            }
            return;
        }

        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                Integer intValue = node.getFieldValueIntBoxed(fieldName);
                if (intValue != null) {
                    builder.setField(field, intValue);
                }
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                Long longValue = node.getFieldValueLongBoxed(fieldName);
                if (longValue != null) {
                    builder.setField(field, longValue);
                }
                break;

            case FLOAT:
                Float floatValue = node.getFieldValueFloatBoxed(fieldName);
                if (floatValue != null) {
                    builder.setField(field, floatValue);
                }
                break;

            case DOUBLE:
                Double doubleValue = node.getFieldValueDoubleBoxed(fieldName);
                if (doubleValue != null) {
                    builder.setField(field, doubleValue);
                }
                break;

            case BOOL:
                Boolean boolValue = node.getFieldValueBooleanBoxed(fieldName);
                if (boolValue != null) {
                    builder.setField(field, boolValue);
                }
                break;

            case STRING:
            case BYTES:
                String stringValue = node.getFieldValueString(fieldName);
                if (stringValue != null) {
                    if (field.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                        builder.setField(field, com.google.protobuf.ByteString.copyFromUtf8(stringValue));
                    } else {
                        builder.setField(field, stringValue);
                    }
                }
                break;

            case ENUM:
                String enumValue = node.getFieldValueString(fieldName);
                if (enumValue != null) {
                    Descriptors.EnumValueDescriptor enumDesc = field.getEnumType().findValueByName(enumValue);
                    if (enumDesc != null) {
                        builder.setField(field, enumDesc);
                    }
                }
                break;

            case MESSAGE:
                // Handle nested messages recursively
                com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode nestedNode =
                    node.getFieldNode(fieldName);
                if (nestedNode instanceof com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode) {
                    com.google.protobuf.Message nestedMessage = readFlat(nestedNode, field.getMessageType());
                    builder.setField(field, nestedMessage);
                }
                break;
        }
    }

    /**
     * Read an element from a flat record list and convert to the appropriate protobuf type.
     */
    private Object readFlatListElement(
            com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode elementNode,
            Descriptors.FieldDescriptor field) {

        if (!(elementNode instanceof com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode)) {
            return null;
        }

        com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode objNode =
            (com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode) elementNode;

        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                return objNode.getFieldValueIntBoxed("value");

            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                return objNode.getFieldValueLongBoxed("value");

            case FLOAT:
                return objNode.getFieldValueFloatBoxed("value");

            case DOUBLE:
                return objNode.getFieldValueDoubleBoxed("value");

            case BOOL:
                return objNode.getFieldValueBooleanBoxed("value");

            case STRING:
            case BYTES:
                String stringValue = objNode.getFieldValueString("value");
                if (stringValue != null) {
                    if (field.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                        return com.google.protobuf.ByteString.copyFromUtf8(stringValue);
                    } else {
                        return stringValue;
                    }
                }
                break;

            case ENUM:
                String enumValue = objNode.getFieldValueString("value");
                if (enumValue != null) {
                    return field.getEnumType().findValueByName(enumValue);
                }
                break;

            case MESSAGE:
                // Nested messages in lists
                return readFlat(objNode, field.getMessageType());
        }

        return null;
    }

    /**
     * Get the field index in a Hollow schema.
     */
    private int getFieldIndex(com.netflix.hollow.core.schema.HollowSchema schema, String fieldName) {
        if (!(schema instanceof com.netflix.hollow.core.schema.HollowObjectSchema)) {
            return -1;
        }

        com.netflix.hollow.core.schema.HollowObjectSchema objSchema =
            (com.netflix.hollow.core.schema.HollowObjectSchema) schema;

        return objSchema.getPosition(fieldName);
    }

    /**
     * Get the state engine.
     *
     * @return the state engine
     */
    public HollowWriteStateEngine getStateEngine() {
        return stateEngine;
    }
}
