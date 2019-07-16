package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.gatekeeper2.ParentNodeIdTestData.ParentNodeIdField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.VideoNodeTypeTestData.VideoNodeTypeField;

public class VideoHierarchyInfoTestData extends HollowTestObjectRecord {

    VideoHierarchyInfoTestData(VideoHierarchyInfoField... fields){
        super(fields);
    }

    public static VideoHierarchyInfoTestData VideoHierarchyInfo(VideoHierarchyInfoField... fields) {
        return new VideoHierarchyInfoTestData(fields);
    }

    public VideoHierarchyInfoTestData update(VideoHierarchyInfoField... fields){
        super.addFields(fields);
        return this;
    }

    public ParentNodeIdTestData topNodeIdRef() {
        Field f = super.getField("topNodeId");
        return f == null ? null : (ParentNodeIdTestData)f.value;
    }

    public long topNodeId() {
        Field f = super.getField("topNodeId");
        if(f == null) return Long.MIN_VALUE;
        ParentNodeIdTestData ref = (ParentNodeIdTestData)f.value;
        return ref.value();
    }

    public ParentNodeIdTestData parentIdRef() {
        Field f = super.getField("parentId");
        return f == null ? null : (ParentNodeIdTestData)f.value;
    }

    public long parentId() {
        Field f = super.getField("parentId");
        if(f == null) return Long.MIN_VALUE;
        ParentNodeIdTestData ref = (ParentNodeIdTestData)f.value;
        return ref.value();
    }

    public VideoNodeTypeTestData nodeTypeRef() {
        Field f = super.getField("nodeType");
        return f == null ? null : (VideoNodeTypeTestData)f.value;
    }

    public String nodeType() {
        Field f = super.getField("nodeType");
        if(f == null) return null;
        VideoNodeTypeTestData ref = (VideoNodeTypeTestData)f.value;
        return ref.nodeType();
    }

    public static class VideoHierarchyInfoField extends HollowTestObjectRecord.Field {

        private VideoHierarchyInfoField(String name, Object val) { super(name, val); }

        public static VideoHierarchyInfoField topNodeId(ParentNodeIdTestData val) {
            return new VideoHierarchyInfoField("topNodeId", val);
        }

        public static VideoHierarchyInfoField topNodeId(ParentNodeIdField... fields) {
            return topNodeId(new ParentNodeIdTestData(fields));
        }

        public static VideoHierarchyInfoField topNodeId(long val) {
            return topNodeId(ParentNodeIdField.value(val));
        }

        public static VideoHierarchyInfoField parentId(ParentNodeIdTestData val) {
            return new VideoHierarchyInfoField("parentId", val);
        }

        public static VideoHierarchyInfoField parentId(ParentNodeIdField... fields) {
            return parentId(new ParentNodeIdTestData(fields));
        }

        public static VideoHierarchyInfoField parentId(long val) {
            return parentId(ParentNodeIdField.value(val));
        }

        public static VideoHierarchyInfoField nodeType(VideoNodeTypeTestData val) {
            return new VideoHierarchyInfoField("nodeType", val);
        }

        public static VideoHierarchyInfoField nodeType(VideoNodeTypeField... fields) {
            return nodeType(new VideoNodeTypeTestData(fields));
        }

        public static VideoHierarchyInfoField nodeType(String val) {
            return nodeType(VideoNodeTypeField.nodeType(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoHierarchyInfo", 3);

    static {
        SCHEMA.addField("topNodeId", FieldType.REFERENCE, "ParentNodeId");
        SCHEMA.addField("parentId", FieldType.REFERENCE, "ParentNodeId");
        SCHEMA.addField("nodeType", FieldType.REFERENCE, "VideoNodeType");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}