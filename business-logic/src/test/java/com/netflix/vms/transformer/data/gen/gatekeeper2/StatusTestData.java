package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.gatekeeper2.AvailableAssetsTestData.AvailableAssetsField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.FlagsTestData.FlagsField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.RightsTestData.RightsField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.VideoHierarchyInfoTestData.VideoHierarchyInfoField;

public class StatusTestData extends HollowTestObjectRecord {

    StatusTestData(StatusField... fields){
        super(fields);
    }

    public static StatusTestData Status(StatusField... fields) {
        return new StatusTestData(fields);
    }

    public StatusTestData update(StatusField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
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

    public RightsTestData rights() {
        Field f = super.getField("rights");
        return f == null ? null : (RightsTestData)f.value;
    }

    public FlagsTestData flags() {
        Field f = super.getField("flags");
        return f == null ? null : (FlagsTestData)f.value;
    }

    public AvailableAssetsTestData availableAssets() {
        Field f = super.getField("availableAssets");
        return f == null ? null : (AvailableAssetsTestData)f.value;
    }

    public VideoHierarchyInfoTestData hierarchyInfo() {
        Field f = super.getField("hierarchyInfo");
        return f == null ? null : (VideoHierarchyInfoTestData)f.value;
    }

    public static class StatusField extends HollowTestObjectRecord.Field {

        private StatusField(String name, Object val) { super(name, val); }

        public static StatusField movieId(long val) {
            return new StatusField("movieId", val);
        }

        public static StatusField countryCode(StringTestData val) {
            return new StatusField("countryCode", val);
        }

        public static StatusField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static StatusField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static StatusField rights(RightsTestData val) {
            return new StatusField("rights", val);
        }

        public static StatusField rights(RightsField... fields) {
            return rights(new RightsTestData(fields));
        }

        public static StatusField flags(FlagsTestData val) {
            return new StatusField("flags", val);
        }

        public static StatusField flags(FlagsField... fields) {
            return flags(new FlagsTestData(fields));
        }

        public static StatusField availableAssets(AvailableAssetsTestData val) {
            return new StatusField("availableAssets", val);
        }

        public static StatusField availableAssets(AvailableAssetsField... fields) {
            return availableAssets(new AvailableAssetsTestData(fields));
        }

        public static StatusField hierarchyInfo(VideoHierarchyInfoTestData val) {
            return new StatusField("hierarchyInfo", val);
        }

        public static StatusField hierarchyInfo(VideoHierarchyInfoField... fields) {
            return hierarchyInfo(new VideoHierarchyInfoTestData(fields));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Status", 6, new PrimaryKey("Status", "movieId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("rights", FieldType.REFERENCE, "Rights");
        SCHEMA.addField("flags", FieldType.REFERENCE, "Flags");
        SCHEMA.addField("availableAssets", FieldType.REFERENCE, "AvailableAssets");
        SCHEMA.addField("hierarchyInfo", FieldType.REFERENCE, "VideoHierarchyInfo");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}