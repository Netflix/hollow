package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.LongTestData.LongField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTypeTestData.MovieTypeField;
import com.netflix.vms.transformer.data.gen.oscar.RatingsRequirementsTestData.RatingsRequirementsField;
import com.netflix.vms.transformer.data.gen.oscar.RecipeGroupsTestData.RecipeGroupsField;
import com.netflix.vms.transformer.data.gen.oscar.SourceRequestDefaultFulfillmentTestData.SourceRequestDefaultFulfillmentField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.SubtypeTestData.SubtypeField;

public class TitleSetupRequirementsTemplateTestData extends HollowTestObjectRecord {

    TitleSetupRequirementsTemplateTestData(TitleSetupRequirementsTemplateField... fields){
        super(fields);
    }

    public static TitleSetupRequirementsTemplateTestData TitleSetupRequirementsTemplate(TitleSetupRequirementsTemplateField... fields) {
        return new TitleSetupRequirementsTemplateTestData(fields);
    }

    public TitleSetupRequirementsTemplateTestData update(TitleSetupRequirementsTemplateField... fields){
        super.addFields(fields);
        return this;
    }

    public MovieTypeTestData movieType() {
        Field f = super.getField("movieType");
        return f == null ? null : (MovieTypeTestData)f.value;
    }

    public SubtypeTestData subtype() {
        Field f = super.getField("subtype");
        return f == null ? null : (SubtypeTestData)f.value;
    }

    public Boolean active() {
        Field f = super.getField("active");
        return f == null ? null : (Boolean)f.value;
    }

    public int version() {
        Field f = super.getField("version");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean enforcePackageLanguageCheck() {
        Field f = super.getField("enforcePackageLanguageCheck");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean collectionRequired() {
        Field f = super.getField("collectionRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean startEndPointRequired() {
        Field f = super.getField("startEndPointRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public LongTestData windowStartOffsetRef() {
        Field f = super.getField("windowStartOffset");
        return f == null ? null : (LongTestData)f.value;
    }

    public long windowStartOffset() {
        Field f = super.getField("windowStartOffset");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public Boolean ratingReviewRequired() {
        Field f = super.getField("ratingReviewRequired");
        return f == null ? null : (Boolean)f.value;
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

    public static class TitleSetupRequirementsTemplateField extends HollowTestObjectRecord.Field {

        private TitleSetupRequirementsTemplateField(String name, Object val) { super(name, val); }

        public static TitleSetupRequirementsTemplateField movieType(MovieTypeTestData val) {
            return new TitleSetupRequirementsTemplateField("movieType", val);
        }

        public static TitleSetupRequirementsTemplateField movieType(MovieTypeField... fields) {
            return movieType(new MovieTypeTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField subtype(SubtypeTestData val) {
            return new TitleSetupRequirementsTemplateField("subtype", val);
        }

        public static TitleSetupRequirementsTemplateField subtype(SubtypeField... fields) {
            return subtype(new SubtypeTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField active(boolean val) {
            return new TitleSetupRequirementsTemplateField("active", val);
        }

        public static TitleSetupRequirementsTemplateField version(int val) {
            return new TitleSetupRequirementsTemplateField("version", val);
        }

        public static TitleSetupRequirementsTemplateField enforcePackageLanguageCheck(boolean val) {
            return new TitleSetupRequirementsTemplateField("enforcePackageLanguageCheck", val);
        }

        public static TitleSetupRequirementsTemplateField collectionRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("collectionRequired", val);
        }

        public static TitleSetupRequirementsTemplateField startEndPointRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("startEndPointRequired", val);
        }

        public static TitleSetupRequirementsTemplateField windowStartOffset(LongTestData val) {
            return new TitleSetupRequirementsTemplateField("windowStartOffset", val);
        }

        public static TitleSetupRequirementsTemplateField windowStartOffset(LongField... fields) {
            return windowStartOffset(new LongTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField windowStartOffset(long val) {
            return windowStartOffset(LongField.value(val));
        }

        public static TitleSetupRequirementsTemplateField ratingReviewRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("ratingReviewRequired", val);
        }

        public static TitleSetupRequirementsTemplateField subsRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("subsRequired", val);
        }

        public static TitleSetupRequirementsTemplateField dubsRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("dubsRequired", val);
        }

        public static TitleSetupRequirementsTemplateField artworkRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("artworkRequired", val);
        }

        public static TitleSetupRequirementsTemplateField instreamStillsRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("instreamStillsRequired", val);
        }

        public static TitleSetupRequirementsTemplateField informativeSynopsisRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("informativeSynopsisRequired", val);
        }

        public static TitleSetupRequirementsTemplateField ratingsRequired(RatingsRequirementsTestData val) {
            return new TitleSetupRequirementsTemplateField("ratingsRequired", val);
        }

        public static TitleSetupRequirementsTemplateField ratingsRequired(RatingsRequirementsField... fields) {
            return ratingsRequired(new RatingsRequirementsTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField ratingsRequired(String val) {
            return ratingsRequired(RatingsRequirementsField._name(val));
        }

        public static TitleSetupRequirementsTemplateField taggingRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("taggingRequired", val);
        }

        public static TitleSetupRequirementsTemplateField castRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("castRequired", val);
        }

        public static TitleSetupRequirementsTemplateField displayNameRequired(boolean val) {
            return new TitleSetupRequirementsTemplateField("displayNameRequired", val);
        }

        public static TitleSetupRequirementsTemplateField sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentTestData val) {
            return new TitleSetupRequirementsTemplateField("sourceRequestDefaultFulfillment", val);
        }

        public static TitleSetupRequirementsTemplateField sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField... fields) {
            return sourceRequestDefaultFulfillment(new SourceRequestDefaultFulfillmentTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField sourceRequestDefaultFulfillment(String val) {
            return sourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField._name(val));
        }

        public static TitleSetupRequirementsTemplateField recipeGroups(RecipeGroupsTestData val) {
            return new TitleSetupRequirementsTemplateField("recipeGroups", val);
        }

        public static TitleSetupRequirementsTemplateField recipeGroups(RecipeGroupsField... fields) {
            return recipeGroups(new RecipeGroupsTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField recipeGroups(String val) {
            return recipeGroups(RecipeGroupsField.value(val));
        }

        public static TitleSetupRequirementsTemplateField dateCreated(DateTestData val) {
            return new TitleSetupRequirementsTemplateField("dateCreated", val);
        }

        public static TitleSetupRequirementsTemplateField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static TitleSetupRequirementsTemplateField lastUpdated(DateTestData val) {
            return new TitleSetupRequirementsTemplateField("lastUpdated", val);
        }

        public static TitleSetupRequirementsTemplateField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static TitleSetupRequirementsTemplateField createdBy(StringTestData val) {
            return new TitleSetupRequirementsTemplateField("createdBy", val);
        }

        public static TitleSetupRequirementsTemplateField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static TitleSetupRequirementsTemplateField updatedBy(StringTestData val) {
            return new TitleSetupRequirementsTemplateField("updatedBy", val);
        }

        public static TitleSetupRequirementsTemplateField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static TitleSetupRequirementsTemplateField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TitleSetupRequirementsTemplate", 24);

    static {
        SCHEMA.addField("movieType", FieldType.REFERENCE, "MovieType");
        SCHEMA.addField("subtype", FieldType.REFERENCE, "Subtype");
        SCHEMA.addField("active", FieldType.BOOLEAN);
        SCHEMA.addField("version", FieldType.INT);
        SCHEMA.addField("enforcePackageLanguageCheck", FieldType.BOOLEAN);
        SCHEMA.addField("collectionRequired", FieldType.BOOLEAN);
        SCHEMA.addField("startEndPointRequired", FieldType.BOOLEAN);
        SCHEMA.addField("windowStartOffset", FieldType.REFERENCE, "Long");
        SCHEMA.addField("ratingReviewRequired", FieldType.BOOLEAN);
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