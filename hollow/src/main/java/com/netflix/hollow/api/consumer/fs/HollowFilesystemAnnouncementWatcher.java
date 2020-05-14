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

import static com.netflix.hollow.core.util.Threads.daemonThread;
import static java.nio.file.Files.getLastModifiedTime;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HollowFilesystemAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {

    private static final Logger log = Logger.getLogger(HollowFilesystemAnnouncementWatcher.class.getName());

    private final Path announcePath;

    private final List<HollowConsumer> subscribedConsumers;
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> watchFuture;
    private boolean ownedExecutor;

    private long latestVersion;

    /**
     * Creates a file system announcement watcher.
     *
     * @param publishPath the publish path
     * @since 2.12.0
     */
    @SuppressWarnings("unused")
    public HollowFilesystemAnnouncementWatcher(Path publishPath) {
        this(publishPath, newScheduledThreadPool(1,
                r -> daemonThread(r, HollowFilesystemAnnouncementWatcher.class, "watch; path=" + publishPath)));
        ownedExecutor = true;
    }

    /**
     * Creates a file system announcement watcher.
     *
     * @param publishPath the publish path
     * @param executor the executor from which watching is executed
     * @since 2.12.0
     */
    @SuppressWarnings("WeakerAccess")
    public HollowFilesystemAnnouncementWatcher(Path publishPath, ScheduledExecutorService executor) {
        this.executor = executor;

        this.announcePath = publishPath.resolve(HollowFilesystemAnnouncer.ANNOUNCEMENT_FILENAME);
        this.subscribedConsumers = new CopyOnWriteArrayList<>();
        this.latestVersion = readLatestVersion();

        this.watchFuture = setupWatch();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        watchFuture.cancel(true);

        if (ownedExecutor) {
            executor.shutdownNow();
        }
    }

    private ScheduledFuture setupWatch() {
        return executor.scheduleWithFixedDelay(new Watch(this), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    @Override
    public void subscribeToUpdates(final HollowConsumer consumer) {
        subscribedConsumers.add(consumer);
    }

    private long readLatestVersion() {
        if (!Files.isReadable(announcePath))
            return NO_ANNOUNCEMENT_AVAILABLE;

        try (BufferedReader reader = new BufferedReader(new FileReader(announcePath.toFile()))) {
            return Long.parseLong(reader.readLine());
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    static class Watch implements Runnable {
        private FileTime previousFileTime = FileTime.from(0, TimeUnit.MILLISECONDS);
        private final WeakReference<HollowFilesystemAnnouncementWatcher> ref;

        Watch(HollowFilesystemAnnouncementWatcher watcher) {
            ref = new WeakReference<>(watcher);
        }

        @Override
        public void run() {
            try {
                HollowFilesystemAnnouncementWatcher watcher = ref.get();
                if (watcher != null) {
                    if (!Files.isReadable(watcher.announcePath)) return;

                    FileTime lastModifiedTime = getLastModifiedTime(watcher.announcePath);
                    if (lastModifiedTime.compareTo(previousFileTime) > 0) {
                        previousFileTime = lastModifiedTime;

                        long currentVersion = watcher.readLatestVersion();
                        if (watcher.latestVersion != currentVersion) {
                            watcher.latestVersion = currentVersion;
                            for (HollowConsumer consumer : watcher.subscribedConsumers)
                                consumer.triggerAsyncRefresh();
                        }
                    }
                }
            } catch (Exception ex) {
                log.log(Level.WARNING, "Exception reading the current announced version", ex);
            } catch (Throwable th) {
                log.log(Level.SEVERE, "Exception reading the current announced version", th);
                throw th;
            }
        }
    }
}
