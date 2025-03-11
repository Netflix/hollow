package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class FloatFieldAccessor extends FieldAccessor{

    private final String fieldName;
    public FloatFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.FLOAT, fieldName);
        this.fieldName = fieldName;
    }
    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public float get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(").append(fieldName).append(" == null)\n");
        builder.append("            return Float.NaN;\n");
        builder.append("        return ").append(fieldName).append(".floatValue();\n");
        builder.append("    }\n\n");
        builder.append("    public Float get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
