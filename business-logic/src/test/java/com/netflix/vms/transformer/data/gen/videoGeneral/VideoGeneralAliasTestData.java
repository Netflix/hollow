package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoGeneral.StringTestData.StringField;

public class VideoGeneralAliasTestData extends HollowTestObjectRecord {

    VideoGeneralAliasTestData(VideoGeneralAliasField... fields){
        super(fields);
    }

    public static VideoGeneralAliasTestData VideoGeneralAlias(VideoGeneralAliasField... fields) {
        return new VideoGeneralAliasTestData(fields);
    }

    public VideoGeneralAliasTestData update(VideoGeneralAliasField... fields){
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

    public static class VideoGeneralAliasField extends Field {

        private VideoGeneralAliasField(String name, Object val) { super(name, val); }

        public static VideoGeneralAliasField value(StringTestData val) {
            return new VideoGeneralAliasField("value", val);
        }

        public static VideoGeneralAliasField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static VideoGeneralAliasField value(String val) {
            return value(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoGeneralAlias", 1);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}