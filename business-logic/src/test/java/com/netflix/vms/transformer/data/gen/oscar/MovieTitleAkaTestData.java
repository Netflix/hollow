package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.BcpCodeTestData.BcpCodeField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTitleStringTestData.MovieTitleStringField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.TitleSourceTypeTestData.TitleSourceTypeField;

public class MovieTitleAkaTestData extends HollowTestObjectRecord {

    MovieTitleAkaTestData(MovieTitleAkaField... fields){
        super(fields);
    }

    public static MovieTitleAkaTestData MovieTitleAka(MovieTitleAkaField... fields) {
        return new MovieTitleAkaTestData(fields);
    }

    public MovieTitleAkaTestData update(MovieTitleAkaField... fields){
        super.addFields(fields);
        return this;
    }

    public long id() {
        Field f = super.getField("id");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
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

    public MovieTitleStringTestData aliasRef() {
        Field f = super.getField("alias");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String alias() {
        Field f = super.getField("alias");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData bcpCodeRef() {
        Field f = super.getField("bcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String bcpCode() {
        Field f = super.getField("bcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public TitleSourceTypeTestData sourceTypeRef() {
        Field f = super.getField("sourceType");
        return f == null ? null : (TitleSourceTypeTestData)f.value;
    }

    public String sourceType() {
        Field f = super.getField("sourceType");
        if(f == null) return null;
        TitleSourceTypeTestData ref = (TitleSourceTypeTestData)f.value;
        return ref._name();
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

    public static class MovieTitleAkaField extends HollowTestObjectRecord.Field {

        private MovieTitleAkaField(String name, Object val) { super(name, val); }

        public static MovieTitleAkaField id(long val) {
            return new MovieTitleAkaField("id", val);
        }

        public static MovieTitleAkaField movieId(MovieIdTestData val) {
            return new MovieTitleAkaField("movieId", val);
        }

        public static MovieTitleAkaField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieTitleAkaField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieTitleAkaField alias(MovieTitleStringTestData val) {
            return new MovieTitleAkaField("alias", val);
        }

        public static MovieTitleAkaField alias(MovieTitleStringField... fields) {
            return alias(new MovieTitleStringTestData(fields));
        }

        public static MovieTitleAkaField alias(String val) {
            return alias(MovieTitleStringField.value(val));
        }

        public static MovieTitleAkaField bcpCode(BcpCodeTestData val) {
            return new MovieTitleAkaField("bcpCode", val);
        }

        public static MovieTitleAkaField bcpCode(BcpCodeField... fields) {
            return bcpCode(new BcpCodeTestData(fields));
        }

        public static MovieTitleAkaField bcpCode(String val) {
            return bcpCode(BcpCodeField.value(val));
        }

        public static MovieTitleAkaField sourceType(TitleSourceTypeTestData val) {
            return new MovieTitleAkaField("sourceType", val);
        }

        public static MovieTitleAkaField sourceType(TitleSourceTypeField... fields) {
            return sourceType(new TitleSourceTypeTestData(fields));
        }

        public static MovieTitleAkaField sourceType(String val) {
            return sourceType(TitleSourceTypeField._name(val));
        }

        public static MovieTitleAkaField dateCreated(DateTestData val) {
            return new MovieTitleAkaField("dateCreated", val);
        }

        public static MovieTitleAkaField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieTitleAkaField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieTitleAkaField lastUpdated(DateTestData val) {
            return new MovieTitleAkaField("lastUpdated", val);
        }

        public static MovieTitleAkaField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieTitleAkaField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieTitleAkaField createdBy(StringTestData val) {
            return new MovieTitleAkaField("createdBy", val);
        }

        public static MovieTitleAkaField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieTitleAkaField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieTitleAkaField updatedBy(StringTestData val) {
            return new MovieTitleAkaField("updatedBy", val);
        }

        public static MovieTitleAkaField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieTitleAkaField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieTitleAka", 9, new PrimaryKey("MovieTitleAka", "id"));

    static {
        SCHEMA.addField("id", FieldType.LONG);
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("alias", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("bcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("sourceType", FieldType.REFERENCE, "TitleSourceType");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}