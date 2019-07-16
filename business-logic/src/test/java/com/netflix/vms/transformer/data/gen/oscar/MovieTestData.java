package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.BcpCodeTestData.BcpCodeField;
import com.netflix.vms.transformer.data.gen.oscar.CountryStringTestData.CountryStringField;
import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.ForceReasonTestData.ForceReasonField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTitleStringTestData.MovieTitleStringField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTypeTestData.MovieTypeField;
import com.netflix.vms.transformer.data.gen.oscar.PersonNameTestData.PersonNameField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.SupplementalSubtypeTestData.SupplementalSubtypeField;

public class MovieTestData extends HollowTestObjectRecord {

    MovieTestData(MovieField... fields){
        super(fields);
    }

    public static MovieTestData Movie(MovieField... fields) {
        return new MovieTestData(fields);
    }

    public MovieTestData update(MovieField... fields){
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

    public MovieTypeTestData type() {
        Field f = super.getField("type");
        return f == null ? null : (MovieTypeTestData)f.value;
    }

    public BcpCodeTestData originalLanguageBcpCodeRef() {
        Field f = super.getField("originalLanguageBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String originalLanguageBcpCode() {
        Field f = super.getField("originalLanguageBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public MovieTitleStringTestData originalTitleRef() {
        Field f = super.getField("originalTitle");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String originalTitle() {
        Field f = super.getField("originalTitle");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData originalTitleBcpCodeRef() {
        Field f = super.getField("originalTitleBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String originalTitleBcpCode() {
        Field f = super.getField("originalTitleBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public MovieTitleStringTestData originalTitleConcatRef() {
        Field f = super.getField("originalTitleConcat");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String originalTitleConcat() {
        Field f = super.getField("originalTitleConcat");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData originalTitleConcatBcpCodeRef() {
        Field f = super.getField("originalTitleConcatBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String originalTitleConcatBcpCode() {
        Field f = super.getField("originalTitleConcatBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public CountryStringTestData countryOfOriginRef() {
        Field f = super.getField("countryOfOrigin");
        return f == null ? null : (CountryStringTestData)f.value;
    }

    public String countryOfOrigin() {
        Field f = super.getField("countryOfOrigin");
        if(f == null) return null;
        CountryStringTestData ref = (CountryStringTestData)f.value;
        return ref.value();
    }

    public int runLenth() {
        Field f = super.getField("runLenth");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean availableInPlastic() {
        Field f = super.getField("availableInPlastic");
        return f == null ? null : (Boolean)f.value;
    }

    public int firstReleaseYear() {
        Field f = super.getField("firstReleaseYear");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean active() {
        Field f = super.getField("active");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean tv() {
        Field f = super.getField("tv");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData commentRef() {
        Field f = super.getField("comment");
        return f == null ? null : (StringTestData)f.value;
    }

    public String comment() {
        Field f = super.getField("comment");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean original() {
        Field f = super.getField("original");
        return f == null ? null : (Boolean)f.value;
    }

    public SupplementalSubtypeTestData subtypeRef() {
        Field f = super.getField("subtype");
        return f == null ? null : (SupplementalSubtypeTestData)f.value;
    }

    public String subtype() {
        Field f = super.getField("subtype");
        if(f == null) return null;
        SupplementalSubtypeTestData ref = (SupplementalSubtypeTestData)f.value;
        return ref.value();
    }

    public Boolean testTitle() {
        Field f = super.getField("testTitle");
        return f == null ? null : (Boolean)f.value;
    }

    public int metadataReleaseDays() {
        Field f = super.getField("metadataReleaseDays");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean manualMetadataReleaseDaysUpdate() {
        Field f = super.getField("manualMetadataReleaseDaysUpdate");
        return f == null ? null : (Boolean)f.value;
    }

    public MovieTitleStringTestData internalTitleRef() {
        Field f = super.getField("internalTitle");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String internalTitle() {
        Field f = super.getField("internalTitle");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData internalTitleBcpCodeRef() {
        Field f = super.getField("internalTitleBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String internalTitleBcpCode() {
        Field f = super.getField("internalTitleBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public MovieTitleStringTestData internalTitlePartRef() {
        Field f = super.getField("internalTitlePart");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String internalTitlePart() {
        Field f = super.getField("internalTitlePart");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public BcpCodeTestData internalTitlePartBcpCodeRef() {
        Field f = super.getField("internalTitlePartBcpCode");
        return f == null ? null : (BcpCodeTestData)f.value;
    }

    public String internalTitlePartBcpCode() {
        Field f = super.getField("internalTitlePartBcpCode");
        if(f == null) return null;
        BcpCodeTestData ref = (BcpCodeTestData)f.value;
        return ref.value();
    }

    public MovieTitleStringTestData searchTitleRef() {
        Field f = super.getField("searchTitle");
        return f == null ? null : (MovieTitleStringTestData)f.value;
    }

    public String searchTitle() {
        Field f = super.getField("searchTitle");
        if(f == null) return null;
        MovieTitleStringTestData ref = (MovieTitleStringTestData)f.value;
        return ref.value();
    }

    public PersonNameTestData directorRef() {
        Field f = super.getField("director");
        return f == null ? null : (PersonNameTestData)f.value;
    }

    public String director() {
        Field f = super.getField("director");
        if(f == null) return null;
        PersonNameTestData ref = (PersonNameTestData)f.value;
        return ref.value();
    }

    public PersonNameTestData creatorRef() {
        Field f = super.getField("creator");
        return f == null ? null : (PersonNameTestData)f.value;
    }

    public String creator() {
        Field f = super.getField("creator");
        if(f == null) return null;
        PersonNameTestData ref = (PersonNameTestData)f.value;
        return ref.value();
    }

    public ForceReasonTestData forceReasonRef() {
        Field f = super.getField("forceReason");
        return f == null ? null : (ForceReasonTestData)f.value;
    }

    public String forceReason() {
        Field f = super.getField("forceReason");
        if(f == null) return null;
        ForceReasonTestData ref = (ForceReasonTestData)f.value;
        return ref.value();
    }

    public Boolean visible() {
        Field f = super.getField("visible");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData createdByTeamRef() {
        Field f = super.getField("createdByTeam");
        return f == null ? null : (StringTestData)f.value;
    }

    public String createdByTeam() {
        Field f = super.getField("createdByTeam");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData updatedByTeamRef() {
        Field f = super.getField("updatedByTeam");
        return f == null ? null : (StringTestData)f.value;
    }

    public String updatedByTeam() {
        Field f = super.getField("updatedByTeam");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
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

    public static class MovieField extends HollowTestObjectRecord.Field {

        private MovieField(String name, Object val) { super(name, val); }

        public static MovieField movieId(MovieIdTestData val) {
            return new MovieField("movieId", val);
        }

        public static MovieField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieField type(MovieTypeTestData val) {
            return new MovieField("type", val);
        }

        public static MovieField type(MovieTypeField... fields) {
            return type(new MovieTypeTestData(fields));
        }

        public static MovieField originalLanguageBcpCode(BcpCodeTestData val) {
            return new MovieField("originalLanguageBcpCode", val);
        }

        public static MovieField originalLanguageBcpCode(BcpCodeField... fields) {
            return originalLanguageBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieField originalLanguageBcpCode(String val) {
            return originalLanguageBcpCode(BcpCodeField.value(val));
        }

        public static MovieField originalTitle(MovieTitleStringTestData val) {
            return new MovieField("originalTitle", val);
        }

        public static MovieField originalTitle(MovieTitleStringField... fields) {
            return originalTitle(new MovieTitleStringTestData(fields));
        }

        public static MovieField originalTitle(String val) {
            return originalTitle(MovieTitleStringField.value(val));
        }

        public static MovieField originalTitleBcpCode(BcpCodeTestData val) {
            return new MovieField("originalTitleBcpCode", val);
        }

        public static MovieField originalTitleBcpCode(BcpCodeField... fields) {
            return originalTitleBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieField originalTitleBcpCode(String val) {
            return originalTitleBcpCode(BcpCodeField.value(val));
        }

        public static MovieField originalTitleConcat(MovieTitleStringTestData val) {
            return new MovieField("originalTitleConcat", val);
        }

        public static MovieField originalTitleConcat(MovieTitleStringField... fields) {
            return originalTitleConcat(new MovieTitleStringTestData(fields));
        }

        public static MovieField originalTitleConcat(String val) {
            return originalTitleConcat(MovieTitleStringField.value(val));
        }

        public static MovieField originalTitleConcatBcpCode(BcpCodeTestData val) {
            return new MovieField("originalTitleConcatBcpCode", val);
        }

        public static MovieField originalTitleConcatBcpCode(BcpCodeField... fields) {
            return originalTitleConcatBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieField originalTitleConcatBcpCode(String val) {
            return originalTitleConcatBcpCode(BcpCodeField.value(val));
        }

        public static MovieField countryOfOrigin(CountryStringTestData val) {
            return new MovieField("countryOfOrigin", val);
        }

        public static MovieField countryOfOrigin(CountryStringField... fields) {
            return countryOfOrigin(new CountryStringTestData(fields));
        }

        public static MovieField countryOfOrigin(String val) {
            return countryOfOrigin(CountryStringField.value(val));
        }

        public static MovieField runLenth(int val) {
            return new MovieField("runLenth", val);
        }

        public static MovieField availableInPlastic(boolean val) {
            return new MovieField("availableInPlastic", val);
        }

        public static MovieField firstReleaseYear(int val) {
            return new MovieField("firstReleaseYear", val);
        }

        public static MovieField active(boolean val) {
            return new MovieField("active", val);
        }

        public static MovieField tv(boolean val) {
            return new MovieField("tv", val);
        }

        public static MovieField comment(StringTestData val) {
            return new MovieField("comment", val);
        }

        public static MovieField comment(StringField... fields) {
            return comment(new StringTestData(fields));
        }

        public static MovieField comment(String val) {
            return comment(StringField.value(val));
        }

        public static MovieField original(boolean val) {
            return new MovieField("original", val);
        }

        public static MovieField subtype(SupplementalSubtypeTestData val) {
            return new MovieField("subtype", val);
        }

        public static MovieField subtype(SupplementalSubtypeField... fields) {
            return subtype(new SupplementalSubtypeTestData(fields));
        }

        public static MovieField subtype(String val) {
            return subtype(SupplementalSubtypeField.value(val));
        }

        public static MovieField testTitle(boolean val) {
            return new MovieField("testTitle", val);
        }

        public static MovieField metadataReleaseDays(int val) {
            return new MovieField("metadataReleaseDays", val);
        }

        public static MovieField manualMetadataReleaseDaysUpdate(boolean val) {
            return new MovieField("manualMetadataReleaseDaysUpdate", val);
        }

        public static MovieField internalTitle(MovieTitleStringTestData val) {
            return new MovieField("internalTitle", val);
        }

        public static MovieField internalTitle(MovieTitleStringField... fields) {
            return internalTitle(new MovieTitleStringTestData(fields));
        }

        public static MovieField internalTitle(String val) {
            return internalTitle(MovieTitleStringField.value(val));
        }

        public static MovieField internalTitleBcpCode(BcpCodeTestData val) {
            return new MovieField("internalTitleBcpCode", val);
        }

        public static MovieField internalTitleBcpCode(BcpCodeField... fields) {
            return internalTitleBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieField internalTitleBcpCode(String val) {
            return internalTitleBcpCode(BcpCodeField.value(val));
        }

        public static MovieField internalTitlePart(MovieTitleStringTestData val) {
            return new MovieField("internalTitlePart", val);
        }

        public static MovieField internalTitlePart(MovieTitleStringField... fields) {
            return internalTitlePart(new MovieTitleStringTestData(fields));
        }

        public static MovieField internalTitlePart(String val) {
            return internalTitlePart(MovieTitleStringField.value(val));
        }

        public static MovieField internalTitlePartBcpCode(BcpCodeTestData val) {
            return new MovieField("internalTitlePartBcpCode", val);
        }

        public static MovieField internalTitlePartBcpCode(BcpCodeField... fields) {
            return internalTitlePartBcpCode(new BcpCodeTestData(fields));
        }

        public static MovieField internalTitlePartBcpCode(String val) {
            return internalTitlePartBcpCode(BcpCodeField.value(val));
        }

        public static MovieField searchTitle(MovieTitleStringTestData val) {
            return new MovieField("searchTitle", val);
        }

        public static MovieField searchTitle(MovieTitleStringField... fields) {
            return searchTitle(new MovieTitleStringTestData(fields));
        }

        public static MovieField searchTitle(String val) {
            return searchTitle(MovieTitleStringField.value(val));
        }

        public static MovieField director(PersonNameTestData val) {
            return new MovieField("director", val);
        }

        public static MovieField director(PersonNameField... fields) {
            return director(new PersonNameTestData(fields));
        }

        public static MovieField director(String val) {
            return director(PersonNameField.value(val));
        }

        public static MovieField creator(PersonNameTestData val) {
            return new MovieField("creator", val);
        }

        public static MovieField creator(PersonNameField... fields) {
            return creator(new PersonNameTestData(fields));
        }

        public static MovieField creator(String val) {
            return creator(PersonNameField.value(val));
        }

        public static MovieField forceReason(ForceReasonTestData val) {
            return new MovieField("forceReason", val);
        }

        public static MovieField forceReason(ForceReasonField... fields) {
            return forceReason(new ForceReasonTestData(fields));
        }

        public static MovieField forceReason(String val) {
            return forceReason(ForceReasonField.value(val));
        }

        public static MovieField visible(boolean val) {
            return new MovieField("visible", val);
        }

        public static MovieField createdByTeam(StringTestData val) {
            return new MovieField("createdByTeam", val);
        }

        public static MovieField createdByTeam(StringField... fields) {
            return createdByTeam(new StringTestData(fields));
        }

        public static MovieField createdByTeam(String val) {
            return createdByTeam(StringField.value(val));
        }

        public static MovieField updatedByTeam(StringTestData val) {
            return new MovieField("updatedByTeam", val);
        }

        public static MovieField updatedByTeam(StringField... fields) {
            return updatedByTeam(new StringTestData(fields));
        }

        public static MovieField updatedByTeam(String val) {
            return updatedByTeam(StringField.value(val));
        }

        public static MovieField dateCreated(DateTestData val) {
            return new MovieField("dateCreated", val);
        }

        public static MovieField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static MovieField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static MovieField lastUpdated(DateTestData val) {
            return new MovieField("lastUpdated", val);
        }

        public static MovieField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static MovieField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static MovieField createdBy(StringTestData val) {
            return new MovieField("createdBy", val);
        }

        public static MovieField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static MovieField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static MovieField updatedBy(StringTestData val) {
            return new MovieField("updatedBy", val);
        }

        public static MovieField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static MovieField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Movie", 34, new PrimaryKey("Movie", "movieId"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("type", FieldType.REFERENCE, "MovieType");
        SCHEMA.addField("originalLanguageBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("originalTitle", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("originalTitleBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("originalTitleConcat", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("originalTitleConcatBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("countryOfOrigin", FieldType.REFERENCE, "CountryString");
        SCHEMA.addField("runLenth", FieldType.INT);
        SCHEMA.addField("availableInPlastic", FieldType.BOOLEAN);
        SCHEMA.addField("firstReleaseYear", FieldType.INT);
        SCHEMA.addField("active", FieldType.BOOLEAN);
        SCHEMA.addField("tv", FieldType.BOOLEAN);
        SCHEMA.addField("comment", FieldType.REFERENCE, "String");
        SCHEMA.addField("original", FieldType.BOOLEAN);
        SCHEMA.addField("subtype", FieldType.REFERENCE, "SupplementalSubtype");
        SCHEMA.addField("testTitle", FieldType.BOOLEAN);
        SCHEMA.addField("metadataReleaseDays", FieldType.INT);
        SCHEMA.addField("manualMetadataReleaseDaysUpdate", FieldType.BOOLEAN);
        SCHEMA.addField("internalTitle", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("internalTitleBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("internalTitlePart", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("internalTitlePartBcpCode", FieldType.REFERENCE, "BcpCode");
        SCHEMA.addField("searchTitle", FieldType.REFERENCE, "MovieTitleString");
        SCHEMA.addField("director", FieldType.REFERENCE, "PersonName");
        SCHEMA.addField("creator", FieldType.REFERENCE, "PersonName");
        SCHEMA.addField("forceReason", FieldType.REFERENCE, "ForceReason");
        SCHEMA.addField("visible", FieldType.BOOLEAN);
        SCHEMA.addField("createdByTeam", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedByTeam", FieldType.REFERENCE, "String");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}