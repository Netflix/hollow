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
package com.netflix.hollow.api.producer.listener;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import java.time.Duration;

/**
 * A listener of integrity check events associated with the producer integrity check stage.
 * <p>
 * An integrity check listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface IntegrityCheckListener extends HollowProducerEventListener {
    /**
     * Called when the {@code HollowProducer} has begun checking the integrity of the {@code HollowBlob}s produced this cycle.
     *
     * @param version version to be checked
     */
    void onIntegrityCheckStart(long version);

    /**
     * Called after the integrity check stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the previous snapshot, current snapshot, delta, and reverse-delta {@code HollowBlob}s are all internally consistent.
     *
     * @param status CycleStatus of the integrity check stage. {@link Status#getType()} will return {@code SUCCESS}
     * when the blobs are internally consistent; @{code FAIL} otherwise.
     * @param readState the read state
     * @param version version that was checked
     * @param elapsed duration of the integrity check stage in {@code unit} units
     */
    void onIntegrityCheckComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed);
}
