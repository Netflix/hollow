package com.netflix.hollow.api.producer.listener;

import com.netflix.hollow.api.producer.Status;
import java.time.Duration;

/**
 * A listener of incremental population events associated with the populate cycle stage.
 * <p>
 * A populate listener instance may be registered when building a incremental
 * {@link com.netflix.hollow.api.producer.HollowProducer.Incremental producer}
 * (see {@link com.netflix.hollow.api.producer.HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link com.netflix.hollow.api.producer.HollowProducer.Incremental#addListener(HollowProducerEventListener)}.
 */
public interface IncrementalPopulateListener extends HollowProducerEventListener {
    /**
     * Called before starting to execute the task to incrementally populate data into Hollow.
     *
     * @param version current version of the cycle
     */
    void onIncrementalPopulateStart(long version);

    /**
     * Called once the incremental populating task stage has finished successfully or failed.
     * Use {@link Status#getType()} to deserializeFrom status of the task.
     *
     * @param status A value of {@code SUCCESS} indicates that all data was successfully populated.
     * {@code FAIL} status indicates populating hollow with data failed.
     * @param removed the number of records removed
     * @param addedOrModified the number of records added or modified
     * @param version current version of the cycle
     * @param elapsed Time taken to populate hollow.
     */
    void onIncrementalPopulateComplete(Status status,
            long removed, long addedOrModified,
            long version, Duration elapsed);
}
