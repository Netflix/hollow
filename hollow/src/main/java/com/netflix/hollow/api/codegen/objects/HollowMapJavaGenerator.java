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
package com.netflix.hollow.api.codegen.objects;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Arrays;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowMapJavaGenerator extends HollowCollectionsGenerator {
    private final HollowMapSchema schema;
    private final String keyClassName;
    private final String valueClassName;

    private final boolean parameterizeKey;
    private final boolean parameterizeValue;

    public HollowMapJavaGenerator(String packageName, String apiClassname, HollowMapSchema schema,
            HollowDataset dataset, Set<String> parameterizedTypes, boolean parameterizeClassNames,
            CodeGeneratorConfig config) {
        super(packageName, apiClassname, schema, dataset, config);
        this.schema = schema;
        this.keyClassName = hollowImplClassname(schema.getKeyType());
        this.valueClassName = hollowImplClassname(schema.getValueType());
        this.parameterizeKey = parameterizeClassNames || parameterizedTypes.contains(schema.getKeyType());
        this.parameterizeValue = parameterizeClassNames || parameterizedTypes.contains(schema.getValueType());
    }


    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder, apiClassname, Arrays.<HollowSchema>asList(schema));

        builder.append("import " + HollowMap.class.getName() + ";\n");
        builder.append("import " + HollowMapSchema.class.getName() + ";\n");
        builder.append("import " + HollowMapDelegate.class.getName() + ";\n");
        builder.append("import " + GenericHollowRecordHelper.class.getName() + ";\n\n");

        builder.append("@SuppressWarnings(\"all\")\n");

        String keyGeneric = parameterizeKey ? "K" : keyClassName;
        String valueGeneric = parameterizeValue ? "V" : valueClassName;

        String classGeneric = "";
        if(parameterizeKey && parameterizeValue)
            classGeneric = "<K, V>";
        else if(parameterizeKey)
            classGeneric = "<K>";
        else if(parameterizeValue)
            classGeneric = "<V>";

        builder.append("public class " + className + classGeneric + " extends HollowMap<" + keyGeneric + ", " + valueGeneric + "> {\n\n");

        appendConstructor(builder);
        appendInstantiateMethods(builder);
        appendGetByHashKeyMethod(builder);
        appendEqualityMethods(builder);
        appendAPIAccessor(builder);
        appendTypeAPIAccessor(builder);

        builder.append("}");

        return builder.toString();
    }

    private void appendConstructor(StringBuilder classBuilder) {
        classBuilder.append("    public " + className + "(HollowMapDelegate delegate, int ordinal) {\n");
        classBuilder.append("        super(delegate, ordinal);\n");
        classBuilder.append("    }\n\n");
    }


    private void appendInstantiateMethods(StringBuilder classBuilder) {
        String keyReturnType = parameterizeKey ? "K" : keyClassName;
        String valueReturnType = parameterizeValue ? "V" : valueClassName;

        classBuilder.append("    @Override\n");
        classBuilder.append("    public " + keyReturnType + " instantiateKey(int ordinal) {\n");
        classBuilder.append("        return (" + keyReturnType + ") api().deserializeFrom").append(keyClassName).append("(ordinal);\n");
        classBuilder.append("    }\n\n");

        classBuilder.append("    @Override\n");
        classBuilder.append("    public " + valueReturnType + " instantiateValue(int ordinal) {\n");
        classBuilder.append("        return (" + valueReturnType + ") api().deserializeFrom").append(valueClassName).append("(ordinal);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendGetByHashKeyMethod(StringBuilder classBuilder) {
        if(schema.getHashKey() != null) {
            String valueReturnType = parameterizeValue ? "V" : valueClassName;

            classBuilder.append("    public " + valueReturnType + " deserializeFrom(");
            classBuilder.append(getKeyFieldType(schema.getHashKey().getFieldPath(0))).append(" k0");
            for(int i=1;i<schema.getHashKey().numFields();i++)
                classBuilder.append(", ").append(getKeyFieldType(schema.getHashKey().getFieldPath(i))).append(" k").append(i);
            classBuilder.append(") {\n");
            classBuilder.append("        return findValue(k0");
            for(int i=1;i<schema.getHashKey().numFields();i++)
                classBuilder.append(", k").append(i);
            classBuilder.append(");\n");
            classBuilder.append("    }\n\n");
        }
    }

    private void appendEqualityMethods(StringBuilder classBuilder) {
        classBuilder.append("    @Override\n");
        classBuilder.append("    public boolean equalsKey(int keyOrdinal, Object testObject) {\n");
        classBuilder.append("        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);\n");
        classBuilder.append("    }\n\n");

        classBuilder.append("    @Override\n");
        classBuilder.append("    public boolean equalsValue(int valueOrdinal, Object testObject) {\n");
        classBuilder.append("        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendAPIAccessor(StringBuilder classBuilder) {
        classBuilder.append("    public " + apiClassname + " api() {\n");
        classBuilder.append("        return typeApi().getAPI();\n");
        classBuilder.append("    }\n\n");
    }

    private void appendTypeAPIAccessor(StringBuilder classBuilder) {
        String typeAPIClassname = typeAPIClassname(schema.getName());
        classBuilder.append("    public " + typeAPIClassname + " typeApi() {\n");
        classBuilder.append("        return (").append(typeAPIClassname).append(") delegate.getTypeAPI();\n");
        classBuilder.append("    }\n\n");
    }

    private String getKeyFieldType(String fieldPath) {
        try {
            HollowObjectSchema keySchema = (HollowObjectSchema)dataset.getSchema(schema.getKeyType());

            String fieldPathElements[] = fieldPath.split("\\.");
            int idx = 0;

            while(idx < fieldPathElements.length-1) {
                keySchema = (HollowObjectSchema)dataset.getSchema(keySchema.getReferencedType(fieldPathElements[idx]));
                idx++;
            }

            FieldType fieldType = keySchema.getFieldType(keySchema.getPosition(fieldPathElements[idx]));

            return HollowCodeGenerationUtils.getJavaBoxedType(fieldType);
        } catch(Throwable th) { }
        throw new IllegalArgumentException("Field path '" + fieldPath + "' specified incorrectly for type: " + schema.getName());
    }

}
