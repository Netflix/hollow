/*
 *  Copyright 2016-2021 Netflix, Inc.
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

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.BlobCompressor;
import com.netflix.hollow.api.producer.HollowProducer.BlobStager;
import com.netflix.hollow.api.producer.HollowProducer.HeaderBlob;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.write.HollowBlobWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HollowFilesystemBlobStager implements BlobStager {

    protected Path stagingPath;
    protected BlobCompressor compressor;
    protected ProducerOptionalBlobPartConfig optionalPartConfig;

    /**
     * Constructor to create a new HollowFilesystemBlobStager with default disk path
     * (java.io.tmpdir) and no compression for Hollow blobs.
     */
    public HollowFilesystemBlobStager() {
        this(Paths.get(System.getProperty("java.io.tmpdir")), BlobCompressor.NO_COMPRESSION);
    }

    /**
     * Constructor to create a new HollowFilesystemBlobStager with specified disk
     * path and compression for Hollow blobs.
     *
     * @param stagingPath the path where to stage blobs
     * @param compressor the blob compressor
     * @throws RuntimeException if errors occur when creating the specified path
     */
    public HollowFilesystemBlobStager(Path stagingPath, BlobCompressor compressor) throws RuntimeException {
        this(stagingPath, compressor, null);
    }

    public HollowFilesystemBlobStager(Path stagingPath, BlobCompressor compressor, ProducerOptionalBlobPartConfig optionalPartConfig) throws RuntimeException {
        this.stagingPath = stagingPath;
        this.compressor = compressor;
        this.optionalPartConfig = optionalPartConfig;

        try {
            if(!Files.exists(stagingPath))
                Files.createDirectories(stagingPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder; path=" + this.stagingPath, e);
        }

    }

    /**
     * Constructor to create a new HollowFilesystemBlobStager with specified disk
     * path and compression for Hollow blobs.
     *
     * @param stagingPath        directory to use to write hollow blob files.
     * @param compressor the {@link HollowProducer.BlobCompressor} to compress blob files with
     * @deprecated Use Path instead
     * @since 2.12.0
     */
    @Deprecated
    public HollowFilesystemBlobStager(File stagingPath, BlobCompressor compressor) {
        this(stagingPath.toPath(), compressor);
    }

    @Override
    public HollowProducer.Blob openSnapshot(long version) {
        return new FilesystemBlob(HollowConstants.VERSION_NONE, version, SNAPSHOT, stagingPath, compressor, optionalPartConfig);
    }

    @Override
    public HollowProducer.Blob openDelta(long fromVersion, long toVersion) {
        return new FilesystemBlob(fromVersion, toVersion, DELTA, stagingPath, compressor, optionalPartConfig);
    }

    @Override
    public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion) {
        return new FilesystemBlob(fromVersion, toVersion, REVERSE_DELTA, stagingPath, compressor, optionalPartConfig);
    }

    @Override
    public HollowProducer.HeaderBlob openHeader(long version) {
        return new FilesystemHeaderBlob(version, stagingPath, compressor);
    }

    public static class FilesystemHeaderBlob extends HeaderBlob {
        protected final Path path;
        private final BlobCompressor compressor;

        protected FilesystemHeaderBlob(long version, Path dirPath, BlobCompressor compressor) {
            super(version);
            this.compressor = compressor;
            int randomExtension = new Random().nextInt() & Integer.MAX_VALUE;
            this.path = dirPath.resolve(String.format("header-%d.%s", version, Integer.toHexString(randomExtension)));
        }

        @Override
        public void cleanup() {
            if(path != null) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException("Could not cleanup file: " + this.path, e);
                }
            }
        }

        @Override
        public void write(HollowBlobWriter blobWriter) throws IOException {
            Path parent = this.path.getParent();
            if(!Files.exists(parent))
                Files.createDirectories(parent);

            if(!Files.exists(path))
                Files.createFile(path);

            try (OutputStream os = new BufferedOutputStream(compressor.compress(Files.newOutputStream(path)))) {
                blobWriter.writeHeader(os, null);
            }
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new BufferedInputStream(compressor.decompress(Files.newInputStream(this.path)));
        }

        @Override
        public File getFile() {
            return path.toFile();
        }

        @Override
        public Path getPath() {
            return path;
        }
    }

    public static class FilesystemBlob extends Blob {

        protected final Path path;
        protected final Map<String, Path> optionalPartPaths;
        private final BlobCompressor compressor;

        private FilesystemBlob(long fromVersion, long toVersion, Type type, Path dirPath, BlobCompressor compressor, ProducerOptionalBlobPartConfig optionalPartConfig) {
            super(fromVersion, toVersion, type, optionalPartConfig);

            this.optionalPartPaths = optionalPartConfig == null ? Collections.emptyMap() : new HashMap<>();

            this.compressor = compressor;

            int randomExtension = new Random().nextInt() & Integer.MAX_VALUE;

            switch(type) {
                case SNAPSHOT:
                    this.path = dirPath.resolve(String.format("%s-%d.%s", type.prefix, toVersion, Integer.toHexString(randomExtension)));
                    break;
                case DELTA:
                case REVERSE_DELTA:
                    this.path = dirPath.resolve(String.format("%s-%d-%d.%s", type.prefix, fromVersion, toVersion, Integer.toHexString(randomExtension)));
                    break;
                default:
                    throw new IllegalStateException("unknown blob type, type=" + type);
            }

            if(optionalPartConfig != null) {
                for(String part : optionalPartConfig.getParts()) {
                    Path partPath;
                    switch(type) {
                        case SNAPSHOT:
                            partPath = dirPath.resolve(String.format("%s_%s-%d.%s", type.prefix, part, toVersion, Integer.toHexString(randomExtension)));
                            break;
                        case DELTA:
                        case REVERSE_DELTA:
                            partPath = dirPath.resolve(String.format("%s_%s-%d-%d.%s", type.prefix, part, fromVersion, toVersion, Integer.toHexString(randomExtension)));
                            break;
                        default:
                            throw new IllegalStateException("unknown blob type, type=" + type);
                    }
                    optionalPartPaths.put(part, partPath);
                }
            }
        }

        @Override
        public File getFile() {
            return path.toFile();
        }

        @Override
        public Path getPath() {
            return path;
        }

        @Override
        public void write(HollowBlobWriter writer) throws IOException {
            Path parent = this.path.getParent();
            if(!Files.exists(parent))
                Files.createDirectories(parent);

            if(!Files.exists(path))
                Files.createFile(path);

            ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams optionalPartStreams = null;

            if(optionalPartConfig != null) {
                optionalPartStreams = optionalPartConfig.newStreams();

                for(Map.Entry<String, Path> partPathEntry : optionalPartPaths.entrySet()) {
                    String partName = partPathEntry.getKey();
                    Path partPath = partPathEntry.getValue();
                    optionalPartStreams.addOutputStream(partName, new BufferedOutputStream(compressor.compress(Files.newOutputStream(partPath))));
                }
            }

            try (OutputStream os = new BufferedOutputStream(compressor.compress(Files.newOutputStream(path)))) {
                switch(type) {
                    case SNAPSHOT:
                        writer.writeSnapshot(os, optionalPartStreams);
                        break;
                    case DELTA:
                        writer.writeDelta(os, optionalPartStreams);
                        break;
                    case REVERSE_DELTA:
                        writer.writeReverseDelta(os, optionalPartStreams);
                        break;
                    default:
                        throw new IllegalStateException("unknown type, type=" + type);
                }
            } finally {
                if(optionalPartStreams != null)
                    optionalPartStreams.close();
            }

        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new BufferedInputStream(compressor.decompress(Files.newInputStream(this.path)));
        }

        @Override
        public InputStream newOptionalPartInputStream(String partName) throws IOException {
            Path partPath = optionalPartPaths.get(partName);
            if(partPath == null)
                throw new IllegalArgumentException("Path for part " + partName + " does not exist.");

            return new BufferedInputStream(compressor.decompress(Files.newInputStream(partPath)));
        }

        @Override
        public Path getOptionalPartPath(String partName) {
            Path partPath = optionalPartPaths.get(partName);
            if(partPath == null)
                throw new IllegalArgumentException("Path for part " + partName + " does not exist.");
            return partPath;
        }

        @Override
        public void cleanup() {
            cleanupFile(path);
            for(Map.Entry<String, Path> entry : optionalPartPaths.entrySet()) {
                cleanupFile(entry.getValue());
            }
        }

        private void cleanupFile(Path path) {
            try {
                if(path != null)
                    Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Could not cleanup file: " + this.path.toString(), e);
            }
        }
    }

}
