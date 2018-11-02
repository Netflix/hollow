package com.netflix.hollow.jsonadapter;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import org.junit.Test;

public class HollowModuleErgonomicSerializationTest extends AbstractHollowModuleSerializationTest {
    @Override
    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        builder.withErgonomicShortcuts();
        builder.withPackageGrouping();
        return super.initGenerator(builder);
    }

    @Test
    public void emptyMovie() throws Exception {
        emptyMovieTest();
    }

    @Test
    public void fullMovie() throws Exception {
        fullMovieTest();
    }
}
