/*
 *
 *  Copyright 2018 Netflix, Inc.
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
package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher;

/**
 * A simple implementation of an AnnouncementWatcher which allows setting the latest version.
 */
public class TestAnnouncementWatcher implements AnnouncementWatcher {
    private long latestVersion = NO_ANNOUNCEMENT_AVAILABLE;

    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    public TestAnnouncementWatcher setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
        return this;
    }

    @Override
    public void subscribeToUpdates(HollowConsumer consumer) {
        // no-op
    }
}
