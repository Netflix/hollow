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
import com.netflix.hollow.api.producer.PublishStageStats;
import com.netflix.hollow.api.producer.Status;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

    /**
     * Called once a blob has been staged successfully or failed to stage.
     * This method is called for every {@link HollowProducer.Blob.Type} that
     * was staged.
     *
     * @param status status of staging. {@link Status#getType()} returns {@code SUCCESS} or {@code FAIL}.
     * @param blob the blob
     * @param elapsed time taken to stage the blob
     */
    default void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
    }

    /**
     * Called once a blob has been published successfully or failed to published.
     * This method is called for every {@link HollowProducer.Blob.Type} that
     * was published.
     *
     * @param status status of publishing. {@link Status#getType()} returns {@code SUCCESS} or {@code FAIL}.
     * @param blob the blob
     * @param elapsed time taken to publish the blob
     */
    void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed);
    /**
     * Called if a blob is to be published asynchronously.
     * This method is called for a {@link HollowProducer.Blob.Type#SNAPSHOT snapshot} blob when the
     * producer is {@link HollowProducer.Builder#withSnapshotPublishExecutor(Executor) built} with a snapshot
     * publish executor (that enables asynchronous publication).
     *
     * @param blob the future holding the blob that will be completed successfully when the blob has been published
     * or with error if publishing failed.  When the blob future completes the contents of the blob are only
     * guaranteed to be available if further stages are executed using the default execution mode of this future
     * (i.e. use of asynchronous execution could result in a subsequent stage completing after the blob contents have
     * been cleaned up).
     */
    default void onBlobPublishAsync(CompletableFuture<HollowProducer.Blob> blob) {
    }

    /**
     * Deprecated in favor of
     * {@code onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats stats)}.
     *
     * @param status CycleStatus of the publish stage. {@link Status#getType()} will return {@code SUCCESS}
     * when the publish was successful; @{code FAIL} otherwise.
     * @param version version that was published.
     * @param elapsed duration of the publish stage in {@code unit} units
     */
    @Deprecated
    void onPublishComplete(Status status, long version, Duration elapsed);

    /**
     * Called when the publish stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the {@code HollowBlob}s produced this cycle has been published to the blob store.
     * <p>
     * This method provides access to publish stage statistics including ordinal map metrics for each type.
     * The stats parameter will be {@code null} if the publish stage encountered errors before statistics
     * could be collected (e.g., during blob staging or publishing failures).
     *
     * @param status CycleStatus of the publish stage. {@link Status#getType()} will return {@code SUCCESS}
     * when the publish was successful; {@code FAIL} otherwise.
     * @param version version that was published.
     * @param elapsed duration of the publish stage
     * @param stats stats collected upon publish stage completion, including
     * {@link com.netflix.hollow.core.memory.ByteArrayOrdinalMapStats} for each type.
     * Will be {@code null} if the stage encountered errors before stats could be collected.
     */
    default void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats stats) {
        onPublishComplete(status, version, elapsed);
    }
}
