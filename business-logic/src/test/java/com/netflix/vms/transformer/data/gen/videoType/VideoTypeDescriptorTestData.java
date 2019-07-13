package com.netflix.vms.transformer.data.gen.videoType;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoType.StringTestData.StringField;

public class VideoTypeDescriptorTestData extends HollowTestObjectRecord {

    VideoTypeDescriptorTestData(VideoTypeDescriptorField... fields){
        super(fields);
    }

    public static VideoTypeDescriptorTestData VideoTypeDescriptor(VideoTypeDescriptorField... fields) {
        return new VideoTypeDescriptorTestData(fields);
    }

    public VideoTypeDescriptorTestData update(VideoTypeDescriptorField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData countryCodeRef() {
        Field f = super.getField("countryCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String countryCode() {
        Field f = super.getField("countryCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData copyrightRef() {
        Field f = super.getField("copyright");
        return f == null ? null : (StringTestData)f.value;
    }

    public String copyright() {
        Field f = super.getField("copyright");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData tierTypeRef() {
        Field f = super.getField("tierType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String tierType() {
        Field f = super.getField("tierType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean original() {
        Field f = super.getField("original");
        return f == null ? null : (Boolean)f.value;
    }

    public VideoTypeMediaListTestData media() {
        Field f = super.getField("media");
        return f == null ? null : (VideoTypeMediaListTestData)f.value;
    }

    public Boolean extended() {
        Field f = super.getField("extended");
        return f == null ? null : (Boolean)f.value;
    }

    public static class VideoTypeDescriptorField extends Field {

        private VideoTypeDescriptorField(String name, Object val) { super(name, val); }

        public static VideoTypeDescriptorField countryCode(StringTestData val) {
            return new VideoTypeDescriptorField("countryCode", val);
        }

        public static VideoTypeDescriptorField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static VideoTypeDescriptorField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static VideoTypeDescriptorField copyright(StringTestData val) {
            return new VideoTypeDescriptorField("copyright", val);
        }

        public static VideoTypeDescriptorField copyright(StringField... fields) {
            return copyright(new StringTestData(fields));
        }

        public static VideoTypeDescriptorField copyright(String val) {
            return copyright(StringField.value(val));
        }

        public static VideoTypeDescriptorField tierType(StringTestData val) {
            return new VideoTypeDescriptorField("tierType", val);
        }

        public static VideoTypeDescriptorField tierType(StringField... fields) {
            return tierType(new StringTestData(fields));
        }

        public static VideoTypeDescriptorField tierType(String val) {
            return tierType(StringField.value(val));
        }

        public static VideoTypeDescriptorField original(boolean val) {
            return new VideoTypeDescriptorField("original", val);
        }

        public static VideoTypeDescriptorField media(VideoTypeMediaListTestData val) {
            return new VideoTypeDescriptorField("media", val);
        }

        public static VideoTypeDescriptorField media(VideoTypeMediaTestData... elements) {
            return media(new VideoTypeMediaListTestData(elements));
        }

        public static VideoTypeDescriptorField extended(boolean val) {
            return new VideoTypeDescriptorField("extended", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoTypeDescriptor", 6);

    static {
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("copyright", FieldType.REFERENCE, "String");
        SCHEMA.addField("tierType", FieldType.REFERENCE, "String");
        SCHEMA.addField("original", FieldType.BOOLEAN);
        SCHEMA.addField("media", FieldType.REFERENCE, "VideoTypeMediaList");
        SCHEMA.addField("extended", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}