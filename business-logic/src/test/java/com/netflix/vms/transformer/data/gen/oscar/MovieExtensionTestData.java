package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.AttributeNameTestData.AttributeNameField;
import com.netflix.vms.transformer.data.gen.oscar.AttributeValueTestData.AttributeValueField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieExtensionTestData extends HollowTestObjectRecord {

    MovieExtensionTestData(MovieExtensionField... fields){
        super(fields);
    }

    public static MovieExtensionTestData MovieExtension(MovieExtensionField... fields) {
        return new MovieExtensionTestData(fields);
    }

    public MovieExtensionTestData update(MovieExtensionField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieExtensionId() {
        Field f = super.getField("movieExtensionId");
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

    public AttributeNameTestData attributeNameRef() {
        Field f = super.getField("attributeName");
        return f == null ? null : (AttributeNameTestData)f.value;
    }

    public String attributeName() {
        Field f = super.getField("attributeName");
        if(f == null) return null;
        AttributeNameTestData ref = (AttributeNameTestData)f.value;
        return ref.value();
    }

    public AttributeValueTestData attributeValueRef() {
        Field f = super.getField("attributeValue");
        return f == null ? null : (AttributeValueTestData)f.value;
    }

    public String attributeValue() {
        Field f = super.getField("attributeValue");
        if(f == null) return null;
        AttributeValueTestData ref = (AttributeValueTestData)f.value;
        return ref.value();
    }

    public SetOfMovieExtensionOverrideTestData overrides() {
        Field f = super.getField("overrides");
        return f == null ? null : (SetOfMovieExtensionOverrideTestData)f.value;
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

    public static class MovieExtensionField extends HollowTestObjectRecord.Field {

        private MovieExtensionField(String name, Object val) { super(name, val); }

        public static MovieExtensionField movieExtensionId(long val) {
            return new MovieExtensionField("movieExtensionId", val);
        }

        public static MovieExtensionField movieId(MovieIdTestData val) {
            return new MovieExtensionField("movieId", val);
        }

        public static MovieExtensionField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieExtensionField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieExtensionField attributeName(AttributeNameTestData val) {
            return new MovieExtensionField("attributeName", val);
        }

        public static MovieExtensionField attributeName(AttributeNameField... fields) {
            return attributeName(new AttributeNameTestData(fields));
        }

        public static MovieExtensionField attributeName(String val) {
            return attributeName(AttributeNameField.value(val));
        }

        public static MovieExtensionField attributeValue(AttributeValueTestData val) {
            return new MovieExtensionField("attributeValue", val);
        }

        public static MovieExtensionField attributeValue(AttributeValueField... fields) {
            return attributeValue(new AttributeValueTestData(fields));
        }

        public static MovieExtensionField attributeValue(String val) {
            return attributeValue(AttributeValueField.value(val));
        }

        public static MovieExtensionField overrides(SetOfMovieExtensionOverrideTestData val) {
            return new MovieExtensionField("overrides", val);
        }

        public static MovieExtensionField overrides(MovieExtensionOverrideTestData... elements) {
            return overrides(new SetOfMovieExtensionOverrideTestData(elements));
        }

        public static MovieExtensionField dateCreated(DateTestData val) {
            return new MovieExtensionField("dateCreated", val);
        }

        public static MovieExtensionField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieExtensionField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieExtensionField lastUpdated(DateTestData val) {
            return new MovieExtensionField("lastUpdated", val);
        }

        public static MovieExtensionField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieExtensionField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieExtensionField createdBy(StringTestData val) {
            return new MovieExtensionField("createdBy", val);
        }

        public static MovieExtensionField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieExtensionField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieExtensionField updatedBy(StringTestData val) {
            return new MovieExtensionField("updatedBy", val);
        }

        public static MovieExtensionField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieExtensionField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieExtension", 9, new PrimaryKey("MovieExtension", "movieId", "attributeName"));

    static {
        SCHEMA.addField("movieExtensionId", FieldType.LONG);
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("attributeName", FieldType.REFERENCE, "AttributeName");
        SCHEMA.addField("attributeValue", FieldType.REFERENCE, "AttributeValue");
        SCHEMA.addField("overrides", FieldType.REFERENCE, "SetOfMovieExtensionOverride");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}