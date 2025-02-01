package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class BooleanFieldAccessor extends FieldAccessor{

    private final String fieldName;
    public BooleanFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.BOOLEAN, fieldName);
        this.fieldName = fieldName;
    }
    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public boolean get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(").append(fieldName).append(" == null)\n");
        builder.append("            return false;\n");
        builder.append("        return ").append(fieldName).append(".booleanValue();\n");
        builder.append("    }\n\n");
        builder.append("    public Boolean get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
