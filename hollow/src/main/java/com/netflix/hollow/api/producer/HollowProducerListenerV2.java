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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.producer.listener.CycleListener;

/**
 * An extension of {@link HollowProducerListener} to allow adding new methods without
 * breaking backwards compatability.
 * Subclasses should extend {@link AbstractHollowProducerListener} to avoid having to
 * override every method in this interface.
 * TODO(hollow3): Collapse this into HollowProducerListener.
 */
public interface HollowProducerListenerV2 extends HollowProducerListener {
    enum CycleSkipReason {
        NOT_PRIMARY_PRODUCER;
    }

    // CycleListener

    @Override
    default void onCycleSkip(CycleListener.CycleSkipReason reason) {
        CycleSkipReason r = reason == null ? null : CycleSkipReason.NOT_PRIMARY_PRODUCER;
        onCycleSkip(r);
    }

    /**
     * Called when a cycle is skipped.
     */
    void onCycleSkip(CycleSkipReason reason);
}
