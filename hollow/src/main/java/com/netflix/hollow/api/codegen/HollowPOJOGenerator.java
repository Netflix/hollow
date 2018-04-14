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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.isPrimitiveType;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to generate java code which defines POJOs, which can in turn be used to populate a 
 * {@link HollowWriteStateEngine} via a {@link HollowObjectMapper}
 * 
 * The generated java code is based on a data model (defined by a set of {@link HollowSchema}).
 */
public class HollowPOJOGenerator {

    private final String packageName;
    private final String pojoClassNameSuffix;
    private final HollowDataset dataset;

    public HollowPOJOGenerator(String packageName, String pojoClassNameSuffix, HollowDataset dataset) {
        this.packageName = packageName;
        this.pojoClassNameSuffix = pojoClassNameSuffix;
        this.dataset = dataset;
    }

    public void generateFiles(String directory) throws IOException {
        generateFiles(new File(directory));
    }

    public void generateFiles(File directory) throws IOException {
        for (HollowSchema schema : dataset.getSchemas()) {
            if (schema instanceof HollowObjectSchema && !isPrimitiveType(schema.getName())) {
                HollowPOJOClassGenerator generator = new HollowPOJOClassGenerator(dataset, (HollowObjectSchema) schema,
                        packageName, pojoClassNameSuffix);
                FileWriter writer = new FileWriter(new File(directory, generator.getClassName() + ".java"));
                writer.write(generator.generate());
                writer.close();
            }
        }
    }
}
