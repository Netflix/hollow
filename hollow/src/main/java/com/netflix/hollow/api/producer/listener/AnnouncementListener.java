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
import java.util.Map;
import java.util.Set;

/**
 * A listener of announcement events associated with the producer announcement stage.
 * <p>
 * A announcement listener instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */


class CustomListener implements AnnouncementListener {
    @Override
    public void onAnnouncementStart(Map<String, ChangedOrdinal> addedRemovedModifiedOridnalMap) {
        ChangedOrdinal movieChangedOrdinals = addedRemovedModifiedOridnalMap.get("Movie");
        Set<Long> addedMovieOrdinals = movieChangedOrdinals.addedOrdinals;
        for (long ordinal : addedMovieOrdinals) {
            Movie addedMovie = MovieAPI.getByOrdinal(ordinal);
        }
    }
}
public interface AnnouncementListener extends HollowProducerEventListener {
    /**
     * Called when the {@code HollowProducer} has begun announcing the {@code HollowBlob} published this cycle.
     *
     * @param version of {@code HollowBlob} that will be announced.
     */
    void onAnnouncementStart(long version);


    default void onAnnouncementStart(HollowProducer.ReadState readState) {}

    class ChangedOrdinal {
        Set<Long> addedOrdinals;
        Set<Long> removedOrdinals;
        Set<Long> modifiedOrdinals;
    }

    default void onAnnouncementStart(Map<String, ChangedOrdinal> addedRemovedModifiedOridnalMap) {
    }

    /**
     * Called after the announcement stage finishes normally or abnormally. A {@code SUCCESS} status indicates
     * that the {@code HollowBlob} published this cycle has been announced to consumers.
     *
     * @param status CycleStatus of the announcement stage. {@link Status#getType()} will return {@code SUCCESS}
     * when the announce was successful; @{code FAIL} otherwise.
     * @param readState the read state
     * @param version of {@code HollowBlob} that was announced.
     * @param elapsed duration of the announcement stage in {@code unit} units
     */
    void onAnnouncementComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed);
}
