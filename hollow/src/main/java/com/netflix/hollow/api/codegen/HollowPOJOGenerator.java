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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.isPrimitiveType;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to generate java code which defines POJOs, which can in turn be used to populate a 
 * {@link HollowWriteStateEngine} via a {@link HollowObjectMapper}
 * 
 * The generated java code is based on a data model (defined by a set of {@link HollowSchema}).
 *
 * You may also run the main() method directly.
 */
public class HollowPOJOGenerator {
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
         * Sets the path the files with be generated in.
         */
        pathToGeneratedFiles,
        /**
         * Sets the package name for the generated files.
         */
        packageName,
        /**
         * Sets the suffix for the generated POJO class names.
         */
        pojoClassNameSuffix;
    }

    private final String packageName;
    private final String pojoClassNameSuffix;
    private final HollowDataset dataset;

    public HollowPOJOGenerator(String packageName, String pojoClassNameSuffix, HollowDataset dataset) {
        this.packageName = packageName;
        this.pojoClassNameSuffix = pojoClassNameSuffix;
        this.dataset = dataset;
    }

    /**
     * Usage: java HollowPOJOGenerator --argName1=argValue1 --argName2==argValue2. See {@link GeneratorArguments}
     * for available arguments.
     * @param args the arguments
     * @throws IOException if the POJOs cannot be created
     * @throws ClassNotFoundException if the class for a data type cannot be loaded
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if(args.length == 0) {
            System.out.println("Usage:\n"
                    + "java " + HollowPOJOGenerator.class.getName() + " --arg1=value1 --arg2=value2\n"
                    + "see " + GeneratorArguments.class.getName() + " for available arguments.");
            return;
        }
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        String packageName = null;
        String pojoClassNameSuffix = null;
        String pathToGeneratedFiles = null;
        HollowObjectMapper mapper = new HollowObjectMapper(engine);
        ArgumentParser<GeneratorArguments> argumentParser = new ArgumentParser(GeneratorArguments.class, args);
        for(ArgumentParser<GeneratorArguments>.ParsedArgument arg : argumentParser.getParsedArguments()) {
            switch(arg.getKey()) {
                case addToDataModel:
                    mapper.initializeTypeState(HollowPOJOGenerator.class.getClassLoader().loadClass(arg.getValue()));
                    break;
                case addSchemaFileToDataModel:
                    HollowWriteStateCreator.readSchemaFileIntoWriteState(arg.getValue(), engine);
                    break;
                case pathToGeneratedFiles:
                    pathToGeneratedFiles = arg.getValue();
                    break;
                case packageName:
                    packageName = arg.getValue();
                    break;
                case pojoClassNameSuffix:
                    pojoClassNameSuffix = arg.getValue();
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled argument " + arg.getKey());
            }
        }
        new HollowPOJOGenerator(packageName, pojoClassNameSuffix, engine).generateFiles(pathToGeneratedFiles);
    }

    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    public void generateFiles(File directory) throws IOException {
        Path destinationPath = directory.toPath();
        Path packagePath = Paths.get(packageName.replace(".", File.separator));
        if(!destinationPath.toAbsolutePath().endsWith(packagePath)) {
            destinationPath = destinationPath.resolve(packagePath);
        }
        directory = destinationPath.toFile();
        if(!directory.exists()) directory.mkdirs();
        for(HollowSchema schema : dataset.getSchemas()) {
            if(schema instanceof HollowObjectSchema && !isPrimitiveType(schema.getName())) {
                HollowPOJOClassGenerator generator = new HollowPOJOClassGenerator(dataset, (HollowObjectSchema) schema,
                        packageName, pojoClassNameSuffix);
                FileWriter writer = new FileWriter(new File(directory, generator.getClassName() + ".java"));
                writer.write(generator.generate());
                writer.close();
            }
        }
    }
}
