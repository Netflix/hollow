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

import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.PublishStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.HollowProducerListenerV2.CycleSkipReason;
import com.netflix.hollow.api.producer.IncrementalCycleListener.IncrementalCycleStatus;
import com.netflix.hollow.api.producer.validation.AllValidationStatus;
import com.netflix.hollow.api.producer.validation.AllValidationStatus.AllValidationStatusBuilder;
import com.netflix.hollow.api.producer.validation.HollowValidationListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
final class ListenerSupport {

    private final Logger log = Logger.getLogger(ListenerSupport.class.getName());

    private final Set<HollowProducerListener> listeners;
    private final Set<HollowValidationListener> validationListeners;
    private final Set<IncrementalCycleListener> incrementalCycleListeners;

    ListenerSupport() {
        listeners = new CopyOnWriteArraySet<>();
        validationListeners = new CopyOnWriteArraySet<>();
        incrementalCycleListeners = new CopyOnWriteArraySet<>();
    }

    void add(HollowProducerListener listener) {
        listeners.add(listener);
    }
    
    void add(HollowValidationListener listener) {
    	validationListeners.add(listener);
    }

    void add(IncrementalCycleListener listener) {
        incrementalCycleListeners.add(listener);
    }

    void remove(HollowProducerListener listener) {
        listeners.remove(listener);
    }

    void remove(IncrementalCycleListener listener) {
        incrementalCycleListeners.remove(listener);
    }

    void fireProducerInit(long elapsedMillis) {
        for(final HollowProducerListener l : listeners) {
            try {
                l.onProducerInit(elapsedMillis, MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireProducerRestoreStart(long version) {
        for(final HollowProducerListener l : listeners) {
            try {
                l.onProducerRestoreStart(version);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireProducerRestoreComplete(RestoreStatus status, long elapsedMillis) {
        for(final HollowProducerListener l : listeners) {
            try {
                l.onProducerRestoreComplete(status, elapsedMillis, MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireNewDeltaChain(long version) {
        for(final HollowProducerListener l : listeners) {
            try {
                l.onNewDeltaChain(version);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder fireCycleSkipped(CycleSkipReason reason) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder();
        for (final HollowProducerListener l : listeners) {
            if (l instanceof HollowProducerListenerV2) {
                try {
                    ((HollowProducerListenerV2) l).onCycleSkip(reason);
                } catch(Throwable t) {
                    log.warning("Error executing listener" + t);
                }
            }
        }
        return psb;
    }

    ProducerStatus.Builder fireCycleStart(long version) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(version);
        for(final HollowProducerListener l : listeners) {
            try {
                l.onCycleStart(version);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return psb;
    }

    void fireCycleComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onCycleComplete(st, psb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireNoDelta(ProducerStatus.Builder psb) {
        for(final HollowProducerListener l : listeners) {
            try {
                l.onNoDeltaAvailable(psb.version());
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder firePopulateStart(long version) {
        ProducerStatus.Builder builder = new ProducerStatus.Builder().version(version);
        for (final HollowProducerListener l : listeners) {
            try {
                l.onPopulateStart(version);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return builder;
    }

    void firePopulateComplete(ProducerStatus.Builder builder) {
        ProducerStatus st = builder.build();
        for (final HollowProducerListener l : listeners) {
            try {
                l.onPopulateComplete(st, builder.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder firePublishStart(long version) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(version);
        for(final HollowProducerListener l : listeners) {
            try {
                l.onPublishStart(version);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return psb;
    }

    void firePublishComplete(ProducerStatus.Builder builder) {
        ProducerStatus status = builder.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onPublishComplete(status, builder.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireArtifactPublish(PublishStatus.Builder builder) {
        PublishStatus status = builder.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onArtifactPublish(status, builder.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder fireIntegrityCheckStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        for(final HollowProducerListener l : listeners) {
            try {
                l.onIntegrityCheckStart(psb.version());
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return psb;
    }

    void fireIntegrityCheckComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onIntegrityCheckComplete(st, psb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder fireValidationStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        long version = readState.getVersion();
        Set<Object> firedListeners = new HashSet<>();
        for (final HollowProducerListener l : listeners) {
            try {
                l.onValidationStart(version);
                firedListeners.add(l);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }

        }
        for (final HollowValidationListener vl: validationListeners){
            try {
                if (!firedListeners.contains(vl)) {
                    vl.onValidationStart(version);
                }
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return psb;
    }

    void fireValidationComplete(ProducerStatus.Builder psb, AllValidationStatusBuilder valStatusBuilder) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onValidationComplete(st, psb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        
        AllValidationStatus valStatus = valStatusBuilder.build();
        for(final HollowValidationListener vl : validationListeners) {
            try {
                vl.onValidationComplete(valStatus, psb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    ProducerStatus.Builder fireAnnouncementStart(HollowProducer.ReadState readState) {
        ProducerStatus.Builder psb = new ProducerStatus.Builder().version(readState);
        for(final HollowProducerListener l : listeners) {
            try {
                l.onAnnouncementStart(psb.version());
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
        return psb;
    }

    void fireAnnouncementComplete(ProducerStatus.Builder psb) {
        ProducerStatus st = psb.build();
        for(final HollowProducerListener l : listeners) {
            try {
                l.onAnnouncementComplete(st, psb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireIncrementalCycleComplete(long version, long recordsAddedOrModified, long recordsRemoved, Map<String, Object> cycleMetadata) {
        IncrementalCycleStatus.Builder icsb = new IncrementalCycleStatus.Builder().success(version, recordsAddedOrModified, recordsRemoved, cycleMetadata);
        for(final IncrementalCycleListener l : incrementalCycleListeners) {
            try {
                l.onCycleComplete(icsb.build(), icsb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

    void fireIncrementalCycleFail(Throwable cause, long recordsAddedOrModified, long recordsRemoved, Map<String, Object> cycleMetadata) {
        IncrementalCycleStatus.Builder icsb = new IncrementalCycleStatus.Builder().fail(cause, recordsAddedOrModified, recordsRemoved, cycleMetadata);
        for(final IncrementalCycleListener l : incrementalCycleListeners) {
            try {
                l.onCycleFail(icsb.build(), icsb.elapsed(), MILLISECONDS);
            } catch(Throwable t) {
                log.warning("Error executing listener" + t);
            }
        }
    }

}
