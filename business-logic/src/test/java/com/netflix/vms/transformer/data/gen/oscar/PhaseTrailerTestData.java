package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class PhaseTrailerTestData extends HollowTestObjectRecord {

    PhaseTrailerTestData(PhaseTrailerField... fields){
        super(fields);
    }

    public static PhaseTrailerTestData PhaseTrailer(PhaseTrailerField... fields) {
        return new PhaseTrailerTestData(fields);
    }

    public PhaseTrailerTestData update(PhaseTrailerField... fields){
        super.addFields(fields);
        return this;
    }

    public MovieIdTestData trailerMovieIdRef() {
        Field f = super.getField("trailerMovieId");
        return f == null ? null : (MovieIdTestData)f.value;
    }

    public long trailerMovieId() {
        Field f = super.getField("trailerMovieId");
        if(f == null) return Long.MIN_VALUE;
        MovieIdTestData ref = (MovieIdTestData)f.value;
        return ref.value();
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
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

    public static class PhaseTrailerField extends HollowTestObjectRecord.Field {

        private PhaseTrailerField(String name, Object val) { super(name, val); }

        public static PhaseTrailerField trailerMovieId(MovieIdTestData val) {
            return new PhaseTrailerField("trailerMovieId", val);
        }

        public static PhaseTrailerField trailerMovieId(MovieIdField... fields) {
            return trailerMovieId(new MovieIdTestData(fields));
        }

        public static PhaseTrailerField trailerMovieId(long val) {
            return trailerMovieId(MovieIdField.value(val));
        }

        public static PhaseTrailerField sequenceNumber(int val) {
            return new PhaseTrailerField("sequenceNumber", val);
        }

        public static PhaseTrailerField dateCreated(DateTestData val) {
            return new PhaseTrailerField("dateCreated", val);
        }

        public static PhaseTrailerField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static PhaseTrailerField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static PhaseTrailerField lastUpdated(DateTestData val) {
            return new PhaseTrailerField("lastUpdated", val);
        }

        public static PhaseTrailerField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static PhaseTrailerField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static PhaseTrailerField createdBy(StringTestData val) {
            return new PhaseTrailerField("createdBy", val);
        }

        public static PhaseTrailerField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static PhaseTrailerField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static PhaseTrailerField updatedBy(StringTestData val) {
            return new PhaseTrailerField("updatedBy", val);
        }

        public static PhaseTrailerField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static PhaseTrailerField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseTrailer", 6);

    static {
        SCHEMA.addField("trailerMovieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}