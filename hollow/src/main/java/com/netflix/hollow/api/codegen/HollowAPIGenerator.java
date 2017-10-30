/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.api.codegen.api.HollowDataAccessorGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPIListJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPIMapJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPIObjectJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPISetJavaGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateCachedImplGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateInterfaceGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateLookupImplGenerator;
import com.netflix.hollow.api.codegen.indexes.HollowHashIndexGenerator;
import com.netflix.hollow.api.codegen.indexes.HollowPrimaryKeyIndexGenerator;
import com.netflix.hollow.api.codegen.indexes.HollowUniqueKeyIndexGenerator;
import com.netflix.hollow.api.codegen.indexes.LegacyHollowPrimaryKeyIndexGenerator;
import com.netflix.hollow.api.codegen.objects.HollowFactoryJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowListJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowMapJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowObjectJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowSetJavaGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * This class is used to generate java code which defines an implementation of a {@link HollowAPI}.
 *
 * The generated java code is based on a data model (defined by a set of {@link HollowSchema}), and will
 * contain convenience methods for traversing a dataset, based on the specific fields in the data model.
 */
public class HollowAPIGenerator {
    protected final String apiClassname;
    protected final String packageName;
    protected final HollowDataset dataset;
    protected final Set<String> parameterizedTypes;
    protected final boolean parameterizeClassNames;
    protected final boolean hasCollectionsInDataSet;
    protected final HollowErgonomicAPIShortcuts ergonomicShortcuts;

    protected CodeGeneratorConfig config = new CodeGeneratorConfig();

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), false, false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeAllClassNames if true, all methods which return a Hollow Object will be parameterized.  This is useful when
     *                               alternate implementations are desired for some types.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, boolean parameterizeAllClassNames) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), parameterizeAllClassNames, false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeSpecificTypeNames methods with matching names which return a Hollow Object will be parameterized.  This is useful when
     *                               alternate implementations are desired for some types.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizeSpecificTypeNames) {
        this(apiClassname, packageName, dataset, parameterizeSpecificTypeNames, false, false);
    }

    protected HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizedTypes, boolean parameterizeAllClassNames, boolean useErgonomicShortcuts) {
        this.apiClassname = apiClassname;
        this.packageName = packageName;
        this.dataset = dataset;
        this.hasCollectionsInDataSet = hasCollectionsInDataSet(dataset);
        this.parameterizedTypes = parameterizedTypes;
        this.parameterizeClassNames = parameterizeAllClassNames;
        this.ergonomicShortcuts = useErgonomicShortcuts ? new HollowErgonomicAPIShortcuts(dataset) : HollowErgonomicAPIShortcuts.NO_SHORTCUTS;
    }

    protected static boolean hasCollectionsInDataSet(HollowDataset dataset) {
        for(HollowSchema schema : dataset.getSchemas()) {
            if ((schema instanceof HollowListSchema) ||
                    (schema instanceof HollowSetSchema) ||
                    (schema instanceof HollowMapSchema)) {
                return true;
            }
        }
        return false;
    }

    protected void setCodeGeneratorConfig(CodeGeneratorConfig config) {
        this.config = config;
    }

    /**
     * Use this method to override the default postfix "Hollow" for all generated Hollow object classes.
     */
    public void setClassPostfix(String classPostfix) {
        config.classPostfix = classPostfix;
    }

    /**
     * Use this method to override the default prefix "_" for all getters on all generated Hollow object classes.
     */
    public void setGetterPrefix(String getterPrefix) {
        config.getterPrefix = getterPrefix;
    }

    /**
     * Use this method to override generated classnames for type names corresponding to any class in the java.lang package.
     *
     * Defaults to false, which overrides only type names corresponding to a few select classes in java.lang.
     */
    public void setUseAggressiveSubstitutions(boolean useAggressiveSubstitutions) {
        config.useAggressiveSubstitutions = useAggressiveSubstitutions;
    }

    /**
     * Use this method to specify to use new boolean field ergonomics for generated API
     *
     * Defaults to false to be backwards compatible
     */
    public void setUseBooleanFieldErgonomics(boolean useBooleanFieldErgonomics) {
        config.useBooleanFieldErgonomics = useBooleanFieldErgonomics;
    }

    /**
     * Use this method to specify to use sub packages in generated code instead of single package
     *
     * Defaults to false to be backwards compatible
     */
    public void setUsePackageGrouping(boolean usePackageGrouping) {
        config.usePackageGrouping = usePackageGrouping;
    }

    /**
     * Use this method to specify to only generate PrimaryKeyIndex for Types that has PrimaryKey defined
     *
     * Defaults to false to be backwards compatible
     */
    public void reservePrimaryKeyIndexForTypeWithPrimaryKey(boolean reservePrimaryKeyIndexForTypeWithPrimaryKey) {
        config.reservePrimaryKeyIndexForTypeWithPrimaryKey = reservePrimaryKeyIndexForTypeWithPrimaryKey;
    }

    /**
     * Use this method to specify to use Hollow Primitive Types instead of generating them per project
     *
     * Defaults to false to be backwards compatible
     */
    public void setUseHollowPrimitiveTypes(boolean useHollowPrimitiveTypes) {
        config.useHollowPrimitiveTypes = useHollowPrimitiveTypes;
    }

    /**
     * If setRestrictApiToFieldType is true, api code only generates get<FieldName> with return type as per schema
     *
     * Defaults to false to be backwards compatible
     */
    public void setRestrictApiToFieldType(boolean restrictApiToFieldType) {
        config.restrictApiToFieldType = restrictApiToFieldType;
    }

    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    public void generateFiles(File directory) throws IOException {
        directory.mkdirs();

        HollowAPIClassJavaGenerator apiClassGenerator = new HollowAPIClassJavaGenerator(packageName, apiClassname, dataset, parameterizeClassNames, config);
        HollowAPIFactoryJavaGenerator apiFactoryGenerator = new HollowAPIFactoryJavaGenerator(packageName, apiClassname, config);

        HollowHashIndexGenerator hashIndexGenerator = new HollowHashIndexGenerator(packageName, apiClassname, dataset, config);

        generateFile(directory, apiClassGenerator);
        generateFile(directory, apiFactoryGenerator);
        generateFile(directory, hashIndexGenerator);

        generateFilesForHollowSchemas(directory);
    }

    protected void generateFilesForHollowSchemas(File directory) throws IOException {
        for(HollowSchema schema : dataset.getSchemas()) {
            String type = schema.getName();
            if (config.isUseHollowPrimitiveTypes() && HollowCodeGenerationUtils.isPrimitiveType(type)) continue; // skip if using hollow primitive type

            generateFile(directory, getStaticAPIGenerator(schema));
            generateFile(directory, getHollowObjectGenerator(schema));
            generateFile(directory, getHollowFactoryGenerator(schema));

            if(schema.getSchemaType() == SchemaType.OBJECT) {
                HollowObjectSchema objSchema = (HollowObjectSchema)schema;
                generateFile(directory, new HollowObjectDelegateInterfaceGenerator(packageName, objSchema, ergonomicShortcuts, config));
                generateFile(directory, new HollowObjectDelegateCachedImplGenerator(packageName, objSchema, ergonomicShortcuts, config));
                generateFile(directory, new HollowObjectDelegateLookupImplGenerator(packageName, objSchema, ergonomicShortcuts, config));

                generateFile(directory, new HollowDataAccessorGenerator(packageName, apiClassname, objSchema, config));
                if (!config.isReservePrimaryKeyIndexForTypeWithPrimaryKey()) {
                    generateFile(directory, new LegacyHollowPrimaryKeyIndexGenerator(packageName, apiClassname, objSchema, config));
                } else if ((objSchema).getPrimaryKey() != null) {
                    generateFile(directory, new HollowPrimaryKeyIndexGenerator(dataset, packageName, apiClassname,  objSchema, config));
                    generateFile(directory, new HollowUniqueKeyIndexGenerator(packageName, apiClassname, objSchema, config));
                }
            }
        }
    }

    protected void generateFile(File directory, HollowJavaFileGenerator generator) throws IOException {
        // create sub folder if not using default package and sub packages are enabled
        if ((packageName!=null && !packageName.trim().isEmpty()) && config.isUsePackageGrouping() && (generator instanceof HollowConsumerJavaFileGenerator)) {
            HollowConsumerJavaFileGenerator consumerCodeGenerator = (HollowConsumerJavaFileGenerator)generator;
            if (hasCollectionsInDataSet) consumerCodeGenerator.useCollectionsImport();
            directory = new File(directory, consumerCodeGenerator.getSubPackageName());
        }
        if (!directory.exists()) directory.mkdirs();

        FileWriter writer = new FileWriter(new File(directory, generator.getClassName() + ".java"));
        writer.write(generator.generate());
        writer.close();
    }

    protected HollowJavaFileGenerator getStaticAPIGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new TypeAPIObjectJavaGenerator(apiClassname, packageName, (HollowObjectSchema) schema, config);
        } else if(schema instanceof HollowListSchema) {
            return new TypeAPIListJavaGenerator(apiClassname, packageName, (HollowListSchema)schema, config);
        } else if(schema instanceof HollowSetSchema) {
            return new TypeAPISetJavaGenerator(apiClassname, packageName, (HollowSetSchema)schema, config);
        } else if(schema instanceof HollowMapSchema) {
            return new TypeAPIMapJavaGenerator(apiClassname, packageName, (HollowMapSchema)schema, config);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    protected HollowJavaFileGenerator getHollowObjectGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new HollowObjectJavaGenerator(packageName, apiClassname, (HollowObjectSchema) schema, parameterizedTypes, parameterizeClassNames, ergonomicShortcuts, config);
        } else if(schema instanceof HollowListSchema) {
            return new HollowListJavaGenerator(packageName, apiClassname, (HollowListSchema) schema, parameterizedTypes, parameterizeClassNames, config);
        } else if(schema instanceof HollowSetSchema) {
            return new HollowSetJavaGenerator(packageName, apiClassname, (HollowSetSchema) schema, parameterizedTypes, parameterizeClassNames, config);
        } else if(schema instanceof HollowMapSchema) {
            return new HollowMapJavaGenerator(packageName, apiClassname, (HollowMapSchema) schema, dataset, parameterizedTypes, parameterizeClassNames, config);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    protected HollowFactoryJavaGenerator getHollowFactoryGenerator(HollowSchema schema) {
        return new HollowFactoryJavaGenerator(packageName, schema, config);
    }

    public static class Builder extends AbstractHollowAPIGeneratorBuilder<Builder, HollowAPIGenerator> {
        @Override
        protected HollowAPIGenerator  instantiateGenerator() {
            return new HollowAPIGenerator(apiClassname, packageName, dataset, parameterizedTypes, parameterizeAllClassnames, useErgonomicShortcuts);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

    public class CodeGeneratorConfig {
        private String classPostfix = "Hollow";
        private String getterPrefix = "_";
        private boolean usePackageGrouping = false;
        private boolean useAggressiveSubstitutions = false;
        private boolean useBooleanFieldErgonomics = false;
        private boolean reservePrimaryKeyIndexForTypeWithPrimaryKey = false;
        private boolean useHollowPrimitiveTypes = false;
        private boolean restrictApiToFieldType = false;

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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((classPostfix == null) ? 0 : classPostfix.hashCode());
            result = prime * result + ((getterPrefix == null) ? 0 : getterPrefix.hashCode());
            result = prime * result + (reservePrimaryKeyIndexForTypeWithPrimaryKey ? 1231 : 1237);
            result = prime * result + (restrictApiToFieldType ? 1231 : 1237);
            result = prime * result + (useAggressiveSubstitutions ? 1231 : 1237);
            result = prime * result + (useBooleanFieldErgonomics ? 1231 : 1237);
            result = prime * result + (useHollowPrimitiveTypes ? 1231 : 1237);
            result = prime * result + (usePackageGrouping ? 1231 : 1237);
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
            if (!getOuterType().equals(other.getOuterType()))
                return false;
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
            if (useHollowPrimitiveTypes != other.useHollowPrimitiveTypes)
                return false;
            if (usePackageGrouping != other.usePackageGrouping)
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("GeneratorConfig [classPostfix=");
            builder.append(classPostfix);
            builder.append(", getterPrefix=");
            builder.append(getterPrefix);
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
            builder.append("]");
            return builder.toString();
        }

        private HollowAPIGenerator getOuterType() {
            return HollowAPIGenerator.this;
        }
    }
}