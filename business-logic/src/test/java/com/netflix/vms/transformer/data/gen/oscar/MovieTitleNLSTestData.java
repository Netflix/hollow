package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.BcpCodeTestData.BcpCodeField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTitleStringTestData.MovieTitleStringField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTitleTypeTestData.MovieTitleTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.TitleSourceTypeTestData.TitleSourceTypeField;
import com.netflix.vms.transformer.data.gen.oscar.isOriginalTitleTestData.isOriginalTitleField;

public class MovieTitleNLSTestData extends HollowTestObjectRecord {

    MovieTitleNLSTestData(MovieTitleNLSField... fields){
        super(fields);
    }

    public static MovieTitleNLSTestData MovieTitleNLS(MovieTitleNLSField... fields) {
        return new MovieTitleNLSTestData(fields);
    }

    public MovieTitleNLSTestData update(MovieTitleNLSField... fields){
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

    public MovieTitleTypeTestData typeRef() {
        Field f = super.getField("type");
        return f == null ? null : (MovieTitleTypeTestData)f.value;
    }

    public String type() {
        Field f = super.getField("type");
        if(f == null) return null;
        MovieTitleTypeTestData ref = (MovieTitleTypeTestData)f.value;
        return ref._name();
    }

    public MovieTitleStringTestData titleTextRef() {
        Field f = super.getField("titleText");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String titleText() {
        Field f = super.getField("titleText");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData merchBcpCodeRef() {
        Field f = super.getField("merchBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String merchBcpCode() {
        Field f = super.getField("merchBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData titleBcpCodeRef() {
        Field f = super.getField("titleBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String titleBcpCode() {
        Field f = super.getField("titleBcpCode");
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

    public isOriginalTitleTestData isOriginalTitleRef() {
        Field f = super.getField("isOriginalTitle");
        return f == null ? null : (isOriginalTitleTestData)f.value;
    }

    public Boolean isOriginalTitle() {
        Field f = super.getField("isOriginalTitle");
        if(f == null) return null;
        isOriginalTitleTestData ref = (isOriginalTitleTestData)f.value;
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

    public static class MovieTitleNLSField extends HollowTestObjectRecord.Field {

        private MovieTitleNLSField(String name, Object val) { super(name, val); }

        public static MovieTitleNLSField movieId(MovieIdTestData val) {
            return new MovieTitleNLSField("movieId", val);
        }

        public static MovieTitleNLSField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieTitleNLSField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieTitleNLSField type(MovieTitleTypeTestData val) {
            return new MovieTitleNLSField("type", val);
        }

        public static MovieTitleNLSField type(MovieTitleTypeField... fields) {
            return type(new MovieTitleTypeTestData(fields));
        }

        public static MovieTitleNLSField type(String val) {
            return type(MovieTitleTypeField._name(val));
        }

        public static MovieTitleNLSField titleText(MovieTitleStringTestData val) {
            return new MovieTitleNLSField("titleText", val);
        }

        public static MovieTitleNLSField titleText(MovieTitleStringField... fields) {
            return titleText(new MovieTitleStringTestData(fields));
        }

        public static MovieTitleNLSField titleText(String val) {
            return titleText(MovieTitleStringField.value(val));
        }

        public static MovieTitleNLSField merchBcpCode(BcpCodeTestData val) {
            return new MovieTitleNLSField("merchBcpCode", val);
        }

        public static MovieTitleNLSField merchBcpCode(BcpCodeField... fields) {
            return merchBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieTitleNLSField merchBcpCode(String val) {
            return merchBcpCode(BcpCodeField.value(val));
        }

        public static MovieTitleNLSField titleBcpCode(BcpCodeTestData val) {
            return new MovieTitleNLSField("titleBcpCode", val);
        }

        public static MovieTitleNLSField titleBcpCode(BcpCodeField... fields) {
            return titleBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieTitleNLSField titleBcpCode(String val) {
            return titleBcpCode(BcpCodeField.value(val));
        }

        public static MovieTitleNLSField sourceType(TitleSourceTypeTestData val) {
            return new MovieTitleNLSField("sourceType", val);
        }

        public static MovieTitleNLSField sourceType(TitleSourceTypeField... fields) {
            return sourceType(new TitleSourceTypeTestData(fields));
        }

        public static MovieTitleNLSField sourceType(String val) {
            return sourceType(TitleSourceTypeField._name(val));
        }

        public static MovieTitleNLSField isOriginalTitle(isOriginalTitleTestData val) {
            return new MovieTitleNLSField("isOriginalTitle", val);
        }

        public static MovieTitleNLSField isOriginalTitle(isOriginalTitleField... fields) {
            return isOriginalTitle(new isOriginalTitleTestData(fields));
        }

        public static MovieTitleNLSField isOriginalTitle(boolean val) {
            return isOriginalTitle(isOriginalTitleField.value(val));
        }

        public static MovieTitleNLSField dateCreated(DateTestData val) {
            return new MovieTitleNLSField("dateCreated", val);
        }

        public static MovieTitleNLSField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieTitleNLSField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieTitleNLSField lastUpdated(DateTestData val) {
            return new MovieTitleNLSField("lastUpdated", val);
        }

        public static MovieTitleNLSField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieTitleNLSField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieTitleNLSField createdBy(StringTestData val) {
            return new MovieTitleNLSField("createdBy", val);
        }

        public static MovieTitleNLSField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieTitleNLSField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieTitleNLSField updatedBy(StringTestData val) {
            return new MovieTitleNLSField("updatedBy", val);
        }

        public static MovieTitleNLSField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieTitleNLSField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieTitleNLS", 11, new PrimaryKey("MovieTitleNLS", "movieId", "type", "merchBcpCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("type", FieldType.REFERENCE, "MovieTitleType");
        SCHEMA.addField("titleText", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("merchBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("titleBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("sourceType", FieldType.REFERENCE, "TitleSourceType");
        SCHEMA.addField("isOriginalTitle", FieldType.REFERENCE, "isOriginalTitle");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}