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
package com.netflix.hollow.api.producer.fs;

import com.netflix.hollow.api.producer.HollowProducer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HollowFilesystemVersionPinner implements HollowProducer.VersionPinner {

    public static final String VERSION_PINNED_FILENAME = "pinned.version";

    public static final long NO_VERSION_AVAILABLE = Long.MIN_VALUE;

    private final File publishDir;

    public HollowFilesystemVersionPinner(File publishDir) {
        this.publishDir = publishDir;
    }

    @Override
    public void pin(long pinnedVersion) {
        writePinVersion(pinnedVersion);
    }

    @Override
    public void unpin() {
        writePinVersion(NO_VERSION_AVAILABLE);
    }

    private void writePinVersion(long pinnedVersion) {
        File pinnedVersionFile = new File(publishDir, VERSION_PINNED_FILENAME);

        try (FileWriter writer = new FileWriter(pinnedVersionFile)){
            writer.write(String.valueOf(pinnedVersion));
        } catch(IOException ex) {
            throw new RuntimeException("Unable to write to pinned version file", ex);
        }
    }
}
