package com.netflix.hollow.api.codegen;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

public class HollowAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void assertMetaInfoAtCustomLocation() throws Exception {
        runGenerator("API", "codegen.api", MyClass.class, b -> b.withMetaInfo("meta-info"));
        assertNonEmptyFileExists(Paths.get("meta-info"));
    }

    @Test
    public void testSchemaDocAtCustomLocation() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withMetaInfo(tmpFolder + "/" + "resources/META-INF/hollow"));
        assertNonEmptyFileExists(Paths.get(tmpFolder, "resources/META-INF/hollow/codegen.api.MyClassTestAPI.schema"));
    }

    @Test
    public void testSchemaDocContents() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class, b -> b.withMetaInfo(Paths.get(sourceFolder)));
        assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen.api.MyClassTestAPI.schema"));

        List<HollowSchema> expected = SimpleHollowDataset.fromClassDefinitions(MyClass.class).getSchemas();
        try (InputStream input = new FileInputStream(sourceFolder + "/" + "codegen.api.MyClassTestAPI.schema")) {
            List<HollowSchema> actual = HollowSchemaParser.parseCollectionOfSchemas(new BufferedReader(new InputStreamReader(input)));
            assertEquals(expected.size(), actual.size());
            assertEquals(new HashSet(expected), new HashSet(actual));
        }
    }

    @Test
    public void generatesFileUsingDestinationPath() throws Exception {
        runGenerator("API", "com.netflix.hollow.example.api.generated", MyClass.class, b -> b);
    }

    @Test
    public void testGenerateWithPostfix() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated"));
        assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/api/StringGenerated.java"));
        assertClassHasHollowTypeName("codegen.api.MyClassGenerated", "MyClass");
    }

    @Test
    public void testGenerateWithPostfixAndPackageGrouping() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
        assertNonEmptyFileExists(Paths.get(sourceFolder, "codegen/api/core/StringGenerated.java"));
    }

    @Test
    public void testGenerateWithPostfixAndPrimitiveTypes() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping()
                .withHollowPrimitiveTypes(true));
        assertFileDoesNotExist("codegen/api/core/StringGenerated.java");
        assertFileDoesNotExist("codegen/api/StringGenerated.java");
    }

    @Test
    public void testGenerateWithPostfixAndAggressiveSubstitutions() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping()
                .withHollowPrimitiveTypes(true).withAggressiveSubstitutions(true));
        assertFileDoesNotExist("codegen/api/core/StringGenerated.java");
        assertFileDoesNotExist("codegen/api/StringGenerated.java");
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = "id")
    private static class MyClass {
        int id;
        String foo;

        public MyClass(int id, String foo) {
            this.id = id;
            this.foo = foo;
        }
    }
}
