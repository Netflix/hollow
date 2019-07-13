package com.netflix.vms.transformer.data.gen.packageDealCountry;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.packageDealCountry.LongTestData.LongField;

public class DealCountryGroupTestData extends HollowTestObjectRecord {

    DealCountryGroupTestData(DealCountryGroupField... fields){
        super(fields);
    }

    public static DealCountryGroupTestData DealCountryGroup(DealCountryGroupField... fields) {
        return new DealCountryGroupTestData(fields);
    }

    public DealCountryGroupTestData update(DealCountryGroupField... fields){
        super.addFields(fields);
        return this;
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

    public MapOfStringToBooleanTestData countryWindow() {
        Field f = super.getField("countryWindow");
        return f == null ? null : (MapOfStringToBooleanTestData)f.value;
    }

    public static class DealCountryGroupField extends Field {

        private DealCountryGroupField(String name, Object val) { super(name, val); }

        public static DealCountryGroupField dealId(LongTestData val) {
            return new DealCountryGroupField("dealId", val);
        }

        public static DealCountryGroupField dealId(LongField... fields) {
            return dealId(new LongTestData(fields));
        }

        public static DealCountryGroupField dealId(long val) {
            return dealId(LongField.value(val));
        }

        public static DealCountryGroupField countryWindow(MapOfStringToBooleanTestData val) {
            return new DealCountryGroupField("countryWindow", val);
        }

        public static DealCountryGroupField countryWindow(
                StringTestData key, BooleanTestData value) {
            return countryWindow(MapOfStringToBooleanTestData.MapOfStringToBoolean(key, value));
        }

        public static DealCountryGroupField countryWindow(
                StringTestData key1, BooleanTestData value1,
                StringTestData key2, BooleanTestData value2) {
            return countryWindow(MapOfStringToBooleanTestData.MapOfStringToBoolean(key1, value1, key2, value2));
        }

        public static DealCountryGroupField countryWindow(
                StringTestData key1, BooleanTestData value1,
                StringTestData key2, BooleanTestData value2,
                StringTestData key3, BooleanTestData value3) {
            return countryWindow(MapOfStringToBooleanTestData.MapOfStringToBoolean(key1, value1, key2, value2, key3, value3));
        }

        public static DealCountryGroupField countryWindow(
                StringTestData key1, BooleanTestData value1,
                StringTestData key2, BooleanTestData value2,
                StringTestData key3, BooleanTestData value3,
                StringTestData key4, BooleanTestData value4) {
            return countryWindow(MapOfStringToBooleanTestData.MapOfStringToBoolean(key1, value1, key2, value2, key3, value3, key4, value4));
        }

        public static DealCountryGroupField countryWindow(
                StringTestData key1, BooleanTestData value1,
                StringTestData key2, BooleanTestData value2,
                StringTestData key3, BooleanTestData value3,
                StringTestData key4, BooleanTestData value4,
                StringTestData key5, BooleanTestData value5) {
            return countryWindow(MapOfStringToBooleanTestData.MapOfStringToBoolean(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("DealCountryGroup", 2, new PrimaryKey("DealCountryGroup", "dealId"));

    static {
        SCHEMA.addField("dealId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("countryWindow", FieldType.REFERENCE, "MapOfStringToBoolean");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}