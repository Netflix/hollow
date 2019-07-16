package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.RatingsRequirementsTestData.RatingsRequirementsField;
import com.netflix.vms.transformer.data.gen.oscar.RecipeGroupsTestData.RecipeGroupsField;
import com.netflix.vms.transformer.data.gen.oscar.SourceRequestDefaultFulfillmentTestData.SourceRequestDefaultFulfillmentField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.TitleSetupRequirementsTemplateTestData.TitleSetupRequirementsTemplateField;

public class TitleSetupRequirementsTestData extends HollowTestObjectRecord {

    TitleSetupRequirementsTestData(TitleSetupRequirementsField... fields){
        super(fields);
    }

    public static TitleSetupRequirementsTestData TitleSetupRequirements(TitleSetupRequirementsField... fields) {
        return new TitleSetupRequirementsTestData(fields);
    }

    public TitleSetupRequirementsTestData update(TitleSetupRequirementsField... fields){
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

    public TitleSetupRequirementsTemplateTestData titleSetupRequirementsTemplate() {
        Field f = super.getField("titleSetupRequirementsTemplate");
        return f == null ? null : (TitleSetupRequirementsTemplateTestData)f.value;
    }

    public Boolean subsRequired() {
        Field f = super.getField("subsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean dubsRequired() {
        Field f = super.getField("dubsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean artworkRequired() {
        Field f = super.getField("artworkRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean instreamStillsRequired() {
        Field f = super.getField("instreamStillsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean informativeSynopsisRequired() {
        Field f = super.getField("informativeSynopsisRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public RatingsRequirementsTestData ratingsRequiredRef() {
        Field f = super.getField("ratingsRequired");
        return f == null ? null : (RatingsRequirementsTestData)f.value;
    }

    public String ratingsRequired() {
        Field f = super.getField("ratingsRequired");
        if(f == null) return null;
        RatingsRequirementsTestData ref = (RatingsRequirementsTestData)f.value;
        return ref._name();
    }

    public Boolean taggingRequired() {
        Field f = super.getField("taggingRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean castRequired() {
        Field f = super.getField("castRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean displayNameRequired() {
        Field f = super.getField("displayNameRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public SourceRequestDefaultFulfillmentTestData sourceRequestDefaultFulfillmentRef() {
        Field f = super.getField("sourceRequestDefaultFulfillment");
        return f == null ? null : (SourceRequestDefaultFulfillmentTestData)f.value;
    }

    public String sourceRequestDefaultFulfillment() {
        Field f = super.getField("sourceRequestDefaultFulfillment");
        if(f == null) return null;
        SourceRequestDefaultFulfillmentTestData ref = (SourceRequestDefaultFulfillmentTestData)f.value;
        return ref._name();
    }

    public RecipeGroupsTestData recipeGroupsRef() {
        Field f = super.getField("recipeGroups");
        return f == null ? null : (RecipeGroupsTestData)f.value;
    }

    public String recipeGroups() {
        Field f = super.getField("recipeGroups");
        if(f == null) return null;
        RecipeGroupsTestData ref = (RecipeGroupsTestData)f.value;
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

    public static class TitleSetupRequirementsField extends HollowTestObjectRecord.Field {

        private TitleSetupRequirementsField(String name, Object val) { super(name, val); }

        public static TitleSetupRequirementsField movieId(MovieIdTestData val) {
            return new TitleSetupRequirementsField("movieId", val);
        }

        public static TitleSetupRequirementsField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static TitleSetupRequirementsField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static TitleSetupRequirementsField titleSetupRequirementsTemplate(TitleSetupRequirementsTemplateTestData val) {
            return new TitleSetupRequirementsField("titleSetupRequirementsTemplate", val);
        }

        public static TitleSetupRequirementsField titleSetupRequirementsTemplate(TitleSetupRequirementsTemplateField... fields) {
            return titleSetupRequirementsTemplate(new TitleSetupRequirementsTemplateTestData(fields));
        }

        public static TitleSetupRequirementsField subsRequired(boolean val) {
            return new TitleSetupRequirementsField("subsRequired", val);
        }

        public static TitleSetupRequirementsField dubsRequired(boolean val) {
            return new TitleSetupRequirementsField("dubsRequired", val);
        }

        public static TitleSetupRequirementsField artworkRequired(boolean val) {
            return new TitleSetupRequirementsField("artworkRequired", val);
        }

        public static TitleSetupRequirementsField instreamStillsRequired(boolean val) {
            return new TitleSetupRequirementsField("instreamStillsRequired", val);
        }

        public static TitleSetupRequirementsField informativeSynopsisRequired(boolean val) {
            return new TitleSetupRequirementsField("informativeSynopsisRequired", val);
        }

        public static TitleSetupRequirementsField ratingsRequired(RatingsRequirementsTestData val) {
            return new TitleSetupRequirementsField("ratingsRequired", val);
        }

        public static TitleSetupRequirementsField ratingsRequired(RatingsRequirementsField... fields) {
            return ratingsRequired(new RatingsRequirementsTestData(fields));
        }

        public static TitleSetupRequirementsField ratingsRequired(String val) {
            return ratingsRequired(RatingsRequirementsField._name(val));
        }

        public static TitleSetupRequirementsField taggingRequired(boolean val) {
            return new TitleSetupRequirementsField("taggingRequired", val);
        }

        public static TitleSetupRequirementsField castRequired(boolean val) {
            return new TitleSetupRequirementsField("castRequired", val);
        }

        public static TitleSetupRequirementsField displayNameRequired(boolean val) {
            return new TitleSetupRequirementsField("displayNameRequired", val);
        }

        public static TitleSetupRequirementsField sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentTestData val) {
            return new TitleSetupRequirementsField("sourceRequestDefaultFulfillment", val);
        }

        public static TitleSetupRequirementsField sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField... fields) {
            return sourceRequestDefaultFulfillment(new SourceRequestDefaultFulfillmentTestData(fields));
        }

        public static TitleSetupRequirementsField sourceRequestDefaultFulfillment(String val) {
            return sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField._name(val));
        }

        public static TitleSetupRequirementsField recipeGroups(RecipeGroupsTestData val) {
            return new TitleSetupRequirementsField("recipeGroups", val);
        }

        public static TitleSetupRequirementsField recipeGroups(RecipeGroupsField... fields) {
            return recipeGroups(new RecipeGroupsTestData(fields));
        }

        public static TitleSetupRequirementsField recipeGroups(String val) {
            return recipeGroups(RecipeGroupsField.value(val));
        }

        public static TitleSetupRequirementsField dateCreated(DateTestData val) {
            return new TitleSetupRequirementsField("dateCreated", val);
        }

        public static TitleSetupRequirementsField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static TitleSetupRequirementsField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static TitleSetupRequirementsField lastUpdated(DateTestData val) {
            return new TitleSetupRequirementsField("lastUpdated", val);
        }

        public static TitleSetupRequirementsField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static TitleSetupRequirementsField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static TitleSetupRequirementsField createdBy(StringTestData val) {
            return new TitleSetupRequirementsField("createdBy", val);
        }

        public static TitleSetupRequirementsField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static TitleSetupRequirementsField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static TitleSetupRequirementsField updatedBy(StringTestData val) {
            return new TitleSetupRequirementsField("updatedBy", val);
        }

        public static TitleSetupRequirementsField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static TitleSetupRequirementsField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TitleSetupRequirements", 17, new PrimaryKey("TitleSetupRequirements", "movieId"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("titleSetupRequirementsTemplate", FieldType.REFERENCE, "TitleSetupRequirementsTemplate");
        SCHEMA.addField("subsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("dubsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("artworkRequired", FieldType.BOOLEAN);
        SCHEMA.addField("instreamStillsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("informativeSynopsisRequired", FieldType.BOOLEAN);
        SCHEMA.addField("ratingsRequired", FieldType.REFERENCE, "RatingsRequirements");
        SCHEMA.addField("taggingRequired", FieldType.BOOLEAN);
        SCHEMA.addField("castRequired", FieldType.BOOLEAN);
        SCHEMA.addField("displayNameRequired", FieldType.BOOLEAN);
        SCHEMA.addField("sourceRequestDefaultFulfillment", FieldType.REFERENCE, "SourceRequestDefaultFulfillment");
        SCHEMA.addField("recipeGroups", FieldType.REFERENCE, "RecipeGroups");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}