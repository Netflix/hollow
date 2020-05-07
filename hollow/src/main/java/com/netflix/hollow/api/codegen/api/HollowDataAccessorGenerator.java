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
package com.netflix.hollow.api.codegen.api;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowConsumerJavaFileGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Arrays;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation. Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowDataAccessorGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "accessor";

    protected final String apiclassName;
    protected final String type;
    protected final HollowObjectSchema schema;

    public HollowDataAccessorGenerator(String packageName, String apiclassName, HollowObjectSchema schema,
            HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, SUB_PACKAGE_NAME, dataset, config);
        this.className = getClassName(schema);
        this.apiclassName = apiclassName;
        this.type =  hollowImplClassname(schema.getName());
        this.schema = schema;
    }

    protected String getClassName(HollowObjectSchema schema) {
        return schema.getName() + "DataAccessor";
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, apiclassName, Arrays.<HollowSchema>asList(schema));

        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + AbstractHollowDataAccessor.class.getName() + ";\n");
        builder.append("import " + PrimaryKey.class.getName() + ";\n");
        builder.append("import " + HollowReadStateEngine.class.getName() + ";\n");

        builder.append("\n");
        builder.append("@SuppressWarnings(\"all\")\n");
        builder.append("public class " + className + " extends " + AbstractHollowDataAccessor.class.getSimpleName() + "<" + type  +"> {\n\n");

        builder.append("    public static final String TYPE = \"" + type + "\";\n");
        builder.append("    private " + apiclassName + " api;\n\n");

        genConstructors(builder);
        genPublicAPIs(builder);

        builder.append("}");

        return builder.toString();
    }

    protected void genConstructors(StringBuilder builder) {
        builder.append("    public " + className + "(HollowConsumer consumer) {\n");
        builder.append("        super(consumer, TYPE);\n");
        builder.append("        this.api = (" + apiclassName + ")consumer.getAPI();\n");
        builder.append("    }\n\n");

        builder.append("    public " + className + "(HollowReadStateEngine rStateEngine, " + apiclassName + " api) {\n");
        builder.append("        super(rStateEngine, TYPE);\n");
        builder.append("        this.api = api;\n");
        builder.append("    }\n\n");

        builder.append("    public " + className + "(HollowReadStateEngine rStateEngine, " + apiclassName + " api, String ... fieldPaths) {\n");
        builder.append("        super(rStateEngine, TYPE, fieldPaths);\n");
        builder.append("        this.api = api;\n");
        builder.append("    }\n\n");

        builder.append("    public " + className + "(HollowReadStateEngine rStateEngine, " + apiclassName + " api, PrimaryKey primaryKey) {\n");
        builder.append("        super(rStateEngine, TYPE, primaryKey);\n");
        builder.append("        this.api = api;\n");
        builder.append("    }\n\n");
    }

    protected void genPublicAPIs(StringBuilder builder) {
        builder.append("    @Override public " + type + " getRecord(int ordinal){\n");
        builder.append("        return api.deserializeFrom" + type + "(ordinal);\n");
        builder.append("    }\n\n");
    }
}