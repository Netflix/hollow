package com.netflix.hollow.api.codegen.testdata;

import com.netflix.hollow.api.codegen.AbstractHollowAPIGeneratorTest;
import org.junit.Test;

import java.nio.file.Paths;

public class HollowTestDataAPIGeneratorTest extends AbstractHollowAPIGeneratorTest  {
  @Test
  public void testGenerateTestDataApi() throws Exception {
      String schemaResourcePath = "/hollow_code_gen_test.schema";
      runTestDataAPIGeneratorFromSchemaFile("TestSchemaAPI", "codegen.perfapi", schemaResourcePath);

      // Verify valid types exist (types that have complete definitions)
      assertNonEmptyFileExists(Paths.get(sourceFolder, "TopLevelObjectTestData.java"));
      assertNonEmptyFileExists(Paths.get(sourceFolder, "Object1TestData.java"));
      assertNonEmptyFileExists(Paths.get(sourceFolder, "ListOfObject1TestData.java"));
      assertNonEmptyFileExists(Paths.get(sourceFolder, "SetOfObject1TestData.java"));
      assertNonEmptyFileExists(Paths.get(sourceFolder, "MapOfStringToObject1TestData.java"));

      // Verify invalid types do NOT exist (Object2 is undefined, so these should not be generated)
      assertFileDoesNotExist("Object2TestData.java");
      assertFileDoesNotExist("ListOfObject2TestData.java");
      assertFileDoesNotExist("SetOfObject2TestData.java");
      assertFileDoesNotExist("MapOfStringToObject2TestData.java");
      assertFileDoesNotExist("MapOfObject2ToStringTestData.java");
  }
}
