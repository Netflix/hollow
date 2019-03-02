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
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import java.time.Duration;

/**
 * A listener of cycle events associated with the producer cycle stage.
 * <p>
 * A cycle listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface CycleListener extends HollowProducerEventListener {
    /**
     * The reasons for a cycle skip event
     */
    enum CycleSkipReason {
        /**
         * The cycle is skipped because the producer is not a primary producer.
         */
        NOT_PRIMARY_PRODUCER
    }

    /**
     * A receiver of a cycle skip event.  Called when a cycle is skipped.
     * <p>
     * If this event occurs then no further cycle events (or any events associated with sub-stages) will occur and
     * the cycle stage is complete.
     *
     * @param reason the reason the cycle is skipped
     */
    // See HollowProducerListenerV2
    // Can this be merged in to onCycleComplete with status?
    void onCycleSkip(CycleSkipReason reason);


    /**
     * A receiver of a new delta chain event.  Called when the next state produced will begin a new delta chain.
     * Occurs before the {@link #onCycleStart(long) cycle start} event.
     * <p>
     * This will be called prior to the next state being produced if
     * {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
     * hasn't been called or the restore failed.
     *
     * @param version the version of the state that will become the first of a new delta chain
     */
    // This is called just before onCycleStart, can the two be merged with additional arguments?
    void onNewDeltaChain(long version);

    /**
     * A receiver of a cycle start event. Called when the {@code HollowProducer} has begun a new cycle.
     *
     * @param version the version produced by the {@code HollowProducer} for new cycle about to start.
     */
    void onCycleStart(long version);

    /**
     * A receiver of a cycle complete event.  Called after the {@code HollowProducer} has completed a cycle
     * with success or failure.  Occurs after the {@link #onCycleStart(long) cycle start} event.
     * <p>
     * If the cycle is successful then the {@code status} reports
     * {@link Status.StatusType#SUCCESS success}.  Success indicates that a new state has been as been
     * {@link PopulateListener populated},
     * {@link PublishListener published},
     * {@link IntegrityCheckListener integrity checked},
     * {@link ValidationStatusListener validated}, and
     * {@link AnnouncementListener announced}.
     * Alternatively success may also indicate that population resulted in no changes and therefore there is no new
     * state to publish and announce.  If so a {@link PublishListener#onNoDeltaAvailable(long) no delta available}
     * event of the {@link PublishListener publisher} stage is be emitted after which the cycle complete event is
     * emitted.
     * <p>
     * If the cycle failed then the {@code status} reports {@link Status.StatusType#FAIL failure}.
     *
     * @param status the status of this cycle.
     * @param readState the read state, null if no read state avaulable
     * @param version the version
     * @param elapsed duration of the cycle
     */
    void onCycleComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed);
}
