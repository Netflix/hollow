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
package com.netflix.hollow.api.codegen.api;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowImplClassname;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowJavaFileGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation. Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowDataAccessorGenerator implements HollowJavaFileGenerator {

    protected final String packageName;
    protected final String classname;
    protected final String apiClassname;
    protected final String classPostfix;
    protected final String type;
    protected final boolean useAggressiveSubstitutions;
    protected final HollowObjectSchema schema;

    public HollowDataAccessorGenerator(String packageName, String apiClassname, String classPostfix, boolean useAggressiveSubstitutions, HollowObjectSchema schema) {
        this.classname = getClassName(schema);
        this.apiClassname = apiClassname;
        this.classPostfix = classPostfix;
        this.packageName = packageName;
        this.type =  hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions);
        this.useAggressiveSubstitutions = useAggressiveSubstitutions;
        this.schema = schema;
    }

    protected String getClassName(HollowObjectSchema schema) {
        return schema.getName() + "DataAccessor";
    }

    @Override
    public String getClassName() {
        return classname;
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();

        builder.append("package " + packageName + ";\n\n");

        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + AbstractHollowDataAccessor.class.getName() + ";\n");
        builder.append("import " + PrimaryKey.class.getName() + ";\n");
        builder.append("import " + HollowReadStateEngine.class.getName() + ";\n");

        builder.append("\n");
        builder.append("public class " + classname + " extends " + AbstractHollowDataAccessor.class.getSimpleName() + "<" + type  +"> {\n\n");

        builder.append("    private " + apiClassname + " api;\n\n");

        genConstructors(builder);
        genPublicAPIs(builder);

        builder.append("}");

        return builder.toString();
    }

    protected void genConstructors(StringBuilder builder) {
        builder.append("    public " + classname + "(HollowConsumer consumer, String type) {\n");
        builder.append("        super(consumer, type);\n");
        builder.append("    }\n\n");

        builder.append("    public " + classname + "(HollowReadStateEngine rStateEngine, String type) {\n");
        builder.append("        super(rStateEngine, type);\n");
        builder.append("    }\n\n");

        builder.append("    public " + classname + "(HollowReadStateEngine rStateEngine, String type, String ... fieldPaths) {\n");
        builder.append("        super(rStateEngine, type, fieldPaths);\n");
        builder.append("    }\n\n");

        builder.append("    public " + classname + "(HollowReadStateEngine rStateEngine, String type, PrimaryKey primaryKey) {\n");
        builder.append("        super(rStateEngine, type, primaryKey);\n");
        builder.append("    }\n\n");
    }

    protected void genPublicAPIs(StringBuilder builder) {
        builder.append("    @Override public " + type + " getRecord(int ordinal){\n");
        builder.append("        return api.get" + type + "(ordinal);\n");
        builder.append("    }\n\n");
    }
}