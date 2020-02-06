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
package com.netflix.hollow.api.producer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.common.ListenerSupport;
import com.netflix.hollow.api.common.Listeners;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.IncrementalCycleListener.IncrementalCycleStatus;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.DataModelInitializationListener;
import com.netflix.hollow.api.producer.listener.HollowProducerEventListener;
import com.netflix.hollow.api.producer.listener.IncrementalPopulateListener;
import com.netflix.hollow.api.producer.listener.IntegrityCheckListener;
import com.netflix.hollow.api.producer.listener.PopulateListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

final class ProducerListenerSupport extends ListenerSupport {

    private static final Logger LOG = Logger.getLogger(ProducerListenerSupport.class.getName());

    private static final Collection<Class<? extends HollowProducerEventListener>> LISTENERS =
            Stream.of(DataModelInitializationListener.class,
                    RestoreListener.class,
                    CycleListener.class,
                    PopulateListener.class,
                    PublishListener.class,
                    IntegrityCheckListener.class,
                    AnnouncementListener.class,
                    ValidatorListener.class,
                    ValidationStatusListener.class)
                    .distinct().collect(toList());

    static boolean isValidListener(HollowProducerEventListener l) {
        return LISTENERS.stream().anyMatch(c -> c.isInstance(l));
    }

    ProducerListenerSupport() {
        // @@@ This is used only by HollowIncrementalProducer, and should be
        // separated out
        incrementalCycleListeners = new CopyOnWriteArraySet<>();
    }

    ProducerListenerSupport(List<? extends HollowProducerEventListener> listeners) {
        super(listeners);

        // @@@ This is used only by HollowIncrementalProducer, and should be
        // separated out
        incrementalCycleListeners = new CopyOnWriteArraySet<>();
    }

    ProducerListenerSupport(ProducerListenerSupport that) {
        super(that);

        // @@@ This is used only by HollowIncrementalProducer, and should be
        // separated out
        incrementalCycleListeners = new CopyOnWriteArraySet<>(that.incrementalCycleListeners);
    }

    //

    /**
     * Copies the collection of listeners so they can be iterated on without changing.
     * From the returned copy events may be fired.
     * Any addition or removal of listeners will take effect on the next cycle.
     */
    ProducerListeners listeners() {
        return new ProducerListeners(eventListeners.toArray(new HollowProducerEventListener[0]));
    }

    static final class ProducerListeners extends Listeners {

        ProducerListeners(HollowProducerEventListener[] listeners) {
            super(listeners);
        }

        void fireProducerInit(long elapsedMillis) {
            fire(DataModelInitializationListener.class,
                    l -> l.onProducerInit(Duration.ofMillis(elapsedMillis)));
        }


        Status.RestoreStageBuilder fireProducerRestoreStart(long version) {
            fire(RestoreListener.class,
                    l -> l.onProducerRestoreStart(version));

            return new Status.RestoreStageBuilder();
        }

        void fireProducerRestoreComplete(Status.RestoreStageBuilder b) {
            Status s = b.build();
            long versionDesired = b.versionDesired;
            long versionReached = b.versionReached;
            Duration elapsed = b.elapsed();

            fire(RestoreListener.class,
                    l -> l.onProducerRestoreComplete(s, versionDesired, versionReached, elapsed));
        }


        void fireNewDeltaChain(long version) {
            fire(CycleListener.class,
                    l -> l.onNewDeltaChain(version));
        }

        void fireCycleSkipped(CycleListener.CycleSkipReason reason) {
            fire(CycleListener.class,
                    l -> l.onCycleSkip(reason));
        }

        Status.StageWithStateBuilder fireCycleStart(long version) {
            fire(CycleListener.class,
                    l -> l.onCycleStart(version));

            return new Status.StageWithStateBuilder().version(version);
        }

        void fireCycleComplete(Status.StageWithStateBuilder b) {
            Status s = b.build();
            HollowProducer.ReadState readState = b.readState;
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(CycleListener.class,
                    l -> l.onCycleComplete(s, readState, version, elapsed));
        }

        Status.IncrementalPopulateBuilder fireIncrementalPopulateStart(long version) {
            fire(IncrementalPopulateListener.class,
                    l -> l.onIncrementalPopulateStart(version));

            return new Status.IncrementalPopulateBuilder().version(version);
        }

        void fireIncrementalPopulateComplete(Status.IncrementalPopulateBuilder b) {
            Status s = b.build();
            long version = b.version;
            Duration elapsed = b.elapsed();
            long removed = b.removed;
            long addedOrModified = b.addedOrModified;

            fire(IncrementalPopulateListener.class,
                    l -> l.onIncrementalPopulateComplete(s, removed, addedOrModified, version, elapsed));
        }

        Status.StageBuilder firePopulateStart(long version) {
            fire(PopulateListener.class,
                    l -> l.onPopulateStart(version));

            return new Status.StageBuilder().version(version);
        }

        void firePopulateComplete(Status.StageBuilder b) {
            Status s = b.build();
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(PopulateListener.class,
                    l -> l.onPopulateComplete(s, version, elapsed));
        }


        void fireNoDelta(long version) {
            fire(PublishListener.class,
                    l -> l.onNoDeltaAvailable(version));
        }

        Status.StageBuilder firePublishStart(long version) {
            fire(PublishListener.class,
                    l -> l.onPublishStart(version));

            return new Status.StageBuilder().version(version);
        }

        void fireBlobStage(Status.PublishBuilder b) {
            Status s = b.build();
            HollowProducer.Blob blob = b.blob;
            Duration elapsed = b.elapsed();

            fire(PublishListener.class,
                    l -> l.onBlobStage(s, blob, elapsed));
        }

        void fireBlobPublishAsync(CompletableFuture<HollowProducer.Blob> f) {
            fire(PublishListener.class,
                    l -> l.onBlobPublishAsync(f));
        }

        void fireBlobPublish(Status.PublishBuilder b) {
            Status s = b.build();
            HollowProducer.Blob blob = b.blob;
            Duration elapsed = b.elapsed();

            fire(PublishListener.class,
                    l -> l.onBlobPublish(s, blob, elapsed));
        }

        void firePublishComplete(Status.StageBuilder b) {
            Status s = b.build();
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(PublishListener.class,
                    l -> l.onPublishComplete(s, version, elapsed));
        }


        Status.StageWithStateBuilder fireIntegrityCheckStart(HollowProducer.ReadState readState) {
            long version = readState.getVersion();
            fire(IntegrityCheckListener.class,
                    l -> l.onIntegrityCheckStart(version));

            return new Status.StageWithStateBuilder().readState(readState);
        }

        void fireIntegrityCheckComplete(Status.StageWithStateBuilder b) {
            Status s = b.build();
            HollowProducer.ReadState readState = b.readState;
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(IntegrityCheckListener.class,
                    l -> l.onIntegrityCheckComplete(s, readState, version, elapsed));
        }


        Status.StageWithStateBuilder fireValidationStart(HollowProducer.ReadState readState) {
            long version = readState.getVersion();
            fire(HollowProducerListener.class,
                    l -> l.onValidationStart(version));

            fire(ValidationStatusListener.class,
                    l -> l.onValidationStatusStart(version));

            return new Status.StageWithStateBuilder().readState(readState);
        }

        void fireValidationComplete(Status.StageWithStateBuilder b, ValidationStatus vs) {
            Status s = b.build();
            HollowProducer.ReadState readState = b.readState;
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(HollowProducerListener.class,
                    l -> l.onValidationComplete(new ProducerStatus(s, readState, version),
                            elapsed.toMillis(), MILLISECONDS));

            fire(ValidationStatusListener.class,
                    l -> l.onValidationStatusComplete(vs, version, elapsed));
        }


        Status.StageWithStateBuilder fireAnnouncementStart(HollowProducer.ReadState readState) {
            long version = readState.getVersion();
            fire(AnnouncementListener.class,
                    l -> l.onAnnouncementStart(version));

            return new Status.StageWithStateBuilder().readState(readState);
        }

        void fireAnnouncementComplete(Status.StageWithStateBuilder b) {
            Status s = b.build();
            HollowProducer.ReadState readState = b.readState;
            long version = b.version;
            Duration elapsed = b.elapsed();

            fire(AnnouncementListener.class,
                    l -> l.onAnnouncementComplete(s, readState, version, elapsed));
        }
    }



    // @@@ This is used only by HollowIncrementalProducer, and should be
    // separated out

    private final Set<IncrementalCycleListener> incrementalCycleListeners;

    void add(IncrementalCycleListener listener) {
        incrementalCycleListeners.add(listener);
    }

    void remove(IncrementalCycleListener listener) {
        incrementalCycleListeners.remove(listener);
    }

    private <T> void fire(Collection<T> ls, Consumer<? super T> r) {
        fire(ls.stream(), r);
    }

    private <T> void fire(Stream<T> ls, Consumer<? super T> r) {
        ls.forEach(l -> {
            try {
                r.accept(l);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Error executing listener", e);
            }
        });
    }

    void fireIncrementalCycleComplete(
            long version, long recordsAddedOrModified, long recordsRemoved,
            Map<String, Object> cycleMetadata) {
        // @@@ This behaviour appears incomplete, the build is created and built
        // for each listener.  The start time (builder creation) and end time (builder built)
        // results in an effectively meaningless elasped time.
        IncrementalCycleStatus.Builder icsb = new IncrementalCycleStatus.Builder()
                .success(version, recordsAddedOrModified, recordsRemoved, cycleMetadata);
        fire(incrementalCycleListeners,
                l -> l.onCycleComplete(icsb.build(), icsb.elapsed(), MILLISECONDS));
    }

    void fireIncrementalCycleFail(
            Throwable cause, long recordsAddedOrModified, long recordsRemoved,
            Map<String, Object> cycleMetadata) {
        IncrementalCycleStatus.Builder icsb = new IncrementalCycleStatus.Builder()
                .fail(cause, recordsAddedOrModified, recordsRemoved, cycleMetadata);
        fire(incrementalCycleListeners,
                l -> l.onCycleFail(icsb.build(), icsb.elapsed(), MILLISECONDS));
    }
}
