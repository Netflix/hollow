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
package com.netflix.hollow.api.codegen.indexes;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Arrays;

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
    protected boolean isImplementsUniqueKeyIndex = true;

    public HollowUniqueKeyIndexGenerator(String packageName, String apiClassname, HollowObjectSchema schema,
            HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, apiClassname, dataset, config);

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
        appendPackageAndCommonImports(builder, apiClassname, Arrays.<HollowSchema>asList(schema));
        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + AbstractHollowUniqueKeyIndex.class.getName() + ";\n");
        builder.append("import " + HollowUniqueKeyIndex.class.getName() + ";\n");
        if (isGenSimpleConstructor)
            builder.append("import " + HollowObjectSchema.class.getName() + ";\n");

        builder.append("\n/**\n");
        genDeprecatedJavaDoc(builder);
        builder.append(" */\n");
        builder.append("@Deprecated\n");
        builder.append("@SuppressWarnings(\"all\")\n");
        builder.append("public class " + className + " extends " + AbstractHollowUniqueKeyIndex.class.getSimpleName() + "<" + apiClassname + ", " + hollowImplClassname(type) + "> ");
        if (isImplementsUniqueKeyIndex) {
            builder.append("implements " + HollowUniqueKeyIndex.class.getSimpleName() + "<" + hollowImplClassname(type) + "> ");
        }
        builder.append("{\n\n");
        {
            genConstructors(builder);
            genPublicAPIs(builder);
        }

        builder.append("}");

        return builder.toString();
    }

    protected void genConstructors(StringBuilder builder) {
        if (isGenSimpleConstructor)
            genSimpleConstructor(builder);

        genParameterizedConstructor(builder);
    }

    protected void genSimpleConstructor(StringBuilder builder) {
        builder.append("    public " + className + "(HollowConsumer consumer) {\n");
        builder.append("        this(consumer, "+ isAutoListenToDataRefresh + ");\n");
        builder.append("    }\n\n");

        builder.append("    public " + className + "(HollowConsumer consumer, boolean isListenToDataRefresh) {\n");
        builder.append("        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema(\"" + type + "\")).getPrimaryKey().getFieldPaths());\n");
        builder.append("    }\n\n");

    }

    protected void genParameterizedConstructor(StringBuilder builder) {
        builder.append("    " + (isParameterizedConstructorPublic ? "public " : "private ") + className + "(HollowConsumer consumer, String... fieldPaths) {\n");
        builder.append("        this(consumer, "+ isAutoListenToDataRefresh + ", fieldPaths);\n");
        builder.append("    }\n\n");

        builder.append("    " + (isParameterizedConstructorPublic ? "public " : "private ") + className + "(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {\n");
        builder.append("        super(consumer, \"" + type + "\", isListenToDataRefresh, fieldPaths);\n");
        builder.append("    }\n\n");

    }

    protected void genPublicAPIs(StringBuilder builder) {
        genFindMatchAPI(builder);
    }

    protected void genFindMatchAPI(StringBuilder builder) {
        if (isImplementsUniqueKeyIndex)
            builder.append("    @Override\n");
        builder.append("    public " + hollowImplClassname(type) + " findMatch(Object... keys) {\n");
        builder.append("        int ordinal = idx.getMatchingOrdinal(keys);\n");
        builder.append("        if(ordinal == -1)\n");
        builder.append("            return null;\n");
        builder.append("        return api.deserializeFrom" + hollowImplClassname(type) + "(ordinal);\n");
        builder.append("    }\n\n");
    }

    protected void genDeprecatedJavaDoc(StringBuilder builder) {
        String typeName = hollowImplClassname(type);
        builder.append(" * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:\n");
        builder.append(" * <pre>{@code\n");
        builder.append(String.format(" *     UniqueKeyIndex<%s, K> uki = UniqueKeyIndex.from(consumer, %1$s.class)\n", typeName));
        builder.append(" *         .usingBean(k);\n");
        builder.append(String.format(" *     %s m = uki.findMatch(k);\n", typeName));
        builder.append(" * }</pre>\n");
        builder.append(" * where {@code K} is a class declaring key field paths members, annotated with\n");
        builder.append(" * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of\n");
        builder.append(String.format(" * {@code K} that is the key to find the unique {@code %s} object.\n", typeName));
    }
}
