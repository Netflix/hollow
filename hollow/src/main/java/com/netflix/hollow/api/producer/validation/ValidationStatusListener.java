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
package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.HollowProducerEventListener;
import java.time.Duration;

/**
 * A listener of validation status events associated with the producer validation status stage.
 * <p>
 * A validation status listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface ValidationStatusListener extends HollowProducerEventListener {
    /**
     * A receiver of a validation status start event.  Called when validation has started and before
     * a {@link ValidatorListener#onValidate(HollowProducer.ReadState) validator} event is emitted to all
     * registered {@link ValidatorListener validator} listeners.
     *
     * @param version the version
     */
    void onValidationStatusStart(long version);

    /**
     * A receiver of a validation status complete event.  Called when validation has completed and after a
     * {@link ValidatorListener#onValidate(HollowProducer.ReadState) validator} event was emitted to all registered
     * {@link ValidatorListener validator} listeners.
     * <p>
     * The validation {@code status} holds the list validation results accumulated from the calling of all
     * registered validator listeners.  If validation {@link ValidationStatus#failed() fails} (one or more
     * validation results failed) then the cycle stage completes with failure and no
     * {@link AnnouncementListener announcement} occurs.
     * The status of the cycle complete event will contain a {@code cause} that is an instance of
     * {@link ValidationStatusException} holding the validation results reported by the validation {@code status}.
     *
     * @param status the validation status
     * @param version the version
     * @param elapsed the time elapsed between this event and {@link #onValidationStatusStart}
     */
    void onValidationStatusComplete(ValidationStatus status, long version, Duration elapsed);
}
