/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.consumer.longevity;

import java.util.BitSet;
import java.util.Set;

/**
 * The changes between the versions of two {@link HollowVersionHold}s, by ordinal, from
 * {@link HollowVersionHold#diffFrom(HollowVersionHold)}.
 * <p>
 * An ordinal is removed if a delta between the versions removed the record the older version has there, and added if
 * a delta between them added the record the newer version has there. It can be both, when its record changed or a
 * delta restored the record an earlier one removed, so apply removals before additions.
 */
public interface HollowVersionDiff {

    /**
     * @return the older hold's version
     */
    long getFromVersion();

    /**
     * @return the newer hold's version
     */
    long getToVersion();

    /**
     * @return false if the holds read different state engines, whose ordinals are unrelated: rebuild from
     * {@link HollowVersionHold#getPopulatedOrdinals(String)} instead
     */
    boolean isIncremental();

    /**
     * @return how many deltas the consumer applied between the two holds, or -1 if the diff is not incremental
     */
    int getDeltaCount();

    /**
     * @return the types with removed or added ordinals
     * @throws IllegalStateException if the diff is not incremental
     */
    Set<String> getChangedTypes();

    /**
     * @param type the type name
     * @return the removed ordinals, empty for a type without changes; read their records through the older hold
     * @throws IllegalStateException if the diff is not incremental
     */
    BitSet getRemovedOrdinals(String type);

    /**
     * @param type the type name
     * @return the added ordinals, empty for a type without changes; read their records through the newer hold
     * @throws IllegalStateException if the diff is not incremental
     */
    BitSet getAddedOrdinals(String type);
}
