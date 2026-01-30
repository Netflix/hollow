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
package com.netflix.hollow.core.schema;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility for comparing Hollow schemas to detect differences.
 */
public class HollowSchemaComparator {

    /**
     * Finds fields that exist in newSchema but not in oldSchema.
     *
     * @param oldSchema the previous schema
     * @param newSchema the current schema
     * @return set of field names that were added (empty if no fields added)
     * @throws IllegalArgumentException if oldSchema is null
     * @throws IllegalArgumentException if newSchema is null
     * @throws IllegalArgumentException if schema names don't match
     */
    public static Set<String> findAddedFields(HollowObjectSchema oldSchema, HollowObjectSchema newSchema) {
        if (oldSchema == null) {
            throw new IllegalArgumentException("oldSchema cannot be null");
        }
        if (newSchema == null) {
            throw new IllegalArgumentException("newSchema cannot be null");
        }
        if (!oldSchema.getName().equals(newSchema.getName())) {
            throw new IllegalArgumentException("Cannot find added fields of two schemas with different names!");
        }

        Set<String> oldFieldNames = new HashSet<>();
        for (int i = 0; i < oldSchema.numFields(); i++) {
            oldFieldNames.add(oldSchema.getFieldName(i));
        }

        Set<String> addedFields = new HashSet<>();
        for (int i = 0; i < newSchema.numFields(); i++) {
            String fieldName = newSchema.getFieldName(i);
            if (!oldFieldNames.contains(fieldName)) {
                addedFields.add(fieldName);
            }
        }

        return addedFields;
    }
}
