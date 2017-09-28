/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.codegen.indexes;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowImplClassname;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstrackHollowUniqueKeyIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowUniqueKeyIndexGenerator extends HollowIndexGenerator {

    protected final HollowObjectSchema schema;
    protected final String type;

    protected boolean isGenSimpleConstructor = false;
    protected boolean isParameterizedConstructorPublic = true;
    protected boolean isAutoListenToDataRefresh = false;

    public HollowUniqueKeyIndexGenerator(String packageName, String apiClassname, String classPostfix, boolean useAggressiveSubstitutions, HollowObjectSchema schema, boolean usePackageGrouping) {
        super(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, usePackageGrouping);

        this.type = schema.getName();
        this.className = getClassName(schema);
        this.schema = schema;
    }

    protected String getClassName(HollowObjectSchema schema) {
        return schema.getName() + "UniqueKeyIndex";
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);

        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + AbstrackHollowUniqueKeyIndex.class.getName() + ";\n");
        if (isGenSimpleConstructor)
            builder.append("import " + HollowObjectSchema.class.getName() + ";\n");

        builder.append("\n");
        builder.append("public class " + className + " extends " + AbstrackHollowUniqueKeyIndex.class.getSimpleName() + "<" + apiClassname + ", " + type + "> {\n\n");

        {
            genConstructors(builder);
            genPublicAPIs(builder);
        }

        builder.append("}");

        return builder.toString();
    }

    protected void genConstructors(StringBuilder builder) {
        if (isGenSimpleConstructor)
            genDefaultConstructor(builder);

        genParameterizedConstructor(builder);
    }

    protected void genDefaultConstructor(StringBuilder builder) {
        builder.append("    public " + className + "(HollowConsumer consumer) {\n");
        builder.append("        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema(\"" + type + "\")).getPrimaryKey().getFieldPaths());\n");
        builder.append("    }\n\n");
    }

    protected void genParameterizedConstructor(StringBuilder builder) {
        builder.append("    " + (isParameterizedConstructorPublic ? "public " : "private ") + className + "(HollowConsumer consumer, String... fieldPaths) {\n");
        builder.append("        this(consumer, "+ isAutoListenToDataRefresh + ", fieldPaths);\n"); 
        builder.append("    }\n\n");

        builder.append("    " + (isParameterizedConstructorPublic ? "public " : "private ") + className + "(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {\n");
        builder.append("        super(consumer, \"" + type + "\", isListenToDataRefreah, fieldPaths);\n");
        builder.append("    }\n\n");

    }

    protected void genPublicAPIs(StringBuilder builder) {
        genFindMatchAPI(builder);
    }

    protected void genFindMatchAPI(StringBuilder builder) {
        builder.append("    public " + hollowImplClassname(type, classPostfix, useAggressiveSubstitutions) + " findMatch(Object... keys) {\n");
        builder.append("        int ordinal = idx.getMatchingOrdinal(keys);\n");
        builder.append("        if(ordinal == -1)\n");
        builder.append("            return null;\n");
        builder.append("        return api.get" + hollowImplClassname(type, classPostfix, useAggressiveSubstitutions) + "(ordinal);\n");
        builder.append("    }\n\n");
    }
}