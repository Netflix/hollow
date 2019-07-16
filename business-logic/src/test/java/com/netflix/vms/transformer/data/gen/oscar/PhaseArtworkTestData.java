package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class PhaseArtworkTestData extends HollowTestObjectRecord {

    PhaseArtworkTestData(PhaseArtworkField... fields){
        super(fields);
    }

    public static PhaseArtworkTestData PhaseArtwork(PhaseArtworkField... fields) {
        return new PhaseArtworkTestData(fields);
    }

    public PhaseArtworkTestData update(PhaseArtworkField... fields){
        super.addFields(fields);
        return this;
    }

    public long assetId() {
        Field f = super.getField("assetId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public String fileId() {
        Field f = super.getField("fileId");
        return f == null ? null : (String)f.value;
    }

    public Boolean isSynthetic() {
        Field f = super.getField("isSynthetic");
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

    public static class PhaseArtworkField extends HollowTestObjectRecord.Field {

        private PhaseArtworkField(String name, Object val) { super(name, val); }

        public static PhaseArtworkField assetId(long val) {
            return new PhaseArtworkField("assetId", val);
        }

        public static PhaseArtworkField fileId(String val) {
            return new PhaseArtworkField("fileId", val);
        }

        public static PhaseArtworkField isSynthetic(boolean val) {
            return new PhaseArtworkField("isSynthetic", val);
        }

        public static PhaseArtworkField dateCreated(DateTestData val) {
            return new PhaseArtworkField("dateCreated", val);
        }

        public static PhaseArtworkField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static PhaseArtworkField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static PhaseArtworkField lastUpdated(DateTestData val) {
            return new PhaseArtworkField("lastUpdated", val);
        }

        public static PhaseArtworkField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static PhaseArtworkField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static PhaseArtworkField createdBy(StringTestData val) {
            return new PhaseArtworkField("createdBy", val);
        }

        public static PhaseArtworkField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static PhaseArtworkField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static PhaseArtworkField updatedBy(StringTestData val) {
            return new PhaseArtworkField("updatedBy", val);
        }

        public static PhaseArtworkField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static PhaseArtworkField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseArtwork", 7);

    static {
        SCHEMA.addField("assetId", FieldType.LONG);
        SCHEMA.addField("fileId", FieldType.STRING);
        SCHEMA.addField("isSynthetic", FieldType.BOOLEAN);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}