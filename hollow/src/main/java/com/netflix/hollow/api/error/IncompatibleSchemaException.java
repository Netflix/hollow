/*
 *
 *  Copyright 2018 Netflix, Inc.
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
package com.netflix.hollow.api.error;

/**
 * An exception thrown when trying to compare two versions of an object with incompatible schema.
 */
public class IncompatibleSchemaException extends HollowException {
    private final String typeName;
    private final String fieldName;
    private final String fieldType;
    private final String otherType;

    public IncompatibleSchemaException(String typeName, String fieldName, String fieldType,
            String otherType) {
        super("No common schema exists for " + typeName + ": field " + fieldName
                + " has unmatched types: " + fieldType + " vs " + otherType);
        this.typeName = typeName;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.otherType = otherType;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public String getOtherType() {
        return this.otherType;
    }
}
