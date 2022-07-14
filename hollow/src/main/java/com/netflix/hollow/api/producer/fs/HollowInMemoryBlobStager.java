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
import com.netflix.hollow.core.write.HollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HollowInMemoryBlobStager implements HollowProducer.BlobStager {

    private final ProducerOptionalBlobPartConfig optionalPartConfig;

    public HollowInMemoryBlobStager() {
        this(null);
    }

    public HollowInMemoryBlobStager(ProducerOptionalBlobPartConfig optionalPartConfig) {
        this.optionalPartConfig = optionalPartConfig;
    }

    @Override
    public Blob openSnapshot(long version) {
        return new InMemoryBlob(HollowConstants.VERSION_NONE, version, Blob.Type.SNAPSHOT, optionalPartConfig);
    }

    @Override
    public Blob openDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.DELTA, optionalPartConfig);
    }

    @Override
    public Blob openReverseDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.REVERSE_DELTA, optionalPartConfig);
    }

    @Override
    public HollowProducer.HeaderBlob openHeader(long version) {
        return new InMemoryHeaderBlob(version);
    }

    public static class InMemoryHeaderBlob extends HeaderBlob {
        private byte[] data;

        protected InMemoryHeaderBlob(long version) {
            super(version);
        }

        @Override
        public void cleanup() {
        }

        @Override
        public void write(HollowBlobWriter blobWriter) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            blobWriter.writeHeader(baos, null);
            data = baos.toByteArray();
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }
    }

    public static class InMemoryBlob extends Blob {

        private byte[] data;
        private Map<String, byte[]> optionalParts;

        protected InMemoryBlob(long fromVersion, long toVersion, Type type) {
            super(fromVersion, toVersion, type);
        }

        protected InMemoryBlob(long fromVersion, long toVersion, Type type, ProducerOptionalBlobPartConfig optionalPartConfig) {
            super(fromVersion, toVersion, type, optionalPartConfig);
        }

        @Override
        public void write(HollowBlobWriter writer) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams optionalPartStreams = null;
            Map<String, ByteArrayOutputStream> optionalPartData = null;
            if(optionalPartConfig != null) {
                optionalPartStreams = optionalPartConfig.newStreams();
                optionalPartData = new HashMap<>();
                for(String part : optionalPartConfig.getParts()) {
                    ByteArrayOutputStream partBaos = new ByteArrayOutputStream();
                    optionalPartStreams.addOutputStream(part, partBaos);
                    optionalPartData.put(part, partBaos);
                }
            }

            switch(type) {
                case SNAPSHOT:
                    writer.writeSnapshot(baos, optionalPartStreams);
                    break;
                case DELTA:
                    writer.writeDelta(baos, optionalPartStreams);
                    break;
                case REVERSE_DELTA:
                    writer.writeReverseDelta(baos, optionalPartStreams);
                    break;
            }

            data = baos.toByteArray();

            if(optionalPartConfig != null) {
                optionalParts = new HashMap<>();
                for(Map.Entry<String, ByteArrayOutputStream> partEntry : optionalPartData.entrySet()) {
                    optionalParts.put(partEntry.getKey(), partEntry.getValue().toByteArray());
                }
            }
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public InputStream newOptionalPartInputStream(String partName) throws IOException {
            return new ByteArrayInputStream(optionalParts.get(partName));
        }

        @Override
        public Path getOptionalPartPath(String partName) {
            throw new UnsupportedOperationException("Path is not available");
        }

        @Override
        public void cleanup() {
        }

    }

}
