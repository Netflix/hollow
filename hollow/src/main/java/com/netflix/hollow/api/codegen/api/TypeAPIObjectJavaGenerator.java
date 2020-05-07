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
package com.netflix.hollow.api.codegen.api;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateLookupClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

import com.netflix.hollow.api.codegen.CodeGeneratorConfig;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 *
 * @author dkoszewnik
 *
 */
public class TypeAPIObjectJavaGenerator extends HollowTypeAPIGenerator {

    private final HollowObjectSchema objectSchema;

    private final Set<Class<?>> importClasses = new TreeSet<Class<?>>(new Comparator<Class<?>>() {
        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });

    public TypeAPIObjectJavaGenerator(String apiClassname, String packageName, HollowObjectSchema schema,
            HollowDataset dataset, CodeGeneratorConfig config) {
        super(apiClassname, packageName, schema, dataset, config);
        this.objectSchema = schema;

        this.importClasses.add(HollowObjectTypeAPI.class);
        this.importClasses.add(HollowObjectTypeDataAccess.class);
    }

    @Override
    public String generate() {
        StringBuilder classBodyBuilder = new StringBuilder();

        classBodyBuilder.append("@SuppressWarnings(\"all\")\n");
        classBodyBuilder.append("public class " + className + " extends HollowObjectTypeAPI {\n\n");

        classBodyBuilder.append("    private final ").append(delegateLookupClassname(objectSchema)).append(" delegateLookupImpl;\n\n");

        classBodyBuilder.append(generateConstructor());
        classBodyBuilder.append("\n\n");

        for(int i=0;i<objectSchema.numFields();i++) {
            switch(objectSchema.getFieldType(i)) {
                case BOOLEAN:
                    classBodyBuilder.append(generateBooleanFieldAccessor(i));
                    break;
                case BYTES:
                    classBodyBuilder.append(generateByteArrayFieldAccessor(i));
                    break;
                case DOUBLE:
                    classBodyBuilder.append(generateDoubleFieldAccessor(i));
                    break;
                case FLOAT:
                    classBodyBuilder.append(generateFloatFieldAccessor(i));
                    break;
                case INT:
                    classBodyBuilder.append(generateIntFieldAccessor(i));
                    break;
                case LONG:
                    classBodyBuilder.append(generateLongFieldAccessor(i));
                    break;
                case REFERENCE:
                    classBodyBuilder.append(generateReferenceFieldAccessors(i));
                    break;
                case STRING:
                    classBodyBuilder.append(generateStringFieldAccessors(i));
                    break;
            }

            classBodyBuilder.append("\n\n");

        }

        classBodyBuilder.append("    public ").append(delegateLookupClassname(objectSchema)).append(" getDelegateLookupImpl() {\n");
        classBodyBuilder.append("        return delegateLookupImpl;\n");
        classBodyBuilder.append("    }\n\n");

        classBodyBuilder.append("    @Override\n");
        classBodyBuilder.append("    public ").append(apiClassname).append(" getAPI() {\n");
        classBodyBuilder.append("        return (").append(apiClassname).append(") api;\n");
        classBodyBuilder.append("    }\n\n");

        classBodyBuilder.append("}");

        StringBuilder classBuilder = new StringBuilder();
        appendPackageAndCommonImports(classBuilder, apiClassname);

        for(Class<?> clazz : importClasses) {
            classBuilder.append("import ").append(clazz.getName()).append(";\n");
        }

        classBuilder.append("\n");

        classBuilder.append(classBodyBuilder.toString());

        return classBuilder.toString();
    }

    private String generateConstructor() {
        StringBuilder builder = new StringBuilder();

        builder.append("    public " + className + "(" + apiClassname + " api, HollowObjectTypeDataAccess typeDataAccess) {\n");
        builder.append("        super(api, typeDataAccess, new String[] {\n");

        for(int i=0;i<objectSchema.numFields();i++) {
            builder.append("            \"" + objectSchema.getFieldName(i) + "\"");
            if(i < objectSchema.numFields() - 1)
                builder.append(",");
            builder.append("\n");
        }

        builder.append("        });\n");
        builder.append("        this.delegateLookupImpl = new ").append(delegateLookupClassname(objectSchema)).append("(this);\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateByteArrayFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public byte[] deserializeFrom" + uppercase(fieldName) + "(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleBytes(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("        return getTypeDataAccess().readBytes(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        return builder.toString();
    }

    private String generateStringFieldAccessors(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public String deserializeFrom" + uppercase(fieldName) + "(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleString(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("        return getTypeDataAccess().readString(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        builder.append("    public boolean is" + uppercase(fieldName) + "Equal(int ordinal, String testValue) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleStringEquals(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\", testValue);\n");
        builder.append("        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[" + fieldNum + "], testValue);\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateReferenceFieldAccessors(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));
        String referencedType = substituteInvalidChars(objectSchema.getReferencedType(fieldNum));

        builder.append("    public int deserializeFrom"+ uppercase(fieldName) + "Ordinal(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleReferencedOrdinal(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        builder.append("    public " + typeAPIClassname(referencedType) + " deserializeFrom" + uppercase(fieldName) + "TypeAPI() {\n");
        builder.append("        return getAPI().deserializeFrom").append(uppercase(referencedType)).append("TypeAPI();\n");
        builder.append("    }");

        return builder.toString();
    }

    private String generateDoubleFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public double deserializeFrom").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleDouble(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readDouble(ordinal, fieldIndex["+fieldNum+"]);\n");
        builder.append("    }\n\n");

        builder.append("    public Double deserializeFrom").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        double d;\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1) {\n");
        builder.append("            d = missingDataHandler().handleDouble(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        } else {\n");
        builder.append("            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("            d = getTypeDataAccess().readDouble(ordinal, fieldIndex["+fieldNum+"]);\n");
        builder.append("        }\n");
        builder.append("        return Double.isNaN(d) ? null : Double.valueOf(d);\n");
        builder.append("    }\n\n");

        importClasses.add(HollowObjectWriteRecord.class);

        return builder.toString();
    }

    private String generateFloatFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public float deserializeFrom").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleFloat(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readFloat(ordinal, fieldIndex["+fieldNum+"]);\n");
        builder.append("    }\n\n");

        builder.append("    public Float deserializeFrom").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        float f;\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1) {\n");
        builder.append("            f = missingDataHandler().handleFloat(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        } else {\n");
        builder.append("            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("            f = getTypeDataAccess().readFloat(ordinal, fieldIndex["+fieldNum+"]);\n");
        builder.append("        }");
        builder.append("        return Float.isNaN(f) ? null : Float.valueOf(f);\n");
        builder.append("    }\n\n");

        importClasses.add(HollowObjectWriteRecord.class);

        return builder.toString();
    }

    private String generateLongFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public long deserializeFrom").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleLong(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readLong(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        builder.append("    public Long deserializeFrom").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        long l;\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1) {\n");
        builder.append("            l = missingDataHandler().handleLong(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        } else {\n");
        builder.append("            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("            l = getTypeDataAccess().readLong(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("        }\n");
        builder.append("        if(l == Long.MIN_VALUE)\n");
        builder.append("            return null;\n");
        builder.append("        return Long.valueOf(l);\n");
        builder.append("    }\n\n");

        return builder.toString();
    }

    private String generateIntFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public int deserializeFrom").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleInt(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readInt(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        builder.append("    public Integer deserializeFrom").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        int i;\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1) {\n");
        builder.append("            i = missingDataHandler().handleInt(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        } else {\n");
        builder.append("            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[" + fieldNum + "]);\n");
        builder.append("            i = getTypeDataAccess().readInt(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("        }\n");
        builder.append("        if(i == Integer.MIN_VALUE)\n");
        builder.append("            return null;\n");
        builder.append("        return Integer.valueOf(i);\n");
        builder.append("    }\n\n");

        return builder.toString();
    }

    private String generateBooleanFieldAccessor(int fieldNum) {
        StringBuilder builder = new StringBuilder();

        String fieldName = substituteInvalidChars(objectSchema.getFieldName(fieldNum));

        builder.append("    public boolean deserializeFrom").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return Boolean.TRUE.equals(missingDataHandler().handleBoolean(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\"));\n");
        builder.append("        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[" + fieldNum + "]));\n");
        builder.append("    }\n\n");

        builder.append("    public Boolean deserializeFrom").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        if(fieldIndex[" + fieldNum +"] == -1)\n");
        builder.append("            return missingDataHandler().handleBoolean(\"").append(objectSchema.getName()).append("\", ordinal, \"").append(fieldName).append("\");\n");
        builder.append("        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[" + fieldNum + "]);\n");
        builder.append("    }\n\n");

        return builder.toString();
    }
}
