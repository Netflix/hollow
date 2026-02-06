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
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps Protocol Buffer message descriptors to Hollow schemas.
 * Similar to HollowObjectMapper for POJOs, but works with protobuf Descriptors.
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is thread-safe and can be used concurrently by multiple threads:
 * <ul>
 *   <li>Internal collections use {@link ConcurrentHashMap} for concurrent access</li>
 *   <li>Schema creation uses double-checked locking to prevent duplicate schemas</li>
 *   <li>Struct field type tracking uses atomic operations ({@code putIfAbsent}) for conflict detection</li>
 * </ul>
 *
 * <h2>Lazy Schema Creation for Well-Known Types</h2>
 * <p>
 * Google Protocol Buffers defines well-known types like {@code google.protobuf.Struct},
 * {@code google.protobuf.Value}, and {@code google.protobuf.ListValue} that have intentional
 * circular references in their schema definitions (e.g., Struct contains Value, Value can
 * contain Struct). Hollow does not support circular references in schemas.
 * <p>
 * To handle this, these well-known dynamic types use <b>lazy schema creation</b>:
 * <ul>
 *   <li>During initial schema generation, these types are skipped</li>
 *   <li>Schemas are created on-demand when actual data is first encountered</li>
 *   <li>This breaks the circular dependency and allows the types to be supported</li>
 * </ul>
 * <p>
 * The lazy approach is safe for Hollow's snapshot/delta model. Schemas can appear dynamically
 * across cycles, and consumers will receive schema updates correctly. See
 * {@code HollowProtoAdapterTest.testLazySchemaCreationAcrossCycles()} for validation.
 *
 * <h2>Circular Reference Detection</h2>
 * <p>
 * For user-defined messages, circular references are detected during schema generation using
 * a backtracking algorithm. If detected, an {@link IllegalStateException} is thrown since
 * Hollow cannot represent such schemas. This matches the behavior of {@code HollowObjectTypeMapper}.
 */
public class HollowMessageMapper {

    private final HollowWriteStateEngine stateEngine;
    private final Set<String> processedTypes = ConcurrentHashMap.newKeySet();
    private final Map<String, HollowProtoAdapter> adapters = new ConcurrentHashMap<String, HollowProtoAdapter>();
    private boolean ignoreListOrdering = false;

    // Track field types for Struct fields to validate consistency across instances
    // Maps "ParentType.fieldPath.structFieldName" -> Value type (e.g., "Person.metadata.age" -> "numberValue")
    // Thread-safe: uses ConcurrentHashMap with atomic putIfAbsent for conflict detection
    private final ConcurrentHashMap<String, String> structFieldTypes = new ConcurrentHashMap<String, String>();

    // Lock objects for schema creation synchronization
    private final Object valueSchemaLock = new Object();
    private final Object structSchemaLock = new Object();
    private final Object listValueSchemaLock = new Object();

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

        // Get or create adapter for this type (cached for reuse)
        HollowProtoAdapter adapter = adapters.get(typeName);
        if (adapter == null) {
            adapter = new HollowProtoAdapter(stateEngine, typeName, ignoreListOrdering, this);
            adapters.put(typeName, adapter);
        }

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
        createSchemas(descriptor, new HashSet<String>());
    }

    /**
     * Recursively creates Hollow schemas from a protobuf Descriptor with cycle detection.
     *
     * @param descriptor the protobuf message descriptor
     * @param visitedInPath types currently being processed in this traversal path (for cycle detection)
     */
    private void createSchemas(Descriptors.Descriptor descriptor, Set<String> visitedInPath) {
        String typeName = descriptor.getName();

        // Skip google.protobuf.*Value types - they are unwrapped, not stored as separate types
        if (isProtoValueType(descriptor)) {
            processedTypes.add(typeName);
            return;
        }

        // Skip google.protobuf dynamic types (Struct, Value, ListValue) during initial schema generation
        // These have intentional circular references and will be handled lazily during data writes
        if (isWellKnownDynamicType(descriptor)) {
            processedTypes.add(typeName);
            return;
        }

        // Detect direct circular references (e.g., message Node { Node next = 1; })
        // Hollow doesn't support circular references in schemas
        if (visitedInPath.contains(typeName)) {
            throw new IllegalStateException(
                "Circular reference detected: message type '" + typeName + "' references itself. " +
                "Hollow does not support circular references in schemas. " +
                "Consider using a reference ID pattern instead.");
        }

        // Skip if already processed (after cycle check, before adding to path)
        if (processedTypes.contains(typeName)) {
            return;
        }

        visitedInPath.add(typeName);
        processedTypes.add(typeName);

        // First, process all nested message types (except *Value types and dynamic types)
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                Descriptors.Descriptor msgType = field.getMessageType();
                // Skip *Value types (unwrapped) and dynamic types (handled lazily)
                if (!isProtoValueType(msgType) && !isWellKnownDynamicType(msgType)) {
                    createSchemas(msgType, visitedInPath);
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
                    case FIXED64:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.LONG);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.LONG);
                        }
                        break;

                    case UINT64:  // Unsigned 64-bit mapped to signed long - requires explicit acknowledgement
                        validateUnsignedField(field, typeName);
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.LONG);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.LONG);
                        }
                        break;

                    // Unsigned integers: use signed counterparts per protobuf spec
                    // "In Java, unsigned 32-bit and 64-bit integers are represented using
                    // their signed counterparts, with the top bit simply being stored in the sign bit."
                    // https://protobuf.dev/programming-guides/proto3/#scalar
                    case UINT32:
                    case FIXED32:
                        validateUnsignedField(field, typeName);
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
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.STRING);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.STRING);
                        }
                        break;

                    case BYTES:
                        if (hasNamespacedType) {
                            ensurePrimitiveWrapperSchema(namespacedTypeName, HollowObjectSchema.FieldType.BYTES);
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, namespacedTypeName);
                        } else {
                            schema.addField(fieldName, HollowObjectSchema.FieldType.BYTES);
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
                        } else if (isWellKnownDynamicType(messageType)) {
                            // google.protobuf.Struct, Value, ListValue - treat as references
                            // Their schemas will be created lazily on first data write
                            String referencedType = messageType.getName();
                            schema.addField(fieldName, HollowObjectSchema.FieldType.REFERENCE, referencedType);
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

        // Remove from visited path (backtrack) to allow this type to be referenced
        // from other branches of the schema tree (just not from itself)
        visitedInPath.remove(typeName);
    }

    /**
     * Ensure schema exists for google.protobuf.Struct/Value/ListValue based on actual data.
     * Creates or validates schemas dynamically as data is encountered.
     *
     * @param message the Struct/Value/ListValue message instance
     * @param parentTypeName the parent type containing this field (for error messages)
     * @param fieldPath the field path in parent (for tracking/validation)
     */
    void ensureDynamicTypeSchema(com.google.protobuf.Message message, String parentTypeName, String fieldPath) {
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        String fullName = descriptor.getFullName();

        if (fullName.equals("google.protobuf.Struct")) {
            ensureStructSchema(message, parentTypeName, fieldPath);
        } else if (fullName.equals("google.protobuf.Value")) {
            ensureValueSchema(message, parentTypeName, fieldPath);
        } else if (fullName.equals("google.protobuf.ListValue")) {
            ensureListValueSchema(message, parentTypeName, fieldPath);
        }
    }

    /**
     * Ensure schema for google.protobuf.Struct based on actual field values.
     * Struct is an object with a map<string, Value> field.
     * Also validates that field types are consistent across all instances.
     */
    private void ensureStructSchema(com.google.protobuf.Message structMessage, String parentTypeName, String fieldPath) {
        // Ensure Value schema exists (will create if needed)
        ensureValueSchemaExists();

        // Validate field type consistency across instances
        validateStructFields(structMessage, parentTypeName, fieldPath);
    }

    /**
     * Validate that Struct field types are consistent across all instances.
     * Throws IllegalStateException if the same field name has different types.
     */
    private void validateStructFields(com.google.protobuf.Message structMessage, String parentTypeName, String fieldPath) {
        Descriptors.Descriptor descriptor = structMessage.getDescriptorForType();
        Descriptors.FieldDescriptor fieldsDescriptor = descriptor.findFieldByName("fields");

        if (fieldsDescriptor == null) return;

        // Get the fields map from the Struct
        Object fieldsObj = structMessage.getField(fieldsDescriptor);
        if (!(fieldsObj instanceof java.util.List)) return;

        @SuppressWarnings("unchecked")
        java.util.List<com.google.protobuf.Message> entries = (java.util.List<com.google.protobuf.Message>) fieldsObj;

        for (com.google.protobuf.Message entry : entries) {
            // Each entry has "key" (string) and "value" (Value message)
            Descriptors.Descriptor entryDescriptor = entry.getDescriptorForType();
            Descriptors.FieldDescriptor keyField = entryDescriptor.findFieldByName("key");
            Descriptors.FieldDescriptor valueField = entryDescriptor.findFieldByName("value");

            if (keyField == null || valueField == null) continue;

            String key = (String) entry.getField(keyField);
            com.google.protobuf.Message value = (com.google.protobuf.Message) entry.getField(valueField);

            // Determine which Value type this is
            String valueType = getValueType(value);
            if (valueType == null) continue;

            // Check for conflicts using atomic putIfAbsent for thread safety
            String fieldKey = parentTypeName + "." + fieldPath + "." + key;
            String existingType = structFieldTypes.putIfAbsent(fieldKey, valueType);

            // If putIfAbsent returned non-null, the field already existed - check for conflict
            if (existingType != null && !existingType.equals(valueType)) {
                throw new IllegalStateException(
                    "Conflicting types for Struct field '" + key + "' in " + parentTypeName + "." + fieldPath +
                    ": previously " + existingType + ", now " + valueType);
            }
        }
    }

    /**
     * Determine which type a Value message contains.
     * Returns the field name (e.g., "numberValue", "stringValue") or null if empty.
     */
    private String getValueType(com.google.protobuf.Message value) {
        Descriptors.Descriptor descriptor = value.getDescriptorForType();

        // Check each possible Value field
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            if (value.hasField(field)) {
                return field.getName();
            }
        }

        return null;
    }

    /**
     * Create the Value schema if it doesn't exist.
     * Value schema is fixed - it has all possible fields for the oneof.
     * Thread-safe using double-checked locking.
     */
    private void ensureValueSchemaExists() {
        String valueTypeName = "Value";

        // Create Value schema if it doesn't exist (double-checked locking for thread safety)
        if (stateEngine.getSchema(valueTypeName) == null) {
            synchronized (valueSchemaLock) {
                if (stateEngine.getSchema(valueTypeName) == null) {
                    // Value has a oneof "kind" with different types
                    // We'll model it as an object with nullable fields for each possible type
                    HollowObjectSchema schema = new HollowObjectSchema(valueTypeName, 6);
                    schema.addField("nullValue", HollowObjectSchema.FieldType.BOOLEAN);  // true if null
                    schema.addField("numberValue", HollowObjectSchema.FieldType.DOUBLE);
                    schema.addField("stringValue", HollowObjectSchema.FieldType.STRING);
                    schema.addField("boolValue", HollowObjectSchema.FieldType.BOOLEAN);
                    schema.addField("structValue", HollowObjectSchema.FieldType.REFERENCE, "Struct");
                    schema.addField("listValue", HollowObjectSchema.FieldType.REFERENCE, "ListValue");
                    stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
                }
            }
        }

        // Ensure prerequisite schemas exist (double-checked locking for thread safety)
        if (stateEngine.getSchema("Struct") == null) {
            synchronized (structSchemaLock) {
                if (stateEngine.getSchema("Struct") == null) {
                    // Create Struct schema - it's an object with a map<string, Value> field called "fields"
                    HollowObjectSchema structSchema = new HollowObjectSchema("Struct", 1);
                    structSchema.addField("fields", HollowObjectSchema.FieldType.REFERENCE, "MapOfStringToValue");
                    stateEngine.addTypeState(new HollowObjectTypeWriteState(structSchema));

                    // Create the map schema for the fields
                    HollowMapSchema fieldsMapSchema = new HollowMapSchema("MapOfStringToValue", "String", "Value");
                    stateEngine.addTypeState(new HollowMapTypeWriteState(fieldsMapSchema));

                    ensurePrimitiveWrapperSchema("String");
                }
            }
        }

        if (stateEngine.getSchema("ListValue") == null) {
            synchronized (listValueSchemaLock) {
                if (stateEngine.getSchema("ListValue") == null) {
                    // Create ListValue schema
                    HollowObjectSchema listValueSchema = new HollowObjectSchema("ListValue", 1);
                    listValueSchema.addField("values", HollowObjectSchema.FieldType.REFERENCE, "ListOfValue");
                    stateEngine.addTypeState(new HollowObjectTypeWriteState(listValueSchema));

                    // Create list schema
                    HollowListSchema listSchema = new HollowListSchema("ListOfValue", "Value");
                    stateEngine.addTypeState(new HollowListTypeWriteState(listSchema));
                }
            }
        }
    }

    /**
     * Ensure schema for google.protobuf.Value based on actual oneof choice.
     * Value can be: null, number, string, bool, struct, or list.
     */
    private void ensureValueSchema(com.google.protobuf.Message valueMessage, String parentTypeName, String fieldPath) {
        ensureValueSchemaExists();
    }

    /**
     * Ensure schema for google.protobuf.ListValue based on actual element types.
     * ListValue is repeated Value.
     */
    private void ensureListValueSchema(com.google.protobuf.Message listValueMessage, String parentTypeName, String fieldPath) {
        ensureValueSchemaExists();
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
                return "String";

            case BYTES:
                return "Bytes";

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
        } else if (wrapperType.equals("Bytes")) {
            wrapperSchema.addField("value", HollowObjectSchema.FieldType.BYTES);
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
     * Check if a message type is a google.protobuf dynamic type (Struct, Value, ListValue).
     * These types have circular references by design and must be handled lazily during data writes.
     */
    private boolean isWellKnownDynamicType(Descriptors.Descriptor descriptor) {
        if (descriptor == null) return false;
        String fullName = descriptor.getFullName();
        return fullName.equals("google.protobuf.Struct")
            || fullName.equals("google.protobuf.Value")
            || fullName.equals("google.protobuf.ListValue");
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
            return "Bytes";
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
        } else if (name.equals("StringValue")) {
            return HollowObjectSchema.FieldType.STRING;
        } else if (name.equals("BytesValue")) {
            return HollowObjectSchema.FieldType.BYTES;
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
     * Validate unsigned integer field has required annotation.
     * UINT32 and UINT64 must have hollow_unsigned_to_signed=true to acknowledge
     * that large values will appear negative when mapped to signed types.
     *
     * @throws IllegalStateException if unsigned field lacks required annotation
     */
    private void validateUnsignedField(Descriptors.FieldDescriptor field, String typeName) {
        boolean hasAcknowledgement = field.getOptions().hasExtension(HollowOptions.hollowUnsignedToSigned)
            && field.getOptions().getExtension(HollowOptions.hollowUnsignedToSigned);

        if (!hasAcknowledgement) {
            String typeStr = field.getType() == Descriptors.FieldDescriptor.Type.UINT32 ? "uint32" : "uint64";
            String hollowType = field.getType() == Descriptors.FieldDescriptor.Type.UINT32 ? "INT" : "LONG";
            throw new IllegalStateException(
                "Field '" + field.getName() + "' in message '" + typeName + "' is " + typeStr +
                " which maps to signed " + hollowType + ". Large unsigned values (> " +
                (field.getType() == Descriptors.FieldDescriptor.Type.UINT32 ? "2^31-1" : "2^63-1") +
                ") will appear as negative numbers.\n" +
                "To acknowledge this limitation and allow the field, add:\n" +
                "  [(com.netflix.hollow.hollow_unsigned_to_signed) = true]\n" +
                "Example:\n" +
                "  " + typeStr + " " + field.getName() + " = " + field.getNumber() +
                " [(com.netflix.hollow.hollow_unsigned_to_signed) = true];"
            );
        }
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

        // Get or create adapter for this type (cached for reuse)
        HollowProtoAdapter adapter = adapters.get(typeName);
        if (adapter == null) {
            adapter = new HollowProtoAdapter(stateEngine, typeName, ignoreListOrdering, this);
            adapters.put(typeName, adapter);
        }

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
     *   option (com.netflix.hollow.hollow_primary_key) = {fields: ["id"]};
     *
     *   int32 id = 1;
     *   string name = 2;
     * }
     *
     * message Account {
     *   option (com.netflix.hollow.hollow_primary_key) = {fields: ["account_id", "region"]};
     *
     *   string account_id = 1;
     *   string region = 2;
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
        com.google.protobuf.DescriptorProtos.MessageOptions options = descriptor.getOptions();
        java.util.List<String> keyFieldNames = null;

        try {
            if (options.hasExtension(HollowOptions.hollowPrimaryKey)) {
                HollowOptions.HollowPrimaryKey pkOption = options.getExtension(HollowOptions.hollowPrimaryKey);
                keyFieldNames = pkOption.getFieldsList();
            }
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
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String stringValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getString(fieldName);
                    if (stringValue != null) {
                        builder.setField(field, stringValue);
                    }
                }
                break;

            case BYTES:
                if (record instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    byte[] bytesValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) record).getBytes(fieldName);
                    if (bytesValue != null) {
                        builder.setField(field, com.google.protobuf.ByteString.copyFrom(bytesValue));
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
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    String stringValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getString("value");
                    if (stringValue != null) {
                        return stringValue;
                    }
                }
                break;

            case BYTES:
                if (element instanceof com.netflix.hollow.api.objects.generic.GenericHollowObject) {
                    byte[] bytesValue = ((com.netflix.hollow.api.objects.generic.GenericHollowObject) element).getBytes("value");
                    if (bytesValue != null) {
                        return com.google.protobuf.ByteString.copyFrom(bytesValue);
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
                String stringValue = node.getFieldValueString(fieldName);
                if (stringValue != null) {
                    builder.setField(field, stringValue);
                }
                break;

            case BYTES:
                byte[] bytesValue = node.getFieldValueBytes(fieldName);
                if (bytesValue != null) {
                    builder.setField(field, com.google.protobuf.ByteString.copyFrom(bytesValue));
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
                String stringValue = objNode.getFieldValueString("value");
                if (stringValue != null) {
                    return stringValue;
                }
                break;

            case BYTES:
                byte[] bytesValue = objNode.getFieldValueBytes("value");
                if (bytesValue != null) {
                    return com.google.protobuf.ByteString.copyFrom(bytesValue);
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
