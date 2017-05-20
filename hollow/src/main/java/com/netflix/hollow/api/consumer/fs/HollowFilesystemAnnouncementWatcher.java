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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HollowFilesystemAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {

    private final File publishDir;
    private final List<HollowConsumer> subscribedConsumers;
    
    private long latestVersion;
    
    public HollowFilesystemAnnouncementWatcher(File publishDir) {
        this.publishDir = publishDir;
        this.subscribedConsumers = new CopyOnWriteArrayList<HollowConsumer>();
        this.latestVersion = readLatestVersion();
        
        setupPolling();
    }
    
    private void setupPolling() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        long currentVersion = readLatestVersion();
                        if(latestVersion != currentVersion) {
                            latestVersion = currentVersion;
                            for(HollowConsumer consumer : subscribedConsumers)
                                consumer.triggerAsyncRefresh();
                        }
                        
                            Thread.sleep(1000);
                    } catch(Throwable th) { 
                        th.printStackTrace();
                    }
                }
            }
        });
        
        t.setDaemon(true);
        t.start();
    }
    
    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    @Override
    public void subscribeToUpdates(final HollowConsumer consumer) {
        subscribedConsumers.add(consumer);
    }
    
    public long readLatestVersion() {
        File f = new File(publishDir, HollowFilesystemAnnouncer.ANNOUNCEMENT_FILENAME);
        
        if(!f.exists())
            return NO_ANNOUNCEMENT_AVAILABLE;
        
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            return Long.parseLong(reader.readLine());
        } catch(IOException e) {
        	throw new RuntimeException(e);
        }
    }
}
