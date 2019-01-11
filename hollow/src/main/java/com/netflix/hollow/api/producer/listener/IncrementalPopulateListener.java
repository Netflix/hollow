package com.netflix.hollow.api.producer.listener;

import com.netflix.hollow.api.producer.Status;
import java.time.Duration;

public interface IncrementalPopulateListener extends HollowProducerEventListener {
    void onIncrementalPopulateStart(long version);

    void onIncrementalPopulateComplete(Status status,
            long removed, long addedOrModified,
            long version, Duration elapsed);
}
