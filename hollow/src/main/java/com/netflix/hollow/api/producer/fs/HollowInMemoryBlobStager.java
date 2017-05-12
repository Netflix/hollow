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
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.core.write.HollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HollowInMemoryBlobStager implements HollowProducer.BlobStager {

    @Override
    public Blob openSnapshot(long version) {
        return new InMemoryBlob(Long.MIN_VALUE, version, Blob.Type.SNAPSHOT);
    }

    @Override
    public Blob openDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.DELTA);
    }

    @Override
    public Blob openReverseDelta(long fromVersion, long toVersion) {
        return new InMemoryBlob(fromVersion, toVersion, Blob.Type.REVERSE_DELTA);
    }
    
    public static class InMemoryBlob extends Blob {

        private byte[] data;
        
        protected InMemoryBlob(long fromVersion, long toVersion, Type type) {
            super(fromVersion, toVersion, type);
        }

        @Override
        protected void write(HollowBlobWriter writer) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            switch(type) {
            case SNAPSHOT:
                writer.writeSnapshot(baos);
                break;
            case DELTA:
                writer.writeDelta(baos);
                break;
            case REVERSE_DELTA:
                writer.writeReverseDelta(baos);
                break;
            }
            
            data = baos.toByteArray();
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public void cleanup() { }
    }

}
