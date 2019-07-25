package com.netflix.vms.transformer.data.gen.topn;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.topn.StringTestData.StringField;

public class TopNAttributeTestData extends HollowTestObjectRecord {

    TopNAttributeTestData(TopNAttributeField... fields){
        super(fields);
    }

    public static TopNAttributeTestData TopNAttribute(TopNAttributeField... fields) {
        return new TopNAttributeTestData(fields);
    }

    public TopNAttributeTestData update(TopNAttributeField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData countryRef() {
        Field f = super.getField("country");
        return f == null ? null : (StringTestData)f.value;
    }

    public String country() {
        Field f = super.getField("country");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public long countryViewHoursDaily() {
        Field f = super.getField("countryViewHoursDaily");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long videoViewHoursDaily() {
        Field f = super.getField("videoViewHoursDaily");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class TopNAttributeField extends HollowTestObjectRecord.Field {

        private TopNAttributeField(String name, Object val) { super(name, val); }

        public static TopNAttributeField country(StringTestData val) {
            return new TopNAttributeField("country", val);
        }

        public static TopNAttributeField country(StringField... fields) {
            return country(new StringTestData(fields));
        }

        public static TopNAttributeField country(String val) {
            return country(StringField.value(val));
        }

        public static TopNAttributeField countryViewHoursDaily(long val) {
            return new TopNAttributeField("countryViewHoursDaily", val);
        }

        public static TopNAttributeField videoViewHoursDaily(long val) {
            return new TopNAttributeField("videoViewHoursDaily", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TopNAttribute", 3, new PrimaryKey("TopNAttribute", "country"));

    static {
        SCHEMA.addField("country", FieldType.REFERENCE, "String");
        SCHEMA.addField("countryViewHoursDaily", FieldType.LONG);
        SCHEMA.addField("videoViewHoursDaily", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}