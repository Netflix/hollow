/*
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
 */
package com.netflix.hollow.api.codegen;

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

    protected String className;
    protected boolean useCollectionsImport=false;

    public HollowConsumerJavaFileGenerator(String packageName, String subPackageName, CodeGeneratorConfig config) {
        this.packageName = packageName;
        this.subPackageName = subPackageName;
        this.config = config;
    }

    protected String hollowImplClassname(String typeName) {
        return  HollowCodeGenerationUtils.hollowImplClassname(typeName, config.getClassPostfix(), config.isUseAggressiveSubstitutions());
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
        String fullPackageName = createFullPackageName(packageName, subPackageName, config.isUsePackageGrouping());
        if (!isEmpty(fullPackageName)) {
            builder.append("package ").append(fullPackageName).append(";\n\n");

            if (config.isUseHollowPrimitiveTypes()) {
                builder.append("import com.netflix.hollow.core.type.*;\n");
            }

            if (config.isUsePackageGrouping()) {
                builder.append("import ").append(packageName).append(".*;\n");
                builder.append("import ").append(packageName).append(".core.*;\n");
                if (useCollectionsImport) builder.append("import ").append(packageName).append(".collections.*;\n\n");
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
}
