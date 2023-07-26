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

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.HeaderBlob;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.SegmentedByteArrayInputStream;
import com.netflix.hollow.core.memory.SegmentedByteArrayOutputStream;
import com.netflix.hollow.core.write.HollowBlobWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HollowInMemoryBlobStager implements HollowProducer.BlobStager {

    private final ProducerOptionalBlobPartConfig optionalPartConfig;
    private final HollowProducer.BlobCompressor blobCompressor;

    public HollowInMemoryBlobStager(HollowProducer.BlobCompressor compressor) {
        this(null, compressor); // shouldn't come here as blob compressor comes from builder
    }

    public HollowInMemoryBlobStager() {
        this(null, HollowProducer.BlobCompressor.NO_COMPRESSION); // shouldn't come here as blob compressor comes from builder
    }

    public HollowInMemoryBlobStager(ProducerOptionalBlobPartConfig optionalPartConfig, HollowProducer.BlobCompressor blobCompressor) {
        this.optionalPartConfig = optionalPartConfig;
        this.blobCompressor = blobCompressor;
    }

    @Override
    public Blob openSnapshot(long version) {
        return new InMemoryBlob(HollowConstants.VERSION_NONE, version, Blob.Type.SNAPSHOT, optionalPartConfig, blobCompressor);
    }

    @Override
    public Blob openDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.DELTA, optionalPartConfig, blobCompressor);
    }

    @Override
    public Blob openReverseDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.REVERSE_DELTA, optionalPartConfig, blobCompressor);
    }

    @Override
    public HollowProducer.HeaderBlob openHeader(long version) {
        return new InMemoryHeaderBlob(version, blobCompressor);
    }

    public static class InMemoryHeaderBlob extends HeaderBlob {
        private byte[] data; // changed from byte[]
        private HollowProducer.BlobCompressor blobCompressor;

        protected InMemoryHeaderBlob(long version, HollowProducer.BlobCompressor blobCompressor) {
            super(version);
            this.blobCompressor = blobCompressor;
        }

        @Override
        public void cleanup() {
            data = null;
        }

        @Override
        public void write(HollowBlobWriter blobWriter) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (OutputStream os = new BufferedOutputStream(blobCompressor.compress(baos))) {
                blobWriter.writeHeader(os, null);
            }
            //segmentedByteArray = baos.toString();
            data = baos.toByteArray();
        }

        @Override
        public InputStream newInputStream() throws IOException {
            //return new BufferedInputStream(blobCompressor.decompress(new ByteArrayInputStream(segmentedByteArray.getBytes(StandardCharsets.UTF_8))));
            return new BufferedInputStream(blobCompressor.decompress(new ByteArrayInputStream(data)));
        }
    }

    public static class InMemoryBlob extends Blob {

        //private ByteDataArray segmentedByteArray;
        private SegmentedByteArray segmentedByteArray;
        private Map<String, byte[]> optionalParts;

        private HollowProducer.BlobCompressor blobCompressor;
        
        protected InMemoryBlob(long fromVersion, long toVersion, Type type) {
            super(fromVersion, toVersion, type);
        }

        protected InMemoryBlob(long fromVersion, long toVersion, Type type, ProducerOptionalBlobPartConfig optionalPartConfig, HollowProducer.BlobCompressor blobCompressor) {
            super(fromVersion, toVersion, type, optionalPartConfig);
            this.blobCompressor = blobCompressor;
        }

        @Override
        public void write(HollowBlobWriter writer) throws IOException {
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //DataOutputStream dataOutputStream = new DataOutputStream();
            SegmentedByteArrayOutputStream outputStream = new SegmentedByteArrayOutputStream(segmentedByteArray);

            ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams optionalPartStreams = null;
            Map<String, ByteArrayOutputStream> optionalPartData = null;
            if (optionalPartConfig != null) {
                optionalPartStreams = optionalPartConfig.newStreams();
                optionalPartData = new HashMap<>();
                for(String part : optionalPartConfig.getParts()) {
                    ByteArrayOutputStream partBaos = new ByteArrayOutputStream();
                    optionalPartStreams.addOutputStream(part, new BufferedOutputStream(blobCompressor.compress(partBaos)));
                    optionalPartData.put(part, partBaos);
                }
            }

            try (OutputStream os = new BufferedOutputStream(blobCompressor.compress(outputStream))) {
                switch (type) {
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
            }
            //segmentedByteArray = baos.toByteArray();
            segmentedByteArray = outputStream.getSegmentedByteArray();
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new BufferedInputStream(blobCompressor.decompress(new SegmentedByteArrayInputStream(segmentedByteArray))); //creating buffer array underneath
        }

        @Override
        public InputStream newOptionalPartInputStream(String partName) throws IOException {
            return new BufferedInputStream(blobCompressor.decompress(new ByteArrayInputStream(optionalParts.get(partName))));
        }

        @Override
        public byte[] getData() {
            return
        }

        @Override
        public SegmentedByteArray getSegmentedByteArray() {
            return this.segmentedByteArray;
        }

        @Override
        public Path getOptionalPartPath(String partName) {
            throw new UnsupportedOperationException("Path is not available");
        }

        @Override
        public void cleanup() {
            segmentedByteArray = null;
        }
    }

}
