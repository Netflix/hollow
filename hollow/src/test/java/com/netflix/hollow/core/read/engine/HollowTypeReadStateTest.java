package com.netflix.hollow.core.read.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    @Test
    public void rejectsRecyclingRecyclerBeforePublishingSnapshot() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new WastefulRecycler(11, 8));
        HollowObjectSchema objectSchema = objectSchema();
        HollowObjectTypeReadState objectState = new HollowObjectTypeReadState(
                stateEngine, MemoryMode.ON_HEAP, objectSchema, objectSchema);

        assertSnapshotRejected(objectState, snapshot(objectSchema));
        for (HollowTypeReadState typeState : collectionStates(stateEngine)) {
            assertSnapshotRejected(typeState, new byte[0]);
        }
    }

    @Test
    public void rejectsRecyclingRecyclerBeforeApplyingDelta() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new WastefulRecycler(11, 8));
        HollowObjectSchema objectSchema = objectSchema();
        HollowObjectTypeReadState objectState = new HollowObjectTypeReadState(
                stateEngine, MemoryMode.ON_HEAP, objectSchema, objectSchema);

        try (HollowBlobInput in = HollowBlobInput.serial(snapshot(objectSchema))) {
            objectState.readSnapshot(in, stateEngine.getMemoryRecycler(), 1);
        }
        assertEquals(42, objectState.readInt(0, 0));
        assertDeltaRejected(objectState);
        assertEquals(42, objectState.readInt(0, 0));

        for (HollowTypeReadState typeState : collectionStates(stateEngine)) {
            assertDeltaRejected(typeState);
        }
    }

    private static HollowObjectSchema objectSchema() {
        HollowObjectSchema schema = new HollowObjectSchema("Test", 1);
        schema.addField("value", HollowObjectSchema.FieldType.INT);
        return schema;
    }

    private static byte[] snapshot(HollowObjectSchema schema) throws IOException {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1);
        HollowObjectWriteRecord record = new HollowObjectWriteRecord(schema);
        record.setInt("value", 42);
        writeState.add(record);
        writeState.prepareForWrite(false);
        writeState.calculateSnapshot();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        writeState.writeSnapshot(new DataOutputStream(bytes));
        return bytes.toByteArray();
    }

    private static HollowTypeReadState[] collectionStates(HollowReadStateEngine stateEngine) {
        return new HollowTypeReadState[] {
                new HollowListTypeReadState(stateEngine, MemoryMode.ON_HEAP, new HollowListSchema("List", "Test")),
                new HollowSetTypeReadState(stateEngine, MemoryMode.ON_HEAP, new HollowSetSchema("Set", "Test")),
                new HollowMapTypeReadState(stateEngine, MemoryMode.ON_HEAP, new HollowMapSchema("Map", "Test", "Test"))
        };
    }

    private static void assertSnapshotRejected(HollowTypeReadState typeState, byte[] snapshot) throws IOException {
        try (HollowBlobInput in = HollowBlobInput.serial(snapshot)) {
            try {
                typeState.readSnapshot(in, new RecyclingRecycler(), 1);
                fail("recycling snapshot accepted for immutable " + typeState.getSchema().getName());
            } catch (IllegalStateException expected) {
                // An incompatible recycler must be rejected before any shard is published.
            }
        }
        assertNull(typeState.getShardsVolatile());
    }

    private static void assertDeltaRejected(HollowTypeReadState typeState) throws IOException {
        ShardsHolder originalHolder = typeState.getShardsVolatile();
        try (HollowBlobInput in = HollowBlobInput.serial(new byte[0])) {
            try {
                typeState.applyDelta(in, typeState.getSchema(), new RecyclingRecycler(), 1);
                fail("recycling delta accepted for immutable " + typeState.getSchema().getName());
            } catch (IllegalStateException expected) {
                // An incompatible recycler must be rejected before input or shards are changed.
            }
        }
        assertSame(originalHolder, typeState.getShardsVolatile());
    }
}
