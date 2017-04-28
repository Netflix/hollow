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

import com.netflix.hollow.api.custom.HollowAPI;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.api.codegen.api.TypeAPIListJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPIMapJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPIObjectJavaGenerator;
import com.netflix.hollow.api.codegen.api.TypeAPISetJavaGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateCachedImplGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateInterfaceGenerator;
import com.netflix.hollow.api.codegen.delegate.HollowObjectDelegateLookupImplGenerator;
import com.netflix.hollow.api.codegen.objects.HollowFactoryJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowListJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowMapJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowObjectJavaGenerator;
import com.netflix.hollow.api.codegen.objects.HollowSetJavaGenerator;
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
    
    private String classPostfix = "Hollow";
    private String getterPrefix = "_";

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), false);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeAllClassNames if true, all methods which return a Hollow Object will be parameterized.  This is useful when 
     *                               alternate implementations are desired for some types.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, boolean parameterizeAllClassNames) {
        this(apiClassname, packageName, dataset, Collections.<String>emptySet(), parameterizeAllClassNames);
    }

    /**
     * @param apiClassname the class name of the generated implementation of {@link HollowAPI}
     * @param packageName the package name under which all generated classes will be placed
     * @param dataset a HollowStateEngine containing the schemas which define the data model.
     * @param parameterizeSpecificTypeNames methods with matching names which return a Hollow Object will be parameterized.  This is useful when 
     *                               alternate implementations are desired for some types.
     */
    public HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizeSpecificTypeNames) {
        this(apiClassname, packageName, dataset, parameterizeSpecificTypeNames, false);
    }
    
    
    private HollowAPIGenerator(String apiClassname, String packageName, HollowDataset dataset, Set<String> parameterizedTypes, boolean parameterizeAllClassNames) {
        this.apiClassname = apiClassname;
        this.packageName = packageName;
        this.dataset = dataset;
        this.parameterizedTypes = parameterizedTypes;
        this.parameterizeClassNames = parameterizeAllClassNames;
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

    
    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    public void generateFiles(File directory) throws IOException {
        directory.mkdirs();
        
        HollowAPIClassJavaGenerator apiClassGenerator = new HollowAPIClassJavaGenerator(packageName, apiClassname, dataset, parameterizeClassNames, classPostfix);
        HollowAPIFactoryJavaGenerator apiFactoryGenerator = new HollowAPIFactoryJavaGenerator(packageName, apiClassname);

        generateFile(directory, apiClassGenerator);
        generateFile(directory, apiFactoryGenerator);

        generateFilesForHollowSchemas(directory);
    }

    private void generateFilesForHollowSchemas(File directory) throws IOException {
        for(HollowSchema schema : dataset.getSchemas()) {
            generateFile(directory, getStaticAPIGenerator(schema));
            generateFile(directory, getHollowObjectGenerator(schema));
            generateFile(directory, getHollowFactoryGenerator(schema));

            if(schema instanceof HollowObjectSchema) {
                generateFile(directory, new HollowObjectDelegateInterfaceGenerator(packageName, (HollowObjectSchema)schema));
                generateFile(directory, new HollowObjectDelegateCachedImplGenerator(packageName, (HollowObjectSchema)schema));
                generateFile(directory, new HollowObjectDelegateLookupImplGenerator(packageName, (HollowObjectSchema)schema));
            }
        }
    }

    private void generateFile(File directory, HollowJavaFileGenerator generator) throws IOException {
        FileWriter writer = new FileWriter(new File(directory, generator.getClassName() + ".java"));
        writer.write(generator.generate());
        writer.close();
    }

    private HollowJavaFileGenerator getStaticAPIGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new TypeAPIObjectJavaGenerator(apiClassname, packageName, (HollowObjectSchema) schema);
        } else if(schema instanceof HollowListSchema) {
            return new TypeAPIListJavaGenerator(apiClassname, packageName, (HollowListSchema)schema);
        } else if(schema instanceof HollowSetSchema) {
            return new TypeAPISetJavaGenerator(apiClassname, packageName, (HollowSetSchema)schema);
        } else if(schema instanceof HollowMapSchema) {
            return new TypeAPIMapJavaGenerator(apiClassname, packageName, (HollowMapSchema)schema);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    private HollowJavaFileGenerator getHollowObjectGenerator(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            return new HollowObjectJavaGenerator(packageName, apiClassname, (HollowObjectSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix, getterPrefix);
        } else if(schema instanceof HollowListSchema) {
            return new HollowListJavaGenerator(packageName, apiClassname, (HollowListSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix);
        } else if(schema instanceof HollowSetSchema) {
            return new HollowSetJavaGenerator(packageName, apiClassname, (HollowSetSchema) schema, parameterizedTypes, parameterizeClassNames, classPostfix);
        } else if(schema instanceof HollowMapSchema) {
            return new HollowMapJavaGenerator(packageName, apiClassname, (HollowMapSchema) schema, dataset, parameterizedTypes, parameterizeClassNames, classPostfix);
        }

        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getName() + "?");
    }

    private HollowFactoryJavaGenerator getHollowFactoryGenerator(HollowSchema schema) {
        return new HollowFactoryJavaGenerator(packageName, schema, classPostfix);
    }

}
