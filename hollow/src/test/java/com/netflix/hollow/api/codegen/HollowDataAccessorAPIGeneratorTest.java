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

public class HollowDataAccessorAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {
    private static final String API_CLASS_NAME = "DataAccessorTestAPI";
    private static final String PACKAGE_NAME = "codegen.data.accessor";

    @Test
    public void test() throws Exception {
        runGenerator(API_CLASS_NAME, PACKAGE_NAME, Movie.class,
                builder -> builder.withAggressiveSubstitutions(true));
    }

    @Test
    public void testGenerateWithPostfix() throws Exception {
        runGenerator(API_CLASS_NAME, PACKAGE_NAME, Movie.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
    }

    @HollowPrimaryKey(fields = "id")
    static class Movie {
        int id;

        String name;
    }
}
