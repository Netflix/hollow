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
package com.netflix.hollow.api.producer.listener;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import java.time.Duration;

/**
 * A listener of populate events associated with the populate cycle stage.
 * <p>
 * A populate listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface PopulateListener extends HollowProducerEventListener {
    /**
     * Called before starting to execute the task to populate data into Hollow.
     *
     * @param version current version of the cycle
     */
    void onPopulateStart(long version);

    /**
     * Called once the populating task stage has finished successfully or failed.
     * Use {@link Status#getType()} to get status of the task.
     *
     * @param status A value of {@code SUCCESS} indicates that all data was successfully populated.
     * {@code FAIL} status indicates populating hollow with data failed.
     * @param version current version of the cycle
     * @param elapsed Time taken to populate hollow.
     */
    void onPopulateComplete(Status status, long version, Duration elapsed);
}
