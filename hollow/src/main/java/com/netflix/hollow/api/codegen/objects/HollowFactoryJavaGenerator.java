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

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowConsumerJavaFileGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.util.Arrays;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.*;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowFactoryJavaGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "core";

    private final String objectClassName;
    private final HollowSchema schema;

    public HollowFactoryJavaGenerator(String packageName, HollowSchema schema, HollowDataset dataset,
            CodeGeneratorConfig config) {
        super(packageName, SUB_PACKAGE_NAME, dataset, config);
        this.objectClassName = hollowImplClassname(schema.getName());
        this.className = hollowFactoryClassname(schema.getName());
        this.schema = schema;
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, null, Arrays.asList(schema));

        builder.append("import " + HollowFactory.class.getName() + ";\n");
        builder.append("import " + HollowTypeDataAccess.class.getName() + ";\n");
        builder.append("import " + HollowTypeAPI.class.getName() + ";\n");

        if(schema instanceof HollowListSchema)
            builder.append("import " + HollowListCachedDelegate.class.getName() + ";\n");
        if(schema instanceof HollowSetSchema)
            builder.append("import " + HollowSetCachedDelegate.class.getName() + ";\n");
        if(schema instanceof HollowMapSchema)
            builder.append("import " + HollowMapCachedDelegate.class.getName() + ";\n");

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class " + className + "<T extends " + objectClassName + "> extends HollowFactory<T> {\n\n");

        builder.append("    @Override\n");
        builder.append("    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {\n");
        builder.append("        return (T)new " + objectClassName + "((" + delegateInterfaceName(schema) + ") typeAPI, ordinal);\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {\n");
        builder.append("        return (T)new " + objectClassName + "(new " + delegateCachedClassname(schema) + "((" + typeAPIClassname(schema.getName()) + ")typeAPI, ordinal), ordinal);\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }
}
