package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieCountriesTestData extends HollowTestObjectRecord {

    MovieCountriesTestData(MovieCountriesField... fields){
        super(fields);
    }

    public static MovieCountriesTestData MovieCountries(MovieCountriesField... fields) {
        return new MovieCountriesTestData(fields);
    }

    public MovieCountriesTestData update(MovieCountriesField... fields){
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

    public SetOfStringTestData countries() {
        Field f = super.getField("countries");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData lastUpdatedCountries() {
        Field f = super.getField("lastUpdatedCountries");
        return f == null ? null : (SetOfStringTestData)f.value;
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

    public static class MovieCountriesField extends HollowTestObjectRecord.Field {

        private MovieCountriesField(String name, Object val) { super(name, val); }

        public static MovieCountriesField movieId(MovieIdTestData val) {
            return new MovieCountriesField("movieId", val);
        }

        public static MovieCountriesField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieCountriesField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieCountriesField countries(SetOfStringTestData val) {
            return new MovieCountriesField("countries", val);
        }

        public static MovieCountriesField countries(StringTestData... elements) {
            return countries(new SetOfStringTestData(elements));
        }

        public static MovieCountriesField lastUpdatedCountries(SetOfStringTestData val) {
            return new MovieCountriesField("lastUpdatedCountries", val);
        }

        public static MovieCountriesField lastUpdatedCountries(StringTestData... elements) {
            return lastUpdatedCountries(new SetOfStringTestData(elements));
        }

        public static MovieCountriesField dateCreated(DateTestData val) {
            return new MovieCountriesField("dateCreated", val);
        }

        public static MovieCountriesField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieCountriesField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieCountriesField lastUpdated(DateTestData val) {
            return new MovieCountriesField("lastUpdated", val);
        }

        public static MovieCountriesField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieCountriesField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieCountriesField createdBy(StringTestData val) {
            return new MovieCountriesField("createdBy", val);
        }

        public static MovieCountriesField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieCountriesField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieCountriesField updatedBy(StringTestData val) {
            return new MovieCountriesField("updatedBy", val);
        }

        public static MovieCountriesField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieCountriesField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieCountries", 7, new PrimaryKey("MovieCountries", "movieId"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("countries", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("lastUpdatedCountries", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}