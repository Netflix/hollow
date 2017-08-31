package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class HollowAPIGeneratorTest {
    private String baseDir = "/tmp"; //System.getProperty("java.io.tmpdir");

    @Before
    public void setUp() throws IOException {}

    @Test
    public void testBooleanFieldErgonimics() throws Exception {
        // Setup
        String packageName = "booleanfieldergo";
        String srcDir = String.format("%s/%s/src/", baseDir, packageName);
        System.out.println("Generated Source under: " + srcDir);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(srcDir), null);

        // Init TypeState 
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(Movie.class);

        // Run Generator
        HollowAPIGenerator generator = new HollowAPIGenerator.Builder()
                .withDataModel(writeEngine)
                .withAPIClassname("MovieAPI")
                .withPackageName(packageName)
                //.withErgonomicShortcuts()
                .withBooleanFieldErgonomics(true)
                .build();
        generator.generateFiles(srcDir + packageName.replace('.', '/'));

        // Compile to validate generated files
        HollowCodeGenerationCompileUtil.compileSrcFiles(srcDir, baseDir + "/classes");
    }

    static class Movie {
        int id;
        boolean playable;
        boolean value;
        boolean isAction;
        Boolean hasSubtitles;
    }

}