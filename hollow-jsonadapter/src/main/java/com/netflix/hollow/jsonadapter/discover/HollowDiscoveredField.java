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
package com.netflix.hollow.jsonadapter.discover;

import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class HollowDiscoveredField {
    FieldType fieldType;
    String referencedType;

    public HollowDiscoveredField(FieldType fieldType, String referencedType) {
        this.fieldType = fieldType;
        this.referencedType = referencedType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HollowDiscoveredField [fieldType=").append(fieldType).append(", referencedType=").append(referencedType).append("]");
        return builder.toString();
    }
}
