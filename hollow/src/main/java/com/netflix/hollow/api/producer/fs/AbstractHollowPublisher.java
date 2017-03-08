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

import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.DELTA;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.REVERSE_DELTA;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.SNAPSHOT;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.deleteIfExists;

import java.util.Map;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;

public abstract class AbstractHollowPublisher implements HollowProducer.Publisher {
    protected final String namespace;
    protected final Path scratchPath;
    protected final Path stagingPath;

    protected AbstractHollowPublisher(String namespace) {
        this(namespace, Paths.get(System.getProperty("java.io.tmpdir")));
    }

    protected AbstractHollowPublisher(String namespace, Path scratchPath) {
        this(namespace, scratchPath, scratchPath.resolve(Paths.get(namespace, "staged")));
    }

    protected AbstractHollowPublisher(String namespace, Path scratchPath, Path stagingPath) {
        this.namespace = namespace;
        this.scratchPath = scratchPath;
        this.stagingPath = stagingPath;
    }

    @Override
    public Blob openSnapshot(long version) {
        return new StagedBlob(SNAPSHOT, namespace, stagingPath, Long.MIN_VALUE, version);
    }

    @Override
    public Blob openDelta(long fromVersion, long toVersion) {
        return new StagedBlob(DELTA, namespace, stagingPath, fromVersion, toVersion);
    }

    @Override
    public Blob openReverseDelta(long fromVersion, long toVersion) {
        return new StagedBlob(REVERSE_DELTA, namespace, stagingPath, fromVersion, toVersion);
    }
    
    @Override
    public void publish(Blob blob, Map<String, String> headerTags) {
        publish((StagedBlob)blob, headerTags);
    }
    
    public abstract void publish(StagedBlob blob, Map<String, String> headerTags);

    public static class StagedBlob implements Blob {
        protected final Blob.Type type;
        protected final long fromVersion;
        protected final long toVersion;
        protected final Path stagedArtifactPath;
        protected long size;

        protected StagedBlob(Blob.Type type, String namespace, Path stagingPath, long fromVersion, long toVersion) {
            this.type = type;
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;

            final Path p;
            switch(type) {
            case SNAPSHOT:
                p = stagingPath.resolve(String.format("%s-%s-%d", namespace, type.prefix, toVersion));
                break;
            case DELTA:
                p = stagingPath.resolve(String.format("%s-%s-%d-%d", namespace, type.prefix, fromVersion, toVersion));
                break;
            case REVERSE_DELTA:
                p = stagingPath.resolve(String.format("%s-%s-%d-%d", namespace, type.prefix, toVersion, fromVersion));
                break;
            default:
                throw new IllegalStateException("unknown blob type, type=" + type);
            }
            this.stagedArtifactPath = p;
        }

        @Override
        public OutputStream newOutputStream() {
            try {
                createDirectories(stagedArtifactPath.getParent());
                return new BufferedOutputStream(Files.newOutputStream(stagedArtifactPath));
            } catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public InputStream newInputStream() {
            try {
                return new BufferedInputStream(Files.newInputStream(stagedArtifactPath));
            } catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void cleanup() {
            try {
                deleteIfExists(stagedArtifactPath);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }

        public Blob.Type getType() {
            return type;
        }

        public long getSize() {
            return 0;// todo: need better way to get size of blobs in bytes.
        }

        public Path getStagedArtifactPath() {
            return stagedArtifactPath;
        }

        @Override
        public long getFromVersion() {
            return fromVersion;
        }

        @Override
        public long getToVersion() {
            return toVersion;
        }
    }
}
