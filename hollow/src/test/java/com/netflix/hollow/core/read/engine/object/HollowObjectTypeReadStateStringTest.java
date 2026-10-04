package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class HollowObjectTypeReadStateStringTest {

    @Parameterized.Parameters(name = "recycling={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[] {true}, new Object[] {false});
    }

    private final boolean recycling;

    public HollowObjectTypeReadStateStringTest(boolean recycling) {
        this.recycling = recycling;
    }

    @Test
    public void readsAndComparesStrings() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(String.class);

        char[] longValue = new char[120];
        Arrays.fill(longValue, 'a');
        String longAscii = new String(longValue);
        String[] values = {
                "short ascii",
                "an ascii string deliberately long enough to cross small segment boundaries",
                "unicode \u123e interspersed \uffff with ascii",
                longAscii,
                longAscii.replace('a', '\u123e'),
                "\u0080 ascii after larger strings"
        };
        int[] ordinals = new int[values.length];
        for(int i = 0; i < values.length; i++)
            ordinals[i] = mapper.add(values[i]);

        ArraySegmentRecycler recycler = recycling
                ? new RecyclingRecycler(5, 2)
                : new WastefulRecycler(5, 2);
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(recycler);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        HollowObjectTypeDataAccess dataAccess =
                (HollowObjectTypeDataAccess)readStateEngine.getTypeDataAccess("String", 0);
        for(int i = 0; i < values.length; i++) {
            assertEquals(values[i], dataAccess.readString(ordinals[i], 0));
            assertTrue(dataAccess.isStringFieldEqual(ordinals[i], 0, values[i]));
            assertFalse(dataAccess.isStringFieldEqual(ordinals[i], 0, values[i] + "x"));
        }
    }
}
