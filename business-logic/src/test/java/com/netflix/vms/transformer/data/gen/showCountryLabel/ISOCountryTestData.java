package com.netflix.vms.transformer.data.gen.showCountryLabel;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ISOCountryTestData extends HollowTestObjectRecord {

    ISOCountryTestData(ISOCountryField... fields){
        super(fields);
    }

    public static ISOCountryTestData ISOCountry(ISOCountryField... fields) {
        return new ISOCountryTestData(fields);
    }

    public static ISOCountryTestData ISOCountry(String val) {
        return ISOCountry(ISOCountryField.value(val));
    }

    public ISOCountryTestData update(ISOCountryField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class ISOCountryField extends Field {

        private ISOCountryField(String name, Object val) { super(name, val); }

        public static ISOCountryField value(String val) {
            return new ISOCountryField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ISOCountry", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}