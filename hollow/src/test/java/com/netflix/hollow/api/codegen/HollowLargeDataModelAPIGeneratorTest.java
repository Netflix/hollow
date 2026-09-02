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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * Verifies that the generated API class scales past the JVM 64KB per-method limit, which the stock
 * (inlined) constructor reaches somewhere past ~460 types. See
 * {@link HollowAPIClassJavaGenerator#CONSTRUCTOR_CHUNKING_THRESHOLD}.
 */
public class HollowLargeDataModelAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    private static final String PACKAGE_NAME = "codegen.large";

    private static String schemaWithTypes(int typeCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < typeCount; i++) {
            sb.append("Type").append(i).append(" { long id; int value; }\n");
        }
        return sb.toString();
    }

    private String generatedApiSource(String apiClassName) throws Exception {
        File apiFile = Paths.get(sourceFolder, PACKAGE_NAME.split("\\.")).resolve(apiClassName + ".java").toFile();
        assertTrue("expected generated API at " + apiFile, apiFile.exists());
        return new String(Files.readAllBytes(apiFile.toPath()));
    }

    @Test
    public void largeDataModelCompilesViaChunkedConstructor() throws Exception {
        // 600 types is comfortably past the point where the old inlined constructor failed with
        // "code too large". This call compiles the generated sources, so it fails if the limit is hit.
        int typeCount = 600;
        runGeneratorFromSchemaString("LargeOfflineAPI", PACKAGE_NAME, schemaWithTypes(typeCount), b -> b);

        String src = generatedApiSource("LargeOfflineAPI");
        assertTrue("large model should use chunked init helpers", src.contains("private void init_0("));
        assertTrue("large model should chunk detachCaches too", src.contains("private void detachCaches_0("));
        // Fields can no longer be final once init moves into helper methods.
        assertFalse("chunked fields must not be final", src.contains("private final HollowObjectProvider"));
    }

    @Test
    public void smallDataModelKeepsInlinedConstructor() throws Exception {
        // At/below the threshold the output must be unchanged: inlined constructor, final fields, no helpers.
        runGeneratorFromSchemaString("SmallOfflineAPI", PACKAGE_NAME, schemaWithTypes(10), b -> b);

        String src = generatedApiSource("SmallOfflineAPI");
        assertFalse("small model must not be chunked", src.contains("private void init_0("));
        assertTrue("small model keeps final provider fields", src.contains("private final HollowObjectProvider"));
    }
}
