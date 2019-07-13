package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.exhibitDealAttribute.BooleanTestData.BooleanField;
import com.netflix.vms.transformer.data.gen.exhibitDealAttribute.LongTestData.LongField;
import com.netflix.vms.transformer.data.gen.exhibitDealAttribute.StringTestData.StringField;

public class VmsAttributeFeedEntryTestData extends HollowTestObjectRecord {

    VmsAttributeFeedEntryTestData(VmsAttributeFeedEntryField... fields){
        super(fields);
    }

    public static VmsAttributeFeedEntryTestData VmsAttributeFeedEntry(VmsAttributeFeedEntryField... fields) {
        return new VmsAttributeFeedEntryTestData(fields);
    }

    public VmsAttributeFeedEntryTestData update(VmsAttributeFeedEntryField... fields){
        super.addFields(fields);
        return this;
    }

    public LongTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public LongTestData dealIdRef() {
        Field f = super.getField("dealId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long dealId() {
        Field f = super.getField("dealId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
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

    public BooleanTestData dayAfterBroadcastRef() {
        Field f = super.getField("dayAfterBroadcast");
        return f == null ? null : (BooleanTestData)f.value;
    }

    public Boolean dayAfterBroadcast() {
        Field f = super.getField("dayAfterBroadcast");
        if(f == null) return null;
        BooleanTestData ref = (BooleanTestData)f.value;
        return ref.value();
    }

    public BooleanTestData dayOfBroadcastRef() {
        Field f = super.getField("dayOfBroadcast");
        return f == null ? null : (BooleanTestData)f.value;
    }

    public Boolean dayOfBroadcast() {
        Field f = super.getField("dayOfBroadcast");
        if(f == null) return null;
        BooleanTestData ref = (BooleanTestData)f.value;
        return ref.value();
    }

    public LongTestData prePromotionDaysRef() {
        Field f = super.getField("prePromotionDays");
        return f == null ? null : (LongTestData)f.value;
    }

    public long prePromotionDays() {
        Field f = super.getField("prePromotionDays");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public SetOfDisallowedAssetBundleEntryTestData disallowedAssetBundles() {
        Field f = super.getField("disallowedAssetBundles");
        return f == null ? null : (SetOfDisallowedAssetBundleEntryTestData)f.value;
    }

    public static class VmsAttributeFeedEntryField extends Field {

        private VmsAttributeFeedEntryField(String name, Object val) { super(name, val); }

        public static VmsAttributeFeedEntryField movieId(LongTestData val) {
            return new VmsAttributeFeedEntryField("movieId", val);
        }

        public static VmsAttributeFeedEntryField movieId(LongField... fields) {
            return movieId(new LongTestData(fields));
        }

        public static VmsAttributeFeedEntryField movieId(long val) {
            return movieId(LongField.value(val));
        }

        public static VmsAttributeFeedEntryField dealId(LongTestData val) {
            return new VmsAttributeFeedEntryField("dealId", val);
        }

        public static VmsAttributeFeedEntryField dealId(LongField... fields) {
            return dealId(new LongTestData(fields));
        }

        public static VmsAttributeFeedEntryField dealId(long val) {
            return dealId(LongField.value(val));
        }

        public static VmsAttributeFeedEntryField countryCode(StringTestData val) {
            return new VmsAttributeFeedEntryField("countryCode", val);
        }

        public static VmsAttributeFeedEntryField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static VmsAttributeFeedEntryField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static VmsAttributeFeedEntryField dayAfterBroadcast(BooleanTestData val) {
            return new VmsAttributeFeedEntryField("dayAfterBroadcast", val);
        }

        public static VmsAttributeFeedEntryField dayAfterBroadcast(BooleanField... fields) {
            return dayAfterBroadcast(new BooleanTestData(fields));
        }

        public static VmsAttributeFeedEntryField dayAfterBroadcast(boolean val) {
            return dayAfterBroadcast(BooleanField.value(val));
        }

        public static VmsAttributeFeedEntryField dayOfBroadcast(BooleanTestData val) {
            return new VmsAttributeFeedEntryField("dayOfBroadcast", val);
        }

        public static VmsAttributeFeedEntryField dayOfBroadcast(BooleanField... fields) {
            return dayOfBroadcast(new BooleanTestData(fields));
        }

        public static VmsAttributeFeedEntryField dayOfBroadcast(boolean val) {
            return dayOfBroadcast(BooleanField.value(val));
        }

        public static VmsAttributeFeedEntryField prePromotionDays(LongTestData val) {
            return new VmsAttributeFeedEntryField("prePromotionDays", val);
        }

        public static VmsAttributeFeedEntryField prePromotionDays(LongField... fields) {
            return prePromotionDays(new LongTestData(fields));
        }

        public static VmsAttributeFeedEntryField prePromotionDays(long val) {
            return prePromotionDays(LongField.value(val));
        }

        public static VmsAttributeFeedEntryField disallowedAssetBundles(SetOfDisallowedAssetBundleEntryTestData val) {
            return new VmsAttributeFeedEntryField("disallowedAssetBundles", val);
        }

        public static VmsAttributeFeedEntryField disallowedAssetBundles(DisallowedAssetBundleEntryTestData... elements) {
            return disallowedAssetBundles(new SetOfDisallowedAssetBundleEntryTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VmsAttributeFeedEntry", 7, new PrimaryKey("VmsAttributeFeedEntry", "movieId", "dealId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("dealId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("dayAfterBroadcast", FieldType.REFERENCE, "Boolean");
        SCHEMA.addField("dayOfBroadcast", FieldType.REFERENCE, "Boolean");
        SCHEMA.addField("prePromotionDays", FieldType.REFERENCE, "Long");
        SCHEMA.addField("disallowedAssetBundles", FieldType.REFERENCE, "SetOfDisallowedAssetBundleEntry");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}