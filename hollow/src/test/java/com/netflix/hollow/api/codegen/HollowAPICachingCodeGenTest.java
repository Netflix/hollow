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
 *
 */
package com.netflix.hollow.api.codegen;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowAPICachingCodeGenTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void generatedApiSupportsRetainRemovedOrdinals() throws Exception {
        runGenerator("API", "codegen.api", MyClass.class, b -> b);

        // The generated API must expose the retain-aware constructor that
        // HollowAPIFactory.ForGeneratedAPI reflectively invokes when retainRemovedOrdinals is enabled.
        ClassLoader cl = new URLClassLoader(new URL[]{new File(clazzFolder).toURI().toURL()});
        Class<?> apiClass = cl.loadClass("codegen.api.API");
        assertNotNull(apiClass.getConstructor(
                HollowDataAccess.class, Set.class, Map.class, apiClass, boolean.class));

        // ...and the flag must be threaded into the object cache providers it builds.
        String generated = new String(
                Files.readAllBytes(Paths.get(sourceFolder, "codegen/api/API.java")), StandardCharsets.UTF_8);
        assertTrue("generated API should pass retainRemovedOrdinalsInCache to HollowObjectCacheProvider",
                generated.contains("new HollowObjectCacheProvider(")
                        && generated.contains("retainRemovedOrdinalsInCache"));
    }

    @SuppressWarnings("unused")
    private static class MyClass {
        int id;
        String foo;

        MyClass(int id, String foo) {
            this.id = id;
            this.foo = foo;
        }
    }
}
