/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import org.junit.Test;

public class HollowPrimaryKeyAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void test() throws Exception {
        String apiClassName = "PrimaryKeyIndexTestAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class,
                builder -> builder.reservePrimaryKeyIndexForTypeWithPrimaryKey(true));
    }

    @Test
    public void testWithPostfix() throws Exception {
        String apiClassName = "PrimaryKeyIndexTestAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
    }

    @HollowPrimaryKey(fields = {"id", "hasSubtitles", "actor", "role.id!", "role.rank"})
    static class Movie {
        int id;

        Boolean hasSubtitles;

        Actor actor;
        Role role;

        BoxedBoolean b1;
        BoxedBytes b2;
        BoxedBytes b3;
        BoxedChar b4;
        BoxedChars b5;
        BoxedString b6;
        BoxedInt b7;
        BoxedLong b8;
        BoxedFloat b9;
        BoxedDouble b10;

        Everything everything;
    }

    static class Actor {
        String name;
    }

    static class Role {
        Integer id;

        Long rank;

        String name;
    }

    @HollowPrimaryKey(fields = {"v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8", "v9", "v10", "ref!"})
    static class Everything {
        boolean v1;
        char v2;
        byte[] v3;
        char v4;
        char[] v5;
        String v6;
        int v7;
        long v8;
        float v9;
        double v10;

        BoxedInt ref;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedBoolean {
        char v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedBytes {
        byte[] v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedChar {
        char v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedChars {
        char[] v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedString {
        String v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedInt {
        int v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedLong {
        long v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedFloat {
        float v;
    }

    @HollowPrimaryKey(fields = "v")
    static class BoxedDouble {
        double v;
    }
}
