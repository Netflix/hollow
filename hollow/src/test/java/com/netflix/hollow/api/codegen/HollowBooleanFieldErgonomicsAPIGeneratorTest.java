package com.netflix.hollow.api.codegen;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class HollowBooleanFieldErgonomicsAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {
    @Override
    @Before
    public void setup() throws IOException {
    }

    @Override
    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        builder.withBooleanFieldErgonomics(true);
        return super.initGenerator(builder);
    }

    @Test
    public void test() throws Exception {
        String apiClassName = "MovieAPI";
        String packageName = "codegen.booleanfieldergo";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    static class Movie {
        int id;
        boolean playable;
        boolean value;
        boolean isAction;
        Boolean hasSubtitles;
    }
}