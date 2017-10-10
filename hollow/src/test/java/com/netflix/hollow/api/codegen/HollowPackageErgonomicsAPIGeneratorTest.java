/*
 *  Copyright 2017 Netflix, Inc.
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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class HollowPackageErgonomicsAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Override
    @Before
    public void setup() throws IOException {
    }

    @Override
    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        builder.withErgonomicShortcuts();
        builder.withPackageGrouping();
        return super.initGenerator(builder);
    }

    @Test
    public void test() throws Exception {
        String apiClassName = "PackageErgoTestAPI";
        String packageName = "codegen.subpackage.grouping";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    @Test
    public void testDefaultPackage() throws Exception {
        String apiClassName = "PackageErgoTestAPI";
        String packageName = "";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    @HollowPrimaryKey(fields = { "id" })
    static class Movie {
        int id;

        // Collections
        List<Actor> actors;
        Map<String, Boolean> map;
        Set<Long> rankings;

        // Native Types
        Integer i;
        Long l;
        Boolean b;
        Float f;
        Double d;
        String s;
    }

    static class Actor {
        String name;

        Role role;
    }

    static class Role {
        Integer id;

        String name;
    }
}