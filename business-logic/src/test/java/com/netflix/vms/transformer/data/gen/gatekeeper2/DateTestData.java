package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class DateTestData extends HollowTestObjectRecord {

    DateTestData(DateField... fields){
        super(fields);
    }

    public static DateTestData Date(DateField... fields) {
        return new DateTestData(fields);
    }

    public static DateTestData Date(long val) {
        return Date(DateField.value(val));
    }

    public DateTestData update(DateField... fields){
        super.addFields(fields);
        return this;
    }

    public long value() {
        Field f = super.getField("value");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class DateField extends Field {

        private DateField(String name, Object val) { super(name, val); }

        public static DateField value(long val) {
            return new DateField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Date", 1);

    static {
        SCHEMA.addField("value", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}