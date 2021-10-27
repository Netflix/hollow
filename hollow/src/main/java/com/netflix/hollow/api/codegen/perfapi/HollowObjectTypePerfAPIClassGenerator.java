/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.codegen.perfapi;

import com.netflix.hollow.api.codegen.HollowCodeGenerationUtils;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Set;

class HollowObjectTypePerfAPIClassGenerator {

    private final HollowObjectSchema schema;
    private final String packageName;
    private final Set<String> checkFieldExistsMethods;

    public HollowObjectTypePerfAPIClassGenerator(HollowObjectSchema schema, String packageName, Set<String> checkFieldExistsMethods) {
        this.schema = schema;
        this.packageName = packageName;
        this.checkFieldExistsMethods = checkFieldExistsMethods;
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();

        builder.append("package " + packageName + ";\n\n");

        builder.append("import com.netflix.hollow.api.perfapi.HollowObjectTypePerfAPI;\n" +
                "import com.netflix.hollow.api.perfapi.HollowPerformanceAPI;\n" +
                "import com.netflix.hollow.api.perfapi.Ref;\n" +
                "import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;\n\n");

        builder.append("public class " + schema.getName() + "PerfAPI extends HollowObjectTypePerfAPI {\n\n");

        builder.append("    public static final String fieldNames[] = { ");
        for(int i=0;i<schema.numFields();i++) {
            if(i > 0)
                builder.append(", ");
            builder.append("\"" + schema.getFieldName(i) + "\"");
        }

        builder.append(" };\n\n");

        builder.append("    public " + schema.getName() + "PerfAPI(HollowDataAccess dataAccess, String typeName, HollowPerformanceAPI api) {\n");
        builder.append("        super(dataAccess, typeName, api, fieldNames);\n");
        builder.append("    }\n\n");

        for(int i=0;i<schema.numFields();i++) {
            FieldType fieldType = schema.getFieldType(i);
            String fieldName = schema.getFieldName(i);
            String referencedType = schema.getReferencedType(i);
            appendFieldMethod(builder, fieldType, fieldName, i, referencedType);
        }

        builder.append("}");

        return builder.toString();
    }

    public void appendFieldMethod(StringBuilder builder, FieldType fieldType, String fieldName, int fieldIdx, String referencedType) {
        String type = fieldType.name();
        if(fieldType == FieldType.REFERENCE)
            type += " (" + referencedType + ")";

        builder.append("    /**\n" +
                "     * <i>"+schema.getName() + "." + fieldName +"</i><br/>\n" +
                "     * <b>" + type + "</b>\n" +
                "     */\n");

        switch(fieldType) {
            case INT:
                builder.append("    public int get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readInt(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");

                builder.append("    public Integer get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Boxed(long ref) {\n");
                builder.append("        int val = typeAccess.readInt(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("        if(val == Integer.MIN_VALUE)\n");
                builder.append("            return null;\n");
                builder.append("        return val;\n");
                builder.append("    }\n\n");
                break;
            case LONG:
                builder.append("    public long get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readLong(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");

                builder.append("    public Long get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Boxed(long ref) {\n");
                builder.append("        long val = typeAccess.readLong(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("        if(val == Long.MIN_VALUE)\n");
                builder.append("            return null;\n");
                builder.append("        return val;\n");
                builder.append("    }\n\n");
                break;
            case FLOAT:
                builder.append("    public float get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readFloat(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");

                builder.append("    public Float get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Boxed(long ref) {\n");
                builder.append("        float val = typeAccess.readFloat(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("        if(Float.isNaN(val))\n");
                builder.append("            return null;\n");
                builder.append("        return val;\n");
                builder.append("    }\n\n");
                break;
            case DOUBLE:
                builder.append("    public double get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readDouble(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");

                builder.append("    public Double get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Boxed(long ref) {\n");
                builder.append("        double val = typeAccess.readDouble(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("        if(Double.isNaN(val))\n");
                builder.append("            return null;\n");
                builder.append("        return val;\n");
                builder.append("    }\n\n");
                break;
            case BOOLEAN:
                builder.append("    public boolean get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return Boolean.TRUE.equals(typeAccess.readBoolean(ordinal(ref), fieldIdx[" + fieldIdx + "]));\n");
                builder.append("    }\n\n");

                builder.append("    public Boolean get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Boxed(long ref) {\n");
                builder.append("        return typeAccess.readBoolean(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");
                break;
            case STRING:
                builder.append("    public String get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readString(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");

                builder.append("    public boolean is" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Equal(long ref, String testValue) {\n");
                builder.append("        return typeAccess.isStringFieldEqual(ordinal(ref), fieldIdx[" + fieldIdx + "], testValue);\n");
                builder.append("    }\n\n");
                break;
            case BYTES:
                builder.append("    public byte[] get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "(long ref) {\n");
                builder.append("        return typeAccess.readBytes(ordinal(ref), fieldIdx[" + fieldIdx + "]);\n");
                builder.append("    }\n\n");
                break;
            case REFERENCE:
                builder.append("    public long get" + HollowCodeGenerationUtils.upperFirstChar(fieldName) + "Ref(long ref) {\n");
                builder.append("        return Ref.toRefWithTypeMasked(refMaskedTypeIdx[" + fieldIdx + "], typeAccess.readOrdinal(ordinal(ref), fieldIdx[" + fieldIdx + "]));\n");
                builder.append("    }\n\n");
                break;
        }
        
        if(checkFieldExistsMethods.contains(schema.getName() + "." + fieldName)) {
            builder.append("    public boolean " + fieldName + "FieldExists() {\n");
            builder.append("        return fieldIdx[" + fieldIdx + "] != -1;\n");
            builder.append("    }\n\n");
        }

    }

}
