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
 * A listener of restore events associated with the producer restore stage.
 * <p>
 * A cycle listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface RestoreListener extends HollowProducerEventListener {
    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    void onProducerRestoreStart(long restoreVersion);

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param status of the restore. {@link Status#getType()} will return {@code SUCCESS} when
     * the desired version was reached during restore, otherwise {@code FAIL} will be returned.
     * @param versionDesired the desired version to restore to
     * @param versionReached the actual version restored to
     * @param elapsed duration of the restore in {@code unit} units
     */
    void onProducerRestoreComplete(Status status, long versionDesired, long versionReached, Duration elapsed);
}
