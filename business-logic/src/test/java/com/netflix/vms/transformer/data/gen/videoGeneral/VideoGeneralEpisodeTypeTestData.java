package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoGeneral.StringTestData.StringField;

public class VideoGeneralEpisodeTypeTestData extends HollowTestObjectRecord {

    VideoGeneralEpisodeTypeTestData(VideoGeneralEpisodeTypeField... fields){
        super(fields);
    }

    public static VideoGeneralEpisodeTypeTestData VideoGeneralEpisodeType(VideoGeneralEpisodeTypeField... fields) {
        return new VideoGeneralEpisodeTypeTestData(fields);
    }

    public VideoGeneralEpisodeTypeTestData update(VideoGeneralEpisodeTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData valueRef() {
        Field f = super.getField("value");
        return f == null ? null : (StringTestData)f.value;
    }

    public String value() {
        Field f = super.getField("value");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData countryRef() {
        Field f = super.getField("country");
        return f == null ? null : (StringTestData)f.value;
    }

    public String country() {
        Field f = super.getField("country");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class VideoGeneralEpisodeTypeField extends Field {

        private VideoGeneralEpisodeTypeField(String name, Object val) { super(name, val); }

        public static VideoGeneralEpisodeTypeField value(StringTestData val) {
            return new VideoGeneralEpisodeTypeField("value", val);
        }

        public static VideoGeneralEpisodeTypeField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static VideoGeneralEpisodeTypeField value(String val) {
            return value(StringField.value(val));
        }

        public static VideoGeneralEpisodeTypeField country(StringTestData val) {
            return new VideoGeneralEpisodeTypeField("country", val);
        }

        public static VideoGeneralEpisodeTypeField country(StringField... fields) {
            return country(new StringTestData(fields));
        }

        public static VideoGeneralEpisodeTypeField country(String val) {
            return country(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoGeneralEpisodeType", 2);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
        SCHEMA.addField("country", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}