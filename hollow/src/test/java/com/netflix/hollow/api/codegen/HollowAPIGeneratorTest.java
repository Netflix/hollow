package com.netflix.hollow.api.codegen;

import org.junit.Test;

public class HollowAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void generatesFileUsingDestinationPath() throws Exception {
        runGenerator("API", "com.netflix.hollow.example.api.generated", MyClass.class, b -> b);
    }

    @Test
    public void testGenerateWithPostfix() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated"));
        assertNonEmptyFileExists("codegen/api/StringGenerated.java");
        assertClassHasHollowTypeName("codegen.api.MyClassGenerated", "MyClass");
    }

    @Test
    public void testGenerateWithPostfixAndPackageGrouping() throws Exception {
        runGenerator("MyClassTestAPI", "codegen.api", MyClass.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
        assertNonEmptyFileExists("codegen/api/core/StringGenerated.java");
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
    private static class MyClass {
        int id;
        String foo;

        public MyClass(int id, String foo) {
            this.id = id;
            this.foo = foo;
        }
    }
}
