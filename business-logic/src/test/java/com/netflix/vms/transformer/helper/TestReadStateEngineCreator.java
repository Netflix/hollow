package com.netflix.vms.transformer.helper;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.Set;
import org.reflections.Reflections;

/**
 * A helper class to allow creating HollowReadStateEngine objects for testing purposes.
 */
public class TestReadStateEngineCreator {
    private static final Set<Class<? extends Cloneable>> TOP_LEVEL_TYPES = new Reflections(
            "com.netflix.vms.transformer.converterpojos").getSubTypesOf(Cloneable.class);
    private final HollowWriteStateEngine writeEngine;
    private final HollowObjectMapper objectMapper;

    private boolean built;

    public TestReadStateEngineCreator() {
        writeEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeEngine);
        // we can remove this when we move ConfiguredSchemaLoader out of the converter
        TOP_LEVEL_TYPES.forEach(objectMapper::initializeTypeState);
    }

    /**
     * Add an object to our HollowReadStateEngine. You cannot add any more objects after calling build().
     */
    public TestReadStateEngineCreator add(Object object) {
      if (built) {
          throw new IllegalArgumentException("Cannot add after building HollowReadStateEngine");
      }
        objectMapper.add(object);
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
            throw new RuntimeException("Error creating ReadStateEngine", e);
        }
        return readEngine;
    }
}
