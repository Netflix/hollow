package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RightsContractPackageTestData extends HollowTestObjectRecord {

    RightsContractPackageTestData(RightsContractPackageField... fields){
        super(fields);
    }

    public static RightsContractPackageTestData RightsContractPackage(RightsContractPackageField... fields) {
        return new RightsContractPackageTestData(fields);
    }

    public RightsContractPackageTestData update(RightsContractPackageField... fields){
        super.addFields(fields);
        return this;
    }

    public long packageId() {
        Field f = super.getField("packageId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public Boolean primary() {
        Field f = super.getField("primary");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean hasRequiredStreams() {
        Field f = super.getField("hasRequiredStreams");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean hasRequiredLanguage() {
        Field f = super.getField("hasRequiredLanguage");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean hasLocalText() {
        Field f = super.getField("hasLocalText");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean hasLocalAudio() {
        Field f = super.getField("hasLocalAudio");
        return f == null ? null : (Boolean)f.value;
    }

    public static class RightsContractPackageField extends HollowTestObjectRecord.Field {

        private RightsContractPackageField(String name, Object val) { super(name, val); }

        public static RightsContractPackageField packageId(long val) {
            return new RightsContractPackageField("packageId", val);
        }

        public static RightsContractPackageField primary(boolean val) {
            return new RightsContractPackageField("primary", val);
        }

        public static RightsContractPackageField hasRequiredStreams(boolean val) {
            return new RightsContractPackageField("hasRequiredStreams", val);
        }

        public static RightsContractPackageField hasRequiredLanguage(boolean val) {
            return new RightsContractPackageField("hasRequiredLanguage", val);
        }

        public static RightsContractPackageField hasLocalText(boolean val) {
            return new RightsContractPackageField("hasLocalText", val);
        }

        public static RightsContractPackageField hasLocalAudio(boolean val) {
            return new RightsContractPackageField("hasLocalAudio", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RightsContractPackage", 6);

    static {
        SCHEMA.addField("packageId", FieldType.LONG);
        SCHEMA.addField("primary", FieldType.BOOLEAN);
        SCHEMA.addField("hasRequiredStreams", FieldType.BOOLEAN);
        SCHEMA.addField("hasRequiredLanguage", FieldType.BOOLEAN);
        SCHEMA.addField("hasLocalText", FieldType.BOOLEAN);
        SCHEMA.addField("hasLocalAudio", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}