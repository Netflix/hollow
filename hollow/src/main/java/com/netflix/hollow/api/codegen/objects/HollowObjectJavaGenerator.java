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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateInterfaceName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.generateBooleanAccessorMethodName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.isPrimitiveType;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;
import static java.util.stream.Collectors.joining;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.api.codegen.HollowConsumerJavaFileGenerator;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts.Shortcut;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowObjectJavaGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "";

    private final HollowObjectSchema schema;
    private final String apiClassname;
    private final Set<String> parameterizedTypes;
    private final boolean parameterizeClassNames;
    private final String getterPrefix;
    private final HollowErgonomicAPIShortcuts ergonomicShortcuts;
    private final boolean useBooleanFieldErgonomics;
    private final boolean restrictApiToFieldType;

    public HollowObjectJavaGenerator(String packageName, String apiClassname, HollowObjectSchema schema, Set<String>
            parameterizedTypes, boolean parameterizeClassNames, HollowErgonomicAPIShortcuts ergonomicShortcuts,
            HollowDataset dataset, CodeGeneratorConfig config) {
        super(packageName, computeSubPackageName(schema), dataset, config);

        this.apiClassname = apiClassname;
        this.schema = schema;
        this.className = hollowImplClassname(schema.getName());
        this.parameterizedTypes = parameterizedTypes;
        this.parameterizeClassNames = parameterizeClassNames;
        this.getterPrefix = config.getGetterPrefix();
        this.ergonomicShortcuts = ergonomicShortcuts;
        this.useBooleanFieldErgonomics = config.isUseBooleanFieldErgonomics();
        this.restrictApiToFieldType = config.isRestrictApiToFieldType();
    }

    private static String computeSubPackageName(HollowObjectSchema schema) {
        String type = schema.getName();
        if (isPrimitiveType(type)) {
            return "core";
        }
        return SUB_PACKAGE_NAME;
    }

    @Override
    public String generate() {
        StringBuilder classBuilder = new StringBuilder();
        appendPackageAndCommonImports(classBuilder, apiClassname);
        boolean requiresHollowTypeName = !className.equals(schema.getName());

        classBuilder.append("import " + HollowConsumer.class.getName() + ";\n");
        if (schema.getPrimaryKey() != null && schema.getPrimaryKey().numFields() > 1) {
            classBuilder.append("import " + FieldPath.class.getName() + ";\n");
        }
        if (schema.getPrimaryKey() != null) {
            classBuilder.append("import " + UniqueKeyIndex.class.getName() + ";\n");
        }
        classBuilder.append("import " + HollowObject.class.getName() + ";\n");
        classBuilder.append("import " + HollowObjectSchema.class.getName() + ";\n");
        if (requiresHollowTypeName) {
            classBuilder.append("import " + HollowTypeName.class.getName() + ";\n");
        }
        if (config.isUseVerboseToString()) {
            classBuilder.append("import " + HollowRecordStringifier.class.getName() + ";\n");
        }
        classBuilder.append("\n");

        classBuilder.append("@SuppressWarnings(\"all\")\n");
        if (requiresHollowTypeName) {
            classBuilder.append("@" + HollowTypeName.class.getSimpleName() + "(name=\"" + schema.getName() + "\")\n");
        }
        classBuilder.append("public class " + className + " extends HollowObject {\n\n");

        appendConstructor(classBuilder);

        appendAccessors(classBuilder);

        appendAPIAccessor(classBuilder);
        appendTypeAPIAccessor(classBuilder);
        appendDelegateAccessor(classBuilder);

        if (config.isUseVerboseToString()) {
            appendToString(classBuilder);
        }

        if (schema.getPrimaryKey() != null) {
            appendPrimaryKey(classBuilder, schema.getPrimaryKey());
        }

        classBuilder.append("}");

        return classBuilder.toString();
    }

    private void appendConstructor(StringBuilder classBuilder) {
        classBuilder.append("    public " + className + "(" + delegateInterfaceName(schema.getName()) + " delegate, int ordinal) {\n");
        classBuilder.append("        super(delegate, ordinal);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendAccessors(StringBuilder classBuilder) {
        for(int i=0;i<schema.numFields();i++) {
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                    classBuilder.append(generateBooleanFieldAccessor(i));
                    break;
                case BYTES:
                    classBuilder.append(generateByteArrayFieldAccessor(i));
                    break;
                case DOUBLE:
                    classBuilder.append(generateDoubleFieldAccessor(i));
                    break;
                case FLOAT:
                    classBuilder.append(generateFloatFieldAccessor(i));
                    break;
                case INT:
                    classBuilder.append(generateIntFieldAccessor(i));
                    break;
                case LONG:
                    classBuilder.append(generateLongFieldAccessor(i));
                    break;
                case REFERENCE:
                    classBuilder.append(generateReferenceFieldAccessor(i));
                    break;
                case STRING:
                    classBuilder.append(generateStringFieldAccessors(i));
                    break;
            }

            classBuilder.append("\n\n");
        }
    }

    private String generateByteArrayFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public byte[] ").append(getterPrefix).append("get" + uppercase(fieldName) + "() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateStringFieldAccessors(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public String ").append(getterPrefix).append("get" + uppercase(fieldName) + "() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        builder.append("    public boolean ").append(getterPrefix).append("is" + uppercase(fieldName) + "Equal(String testValue) {\n");
        builder.append("        return delegate().is" + uppercase(fieldName) + "Equal(ordinal, testValue);\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateReferenceFieldAccessor(int fieldNum) {
        Shortcut shortcut = ergonomicShortcuts == null ? null : ergonomicShortcuts.getShortcut(schema.getName() + "." + schema.getFieldName(fieldNum));
        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        StringBuilder builder = new StringBuilder();

        if(shortcut != null) {
            switch(shortcut.getType()) {
            case BOOLEAN:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
                String methodName = (shortcut.getType()==FieldType.BOOLEAN) ? generateBooleanAccessorMethodName(fieldName, useBooleanFieldErgonomics) : "get" + uppercase(fieldName);
                builder.append("    public ").append(HollowCodeGenerationUtils.getJavaBoxedType(shortcut.getType())).append(" ").append(getterPrefix).append(methodName);
                if(!restrictApiToFieldType) {
                    builder.append("Boxed");
                }
                builder.append("() {\n");
                builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
                builder.append("    }\n\n");
                if(!restrictApiToFieldType) {
                    builder.append("    public ").append(HollowCodeGenerationUtils.getJavaScalarType(shortcut.getType())).append(" ").append(getterPrefix).append(methodName + "() {\n");
                    builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
                    builder.append("    }\n\n");
                }
                break;
            case BYTES:
                builder.append("    public byte[] ").append(getterPrefix).append("get" + uppercase(fieldName) + "() {\n");
                builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
                builder.append("    }\n\n");
                break;
            case STRING:
                builder.append("    public String ").append(getterPrefix).append("get" + uppercase(fieldName) + "() {\n");
                builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
                builder.append("    }\n\n");
                builder.append("    public boolean ").append(getterPrefix).append("is" + uppercase(fieldName) + "Equal(String testValue) {\n");
                builder.append("        return delegate().is" + uppercase(fieldName) + "Equal(ordinal, testValue);\n");
                builder.append("    }\n\n");
                break;
            default:
            }
        }

        String referencedType = schema.getReferencedType(fieldNum);

        boolean parameterize = parameterizeClassNames || parameterizedTypes.contains(referencedType);

        String methodName = null;
        if (shortcut != null) {
            methodName = getterPrefix + "get" + uppercase(fieldName) + "HollowReference";
        } else {
            boolean isBooleanRefType = Boolean.class.getSimpleName().equals(referencedType);
            methodName = getterPrefix + (isBooleanRefType ?  generateBooleanAccessorMethodName(fieldName, useBooleanFieldErgonomics) : "get" + uppercase(fieldName));
        }

        if(parameterize)
            builder.append("    public <T> T ").append(methodName).append("() {\n");
        else
            builder.append("    public ").append(hollowImplClassname(referencedType)).append(" ").append(methodName).append("() {\n");

        builder.append("        int refOrdinal = delegate().get" + uppercase(fieldName) + "Ordinal(ordinal);\n");
        builder.append("        if(refOrdinal == -1)\n");
        builder.append("            return null;\n");
        builder.append("        return ").append(parameterize ? "(T)" : "").append(" api().get" + hollowImplClassname(referencedType) + "(refOrdinal);\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateFloatFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public float ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        if(!restrictApiToFieldType) {
            builder.append("    public Float ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("Boxed() {\n");
            builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
            builder.append("    }");
        }


        return builder.toString();
    }

    private String generateDoubleFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public double ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        if(!restrictApiToFieldType) {
            builder.append("    public Double ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("Boxed() {\n");
            builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
            builder.append("    }");
        }

        return builder.toString();
    }

    private String generateLongFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public long ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        if(!restrictApiToFieldType) {
            builder.append("    public Long ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("Boxed() {\n");
            builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
            builder.append("    }");
        }

        return builder.toString();
    }

    private String generateIntFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(schema.getFieldName(fieldNum));

        builder.append("    public int ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        if(!restrictApiToFieldType) {
            builder.append("    public Integer ").append(getterPrefix).append("get").append(uppercase(fieldName)).append("Boxed() {\n");
            builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
            builder.append("    }");
        }

        return builder.toString();
    }

    private String generateBooleanFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = schema.getFieldName(fieldNum);
        String methodName = generateBooleanAccessorMethodName(fieldName, useBooleanFieldErgonomics);

        builder.append("    public boolean ").append(getterPrefix).append(methodName).append("() {\n");
        builder.append("        return delegate().get" + uppercase(fieldName) + "(ordinal);\n");
        builder.append("    }\n\n");

        if(!restrictApiToFieldType) {
            builder.append("    public Boolean ").append(getterPrefix).append(methodName).append("Boxed() {\n");
            builder.append("        return delegate().get" + uppercase(fieldName) + "Boxed(ordinal);\n");
            builder.append("    }");
        }

        return builder.toString();
    }

    private void appendAPIAccessor(StringBuilder classBuilder) {
        classBuilder.append("    public " + apiClassname + " api() {\n");
        classBuilder.append("        return typeApi().getAPI();\n");
        classBuilder.append("    }\n\n");
    }

    private void appendTypeAPIAccessor(StringBuilder classBuilder) {
        String typeAPIClassname = typeAPIClassname(schema.getName());
        classBuilder.append("    public " + typeAPIClassname + " typeApi() {\n");
        classBuilder.append("        return delegate().getTypeAPI();\n");
        classBuilder.append("    }\n\n");
    }

    private void appendDelegateAccessor(StringBuilder classBuilder) {
        classBuilder.append("    protected ").append(delegateInterfaceName(schema.getName())).append(" delegate() {\n");
        classBuilder.append("        return (").append(delegateInterfaceName(schema.getName())).append(")delegate;\n");
        classBuilder.append("    }\n\n");
    }

    private void appendToString(StringBuilder classBuilder) {
        classBuilder.append("    public String toString() {\n");
        classBuilder.append("        return new HollowRecordStringifier().stringify(this);\n");
        classBuilder.append("    }\n\n");
    }

    private void appendPrimaryKey(StringBuilder classBuilder, PrimaryKey pk) {
        if (pk.numFields() == 1) {
            String fieldPath = pk.getFieldPath(0);
            FieldType fieldType = pk.getFieldType(dataset, 0);
            String type, boxedType;
            if (FieldType.REFERENCE.equals(fieldType)) {
                HollowObjectSchema refSchema = pk.getFieldSchema(dataset, 0);
                type = boxedType = hollowImplClassname(refSchema.getName());
            } else {
                type = HollowCodeGenerationUtils.getJavaScalarType(fieldType);
                boxedType = HollowCodeGenerationUtils.getJavaBoxedType(fieldType);
            }

            appendPrimaryKeyDoc(classBuilder, fieldType, type);

            classBuilder.append("    public static UniqueKeyIndex<" + className + ", " + boxedType + "> uniqueIndex(HollowConsumer consumer) {\n");
            classBuilder.append("        return UniqueKeyIndex.from(consumer, " + className + ".class)\n");
            classBuilder.append("            .bindToPrimaryKey()\n");
            classBuilder.append("            .usingPath(\"" + fieldPath + "\", " + type + ".class);\n");
            classBuilder.append("    }\n\n");
        } else {

            appendPrimaryKeyDoc(classBuilder, FieldType.REFERENCE, className + ".Key");

            classBuilder.append("    public static UniqueKeyIndex<" + className + ", " + className + ".Key> uniqueIndex(HollowConsumer consumer) {\n");
            classBuilder.append("        return UniqueKeyIndex.from(consumer, " + className + ".class)\n");
            classBuilder.append("            .bindToPrimaryKey()\n");
            classBuilder.append("            .usingBean(" + className + ".Key.class);\n");
            classBuilder.append("    }\n\n");

            classBuilder.append("    public static class Key {\n");
            Map<String, String> parameterList = new LinkedHashMap<>();
            for (int i = 0; i < pk.numFields(); i++) {
                if (i > 0) {
                    classBuilder.append("\n");
                }

                String fieldPath = pk.getFieldPath(i);
                String name = HollowCodeGenerationUtils.normalizeFieldPathToParamName(fieldPath);
                FieldType fieldType = pk.getFieldType(dataset, i);
                String type;
                if (FieldType.REFERENCE.equals(fieldType)) {
                    HollowObjectSchema refSchema = pk.getFieldSchema(dataset, i);
                    type = hollowImplClassname(refSchema.getName());
                } else {
                    type = HollowCodeGenerationUtils.getJavaScalarType(fieldType);
                }
                parameterList.put(name, type);
                classBuilder.append("        @FieldPath(\"" + fieldPath + "\")\n");
                classBuilder.append("        public final " + type + " " + name + ";\n");
            }
            classBuilder.append("\n");

            String parameters = parameterList.entrySet().stream()
                    .map(e -> e.getValue() + " " + e.getKey())
                    .collect(joining(", "));
            classBuilder.append("        public Key(" + parameters + ") {\n");
            parameterList.forEach((n, t) -> {
                if (t.equals("byte[]")) {
                    classBuilder.append("            this." + n + " = " + n + " == null ? null : " + n + ".clone();\n");
                } else {
                    classBuilder.append("            this." + n + " = " + n + ";\n");
                }
            });
            classBuilder.append("        }\n");

            classBuilder.append("    }\n\n");
        }
    }

    private void appendPrimaryKeyDoc(StringBuilder classBuilder, FieldType type, String keyTypeName) {
        String kindSnippet;
        switch (type) {
            case STRING:
            case REFERENCE:
                kindSnippet = String.format("class {@link %s}", keyTypeName);
                break;
            default:
                kindSnippet = String.format("type {@code %s}", keyTypeName);
                break;
        }
        classBuilder.append("    /**\n");
        classBuilder.append(String.format("     * Creates a unique key index for {@code %s} that has a primary key.\n", className));
        classBuilder.append(String.format("     * The primary key is represented by the %s.\n", kindSnippet));
        classBuilder.append("     * <p>\n");
        classBuilder.append("     * By default the unique key index will not track updates to the {@code consumer} and thus\n");
        classBuilder.append("     * any changes will not be reflected in matched results.  To track updates the index must be\n");
        classBuilder.append("     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}\n");
        classBuilder.append("     * with the {@code consumer}\n");
        classBuilder.append("     *\n");
        classBuilder.append("     * @param consumer the consumer\n");
        classBuilder.append("     * @return the unique key index\n");
        classBuilder.append("     */\n");
    }
}
