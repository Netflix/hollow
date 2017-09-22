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
        builder.reservePrimaryKeyIndexForTypeWithPrimaryKey(true);
        return super.initGenerator(builder);
    }

    @Test
    public void test() throws Exception {
        String apiClassName = "MovieAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    @HollowPrimaryKey(fields = { "id", "hasSubtitles", "actor", "role.id!", "role.rank" })
    static class Movie {
        int id;

        Boolean hasSubtitles;

        Actor actor;
        Role role;
    }

    static class Actor {
        String name;
    }

    static class Role {
        Integer id;

        Long rank;

        String name;
    }
}