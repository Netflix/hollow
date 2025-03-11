package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class LongFieldAccessor extends FieldAccessor{

    private final String fieldName;
    public LongFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.LONG, fieldName);
        this.fieldName = fieldName;
    }
    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public long get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        builder.append("        if(").append(fieldName).append(" == null)\n");
        builder.append("            return Long.MIN_VALUE;\n");
        builder.append("        return ").append(fieldName).append(".longValue();\n");
        builder.append("    }\n\n");
        builder.append("    public Long get").append(uppercase(fieldName)).append("Boxed(int ordinal) {\n");
        builder.append("        return ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
