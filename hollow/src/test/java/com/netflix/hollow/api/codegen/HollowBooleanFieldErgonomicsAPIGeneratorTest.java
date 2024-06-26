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

import org.junit.Test;

public class HollowBooleanFieldErgonomicsAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void test() throws Exception {
        String apiClassName = "BooleanErgoTestAPI";
        String packageName = "codegen.booleanfield.ergo";
        runGenerator(apiClassName, packageName, Movie.class,
                builder -> builder.withBooleanFieldErgonomics(true).withGeneratedAnnotation());
        assertClassHasGeneratedAnnotation(packageName + "." + apiClassName);
    }

    static class Movie {
        int id;
        boolean playable;
        boolean value;
        boolean isAction;
        Boolean hasSubtitles;
    }
}
