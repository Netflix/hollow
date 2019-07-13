package com.netflix.vms.transformer.data.gen.videoType;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoType.StringTestData.StringField;

public class VideoTypeMediaTestData extends HollowTestObjectRecord {

    VideoTypeMediaTestData(VideoTypeMediaField... fields){
        super(fields);
    }

    public static VideoTypeMediaTestData VideoTypeMedia(VideoTypeMediaField... fields) {
        return new VideoTypeMediaTestData(fields);
    }

    public VideoTypeMediaTestData update(VideoTypeMediaField... fields){
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

    public static class VideoTypeMediaField extends Field {

        private VideoTypeMediaField(String name, Object val) { super(name, val); }

        public static VideoTypeMediaField value(StringTestData val) {
            return new VideoTypeMediaField("value", val);
        }

        public static VideoTypeMediaField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static VideoTypeMediaField value(String val) {
            return value(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoTypeMedia", 1);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}