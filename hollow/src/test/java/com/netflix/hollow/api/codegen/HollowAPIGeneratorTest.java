package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;




public class HollowAPIGeneratorTest {

    private static String tmpFolder = System.getProperty("java.io.tmpdir");
    private static String clazzFolder = String.format("%s/classes", tmpFolder);

    @AfterClass
    public static void cleanup() throws IOException {
        Path directory = Paths.get(clazzFolder);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    public void generatesFileUsingDestinationPath()  {
        HollowWriteStateEngine stateEngine  = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(stateEngine);
        objectMapper.initializeTypeState(MyClass.class);
        File clazzFiles = new File(clazzFolder);

        HollowAPIGenerator hollowAPIGenerator = new HollowAPIGenerator.Builder()
                .withAPIClassname("API")
                .withPackageName("com.netflix.hollow.example.api.generated")
                .withDataModel(stateEngine)
                .withDestination(clazzFiles.toPath())
                .build();

        try {
            hollowAPIGenerator.generateSourceFiles();
        } catch (IOException e) {
            throw new RuntimeException("Could not write files to: " + clazzFolder);
        }

        Assert.assertTrue(clazzFiles.list().length > 0);
    }

    @SuppressWarnings("unused")
    private static class MyClass {
        int id;

        public MyClass(int id) {
            this.id = id;
        }
    }
}
