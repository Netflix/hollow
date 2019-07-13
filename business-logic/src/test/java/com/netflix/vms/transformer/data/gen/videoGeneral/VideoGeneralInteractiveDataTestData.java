package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoGeneral.StringTestData.StringField;

public class VideoGeneralInteractiveDataTestData extends HollowTestObjectRecord {

    VideoGeneralInteractiveDataTestData(VideoGeneralInteractiveDataField... fields){
        super(fields);
    }

    public static VideoGeneralInteractiveDataTestData VideoGeneralInteractiveData(VideoGeneralInteractiveDataField... fields) {
        return new VideoGeneralInteractiveDataTestData(fields);
    }

    public VideoGeneralInteractiveDataTestData update(VideoGeneralInteractiveDataField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData interactiveTypeRef() {
        Field f = super.getField("interactiveType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String interactiveType() {
        Field f = super.getField("interactiveType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int interactiveShortestRuntime() {
        Field f = super.getField("interactiveShortestRuntime");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public static class VideoGeneralInteractiveDataField extends Field {

        private VideoGeneralInteractiveDataField(String name, Object val) { super(name, val); }

        public static VideoGeneralInteractiveDataField interactiveType(StringTestData val) {
            return new VideoGeneralInteractiveDataField("interactiveType", val);
        }

        public static VideoGeneralInteractiveDataField interactiveType(StringField... fields) {
            return interactiveType(new StringTestData(fields));
        }

        public static VideoGeneralInteractiveDataField interactiveType(String val) {
            return interactiveType(StringField.value(val));
        }

        public static VideoGeneralInteractiveDataField interactiveShortestRuntime(int val) {
            return new VideoGeneralInteractiveDataField("interactiveShortestRuntime", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoGeneralInteractiveData", 2);

    static {
        SCHEMA.addField("interactiveType", FieldType.REFERENCE, "String");
        SCHEMA.addField("interactiveShortestRuntime", FieldType.INT);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}