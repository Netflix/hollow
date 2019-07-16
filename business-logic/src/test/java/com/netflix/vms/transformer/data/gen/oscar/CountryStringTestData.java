package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class CountryStringTestData extends HollowTestObjectRecord {

    CountryStringTestData(CountryStringField... fields){
        super(fields);
    }

    public static CountryStringTestData CountryString(CountryStringField... fields) {
        return new CountryStringTestData(fields);
    }

    public static CountryStringTestData CountryString(String val) {
        return CountryString(CountryStringField.value(val));
    }

    public CountryStringTestData update(CountryStringField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class CountryStringField extends HollowTestObjectRecord.Field {

        private CountryStringField(String name, Object val) { super(name, val); }

        public static CountryStringField value(String val) {
            return new CountryStringField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("CountryString", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}