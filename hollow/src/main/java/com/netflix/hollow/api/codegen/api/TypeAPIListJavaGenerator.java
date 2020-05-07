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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateLookupClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 * @author dkoszewnik
 *
 */
public class TypeAPIListJavaGenerator extends HollowTypeAPIGenerator {
    private final HollowListSchema schema;

    public TypeAPIListJavaGenerator(String apiClassname, String packageName, HollowListSchema schema,
            HollowDataset dataset, CodeGeneratorConfig config) {
        super(apiClassname, packageName, schema, dataset, config);
        this.schema = schema;
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, apiClassname);

        builder.append("import " + HollowListTypeAPI.class.getName() + ";\n\n");
        builder.append("import " + HollowListTypeDataAccess.class.getName() + ";\n");
        builder.append("import " + HollowListLookupDelegate.class.getName() + ";\n");

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" extends HollowListTypeAPI {\n\n");

        builder.append("    private final ").append(delegateLookupClassname(schema)).append(" delegateLookupImpl;\n\n");

        builder.append("    public ").append(className).append("(").append(apiClassname).append(" api, HollowListTypeDataAccess dataAccess) {\n");
        builder.append("        super(api, dataAccess);\n");
        builder.append("        this.delegateLookupImpl = new ").append(delegateLookupClassname(schema)).append("(this);\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(typeAPIClassname(schema.getElementType())).append(" getElementAPI() {\n");
        builder.append("        return getAPI().deserializeFrom").append(typeAPIClassname(schema.getElementType())).append("();\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(delegateLookupClassname(schema)).append(" getDelegateLookupImpl() {\n");
        builder.append("        return delegateLookupImpl;\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(apiClassname).append(" getAPI() {\n");
        builder.append("        return (").append(apiClassname).append(")api;\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }

}