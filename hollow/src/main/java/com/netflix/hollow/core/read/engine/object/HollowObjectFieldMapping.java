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
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * Maps fields between schemas when applying deltas with schema changes.
 *
 * This class provides bidirectional mappings between:
 * - Target schema (the schema being written to)
 * - From schema (the previous state schema)
 * - Delta schema (the schema in the delta being applied)
 *
 * Not intended for external consumption.
 */
class HollowObjectFieldMapping {
    // Maps target field index -> from field index (-1 if new field)
    private final int[] targetToFromFieldIndex;

    // Maps target field index -> delta field index (-1 if not in delta)
    private final int[] targetToDeltaFieldIndex;

    // Maps from field index -> target field index (-1 if field removed)
    private final int[] fromToTargetFieldIndex;

    /**
     * Creates a field mapping between three schemas.
     *
     * @param targetSchema the schema being written to
     * @param fromSchema the previous state schema
     * @param deltaSchema the schema in the delta being applied
     */
    public HollowObjectFieldMapping(
        HollowObjectSchema targetSchema,
        HollowObjectSchema fromSchema,
        HollowObjectSchema deltaSchema
    ) {
        targetToFromFieldIndex = new int[targetSchema.numFields()];
        targetToDeltaFieldIndex = new int[targetSchema.numFields()];
        fromToTargetFieldIndex = new int[fromSchema.numFields()];

        // Build bidirectional mappings by field name
        for (int i = 0; i < targetSchema.numFields(); i++) {
            String fieldName = targetSchema.getFieldName(i);
            targetToFromFieldIndex[i] = fromSchema.getPosition(fieldName);
            targetToDeltaFieldIndex[i] = deltaSchema.getPosition(fieldName);
        }

        for (int i = 0; i < fromSchema.numFields(); i++) {
            String fieldName = fromSchema.getFieldName(i);
            fromToTargetFieldIndex[i] = targetSchema.getPosition(fieldName);
        }
    }

    /**
     * Gets the field index in the from schema for a given target field index.
     *
     * @param targetFieldIndex the field index in the target schema
     * @return the field index in the from schema, or -1 if this is a new field
     */
    public int getFromFieldIndex(int targetFieldIndex) {
        return targetToFromFieldIndex[targetFieldIndex];
    }

    /**
     * Gets the field index in the delta schema for a given target field index.
     *
     * @param targetFieldIndex the field index in the target schema
     * @return the field index in the delta schema, or -1 if field not in delta
     */
    public int getDeltaFieldIndex(int targetFieldIndex) {
        return targetToDeltaFieldIndex[targetFieldIndex];
    }

    /**
     * Checks if a field is new (not present in the from schema).
     *
     * @param targetFieldIndex the field index in the target schema
     * @return true if this is a new field
     */
    public boolean isNewField(int targetFieldIndex) {
        return targetToFromFieldIndex[targetFieldIndex] == -1;
    }

    /**
     * Checks if there are any schema changes between from and target schemas.
     *
     * @return true if schemas differ (fields added, removed, or reordered)
     */
    public boolean hasSchemaChanges() {
        for (int mapping : targetToFromFieldIndex) {
            if (mapping == -1) return true;
        }
        return false;
    }
}
