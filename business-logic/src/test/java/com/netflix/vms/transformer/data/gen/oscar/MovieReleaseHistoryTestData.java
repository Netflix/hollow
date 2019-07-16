package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.BcpCodeTestData.BcpCodeField;
import com.netflix.vms.transformer.data.gen.oscar.CountryStringTestData.CountryStringField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.DistributorNameTestData.DistributorNameField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieReleaseTypeTestData.MovieReleaseTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieReleaseHistoryTestData extends HollowTestObjectRecord {

    MovieReleaseHistoryTestData(MovieReleaseHistoryField... fields){
        super(fields);
    }

    public static MovieReleaseHistoryTestData MovieReleaseHistory(MovieReleaseHistoryField... fields) {
        return new MovieReleaseHistoryTestData(fields);
    }

    public MovieReleaseHistoryTestData update(MovieReleaseHistoryField... fields){
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

    public MovieReleaseTypeTestData typeRef() {
        Field f = super.getField("type");
        return f == null ? null : (MovieReleaseTypeTestData)f.value;
    }

    public String type() {
        Field f = super.getField("type");
        if(f == null) return null;
        MovieReleaseTypeTestData ref = (MovieReleaseTypeTestData)f.value;
        return ref._name();
    }

    public int year() {
        Field f = super.getField("year");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int month() {
        Field f = super.getField("month");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int day() {
        Field f = super.getField("day");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public DistributorNameTestData distributorNameRef() {
        Field f = super.getField("distributorName");
        return f == null ? null : (DistributorNameTestData)f.value;
    }

    public String distributorName() {
        Field f = super.getField("distributorName");
        if(f == null) return null;
        DistributorNameTestData ref = (DistributorNameTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData distributorBcpCodeRef() {
        Field f = super.getField("distributorBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String distributorBcpCode() {
        Field f = super.getField("distributorBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
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

    public static class MovieReleaseHistoryField extends HollowTestObjectRecord.Field {

        private MovieReleaseHistoryField(String name, Object val) { super(name, val); }

        public static MovieReleaseHistoryField movieId(MovieIdTestData val) {
            return new MovieReleaseHistoryField("movieId", val);
        }

        public static MovieReleaseHistoryField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieReleaseHistoryField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieReleaseHistoryField countryCode(CountryStringTestData val) {
            return new MovieReleaseHistoryField("countryCode", val);
        }

        public static MovieReleaseHistoryField countryCode(CountryStringField... fields) {
            return countryCode(new CountryStringTestData(fields));
        }

        public static MovieReleaseHistoryField countryCode(String val) {
            return countryCode(CountryStringField.value(val));
        }

        public static MovieReleaseHistoryField type(MovieReleaseTypeTestData val) {
            return new MovieReleaseHistoryField("type", val);
        }

        public static MovieReleaseHistoryField type(MovieReleaseTypeField... fields) {
            return type(new MovieReleaseTypeTestData(fields));
        }

        public static MovieReleaseHistoryField type(String val) {
            return type(MovieReleaseTypeField._name(val));
        }

        public static MovieReleaseHistoryField year(int val) {
            return new MovieReleaseHistoryField("year", val);
        }

        public static MovieReleaseHistoryField month(int val) {
            return new MovieReleaseHistoryField("month", val);
        }

        public static MovieReleaseHistoryField day(int val) {
            return new MovieReleaseHistoryField("day", val);
        }

        public static MovieReleaseHistoryField distributorName(DistributorNameTestData val) {
            return new MovieReleaseHistoryField("distributorName", val);
        }

        public static MovieReleaseHistoryField distributorName(DistributorNameField... fields) {
            return distributorName(new DistributorNameTestData(fields));
        }

        public static MovieReleaseHistoryField distributorName(String val) {
            return distributorName(DistributorNameField.value(val));
        }

        public static MovieReleaseHistoryField distributorBcpCode(BcpCodeTestData val) {
            return new MovieReleaseHistoryField("distributorBcpCode", val);
        }

        public static MovieReleaseHistoryField distributorBcpCode(BcpCodeField... fields) {
            return distributorBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieReleaseHistoryField distributorBcpCode(String val) {
            return distributorBcpCode(BcpCodeField.value(val));
        }

        public static MovieReleaseHistoryField dateCreated(DateTestData val) {
            return new MovieReleaseHistoryField("dateCreated", val);
        }

        public static MovieReleaseHistoryField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieReleaseHistoryField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieReleaseHistoryField lastUpdated(DateTestData val) {
            return new MovieReleaseHistoryField("lastUpdated", val);
        }

        public static MovieReleaseHistoryField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieReleaseHistoryField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieReleaseHistoryField createdBy(StringTestData val) {
            return new MovieReleaseHistoryField("createdBy", val);
        }

        public static MovieReleaseHistoryField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieReleaseHistoryField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieReleaseHistoryField updatedBy(StringTestData val) {
            return new MovieReleaseHistoryField("updatedBy", val);
        }

        public static MovieReleaseHistoryField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieReleaseHistoryField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieReleaseHistory", 12, new PrimaryKey("MovieReleaseHistory", "movieId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "CountryString");
        SCHEMA.addField("type", FieldType.REFERENCE, "MovieReleaseType");
        SCHEMA.addField("year", FieldType.INT);
        SCHEMA.addField("month", FieldType.INT);
        SCHEMA.addField("day", FieldType.INT);
        SCHEMA.addField("distributorName", FieldType.REFERENCE, "DistributorName");
        SCHEMA.addField("distributorBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}