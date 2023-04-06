package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.uppercase;

public class BytesFieldAccessor extends FieldAccessor{
    private final String fieldName;
    public BytesFieldAccessor(String fieldName){
        super(HollowObjectSchema.FieldType.BYTES, fieldName);
        this.fieldName = fieldName;
    }


    @Override
    public String generateGetCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("    public byte[] get").append(uppercase(fieldName)).append("(int ordinal) {\n");
        // we need the cast to get around http://findbugs.sourceforge.net/bugDescriptions.html#EI_EXPOSE_REP
        builder.append("        return (byte[]) ").append(fieldName).append(";\n");
        builder.append("    }\n\n");
        return builder.toString();
    }
}
