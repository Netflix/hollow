package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class IntFieldAccessor extends FieldAccessor {


    private final String fieldName;
    public IntFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.INT, fieldName);
        this.fieldName = fieldName;
    }
    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public int get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(").append(fieldName).append(" == null)\n");
        builder.append("            return Integer.MIN_VALUE;\n");
        builder.append("        return ").append(fieldName).append(".intValue();\n");
        builder.append("    }\n\n");
        builder.append("    public Integer get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
