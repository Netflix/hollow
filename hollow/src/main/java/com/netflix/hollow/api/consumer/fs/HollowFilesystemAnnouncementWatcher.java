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
import com.netflix.hollow.api.producer.fs.HollowFilesystemVersionPinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.Files.getLastModifiedTime;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class HollowFilesystemAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {

    private final Logger log = Logger.getLogger(HollowFilesystemAnnouncementWatcher.class.getName());

    private final Path publishDir;
    private final Path announceFile;
    private final Path pinVersionFile;

    private final List<HollowConsumer> subscribedConsumers;
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> watchFuture;
    private boolean ownedExecutor;

    private long latestVersion;
    private long pinVersion = NO_VERSION_AVAILABLE;

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
        this.pinVersionFile = this.publishDir.resolve(HollowFilesystemVersionPinner.VERSION_PIN_FILENAME);
        this.subscribedConsumers = new CopyOnWriteArrayList<>();
        this.latestVersion = readLatestVersion();

        this.watchFuture = setupWatch();
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
    }

    private ScheduledFuture setupWatch() {
        return executor.scheduleWithFixedDelay(new Runnable() {
            private FileTime previousFileTime = FileTime.from(0, TimeUnit.MILLISECONDS);

            @Override
            public void run() {
                try {
                    if (!Files.isReadable(announceFile) && !Files.isReadable(pinVersionFile)) return;

                    long pinnedVersion = readVersion(pinVersionFile);
                    FileTime lastModifiedTime;
                    if(pinnedVersion != NO_VERSION_AVAILABLE) {
                        lastModifiedTime = getLastModifiedTime(pinVersionFile);
                        pinVersion = pinnedVersion;
                    } else {
                        lastModifiedTime = getLastModifiedTime(announceFile);
                    }

                    if (lastModifiedTime.compareTo(previousFileTime) > 0) {
                        previousFileTime = lastModifiedTime;
                        long currentVersion = readLatestVersion();
                        if (latestVersion != currentVersion) {
                            latestVersion = currentVersion;
                            for (HollowConsumer consumer : subscribedConsumers)
                                consumer.triggerAsyncRefresh();
                        }
                    }
                } catch (Throwable th) {
                    log.log(Level.WARNING, "Exception reading the current announced version", th);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    @Override
    public long getPinVersion() { return pinVersion; }

    @Override
    public boolean hasPinVersion() {
        return pinVersion != NO_VERSION_AVAILABLE;
    }

    @Override
    public void subscribeToUpdates(final HollowConsumer consumer) {
        subscribedConsumers.add(consumer);
    }

    private long readLatestVersion() {
        long pinnedVersion = readVersion(pinVersionFile);
        if(pinnedVersion != NO_VERSION_AVAILABLE ) {
            return pinnedVersion;
        }

        return readVersion(announceFile);
    }

    private long readVersion(Path versionFile) {
        if (!Files.isReadable(versionFile))
            return NO_VERSION_AVAILABLE;

        try (BufferedReader reader = new BufferedReader(new FileReader(versionFile.toFile()))) {
            return Long.parseLong(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
