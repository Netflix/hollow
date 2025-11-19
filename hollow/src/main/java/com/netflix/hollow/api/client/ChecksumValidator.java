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
package com.netflix.hollow.api.client;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates consumer state integrity by comparing computed checksum against producer checksum.
 */
public class ChecksumValidator {
    private static final Logger LOG = Logger.getLogger(ChecksumValidator.class.getName());
    private static final String CHECKSUM_METADATA_KEY = "hollow.checksum";
    private static final String TYPE_CHECKSUM_PREFIX = "hollow.checksum.";

    /**
     * Validates that the consumer state checksum matches the producer checksum from metadata.
     *
     * @param stateEngine current consumer state engine
     * @param announcementMetadata metadata from producer announcement
     * @param computedChecksum checksum computed from consumer state
     * @return true if checksums match or no checksum in metadata; false if mismatch
     */
    public boolean validate(HollowReadStateEngine stateEngine,
                           Map<String, String> announcementMetadata,
                           HollowChecksum computedChecksum) {
        if (announcementMetadata == null || !announcementMetadata.containsKey(CHECKSUM_METADATA_KEY)) {
            LOG.log(Level.FINE, "No checksum in announcement metadata, skipping validation");
            return true;
        }

        String producerChecksumStr = announcementMetadata.get(CHECKSUM_METADATA_KEY);
        int producerChecksum;
        try {
            producerChecksum = Integer.parseInt(producerChecksumStr);
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Invalid checksum format in metadata: " + producerChecksumStr, e);
            return true; // Don't fail on malformed metadata
        }

        int consumerChecksum = computedChecksum.intValue();

        if (producerChecksum != consumerChecksum) {
            LOG.log(Level.WARNING, String.format(
                "Checksum mismatch detected! Producer: %d, Consumer: %d, Version: %s",
                producerChecksum, consumerChecksum,
                stateEngine.getCurrentRandomizedTag()
            ));
            return false;
        }

        LOG.log(Level.FINE, "Checksum validation passed: " + consumerChecksum);
        return true;
    }

    /**
     * Validates consumer state using per-type checksums for faster mismatch detection.
     * Falls back to full checksum validation if per-type checksums not available.
     *
     * @param stateEngine current consumer state engine
     * @param announcementMetadata metadata from producer announcement
     * @param computedChecksum checksum computed from consumer state
     * @return IncrementalResult with validation status and mismatched types
     */
    public IncrementalResult validateIncremental(HollowReadStateEngine stateEngine,
                                                 Map<String, String> announcementMetadata,
                                                 HollowChecksum computedChecksum) {
        if (announcementMetadata == null) {
            LOG.log(Level.FINE, "No announcement metadata, skipping incremental validation");
            return new IncrementalResult(true, java.util.Collections.emptySet());
        }

        // Check if per-type checksums are available
        boolean hasPerTypeChecksums = announcementMetadata.keySet().stream()
            .anyMatch(key -> key.startsWith(TYPE_CHECKSUM_PREFIX));

        if (!hasPerTypeChecksums) {
            // Fall back to full checksum validation
            boolean valid = validate(stateEngine, announcementMetadata, computedChecksum);
            return new IncrementalResult(valid, java.util.Collections.emptySet());
        }

        // Perform per-type validation
        java.util.Set<String> mismatchedTypes = new java.util.HashSet<>();

        for (Map.Entry<String, String> entry : announcementMetadata.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(TYPE_CHECKSUM_PREFIX)) {
                continue;
            }

            String typeName = key.substring(TYPE_CHECKSUM_PREFIX.length());
            int producerChecksum;

            try {
                producerChecksum = Integer.parseInt(entry.getValue());
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "Invalid checksum format for type " + typeName + ": " + entry.getValue(), e);
                continue;
            }

            int consumerChecksum = computedChecksum.getTypeChecksum(typeName);

            if (producerChecksum != consumerChecksum) {
                LOG.log(Level.WARNING, String.format(
                    "Type checksum mismatch! Type: %s, Producer: %d, Consumer: %d",
                    typeName, producerChecksum, consumerChecksum
                ));
                mismatchedTypes.add(typeName);
            }
        }

        boolean valid = mismatchedTypes.isEmpty();
        if (valid) {
            LOG.log(Level.FINE, "Incremental checksum validation passed for all types");
        } else {
            LOG.log(Level.WARNING, "Incremental checksum validation failed for types: " + mismatchedTypes);
        }

        return new IncrementalResult(valid, mismatchedTypes);
    }

    /**
     * Result of incremental checksum validation.
     */
    public static class IncrementalResult {
        private final boolean valid;
        private final java.util.Set<String> mismatchedTypes;

        public IncrementalResult(boolean valid, java.util.Set<String> mismatchedTypes) {
            this.valid = valid;
            this.mismatchedTypes = mismatchedTypes;
        }

        public boolean isValid() {
            return valid;
        }

        public java.util.Set<String> getMismatchedTypes() {
            return java.util.Collections.unmodifiableSet(mismatchedTypes);
        }
    }
}
