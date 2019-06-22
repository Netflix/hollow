package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class VideoNodeTypeTestData extends HollowTestObjectRecord {

    VideoNodeTypeTestData(VideoNodeTypeField... fields){
        super(fields);
    }

    public static VideoNodeTypeTestData VideoNodeType(VideoNodeTypeField... fields) {
        return new VideoNodeTypeTestData(fields);
    }

    public static VideoNodeTypeTestData VideoNodeType(String val) {
        return VideoNodeType(VideoNodeTypeField.nodeType(val));
    }

    public VideoNodeTypeTestData update(VideoNodeTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String nodeType() {
        Field f = super.getField("nodeType");
        return f == null ? null : (String)f.value;
    }

    public static class VideoNodeTypeField extends HollowTestObjectRecord.Field {

        private VideoNodeTypeField(String name, Object val) { super(name, val); }

        public static VideoNodeTypeField nodeType(String val) {
            return new VideoNodeTypeField("nodeType", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoNodeType", 1);

    static {
        SCHEMA.addField("nodeType", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}