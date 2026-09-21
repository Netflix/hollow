package com.netflix.hollow.core.read.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import org.junit.Test;

public class HollowTypeReadStateTest {

    private final HollowObjectSchema schema = new HollowObjectSchema("Test", 0);

    @Test
    public void onHeapShardsAreImmutableWhenArraysAreNotRecycled() {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new WastefulRecycler(11, 8));

        HollowTypeReadState typeState = new HollowObjectTypeReadState(
                stateEngine, MemoryMode.ON_HEAP, schema, schema);

        assertTrue(typeState.shardsAreImmutable);
    }

    @Test
    public void shardsAreNotImmutableWhenArraysAreRecycled() {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new RecyclingRecycler());

        HollowTypeReadState typeState = new HollowObjectTypeReadState(
                stateEngine, MemoryMode.ON_HEAP, schema, schema);

        assertFalse(typeState.shardsAreImmutable);
    }

    @Test
    public void sharedMemoryShardsAreNotImmutable() {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new WastefulRecycler(11, 8));

        HollowTypeReadState typeState = new HollowObjectTypeReadState(
                stateEngine, MemoryMode.SHARED_MEMORY_LAZY, schema, schema);

        assertFalse(typeState.shardsAreImmutable);
    }
}
