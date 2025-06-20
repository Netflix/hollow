package com.netflix.hollow.core.schema;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

public class HollowTopLevelTypesTest {

    @Test
    public void testHollowTopLevelTypes() throws IOException {
        HollowProducer p = HollowProducer.withPublisher(new InMemoryBlobStore())
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        TypeWithPrimitive primitive = new TypeWithPrimitive();
        TypeWithReference reference = new TypeWithReference();
        TypeWithSetOfReference setOfReference = new TypeWithSetOfReference();

        p.runCycle(cycle -> {
            cycle.add(reference);
        });
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(p.getWriteEngine(), readStateEngine);
        Assert.assertEquals(
                new HashSet<String>() {{
                    add("TypeWithPrimitive");
                    add("TypeWithReference");
                }},
                readStateEngine.getAllTypes());
        Assert.assertEquals(
                new HashSet<String>() {{
                    add("TypeWithReference");
                }},
                HollowSchemaUtil.getTopLevelTypes(readStateEngine));

        // Add a new type with a set of TypeWithReference, now only
        // TypeWithSetOfReference should be a top-level type.
        p.runCycle(cycle -> {
            cycle.add(setOfReference);
        });
        StateEngineRoundTripper.roundTripSnapshot(p.getWriteEngine(), readStateEngine);
        Assert.assertEquals(
                new HashSet<String>() {{
                    add("TypeWithPrimitive");
                    add("TypeWithReference");
                    add("TypeWithSetOfReference");
                    add("SetOfTypeWithReference");
                }},
                readStateEngine.getAllTypes());
        Assert.assertEquals(
                new HashSet<String>() {{
                    add("TypeWithSetOfReference");
                }},
                HollowSchemaUtil.getTopLevelTypes(readStateEngine));
    }

    private static class TypeWithSetOfReference {
        private final HashSet<TypeWithReference> set;

        public TypeWithSetOfReference() {
            this.set = new HashSet<>();
        }
    }

    private static class TypeWithPrimitive {
        private final int value;

        public TypeWithPrimitive() {
            this.value = 0;
        }
    }

    private static class TypeWithReference {
        private final TypeWithPrimitive primitive ;

        public TypeWithReference() {
            this.primitive = new TypeWithPrimitive();
        }
    }
}
