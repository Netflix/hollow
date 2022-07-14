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
package com.netflix.hollow.api.codegen;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.getJavaScalarType;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.lowercase;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class contains template logic for generating POJOs.  Not intended for external consumption.
 * 
 * @see HollowPOJOGenerator
 */
public class HollowPOJOClassGenerator implements HollowJavaFileGenerator {
    private final HollowDataset dataset;
    private final HollowObjectSchema schema;

    private final String className;
    private final String classNameSuffix;
    private final String packageName;
    private final boolean memoizeOrdinal;
    private final Set<Class<?>> importClasses;

    public HollowPOJOClassGenerator(HollowDataset dataset, HollowObjectSchema schema,
            String packageName, String classNameSuffix) {
        this(dataset, schema, packageName, classNameSuffix, false);
    }

    public HollowPOJOClassGenerator(HollowDataset dataset, HollowObjectSchema schema,
            String packageName, String classNameSuffix, boolean memoizeOrdinal) {
        this.dataset = dataset;
        this.schema = schema;
        this.packageName = packageName;
        this.classNameSuffix = classNameSuffix;
        this.className = buildClassName(schema.getName(), classNameSuffix);
        this.importClasses = new HashSet<Class<?>>();
        this.memoizeOrdinal = memoizeOrdinal;
    }

    private static String buildClassName(String name, String suffix) {
        if(suffix == null) return name;
        return name + suffix;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String generate() {
        StringBuilder classBodyBuilder = new StringBuilder();

        importClasses.add(HollowTypeName.class);
        classBodyBuilder.append("@SuppressWarnings(\"all\")\n");
        classBodyBuilder.append("@HollowTypeName(name=\"").append(schema.getName()).append("\")\n");
        generateHollowPrimaryKeyAnnotation(classBodyBuilder);
        classBodyBuilder.append("public class ").append(getClassName()).append(" implements Cloneable {\n");

        generateInstanceVariables(classBodyBuilder);
        classBodyBuilder.append("\n");
        generateConstructorForPrimaryKey(classBodyBuilder);
        generateChainableSetters(classBodyBuilder);
        generateChainableAddForSetAndList(classBodyBuilder);
        generateEqualsMethod(classBodyBuilder);
        generateHashCodeMethod(classBodyBuilder);
        generateToStringMethod(classBodyBuilder);
        generateCloneMethod(classBodyBuilder);
        classBodyBuilder.append("    }\n\n");

        if(memoizeOrdinal) {
            classBodyBuilder.append("    private long __assigned_ordinal = -1;\n");
        }

        classBodyBuilder.append("}");

        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(packageName).append(";\n\n");

        List<String> importClassNames = new ArrayList<String>();

        for(Class<?> c : importClasses) {
            importClassNames.add(c.getName());
        }

        Collections.sort(importClassNames);

        for(String className : importClassNames) {
            builder.append("import ").append(className).append(";\n");
        }

        builder.append("\n").append(classBodyBuilder);

        return builder.toString();
    }

    private void generateHollowPrimaryKeyAnnotation(StringBuilder classBodyBuilder) {
        PrimaryKey primaryKey = schema.getPrimaryKey();
        if(primaryKey == null) {
            return;
        }
        importClasses.add(HollowPrimaryKey.class);
        classBodyBuilder.append("@HollowPrimaryKey(fields={");
        for(int i = 0; i < primaryKey.numFields(); i++) {
            if(i > 0) {
                classBodyBuilder.append(", ");
            }
            classBodyBuilder.append("\"").append(primaryKey.getFieldPath(i)).append("\"");
        }
        classBodyBuilder.append("})\n");
    }

    private void generateInstanceVariables(StringBuilder classBodyBuilder) {
        for(int i = 0; i < schema.numFields(); i++) {
            if(fieldNeedsTypeNameAnnotation(i)) {
                classBodyBuilder.append("    @HollowTypeName(name=\"").append(schema.getReferencedType(i)).append("\")\n");
            }
            if(fieldNeedsInlineAnnotation(i)) {
                importClasses.add(HollowInline.class);
                classBodyBuilder.append("    @HollowInline\n");
            }
            classBodyBuilder.append("    public ");
            classBodyBuilder.append(fieldType(i));
            classBodyBuilder.append(" ").append(getFieldName(i)).append(" = ").append(defaultValue(i)).append(";\n");
        }
    }

    private void generateConstructorForPrimaryKey(StringBuilder classBodyBuilder) {
        PrimaryKey primaryKey = schema.getPrimaryKey();
        if(primaryKey == null) {
            return;
        }
        // don't allow no-arg constructors if we have a primary key
        classBodyBuilder.append("    private ").append(getClassName()).append("() {}\n\n");
        classBodyBuilder.append("    public ").append(getClassName()).append("(");
        // classBodyBuilder.append("        this.").append(.fieldType
        for(int i = 0; i < primaryKey.numFields(); i++) {
            if(i > 0) {
                classBodyBuilder.append(", ");
            }
            int fieldIndex = getIndexFromFieldName(primaryKey.getFieldPath(i));
            classBodyBuilder.append(fieldType(fieldIndex)).append(" ").append(getFieldName(fieldIndex));
        }
        classBodyBuilder.append(") {\n");
        for(int i = 0; i < primaryKey.numFields(); i++) {
            int fieldIndex = getIndexFromFieldName(primaryKey.getFieldPath(i));
            classBodyBuilder.append("        this.").append(getFieldName(fieldIndex)).append(" = ")
                    .append(getFieldName(fieldIndex)).append(";\n");
        }
        classBodyBuilder.append("    }\n\n");
    }

    private void generateChainableSetters(StringBuilder classBodyBuilder) {
        for(int i = 0; i < schema.numFields(); i++) {
            classBodyBuilder.append("    public ").append(getClassName()).append(" set")
                    .append(uppercase(getFieldName(i))).append("(")
                    .append(fieldType(i)).append(" ").append(getFieldName(i)).append(") {\n");
            classBodyBuilder.append("        this.").append(getFieldName(i)).append(" = ")
                    .append(getFieldName(i)).append(";\n");
            classBodyBuilder.append("        return this;\n");
            classBodyBuilder.append("    }\n");
        }
    }

    private void generateChainableAddForSetAndList(StringBuilder classBodyBuilder) {
        for(int i = 0; i < schema.numFields(); i++) {
            if(schema.getFieldType(i) != FieldType.REFERENCE) {
                continue;
            }
            HollowSchema referencedSchema = dataset.getSchema(schema.getReferencedType(i));
            if(referencedSchema instanceof HollowListSchema || referencedSchema instanceof HollowSetSchema) {
                HollowSchema elementSchema = dataset.getSchema(referencedSchema instanceof HollowListSchema
                        ? ((HollowListSchema) referencedSchema).getElementType()
                        : ((HollowSetSchema) referencedSchema).getElementType());
                String elementType = buildFieldType(elementSchema);
                Class fieldImplementationType = referencedSchema instanceof HollowListSchema
                        ? ArrayList.class : HashSet.class;
                importClasses.add(fieldImplementationType);
                classBodyBuilder.append("    public ").append(getClassName()).append(" addTo")
                        .append(uppercase(getFieldName(i))).append("(")
                        .append(elementType).append(" ").append(lowercase(elementType)).append(") {\n");
                classBodyBuilder.append("        if (this.").append(getFieldName(i)).append(" == null) {\n");
                classBodyBuilder.append("            this.").append(getFieldName(i)).append(" = new ")
                        .append(fieldImplementationType.getSimpleName()).append("<").append(elementType).append(">();\n");
                classBodyBuilder.append("        }\n");
                classBodyBuilder.append("        this.").append(getFieldName(i)).append(".add(")
                        .append(lowercase(elementType)).append(");\n");
                classBodyBuilder.append("        return this;\n");
                classBodyBuilder.append("    }\n");
            }
        }
    }

    private void generateEqualsMethod(StringBuilder classBodyBuilder) {
        classBodyBuilder.append("    public boolean equals(Object other) {\n");
        classBodyBuilder.append("        if (other == this)  return true;\n");
        classBodyBuilder.append("        if (!(other instanceof ").append(getClassName()).append("))\n");
        classBodyBuilder.append("            return false;\n\n");
        classBodyBuilder.append("        ").append(getClassName()).append(" o = (").append(getClassName()).append(") other;\n");
        for(int i = 0; i < schema.numFields(); i++) {
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                    classBodyBuilder.append("        if (o.").append(getFieldName(i)).append(" != ").append(getFieldName(i)).append(") return false;\n");
                    break;
                case BYTES:
                case STRING:
                    importClasses.add(Objects.class);
                    classBodyBuilder.append("        if (!Objects.equals(o.").append(getFieldName(i)).append(", ")
                            .append(getFieldName(i)).append(")) return false;\n");
                    break;
                case REFERENCE:
                    classBodyBuilder.append("        if (o.").append(getFieldName(i)).append(" == null) {\n");
                    classBodyBuilder.append("            if (").append(getFieldName(i)).append(" != null) return false;\n");
                    classBodyBuilder.append("        } else if (!o.").append(getFieldName(i)).append(".equals(").append(getFieldName(i)).append(")) return false;\n");
                    break;
            }
        }
        classBodyBuilder.append("        return true;\n");
        classBodyBuilder.append("    }\n\n");
    }

    private void generateHashCodeMethod(StringBuilder classBodyBuilder) {
        classBodyBuilder.append("    public int hashCode() {\n");
        classBodyBuilder.append("        int hashCode = 1;\n");
        boolean tempExists = false;
        for(int i = 0; i < schema.numFields(); i++) {
            String fieldName = getFieldName(i);
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                    classBodyBuilder.append("        hashCode = hashCode * 31 + (" + fieldName + "? 1231 : 1237);\n");
                    break;
                case DOUBLE:
                    if(!tempExists)
                        classBodyBuilder.append("        long temp;\n");
                    classBodyBuilder.append("        temp = java.lang.Double.doubleToLongBits(" + fieldName + ")\n");
                    classBodyBuilder.append("        hashCode = hashCode * 31 + (int) (temp ^ (temp >>> 32));\n");
                    break;
                case FLOAT:
                    classBodyBuilder.append("        hashCode = hashCode * 31 + java.lang.Float.floatToIntBits(" + fieldName + ");\n");
                    break;
                case INT:
                    classBodyBuilder.append("        hashCode = hashCode * 31 + " + fieldName + ";\n");
                    break;
                case LONG:
                    classBodyBuilder.append("        hashCode = hashCode * 31 + (int) (" + fieldName + " ^ (" + fieldName + " >>> 32));\n");
                    break;
                case BYTES:
                case STRING:
                    importClasses.add(Objects.class);
                    classBodyBuilder.append("        hashCode = hashCode * 31 + Objects.hash(" + fieldName + ");\n");
                    break;
                case REFERENCE:
                    importClasses.add(Objects.class);
                    classBodyBuilder.append("        hashCode = hashCode * 31 + Objects.hash(" + fieldName + ");\n");
                    break;
            }
        }
        classBodyBuilder.append("        return hashCode;\n");
        classBodyBuilder.append("    }\n\n");
    }

    private void generateToStringMethod(StringBuilder classBodyBuilder) {
        classBodyBuilder.append("    public String toString() {\n");
        classBodyBuilder.append("        StringBuilder builder = new StringBuilder(\"").append(getClassName()).append("{\");\n");
        for(int i = 0; i < schema.numFields(); i++) {
            classBodyBuilder.append("        builder.append(\"");
            if(i > 0)
                classBodyBuilder.append(",");
            classBodyBuilder.append(getFieldName(i)).append("=\").append(").append(getFieldName(i)).append(");\n");
        }
        classBodyBuilder.append("        builder.append(\"}\");\n");
        classBodyBuilder.append("        return builder.toString();\n");
        classBodyBuilder.append("    }\n\n");
    }

    private void generateCloneMethod(StringBuilder classBodyBuilder) {
        classBodyBuilder.append("    public ").append(getClassName()).append(" clone() {\n");
        classBodyBuilder.append("        try {\n");
        classBodyBuilder.append("            ").append(getClassName())
                .append(" clone = (" + getClassName() + ") super.clone();\n");
        if(memoizeOrdinal) {
            classBodyBuilder.append("            clone.__assigned_ordinal = -1;\n");
        }
        classBodyBuilder.append("            return clone;\n");
        classBodyBuilder
                .append("        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }\n");
    }

    private boolean fieldNeedsTypeNameAnnotation(int i) {
        if(schema.getFieldType(i) == FieldType.REFERENCE) {
            HollowSchema referencedSchema = dataset.getSchema(schema.getReferencedType(i));
            return !referencedSchema.getName().equals(expectedCollectionClassName(referencedSchema));
        }
        return false;
    }

    private boolean fieldNeedsInlineAnnotation(int i) {
        return schema.getFieldType(i) == FieldType.STRING;
    }

    private String fieldType(int i) {
        switch(schema.getFieldType(i)) {
            case BOOLEAN:
            case BYTES:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case STRING:
                return getJavaScalarType(schema.getFieldType(i));
            case REFERENCE:
                return buildFieldType(dataset.getSchema(schema.getReferencedType(i)));
            default:
                throw new IllegalArgumentException("Field type is unrecognized: " + schema.getFieldType(i));
        }

    }

    private String defaultValue(int i) {
        switch(schema.getFieldType(i)) {
            case BOOLEAN:
                return "false";
            case DOUBLE:
                return "java.lang.Double.NaN";
            case FLOAT:
                return "java.lang.Float.NaN";
            case INT:
                return "java.lang.Integer.MIN_VALUE";
            case LONG:
                return "java.lang.Long.MIN_VALUE";
            case BYTES:
            case STRING:
            case REFERENCE:
                return "null";
        }

        throw new IllegalArgumentException("Field type is unrecognized: " + schema.getFieldType(i));
    }

    private String buildFieldType(HollowSchema referencedSchema) {
        if(referencedSchema instanceof HollowObjectSchema) {
            return buildClassName(referencedSchema.getName(), classNameSuffix);
        } else if(referencedSchema instanceof HollowListSchema) {
            importClasses.add(List.class);
            HollowSchema elementSchema = dataset.getSchema(((HollowListSchema) referencedSchema).getElementType());
            return "List<" + buildFieldType(elementSchema) + ">";
        } else if(referencedSchema instanceof HollowSetSchema) {
            importClasses.add(Set.class);
            HollowSchema elementSchema = dataset.getSchema(((HollowSetSchema) referencedSchema).getElementType());
            return "Set<" + buildFieldType(elementSchema) + ">";
        } else if(referencedSchema instanceof HollowMapSchema) {
            importClasses.add(Map.class);
            HollowSchema keySchema = dataset.getSchema(((HollowMapSchema) referencedSchema).getKeyType());
            HollowSchema valueSchema = dataset.getSchema(((HollowMapSchema) referencedSchema).getValueType());
            return "Map<" + buildFieldType(keySchema) + ", " + buildFieldType(valueSchema) + ">";
        }

        throw new IllegalArgumentException("Schema is unrecognized type " + referencedSchema.getClass().getSimpleName());
    }

    private String expectedCollectionClassName(HollowSchema referencedSchema) {
        if(referencedSchema instanceof HollowObjectSchema) {
            return referencedSchema.getName();
        } else if(referencedSchema instanceof HollowListSchema) {
            importClasses.add(List.class);
            HollowSchema elementSchema = dataset.getSchema(((HollowListSchema) referencedSchema).getElementType());
            return "ListOf" + expectedCollectionClassName(elementSchema);
        } else if(referencedSchema instanceof HollowSetSchema) {
            importClasses.add(Set.class);
            HollowSchema elementSchema = dataset.getSchema(((HollowSetSchema) referencedSchema).getElementType());
            return "SetOf" + expectedCollectionClassName(elementSchema);
        } else if(referencedSchema instanceof HollowMapSchema) {
            importClasses.add(Map.class);
            HollowSchema keySchema = dataset.getSchema(((HollowMapSchema) referencedSchema).getKeyType());
            HollowSchema valueSchema = dataset.getSchema(((HollowMapSchema) referencedSchema).getValueType());
            return "MapOf" + expectedCollectionClassName(keySchema) + "To" + expectedCollectionClassName(valueSchema);
        }
        throw new IllegalArgumentException("Expected HollowCollectionSchema or HollowMapSchema but got " + referencedSchema.getClass().getSimpleName());
    }

    /**
     * Returns a field name that is same for use as a java variable.
     */
    private String getFieldName(int index) {
        return substituteInvalidChars(schema.getFieldName(index));
    }

    private int getIndexFromFieldName(String fieldName) {
        for(int i = 0; i < schema.numFields(); i++) {
            if(getFieldName(i).equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }
}
