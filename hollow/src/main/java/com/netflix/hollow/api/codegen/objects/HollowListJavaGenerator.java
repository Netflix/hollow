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
 *
 */
package com.netflix.hollow.api.codegen.objects;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Arrays;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 */
public class HollowListJavaGenerator extends HollowCollectionsGenerator {

    private final HollowListSchema schema;
    private final String elementClassName;
    private final boolean parameterize;

    public HollowListJavaGenerator(String packageName, String apiClassname, HollowListSchema schema, Set<String>
            parameterizedTypes, boolean parameterizeClassNames, HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, apiClassname, schema, dataset, config);

        this.schema = schema;
        this.elementClassName = hollowImplClassname(schema.getElementType());
        this.parameterize = parameterizeClassNames || parameterizedTypes.contains(schema.getElementType());
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, apiClassname, Arrays.<HollowSchema>asList(schema));

        builder.append("import " + HollowList.class.getName() + ";\n");
        builder.append("import " + HollowListSchema.class.getName() + ";\n");
        builder.append("import " + HollowListDelegate.class.getName() + ";\n");
        builder.append("import " + GenericHollowRecordHelper.class.getName() + ";\n\n");

        builder.append("@SuppressWarnings(\"all\")\n");
        if(parameterize)
            builder.append("public class " + className + "<T> extends HollowList<T> {\n\n");
        else
            builder.append("public class " + className + " extends HollowList<" + elementClassName + "> {\n\n");

        appendConstructor(builder);
        appendInstantiateMethod(builder);
        appendEqualityMethod(builder);
        appendAPIAccessor(builder);
        appendTypeAPIAccessor(builder);

        builder.append("}");

        return builder.toString();
    }

    private void appendConstructor(StringBuilder classBuilder) {
        classBuilder.append("    public " + className + "(HollowListDelegate delegate, int ordinal) {\n");
        classBuilder.append("        super(delegate, ordinal);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendInstantiateMethod(StringBuilder classBuilder) {
        String returnType = parameterize ? "T" : elementClassName;

        classBuilder.append("    @Override\n");
        classBuilder.append("    public ").append(returnType).append(" instantiateElement(int ordinal) {\n");
        classBuilder.append("        return (").append(returnType).append(") api().get").append(elementClassName).append("(ordinal);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendEqualityMethod(StringBuilder classBuilder) {
        classBuilder.append("    @Override\n");
        classBuilder.append("    public boolean equalsElement(int elementOrdinal, Object testObject) {\n");
        classBuilder.append("        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendAPIAccessor(StringBuilder classBuilder) {
        classBuilder.append("    public " + apiClassname + " api() {\n");
        classBuilder.append("        return typeApi().getAPI();\n");
        classBuilder.append("    }\n\n");
    }

    private void appendTypeAPIAccessor(StringBuilder classBuilder) {
        String typeAPIClassname = typeAPIClassname(schema.getName());
        classBuilder.append("    public " + typeAPIClassname + " typeApi() {\n");
        classBuilder.append("        return (").append(typeAPIClassname).append(") delegate.getTypeAPI();\n");
        classBuilder.append("    }\n\n");
    }
}
