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

    private final String apiClassname;
    private final String packageName;
    private final HollowDataset dataset;
    private final Set<String> parameterizedTypes;
    private final boolean parameterizeClassNames;
    private final boolean hasCollectionsInDataSet;
    private final HollowErgonomicAPIShortcuts ergonomicShortcuts;


    private String classPostfix = "Hollow";
    private String getterPrefix = "_";
    private boolean usePackageGrouping = false;
    private boolean useAggressiveSubstitutions = false;
    private boolean useBooleanFieldErgonomics = false;
    private boolean reservePrimaryKeyIndexForTypeWithPrimaryKey = false;
    private boolean useHollowPrimitiveTypes = false;
    private boolean restrictApiToFieldType = false;

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

    private HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizedTypes, boolean parameterizeAllClassNames, boolean useErgonomicShortcuts) {
        this.apiClassname = apiClassname;
        this.packageName = packageName;
        this.dataset = dataset;
        this.hasCollectionsInDataSet = hasCollectionsInDataSet(dataset);
        this.parameterizedTypes = parameterizedTypes;
        this.parameterizeClassNames = parameterizeAllClassNames;
        this.ergonomicShortcuts = useErgonomicShortcuts ? new HollowErgonomicAPIShortcuts(dataset) : HollowErgonomicAPIShortcuts.NO_SHORTCUTS;
    }

    private static boolean hasCollectionsInDataSet(HollowDataset dataset) {
        for(HollowSchema schema : dataset.getSchemas()) {
            if ((schema instanceof HollowListSchema) ||
                    (schema instanceof HollowSetSchema) ||
                    (schema instanceof HollowMapSchema)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Use this method to override the default postfix "Hollow" for all generated Hollow object classes.
     */
    public void setClassPostfix(String classPostfix) {
        this.classPostfix = classPostfix;
    }

    /**
     * Use this method to override the default prefix "_" for all getters on all generated Hollow object classes.
     */
    public void setGetterPrefix(String getterPrefix) {
        this.getterPrefix = getterPrefix;
    }

    /**
     * Use this method to override generated classnames for type names corresponding to any class in the java.lang package.
     *
     * Defaults to false, which overrides only type names corresponding to a few select classes in java.lang.
     */
    public void setUseAggressiveSubstitutions(boolean useAggressiveSubstitutions) {
        this.useAggressiveSubstitutions = useAggressiveSubstitutions;
    }

    /**
     * Use this method to specify to use new boolean field ergonomics for generated API
     *
     * Defaults to false to be backwards compatible
     */
    public void setUseBooleanFieldErgonomics(boolean useBooleanFieldErgonomics) {
        this.useBooleanFieldErgonomics = useBooleanFieldErgonomics;
    }

    /**
     * Use this method to specify to use sub packages in generated code instead of single package
     *
     * Defaults to false to be backwards compatible
     */
    public void setUsePackageGrouping(boolean usePackageGrouping) {
        this.usePackageGrouping = usePackageGrouping;
    }

    /**
     * Use this method to specify to only generate PrimaryKeyIndex for Types that has PrimaryKey defined
     *
     * Defaults to false to be backwards compatible
     */
    public void reservePrimaryKeyIndexForTypeWithPrimaryKey(boolean reservePrimaryKeyIndexForTypeWithPrimaryKey) {
        this.reservePrimaryKeyIndexForTypeWithPrimaryKey = reservePrimaryKeyIndexForTypeWithPrimaryKey;
    }

    /**
     * Use this method to specify to use Hollow Primitive Types instead of generating them per project
     *
     * Defaults to false to be backwards compatible
     */
    public void setUseHollowPrimitiveTypes(boolean useHollowPrimitiveTypes) {
        this.useHollowPrimitiveTypes = useHollowPrimitiveTypes;
    }

    /**
     * If setRestrictApiToFieldType is true, api code only generates get<FieldName> with return type as per schema  
     * 
     * Defaults to false to be backwards compatible
     */
    public void setRestrictApiToFieldType(boolean restrictApiToFieldType) {
        this.restrictApiToFieldType = restrictApiToFieldType;
    }
    
    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    public void generateFiles(File directory) throws IOException {
        directory.mkdirs();

        HollowAPIClassJavaGenerator apiClassGenerator = new HollowAPIClassJavaGenerator(packageName, apiClassname, dataset, parameterizeClassNames, classPostfix, useAggressiveSubstitutions, usePackageGrouping, useHollowPrimitiveTypes);
        HollowAPIFactoryJavaGenerator apiFactoryGenerator = new HollowAPIFactoryJavaGenerator(packageName, apiClassname, usePackageGrouping, useHollowPrimitiveTypes);

        boolean isListenToDataRefreah = !reservePrimaryKeyIndexForTypeWithPrimaryKey; // NOTE: to be backwards compatible
        HollowHashIndexGenerator hashIndexGenerator = new HollowHashIndexGenerator(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, dataset, usePackageGrouping, isListenToDataRefreah, useHollowPrimitiveTypes);

        generateFile(directory, apiClassGenerator);
        generateFile(directory, apiFactoryGenerator);
        generateFile(directory, hashIndexGenerator);

        generateFilesForHollowSchemas(directory);
    }

    private void generateFilesForHollowSchemas(File directory) throws IOException {
        for(HollowSchema schema : dataset.getSchemas()) {
            String type = schema.getName();
            if (useHollowPrimitiveTypes && HollowCodeGenerationUtils.isPrimitiveType(type)) continue; // skip if using hollow primitive type

            generateFile(directory, getStaticAPIGenerator(schema));
            generateFile(directory, getHollowObjectGenerator(schema));
            generateFile(directory, getHollowFactoryGenerator(schema));

            if(schema.getSchemaType() == SchemaType.OBJECT) {
                generateFile(directory, new HollowObjectDelegateInterfaceGenerator(packageName, (HollowObjectSchema)schema, ergonomicShortcuts, usePackageGrouping, useHollowPrimitiveTypes, restrictApiToFieldType));
                generateFile(directory, new HollowObjectDelegateCachedImplGenerator(packageName, (HollowObjectSchema)schema, ergonomicShortcuts, usePackageGrouping, useHollowPrimitiveTypes, restrictApiToFieldType));
                generateFile(directory, new HollowObjectDelegateLookupImplGenerator(packageName, (HollowObjectSchema)schema, ergonomicShortcuts, usePackageGrouping, useHollowPrimitiveTypes, restrictApiToFieldType));

                generateFile(directory, new HollowDataAccessorGenerator(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, (HollowObjectSchema) schema, usePackageGrouping, useHollowPrimitiveTypes));
                if (!reservePrimaryKeyIndexForTypeWithPrimaryKey) {
                    generateFile(directory, new LegacyHollowPrimaryKeyIndexGenerator(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, (HollowObjectSchema) schema, usePackageGrouping, useHollowPrimitiveTypes));
                } else if (((HollowObjectSchema) schema).getPrimaryKey() != null) {
                    generateFile(directory, new HollowPrimaryKeyIndexGenerator(dataset, packageName, apiClassname, classPostfix, useAggressiveSubstitutions, (HollowObjectSchema) schema, usePackageGrouping, useHollowPrimitiveTypes));
                    generateFile(directory, new HollowUniqueKeyIndexGenerator(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, (HollowObjectSchema) schema, usePackageGrouping, useHollowPrimitiveTypes));
                }
            }
        }
    }

    private void generateFile(File directory, HollowJavaFileGenerator generator) throws IOException {
        // create sub folder if not using default package and sub packages are enabled
        if ((packageName!=null && !packageName.trim().isEmpty()) && usePackageGrouping && (generator instanceof HollowConsumerJavaFileGenerator)) {
            HollowConsumerJavaFileGenerator consumerCodeGenerator = (HollowConsumerJavaFileGenerator)generator;
            if (hasCollectionsInDataSet) consumerCodeGenerator.useCollectionsImport();
            directory = new File(directory, consumerCodeGenerator.getSubPackageName());
        }
        if (!directory.exists()) directory.mkdirs();

        FileWriter writer = new FileWriter(new File(directory, generator.getClassName() + ".java"));
        writer.write(generator.generate());
        writer.close();
    }

    private HollowJavaFileGenerator getStaticAPIGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new TypeAPIObjectJavaGenerator(apiClassname, packageName, (HollowObjectSchema) schema, usePackageGrouping, useHollowPrimitiveTypes);
        } else if(schema instanceof HollowListSchema) {
            return new TypeAPIListJavaGenerator(apiClassname, packageName, (HollowListSchema)schema, usePackageGrouping, useHollowPrimitiveTypes);
        } else if(schema instanceof HollowSetSchema) {
            return new TypeAPISetJavaGenerator(apiClassname, packageName, (HollowSetSchema)schema, usePackageGrouping, useHollowPrimitiveTypes);
        } else if(schema instanceof HollowMapSchema) {
            return new TypeAPIMapJavaGenerator(apiClassname, packageName, (HollowMapSchema)schema, usePackageGrouping, useHollowPrimitiveTypes);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    private HollowJavaFileGenerator getHollowObjectGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new HollowObjectJavaGenerator(packageName, apiClassname, (HollowObjectSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix, getterPrefix, useAggressiveSubstitutions, ergonomicShortcuts, useBooleanFieldErgonomics, usePackageGrouping, useHollowPrimitiveTypes, restrictApiToFieldType);
        } else if(schema instanceof HollowListSchema) {
            return new HollowListJavaGenerator(packageName, apiClassname, (HollowListSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix, useAggressiveSubstitutions, usePackageGrouping, useHollowPrimitiveTypes);
        } else if(schema instanceof HollowSetSchema) {
            return new HollowSetJavaGenerator(packageName, apiClassname, (HollowSetSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix, useAggressiveSubstitutions, usePackageGrouping, useHollowPrimitiveTypes);
        } else if(schema instanceof HollowMapSchema) {
            return new HollowMapJavaGenerator(packageName, apiClassname, (HollowMapSchema) schema, dataset, parameterizedTypes, parameterizeClassNames, classPostfix, useAggressiveSubstitutions, usePackageGrouping, useHollowPrimitiveTypes);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    private HollowFactoryJavaGenerator getHollowFactoryGenerator(HollowSchema schema) {
        return new HollowFactoryJavaGenerator(packageName, schema, classPostfix, useAggressiveSubstitutions, usePackageGrouping, useHollowPrimitiveTypes);
    }

    public static class Builder {
        private String apiClassname;
        private String packageName;
        private HollowDataset dataset;
        private Set<String> parameterizedTypes = Collections.emptySet();
        private boolean parameterizeAllClassnames = false;
        private String classPostfix = "";
        private String getterPrefix = "";
        private boolean useAggressiveSubstitutions = false;
        private boolean useErgonomicShortcuts = false;
        private boolean useBooleanFieldErgonomics = false;
        private boolean reservePrimaryKeyIndexForTypeWithPrimaryKey = false;
        private boolean usePackageGrouping = false;
        private boolean useHollowPrimitiveTypes = false;
        private boolean restrictApiToFieldType = false;

        public Builder withAPIClassname(String apiClassname) {
            this.apiClassname = apiClassname;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withDataModel(HollowDataset dataset) {
            this.dataset = dataset;
            return this;
        }

        public Builder withParameterizedTypes(Set<String> parameterizedTypes) {
            this.parameterizedTypes = parameterizedTypes;
            return this;
        }

        public Builder withParameterizeAllClassNames(boolean parameterizeAllClassnames) {
            this.parameterizeAllClassnames = parameterizeAllClassnames;
            return this;
        }

        public Builder withClassPostfix(String classPostfix) {
            this.classPostfix = classPostfix;
            return this;
        }

        public Builder withGetterPrefix(String getterPrefix) {
            this.getterPrefix = getterPrefix;
            return this;
        }

        public Builder withAggressiveSubstitutions(boolean useAggressiveSubstitutions) {
            this.useAggressiveSubstitutions = useAggressiveSubstitutions;
            return this;
        }

        public Builder withErgonomicShortcuts() {
            this.useErgonomicShortcuts = true;
            return this;
        }

        public Builder withPackageGrouping() {
            this.usePackageGrouping = true;
            return this;
        }

        public Builder withBooleanFieldErgonomics(boolean useBooleanFieldErgonomics) {
            this.useBooleanFieldErgonomics = useBooleanFieldErgonomics;
            return this;
        }

        public Builder reservePrimaryKeyIndexForTypeWithPrimaryKey(boolean reservePrimaryKeyIndexForTypeWithPrimaryKey) {
            this.reservePrimaryKeyIndexForTypeWithPrimaryKey = reservePrimaryKeyIndexForTypeWithPrimaryKey;
            return this;
        }

        /**
         * NOTE: Have to be enabled with withErgonomicShortcuts 
         * @return
         */
        public Builder withRestrictApiToFieldType() {
            restrictApiToFieldType = true;
            return this;
        }

        public Builder withHollowPrimitiveTypes(boolean useHollowPrimitiveTypes) {
            this.useHollowPrimitiveTypes = useHollowPrimitiveTypes;
            return this;
        }

        public HollowAPIGenerator build() {
            if(apiClassname == null)
                throw new IllegalStateException("Please specify an API classname (.withAPIClassname()) before calling .build()");
            if(packageName == null)
                throw new IllegalStateException("Please specify a package name (.withPackageName()) before calling .build()");
            if(dataset == null)
                throw new IllegalStateException("Please specify a data model (.withDataModel()) before calling .build()");

            if(restrictApiToFieldType && !useErgonomicShortcuts) {
                throw new IllegalStateException(" restrictApiToFieldType requires withErgonomicShortcuts");
            }

            HollowAPIGenerator generator = new HollowAPIGenerator(apiClassname, packageName, dataset, parameterizedTypes, parameterizeAllClassnames, useErgonomicShortcuts);
            generator.setClassPostfix(classPostfix);
            generator.setGetterPrefix(getterPrefix);
            generator.setUseAggressiveSubstitutions(useAggressiveSubstitutions);
            generator.setUseBooleanFieldErgonomics(useBooleanFieldErgonomics);
            generator.reservePrimaryKeyIndexForTypeWithPrimaryKey(reservePrimaryKeyIndexForTypeWithPrimaryKey);
            generator.setUsePackageGrouping(usePackageGrouping);
            generator.setUseHollowPrimitiveTypes(useHollowPrimitiveTypes);
            generator.setRestrictApiToFieldType(restrictApiToFieldType);
            return generator;
        }
    }
}