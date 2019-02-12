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
import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowPrimaryKeyIndexGenerator extends HollowUniqueKeyIndexGenerator {
    protected final PrimaryKey pk;

    public HollowPrimaryKeyIndexGenerator(HollowDataset dataset, String packageName, String apiClassname, HollowObjectSchema schema, CodeGeneratorConfig config) {
        super(packageName, apiClassname, schema, dataset, config);
        this.pk = schema.getPrimaryKey();
        isGenSimpleConstructor = true;
        isParameterizedConstructorPublic = false;
        isAutoListenToDataRefresh = false;
        isImplementsUniqueKeyIndex = false;
    }

    @Override
    protected String getClassName(HollowObjectSchema schema) {
        return schema.getName() + "PrimaryKeyIndex";
    }

    @Override
    protected void genFindMatchAPI(StringBuilder builder) {
        List<String> params = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();
        for (int i = 0; i < pk.numFields(); i++) {
            String fp = pk.getFieldPath(i);
            String fn = HollowCodeGenerationUtils.normalizeFieldPathToParamName(fp);
            fieldNames.add(fn);

            FieldType ft = pk.getFieldType(dataset, i);
            if (FieldType.REFERENCE.equals(ft)) {
                HollowObjectSchema refSchema = pk.getFieldSchema(dataset, i);
                params.add(refSchema.getName() + " " + fn);
            } else {
                params.add(HollowCodeGenerationUtils.getJavaScalarType(ft) + " " + fn);
            }
        }

        StringBuilder paramsAsStr = new StringBuilder();
        StringBuilder fieldNamesAsStr = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                paramsAsStr.append(", ");
                fieldNamesAsStr.append(", ");
            }
            paramsAsStr.append(params.get(i));
            fieldNamesAsStr.append(fieldNames.get(i));
        }

        builder.append("    public " + hollowImplClassname(schema.getName()) + " findMatch(" + paramsAsStr + ") {\n");
        builder.append("        int ordinal = idx.getMatchingOrdinal(" + fieldNamesAsStr + ");\n");
        builder.append("        if(ordinal == -1)\n");
        builder.append("            return null;\n");
        builder.append("        return api.get" + hollowImplClassname(schema.getName()) + "(ordinal);\n");
        builder.append("    }\n\n");
    }

    @Override
    protected void genDeprecatedJavaDoc(StringBuilder builder) {
        String typeName = hollowImplClassname(type);
        builder.append(" * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:\n");
        builder.append(" * <pre>{@code\n");
        builder.append(String.format(" *     UniqueKeyIndex<%s, K> uki = UniqueKeyIndex.from(consumer, %1$s.class)\n", typeName));
        builder.append(" *         .bindToPrimaryKey()\n");
        builder.append(" *         .usingBean(k);\n");
        builder.append(String.format(" *     %s m = uki.findMatch(k);\n", typeName));
        builder.append(" * }</pre>\n");
        builder.append(" * where {@code K} is a class declaring primary key field paths members, annotated with\n");
        builder.append(" * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of\n");
        builder.append(String.format(" * {@code K} that is the key to find the primary {@code %s} object.\n", typeName));
    }
}