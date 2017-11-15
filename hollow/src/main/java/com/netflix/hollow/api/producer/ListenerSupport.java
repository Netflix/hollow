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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.PublishStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.validation.AllValidationStatus;
import com.netflix.hollow.api.producer.validation.AllValidationStatus.AllValidationStatusBuilder;
import com.netflix.hollow.api.producer.validation.HollowValidationListener;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
final class ListenerSupport {
    private final Set<HollowProducerListener> listeners;
    private final Set<HollowValidationListener> validationListeners;

    ListenerSupport() {
        listeners = new CopyOnWriteArraySet<>();
        validationListeners = new CopyOnWriteArraySet<>();
    }

    void add(HollowProducerListener listener) {
        listeners.add(listener);
    }
    
    void add(HollowValidationListener listener) {
    	validationListeners.add(listener);
    }

    void remove(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    void fireProducerInit(long elapsedMillis) {
        for(final HollowProducerListener l : listeners) l.onProducerInit(elapsedMillis, MILLISECONDS);
    }

    void fireProducerRestoreStart(long version) {
        for(final HollowProducerListener l : listeners) l.onProducerRestoreStart(version);
    }

    void fireProducerRestoreComplete(RestoreStatus status, long elapsedMillis) {
        for(final HollowProducerListener l : listeners) l.onProducerRestoreComplete(status, elapsedMillis, MILLISECONDS);
    }

    void fireNewDeltaChain(long version) {
        for(final HollowProducerListener l : listeners) l.onNewDeltaChain(version);
    }

    ProducerStatus.Builder fireCycleStart(long version) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(version);
        for(final HollowProducerListener l : listeners) l.onCycleStart(version);
        return psb;
    }

    void fireCycleComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) l.onCycleComplete(st, psb.elapsed(), MILLISECONDS);
    }

    void fireNoDelta(ProducerStatus.Builder psb) {
        for(final HollowProducerListener l : listeners) l.onNoDeltaAvailable(psb.version());
    }

    ProducerStatus.Builder firePopulateStart(long version) {
        ProducerStatus.Builder builder = new ProducerStatus.Builder().version(version);
        for (final HollowProducerListener l : listeners) l.onPopulateStart(version);
        return builder;
    }

    void firePopulateComplete(ProducerStatus.Builder builder) {
        ProducerStatus st = builder.build();
        for (final HollowProducerListener l : listeners) l.onPopulateComplete(st, builder.elapsed(), MILLISECONDS);
    }

    ProducerStatus.Builder firePublishStart(long version) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(version);
        for(final HollowProducerListener l : listeners) l.onPublishStart(version);
        return psb;
    }

    void firePublishComplete(ProducerStatus.Builder builder) {
        ProducerStatus status = builder.build();
        for(final HollowProducerListener l : listeners) l.onPublishComplete(status, builder.elapsed(), MILLISECONDS);
    }

    void fireArtifactPublish(PublishStatus.Builder builder) {
        PublishStatus status = builder.build();
        for(final HollowProducerListener l : listeners) l.onArtifactPublish(status, builder.elapsed(), MILLISECONDS);
    }

    ProducerStatus.Builder fireIntegrityCheckStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        for(final HollowProducerListener l : listeners) l.onIntegrityCheckStart(psb.version());
        return psb;
    }

    void fireIntegrityCheckComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) l.onIntegrityCheckComplete(st, psb.elapsed(), MILLISECONDS);
    }

    ProducerStatus.Builder fireValidationStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        for(final HollowProducerListener l : listeners) l.onValidationStart(psb.version());
        
        long version = readState.getVersion();
        for(final HollowValidationListener vl: validationListeners){
			vl.onValidationStart(version);
        }
        
        return psb;
    }

    void fireValidationComplete(ProducerStatus.Builder psb, AllValidationStatusBuilder valStatusBuilder) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) l.onValidationComplete(st, psb.elapsed(), MILLISECONDS);
        
        AllValidationStatus valStatus = valStatusBuilder.build();
        for(final HollowValidationListener vl : validationListeners) vl.onValidationComplete(valStatus, psb.elapsed(), MILLISECONDS);
    }

    ProducerStatus.Builder fireAnnouncementStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        for(final HollowProducerListener l : listeners) l.onAnnouncementStart(psb.version());
        return psb;
    }

    void fireAnnouncementComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) l.onAnnouncementComplete(st, psb.elapsed(), MILLISECONDS);
    }
}
