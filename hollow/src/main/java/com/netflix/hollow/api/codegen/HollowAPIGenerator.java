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
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

/**
 * This class is used to generate java code which defines an implementation of a {@link HollowAPI}.
 *
 * The generated java code is based on a data model (defined by a set of {@link HollowSchema}), and will
 * contain convenience methods for traversing a dataset, based on the specific fields in the data model.
 *
 * You may also run the main() method directly.
 */
public class HollowAPIGenerator {
    /**
     * An enumeration of possible arguments to the code generator when being called via the main
     * function. Not expected to be used outside the library itself, except for documentation
     * purposes.
     * Unless otherwise noted, having repeated parameters results in the previous value being
     * overwritten.
     */
    public enum GeneratorArguments {
        /**
         * Add a class to the data model. Takes the fully qualified class name. This class must be
         * available on the classpath. Having multiple of this parameter results in multiple classes
         * being added to the data model.
         */
        addToDataModel,
        /**
         * Add schema from a schema file to the data model. The schema file must be available on the
         * classpath. Having multiple of this parameter results in multiple schemas being added to
         * the data model.
         */
        addSchemaFileToDataModel,
        /**
         * Sets the API class name.
         */
        apiClassName,
        /**
         * Sets the class postfix for the generated types.
         */
        classPostfix,
        /**
         * Sets the getter prefix to the provided string.
         */
        getterPrefix,
        /**
         * Sets the package name for the generated files.
         */
        packageName,
        /**
         * Sets the path the files with be generated in.
         */
        pathToGeneratedFiles,
        /**
         * Parameterizes all methods that return a HollowObject.
         */
        parameterizeAllClassNames;
    }

    protected final String apiClassname;
    protected final String packageName;
    protected final Path destinationPath;
    protected final HollowDataset dataset;
    protected final Set<String> parameterizedTypes;
    protected final boolean parameterizeClassNames;
    protected final boolean hasCollectionsInDataSet;
    protected final HollowErgonomicAPIShortcuts ergonomicShortcuts;

    protected CodeGeneratorConfig config = new CodeGeneratorConfig("Hollow", "_"); // NOTE: to be backwards compatible

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     *
     * @deprecated use {@link #HollowAPIGenerator(String, String, HollowDataset, Path)} and use {@link #generateSourceFiles()}
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), false, false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param destinationPath the directory under which the source files will be generated
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Path destinationPath) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), false, false, destinationPath);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeAllClassNames if true, all methods which return a Hollow Object will be parameterized.  This is useful when
     *                               alternate implementations are desired for some types.
     *
     * @deprecated use {@link #HollowAPIGenerator(String, String, HollowDataset, boolean, Path)} and use {@link #generateSourceFiles()}
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, boolean parameterizeAllClassNames) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), parameterizeAllClassNames, false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeAllClassNames if true, all methods which return a Hollow Object will be parameterized.  This is useful when
     *                               alternate implementations are desired for some types.
     * @param destinationPath the directory under which the source files will be generated
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, boolean parameterizeAllClassNames, Path destinationPath) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), parameterizeAllClassNames, false, destinationPath);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeSpecificTypeNames methods with matching names which return a Hollow Object will be parameterized.  This is useful when
     *                               alternate implementations are desired for some types.
     *
     * @deprecated use {@link #HollowAPIGenerator(String, String, HollowDataset, Set, Path)} and use {@link #generateSourceFiles()}
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizeSpecificTypeNames) {
        this(apiClassname, packageName, dataset, parameterizeSpecificTypeNames, false, false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizedTypes the parameterized types
     * @param destinationPath the directory under which the source files will be generated
     */
    public HollowAPIGenerator(String apiClassname,
                              String packageName,
                              HollowDataset dataset,
                              Set<String> parameterizedTypes,
                              Path destinationPath) {
        this(apiClassname, packageName, dataset, parameterizedTypes, false, false, destinationPath);
    }

    /**
     * @param apiClassname the api class name
     * @param packageName the api package name
     * @param dataset the data set
     * @param parameterizedTypes the set of parameterized types
     * @param parameterizeAllClassNames true if class names should be parameterized
     * @param useErgonomicShortcuts true if ergonomic shortcuts should be used
     * @deprecated construct with a {@code destinationPath} and use {@link #generateSourceFiles()}
     */
    protected HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizedTypes, boolean parameterizeAllClassNames, boolean useErgonomicShortcuts) {
        this(apiClassname, packageName, dataset, parameterizedTypes, false, false, null);
    }

    protected HollowAPIGenerator(String apiClassname,
                                 String packageName,
                                 HollowDataset dataset,
                                 Set<String> parameterizedTypes,
                                 boolean parameterizeAllClassNames,
                                 boolean useErgonomicShortcuts,
                                 Path destinationPath) {
        this.apiClassname = apiClassname;
        this.packageName = packageName;
        this.dataset = dataset;
        this.hasCollectionsInDataSet = hasCollectionsInDataSet(dataset);
        this.parameterizedTypes = parameterizedTypes;
        this.parameterizeClassNames = parameterizeAllClassNames;
        this.ergonomicShortcuts = useErgonomicShortcuts ? new HollowErgonomicAPIShortcuts(dataset) : HollowErgonomicAPIShortcuts.NO_SHORTCUTS;

        if (destinationPath != null && packageName != null && !packageName.trim().isEmpty()) {
            Path packagePath = Paths.get(packageName.replace(".", File.separator));
            if (!destinationPath.toAbsolutePath().endsWith(packagePath)) {
                destinationPath = destinationPath.resolve(packagePath);
            }
        }
        this.destinationPath = destinationPath;
    }

    /**
     * Usage: java HollowAPIGenerator --argName1=argValue1 --argName2==argValue2. See {@link GeneratorArguments} for
     * available arguments.
     * @param args the arguments
     * @throws IOException if a schema could not be read or the data model written
     * @throws ClassNotFoundException if a class of a data model cannot be loaded
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("Usage:\n"
                    + "java " + HollowAPIGenerator.class.getName() + " --arg1=value1 --arg2=value2\n"
                    + "see " + GeneratorArguments.class.getName() + " for available arguments.");
            return;
        }
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        HollowAPIGenerator.Builder builder = new HollowAPIGenerator.Builder();
        HollowObjectMapper mapper = new HollowObjectMapper(engine);
        ArgumentParser<GeneratorArguments> argumentParser = new ArgumentParser(GeneratorArguments.class, args);
        for (ArgumentParser<GeneratorArguments>.ParsedArgument arg : argumentParser.getParsedArguments()) {
            switch (arg.getKey()) {
                case addToDataModel:
                    mapper.initializeTypeState(HollowAPIGenerator.class.getClassLoader().loadClass(arg.getValue()));
                    break;
                case addSchemaFileToDataModel:
                    HollowWriteStateCreator.readSchemaFileIntoWriteState(arg.getValue(), engine);
                    break;
                case apiClassName:
                    builder.withAPIClassname(arg.getValue());
                    break;
                case classPostfix:
                    builder.withClassPostfix(arg.getValue());
                    break;
                case getterPrefix:
                    builder.withGetterPrefix(arg.getValue());
                    break;
                case packageName:
                    builder.withPackageName(arg.getValue());
                    break;
                case pathToGeneratedFiles:
                    builder.withDestination(arg.getValue());
                    break;
                case parameterizeAllClassNames:
                    builder.withParameterizeAllClassNames(Boolean.valueOf(arg.getValue()));
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled argument " + arg.getKey());
            }
        }
        builder.withDataModel(engine).build().generateSourceFiles();
    }

    /**
     * Determines whether DataSet contains any collections schema
     * @param dataset the data set
     * @return {@code true} if the data set contains any collections schema
     */
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

    /**
     * Set the CodeGeneratorConfig
     * @param config the configuration
     */
    protected void setCodeGeneratorConfig(CodeGeneratorConfig config) {
        this.config = config;
    }

    /**
     * Use this method to override the default postfix "Hollow" for all generated Hollow object classes.
     * @param classPostfix the postfix for all generated Hollow object classes
     */
    public void setClassPostfix(String classPostfix) {
        config.setClassPostfix(classPostfix);
    }

    /**
     * Use this method to override the default prefix "_" for all getters on all generated Hollow object classes.
     * @param getterPrefix the prefix for all generated getters
     */
    public void setGetterPrefix(String getterPrefix) {
        config.setGetterPrefix(getterPrefix);
    }

    /**
     * Use this method to override generated classnames for type names corresponding to any class in the java.lang package.
     *
     * Defaults to false, which overrides only type names corresponding to a few select classes in java.lang.
     *
     * @param useAggressiveSubstitutions true if applied.
     */
    public void setUseAggressiveSubstitutions(boolean useAggressiveSubstitutions) {
        config.setUseAggressiveSubstitutions(useAggressiveSubstitutions);
    }

    /**
     * Use this method to specify to use new boolean field ergonomics for generated API
     *
     * Defaults to false to be backwards compatible
     *
     * @param useBooleanFieldErgonomics true if applied.
     */
    public void setUseBooleanFieldErgonomics(boolean useBooleanFieldErgonomics) {
        config.setUseBooleanFieldErgonomics(useBooleanFieldErgonomics);
    }

    /**
     * Use this method to specify to use sub packages in generated code instead of single package
     *
     * Defaults to false to be backwards compatible
     *
     * @param usePackageGrouping true if applied.
     */
    public void setUsePackageGrouping(boolean usePackageGrouping) {
        config.setUsePackageGrouping(usePackageGrouping );
    }

    /**
     * Use this method to specify to only generate PrimaryKeyIndex for Types that has PrimaryKey defined
     *
     * Defaults to false to be backwards compatible
     *
     * @param reservePrimaryKeyIndexForTypeWithPrimaryKey true if applied.
     */
    public void reservePrimaryKeyIndexForTypeWithPrimaryKey(boolean reservePrimaryKeyIndexForTypeWithPrimaryKey) {
        config.setReservePrimaryKeyIndexForTypeWithPrimaryKey(reservePrimaryKeyIndexForTypeWithPrimaryKey);
    }

    /**
     * Use this method to specify to use Hollow Primitive Types instead of generating them per project
     *
     * Defaults to false to be backwards compatible
     *
     * @param useHollowPrimitiveTypes true if applied.
     */
    public void setUseHollowPrimitiveTypes(boolean useHollowPrimitiveTypes) {
        config.setUseHollowPrimitiveTypes(useHollowPrimitiveTypes);
    }

    /**
     * If setRestrictApiToFieldType is true, api code only generates {@code get<FieldName>} with return type as per schema
     *
     * Defaults to false to be backwards compatible
     *
     * @param restrictApiToFieldType true if applied.
     */
    public void setRestrictApiToFieldType(boolean restrictApiToFieldType) {
        config.setRestrictApiToFieldType(restrictApiToFieldType);
    }

    /**
     * Generate all files under {@code destinationPath}
     *
     * @throws IOException if the files cannot be generated
     */
    public void generateSourceFiles() throws IOException {
        generateFiles(destinationPath.toFile());
    }

    /**
     * Generate files under the specified directory
     *
     * @param directory the directory under which to generate files
     * @throws IOException if the files cannot be generated
     * @deprecated construct {@code HollowAPIGenerator} with a {@code destinationPath} then call {@link #generateSourceFiles()}
     */
    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    /**
     * Generate files under the specified directory
     *
     * @param directory the directory under which to generate files
     * @throws IOException if the files cannot be generated
     * @deprecated construct {@code HollowAPIGenerator} with a {@code destinationPath} then call {@link #generateSourceFiles()}
     */
    public void generateFiles(File directory) throws IOException {
        if (packageName != null && !packageName.trim().isEmpty()) {
            String packageDir = packageName.replace(".", File.separator);
            if (!directory.getAbsolutePath().endsWith(packageDir)) {
                directory = new File(directory, packageDir);
            }
        }
        directory.mkdirs();

        HollowAPIClassJavaGenerator apiClassGenerator = new HollowAPIClassJavaGenerator(packageName, apiClassname,
                dataset, parameterizeClassNames, config);
        HollowAPIFactoryJavaGenerator apiFactoryGenerator = new HollowAPIFactoryJavaGenerator(packageName,
                apiClassname, dataset, config);

        HollowHashIndexGenerator hashIndexGenerator = new HollowHashIndexGenerator(packageName, apiClassname, dataset, config);

        generateFile(directory, apiClassGenerator);
        generateFile(directory, apiFactoryGenerator);
        generateFile(directory, hashIndexGenerator);

        generateFilesForHollowSchemas(directory);
    }

    /**
     * Generate files based on dataset schemas under {@code destinationPath}
     *
     * @throws IOException if the files cannot be generated
     */
    protected void generateSourceFilesForHollowSchemas() throws IOException {
        this.generateFilesForHollowSchemas(destinationPath.toFile());
    }

    /**
     * Generate files based on dataset schemas under the specified directory
     *
     * @param directory the directory under which to generate files
     * @throws IOException if the files cannot be generated
     * @deprecated construct {@code HollowAPIGenerator} with a {@code destinationPath} then call {@link #generateSourceFilesForHollowSchemas()}
     */
    protected void generateFilesForHollowSchemas(File directory) throws IOException {
        for(HollowSchema schema : dataset.getSchemas()) {
            String type = schema.getName();
            if (config.isUseHollowPrimitiveTypes() && HollowCodeGenerationUtils.isPrimitiveType(type)) continue; // skip if using hollow primitive type

            generateFile(directory, getStaticAPIGenerator(schema));
            generateFile(directory, getHollowObjectGenerator(schema));
            generateFile(directory, getHollowFactoryGenerator(schema));

            if(schema.getSchemaType() == SchemaType.OBJECT) {
                HollowObjectSchema objSchema = (HollowObjectSchema)schema;
                generateFile(directory, new HollowObjectDelegateInterfaceGenerator(packageName, objSchema,
                        ergonomicShortcuts, dataset, config));
                generateFile(directory, new HollowObjectDelegateCachedImplGenerator(packageName, objSchema,
                        ergonomicShortcuts, dataset, config));
                generateFile(directory, new HollowObjectDelegateLookupImplGenerator(packageName, objSchema,
                        ergonomicShortcuts, dataset, config));

                generateFile(directory, new HollowDataAccessorGenerator(packageName, apiClassname, objSchema,
                        dataset, config));
                if (!config.isReservePrimaryKeyIndexForTypeWithPrimaryKey()) {
                    generateFile(directory, new LegacyHollowPrimaryKeyIndexGenerator(packageName, apiClassname,
                            objSchema, dataset, config));
                } else if ((objSchema).getPrimaryKey() != null) {
                    generateFile(directory, new HollowPrimaryKeyIndexGenerator(dataset, packageName, apiClassname,
                            objSchema, config));
                    generateFile(directory, new HollowUniqueKeyIndexGenerator(packageName, apiClassname, objSchema,
                            dataset, config));
                }
            }
        }
    }

    protected void generateSourceFile(HollowJavaFileGenerator generator) throws IOException {
        this.generateFile(destinationPath.toFile(), generator);
    }

    /**
     * @param directory the directory under which to generate a file
     * @param generator the file generator
     * @throws IOException if the file cannot be generated
     * @deprecated construct {@code HollowAPIGenerator} with a {@code destinationPath} then call {@link #generateSourceFile(HollowJavaFileGenerator)}
     */
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
            return new TypeAPIObjectJavaGenerator(apiClassname, packageName, (HollowObjectSchema) schema, dataset, config);
        } else if(schema instanceof HollowListSchema) {
            return new TypeAPIListJavaGenerator(apiClassname, packageName, (HollowListSchema)schema, dataset, config);
        } else if(schema instanceof HollowSetSchema) {
            return new TypeAPISetJavaGenerator(apiClassname, packageName, (HollowSetSchema)schema, dataset, config);
        } else if(schema instanceof HollowMapSchema) {
            return new TypeAPIMapJavaGenerator(apiClassname, packageName, (HollowMapSchema)schema, dataset, config);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    protected HollowJavaFileGenerator getHollowObjectGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new HollowObjectJavaGenerator(packageName, apiClassname, (HollowObjectSchema) schema,
                    parameterizedTypes, parameterizeClassNames, ergonomicShortcuts, dataset, config);
        } else if(schema instanceof HollowListSchema) {
            return new HollowListJavaGenerator(packageName, apiClassname, (HollowListSchema) schema,
                    parameterizedTypes, parameterizeClassNames, dataset, config);
        } else if(schema instanceof HollowSetSchema) {
            return new HollowSetJavaGenerator(packageName, apiClassname, (HollowSetSchema) schema,
                    parameterizedTypes, parameterizeClassNames, dataset, config);
        } else if(schema instanceof HollowMapSchema) {
            return new HollowMapJavaGenerator(packageName, apiClassname, (HollowMapSchema) schema, dataset, parameterizedTypes, parameterizeClassNames, config);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    protected HollowFactoryJavaGenerator getHollowFactoryGenerator(HollowSchema schema) {
        return new HollowFactoryJavaGenerator(packageName, schema, dataset, config);
    }

    public static class Builder extends AbstractHollowAPIGeneratorBuilder<Builder, HollowAPIGenerator> {
        @Override
        protected HollowAPIGenerator instantiateGenerator() {
            return new HollowAPIGenerator(apiClassname, packageName, dataset, parameterizedTypes, parameterizeAllClassNames, useErgonomicShortcuts, destinationPath);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }
}
