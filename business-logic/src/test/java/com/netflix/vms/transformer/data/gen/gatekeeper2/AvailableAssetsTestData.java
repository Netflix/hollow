package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class AvailableAssetsTestData extends HollowTestObjectRecord {

    AvailableAssetsTestData(AvailableAssetsField... fields){
        super(fields);
    }

    public static AvailableAssetsTestData AvailableAssets(AvailableAssetsField... fields) {
        return new AvailableAssetsTestData(fields);
    }

    public AvailableAssetsTestData update(AvailableAssetsField... fields){
        super.addFields(fields);
        return this;
    }

    public SetOfStringTestData availableSubs() {
        Field f = super.getField("availableSubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData availableDubs() {
        Field f = super.getField("availableDubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData blockedSubs() {
        Field f = super.getField("blockedSubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData blockedDubs() {
        Field f = super.getField("blockedDubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData missingSubs() {
        Field f = super.getField("missingSubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfStringTestData missingDubs() {
        Field f = super.getField("missingDubs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public static class AvailableAssetsField extends Field {

        private AvailableAssetsField(String name, Object val) { super(name, val); }

        public static AvailableAssetsField availableSubs(SetOfStringTestData val) {
            return new AvailableAssetsField("availableSubs", val);
        }

        public static AvailableAssetsField availableSubs(StringTestData... elements) {
            return availableSubs(new SetOfStringTestData(elements));
        }

        public static AvailableAssetsField availableDubs(SetOfStringTestData val) {
            return new AvailableAssetsField("availableDubs", val);
        }

        public static AvailableAssetsField availableDubs(StringTestData... elements) {
            return availableDubs(new SetOfStringTestData(elements));
        }

        public static AvailableAssetsField blockedSubs(SetOfStringTestData val) {
            return new AvailableAssetsField("blockedSubs", val);
        }

        public static AvailableAssetsField blockedSubs(StringTestData... elements) {
            return blockedSubs(new SetOfStringTestData(elements));
        }

        public static AvailableAssetsField blockedDubs(SetOfStringTestData val) {
            return new AvailableAssetsField("blockedDubs", val);
        }

        public static AvailableAssetsField blockedDubs(StringTestData... elements) {
            return blockedDubs(new SetOfStringTestData(elements));
        }

        public static AvailableAssetsField missingSubs(SetOfStringTestData val) {
            return new AvailableAssetsField("missingSubs", val);
        }

        public static AvailableAssetsField missingSubs(StringTestData... elements) {
            return missingSubs(new SetOfStringTestData(elements));
        }

        public static AvailableAssetsField missingDubs(SetOfStringTestData val) {
            return new AvailableAssetsField("missingDubs", val);
        }

        public static AvailableAssetsField missingDubs(StringTestData... elements) {
            return missingDubs(new SetOfStringTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("AvailableAssets", 6);

    static {
        SCHEMA.addField("availableSubs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("availableDubs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("blockedSubs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("blockedDubs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("missingSubs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("missingDubs", FieldType.REFERENCE, "SetOfString");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}