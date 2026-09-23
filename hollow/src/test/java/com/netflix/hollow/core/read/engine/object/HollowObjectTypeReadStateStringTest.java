package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Test;

public class HollowObjectTypeReadStateStringTest {

    @Test
    public void readsStringsFromImmutableSegments() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(String.class);

        String[] values = {
                "short ascii",
                "an ascii string deliberately long enough to cross small segment boundaries",
                "unicode \u123e interspersed \uffff with ascii"
        };
        int[] ordinals = new int[values.length];
        for(int i = 0; i < values.length; i++)
            ordinals[i] = mapper.add(values[i]);

        HollowReadStateEngine readStateEngine =
                new HollowReadStateEngine(new WastefulRecycler(5, 2));
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
