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
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HollowTestDataAPIGenerator {
    
    private HollowDataset dataset;
    private String apiClassname;
    private String packageName;
    private Path destinationPath;

    public static Builder newBuilder() {
        HollowTestDataAPIGenerator gen = new HollowTestDataAPIGenerator();
        return gen.theBuilder();
    }
    
    private Builder theBuilder() {
        return new Builder();
    }

    public class Builder {
        public Builder withDataset(HollowDataset dataset) {
            HollowTestDataAPIGenerator.this.dataset = dataset;
            return this;
        }
        
        public Builder withAPIClassname(String apiClassname) {
            HollowTestDataAPIGenerator.this.apiClassname = apiClassname;
            return this;
        }
        
        public Builder withPackageName(String packageName) {
            HollowTestDataAPIGenerator.this.packageName = packageName;
            return this;
        }
        
        public Builder withDestination(String destinationPath) {
            return withDestination(Paths.get(destinationPath));
        }

        public Builder withDestination(Path destinationPath) {
            HollowTestDataAPIGenerator.this.destinationPath = destinationPath;
            return this;
        }
        
        public HollowTestDataAPIGenerator build() {
            return HollowTestDataAPIGenerator.this;
        }
    }
    
    public void generateSourceFiles() throws IOException {
        generate(dataset, packageName, apiClassname, destinationPath);
    }

    
    private void generate(HollowDataset dataset, String packageName, String apiClassName, Path destination) throws IOException {
        Path apiClassDestination = destination.resolve(apiClassName + ".java");
        Files.createDirectories(destination);

        String apiClassContent = new HollowTestDataAPIClassGenerator(dataset, apiClassName, packageName).generate();
        try(FileWriter writer = new FileWriter(apiClassDestination.toFile())) {
            writer.write(apiClassContent);
        }

        for(HollowSchema schema : dataset.getSchemas()) {
            File classDestination = destination.resolve(schema.getName() + "TestData.java").toFile();
            String classContent = null;
            switch(schema.getSchemaType()) {
            case OBJECT:
                classContent = new HollowObjectTypeTestDataAPIClassGenerator(dataset, (HollowObjectSchema) schema, packageName).generate();
                break;
            case LIST:
                classContent = new HollowListTypeTestDataAPIClassGenerator(dataset, (HollowListSchema) schema, packageName).generate();
                break;
            case SET:
                classContent = new HollowSetTypeTestDataAPIClassGenerator(dataset, (HollowSetSchema) schema, packageName).generate();
                break;
            case MAP:
                classContent = new HollowMapTypeTestDataAPIClassGenerator(dataset, (HollowMapSchema) schema, packageName).generate();
                break;
            }
            
            if(classContent != null)
            try(FileWriter writer = new FileWriter(classDestination)) {
                writer.write(classContent);
            }
        }
    }
    
        
}
