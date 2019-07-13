package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class LongTestData extends HollowTestObjectRecord {

    LongTestData(LongField... fields){
        super(fields);
    }

    public static LongTestData Long(LongField... fields) {
        return new LongTestData(fields);
    }

    public static LongTestData Long(long val) {
        return Long(LongField.value(val));
    }

    public LongTestData update(LongField... fields){
        super.addFields(fields);
        return this;
    }

    public long value() {
        Field f = super.getField("value");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class LongField extends Field {

        private LongField(String name, Object val) { super(name, val); }

        public static LongField value(long val) {
            return new LongField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Long", 1);

    static {
        SCHEMA.addField("value", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}