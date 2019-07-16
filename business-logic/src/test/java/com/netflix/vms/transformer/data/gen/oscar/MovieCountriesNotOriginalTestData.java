package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.CountryStringTestData.CountryStringField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieCountriesNotOriginalTestData extends HollowTestObjectRecord {

    MovieCountriesNotOriginalTestData(MovieCountriesNotOriginalField... fields){
        super(fields);
    }

    public static MovieCountriesNotOriginalTestData MovieCountriesNotOriginal(MovieCountriesNotOriginalField... fields) {
        return new MovieCountriesNotOriginalTestData(fields);
    }

    public MovieCountriesNotOriginalTestData update(MovieCountriesNotOriginalField... fields){
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

    public CountryStringTestData countriesRef() {
        Field f = super.getField("countries");
        return f == null ? null : (CountryStringTestData)f.value;
    }

    public String countries() {
        Field f = super.getField("countries");
        if(f == null) return null;
        CountryStringTestData ref = (CountryStringTestData)f.value;
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

    public static class MovieCountriesNotOriginalField extends HollowTestObjectRecord.Field {

        private MovieCountriesNotOriginalField(String name, Object val) { super(name, val); }

        public static MovieCountriesNotOriginalField movieId(MovieIdTestData val) {
            return new MovieCountriesNotOriginalField("movieId", val);
        }

        public static MovieCountriesNotOriginalField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieCountriesNotOriginalField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieCountriesNotOriginalField countries(CountryStringTestData val) {
            return new MovieCountriesNotOriginalField("countries", val);
        }

        public static MovieCountriesNotOriginalField countries(CountryStringField... fields) {
            return countries(new CountryStringTestData(fields));
        }

        public static MovieCountriesNotOriginalField countries(String val) {
            return countries(CountryStringField.value(val));
        }

        public static MovieCountriesNotOriginalField dateCreated(DateTestData val) {
            return new MovieCountriesNotOriginalField("dateCreated", val);
        }

        public static MovieCountriesNotOriginalField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieCountriesNotOriginalField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieCountriesNotOriginalField lastUpdated(DateTestData val) {
            return new MovieCountriesNotOriginalField("lastUpdated", val);
        }

        public static MovieCountriesNotOriginalField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieCountriesNotOriginalField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieCountriesNotOriginalField createdBy(StringTestData val) {
            return new MovieCountriesNotOriginalField("createdBy", val);
        }

        public static MovieCountriesNotOriginalField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieCountriesNotOriginalField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieCountriesNotOriginalField updatedBy(StringTestData val) {
            return new MovieCountriesNotOriginalField("updatedBy", val);
        }

        public static MovieCountriesNotOriginalField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieCountriesNotOriginalField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieCountriesNotOriginal", 6, new PrimaryKey("MovieCountriesNotOriginal", "movieId"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("countries", FieldType.REFERENCE, "CountryString");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}