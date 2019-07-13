package com.netflix.vms.transformer.data.gen.videoDate;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class VideoDateTestData extends HollowTestObjectRecord {

    VideoDateTestData(VideoDateField... fields){
        super(fields);
    }

    public static VideoDateTestData VideoDate(VideoDateField... fields) {
        return new VideoDateTestData(fields);
    }

    public VideoDateTestData update(VideoDateField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public VideoDateWindowListTestData window() {
        Field f = super.getField("window");
        return f == null ? null : (VideoDateWindowListTestData)f.value;
    }

    public static class VideoDateField extends Field {

        private VideoDateField(String name, Object val) { super(name, val); }

        public static VideoDateField videoId(long val) {
            return new VideoDateField("videoId", val);
        }

        public static VideoDateField window(VideoDateWindowListTestData val) {
            return new VideoDateField("window", val);
        }

        public static VideoDateField window(VideoDateWindowTestData... elements) {
            return window(new VideoDateWindowListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoDate", 2, new PrimaryKey("VideoDate", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("window", FieldType.REFERENCE, "VideoDateWindowList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}