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
package com.netflix.hollow.api.consumer;

import java.util.ArrayList;

import com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.VersionPinner;
import java.util.List;

/// This InMemoryAnnouncement is a HollowProducer.VersionPinner, HollowProducer.Announcer and HollowConsumer.AnnouncementWatcher!
public class InMemoryAnnouncement implements VersionPinner, Announcer, AnnouncementWatcher {
    
    private final List<HollowConsumer> subscribedConsumers;
    
    long latestAnnouncedVersion = NO_VERSION_AVAILABLE;
    long pinnedVersion = NO_VERSION_AVAILABLE;
        
    
    public InMemoryAnnouncement() {
        this.subscribedConsumers = new ArrayList<HollowConsumer>();
    }
            
    @Override
    public long getLatestVersion() {
        if(pinnedVersion != NO_VERSION_AVAILABLE)
            return pinnedVersion;
        return latestAnnouncedVersion;
    }

    @Override
    public long getPinnedVersion() {
        return pinnedVersion;
    }

    @Override
    public boolean hasPinnedVersion() {
        return pinnedVersion != NO_VERSION_AVAILABLE;
    }

    @Override
    public void subscribeToUpdates(HollowConsumer consumer) {
        subscribedConsumers.add(consumer);
    }

    @Override
    public void announce(long stateVersion) {
        latestAnnouncedVersion = stateVersion;
        notifyConsumers();
    }

    @Override
    public void pin(long stateVersion) {
        pinnedVersion = stateVersion;
        notifyConsumers();
    }

    @Override
    public void unpin() {
        pinnedVersion = NO_VERSION_AVAILABLE;
        notifyConsumers();
    }

    private void notifyConsumers() {
        for(HollowConsumer consumer : subscribedConsumers) {
            consumer.triggerRefresh(); // triggering a blocking refresh so we can make assertions
        }
    };
}
