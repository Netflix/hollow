package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.ImageTypeTestData.ImageTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class PhaseRequiredImageTypeTestData extends HollowTestObjectRecord {

    PhaseRequiredImageTypeTestData(PhaseRequiredImageTypeField... fields){
        super(fields);
    }

    public static PhaseRequiredImageTypeTestData PhaseRequiredImageType(PhaseRequiredImageTypeField... fields) {
        return new PhaseRequiredImageTypeTestData(fields);
    }

    public PhaseRequiredImageTypeTestData update(PhaseRequiredImageTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public ImageTypeTestData imageTypeRef() {
        Field f = super.getField("imageType");
        return f == null ? null : (ImageTypeTestData)f.value;
    }

    public String imageType() {
        Field f = super.getField("imageType");
        if(f == null) return null;
        ImageTypeTestData ref = (ImageTypeTestData)f.value;
        return ref.value();
    }

    public Boolean imageSwapRequired() {
        Field f = super.getField("imageSwapRequired");
        return f == null ? null : (Boolean)f.value;
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

    public static class PhaseRequiredImageTypeField extends HollowTestObjectRecord.Field {

        private PhaseRequiredImageTypeField(String name, Object val) { super(name, val); }

        public static PhaseRequiredImageTypeField imageType(ImageTypeTestData val) {
            return new PhaseRequiredImageTypeField("imageType", val);
        }

        public static PhaseRequiredImageTypeField imageType(ImageTypeField... fields) {
            return imageType(new ImageTypeTestData(fields));
        }

        public static PhaseRequiredImageTypeField imageType(String val) {
            return imageType(ImageTypeField.value(val));
        }

        public static PhaseRequiredImageTypeField imageSwapRequired(boolean val) {
            return new PhaseRequiredImageTypeField("imageSwapRequired", val);
        }

        public static PhaseRequiredImageTypeField dateCreated(DateTestData val) {
            return new PhaseRequiredImageTypeField("dateCreated", val);
        }

        public static PhaseRequiredImageTypeField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static PhaseRequiredImageTypeField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static PhaseRequiredImageTypeField lastUpdated(DateTestData val) {
            return new PhaseRequiredImageTypeField("lastUpdated", val);
        }

        public static PhaseRequiredImageTypeField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static PhaseRequiredImageTypeField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static PhaseRequiredImageTypeField createdBy(StringTestData val) {
            return new PhaseRequiredImageTypeField("createdBy", val);
        }

        public static PhaseRequiredImageTypeField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static PhaseRequiredImageTypeField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static PhaseRequiredImageTypeField updatedBy(StringTestData val) {
            return new PhaseRequiredImageTypeField("updatedBy", val);
        }

        public static PhaseRequiredImageTypeField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static PhaseRequiredImageTypeField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseRequiredImageType", 6);

    static {
        SCHEMA.addField("imageType", FieldType.REFERENCE, "ImageType");
        SCHEMA.addField("imageSwapRequired", FieldType.BOOLEAN);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}