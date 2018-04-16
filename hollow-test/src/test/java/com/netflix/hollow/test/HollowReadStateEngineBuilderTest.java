package com.netflix.hollow.test;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class HollowReadStateEngineBuilderTest {
    @Test
    public void testBuild_withoutTypeInitialization() {
        HollowReadStateEngine readEngine = new HollowReadStateEngineBuilder()
            .add("penguins3peat").add(3L).build();
        assertEquals("Should have both types", new HashSet<String>(Arrays.asList(
                        String.class.getSimpleName(), Long.class.getSimpleName())),
                new HashSet<String>(readEngine.getAllTypes()));
    }

    @Test
    public void testBuild_withTypeInitialization() {
        HollowReadStateEngine readEngine =
            new HollowReadStateEngineBuilder(Arrays.<Class<?>>asList(String.class, Long.class))
            .add("foo").add(3L).build();
        assertEquals("Should have both types", new HashSet<String>(Arrays.asList(
                        String.class.getSimpleName(), Long.class.getSimpleName())),
                new HashSet<String>(readEngine.getAllTypes()));
        assertEquals("Should have one String", 1, readEngine.getTypeDataAccess(
                    String.class.getSimpleName()).getTypeState().getPopulatedOrdinals().cardinality());
        assertEquals("The one String should be foo", "foo", new GenericHollowObject(readEngine,
                    String.class.getSimpleName(), 0).getString("value"));
        assertEquals("Should have one Long", 1, readEngine.getTypeDataAccess(
                    Long.class.getSimpleName()).getTypeState().getPopulatedOrdinals().cardinality());
        assertEquals("The one Long should be 3L", 3L, new GenericHollowObject(readEngine,
                    Long.class.getSimpleName(), 0).getLong("value"));
    }

    public void testBuild_canConstructMultiple() {
        HollowReadStateEngineBuilder builder = new HollowReadStateEngineBuilder();
        builder.build();
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuild_cannotAddAfterBuild() {
        HollowReadStateEngineBuilder builder = new HollowReadStateEngineBuilder();
        builder.build();
        builder.add("foo");
    }
}
