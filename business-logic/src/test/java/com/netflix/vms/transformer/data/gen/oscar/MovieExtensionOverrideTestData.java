package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.AttributeValueTestData.AttributeValueField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.OverrideEntityTypeTestData.OverrideEntityTypeField;
import com.netflix.vms.transformer.data.gen.oscar.OverrideEntityValueTestData.OverrideEntityValueField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieExtensionOverrideTestData extends HollowTestObjectRecord {

    MovieExtensionOverrideTestData(MovieExtensionOverrideField... fields){
        super(fields);
    }

    public static MovieExtensionOverrideTestData MovieExtensionOverride(MovieExtensionOverrideField... fields) {
        return new MovieExtensionOverrideTestData(fields);
    }

    public MovieExtensionOverrideTestData update(MovieExtensionOverrideField... fields){
        super.addFields(fields);
        return this;
    }

    public OverrideEntityTypeTestData entityTypeRef() {
        Field f = super.getField("entityType");
        return f == null ? null : (OverrideEntityTypeTestData)f.value;
    }

    public String entityType() {
        Field f = super.getField("entityType");
        if(f == null) return null;
        OverrideEntityTypeTestData ref = (OverrideEntityTypeTestData)f.value;
        return ref._name();
    }

    public OverrideEntityValueTestData entityValueRef() {
        Field f = super.getField("entityValue");
        return f == null ? null : (OverrideEntityValueTestData)f.value;
    }

    public String entityValue() {
        Field f = super.getField("entityValue");
        if(f == null) return null;
        OverrideEntityValueTestData ref = (OverrideEntityValueTestData)f.value;
        return ref.value();
    }

    public AttributeValueTestData attributeValueRef() {
        Field f = super.getField("attributeValue");
        return f == null ? null : (AttributeValueTestData)f.value;
    }

    public String attributeValue() {
        Field f = super.getField("attributeValue");
        if(f == null) return null;
        AttributeValueTestData ref = (AttributeValueTestData)f.value;
        return ref.value();
    }

    public DateTestData dateCreatedRef() {
        Field f = super.getField("dateCreated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long dateCreated() {
        Field f = super.getField("dateCreated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public DateTestData lastUpdatedRef() {
        Field f = super.getField("lastUpdated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long lastUpdated() {
        Field f = super.getField("lastUpdated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public StringTestData createdByRef() {
        Field f = super.getField("createdBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String createdBy() {
        Field f = super.getField("createdBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData updatedByRef() {
        Field f = super.getField("updatedBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String updatedBy() {
        Field f = super.getField("updatedBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class MovieExtensionOverrideField extends HollowTestObjectRecord.Field {

        private MovieExtensionOverrideField(String name, Object val) { super(name, val); }

        public static MovieExtensionOverrideField entityType(OverrideEntityTypeTestData val) {
            return new MovieExtensionOverrideField("entityType", val);
        }

        public static MovieExtensionOverrideField entityType(OverrideEntityTypeField... fields) {
            return entityType(new OverrideEntityTypeTestData(fields));
        }

        public static MovieExtensionOverrideField entityType(String val) {
            return entityType(OverrideEntityTypeField._name(val));
        }

        public static MovieExtensionOverrideField entityValue(OverrideEntityValueTestData val) {
            return new MovieExtensionOverrideField("entityValue", val);
        }

        public static MovieExtensionOverrideField entityValue(OverrideEntityValueField... fields) {
            return entityValue(new OverrideEntityValueTestData(fields));
        }

        public static MovieExtensionOverrideField entityValue(String val) {
            return entityValue(OverrideEntityValueField.value(val));
        }

        public static MovieExtensionOverrideField attributeValue(AttributeValueTestData val) {
            return new MovieExtensionOverrideField("attributeValue", val);
        }

        public static MovieExtensionOverrideField attributeValue(AttributeValueField... fields) {
            return attributeValue(new AttributeValueTestData(fields));
        }

        public static MovieExtensionOverrideField attributeValue(String val) {
            return attributeValue(AttributeValueField.value(val));
        }

        public static MovieExtensionOverrideField dateCreated(DateTestData val) {
            return new MovieExtensionOverrideField("dateCreated", val);
        }

        public static MovieExtensionOverrideField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieExtensionOverrideField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieExtensionOverrideField lastUpdated(DateTestData val) {
            return new MovieExtensionOverrideField("lastUpdated", val);
        }

        public static MovieExtensionOverrideField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieExtensionOverrideField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieExtensionOverrideField createdBy(StringTestData val) {
            return new MovieExtensionOverrideField("createdBy", val);
        }

        public static MovieExtensionOverrideField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieExtensionOverrideField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieExtensionOverrideField updatedBy(StringTestData val) {
            return new MovieExtensionOverrideField("updatedBy", val);
        }

        public static MovieExtensionOverrideField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieExtensionOverrideField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieExtensionOverride", 7);

    static {
        SCHEMA.addField("entityType", FieldType.REFERENCE, "OverrideEntityType");
        SCHEMA.addField("entityValue", FieldType.REFERENCE, "OverrideEntityValue");
        SCHEMA.addField("attributeValue", FieldType.REFERENCE, "AttributeValue");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}