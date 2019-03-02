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
 * A listener of publish events associated with the producer publish stage.
 * <p>
 * A publish listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface PublishListener extends HollowProducerEventListener {
    // Called after populateComplete and instead of publish
    // Can be merged in to PublishListener?

    /**
     * Called after the new state has been populated if the {@code HollowProducer} detects that no data has changed,
     * thus no snapshot nor delta should be produced.<p>
     *
     * @param version Current version of the cycle.
     */
    void onNoDeltaAvailable(long version);

    /**
     * Called when the {@code HollowProducer} has begun publishing the {@code HollowBlob}s produced this cycle.
     *
     * @param version version to be published.
     */
    void onPublishStart(long version);

    // Called during publish start-complete cycle for each blob
    // Can be merged in to PublishListener?

    /**
     * Called once a blob has been published successfully or failed to published.
     * This method is called for every {@link com.netflix.hollow.api.producer.HollowProducer.Blob.Type} that
     * was published.
     *
     * @param status status of publishing. {@link Status#getType()} returns {@code SUCCESS} or {@code FAIL}.
     * @param blob the blob
     * @param elapsed time taken to publish the blob
     */
    void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed);

    /**
     * Called after the publish stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the {@code HollowBlob}s produced this cycle has been published to the blob store.
     *
     * @param status CycleStatus of the publish stage. {@link Status#getType()} will return {@code SUCCESS}
     * when the publish was successful; @{code FAIL} otherwise.
     * @param version version that was published.
     * @param elapsed duration of the publish stage in {@code unit} units
     */
    void onPublishComplete(Status status, long version, Duration elapsed);
}
