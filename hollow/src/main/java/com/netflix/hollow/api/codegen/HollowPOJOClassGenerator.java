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
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains template logic for generating POJOs.  Not intended for external consumption.
 * 
 * @see HollowPOJOGenerator
 */
public class HollowPOJOClassGenerator implements HollowJavaFileGenerator {
    
    private final Collection<HollowSchema> allSchemas;
    private final HollowObjectSchema schema;

    private final String className;
    private final String classNameSuffix;
    private final String packageName;
    private final boolean memoizeOrdinal;
    private final Set<Class<?>> importClasses;

    public HollowPOJOClassGenerator(Collection<HollowSchema> allSchemas, HollowObjectSchema schema, String packageName) {
        this(allSchemas, schema, packageName, null);
    }

    public HollowPOJOClassGenerator(Collection<HollowSchema> allSchemas, HollowObjectSchema schema, String packageName, String classNameSuffix) {
        this(allSchemas, schema, packageName, classNameSuffix, false);
    }
    
    public HollowPOJOClassGenerator(Collection<HollowSchema> allSchemas, HollowObjectSchema schema, String packageName, String classNameSuffix, boolean memoizeOrdinal) {
        this.allSchemas = allSchemas;
        this.schema = schema;
        this.packageName = packageName;
        this.classNameSuffix = classNameSuffix;
        this.className = buildClassName(schema.getName(), classNameSuffix);
        this.importClasses = new HashSet<Class<?>>();
        this.memoizeOrdinal = memoizeOrdinal;
    }
    
    private static String buildClassName(String name, String suffix) {
        if (suffix == null) return name;
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
        classBodyBuilder.append("\n@SuppressWarnings(\"all\")\n");
        classBodyBuilder.append("@HollowTypeName(name=\"").append(schema.getName()).append("\")\n");
        classBodyBuilder.append("public class ").append(getClassName()).append(" implements Cloneable {\n\n");

        for(int i=0;i<schema.numFields();i++) {
            if(fieldNeedsTypeNameAnnotation(i)) {
                classBodyBuilder.append("    @HollowTypeName(name=\"").append(schema.getReferencedType(i)).append("\")\n");
            }

            classBodyBuilder.append("    public ");

            classBodyBuilder.append(fieldType(i));

            classBodyBuilder.append(" ").append(schema.getFieldName(i)).append(" = ").append(defaultValue(i)).append(";\n");
        }

        classBodyBuilder.append("\n");

        if(schema.numFields() == 1) {
            classBodyBuilder.append("    public ").append(getClassName()).append("() { }\n\n");

            classBodyBuilder.append("    public ").append(getClassName()).append("(").append(fieldType(0)).append(" value) {\n");
            classBodyBuilder.append("        this.").append(schema.getFieldName(0)).append(" = value;\n");
            classBodyBuilder.append("    }\n\n");

            if(schema.getFieldType(0) == FieldType.STRING) {
                classBodyBuilder.append("    public ").append(getClassName()).append("(String value) {\n");
                classBodyBuilder.append("        this.").append(schema.getFieldName(0)).append(" = value.toCharArray();\n");
                classBodyBuilder.append("    }\n\n");
            }
        }

        classBodyBuilder.append("    public boolean equals(Object other) {\n");
        classBodyBuilder.append("        if(other == this)  return true;\n");
        classBodyBuilder.append("        if(!(other instanceof ").append(getClassName()).append("))\n");
        classBodyBuilder.append("            return false;\n\n");
        classBodyBuilder.append("        ").append(getClassName()).append(" o = (").append(getClassName()).append(") other;\n");
        for(int i=0;i<schema.numFields();i++) {
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                    classBodyBuilder.append("        if(o.").append(schema.getFieldName(i)).append(" != ").append(schema.getFieldName(i)).append(") return false;\n");
                    break;
                case BYTES:
                case STRING:
                    importClasses.add(Arrays.class);
                    classBodyBuilder.append("        if(!Arrays.equals(o.").append(schema.getFieldName(i)).append(", ").append(schema.getFieldName(i)).append(")) return false;\n");
                    break;
                case REFERENCE:
                    classBodyBuilder.append("        if(o.").append(schema.getFieldName(i)).append(" == null) {\n");
                    classBodyBuilder.append("            if(").append(schema.getFieldName(i)).append(" != null) return false;\n");
                    classBodyBuilder.append("        } else if(!o.").append(schema.getFieldName(i)).append(".equals(").append(schema.getFieldName(i)).append(")) return false;\n");
                    break;
            }
        }
        classBodyBuilder.append("        return true;\n");
        classBodyBuilder.append("    }\n\n");


        classBodyBuilder.append("    public int hashCode() {\n");
        classBodyBuilder.append("        int hashCode = 1;\n");
        boolean tempExists = false;
        for(int i=0;i<schema.numFields();i++) {
            String fieldName = schema.getFieldName(i);
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
                    classBodyBuilder.append("        hashCode = hashCode * 31 + (int) (" + fieldName + " ^ ("+ fieldName + " >>> 32));\n");
                    break;
                case BYTES:
                case STRING:
                    importClasses.add(Arrays.class);
                    classBodyBuilder.append("        hashCode = hashCode * 31 + Arrays.hashCode(" + fieldName + ");\n");
                    break;
                case REFERENCE:
                    classBodyBuilder.append("        hashCode = hashCode * 31 + (" + fieldName + " == null ? 1237 : " + fieldName + ".hashCode());\n");
                    break;
            }
        }
        classBodyBuilder.append("        return hashCode;\n");
        classBodyBuilder.append("    }\n\n");

        classBodyBuilder.append("    public String toString() {\n");
        classBodyBuilder.append("        StringBuilder builder = new StringBuilder(\"").append(getClassName()).append("{\");\n");
        for(int i=0;i<schema.numFields();i++) {
            classBodyBuilder.append("        builder.append(\"");
            if(i > 0)
                classBodyBuilder.append(",");
            classBodyBuilder.append(schema.getFieldName(i)).append("=\").append(").append(schema.getFieldName(i)).append(");\n");
        }
        classBodyBuilder.append("        builder.append(\"}\");\n");
        classBodyBuilder.append("        return builder.toString();\n");
        classBodyBuilder.append("    }\n\n");


        classBodyBuilder.append("    public ").append(getClassName()).append(" clone() {\n");
        classBodyBuilder.append("        try {\n");
        classBodyBuilder.append("            ").append(getClassName())
            .append(" clone = (" + getClassName() + ")super.clone();\n");
        if (memoizeOrdinal) {
            classBodyBuilder.append("            clone.__assigned_ordinal = -1;\n");
        }
        classBodyBuilder.append("            return clone;\n");
        classBodyBuilder
                .append("        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }\n");
        classBodyBuilder.append("    }\n\n");

        if (memoizeOrdinal) {
            classBodyBuilder.append("    private int __assigned_ordinal = -1;\n");
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

    private boolean fieldNeedsTypeNameAnnotation(int i) {
        if(schema.getFieldType(i) == FieldType.REFERENCE) {
            HollowSchema referencedSchema = findSchema(schema.getReferencedType(i));
            return !referencedSchema.getName().equals(expectedCollectionClassName(referencedSchema));
        }
        return false;
    }

    private String fieldType(int i) {
        switch(schema.getFieldType(i)) {
            case BOOLEAN:
                return "boolean";
            case BYTES:
                return "byte[]";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case INT:
                return "int";
            case LONG:
                return "long";
            case STRING:
                return "char[]";
            case REFERENCE:
                HollowSchema referencedSchema = findSchema(schema.getReferencedType(i));
                return buildFieldType(referencedSchema);
        }

        throw new IllegalArgumentException("Field type is unrecognized: " + schema.getFieldType(i));
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
            HollowSchema elementSchema = findSchema(((HollowListSchema)referencedSchema).getElementType());
            return "List<" + buildFieldType(elementSchema) + ">";
        } else if(referencedSchema instanceof HollowSetSchema) {
            importClasses.add(Set.class);
            HollowSchema elementSchema = findSchema(((HollowSetSchema)referencedSchema).getElementType());
            return "Set<" + buildFieldType(elementSchema) + ">";
        } else if(referencedSchema instanceof HollowMapSchema) {
            importClasses.add(Map.class);
            HollowSchema keySchema = findSchema(((HollowMapSchema)referencedSchema).getKeyType());
            HollowSchema valueSchema = findSchema(((HollowMapSchema)referencedSchema).getValueType());
            return "Map<" + buildFieldType(keySchema) + ", " + buildFieldType(valueSchema) + ">";
        }

        throw new IllegalArgumentException("Schema is unrecognized type " + referencedSchema.getClass().getSimpleName());
    }

    private String expectedCollectionClassName(HollowSchema referencedSchema) {
        if(referencedSchema instanceof HollowObjectSchema) {
            return referencedSchema.getName();
        } else if(referencedSchema instanceof HollowListSchema) {
            importClasses.add(List.class);
            HollowSchema elementSchema = findSchema(((HollowListSchema)referencedSchema).getElementType());
            return "ListOf" + expectedCollectionClassName(elementSchema);
        } else if(referencedSchema instanceof HollowSetSchema) {
            importClasses.add(Set.class);
            HollowSchema elementSchema = findSchema(((HollowSetSchema)referencedSchema).getElementType());
            return "SetOf" + expectedCollectionClassName(elementSchema);
        } else if(referencedSchema instanceof HollowMapSchema) {
            importClasses.add(Map.class);
            HollowSchema keySchema = findSchema(((HollowMapSchema)referencedSchema).getKeyType());
            HollowSchema valueSchema = findSchema(((HollowMapSchema)referencedSchema).getValueType());
            return "MapOf" + expectedCollectionClassName(keySchema) + "To" + expectedCollectionClassName(valueSchema);
        }

        throw new IllegalArgumentException("Expected HollowCollectionSchema or HollowMapSchema but got " + referencedSchema.getClass().getSimpleName());
    }


    private HollowSchema findSchema(String schemaName) {
        for(HollowSchema schema : allSchemas) {
            if(schema.getName().equals(schemaName))
                return schema;
        }
        throw new IllegalArgumentException("Schema " + schemaName + " does not exist!  Referenced by type " + schema.getName());
    }
}