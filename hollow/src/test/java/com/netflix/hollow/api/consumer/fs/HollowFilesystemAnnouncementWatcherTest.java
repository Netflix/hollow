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
package com.netflix.hollow.api.consumer.fs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Coverage for https://github.com/Netflix/hollow/issues/273: when an
 * announcement already exists at subscribe time, the watcher must trigger a
 * refresh without waiting for a later version change.
 */
public class HollowFilesystemAnnouncementWatcherTest {

    private Path publishPath;
    private ScheduledExecutorService executor;
    private HollowFilesystemAnnouncementWatcher watcher;

    @Before
    public void setUp() throws IOException {
        publishPath = Files.createTempDirectory("hollow_announce");
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void tearDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    public void subscribeTriggersRefreshWhenAnnouncementAlreadyPresent() throws Exception {
        writeAnnouncement(42L);
        watcher = new HollowFilesystemAnnouncementWatcher(publishPath, executor);
        assertEquals(42L, watcher.getLatestVersion());

        HollowConsumer consumer = mock(HollowConsumer.class);
        watcher.subscribeToUpdates(consumer);

        verify(consumer, timeout(2000)).triggerAsyncRefresh();
    }

    @Test
    public void subscribeDoesNotTriggerWhenNoAnnouncementExists() {
        watcher = new HollowFilesystemAnnouncementWatcher(publishPath, executor);
        assertEquals(HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE, watcher.getLatestVersion());

        HollowConsumer consumer = mock(HollowConsumer.class);
        watcher.subscribeToUpdates(consumer);

        verifyNoInteractions(consumer);
    }

    private void writeAnnouncement(long version) throws IOException {
        Path announce = publishPath.resolve(HollowFilesystemAnnouncer.ANNOUNCEMENT_FILENAME);
        Files.write(announce, Long.toString(version).getBytes(StandardCharsets.UTF_8));
    }
}
