package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class HollowPrimaryKeyAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Override
    @Before
    public void setup() throws IOException {
    }

    @Override
    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        builder.reservePrimaryKeyIndexForTypeWithPrimaryKey(false);
        return super.initGenerator(builder);
    }

    @Test
    public void test() throws Exception {
        String apiClassName = "MovieAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    @HollowPrimaryKey(fields="id")
    static class Movie {
        int id;
        boolean playable;
        boolean isAction;
        Boolean hasSubtitles;
    }
}