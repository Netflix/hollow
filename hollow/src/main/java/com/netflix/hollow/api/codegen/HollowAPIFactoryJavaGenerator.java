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
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPIFactory} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 */
public class HollowAPIFactoryJavaGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "core";

    private final String apiClassname;

    public HollowAPIFactoryJavaGenerator(String packageName, String apiClassname, HollowDataset dataset,
            CodeGeneratorConfig config) {
        super(packageName, SUB_PACKAGE_NAME, dataset, config);
        this.apiClassname = apiClassname;
        this.className = apiClassname + "Factory";
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, apiClassname);

        builder.append("import ").append(HollowAPIFactory.class.getName()).append(";\n");
        builder.append("import ").append(HollowAPI.class.getName()).append(";\n");
        builder.append("import ").append(HollowFactory.class.getName()).append(";\n");
        builder.append("import ").append(HollowDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(Collections.class.getName()).append(";\n");
        builder.append("import ").append(Set.class.getName()).append(";\n");

        appendGeneratedAnnotation(builder);

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" implements HollowAPIFactory {\n\n");

        builder.append("    private final Set<String> cachedTypes;\n\n");

        builder.append("    public ").append(className).append("() {\n");
        builder.append("        this(Collections.<String>emptySet());\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(className).append("(Set<String> cachedTypes) {\n");
        builder.append("        this.cachedTypes = cachedTypes;\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public HollowAPI createAPI(HollowDataAccess dataAccess) {\n");
        builder.append("        return new ").append(apiClassname).append("(dataAccess, cachedTypes);\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {\n");
        builder.append("        if (!(previousCycleAPI instanceof ").append(apiClassname).append(")) {\n");
        builder.append("            throw new ClassCastException(previousCycleAPI.getClass() + \" not instance of ").append(apiClassname).append("\");");
        builder.append("        }\n");
        builder.append("        return new ").append(apiClassname).append("(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (").append(apiClassname).append(") previousCycleAPI);\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }

}
