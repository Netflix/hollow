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

import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.DELTA;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.REVERSE_DELTA;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.SNAPSHOT;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.FileSystems;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HollowFilesystemPublisher implements HollowProducer.Publisher {
    protected final Path publishPath;
    protected final HollowFilesystemBlobFactory blobFactory;
    protected final String namespace;
    protected final String dir;

    public HollowFilesystemPublisher(String namespace) {
        this.namespace = namespace;
        this.dir = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), namespace).toString();
        this.publishPath = Paths.get(System.getProperty("java.io.tmpdir"), namespace, "published");
        this.blobFactory = new HollowFilesystemBlobFactory();
    }

    public Path getStagingDir() {
        return publishPath;
    }
    
    public Path getPublishDir() {
        return publishPath;
    }

    @Override
    public HollowProducer.Blob openSnapshot(long version) {
        return blobFactory.withNamespace(namespace, Long.MIN_VALUE, version, dir, SNAPSHOT);
    }

    @Override
    public HollowProducer.Blob openDelta(long fromVersion, long toVersion) {
        return blobFactory.withNamespace(namespace, fromVersion, toVersion, dir, DELTA);
    }

    @Override
    public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion) {
        return blobFactory.withNamespace(namespace, fromVersion, toVersion, dir, REVERSE_DELTA);
    }

    @Override
    public void publish(HollowProducer.Blob blob, Map<String, String> headerTags) {
        try {
            Files.createDirectories(publishPath);

            Path source = Paths.get(((HollowFilesystemBlobFactory.FsBlob) blob).getFile().getPath());
            Path filename = source.getFileName();
            Path destination = publishPath.resolve(filename);
            Path intermediate = destination.resolveSibling(filename + ".incomplete");
            Files.copy(source, intermediate, REPLACE_EXISTING);
            Files.move(intermediate, destination, ATOMIC_MOVE);

        } catch(IOException ex) {
            throw new RuntimeException("Unable to publish file!", ex);
        }
    }
}
