package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;

public class AbstractHollowAPIGeneratorTest {
    protected boolean isCleanupAfterEnabled = false;
    protected String tmpFolder = System.getProperty("java.io.tmpdir");
    protected String sourceFolder = String.format("%s/src", tmpFolder);
    protected String clazzFolder = String.format("%s/classes", tmpFolder);

    @Before
    public void setup() throws IOException {}

    protected void runGenerator(String apiClassName, String packageName, Class<?> clazz) throws Exception {
        // Setup Folders
        System.out.println(String.format("Folders: \n\tsource=%s \n\tclasses=%s", sourceFolder, clazzFolder));
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
        HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);

        // Init ObjectMapper 
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(clazz);

        // Run Generator
        HollowAPIGenerator generator = initGenerator(new HollowAPIGenerator.Builder().withDataModel(writeEngine).withAPIClassname(apiClassName).withPackageName(packageName));
        generator.generateFiles(sourceFolder + "/" + packageName.replace('.', '/'));

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