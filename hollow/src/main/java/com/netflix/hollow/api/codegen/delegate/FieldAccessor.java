package com.netflix.hollow.api.codegen.delegate;

import com.netflix.hollow.core.schema.HollowObjectSchema;

public abstract class FieldAccessor {

    private final HollowObjectSchema.FieldType fieldType;
    private final String fieldName;

    public FieldAccessor(HollowObjectSchema.FieldType fieldType, String fieldName){
        this.fieldType = fieldType;
        this.fieldName = fieldName;
    }
    public abstract String generateGetCode();

}
