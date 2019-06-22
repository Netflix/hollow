package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.gatekeeper2.StringTestData.StringField;

public class RightsContractAssetTestData extends HollowTestObjectRecord {

    RightsContractAssetTestData(RightsContractAssetField... fields){
        super(fields);
    }

    public static RightsContractAssetTestData RightsContractAsset(RightsContractAssetField... fields) {
        return new RightsContractAssetTestData(fields);
    }

    public RightsContractAssetTestData update(RightsContractAssetField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData bcp47CodeRef() {
        Field f = super.getField("bcp47Code");
        return f == null ? null : (StringTestData)f.value;
    }

    public String bcp47Code() {
        Field f = super.getField("bcp47Code");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData assetTypeRef() {
        Field f = super.getField("assetType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String assetType() {
        Field f = super.getField("assetType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class RightsContractAssetField extends HollowTestObjectRecord.Field {

        private RightsContractAssetField(String name, Object val) { super(name, val); }

        public static RightsContractAssetField bcp47Code(StringTestData val) {
            return new RightsContractAssetField("bcp47Code", val);
        }

        public static RightsContractAssetField bcp47Code(StringField... fields) {
            return bcp47Code(new StringTestData(fields));
        }

        public static RightsContractAssetField bcp47Code(String val) {
            return bcp47Code(StringField.value(val));
        }

        public static RightsContractAssetField assetType(StringTestData val) {
            return new RightsContractAssetField("assetType", val);
        }

        public static RightsContractAssetField assetType(StringField... fields) {
            return assetType(new StringTestData(fields));
        }

        public static RightsContractAssetField assetType(String val) {
            return assetType(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RightsContractAsset", 2);

    static {
        SCHEMA.addField("bcp47Code", FieldType.REFERENCE, "String");
        SCHEMA.addField("assetType", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}