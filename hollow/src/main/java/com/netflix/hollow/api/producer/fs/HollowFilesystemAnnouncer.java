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
package com.netflix.hollow.api.producer.fs;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.netflix.hollow.api.producer.HollowProducer;

public class HollowFilesystemAnnouncer implements HollowProducer.Announcer {
    private final Path publishPath;
    private final Path annnouncementPath;

    public HollowFilesystemAnnouncer(String namespace) {
        this(namespace, "announced.version");
    }

    public HollowFilesystemAnnouncer(String namespace, String announcementFilename) {
        this(Paths.get(System.getProperty("java.io.tmpdir"), namespace, "published"),
                announcementFilename);
    }

    public HollowFilesystemAnnouncer(Path publishDir, String announcementFilename) {
        this.publishPath = publishDir;
        annnouncementPath = publishPath.resolve(announcementFilename);
    }

    @Override
    public void announce(long stateVersion) {
        BufferedWriter writer = null;
        try {
            createDirectories(publishPath);
            writer = newBufferedWriter(annnouncementPath, UTF_8);
            writer.write(String.valueOf(stateVersion));
        } catch(IOException ex) {
            throw new RuntimeException("Unable to write to announcement file", ex);
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}