package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.gatekeeper2.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.gatekeeper2.StringTestData.StringField;

public class FlagsTestData extends HollowTestObjectRecord {

    FlagsTestData(FlagsField... fields){
        super(fields);
    }

    public static FlagsTestData Flags(FlagsField... fields) {
        return new FlagsTestData(fields);
    }

    public FlagsTestData update(FlagsField... fields){
        super.addFields(fields);
        return this;
    }

    public Boolean searchOnly() {
        Field f = super.getField("searchOnly");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean localText() {
        Field f = super.getField("localText");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean languageOverride() {
        Field f = super.getField("languageOverride");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean localAudio() {
        Field f = super.getField("localAudio");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean goLive() {
        Field f = super.getField("goLive");
        return f == null ? null : (Boolean)f.value;
    }

    public DateTestData goLiveFlipDateRef() {
        Field f = super.getField("goLiveFlipDate");
        return f == null ? null : (DateTestData)f.value;
    }

    public long goLiveFlipDate() {
        Field f = super.getField("goLiveFlipDate");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public Boolean autoPlay() {
        Field f = super.getField("autoPlay");
        return f == null ? null : (Boolean)f.value;
    }

    public DateTestData firstDisplayDateRef() {
        Field f = super.getField("firstDisplayDate");
        return f == null ? null : (DateTestData)f.value;
    }

    public long firstDisplayDate() {
        Field f = super.getField("firstDisplayDate");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public MapOfFlagsFirstDisplayDatesTestData firstDisplayDates() {
        Field f = super.getField("firstDisplayDates");
        return f == null ? null : (MapOfFlagsFirstDisplayDatesTestData)f.value;
    }

    public SetOfStringTestData grandfatheredLanguages() {
        Field f = super.getField("grandfatheredLanguages");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public Boolean liveOnSite() {
        Field f = super.getField("liveOnSite");
        return f == null ? null : (Boolean)f.value;
    }

    public DateTestData liveOnSiteFlipDateRef() {
        Field f = super.getField("liveOnSiteFlipDate");
        return f == null ? null : (DateTestData)f.value;
    }

    public long liveOnSiteFlipDate() {
        Field f = super.getField("liveOnSiteFlipDate");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public ListOfStringTestData offsiteReasons() {
        Field f = super.getField("offsiteReasons");
        return f == null ? null : (ListOfStringTestData)f.value;
    }

    public Boolean contentApproved() {
        Field f = super.getField("contentApproved");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean allowIncomplete() {
        Field f = super.getField("allowIncomplete");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean goLivePartialSubDubIgnored() {
        Field f = super.getField("goLivePartialSubDubIgnored");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData alternateLanguageRef() {
        Field f = super.getField("alternateLanguage");
        return f == null ? null : (StringTestData)f.value;
    }

    public String alternateLanguage() {
        Field f = super.getField("alternateLanguage");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean hasRequiredLanguages() {
        Field f = super.getField("hasRequiredLanguages");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean hasRequiredStreams() {
        Field f = super.getField("hasRequiredStreams");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean releaseAsAvailable() {
        Field f = super.getField("releaseAsAvailable");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData removeAssetRef() {
        Field f = super.getField("removeAsset");
        return f == null ? null : (StringTestData)f.value;
    }

    public String removeAsset() {
        Field f = super.getField("removeAsset");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean removeFromWebsiteOverride() {
        Field f = super.getField("removeFromWebsiteOverride");
        return f == null ? null : (Boolean)f.value;
    }

    public SetOfStringTestData requiredLangs() {
        Field f = super.getField("requiredLangs");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public Boolean searchOnlyOverride() {
        Field f = super.getField("searchOnlyOverride");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean allowPartialSubsDubsOverride() {
        Field f = super.getField("allowPartialSubsDubsOverride");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean ignoreLanguageRequirementOverride() {
        Field f = super.getField("ignoreLanguageRequirementOverride");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean subsRequired() {
        Field f = super.getField("subsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean dubsRequired() {
        Field f = super.getField("dubsRequired");
        return f == null ? null : (Boolean)f.value;
    }

    public static class FlagsField extends Field {

        private FlagsField(String name, Object val) { super(name, val); }

        public static FlagsField searchOnly(boolean val) {
            return new FlagsField("searchOnly", val);
        }

        public static FlagsField localText(boolean val) {
            return new FlagsField("localText", val);
        }

        public static FlagsField languageOverride(boolean val) {
            return new FlagsField("languageOverride", val);
        }

        public static FlagsField localAudio(boolean val) {
            return new FlagsField("localAudio", val);
        }

        public static FlagsField goLive(boolean val) {
            return new FlagsField("goLive", val);
        }

        public static FlagsField goLiveFlipDate(DateTestData val) {
            return new FlagsField("goLiveFlipDate", val);
        }

        public static FlagsField goLiveFlipDate(DateField... fields) {
            return goLiveFlipDate(new DateTestData(fields));
        }

        public static FlagsField goLiveFlipDate(long val) {
            return goLiveFlipDate(DateField.value(val));
        }

        public static FlagsField autoPlay(boolean val) {
            return new FlagsField("autoPlay", val);
        }

        public static FlagsField firstDisplayDate(DateTestData val) {
            return new FlagsField("firstDisplayDate", val);
        }

        public static FlagsField firstDisplayDate(DateField... fields) {
            return firstDisplayDate(new DateTestData(fields));
        }

        public static FlagsField firstDisplayDate(long val) {
            return firstDisplayDate(DateField.value(val));
        }

        public static FlagsField firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData val) {
            return new FlagsField("firstDisplayDates", val);
        }

        public static FlagsField firstDisplayDates(
                MapKeyTestData key, DateTestData value) {
            return firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(key, value));
        }

        public static FlagsField firstDisplayDates(
                MapKeyTestData key1, DateTestData value1,
                MapKeyTestData key2, DateTestData value2) {
            return firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(key1, value1, key2, value2));
        }

        public static FlagsField firstDisplayDates(
                MapKeyTestData key1, DateTestData value1,
                MapKeyTestData key2, DateTestData value2,
                MapKeyTestData key3, DateTestData value3) {
            return firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(key1, value1, key2, value2, key3, value3));
        }

        public static FlagsField firstDisplayDates(
                MapKeyTestData key1, DateTestData value1,
                MapKeyTestData key2, DateTestData value2,
                MapKeyTestData key3, DateTestData value3,
                MapKeyTestData key4, DateTestData value4) {
            return firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(key1, value1, key2, value2, key3, value3, key4, value4));
        }

        public static FlagsField firstDisplayDates(
                MapKeyTestData key1, DateTestData value1,
                MapKeyTestData key2, DateTestData value2,
                MapKeyTestData key3, DateTestData value3,
                MapKeyTestData key4, DateTestData value4,
                MapKeyTestData key5, DateTestData value5) {
            return firstDisplayDates(MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
        }

        public static FlagsField grandfatheredLanguages(SetOfStringTestData val) {
            return new FlagsField("grandfatheredLanguages", val);
        }

        public static FlagsField grandfatheredLanguages(StringTestData... elements) {
            return grandfatheredLanguages(new SetOfStringTestData(elements));
        }

        public static FlagsField liveOnSite(boolean val) {
            return new FlagsField("liveOnSite", val);
        }

        public static FlagsField liveOnSiteFlipDate(DateTestData val) {
            return new FlagsField("liveOnSiteFlipDate", val);
        }

        public static FlagsField liveOnSiteFlipDate(DateField... fields) {
            return liveOnSiteFlipDate(new DateTestData(fields));
        }

        public static FlagsField liveOnSiteFlipDate(long val) {
            return liveOnSiteFlipDate(DateField.value(val));
        }

        public static FlagsField offsiteReasons(ListOfStringTestData val) {
            return new FlagsField("offsiteReasons", val);
        }

        public static FlagsField offsiteReasons(StringTestData... elements) {
            return offsiteReasons(new ListOfStringTestData(elements));
        }

        public static FlagsField contentApproved(boolean val) {
            return new FlagsField("contentApproved", val);
        }

        public static FlagsField allowIncomplete(boolean val) {
            return new FlagsField("allowIncomplete", val);
        }

        public static FlagsField goLivePartialSubDubIgnored(boolean val) {
            return new FlagsField("goLivePartialSubDubIgnored", val);
        }

        public static FlagsField alternateLanguage(StringTestData val) {
            return new FlagsField("alternateLanguage", val);
        }

        public static FlagsField alternateLanguage(StringField... fields) {
            return alternateLanguage(new StringTestData(fields));
        }

        public static FlagsField alternateLanguage(String val) {
            return alternateLanguage(StringField.value(val));
        }

        public static FlagsField hasRequiredLanguages(boolean val) {
            return new FlagsField("hasRequiredLanguages", val);
        }

        public static FlagsField hasRequiredStreams(boolean val) {
            return new FlagsField("hasRequiredStreams", val);
        }

        public static FlagsField releaseAsAvailable(boolean val) {
            return new FlagsField("releaseAsAvailable", val);
        }

        public static FlagsField removeAsset(StringTestData val) {
            return new FlagsField("removeAsset", val);
        }

        public static FlagsField removeAsset(StringField... fields) {
            return removeAsset(new StringTestData(fields));
        }

        public static FlagsField removeAsset(String val) {
            return removeAsset(StringField.value(val));
        }

        public static FlagsField removeFromWebsiteOverride(boolean val) {
            return new FlagsField("removeFromWebsiteOverride", val);
        }

        public static FlagsField requiredLangs(SetOfStringTestData val) {
            return new FlagsField("requiredLangs", val);
        }

        public static FlagsField requiredLangs(StringTestData... elements) {
            return requiredLangs(new SetOfStringTestData(elements));
        }

        public static FlagsField searchOnlyOverride(boolean val) {
            return new FlagsField("searchOnlyOverride", val);
        }

        public static FlagsField allowPartialSubsDubsOverride(boolean val) {
            return new FlagsField("allowPartialSubsDubsOverride", val);
        }

        public static FlagsField ignoreLanguageRequirementOverride(boolean val) {
            return new FlagsField("ignoreLanguageRequirementOverride", val);
        }

        public static FlagsField subsRequired(boolean val) {
            return new FlagsField("subsRequired", val);
        }

        public static FlagsField dubsRequired(boolean val) {
            return new FlagsField("dubsRequired", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Flags", 28);

    static {
        SCHEMA.addField("searchOnly", FieldType.BOOLEAN);
        SCHEMA.addField("localText", FieldType.BOOLEAN);
        SCHEMA.addField("languageOverride", FieldType.BOOLEAN);
        SCHEMA.addField("localAudio", FieldType.BOOLEAN);
        SCHEMA.addField("goLive", FieldType.BOOLEAN);
        SCHEMA.addField("goLiveFlipDate", FieldType.REFERENCE, "Date");
        SCHEMA.addField("autoPlay", FieldType.BOOLEAN);
        SCHEMA.addField("firstDisplayDate", FieldType.REFERENCE, "Date");
        SCHEMA.addField("firstDisplayDates", FieldType.REFERENCE, "MapOfFlagsFirstDisplayDates");
        SCHEMA.addField("grandfatheredLanguages", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("liveOnSite", FieldType.BOOLEAN);
        SCHEMA.addField("liveOnSiteFlipDate", FieldType.REFERENCE, "Date");
        SCHEMA.addField("offsiteReasons", FieldType.REFERENCE, "ListOfString");
        SCHEMA.addField("contentApproved", FieldType.BOOLEAN);
        SCHEMA.addField("allowIncomplete", FieldType.BOOLEAN);
        SCHEMA.addField("goLivePartialSubDubIgnored", FieldType.BOOLEAN);
        SCHEMA.addField("alternateLanguage", FieldType.REFERENCE, "String");
        SCHEMA.addField("hasRequiredLanguages", FieldType.BOOLEAN);
        SCHEMA.addField("hasRequiredStreams", FieldType.BOOLEAN);
        SCHEMA.addField("releaseAsAvailable", FieldType.BOOLEAN);
        SCHEMA.addField("removeAsset", FieldType.REFERENCE, "String");
        SCHEMA.addField("removeFromWebsiteOverride", FieldType.BOOLEAN);
        SCHEMA.addField("requiredLangs", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("searchOnlyOverride", FieldType.BOOLEAN);
        SCHEMA.addField("allowPartialSubsDubsOverride", FieldType.BOOLEAN);
        SCHEMA.addField("ignoreLanguageRequirementOverride", FieldType.BOOLEAN);
        SCHEMA.addField("subsRequired", FieldType.BOOLEAN);
        SCHEMA.addField("dubsRequired", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}