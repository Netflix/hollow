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

import com.netflix.hollow.api.producer.HollowProducer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HollowFilesystemAnnouncer implements HollowProducer.Announcer {
    
    public static final String ANNOUNCEMENT_FILENAME = "announced.version";
    
    private final Path publishPath;

    // TODO: deprecate in Hollow 3.0.0
    // @Deprecated
    public HollowFilesystemAnnouncer(File publishDir) {
        this(publishDir.toPath());
    }

    /**
     * @since 2.12.0
     */
    public HollowFilesystemAnnouncer(Path publishPath) {
        this.publishPath = publishPath;
    }

    @Override
    public void announce(long stateVersion) {
        Path announcePath = publishPath.resolve(ANNOUNCEMENT_FILENAME);
        try {
            Files.write(announcePath, String.valueOf(stateVersion).getBytes());
        } catch(IOException ex) {
            throw new RuntimeException("Unable to write to announcement file; path=" + announcePath, ex);
        }
    }

}
