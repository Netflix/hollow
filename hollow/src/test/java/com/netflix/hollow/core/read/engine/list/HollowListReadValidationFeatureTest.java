package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadConfiguration;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowListSchema;
import java.lang.reflect.Method;
import org.junit.Test;

public class HollowListReadValidationFeatureTest {
    @Test
    public void onlyOptedInImmutableReadsSkipValidation() throws ReflectiveOperationException {
        Method validate = HollowListTypeReadState.class.getDeclaredMethod("readWasUnsafe",
                HollowListTypeShardsHolder.class, int.class, HollowListTypeReadStateShard.class);
        validate.setAccessible(true);
        for(boolean enabled : new boolean[] { false, true }) {
            for(boolean recycling : new boolean[] { false, true }) {
                for(MemoryMode mode : new MemoryMode[] { MemoryMode.ON_HEAP, MemoryMode.SHARED_MEMORY_LAZY }) {
                    ArraySegmentRecycler recycler = recycling ? new RecyclingRecycler() : new WastefulRecycler(11, 8);
                    HollowReadStateEngine engine = new HollowReadStateEngine(new HollowReadConfiguration(mode, recycler, enabled
                            ? new ExperimentalFeature[] { ExperimentalFeature.SHARD_READ_FAST_PATHS }
                            : new ExperimentalFeature[0]));
                    HollowListTypeReadState state = new HollowListTypeReadState(engine, mode, new HollowListSchema("List", "Object"));
                    HollowListTypeReadStateShard oldShard = new HollowListTypeReadStateShard(null, 0);
                    HollowListTypeShardsHolder oldHolder = new HollowListTypeShardsHolder(new HollowListTypeReadStateShard[] { oldShard });
                    state.shardsVolatile = new HollowListTypeShardsHolder(new HollowListTypeReadStateShard[] {
                            new HollowListTypeReadStateShard(null, 0) });
                    boolean immutable = mode == MemoryMode.SHARED_MEMORY_LAZY || !recycling;
                    assertEquals(!(enabled && immutable), validate.invoke(state, oldHolder, 0, oldShard));
                }
            }
        }
    }
}
