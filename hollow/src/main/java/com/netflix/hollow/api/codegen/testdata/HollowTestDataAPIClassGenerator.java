/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.codegen.testdata;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class HollowTestDataAPIClassGenerator {

    private final HollowDataset dataset;
    private final String apiClassName;
    private final String packageName;

    public HollowTestDataAPIClassGenerator(HollowDataset dataset, String apiClassName, String packageName) {
        this.dataset = dataset;
        this.apiClassName = apiClassName;
        this.packageName = packageName;
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();

        builder.append("package " + packageName + ";\n\n");

        builder.append("import com.netflix.hollow.api.testdata.HollowTestDataset;\n\n");

        builder.append("public class " + apiClassName + " extends HollowTestDataset {\n\n");

        List<HollowSchema> schemas = new ArrayList<>(dataset.getSchemas());
        schemas.sort(Comparator.comparing(HollowSchema::getName));

        for(HollowSchema schema : schemas) {
            builder.append("    public " + schema.getName() + "TestData<Void> " + schema.getName() + "() {\n");
            builder.append("        " + schema.getName() + "TestData<Void> rec = new " + schema.getName() + "TestData<>(null);\n");
            builder.append("        add(rec);\n");
            builder.append("        return rec;\n");
            builder.append("    }\n\n");
        }

        builder.append("}");

        return builder.toString();
    }
}
