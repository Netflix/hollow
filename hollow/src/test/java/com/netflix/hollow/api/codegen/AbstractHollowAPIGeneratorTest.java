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

import java.io.File;
import java.util.function.UnaryOperator;
import org.junit.After;

public class AbstractHollowAPIGeneratorTest {
    private String tmpFolder = System.getProperty("java.io.tmpdir");
    private String sourceFolder = String.format("%s/src", tmpFolder);
    private String clazzFolder = String.format("%s/classes", tmpFolder);

    void runGenerator(String apiClassName, String packageName, Class<?> clazz,
            UnaryOperator<HollowAPIGenerator.Builder> generatorCustomizer) throws Exception {
        System.out.println(String.format("Folders (%s) : \n\tsource=%s \n\tclasses=%s",
                    getClass().getSimpleName(), sourceFolder, clazzFolder));

        // Setup Folders
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);

        // Run Generator
        HollowAPIGenerator generator = generatorCustomizer.apply(new HollowAPIGenerator.Builder())
                .withDataModel(clazz).withAPIClassname(apiClassName).withPackageName(packageName)
                .withDestination(sourceFolder).build();
        generator.generateSourceFiles();

        // Compile to validate generated files
        HollowCodeGenerationCompileUtil.compileSrcFiles(sourceFolder, clazzFolder);
    }

    @After
    public void cleanup() {
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);
    }
}
