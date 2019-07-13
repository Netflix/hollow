package com.netflix.vms.transformer.data.gen.videoType;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class VideoTypeTestData extends HollowTestObjectRecord {

    VideoTypeTestData(VideoTypeField... fields){
        super(fields);
    }

    public static VideoTypeTestData VideoType(VideoTypeField... fields) {
        return new VideoTypeTestData(fields);
    }

    public VideoTypeTestData update(VideoTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public VideoTypeDescriptorSetTestData countryInfos() {
        Field f = super.getField("countryInfos");
        return f == null ? null : (VideoTypeDescriptorSetTestData)f.value;
    }

    public static class VideoTypeField extends Field {

        private VideoTypeField(String name, Object val) { super(name, val); }

        public static VideoTypeField videoId(long val) {
            return new VideoTypeField("videoId", val);
        }

        public static VideoTypeField countryInfos(VideoTypeDescriptorSetTestData val) {
            return new VideoTypeField("countryInfos", val);
        }

        public static VideoTypeField countryInfos(VideoTypeDescriptorTestData... elements) {
            return countryInfos(new VideoTypeDescriptorSetTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoType", 2, new PrimaryKey("VideoType", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("countryInfos", FieldType.REFERENCE, "VideoTypeDescriptorSet");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}