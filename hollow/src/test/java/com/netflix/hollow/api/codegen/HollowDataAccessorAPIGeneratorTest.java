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
import org.junit.Before;
import org.junit.Test;

public class HollowDataAccessorAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Override
    @Before
    public void setup() throws IOException {
    }

    @Override
    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        builder.withAggressiveSubstitutions(true);
        return super.initGenerator(builder);
    }

    @Test
    public void test() throws Exception {
        String apiClassName = "DataAccessorTestAPI";
        String packageName = "codegen.data.accessor";
        runGenerator(apiClassName, packageName, Movie.class);
    }

    @HollowPrimaryKey(fields="id")
    static class Movie {
        int id;

        String name;
    }
}