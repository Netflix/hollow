package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.CountryStringTestData.CountryStringField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieSetContentLabelTestData.MovieSetContentLabelField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class ShowCountryLabelOverrideTestData extends HollowTestObjectRecord {

    ShowCountryLabelOverrideTestData(ShowCountryLabelOverrideField... fields){
        super(fields);
    }

    public static ShowCountryLabelOverrideTestData ShowCountryLabelOverride(ShowCountryLabelOverrideField... fields) {
        return new ShowCountryLabelOverrideTestData(fields);
    }

    public ShowCountryLabelOverrideTestData update(ShowCountryLabelOverrideField... fields){
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

    public MovieSetContentLabelTestData label() {
        Field f = super.getField("label");
        return f == null ? null : (MovieSetContentLabelTestData)f.value;
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

    public static class ShowCountryLabelOverrideField extends HollowTestObjectRecord.Field {

        private ShowCountryLabelOverrideField(String name, Object val) { super(name, val); }

        public static ShowCountryLabelOverrideField movieId(MovieIdTestData val) {
            return new ShowCountryLabelOverrideField("movieId", val);
        }

        public static ShowCountryLabelOverrideField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static ShowCountryLabelOverrideField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static ShowCountryLabelOverrideField countryCode(CountryStringTestData val) {
            return new ShowCountryLabelOverrideField("countryCode", val);
        }

        public static ShowCountryLabelOverrideField countryCode(CountryStringField... fields) {
            return countryCode(new CountryStringTestData(fields));
        }

        public static ShowCountryLabelOverrideField countryCode(String val) {
            return countryCode(CountryStringField.value(val));
        }

        public static ShowCountryLabelOverrideField label(MovieSetContentLabelTestData val) {
            return new ShowCountryLabelOverrideField("label", val);
        }

        public static ShowCountryLabelOverrideField label(MovieSetContentLabelField... fields) {
            return label(new MovieSetContentLabelTestData(fields));
        }

        public static ShowCountryLabelOverrideField dateCreated(DateTestData val) {
            return new ShowCountryLabelOverrideField("dateCreated", val);
        }

        public static ShowCountryLabelOverrideField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static ShowCountryLabelOverrideField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static ShowCountryLabelOverrideField lastUpdated(DateTestData val) {
            return new ShowCountryLabelOverrideField("lastUpdated", val);
        }

        public static ShowCountryLabelOverrideField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static ShowCountryLabelOverrideField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static ShowCountryLabelOverrideField createdBy(StringTestData val) {
            return new ShowCountryLabelOverrideField("createdBy", val);
        }

        public static ShowCountryLabelOverrideField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static ShowCountryLabelOverrideField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static ShowCountryLabelOverrideField updatedBy(StringTestData val) {
            return new ShowCountryLabelOverrideField("updatedBy", val);
        }

        public static ShowCountryLabelOverrideField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static ShowCountryLabelOverrideField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ShowCountryLabelOverride", 7, new PrimaryKey("ShowCountryLabelOverride", "movieId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "CountryString");
        SCHEMA.addField("label", FieldType.REFERENCE, "MovieSetContentLabel");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}