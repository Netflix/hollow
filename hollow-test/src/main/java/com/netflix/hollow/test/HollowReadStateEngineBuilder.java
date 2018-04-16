package com.netflix.hollow.test;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * This class allows constructing HollowReadStateEngine instances for testing purposes.
 * You cannot add to a HollowReadStateEngineBuilder after calling build, however you may build
 * multiple HollowReadStateEngine objects with the same builder.
 */
public class HollowReadStateEngineBuilder {
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;

    private boolean built;

    /**
     * Create a HollowReadStateEngineBuilder with an empty initial type state. Adding objects will
     * add their types, if not already present.
     */
    public HollowReadStateEngineBuilder() {
        this(Collections.<Class<?>>emptyList());
    }

    /**
     * Create a HollowReadStateEngineBuilder, initializing it with a type state containing the
     * provided types. Adding objects will add their types, if not already present.
     */
    public HollowReadStateEngineBuilder(Collection<Class<?>> types) {
        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        for (Class<?> type : types) {
            objectMapper.initializeTypeState(type);
        }
    }

    /**
     * Add an object that will be in our built HollowReadStateEngine. You cannot add any more
     * objects after calling build().
     */
    public HollowReadStateEngineBuilder add(Object... objects) {
        if (built) {
            throw new IllegalArgumentException("Cannot add after building HollowReadStateEngine");
        }
        for (Object o : objects) {
            objectMapper.add(o);
        }
        return this;
    }

    /**
     * Build a HollowReadStateEngine. You cannot add() any more objects after calling this.
     */
    public HollowReadStateEngine build() {
        built = true;
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        try {
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine, null);
        } catch (IOException e) {
            throw new RuntimeException("Error creating HollowReadStateEngine", e);
        }
        return readEngine;
    }
}
