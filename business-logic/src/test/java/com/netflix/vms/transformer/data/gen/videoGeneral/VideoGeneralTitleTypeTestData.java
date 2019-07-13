package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoGeneral.StringTestData.StringField;

public class VideoGeneralTitleTypeTestData extends HollowTestObjectRecord {

    VideoGeneralTitleTypeTestData(VideoGeneralTitleTypeField... fields){
        super(fields);
    }

    public static VideoGeneralTitleTypeTestData VideoGeneralTitleType(VideoGeneralTitleTypeField... fields) {
        return new VideoGeneralTitleTypeTestData(fields);
    }

    public VideoGeneralTitleTypeTestData update(VideoGeneralTitleTypeField... fields){
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

    public static class VideoGeneralTitleTypeField extends Field {

        private VideoGeneralTitleTypeField(String name, Object val) { super(name, val); }

        public static VideoGeneralTitleTypeField value(StringTestData val) {
            return new VideoGeneralTitleTypeField("value", val);
        }

        public static VideoGeneralTitleTypeField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static VideoGeneralTitleTypeField value(String val) {
            return value(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoGeneralTitleType", 1);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}