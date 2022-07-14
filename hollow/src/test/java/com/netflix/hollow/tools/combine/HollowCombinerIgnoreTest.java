package com.netflix.hollow.tools.combine;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.IOException;
import org.junit.Test;

public class HollowCombinerIgnoreTest {

    @Test
    public void testIgnoreWithSingleInput() throws IOException {
        HollowWriteStateEngine combineInto = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(combineInto);
        mapper.initializeTypeState(TypeA.class);
        mapper.initializeTypeState(TypeB.class);

        HollowWriteStateEngine combineFromWriteEngine = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(combineFromWriteEngine);
        mapper.add(new TypeA(1));
        mapper.add(new TypeB(1));
        mapper.add(new TypeC(1));
        HollowReadStateEngine combineFrom = StateEngineRoundTripper.roundTripSnapshot(combineFromWriteEngine);

        HollowCombiner combiner = new HollowCombiner(combineInto, combineFrom);
        combiner.addIgnoredTypes("TypeB", "TypeC");
        combiner.combine();

        HollowReadStateEngine combined = StateEngineRoundTripper.roundTripSnapshot(combineInto);
        assertEquals(1, combined.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
        assertEquals(0, combined.getTypeState("TypeB").getPopulatedOrdinals().cardinality());
        assertEquals(0, combined.getTypeState("TypeC").getPopulatedOrdinals().cardinality());
    }

    @Test
    public void testIgnoreWithMultipleInput() throws IOException {
        HollowWriteStateEngine combineInto = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(combineInto);
        mapper.initializeTypeState(TypeA.class);
        mapper.initializeTypeState(TypeB.class);

        HollowWriteStateEngine combineFromWriteEngine1 = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(combineFromWriteEngine1);
        mapper.add(new TypeA(1));
        mapper.add(new TypeB(1));
        mapper.add(new TypeC(1));
        HollowReadStateEngine combineFrom1 = StateEngineRoundTripper.roundTripSnapshot(combineFromWriteEngine1);

        HollowWriteStateEngine combineFromWriteEngine2 = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(combineFromWriteEngine2);
        mapper.add(new TypeA(2));
        mapper.add(new TypeB(2));
        mapper.add(new TypeC(2));
        HollowReadStateEngine combineFrom2 = StateEngineRoundTripper.roundTripSnapshot(combineFromWriteEngine2);

        HollowCombiner combiner = new HollowCombiner(combineInto, combineFrom1, combineFrom2);
        combiner.addIgnoredTypes("TypeB", "TypeC");
        combiner.combine();

        HollowReadStateEngine combined = StateEngineRoundTripper.roundTripSnapshot(combineInto);
        assertEquals(2, combined.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
        assertEquals(0, combined.getTypeState("TypeB").getPopulatedOrdinals().cardinality());
        assertEquals(0, combined.getTypeState("TypeC").getPopulatedOrdinals().cardinality());
    }

    @HollowPrimaryKey(fields = {"id", "b.id"})
    private static class TypeA {
        final int id;
        final TypeB b;
        final TypeC c;

        private TypeA(int id) {
            this.id = id;
            this.b = new TypeB(id);
            this.c = new TypeC(id);
        }
    }

    @HollowPrimaryKey(fields = "id")
    private static class TypeB {
        final int id;

        private TypeB(int id) {
            this.id = id;
        }
    }

    private static class TypeC {
        final int id;

        private TypeC(int id) {
            this.id = id;
        }
    }
}
