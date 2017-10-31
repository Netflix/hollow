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
package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowConsumerJavaFileGenerator;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts;
import com.netflix.hollow.core.schema.HollowObjectSchema;

public abstract class HollowObjectDelegateGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "core";

    protected final HollowObjectSchema schema;
    protected final HollowErgonomicAPIShortcuts ergonomicShortcuts;

    public HollowObjectDelegateGenerator(String packageName, HollowObjectSchema schema, HollowErgonomicAPIShortcuts ergonomicShortcuts, CodeGeneratorConfig config) {
        super(packageName, SUB_PACKAGE_NAME, config);
        this.schema = schema;
        this.ergonomicShortcuts = ergonomicShortcuts;
    }
}