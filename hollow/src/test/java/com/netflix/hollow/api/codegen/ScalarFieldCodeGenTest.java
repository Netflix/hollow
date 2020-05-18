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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class ScalarFieldCodeGenTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void test() throws Exception {
        String apiClassName = "ScalarFieldCodeGenAPI";
        String packageName = "codegen.scalar.types";
        runGenerator(apiClassName, packageName, Movie.class, builder ->
                builder.withErgonomicShortcuts().withPackageGrouping()
                        .withHollowPrimitiveTypes(true).withRestrictApiToFieldType());
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = { "id" })
    private static class Movie {
        int id;

        // Collections
        List<Actor> actors;
        Map<String, Boolean> map;
        Set<Long> rankings;

        Integer intObj;
        int intPrimitive;
        
        Long longObj;
        long longPrimitive;
        
        Boolean boolObj;
        boolean boolPrimitive;

        Float floatObj;
        float floatPrimitive;
        
        Double doubleObj;
        double doublePrimitive;
        
        
        byte bytePrimitive; // -> INT
        byte[] byteArray;   // -> BYTES
        char[] charArray;   // -> STRING
        String stringType;  // -> REFERENCE
    }

    @SuppressWarnings("unused")
    private static class Actor {
        String name;

        Role role;
    }

    @SuppressWarnings("unused")
    private static class Role {
        Integer roleId;

        String name;
    }
}
