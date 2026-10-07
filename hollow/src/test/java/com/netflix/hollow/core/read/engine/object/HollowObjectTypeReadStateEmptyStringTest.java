package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class HollowObjectTypeReadStateEmptyStringTest {

    @Parameterized.Parameters(name = "recycling={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[] {true}, new Object[] {false});
    }

    private final boolean recycling;
    private HollowObjectTypeReadState readState;
    private int[] ordinals;

    public HollowObjectTypeReadStateEmptyStringTest(boolean recycling) {
        this.recycling = recycling;
    }

    @Before
    public void setUp() throws Exception {
        populate("", null);
        assertNull(readState.currentDataElements()[0].varLengthData[0]);
    }

    @Test
    public void readsEmptyStringWithoutVariableLengthStorage() {
        assertEquals("", readState.readString(ordinals[0], 0));
    }

    @Test
    public void matchesEmptyStringWithoutVariableLengthStorage() {
        assertTrue(readState.isStringFieldEqual(ordinals[0], 0, ""));
    }

    @Test
    public void rejectsNonEmptyStringWithoutVariableLengthStorage() {
        assertFalse(readState.isStringFieldEqual(ordinals[0], 0, "other"));
    }

    @Test
    public void distinguishesEmptyAndNullStringsWithoutVariableLengthStorage() {
        assertFalse(readState.isStringFieldEqual(ordinals[0], 0, null));
        assertNull(readState.readString(ordinals[1], 0));
        assertTrue(readState.isStringFieldEqual(ordinals[1], 0, null));
        assertFalse(readState.isStringFieldEqual(ordinals[1], 0, ""));
    }

    @Test
    public void readsAndMatchesEmptyStringAfterNonEmptyString() throws Exception {
        populate("prefix", "", null);
        assertEquals("prefix", readState.readString(ordinals[0], 0));
        assertEquals("", readState.readString(ordinals[1], 0));
        assertTrue(readState.isStringFieldEqual(ordinals[1], 0, ""));
        assertFalse(readState.isStringFieldEqual(ordinals[1], 0, "other"));
        assertNull(readState.readString(ordinals[2], 0));
    }

    private void populate(String... values) throws Exception {
        HollowObjectSchema schema = new HollowObjectSchema("Test", 1);
        schema.addField("value", HollowObjectSchema.FieldType.STRING);
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        ordinals = new int[values.length];
        HollowObjectWriteRecord record = new HollowObjectWriteRecord(schema);
        for(int i = 0; i < values.length; i++) {
            record.reset();
            record.setString("value", values[i]);
            ordinals[i] = writeStateEngine.add("Test", record);
        }

        ArraySegmentRecycler recycler = recycling
                ? new RecyclingRecycler()
                : WastefulRecycler.SMALL_ARRAY_RECYCLER;
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(recycler);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        readState = (HollowObjectTypeReadState) readStateEngine.getTypeState("Test");
    }
}
