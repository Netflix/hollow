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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.HollowGenerated;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import org.junit.After;

public class AbstractHollowAPIGeneratorTest {
    protected String tmpFolder = System.getProperty("java.io.tmpdir");
    protected String sourceFolder = String.format("%s/src", tmpFolder);
    protected String clazzFolder = String.format("%s/classes", tmpFolder);
    private Path metaInfoPath = null;

    protected void runGenerator(String apiClassName, String packageName, Class<?> clazz,
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

        if(generator.config.isUseMetaInfo()) {
            metaInfoPath = generator.config.getMetaInfoPath();
        }
        // Compile to validate generated files
        HollowCodeGenerationCompileUtil.compileSrcFiles(sourceFolder, clazzFolder);
    }

    protected void assertNonEmptyFileExists(Path absolutePath) {
        assertTrue("File at " + absolutePath + " should exist", absolutePath.toFile().exists() && absolutePath.toFile().length() > 0L);
    }

    void assertClassHasHollowTypeName(String clazz, String typeName) throws IOException, ClassNotFoundException {
        ClassLoader cl = new URLClassLoader(new URL[]{new File(clazzFolder).toURI().toURL()});
        Class cls = cl.loadClass(clazz);
        Annotation annotation = cls.getAnnotation(HollowTypeName.class);
        assertNotNull(annotation);
        assertEquals(typeName, ((HollowTypeName) annotation).name());
    }

    protected void assertClassHasGeneratedAnnotation(String clazz) throws IOException, ClassNotFoundException {
        ClassLoader cl = new URLClassLoader(new URL[]{new File(clazzFolder).toURI().toURL()});
        Class cls = cl.loadClass(clazz);
        Annotation annotation = cls.getAnnotation(HollowGenerated.class);
        assertNotNull(annotation);
    }

    void assertFileDoesNotExist(String relativePath) {
        if (relativePath.startsWith("/")) {
            throw new IllegalArgumentException("Relative paths should not start with /");
        }
        assertFalse("File should not exist at " + relativePath,
                new File(sourceFolder + "/" + relativePath).exists());
    }

    @After
    public void cleanup() {
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);
        if (metaInfoPath != null) {
            HollowCodeGenerationCompileUtil.cleanupFolder(metaInfoPath.toFile(), null);
        }
    }
}
