package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class BooleanTestData extends HollowTestObjectRecord {

    BooleanTestData(BooleanField... fields){
        super(fields);
    }

    public static BooleanTestData Boolean(BooleanField... fields) {
        return new BooleanTestData(fields);
    }

    public static BooleanTestData Boolean(boolean val) {
        return Boolean(BooleanField.value(val));
    }

    public BooleanTestData update(BooleanField... fields){
        super.addFields(fields);
        return this;
    }

    public Boolean value() {
        Field f = super.getField("value");
        return f == null ? null : (Boolean)f.value;
    }

    public static class BooleanField extends Field {

        private BooleanField(String name, Object val) { super(name, val); }

        public static BooleanField value(boolean val) {
            return new BooleanField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Boolean", 1);

    static {
        SCHEMA.addField("value", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}