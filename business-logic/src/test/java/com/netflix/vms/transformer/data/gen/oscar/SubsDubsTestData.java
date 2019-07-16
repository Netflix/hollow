package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.CountryStringTestData.CountryStringField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class SubsDubsTestData extends HollowTestObjectRecord {

    SubsDubsTestData(SubsDubsField... fields){
        super(fields);
    }

    public static SubsDubsTestData SubsDubs(SubsDubsField... fields) {
        return new SubsDubsTestData(fields);
    }

    public SubsDubsTestData update(SubsDubsField... fields){
        super.addFields(fields);
        return this;
    }

    public MovieIdTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (MovieIdTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        MovieIdTestData ref = (MovieIdTestData)f.value;
        return ref.value();
    }

    public CountryStringTestData countryCodeRef() {
        Field f = super.getField("countryCode");
        return f == null ? null : (CountryStringTestData)f.value;
    }

    public String countryCode() {
        Field f = super.getField("countryCode");
        if(f == null) return null;
        CountryStringTestData ref = (CountryStringTestData)f.value;
        return ref.value();
    }

    public Boolean subsRequired() {
        Field f = super.getField("subsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean dubsRequired() {
        Field f = super.getField("dubsRequired");
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

    public static class SubsDubsField extends HollowTestObjectRecord.Field {

        private SubsDubsField(String name, Object val) { super(name, val); }

        public static SubsDubsField movieId(MovieIdTestData val) {
            return new SubsDubsField("movieId", val);
        }

        public static SubsDubsField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static SubsDubsField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static SubsDubsField countryCode(CountryStringTestData val) {
            return new SubsDubsField("countryCode", val);
        }

        public static SubsDubsField countryCode(CountryStringField... fields) {
            return countryCode(new CountryStringTestData(fields));
        }

        public static SubsDubsField countryCode(String val) {
            return countryCode(CountryStringField.value(val));
        }

        public static SubsDubsField subsRequired(boolean val) {
            return new SubsDubsField("subsRequired", val);
        }

        public static SubsDubsField dubsRequired(boolean val) {
            return new SubsDubsField("dubsRequired", val);
        }

        public static SubsDubsField dateCreated(DateTestData val) {
            return new SubsDubsField("dateCreated", val);
        }

        public static SubsDubsField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static SubsDubsField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static SubsDubsField lastUpdated(DateTestData val) {
            return new SubsDubsField("lastUpdated", val);
        }

        public static SubsDubsField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static SubsDubsField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static SubsDubsField createdBy(StringTestData val) {
            return new SubsDubsField("createdBy", val);
        }

        public static SubsDubsField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static SubsDubsField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static SubsDubsField updatedBy(StringTestData val) {
            return new SubsDubsField("updatedBy", val);
        }

        public static SubsDubsField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static SubsDubsField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("SubsDubs", 8, new PrimaryKey("SubsDubs", "movieId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "CountryString");
        SCHEMA.addField("subsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("dubsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}