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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowMapAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {
    private static class Weapon {
    }

    private static class Minion {
    }

    private static class Frank {
    }

    private static class Kevin {
    }

    @SuppressWarnings("unused")
    static class Gru {
        Map<String, Minion> minions;
        Map<String, List<Frank>> franks;
        Map<String, List<List<Kevin>>> kevins;
        Set<Weapon> weapons;
    }

    private static final String API_CLASS_NAME = "MapTestAPI";
    private static final String PACKAGE_NAME = "codegen.map";

    @Test
    public void test_withClassPostfix() throws Exception {
        runGenerator(API_CLASS_NAME, PACKAGE_NAME, Gru.class,
                builder -> builder.withPackageGrouping().withClassPostfix("Generated"));
    }

    @Test
    public void test_withPackageGrouping() throws Exception {
        runGenerator(API_CLASS_NAME, PACKAGE_NAME, Gru.class,
                HollowAPIGenerator.Builder::withPackageGrouping);
    }

    @Test
    public void test_withoutPackageGrouping() throws Exception {
        runGenerator(API_CLASS_NAME, PACKAGE_NAME, Gru.class, b -> b);
    }
}
