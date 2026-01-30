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

/**
 * Configuration for delta schema append feature.
 * When enabled, delta blobs will include appended data for new fields on preserved ordinals,
 * allowing consumers with updated schemas to receive values for newly added fields.
 *
 * This feature maintains backward compatibility - old consumers can skip the appended data
 * at O(1) cost, while new consumers can opt-in to consuming the schema evolution data.
 */
public class HollowDeltaSchemaAppendConfig {
    private final boolean enabled;
    private final boolean includeSchemaDefinitions;

    /**
     * Create a new configuration.
     *
     * @param enabled whether to enable delta schema append feature
     */
    public HollowDeltaSchemaAppendConfig(boolean enabled) {
        this(enabled, false);  // Default to field values only, no schema definitions
    }

    /**
     * Create a new configuration with control over schema definitions.
     *
     * @param enabled whether to enable delta schema append feature
     * @param includeSchemaDefinitions whether to include schema definitions in deltas
     */
    public HollowDeltaSchemaAppendConfig(boolean enabled, boolean includeSchemaDefinitions) {
        this.enabled = enabled;
        this.includeSchemaDefinitions = includeSchemaDefinitions;
    }

    /**
     * @return true if the feature is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return true if schema definitions should be included in deltas
     */
    public boolean shouldIncludeSchemaDefinitions() {
        return enabled && includeSchemaDefinitions;
    }

    @Override
    public String toString() {
        return "HollowDeltaSchemaAppendConfig{enabled=" + enabled +
               ", includeSchemaDefinitions=" + includeSchemaDefinitions + "}";
    }
}
