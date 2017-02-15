/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
final class ListenerSupport {
    private final Set<HollowProducerListener> listeners;

    ListenerSupport() {
        listeners = new CopyOnWriteArraySet<>();
    }

    void add(HollowProducerListener listener) {
        listeners.add(listener);
    }

    void remove(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    void fireProducerInit() {
        for(final HollowProducerListener l : listeners) l.onProducerInit();
    }

    void fireProducerRestore(long version) {
        for(final HollowProducerListener l : listeners) l.onProducerRestore(version);
    }

    void fireCycleStart(long version) {
        for(final HollowProducerListener l : listeners) l.onCycleStart(version);
    }

    void fireCycleComplete(ProducerStatus cycleStatus) {
        for(final HollowProducerListener l : listeners) l.onCycleComplete(cycleStatus);
    }

    void fireNoDelta(long version) {
        for(final HollowProducerListener l : listeners) l.onNoDeltaAvailable(version);
    }

    void firePublishStart(long version) {
        for(final HollowProducerListener l : listeners) l.onPublishStart(version);
    }

    void firePublishComplete(ProducerStatus publishStatus) {
        for(final HollowProducerListener l : listeners) l.onPublishComplete(publishStatus);
    }

    void fireIntegrityCheckStart(long version) {
        for(final HollowProducerListener l : listeners) l.onIntegrityCheckStart(version);
    }

    void fireIntegrityCheckComplete(ProducerStatus integrityCheckStatus) {
        for(final HollowProducerListener l : listeners) l.onIntegrityCheckComplete(integrityCheckStatus);
    }

    void fireValidationStart(long version) {
        for(final HollowProducerListener l : listeners) l.onValidationStart(version);
    }

    void fireValidationComplete(ProducerStatus validationStatus) {
        for(final HollowProducerListener l : listeners) l.onValidationComplete(validationStatus);
    }

    void fireAnnouncementStart(long version) {
        for(final HollowProducerListener l : listeners) l.onAnnouncementStart(version);
    }

    void fireAnnouncementComplete(ProducerStatus announcementStatus) {
        for(final HollowProducerListener l : listeners) l.onAnnouncementComplete(announcementStatus);
    }
}
