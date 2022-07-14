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
package com.netflix.hollow.test;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * This class allows constructing HollowWriteStateEngine instances for testing purposes.
 * You cannot add to a HollowWriteStateEngineBuilder after calling build, however you may build
 * multiple HollowWriteStateEngine objects with the same builder.
 */
public class HollowWriteStateEngineBuilder {
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;

    private boolean built;

    /**
     * Create a HollowWriteStateEngineBuilder with an empty initial type state. Adding objects will
     * add their types, if not already present.
     */
    public HollowWriteStateEngineBuilder() {
        this(Collections.emptyList());
    }

    /**
     * Create a HollowWriteStateEngineBuilder, initializing it with a type state containing the
     * provided types. Adding objects will add their types, if not already present.
     */
    public HollowWriteStateEngineBuilder(Collection<Class<?>> types) {
        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        for(Class<?> type : types) {
            objectMapper.initializeTypeState(type);
        }
    }

    /**
     * Add an object that will be in our built HollowWriteStateEngine. You cannot add any more
     * objects after calling build().
     */
    public HollowWriteStateEngineBuilder add(Object... objects) {
        if(built) {
            throw new IllegalArgumentException("Cannot add after building Hollow state engine");
        }
        Arrays.stream(objects).forEach(objectMapper::add);
        return this;
    }

    /**
     * Build a HollowWriteStateEngine. You cannot add() any more objects after calling this.
     */
    public HollowWriteStateEngine build() {
        built = true;
        return writeEngine;
    }
}
