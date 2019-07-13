package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RightsWindowContractTestData extends HollowTestObjectRecord {

    RightsWindowContractTestData(RightsWindowContractField... fields){
        super(fields);
    }

    public static RightsWindowContractTestData RightsWindowContract(RightsWindowContractField... fields) {
        return new RightsWindowContractTestData(fields);
    }

    public RightsWindowContractTestData update(RightsWindowContractField... fields){
        super.addFields(fields);
        return this;
    }

    public ListOfRightsContractAssetTestData assets() {
        Field f = super.getField("assets");
        return f == null ? null : (ListOfRightsContractAssetTestData)f.value;
    }

    public long dealId() {
        Field f = super.getField("dealId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long packageId() {
        Field f = super.getField("packageId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public ListOfRightsContractPackageTestData packages() {
        Field f = super.getField("packages");
        return f == null ? null : (ListOfRightsContractPackageTestData)f.value;
    }

    public Boolean download() {
        Field f = super.getField("download");
        return f == null ? null : (Boolean)f.value;
    }

    public static class RightsWindowContractField extends Field {

        private RightsWindowContractField(String name, Object val) { super(name, val); }

        public static RightsWindowContractField assets(ListOfRightsContractAssetTestData val) {
            return new RightsWindowContractField("assets", val);
        }

        public static RightsWindowContractField assets(RightsContractAssetTestData... elements) {
            return assets(new ListOfRightsContractAssetTestData(elements));
        }

        public static RightsWindowContractField dealId(long val) {
            return new RightsWindowContractField("dealId", val);
        }

        public static RightsWindowContractField packageId(long val) {
            return new RightsWindowContractField("packageId", val);
        }

        public static RightsWindowContractField packages(ListOfRightsContractPackageTestData val) {
            return new RightsWindowContractField("packages", val);
        }

        public static RightsWindowContractField packages(RightsContractPackageTestData... elements) {
            return packages(new ListOfRightsContractPackageTestData(elements));
        }

        public static RightsWindowContractField download(boolean val) {
            return new RightsWindowContractField("download", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RightsWindowContract", 5);

    static {
        SCHEMA.addField("assets", FieldType.REFERENCE, "ListOfRightsContractAsset");
        SCHEMA.addField("dealId", FieldType.LONG);
        SCHEMA.addField("packageId", FieldType.LONG);
        SCHEMA.addField("packages", FieldType.REFERENCE, "ListOfRightsContractPackage");
        SCHEMA.addField("download", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}