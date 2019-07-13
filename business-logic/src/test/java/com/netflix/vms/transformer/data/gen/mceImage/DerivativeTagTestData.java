package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class DerivativeTagTestData extends HollowTestObjectRecord {

    DerivativeTagTestData(DerivativeTagField... fields){
        super(fields);
    }

    public static DerivativeTagTestData DerivativeTag(DerivativeTagField... fields) {
        return new DerivativeTagTestData(fields);
    }

    public static DerivativeTagTestData DerivativeTag(String val) {
        return DerivativeTag(DerivativeTagField.value(val));
    }

    public DerivativeTagTestData update(DerivativeTagField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class DerivativeTagField extends Field {

        private DerivativeTagField(String name, Object val) { super(name, val); }

        public static DerivativeTagField value(String val) {
            return new DerivativeTagField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("DerivativeTag", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}