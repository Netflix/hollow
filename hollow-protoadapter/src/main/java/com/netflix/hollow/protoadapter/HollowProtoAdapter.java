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

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.protoadapter.field.FieldProcessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Populate a HollowWriteStateEngine based on data encoded in Protocol Buffers.
 */
    public class HollowProtoAdapter extends AbstractHollowProtoAdapterTask {

    final HollowWriteStateEngine stateEngine;
    private final Map<String, HollowSchema> hollowSchemas;
    private final ThreadLocal<Map<String, HollowWriteRecord>> hollowWriteRecordsHolder = new ThreadLocal<Map<String, HollowWriteRecord>>();
    private final ThreadLocal<Map<String, ObjectFieldMapping>> objectFieldMappingHolder = new ThreadLocal<Map<String, ObjectFieldMapping>>();

    private final Map<String, ObjectFieldMapping> canonicalObjectFieldMappings;

    private final Set<String> passthroughDecoratedTypes;
    private final ThreadLocal<PassthroughWriteRecords> passthroughRecords;

    private final boolean ignoreListOrdering;

    public HollowProtoAdapter(HollowWriteStateEngine stateEngine, String typeName) {
        this(stateEngine, typeName, false);
    }

    public HollowProtoAdapter(HollowWriteStateEngine stateEngine, String typeName, boolean ignoreListOrdering) {
        super(typeName, "populate");
        this.stateEngine = stateEngine;
        this.hollowSchemas = new HashMap<String, HollowSchema>();
        this.canonicalObjectFieldMappings = new HashMap<String, ObjectFieldMapping>();
        this.passthroughDecoratedTypes = new HashSet<String>();
        this.ignoreListOrdering = ignoreListOrdering;

        for(HollowSchema schema : stateEngine.getSchemas()) {
            hollowSchemas.put(schema.getName(), schema);
            if(schema instanceof HollowObjectSchema)
                canonicalObjectFieldMappings.put(schema.getName(), new ObjectFieldMapping(schema.getName(), this));
        }

        this.passthroughRecords = new ThreadLocal<PassthroughWriteRecords>();
    }

    @Override
    public void addFieldProcessor(FieldProcessor... processors) {
        super.addFieldProcessor(processors);

        for(FieldProcessor processor : processors) {
            ObjectFieldMapping ofm = canonicalObjectFieldMappings.get(processor.getEntityName());
            if(ofm != null) {
                ofm.addFieldProcessor(processor);
            } else {
                for(Map.Entry<String, ObjectFieldMapping> entry : canonicalObjectFieldMappings.entrySet()) {
                    entry.getValue().addFieldProcessor(processor);
                }
            }
        }
    }

    public void remapFieldPath(String type, String fieldName, String... fieldPaths) {
        canonicalObjectFieldMappings.get(type).addRemappedPath(fieldName, fieldPaths);
    }

    public void addPassthroughDecoratedType(String type) {
        passthroughDecoratedTypes.add(type);
    }

    public void populate(File protoFile) throws Exception {
        processFile(protoFile, Integer.MAX_VALUE);
    }

    public int processMessage(Message message) throws IOException {
        return processMessage(message, null);
    }

    public int processMessage(Message message, FlatRecordWriter flatRecordWriter) throws IOException {
        initHollowWriteRecordsIfNecessary();
        return parseMessage(message, flatRecordWriter, typeName);
    }

    private int parseMessage(Message message, FlatRecordWriter flatRecordWriter, String typeName) throws IOException {
        HollowSchema typeSchema = hollowSchemas.get(typeName);
        switch(typeSchema.getSchemaType()) {
            case OBJECT:
                return addObject(message, flatRecordWriter, typeName);
            case LIST:
            case SET:
                return addCollection(message, flatRecordWriter, typeName, getWriteRecord(typeName));
            case MAP:
                return addMap(message, flatRecordWriter, typeName, (HollowMapWriteRecord) getWriteRecord(typeName));
        }
        throw new IOException("Unsupported schema type: " + typeSchema.getSchemaType());
    }

    private int addObject(Message message, FlatRecordWriter flatRecordWriter, String typeName) throws IOException {
        ObjectFieldMapping objectMapping = getObjectFieldMapping(typeName);
        Boolean passthroughDecoratedTypes = null;
        PassthroughWriteRecords rec = null;

        for (Map.Entry<FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
            FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            String fieldName = field.getName();

            ObjectMappedFieldPath mappedFieldPath = objectMapping.getMappedFieldPath(fieldName);

            if (mappedFieldPath != null) {
                addObjectField(message, field, value, flatRecordWriter, mappedFieldPath);
            } else {
                if (passthroughDecoratedTypes == null) {
                    passthroughDecoratedTypes = Boolean.valueOf(this.passthroughDecoratedTypes.contains(typeName));

                    if (passthroughDecoratedTypes.booleanValue()) {
                        rec = getPassthroughWriteRecords();
                    }
                }
                if (passthroughDecoratedTypes.booleanValue()) {
                    addPassthroughField(field, value, flatRecordWriter, fieldName, rec);
                }
            }
        }

        if (passthroughDecoratedTypes != null && passthroughDecoratedTypes.booleanValue()) {
            rec.passthroughRec.setReference("singleValues", addRecord("SingleValuePassthroughMap", rec.singleValuePassthroughMapRec, flatRecordWriter));
            rec.passthroughRec.setReference("multiValues", addRecord("MultiValuePassthroughMap", rec.multiValuePassthroughMapRec, flatRecordWriter));

            int passthroughOrdinal = addRecord("PassthroughData", rec.passthroughRec, flatRecordWriter);

            return objectMapping.build(passthroughOrdinal, flatRecordWriter);
        }

        return objectMapping.build(-1, flatRecordWriter);
    }

    private void addPassthroughField(FieldDescriptor field, Object value, FlatRecordWriter flatRecordWriter, String fieldName, PassthroughWriteRecords rec) throws IOException {
        rec.passthroughMapKeyWriteRecord.reset();
        rec.passthroughMapKeyWriteRecord.setString("value", fieldName);
        int keyOrdinal = addRecord("MapKey", rec.passthroughMapKeyWriteRecord, flatRecordWriter);

        if (field.isRepeated()) {
            rec.multiValuePassthroughListRec.reset();

            for (Object item : (List<?>) value) {
                rec.passthroughMapValueWriteRecord.reset();
                rec.passthroughMapValueWriteRecord.setString("value", item.toString());
                int elementOrdinal = addRecord("String", rec.passthroughMapValueWriteRecord, flatRecordWriter);
                rec.multiValuePassthroughListRec.addElement(elementOrdinal);
            }

            int valueListOrdinal = addRecord("ListOfString", rec.multiValuePassthroughListRec, flatRecordWriter);
            rec.multiValuePassthroughMapRec.addEntry(keyOrdinal, valueListOrdinal);
        } else {
            rec.passthroughMapValueWriteRecord.reset();
            rec.passthroughMapValueWriteRecord.setString("value", value.toString());
            int valueOrdinal = addRecord("String", rec.passthroughMapValueWriteRecord, flatRecordWriter);
            rec.singleValuePassthroughMapRec.addEntry(keyOrdinal, valueOrdinal);
        }
    }

    private void addObjectField(Message message, FieldDescriptor field, Object value, FlatRecordWriter flatRecordWriter, ObjectMappedFieldPath mappedFieldPath) throws IOException {
        HollowObjectWriteRecord writeRec = mappedFieldPath.getWriteRecord();
        HollowObjectSchema schema = writeRec.getSchema();
        String fieldName = mappedFieldPath.getFieldName();
        int fieldPosition = mappedFieldPath.getFieldPosition();

        FieldProcessor processor = mappedFieldPath.getFieldProcessor();
        if (processor != null && value != null) {
            processor.processField(message, stateEngine, writeRec);
            return;
        }

        if (value == null) {
            return; // Skip null values
        }

        if (field.isRepeated()) {
            // Handle repeated fields (lists)
            int refOrdinal = parseCollection(message, (List<?>) value, flatRecordWriter, schema.getReferencedType(fieldPosition));
            writeRec.setReference(fieldName, refOrdinal);
        } else if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            // Check if this is a google.protobuf.*Value type that needs unwrapping
            Message msgValue = (Message) value;
            String msgTypeName = msgValue.getDescriptorForType().getName();
            if (isProtoValueType(msgValue.getDescriptorForType())) {
                // Unwrap *Value type
                Object unwrappedValue = unwrapValueType(msgValue);
                FieldDescriptor.JavaType unwrappedType = getUnwrappedJavaType(msgTypeName);

                // Check if field should be inlined or referenced
                if (schema.getFieldType(fieldPosition) == HollowObjectSchema.FieldType.REFERENCE) {
                    // Wrap in Hollow wrapper type (e.g., Integer, String)
                    String wrapperTypeName = schema.getReferencedType(fieldPosition);
                    int wrapperOrdinal = wrapPrimitiveValue(unwrappedType, unwrappedValue, wrapperTypeName, flatRecordWriter);
                    writeRec.setReference(fieldName, wrapperOrdinal);
                } else {
                    // Inline the primitive value
                    writePrimitiveValue(writeRec, fieldName, unwrappedType, unwrappedValue);
                }
            } else {
                // Regular nested message
                int refOrdinal = parseMessage(msgValue, flatRecordWriter, schema.getReferencedType(fieldPosition));
                writeRec.setReference(fieldName, refOrdinal);
            }
        } else if (schema.getFieldType(fieldPosition) == HollowObjectSchema.FieldType.REFERENCE) {
            // Primitive field with namespaced wrapper type (hollow_type_name option)
            // Wrap the primitive value in a wrapper object
            String wrapperTypeName = schema.getReferencedType(fieldPosition);
            int wrapperOrdinal = wrapPrimitiveValue(field.getJavaType(), value, wrapperTypeName, flatRecordWriter);
            writeRec.setReference(fieldName, wrapperOrdinal);
        } else {
            // Handle primitive types (inline)
            switch (field.getJavaType()) {
                case BOOLEAN:
                    writeRec.setBoolean(fieldName, (Boolean) value);
                    break;
                case INT:
                    writeRec.setInt(fieldName, (Integer) value);
                    break;
                case LONG:
                    writeRec.setLong(fieldName, (Long) value);
                    break;
                case FLOAT:
                    writeRec.setFloat(fieldName, (Float) value);
                    break;
                case DOUBLE:
                    writeRec.setDouble(fieldName, (Double) value);
                    break;
                case STRING:
                    writeRec.setString(fieldName, (String) value);
                    break;
                case BYTE_STRING:
                    writeRec.setBytes(fieldName, ((ByteString) value).toByteArray());
                    break;
                case ENUM:
                    // Store enum as string
                    writeRec.setString(fieldName, value.toString());
                    break;
                default:
                    throw new IOException("Unsupported field type: " + field.getJavaType());
            }
        }
    }

    /**
     * Check if a descriptor is a google.protobuf.*Value wrapper type.
     * Note: google.protobuf.Value (without a prefix) is NOT a wrapper type - it's a union type.
     */
    private boolean isProtoValueType(com.google.protobuf.Descriptors.Descriptor descriptor) {
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
     * Unwrap a google.protobuf.*Value message to get the underlying value.
     */
    private Object unwrapValueType(Message valueMessage) {
        // All *Value types have a "value" field
        com.google.protobuf.Descriptors.FieldDescriptor valueField =
            valueMessage.getDescriptorForType().findFieldByName("value");
        return valueMessage.getField(valueField);
    }

    /**
     * Get the JavaType for the unwrapped value of a *Value type.
     */
    private FieldDescriptor.JavaType getUnwrappedJavaType(String valueTypeName) {
        if (valueTypeName.equals("Int32Value") || valueTypeName.equals("UInt32Value")) {
            return FieldDescriptor.JavaType.INT;
        } else if (valueTypeName.equals("Int64Value") || valueTypeName.equals("UInt64Value")) {
            return FieldDescriptor.JavaType.LONG;
        } else if (valueTypeName.equals("FloatValue")) {
            return FieldDescriptor.JavaType.FLOAT;
        } else if (valueTypeName.equals("DoubleValue")) {
            return FieldDescriptor.JavaType.DOUBLE;
        } else if (valueTypeName.equals("BoolValue")) {
            return FieldDescriptor.JavaType.BOOLEAN;
        } else if (valueTypeName.equals("StringValue")) {
            return FieldDescriptor.JavaType.STRING;
        } else if (valueTypeName.equals("BytesValue")) {
            return FieldDescriptor.JavaType.BYTE_STRING;
        }
        throw new IllegalArgumentException("Unknown *Value type: " + valueTypeName);
    }

    /**
     * Write a primitive value to a record field.
     */
    private void writePrimitiveValue(HollowObjectWriteRecord writeRec, String fieldName,
                                      FieldDescriptor.JavaType javaType, Object value) {
        switch (javaType) {
            case BOOLEAN:
                writeRec.setBoolean(fieldName, (Boolean) value);
                break;
            case INT:
                writeRec.setInt(fieldName, (Integer) value);
                break;
            case LONG:
                writeRec.setLong(fieldName, (Long) value);
                break;
            case FLOAT:
                writeRec.setFloat(fieldName, (Float) value);
                break;
            case DOUBLE:
                writeRec.setDouble(fieldName, (Double) value);
                break;
            case STRING:
                writeRec.setString(fieldName, (String) value);
                break;
            case BYTE_STRING:
                writeRec.setBytes(fieldName, ((ByteString) value).toByteArray());
                break;
            default:
                throw new IllegalArgumentException("Unsupported primitive type: " + javaType);
        }
    }

    /**
     * Wrap a primitive value in a wrapper object (for namespaced types created by hollow_type_name).
     */
    private int wrapPrimitiveValue(FieldDescriptor.JavaType javaType, Object value, String wrapperTypeName, FlatRecordWriter flatRecordWriter) throws IOException {
        HollowObjectWriteRecord wrapperRec = (HollowObjectWriteRecord) getWriteRecord(wrapperTypeName);
        wrapperRec.reset();

        // Write the value to the wrapper's "value" field
        switch (javaType) {
            case BOOLEAN:
                wrapperRec.setBoolean("value", (Boolean) value);
                break;
            case INT:
                wrapperRec.setInt("value", (Integer) value);
                break;
            case LONG:
                wrapperRec.setLong("value", (Long) value);
                break;
            case FLOAT:
                wrapperRec.setFloat("value", (Float) value);
                break;
            case DOUBLE:
                wrapperRec.setDouble("value", (Double) value);
                break;
            case STRING:
                wrapperRec.setString("value", (String) value);
                break;
            case BYTE_STRING:
                wrapperRec.setBytes("value", ((ByteString) value).toByteArray());
                break;
            case ENUM:
                wrapperRec.setString("value", value.toString());
                break;
            default:
                throw new IOException("Unsupported primitive type for wrapper: " + javaType);
        }

        return addRecord(wrapperTypeName, wrapperRec, flatRecordWriter);
    }

    private int parseCollection(Message message, List<?> values, FlatRecordWriter flatRecordWriter, String collectionType) throws IOException {
        HollowSchema schema = hollowSchemas.get(collectionType);
        HollowWriteRecord collectionRec = getWriteRecord(collectionType);
        collectionRec.reset();

        if (schema instanceof HollowCollectionSchema) {
            HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
            String elementType = collectionSchema.getElementType();

            // Collect all element ordinals
            List<Integer> elementOrdinals = new java.util.ArrayList<Integer>();

            for (Object value : values) {
                int elementOrdinal;
                if (value instanceof Message) {
                    elementOrdinal = parseMessage((Message) value, flatRecordWriter, elementType);
                } else {
                    // Handle primitive types in collections
                    ObjectFieldMapping valueRec = getObjectFieldMapping(elementType);
                    ObjectMappedFieldPath fieldMapping = valueRec.getSingleFieldMapping();
                    HollowObjectWriteRecord writeRec = fieldMapping.getWriteRecord();
                    writeRec.reset();

                    String fieldName = fieldMapping.getFieldName();
                    int fieldPosition = fieldMapping.getFieldPosition();
                    HollowObjectSchema objSchema = writeRec.getSchema();

                    switch (objSchema.getFieldType(fieldPosition)) {
                        case BOOLEAN:
                            writeRec.setBoolean(fieldName, (Boolean) value);
                            break;
                        case INT:
                            writeRec.setInt(fieldName, (Integer) value);
                            break;
                        case LONG:
                            writeRec.setLong(fieldName, (Long) value);
                            break;
                        case FLOAT:
                            writeRec.setFloat(fieldName, (Float) value);
                            break;
                        case DOUBLE:
                            writeRec.setDouble(fieldName, (Double) value);
                            break;
                        case STRING:
                            writeRec.setString(fieldName, value.toString());
                            break;
                        default:
                            throw new IOException("Unsupported field type in collection: " + objSchema.getFieldType(fieldPosition));
                    }

                    elementOrdinal = valueRec.build(-1, flatRecordWriter);
                }

                elementOrdinals.add(elementOrdinal);
            }

            // Sort ordinals if ignoring list ordering (treats list like a set)
            if (ignoreListOrdering && collectionRec instanceof HollowListWriteRecord) {
                java.util.Collections.sort(elementOrdinals);
            }

            // Add ordinals to the collection record
            for (int ordinal : elementOrdinals) {
                if (collectionRec instanceof HollowListWriteRecord) {
                    ((HollowListWriteRecord) collectionRec).addElement(ordinal);
                } else {
                    ((HollowSetWriteRecord) collectionRec).addElement(ordinal);
                }
            }
        }

        return addRecord(collectionType, collectionRec, flatRecordWriter);
    }

    private int addCollection(Message message, FlatRecordWriter flatRecordWriter, String collectionType, HollowWriteRecord collectionRec) throws IOException {
        // Top-level collections are not common in Protocol Buffers since messages are typically objects.
        // Collections are usually represented as repeated fields within a message (handled by parseCollection).
        //
        // If you need this functionality, the implementation should iterate over the message's repeated
        // fields and add each element to the HollowListWriteRecord or HollowSetWriteRecord.
        throw new UnsupportedOperationException(
            "Top-level collection schemas are not commonly used with Protocol Buffers. " +
            "Use a wrapper message with a repeated field instead."
        );
    }

    private int addMap(Message message, FlatRecordWriter flatRecordWriter, String mapType, HollowMapWriteRecord mapRec) throws IOException {
        // Top-level map schemas are not common in Protocol Buffers.
        // Protocol Buffers represent maps as repeated message fields with 'key' and 'value' fields,
        // and these are typically nested within another message (handled in addObjectField).
        //
        // If you need top-level map support, iterate over the map entries in the message and
        // add each key-value pair to the HollowMapWriteRecord.
        throw new UnsupportedOperationException(
            "Top-level map schemas are not commonly used with Protocol Buffers. " +
            "Use a wrapper message with a map field instead."
        );
    }

    private int addRecord(String type, HollowWriteRecord rec, FlatRecordWriter flatRecordWriter) {
        if (flatRecordWriter != null) {
            HollowSchema schema = stateEngine.getSchema(type);
            return flatRecordWriter.write(schema, rec);
        }
        return stateEngine.add(type, rec);
    }

    private void initHollowWriteRecordsIfNecessary() {
        if (hollowWriteRecordsHolder.get() == null) {
            synchronized (this) {
                if (hollowWriteRecordsHolder.get() == null) {
                    Map<String, HollowWriteRecord> lookupMap = createWriteRecords(stateEngine);
                    hollowWriteRecordsHolder.set(lookupMap);
                    objectFieldMappingHolder.set(cloneFieldMappings());
                }
            }
        }
    }

    private static Map<String, HollowWriteRecord> createWriteRecords(HollowWriteStateEngine stateEngine) {
        Map<String, HollowWriteRecord> hollowWriteRecords = new HashMap<>();

        for (HollowSchema schema : stateEngine.getSchemas()) {
            switch (schema.getSchemaType()) {
                case LIST:
                    hollowWriteRecords.put(schema.getName(), new HollowListWriteRecord());
                    break;
                case MAP:
                    hollowWriteRecords.put(schema.getName(), new HollowMapWriteRecord());
                    break;
                case OBJECT:
                    hollowWriteRecords.put(schema.getName(), new HollowObjectWriteRecord((HollowObjectSchema) schema));
                    break;
                case SET:
                    hollowWriteRecords.put(schema.getName(), new HollowSetWriteRecord());
                    break;
            }
        }

        return hollowWriteRecords;
    }

    private Map<String, ObjectFieldMapping> cloneFieldMappings() {
        Map<String, ObjectFieldMapping> clonedMap = new HashMap<String, ObjectFieldMapping>();
        for (Map.Entry<String, ObjectFieldMapping> entry : canonicalObjectFieldMappings.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    ObjectFieldMapping getObjectFieldMapping(String type) throws IOException {
        Map<String, ObjectFieldMapping> objectFieldMappings = objectFieldMappingHolder.get();
        ObjectFieldMapping mapping = objectFieldMappings.get(type);
        if (mapping == null) {
            throw new IOException("WriteRecord for " + type + " not found. Make sure Schema Discovery is done correctly.");
        }
        return mapping;
    }

    HollowWriteRecord getWriteRecord(String type) throws IOException {
        Map<String, HollowWriteRecord> hollowWriteRecords = hollowWriteRecordsHolder.get();
        HollowWriteRecord wRec = hollowWriteRecords.get(type);
        if (wRec == null) {
            throw new IOException("WriteRecord for " + type + " not found. Make sure Schema Discovery is done correctly.");
        }
        return wRec;
    }

    private PassthroughWriteRecords getPassthroughWriteRecords() {
        PassthroughWriteRecords rec;
        rec = passthroughRecords.get();
        if (rec == null) {
            rec = new PassthroughWriteRecords();
            passthroughRecords.set(rec);
        }
        rec.passthroughRec.reset();
        rec.multiValuePassthroughMapRec.reset();
        rec.singleValuePassthroughMapRec.reset();
        return rec;
    }

    private class PassthroughWriteRecords {
        final HollowObjectWriteRecord passthroughRec;
        final HollowObjectWriteRecord passthroughMapKeyWriteRecord;
        final HollowObjectWriteRecord passthroughMapValueWriteRecord;
        final HollowMapWriteRecord singleValuePassthroughMapRec;
        final HollowMapWriteRecord multiValuePassthroughMapRec;
        final HollowListWriteRecord multiValuePassthroughListRec;

        public PassthroughWriteRecords() {
            this.passthroughRec = hollowSchemas.get("PassthroughData") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("PassthroughData")) : null;
            this.passthroughMapKeyWriteRecord = hollowSchemas.get("MapKey") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("MapKey")) : null;
            this.passthroughMapValueWriteRecord = hollowSchemas.get("String") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("String")) : null;
            this.singleValuePassthroughMapRec = new HollowMapWriteRecord();
            this.multiValuePassthroughMapRec = new HollowMapWriteRecord();
            this.multiValuePassthroughListRec = new HollowListWriteRecord();
        }
    }
}
