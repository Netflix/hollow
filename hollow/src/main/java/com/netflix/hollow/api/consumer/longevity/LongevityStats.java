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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowConstants;

/**
 * A held-mode consumer's version holds and the copies kept for them, as of {@link HollowConsumer#getLongevityStats()}.
 * Open holds and reads after release count every state engine of the consumer, since holds outlive a state engine that
 * a double snapshot replaces; the rest describes the current state engine.
 */
public interface LongevityStats {

    /**
     * @return the approximate heap footprint of the copies kept for holds behind the state engine, of the records each
     * delta since the oldest hold removed
     */
    long getRetainedCopyBytes();

    /** @return the ordinals, across types, that direct lookup maps to their copies; 0 while it is off */
    int getDirectLookupOrdinals();

    /** @return the holds not yet closed, on any of the consumer's state engines */
    int getOpenHolds();

    /** @return the version of the state engine's oldest open hold, or {@link HollowConstants#VERSION_NONE} if none */
    long getOldestHoldVersion();

    /**
     * @return the deltas, forward and reverse, that the state engine has applied past its oldest open hold, which
     * {@link HollowConsumer.HeldVersionsConfig#maxDeltasPastOldestHold()} limits; 0 with no hold open
     */
    int getDeltasPastOldestHold();

    /** @return whether held versions behind the state engine are read through direct lookup, as of the last refresh */
    boolean isDirectLookupActive();

    /**
     * @return the reads through holds after their release, which read the state engine's version instead of the held
     * one; a caller bug, so this should stay 0
     */
    long getReadsAfterRelease();
}
