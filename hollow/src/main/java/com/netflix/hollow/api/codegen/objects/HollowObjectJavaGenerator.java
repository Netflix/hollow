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
package com.netflix.hollow.api.codegen.objects;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateInterfaceName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.generateBooleanAccessorMethodName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.isPrimitiveType;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.api.codegen.HollowConsumerJavaFileGenerator;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts;
import com.netflix.hollow.api.codegen.HollowErgonomicAPIShortcuts.Shortcut;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
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

    public HollowObjectJavaGenerator(String packageName, String apiClassname, HollowObjectSchema schema, Set<String> parameterizedTypes, boolean parameterizeClassNames, HollowErgonomicAPIShortcuts ergonomicShortcuts, CodeGeneratorConfig config) {
        super(packageName, computeSubPackageName(schema), config);

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
        appendPackageAndCommonImports(classBuilder);

        classBuilder.append("import " + HollowObject.class.getName() + ";\n");
        classBuilder.append("import " + HollowObjectSchema.class.getName() + ";\n\n");
        classBuilder.append("import " + HollowRecordStringifier.class.getName() + ";\n\n");


        classBuilder.append("@SuppressWarnings(\"all\")\n");
        classBuilder.append("public class " + className + " extends HollowObject {\n\n");

        appendConstructor(classBuilder);

        appendAccessors(classBuilder);

        appendAPIAccessor(classBuilder);
        appendTypeAPIAccessor(classBuilder);
        appendDelegateAccessor(classBuilder);

        if (config.isUseVerboseToString()) {
            appendToString(classBuilder);
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

}
