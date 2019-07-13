package com.netflix.vms.transformer.data.gen.videoAward;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class VideoAwardTestData extends HollowTestObjectRecord {

    VideoAwardTestData(VideoAwardField... fields){
        super(fields);
    }

    public static VideoAwardTestData VideoAward(VideoAwardField... fields) {
        return new VideoAwardTestData(fields);
    }

    public VideoAwardTestData update(VideoAwardField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public VideoAwardListTestData award() {
        Field f = super.getField("award");
        return f == null ? null : (VideoAwardListTestData)f.value;
    }

    public static class VideoAwardField extends Field {

        private VideoAwardField(String name, Object val) { super(name, val); }

        public static VideoAwardField videoId(long val) {
            return new VideoAwardField("videoId", val);
        }

        public static VideoAwardField award(VideoAwardListTestData val) {
            return new VideoAwardField("award", val);
        }

        public static VideoAwardField award(VideoAwardMappingTestData... elements) {
            return award(new VideoAwardListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoAward", 2, new PrimaryKey("VideoAward", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("award", FieldType.REFERENCE, "VideoAwardList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}