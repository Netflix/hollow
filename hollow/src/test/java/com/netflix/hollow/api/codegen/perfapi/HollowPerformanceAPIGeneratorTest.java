package com.netflix.hollow.api.codegen.perfapi;

import com.netflix.hollow.api.codegen.AbstractHollowAPIGeneratorTest;
import com.netflix.hollow.api.codegen.HollowCodeGenerationCompileUtil;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import java.io.File;
import org.junit.Test;

public class HollowPerformanceAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

  @Test
  public void testGeneratePerformanceApi() throws Exception {
    runGenerator("API", "com.netflix.hollow.example.api.performance.generated", MyClass.class);
  }

  @Test
  public void testGeneratedFilesArePlacedInPackageDirectory() throws Exception {
    runGenerator("API", "codegen.api", MyClass.class);
    assertNonEmptyFileExists("codegen/api/MyClassPerfAPI.java");
    assertNonEmptyFileExists("codegen/api/StringPerfAPI.java");
    assertNonEmptyFileExists("codegen/api/API.java");
  }

  private void runGenerator(String apiClassName, String packageName, Class<?> clazz) throws Exception {
    System.out.println(String.format("Folders (%s) : \n\tsource=%s \n\tclasses=%s",
        getClass().getSimpleName(), sourceFolder, clazzFolder));

    // Setup Folders
    HollowCodeGenerationCompileUtil.cleanupFolder(new File(sourceFolder), null);
    HollowCodeGenerationCompileUtil.cleanupFolder(new File(clazzFolder), null);

    // Run generator
    HollowPerformanceAPIGenerator generator = HollowPerformanceAPIGenerator.newBuilder()
        .withDestination(sourceFolder)
        .withPackageName(packageName)
        .withAPIClassname(apiClassName)
        .withDataset(SimpleHollowDataset.fromClassDefinitions(clazz))
        .build();
    generator.generateSourceFiles();

    // Compile to validate generated files
    HollowCodeGenerationCompileUtil.compileSrcFiles(sourceFolder, clazzFolder);
  }

  @SuppressWarnings("unused")
  private static class MyClass {
    int id;
    String foo;

    public MyClass(int id, String foo) {
      this.id = id;
      this.foo = foo;
    }
  }
}
