package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoGeneral.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.videoGeneral.VideoGeneralInteractiveDataTestData.VideoGeneralInteractiveDataField;

public class VideoGeneralTestData extends HollowTestObjectRecord {

    VideoGeneralTestData(VideoGeneralField... fields){
        super(fields);
    }

    public static VideoGeneralTestData VideoGeneral(VideoGeneralField... fields) {
        return new VideoGeneralTestData(fields);
    }

    public VideoGeneralTestData update(VideoGeneralField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public Boolean tv() {
        Field f = super.getField("tv");
        return f == null ? null : (Boolean)f.value;
    }

    public VideoGeneralAliasListTestData aliases() {
        Field f = super.getField("aliases");
        return f == null ? null : (VideoGeneralAliasListTestData)f.value;
    }

    public StringTestData videoTypeRef() {
        Field f = super.getField("videoType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String videoType() {
        Field f = super.getField("videoType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int runtime() {
        Field f = super.getField("runtime");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData supplementalSubTypeRef() {
        Field f = super.getField("supplementalSubType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String supplementalSubType() {
        Field f = super.getField("supplementalSubType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int firstReleaseYear() {
        Field f = super.getField("firstReleaseYear");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean testTitle() {
        Field f = super.getField("testTitle");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData originalLanguageBcpCodeRef() {
        Field f = super.getField("originalLanguageBcpCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String originalLanguageBcpCode() {
        Field f = super.getField("originalLanguageBcpCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int metadataReleaseDays() {
        Field f = super.getField("metadataReleaseDays");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData originCountryCodeRef() {
        Field f = super.getField("originCountryCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String originCountryCode() {
        Field f = super.getField("originCountryCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData originalTitleRef() {
        Field f = super.getField("originalTitle");
        return f == null ? null : (StringTestData)f.value;
    }

    public String originalTitle() {
        Field f = super.getField("originalTitle");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public VideoGeneralTitleTypeListTestData testTitleTypes() {
        Field f = super.getField("testTitleTypes");
        return f == null ? null : (VideoGeneralTitleTypeListTestData)f.value;
    }

    public StringTestData originalTitleBcpCodeRef() {
        Field f = super.getField("originalTitleBcpCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String originalTitleBcpCode() {
        Field f = super.getField("originalTitleBcpCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData internalTitleRef() {
        Field f = super.getField("internalTitle");
        return f == null ? null : (StringTestData)f.value;
    }

    public String internalTitle() {
        Field f = super.getField("internalTitle");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public VideoGeneralEpisodeTypeListTestData episodeTypes() {
        Field f = super.getField("episodeTypes");
        return f == null ? null : (VideoGeneralEpisodeTypeListTestData)f.value;
    }

    public SetOfStringTestData regulatoryAdvisories() {
        Field f = super.getField("regulatoryAdvisories");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public Boolean active() {
        Field f = super.getField("active");
        return f == null ? null : (Boolean)f.value;
    }

    public int displayRuntime() {
        Field f = super.getField("displayRuntime");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public VideoGeneralInteractiveDataTestData interactiveData() {
        Field f = super.getField("interactiveData");
        return f == null ? null : (VideoGeneralInteractiveDataTestData)f.value;
    }

    public static class VideoGeneralField extends Field {

        private VideoGeneralField(String name, Object val) { super(name, val); }

        public static VideoGeneralField videoId(long val) {
            return new VideoGeneralField("videoId", val);
        }

        public static VideoGeneralField tv(boolean val) {
            return new VideoGeneralField("tv", val);
        }

        public static VideoGeneralField aliases(VideoGeneralAliasListTestData val) {
            return new VideoGeneralField("aliases", val);
        }

        public static VideoGeneralField aliases(VideoGeneralAliasTestData... elements) {
            return aliases(new VideoGeneralAliasListTestData(elements));
        }

        public static VideoGeneralField videoType(StringTestData val) {
            return new VideoGeneralField("videoType", val);
        }

        public static VideoGeneralField videoType(StringField... fields) {
            return videoType(new StringTestData(fields));
        }

        public static VideoGeneralField videoType(String val) {
            return videoType(StringField.value(val));
        }

        public static VideoGeneralField runtime(int val) {
            return new VideoGeneralField("runtime", val);
        }

        public static VideoGeneralField supplementalSubType(StringTestData val) {
            return new VideoGeneralField("supplementalSubType", val);
        }

        public static VideoGeneralField supplementalSubType(StringField... fields) {
            return supplementalSubType(new StringTestData(fields));
        }

        public static VideoGeneralField supplementalSubType(String val) {
            return supplementalSubType(StringField.value(val));
        }

        public static VideoGeneralField firstReleaseYear(int val) {
            return new VideoGeneralField("firstReleaseYear", val);
        }

        public static VideoGeneralField testTitle(boolean val) {
            return new VideoGeneralField("testTitle", val);
        }

        public static VideoGeneralField originalLanguageBcpCode(StringTestData val) {
            return new VideoGeneralField("originalLanguageBcpCode", val);
        }

        public static VideoGeneralField originalLanguageBcpCode(StringField... fields) {
            return originalLanguageBcpCode(new StringTestData(fields));
        }

        public static VideoGeneralField originalLanguageBcpCode(String val) {
            return originalLanguageBcpCode(StringField.value(val));
        }

        public static VideoGeneralField metadataReleaseDays(int val) {
            return new VideoGeneralField("metadataReleaseDays", val);
        }

        public static VideoGeneralField originCountryCode(StringTestData val) {
            return new VideoGeneralField("originCountryCode", val);
        }

        public static VideoGeneralField originCountryCode(StringField... fields) {
            return originCountryCode(new StringTestData(fields));
        }

        public static VideoGeneralField originCountryCode(String val) {
            return originCountryCode(StringField.value(val));
        }

        public static VideoGeneralField originalTitle(StringTestData val) {
            return new VideoGeneralField("originalTitle", val);
        }

        public static VideoGeneralField originalTitle(StringField... fields) {
            return originalTitle(new StringTestData(fields));
        }

        public static VideoGeneralField originalTitle(String val) {
            return originalTitle(StringField.value(val));
        }

        public static VideoGeneralField testTitleTypes(VideoGeneralTitleTypeListTestData val) {
            return new VideoGeneralField("testTitleTypes", val);
        }

        public static VideoGeneralField testTitleTypes(VideoGeneralTitleTypeTestData... elements) {
            return testTitleTypes(new VideoGeneralTitleTypeListTestData(elements));
        }

        public static VideoGeneralField originalTitleBcpCode(StringTestData val) {
            return new VideoGeneralField("originalTitleBcpCode", val);
        }

        public static VideoGeneralField originalTitleBcpCode(StringField... fields) {
            return originalTitleBcpCode(new StringTestData(fields));
        }

        public static VideoGeneralField originalTitleBcpCode(String val) {
            return originalTitleBcpCode(StringField.value(val));
        }

        public static VideoGeneralField internalTitle(StringTestData val) {
            return new VideoGeneralField("internalTitle", val);
        }

        public static VideoGeneralField internalTitle(StringField... fields) {
            return internalTitle(new StringTestData(fields));
        }

        public static VideoGeneralField internalTitle(String val) {
            return internalTitle(StringField.value(val));
        }

        public static VideoGeneralField episodeTypes(VideoGeneralEpisodeTypeListTestData val) {
            return new VideoGeneralField("episodeTypes", val);
        }

        public static VideoGeneralField episodeTypes(VideoGeneralEpisodeTypeTestData... elements) {
            return episodeTypes(new VideoGeneralEpisodeTypeListTestData(elements));
        }

        public static VideoGeneralField regulatoryAdvisories(SetOfStringTestData val) {
            return new VideoGeneralField("regulatoryAdvisories", val);
        }

        public static VideoGeneralField regulatoryAdvisories(StringTestData... elements) {
            return regulatoryAdvisories(new SetOfStringTestData(elements));
        }

        public static VideoGeneralField active(boolean val) {
            return new VideoGeneralField("active", val);
        }

        public static VideoGeneralField displayRuntime(int val) {
            return new VideoGeneralField("displayRuntime", val);
        }

        public static VideoGeneralField interactiveData(VideoGeneralInteractiveDataTestData val) {
            return new VideoGeneralField("interactiveData", val);
        }

        public static VideoGeneralField interactiveData(VideoGeneralInteractiveDataField... fields) {
            return interactiveData(new VideoGeneralInteractiveDataTestData(fields));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoGeneral", 20, new PrimaryKey("VideoGeneral", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("tv", FieldType.BOOLEAN);
        SCHEMA.addField("aliases", FieldType.REFERENCE, "VideoGeneralAliasList");
        SCHEMA.addField("videoType", FieldType.REFERENCE, "String");
        SCHEMA.addField("runtime", FieldType.INT);
        SCHEMA.addField("supplementalSubType", FieldType.REFERENCE, "String");
        SCHEMA.addField("firstReleaseYear", FieldType.INT);
        SCHEMA.addField("testTitle", FieldType.BOOLEAN);
        SCHEMA.addField("originalLanguageBcpCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("metadataReleaseDays", FieldType.INT);
        SCHEMA.addField("originCountryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("originalTitle", FieldType.REFERENCE, "String");
        SCHEMA.addField("testTitleTypes", FieldType.REFERENCE, "VideoGeneralTitleTypeList");
        SCHEMA.addField("originalTitleBcpCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("internalTitle", FieldType.REFERENCE, "String");
        SCHEMA.addField("episodeTypes", FieldType.REFERENCE, "VideoGeneralEpisodeTypeList");
        SCHEMA.addField("regulatoryAdvisories", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("active", FieldType.BOOLEAN);
        SCHEMA.addField("displayRuntime", FieldType.INT);
        SCHEMA.addField("interactiveData", FieldType.REFERENCE, "VideoGeneralInteractiveData");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}