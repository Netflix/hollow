package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class RolloutCountryTestData extends HollowTestObjectRecord {

    RolloutCountryTestData(RolloutCountryField... fields){
        super(fields);
    }

    public static RolloutCountryTestData RolloutCountry(RolloutCountryField... fields) {
        return new RolloutCountryTestData(fields);
    }

    public RolloutCountryTestData update(RolloutCountryField... fields){
        super.addFields(fields);
        return this;
    }

    public String countryCode() {
        Field f = super.getField("countryCode");
        return f == null ? null : (String)f.value;
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

    public static class RolloutCountryField extends HollowTestObjectRecord.Field {

        private RolloutCountryField(String name, Object val) { super(name, val); }

        public static RolloutCountryField countryCode(String val) {
            return new RolloutCountryField("countryCode", val);
        }

        public static RolloutCountryField dateCreated(DateTestData val) {
            return new RolloutCountryField("dateCreated", val);
        }

        public static RolloutCountryField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static RolloutCountryField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static RolloutCountryField lastUpdated(DateTestData val) {
            return new RolloutCountryField("lastUpdated", val);
        }

        public static RolloutCountryField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static RolloutCountryField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static RolloutCountryField createdBy(StringTestData val) {
            return new RolloutCountryField("createdBy", val);
        }

        public static RolloutCountryField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static RolloutCountryField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static RolloutCountryField updatedBy(StringTestData val) {
            return new RolloutCountryField("updatedBy", val);
        }

        public static RolloutCountryField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static RolloutCountryField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutCountry", 5);

    static {
        SCHEMA.addField("countryCode", FieldType.STRING);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}