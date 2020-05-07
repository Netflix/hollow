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
 */
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.api.codegen.objects.HollowCollectionsGenerator;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 * @author dsu
 */
public abstract class HollowConsumerJavaFileGenerator implements HollowJavaFileGenerator {
    protected final String packageName;
    protected final String subPackageName;
    protected final CodeGeneratorConfig config;
    protected final HollowDataset dataset;

    protected String className;
    protected boolean useCollectionsImport=false;

    public HollowConsumerJavaFileGenerator(String packageName, String subPackageName, HollowDataset dataset,
            CodeGeneratorConfig config) {
        this.dataset = dataset;
        this.packageName = packageName;
        this.subPackageName = subPackageName;
        this.config = config;
    }

    protected String hollowImplClassname(String typeName) {
        return HollowCodeGenerationUtils.hollowImplClassname(typeName, config.getClassPostfix(),
                config.isUseAggressiveSubstitutions(), config.isUseHollowPrimitiveTypes());
    }

    public String getSubPackageName() {
        return subPackageName;
    }

    @Override
    public final String getClassName() {
        return className;
    }

    public void useCollectionsImport() {
        this.useCollectionsImport=true;
    }

    protected void appendPackageAndCommonImports(StringBuilder builder) {
        appendPackageAndCommonImports(builder, null, new ArrayList<>());
    }

    protected void appendPackageAndCommonImports(StringBuilder builder,
            String apiClassname) {
        appendPackageAndCommonImports(builder, apiClassname, new ArrayList<>());
    }

    protected void appendPackageAndCommonImports(StringBuilder builder,
            String apiClassname, List<HollowSchema> schemasToImport) {
        String fullPackageName =
            createFullPackageName(packageName, subPackageName, config.isUsePackageGrouping());
        if (!isEmpty(fullPackageName)) {
            builder.append("package ").append(fullPackageName).append(";\n\n");

            if (config.isUseHollowPrimitiveTypes()) {
                builder.append("import com.netflix.hollow.core.type.*;\n");
            }

            if (config.isUsePackageGrouping()) {
                if (apiClassname != null) {
                    appendImportFromBasePackage(builder, apiClassname);
                }
                Set<String> schemaNameSet = new HashSet<>();
                for (HollowSchema schema : schemasToImport) {
                    switch (schema.getSchemaType()) {
                        case OBJECT:
                            addToSetIfNotPrimitiveOrCollection(schemaNameSet, schema.getName());
                            break;
                        case SET:
                            addToSetIfNotPrimitiveOrCollection(schemaNameSet,
                                    ((HollowSetSchema) schema).getElementType());
                            break;
                        case LIST:
                            addToSetIfNotPrimitiveOrCollection(schemaNameSet,
                                    ((HollowListSchema) schema).getElementType());
                            break;
                        case MAP:
                            HollowMapSchema mapSchema = (HollowMapSchema) schema;
                            addToSetIfNotPrimitiveOrCollection(schemaNameSet, mapSchema.getKeyType(),
                                    mapSchema.getValueType());
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected HollowSchema to import: " + schema);

                    }
                }
                for (String schemaName : schemaNameSet) {
                    appendImportFromBasePackage(builder, schemaName + config.getClassPostfix());
                }
                appendImportFromBasePackage(builder, "core.*");
                if (useCollectionsImport) {
                    appendImportFromBasePackage(builder, HollowCollectionsGenerator.SUB_PACKAGE_NAME + ".*");
                }
                builder.append("\n");
            }
        }
    }

    private String createFullPackageName(String packageName, String subPackageName, boolean usePackageGrouping) {
        if (usePackageGrouping && !isEmpty(packageName) && !isEmpty(subPackageName)) {
            return packageName + "."  + subPackageName;
        } else {
            return packageName;
        }

    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void appendImportFromBasePackage(StringBuilder builder, String leaf) {
        builder.append("import ").append(packageName).append(".").append(leaf).append(";\n");
    }

    /**
     * Adds the schema name to the set if the schema name doesn't correspond to a Hollow
     * primitive type. Factored out to prevent bloat in the switch statement it is called
     * from.
     */
    private void addToSetIfNotPrimitiveOrCollection(Set<String> schemaNameSet, String... schemaNames) {
        for (String schemaName : schemaNames) {
            // collections schemas deserializeFrom brought in by a star import
            if (!HollowCodeGenerationUtils.isCollectionType(schemaName, dataset) &&
                    !HollowCodeGenerationUtils.isPrimitiveType(schemaName)) {
                schemaNameSet.add(schemaName);
            }
        }
    }
}
