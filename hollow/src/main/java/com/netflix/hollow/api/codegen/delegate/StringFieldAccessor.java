package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class StringFieldAccessor extends FieldAccessor{


    private final String fieldName;
    public StringFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.STRING, fieldName);
        this.fieldName = fieldName;
    }
    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public String get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        builder.append("    public boolean is").append(uppercase(fieldName)).append("Equal(int ordinal, String testValue) {\n");
        builder.append("        if(testValue == null)\n");
        builder.append("            return ").append(fieldName).append(" == null;\n");
        builder.append("        return testValue.equals(").append(fieldName).append(");\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
