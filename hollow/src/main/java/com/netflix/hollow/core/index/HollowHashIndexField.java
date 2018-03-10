/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class HollowHashIndexField {
    private final int baseIteratorFieldIdx;
    private final int schemaFieldPositionPath[];
    private final HollowTypeReadState baseDataAccess;
    private final FieldType fieldType;

    public HollowHashIndexField(int baseIteratorFieldIdx, int[] remainingPath, HollowTypeReadState baseDataAccess, FieldType fieldType) {
        this.baseIteratorFieldIdx = baseIteratorFieldIdx;
        this.schemaFieldPositionPath = remainingPath;
        this.baseDataAccess = baseDataAccess;
        this.fieldType = fieldType;
    }

    public HollowTypeReadState getBaseDataAccess() {
        return baseDataAccess;
    }

    public int getBaseIteratorFieldIdx() {
        return baseIteratorFieldIdx;
    }

    public int[] getSchemaFieldPositionPath() {
        return schemaFieldPositionPath;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

}