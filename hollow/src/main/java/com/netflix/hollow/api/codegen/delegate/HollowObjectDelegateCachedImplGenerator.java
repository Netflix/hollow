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
package com.netflix.hollow.api.codegen.delegate;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateCachedImplName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateInterfaceName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts.Shortcut;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowObjectDelegateCachedImplGenerator extends HollowObjectDelegateGenerator {

    public HollowObjectDelegateCachedImplGenerator(String packageName, HollowObjectSchema schema,
            HollowErgonomicAPIShortcuts ergonomicShortcuts, HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, schema, ergonomicShortcuts, dataset, config);
        this.className = delegateCachedImplName(schema.getName());
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);

        builder.append("import ").append(HollowObjectAbstractDelegate.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectSchema.class.getName()).append(";\n");
        builder.append("import ").append(HollowTypeAPI.class.getName()).append(";\n");
        builder.append("import ").append(HollowCachedDelegate.class.getName()).append(";\n");

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ").append(delegateInterfaceName(schema.getName())).append(" {\n\n");

        for(int i=0;i<schema.numFields();i++) {
            switch(schema.getFieldType(i)) {
            case BOOLEAN:
                builder.append("    private final Boolean ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case BYTES:
                builder.append("    private final byte[] ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case DOUBLE:
                builder.append("    private final Double ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case FLOAT:
                builder.append("    private final Float ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case INT:
                builder.append("    private final Integer ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case LONG:
                builder.append("    private final Long ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            case REFERENCE:
                Shortcut shortcut = ergonomicShortcuts.getShortcut(schema.getName() + "." + schema.getFieldName(i));
                if(shortcut != null)
                    builder.append("    private final ").append(HollowCodeGenerationUtils.getJavaBoxedType(shortcut.getType())).append(" ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                builder.append("    private final int ").append(substituteInvalidChars(schema.getFieldName(i))).append("Ordinal;\n");
                break;
            case STRING:
                builder.append("    private final String ").append(substituteInvalidChars(schema.getFieldName(i))).append(";\n");
                break;
            }
        }

        builder.append("    private ").append(typeAPIClassname(schema.getName())).append(" typeAPI;\n\n");

        builder.append("    public ").append(className).append("(").append(typeAPIClassname(schema.getName())).append(" typeAPI, int ordinal) {\n");

        for(int i=0;i<schema.numFields();i++) {
            String fieldName = substituteInvalidChars(schema.getFieldName(i));
            switch(schema.getFieldType(i)) {
            case STRING:
            case BYTES:
                builder.append("        this.").append(fieldName).append(" = typeAPI.get").append(uppercase(fieldName)).append("(ordinal);\n");
                break;
            case BOOLEAN:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
                builder.append("        this.").append(fieldName).append(" = typeAPI.get").append(uppercase(fieldName)).append("Boxed(ordinal);\n");
                break;
            case REFERENCE:
                builder.append("        this.").append(fieldName).append("Ordinal = typeAPI.get").append(uppercase(fieldName)).append("Ordinal(ordinal);\n");
                Shortcut shortcut = ergonomicShortcuts.getShortcut(schema.getName() + "." + schema.getFieldName(i));
                if(shortcut != null) {
                    String ordinalVariableName = fieldName + "TempOrdinal";

                    builder.append("        int ").append(ordinalVariableName).append(" = ").append(fieldName).append("Ordinal;\n");

                    for(int j=0;j<shortcut.getPath().length-1;j++) {
                        String typeAPIName = HollowCodeGenerationUtils.typeAPIClassname(shortcut.getPathTypes()[j]);
                        builder.append("        " + ordinalVariableName + " = " + ordinalVariableName + " == -1 ? -1 : typeAPI.getAPI().get").append(typeAPIName).append("().get").append(uppercase(shortcut.getPath()[j])).append("Ordinal(").append(ordinalVariableName).append(");\n");
                    }

                    String typeAPIName = HollowCodeGenerationUtils.typeAPIClassname(shortcut.getPathTypes()[shortcut.getPathTypes().length-1]);
                    builder.append("        this.").append(fieldName).append(" = ").append(ordinalVariableName).append(" == -1 ? null : ").append("typeAPI.getAPI().get").append(typeAPIName).append("().get").append(uppercase(shortcut.getPath()[shortcut.getPath().length-1])).append("(").append(ordinalVariableName).append(");\n");
                }
            }
        }

        builder.append("        this.typeAPI = typeAPI;\n");
        builder.append("    }\n\n");

        for(int i=0;i<schema.numFields();i++) {
            FieldType fieldType = schema.getFieldType(i);
            String fieldName = substituteInvalidChars(schema.getFieldName(i));
            if(schema.getFieldType(i) == FieldType.REFERENCE) {
                Shortcut shortcut = ergonomicShortcuts.getShortcut(schema.getName() + "." + schema.getFieldName(i));
                if(shortcut != null)
                    addAccessor(builder, shortcut.getType(), fieldName);

                builder.append("    public int get").append(uppercase(fieldName)).append("Ordinal(int ordinal) {\n");
                builder.append("        return ").append(fieldName).append("Ordinal;\n");
                builder.append("    }\n\n");
            } else {
                addAccessor(builder, fieldType, fieldName);
            }
        }

        builder.append("    @Override\n");
        builder.append("    public HollowObjectSchema getSchema() {\n");
        builder.append("        return typeAPI.getTypeDataAccess().getSchema();\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public HollowObjectTypeDataAccess getTypeDataAccess() {\n");
        builder.append("        return typeAPI.getTypeDataAccess();\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(typeAPIClassname(schema.getName())).append(" getTypeAPI() {\n");
        builder.append("        return typeAPI;\n");
        builder.append("    }\n\n");

        builder.append("    public void updateTypeAPI(HollowTypeAPI typeAPI) {\n");
        builder.append("        this.typeAPI = (").append(typeAPIClassname(schema.getName())).append(") typeAPI;\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }

    private void addAccessor(StringBuilder builder, FieldType fieldType, String fieldName) {
        switch(fieldType) {
        case BOOLEAN:
            builder.append("    public boolean get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        if(").append(fieldName).append(" == null)\n");
            builder.append("            return false;\n");
            builder.append("        return ").append(fieldName).append(".booleanValue();\n");
            builder.append("    }\n\n");
            builder.append("    public Boolean get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case BYTES:
            builder.append("    public byte[] get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            // we need the cast to get around http://findbugs.sourceforge.net/bugDescriptions.html#EI_EXPOSE_REP
            builder.append("        return (byte[]) ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case DOUBLE:
            builder.append("    public double get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        if(").append(fieldName).append(" == null)\n");
            builder.append("            return Double.NaN;\n");
            builder.append("        return ").append(fieldName).append(".doubleValue();\n");
            builder.append("    }\n\n");
            builder.append("    public Double get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case FLOAT:
            builder.append("    public float get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        if(").append(fieldName).append(" == null)\n");
            builder.append("            return Float.NaN;\n");
            builder.append("        return ").append(fieldName).append(".floatValue();\n");
            builder.append("    }\n\n");
            builder.append("    public Float get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case INT:
            builder.append("    public int get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        if(").append(fieldName).append(" == null)\n");
            builder.append("            return Integer.MIN_VALUE;\n");
            builder.append("        return ").append(fieldName).append(".intValue();\n");
            builder.append("    }\n\n");
            builder.append("    public Integer get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case LONG:
            builder.append("    public long get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        if(").append(fieldName).append(" == null)\n");
            builder.append("            return Long.MIN_VALUE;\n");
            builder.append("        return ").append(fieldName).append(".longValue();\n");
            builder.append("    }\n\n");
            builder.append("    public Long get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            break;
        case STRING:
            builder.append("    public String get").append(uppercase(fieldName)).append("(int ordinal) {\n");
            builder.append("        return ").append(fieldName).append(";\n");
            builder.append("    }\n\n");
            builder.append("    public boolean is").append(uppercase(fieldName)).append("Equal(int ordinal, String testValue) {\n");
            builder.append("        if(testValue == null)\n");
            builder.append("            return ").append(fieldName).append(" == null;\n");
            builder.append("        return testValue.equals(").append(fieldName).append(");\n");
            builder.append("    }\n\n");
            break;
        case REFERENCE:
            throw new IllegalArgumentException();
        }
    }

}
