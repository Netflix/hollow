package com.netflix.hollow.explorer.ui.model;

import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;

public class SchemaDisplayField {
    
    private final String fieldName;
    private final FieldType fieldType;
    private final boolean isSearchable;
    
    private final SchemaDisplay referencedType;
    
    public SchemaDisplayField(HollowCollectionSchema parentSchema) {
        this.fieldName = "element";
        this.fieldType = FieldType.REFERENCE;
        this.isSearchable = false;
        this.referencedType = new SchemaDisplay(parentSchema.getElementTypeState().getSchema());
    }
    
    public SchemaDisplayField(HollowMapSchema parentSchema, int fieldNumber) {
        this.fieldName = fieldNumber == 0 ? "key" : "value";
        this.fieldType = FieldType.REFERENCE;
        this.isSearchable = false;
        this.referencedType = fieldNumber == 0 ? new SchemaDisplay(parentSchema.getKeyTypeState().getSchema()) : new SchemaDisplay(parentSchema.getValueTypeState().getSchema());
    }
    
    public SchemaDisplayField(HollowObjectSchema parentSchema, int fieldNumber) {
        this.fieldName = parentSchema.getFieldName(fieldNumber);
        this.fieldType = parentSchema.getFieldType(fieldNumber);
        this.isSearchable = isSearchable(parentSchema, fieldNumber);
        this.referencedType = fieldType == FieldType.REFERENCE ? new SchemaDisplay(parentSchema.getReferencedTypeState(fieldNumber).getSchema()) : null;
    }
    
    private boolean isSearchable(HollowObjectSchema schema, int fieldNumber) {
        if(schema.getFieldType(fieldNumber) == FieldType.REFERENCE) {
            if(schema.getReferencedTypeState(fieldNumber).getSchema().getSchemaType() != SchemaType.OBJECT)
                return false;
            HollowObjectSchema refObjSchema = (HollowObjectSchema)schema.getReferencedTypeState(fieldNumber).getSchema();
            if(refObjSchema.numFields() != 1)
                return false;
            
            return isSearchable(refObjSchema, 0);
        }
        
        return true;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public FieldType getFieldType() {
        return fieldType;
    }
    
    public boolean isSearchable() {
        return isSearchable;
    }
    
    public SchemaDisplay getReferencedType() {
        return referencedType;
    }

}
