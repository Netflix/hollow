package com.netflix.hollow.api.codegen.perfapi;

import com.netflix.hollow.api.codegen.AbstractHollowAPIGeneratorTest;
import java.nio.file.Paths;
import org.junit.Test;

public class HollowPerformanceAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

  @Test
  public void testGeneratePerformanceApi() throws Exception {
    runPerformanceGenerator("API", "com.netflix.hollow.example.api.performance.generated", MyClass.class);
  }

  @Test
  public void testGeneratedFilesArePlacedInPackageDirectory() throws Exception {
    runPerformanceGenerator("API", "codegen.api", MyClass.class);
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/api/MyClassPerfAPI.java"));
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/api/StringPerfAPI.java"));
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/api/API.java"));
  }

  @Test
  public void testGeneratedFilesDontIncludeUndefinedTypes() throws Exception {
    String schemaResourcePath = "/hollow_code_gen_test.schema";

    // Generate code from schema file
    runPerformanceGeneratorFromSchemaFile("TestSchemaAPI", "codegen.perfapi", schemaResourcePath);

    // Verify valid types exist (types that have complete definitions)
    // Performance API generates PerfAPI classes only for OBJECT types
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/perfapi/TopLevelObjectPerfAPI.java"));
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/perfapi/Object1PerfAPI.java"));
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/perfapi/StringPerfAPI.java"));
    assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/perfapi/TestSchemaAPI.java"));

    // Verify invalid types do NOT exist (Object2 is undefined, so these should not be generated)
    // Note: Collection types (List, Set, Map) don't generate individual PerfAPI files anyway,
    // but we verify that Object2 itself is not generated
    assertFileDoesNotExist("codegen/perfapi/Object2PerfAPI.java");
    assertFileDoesNotExist("codegen/perfapi/ListOfObject2PerfAPI.java");
    assertFileDoesNotExist("codegen/perfapi/SetOfObject2PerfAPI.java");
    assertFileDoesNotExist("codegen/perfapi/MapOfStringToObject2PerfAPI.java");
    assertFileDoesNotExist("codegen/perfapi/MapOfObject2ToStringPerfAPI.java");
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
