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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateInterfaceName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateLookupImplName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts.Shortcut;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowObjectDelegateLookupImplGenerator extends HollowObjectDelegateGenerator {

    public HollowObjectDelegateLookupImplGenerator(String packageName, HollowObjectSchema schema,
            HollowErgonomicAPIShortcuts ergonomicShortcuts, HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, schema, ergonomicShortcuts, dataset, config);
        this.className = delegateLookupImplName(schema.getName());
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);

        builder.append("import ").append(HollowObjectAbstractDelegate.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectSchema.class.getName()).append(";\n");

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" extends HollowObjectAbstractDelegate implements ").append(delegateInterfaceName(schema.getName())).append(" {\n\n");

        builder.append("    private final ").append(typeAPIClassname(schema.getName())).append(" typeAPI;\n\n");

        builder.append("    public ").append(className).append("(").append(typeAPIClassname(schema.getName())).append(" typeAPI) {\n");
        builder.append("        this.typeAPI = typeAPI;\n");
        builder.append("    }\n\n");

        for(int i=0;i<schema.numFields();i++) {
            String methodFieldName = substituteInvalidChars(uppercase(schema.getFieldName(i)));

            switch(schema.getFieldType(i)) {
            case BOOLEAN:
                builder.append("    public boolean deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public Boolean deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Boxed(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case BYTES:
                builder.append("    public byte[] deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case DOUBLE:
                builder.append("    public double deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public Double deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Boxed(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case FLOAT:
                builder.append("    public float deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public Float deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Boxed(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case INT:
                builder.append("    public int deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public Integer deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Boxed(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case LONG:
                builder.append("    public long deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public Long deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Boxed(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case STRING:
                builder.append("    public String deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public boolean is").append(methodFieldName).append("Equal(int ordinal, String testValue) {\n");
                builder.append("        return typeAPI.is").append(methodFieldName).append("Equal(ordinal, testValue);\n");
                builder.append("    }\n\n");
                break;
            case REFERENCE:
                Shortcut shortcut = ergonomicShortcuts.getShortcut(schema.getName() + "." + schema.getFieldName(i));
                if(shortcut != null) {
                    addShortcutAccessMethod(builder, methodFieldName, shortcut);
                }

                builder.append("    public int deserializeFrom").append(methodFieldName).append("Ordinal(int ordinal) {\n");
                builder.append("        return typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
                builder.append("    }\n\n");
                break;
            }
        }

        builder.append("    public ").append(typeAPIClassname(schema.getName())).append(" getTypeAPI() {\n");
        builder.append("        return typeAPI;\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public HollowObjectSchema getSchema() {\n");
        builder.append("        return typeAPI.getTypeDataAccess().getSchema();\n");
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    public HollowObjectTypeDataAccess getTypeDataAccess() {\n");
        builder.append("        return typeAPI.getTypeDataAccess();\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();

    }

    private void addShortcutAccessMethod(StringBuilder builder, String methodFieldName, Shortcut shortcut) {
        String finalFieldName = substituteInvalidChars(uppercase(shortcut.getPath()[shortcut.getPath().length-1]));
        String finalTypeAPI = typeAPIClassname(shortcut.getPathTypes()[shortcut.getPathTypes().length-1]);

        switch(shortcut.getType()) {
        case BOOLEAN:
            builder.append("    public boolean deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? false : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public Boolean deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("Boxed(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case BYTES:
            builder.append("    public byte[] deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case DOUBLE:
            builder.append("    public double deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? Double.NaN : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public Double deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("Boxed(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case FLOAT:
            builder.append("    public float deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? Float.NaN : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public Float deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("Boxed(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case INT:
            builder.append("    public int deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? Integer.MIN_VALUE : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public Integer deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("Boxed(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case LONG:
            builder.append("    public long deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public Long deserializeFrom").append(methodFieldName).append("Boxed(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("Boxed(ordinal);\n");
            builder.append("    }\n\n");
            break;
        case STRING:
            builder.append("    public String deserializeFrom").append(methodFieldName).append("(int ordinal) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().deserializeFrom").append(finalFieldName).append("(ordinal);\n");
            builder.append("    }\n\n");
            builder.append("    public boolean is").append(methodFieldName).append("Equal(int ordinal, String testValue) {\n");
            builder.append("        ordinal = typeAPI.deserializeFrom").append(methodFieldName).append("Ordinal(ordinal);\n");
            addShortcutTraversal(builder, shortcut);
            builder.append("        return ordinal == -1 ? testValue == null : typeAPI.getAPI().deserializeFrom" + finalTypeAPI + "().is").append(finalFieldName).append("Equal(ordinal, testValue);\n");
            builder.append("    }\n\n");
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void addShortcutTraversal(StringBuilder builder, Shortcut shortcut) {
        for(int i=0;i<shortcut.getPath().length-1;i++) {
            String typeAPIClassname = typeAPIClassname(shortcut.getPathTypes()[i]);
            builder.append("        if(ordinal != -1) ordinal = typeAPI.getAPI().deserializeFrom" + typeAPIClassname + "().deserializeFrom" + uppercase(shortcut.getPath()[i]) + "Ordinal(ordinal);\n");
        }
    }

}
