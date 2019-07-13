package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.exhibitDealAttribute.BooleanTestData.BooleanField;
import com.netflix.vms.transformer.data.gen.exhibitDealAttribute.StringTestData.StringField;

public class DisallowedAssetBundleEntryTestData extends HollowTestObjectRecord {

    DisallowedAssetBundleEntryTestData(DisallowedAssetBundleEntryField... fields){
        super(fields);
    }

    public static DisallowedAssetBundleEntryTestData DisallowedAssetBundleEntry(DisallowedAssetBundleEntryField... fields) {
        return new DisallowedAssetBundleEntryTestData(fields);
    }

    public DisallowedAssetBundleEntryTestData update(DisallowedAssetBundleEntryField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData audioLanguageCodeRef() {
        Field f = super.getField("audioLanguageCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String audioLanguageCode() {
        Field f = super.getField("audioLanguageCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public BooleanTestData forceSubtitleRef() {
        Field f = super.getField("forceSubtitle");
        return f == null ? null : (BooleanTestData)f.value;
    }

    public Boolean forceSubtitle() {
        Field f = super.getField("forceSubtitle");
        if(f == null) return null;
        BooleanTestData ref = (BooleanTestData)f.value;
        return ref.value();
    }

    public SetOfStringTestData disallowedSubtitleLangCodes() {
        Field f = super.getField("disallowedSubtitleLangCodes");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public static class DisallowedAssetBundleEntryField extends Field {

        private DisallowedAssetBundleEntryField(String name, Object val) { super(name, val); }

        public static DisallowedAssetBundleEntryField audioLanguageCode(StringTestData val) {
            return new DisallowedAssetBundleEntryField("audioLanguageCode", val);
        }

        public static DisallowedAssetBundleEntryField audioLanguageCode(StringField... fields) {
            return audioLanguageCode(new StringTestData(fields));
        }

        public static DisallowedAssetBundleEntryField audioLanguageCode(String val) {
            return audioLanguageCode(StringField.value(val));
        }

        public static DisallowedAssetBundleEntryField forceSubtitle(BooleanTestData val) {
            return new DisallowedAssetBundleEntryField("forceSubtitle", val);
        }

        public static DisallowedAssetBundleEntryField forceSubtitle(BooleanField... fields) {
            return forceSubtitle(new BooleanTestData(fields));
        }

        public static DisallowedAssetBundleEntryField forceSubtitle(boolean val) {
            return forceSubtitle(BooleanField.value(val));
        }

        public static DisallowedAssetBundleEntryField disallowedSubtitleLangCodes(SetOfStringTestData val) {
            return new DisallowedAssetBundleEntryField("disallowedSubtitleLangCodes", val);
        }

        public static DisallowedAssetBundleEntryField disallowedSubtitleLangCodes(StringTestData... elements) {
            return disallowedSubtitleLangCodes(new SetOfStringTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("DisallowedAssetBundleEntry", 3);

    static {
        SCHEMA.addField("audioLanguageCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("forceSubtitle", FieldType.REFERENCE, "Boolean");
        SCHEMA.addField("disallowedSubtitleLangCodes", FieldType.REFERENCE, "SetOfString");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}