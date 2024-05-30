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

import java.nio.file.Path;

public class CodeGeneratorConfig {
    private String classPostfix = "";
    private String getterPrefix = "";
    private boolean useAggressiveSubstitutions = false;

    // @TODO: Need to default this to be true in next major version of Hollow
    private boolean usePackageGrouping = false;
    private boolean useBooleanFieldErgonomics = false;
    private boolean reservePrimaryKeyIndexForTypeWithPrimaryKey = false;
    private boolean useHollowPrimitiveTypes = false;
    private boolean restrictApiToFieldType = false;
    private boolean useVerboseToString = false;
    private boolean useGeneratedAnnotation = false;

    private boolean useMetaInfo = false;
    private Path metaInfoPath;

    public void setMetaInfoPath(Path metaInfoPath) {
        this.useMetaInfo = true;
        this.metaInfoPath = metaInfoPath;
    }

    public boolean isUseMetaInfo() {
        return useMetaInfo;
    }

    public Path getMetaInfoPath() {
        return metaInfoPath;
    }

    public CodeGeneratorConfig() {}

    public CodeGeneratorConfig(String classPostfix, String getterPrefix) {
        this.classPostfix = classPostfix;
        this.getterPrefix = getterPrefix;
    }

    // Make it easier to automatically use defaults for next major version
    public void initWithNextMajorVersionDefaults_V3() {
        usePackageGrouping = true;
        useBooleanFieldErgonomics = true;
        reservePrimaryKeyIndexForTypeWithPrimaryKey = true;
        useHollowPrimitiveTypes = true;
        restrictApiToFieldType = true;
        useVerboseToString = true;
    }

    public String getClassPostfix() {
        return classPostfix;
    }

    public void setClassPostfix(String classPostfix) {
        this.classPostfix = classPostfix;
    }

    public String getGetterPrefix() {
        return getterPrefix;
    }

    public void setGetterPrefix(String getterPrefix) {
        this.getterPrefix = getterPrefix;
    }

    public boolean isUsePackageGrouping() {
        return usePackageGrouping;
    }

    public void setUsePackageGrouping(boolean usePackageGrouping) {
        this.usePackageGrouping = usePackageGrouping;
    }

    public boolean isUseAggressiveSubstitutions() {
        return useAggressiveSubstitutions;
    }

    public void setUseAggressiveSubstitutions(boolean useAggressiveSubstitutions) {
        this.useAggressiveSubstitutions = useAggressiveSubstitutions;
    }

    public boolean isUseBooleanFieldErgonomics() {
        return useBooleanFieldErgonomics;
    }

    public void setUseBooleanFieldErgonomics(boolean useBooleanFieldErgonomics) {
        this.useBooleanFieldErgonomics = useBooleanFieldErgonomics;
    }

    public boolean isReservePrimaryKeyIndexForTypeWithPrimaryKey() {
        return reservePrimaryKeyIndexForTypeWithPrimaryKey;
    }

    public void setReservePrimaryKeyIndexForTypeWithPrimaryKey(boolean reservePrimaryKeyIndexForTypeWithPrimaryKey) {
        this.reservePrimaryKeyIndexForTypeWithPrimaryKey = reservePrimaryKeyIndexForTypeWithPrimaryKey;
    }

    public boolean isListenToDataRefresh() {
        return !reservePrimaryKeyIndexForTypeWithPrimaryKey; // NOTE: to be backwards compatible
    }

    public boolean isUseHollowPrimitiveTypes() {
        return useHollowPrimitiveTypes;
    }

    public void setUseHollowPrimitiveTypes(boolean useHollowPrimitiveTypes) {
        this.useHollowPrimitiveTypes = useHollowPrimitiveTypes;
    }

    public boolean isRestrictApiToFieldType() {
        return restrictApiToFieldType;
    }

    public void setRestrictApiToFieldType(boolean restrictApiToFieldType) {
        this.restrictApiToFieldType = restrictApiToFieldType;
    }

    public boolean isUseVerboseToString() {
        return useVerboseToString;
    }

    public void setUseVerboseToString(boolean useVerboseToString) {
        this.useVerboseToString = useVerboseToString;
    }

    public boolean isUseGeneratedAnnotation() {
        return useGeneratedAnnotation;
    }

    public void setUseGeneratedAnnotation(boolean useGeneratedAnnotation) {
        this.useGeneratedAnnotation = useGeneratedAnnotation;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classPostfix == null) ? 0 : classPostfix.hashCode());
        result = prime * result + ((getterPrefix == null) ? 0 : getterPrefix.hashCode());
        result = prime * result + (reservePrimaryKeyIndexForTypeWithPrimaryKey ? 1231 : 1237);
        result = prime * result + (restrictApiToFieldType ? 1231 : 1237);
        result = prime * result + (useAggressiveSubstitutions ? 1231 : 1237);
        result = prime * result + (useBooleanFieldErgonomics ? 1231 : 1237);
        result = prime * result + (useGeneratedAnnotation ? 1231 : 1237);
        result = prime * result + (useHollowPrimitiveTypes ? 1231 : 1237);
        result = prime * result + (usePackageGrouping ? 1231 : 1237);
        result = prime * result + (useVerboseToString ? 1231 : 1237);
        result = prime * result + (useMetaInfo ? 1231 : 1237);
        result = prime * result + ((metaInfoPath == null) ? 0 : metaInfoPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CodeGeneratorConfig other = (CodeGeneratorConfig) obj;
        if (classPostfix == null) {
            if (other.classPostfix != null)
                return false;
        } else if (!classPostfix.equals(other.classPostfix))
            return false;
        if (getterPrefix == null) {
            if (other.getterPrefix != null)
                return false;
        } else if (!getterPrefix.equals(other.getterPrefix))
            return false;
        if (reservePrimaryKeyIndexForTypeWithPrimaryKey != other.reservePrimaryKeyIndexForTypeWithPrimaryKey)
            return false;
        if (restrictApiToFieldType != other.restrictApiToFieldType)
            return false;
        if (useAggressiveSubstitutions != other.useAggressiveSubstitutions)
            return false;
        if (useBooleanFieldErgonomics != other.useBooleanFieldErgonomics)
            return false;
        if (useGeneratedAnnotation != other.useGeneratedAnnotation)
            return false;
        if (useHollowPrimitiveTypes != other.useHollowPrimitiveTypes)
            return false;
        if (usePackageGrouping != other.usePackageGrouping)
            return false;
        if (useVerboseToString != other.useVerboseToString)
            return false;
        if (useMetaInfo != other.useMetaInfo)
            return false;
        if (metaInfoPath == null) {
            if (other.metaInfoPath != null)
                return false;
        } else if (!metaInfoPath.equals(other.metaInfoPath))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CodeGeneratorConfig [classPostfix=");
        builder.append(classPostfix);
        builder.append(", getterPrefix=");
        builder.append(getterPrefix);
        builder.append(", useGeneratedAnnotation=");
        builder.append(useGeneratedAnnotation);
        builder.append(", usePackageGrouping=");
        builder.append(usePackageGrouping);
        builder.append(", useAggressiveSubstitutions=");
        builder.append(useAggressiveSubstitutions);
        builder.append(", useBooleanFieldErgonomics=");
        builder.append(useBooleanFieldErgonomics);
        builder.append(", reservePrimaryKeyIndexForTypeWithPrimaryKey=");
        builder.append(reservePrimaryKeyIndexForTypeWithPrimaryKey);
        builder.append(", useHollowPrimitiveTypes=");
        builder.append(useHollowPrimitiveTypes);
        builder.append(", restrictApiToFieldType=");
        builder.append(restrictApiToFieldType);
        builder.append(", useVerboseToString=");
        builder.append(useVerboseToString);
        builder.append(", useMetaInfo=");
        builder.append(useMetaInfo);
        builder.append(", metaInfoPath=");
        builder.append(metaInfoPath);
        builder.append("]");
        return builder.toString();
    }
}