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

import com.netflix.hollow.api.producer.HollowProducer.BlobStager;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.BlobCompressor;
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
import java.util.Random;

public class HollowFilesystemBlobStager implements BlobStager {

    protected Path stagingPath;
    protected BlobCompressor compressor;

    /**
     * Constructor to create a new HollowFilesystemBlobStager with default disk path (java.io.tmpdir) and no compression for Hollow blobs.
     */
    public HollowFilesystemBlobStager() {
        this(Paths.get(System.getProperty("java.io.tmpdir")), BlobCompressor.NO_COMPRESSION);
    }

    /**
     * Constructor to create a new HollowFilesystemBlobStager with specified disk path and compression for Hollow blobs.
     *
     * @param stagingPath
     * @param compressor
     * @throws IOException
     */
    public HollowFilesystemBlobStager(Path stagingPath, BlobCompressor compressor) {
        this.stagingPath = stagingPath;
        this.compressor = compressor;

        try {
            if(!Files.exists(stagingPath))
                Files.createDirectories(stagingPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder; path=" + this.stagingPath, e);
        }

    }

    /**
     * Constructor to create a new HollowFilesystemBlobStager with specified disk path and compression for Hollow blobs.
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
        return new FilesystemBlob(Long.MIN_VALUE, version, SNAPSHOT, stagingPath, compressor);
    }

    @Override
    public HollowProducer.Blob openDelta(long fromVersion, long toVersion){
        return new FilesystemBlob(fromVersion, toVersion, DELTA, stagingPath, compressor);
    }

    @Override
    public HollowProducer.Blob openReverseDelta(long fromVersion, long toVersion) {
        return new FilesystemBlob(fromVersion, toVersion, REVERSE_DELTA, stagingPath, compressor);
    }

    public static class FilesystemBlob extends Blob {

        protected final Path path;
        private final BlobCompressor compressor;
        
        private FilesystemBlob(long fromVersion, long toVersion, Type type, Path dirPath, BlobCompressor compressor) {
            super(fromVersion, toVersion, type);
            
            this.compressor = compressor;

            int randomExtension = new Random().nextInt() & Integer.MAX_VALUE;

            switch (type) {
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
        }
        
        @Override
        public File getFile() {
            return path.toFile();
        }

        @Override
        public Path getPath() { return path; }

        @Override
        protected void write(HollowBlobWriter writer) throws IOException {
            Path parent = this.path.getParent();
            if(!Files.exists(parent))
                Files.createDirectories(parent);

            if(!Files.exists(path))
                Files.createFile(path);


            try (OutputStream os = new BufferedOutputStream(compressor.compress(Files.newOutputStream(path)))) {
                switch (type) {
                    case SNAPSHOT:
                        writer.writeSnapshot(os);
                        break;
                    case DELTA:
                        writer.writeDelta(os);
                        break;
                    case REVERSE_DELTA:
                        writer.writeReverseDelta(os);
                        break;
                    default:
                        throw new IllegalStateException("unknown type, type=" + type);
                }
            }
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new BufferedInputStream(compressor.decompress(Files.newInputStream(this.path)));
        }

        @Override
        public void cleanup() {
            try {
                if (this.path != null) Files.delete(this.path);
            } catch (IOException e) {
                throw new RuntimeException("Could not cleanup file: " + this.path.toString(), e);
            }
        }
    }

}
