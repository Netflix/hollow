/*
 *
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
 *
 */
package com.netflix.hollow.api.codegen.indexes;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 */
public class LegacyHollowPrimaryKeyIndexGenerator extends HollowUniqueKeyIndexGenerator {

    public LegacyHollowPrimaryKeyIndexGenerator(String packageName, String apiClassname, HollowObjectSchema schema, CodeGeneratorConfig config) {
        super(packageName, apiClassname, schema, config);

        isGenSimpleConstructor = true;
        isParameterizedConstructorPublic = true;
        isAutoListenToDataRefresh = true;
    }

    @Override
    protected String getClassName(HollowObjectSchema schema) {
        return schema.getName() + "PrimaryKeyIndex";
    }
}