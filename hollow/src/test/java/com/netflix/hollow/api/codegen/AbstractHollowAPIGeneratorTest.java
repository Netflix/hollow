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
import java.io.IOException;
import org.junit.After;
import org.junit.Before;

public class AbstractHollowAPIGeneratorTest {
    protected boolean isForParity = false;
    protected boolean isCleanupBeforeEnabled = isForParity ? false : true;
    protected boolean isCleanupAfterEnabled = isForParity ? false : true;
    protected String tmpFolder = System.getProperty("java.io.tmpdir");
    protected String sourceFolder = String.format("%s/src", tmpFolder);
    protected String clazzFolder = String.format("%s/classes", tmpFolder);

    @Before
    public void setup() throws IOException {}

    protected void runGenerator(String apiClassName, String packageName, Class<?> clazz) throws Exception {
        System.out.println(String.format("Folders (%s) : \n\tsource=%s \n\tclasses=%s", this.getClass().getSimpleName(), sourceFolder, clazzFolder));

        // Setup Folders
        if (isCleanupBeforeEnabled) {
            HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
            HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);
        }

        // Run Generator
        HollowAPIGenerator generator = initGenerator(new HollowAPIGenerator.Builder().withDataModel(clazz).withAPIClassname(apiClassName).withPackageName(packageName));
        generator.generateFiles(sourceFolder);

        // Compile to validate generated files
        HollowCodeGenerationCompileUtil.compileSrcFiles(sourceFolder, clazzFolder);
    }

    @After
    public void cleanup() {
        if (isCleanupAfterEnabled) {
            HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
            HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);
        } else {
            System.out.println("Cleanup skipped:");
            System.out.println("\t sourceFolder=" + sourceFolder);
            System.out.println("\t clazzFolder=" + clazzFolder);
        }
    }

    protected HollowAPIGenerator initGenerator(HollowAPIGenerator.Builder builder) {
        return builder.build();
    }
}