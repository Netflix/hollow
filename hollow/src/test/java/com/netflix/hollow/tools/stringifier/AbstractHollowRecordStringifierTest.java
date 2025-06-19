/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.tools.stringifier;

import static com.netflix.hollow.tools.stringifier.HollowStringifier.NEWLINE;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

/**
 * Code shared between HollowRecordStringifierTest and HollowRecordJsonStringifierTest.
 */
public class AbstractHollowRecordStringifierTest {
    static class TypeWithString {
        private final String value;

        public TypeWithString(String value) {
            this.value = value;
        }
    }

    static class TypeWithPrimitive {
        private final int value;

        public TypeWithPrimitive(int value) {
            this.value = value;
        }
    }

    static class TypeWithNonPrimitive {
        private final Integer value;

        public TypeWithNonPrimitive(Integer value) {
            this.value = value;
        }
    }

    static class TypeWithNestedPrimitive {
        private final Double value;
        private final TypeWithPrimitive nestedType;

        public TypeWithNestedPrimitive(Double value, TypeWithPrimitive nestedType) {
            this.value = value;
            this.nestedType = nestedType;
        }
    }

    static class TypeWithNestedNonPrimitive {
        private final Double value;
        private final TypeWithNonPrimitive nestedType;

        public TypeWithNestedNonPrimitive(Double value, TypeWithNonPrimitive nestedType) {
            this.value = value;
            this.nestedType = nestedType;
        }
    }

    static class TypeWithSetOfPrimitives {
        private final Set<TypeWithPrimitive> values;

        public TypeWithSetOfPrimitives(Set<TypeWithPrimitive> values) {
            this.values = values;
        }
    }

    static class TypeWithSetOfStrings {
        private final Set<TypeWithString> values;

        public TypeWithSetOfStrings(Set<TypeWithString> values) {
            this.values = values;
        }
    }

    /**
     * Sends instances of a type through the HollowRecordStringifier. This concatenates records
     * for all the instances, separated by newlines.
     */
    protected static <T, S extends HollowStringifier> String stringifyType(
            Class<T> clazz, HollowStringifier<S> stringifier, T... instances) throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        // add our types and records to the writeStateEngine
        objectMapper.initializeTypeState(clazz);
        for (T instance : instances) {
            objectMapper.add(instance);
        }
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        // simulate a roundtrip to copy our types and records into the readStateEngine
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
        StringWriter writer = new StringWriter();
        // use the version of stringify that takes a Writer, since the non-Writer version calls this
        stringifier.stringify(writer, readStateEngine, clazz.getSimpleName(), 0);
        // join all records with newlines
        for (int i = 1; i < instances.length; i++) {
            writer.append(NEWLINE);
            stringifier.stringify(writer, readStateEngine, clazz.getSimpleName(), i);
        }
        return writer.toString();
    }
}
