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

import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.protoadapter.field.FieldProcessor;

/**
 * Maps a field path from a Protocol Buffer message to a Hollow object field.
 */
public class ObjectMappedFieldPath {

    private final HollowObjectWriteRecord rec;
    private final String unmappedTypeName;
    private final String unmappedFieldName;
    private final String fieldName;
    private final int fieldPosition;
    private FieldProcessor fieldProcessor;

    public ObjectMappedFieldPath(HollowObjectWriteRecord rec, String fieldName, String unmappedTypeName, String unmappedFieldName, int fieldPosition, FieldProcessor fieldProcessor) {
        this.rec = rec;
        this.unmappedTypeName = unmappedTypeName;
        this.unmappedFieldName = unmappedFieldName;
        this.fieldName = fieldName;
        this.fieldPosition = fieldPosition;
        this.fieldProcessor = fieldProcessor;
    }

    public HollowObjectWriteRecord getWriteRecord() {
        return rec;
    }

    public String getTypeName() {
        return rec.getSchema().getName();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getUnmappedTypeName() {
        return unmappedTypeName;
    }

    public String getUnmappedFieldName() {
        return unmappedFieldName;
    }

    public int getFieldPosition() {
        return fieldPosition;
    }

    public FieldProcessor getFieldProcessor() {
        return fieldProcessor;
    }

    public void setFieldProcessor(FieldProcessor fieldProcessor) {
        this.fieldProcessor = fieldProcessor;
    }
}
