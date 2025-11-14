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
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.junit.After;

public class AbstractHollowAPIGeneratorTest {
    protected String tmpFolder = System.getProperty("java.io.tmpdir");
    protected String sourceFolder = String.format("%s/src", tmpFolder);
    protected String clazzFolder = String.format("%s/classes", tmpFolder);
    private Path metaInfoPath = null;

    protected void runGenerator(String apiClassName, String packageName, Class<?> clazz,
            UnaryOperator<HollowAPIGenerator.Builder> generatorCustomizer) throws Exception {
        setupFolders();

        // Run Generator
        HollowAPIGenerator generator = generatorCustomizer.apply(new HollowAPIGenerator.Builder())
                .withDataModel(clazz).withAPIClassname(apiClassName).withPackageName(packageName)
                .withDestination(sourceFolder).build();
        generator.generateSourceFiles();

        if(generator.config.isUseMetaInfo()) {
            metaInfoPath = generator.config.getMetaInfoPath();
        }
        
        compileGeneratedFiles();
    }

    protected void assertNonEmptyFileExists(Path absolutePath) {
        assertTrue("File at " + absolutePath + " should exist", absolutePath.toFile().exists() && absolutePath.toFile().length() > 0L);
    }

    protected void assertClassHasHollowTypeName(String clazz, String typeName) throws IOException, ClassNotFoundException {
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

    protected void assertFileDoesNotExist(String relativePath) {
        if (relativePath.startsWith("/")) {
            throw new IllegalArgumentException("Relative paths should not start with /");
        }
        assertFalse("File should not exist at " + relativePath,
                new File(sourceFolder + "/" + relativePath).exists());
    }

    protected void runPerformanceGenerator(String apiClassName, String packageName, Class<?> clazz) throws Exception {
        setupFolders();

        // Run generator
        com.netflix.hollow.api.codegen.perfapi.HollowPerformanceAPIGenerator generator =
                com.netflix.hollow.api.codegen.perfapi.HollowPerformanceAPIGenerator.newBuilder()
                .withDestination(sourceFolder)
                .withPackageName(packageName)
                .withAPIClassname(apiClassName)
                .withDataset(SimpleHollowDataset.fromClassDefinitions(clazz))
                .build();
        generator.generateSourceFiles();

        compileGeneratedFiles();
    }

    protected void runGeneratorFromSchemaString(String apiClassName, String packageName, String schemaContentAsStr,
                                                UnaryOperator<HollowAPIGenerator.Builder> generatorCustomizer) throws Exception {
        setupFolders();
        SimpleHollowDataset dataset = new SimpleHollowDataset(HollowSchemaParser.parseCollectionOfSchemas(schemaContentAsStr));
        // Run Generator
        HollowAPIGenerator generator = generatorCustomizer.apply(new HollowAPIGenerator.Builder())
                .withDataModel(dataset)
                .withAPIClassname(apiClassName)
                .withPackageName(packageName)
                .withDestination(sourceFolder)
                .build();
        generator.generateSourceFiles();

        if(generator.config.isUseMetaInfo()) {
            metaInfoPath = generator.config.getMetaInfoPath();
        }

        compileGeneratedFiles();
    }

    protected void runGeneratorFromSchemaFile(String apiClassName, String packageName, String schemaFilePathOrResource,
            UnaryOperator<HollowAPIGenerator.Builder> generatorCustomizer) throws Exception {
        setupFolders();

        SimpleHollowDataset dataset = readSchemaFile(schemaFilePathOrResource);

        // Run Generator
        HollowAPIGenerator generator = generatorCustomizer.apply(new HollowAPIGenerator.Builder())
                .withDataModel(dataset)
                .withAPIClassname(apiClassName)
                .withPackageName(packageName)
                .withDestination(sourceFolder)
                .build();
        generator.generateSourceFiles();

        if(generator.config.isUseMetaInfo()) {
            metaInfoPath = generator.config.getMetaInfoPath();
        }

        compileGeneratedFiles();
    }

    protected void runPerformanceGeneratorFromSchemaFile(String apiClassName, String packageName, String schemaFilePathOrResource) throws Exception {
        setupFolders();

        SimpleHollowDataset dataset = readSchemaFile(schemaFilePathOrResource);

        // Run Generator
        com.netflix.hollow.api.codegen.perfapi.HollowPerformanceAPIGenerator generator = 
                com.netflix.hollow.api.codegen.perfapi.HollowPerformanceAPIGenerator.newBuilder()
                .withDataset(dataset)
                .withAPIClassname(apiClassName)
                .withPackageName(packageName)
                .withDestination(sourceFolder)
                .build();
        generator.generateSourceFiles();

        compileGeneratedFiles();
    }

    private void setupFolders() {
        System.out.println(String.format("Folders (%s) : \n\tsource=%s \n\tclasses=%s",
                getClass().getSimpleName(), sourceFolder, clazzFolder));
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);
    }

    private SimpleHollowDataset readSchemaFile(String schemaFilePathOrResource) throws Exception {
        String schemaContent;
        InputStream inputStream = getClass().getResourceAsStream(schemaFilePathOrResource);
        if (inputStream == null) {
            // Try as file path
            File schemaFile = new File(schemaFilePathOrResource);
            if (!schemaFile.exists()) {
                throw new FileNotFoundException("Schema file not found as resource or file: " + schemaFilePathOrResource);
            }
            inputStream = new FileInputStream(schemaFile);
        }
        try (InputStream is = inputStream;
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            schemaContent = reader.lines().collect(Collectors.joining("\n"));
        }
        List<HollowSchema> schemaList = HollowSchemaParser.parseCollectionOfSchemas(schemaContent);
        return new SimpleHollowDataset(schemaList);
    }

    private void compileGeneratedFiles() throws Exception {
        HollowCodeGenerationCompileUtil.compileSrcFiles(sourceFolder, clazzFolder);
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
