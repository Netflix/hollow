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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class HollowHashIndexField {
    private final int baseIteratorFieldIdx;
    private final FieldPathSegment[] schemaFieldPositionPath;
    private final HollowTypeDataAccess baseDataAccess;
    private final FieldType fieldType;

    public HollowHashIndexField(int baseIteratorFieldIdx, FieldPathSegment[] remainingPath, HollowTypeDataAccess baseDataAccess, FieldType fieldType) {
        this.baseIteratorFieldIdx = baseIteratorFieldIdx;
        this.schemaFieldPositionPath = remainingPath;
        this.baseDataAccess = baseDataAccess;
        this.fieldType = fieldType;
    }

    public HollowTypeDataAccess getBaseDataAccess() {
        return baseDataAccess;
    }

    public int getBaseIteratorFieldIdx() {
        return baseIteratorFieldIdx;
    }

    public FieldPathSegment[] getSchemaFieldPositionPath() {
        return schemaFieldPositionPath;
    }

    FieldPathSegment getLastFieldPositionPathElement() {
        return schemaFieldPositionPath[schemaFieldPositionPath.length - 1];
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    static class FieldPathSegment {
        /**
         * Field position for this segment of the path. For path {@code actor.name},
         * {@code actor} is 0 and {@code name} is 1.
         */
        private final int fieldPosition;

        /**
         * For the path {@code actor.name}, position 0 is {@code actor} and the data access is
         * {@code ThingThatReferencesActorDataAccess}. For {@code name}, position is 1 and data access
         * is {@code ActorTypeDataAccess}.
         */
        private final HollowObjectTypeDataAccess objectTypeDataAccess;

        FieldPathSegment(int fieldPosition, HollowObjectTypeDataAccess objectTypeDataAccess) {
            this.fieldPosition = fieldPosition;
            this.objectTypeDataAccess = objectTypeDataAccess;
        }

        /**
         * @param ordinal ordinal of record containing the desired field.
         * @return ordinal of the record referenced by the field
         */
        int getOrdinalForField(int ordinal) {
            return this.objectTypeDataAccess.readOrdinal(ordinal, fieldPosition);
        }

        int getSegmentFieldPosition() {
            return fieldPosition;
        }

        HollowObjectTypeDataAccess getObjectTypeDataAccess() {
            return objectTypeDataAccess;
        }
    }

}