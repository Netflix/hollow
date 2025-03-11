package com.netflix.hollow.api.codegen.delegate;


import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class DoubleFieldAccessor extends FieldAccessor {
    private final String fieldName;
    public DoubleFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.DOUBLE, fieldName);
        this.fieldName = fieldName;
    }

    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public double get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(").append(fieldName).append(" == null)\n");
        builder.append("            return Double.NaN;\n");
        builder.append("        return ").append(fieldName).append(".doubleValue();\n");
        builder.append("    }\n\n");
        builder.append("    public Double get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
