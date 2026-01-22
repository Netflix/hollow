/*
 *  Copyright 2016-2025 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchemaComparator;
import java.util.*;

/**
 * Collects new field data for preserved ordinals during delta generation.
 * This data will be appended to the delta blob for consumers with updated schemas.
 */
public class DeltaSchemaAppendDataCollector {

    private final HollowWriteStateEngine stateEngine;
    private final Map<String, TypeAppendData> typeDataMap;
    private final Map<String, HollowObjectSchema> previousSchemas = new HashMap<>();

    public DeltaSchemaAppendDataCollector(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.typeDataMap = new HashMap<>();
    }

    /**
     * Collect appended data for all types with changes.
     * When schema evolution is detected (via previous schema tracking), collect only newly-added fields.
     * When no schema evolution, collect all fields (conservative approach - existing behavior).
     */
    public void collect() {
        // Load previous schemas from write state engine if available
        Map<String, HollowObjectSchema> capturedSchemas = stateEngine.getPreviousSchemas();
        if (capturedSchemas != null) {
            previousSchemas.putAll(capturedSchemas);
        }

        for (HollowTypeWriteState typeWriteState : stateEngine.getOrderedTypeStates()) {
            // Only process types that have changed and are OBJECT types
            if (typeWriteState.hasChangedSinceLastCycle() &&
                typeWriteState instanceof HollowObjectTypeWriteState) {
                HollowObjectTypeWriteState objectState = (HollowObjectTypeWriteState) typeWriteState;
                collectFromObjectType(objectState);
            }
        }
    }

    private void collectFromObjectType(HollowObjectTypeWriteState typeState) {
        HollowObjectSchema currentSchema = typeState.getSchema();
        String typeName = currentSchema.getName();

        // Skip if no fields
        if (currentSchema.numFields() == 0) {
            return;
        }

        // Check if schema has evolved
        boolean schemaEvolved = hasSchemaEvolved(typeName);
        Set<String> fieldsToCollect = new HashSet<>();

        if (schemaEvolved) {
            // Schema evolution case: collect only newly-added fields
            HollowObjectSchema previousSchema = previousSchemas.get(typeName);
            if (previousSchema == null) {
                throw new IllegalStateException("Schema evolution detected for type '" + typeName +
                    "' but previous schema is missing from the map");
            }
            Set<String> addedFields = HollowSchemaComparator.findAddedFields(previousSchema, currentSchema);
            fieldsToCollect.addAll(addedFields);
        } else {
            // Normal case (no schema evolution): collect all fields (conservative approach)
            for (int i = 0; i < currentSchema.numFields(); i++) {
                fieldsToCollect.add(currentSchema.getFieldName(i));
            }
        }

        if (fieldsToCollect.isEmpty()) {
            return; // Nothing to collect for this type
        }

        TypeAppendData typeData = new TypeAppendData();
        typeData.typeName = typeName;
        typeData.fields = new ArrayList<>();

        // Collect field values for the selected fields
        for (String fieldName : fieldsToCollect) {
            int fieldIndex = currentSchema.getPosition(fieldName);
            if (fieldIndex == -1) {
                continue; // Field not found (shouldn't happen)
            }

            FieldAppendData fieldData = collectFieldData(typeState, fieldIndex);
            if (fieldData != null && !fieldData.ordinalToValue.isEmpty()) {
                typeData.fields.add(fieldData);
            }
        }

        if (!typeData.fields.isEmpty()) {
            typeDataMap.put(typeData.typeName, typeData);
        }
    }

    private FieldAppendData collectFieldData(HollowObjectTypeWriteState typeState, int fieldIdx) {
        HollowObjectSchema schema = typeState.getSchema();

        FieldAppendData fieldData = new FieldAppendData();
        fieldData.fieldName = schema.getFieldName(fieldIdx);
        fieldData.fieldType = schema.getFieldType(fieldIdx);
        fieldData.referencedType = schema.getReferencedType(fieldIdx);
        fieldData.ordinalToValue = new HashMap<>();

        // Collect values for preserved ordinals (existed in both previous and current cycles)
        ThreadSafeBitSet previousPopulated = typeState.getPreviousCyclePopulatedBitSet();
        ThreadSafeBitSet currentPopulated = typeState.getPopulatedBitSet();

        int ordinal = 0;
        while (ordinal <= typeState.maxOrdinal) {
            // Only process preserved ordinals
            if (previousPopulated.get(ordinal) && currentPopulated.get(ordinal)) {
                Object value = extractFieldValue(typeState, ordinal, fieldIdx);
                if (value != null) {
                    fieldData.ordinalToValue.put(ordinal, value);
                }
            }
            ordinal++;
        }

        return fieldData;
    }

    private Object extractFieldValue(HollowObjectTypeWriteState typeState, int ordinal, int fieldIdx) {
        HollowObjectSchema schema = typeState.getSchema();
        FieldType fieldType = schema.getFieldType(fieldIdx);

        try {
            long pointer = typeState.ordinalMap.getPointerForData(ordinal);
            ByteData data = typeState.ordinalMap.getByteData().getUnderlyingArray();

            // Navigate to the correct field by skipping previous fields
            for (int i = 0; i < fieldIdx; i++) {
                pointer = skipField(data, pointer, schema.getFieldType(i));
            }

            // Now read the target field
            return readFieldValue(data, pointer, fieldType);
        } catch (Exception e) {
            // If extraction fails, return null (field will be skipped)
            return null;
        }
    }

    private long skipField(ByteData data, long pointer, FieldType fieldType) {
        switch (fieldType) {
            case BOOLEAN:
                return pointer + 1;
            case FLOAT:
                return pointer + 4;
            case DOUBLE:
                return pointer + 8;
            case INT:
            case LONG:
            case REFERENCE:
                if (VarInt.readVNull(data, pointer)) {
                    return pointer + 1;
                } else {
                    long value = VarInt.readVLong(data, pointer);
                    return pointer + VarInt.sizeOfVLong(value);
                }
            case STRING:
            case BYTES:
                if (VarInt.readVNull(data, pointer)) {
                    return pointer + 1;
                } else {
                    int length = VarInt.readVInt(data, pointer);
                    return pointer + VarInt.sizeOfVInt(length) + length;
                }
            default:
                return pointer;
        }
    }

    private Object readFieldValue(ByteData data, long pointer, FieldType fieldType) {
        switch (fieldType) {
            case BOOLEAN:
                if (VarInt.readVNull(data, pointer)) {
                    return null;
                }
                return data.get(pointer) == 1;
            case FLOAT:
                int intBits = (int) data.readIntBits(pointer);
                return Float.intBitsToFloat(intBits);
            case DOUBLE:
                long longBits = data.readLongBits(pointer);
                return Double.longBitsToDouble(longBits);
            case INT:
            case REFERENCE:
                if (VarInt.readVNull(data, pointer)) {
                    return null;
                }
                long vLong = VarInt.readVLong(data, pointer);
                return (int) vLong;
            case LONG:
                if (VarInt.readVNull(data, pointer)) {
                    return null;
                }
                return VarInt.readVLong(data, pointer);
            case STRING:
                if (VarInt.readVNull(data, pointer)) {
                    return null;
                }
                int length = VarInt.readVInt(data, pointer);
                pointer += VarInt.sizeOfVInt(length);
                byte[] bytes = new byte[length];
                for (int i = 0; i < length; i++) {
                    bytes[i] = data.get(pointer + i);
                }
                return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            case BYTES:
                if (VarInt.readVNull(data, pointer)) {
                    return null;
                }
                int byteLength = VarInt.readVInt(data, pointer);
                pointer += VarInt.sizeOfVInt(byteLength);
                byte[] byteArray = new byte[byteLength];
                for (int i = 0; i < byteLength; i++) {
                    byteArray[i] = data.get(pointer + i);
                }
                return byteArray;
            default:
                return null;
        }
    }

    /**
     * @return true if any data was collected
     */
    public boolean hasData() {
        return !typeDataMap.isEmpty();
    }

    /**
     * @return the collected type data map
     */
    public Map<String, TypeAppendData> getTypeDataMap() {
        return typeDataMap;
    }

    /**
     * Set the previous schema for a type (used for detecting schema evolution).
     * This is typically called automatically during restoration from read state.
     *
     * @param typeName the type name
     * @param previousSchema the schema from the previous cycle
     */
    public void setPreviousSchema(String typeName, HollowObjectSchema previousSchema) {
        previousSchemas.put(typeName, previousSchema);
    }

    /**
     * Check if a type's schema has evolved since the previous cycle.
     *
     * @param typeName the type name to check
     * @return true if schema has changed, false otherwise
     */
    public boolean hasSchemaEvolved(String typeName) {
        HollowObjectSchema previousSchema = previousSchemas.get(typeName);
        if (previousSchema == null) {
            return false; // No previous schema known, assume no evolution
        }

        HollowTypeWriteState currentWriteState = stateEngine.getTypeState(typeName);
        if (currentWriteState == null || !(currentWriteState instanceof HollowObjectTypeWriteState)) {
            return false;
        }

        HollowObjectSchema currentSchema = (HollowObjectSchema) currentWriteState.getSchema();

        // Check if field counts differ (handles field removals)
        if (currentSchema.numFields() != previousSchema.numFields()) {
            return true;
        }

        // Use HollowSchemaComparator to detect added fields
        Set<String> addedFields = HollowSchemaComparator.findAddedFields(previousSchema, currentSchema);
        return !addedFields.isEmpty();
    }

    /**
     * Collect schema diffs for all types with schema changes.
     * Only collects if schema definitions should be included in deltas.
     *
     * @return map of type name to schema diff
     */
    public Map<String, HollowSchemaDiff> collectSchemaDiffs() {
        Map<String, HollowSchemaDiff> schemaDiffs = new HashMap<>();

        // Only collect schema diffs if feature is enabled for schema definitions
        HollowDeltaSchemaAppendConfig config = stateEngine.getDeltaSchemaAppendConfig();
        if (config == null || !config.shouldIncludeSchemaDefinitions()) {
            return schemaDiffs;  // Return empty map
        }

        for (HollowTypeWriteState typeWriteState : stateEngine.getOrderedTypeStates()) {
            if (typeWriteState instanceof HollowObjectTypeWriteState) {
                HollowObjectTypeWriteState objectState = (HollowObjectTypeWriteState) typeWriteState;
                HollowObjectSchema currentSchema = objectState.getSchema();
                String typeName = currentSchema.getName();

                HollowObjectSchema previousSchema = previousSchemas.get(typeName);
                if (previousSchema != null && !previousSchema.equals(currentSchema)) {
                    try {
                        HollowSchemaDiff diff = HollowSchemaDiff.compute(previousSchema, currentSchema);
                        if (!diff.isEmpty()) {
                            schemaDiffs.put(typeName, diff);
                        }
                    } catch (IllegalArgumentException e) {
                        // Schema change not supported (e.g., field removal, type change)
                        // Log and skip - will require snapshot
                        System.err.println("Warning: Unsupported schema change for type " + typeName + ": " + e.getMessage());
                    }
                }
            }
        }

        return schemaDiffs;
    }

    /**
     * Calculate total byte size of the appended section.
     * Used to write the length prefix.
     */
    public long calculateTotalSize() {
        if (!hasData()) {
            return 0;
        }

        long size = 0;

        // Size of numTypes
        size += estimateVarIntSize(typeDataMap.size());

        for (TypeAppendData typeData : typeDataMap.values()) {
            // Type name
            size += estimateVarIntSize(typeData.typeName.length());
            size += typeData.typeName.getBytes().length;

            // Num fields
            size += estimateVarIntSize(typeData.fields.size());

            for (FieldAppendData fieldData : typeData.fields) {
                // Field name
                size += estimateVarIntSize(fieldData.fieldName.length());
                size += fieldData.fieldName.getBytes().length;

                // Field type ordinal
                size += 1;

                // Referenced type if REFERENCE
                if (fieldData.fieldType == FieldType.REFERENCE) {
                    size += estimateVarIntSize(fieldData.referencedType.length());
                    size += fieldData.referencedType.getBytes().length;
                }

                // Num ordinals
                size += estimateVarIntSize(fieldData.ordinalToValue.size());

                // Ordinal-value pairs
                for (Map.Entry<Integer, Object> entry : fieldData.ordinalToValue.entrySet()) {
                    size += estimateVarIntSize(entry.getKey());
                    size += estimateValueSize(fieldData.fieldType, entry.getValue());
                }
            }
        }

        return size;
    }

    private int estimateVarIntSize(int value) {
        // Conservative estimate for VarInt size
        if (value < 128) return 1;
        if (value < 16384) return 2;
        if (value < 2097152) return 3;
        if (value < 268435456) return 4;
        return 5;
    }

    private long estimateValueSize(FieldType fieldType, Object value) {
        switch (fieldType) {
            case REFERENCE:
            case INT:
                return estimateVarIntSize((Integer) value);
            case LONG:
                return estimateVarIntSize(((Long) value).intValue()); // Rough estimate
            case BOOLEAN:
                return 1;
            case FLOAT:
                return 4;
            case DOUBLE:
                return 8;
            case STRING:
                String s = (String) value;
                byte[] bytes = s.getBytes();
                return estimateVarIntSize(bytes.length) + bytes.length;
            case BYTES:
                byte[] byteArray = (byte[]) value;
                return estimateVarIntSize(byteArray.length) + byteArray.length;
            default:
                return 0;
        }
    }

    /**
     * Internal data structure for a type's appended data.
     */
    public static class TypeAppendData {
        public String typeName;
        public List<FieldAppendData> fields;
    }

    /**
     * Internal data structure for a field's appended data.
     */
    public static class FieldAppendData {
        public String fieldName;
        public FieldType fieldType;
        public String referencedType; // for REFERENCE fields
        public Map<Integer, Object> ordinalToValue; // ordinal -> value
    }
}
