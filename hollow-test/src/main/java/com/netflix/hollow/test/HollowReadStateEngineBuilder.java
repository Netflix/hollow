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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * This class allows constructing HollowReadStateEngine instances for testing purposes.
 * You cannot add to a HollowReadStateEngineBuilder after calling build, however you may build
 * multiple HollowReadStateEngine objects with the same builder.
 */
public class HollowReadStateEngineBuilder {
    private final HollowWriteStateEngineBuilder writeEngineBuilder;

    /**
     * Create a HollowReadStateEngineBuilder with an empty initial type state. Adding objects will
     * add their types, if not already present.
     */
    public HollowReadStateEngineBuilder() {
        this(Collections.emptyList());
    }

    /**
     * Create a HollowReadStateEngineBuilder, initializing it with a type state containing the
     * provided types. Adding objects will add their types, if not already present.
     */
    public HollowReadStateEngineBuilder(Collection<Class<?>> types) {
        writeEngineBuilder = new HollowWriteStateEngineBuilder(types);
    }

    /**
     * Add an object that will be in our built HollowReadStateEngine. You cannot add any more
     * objects after calling build().
     */
    public HollowReadStateEngineBuilder add(Object... objects) {
        writeEngineBuilder.add(objects);
        return this;
    }

    /**
     * Build a HollowReadStateEngine. You cannot add() any more objects after calling this.
     */
    public HollowReadStateEngine build() {
        HollowWriteStateEngine writeEngine = writeEngineBuilder.build();
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        try {
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine, null);
        } catch (IOException e) {
            throw new RuntimeException("Error creating HollowReadStateEngine", e);
        }
        return readEngine;
    }
}
