package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTypeTestData.MovieTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.SubtypeStringTestData.SubtypeStringField;

public class SubtypeTestData extends HollowTestObjectRecord {

    SubtypeTestData(SubtypeField... fields){
        super(fields);
    }

    public static SubtypeTestData Subtype(SubtypeField... fields) {
        return new SubtypeTestData(fields);
    }

    public SubtypeTestData update(SubtypeField... fields){
        super.addFields(fields);
        return this;
    }

    public SubtypeStringTestData subtypeRef() {
        Field f = super.getField("subtype");
        return f == null ? null : (SubtypeStringTestData)f.value;
    }

    public String subtype() {
        Field f = super.getField("subtype");
        if(f == null) return null;
        SubtypeStringTestData ref = (SubtypeStringTestData)f.value;
        return ref.value();
    }

    public MovieTypeTestData movieType() {
        Field f = super.getField("movieType");
        return f == null ? null : (MovieTypeTestData)f.value;
    }

    public Boolean active() {
        Field f = super.getField("active");
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

    public static class SubtypeField extends HollowTestObjectRecord.Field {

        private SubtypeField(String name, Object val) { super(name, val); }

        public static SubtypeField subtype(SubtypeStringTestData val) {
            return new SubtypeField("subtype", val);
        }

        public static SubtypeField subtype(SubtypeStringField... fields) {
            return subtype(new SubtypeStringTestData(fields));
        }

        public static SubtypeField subtype(String val) {
            return subtype(SubtypeStringField.value(val));
        }

        public static SubtypeField movieType(MovieTypeTestData val) {
            return new SubtypeField("movieType", val);
        }

        public static SubtypeField movieType(MovieTypeField... fields) {
            return movieType(new MovieTypeTestData(fields));
        }

        public static SubtypeField active(boolean val) {
            return new SubtypeField("active", val);
        }

        public static SubtypeField dateCreated(DateTestData val) {
            return new SubtypeField("dateCreated", val);
        }

        public static SubtypeField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static SubtypeField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static SubtypeField lastUpdated(DateTestData val) {
            return new SubtypeField("lastUpdated", val);
        }

        public static SubtypeField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static SubtypeField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static SubtypeField createdBy(StringTestData val) {
            return new SubtypeField("createdBy", val);
        }

        public static SubtypeField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static SubtypeField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static SubtypeField updatedBy(StringTestData val) {
            return new SubtypeField("updatedBy", val);
        }

        public static SubtypeField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static SubtypeField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Subtype", 7);

    static {
        SCHEMA.addField("subtype", FieldType.REFERENCE, "SubtypeString");
        SCHEMA.addField("movieType", FieldType.REFERENCE, "MovieType");
        SCHEMA.addField("active", FieldType.BOOLEAN);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}