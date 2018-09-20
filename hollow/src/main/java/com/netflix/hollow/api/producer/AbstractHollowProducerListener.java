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

import java.util.concurrent.TimeUnit;

/**
 * A trivial implementation of {@link HollowProducerListenerV2} which does nothing.
 * Implementations of HollowProducerListenerV2 should subclass this class for convenience.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
public class AbstractHollowProducerListener implements HollowProducerListenerV2 {
    // DataModelInitializationListener
    @Override public void onProducerInit(long elapsed, TimeUnit unit) {}

    // RestoreListener
    @Override public void onProducerRestoreStart(long restoreVersion) {}
    @Override public void onProducerRestoreComplete(RestoreStatus status, long elapsed, TimeUnit unit) {}

    // CycleListener
    @Override public void onNewDeltaChain(long version) {}
    @Override public void onCycleSkip(CycleSkipReason reason) {}
    @Override public void onCycleStart(long version) {}
    @Override public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}

    // PopulateListener
    @Override public void onPopulateStart(long version) {}
    @Override public void onPopulateComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}

    // PublishListener
    @Override public void onNoDeltaAvailable(long version) {}
    @Override public void onPublishStart(long version) {}
    @Override public void onArtifactPublish(PublishStatus publishStatus, long elapsed, TimeUnit unit) {}
    @Override public void onPublishComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}

    // IntegrityCheckListener
    @Override public void onIntegrityCheckStart(long version) {}
    @Override public void onIntegrityCheckComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}

    // ValidationListener
    @Override public void onValidationStart(long version) {}
    @Override public void onValidationComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}

    // AnnouncementListener
    @Override public void onAnnouncementStart(long version) {}
    @Override public void onAnnouncementComplete(ProducerStatus status, long elapsed, TimeUnit unit) {}
}
