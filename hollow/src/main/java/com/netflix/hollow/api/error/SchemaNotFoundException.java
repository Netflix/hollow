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

import java.util.Collection;

/**
 * An exception thrown when trying to use a schema that does not exist.
 */
public class SchemaNotFoundException extends HollowException {
    private final String typeName;
    private final Collection<String> availableTypes;

    public SchemaNotFoundException(String typeName, Collection<String> availableTypes) {
        super("Could not find schema for " + typeName + " - " + getMessageSuffix(availableTypes));
        this.typeName = typeName;
        this.availableTypes = availableTypes;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public Collection<String> getAvailableTypes() {
        return this.availableTypes;
    }

    private static final String getMessageSuffix(Collection<String> availableTypes) {
        if (availableTypes.isEmpty()) {
            return "empty type state, make sure your namespace has published versions";
        } else {
            return "available schemas: " + availableTypes;
        }
    }
}
