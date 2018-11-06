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
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
    }

    @SuppressWarnings("unused")
    private static class MyClass {
        int id;

        public MyClass(int id) {
            this.id = id;
        }
    }
}
