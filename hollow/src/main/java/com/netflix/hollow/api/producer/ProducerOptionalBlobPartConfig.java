/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A blob may be configured to produce optional "parts".  Parts will contain the data one or more types.
 * 
 * A type may be assigned to one and only one section -- by default a type will be serialized to the main "part".
 * 
 * The optional blob parts are intended to be stored as separate files in the blob store, so that they may be 
 * retrieved by consumers only when required. Details of how this is accomplished will be dependent upon individual
 * implementations of {@link HollowProducer.Publisher} and {@link HollowConsumer.BlobRetriever}.
 */
public class ProducerOptionalBlobPartConfig {

    private final Map<String, Set<String>> parts;

    public ProducerOptionalBlobPartConfig() {
        this.parts = new HashMap<>();
    }

    public void addTypesToPart(String partName, String... types) {
        if(types.length == 0)
            return;

        Set<String> typeSet = parts.computeIfAbsent(partName, n -> new HashSet<>());

        for(String type : types) {
            typeSet.add(type);
        }
    }

    public Set<String> getParts() {
        return parts.keySet();
    }

    public OptionalBlobPartOutputStreams newStreams() {
        return new OptionalBlobPartOutputStreams();
    }

    public OptionalBlobPartOutputStreams newStreams(Function<String, OutputStream> streamCreator) {
        OptionalBlobPartOutputStreams s = newStreams();
        for(String part : getParts()) {
            s.addOutputStream(part, streamCreator.apply(part));
        }
        return s;
    }

    public class OptionalBlobPartOutputStreams {

        private final Map<String, ConfiguredOutputStream> partStreams;

        private OptionalBlobPartOutputStreams() {
            this.partStreams = new HashMap<>();
        }

        public void addOutputStream(String partName, OutputStream os) {
            Set<String> types = parts.get(partName);

            if(types == null)
                throw new IllegalArgumentException("There is no blob part named " + partName + " in this configuration");

            partStreams.put(partName, new ConfiguredOutputStream(partName, types, new DataOutputStream(os)));
        }

        public Map<String, DataOutputStream> getStreamsByType() {
            if(!allPartsHaveStreams())
                throw new IllegalStateException("Not all configured parts have streams!");

            Map<String, DataOutputStream> streamsByType = new HashMap<>();

            for(Map.Entry<String, ConfiguredOutputStream> entry : partStreams.entrySet()) {
                ConfiguredOutputStream cos = entry.getValue();

                for(String type : cos.getTypes()) {
                    streamsByType.put(type, cos.getStream());
                }
            }

            return streamsByType;
        }

        public Map<String, String> getPartNameByType() {
            if(!allPartsHaveStreams())
                throw new IllegalStateException("Not all configured parts have streams!");

            Map<String, String> streamsByType = new HashMap<>();

            for(Map.Entry<String, ConfiguredOutputStream> entry : partStreams.entrySet()) {
                ConfiguredOutputStream cos = entry.getValue();

                for(String type : cos.getTypes()) {
                    streamsByType.put(type, cos.getPartName());
                }
            }

            return streamsByType;
        }

        public Map<String, ConfiguredOutputStream> getPartStreams() {
            return Collections.unmodifiableMap(partStreams);
        }

        public void flush() throws IOException {
            for(Map.Entry<String, ConfiguredOutputStream> entry : partStreams.entrySet()) {
                entry.getValue().getStream().flush();
            }
        }

        public void close() throws IOException {
            for(Map.Entry<String, ConfiguredOutputStream> entry : partStreams.entrySet()) {
                entry.getValue().getStream().close();
            }
        }

        private boolean allPartsHaveStreams() {
            return parts.keySet().equals(partStreams.keySet());
        }
    }

    public static class ConfiguredOutputStream {
        private final String partName;
        private final Set<String> types;
        private final DataOutputStream stream;

        public ConfiguredOutputStream(String partName, Set<String> types, DataOutputStream stream) {
            this.partName = partName;
            this.types = Collections.unmodifiableSet(types);
            this.stream = stream;
        }

        public String getPartName() {
            return partName;
        }

        public Set<String> getTypes() {
            return types;
        }

        public DataOutputStream getStream() {
            return stream;
        }
    }
}
