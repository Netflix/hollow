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
package com.netflix.hollow.api.consumer.fs;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class HollowFilesystemAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {

    private final Logger log = Logger.getLogger(HollowFilesystemAnnouncementWatcher.class.getName());

    private final Path publishDir;
    private final Path announceFile;

    private final List<HollowConsumer> subscribedConsumers;
    private final ScheduledExecutorService executor;
    private boolean ownedExecutor;

    private WatchService watchService;
    private ScheduledFuture<?> watchFuture;

    private long latestVersion;

    public HollowFilesystemAnnouncementWatcher(File publishDir) {
        this(publishDir,
             newScheduledThreadPool(
                     1 /*corePoolSize*/,
                     new ThreadFactory() {
                         @Override
                         public Thread newThread(Runnable r) {
                             Thread t = new Thread(r);
                             t.setDaemon(true);
                             return t;
                         }
                     })
           );

        ownedExecutor = true;
    }

    public HollowFilesystemAnnouncementWatcher(File publishDir, ScheduledExecutorService executor) {
        this.publishDir = publishDir.toPath();
        this.executor = executor;

        this.announceFile = this.publishDir.resolve(HollowFilesystemAnnouncer.ANNOUNCEMENT_FILENAME);
        this.subscribedConsumers = new CopyOnWriteArrayList<HollowConsumer>();
        this.latestVersion = readLatestVersion();

        setupWatch();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (ownedExecutor) {
            executor.shutdownNow();
        }
        else {
            watchFuture.cancel(true);
        }

        if (watchService != null) {
            watchService.close();
        }
    }

    private void setupWatch() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            publishDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);

            watchFuture = executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        WatchKey watchKey;
                        while ((watchKey = watchService.poll()) != null) {
                            try {
                                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                                    Path changed = (Path) watchEvent.context();
                                    if (changed.endsWith(announceFile.getFileName())) {
                                        long currentVersion = readLatestVersion();
                                        if (latestVersion != currentVersion) {
                                            latestVersion = currentVersion;
                                            for (HollowConsumer consumer : subscribedConsumers)
                                                consumer.triggerAsyncRefresh();
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                                log.log(Level.WARNING, "Exception reading the current announced version", th);
                            } finally {
                                watchKey.reset();
                            }
                        }
                    } catch (ClosedWatchServiceException io) {
                        // Ignore.  The finalizer must have run while we were running.
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        } catch (IOException io) {
            String msg = "IOException creating watch service (" + io.getMessage() + ").";
            log.log(Level.SEVERE, msg, io);
            throw new RuntimeException(msg, io);
        }
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
        if (!Files.isReadable(announceFile))
            return NO_ANNOUNCEMENT_AVAILABLE;

        try (BufferedReader reader = new BufferedReader(new FileReader(announceFile.toFile()))) {
            return Long.parseLong(reader.readLine());
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }
}
